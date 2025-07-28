package com.gig.collide.api.follow;

import com.gig.collide.api.follow.request.FollowRequest;
import com.gig.collide.api.follow.response.FollowResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

/**
 * 关注管理门面服务接口 - 简洁版
 * 基于简洁版SQL设计，保留核心功能
 *
 * @author GIG Team
 * @version 2.0.0
 */
public interface FollowFacadeService {

    /**
     * 关注用户
     */
    Result<Void> follow(FollowRequest request);

    /**
     * 取消关注
     */
    Result<Void> unfollow(Long followerId, Long followeeId);

    /**
     * 检查是否已关注
     */
    Result<Boolean> isFollowing(Long followerId, Long followeeId);

    /**
     * 获取关注列表（我关注的人）
     */
    Result<PageResponse<FollowResponse>> getFollowingList(Long userId, Integer page, Integer size);

    /**
     * 获取粉丝列表（关注我的人）
     */
    Result<PageResponse<FollowResponse>> getFollowerList(Long userId, Integer page, Integer size);

    /**
     * 统计关注数
     */
    Result<Long> countFollowing(Long userId);

    /**
     * 统计粉丝数
     */
    Result<Long> countFollowers(Long userId);
} 