package com.gig.collide.api.search.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * 搜索历史统计响应
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
public class SearchHistoryStatisticsResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 统计天数
     */
    private Integer days;

    /**
     * 统计开始日期
     */
    private String startDate;

    /**
     * 统计结束日期
     */
    private String endDate;

    /**
     * 基础统计信息
     */
    private BasicStatistics basicStats;

    /**
     * 关键词统计Top10
     */
    private List<KeywordStats> topKeywords;

    /**
     * 内容类型分布
     */
    private List<ContentTypeStats> contentTypeDistribution;

    /**
     * 每日搜索趋势
     */
    private List<DailySearchTrend> dailyTrends;

    /**
     * 时段分布
     */
    private Map<String, Integer> hourlyDistribution;

    /**
     * 设备类型分布
     */
    private Map<String, Integer> deviceDistribution;

    /**
     * 基础统计信息
     */
    @Getter
    @Setter
    @ToString
    public static class BasicStatistics {
        private Long totalSearchCount;
        private Integer uniqueKeywordCount;
        private Integer activeDays;
        private Double avgSearchPerDay;
        private String mostActiveDay;
        private String mostActiveHour;
        private String favoriteContentType;
        private Double searchConsistency;
    }

    /**
     * 关键词统计
     */
    @Getter
    @Setter
    @ToString
    public static class KeywordStats {
        private String keyword;
        private Long searchCount;
        private Double frequency;
        private String contentType;
        private String firstSearchTime;
        private String lastSearchTime;
        private Integer searchDays;
    }

    /**
     * 内容类型统计
     */
    @Getter
    @Setter
    @ToString
    public static class ContentTypeStats {
        private String contentType;
        private Long searchCount;
        private Double percentage;
        private Integer uniqueKeywords;
        private String mostSearchedKeyword;
    }

    /**
     * 每日搜索趋势
     */
    @Getter
    @Setter
    @ToString
    public static class DailySearchTrend {
        private String date;
        private Long searchCount;
        private Integer uniqueKeywords;
        private String topKeyword;
        private Double dayOfWeekAvg;
    }
} 