package com.gig.collide.order.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.order.domain.constant.OrderEvent;
import com.gig.collide.order.domain.constant.OrderStatus;
import com.gig.collide.order.domain.entity.OrderContentAssociation;
import com.gig.collide.order.domain.entity.OrderInfo;
import com.gig.collide.order.domain.exception.OrderBusinessException;
import com.gig.collide.order.domain.statemachine.OrderStateMachine;
import com.gig.collide.order.infrastructure.mapper.OrderContentAssociationMapper;
import com.gig.collide.order.infrastructure.mapper.OrderInfoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 订单领域服务
 * 
 * 包含核心业务逻辑和幂等性控制
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderDomainService {

    private final OrderInfoMapper orderInfoMapper;
    private final OrderContentAssociationMapper orderContentAssociationMapper;
    private final IdempotencyService idempotencyService;

    /**
     * 分页查询订单
     * 
     * @param queryParams 查询参数
     * @return 订单分页信息
     */
    public IPage<OrderInfo> pageQueryOrders(Map<String, Object> queryParams) {
        log.info("分页查询订单，查询参数：{}", queryParams);
        
        try {
            // 解析分页参数
            Integer pageNo = (Integer) queryParams.getOrDefault("pageNo", 1);
            Integer pageSize = (Integer) queryParams.getOrDefault("pageSize", 20);
            
            // 参数校验
            if (pageNo <= 0) pageNo = 1;
            if (pageSize <= 0 || pageSize > 100) pageSize = 20;
            
            Page<OrderInfo> page = new Page<>(pageNo, pageSize);
            IPage<OrderInfo> result = orderInfoMapper.pageQuery(page, queryParams);
            
            log.info("分页查询订单成功，总数：{}", result.getTotal());
            return result;
            
        } catch (Exception e) {
            log.error("分页查询订单失败，查询参数：{}", queryParams, e);
            throw OrderBusinessException.systemError("查询订单失败", e);
        }
    }

    /**
     * 根据订单号获取订单详情
     * 
     * @param orderNo 订单号
     * @return 订单信息
     */
    public OrderInfo getOrderByNo(String orderNo) {
        log.info("获取订单详情，订单号：{}", orderNo);
        
        if (!StringUtils.hasText(orderNo)) {
            throw OrderBusinessException.parameterInvalid("orderNo", "订单号不能为空");
        }
        
        try {
            OrderInfo orderInfo = orderInfoMapper.selectByOrderNo(orderNo);
            if (orderInfo == null) {
                throw OrderBusinessException.orderNotFound(orderNo);
            }
            
            log.info("获取订单详情成功，订单号：{}", orderNo);
            return orderInfo;
            
        } catch (OrderBusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取订单详情失败，订单号：{}", orderNo, e);
            throw OrderBusinessException.systemError("获取订单详情失败", e);
        }
    }

    /**
     * 幂等性取消订单
     * 
     * @param orderNo 订单号
     * @param reason 取消原因
     * @param requestId 请求ID（用于幂等性控制）
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrderIdempotent(String orderNo, String reason, String requestId) {
        log.info("幂等性取消订单，订单号：{}，原因：{}，请求ID：{}", orderNo, reason, requestId);
        
        if (!StringUtils.hasText(orderNo)) {
            throw OrderBusinessException.parameterInvalid("orderNo", "订单号不能为空");
        }
        
        if (!StringUtils.hasText(requestId)) {
            requestId = "cancel_" + orderNo + "_" + System.currentTimeMillis();
        }
        
        String lockKey = "cancel_order_" + orderNo;
        
        try {
            idempotencyService.executeWithLock(requestId, lockKey, () -> {
                // 1. 获取订单并加锁
                OrderInfo order = orderInfoMapper.selectByOrderNoForUpdate(orderNo);
                if (order == null) {
                    throw OrderBusinessException.orderNotFound(orderNo);
                }
                
                // 2. 检查订单状态
                OrderStatus currentStatus = OrderStatus.fromCode(order.getStatus());
                if (!OrderStateMachine.canTransition(currentStatus, OrderEvent.CANCEL)) {
                    throw OrderBusinessException.orderCannotCancel(orderNo, 
                        "当前状态不允许取消：" + currentStatus.getDescription());
                }
                
                // 3. 更新订单状态
                OrderStatus newStatus = OrderStateMachine.transition(currentStatus, OrderEvent.CANCEL);
                int updated = orderInfoMapper.updateStatusIdempotent(
                    orderNo, order.getStatus(), newStatus.getCode(), order.getVersion());
                
                if (updated == 0) {
                    throw OrderBusinessException.concurrentOperation(orderNo);
                }
                
                // 4. 处理订单内容关联（如果有）
                List<OrderContentAssociation> associations = orderContentAssociationMapper.selectByOrderNo(orderNo);
                if (!CollectionUtils.isEmpty(associations)) {
                    orderContentAssociationMapper.batchUpdateStatusByOrderNo(
                        orderNo, "REVOKED", "订单取消：" + reason);
                }
                
                log.info("订单取消成功，订单号：{}，状态：{} -> {}", orderNo, 
                    currentStatus.getDescription(), newStatus.getDescription());
                
                return null;
            });
            
        } catch (OrderBusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("取消订单失败，订单号：{}", orderNo, e);
            throw OrderBusinessException.systemError("取消订单失败", e);
        }
    }

    /**
     * 幂等性退款订单
     * 
     * @param orderNo 订单号
     * @param refundAmount 退款金额
     * @param reason 退款原因
     * @param requestId 请求ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void refundOrderIdempotent(String orderNo, String refundAmount, String reason, String requestId) {
        log.info("幂等性退款订单，订单号：{}，退款金额：{}，原因：{}，请求ID：{}", 
            orderNo, refundAmount, reason, requestId);
        
        if (!StringUtils.hasText(orderNo)) {
            throw OrderBusinessException.parameterInvalid("orderNo", "订单号不能为空");
        }
        
        if (!StringUtils.hasText(requestId)) {
            requestId = "refund_" + orderNo + "_" + System.currentTimeMillis();
        }
        
        String lockKey = "refund_order_" + orderNo;
        
        try {
            idempotencyService.executeWithLock(requestId, lockKey, () -> {
                // 1. 获取订单并加锁
                OrderInfo order = orderInfoMapper.selectByOrderNoForUpdate(orderNo);
                if (order == null) {
                    throw OrderBusinessException.orderNotFound(orderNo);
                }
                
                // 2. 检查订单状态
                if (!"PAID".equals(order.getStatus())) {
                    throw OrderBusinessException.orderCannotRefund(orderNo, 
                        "订单状态不是已支付，无法退款");
                }
                
                // 3. 这里应该调用支付服务进行退款处理
                // TODO: 集成支付服务退款接口
                
                // 4. 更新订单状态为已退款（这里简化处理）
                int updated = orderInfoMapper.updateStatusIdempotent(
                    orderNo, order.getStatus(), "REFUNDED", order.getVersion());
                
                if (updated == 0) {
                    throw OrderBusinessException.concurrentOperation(orderNo);
                }
                
                // 5. 撤销内容访问权限
                List<OrderContentAssociation> associations = orderContentAssociationMapper.selectByOrderNo(orderNo);
                if (!CollectionUtils.isEmpty(associations)) {
                    orderContentAssociationMapper.batchUpdateStatusByOrderNo(
                        orderNo, "REVOKED", "订单退款：" + reason);
                }
                
                log.info("订单退款成功，订单号：{}", orderNo);
                return null;
            });
            
        } catch (OrderBusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("退款订单失败，订单号：{}", orderNo, e);
            throw OrderBusinessException.systemError("退款订单失败", e);
        }
    }

    /**
     * 批量处理订单
     * 
     * @param action 操作类型（cancel, refund等）
     * @param orderNos 订单号列表
     * @param reason 处理原因
     * @param requestId 请求ID
     * @return 处理结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> batchProcessOrders(String action, List<String> orderNos, 
                                                  String reason, String requestId) throws Exception {
        log.info("批量处理订单，操作：{}，订单数量：{}，原因：{}，请求ID：{}", 
            action, orderNos.size(), reason, requestId);
        
        if (CollectionUtils.isEmpty(orderNos)) {
            throw OrderBusinessException.parameterInvalid("orderNos", "订单号列表不能为空");
        }
        
        if (orderNos.size() > 100) {
            throw OrderBusinessException.parameterInvalid("orderNos", "批量处理订单数量不能超过100个");
        }
        
        if (!StringUtils.hasText(requestId)) {
            requestId = "batch_" + action + "_" + System.currentTimeMillis();
        }
        
        try {
            return idempotencyService.executeIdempotent(requestId, () -> {
                Map<String, Object> result = new HashMap<>();
                int successCount = 0;
                int failCount = 0;
                StringBuilder errors = new StringBuilder();
                
                for (String orderNo : orderNos) {
                    try {
                        switch (action.toLowerCase()) {
                            case "cancel":
                                cancelOrderIdempotent(orderNo, reason, UUID.randomUUID().toString());
                                successCount++;
                                break;
                            case "refund":
                                refundOrderIdempotent(orderNo, null, reason, UUID.randomUUID().toString());
                                successCount++;
                                break;
                            default:
                                throw OrderBusinessException.parameterInvalid("action", "不支持的操作类型：" + action);
                        }
                    } catch (Exception e) {
                        failCount++;
                        errors.append(String.format("订单%s处理失败：%s; ", orderNo, e.getMessage()));
                        log.error("批量处理订单失败，订单号：{}，操作：{}", orderNo, action, e);
                    }
                }
                
                result.put("totalCount", orderNos.size());
                result.put("successCount", successCount);
                result.put("failCount", failCount);
                result.put("errors", errors.toString());
                
                log.info("批量处理订单完成，操作：{}，成功：{}，失败：{}", action, successCount, failCount);
                return result;
            });
            
        } catch (OrderBusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("批量处理订单失败，操作：{}", action, e);
            throw OrderBusinessException.systemError("批量处理订单失败", e);
        }
    }

    /**
     * 获取订单统计数据
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param dimension 统计维度
     * @return 统计结果
     */
    public Map<String, Object> getOrderStatistics(String startDate, String endDate, String dimension) {
        log.info("获取订单统计，开始日期：{}，结束日期：{}，维度：{}", startDate, endDate, dimension);
        
        try {
            LocalDateTime startTime = null;
            LocalDateTime endTime = null;
            
            if (StringUtils.hasText(startDate)) {
                startTime = LocalDateTime.parse(startDate + " 00:00:00", 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            
            if (StringUtils.hasText(endDate)) {
                endTime = LocalDateTime.parse(endDate + " 23:59:59", 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            
            Map<String, Object> statistics = orderInfoMapper.getOrderStatistics(startTime, endTime, null);
            statistics.put("period", startDate + " ~ " + endDate);
            statistics.put("dimension", dimension);
            statistics.put("queryTime", LocalDateTime.now());
            
            log.info("获取订单统计成功");
            return statistics;
            
        } catch (Exception e) {
            log.error("获取订单统计失败", e);
            throw OrderBusinessException.systemError("获取订单统计失败", e);
        }
    }

    /**
     * 获取订单内容关联
     * 
     * @param orderNo 订单号
     * @return 内容关联列表
     */
    public List<OrderContentAssociation> getOrderContents(String orderNo) {
        log.info("获取订单内容关联，订单号：{}", orderNo);
        
        if (!StringUtils.hasText(orderNo)) {
            throw OrderBusinessException.parameterInvalid("orderNo", "订单号不能为空");
        }
        
        try {
            List<OrderContentAssociation> associations = orderContentAssociationMapper.selectByOrderNo(orderNo);
            log.info("获取订单内容关联成功，订单号：{}，关联数量：{}", orderNo, associations.size());
            return associations;
            
        } catch (Exception e) {
            log.error("获取订单内容关联失败，订单号：{}", orderNo, e);
            throw OrderBusinessException.systemError("获取订单内容关联失败", e);
        }
    }

    /**
     * 管理订单权限
     * 
     * @param orderNo 订单号
     * @param action 操作类型（activate, revoke等）
     * @param reason 操作原因
     * @param requestId 请求ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void manageOrderPermissions(String orderNo, String action, String reason, String requestId) throws Exception {
        log.info("管理订单权限，订单号：{}，操作：{}，原因：{}，请求ID：{}", orderNo, action, reason, requestId);
        
        if (!StringUtils.hasText(orderNo)) {
            throw OrderBusinessException.parameterInvalid("orderNo", "订单号不能为空");
        }
        
        if (!StringUtils.hasText(requestId)) {
            requestId = "permission_" + action + "_" + orderNo + "_" + System.currentTimeMillis();
        }
        
        try {
            idempotencyService.executeIdempotent(requestId, () -> {
                String newStatus;
                switch (action.toLowerCase()) {
                    case "activate":
                        newStatus = "ACTIVE";
                        break;
                    case "revoke":
                        newStatus = "REVOKED";
                        break;
                    case "expire":
                        newStatus = "EXPIRED";
                        break;
                    default:
                        throw OrderBusinessException.parameterInvalid("action", "不支持的操作类型：" + action);
                }
                
                int updated = orderContentAssociationMapper.batchUpdateStatusByOrderNo(
                    orderNo, newStatus, reason);
                
                log.info("管理订单权限成功，订单号：{}，操作：{}，影响行数：{}", orderNo, action, updated);
                return null;
            });
            
        } catch (OrderBusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("管理订单权限失败，订单号：{}，操作：{}", orderNo, action, e);
            throw OrderBusinessException.systemError("管理订单权限失败", e);
        }
    }

    /**
     * 检查用户内容访问权限
     * 
     * @param userId 用户ID
     * @param contentId 内容ID
     * @return true-有权限，false-无权限
     */
    public boolean checkUserContentAccess(Long userId, Long contentId) {
        log.debug("检查用户内容访问权限，用户ID：{}，内容ID：{}", userId, contentId);
        
        if (userId == null || contentId == null) {
            return false;
        }
        
        try {
            int count = orderContentAssociationMapper.checkUserContentAccess(userId, contentId);
            boolean hasAccess = count > 0;
            log.debug("检查用户内容访问权限结果，用户ID：{}，内容ID：{}，有权限：{}", userId, contentId, hasAccess);
            return hasAccess;
            
        } catch (Exception e) {
            log.error("检查用户内容访问权限失败，用户ID：{}，内容ID：{}", userId, contentId, e);
            return false;
        }
    }
} 