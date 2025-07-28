package com.gig.collide.api.search.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 搜索趋势响应
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
public class SearchTrendResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 关键词
     */
    private String keyword;

    /**
     * 趋势数据列表
     */
    private List<TrendDataPoint> trendData;

    /**
     * 统计天数
     */
    private Integer days;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * 总搜索次数
     */
    private Long totalSearchCount;

    /**
     * 平均每日搜索次数
     */
    private Double avgDailySearchCount;

    /**
     * 最高单日搜索次数
     */
    private Long maxDailySearchCount;

    /**
     * 最低单日搜索次数
     */
    private Long minDailySearchCount;

    /**
     * 趋势方向（up/down/stable）
     */
    private String trendDirection;

    /**
     * 趋势数据点
     */
    @Getter
    @Setter
    @ToString
    public static class TrendDataPoint {

        /**
         * 日期（YYYY-MM-DD）
         */
        private String date;

        /**
         * 搜索次数
         */
        private Long searchCount;

        /**
         * 用户数
         */
        private Long userCount;

        /**
         * 点击次数
         */
        private Long clickCount;

        /**
         * 转化次数
         */
        private Long conversionCount;

        /**
         * 热度评分
         */
        private Double hotScore;

        /**
         * 与前一天相比的变化百分比
         */
        private Double changePercent;

        /**
         * 当日排名
         */
        private Integer rank;
    }
} 