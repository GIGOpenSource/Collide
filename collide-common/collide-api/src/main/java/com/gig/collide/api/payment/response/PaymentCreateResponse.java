package com.gig.collide.api.payment.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 支付创建响应
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
public class PaymentCreateResponse extends BaseResponse {

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
     * 第三方支付URL（需要跳转时返回）
     */
    private String paymentUrl;

    /**
     * 支付二维码（二维码支付时返回）
     */
    private String qrCode;

    /**
     * 预支付交易会话标识（APP支付时返回）
     */
    private String prepayId;

    /**
     * 支付过期时间
     */
    private String expireTime;

    /**
     * 支付附加信息（JSON格式）
     */
    private String extraData;
} 