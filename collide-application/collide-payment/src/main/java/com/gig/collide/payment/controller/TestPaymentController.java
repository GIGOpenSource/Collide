package com.gig.collide.payment.controller;

import com.gig.collide.web.vo.Result;
import com.gig.collide.payment.domain.service.TestPaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 测试环境支付控制器
 * 提供测试环境下的模拟支付接口，可以直接完成支付流程
 * 
 * 注意：此控制器仅在测试环境下生效
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/test/payment")
@RequiredArgsConstructor
@Tag(name = "测试支付接口", description = "测试环境模拟支付接口")
@ConditionalOnProperty(name = "collide.payment.test.enabled", havingValue = "true", matchIfMissing = false)
public class TestPaymentController {
    
    private final TestPaymentService testPaymentService;
    
    /**
     * 模拟支付成功
     */
    @PostMapping("/mock-success/{orderNo}")
    @Operation(summary = "模拟支付成功", description = "测试环境下模拟订单支付成功")
    public Result<Map<String, Object>> mockPaymentSuccess(
            @Parameter(description = "订单号", required = true) 
            @PathVariable String orderNo,
            
            @Parameter(description = "模拟支付金额")
            @RequestParam(required = false) String payAmount,
            
            @Parameter(description = "模拟支付方式")
            @RequestParam(defaultValue = "TEST") String payType) {
        
        try {
            log.info("模拟支付成功，订单号：{}，支付金额：{}，支付方式：{}", orderNo, payAmount, payType);
            
            Map<String, Object> result = testPaymentService.mockPaymentSuccess(orderNo, payAmount, payType);
            
            log.info("模拟支付成功完成，订单号：{}", orderNo);
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("模拟支付失败，订单号：{}", orderNo, e);
            return Result.error("MOCK_PAYMENT_ERROR", "模拟支付失败：" + e.getMessage());
        }
    }
    
    /**
     * 模拟支付失败
     */
    @PostMapping("/mock-failure/{orderNo}")
    @Operation(summary = "模拟支付失败", description = "测试环境下模拟订单支付失败")
    public Result<Map<String, Object>> mockPaymentFailure(
            @Parameter(description = "订单号", required = true) 
            @PathVariable String orderNo,
            
            @Parameter(description = "失败原因")
            @RequestParam(defaultValue = "余额不足") String failureReason) {
        
        try {
            log.info("模拟支付失败，订单号：{}，失败原因：{}", orderNo, failureReason);
            
            Map<String, Object> result = testPaymentService.mockPaymentFailure(orderNo, failureReason);
            
            log.info("模拟支付失败完成，订单号：{}", orderNo);
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("模拟支付失败处理异常，订单号：{}", orderNo, e);
            return Result.error("MOCK_PAYMENT_ERROR", "模拟支付失败处理异常：" + e.getMessage());
        }
    }
    
    /**
     * 批量模拟支付成功（用于测试数据准备）
     */
    @PostMapping("/batch-mock-success")
    @Operation(summary = "批量模拟支付成功", description = "批量模拟多个订单支付成功，用于测试数据准备")
    public Result<Map<String, Object>> batchMockPaymentSuccess(
            @Parameter(description = "订单号列表", required = true)
            @RequestBody java.util.List<String> orderNos) {
        
        try {
            log.info("批量模拟支付成功，订单数量：{}", orderNos.size());
            
            Map<String, Object> result = testPaymentService.batchMockPaymentSuccess(orderNos);
            
            log.info("批量模拟支付成功完成，成功数量：{}", result.get("successCount"));
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("批量模拟支付失败", e);
            return Result.error("BATCH_MOCK_PAYMENT_ERROR", "批量模拟支付失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取支付状态（测试查询）
     */
    @GetMapping("/status/{orderNo}")
    @Operation(summary = "查询支付状态", description = "查询订单的支付状态")
    public Result<Map<String, Object>> getPaymentStatus(
            @Parameter(description = "订单号", required = true) 
            @PathVariable String orderNo) {
        
        try {
            log.info("查询支付状态，订单号：{}", orderNo);
            
            Map<String, Object> status = testPaymentService.getPaymentStatus(orderNo);
            
            return Result.success(status);
            
        } catch (Exception e) {
            log.error("查询支付状态失败，订单号：{}", orderNo, e);
            return Result.error("QUERY_PAYMENT_STATUS_ERROR", "查询支付状态失败：" + e.getMessage());
        }
    }
    
    /**
     * 重置支付状态（测试辅助）
     */
    @PostMapping("/reset/{orderNo}")
    @Operation(summary = "重置支付状态", description = "重置订单的支付状态到初始状态，用于重复测试")
    public Result<Void> resetPaymentStatus(
            @Parameter(description = "订单号", required = true) 
            @PathVariable String orderNo) {
        
        try {
            log.info("重置支付状态，订单号：{}", orderNo);
            
            testPaymentService.resetPaymentStatus(orderNo);
            
            log.info("重置支付状态成功，订单号：{}", orderNo);
            return Result.success(null);
            
        } catch (Exception e) {
            log.error("重置支付状态失败，订单号：{}", orderNo, e);
            return Result.error("RESET_PAYMENT_STATUS_ERROR", "重置支付状态失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取测试支付配置
     */
    @GetMapping("/config")
    @Operation(summary = "获取测试支付配置", description = "获取当前测试支付的配置信息")
    public Result<Map<String, Object>> getTestPaymentConfig() {
        
        try {
            Map<String, Object> config = testPaymentService.getTestPaymentConfig();
            
            return Result.success(config);
            
        } catch (Exception e) {
            log.error("获取测试支付配置失败", e);
            return Result.error("GET_TEST_CONFIG_ERROR", "获取测试支付配置失败：" + e.getMessage());
        }
    }
    
    /**
     * 模拟支付回调（异步通知）
     */
    @PostMapping("/callback/{orderNo}")
    @Operation(summary = "模拟支付回调", description = "模拟第三方支付平台的异步回调通知")
    public String mockPaymentCallback(
            @Parameter(description = "订单号", required = true) 
            @PathVariable String orderNo,
            
            @Parameter(description = "回调参数")
            @RequestParam Map<String, String> callbackParams) {
        
        try {
            log.info("模拟支付回调，订单号：{}，回调参数：{}", orderNo, callbackParams);
            
            boolean success = testPaymentService.processPaymentCallback(orderNo, callbackParams);
            
            // 返回第三方支付平台期望的响应格式
            if (success) {
                log.info("支付回调处理成功，订单号：{}", orderNo);
                return "SUCCESS";
            } else {
                log.warn("支付回调处理失败，订单号：{}", orderNo);
                return "FAIL";
            }
            
        } catch (Exception e) {
            log.error("支付回调处理异常，订单号：{}", orderNo, e);
            return "FAIL";
        }
    }
} 