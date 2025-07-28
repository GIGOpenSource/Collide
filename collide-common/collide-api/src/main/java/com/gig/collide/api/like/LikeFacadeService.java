package com.gig.collide.api.like;

import com.gig.collide.api.like.request.LikeRequest;
import com.gig.collide.api.like.response.LikeResponse;
import com.gig.collide.web.vo.Result;

import java.util.List;

/**
 * 点赞管理门面服务接口 - 简洁版
 * 基于简洁版SQL设计，保留核心功能
 *
 * @author GIG Team
 * @version 2.0.0
 */
public interface LikeFacadeService {

    /**
     * 点赞
     */
    Result<Void> like(LikeRequest request);

    /**
     * 取消点赞
     */
    Result<Void> unlike(Long userId, String likeType, Long targetId);

    /**
     * 检查是否已点赞
     */
    Result<Boolean> isLiked(Long userId, String likeType, Long targetId);

    /**
     * 获取用户点赞列表
     */
    Result<List<LikeResponse>> getUserLikes(Long userId, String likeType, Integer limit);

    /**
     * 获取目标对象的点赞列表
     */
    Result<List<LikeResponse>> getTargetLikes(Long targetId, String likeType, Integer limit);

    /**
     * 统计目标对象的点赞数
     */
    Result<Long> countTargetLikes(Long targetId, String likeType);
} 