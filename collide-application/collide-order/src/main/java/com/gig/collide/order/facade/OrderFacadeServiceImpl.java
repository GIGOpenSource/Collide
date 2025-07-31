package com.gig.collide.order.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.order.OrderFacadeService;
import com.gig.collide.api.order.request.OrderCreateRequest;
import com.gig.collide.api.order.request.OrderQueryRequest;
import com.gig.collide.api.order.response.OrderResponse;
import com.gig.collide.api.user.WalletFacadeService;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import com.gig.collide.order.domain.entity.Order;
import com.gig.collide.order.domain.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单模块对外服务实现类 - 缓存增强版
 *
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 */
@Slf4j
@Service
@DubboService(version = "2.0.0", timeout = 10000)
@RequiredArgsConstructor
public class OrderFacadeServiceImpl implements OrderFacadeService {

    private final OrderService orderService;
    
    @DubboReference(version = "2.0.0", timeout = 5000, check = false)
    private WalletFacadeService walletFacadeService;

    @Override
    public Result<Void> createOrder(OrderCreateRequest request) {
        try {
            log.info("REST创建订单: userId={}, goodsId={}", request.getUserId(), request.getGoodsId());
            request.validateParams();
            
            Order order = convertToEntity(request);
            orderService.createOrder(order);
            
            return Result.success();
        } catch (Exception e) {
            log.error("订单创建失败", e);
            return Result.failure("订单创建失败: " + e.getMessage());
        }
    }

    @Override
    public Result<OrderResponse> getOrderById(Long id) {
        try {
            Order order = orderService.getOrderById(id);
            if (order == null) {
                return Result.failure("订单不存在");
            }
            return Result.success(convertToResponse(order));
        } catch (Exception e) {
            log.error("查询订单失败: id={}", id, e);
            return Result.failure("查询失败");
        }
    }

    @Override
    public Result<OrderResponse> getOrderByOrderNo(String orderNo) {
        try {
            Order order = orderService.getOrderByOrderNo(orderNo);
            if (order == null) {
                return Result.failure("订单不存在");
            }
            return Result.success(convertToResponse(order));
        } catch (Exception e) {
            log.error("查询订单失败: orderNo={}", orderNo, e);
            return Result.failure("查询失败");
        }
    }

    @Override
    public Result<Void> cancelOrder(Long orderId, String reason) {
        try {
            boolean success = orderService.cancelOrder(orderId, reason);
            return success ? Result.success() : Result.failure("取消失败");
        } catch (Exception e) {
            log.error("取消订单失败: orderId={}", orderId, e);
            return Result.failure("取消失败");
        }
    }

    @Override
    public Result<Void> batchCancelOrders(List<Long> orderIds, String reason) {
        try {
            boolean success = orderService.batchCancelOrders(orderIds, reason);
            return success ? Result.success() : Result.failure("批量取消失败");
        } catch (Exception e) {
            log.error("批量取消失败", e);
            return Result.failure("批量取消失败");
        }
    }

    @Override
    public PageResponse<OrderResponse> queryOrders(OrderQueryRequest request) {
        try {
            request.validateParams();
            Page<Order> page = new Page<>(request.getCurrentPage(), request.getPageSize());
            IPage<Order> result;
            
            if (request.getUserId() != null) {
                result = orderService.getOrdersByUserId(page, request.getUserId(), request.getStatus());
            } else {
                result = orderService.getOrdersByGoodsType(page, request.getGoodsType(), request.getStatus());
            }
            
            return convertToPageResponse(result);
        } catch (Exception e) {
            log.error("查询订单失败", e);
            return PageResponse.empty();
        }
    }

    // 其他方法的简化实现...
    @Override
    public PageResponse<OrderResponse> getUserOrders(Long userId, String status, Integer currentPage, Integer pageSize) {
        Page<Order> page = new Page<>(currentPage, pageSize);
        IPage<Order> result = orderService.getOrdersByUserId(page, userId, status);
        return convertToPageResponse(result);
    }

    @Override
    public PageResponse<OrderResponse> getOrdersByGoodsType(String goodsType, String status, Integer currentPage, Integer pageSize) {
        Page<Order> page = new Page<>(currentPage, pageSize);
        IPage<Order> result = orderService.getOrdersByGoodsType(page, goodsType, status);
        return convertToPageResponse(result);
    }

    @Override
    public PageResponse<OrderResponse> getSellerOrders(Long sellerId, String status, Integer currentPage, Integer pageSize) {
        Page<Order> page = new Page<>(currentPage, pageSize);
        IPage<Order> result = orderService.getOrdersBySellerId(page, sellerId, status);
        return convertToPageResponse(result);
    }

    @Override
    public PageResponse<OrderResponse> searchOrders(String keyword, Integer currentPage, Integer pageSize) {
        Page<Order> page = new Page<>(currentPage, pageSize);
        IPage<Order> result = orderService.searchOrders(page, keyword);
        return convertToPageResponse(result);
    }

    @Override
    public PageResponse<OrderResponse> getOrdersByTimeRange(LocalDateTime startTime, LocalDateTime endTime, String status, Integer currentPage, Integer pageSize) {
        Page<Order> page = new Page<>(currentPage, pageSize);
        IPage<Order> result = orderService.getOrdersByTimeRange(page, startTime, endTime, status);
        return convertToPageResponse(result);
    }

    @Override
    public Result<Map<String, Object>> processPayment(Long orderId, String payMethod) {
        try {
            log.info("处理订单支付: orderId={}, payMethod={}", orderId, payMethod);
            
            // 1. 处理支付
            Map<String, Object> result = orderService.processPayment(orderId, payMethod);
            
            // 2. 检查是否支付成功，如果是金币充值订单则自动充值
            if (result != null && "success".equals(result.get("status"))) {
                handleCoinRechargeIfNeeded(orderId);
            }
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("支付处理失败: orderId={}", orderId, e);
            return Result.failure("支付处理失败");
        }
    }
    
    /**
     * 处理金币充值订单的自动充值逻辑
     */
    private void handleCoinRechargeIfNeeded(Long orderId) {
        try {
            // 获取订单信息
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                log.warn("订单不存在，无法处理金币充值: orderId={}", orderId);
                return;
            }
            
            // 检查是否是金币充值订单
            if ("coin".equals(order.getGoodsType()) && order.getCoinAmount() != null && order.getCoinAmount() > 0) {
                log.info("检测到金币充值订单，开始自动充值: orderId={}, userId={}, coinAmount={}", 
                        orderId, order.getUserId(), order.getCoinAmount());
                
                // 调用钱包服务给用户充值金币
                Result<Void> rechargeResult = walletFacadeService.grantCoinReward(
                    order.getUserId(), 
                    order.getCoinAmount(), 
                    "recharge", 
                    "金币充值包购买自动到账，订单号：" + order.getOrderNo()
                );
                
                if (rechargeResult != null && rechargeResult.getSuccess()) {
                    log.info("金币充值成功: orderId={}, userId={}, coinAmount={}", 
                            orderId, order.getUserId(), order.getCoinAmount());
                } else {
                    log.error("金币充值失败: orderId={}, userId={}, coinAmount={}, error={}", 
                            orderId, order.getUserId(), order.getCoinAmount(), 
                            rechargeResult != null ? rechargeResult.getMessage() : "unknown");
                }
            }
        } catch (Exception e) {
            log.error("处理金币充值失败: orderId={}", orderId, e);
            // 不抛出异常，避免影响订单支付流程
        }
    }

    @Override
    public Result<Void> confirmPayment(Long orderId, String payMethod) {
        try {
            boolean success = orderService.confirmPayment(orderId, payMethod);
            return success ? Result.success() : Result.failure("确认失败");
        } catch (Exception e) {
            return Result.failure("确认失败");
        }
    }

    @Override
    public Result<Void> handlePaymentCallback(String orderNo, String payStatus, String payMethod, Map<String, Object> extraInfo) {
        try {
            log.info("处理支付回调: orderNo={}, payStatus={}, payMethod={}", orderNo, payStatus, payMethod);
            
            boolean success = orderService.handlePaymentCallback(orderNo, payStatus, payMethod, LocalDateTime.now(), extraInfo);
            
            // 如果支付成功，检查是否需要处理金币充值
            if (success && "success".equals(payStatus)) {
                // 通过订单号获取订单ID
                Order order = orderService.getOrderByOrderNo(orderNo);
                if (order != null) {
                    handleCoinRechargeIfNeeded(order.getId());
                }
            }
            
            return success ? Result.success() : Result.failure("回调处理失败");
        } catch (Exception e) {
            log.error("支付回调处理失败: orderNo={}", orderNo, e);
            return Result.failure("回调处理失败");
        }
    }

    @Override
    public Result<Map<String, Object>> requestRefund(Long orderId, String reason) {
        try {
            Map<String, Object> result = orderService.requestRefund(orderId, reason);
            return Result.success(result);
        } catch (Exception e) {
            return Result.failure("退款申请失败");
        }
    }

    // 状态管理方法
    @Override
    public Result<Void> shipOrder(Long orderId, Map<String, Object> shippingInfo) {
        try {
            boolean success = orderService.shipOrder(orderId, shippingInfo);
            return success ? Result.success() : Result.failure("发货失败");
        } catch (Exception e) {
            return Result.failure("发货失败");
        }
    }

    @Override
    public Result<Void> confirmReceipt(Long orderId) {
        try {
            boolean success = orderService.confirmReceipt(orderId);
            return success ? Result.success() : Result.failure("确认收货失败");
        } catch (Exception e) {
            return Result.failure("确认收货失败");
        }
    }

    @Override
    public Result<Void> completeOrder(Long orderId) {
        try {
            boolean success = orderService.completeOrder(orderId);
            return success ? Result.success() : Result.failure("完成订单失败");
        } catch (Exception e) {
            return Result.failure("完成订单失败");
        }
    }

    // 统计方法
    @Override
    public Result<Map<String, Object>> getUserOrderStatistics(Long userId) {
        try {
            Map<String, Object> stats = orderService.getUserOrderStatistics(userId);
            return Result.success(stats);
        } catch (Exception e) {
            return Result.failure("获取统计失败");
        }
    }

    @Override
    public Result<Map<String, Object>> getGoodsSalesStatistics(Long goodsId) {
        try {
            Map<String, Object> stats = orderService.getGoodsSalesStatistics(goodsId);
            return Result.success(stats);
        } catch (Exception e) {
            return Result.failure("获取统计失败");
        }
    }

    @Override
    public Result<List<Map<String, Object>>> getOrderStatisticsByType() {
        try {
            List<Map<String, Object>> stats = orderService.getOrderStatisticsByType();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.failure("获取统计失败");
        }
    }

    @Override
    public Result<List<Map<String, Object>>> getHotGoods(Integer limit) {
        try {
            List<Map<String, Object>> hotGoods = orderService.getHotGoods(limit);
            return Result.success(hotGoods);
        } catch (Exception e) {
            return Result.failure("获取热门商品失败");
        }
    }

    @Override
    public Result<List<Map<String, Object>>> getDailyRevenue(String startDate, String endDate) {
        try {
            List<Map<String, Object>> revenue = orderService.getDailyRevenue(startDate, endDate);
            return Result.success(revenue);
        } catch (Exception e) {
            return Result.failure("获取营收统计失败");
        }
    }

    @Override
    public Result<List<OrderResponse>> getUserRecentOrders(Long userId, Integer limit) {
        try {
            List<Order> orders = orderService.getUserRecentOrders(userId, limit);
            List<OrderResponse> responses = orders.stream().map(this::convertToResponse).collect(Collectors.toList());
            return Result.success(responses);
        } catch (Exception e) {
            return Result.failure("获取最近订单失败");
        }
    }

    // 专用查询
    @Override
    public PageResponse<OrderResponse> getUserCoinOrders(Long userId, Integer currentPage, Integer pageSize) {
        Page<Order> page = new Page<>(currentPage, pageSize);
        IPage<Order> result = orderService.getUserCoinOrders(page, userId);
        return convertToPageResponse(result);
    }

    @Override
    public PageResponse<OrderResponse> getUserRechargeOrders(Long userId, Integer currentPage, Integer pageSize) {
        Page<Order> page = new Page<>(currentPage, pageSize);
        IPage<Order> result = orderService.getUserRechargeOrders(page, userId);
        return convertToPageResponse(result);
    }

    @Override
    public PageResponse<OrderResponse> getContentOrders(Long contentId, Integer currentPage, Integer pageSize) {
        Page<Order> page = new Page<>(currentPage, pageSize);
        IPage<Order> result = orderService.getContentOrders(page, contentId);
        return convertToPageResponse(result);
    }

    @Override
    public PageResponse<OrderResponse> getSubscriptionOrders(String subscriptionType, Integer currentPage, Integer pageSize) {
        Page<Order> page = new Page<>(currentPage, pageSize);
        IPage<Order> result = orderService.getSubscriptionOrders(page, subscriptionType);
        return convertToPageResponse(result);
    }

    // 业务验证
    @Override
    public Result<Map<String, Object>> validatePayment(Long orderId) {
        try {
            Map<String, Object> validation = orderService.validatePayment(orderId);
            return Result.success(validation);
        } catch (Exception e) {
            return Result.failure("验证失败");
        }
    }

    @Override
    public Result<Map<String, Object>> validateCancel(Long orderId) {
        try {
            Map<String, Object> validation = orderService.validateCancel(orderId);
            return Result.success(validation);
        } catch (Exception e) {
            return Result.failure("验证失败");
        }
    }

    @Override
    public Result<Map<String, Object>> validateRefund(Long orderId) {
        try {
            Map<String, Object> validation = orderService.validateRefund(orderId);
            return Result.success(validation);
        } catch (Exception e) {
            return Result.failure("验证失败");
        }
    }

    // 快捷查询
    @Override
    public PageResponse<OrderResponse> getPendingOrders(Long userId, Integer currentPage, Integer pageSize) {
        return userId != null ? getUserOrders(userId, "pending", currentPage, pageSize) : 
                               getOrdersByGoodsType(null, "pending", currentPage, pageSize);
    }

    @Override
    public PageResponse<OrderResponse> getCompletedOrders(Long userId, Integer currentPage, Integer pageSize) {
        return userId != null ? getUserOrders(userId, "completed", currentPage, pageSize) : 
                               getOrdersByGoodsType(null, "completed", currentPage, pageSize);
    }

    @Override
    public PageResponse<OrderResponse> getTodayOrders(Integer currentPage, Integer pageSize) {
        LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime end = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        return getOrdersByTimeRange(start, end, null, currentPage, pageSize);
    }

    @Override
    public Result<List<OrderResponse>> getTimeoutOrders(Integer timeoutMinutes) {
        try {
            List<Order> orders = orderService.getTimeoutOrders(timeoutMinutes);
            List<OrderResponse> responses = orders.stream().map(this::convertToResponse).collect(Collectors.toList());
            return Result.success(responses);
        } catch (Exception e) {
            return Result.failure("获取超时订单失败");
        }
    }

    @Override
    public Result<Integer> autoCancelTimeoutOrders(Integer timeoutMinutes) {
        try {
            int count = orderService.autoCancelTimeoutOrders(timeoutMinutes);
            return Result.success(count);
        } catch (Exception e) {
            return Result.failure("自动取消失败");
        }
    }

    @Override
    public Result<Integer> autoCompleteShippedOrders(Integer days) {
        try {
            int count = orderService.autoCompleteShippedOrders(days);
            return Result.success(count);
        } catch (Exception e) {
            return Result.failure("自动完成失败");
        }
    }

    // 转换方法
    private Order convertToEntity(OrderCreateRequest request) {
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setUserNickname(request.getUserNickname());
        order.setGoodsId(request.getGoodsId());
        order.setQuantity(request.getQuantity());
        order.setDiscountAmount(request.getEffectiveDiscountAmount());
        order.setOrderNo(orderService.generateOrderNo(request.getUserId()));
        return order;
    }

    private OrderResponse convertToResponse(Order order) {
        if (order == null) return null;
        
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNo(order.getOrderNo());
        response.setUserId(order.getUserId());
        response.setUserNickname(order.getUserNickname());
        response.setGoodsId(order.getGoodsId());
        response.setGoodsName(order.getGoodsName());
        response.setGoodsType(order.getGoodsType() != null ? order.getGoodsType().getCode() : null);
        response.setQuantity(order.getQuantity());
        response.setPaymentMode(order.getPaymentMode() != null ? order.getPaymentMode().getCode() : null);
        response.setCashAmount(order.getCashAmount());
        response.setCoinCost(order.getCoinCost());
        response.setTotalAmount(order.getTotalAmount());
        response.setFinalAmount(order.getFinalAmount());
        response.setStatus(order.getStatus() != null ? order.getStatus().getCode() : null);
        response.setPayStatus(order.getPayStatus() != null ? order.getPayStatus().getCode() : null);
        response.setPayMethod(order.getPayMethod());
        response.setPayTime(order.getPayTime());
        response.setCreateTime(order.getCreateTime());
        response.setUpdateTime(order.getUpdateTime());
        return response;
    }

    private PageResponse<OrderResponse> convertToPageResponse(IPage<Order> page) {
        List<OrderResponse> responses = page.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return PageResponse.of(responses, (int) page.getTotal(), (int) page.getSize(), (int) page.getCurrent());
    }
}