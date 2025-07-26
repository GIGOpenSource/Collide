package com.gig.collide.payment.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 测试支付服务
 * 
 * 提供测试环境下的模拟支付功能
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "collide.payment.test.enabled", havingValue = "true", matchIfMissing = false)
public class TestPaymentService {

    // 模拟的支付状态存储（实际应该使用Redis或数据库）
    private final Map<String, Map<String, Object>> paymentStatusCache = new HashMap<>();

    /**
     * 模拟支付成功
     * 
     * @param orderNo 订单号
     * @param payAmount 支付金额
     * @param payType 支付方式
     * @return 支付结果
     */
    public Map<String, Object> mockPaymentSuccess(String orderNo, String payAmount, String payType) {
        log.info("处理模拟支付成功，订单号：{}，支付金额：{}，支付方式：{}", orderNo, payAmount, payType);
        
        Map<String, Object> result = new HashMap<>();
        
        // 构造支付成功结果
        result.put("orderNo", orderNo);
        result.put("payAmount", payAmount != null ? payAmount : "99.99");
        result.put("payType", payType);
        result.put("payStatus", "SUCCESS");
        result.put("transactionNo", "TEST_" + orderNo + "_" + System.currentTimeMillis());
        result.put("payTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        result.put("message", "模拟支付成功");
        
        // 缓存支付状态
        paymentStatusCache.put(orderNo, result);
        
        // TODO: 这里应该调用订单服务更新订单状态
        // orderService.updateOrderStatus(orderNo, OrderStatus.PAID);
        
        log.info("模拟支付成功完成，订单号：{}，交易号：{}", orderNo, result.get("transactionNo"));
        
        return result;
    }

    /**
     * 模拟支付失败
     * 
     * @param orderNo 订单号
     * @param failureReason 失败原因
     * @return 支付结果
     */
    public Map<String, Object> mockPaymentFailure(String orderNo, String failureReason) {
        log.info("处理模拟支付失败，订单号：{}，失败原因：{}", orderNo, failureReason);
        
        Map<String, Object> result = new HashMap<>();
        
        // 构造支付失败结果
        result.put("orderNo", orderNo);
        result.put("payStatus", "FAILED");
        result.put("failureReason", failureReason);
        result.put("transactionNo", "TEST_FAIL_" + orderNo + "_" + System.currentTimeMillis());
        result.put("failTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        result.put("message", "模拟支付失败：" + failureReason);
        
        // 缓存支付状态
        paymentStatusCache.put(orderNo, result);
        
        log.info("模拟支付失败完成，订单号：{}", orderNo);
        
        return result;
    }

    /**
     * 批量模拟支付成功
     * 
     * @param orderNos 订单号列表
     * @return 批量处理结果
     */
    public Map<String, Object> batchMockPaymentSuccess(List<String> orderNos) {
        log.info("批量模拟支付成功，订单数量：{}", orderNos.size());
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> successResults = new ArrayList<>();
        List<String> failedOrderNos = new ArrayList<>();
        
        for (String orderNo : orderNos) {
            try {
                Map<String, Object> paymentResult = mockPaymentSuccess(orderNo, "99.99", "TEST");
                successResults.add(paymentResult);
            } catch (Exception e) {
                log.error("批量支付处理失败，订单号：{}", orderNo, e);
                failedOrderNos.add(orderNo);
            }
        }
        
        result.put("totalCount", orderNos.size());
        result.put("successCount", successResults.size());
        result.put("failedCount", failedOrderNos.size());
        result.put("successResults", successResults);
        result.put("failedOrderNos", failedOrderNos);
        
        log.info("批量模拟支付完成，总数：{}，成功：{}，失败：{}", 
            orderNos.size(), successResults.size(), failedOrderNos.size());
        
        return result;
    }

    /**
     * 获取支付状态
     * 
     * @param orderNo 订单号
     * @return 支付状态
     */
    public Map<String, Object> getPaymentStatus(String orderNo) {
        log.info("查询支付状态，订单号：{}", orderNo);
        
        Map<String, Object> status = paymentStatusCache.get(orderNo);
        
        if (status == null) {
            // 如果没有缓存，返回默认状态
            status = new HashMap<>();
            status.put("orderNo", orderNo);
            status.put("payStatus", "PENDING");
            status.put("message", "订单未找到支付记录");
        }
        
        return status;
    }

    /**
     * 重置支付状态
     * 
     * @param orderNo 订单号
     */
    public void resetPaymentStatus(String orderNo) {
        log.info("重置支付状态，订单号：{}", orderNo);
        
        // 清除缓存的支付状态
        paymentStatusCache.remove(orderNo);
        
        // TODO: 这里应该调用订单服务重置订单状态
        // orderService.updateOrderStatus(orderNo, OrderStatus.UNPAID);
        
        log.info("支付状态重置完成，订单号：{}", orderNo);
    }

    /**
     * 获取测试支付配置
     * 
     * @return 测试配置信息
     */
    public Map<String, Object> getTestPaymentConfig() {
        Map<String, Object> config = new HashMap<>();
        
        config.put("enabled", true);
        config.put("supportedPayTypes", Arrays.asList("TEST", "ALIPAY", "WECHAT"));
        config.put("defaultAmount", "99.99");
        config.put("autoSuccessDelay", 3000); // 3秒后自动成功
        config.put("description", "测试环境支付配置");
        config.put("cachedPayments", paymentStatusCache.size());
        
        return config;
    }

    /**
     * 处理支付回调
     * 
     * @param orderNo 订单号
     * @param callbackParams 回调参数
     * @return 处理是否成功
     */
    public boolean processPaymentCallback(String orderNo, Map<String, String> callbackParams) {
        log.info("处理支付回调，订单号：{}，回调参数：{}", orderNo, callbackParams);
        
        try {
            // 模拟验证回调参数
            if (!validateCallbackParams(orderNo, callbackParams)) {
                log.warn("支付回调参数验证失败，订单号：{}", orderNo);
                return false;
            }
            
            // 获取支付状态
            String payStatus = callbackParams.getOrDefault("pay_status", "SUCCESS");
            
            if ("SUCCESS".equals(payStatus)) {
                // 模拟支付成功的回调处理
                mockPaymentSuccess(orderNo, callbackParams.get("pay_amount"), 
                    callbackParams.getOrDefault("pay_type", "TEST"));
            } else {
                // 模拟支付失败的回调处理
                mockPaymentFailure(orderNo, callbackParams.getOrDefault("failure_reason", "支付失败"));
            }
            
            log.info("支付回调处理成功，订单号：{}，支付状态：{}", orderNo, payStatus);
            return true;
            
        } catch (Exception e) {
            log.error("支付回调处理异常，订单号：{}", orderNo, e);
            return false;
        }
    }

    /**
     * 验证回调参数（简单模拟）
     * 
     * @param orderNo 订单号
     * @param callbackParams 回调参数
     * @return 验证是否通过
     */
    private boolean validateCallbackParams(String orderNo, Map<String, String> callbackParams) {
        // 简单的参数验证
        if (callbackParams == null || callbackParams.isEmpty()) {
            return false;
        }
        
        // 检查必要参数
        if (!callbackParams.containsKey("order_no") || 
            !orderNo.equals(callbackParams.get("order_no"))) {
            return false;
        }
        
        // 在真实环境中，这里应该验证签名等安全参数
        
        return true;
    }
}