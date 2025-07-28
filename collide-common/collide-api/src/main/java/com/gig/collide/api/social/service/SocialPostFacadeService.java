package com.gig.collide.api.social.service;

import com.gig.collide.api.social.request.SocialPostCreateRequest;
import com.gig.collide.api.social.request.SocialPostQueryRequest;
import com.gig.collide.api.social.request.SocialPostUpdateRequest;
import com.gig.collide.api.social.response.SocialPostOperationResponse;
import com.gig.collide.api.social.response.SocialPostQueryResponse;
import com.gig.collide.api.social.response.data.BasicSocialPostInfo;
import com.gig.collide.api.social.response.data.SocialPostInfo;
import com.gig.collide.api.social.constant.PostTypeEnum;
import com.gig.collide.api.social.constant.VisibilityEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 社交动态门面服务接口
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
public interface SocialPostFacadeService {
    
    /**
     * 创建动态
     */
    SocialPostOperationResponse createPost(SocialPostCreateRequest request);
    
    /**
     * 更新动态
     */
    SocialPostOperationResponse updatePost(SocialPostUpdateRequest request);
    
    /**
     * 删除动态
     */
    SocialPostOperationResponse deletePost(Long postId, Long operatorUserId, Integer version);
    
    /**
     * 发布动态（草稿转发布）
     */
    SocialPostOperationResponse publishPost(Long postId, Long operatorUserId, Integer version);
    
    /**
     * 查询动态详情
     */
    SocialPostQueryResponse<SocialPostInfo> getPostDetail(Long postId, Long viewerUserId);
    
    /**
     * 分页查询动态
     */
    SocialPostQueryResponse<List<BasicSocialPostInfo>> queryPosts(SocialPostQueryRequest request);
    
    /**
     * 获取用户动态列表
     */
    SocialPostQueryResponse<List<BasicSocialPostInfo>> getUserPosts(Long authorId, Long viewerUserId, 
                                                                    Integer pageNum, Integer pageSize);
    
    /**
     * 获取关注用户动态时间线
     */
    SocialPostQueryResponse<List<BasicSocialPostInfo>> getFollowingTimeline(Long userId, 
                                                                            Integer pageNum, Integer pageSize);
    
    /**
     * 获取推荐动态（热门、个性化推荐）
     */
    SocialPostQueryResponse<List<BasicSocialPostInfo>> getRecommendedPosts(Long userId, 
                                                                           Integer pageNum, Integer pageSize);
    
    /**
     * 获取热门动态
     */
    SocialPostQueryResponse<List<BasicSocialPostInfo>> getHotPosts(Integer pageNum, Integer pageSize);
    
    /**
     * 获取最新动态
     */
    SocialPostQueryResponse<List<BasicSocialPostInfo>> getLatestPosts(Integer pageNum, Integer pageSize);
    
    /**
     * 根据话题搜索动态
     */
    SocialPostQueryResponse<List<BasicSocialPostInfo>> searchPostsByTopic(String topic, 
                                                                          Integer pageNum, Integer pageSize);
    
    /**
     * 根据内容关键词搜索动态
     */
    SocialPostQueryResponse<List<BasicSocialPostInfo>> searchPostsByKeyword(String keyword, 
                                                                           Integer pageNum, Integer pageSize);
    
    /**
     * 根据位置搜索动态
     */
    SocialPostQueryResponse<List<BasicSocialPostInfo>> searchPostsByLocation(BigDecimal latitude, 
                                                                             BigDecimal longitude, 
                                                                             Double radiusKm,
                                                                             Integer pageNum, Integer pageSize);
    
    /**
     * 获取动态类型统计
     */
    SocialPostQueryResponse<List<PostTypeStats>> getPostTypeStatistics(Long authorId, 
                                                                       LocalDateTime startTime, 
                                                                       LocalDateTime endTime);
    
    /**
     * 批量获取动态详情
     */
    SocialPostQueryResponse<List<SocialPostInfo>> batchGetPosts(List<Long> postIds, Long viewerUserId);
    
    /**
     * 获取草稿动态列表
     */
    SocialPostQueryResponse<List<BasicSocialPostInfo>> getDraftPosts(Long authorId, 
                                                                     Integer pageNum, Integer pageSize);
    
    /**
     * 更新动态热度分数
     */
    void updateHotScore(Long postId);
    
    /**
     * 批量更新用户信息（当用户信息变更时同步更新动态中的冗余数据）
     */
    void batchUpdateUserInfo(Long userId, String username, String nickname, String avatar, Boolean verified);
    
    /**
     * 获取用户发布能力检查
     */
    PostPublishCapability checkPostPublishCapability(Long userId, PostTypeEnum postType);
    
    /**
     * 动态类型统计信息
     */
    class PostTypeStats {
        private PostTypeEnum postType;
        private Long count;
        private Long totalLikes;
        private Long totalShares;
        private BigDecimal avgHotScore;
        
        // getters and setters
        public PostTypeEnum getPostType() { return postType; }
        public void setPostType(PostTypeEnum postType) { this.postType = postType; }
        public Long getCount() { return count; }
        public void setCount(Long count) { this.count = count; }
        public Long getTotalLikes() { return totalLikes; }
        public void setTotalLikes(Long totalLikes) { this.totalLikes = totalLikes; }
        public Long getTotalShares() { return totalShares; }
        public void setTotalShares(Long totalShares) { this.totalShares = totalShares; }
        public BigDecimal getAvgHotScore() { return avgHotScore; }
        public void setAvgHotScore(BigDecimal avgHotScore) { this.avgHotScore = avgHotScore; }
    }
    
    /**
     * 动态发布能力检查结果
     */
    class PostPublishCapability {
        private Boolean canPublish;
        private String reason;
        private Integer dailyLimit;
        private Integer dailyPublished;
        private Integer remainingQuota;
        
        // getters and setters
        public Boolean getCanPublish() { return canPublish; }
        public void setCanPublish(Boolean canPublish) { this.canPublish = canPublish; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public Integer getDailyLimit() { return dailyLimit; }
        public void setDailyLimit(Integer dailyLimit) { this.dailyLimit = dailyLimit; }
        public Integer getDailyPublished() { return dailyPublished; }
        public void setDailyPublished(Integer dailyPublished) { this.dailyPublished = dailyPublished; }
        public Integer getRemainingQuota() { return remainingQuota; }
        public void setRemainingQuota(Integer remainingQuota) { this.remainingQuota = remainingQuota; }
    }
} 