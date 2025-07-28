package com.gig.collide.api.payment.service;

import com.gig.collide.api.payment.request.*;
import com.gig.collide.api.payment.response.*;
import com.gig.collide.api.payment.response.data.PaymentCallbackInfo;
import com.gig.collide.base.response.BaseResponse;
import com.gig.collide.base.response.PageResponse;

/**
 * 支付回调门面服务接口
 * 提供支付回调处理功能
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public interface PaymentCallbackFacadeService {

    /**
     * 处理支付回调
     * 
     * @param callbackRequest 回调处理请求
     * @return 回调处理响应
     */
    PaymentCallbackHandleResponse handleCallback(PaymentCallbackHandleRequest callbackRequest);

    /**
     * 查询回调记录
     * 
     * @param queryRequest 查询请求
     * @return 查询响应
     */
    PaymentCallbackQueryResponse queryCallback(PaymentCallbackQueryRequest queryRequest);

    /**
     * 分页查询回调记录
     * 
     * @param queryRequest 分页查询请求
     * @return 分页响应
     */
    PageResponse<PaymentCallbackInfo> pageQueryCallbacks(PaymentCallbackQueryRequest queryRequest);

    /**
     * 重试失败的回调
     * 
     * @param retryRequest 重试请求
     * @return 重试响应
     */
    PaymentCallbackRetryResponse retryCallback(PaymentCallbackRetryRequest retryRequest);

    /**
     * 批量重试失败的回调
     * 
     * @param batchRetryRequest 批量重试请求
     * @return 批量重试响应
     */
    PaymentCallbackBatchRetryResponse batchRetryCallbacks(PaymentCallbackBatchRetryRequest batchRetryRequest);

    /**
     * 获取待处理的回调通知
     * 
     * @param pendingRequest 待处理回调请求
     * @return 待处理回调响应
     */
    PaymentCallbackPendingResponse getPendingCallbacks(PaymentCallbackPendingRequest pendingRequest);

    /**
     * 验证回调签名
     * 
     * @param verifyRequest 验证请求
     * @return 验证响应
     */
    PaymentCallbackVerifyResponse verifyCallbackSignature(PaymentCallbackVerifyRequest verifyRequest);

    /**
     * 标记回调为已处理
     * 
     * @param markRequest 标记请求
     * @return 标记响应
     */
    BaseResponse markCallbackProcessed(PaymentCallbackMarkRequest markRequest);

    /**
     * 获取回调统计信息
     * 
     * @param statsRequest 统计请求
     * @return 统计响应
     */
    PaymentCallbackStatsResponse getCallbackStats(PaymentCallbackStatsRequest statsRequest);
} 