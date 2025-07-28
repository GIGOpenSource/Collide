package com.gig.collide.api.payment.service;

import com.gig.collide.api.payment.request.*;
import com.gig.collide.api.payment.response.*;
import com.gig.collide.api.payment.response.data.PaymentRecordInfo;
import com.gig.collide.base.response.BaseResponse;
import com.gig.collide.base.response.PageResponse;

/**
 * 支付门面服务接口
 * 提供支付核心业务功能
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public interface PaymentFacadeService {

    /**
     * 创建支付订单
     * 
     * @param createRequest 创建支付请求
     * @return 创建支付响应
     */
    PaymentCreateResponse createPayment(PaymentCreateRequest createRequest);

    /**
     * 查询支付记录
     * 
     * @param queryRequest 查询请求
     * @return 查询响应
     */
    PaymentQueryResponse queryPayment(PaymentQueryRequest queryRequest);

    /**
     * 分页查询支付记录
     * 
     * @param queryRequest 分页查询请求
     * @return 分页响应
     */
    PageResponse<PaymentRecordInfo> pageQueryPayments(PaymentQueryRequest queryRequest);

    /**
     * 取消支付订单
     * 
     * @param cancelRequest 取消支付请求
     * @return 取消支付响应
     */
    PaymentCancelResponse cancelPayment(PaymentCancelRequest cancelRequest);

    /**
     * 申请退款
     * 
     * @param refundRequest 退款请求
     * @return 退款响应
     */
    PaymentRefundResponse refundPayment(PaymentRefundRequest refundRequest);

    /**
     * 查询退款状态
     * 
     * @param refundQueryRequest 退款查询请求
     * @return 退款查询响应
     */
    PaymentRefundQueryResponse queryRefund(PaymentRefundQueryRequest refundQueryRequest);

    /**
     * 支付状态同步
     * 
     * @param syncRequest 同步请求
     * @return 同步响应
     */
    PaymentSyncResponse syncPaymentStatus(PaymentSyncRequest syncRequest);

    /**
     * 获取支付方式列表
     * 
     * @param paymentMethodRequest 支付方式请求
     * @return 支付方式响应
     */
    PaymentMethodResponse getPaymentMethods(PaymentMethodRequest paymentMethodRequest);

    /**
     * 验证支付签名
     * 
     * @param verifyRequest 验证请求
     * @return 验证响应
     */
    PaymentVerifyResponse verifyPaymentSignature(PaymentVerifyRequest verifyRequest);

    /**
     * 检查订单号是否存在
     * 
     * @param orderNo 订单号
     * @return 是否存在
     */
    Boolean checkOrderExists(String orderNo);

    /**
     * 生成内部交易流水号
     * 
     * @param orderNo 订单号
     * @return 内部交易流水号
     */
    String generateInternalTransactionNo(String orderNo);
} 