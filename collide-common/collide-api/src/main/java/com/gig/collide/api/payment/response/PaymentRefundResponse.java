package com.gig.collide.api.payment.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 支付退款响应
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
public class PaymentRefundResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 支付记录ID
     */
    private Long paymentId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 退款单号
     */
    private String refundNo;

    /**
     * 内部交易流水号
     */
    private String internalTransactionNo;

    /**
     * 第三方退款流水号
     */
    private String refundTransactionNo;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 退款状态
     */
    private String refundStatus;

    /**
     * 预计到账时间
     */
    private String estimatedArrivalTime;

    /**
     * 退款处理时间
     */
    private String refundTime;

    /**
     * 第三方退款结果
     */
    private String thirdPartyResult;
} 