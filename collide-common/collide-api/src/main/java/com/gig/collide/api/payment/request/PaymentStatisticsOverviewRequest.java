package com.gig.collide.api.payment.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 支付统计概览请求
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
public class PaymentStatisticsOverviewRequest extends BaseRequest {

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
     * 概览类型（DASHBOARD、SUMMARY、DETAIL）
     */
    private String overviewType = "DASHBOARD";

    /**
     * 包含的指标
     */
    private List<String> includeMetrics;

    /**
     * 对比时间范围
     */
    private String compareTimeRange;

    /**
     * 分组维度
     */
    private List<String> groupByDimensions;
} 