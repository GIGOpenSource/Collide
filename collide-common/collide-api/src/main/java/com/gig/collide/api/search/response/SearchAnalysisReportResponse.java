package com.gig.collide.api.search.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * 搜索分析报告响应
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
public class SearchAnalysisReportResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 报告ID
     */
    private String reportId;

    /**
     * 报告类型
     */
    private String reportType;

    /**
     * 报告标题
     */
    private String title;

    /**
     * 报告描述
     */
    private String description;

    /**
     * 分析时间范围
     */
    private String timeRange;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * 生成时间
     */
    private String generateTime;

    /**
     * 请求人ID
     */
    private Long requesterId;

    /**
     * 概览统计
     */
    private OverviewStatistics overview;

    /**
     * 热门关键词Top10
     */
    private List<KeywordRankItem> topKeywords;

    /**
     * 用户行为分析
     */
    private UserBehaviorAnalysis userBehavior;

    /**
     * 趋势分析数据
     */
    private List<TrendAnalysisItem> trendAnalysis;

    /**
     * 性能指标
     */
    private PerformanceMetrics performance;

    /**
     * 图表数据
     */
    private Map<String, Object> chartData;

    /**
     * 报告文件下载链接（如果有）
     */
    private String downloadUrl;

    /**
     * 概览统计
     */
    @Getter
    @Setter
    @ToString
    public static class OverviewStatistics {
        private Long totalSearchCount;
        private Long totalUserCount;
        private Long uniqueKeywordCount;
        private Double avgSearchPerUser;
        private Double popularityGrowthRate;
        private String mostPopularKeyword;
        private String mostGrowingKeyword;
    }

    /**
     * 关键词排行项
     */
    @Getter
    @Setter
    @ToString
    public static class KeywordRankItem {
        private Integer rank;
        private String keyword;
        private Long searchCount;
        private Long userCount;
        private Double hotScore;
        private Double growthRate;
    }

    /**
     * 用户行为分析
     */
    @Getter
    @Setter
    @ToString
    public static class UserBehaviorAnalysis {
        private Double avgSearchSessionDuration;
        private Double avgSearchesPerSession;
        private Double clickThroughRate;
        private Double conversionRate;
        private List<String> topSearchPatterns;
        private Map<String, Integer> deviceDistribution;
        private Map<String, Integer> timeDistribution;
    }

    /**
     * 趋势分析项
     */
    @Getter
    @Setter
    @ToString
    public static class TrendAnalysisItem {
        private String date;
        private Long searchCount;
        private Long userCount;
        private Double growthRate;
        private String trendDirection;
    }

    /**
     * 性能指标
     */
    @Getter
    @Setter
    @ToString
    public static class PerformanceMetrics {
        private Double avgResponseTime;
        private Double successRate;
        private Double errorRate;
        private Long totalRequests;
        private Long errorRequests;
    }
} 