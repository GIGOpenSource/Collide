package com.gig.collide.api.payment.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 支付回调处理响应
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
public class PaymentCallbackHandleResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 回调记录ID
     */
    private Long callbackId;

    /**
     * 支付记录ID
     */
    private Long paymentRecordId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 处理结果状态
     */
    private String processResult;

    /**
     * 处理结果消息
     */
    private String processMessage;

    /**
     * 签名验证结果
     */
    private Boolean signatureValid;

    /**
     * 处理开始时间
     */
    private String processStartTime;

    /**
     * 处理结束时间
     */
    private String processEndTime;

    /**
     * 处理耗时（毫秒）
     */
    private Long processTimeMs;

    /**
     * 是否需要重试
     */
    private Boolean needRetry;

    /**
     * 下次重试时间
     */
    private String nextRetryTime;
} 