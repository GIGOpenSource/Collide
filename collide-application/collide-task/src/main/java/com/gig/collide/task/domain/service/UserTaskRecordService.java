package com.gig.collide.task.domain.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.task.domain.entity.UserTaskRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 用户任务记录业务服务接口 - 简洁版
 * 基于task-simple.sql的单表设计
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
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
     * 更新任务进度
     */
    boolean updateTaskProgress(Long userId, Long taskId, String taskAction, Integer incrementCount);

    /**
     * 批量更新任务进度
     */
    boolean batchUpdateTaskProgress(List<Map<String, Object>> progressList);

    /**
     * 标记任务为完成
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
     * 分页查询用户任务记录
     */
    Page<UserTaskRecord> queryUserTaskRecords(Long userId, Long taskId, String taskType,
                                             String taskCategory, Boolean isCompleted, Boolean isRewarded,
                                             LocalDate startDate, LocalDate endDate,
                                             String orderBy, String orderDirection,
                                             Integer currentPage, Integer pageSize);

    /**
     * 获取用户今日任务列表
     */
    List<UserTaskRecord> getUserTodayTasks(Long userId);

    /**
     * 获取用户指定日期的任务列表
     */
    List<UserTaskRecord> getUserTasksByDate(Long userId, LocalDate taskDate, String taskType);

    /**
     * 获取用户可领取奖励的任务
     */
    List<UserTaskRecord> getUserClaimableTasks(Long userId);

    /**
     * 获取用户未完成的任务
     */
    List<UserTaskRecord> getUserIncompleteTasks(Long userId, String taskType);

    /**
     * 搜索用户任务记录
     */
    Page<UserTaskRecord> searchUserTaskRecords(Long userId, String keyword, String taskType,
                                              Boolean isCompleted, Integer currentPage, Integer pageSize);

    // =================== 统计操作 ===================

    /**
     * 获取用户任务统计信息
     */
    Map<String, Object> getUserTaskStatistics(Long userId, LocalDate taskDate);

    /**
     * 统计用户指定类型任务完成数
     */
    Long countUserCompletedTasks(Long userId, String taskType, LocalDate startDate, LocalDate endDate);

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
     * 获取用户各类型任务完成情况
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
     * 批量初始化用户任务
     */
    boolean batchInitializeUserTasks(List<Long> userIds, String taskType, LocalDate taskDate);

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
     * 获取用户任务完成排行榜
     */
    List<Map<String, Object>> getUserTaskRanking(String taskType, LocalDate taskDate, Integer limit);

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
     * 处理用户行为触发的任务进度更新
     */
    List<UserTaskRecord> handleUserAction(Long userId, String actionType, Map<String, Object> actionData);

    /**
     * 自动完成符合条件的任务
     */
    List<UserTaskRecord> autoCompleteEligibleTasks(Long userId);

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