package com.gig.collide.api.like;

import com.gig.collide.api.like.request.LikeRequest;
import com.gig.collide.api.like.request.LikeQueryRequest;
import com.gig.collide.api.like.request.LikeToggleRequest;
import com.gig.collide.api.like.request.LikeCancelRequest;
import com.gig.collide.api.like.response.LikeResponse;
import com.gig.collide.web.vo.Result;
import com.gig.collide.base.response.PageResponse;

/**
 * 点赞门面服务接口 - 简洁版
 * 基于like-simple.sql的单表设计，实现核心点赞功能
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
public interface LikeFacadeService {
    
    /**
     * 点赞操作
     * 支持内容、评论、动态的点赞，包含目标对象和用户信息冗余
     * 
     * @param request 点赞请求
     * @return 点赞结果
     */
    Result<LikeResponse> addLike(LikeRequest request);
    
    /**
     * 取消点赞
     * 将点赞状态更新为cancelled
     * 
     * @param request 取消点赞请求
     * @return 取消结果
     */
    Result<Void> cancelLike(LikeCancelRequest request);
    
    /**
     * 切换点赞状态
     * 如果已点赞则取消，如果未点赞则添加
     * 
     * @param request 切换请求
     * @return 切换结果
     */
    Result<LikeResponse> toggleLike(LikeToggleRequest request);
    
    /**
     * 检查点赞状态
     * 查询用户是否已对目标对象点赞
     * 
     * @param userId 用户ID
     * @param likeType 点赞类型
     * @param targetId 目标ID
     * @return 点赞状态
     */
    Result<Boolean> checkLikeStatus(Long userId, String likeType, Long targetId);
    
    /**
     * 分页查询点赞记录
     * 支持按用户、目标类型、目标对象等条件查询
     * 
     * @param request 查询请求
     * @return 点赞记录列表
     */
    Result<PageResponse<LikeResponse>> queryLikes(LikeQueryRequest request);
    
    /**
     * 获取目标对象的点赞数量
     * 统计某个对象的总点赞数
     * 
     * @param likeType 点赞类型
     * @param targetId 目标ID
     * @return 点赞数量
     */
    Result<Long> getLikeCount(String likeType, Long targetId);
    
    /**
     * 获取用户的点赞数量
     * 统计用户的总点赞数（按类型）
     * 
     * @param userId 用户ID
     * @param likeType 点赞类型（可选）
     * @return 点赞数量
     */
    Result<Long> getUserLikeCount(Long userId, String likeType);
    
    /**
     * 批量检查点赞状态
     * 检查用户对多个目标对象的点赞状态
     * 
     * @param userId 用户ID
     * @param likeType 点赞类型
     * @param targetIds 目标ID列表
     * @return 点赞状态Map (targetId -> isLiked)
     */
    Result<java.util.Map<Long, Boolean>> batchCheckLikeStatus(Long userId, String likeType, java.util.List<Long> targetIds);
} 