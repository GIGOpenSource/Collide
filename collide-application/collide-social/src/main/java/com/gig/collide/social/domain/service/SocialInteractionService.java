package com.gig.collide.social.domain.service;

import com.gig.collide.api.social.request.SocialInteractionRequest;
import com.gig.collide.api.social.request.SocialInteractionQueryRequest;
import com.gig.collide.api.social.response.data.SocialInteractionInfo;
import com.gig.collide.social.domain.entity.SocialPostInteraction;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 社交互动领域服务接口
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface SocialInteractionService {

    /**
     * 执行互动操作
     */
    SocialPostInteraction executeInteraction(SocialInteractionRequest request);

    /**
     * 取消互动操作
     */
    boolean cancelInteraction(Long postId, Long userId, String interactionType);

    /**
     * 查询用户对动态的互动状态
     */
    SocialPostInteraction getUserInteraction(Long postId, Long userId, String interactionType);

    /**
     * 分页查询动态的互动记录
     */
    IPage<SocialInteractionInfo> getPostInteractions(SocialInteractionQueryRequest request);

    /**
     * 分页查询用户的互动历史
     */
    IPage<SocialInteractionInfo> getUserInteractionHistory(SocialInteractionQueryRequest request);

    /**
     * 批量查询用户对多个动态的互动状态
     */
    List<SocialPostInteraction> batchGetUserInteractions(List<Long> postIds, Long userId, String interactionType);

    /**
     * 统计动态的互动数量
     */
    Long countPostInteractions(Long postId, String interactionType);

    /**
     * 统计用户的互动数量
     */
    Long countUserInteractions(Long userId, String interactionType, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取动态的点赞用户列表
     */
    IPage<SocialInteractionInfo> getPostLikeUsers(Long postId, Integer pageNum, Integer pageSize);

    /**
     * 获取动态的收藏用户列表
     */
    IPage<SocialInteractionInfo> getPostFavoriteUsers(Long postId, Integer pageNum, Integer pageSize);

    /**
     * 获取动态的转发用户列表
     */
    IPage<SocialInteractionInfo> getPostShareUsers(Long postId, Integer pageNum, Integer pageSize);

    /**
     * 获取用户收到的互动通知
     */
    IPage<SocialInteractionInfo> getReceivedInteractions(Long authorId, String interactionType, 
                                                         Integer pageNum, Integer pageSize);

    /**
     * 批量更新用户信息
     */
    void batchUpdateUserInfo(Long userId, String userNickname, String userAvatar);

    /**
     * 清理过期的浏览记录
     */
    void cleanExpiredViewRecords(Integer daysBack);

    /**
     * 检查互动权限
     */
    boolean checkInteractionPermission(Long postId, Long userId, String interactionType);

    /**
     * 检查是否已互动
     */
    boolean hasInteracted(Long postId, Long userId, String interactionType);

    /**
     * 获取热门互动用户排行
     */
    List<Object> getTopInteractionUsers(LocalDateTime startTime, LocalDateTime endTime, Integer limit);
} 