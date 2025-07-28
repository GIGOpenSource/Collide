package com.gig.collide.api.payment.request;

import com.gig.collide.api.payment.constant.CallbackTypeEnum;
import com.gig.collide.api.payment.constant.PaymentTypeEnum;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * 获取待处理回调请求
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
public class PaymentCallbackPendingRequest extends BaseRequest {

    /**
     * 回调类型（可选）
     */
    private CallbackTypeEnum callbackType;

    /**
     * 回调来源（可选）
     */
    private PaymentTypeEnum callbackSource;

    /**
     * 最大重试次数过滤（可选）
     */
    @Min(value = 0, message = "最大重试次数不能为负数")
    private Integer maxRetryCount;

    /**
     * 超时分钟数（可选，默认30分钟）
     */
    @Min(value = 1, message = "超时分钟数必须大于0")
    @Max(value = 1440, message = "超时分钟数不能超过1440分钟（24小时）")
    private Integer timeoutMinutes = 30;

    /**
     * 限制返回数量（可选，默认100）
     */
    @Min(value = 1, message = "限制返回数量必须大于0")
    @Max(value = 1000, message = "限制返回数量不能超过1000")
    private Integer limitCount = 100;

    /**
     * 是否仅返回失败的回调（可选，默认false）
     */
    private Boolean onlyFailed = false;
} 