package com.gig.collide.api.payment.request;

import com.gig.collide.api.payment.constant.CallbackStatusEnum;
import com.gig.collide.api.payment.constant.CallbackTypeEnum;
import com.gig.collide.api.payment.constant.PaymentTypeEnum;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 支付回调查询请求
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCallbackQueryRequest extends BaseRequest {

    /**
     * 支付记录ID（可选）
     */
    @Positive(message = "支付记录ID必须为正数")
    private Long paymentRecordId;

    /**
     * 订单号（可选）
     */
    @Size(max = 64, message = "订单号长度不能超过64个字符")
    private String orderNo;

    /**
     * 第三方交易流水号（可选）
     */
    @Size(max = 128, message = "第三方交易流水号长度不能超过128个字符")
    private String transactionNo;

    /**
     * 内部交易流水号（可选）
     */
    @Size(max = 128, message = "内部交易流水号长度不能超过128个字符")
    private String internalTransactionNo;

    /**
     * 用户ID（可选）
     */
    @Positive(message = "用户ID必须为正数")
    private Long userId;

    /**
     * 用户名称（可选）
     */
    @Size(max = 64, message = "用户名称长度不能超过64个字符")
    private String userName;

    /**
     * 回调类型（可选）
     */
    private CallbackTypeEnum callbackType;

    /**
     * 回调来源（可选）
     */
    private PaymentTypeEnum callbackSource;

    /**
     * 回调状态（可选）
     */
    private CallbackStatusEnum callbackStatus;

    /**
     * 签名验证结果（可选）
     */
    private Boolean signatureValid;

    /**
     * 创建时间开始（可选）
     */
    private LocalDateTime createTimeStart;

    /**
     * 创建时间结束（可选）
     */
    private LocalDateTime createTimeEnd;

    /**
     * 处理开始时间开始（可选）
     */
    private LocalDateTime processStartTimeStart;

    /**
     * 处理开始时间结束（可选）
     */
    private LocalDateTime processStartTimeEnd;
} 