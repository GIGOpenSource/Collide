package com.gig.collide.api.search.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 搜索建议统计响应
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
public class SearchSuggestionStatisticsResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 建议ID
     */
    private Long suggestionId;

    /**
     * 建议关键词
     */
    private String keyword;

    /**
     * 建议类型
     */
    private String suggestionType;

    /**
     * 基础统计信息
     */
    private BasicStatistics basicStats;

    /**
     * 点击统计
     */
    private ClickStatistics clickStats;

    /**
     * 转化统计
     */
    private ConversionStatistics conversionStats;

    /**
     * 时间趋势统计
     */
    private List<DailyTrendData> trendData;

    /**
     * 设备类型分布
     */
    private List<DeviceTypeStats> deviceStats;

    /**
     * 地域分布统计
     */
    private List<RegionStats> regionStats;

    /**
     * 统计更新时间
     */
    private String updateTime;

    /**
     * 基础统计信息
     */
    @Getter
    @Setter
    @ToString
    public static class BasicStatistics {
        private Long totalClicks;
        private Long uniqueUsers;
        private Double clickRate;
        private Integer weight;
        private Boolean enabled;
        private String createTime;
        private String lastClickTime;
        private Integer activeDays;
    }

    /**
     * 点击统计
     */
    @Getter
    @Setter
    @ToString
    public static class ClickStatistics {
        private Long todayClicks;
        private Long weekClicks;
        private Long monthClicks;
        private Double avgClicksPerDay;
        private Integer peakHour;
        private String mostActiveDay;
        private Double clickGrowthRate;
    }

    /**
     * 转化统计
     */
    @Getter
    @Setter
    @ToString
    public static class ConversionStatistics {
        private Long totalConversions;
        private Double conversionRate;
        private Double avgConversionValue;
        private Long totalConversionValue;
        private String topConversionSource;
    }

    /**
     * 每日趋势数据
     */
    @Getter
    @Setter
    @ToString
    public static class DailyTrendData {
        private String date;
        private Long clicks;
        private Long uniqueUsers;
        private Long conversions;
        private Double clickRate;
        private Double conversionRate;
    }

    /**
     * 设备类型统计
     */
    @Getter
    @Setter
    @ToString
    public static class DeviceTypeStats {
        private String deviceType;
        private Long clickCount;
        private Double percentage;
        private Double conversionRate;
    }

    /**
     * 地域统计
     */
    @Getter
    @Setter
    @ToString
    public static class RegionStats {
        private String region;
        private Long clickCount;
        private Double percentage;
        private Long uniqueUsers;
    }
} 