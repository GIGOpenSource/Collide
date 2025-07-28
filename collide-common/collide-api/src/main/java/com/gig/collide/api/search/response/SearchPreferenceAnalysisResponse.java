package com.gig.collide.api.search.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * 搜索偏好分析响应
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
public class SearchPreferenceAnalysisResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 分析天数
     */
    private Integer days;

    /**
     * 分析开始日期
     */
    private String startDate;

    /**
     * 分析结束日期
     */
    private String endDate;

    /**
     * 用户搜索概览
     */
    private UserSearchOverview overview;

    /**
     * 偏好关键词Top10
     */
    private List<PreferenceKeyword> preferenceKeywords;

    /**
     * 内容类型偏好
     */
    private List<ContentTypePreference> contentTypePreferences;

    /**
     * 搜索时间偏好
     */
    private SearchTimePreference timePreference;

    /**
     * 搜索行为模式
     */
    private SearchBehaviorPattern behaviorPattern;

    /**
     * 兴趣标签
     */
    private List<String> interestTags;

    /**
     * 个性化推荐关键词
     */
    private List<String> recommendedKeywords;

    /**
     * 用户搜索概览
     */
    @Getter
    @Setter
    @ToString
    public static class UserSearchOverview {
        private Long totalSearchCount;
        private Integer uniqueKeywordCount;
        private Double avgSearchPerDay;
        private String mostActiveDay;
        private String mostActiveHour;
        private Double searchConsistency;
    }

    /**
     * 偏好关键词
     */
    @Getter
    @Setter
    @ToString
    public static class PreferenceKeyword {
        private String keyword;
        private Long searchCount;
        private Double frequency;
        private String contentType;
        private String firstSearchTime;
        private String lastSearchTime;
        private Integer searchDays;
    }

    /**
     * 内容类型偏好
     */
    @Getter
    @Setter
    @ToString
    public static class ContentTypePreference {
        private String contentType;
        private Long searchCount;
        private Double percentage;
        private Integer rank;
        private Double avgSessionDuration;
    }

    /**
     * 搜索时间偏好
     */
    @Getter
    @Setter
    @ToString
    public static class SearchTimePreference {
        private Map<String, Integer> hourlyDistribution;
        private Map<String, Integer> weeklyDistribution;
        private String peakHour;
        private String peakDay;
        private String searchTimePattern;
    }

    /**
     * 搜索行为模式
     */
    @Getter
    @Setter
    @ToString
    public static class SearchBehaviorPattern {
        private Double avgKeywordLength;
        private Double refinementRate;
        private Double explorationRate;
        private String searchStyle; // focused/exploratory/mixed
        private List<String> commonSearchPatterns;
        private Double sessionContinuityRate;
    }
} 