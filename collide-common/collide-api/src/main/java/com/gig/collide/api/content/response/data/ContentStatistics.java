package com.gig.collide.api.content.response.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 内容统计信息响应对象
 * 包含内容的各种统计数据
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class ContentStatistics implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 内容ID
     */
    private Long contentId;

    /**
     * 总查看数
     */
    private Long totalViews;

    /**
     * 今日查看数
     */
    private Long todayViews;

    /**
     * 总点赞数
     */
    private Long totalLikes;

    /**
     * 今日点赞数
     */
    private Long todayLikes;

    /**
     * 总评论数
     */
    private Long totalComments;

    /**
     * 今日评论数
     */
    private Long todayComments;

    /**
     * 总分享数
     */
    private Long totalShares;

    /**
     * 今日分享数
     */
    private Long todayShares;

    /**
     * 总收藏数
     */
    private Long totalFavorites;

    /**
     * 今日收藏数
     */
    private Long todayFavorites;

    /**
     * 平均查看时长（秒）
     */
    private Double avgViewDuration;

    /**
     * 跳出率
     */
    private Double bounceRate;

    /**
     * 完成率（对于视频/音频内容）
     */
    private Double completionRate;

    /**
     * 每日统计数据（日期 -> 统计值）
     */
    private Map<String, Long> dailyStats;

    /**
     * 用户行为统计
     */
    private Map<String, Object> userBehaviorStats;

    /**
     * 统计更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 计算总互动数
     *
     * @return 总互动数
     */
    public Long getTotalInteractions() {
        return (totalLikes != null ? totalLikes : 0L)
            + (totalComments != null ? totalComments : 0L)
            + (totalShares != null ? totalShares : 0L)
            + (totalFavorites != null ? totalFavorites : 0L);
    }

    /**
     * 计算今日互动数
     *
     * @return 今日互动数
     */
    public Long getTodayInteractions() {
        return (todayLikes != null ? todayLikes : 0L)
            + (todayComments != null ? todayComments : 0L)
            + (todayShares != null ? todayShares : 0L)
            + (todayFavorites != null ? todayFavorites : 0L);
    }

    /**
     * 计算互动率
     *
     * @return 互动率
     */
    public Double getEngagementRate() {
        if (totalViews == null || totalViews == 0) {
            return 0.0;
        }
        return getTotalInteractions().doubleValue() / totalViews;
    }
} 