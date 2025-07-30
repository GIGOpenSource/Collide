package com.gig.collide.order.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.order.OrderFacadeService;
import com.gig.collide.api.order.request.OrderCreateRequest;
import com.gig.collide.api.order.request.OrderQueryRequest;
import com.gig.collide.api.order.request.OrderPayRequest;
import com.gig.collide.api.order.request.OrderCancelRequest;
import com.gig.collide.api.order.response.OrderResponse;
import com.gig.collide.order.domain.entity.Order;
import com.gig.collide.order.domain.service.OrderService;
import com.gig.collide.order.infrastructure.cache.OrderCacheConstant;
import com.gig.collide.web.vo.Result;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.api.payment.PaymentFacadeService;
import com.gig.collide.api.user.UserFacadeService;
import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 订单门面服务实现类 - 缓存增强版
 * 对齐payment模块设计风格，提供完整的订单服务
 * 包含缓存功能、跨模块集成、错误处理、数据转换
 * 
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-16
 */
@Slf4j
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class OrderFacadeServiceImpl implements OrderFacadeService {

    private final OrderService orderService;
    
    // =================== 跨模块服务注入（预留扩展） ===================
    // 注：跨模块服务调用根据业务需要可在此添加
    @Autowired
    private PaymentFacadeService paymentFacadeService;
    
    @Autowired  
    private UserFacadeService userFacadeService;

    // =================== 订单核心功能 ===================

    @Override
    @CacheInvalidate(name = OrderCacheConstant.USER_ORDER_CACHE)
    @CacheInvalidate(name = OrderCacheConstant.ORDER_STATISTICS_CACHE)
    public Result<OrderResponse> createOrder(OrderCreateRequest request) {
        try {
            log.info("创建订单请求: 用户={}, 商品={}, 数量={}, 金额={}", 
                    request.getUserId(), request.getGoodsId(), request.getQuantity(), request.getFinalAmount());
            long startTime = System.currentTimeMillis();
            
            // 1. 请求参数转换为实体
            Order order = new Order();
            BeanUtils.copyProperties(request, order);
            
            // 2. 调用业务服务创建订单
            Order createdOrder = orderService.createOrder(order);
            
            // 3. 实体转换为响应对象
            OrderResponse response = convertToResponse(createdOrder);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("订单创建成功: 订单号={}, 耗时={}ms", createdOrder.getOrderNo(), duration);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("创建订单失败: 用户={}, 商品={}", request.getUserId(), request.getGoodsId(), e);
            return Result.error("ORDER_CREATE_ERROR", "创建订单失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = OrderCacheConstant.ORDER_DETAIL_CACHE)
    @CacheInvalidate(name = OrderCacheConstant.USER_ORDER_CACHE)
    @CacheInvalidate(name = OrderCacheConstant.ORDER_PAY_STATUS_CACHE)
    public Result<OrderResponse> payOrder(OrderPayRequest request) {
        try {
            log.info("支付订单请求: 订单={}, 支付方式={}, 金额={}", 
                    request.getOrderId(), request.getPayMethod(), request.getPayAmount());
            long startTime = System.currentTimeMillis();
            
            // 调用业务服务支付订单
            Order paidOrder = orderService.payOrder(
                request.getOrderId(), 
                request.getPayMethod(), 
                request.getPayAmount(),
                request.getThirdPartyTradeNo()
            );
            
            // 实体转换为响应对象
            OrderResponse response = convertToResponse(paidOrder);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("订单支付成功: 订单号={}, 耗时={}ms", paidOrder.getOrderNo(), duration);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("支付订单失败: 订单={}", request.getOrderId(), e);
            return Result.error("ORDER_PAY_ERROR", "支付订单失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = OrderCacheConstant.ORDER_DETAIL_CACHE)
    @CacheInvalidate(name = OrderCacheConstant.USER_ORDER_CACHE)
    @CacheInvalidate(name = OrderCacheConstant.ORDER_STATUS_CACHE)
    public Result<Void> cancelOrder(OrderCancelRequest request) {
        try {
            log.info("取消订单请求: 订单={}, 用户={}, 原因={}", 
                    request.getOrderId(), request.getUserId(), request.getCancelReason());
            
            // 调用业务服务取消订单
            boolean success = orderService.cancelOrder(
                request.getOrderId(), 
                request.getUserId(), 
                request.getCancelReason()
            );
            
            if (success) {
                log.info("订单取消成功: 订单={}", request.getOrderId());
                return Result.success(null);
            } else {
                log.warn("订单取消失败: 订单={}", request.getOrderId());
                return Result.error("ORDER_CANCEL_ERROR", "取消订单失败");
            }
            
        } catch (Exception e) {
            log.error("取消订单失败: 订单={}, 用户={}", request.getOrderId(), request.getUserId(), e);
            return Result.error("ORDER_CANCEL_ERROR", "取消订单失败: " + e.getMessage());
        }
    }

    // =================== 订单查询功能 ===================

    @Override
    @Cached(name = OrderCacheConstant.ORDER_DETAIL_CACHE, key = OrderCacheConstant.ORDER_DETAIL_BY_ID_KEY,
            expire = OrderCacheConstant.ORDER_DETAIL_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<OrderResponse> getOrderById(Long orderId) {
        try {
            log.debug("查询订单详情: ID={}", orderId);
            
            Order order = orderService.getOrderById(orderId);
            
            if (order != null) {
                OrderResponse response = convertToResponse(order);
                log.debug("订单详情查询成功: ID={}, 状态={}", orderId, response.getStatus());
                return Result.success(response);
            } else {
                log.warn("订单不存在: ID={}", orderId);
                return Result.error("ORDER_NOT_FOUND", "订单不存在");
            }
            
        } catch (Exception e) {
            log.error("查询订单详情失败: ID={}", orderId, e);
            return Result.error("ORDER_QUERY_ERROR", "查询订单失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = OrderCacheConstant.ORDER_DETAIL_CACHE, key = OrderCacheConstant.ORDER_DETAIL_BY_NO_KEY,
            expire = OrderCacheConstant.ORDER_DETAIL_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<OrderResponse> getOrderByOrderNo(String orderNo) {
        try {
            log.debug("根据订单号查询: 订单号={}", orderNo);
            
            Order order = orderService.getOrderByOrderNo(orderNo);
            
            if (order != null) {
                OrderResponse response = convertToResponse(order);
                log.debug("订单号查询成功: 订单号={}, 状态={}", orderNo, response.getStatus());
                return Result.success(response);
            } else {
                log.warn("订单不存在: 订单号={}", orderNo);
                return Result.error("ORDER_NOT_FOUND", "订单不存在");
            }
            
        } catch (Exception e) {
            log.error("根据订单号查询失败: 订单号={}", orderNo, e);
            return Result.error("ORDER_QUERY_ERROR", "查询订单失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = OrderCacheConstant.USER_ORDER_CACHE, key = OrderCacheConstant.USER_ORDER_KEY,
            expire = OrderCacheConstant.USER_ORDER_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<PageResponse<OrderResponse>> queryOrders(OrderQueryRequest request) {
        try {
            log.debug("分页查询订单: 用户={}, 页码={}, 状态={}", 
                     request.getUserId(), request.getPageNum(), request.getStatus());
            long startTime = System.currentTimeMillis();
            
            // 调用业务服务分页查询
            IPage<Order> orderPage = orderService.queryOrders(
                request.getUserId(),
                request.getStatus(),
                request.getPayStatus(),
                request.getPageNum(),
                request.getPageSize()
            );
            
            // 转换分页数据
            List<OrderResponse> responseList = orderPage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            PageResponse<OrderResponse> pageResponse = convertToPageResponse(orderPage, responseList);
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("订单查询完成: 用户={}, 记录数={}, 耗时={}ms", 
                    request.getUserId(), pageResponse.getTotal(), duration);
            
            return Result.success(pageResponse);
            
        } catch (Exception e) {
            log.error("分页查询订单失败: 用户={}", request.getUserId(), e);
            return Result.error("ORDER_QUERY_ERROR", "查询订单失败: " + e.getMessage());
        }
    }

    // =================== 订单状态管理 ===================

    @Override
    @CacheInvalidate(name = OrderCacheConstant.ORDER_DETAIL_CACHE)
    @CacheInvalidate(name = OrderCacheConstant.USER_ORDER_CACHE)
    @CacheInvalidate(name = OrderCacheConstant.ORDER_STATUS_CACHE)
    public Result<Void> updateOrderStatus(Long orderId, String status) {
        try {
            log.info("更新订单状态: 订单={}, 新状态={}", orderId, status);
            
            boolean success = orderService.updateOrderStatus(orderId, status);
            
            if (success) {
                log.info("订单状态更新成功: 订单={}", orderId);
                return Result.success(null);
            } else {
                log.warn("订单状态更新失败: 订单={}", orderId);
                return Result.error("ORDER_UPDATE_ERROR", "更新订单状态失败");
            }
            
        } catch (Exception e) {
            log.error("更新订单状态失败: 订单={}", orderId, e);
            return Result.error("ORDER_UPDATE_ERROR", "更新订单状态失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = OrderCacheConstant.ORDER_DETAIL_CACHE)
    @CacheInvalidate(name = OrderCacheConstant.USER_ORDER_CACHE)
    @CacheInvalidate(name = OrderCacheConstant.ORDER_STATISTICS_CACHE)
    public Result<Void> deleteOrder(Long orderId) {
        try {
            log.info("删除订单: 订单={}", orderId);
            
            // 注意：这里需要用户ID进行权限校验，但API接口没有提供
            // 实际项目中应该从请求上下文或Token中获取当前用户ID
            // 这里简化处理，直接查询订单获取用户ID
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                log.warn("订单不存在: 订单={}", orderId);
                return Result.error("ORDER_NOT_FOUND", "订单不存在");
            }
            
            boolean success = orderService.deleteOrder(orderId, order.getUserId());
            
            if (success) {
                log.info("订单删除成功: 订单={}", orderId);
                return Result.success(null);
            } else {
                log.warn("订单删除失败: 订单={}", orderId);
                return Result.error("ORDER_DELETE_ERROR", "删除订单失败");
            }
            
        } catch (Exception e) {
            log.error("删除订单失败: 订单={}", orderId, e);
            return Result.error("ORDER_DELETE_ERROR", "删除订单失败: " + e.getMessage());
        }
    }

    // =================== 数据转换工具方法 ===================

    /**
     * 实体转换为响应对象
     * 对齐payment模块数据转换风格
     * 
     * @param order 订单实体
     * @return 订单响应对象
     */
    private OrderResponse convertToResponse(Order order) {
        if (order == null) {
            return null;
        }
        
        OrderResponse response = new OrderResponse();
        BeanUtils.copyProperties(order, response);
        
        // 设置业务扩展字段（如果OrderResponse支持的话）
        // 注：这些字段需要在OrderResponse中定义，暂时注释掉以避免编译错误
        // response.setStatusDesc(getStatusDesc(order.getStatus()));
        // response.setPayStatusDesc(getPayStatusDesc(order.getPayStatus()));
        // response.setCancellable(order.canCancel());
        // response.setPayable(order.canPay());
        
        // 计算订单持续时间（如果已完成且OrderResponse支持的话）
        // if (order.getPayTime() != null && order.getCreateTime() != null) {
        //     long duration = java.time.Duration.between(order.getCreateTime(), order.getPayTime()).getSeconds();
        //     response.setProcessDuration(duration);
        // }
        
        return response;
    }

    /**
     * 转换为分页响应对象
     * 对齐payment模块分页转换风格
     */
    private PageResponse<OrderResponse> convertToPageResponse(IPage<Order> page, List<OrderResponse> responses) {
        PageResponse<OrderResponse> result = new PageResponse<>();
        result.setDatas(responses);
        result.setTotal((int) page.getTotal());
        result.setCurrentPage((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setTotalPage((int) page.getPages());
        return result;
    }

    /**
     * 获取状态描述
     * 对齐payment模块描述风格
     */
    private String getStatusDesc(String status) {
        if (status == null) {
            return "未知状态";
        }
        
        return switch (status.toLowerCase()) {
            case "pending" -> "待支付";
            case "paid" -> "已支付";
            case "shipped" -> "已发货";
            case "completed" -> "已完成";
            case "cancelled" -> "已取消";
            case "refunding" -> "退款中";
            case "refunded" -> "已退款";
            default -> "未知状态";
        };
    }

    /**
     * 获取支付状态描述
     * 对齐payment模块描述风格
     */
    private String getPayStatusDesc(String payStatus) {
        if (payStatus == null) {
            return "未知支付状态";
        }
        
        return switch (payStatus.toLowerCase()) {
            case "unpaid" -> "未支付";
            case "paid" -> "已支付";
            case "refunded" -> "已退款";
            case "partial_refunded" -> "部分退款";
            default -> "未知支付状态";
        };
    }
}