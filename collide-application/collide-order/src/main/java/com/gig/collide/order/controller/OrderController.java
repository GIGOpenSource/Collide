package com.gig.collide.order.controller;

import com.gig.collide.api.order.request.OrderCreateRequest;
import com.gig.collide.api.order.request.OrderQueryRequest;
import com.gig.collide.api.order.response.OrderResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import com.gig.collide.order.facade.OrderFacadeServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单管理REST控制器 - 缓存增强版
 *
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Validated
@Tag(name = "订单管理", description = "订单的增删改查、支付管理、统计分析等功能")
public class OrderController {

    private final OrderFacadeServiceImpl orderFacadeService;

    @PostMapping
    @Operation(summary = "创建订单", description = "创建新订单，支持四种商品类型和双支付模式")
    public Result<OrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        return orderFacadeService.createOrder(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情", description = "根据ID获取订单详细信息，需要用户权限验证")
    public Result<OrderResponse> getOrderById(@PathVariable @NotNull @Min(1) Long id,
                                             @RequestParam @NotNull @Min(1) @Parameter(description = "用户ID，用于权限验证") Long userId) {
        log.info("REST获取订单详情: orderId={}, userId={}", id, userId);
        return orderFacadeService.getOrderById(id, userId);
    }

    @GetMapping("/no/{orderNo}")
    @Operation(summary = "根据订单号获取详情", description = "根据订单号获取订单详细信息，需要用户权限验证")
    public Result<OrderResponse> getOrderByOrderNo(@PathVariable @NotBlank String orderNo,
                                                  @RequestParam @NotNull @Min(1) @Parameter(description = "用户ID，用于权限验证") Long userId) {
        log.info("REST根据订单号获取详情: orderNo={}, userId={}", orderNo, userId);
        return orderFacadeService.getOrderByOrderNo(orderNo, userId);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消订单", description = "取消指定订单，需要用户权限验证")
    public Result<Void> cancelOrder(@PathVariable @NotNull @Min(1) Long id,
                                   @RequestParam(defaultValue = "用户主动取消") String reason,
                                   @RequestParam @NotNull @Min(1) @Parameter(description = "用户ID，用于权限验证") Long userId) {
        log.info("REST取消订单: orderId={}, reason={}, userId={}", id, reason, userId);
        return orderFacadeService.cancelOrder(id, reason, userId);
    }

    @PostMapping("/batch-cancel")
    @Operation(summary = "批量取消订单", description = "批量取消多个订单，需要操作者权限")
    public Result<Void> batchCancelOrders(@RequestBody @NotNull List<Long> orderIds,
                                         @RequestParam(defaultValue = "批量取消") String reason,
                                         @RequestParam @NotNull @Min(1) @Parameter(description = "操作者ID") Long operatorId) {
        log.info("REST批量取消订单: count={}, reason={}, operatorId={}", orderIds.size(), reason, operatorId);
        return orderFacadeService.batchCancelOrders(orderIds, reason, operatorId);
    }

    @PostMapping("/query")
    @Operation(summary = "分页查询订单", description = "支持多条件分页查询订单列表")
    public Result<PageResponse<OrderResponse>> queryOrders(@Valid @RequestBody OrderQueryRequest request) {
        log.info("REST分页查询订单: userId={}, status={}", request.getUserId(), request.getStatus());
        return orderFacadeService.queryOrders(request);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "根据用户查询订单", description = "获取指定用户的订单列表，需要权限验证")
    public Result<PageResponse<OrderResponse>> getUserOrders(@PathVariable @NotNull @Min(1) Long userId,
                                                            @RequestParam(required = false) String status,
                                                            @RequestParam(defaultValue = "1") @Min(1) Integer currentPage,
                                                            @RequestParam(defaultValue = "20") @Min(1) Integer pageSize) {
        log.info("REST查询用户订单: userId={}, status={}, page={}", userId, status, currentPage);
        return orderFacadeService.getUserOrders(userId, status, currentPage, pageSize);
    }

    @PostMapping("/{id}/payment/process")
    @Operation(summary = "处理订单支付", description = "处理订单支付请求，需要用户权限验证")
    public Result<Map<String, Object>> processPayment(@PathVariable @NotNull @Min(1) Long id,
                                                     @RequestParam @NotBlank String payMethod,
                                                     @RequestParam @NotNull @Min(1) @Parameter(description = "用户ID，用于权限验证") Long userId) {
        log.info("REST处理订单支付: orderId={}, payMethod={}, userId={}", id, payMethod, userId);
        return orderFacadeService.processPayment(id, payMethod, userId);
    }

    @GetMapping("/statistics/user/{userId}")
    @Operation(summary = "用户订单统计", description = "获取用户订单统计信息")
    public Result<Map<String, Object>> getUserOrderStatistics(@PathVariable @NotNull @Min(1) Long userId) {
        return orderFacadeService.getUserOrderStatistics(userId);
    }

    @PostMapping("/{id}/mock-payment")
    @Operation(summary = "模拟支付成功 🧪", description = "测试专用 - 模拟订单支付成功，自动处理金币充值")
    public Result<Map<String, Object>> mockPaymentSuccess(@PathVariable @NotNull @Min(1) Long id,
                                                         @RequestParam @NotNull @Min(1) @Parameter(description = "用户ID") Long userId) {
        try {
            log.info("模拟支付成功: orderId={}, userId={}", id, userId);
            
            // 直接调用支付处理逻辑，会自动处理金币充值
            return orderFacadeService.processPayment(id, "mock", userId);
        } catch (Exception e) {
            log.error("模拟支付失败: orderId={}, userId={}", id, userId, e);
            return Result.failure("模拟支付失败: " + e.getMessage());
        }
    }

    // =================== 商品类型查询 ===================

    @GetMapping("/goods-type/{goodsType}")
    @Operation(summary = "根据商品类型查询订单", description = "查询特定类型商品的订单")
    public Result<PageResponse<OrderResponse>> getOrdersByGoodsType(@PathVariable @NotBlank String goodsType,
                                                                   @RequestParam(required = false) String status,
                                                                   @RequestParam(defaultValue = "1") @Min(1) Integer currentPage,
                                                                   @RequestParam(defaultValue = "20") @Min(1) Integer pageSize) {
        log.info("REST根据商品类型查询订单: goodsType={}, status={}", goodsType, status);
        return orderFacadeService.getOrdersByGoodsType(goodsType, status, currentPage, pageSize);
    }

    @GetMapping("/seller/{sellerId}")
    @Operation(summary = "查询商家订单", description = "查询指定商家的订单列表")
    public Result<PageResponse<OrderResponse>> getSellerOrders(@PathVariable @NotNull @Min(1) Long sellerId,
                                                              @RequestParam(required = false) String status,
                                                              @RequestParam(defaultValue = "1") @Min(1) Integer currentPage,
                                                              @RequestParam(defaultValue = "20") @Min(1) Integer pageSize) {
        log.info("REST查询商家订单: sellerId={}, status={}", sellerId, status);
        return orderFacadeService.getSellerOrders(sellerId, status, currentPage, pageSize);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索订单", description = "根据关键词搜索订单")
    public Result<PageResponse<OrderResponse>> searchOrders(@RequestParam @NotBlank String keyword,
                                                           @RequestParam(defaultValue = "1") @Min(1) Integer currentPage,
                                                           @RequestParam(defaultValue = "20") @Min(1) Integer pageSize) {
        log.info("REST搜索订单: keyword={}", keyword);
        return orderFacadeService.searchOrders(keyword, currentPage, pageSize);
    }

    @GetMapping("/time-range")
    @Operation(summary = "根据时间范围查询订单", description = "查询指定时间范围内的订单")
    public Result<PageResponse<OrderResponse>> getOrdersByTimeRange(@RequestParam @Parameter(description = "开始时间") LocalDateTime startTime,
                                                                   @RequestParam @Parameter(description = "结束时间") LocalDateTime endTime,
                                                                   @RequestParam(required = false) String status,
                                                                   @RequestParam(defaultValue = "1") @Min(1) Integer currentPage,
                                                                   @RequestParam(defaultValue = "20") @Min(1) Integer pageSize) {
        log.info("REST根据时间范围查询订单: start={}, end={}, status={}", startTime, endTime, status);
        return orderFacadeService.getOrdersByTimeRange(startTime, endTime, status, currentPage, pageSize);
    }

    // =================== 支付相关 ===================

    @PostMapping("/{id}/payment/confirm")
    @Operation(summary = "确认支付成功", description = "确认订单支付成功，需要用户权限验证")
    public Result<Void> confirmPayment(@PathVariable @NotNull @Min(1) Long id,
                                      @RequestParam @NotBlank String payMethod,
                                      @RequestParam @NotNull @Min(1) @Parameter(description = "用户ID") Long userId) {
        log.info("REST确认支付成功: orderId={}, payMethod={}, userId={}", id, payMethod, userId);
        return orderFacadeService.confirmPayment(id, payMethod, userId);
    }

    @PostMapping("/payment/callback")
    @Operation(summary = "支付回调处理", description = "处理第三方支付平台的回调通知")
    public Result<Void> handlePaymentCallback(@RequestParam @NotBlank String orderNo,
                                             @RequestParam @NotBlank String payStatus,
                                             @RequestParam @NotBlank String payMethod,
                                             @RequestBody(required = false) Map<String, Object> extraInfo) {
        log.info("REST处理支付回调: orderNo={}, payStatus={}, payMethod={}", orderNo, payStatus, payMethod);
        return orderFacadeService.handlePaymentCallback(orderNo, payStatus, payMethod, extraInfo);
    }

    @PostMapping("/{id}/refund")
    @Operation(summary = "申请退款", description = "申请订单退款，需要用户权限验证")
    public Result<Map<String, Object>> requestRefund(@PathVariable @NotNull @Min(1) Long id,
                                                    @RequestParam @NotBlank String reason,
                                                    @RequestParam @NotNull @Min(1) @Parameter(description = "用户ID") Long userId) {
        log.info("REST申请退款: orderId={}, reason={}, userId={}", id, reason, userId);
        return orderFacadeService.requestRefund(id, reason, userId);
    }

    // =================== 订单管理 ===================

    @PostMapping("/{id}/ship")
    @Operation(summary = "订单发货", description = "商家发货操作，需要操作者权限")
    public Result<Void> shipOrder(@PathVariable @NotNull @Min(1) Long id,
                                  @RequestBody Map<String, Object> shippingInfo,
                                  @RequestParam @NotNull @Min(1) @Parameter(description = "操作者ID（商家ID）") Long operatorId) {
        log.info("REST订单发货: orderId={}, operatorId={}", id, operatorId);
        return orderFacadeService.shipOrder(id, shippingInfo, operatorId);
    }

    @PostMapping("/{id}/confirm-receipt")
    @Operation(summary = "确认收货", description = "用户确认收到商品")
    public Result<Void> confirmReceipt(@PathVariable @NotNull @Min(1) Long id,
                                      @RequestParam @NotNull @Min(1) @Parameter(description = "用户ID") Long userId) {
        log.info("REST确认收货: orderId={}, userId={}", id, userId);
        return orderFacadeService.confirmReceipt(id, userId);
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "完成订单", description = "系统或管理员完成订单的最终处理")
    public Result<Void> completeOrder(@PathVariable @NotNull @Min(1) Long id,
                                     @RequestParam @NotNull @Min(1) @Parameter(description = "操作者ID") Long operatorId) {
        log.info("REST完成订单: orderId={}, operatorId={}", id, operatorId);
        return orderFacadeService.completeOrder(id, operatorId);
    }

    // =================== 统计分析 ===================

    @GetMapping("/statistics/goods/{goodsId}")
    @Operation(summary = "商品销售统计", description = "获取商品的销量和营收统计")
    public Result<Map<String, Object>> getGoodsSalesStatistics(@PathVariable @NotNull @Min(1) Long goodsId) {
        log.info("REST获取商品销售统计: goodsId={}", goodsId);
        return orderFacadeService.getGoodsSalesStatistics(goodsId);
    }

    @GetMapping("/statistics/by-type")
    @Operation(summary = "按商品类型统计订单", description = "统计各类型商品的订单分布和营收情况")
    public Result<List<Map<String, Object>>> getOrderStatisticsByType() {
        log.info("REST按商品类型统计订单");
        return orderFacadeService.getOrderStatisticsByType();
    }

    @GetMapping("/statistics/hot-goods")
    @Operation(summary = "获取热门商品", description = "根据销量统计最受欢迎的商品")
    public Result<List<Map<String, Object>>> getHotGoods(@RequestParam(defaultValue = "10") @Min(1) Integer limit) {
        log.info("REST获取热门商品: limit={}", limit);
        return orderFacadeService.getHotGoods(limit);
    }

    @GetMapping("/statistics/daily-revenue")
    @Operation(summary = "获取日营收统计", description = "统计指定时间范围内的每日营收数据")
    public Result<List<Map<String, Object>>> getDailyRevenue(@RequestParam @NotBlank String startDate,
                                                            @RequestParam @NotBlank String endDate) {
        log.info("REST获取日营收统计: start={}, end={}", startDate, endDate);
        return orderFacadeService.getDailyRevenue(startDate, endDate);
    }

    @GetMapping("/user/{userId}/recent")
    @Operation(summary = "获取用户最近购买记录", description = "查看用户最近的购买历史")
    public Result<List<OrderResponse>> getUserRecentOrders(@PathVariable @NotNull @Min(1) Long userId,
                                                          @RequestParam(defaultValue = "10") @Min(1) Integer limit) {
        log.info("REST获取用户最近订单: userId={}, limit={}", userId, limit);
        return orderFacadeService.getUserRecentOrders(userId, limit);
    }

    // =================== 专用查询 ===================

    @GetMapping("/user/{userId}/coin-orders")
    @Operation(summary = "查询用户金币消费订单", description = "查看用户使用金币购买内容的订单记录")
    public Result<PageResponse<OrderResponse>> getUserCoinOrders(@PathVariable @NotNull @Min(1) Long userId,
                                                                @RequestParam(defaultValue = "1") @Min(1) Integer currentPage,
                                                                @RequestParam(defaultValue = "20") @Min(1) Integer pageSize) {
        log.info("REST查询用户金币订单: userId={}", userId);
        return orderFacadeService.getUserCoinOrders(userId, currentPage, pageSize);
    }

    @GetMapping("/user/{userId}/recharge-orders")
    @Operation(summary = "查询用户充值订单", description = "查看用户购买金币的充值订单记录")
    public Result<PageResponse<OrderResponse>> getUserRechargeOrders(@PathVariable @NotNull @Min(1) Long userId,
                                                                    @RequestParam(defaultValue = "1") @Min(1) Integer currentPage,
                                                                    @RequestParam(defaultValue = "20") @Min(1) Integer pageSize) {
        log.info("REST查询用户充值订单: userId={}", userId);
        return orderFacadeService.getUserRechargeOrders(userId, currentPage, pageSize);
    }

    @GetMapping("/content-orders")
    @Operation(summary = "查询内容购买订单", description = "查看特定内容的购买订单")
    public Result<PageResponse<OrderResponse>> getContentOrders(@RequestParam(required = false) Long contentId,
                                                               @RequestParam(defaultValue = "1") @Min(1) Integer currentPage,
                                                               @RequestParam(defaultValue = "20") @Min(1) Integer pageSize) {
        log.info("REST查询内容订单: contentId={}", contentId);
        return orderFacadeService.getContentOrders(contentId, currentPage, pageSize);
    }

    @GetMapping("/subscription-orders")
    @Operation(summary = "查询订阅订单", description = "查看用户的会员订阅、VIP等订阅类服务订单")
    public Result<PageResponse<OrderResponse>> getSubscriptionOrders(@RequestParam(required = false) String subscriptionType,
                                                                    @RequestParam(defaultValue = "1") @Min(1) Integer currentPage,
                                                                    @RequestParam(defaultValue = "20") @Min(1) Integer pageSize) {
        log.info("REST查询订阅订单: subscriptionType={}", subscriptionType);
        return orderFacadeService.getSubscriptionOrders(subscriptionType, currentPage, pageSize);
    }

    // =================== 业务验证 ===================

    @GetMapping("/{id}/validate-payment")
    @Operation(summary = "验证订单支付条件", description = "检查订单状态和支付前置条件")
    public Result<Map<String, Object>> validatePayment(@PathVariable @NotNull @Min(1) Long id,
                                                      @RequestParam @NotNull @Min(1) @Parameter(description = "用户ID") Long userId) {
        log.info("REST验证支付条件: orderId={}, userId={}", id, userId);
        return orderFacadeService.validatePayment(id, userId);
    }

    @GetMapping("/{id}/validate-cancel")
    @Operation(summary = "验证订单取消条件", description = "检查订单状态和取消规则")
    public Result<Map<String, Object>> validateCancel(@PathVariable @NotNull @Min(1) Long id,
                                                     @RequestParam @NotNull @Min(1) @Parameter(description = "用户ID") Long userId) {
        log.info("REST验证取消条件: orderId={}, userId={}", id, userId);
        return orderFacadeService.validateCancel(id, userId);
    }

    @GetMapping("/{id}/validate-refund")
    @Operation(summary = "验证订单退款条件", description = "检查退款政策和退款条件")
    public Result<Map<String, Object>> validateRefund(@PathVariable @NotNull @Min(1) Long id,
                                                     @RequestParam @NotNull @Min(1) @Parameter(description = "用户ID") Long userId) {
        log.info("REST验证退款条件: orderId={}, userId={}", id, userId);
        return orderFacadeService.validateRefund(id, userId);
    }

    // =================== 快捷查询 ===================

    @GetMapping("/pending")
    @Operation(summary = "获取待支付订单", description = "查询用户或系统中待支付的订单")
    public Result<PageResponse<OrderResponse>> getPendingOrders(@RequestParam(required = false) Long userId,
                                                               @RequestParam(defaultValue = "1") @Min(1) Integer currentPage,
                                                               @RequestParam(defaultValue = "20") @Min(1) Integer pageSize) {
        log.info("REST获取待支付订单: userId={}", userId);
        return orderFacadeService.getPendingOrders(userId, currentPage, pageSize);
    }

    @GetMapping("/completed")
    @Operation(summary = "获取已完成订单", description = "查询用户或系统中已完成的订单")
    public Result<PageResponse<OrderResponse>> getCompletedOrders(@RequestParam(required = false) Long userId,
                                                                 @RequestParam(defaultValue = "1") @Min(1) Integer currentPage,
                                                                 @RequestParam(defaultValue = "20") @Min(1) Integer pageSize) {
        log.info("REST获取已完成订单: userId={}", userId);
        return orderFacadeService.getCompletedOrders(userId, currentPage, pageSize);
    }

    @GetMapping("/today")
    @Operation(summary = "获取今日订单", description = "查询今天创建的所有订单")
    public Result<PageResponse<OrderResponse>> getTodayOrders(@RequestParam(defaultValue = "1") @Min(1) Integer currentPage,
                                                             @RequestParam(defaultValue = "20") @Min(1) Integer pageSize) {
        log.info("REST获取今日订单");
        return orderFacadeService.getTodayOrders(currentPage, pageSize);
    }

    // =================== 系统管理 ===================

    @GetMapping("/timeout")
    @Operation(summary = "获取超时订单", description = "查询超过指定时间仍未支付的订单")
    public Result<List<OrderResponse>> getTimeoutOrders(@RequestParam(defaultValue = "30") @Min(1) Integer timeoutMinutes) {
        log.info("REST获取超时订单: timeoutMinutes={}", timeoutMinutes);
        return orderFacadeService.getTimeoutOrders(timeoutMinutes);
    }

    @PostMapping("/auto-cancel-timeout")
    @Operation(summary = "自动取消超时订单", description = "系统定时任务，自动取消超时未支付的订单")
    public Result<Integer> autoCancelTimeoutOrders(@RequestParam(defaultValue = "30") @Min(1) Integer timeoutMinutes) {
        log.info("REST自动取消超时订单: timeoutMinutes={}", timeoutMinutes);
        return orderFacadeService.autoCancelTimeoutOrders(timeoutMinutes);
    }

    @PostMapping("/auto-complete-shipped")
    @Operation(summary = "自动完成已发货订单", description = "系统定时任务，自动完成长时间未确认收货的订单")
    public Result<Integer> autoCompleteShippedOrders(@RequestParam(defaultValue = "7") @Min(1) Integer days) {
        log.info("REST自动完成已发货订单: days={}", days);
        return orderFacadeService.autoCompleteShippedOrders(days);
    }

    @GetMapping("/count/goods/{goodsId}")
    @Operation(summary = "统计商品订单数", description = "根据商品ID统计订单数量")
    public Result<Long> countOrdersByGoodsId(@PathVariable @NotNull @Min(1) Long goodsId,
                                            @RequestParam(required = false) String status) {
        log.info("REST统计商品订单数: goodsId={}, status={}", goodsId, status);
        return orderFacadeService.countOrdersByGoodsId(goodsId, status);
    }

    @GetMapping("/count/user/{userId}")
    @Operation(summary = "统计用户订单数", description = "根据用户ID统计订单数量")
    public Result<Long> countOrdersByUserId(@PathVariable @NotNull @Min(1) Long userId,
                                           @RequestParam(required = false) String status) {
        log.info("REST统计用户订单数: userId={}, status={}", userId, status);
        return orderFacadeService.countOrdersByUserId(userId, status);
    }

    @PostMapping("/{id}/update-payment-info")
    @Operation(summary = "更新订单支付信息", description = "内部系统调用，更新订单的支付状态和时间")
    public Result<Void> updatePaymentInfo(@PathVariable @NotNull @Min(1) Long id,
                                         @RequestParam @NotBlank String payStatus,
                                         @RequestParam @NotBlank String payMethod,
                                         @RequestParam LocalDateTime payTime) {
        log.info("REST更新订单支付信息: orderId={}, payStatus={}, payMethod={}", id, payStatus, payMethod);
        return orderFacadeService.updatePaymentInfo(id, payStatus, payMethod, payTime);
    }

    @GetMapping("/health")
    @Operation(summary = "订单系统健康检查", description = "检查订单系统的运行状态")
    public Result<String> healthCheck() {
        log.debug("REST订单系统健康检查");
        return orderFacadeService.healthCheck();
    }
}