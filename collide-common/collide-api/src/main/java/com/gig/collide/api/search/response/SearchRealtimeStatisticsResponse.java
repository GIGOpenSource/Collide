package com.gig.collide.api.search.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 实时搜索统计响应
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
public class SearchRealtimeStatisticsResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 关键词
     */
    private String keyword;

    /**
     * 实时搜索次数
     */
    private Long realtimeSearchCount;

    /**
     * 实时用户数
     */
    private Long realtimeUserCount;

    /**
     * 实时点击次数
     */
    private Long realtimeClickCount;

    /**
     * 实时转化次数
     */
    private Long realtimeConversionCount;

    /**
     * 实时热度评分
     */
    private Double realtimeHotScore;

    /**
     * 当前排名
     */
    private Integer currentRank;

    /**
     * 1小时前排名
     */
    private Integer rankOneHourAgo;

    /**
     * 排名变化
     */
    private Integer rankChange;

    /**
     * 统计时间窗口（分钟）
     */
    private Integer timeWindowMinutes;

    /**
     * 数据更新时间
     */
    private String updateTime;

    /**
     * 近期时间点数据
     */
    private List<RealtimeDataPoint> recentDataPoints;

    /**
     * 实时数据点
     */
    @Getter
    @Setter
    @ToString
    public static class RealtimeDataPoint {

        /**
         * 时间点（HH:mm:ss）
         */
        private String timePoint;

        /**
         * 搜索次数
         */
        private Long searchCount;

        /**
         * 用户数
         */
        private Long userCount;

        /**
         * 热度评分
         */
        private Double hotScore;

        /**
         * 与前一时间点的变化百分比
         */
        private Double changePercent;
    }
} 