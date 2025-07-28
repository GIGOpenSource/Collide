package com.gig.collide.api.social.response.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 社交统计信息 DTO
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialStatisticsInfo implements Serializable {
    
    /**
     * 统计时间范围开始
     */
    private LocalDateTime startTime;
    
    /**
     * 统计时间范围结束
     */
    private LocalDateTime endTime;
    
    /**
     * 统计维度
     */
    private String statisticsDimension;
    
    /**
     * 总动态数
     */
    private Long totalPosts;
    
    /**
     * 总互动数
     */
    private Long totalInteractions;
    
    /**
     * 总点赞数
     */
    private Long totalLikes;
    
    /**
     * 总评论数
     */
    private Long totalComments;
    
    /**
     * 总转发数
     */
    private Long totalShares;
    
    /**
     * 总浏览数
     */
    private Long totalViews;
    
    /**
     * 总收藏数
     */
    private Long totalFavorites;
    
    /**
     * 平均热度分数
     */
    private BigDecimal avgHotScore;
    
    /**
     * 最高热度分数
     */
    private BigDecimal maxHotScore;
    
    /**
     * 活跃用户数
     */
    private Long activeUsers;
    
    /**
     * 新增用户数
     */
    private Long newUsers;
    
    /**
     * 用户活跃度排行
     */
    private List<UserActivityRankInfo> userActivityRanking;
    
    /**
     * 热门动态排行
     */
    private List<HotPostRankInfo> hotPostRanking;
    
    /**
     * 话题热度排行
     */
    private List<TopicHeatRankInfo> topicHeatRanking;
    
    /**
     * 用户活跃度排行信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserActivityRankInfo implements Serializable {
        private Long userId;
        private String username;
        private String nickname;
        private String avatar;
        private Long postCount;
        private Long interactionCount;
        private BigDecimal activityScore;
        private Integer rank;
    }
    
    /**
     * 热门动态排行信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HotPostRankInfo implements Serializable {
        private Long postId;
        private String postTitle;
        private Long authorId;
        private String authorNickname;
        private BigDecimal hotScore;
        private Long totalInteractions;
        private Integer rank;
    }
    
    /**
     * 话题热度排行信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopicHeatRankInfo implements Serializable {
        private String topic;
        private Long postCount;
        private Long interactionCount;
        private BigDecimal heatScore;
        private Integer rank;
    }
    
    /**
     * 计算平均参与度
     */
    public Double getAvgEngagementRate() {
        if (totalViews == null || totalViews == 0) {
            return 0.0;
        }
        return totalInteractions.doubleValue() / totalViews.doubleValue();
    }
    
    /**
     * 计算用户平均发布数
     */
    public Double getAvgPostsPerUser() {
        if (activeUsers == null || activeUsers == 0) {
            return 0.0;
        }
        return totalPosts.doubleValue() / activeUsers.doubleValue();
    }
    
    /**
     * 计算动态平均互动数
     */
    public Double getAvgInteractionsPerPost() {
        if (totalPosts == null || totalPosts == 0) {
            return 0.0;
        }
        return totalInteractions.doubleValue() / totalPosts.doubleValue();
    }
} 