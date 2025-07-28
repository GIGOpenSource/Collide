package com.gig.collide.api.payment;

import com.gig.collide.api.payment.request.PaymentCreateRequest;
import com.gig.collide.api.payment.request.PaymentQueryRequest;
import com.gig.collide.api.payment.request.PaymentCallbackRequest;
import com.gig.collide.api.payment.response.PaymentResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import java.util.List;

/**
 * 支付门面服务接口 - 简洁版
 * 基于简洁版SQL设计（t_payment）
 *
 * @author GIG Team
 * @version 2.0.0
 */
public interface PaymentFacadeService {

    /**
     * 创建支付订单
     */
    Result<PaymentResponse> createPayment(PaymentCreateRequest request);

    /**
     * 查询支付详情
     */
    Result<PaymentResponse> getPaymentById(Long paymentId);

    /**
     * 根据支付单号查询
     */
    Result<PaymentResponse> getPaymentByNo(String paymentNo);

    /**
     * 分页查询支付记录
     */
    Result<PageResponse<PaymentResponse>> queryPayments(PaymentQueryRequest request);

    /**
     * 支付回调处理
     */
    Result<Void> handlePaymentCallback(PaymentCallbackRequest request);

    /**
     * 取消支付
     */
    Result<Void> cancelPayment(Long paymentId);

    /**
     * 同步支付状态
     */
    Result<PaymentResponse> syncPaymentStatus(String paymentNo);

    /**
     * 获取用户支付记录
     */
    Result<List<PaymentResponse>> getUserPayments(Long userId, Integer limit);

    /**
     * 根据订单ID查询支付记录
     */
    Result<List<PaymentResponse>> getPaymentsByOrderId(Long orderId);

    /**
     * 验证支付状态
     */
    Result<Boolean> verifyPaymentStatus(String paymentNo);

    /**
     * 获取支付统计信息
     */
    Result<PaymentResponse> getPaymentStatistics(Long userId);
} 