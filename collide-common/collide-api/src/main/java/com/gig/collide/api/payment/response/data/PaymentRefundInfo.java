package com.gig.collide.api.payment.response.data;

import com.gig.collide.api.payment.constant.PaymentTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付退款信息响应对象
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class PaymentRefundInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 支付记录ID
     */
    private Long paymentRecordId;

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
     * 第三方交易流水号
     */
    private String transactionNo;

    /**
     * 第三方退款流水号
     */
    private String refundTransactionNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 原支付金额
     */
    private BigDecimal originalPayAmount;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 已退款总金额
     */
    private BigDecimal totalRefundAmount;

    /**
     * 可退款余额
     */
    private BigDecimal refundableBalance;

    /**
     * 支付方式
     */
    private PaymentTypeEnum payType;

    /**
     * 退款原因
     */
    private String refundReason;

    /**
     * 退款状态
     */
    private String refundStatus;

    /**
     * 退款发起时间
     */
    private LocalDateTime refundInitiateTime;

    /**
     * 退款处理时间
     */
    private LocalDateTime refundProcessTime;

    /**
     * 退款完成时间
     */
    private LocalDateTime refundCompleteTime;

    /**
     * 预计到账时间
     */
    private LocalDateTime estimatedArrivalTime;

    /**
     * 操作员ID
     */
    private Long operatorId;

    /**
     * 操作员名称
     */
    private String operatorName;

    /**
     * 第三方返回信息
     */
    private String thirdPartyResponse;

    /**
     * 退款失败原因
     */
    private String failureReason;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 