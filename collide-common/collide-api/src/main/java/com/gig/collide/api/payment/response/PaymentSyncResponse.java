package com.gig.collide.api.payment.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 支付状态同步响应
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
public class PaymentSyncResponse extends BaseResponse {

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
     * 同步前状态
     */
    private String beforeStatus;

    /**
     * 同步后状态
     */
    private String afterStatus;

    /**
     * 是否状态发生变化
     */
    private Boolean statusChanged;

    /**
     * 同步时间
     */
    private String syncTime;

    /**
     * 第三方返回数据
     */
    private String thirdPartyData;
} 