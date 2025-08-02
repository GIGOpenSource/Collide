package com.gig.collide.api.user;

import com.gig.collide.api.user.request.users.stats.UserStatsCreateRequest;
import com.gig.collide.api.user.request.users.stats.UserStatsUpdateRequest;
import com.gig.collide.api.user.request.users.stats.UserStatsQueryRequest;
import com.gig.collide.api.user.response.users.stats.UserStatsResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

/**
 * 用户统计服务接口 - 对应 t_user_stats 表
 * 负责用户统计数据管理
 * 
 * @author GIG Team
 * @version 2.0.0
 */
public interface UserStatsFacadeService {

    /**
     * 创建用户统计记录
     * 
     * @param request 用户统计创建请求
     * @return 创建结果
     */
    Result<UserStatsResponse> createStats(UserStatsCreateRequest request);

    /**
     * 更新用户统计数据
     * 
     * @param request 用户统计更新请求
     * @return 更新结果
     */
    Result<UserStatsResponse> updateStats(UserStatsUpdateRequest request);

    /**
     * 根据用户ID查询统计数据
     * 
     * @param userId 用户ID
     * @return 用户统计数据
     */
    Result<UserStatsResponse> getStatsByUserId(Long userId);

    /**
     * 批量查询用户统计数据
     * 
     * @param userIds 用户ID列表
     * @return 用户统计数据列表
     */
    Result<java.util.List<UserStatsResponse>> batchGetStats(java.util.List<Long> userIds);

    /**
     * 增加粉丝数
     * 
     * @param userId 用户ID
     * @param increment 增量（可为负数）
     * @return 更新结果
     */
    Result<Void> incrementFollowerCount(Long userId, Integer increment);

    /**
     * 增加关注数
     * 
     * @param userId 用户ID
     * @param increment 增量（可为负数）
     * @return 更新结果
     */
    Result<Void> incrementFollowingCount(Long userId, Integer increment);

    /**
     * 增加内容数
     * 
     * @param userId 用户ID
     * @param increment 增量（可为负数）
     * @return 更新结果
     */
    Result<Void> incrementContentCount(Long userId, Integer increment);

    /**
     * 增加获得点赞数
     * 
     * @param userId 用户ID
     * @param increment 增量（可为负数）
     * @return 更新结果
     */
    Result<Void> incrementLikeCount(Long userId, Integer increment);

    /**
     * 增加登录次数
     * 
     * @param userId 用户ID
     * @param increment 增量（默认为1）
     * @return 更新结果
     */
    Result<Void> incrementLoginCount(Long userId, Integer increment);

    /**
     * 批量更新统计数据
     * 
     * @param userId 用户ID
     * @param followerIncrement 粉丝数增量
     * @param followingIncrement 关注数增量
     * @param contentIncrement 内容数增量
     * @param likeIncrement 点赞数增量
     * @return 更新结果
     */
    Result<Void> batchUpdateStats(Long userId, Integer followerIncrement, Integer followingIncrement, 
                                  Integer contentIncrement, Integer likeIncrement);

    /**
     * 重置用户统计数据
     * 
     * @param userId 用户ID
     * @return 重置结果
     */
    Result<Void> resetStats(Long userId);

    /**
     * 分页查询用户统计数据
     * 
     * @param request 查询请求
     * @return 分页统计数据列表
     */
    Result<PageResponse<UserStatsResponse>> queryStats(UserStatsQueryRequest request);

    /**
     * 获取粉丝数排行榜
     * 
     * @param limit 返回数量限制
     * @return 用户统计列表
     */
    Result<java.util.List<UserStatsResponse>> getFollowerRanking(Integer limit);

    /**
     * 获取内容数排行榜
     * 
     * @param limit 返回数量限制
     * @return 用户统计列表
     */
    Result<java.util.List<UserStatsResponse>> getContentRanking(Integer limit);

    /**
     * 获取点赞数排行榜
     * 
     * @param limit 返回数量限制
     * @return 用户统计列表
     */
    Result<java.util.List<UserStatsResponse>> getLikeRanking(Integer limit);

    /**
     * 获取登录次数排行榜
     * 
     * @param limit 返回数量限制
     * @return 用户统计列表
     */
    Result<java.util.List<UserStatsResponse>> getLoginRanking(Integer limit);

    /**
     * 删除用户统计数据
     * 
     * @param userId 用户ID
     * @return 删除结果
     */
    Result<Void> deleteStats(Long userId);

    /**
     * 检查用户统计数据是否存在
     * 
     * @param userId 用户ID
     * @return 是否存在
     */
    Result<Boolean> checkStatsExists(Long userId);

    /**
     * 获取平台总体统计数据
     * 
     * @return 平台统计数据
     */
    Result<java.util.Map<String, Object>> getPlatformStats();
}