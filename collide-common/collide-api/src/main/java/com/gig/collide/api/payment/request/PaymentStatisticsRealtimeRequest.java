package com.gig.collide.api.payment.request;

import com.gig.collide.api.payment.constant.PaymentTypeEnum;
import com.gig.collide.api.payment.constant.PaymentSceneEnum;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 支付实时统计请求
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
public class PaymentStatisticsRealtimeRequest extends BaseRequest {

    /**
     * 统计时间范围开始（可选，默认当前时间前24小时）
     */
    private LocalDateTime timeRangeStart;

    /**
     * 统计时间范围结束（可选，默认当前时间）
     */
    private LocalDateTime timeRangeEnd;

    /**
     * 支付方式（可选）
     */
    private PaymentTypeEnum payType;

    /**
     * 支付场景（可选）
     */
    private PaymentSceneEnum payScene;

    /**
     * 商户ID（可选）
     */
    @Positive(message = "商户ID必须为正数")
    private Long merchantId;

    /**
     * 统计维度（可选，默认HOUR）
     */
    @Pattern(regexp = "^(MINUTE|HOUR|DAY)$", message = "统计维度只能是MINUTE、HOUR或DAY")
    private String dimension = "HOUR";

    /**
     * 是否包含成功率（可选，默认true）
     */
    private Boolean includeSuccessRate = true;

    /**
     * 是否包含用户统计（可选，默认false）
     */
    private Boolean includeUserStats = false;
} 