package com.gig.collide.users.domain.service;

import com.gig.collide.base.response.PageResponse;
import com.gig.collide.users.domain.entity.UserStats;
import com.gig.collide.api.user.request.users.stats.UserStatsQueryRequest;

import java.util.List;

/**
 * 用户统计领域服务接口 - 对应 t_user_stats 表
 * 负责用户统计数据管理
 * 
 * @author GIG Team
 * @version 2.0.0
 */
public interface UserStatsService {

    /**
     * 创建用户统计数据
     */
    UserStats createStats(UserStats userStats);

    /**
     * 更新用户统计数据
     */
    UserStats updateStats(UserStats userStats);

    /**
     * 根据用户ID查询统计数据
     */
    UserStats getStatsByUserId(Long userId);

    /**
     * 批量查询用户统计数据
     */
    List<UserStats> batchGetStats(List<Long> userIds);

    /**
     * 增加粉丝数
     */
    boolean incrementFollowerCount(Long userId, Integer increment);

    /**
     * 增加关注数
     */
    boolean incrementFollowingCount(Long userId, Integer increment);

    /**
     * 增加内容数
     */
    boolean incrementContentCount(Long userId, Integer increment);

    /**
     * 增加点赞数
     */
    boolean incrementLikeCount(Long userId, Integer increment);

    /**
     * 增加登录次数
     */
    boolean incrementLoginCount(Long userId);

    /**
     * 批量更新统计数据
     */
    boolean batchUpdateStats(List<UserStats> statsList);

    /**
     * 重置用户统计数据
     */
    boolean resetUserStats(Long userId);

    /**
     * 查询粉丝数排行榜
     */
    List<UserStats> getTopFollowerUsers(Integer limit);

    /**
     * 查询最活跃用户排行榜
     */
    List<UserStats> getMostActiveUsers(Integer limit);

    /**
     * 查询内容数排行榜
     */
    List<UserStats> getTopContentUsers(Integer limit);

    /**
     * 分页查询用户统计数据
     */
    PageResponse<UserStats> queryStats(UserStatsQueryRequest request);

    /**
     * 计算用户活跃度分数
     */
    double calculateActivityScore(Long userId);

    /**
     * 计算用户影响力分数
     */
    double calculateInfluenceScore(Long userId);

    /**
     * 检查是否为活跃用户
     */
    boolean isActiveUser(Long userId);

    /**
     * 检查是否为知名用户
     */
    boolean isInfluentialUser(Long userId);

    /**
     * 批量初始化用户统计数据
     */
    List<UserStats> batchInitializeStats(List<Long> userIds);

    /**
     * 删除用户统计数据
     */
    boolean deleteStats(Long userId);

    /**
     * 同步统计数据（定时任务调用）
     */
    boolean syncUserStats(Long userId);

    /**
     * 批量同步统计数据
     */
    boolean batchSyncStats(List<Long> userIds);
}