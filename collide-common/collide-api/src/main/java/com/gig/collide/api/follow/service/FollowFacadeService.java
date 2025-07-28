package com.gig.collide.api.follow.service;

import com.gig.collide.api.follow.request.*;
import com.gig.collide.api.follow.response.*;
import com.gig.collide.api.follow.response.data.BasicFollowInfo;
import com.gig.collide.api.follow.response.data.FollowInfo;
import com.gig.collide.base.response.PageResponse;

/**
 * 关注门面服务接口
 * 提供关注核心业务功能
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface FollowFacadeService {

    // ===================== 基础查询功能 =====================

    /**
     * 关注信息查询
     *
     * @param queryRequest 查询请求
     * @return 查询响应
     */
    FollowQueryResponse<FollowInfo> queryFollow(FollowQueryRequest queryRequest);

    /**
     * 基础关注信息查询（不包含敏感信息）
     *
     * @param queryRequest 查询请求
     * @return 基础关注信息响应
     */
    FollowQueryResponse<BasicFollowInfo> queryBasicFollow(FollowQueryRequest queryRequest);

    /**
     * 分页查询关注信息
     *
     * @param queryRequest 分页查询请求
     * @return 分页响应
     */
    PageResponse<FollowInfo> pageQueryFollows(FollowQueryRequest queryRequest);

    /**
     * 分页查询基础关注信息
     *
     * @param queryRequest 分页查询请求
     * @return 基础关注信息分页响应
     */
    PageResponse<BasicFollowInfo> pageQueryBasicFollows(FollowQueryRequest queryRequest);

    // ===================== 关注操作功能 =====================

    /**
     * 创建关注关系
     *
     * @param createRequest 创建请求
     * @return 创建响应
     */
    FollowCreateResponse createFollow(FollowCreateRequest createRequest);

    /**
     * 更新关注关系
     *
     * @param updateRequest 更新请求
     * @return 更新响应
     */
    FollowUpdateResponse updateFollow(FollowUpdateRequest updateRequest);

    /**
     * 删除关注关系
     *
     * @param deleteRequest 删除请求
     * @return 删除响应
     */
    FollowDeleteResponse deleteFollow(FollowDeleteRequest deleteRequest);

    // ===================== 批量操作功能 =====================

    /**
     * 批量操作关注关系
     *
     * @param batchRequest 批量操作请求
     * @return 批量操作响应
     */
    FollowBatchOperationResponse batchOperation(FollowBatchOperationRequest batchRequest);

    // ===================== 统计功能 =====================

    /**
     * 查询关注统计信息
     *
     * @param statisticsRequest 统计请求
     * @return 统计响应
     */
    FollowStatisticsResponse queryStatistics(FollowStatisticsRequest statisticsRequest);

    // ===================== 便捷查询方法 =====================

    /**
     * 查询用户关注列表
     *
     * @param followerUserId 关注者用户ID
     * @return 关注列表响应
     */
    FollowQueryResponse<BasicFollowInfo> queryUserFollowings(Long followerUserId);

    /**
     * 查询用户粉丝列表
     *
     * @param followedUserId 被关注者用户ID
     * @return 粉丝列表响应
     */
    FollowQueryResponse<BasicFollowInfo> queryUserFollowers(Long followedUserId);

    /**
     * 查询两用户关注关系
     *
     * @param followerUserId 关注者用户ID
     * @param followedUserId 被关注者用户ID
     * @return 关注关系响应
     */
    FollowQueryResponse<FollowInfo> queryFollowRelation(Long followerUserId, Long followedUserId);

    /**
     * 检查关注关系是否存在
     *
     * @param followerUserId 关注者用户ID
     * @param followedUserId 被关注者用户ID
     * @return 是否存在关注关系
     */
    Boolean checkFollowExists(Long followerUserId, Long followedUserId);

    /**
     * 获取用户关注统计
     *
     * @param userId 用户ID
     * @return 统计响应
     */
    FollowStatisticsResponse getUserStatistics(Long userId);

    // ===================== 快捷操作方法 =====================

    /**
     * 关注用户（普通关注）
     *
     * @param followerUserId 关注者用户ID
     * @param followedUserId 被关注者用户ID
     * @return 创建响应
     */
    FollowCreateResponse followUser(Long followerUserId, Long followedUserId);

    /**
     * 特别关注用户
     *
     * @param followerUserId 关注者用户ID
     * @param followedUserId 被关注者用户ID
     * @return 创建响应
     */
    FollowCreateResponse specialFollowUser(Long followerUserId, Long followedUserId);

    /**
     * 取消关注用户
     *
     * @param followerUserId 关注者用户ID
     * @param followedUserId 被关注者用户ID
     * @return 删除响应
     */
    FollowDeleteResponse unfollowUser(Long followerUserId, Long followedUserId);

    /**
     * 屏蔽关注关系
     *
     * @param followerUserId 关注者用户ID
     * @param followedUserId 被关注者用户ID
     * @return 更新响应
     */
    FollowUpdateResponse blockFollow(Long followerUserId, Long followedUserId);

    /**
     * 恢复关注关系
     *
     * @param followerUserId 关注者用户ID
     * @param followedUserId 被关注者用户ID
     * @return 更新响应
     */
    FollowUpdateResponse unblockFollow(Long followerUserId, Long followedUserId);

    /**
     * 设置特别关注
     *
     * @param followerUserId 关注者用户ID
     * @param followedUserId 被关注者用户ID
     * @return 更新响应
     */
    FollowUpdateResponse setSpecialFollow(Long followerUserId, Long followedUserId);

    /**
     * 取消特别关注
     *
     * @param followerUserId 关注者用户ID
     * @param followedUserId 被关注者用户ID
     * @return 更新响应
     */
    FollowUpdateResponse cancelSpecialFollow(Long followerUserId, Long followedUserId);
} 