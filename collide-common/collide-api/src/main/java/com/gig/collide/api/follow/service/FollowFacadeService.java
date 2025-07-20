package com.gig.collide.api.follow.service;

import com.gig.collide.api.follow.request.*;
import com.gig.collide.api.follow.response.FollowOperatorResponse;
import com.gig.collide.api.follow.response.FollowQueryResponse;
import com.gig.collide.api.follow.response.FollowStatusResponse;
import com.gig.collide.api.follow.response.data.FollowInfo;
import com.gig.collide.api.follow.response.data.FollowerInfo;
import com.gig.collide.base.response.PageResponse;

/**
 * 关注服务门面接口
 * @author GIG
 */
public interface FollowFacadeService {

    /**
     * 关注用户
     * @param followRequest 关注请求
     * @return 操作结果
     */
    FollowOperatorResponse follow(FollowRequest followRequest);

    /**
     * 取消关注用户
     * @param unfollowRequest 取消关注请求
     * @return 操作结果
     */
    FollowOperatorResponse unfollow(UnfollowRequest unfollowRequest);

    /**
     * 查询关注状态
     * @param followStatusRequest 关注状态查询请求
     * @return 关注状态
     */
    FollowStatusResponse queryFollowStatus(FollowStatusRequest followStatusRequest);

    /**
     * 分页查询关注列表
     * @param followListRequest 关注列表查询请求
     * @return 关注列表
     */
    PageResponse<FollowInfo> queryFollowList(FollowListRequest followListRequest);

    /**
     * 分页查询粉丝列表
     * @param followerListRequest 粉丝列表查询请求
     * @return 粉丝列表
     */
    PageResponse<FollowerInfo> queryFollowerList(FollowerListRequest followerListRequest);

    /**
     * 查询关注统计信息
     * @param followStatisticsRequest 关注统计请求
     * @return 统计信息
     */
    FollowQueryResponse<Integer> queryFollowCount(FollowStatisticsRequest followStatisticsRequest);

    /**
     * 查询粉丝统计信息
     * @param followerStatisticsRequest 粉丝统计请求
     * @return 统计信息
     */
    FollowQueryResponse<Integer> queryFollowerCount(FollowerStatisticsRequest followerStatisticsRequest);
} 