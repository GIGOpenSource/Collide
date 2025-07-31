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
import java.util.HashMap;
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
    public Result<Void> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        return orderFacadeService.createOrder(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情", description = "根据ID获取订单详细信息")
    public Result<OrderResponse> getOrderById(@PathVariable @NotNull @Min(1) Long id) {
        return orderFacadeService.getOrderById(id);
    }

    @GetMapping("/no/{orderNo}")
    @Operation(summary = "根据订单号获取详情", description = "根据订单号获取订单详细信息")
    public Result<OrderResponse> getOrderByOrderNo(@PathVariable @NotBlank String orderNo) {
        return orderFacadeService.getOrderByOrderNo(orderNo);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消订单", description = "取消指定订单")
    public Result<Void> cancelOrder(@PathVariable @NotNull @Min(1) Long id,
                                   @RequestParam(defaultValue = "用户主动取消") String reason) {
        return orderFacadeService.cancelOrder(id, reason);
    }

    @PostMapping("/batch-cancel")
    @Operation(summary = "批量取消订单", description = "批量取消多个订单")
    public Result<Void> batchCancelOrders(@RequestBody @NotNull List<Long> orderIds,
                                         @RequestParam(defaultValue = "批量取消") String reason) {
        return orderFacadeService.batchCancelOrders(orderIds, reason);
    }

    @PostMapping("/query")
    @Operation(summary = "分页查询订单", description = "支持多条件分页查询订单列表")
    public PageResponse<OrderResponse> queryOrders(@Valid @RequestBody OrderQueryRequest request) {
        return orderFacadeService.queryOrders(request);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "根据用户查询订单", description = "获取指定用户的订单列表")
    public PageResponse<OrderResponse> getUserOrders(@PathVariable @NotNull @Min(1) Long userId,
                                                    @RequestParam(required = false) String status,
                                                    @RequestParam(defaultValue = "1") @Min(1) Integer currentPage,
                                                    @RequestParam(defaultValue = "20") @Min(1) Integer pageSize) {
        return orderFacadeService.getUserOrders(userId, status, currentPage, pageSize);
    }

    @PostMapping("/{id}/payment/process")
    @Operation(summary = "处理订单支付", description = "处理订单支付请求")
    public Result<Map<String, Object>> processPayment(@PathVariable @NotNull @Min(1) Long id,
                                                     @RequestParam @NotBlank String payMethod) {
        return orderFacadeService.processPayment(id, payMethod);
    }

    @GetMapping("/statistics/user/{userId}")
    @Operation(summary = "用户订单统计", description = "获取用户订单统计信息")
    public Result<Map<String, Object>> getUserOrderStatistics(@PathVariable @NotNull @Min(1) Long userId) {
        return orderFacadeService.getUserOrderStatistics(userId);
    }

    @PostMapping("/{id}/mock-payment")
    @Operation(summary = "模拟支付成功 🧪", description = "测试专用 - 模拟订单支付成功，自动处理金币充值")
    public Result<Map<String, Object>> mockPaymentSuccess(@PathVariable @NotNull @Min(1) Long id) {
        try {
            log.info("模拟支付成功: orderId={}", id);
            
            // 模拟支付成功的返回结果
            Map<String, Object> mockResult = new HashMap<>();
            mockResult.put("status", "success");
            mockResult.put("paymentId", "MOCK_PAY_" + System.currentTimeMillis());
            mockResult.put("payMethod", "mock");
            mockResult.put("payTime", LocalDateTime.now());
            
            // 直接调用支付处理逻辑，会自动处理金币充值
            return orderFacadeService.processPayment(id, "mock");
        } catch (Exception e) {
            log.error("模拟支付失败: orderId={}", id, e);
            return Result.failure("模拟支付失败: " + e.getMessage());
        }
    }
}