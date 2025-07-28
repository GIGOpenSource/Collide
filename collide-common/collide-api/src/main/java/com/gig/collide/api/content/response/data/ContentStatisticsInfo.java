package com.gig.collide.api.content.response.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 内容统计信息
 * 包含内容的各项统计数据
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class ContentStatisticsInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 内容ID
     */
    private Long contentId;

    /**
     * 总浏览数
     */
    private Long totalViews;

    /**
     * 总点赞数
     */
    private Long totalLikes;

    /**
     * 总点踩数
     */
    private Long totalDislikes;

    /**
     * 总评论数
     */
    private Long totalComments;

    /**
     * 总分享数
     */
    private Long totalShares;

    /**
     * 总收藏数
     */
    private Long totalFavorites;

    /**
     * 实时点赞数（来自交互记录表）
     */
    private Long realLikeCount;

    /**
     * 实时收藏数（来自交互记录表）
     */
    private Long realFavoriteCount;

    /**
     * 实时分享数（来自交互记录表）
     */
    private Long realShareCount;

    /**
     * 今日点赞数
     */
    private Long todayLikes;

    /**
     * 今日收藏数
     */
    private Long todayFavorites;

    /**
     * 今日分享数
     */
    private Long todayShares;

    /**
     * 互动评分
     */
    private Double engagementScore;

    /**
     * 权重分数
     */
    private Double weightScore;

    /**
     * 计算总互动数
     *
     * @return 总互动数
     */
    public long calculateTotalEngagement() {
        long likes = totalLikes != null ? totalLikes : 0;
        long comments = totalComments != null ? totalComments : 0;
        long shares = totalShares != null ? totalShares : 0;
        long favorites = totalFavorites != null ? totalFavorites : 0;
        
        return likes + comments + shares + favorites;
    }

    /**
     * 计算今日互动数
     *
     * @return 今日互动数
     */
    public long calculateTodayEngagement() {
        long likes = todayLikes != null ? todayLikes : 0;
        long favorites = todayFavorites != null ? todayFavorites : 0;
        long shares = todayShares != null ? todayShares : 0;
        
        return likes + favorites + shares;
    }

    /**
     * 计算互动率（相对于浏览数）
     *
     * @return 互动率（百分比）
     */
    public double calculateEngagementRate() {
        if (totalViews == null || totalViews == 0) {
            return 0.0;
        }
        
        long totalEngagement = calculateTotalEngagement();
        return (double) totalEngagement / totalViews * 100;
    }

    /**
     * 计算点赞率（相对于浏览数）
     *
     * @return 点赞率（百分比）
     */
    public double calculateLikeRate() {
        if (totalViews == null || totalViews == 0) {
            return 0.0;
        }
        
        long likes = totalLikes != null ? totalLikes : 0;
        return (double) likes / totalViews * 100;
    }
} 