package com.gig.collide.api.payment.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

/**
 * 支付回调批量重试请求
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
public class PaymentCallbackBatchRetryRequest extends BaseRequest {

    /**
     * 回调记录ID列表（必填）
     */
    @NotNull(message = "回调记录ID列表不能为空")
    @Size(min = 1, max = 100, message = "回调记录ID列表大小必须在1-100之间")
    private List<@Positive(message = "回调记录ID必须为正数") Long> callbackIds;

    /**
     * 批量重试原因（可选）
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

    /**
     * 批量处理超时时间（秒）（可选，默认300秒）
     */
    @Min(value = 10, message = "批量处理超时时间不能少于10秒")
    @Max(value = 3600, message = "批量处理超时时间不能超过3600秒")
    private Integer timeoutSeconds = 300;
} 