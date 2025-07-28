package com.gig.collide.api.payment.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * 支付退款查询请求
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
public class PaymentRefundQueryRequest extends BaseRequest {

    /**
     * 订单号（可选）
     */
    @Size(max = 64, message = "订单号长度不能超过64个字符")
    private String orderNo;

    /**
     * 退款单号（可选）
     */
    @Size(max = 64, message = "退款单号长度不能超过64个字符")
    private String refundNo;

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
     * 用户ID（可选）
     */
    @Positive(message = "用户ID必须为正数")
    private Long userId;

    /**
     * 用户名称（可选）
     */
    @Size(max = 64, message = "用户名称长度不能超过64个字符")
    private String userName;
} 