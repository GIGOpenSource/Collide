package com.gig.collide.api.payment.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 支付统计报告请求
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
public class PaymentStatisticsReportRequest extends BaseRequest {

    /**
     * 报告类型（DAILY、WEEKLY、MONTHLY、CUSTOM）
     */
    @NotBlank(message = "报告类型不能为空")
    private String reportType;

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
     * 报告格式（PDF、EXCEL、JSON）
     */
    private String reportFormat = "JSON";

    /**
     * 包含的统计项
     */
    private List<String> includeMetrics;

    /**
     * 分组维度
     */
    private List<String> groupByDimensions;

    /**
     * 过滤条件
     */
    private List<String> filterConditions;

    /**
     * 报告标题
     */
    private String reportTitle;

    /**
     * 报告说明
     */
    private String reportDescription;
} 