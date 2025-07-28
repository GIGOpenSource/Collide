package com.gig.collide.social.domain.service;

import com.gig.collide.api.social.request.SocialPostCreateRequest;
import com.gig.collide.api.social.request.SocialPostUpdateRequest;
import com.gig.collide.api.social.request.SocialPostQueryRequest;
import com.gig.collide.api.social.response.data.SocialPostInfo;
import com.gig.collide.api.social.response.data.BasicSocialPostInfo;
import com.gig.collide.social.domain.entity.SocialPost;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 社交动态领域服务接口
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface SocialPostService {

    /**
     * 创建动态
     */
    SocialPost createPost(SocialPostCreateRequest request);

    /**
     * 更新动态
     */
    SocialPost updatePost(SocialPostUpdateRequest request);

    /**
     * 发布动态(草稿转发布)
     */
    SocialPost publishPost(Long postId, Long operatorUserId, Integer version);

    /**
     * 删除动态
     */
    boolean deletePost(Long postId, Long operatorUserId, Integer version);

    /**
     * 根据ID查询动态
     */
    SocialPost findById(Long postId);

    /**
     * 查询动态详情(包含浏览统计)
     */
    SocialPostInfo getPostDetail(Long postId, Long viewerUserId);

    /**
     * 分页查询动态
     */
    IPage<BasicSocialPostInfo> queryPosts(SocialPostQueryRequest request);

    /**
     * 获取用户动态列表
     */
    IPage<BasicSocialPostInfo> getUserPosts(Long authorId, Long viewerUserId, Integer pageNum, Integer pageSize);

    /**
     * 获取关注用户动态时间线
     */
    IPage<BasicSocialPostInfo> getFollowingTimeline(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取推荐动态
     */
    IPage<BasicSocialPostInfo> getRecommendedPosts(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取热门动态
     */
    IPage<BasicSocialPostInfo> getHotPosts(Integer pageNum, Integer pageSize);

    /**
     * 获取最新动态
     */
    IPage<BasicSocialPostInfo> getLatestPosts(Integer pageNum, Integer pageSize);

    /**
     * 根据话题搜索动态
     */
    IPage<BasicSocialPostInfo> searchPostsByTopic(String topic, Integer pageNum, Integer pageSize);

    /**
     * 根据关键词搜索动态
     */
    IPage<BasicSocialPostInfo> searchPostsByKeyword(String keyword, Integer pageNum, Integer pageSize);

    /**
     * 根据位置搜索动态
     */
    IPage<BasicSocialPostInfo> searchPostsByLocation(BigDecimal latitude, BigDecimal longitude, 
                                                     Double radiusKm, Integer pageNum, Integer pageSize);

    /**
     * 批量获取动态详情
     */
    List<SocialPostInfo> batchGetPosts(List<Long> postIds, Long viewerUserId);

    /**
     * 获取草稿动态列表
     */
    IPage<BasicSocialPostInfo> getDraftPosts(Long authorId, Integer pageNum, Integer pageSize);

    /**
     * 更新动态热度分数
     */
    void updateHotScore(Long postId);

    /**
     * 批量更新用户信息
     */
    void batchUpdateUserInfo(Long userId, String username, String nickname, String avatar, Boolean verified);

    /**
     * 增加浏览量
     */
    void incrementViewCount(Long postId, Long viewerUserId);

    /**
     * 增加点赞数
     */
    void incrementLikeCount(Long postId, Integer increment);

    /**
     * 增加评论数
     */
    void incrementCommentCount(Long postId, Integer increment);

    /**
     * 增加转发数
     */
    void incrementShareCount(Long postId, Integer increment);

    /**
     * 增加收藏数
     */
    void incrementFavoriteCount(Long postId, Integer increment);

    /**
     * 校验动态发布权限
     */
    boolean checkPublishPermission(Long userId, String postType);

    /**
     * 获取用户今日发布动态数量
     */
    Long getUserTodayPostCount(Long userId);

    /**
     * 统计用户动态数量
     */
    Long countUserPosts(Long authorId, String status, LocalDateTime startTime, LocalDateTime endTime);
} 