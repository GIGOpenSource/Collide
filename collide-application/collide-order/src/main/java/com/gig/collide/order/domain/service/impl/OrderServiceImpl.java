package com.gig.collide.order.domain.service.impl;

import com.alicp.jetcache.anno.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.order.domain.entity.Order;
import com.gig.collide.order.domain.service.OrderService;
import com.gig.collide.order.infrastructure.cache.OrderCacheConstant;
import com.gig.collide.order.infrastructure.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 订单业务服务实现类 - 缓存增强版
 * 基于JetCache的高性能订单服务
 *
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    // =================== 订单创建和管理 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = OrderCacheConstant.ORDER_LIST_CACHE)
    @CacheInvalidate(name = OrderCacheConstant.USER_ORDER_CACHE)
    @CacheInvalidate(name = OrderCacheConstant.ORDER_STATISTICS_CACHE)
    public Long createOrder(Order order) {
        log.info("创建订单: orderNo={}, userId={}, goodsId={}, goodsType={}", 
                order.getOrderNo(), order.getUserId(), order.getGoodsId(), order.getGoodsType());
        
        // 设置默认值
        setDefaultValues(order);
        
        // 保存订单
        int result = orderMapper.insert(order);
        if (result > 0) {
            log.info("订单创建成功: id={}, orderNo={}", order.getId(), order.getOrderNo());
            return order.getId();
        } else {
            throw new RuntimeException("订单创建失败");
        }
    }

    @Override
    @Cached(name = OrderCacheConstant.ORDER_DETAIL_CACHE, 
            key = OrderCacheConstant.ORDER_DETAIL_KEY + "#id",
            expire = OrderCacheConstant.DETAIL_EXPIRE, 
            timeUnit = TimeUnit.MINUTES)
    public Order getOrderById(Long id) {
        log.debug("查询订单详情: id={}", id);
        
        if (id == null || id <= 0) {
            return null;
        }
        
        Order order = orderMapper.selectById(id);
        if (order != null) {
            log.debug("订单查询成功: id={}, orderNo={}, status={}", 
                    order.getId(), order.getOrderNo(), order.getStatus());
        }
        
        return order;
    }

    @Override
    @Cached(name = OrderCacheConstant.ORDER_DETAIL_CACHE, 
            key = OrderCacheConstant.ORDER_NO_KEY + "#orderNo",
            expire = OrderCacheConstant.DETAIL_EXPIRE, 
            timeUnit = TimeUnit.MINUTES)
    public Order getOrderByOrderNo(String orderNo) {
        log.debug("根据订单号查询订单: orderNo={}", orderNo);
        
        if (!StringUtils.hasText(orderNo)) {
            return null;
        }
        
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            log.debug("订单查询成功: orderNo={}, id={}, status={}", 
                    orderNo, order.getId(), order.getStatus());
        }
        
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheUpdate(name = OrderCacheConstant.ORDER_DETAIL_CACHE, 
                 key = OrderCacheConstant.ORDER_DETAIL_KEY + "#order.id", 
                 value = "#order")
    @CacheInvalidate(name = OrderCacheConstant.ORDER_LIST_CACHE)
    @CacheInvalidate(name = OrderCacheConstant.USER_ORDER_CACHE)
    public boolean updateOrder(Order order) {
        log.info("更新订单: id={}, orderNo={}", order.getId(), order.getOrderNo());
        
        if (order.getId() == null || order.getId() <= 0) {
            throw new IllegalArgumentException("订单ID不能为空");
        }
        
        // 设置更新时间
        order.setUpdateTime(LocalDateTime.now());
        
        int result = orderMapper.updateById(order);
        if (result > 0) {
            log.info("订单更新成功: id={}", order.getId());
            return true;
        } else {
            log.warn("订单更新失败: id={}", order.getId());
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = OrderCacheConstant.ORDER_DETAIL_CACHE, 
                     key = OrderCacheConstant.ORDER_DETAIL_KEY + "#orderId")
    @CacheInvalidate(name = OrderCacheConstant.ORDER_LIST_CACHE)
    @CacheInvalidate(name = OrderCacheConstant.USER_ORDER_CACHE)
    public boolean cancelOrder(Long orderId, String reason) {
        log.info("取消订单: orderId={}, reason={}", orderId, reason);
        
        if (orderId == null || orderId <= 0) {
            throw new IllegalArgumentException("订单ID不能为空");
        }
        
        // 验证是否可以取消
        Map<String, Object> validation = validateCancel(orderId);
        if (!(Boolean) validation.get("valid")) {
            throw new IllegalStateException((String) validation.get("message"));
        }
        
        // 更新订单状态
        LambdaUpdateWrapper<Order> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Order::getId, orderId)
                    .set(Order::getStatus, Order.OrderStatus.CANCELLED)
                    .set(Order::getUpdateTime, LocalDateTime.now());
        
        int result = orderMapper.update(null, updateWrapper);
        if (result > 0) {
            log.info("订单取消成功: orderId={}", orderId);
            return true;
        } else {
            log.warn("订单取消失败: orderId={}", orderId);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = OrderCacheConstant.ORDER_LIST_CACHE)
    @CacheInvalidate(name = OrderCacheConstant.USER_ORDER_CACHE)
    public boolean batchCancelOrders(List<Long> orderIds, String reason) {
        log.info("批量取消订单: count={}, reason={}", orderIds.size(), reason);
        
        if (CollectionUtils.isEmpty(orderIds)) {
            return true;
        }
        
        int result = orderMapper.batchUpdateStatus(orderIds, Order.OrderStatus.CANCELLED.getCode());
        log.info("批量取消订单完成: 目标={}, 实际={}", orderIds.size(), result);
        return result > 0;
    }

    // =================== 订单查询 ===================

    @Override
    @Cached(name = OrderCacheConstant.USER_ORDER_CACHE,
            key = "T(com.gig.collide.order.infrastructure.cache.OrderCacheConstant).buildUserOrderKey(#userId, #status, #page.current, #page.size)",
            expire = OrderCacheConstant.USER_ORDER_EXPIRE,
            timeUnit = TimeUnit.MINUTES)
    public IPage<Order> getOrdersByUserId(Page<Order> page, Long userId, String status) {
        log.debug("根据用户查询订单: userId={}, status={}, page={}, size={}", 
                userId, status, page.getCurrent(), page.getSize());
        
        return orderMapper.selectByUserId(page, userId, status);
    }

    @Override
    @Cached(name = OrderCacheConstant.ORDER_LIST_CACHE,
            expire = OrderCacheConstant.LIST_EXPIRE,
            timeUnit = TimeUnit.MINUTES)
    public IPage<Order> getOrdersByGoodsType(Page<Order> page, String goodsType, String status) {
        log.debug("根据商品类型查询订单: goodsType={}, status={}, page={}, size={}", 
                goodsType, status, page.getCurrent(), page.getSize());
        
        return orderMapper.selectByGoodsType(page, goodsType, status);
    }

    @Override
    @Cached(name = OrderCacheConstant.ORDER_LIST_CACHE,
            expire = OrderCacheConstant.LIST_EXPIRE,
            timeUnit = TimeUnit.MINUTES)
    public IPage<Order> getOrdersByPaymentMode(Page<Order> page, String paymentMode, String payStatus) {
        log.debug("根据支付模式查询订单: paymentMode={}, payStatus={}, page={}, size={}", 
                paymentMode, payStatus, page.getCurrent(), page.getSize());
        
        return orderMapper.selectByPaymentMode(page, paymentMode, payStatus);
    }

    @Override
    @Cached(name = OrderCacheConstant.ORDER_LIST_CACHE,
            key = "T(com.gig.collide.order.infrastructure.cache.OrderCacheConstant).buildSellerOrderKey(#sellerId, #page.current, #page.size)",
            expire = OrderCacheConstant.LIST_EXPIRE,
            timeUnit = TimeUnit.MINUTES)
    public IPage<Order> getOrdersBySellerId(Page<Order> page, Long sellerId, String status) {
        log.debug("根据商家查询订单: sellerId={}, status={}, page={}, size={}", 
                sellerId, status, page.getCurrent(), page.getSize());
        
        return orderMapper.selectBySellerId(page, sellerId, status);
    }

    @Override
    public IPage<Order> getOrdersByTimeRange(Page<Order> page, LocalDateTime startTime, LocalDateTime endTime, String status) {
        log.debug("根据时间范围查询订单: start={}, end={}, status={}, page={}, size={}", 
                startTime, endTime, status, page.getCurrent(), page.getSize());
        
        return orderMapper.selectByTimeRange(page, startTime, endTime, status);
    }

    @Override
    public IPage<Order> searchOrders(Page<Order> page, String keyword) {
        log.debug("搜索订单: keyword={}, page={}, size={}", 
                keyword, page.getCurrent(), page.getSize());
        
        if (!StringUtils.hasText(keyword)) {
            return new Page<>(page.getCurrent(), page.getSize());
        }
        
        return orderMapper.searchOrders(page, keyword.trim());
    }

    // =================== 支付相关 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> processPayment(Long orderId, String payMethod) {
        log.info("处理订单支付: orderId={}, payMethod={}", orderId, payMethod);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 验证订单是否可以支付
            Map<String, Object> validation = validatePayment(orderId);
            if (!(Boolean) validation.get("valid")) {
                result.put("success", false);
                result.put("message", validation.get("message"));
                return result;
            }
            
            Order order = (Order) validation.get("order");
            
            // 根据支付模式处理
            if (order.getPaymentMode() == Order.PaymentMode.COIN) {
                // 金币支付逻辑
                result = processCoinPayment(order, payMethod);
            } else {
                // 现金支付逻辑
                result = processCashPayment(order, payMethod);
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("处理订单支付失败: orderId={}", orderId, e);
            result.put("success", false);
            result.put("message", "支付处理失败: " + e.getMessage());
            return result;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = OrderCacheConstant.ORDER_DETAIL_CACHE, 
                     key = OrderCacheConstant.ORDER_DETAIL_KEY + "#orderId")
    @CacheInvalidate(name = OrderCacheConstant.USER_ORDER_CACHE)
    public boolean confirmPayment(Long orderId, String payMethod) {
        log.info("确认支付成功: orderId={}, payMethod={}", orderId, payMethod);
        
        // 更新支付信息
        int result = orderMapper.updatePaymentInfo(orderId, 
                Order.PayStatus.PAID.getCode(), payMethod, LocalDateTime.now());
        
        if (result > 0) {
            // 更新订单状态
            Order order = getOrderById(orderId);
            if (order != null) {
                String newStatus = order.isVirtualGoods() ? 
                        Order.OrderStatus.COMPLETED.getCode() : Order.OrderStatus.PAID.getCode();
                updateOrderStatus(orderId, newStatus);
                
                // 处理后续业务逻辑
                handlePaymentSuccess(order);
            }
            
            log.info("支付确认成功: orderId={}", orderId);
            return true;
        } else {
            log.warn("支付确认失败: orderId={}", orderId);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = OrderCacheConstant.ORDER_DETAIL_CACHE, 
                     key = OrderCacheConstant.ORDER_DETAIL_KEY + "#orderId")
    @CacheInvalidate(name = OrderCacheConstant.USER_ORDER_CACHE)
    public boolean updatePaymentStatus(Long orderId, String payStatus, String payMethod) {
        log.info("更新订单支付状态: orderId={}, payStatus={}, payMethod={}", orderId, payStatus, payMethod);
        
        try {
            // 更新支付状态和支付方式
            LocalDateTime payTime = "paid".equals(payStatus) ? LocalDateTime.now() : null;
            int result = orderMapper.updatePaymentInfo(orderId, payStatus, payMethod, payTime);
            
            if (result > 0) {
                // 如果支付成功，更新订单状态
                if ("paid".equals(payStatus)) {
                    Order order = getOrderById(orderId);
                    if (order != null) {
                        String newStatus = order.isVirtualGoods() ? 
                                Order.OrderStatus.COMPLETED.getCode() : Order.OrderStatus.PAID.getCode();
                        updateOrderStatus(orderId, newStatus);
                        
                        // 处理支付成功后的业务逻辑
                        handlePaymentSuccess(order);
                    }
                }
                
                log.info("订单支付状态更新成功: orderId={}, payStatus={}", orderId, payStatus);
                return true;
            } else {
                log.warn("订单支付状态更新失败: orderId={}, payStatus={}", orderId, payStatus);
                return false;
            }
        } catch (Exception e) {
            log.error("订单支付状态更新异常: orderId={}, payStatus={}", orderId, payStatus, e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handlePaymentCallback(String orderNo, String payStatus, String payMethod, 
                                       LocalDateTime payTime, Map<String, Object> extraInfo) {
        log.info("处理支付回调: orderNo={}, payStatus={}, payMethod={}", orderNo, payStatus, payMethod);
        
        Order order = getOrderByOrderNo(orderNo);
        if (order == null) {
            log.warn("订单不存在: orderNo={}", orderNo);
            return false;
        }
        
        if ("paid".equals(payStatus)) {
            return confirmPayment(order.getId(), payMethod);
        } else {
            log.warn("支付失败回调: orderNo={}, payStatus={}", orderNo, payStatus);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> requestRefund(Long orderId, String reason) {
        log.info("申请退款: orderId={}, reason={}", orderId, reason);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 验证是否可以退款
            Map<String, Object> validation = validateRefund(orderId);
            if (!(Boolean) validation.get("valid")) {
                result.put("success", false);
                result.put("message", validation.get("message"));
                return result;
            }
            
            Order order = (Order) validation.get("order");
            
            // 处理退款逻辑
            boolean success = processRefund(order, reason);
            
            result.put("success", success);
            result.put("message", success ? "退款申请成功" : "退款申请失败");
            
            return result;
            
        } catch (Exception e) {
            log.error("申请退款失败: orderId={}", orderId, e);
            result.put("success", false);
            result.put("message", "退款申请失败: " + e.getMessage());
            return result;
        }
    }

    // =================== 订单状态管理 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = OrderCacheConstant.ORDER_DETAIL_CACHE, 
                     key = OrderCacheConstant.ORDER_DETAIL_KEY + "#orderId")
    @CacheInvalidate(name = OrderCacheConstant.USER_ORDER_CACHE)
    public boolean updateOrderStatus(Long orderId, String newStatus) {
        log.info("更新订单状态: orderId={}, newStatus={}", orderId, newStatus);
        
        LambdaUpdateWrapper<Order> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Order::getId, orderId)
                    .set(Order::getStatus, newStatus)
                    .set(Order::getUpdateTime, LocalDateTime.now());
        
        int result = orderMapper.update(null, updateWrapper);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = OrderCacheConstant.ORDER_LIST_CACHE)
    @CacheInvalidate(name = OrderCacheConstant.USER_ORDER_CACHE)
    public boolean batchUpdateOrderStatus(List<Long> orderIds, String newStatus) {
        log.info("批量更新订单状态: count={}, newStatus={}", orderIds.size(), newStatus);
        
        if (CollectionUtils.isEmpty(orderIds)) {
            return true;
        }
        
        int result = orderMapper.batchUpdateStatus(orderIds, newStatus);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean shipOrder(Long orderId, Map<String, Object> shippingInfo) {
        log.info("订单发货: orderId={}, shippingInfo={}", orderId, shippingInfo);
        
        // 更新为已发货状态
        boolean success = updateOrderStatus(orderId, Order.OrderStatus.SHIPPED.getCode());
        
        if (success) {
            // 处理物流信息（可以扩展到物流表）
            log.info("订单发货成功: orderId={}", orderId);
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmReceipt(Long orderId) {
        log.info("确认收货: orderId={}", orderId);
        
        return completeOrder(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean completeOrder(Long orderId) {
        log.info("完成订单: orderId={}", orderId);
        
        boolean success = updateOrderStatus(orderId, Order.OrderStatus.COMPLETED.getCode());
        
        if (success) {
            // 处理订单完成后的业务逻辑
            handleOrderComplete(orderId);
        }
        
        return success;
    }

    // =================== 定时任务相关 ===================

    @Override
    public List<Order> getTimeoutOrders(Integer timeoutMinutes) {
        log.debug("查询超时订单: timeoutMinutes={}", timeoutMinutes);
        
        if (timeoutMinutes == null || timeoutMinutes <= 0) {
            timeoutMinutes = 30; // 默认30分钟
        }
        
        LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(timeoutMinutes);
        return orderMapper.selectTimeoutOrders(timeoutTime);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int autoCancelTimeoutOrders(Integer timeoutMinutes) {
        log.info("自动取消超时订单: timeoutMinutes={}", timeoutMinutes);
        
        List<Order> timeoutOrders = getTimeoutOrders(timeoutMinutes);
        if (CollectionUtils.isEmpty(timeoutOrders)) {
            return 0;
        }
        
        List<Long> orderIds = timeoutOrders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());
        
        boolean success = batchCancelOrders(orderIds, "系统自动取消（支付超时）");
        
        int cancelCount = success ? orderIds.size() : 0;
        log.info("自动取消超时订单完成: count={}", cancelCount);
        
        return cancelCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int autoCompleteShippedOrders(Integer days) {
        log.info("自动完成已发货订单: days={}", days);
        
        if (days == null || days <= 0) {
            days = 7; // 默认7天
        }
        
        // 查询超过指定天数的已发货订单
        LocalDateTime completionTime = LocalDateTime.now().minusDays(days);
        
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getStatus, Order.OrderStatus.SHIPPED.getCode())
                   .le(Order::getUpdateTime, completionTime);
        
        List<Order> shippedOrders = orderMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(shippedOrders)) {
            return 0;
        }
        
        List<Long> orderIds = shippedOrders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());
        
        boolean success = batchUpdateOrderStatus(orderIds, Order.OrderStatus.COMPLETED.getCode());
        
        int completeCount = success ? orderIds.size() : 0;
        log.info("自动完成已发货订单完成: count={}", completeCount);
        
        return completeCount;
    }

    // =================== 统计分析 ===================

    @Override
    @Cached(name = OrderCacheConstant.ORDER_STATISTICS_CACHE,
            key = OrderCacheConstant.USER_STATS_KEY + "#userId",
            expire = OrderCacheConstant.STATISTICS_EXPIRE,
            timeUnit = TimeUnit.MINUTES)
    public Map<String, Object> getUserOrderStatistics(Long userId) {
        log.debug("统计用户订单数据: userId={}", userId);
        return orderMapper.selectUserOrderStatistics(userId);
    }

    @Override
    @Cached(name = OrderCacheConstant.ORDER_STATISTICS_CACHE,
            key = OrderCacheConstant.GOODS_STATS_KEY + "#goodsId",
            expire = OrderCacheConstant.STATISTICS_EXPIRE,
            timeUnit = TimeUnit.MINUTES)
    public Map<String, Object> getGoodsSalesStatistics(Long goodsId) {
        log.debug("统计商品销售数据: goodsId={}", goodsId);
        return orderMapper.selectGoodsSalesStatistics(goodsId);
    }

    @Override
    @Cached(name = OrderCacheConstant.ORDER_STATISTICS_CACHE,
            expire = OrderCacheConstant.STATISTICS_EXPIRE,
            timeUnit = TimeUnit.MINUTES)
    public List<Map<String, Object>> getOrderStatisticsByType() {
        log.debug("按商品类型统计订单");
        return orderMapper.selectOrderStatisticsByType();
    }

    @Override
    @Cached(name = OrderCacheConstant.HOT_GOODS_CACHE,
            expire = OrderCacheConstant.HOT_GOODS_EXPIRE,
            timeUnit = TimeUnit.MINUTES)
    public List<Map<String, Object>> getHotGoods(Integer limit) {
        log.debug("查询热门商品: limit={}", limit);
        
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        
        return orderMapper.selectHotGoods(limit);
    }

    @Override
    @Cached(name = OrderCacheConstant.REVENUE_CACHE,
            key = "T(com.gig.collide.order.infrastructure.cache.OrderCacheConstant).buildRevenueKey(#startDate, #endDate)",
            expire = OrderCacheConstant.REVENUE_EXPIRE,
            timeUnit = TimeUnit.MINUTES)
    public List<Map<String, Object>> getDailyRevenue(String startDate, String endDate) {
        log.debug("查询日营收统计: start={}, end={}", startDate, endDate);
        return orderMapper.selectDailyRevenue(startDate, endDate);
    }

    @Override
    @Cached(name = OrderCacheConstant.USER_PURCHASE_CACHE,
            expire = OrderCacheConstant.PURCHASE_EXPIRE,
            timeUnit = TimeUnit.MINUTES)
    public List<Order> getUserRecentOrders(Long userId, Integer limit) {
        log.debug("查询用户最近购买记录: userId={}, limit={}", userId, limit);
        
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        
        return orderMapper.selectUserRecentOrders(userId, limit);
    }

    // =================== 专用查询 ===================

    @Override
    public IPage<Order> getUserCoinOrders(Page<Order> page, Long userId) {
        log.debug("查询用户金币消费订单: userId={}, page={}, size={}", 
                userId, page.getCurrent(), page.getSize());
        
        return orderMapper.selectUserCoinOrders(page, userId);
    }

    @Override
    public IPage<Order> getUserRechargeOrders(Page<Order> page, Long userId) {
        log.debug("查询用户充值订单: userId={}, page={}, size={}", 
                userId, page.getCurrent(), page.getSize());
        
        return orderMapper.selectUserRechargeOrders(page, userId);
    }

    @Override
    public IPage<Order> getContentOrders(Page<Order> page, Long contentId) {
        log.debug("查询内容购买订单: contentId={}, page={}, size={}", 
                contentId, page.getCurrent(), page.getSize());
        
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getGoodsType, Order.GoodsType.CONTENT.getCode())
                   .eq(Order::getPayStatus, Order.PayStatus.PAID.getCode());
        
        if (contentId != null) {
            queryWrapper.eq(Order::getContentId, contentId);
        }
        
        queryWrapper.orderByDesc(Order::getPayTime);
        
        return orderMapper.selectPage(page, queryWrapper);
    }

    @Override
    public IPage<Order> getSubscriptionOrders(Page<Order> page, String subscriptionType) {
        log.debug("查询订阅订单: subscriptionType={}, page={}, size={}", 
                subscriptionType, page.getCurrent(), page.getSize());
        
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getGoodsType, Order.GoodsType.SUBSCRIPTION.getCode())
                   .eq(Order::getPayStatus, Order.PayStatus.PAID.getCode());
        
        if (StringUtils.hasText(subscriptionType)) {
            queryWrapper.eq(Order::getSubscriptionType, subscriptionType);
        }
        
        queryWrapper.orderByDesc(Order::getPayTime);
        
        return orderMapper.selectPage(page, queryWrapper);
    }

    // =================== 业务验证 ===================

    @Override
    public Map<String, Object> validatePayment(Long orderId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Order order = getOrderById(orderId);
            if (order == null) {
                result.put("valid", false);
                result.put("message", "订单不存在");
                return result;
            }
            
            if (!order.canPay()) {
                result.put("valid", false);
                result.put("message", "订单状态不允许支付");
                return result;
            }
            
            result.put("valid", true);
            result.put("order", order);
            
        } catch (Exception e) {
            log.error("支付验证失败: orderId={}", orderId, e);
            result.put("valid", false);
            result.put("message", "验证失败: " + e.getMessage());
        }
        
        return result;
    }

    @Override
    public Map<String, Object> validateCancel(Long orderId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Order order = getOrderById(orderId);
            if (order == null) {
                result.put("valid", false);
                result.put("message", "订单不存在");
                return result;
            }
            
            if (!order.canCancel()) {
                result.put("valid", false);
                result.put("message", "订单状态不允许取消");
                return result;
            }
            
            result.put("valid", true);
            result.put("order", order);
            
        } catch (Exception e) {
            log.error("取消验证失败: orderId={}", orderId, e);
            result.put("valid", false);
            result.put("message", "验证失败: " + e.getMessage());
        }
        
        return result;
    }

    @Override
    public Map<String, Object> validateRefund(Long orderId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Order order = getOrderById(orderId);
            if (order == null) {
                result.put("valid", false);
                result.put("message", "订单不存在");
                return result;
            }
            
            if (!order.canRefund()) {
                result.put("valid", false);
                result.put("message", "订单状态不允许退款");
                return result;
            }
            
            result.put("valid", true);
            result.put("order", order);
            
        } catch (Exception e) {
            log.error("退款验证失败: orderId={}", orderId, e);
            result.put("valid", false);
            result.put("message", "验证失败: " + e.getMessage());
        }
        
        return result;
    }

    @Override
    public String generateOrderNo(Long userId) {
        // 格式：ORD + 年月日时分秒 + 用户ID后4位 + 随机3位数
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timeStr = LocalDateTime.now().format(formatter);
        String userSuffix = String.format("%04d", userId % 10000);
        String randomSuffix = String.format("%03d", new Random().nextInt(1000));
        
        return "ORD" + timeStr + userSuffix + randomSuffix;
    }

    // =================== 私有方法 ===================

    /**
     * 设置订单默认值
     */
    private void setDefaultValues(Order order) {
        if (order.getStatus() == null) {
            order.setStatus(Order.OrderStatus.PENDING);
        }
        
        if (order.getPayStatus() == null) {
            order.setPayStatus(Order.PayStatus.UNPAID);
        }
        
        if (order.getQuantity() == null || order.getQuantity() <= 0) {
            order.setQuantity(1);
        }
    }

    /**
     * 处理金币支付
     */
    private Map<String, Object> processCoinPayment(Order order, String payMethod) {
        Map<String, Object> result = new HashMap<>();
        
        // TODO: 调用钱包服务扣减金币
        // 这里需要集成钱包模块的 consume_coin 方法
        
        log.info("处理金币支付: orderId={}, coinCost={}", order.getId(), order.getCoinCost());
        
        // 模拟金币支付成功
        boolean success = confirmPayment(order.getId(), "coin");
        
        result.put("success", success);
        result.put("message", success ? "金币支付成功" : "金币支付失败");
        result.put("paymentMode", "coin");
        
        return result;
    }

    /**
     * 处理现金支付
     */
    private Map<String, Object> processCashPayment(Order order, String payMethod) {
        Map<String, Object> result = new HashMap<>();
        
        log.info("处理现金支付: orderId={}, finalAmount={}, payMethod={}", 
                order.getId(), order.getFinalAmount(), payMethod);
        
        // TODO: 集成第三方支付
        // 这里应该调用支付宝、微信等支付接口
        
        result.put("success", true);
        result.put("message", "支付请求已发起");
        result.put("paymentMode", "cash");
        result.put("payUrl", "mock_pay_url"); // 模拟支付URL
        
        return result;
    }

    /**
     * 处理退款
     */
    private boolean processRefund(Order order, String reason) {
        log.info("处理退款: orderId={}, reason={}", order.getId(), reason);
        
        // TODO: 调用退款接口
        
        // 更新支付状态为已退款
        int result = orderMapper.updatePaymentInfo(order.getId(), 
                Order.PayStatus.REFUNDED.getCode(), order.getPayMethod(), LocalDateTime.now());
        
        return result > 0;
    }

    /**
     * 处理支付成功后的业务逻辑
     */
    private void handlePaymentSuccess(Order order) {
        log.info("处理支付成功后续逻辑: orderId={}, goodsType={}", order.getId(), order.getGoodsType());
        
        // 根据商品类型处理不同的业务逻辑
        switch (order.getGoodsType()) {
            case COIN:
                // 金币充值：调用钱包服务发放金币
                log.info("处理金币充值: orderId={}, coinAmount={}", order.getId(), order.getCoinAmount());
                // TODO: 调用 grant_coin_reward
                break;
                
            case SUBSCRIPTION:
                // 订阅服务：开通会员权限
                log.info("处理订阅开通: orderId={}, subscriptionType={}, duration={}", 
                        order.getId(), order.getSubscriptionType(), order.getSubscriptionDuration());
                // TODO: 调用用户服务开通VIP
                break;
                
            case CONTENT:
                // 付费内容：开放访问权限
                log.info("处理内容解锁: orderId={}, contentId={}", order.getId(), order.getContentId());
                // TODO: 调用内容服务解锁访问权限
                break;
                
            case GOODS:
                // 实体商品：通知发货
                log.info("处理商品发货通知: orderId={}, goodsId={}", order.getId(), order.getGoodsId());
                // TODO: 发送发货通知
                break;
        }
    }

    /**
     * 处理订单完成后的业务逻辑
     */
    private void handleOrderComplete(Long orderId) {
        log.info("处理订单完成后续逻辑: orderId={}", orderId);
        
        // TODO: 
        // 1. 更新商品销量
        // 2. 发送完成通知
        // 3. 触发推荐算法
    }
}