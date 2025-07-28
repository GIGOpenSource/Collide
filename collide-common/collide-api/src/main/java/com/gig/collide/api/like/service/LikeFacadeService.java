package com.gig.collide.api.like.service;

import com.gig.collide.api.like.request.*;
import com.gig.collide.api.like.response.*;
import com.gig.collide.api.like.response.data.LikeInfo;
import com.gig.collide.base.response.PageResponse;

/**
 * 点赞门面服务接口
 * 提供点赞核心业务功能
 * 参考SQL设计，实现去连表化的高性能点赞系统
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public interface LikeFacadeService {

    /**
     * 执行点赞操作
     * 
     * @param request 点赞请求
     * @return 点赞响应
     */
    LikeActionResponse performLike(LikeActionRequest request);

    /**
     * 取消点赞操作
     * 
     * @param request 取消点赞请求
     * @return 取消点赞响应
     */
    LikeCancelResponse cancelLike(LikeCancelRequest request);

    /**
     * 切换点赞状态（如果已点赞则取消，如果未点赞则点赞）
     * 
     * @param request 切换点赞请求
     * @return 切换点赞响应
     */
    LikeToggleResponse toggleLike(LikeToggleRequest request);

    /**
     * 查询点赞记录
     * 
     * @param request 查询请求
     * @return 查询响应
     */
    LikeQueryResponse queryLikes(LikeQueryRequest request);

    /**
     * 分页查询点赞记录
     * 
     * @param request 分页查询请求
     * @return 分页响应
     */
    PageResponse<LikeInfo> pageQueryLikes(LikeQueryRequest request);

    /**
     * 检查用户是否对目标对象点过赞
     * 
     * @param request 检查请求
     * @return 检查响应
     */
    LikeCheckResponse checkUserLike(LikeCheckRequest request);

    /**
     * 批量检查用户对多个目标的点赞状态
     * 
     * @param request 批量检查请求
     * @return 批量检查响应
     */
    LikeBatchCheckResponse batchCheckUserLikes(LikeBatchCheckRequest request);

    /**
     * 获取用户的点赞历史
     * 
     * @param request 用户点赞历史请求
     * @return 用户点赞历史响应
     */
    LikeUserHistoryResponse getUserLikeHistory(LikeUserHistoryRequest request);

    /**
     * 获取目标对象的点赞详情
     * 
     * @param request 目标点赞详情请求
     * @return 目标点赞详情响应
     */
    LikeTargetDetailResponse getTargetLikeDetail(LikeTargetDetailRequest request);
} 