package com.gig.collide.api.payment.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * 支付回调重试请求
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
public class PaymentCallbackRetryRequest extends BaseRequest {

    /**
     * 回调记录ID（必填）
     */
    @NotNull(message = "回调记录ID不能为空")
    @Positive(message = "回调记录ID必须为正数")
    private Long callbackId;

    /**
     * 订单号（可选）
     */
    @Size(max = 64, message = "订单号长度不能超过64个字符")
    private String orderNo;

    /**
     * 重试原因（可选）
     */
    @Size(max = 255, message = "重试原因长度不能超过255个字符")
    private String retryReason;

    /**
     * 操作员ID（可选）
     */
    private Long operatorId;

    /**
     * 是否立即重试（可选，默认false）
     */
    private Boolean immediateRetry = false;
} 