package com.gig.collide.api.payment.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 支付成功率统计请求
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
public class PaymentStatisticsSuccessRateRequest extends BaseRequest {

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
     * 统计维度（OVERALL、PAYMENT_METHOD、TIME_PERIOD等）
     */
    private String dimension = "OVERALL";

    /**
     * 时间粒度（HOURLY、DAILY、WEEKLY等）
     */
    private String timeGranularity = "DAILY";

    /**
     * 支付方式过滤
     */
    private String paymentMethod;

    /**
     * 金额范围过滤
     */
    private String amountRange;
} 