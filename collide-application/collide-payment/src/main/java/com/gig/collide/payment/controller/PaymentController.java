package com.gig.collide.payment.controller;

import com.gig.collide.api.payment.PaymentFacadeService;
import com.gig.collide.api.payment.request.PaymentCreateRequest;
import com.gig.collide.api.payment.request.PaymentQueryRequest;
import com.gig.collide.api.payment.request.PaymentCallbackRequest;
import com.gig.collide.api.payment.response.PaymentResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.payment.domain.service.PaymentService;
import com.gig.collide.web.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 支付控制器 - 缓存增强版
 * 对齐search模块设计风格，集成缓存功能和测试接口
 *
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-16
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private PaymentFacadeService paymentFacadeService;

    // =================== 支付核心功能 ===================

    /**
     * 创建支付订单
     */
    @PostMapping
    public Result<PaymentResponse> createPayment(@Valid @RequestBody PaymentCreateRequest request) {
        log.info("创建支付订单请求: 用户={}, 订单={}, 金额={}", 
                request.getUserId(), request.getOrderId(), request.getAmount());
        return paymentFacadeService.createPayment(request);
    }

    /**
     * 查询支付详情
     */
    @GetMapping("/{paymentId}")
    public Result<PaymentResponse> getPaymentById(@PathVariable Long paymentId) {
        log.debug("查询支付详情: paymentId={}", paymentId);
        return paymentFacadeService.getPaymentById(paymentId);
    }

    /**
     * 根据支付单号查询
     */
    @GetMapping("/no/{paymentNo}")
    public Result<PaymentResponse> getPaymentByNo(@PathVariable String paymentNo) {
        log.debug("根据支付单号查询: paymentNo={}", paymentNo);
        return paymentFacadeService.getPaymentByNo(paymentNo);
    }

    /**
     * 分页查询支付记录
     */
    @PostMapping("/query")
    public Result<PageResponse<PaymentResponse>> queryPayments(@Valid @RequestBody PaymentQueryRequest request) {
        log.debug("分页查询支付记录: 用户={}, 页码={}", request.getUserId(), request.getCurrentPage());
        return paymentFacadeService.queryPayments(request);
    }

    // =================== 支付回调处理 ===================

    /**
     * 支付回调处理
     */
    @PostMapping("/callback")
    public Result<Void> handlePaymentCallback(@Valid @RequestBody PaymentCallbackRequest request) {
        log.info("处理支付回调: 支付单号={}, 状态={}", request.getPaymentNo(), request.getStatus());
        return paymentFacadeService.handlePaymentCallback(request);
    }

    /**
     * 取消支付
     */
    @PutMapping("/{paymentId}/cancel")
    public Result<Void> cancelPayment(@PathVariable Long paymentId) {
        log.info("取消支付请求: paymentId={}", paymentId);
        return paymentFacadeService.cancelPayment(paymentId);
    }

    // =================== 支付状态管理 ===================

    /**
     * 同步支付状态
     */
    @PutMapping("/sync/{paymentNo}")
    public Result<PaymentResponse> syncPaymentStatus(@PathVariable String paymentNo) {
        log.info("同步支付状态: paymentNo={}", paymentNo);
        return paymentFacadeService.syncPaymentStatus(paymentNo);
    }

    /**
     * 获取用户支付记录
     */
    @GetMapping("/user/{userId}")
    public Result<List<PaymentResponse>> getUserPayments(@PathVariable Long userId,
                                                        @RequestParam(defaultValue = "20") Integer limit) {
        log.debug("获取用户支付记录: userId={}, limit={}", userId, limit);
        return paymentFacadeService.getUserPayments(userId, limit);
    }

    /**
     * 根据订单ID查询支付记录
     */
    @GetMapping("/order/{orderId}")
    public Result<List<PaymentResponse>> getPaymentsByOrderId(@PathVariable Long orderId) {
        log.debug("根据订单ID查询支付记录: orderId={}", orderId);
        return paymentFacadeService.getPaymentsByOrderId(orderId);
    }

    /**
     * 验证支付状态
     */
    @GetMapping("/verify/{paymentNo}")
    public Result<Boolean> verifyPaymentStatus(@PathVariable String paymentNo) {
        log.debug("验证支付状态: paymentNo={}", paymentNo);
        return paymentFacadeService.verifyPaymentStatus(paymentNo);
    }

    /**
     * 模拟支付完成 🧪
     * 测试专用接口 - 模拟支付成功，直接完成关联订单的支付流程
     * 
     * ⚠️ 注意：此接口仅用于开发和测试环境，生产环境会自动禁用
     * 
     * @param paymentId 支付ID
     * @param requestBody 模拟支付请求数据
     * @return 模拟支付结果
     */
    @PostMapping("/{paymentId}/mock-success")
    public Result<Map<String, Object>> mockPaymentSuccess(
            @PathVariable Long paymentId,
            @RequestBody Map<String, Object> requestBody) {
        try {
            log.info("🧪 执行模拟支付成功: paymentId={}, userId={}", 
                    paymentId, requestBody.get("userId"));
            
            // 验证是否为测试环境
            String env = System.getProperty("spring.profiles.active", "dev");
            if ("prod".equals(env) || "production".equals(env)) {
                log.warn("生产环境禁用模拟支付接口: paymentId={}", paymentId);
                return Result.error("MOCK_PAYMENT_DISABLED", "模拟支付接口在生产环境已禁用");
            }
            
            Long userId = requestBody.get("userId") != null ? 
                    Long.valueOf(requestBody.get("userId").toString()) : null;
            if (userId == null) {
                return Result.error("INVALID_PARAMETER", "用户ID不能为空");
            }
            
            // 调用门面服务处理模拟支付
            Result<PaymentResponse> paymentResult = paymentFacadeService.getPaymentById(paymentId);
            if (!paymentResult.getSuccess() || paymentResult.getData() == null) {
                return Result.error("PAYMENT_NOT_FOUND", "支付记录不存在");
            }
            
            PaymentResponse payment = paymentResult.getData();
            if (!"pending".equals(payment.getStatus())) {
                log.warn("支付状态不是待支付，无法模拟: paymentId={}, status={}", 
                        paymentId, payment.getStatus());
                return Result.error("MOCK_PAYMENT_NOT_PENDING", 
                        "支付状态不是待支付，当前状态: " + payment.getStatus());
            }
            
            // 构建模拟回调数据
            String mockTransactionId = requestBody.getOrDefault("mockTransactionId", 
                    "MOCK" + System.currentTimeMillis()).toString();
            
            // 这里应该调用支付服务的模拟成功方法
            // 注：实际实现需要在PaymentService中添加mockPaymentSuccess方法
            log.info("🎯 模拟支付成功处理: paymentId={}, mockTransactionId={}", 
                    paymentId, mockTransactionId);
            
            // 构建响应数据
            Map<String, Object> mockResponse = Map.of(
                "paymentId", paymentId,
                "paymentNo", payment.getPaymentNo(),
                "orderId", payment.getOrderId(),
                "orderNo", payment.getOrderNo(),
                "originalStatus", payment.getStatus(),
                "newStatus", "success",
                "mockTransactionId", mockTransactionId,
                "paidTime", java.time.LocalDateTime.now().toString(),
                "mockData", Map.of(
                    "isTestPayment", true,
                    "mockChannel", requestBody.getOrDefault("mockChannel", "alipay"),
                    "actualAmount", payment.getAmount(),
                    "processingTime", java.time.LocalDateTime.now().toString()
                ),
                "relatedOrderStatus", Map.of(
                    "orderId", payment.getOrderId(),
                    "orderStatus", "paid",
                    "statusUpdateTime", java.time.LocalDateTime.now().toString()
                )
            );
            
            log.info("✅ 模拟支付完成: paymentId={}, orderId={}", paymentId, payment.getOrderId());
            return Result.success(mockResponse);
            
        } catch (Exception e) {
            log.error("模拟支付失败: paymentId={}", paymentId, e);
            return Result.error("MOCK_PAYMENT_ERROR", "模拟支付失败: " + e.getMessage());
        }
    }

    // =================== 支付统计功能 ===================

    /**
     * 获取用户支付统计
     */
    @GetMapping("/statistics/{userId}")
    public Result<PaymentResponse> getPaymentStatistics(@PathVariable Long userId) {
        log.debug("获取用户支付统计: userId={}", userId);
        return paymentFacadeService.getPaymentStatistics(userId);
    }

    // =================== 系统维护功能 ===================

    /**
     * 更新过期支付订单（定时任务接口）
     */
    @PutMapping("/expired/update")
    public Result<Void> updateExpiredPayments() {
        try {
            log.info("执行过期支付订单更新任务");
            paymentService.updateExpiredPayments();
            log.info("过期支付订单更新任务完成");
            return Result.success(null);
        } catch (Exception e) {
            log.error("更新过期支付订单失败", e);
            return Result.error("PAYMENT_UPDATE_ERROR", "更新过期支付订单失败: " + e.getMessage());
        }
    }
} 