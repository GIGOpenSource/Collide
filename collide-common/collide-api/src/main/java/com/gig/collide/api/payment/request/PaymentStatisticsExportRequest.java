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
 * 支付统计导出请求
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
public class PaymentStatisticsExportRequest extends BaseRequest {

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
     * 导出格式（EXCEL、CSV、PDF）
     */
    @NotBlank(message = "导出格式不能为空")
    private String exportFormat;

    /**
     * 导出的统计项
     */
    private List<String> exportMetrics;

    /**
     * 分组维度
     */
    private List<String> groupByDimensions;

    /**
     * 过滤条件
     */
    private List<String> filterConditions;

    /**
     * 导出文件名
     */
    private String fileName;

    /**
     * 导出说明
     */
    private String exportDescription;
} 