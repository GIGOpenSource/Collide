package com.gig.collide.api.payment.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * 支付退款请求
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
public class PaymentRefundRequest extends BaseRequest {

    /**
     * 订单号（必填）
     */
    @NotBlank(message = "订单号不能为空")
    @Size(max = 64, message = "订单号长度不能超过64个字符")
    private String orderNo;

    /**
     * 内部交易流水号（可选）
     */
    @Size(max = 128, message = "内部交易流水号长度不能超过128个字符")
    private String internalTransactionNo;

    /**
     * 第三方交易流水号（可选）
     */
    @Size(max = 128, message = "第三方交易流水号长度不能超过128个字符")
    private String transactionNo;

    /**
     * 用户ID（必填）
     */
    @NotNull(message = "用户ID不能为空")
    @Positive(message = "用户ID必须为正数")
    private Long userId;

    /**
     * 退款金额（必填）
     */
    @NotNull(message = "退款金额不能为空")
    @DecimalMin(value = "0.01", message = "退款金额必须大于0.01元")
    @DecimalMax(value = "999999.99", message = "退款金额不能超过999999.99元")
    private BigDecimal refundAmount;

    /**
     * 退款原因（必填）
     */
    @NotBlank(message = "退款原因不能为空")
    @Size(max = 255, message = "退款原因长度不能超过255个字符")
    private String refundReason;

    /**
     * 操作员ID（可选）
     */
    private Long operatorId;

    /**
     * 客户端IP地址（可选）
     */
    @Size(max = 45, message = "IP地址长度不能超过45个字符")
    private String clientIp;

    /**
     * 退款单号（可选，系统自动生成）
     */
    @Size(max = 64, message = "退款单号长度不能超过64个字符")
    private String refundNo;

    /**
     * 异步通知URL（可选）
     */
    @Size(max = 512, message = "通知URL长度不能超过512个字符")
    private String notifyUrl;

    /**
     * 备注信息（可选）
     */
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String remark;
} 