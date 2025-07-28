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
 * 支付统计趋势分析响应
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
public class PaymentStatisticsTrendResponse extends BaseResponse {

    /**
     * 趋势数据点列表
     */
    private List<TrendDataPoint> trendData;

    /**
     * 总体统计
     */
    private Map<String, BigDecimal> overallStats;

    /**
     * 趋势分析结果
     */
    private String trendAnalysis;

    /**
     * 增长率
     */
    private BigDecimal growthRate;

    /**
     * 趋势数据点
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class TrendDataPoint {
        /**
         * 时间点
         */
        private Long timestamp;

        /**
         * 时间标签
         */
        private String timeLabel;

        /**
         * 数值
         */
        private BigDecimal value;

        /**
         * 计数
         */
        private Long count;
    }
} 