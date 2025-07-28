package com.gig.collide.api.payment.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 支付取消响应
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
public class PaymentCancelResponse extends BaseResponse {

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
     * 内部交易流水号
     */
    private String internalTransactionNo;

    /**
     * 取消时间
     */
    private String cancelTime;

    /**
     * 取消状态
     */
    private String cancelStatus;

    /**
     * 第三方取消结果
     */
    private String thirdPartyResult;
} 