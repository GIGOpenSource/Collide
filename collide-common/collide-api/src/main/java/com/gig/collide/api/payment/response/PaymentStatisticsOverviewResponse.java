package com.gig.collide.api.payment.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 支付统计概览响应
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
public class PaymentStatisticsOverviewResponse extends BaseResponse {

    /**
     * 核心指标
     */
    private Map<String, BigDecimal> coreMetrics;

    /**
     * 趋势指标
     */
    private Map<String, BigDecimal> trendMetrics;

    /**
     * 对比指标
     */
    private Map<String, BigDecimal> compareMetrics;

    /**
     * 图表数据
     */
    private List<ChartData> chartDataList;

    /**
     * 告警信息
     */
    private List<AlertInfo> alerts;

    /**
     * 统计摘要
     */
    private String statisticsSummary;

    /**
     * 图表数据
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class ChartData {
        /**
         * 图表类型
         */
        private String chartType;

        /**
         * 图表标题
         */
        private String chartTitle;

        /**
         * 数据系列
         */
        private List<DataSeries> dataSeries;
    }

    /**
     * 数据系列
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class DataSeries {
        /**
         * 系列名称
         */
        private String seriesName;

        /**
         * 数据点
         */
        private List<DataPoint> dataPoints;
    }

    /**
     * 数据点
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class DataPoint {
        /**
         * X轴值（时间戳或标签）
         */
        private String xValue;

        /**
         * Y轴值
         */
        private BigDecimal yValue;
    }

    /**
     * 告警信息
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class AlertInfo {
        /**
         * 告警级别
         */
        private String alertLevel;

        /**
         * 告警消息
         */
        private String alertMessage;

        /**
         * 告警时间
         */
        private Long alertTime;
    }
} 