package com.gig.collide.api.follow.service;

import com.gig.collide.api.follow.request.FollowRequest;
import com.gig.collide.api.follow.request.FollowQueryRequest;
import com.gig.collide.api.follow.request.UnfollowRequest;
import com.gig.collide.api.follow.response.FollowResponse;
import com.gig.collide.api.follow.response.FollowQueryResponse;
import com.gig.collide.api.follow.response.data.FollowInfo;
import com.gig.collide.api.follow.response.data.FollowStatistics;
import com.gig.collide.base.response.PageResponse;

/**
 * 关注服务 Facade 接口
 * 基于 t_follow 和 t_follow_statistics 表设计
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface FollowFacadeService {

    /**
     * 关注用户
     *
     * @param followRequest 关注请求
     * @return 关注响应
     */
    FollowResponse follow(FollowRequest followRequest);

    /**
     * 取消关注用户
     *
     * @param unfollowRequest 取消关注请求
     * @return 关注响应
     */
    FollowResponse unfollow(UnfollowRequest unfollowRequest);

    /**
     * 查询关注关系
     *
     * @param queryRequest 查询请求
     * @return 关注关系响应
     */
    FollowQueryResponse<FollowInfo> queryFollow(FollowQueryRequest queryRequest);

    /**
     * 分页查询关注列表
     *
     * @param queryRequest 分页查询请求
     * @return 关注列表响应
     */
    PageResponse<FollowInfo> pageQueryFollowing(FollowQueryRequest queryRequest);

    /**
     * 分页查询粉丝列表
     *
     * @param queryRequest 分页查询请求
     * @return 粉丝列表响应
     */
    PageResponse<FollowInfo> pageQueryFollowers(FollowQueryRequest queryRequest);

    /**
     * 获取用户关注统计
     *
     * @param userId 用户ID
     * @return 关注统计信息
     */
    FollowQueryResponse<FollowStatistics> getFollowStatistics(Long userId);

    /**
     * 批量获取关注统计
     *
     * @param queryRequest 批量查询请求
     * @return 关注统计列表
     */
    FollowQueryResponse<java.util.List<FollowStatistics>> batchGetFollowStatistics(FollowQueryRequest queryRequest);
} 