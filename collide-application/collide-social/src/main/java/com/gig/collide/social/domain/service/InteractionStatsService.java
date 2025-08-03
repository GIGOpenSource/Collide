package com.gig.collide.social.domain.service;

import com.gig.collide.social.domain.entity.SocialContent;

/**
 * 互动统计服务接口
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
public interface InteractionStatsService {

    /**
     * 增加点赞数
     */
    void incrementLikeCount(Long contentId);

    /**
     * 减少点赞数
     */
    void decrementLikeCount(Long contentId);

    /**
     * 增加评论数
     */
    void incrementCommentCount(Long contentId);

    /**
     * 减少评论数
     */
    void decrementCommentCount(Long contentId);

    /**
     * 增加收藏数
     */
    void incrementFavoriteCount(Long contentId);

    /**
     * 减少收藏数
     */
    void decrementFavoriteCount(Long contentId);

    /**
     * 增加分享数
     */
    void incrementShareCount(Long contentId);

    /**
     * 增加浏览数
     */
    void incrementViewCount(Long contentId);

    /**
     * 获取内容统计信息
     */
    ContentStats getContentStats(Long contentId);

    /**
     * 批量获取内容统计信息
     */
    java.util.Map<Long, ContentStats> getContentStatsBatch(java.util.List<Long> contentIds);

    /**
     * 重新计算内容统计数据
     */
    void recalculateContentStats(Long contentId);
    
    /**
     * 诊断统计数据一致性
     */
    void diagnoseContentStats(Long contentId);

    /**
     * 内容统计信息类
     */
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    class ContentStats {
        private Integer likeCount;
        private Integer commentCount;
        private Integer favoriteCount;
        private Integer shareCount;
        private Integer viewCount;
    }
}