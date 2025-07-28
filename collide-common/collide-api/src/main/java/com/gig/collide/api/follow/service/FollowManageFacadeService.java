package com.gig.collide.api.follow.service;

import com.gig.collide.api.follow.request.*;
import com.gig.collide.api.follow.response.*;
import com.gig.collide.api.follow.response.data.FollowInfo;
import com.gig.collide.base.response.PageResponse;

import java.util.List;

/**
 * 关注管理门面服务接口
 * 提供关注管理和系统维护功能
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface FollowManageFacadeService {

    // ===================== 管理员查询功能 =====================

    /**
     * 管理员查询关注信息（包含所有字段）
     *
     * @param queryRequest 查询请求
     * @return 查询响应
     */
    FollowQueryResponse<FollowInfo> adminQueryFollow(FollowQueryRequest queryRequest);

    /**
     * 管理员分页查询关注信息
     *
     * @param queryRequest 分页查询请求
     * @return 分页响应
     */
    PageResponse<FollowInfo> adminPageQueryFollows(FollowQueryRequest queryRequest);

    /**
     * 查询异常关注关系（状态异常、数据不一致等）
     *
     * @return 异常关注列表
     */
    FollowQueryResponse<FollowInfo> queryAbnormalFollows();

    /**
     * 查询已删除的关注关系
     *
     * @param queryRequest 查询请求
     * @return 已删除关注列表
     */
    FollowQueryResponse<FollowInfo> queryDeletedFollows(FollowQueryRequest queryRequest);

    // ===================== 强制操作功能 =====================

    /**
     * 强制删除关注关系（忽略业务规则）
     *
     * @param deleteRequest 删除请求
     * @return 删除响应
     */
    FollowDeleteResponse forceDeleteFollow(FollowDeleteRequest deleteRequest);

    /**
     * 强制更新关注状态
     *
     * @param updateRequest 更新请求
     * @return 更新响应
     */
    FollowUpdateResponse forceUpdateFollowStatus(FollowUpdateRequest updateRequest);

    /**
     * 物理删除关注记录
     *
     * @param followId 关注ID
     * @return 删除响应
     */
    FollowDeleteResponse physicalDeleteFollow(Long followId);

    /**
     * 恢复已删除的关注关系
     *
     * @param followId 关注ID
     * @return 更新响应
     */
    FollowUpdateResponse restoreDeletedFollow(Long followId);

    // ===================== 批量管理功能 =====================

    /**
     * 批量强制删除关注关系
     *
     * @param followIds 关注ID列表
     * @param reason 删除原因
     * @return 批量操作响应
     */
    FollowBatchOperationResponse batchForceDelete(List<Long> followIds, String reason);

    /**
     * 批量禁用用户关注功能
     *
     * @param userIds 用户ID列表
     * @param reason 禁用原因
     * @return 批量操作响应
     */
    FollowBatchOperationResponse batchDisableUserFollow(List<Long> userIds, String reason);

    /**
     * 批量启用用户关注功能
     *
     * @param userIds 用户ID列表
     * @return 批量操作响应
     */
    FollowBatchOperationResponse batchEnableUserFollow(List<Long> userIds);

    // ===================== 数据维护功能 =====================

    /**
     * 重新计算用户关注统计
     *
     * @param userId 用户ID，null表示重新计算所有用户
     * @return 统计响应
     */
    FollowStatisticsResponse recalculateUserStatistics(Long userId);

    /**
     * 检查数据一致性
     *
     * @return 一致性检查结果
     */
    FollowQueryResponse<String> checkDataConsistency();

    /**
     * 修复数据不一致问题
     *
     * @return 修复结果
     */
    FollowQueryResponse<String> repairDataInconsistency();

    /**
     * 清理无效关注数据
     *
     * @return 清理结果
     */
    FollowBatchOperationResponse cleanupInvalidFollows();

    // ===================== 平台统计功能 =====================

    /**
     * 获取平台关注统计
     *
     * @return 平台统计响应
     */
    FollowStatisticsResponse getPlatformStatistics();

    /**
     * 获取热门用户排行榜
     *
     * @param limit 排行榜数量
     * @return 统计响应
     */
    FollowStatisticsResponse getPopularUsersRanking(Integer limit);

    /**
     * 获取活跃用户统计
     *
     * @param days 统计天数
     * @return 统计响应
     */
    FollowStatisticsResponse getActiveUsersStatistics(Integer days);

    // ===================== 审计功能 =====================

    /**
     * 审计用户关注行为
     *
     * @param userId 用户ID
     * @param days 审计天数
     * @return 审计结果
     */
    FollowQueryResponse<String> auditUserFollowBehavior(Long userId, Integer days);

    /**
     * 检测异常关注行为
     *
     * @return 异常行为检测结果
     */
    FollowQueryResponse<String> detectAbnormalFollowBehavior();

    /**
     * 获取系统性能指标
     *
     * @return 性能指标
     */
    FollowQueryResponse<String> getSystemPerformanceMetrics();

    // ===================== 配置管理功能 =====================

    /**
     * 更新系统配置
     *
     * @param configKey 配置键
     * @param configValue 配置值
     * @return 更新结果
     */
    FollowQueryResponse<String> updateSystemConfig(String configKey, String configValue);

    /**
     * 获取系统配置
     *
     * @param configKey 配置键
     * @return 配置值
     */
    FollowQueryResponse<String> getSystemConfig(String configKey);

    /**
     * 重置系统配置为默认值
     *
     * @return 重置结果
     */
    FollowQueryResponse<String> resetSystemConfig();
} 