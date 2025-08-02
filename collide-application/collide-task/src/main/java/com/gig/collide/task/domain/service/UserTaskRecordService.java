package com.gig.collide.task.domain.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.task.domain.entity.UserTaskRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 用户任务记录业务服务接口 - 优化版
 * 基于优化后的task-simple.sql的单表设计
 * 支持数字常量和立即钱包同步功能
 * 
 * @author GIG Team
 * @version 2.0.0 (优化版)
 * @since 2024-01-16
 */
public interface UserTaskRecordService {

    // =================== 基础操作 ===================

    /**
     * 创建用户任务记录
     */
    UserTaskRecord createUserTaskRecord(UserTaskRecord userTaskRecord);

    /**
     * 根据ID获取用户任务记录
     */
    UserTaskRecord getUserTaskRecordById(Long id);

    /**
     * 更新用户任务记录
     */
    boolean updateUserTaskRecord(UserTaskRecord userTaskRecord);

    /**
     * 删除用户任务记录
     */
    boolean deleteUserTaskRecord(Long id);

    // =================== 任务进度管理 ===================

    /**
     * 更新任务进度（优化版 - 使用数字常量）
     * 支持任务完成时立即同步奖励到钱包
     */
    boolean updateTaskProgress(Long userId, Long taskId, Integer taskAction, Integer incrementCount);

    /**
     * 更新任务进度并立即处理奖励
     * 这是核心方法，任务完成时会自动发放奖励并同步到钱包
     */
    boolean updateTaskProgressWithReward(Long userId, Integer taskAction, Integer incrementCount);

    /**
     * 批量更新任务进度
     */
    boolean batchUpdateTaskProgress(List<Map<String, Object>> progressList);

    /**
     * 标记任务为完成并立即发放奖励
     * 任务完成时会自动触发奖励发放和钱包同步
     */
    boolean markTaskCompletedWithReward(Long userId, Long taskId, LocalDate taskDate);

    /**
     * 标记任务为完成（不发放奖励）
     */
    boolean markTaskCompleted(Long userId, Long taskId, LocalDate taskDate);

    /**
     * 标记奖励为已领取
     */
    boolean markRewardReceived(Long userId, Long taskId, LocalDate taskDate);

    /**
     * 批量标记奖励已领取
     */
    boolean batchMarkRewardsReceived(List<Long> recordIds);

    // =================== 查询操作 ===================

    /**
     * 分页查询用户任务记录（优化版 - 使用数字常量）
     */
    Page<UserTaskRecord> queryUserTaskRecords(Long userId, Long taskId, Integer taskType,
                                             Integer taskCategory, Boolean isCompleted, Boolean isRewarded,
                                             LocalDate startDate, LocalDate endDate,
                                             String orderBy, String orderDirection,
                                             Integer currentPage, Integer pageSize);

    /**
     * 获取用户今日任务列表
     */
    List<UserTaskRecord> getUserTodayTasks(Long userId);

    /**
     * 获取用户指定日期的任务列表（优化版 - 使用数字常量）
     */
    List<UserTaskRecord> getUserTasksByDate(Long userId, LocalDate taskDate, Integer taskType);

    /**
     * 获取用户可领取奖励的任务
     */
    List<UserTaskRecord> getUserClaimableTasks(Long userId);

    /**
     * 获取用户未完成的任务（优化版 - 使用数字常量）
     */
    List<UserTaskRecord> getUserIncompleteTasks(Long userId, Integer taskType);

    /**
     * 搜索用户任务记录（优化版 - 使用数字常量）
     */
    Page<UserTaskRecord> searchUserTaskRecords(Long userId, String keyword, Integer taskType,
                                              Boolean isCompleted, Integer currentPage, Integer pageSize);

    // =================== 统计操作 ===================

    /**
     * 获取用户任务统计信息
     */
    Map<String, Object> getUserTaskStatistics(Long userId, LocalDate taskDate);

    /**
     * 统计用户指定类型任务完成数（优化版 - 使用数字常量）
     */
    Long countUserCompletedTasks(Long userId, Integer taskType, LocalDate startDate, LocalDate endDate);

    /**
     * 统计用户连续登录天数
     */
    Integer getUserConsecutiveLoginDays(Long userId);

    /**
     * 统计用户未领取奖励数量
     */
    Long countUserUnclaimedRewards(Long userId);

    /**
     * 统计用户历史任务完成总数
     */
    Long countUserTotalCompletedTasks(Long userId);

    /**
     * 获取用户各类型任务完成情况（优化版 - 返回数字类型统计）
     */
    Map<String, Object> getUserTaskCompletionByType(Long userId, LocalDate startDate, LocalDate endDate);

    // =================== 任务初始化 ===================

    /**
     * 初始化用户每日任务
     */
    boolean initializeDailyTasks(Long userId, LocalDate taskDate);

    /**
     * 初始化用户周常任务
     */
    boolean initializeWeeklyTasks(Long userId, LocalDate taskDate);

    /**
     * 批量初始化用户任务（优化版 - 使用数字常量）
     */
    boolean batchInitializeUserTasks(List<Long> userIds, Integer taskType, LocalDate taskDate);

    // =================== 任务重置 ===================

    /**
     * 重置用户每日任务
     */
    boolean resetUserDailyTasks(Long userId, LocalDate taskDate);

    /**
     * 批量重置每日任务
     */
    boolean batchResetDailyTasks(LocalDate taskDate);

    /**
     * 清理过期任务记录
     */
    int cleanExpiredTaskRecords(Integer days);

    // =================== 排行榜 ===================

    /**
     * 获取用户任务完成排行榜（优化版 - 使用数字常量）
     */
    List<Map<String, Object>> getUserTaskRanking(Integer taskType, LocalDate taskDate, Integer limit);

    /**
     * 获取活跃用户列表
     */
    List<Long> getActiveUsers(Integer days, Integer minCompletedTasks);

    // =================== 验证方法 ===================

    /**
     * 检查用户今日是否已有任务记录
     */
    boolean hasTodayTaskRecord(Long userId, Long taskId);

    /**
     * 检查用户是否可以领取奖励
     */
    boolean canUserClaimReward(Long userId, Long taskId, LocalDate taskDate);

    /**
     * 获取用户任务当前进度
     */
    UserTaskRecord getUserTaskProgress(Long userId, Long taskId, LocalDate taskDate);

    /**
     * 检查任务是否可以更新进度
     */
    boolean canUpdateTaskProgress(Long userId, Long taskId, LocalDate taskDate);

    // =================== 特殊操作 ===================

    /**
     * 处理用户行为触发的任务进度更新（优化版 - 使用数字常量）
     * 支持任务完成时立即发放奖励并同步到钱包
     */
    List<UserTaskRecord> handleUserAction(Long userId, Integer actionType, Map<String, Object> actionData);

    /**
     * 自动完成符合条件的任务
     * 支持任务完成时立即发放奖励并同步到钱包
     */
    List<UserTaskRecord> autoCompleteEligibleTasks(Long userId);

    // =================== 钱包同步相关方法 ===================

    /**
     * 处理任务完成后的奖励发放和钱包同步
     * 这是核心方法，确保任务完成后金币立即到账
     */
    boolean processTaskCompletionRewards(Long userId, Long taskRecordId);

    /**
     * 批量处理任务完成奖励
     * 用于批量任务完成时的奖励发放
     */
    int batchProcessTaskCompletionRewards(List<Long> taskRecordIds);

    /**
     * 检查任务奖励是否已同步到钱包
     */
    boolean isRewardSyncedToWallet(Long userId, Long taskRecordId);

    /**
     * 同步任务模板变更到用户任务记录
     */
    boolean syncTaskTemplateChanges(Long taskId);

    /**
     * 修复异常任务数据
     */
    int fixAbnormalTaskData();

    /**
     * 获取用户任务进度报告
     */
    Map<String, Object> getUserTaskProgressReport(Long userId, LocalDate startDate, LocalDate endDate);
}