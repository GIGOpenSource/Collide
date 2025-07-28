package com.gig.collide.api.user.service;

import com.gig.collide.api.user.request.*;
import com.gig.collide.api.user.response.*;
import com.gig.collide.api.user.response.data.UserInviteRelationInfo;
import com.gig.collide.api.user.response.data.UserInviteStatisticsInfo;
import com.gig.collide.api.user.response.data.UserInviteRankingInfo;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.base.response.BaseResponse;

/**
 * 用户邀请关系门面服务接口
 * 提供用户邀请关系管理功能
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public interface UserInviteRelationFacadeService {

    /**
     * 创建邀请关系
     * 
     * @param createRequest 创建请求
     * @return 创建响应
     */
    UserInviteRelationCreateResponse createInviteRelation(UserInviteRelationCreateRequest createRequest);

    /**
     * 查询邀请关系
     * 
     * @param queryRequest 查询请求
     * @return 查询响应
     */
    UserInviteRelationQueryResponse<UserInviteRelationInfo> queryInviteRelations(UserInviteRelationQueryRequest queryRequest);

    /**
     * 分页查询邀请关系
     * 
     * @param queryRequest 分页查询请求
     * @return 分页响应
     */
    PageResponse<UserInviteRelationInfo> pageQueryInviteRelations(UserInviteRelationQueryRequest queryRequest);

    /**
     * 获取邀请关系统计信息
     * 
     * @param statisticsRequest 统计请求
     * @return 统计响应
     */
    UserInviteRelationStatisticsResponse getInviteStatistics(UserInviteRelationStatisticsRequest statisticsRequest);

    /**
     * 获取用户的邀请关系（我邀请的人）
     * 
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 分页响应
     */
    PageResponse<UserInviteRelationInfo> getMyInvitees(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取用户的邀请关系（邀请我的人）
     * 
     * @param userId 用户ID
     * @return 邀请关系信息
     */
    UserInviteRelationQueryResponse<UserInviteRelationInfo> getMyInviter(Long userId);

    /**
     * 获取邀请排行榜
     * 
     * @param limit 排行数量
     * @param timeRange 时间范围（today、week、month、all）
     * @return 排行榜列表
     */
    UserInviteRelationQueryResponse<UserInviteRankingInfo[]> getInviteRanking(Integer limit, String timeRange);

    /**
     * 验证邀请码是否有效
     * 
     * @param inviteCode 邀请码
     * @return 是否有效
     */
    Boolean validateInviteCode(String inviteCode);

    /**
     * 根据邀请码获取邀请人信息
     * 
     * @param inviteCode 邀请码
     * @return 邀请人信息
     */
    UserInviteRelationQueryResponse<UserInviteRelationInfo> getInviterByCode(String inviteCode);

    /**
     * 获取用户的邀请链（从根节点到当前用户）
     * 
     * @param userId 用户ID
     * @return 邀请链
     */
    UserInviteRelationQueryResponse<UserInviteRelationInfo[]> getInviteChain(Long userId);

    /**
     * 获取用户的邀请树（用户及其所有下级）
     * 
     * @param userId 用户ID
     * @param maxLevel 最大层级
     * @return 邀请树
     */
    UserInviteRelationQueryResponse<UserInviteRelationInfo[]> getInviteTree(Long userId, Integer maxLevel);

    /**
     * 更新邀请关系状态
     * 
     * @param relationId 关系ID
     * @param status 新状态
     * @param operatorId 操作员ID
     * @return 操作结果
     */
    BaseResponse updateInviteStatus(Long relationId, String status, Long operatorId);

    /**
     * 获取今日邀请统计
     * 
     * @return 今日统计
     */
    UserInviteRelationStatisticsResponse getTodayInviteStatistics();

    /**
     * 获取本周邀请统计
     * 
     * @return 本周统计
     */
    UserInviteRelationStatisticsResponse getThisWeekInviteStatistics();

    /**
     * 获取本月邀请统计
     * 
     * @return 本月统计
     */
    UserInviteRelationStatisticsResponse getThisMonthInviteStatistics();

    /**
     * 计算邀请奖励积分
     * 
     * @param inviterId 邀请人ID
     * @param inviteLevel 邀请层级
     * @return 奖励积分
     */
    Integer calculateInviteReward(Long inviterId, Integer inviteLevel);

    /**
     * 批量处理邀请关系（激活、失效等）
     * 
     * @param relationIds 关系ID列表
     * @param operation 操作类型
     * @param operatorId 操作员ID
     * @return 操作结果
     */
    BaseResponse batchProcessInviteRelations(Long[] relationIds, String operation, Long operatorId);
} 