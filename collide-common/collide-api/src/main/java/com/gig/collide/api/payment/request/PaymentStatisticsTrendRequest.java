package com.gig.collide.api.payment.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 支付统计趋势分析请求
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
public class PaymentStatisticsTrendRequest extends BaseRequest {

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    private Long startTime;

    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空")
    private Long endTime;

    /**
     * 趋势维度（HOURLY、DAILY、WEEKLY、MONTHLY）
     */
    private String dimension = "DAILY";

    /**
     * 指标类型（AMOUNT、COUNT、SUCCESS_RATE等）
     */
    private String metricType = "AMOUNT";

    /**
     * 支付方式过滤
     */
    private String paymentMethod;

    /**
     * 用户ID过滤
     */
    private Long userId;
} 