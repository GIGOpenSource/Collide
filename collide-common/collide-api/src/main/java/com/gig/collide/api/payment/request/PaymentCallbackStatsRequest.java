package com.gig.collide.api.payment.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 支付回调统计请求
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class PaymentCallbackStatsRequest extends BaseRequest {

    /**
     * 统计开始时间
     */
    @NotNull(message = "统计开始时间不能为空")
    private Long startTime;

    /**
     * 统计结束时间
     */
    @NotNull(message = "统计结束时间不能为空")
    private Long endTime;

    /**
     * 支付渠道过滤
     */
    private String paymentChannel;

    /**
     * 回调类型过滤
     */
    private String callbackType;

    /**
     * 统计维度
     */
    private String dimension;
} 