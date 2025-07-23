package com.gig.collide.api.like.service;

import com.gig.collide.api.like.request.LikeQueryRequest;
import com.gig.collide.api.like.request.LikeRequest;
import com.gig.collide.api.like.response.LikeQueryResponse;
import com.gig.collide.api.like.response.LikeResponse;

/**
 * 点赞服务门面接口
 * 
 * @author Collide
 * @since 1.0.0
 */
public interface LikeFacadeService {
    
    /**
     * 点赞/取消点赞/点踩操作
     * 
     * @param likeRequest 点赞请求
     * @return 点赞响应
     */
    LikeResponse likeAction(LikeRequest likeRequest);
    
    /**
     * 批量点赞操作
     * 
     * @param likeRequests 批量点赞请求
     * @return 点赞响应
     */
    LikeResponse batchLikeAction(java.util.List<LikeRequest> likeRequests);
    
    /**
     * 查询点赞记录
     * 
     * @param queryRequest 查询请求
     * @return 点赞查询响应
     */
    LikeQueryResponse queryLikes(LikeQueryRequest queryRequest);
    
    /**
     * 查询用户是否点赞某个对象
     * 
     * @param userId 用户ID
     * @param targetId 目标对象ID
     * @param likeType 点赞类型
     * @return 点赞状态响应
     */
    LikeResponse checkUserLikeStatus(Long userId, Long targetId, String likeType);
    
    /**
     * 获取对象的点赞统计
     * 
     * @param targetId 目标对象ID
     * @param likeType 点赞类型
     * @return 点赞统计响应
     */
    LikeResponse getLikeStatistics(Long targetId, String likeType);
    
    /**
     * 获取用户的点赞历史
     * 
     * @param queryRequest 查询请求
     * @return 点赞查询响应
     */
    LikeQueryResponse getUserLikeHistory(LikeQueryRequest queryRequest);
} 