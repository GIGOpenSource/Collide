package com.gig.collide.api.payment.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 支付回调重试响应
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
public class PaymentCallbackRetryResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 回调记录ID
     */
    private Long callbackId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 重试结果状态
     */
    private String retryResult;

    /**
     * 重试结果消息
     */
    private String retryMessage;

    /**
     * 重试时间
     */
    private String retryTime;

    /**
     * 当前重试次数
     */
    private Integer currentRetryCount;

    /**
     * 最大重试次数
     */
    private Integer maxRetryCount;

    /**
     * 下次重试时间
     */
    private String nextRetryTime;

    /**
     * 是否已达到最大重试次数
     */
    private Boolean reachedMaxRetry;
} 