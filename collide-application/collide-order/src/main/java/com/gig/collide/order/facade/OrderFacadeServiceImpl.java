package com.gig.collide.order.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.order.OrderFacadeService;
import com.gig.collide.api.order.request.OrderCreateRequest;
import com.gig.collide.api.order.request.OrderQueryRequest;
import com.gig.collide.api.order.response.OrderResponse;
import com.gig.collide.api.user.WalletFacadeService;
import com.gig.collide.api.user.UserFacadeService;
import com.gig.collide.api.user.response.UserResponse;
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
import java.util.HashMap;
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
    
    @DubboReference(version = "1.0.0", timeout = 10000, check = false)
    private UserFacadeService userFacadeService;

    @Override
    public Result<OrderResponse> createOrder(OrderCreateRequest request) {
        try {
            log.info("REST创建订单: userId={}, goodsId={}, goodsType={}, paymentMode={}", 
                request.getUserId(), request.getGoodsId(), request.getGoodsType(), request.getPaymentMode());
            request.validateParams();
            
            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(request.getUserId());
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法创建订单: userId={}", request.getUserId());
                return Result.failure("USER_NOT_FOUND", "用户不存在");
            }
            
            Order order = convertToEntity(request);
            
            // 创建订单并获取ID
            Long orderId = orderService.createOrder(order);
            order.setId(orderId);
            
            // 返回创建的订单信息
            OrderResponse response = convertToResponse(order);
            
            log.info("订单创建成功: orderId={}, orderNo={}, 用户={}", 
                    orderId, order.getOrderNo(), userResult.getData().getNickname());
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("订单创建失败: userId={}, goodsId={}", request.getUserId(), request.getGoodsId(), e);
            return Result.failure("订单创建失败: " + e.getMessage());
        }
    }

    @Override
    public Result<OrderResponse> getOrderById(Long orderId, Long userId) {
        try {
            log.debug("查询订单详情: orderId={}, userId={}", orderId, userId);
            
            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法查询订单: userId={}", userId);
                return Result.failure("USER_NOT_FOUND", "用户不存在");
            }
            
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return Result.failure("ORDER_NOT_FOUND", "订单不存在");
            }
            
            // 权限验证：用户只能查看自己的订单
            if (!order.getUserId().equals(userId)) {
                log.warn("用户无权限查看此订单: orderId={}, userId={}, orderUserId={}", 
                        orderId, userId, order.getUserId());
                return Result.failure("ACCESS_DENIED", "无权限查看此订单");
            }
            
            log.debug("订单查询成功: orderId={}, 用户={}({})", 
                    orderId, userId, userResult.getData().getNickname());
            return Result.success(convertToResponse(order));
        } catch (Exception e) {
            log.error("查询订单失败: orderId={}, userId={}", orderId, userId, e);
            return Result.failure("QUERY_FAILED", "查询失败");
        }
    }

    @Override
    public Result<OrderResponse> getOrderByOrderNo(String orderNo, Long userId) {
        try {
            log.debug("根据订单号查询订单: orderNo={}, userId={}", orderNo, userId);
            
            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法查询订单: userId={}", userId);
                return Result.failure("USER_NOT_FOUND", "用户不存在");
            }
            
            Order order = orderService.getOrderByOrderNo(orderNo);
            if (order == null) {
                return Result.failure("ORDER_NOT_FOUND", "订单不存在");
            }
            
            // 权限验证：用户只能查看自己的订单
            if (!order.getUserId().equals(userId)) {
                log.warn("用户无权限查看此订单: orderNo={}, userId={}, orderUserId={}", 
                        orderNo, userId, order.getUserId());
                return Result.failure("ACCESS_DENIED", "无权限查看此订单");
            }
            
            log.debug("订单查询成功: orderNo={}, 用户={}({})", 
                    orderNo, userId, userResult.getData().getNickname());
            return Result.success(convertToResponse(order));
        } catch (Exception e) {
            log.error("查询订单失败: orderNo={}, userId={}", orderNo, userId, e);
            return Result.failure("QUERY_FAILED", "查询失败");
        }
    }

    @Override
    public Result<Void> cancelOrder(Long orderId, String reason, Long userId) {
        try {
            log.info("取消订单: orderId={}, reason={}, userId={}", orderId, reason, userId);
            
            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法取消订单: userId={}", userId);
                return Result.failure("USER_NOT_FOUND", "用户不存在");
            }
            
            // 获取订单验证权限
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return Result.failure("ORDER_NOT_FOUND", "订单不存在");
            }
            
            // 权限验证：用户只能取消自己的订单
            if (!order.getUserId().equals(userId)) {
                log.warn("用户无权限取消此订单: orderId={}, userId={}, orderUserId={}", 
                        orderId, userId, order.getUserId());
                return Result.failure("ACCESS_DENIED", "无权限取消此订单");
            }
            
            boolean success = orderService.cancelOrder(orderId, reason);
            if (success) {
                log.info("订单取消成功: orderId={}, 用户={}({})", 
                        orderId, userId, userResult.getData().getNickname());
                return Result.success();
            } else {
                return Result.failure("CANCEL_FAILED", "取消失败");
            }
        } catch (Exception e) {
            log.error("取消订单失败: orderId={}, userId={}", orderId, userId, e);
            return Result.failure("CANCEL_FAILED", "取消失败");
        }
    }

    @Override
    public Result<Void> batchCancelOrders(List<Long> orderIds, String reason, Long operatorId) {
        try {
            log.info("批量取消订单: count={}, reason={}, operatorId={}", orderIds.size(), reason, operatorId);
            
            // 验证操作者是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(operatorId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("操作者不存在，无法批量取消订单: operatorId={}", operatorId);
                return Result.failure("USER_NOT_FOUND", "操作者不存在");
            }
            
            boolean success = orderService.batchCancelOrders(orderIds, reason);
            if (success) {
                log.info("批量取消订单成功: count={}, 操作者={}({})", 
                        orderIds.size(), operatorId, userResult.getData().getNickname());
                return Result.success();
            } else {
                return Result.failure("BATCH_CANCEL_FAILED", "批量取消失败");
            }
        } catch (Exception e) {
            log.error("批量取消订单失败: count={}, operatorId={}", orderIds.size(), operatorId, e);
            return Result.failure("BATCH_CANCEL_FAILED", "批量取消失败");
        }
    }

    @Override
    public Result<PageResponse<OrderResponse>> queryOrders(OrderQueryRequest request) {
        try {
            log.debug("分页查询订单: userId={}, status={}", request.getUserId(), request.getStatus());
            
            request.validateParams();
            
            // 验证用户是否存在（如果指定了用户ID）
            if (request.getUserId() != null) {
                Result<UserResponse> userResult = userFacadeService.getUserById(request.getUserId());
                if (userResult == null || !userResult.getSuccess()) {
                    log.warn("用户不存在，无法查询订单: userId={}", request.getUserId());
                    return Result.failure("USER_NOT_FOUND", "用户不存在");
                }
            }
            
            Page<Order> page = new Page<>(request.getCurrentPage(), request.getPageSize());
            IPage<Order> result;
            
            if (request.getUserId() != null) {
                result = orderService.getOrdersByUserId(page, request.getUserId(), request.getStatus());
            } else {
                result = orderService.getOrdersByGoodsType(page, request.getGoodsType(), request.getStatus());
            }
            
            PageResponse<OrderResponse> pageResponse = convertToPageResponse(result);
            log.debug("订单查询成功: 总数={}, 页码={}", pageResponse.getTotal(), pageResponse.getCurrentPage());
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("查询订单失败", e);
            return Result.failure("QUERY_FAILED", "查询失败");
        }
    }

    @Override
    public Result<PageResponse<OrderResponse>> getUserOrders(Long userId, String status, Integer currentPage, Integer pageSize) {
        try {
            log.debug("查询用户订单: userId={}, status={}", userId, status);
            
            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法查询用户订单: userId={}", userId);
                return Result.failure("USER_NOT_FOUND", "用户不存在");
            }
            
            Page<Order> page = new Page<>(currentPage, pageSize);
            IPage<Order> result = orderService.getOrdersByUserId(page, userId, status);
            PageResponse<OrderResponse> pageResponse = convertToPageResponse(result);
            
            log.debug("用户订单查询成功: 用户={}({}), 总数={}", 
                    userId, userResult.getData().getNickname(), pageResponse.getTotal());
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("查询用户订单失败: userId={}", userId, e);
            return Result.failure("QUERY_FAILED", "查询失败");
        }
    }

    @Override
    public Result<PageResponse<OrderResponse>> getOrdersByGoodsType(String goodsType, String status, Integer currentPage, Integer pageSize) {
        try {
            log.debug("根据商品类型查询订单: goodsType={}, status={}", goodsType, status);
            
            Page<Order> page = new Page<>(currentPage, pageSize);
            IPage<Order> result = orderService.getOrdersByGoodsType(page, goodsType, status);
            PageResponse<OrderResponse> pageResponse = convertToPageResponse(result);
            
            log.debug("商品类型订单查询成功: goodsType={}, 总数={}", goodsType, pageResponse.getTotal());
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("查询商品类型订单失败: goodsType={}", goodsType, e);
            return Result.failure("QUERY_FAILED", "查询失败");
        }
    }

    @Override
    public Result<PageResponse<OrderResponse>> getSellerOrders(Long sellerId, String status, Integer currentPage, Integer pageSize) {
        try {
            log.debug("查询商家订单: sellerId={}, status={}", sellerId, status);
            
            Page<Order> page = new Page<>(currentPage, pageSize);
            IPage<Order> result = orderService.getOrdersBySellerId(page, sellerId, status);
            PageResponse<OrderResponse> pageResponse = convertToPageResponse(result);
            
            log.debug("商家订单查询成功: sellerId={}, 总数={}", sellerId, pageResponse.getTotal());
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("查询商家订单失败: sellerId={}", sellerId, e);
            return Result.failure("QUERY_FAILED", "查询失败");
        }
    }

    @Override
    public Result<PageResponse<OrderResponse>> searchOrders(String keyword, Integer currentPage, Integer pageSize) {
        try {
            log.debug("搜索订单: keyword={}", keyword);
            
            Page<Order> page = new Page<>(currentPage, pageSize);
            IPage<Order> result = orderService.searchOrders(page, keyword);
            PageResponse<OrderResponse> pageResponse = convertToPageResponse(result);
            
            log.debug("订单搜索成功: keyword={}, 总数={}", keyword, pageResponse.getTotal());
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("搜索订单失败: keyword={}", keyword, e);
            return Result.failure("QUERY_FAILED", "搜索失败");
        }
    }

    @Override
    public Result<PageResponse<OrderResponse>> getOrdersByTimeRange(LocalDateTime startTime, LocalDateTime endTime, String status, Integer currentPage, Integer pageSize) {
        try {
            log.debug("根据时间范围查询订单: start={}, end={}, status={}", startTime, endTime, status);
            
            Page<Order> page = new Page<>(currentPage, pageSize);
            IPage<Order> result = orderService.getOrdersByTimeRange(page, startTime, endTime, status);
            PageResponse<OrderResponse> pageResponse = convertToPageResponse(result);
            
            log.debug("时间范围订单查询成功: 总数={}", pageResponse.getTotal());
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("查询时间范围订单失败", e);
            return Result.failure("QUERY_FAILED", "查询失败");
        }
    }

    @Override
    public Result<Map<String, Object>> processPayment(Long orderId, String payMethod, Long userId) {
        try {
            log.info("处理订单支付: orderId={}, payMethod={}, userId={}", orderId, payMethod, userId);
            
            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法处理支付: userId={}", userId);
                return Result.failure("USER_NOT_FOUND", "用户不存在");
            }
            
            // 获取订单信息，用于判断支付类型
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return Result.failure("ORDER_NOT_FOUND", "订单不存在");
            }
            
            // 权限验证：用户只能支付自己的订单
            if (!order.getUserId().equals(userId)) {
                log.warn("用户无权限支付此订单: orderId={}, userId={}, orderUserId={}", 
                        orderId, userId, order.getUserId());
                return Result.failure("ACCESS_DENIED", "无权限支付此订单");
            }
            
            // 1. 处理金币支付模式
            if ("coin".equals(payMethod) && Order.PaymentMode.COIN.equals(order.getPaymentMode())) {
                return handleCoinPayment(order);
            }
            
            // 2. 处理现金支付模式
            Map<String, Object> result = orderService.processPayment(orderId, payMethod);
            
            // 3. 检查是否支付成功，如果是金币充值订单则自动充值
            if (result != null && "success".equals(result.get("status"))) {
                handleCoinRechargeIfNeeded(orderId);
            }
            
            log.info("支付处理成功: orderId={}, 用户={}({})", 
                    orderId, userId, userResult.getData().getNickname());
            return Result.success(result);
        } catch (Exception e) {
            log.error("支付处理失败: orderId={}, userId={}", orderId, userId, e);
            return Result.failure("PAYMENT_FAILED", "支付处理失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理金币支付逻辑
     * 用于虚拟商品（如内容）的金币支付
     */
    private Result<Map<String, Object>> handleCoinPayment(Order order) {
        try {
            log.info("处理金币支付: orderId={}, userId={}, coinCost={}", 
                order.getId(), order.getUserId(), order.getCoinCost());
            
            // 1. 检查金币余额
            Result<Boolean> balanceCheck = walletFacadeService.checkCoinBalance(
                order.getUserId(), order.getCoinCost());
            if (!balanceCheck.getSuccess() || !balanceCheck.getData()) {
                return Result.failure("金币余额不足");
            }
            
            // 2. 扣减金币
            Result<Void> consumeResult = walletFacadeService.consumeCoin(
                order.getUserId(),
                order.getCoinCost(),
                "content_purchase",
                "内容购买支付，订单号：" + order.getOrderNo()
            );
            
            if (!consumeResult.getSuccess()) {
                return Result.failure("金币扣减失败: " + consumeResult.getMessage());
            }
            
            // 3. 更新订单状态为已支付
            boolean updateSuccess = orderService.updatePaymentStatus(order.getId(), "paid", "coin");
            if (!updateSuccess) {
                // 扣费失败，尝试回滚金币
                try {
                    walletFacadeService.grantCoinReward(
                        order.getUserId(),
                        order.getCoinCost(),
                        "refund",
                        "订单支付失败回滚，订单号：" + order.getOrderNo()
                    );
                } catch (Exception e) {
                    log.error("金币回滚失败: orderId={}, userId={}, coinCost={}", 
                        order.getId(), order.getUserId(), order.getCoinCost(), e);
                }
                return Result.failure("订单状态更新失败");
            }
            
            // 4. 处理特定商品类型的后续逻辑
            handlePostPaymentActions(order);
            
            // 5. 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("status", "success");
            result.put("orderId", order.getId());
            result.put("orderNo", order.getOrderNo());
            result.put("payMethod", "coin");
            result.put("coinCost", order.getCoinCost());
            result.put("message", "金币支付成功");
            result.put("payTime", System.currentTimeMillis());
            
            log.info("金币支付成功: orderId={}, userId={}, coinCost={}", 
                order.getId(), order.getUserId(), order.getCoinCost());
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("金币支付处理失败: orderId={}, userId={}", 
                order.getId(), order.getUserId(), e);
            return Result.failure("金币支付处理失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理支付成功后的特定商品类型逻辑
     */
    private void handlePostPaymentActions(Order order) {
        try {
            if (Order.GoodsType.CONTENT.equals(order.getGoodsType()) && order.getContentId() != null) {
                // 内容购买成功，创建用户内容购买记录
                log.info("内容购买支付成功，准备创建购买记录: orderId={}, userId={}, contentId={}", 
                    order.getId(), order.getUserId(), order.getContentId());
                
                // 这里可以通过消息队列或直接调用内容服务来创建购买记录
                // 暂时通过日志记录，后续可以集成内容服务
                // contentFacadeService.createUserContentPurchase(order.getUserId(), order.getContentId(), order.getId());
            }
        } catch (Exception e) {
            log.error("支付后处理失败: orderId={}", order.getId(), e);
            // 不抛出异常，避免影响支付流程
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
    public Result<Void> confirmPayment(Long orderId, String payMethod, Long userId) {
        try {
            log.info("确认支付成功: orderId={}, payMethod={}, userId={}", orderId, payMethod, userId);
            
            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法确认支付: userId={}", userId);
                return Result.failure("USER_NOT_FOUND", "用户不存在");
            }
            
            // 获取订单验证权限
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return Result.failure("ORDER_NOT_FOUND", "订单不存在");
            }
            
            // 权限验证：用户只能确认自己的订单支付
            if (!order.getUserId().equals(userId)) {
                log.warn("用户无权限确认此订单支付: orderId={}, userId={}, orderUserId={}", 
                        orderId, userId, order.getUserId());
                return Result.failure("ACCESS_DENIED", "无权限确认此订单支付");
            }
            
            boolean success = orderService.confirmPayment(orderId, payMethod);
            if (success) {
                log.info("支付确认成功: orderId={}, 用户={}({})", 
                        orderId, userId, userResult.getData().getNickname());
                return Result.success();
            } else {
                return Result.failure("CONFIRM_FAILED", "确认失败");
            }
        } catch (Exception e) {
            log.error("确认支付失败: orderId={}, userId={}", orderId, userId, e);
            return Result.failure("CONFIRM_FAILED", "确认失败");
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
    public Result<Map<String, Object>> requestRefund(Long orderId, String reason, Long userId) {
        try {
            log.info("申请退款: orderId={}, reason={}, userId={}", orderId, reason, userId);
            
            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法申请退款: userId={}", userId);
                return Result.failure("USER_NOT_FOUND", "用户不存在");
            }
            
            // 获取订单验证权限
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return Result.failure("ORDER_NOT_FOUND", "订单不存在");
            }
            
            // 权限验证：用户只能申请自己的订单退款
            if (!order.getUserId().equals(userId)) {
                log.warn("用户无权限申请此订单退款: orderId={}, userId={}, orderUserId={}", 
                        orderId, userId, order.getUserId());
                return Result.failure("ACCESS_DENIED", "无权限申请此订单退款");
            }
            
            Map<String, Object> result = orderService.requestRefund(orderId, reason);
            log.info("退款申请成功: orderId={}, 用户={}({})", 
                    orderId, userId, userResult.getData().getNickname());
            return Result.success(result);
        } catch (Exception e) {
            log.error("申请退款失败: orderId={}, userId={}", orderId, userId, e);
            return Result.failure("REFUND_FAILED", "退款申请失败");
        }
    }

    @Override
    public Result<Void> shipOrder(Long orderId, Map<String, Object> shippingInfo, Long operatorId) {
        try {
            log.info("订单发货: orderId={}, operatorId={}", orderId, operatorId);
            
            // 验证操作者是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(operatorId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("操作者不存在，无法发货: operatorId={}", operatorId);
                return Result.failure("USER_NOT_FOUND", "操作者不存在");
            }
            
            boolean success = orderService.shipOrder(orderId, shippingInfo);
            if (success) {
                log.info("订单发货成功: orderId={}, 操作者={}({})", 
                        orderId, operatorId, userResult.getData().getNickname());
                return Result.success();
            } else {
                return Result.failure("SHIP_FAILED", "发货失败");
            }
        } catch (Exception e) {
            log.error("订单发货失败: orderId={}, operatorId={}", orderId, operatorId, e);
            return Result.failure("SHIP_FAILED", "发货失败");
        }
    }

    @Override
    public Result<Void> confirmReceipt(Long orderId, Long userId) {
        try {
            log.info("确认收货: orderId={}, userId={}", orderId, userId);
            
            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法确认收货: userId={}", userId);
                return Result.failure("USER_NOT_FOUND", "用户不存在");
            }
            
            // 获取订单验证权限
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return Result.failure("ORDER_NOT_FOUND", "订单不存在");
            }
            
            // 权限验证：用户只能确认自己的订单收货
            if (!order.getUserId().equals(userId)) {
                log.warn("用户无权限确认此订单收货: orderId={}, userId={}, orderUserId={}", 
                        orderId, userId, order.getUserId());
                return Result.failure("ACCESS_DENIED", "无权限确认此订单收货");
            }
            
            boolean success = orderService.confirmReceipt(orderId);
            if (success) {
                log.info("确认收货成功: orderId={}, 用户={}({})", 
                        orderId, userId, userResult.getData().getNickname());
                return Result.success();
            } else {
                return Result.failure("RECEIPT_FAILED", "确认收货失败");
            }
        } catch (Exception e) {
            log.error("确认收货失败: orderId={}, userId={}", orderId, userId, e);
            return Result.failure("RECEIPT_FAILED", "确认收货失败");
        }
    }

    @Override
    public Result<Void> completeOrder(Long orderId, Long operatorId) {
        try {
            log.info("完成订单: orderId={}, operatorId={}", orderId, operatorId);
            
            // 验证操作者是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(operatorId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("操作者不存在，无法完成订单: operatorId={}", operatorId);
                return Result.failure("USER_NOT_FOUND", "操作者不存在");
            }
            
            boolean success = orderService.completeOrder(orderId);
            if (success) {
                log.info("订单完成成功: orderId={}, 操作者={}({})", 
                        orderId, operatorId, userResult.getData().getNickname());
                return Result.success();
            } else {
                return Result.failure("COMPLETE_FAILED", "完成订单失败");
            }
        } catch (Exception e) {
            log.error("完成订单失败: orderId={}, operatorId={}", orderId, operatorId, e);
            return Result.failure("COMPLETE_FAILED", "完成订单失败");
        }
    }

    // 统计方法
    @Override
    public Result<Map<String, Object>> getUserOrderStatistics(Long userId) {
        try {
            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法获取用户订单统计: userId={}", userId);
                return Result.failure("USER_NOT_FOUND", "用户不存在");
            }
            
            Map<String, Object> stats = orderService.getUserOrderStatistics(userId);
            log.debug("用户订单统计查询成功: 用户={}({})", userId, userResult.getData().getNickname());
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取用户订单统计失败: userId={}", userId, e);
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
            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法获取用户最近订单: userId={}", userId);
                return Result.failure("USER_NOT_FOUND", "用户不存在");
            }
            
            List<Order> orders = orderService.getUserRecentOrders(userId, limit);
            List<OrderResponse> responses = orders.stream().map(this::convertToResponse).collect(Collectors.toList());
            log.debug("用户最近订单查询成功: 用户={}({}), 订单数={}", 
                    userId, userResult.getData().getNickname(), responses.size());
            return Result.success(responses);
        } catch (Exception e) {
            log.error("获取用户最近订单失败: userId={}", userId, e);
            return Result.failure("获取最近订单失败");
        }
    }

    @Override
    public Result<PageResponse<OrderResponse>> getUserCoinOrders(Long userId, Integer currentPage, Integer pageSize) {
        try {
            log.debug("查询用户金币消费订单: userId={}", userId);
            
            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法查询用户金币订单: userId={}", userId);
                return Result.failure("USER_NOT_FOUND", "用户不存在");
            }
            
            Page<Order> page = new Page<>(currentPage, pageSize);
            IPage<Order> result = orderService.getUserCoinOrders(page, userId);
            PageResponse<OrderResponse> pageResponse = convertToPageResponse(result);
            
            log.debug("用户金币订单查询成功: 用户={}({}), 总数={}", 
                    userId, userResult.getData().getNickname(), pageResponse.getTotal());
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("查询用户金币订单失败: userId={}", userId, e);
            return Result.failure("QUERY_FAILED", "查询失败");
        }
    }

    @Override
    public Result<PageResponse<OrderResponse>> getUserRechargeOrders(Long userId, Integer currentPage, Integer pageSize) {
        try {
            log.debug("查询用户充值订单: userId={}", userId);
            
            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法查询用户充值订单: userId={}", userId);
                return Result.failure("USER_NOT_FOUND", "用户不存在");
            }
            
            Page<Order> page = new Page<>(currentPage, pageSize);
            IPage<Order> result = orderService.getUserRechargeOrders(page, userId);
            PageResponse<OrderResponse> pageResponse = convertToPageResponse(result);
            
            log.debug("用户充值订单查询成功: 用户={}({}), 总数={}", 
                    userId, userResult.getData().getNickname(), pageResponse.getTotal());
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("查询用户充值订单失败: userId={}", userId, e);
            return Result.failure("QUERY_FAILED", "查询失败");
        }
    }

    @Override
    public Result<PageResponse<OrderResponse>> getContentOrders(Long contentId, Integer currentPage, Integer pageSize) {
        try {
            log.debug("查询内容购买订单: contentId={}", contentId);
            
            Page<Order> page = new Page<>(currentPage, pageSize);
            IPage<Order> result = orderService.getContentOrders(page, contentId);
            PageResponse<OrderResponse> pageResponse = convertToPageResponse(result);
            
            log.debug("内容订单查询成功: contentId={}, 总数={}", contentId, pageResponse.getTotal());
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("查询内容订单失败: contentId={}", contentId, e);
            return Result.failure("QUERY_FAILED", "查询失败");
        }
    }

    @Override
    public Result<PageResponse<OrderResponse>> getSubscriptionOrders(String subscriptionType, Integer currentPage, Integer pageSize) {
        try {
            log.debug("查询订阅订单: subscriptionType={}", subscriptionType);
            
            Page<Order> page = new Page<>(currentPage, pageSize);
            IPage<Order> result = orderService.getSubscriptionOrders(page, subscriptionType);
            PageResponse<OrderResponse> pageResponse = convertToPageResponse(result);
            
            log.debug("订阅订单查询成功: subscriptionType={}, 总数={}", subscriptionType, pageResponse.getTotal());
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("查询订阅订单失败: subscriptionType={}", subscriptionType, e);
            return Result.failure("QUERY_FAILED", "查询失败");
        }
    }

    @Override
    public Result<Map<String, Object>> validatePayment(Long orderId, Long userId) {
        try {
            log.debug("验证订单支付条件: orderId={}, userId={}", orderId, userId);
            
            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法验证支付: userId={}", userId);
                return Result.failure("USER_NOT_FOUND", "用户不存在");
            }
            
            // 获取订单验证权限
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return Result.failure("ORDER_NOT_FOUND", "订单不存在");
            }
            
            // 权限验证：用户只能验证自己的订单
            if (!order.getUserId().equals(userId)) {
                log.warn("用户无权限验证此订单: orderId={}, userId={}, orderUserId={}", 
                        orderId, userId, order.getUserId());
                return Result.failure("ACCESS_DENIED", "无权限验证此订单");
            }
            
            Map<String, Object> validation = orderService.validatePayment(orderId);
            log.debug("支付条件验证完成: orderId={}, valid={}", orderId, validation.get("valid"));
            return Result.success(validation);
        } catch (Exception e) {
            log.error("验证支付条件失败: orderId={}, userId={}", orderId, userId, e);
            return Result.failure("VALIDATE_FAILED", "验证失败");
        }
    }

    @Override
    public Result<Map<String, Object>> validateCancel(Long orderId, Long userId) {
        try {
            log.debug("验证订单取消条件: orderId={}, userId={}", orderId, userId);
            
            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法验证取消: userId={}", userId);
                return Result.failure("USER_NOT_FOUND", "用户不存在");
            }
            
            // 获取订单验证权限
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return Result.failure("ORDER_NOT_FOUND", "订单不存在");
            }
            
            // 权限验证：用户只能验证自己的订单
            if (!order.getUserId().equals(userId)) {
                log.warn("用户无权限验证此订单: orderId={}, userId={}, orderUserId={}", 
                        orderId, userId, order.getUserId());
                return Result.failure("ACCESS_DENIED", "无权限验证此订单");
            }
            
            Map<String, Object> validation = orderService.validateCancel(orderId);
            log.debug("取消条件验证完成: orderId={}, valid={}", orderId, validation.get("valid"));
            return Result.success(validation);
        } catch (Exception e) {
            log.error("验证取消条件失败: orderId={}, userId={}", orderId, userId, e);
            return Result.failure("VALIDATE_FAILED", "验证失败");
        }
    }

    @Override
    public Result<Map<String, Object>> validateRefund(Long orderId, Long userId) {
        try {
            log.debug("验证订单退款条件: orderId={}, userId={}", orderId, userId);
            
            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法验证退款: userId={}", userId);
                return Result.failure("USER_NOT_FOUND", "用户不存在");
            }
            
            // 获取订单验证权限
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return Result.failure("ORDER_NOT_FOUND", "订单不存在");
            }
            
            // 权限验证：用户只能验证自己的订单
            if (!order.getUserId().equals(userId)) {
                log.warn("用户无权限验证此订单: orderId={}, userId={}, orderUserId={}", 
                        orderId, userId, order.getUserId());
                return Result.failure("ACCESS_DENIED", "无权限验证此订单");
            }
            
            Map<String, Object> validation = orderService.validateRefund(orderId);
            log.debug("退款条件验证完成: orderId={}, valid={}", orderId, validation.get("valid"));
            return Result.success(validation);
        } catch (Exception e) {
            log.error("验证退款条件失败: orderId={}, userId={}", orderId, userId, e);
            return Result.failure("VALIDATE_FAILED", "验证失败");
        }
    }

    @Override
    public Result<PageResponse<OrderResponse>> getPendingOrders(Long userId, Integer currentPage, Integer pageSize) {
        log.debug("查询待支付订单: userId={}", userId);
        return userId != null ? getUserOrders(userId, "pending", currentPage, pageSize) : 
                               getOrdersByGoodsType(null, "pending", currentPage, pageSize);
    }

    @Override
    public Result<PageResponse<OrderResponse>> getCompletedOrders(Long userId, Integer currentPage, Integer pageSize) {
        log.debug("查询已完成订单: userId={}", userId);
        return userId != null ? getUserOrders(userId, "completed", currentPage, pageSize) : 
                               getOrdersByGoodsType(null, "completed", currentPage, pageSize);
    }

    @Override
    public Result<PageResponse<OrderResponse>> getTodayOrders(Integer currentPage, Integer pageSize) {
        log.debug("查询今日订单");
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
            log.info("自动完成已发货订单: days={}", days);
            int count = orderService.autoCompleteShippedOrders(days);
            log.info("自动完成已发货订单成功: count={}", count);
            return Result.success(count);
        } catch (Exception e) {
            log.error("自动完成已发货订单失败: days={}", days, e);
            return Result.failure("AUTO_COMPLETE_FAILED", "自动完成失败");
        }
    }

    @Override
    public Result<Long> countOrdersByGoodsId(Long goodsId, String status) {
        try {
            log.debug("统计商品订单数: goodsId={}, status={}", goodsId, status);
            
            if (goodsId == null || goodsId <= 0) {
                return Result.failure("INVALID_PARAM", "商品ID不能为空");
            }
            
            Long count = orderService.countOrdersByGoodsId(goodsId, status);
            log.debug("商品订单统计成功: goodsId={}, status={}, count={}", goodsId, status, count);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计商品订单数失败: goodsId={}, status={}", goodsId, status, e);
            return Result.failure("COUNT_FAILED", "统计失败");
        }
    }

    @Override
    public Result<Long> countOrdersByUserId(Long userId, String status) {
        try {
            log.debug("统计用户订单数: userId={}, status={}", userId, status);
            
            if (userId == null || userId <= 0) {
                return Result.failure("INVALID_PARAM", "用户ID不能为空");
            }
            
            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法统计订单: userId={}", userId);
                return Result.failure("USER_NOT_FOUND", "用户不存在");
            }
            
            Long count = orderService.countOrdersByUserId(userId, status);
            log.debug("用户订单统计成功: 用户={}({}), status={}, count={}", 
                    userId, userResult.getData().getNickname(), status, count);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计用户订单数失败: userId={}, status={}", userId, status, e);
            return Result.failure("COUNT_FAILED", "统计失败");
        }
    }

    @Override
    public Result<Void> updatePaymentInfo(Long orderId, String payStatus, String payMethod, LocalDateTime payTime) {
        try {
            log.info("更新订单支付信息: orderId={}, payStatus={}, payMethod={}, payTime={}", 
                    orderId, payStatus, payMethod, payTime);
            
            if (orderId == null || orderId <= 0) {
                return Result.failure("INVALID_PARAM", "订单ID不能为空");
            }
            
            boolean success = orderService.updatePaymentInfo(orderId, payStatus, payMethod, payTime);
            if (success) {
                log.info("订单支付信息更新成功: orderId={}", orderId);
                
                // 如果支付成功，检查是否需要处理金币充值
                if ("paid".equals(payStatus)) {
                    handleCoinRechargeIfNeeded(orderId);
                }
                
                return Result.success();
            } else {
                return Result.failure("UPDATE_FAILED", "更新失败");
            }
        } catch (Exception e) {
            log.error("更新订单支付信息失败: orderId={}, payStatus={}", orderId, payStatus, e);
            return Result.failure("UPDATE_FAILED", "更新失败");
        }
    }

    @Override
    public Result<String> healthCheck() {
        try {
            log.debug("订单系统健康检查");
            
            // 检查基本服务状态
            StringBuilder status = new StringBuilder();
            status.append("订单系统运行正常");
            
            // 检查数据库连接（通过简单查询）
            try {
                orderService.getOrderStatisticsByType();
                status.append(", 数据库连接正常");
            } catch (Exception e) {
                status.append(", 数据库连接异常: ").append(e.getMessage());
            }
            
            // 检查缓存服务
            try {
                // 简单的缓存测试
                orderService.getUserOrderStatistics(1L);
                status.append(", 缓存服务正常");
            } catch (Exception e) {
                status.append(", 缓存服务异常: ").append(e.getMessage());
            }
            
            // 检查外部服务依赖
            try {
                userFacadeService.getUserById(1L);
                status.append(", 用户服务连接正常");
            } catch (Exception e) {
                status.append(", 用户服务连接异常: ").append(e.getMessage());
            }
            
            String healthStatus = status.toString();
            log.info("订单系统健康检查完成: {}", healthStatus);
            return Result.success(healthStatus);
        } catch (Exception e) {
            log.error("订单系统健康检查失败", e);
            return Result.failure("HEALTH_CHECK_FAILED", "健康检查失败: " + e.getMessage());
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
        
        // 订单号统一由系统生成，不接受前端传入
        // 在OrderServiceImpl.setDefaultValues()中会自动生成
        
        // 设置扩展字段
        if (request.getGoodsType() != null) {
            order.setGoodsType(Order.GoodsType.valueOf(request.getGoodsType().toUpperCase()));
        }
        
        if (request.getPaymentMode() != null) {
            order.setPaymentMode(Order.PaymentMode.valueOf(request.getPaymentMode().toUpperCase()));
        }
        
        if (request.getCashAmount() != null) {
            order.setCashAmount(request.getCashAmount());
        }
        
        if (request.getCoinCost() != null) {
            order.setCoinCost(request.getCoinCost());
        }
        
        if (request.getContentId() != null) {
            order.setContentId(request.getContentId());
        }
        
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