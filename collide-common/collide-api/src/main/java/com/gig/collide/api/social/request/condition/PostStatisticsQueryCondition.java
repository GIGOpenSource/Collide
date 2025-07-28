package com.gig.collide.api.social.request.condition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 基于动态统计数据的查询条件
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostStatisticsQueryCondition implements SocialQueryCondition {
    
    /**
     * 最小点赞数
     */
    private Long minLikeCount;
    
    /**
     * 最大点赞数
     */
    private Long maxLikeCount;
    
    /**
     * 最小评论数
     */
    private Long minCommentCount;
    
    /**
     * 最大评论数
     */
    private Long maxCommentCount;
    
    /**
     * 最小转发数
     */
    private Long minShareCount;
    
    /**
     * 最大转发数
     */
    private Long maxShareCount;
    
    /**
     * 最小浏览数
     */
    private Long minViewCount;
    
    /**
     * 最大浏览数
     */
    private Long maxViewCount;
    
    /**
     * 最小收藏数
     */
    private Long minFavoriteCount;
    
    /**
     * 最大收藏数
     */
    private Long maxFavoriteCount;
    
    /**
     * 最小热度分数
     */
    private BigDecimal minHotScore;
    
    /**
     * 最大热度分数
     */
    private BigDecimal maxHotScore;
    
    /**
     * 构造点赞数范围查询条件
     */
    public static PostStatisticsQueryCondition ofLikeRange(Long minLikeCount, Long maxLikeCount) {
        return new PostStatisticsQueryCondition(minLikeCount, maxLikeCount, null, null,
                null, null, null, null, null, null, null, null);
    }
    
    /**
     * 构造评论数范围查询条件
     */
    public static PostStatisticsQueryCondition ofCommentRange(Long minCommentCount, Long maxCommentCount) {
        return new PostStatisticsQueryCondition(null, null, minCommentCount, maxCommentCount,
                null, null, null, null, null, null, null, null);
    }
    
    /**
     * 构造热度分数范围查询条件
     */
    public static PostStatisticsQueryCondition ofHotScoreRange(BigDecimal minHotScore, BigDecimal maxHotScore) {
        return new PostStatisticsQueryCondition(null, null, null, null,
                null, null, null, null, null, null, minHotScore, maxHotScore);
    }
    
    /**
     * 构造热门动态查询条件（高点赞、高分享）
     */
    public static PostStatisticsQueryCondition ofPopular() {
        return new PostStatisticsQueryCondition(100L, null, null, null,
                10L, null, null, null, null, null, BigDecimal.valueOf(50), null);
    }
} 