package com.gig.collide.api.social.service;

import com.gig.collide.api.social.request.SocialInteractionRequest;
import com.gig.collide.api.social.request.SocialInteractionQueryRequest;
import com.gig.collide.api.social.response.SocialInteractionResponse;
import com.gig.collide.api.social.response.SocialPostQueryResponse;
import com.gig.collide.api.social.response.data.SocialInteractionInfo;
import com.gig.collide.api.social.constant.InteractionTypeEnum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 社交互动门面服务接口
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
public interface SocialInteractionFacadeService {
    
    /**
     * 执行互动操作（点赞、收藏、转发、浏览）
     */
    SocialInteractionResponse interact(SocialInteractionRequest request);
    
    /**
     * 取消互动操作
     */
    SocialInteractionResponse cancelInteraction(Long postId, Long userId, InteractionTypeEnum interactionType);
    
    /**
     * 点赞动态
     */
    SocialInteractionResponse likePost(Long postId, Long userId, String deviceInfo, String ipAddress);
    
    /**
     * 取消点赞
     */
    SocialInteractionResponse unlikePost(Long postId, Long userId);
    
    /**
     * 收藏动态
     */
    SocialInteractionResponse favoritePost(Long postId, Long userId, String deviceInfo, String ipAddress);
    
    /**
     * 取消收藏
     */
    SocialInteractionResponse unfavoritePost(Long postId, Long userId);
    
    /**
     * 转发动态
     */
    SocialInteractionResponse sharePost(Long postId, Long userId, String shareComment, 
                                       String deviceInfo, String ipAddress);
    
    /**
     * 记录浏览
     */
    SocialInteractionResponse viewPost(Long postId, Long userId, String deviceInfo, String ipAddress);
    
    /**
     * 查询用户互动记录
     */
    SocialPostQueryResponse<List<SocialInteractionInfo>> getUserInteractions(Long userId, 
                                                                             InteractionTypeEnum interactionType,
                                                                             Integer pageNum, Integer pageSize);
    
    /**
     * 查询动态的互动记录
     */
    SocialPostQueryResponse<List<SocialInteractionInfo>> getPostInteractions(Long postId, 
                                                                             InteractionTypeEnum interactionType,
                                                                             Integer pageNum, Integer pageSize);
    
    /**
     * 查询用户的点赞列表
     */
    SocialPostQueryResponse<List<SocialInteractionInfo>> getUserLikes(Long userId, 
                                                                      Integer pageNum, Integer pageSize);
    
    /**
     * 查询用户的收藏列表
     */
    SocialPostQueryResponse<List<SocialInteractionInfo>> getUserFavorites(Long userId, 
                                                                          Integer pageNum, Integer pageSize);
    
    /**
     * 查询用户的转发列表
     */
    SocialPostQueryResponse<List<SocialInteractionInfo>> getUserShares(Long userId, 
                                                                       Integer pageNum, Integer pageSize);
    
    /**
     * 查询动态的点赞用户列表
     */
    SocialPostQueryResponse<List<SocialInteractionInfo>> getPostLikes(Long postId, 
                                                                      Integer pageNum, Integer pageSize);
    
    /**
     * 查询动态的收藏用户列表
     */
    SocialPostQueryResponse<List<SocialInteractionInfo>> getPostFavorites(Long postId, 
                                                                          Integer pageNum, Integer pageSize);
    
    /**
     * 查询动态的转发用户列表
     */
    SocialPostQueryResponse<List<SocialInteractionInfo>> getPostShares(Long postId, 
                                                                       Integer pageNum, Integer pageSize);
    
    /**
     * 批量查询用户对动态的互动状态
     */
    Map<Long, UserInteractionStatus> batchGetUserInteractionStatus(Long userId, List<Long> postIds);
    
    /**
     * 获取用户互动统计
     */
    UserInteractionStatistics getUserInteractionStatistics(Long userId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取动态互动统计
     */
    PostInteractionStatistics getPostInteractionStatistics(Long postId);
    
    /**
     * 获取热门互动用户排行
     */
    List<UserInteractionRanking> getInteractionUserRanking(InteractionTypeEnum interactionType, 
                                                           LocalDateTime startTime, LocalDateTime endTime, 
                                                           Integer limit);
    
    /**
     * 清理过期的浏览记录
     */
    void cleanExpiredViewRecords(Integer retentionDays);
    
    /**
     * 同步互动统计数据到动态表
     */
    void syncInteractionStatistics(Long postId);
    
    /**
     * 批量同步互动统计数据
     */
    void batchSyncInteractionStatistics(List<Long> postIds);
    
    /**
     * 用户互动状态
     */
    class UserInteractionStatus {
        private Boolean isLiked;
        private Boolean isFavorited;
        private Boolean isShared;
        private LocalDateTime lastViewTime;
        
        // getters and setters
        public Boolean getIsLiked() { return isLiked; }
        public void setIsLiked(Boolean isLiked) { this.isLiked = isLiked; }
        public Boolean getIsFavorited() { return isFavorited; }
        public void setIsFavorited(Boolean isFavorited) { this.isFavorited = isFavorited; }
        public Boolean getIsShared() { return isShared; }
        public void setIsShared(Boolean isShared) { this.isShared = isShared; }
        public LocalDateTime getLastViewTime() { return lastViewTime; }
        public void setLastViewTime(LocalDateTime lastViewTime) { this.lastViewTime = lastViewTime; }
    }
    
    /**
     * 用户互动统计
     */
    class UserInteractionStatistics {
        private Long userId;
        private Long totalLikes;
        private Long totalFavorites;
        private Long totalShares;
        private Long totalViews;
        private Long uniquePostsInteracted;
        private Double avgInteractionsPerDay;
        
        // getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Long getTotalLikes() { return totalLikes; }
        public void setTotalLikes(Long totalLikes) { this.totalLikes = totalLikes; }
        public Long getTotalFavorites() { return totalFavorites; }
        public void setTotalFavorites(Long totalFavorites) { this.totalFavorites = totalFavorites; }
        public Long getTotalShares() { return totalShares; }
        public void setTotalShares(Long totalShares) { this.totalShares = totalShares; }
        public Long getTotalViews() { return totalViews; }
        public void setTotalViews(Long totalViews) { this.totalViews = totalViews; }
        public Long getUniquePostsInteracted() { return uniquePostsInteracted; }
        public void setUniquePostsInteracted(Long uniquePostsInteracted) { this.uniquePostsInteracted = uniquePostsInteracted; }
        public Double getAvgInteractionsPerDay() { return avgInteractionsPerDay; }
        public void setAvgInteractionsPerDay(Double avgInteractionsPerDay) { this.avgInteractionsPerDay = avgInteractionsPerDay; }
    }
    
    /**
     * 动态互动统计
     */
    class PostInteractionStatistics {
        private Long postId;
        private Long likeCount;
        private Long favoriteCount;
        private Long shareCount;
        private Long viewCount;
        private Long uniqueInteractors;
        private Double engagementRate;
        
        // getters and setters
        public Long getPostId() { return postId; }
        public void setPostId(Long postId) { this.postId = postId; }
        public Long getLikeCount() { return likeCount; }
        public void setLikeCount(Long likeCount) { this.likeCount = likeCount; }
        public Long getFavoriteCount() { return favoriteCount; }
        public void setFavoriteCount(Long favoriteCount) { this.favoriteCount = favoriteCount; }
        public Long getShareCount() { return shareCount; }
        public void setShareCount(Long shareCount) { this.shareCount = shareCount; }
        public Long getViewCount() { return viewCount; }
        public void setViewCount(Long viewCount) { this.viewCount = viewCount; }
        public Long getUniqueInteractors() { return uniqueInteractors; }
        public void setUniqueInteractors(Long uniqueInteractors) { this.uniqueInteractors = uniqueInteractors; }
        public Double getEngagementRate() { return engagementRate; }
        public void setEngagementRate(Double engagementRate) { this.engagementRate = engagementRate; }
    }
    
    /**
     * 用户互动排行
     */
    class UserInteractionRanking {
        private Long userId;
        private String username;
        private String nickname;
        private String avatar;
        private Long interactionCount;
        private Integer rank;
        
        // getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        public Long getInteractionCount() { return interactionCount; }
        public void setInteractionCount(Long interactionCount) { this.interactionCount = interactionCount; }
        public Integer getRank() { return rank; }
        public void setRank(Integer rank) { this.rank = rank; }
    }
} 