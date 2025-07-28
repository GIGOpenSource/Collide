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
 * 支付成功率统计响应
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
public class PaymentStatisticsSuccessRateResponse extends BaseResponse {

    /**
     * 总体成功率
     */
    private BigDecimal overallSuccessRate;

    /**
     * 按维度分组的成功率
     */
    private Map<String, BigDecimal> dimensionSuccessRates;

    /**
     * 时间序列成功率数据
     */
    private List<TimeSeriesSuccessRate> timeSeriesData;

    /**
     * 成功率趋势分析
     */
    private String trendAnalysis;

    /**
     * 对比基准成功率
     */
    private BigDecimal benchmarkSuccessRate;

    /**
     * 时间序列成功率数据
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class TimeSeriesSuccessRate {
        /**
         * 时间点
         */
        private Long timestamp;

        /**
         * 时间标签
         */
        private String timeLabel;

        /**
         * 成功率
         */
        private BigDecimal successRate;

        /**
         * 总计数
         */
        private Long totalCount;

        /**
         * 成功计数
         */
        private Long successCount;
    }
} 