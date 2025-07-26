package com.gig.collide.api.payment.service;

import com.gig.collide.base.response.SingleResponse;

import java.util.Map;

/**
 * 支付服务 Facade 接口
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
public interface PaymentFacadeService {

    /**
     * 模拟支付成功
     * 
     * @param orderNo 订单号
     * @param payAmount 支付金额
     * @param payType 支付方式
     * @return 支付结果
     */
    SingleResponse<Map<String, Object>> mockPaymentSuccess(String orderNo, String payAmount, String payType);

    /**
     * 模拟支付失败
     * 
     * @param orderNo 订单号
     * @param failureReason 失败原因
     * @return 支付结果
     */
    SingleResponse<Map<String, Object>> mockPaymentFailure(String orderNo, String failureReason);

    /**
     * 批量模拟支付成功
     * 
     * @param orderNos 订单号列表
     * @return 批量处理结果
     */
    SingleResponse<Map<String, Object>> batchMockPaymentSuccess(java.util.List<String> orderNos);

    /**
     * 获取支付状态
     * 
     * @param orderNo 订单号
     * @return 支付状态
     */
    SingleResponse<Map<String, Object>> getPaymentStatus(String orderNo);

    /**
     * 重置支付状态
     * 
     * @param orderNo 订单号
     * @return 操作结果
     */
    SingleResponse<Void> resetPaymentStatus(String orderNo);

    /**
     * 获取测试支付配置
     * 
     * @return 配置信息
     */
    SingleResponse<Map<String, Object>> getTestPaymentConfig();

    /**
     * 处理支付回调
     * 
     * @param orderNo 订单号
     * @param callbackParams 回调参数
     * @return 处理结果
     */
    SingleResponse<Boolean> processPaymentCallback(String orderNo, Map<String, String> callbackParams);
} 