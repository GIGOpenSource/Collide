package com.gig.collide.api.task;

import com.gig.collide.api.task.request.TaskTemplateCreateRequest;
import com.gig.collide.api.task.request.TaskTemplateQueryRequest;
import com.gig.collide.api.task.request.UserTaskQueryRequest;
import com.gig.collide.api.task.request.TaskProgressUpdateRequest;
import com.gig.collide.api.task.response.TaskTemplateResponse;
import com.gig.collide.api.task.response.UserTaskResponse;
import com.gig.collide.api.task.response.TaskRewardResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import java.util.List;
import java.util.Map;

/**
 * 任务模块门面服务接口 - 简洁版
 * 基于task-simple.sql的单表设计，实现核心任务功能
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
public interface TaskFacadeService {

    // =================== 任务模板管理 ===================

    /**
     * 创建任务模板
     */
    Result<TaskTemplateResponse> createTaskTemplate(TaskTemplateCreateRequest request);

    /**
     * 获取任务模板详情
     */
    Result<TaskTemplateResponse> getTaskTemplateById(Long id);

    /**
     * 更新任务模板
     */
    Result<TaskTemplateResponse> updateTaskTemplate(Long id, TaskTemplateCreateRequest request);

    /**
     * 删除任务模板
     */
    Result<Void> deleteTaskTemplate(Long id);

    /**
     * 分页查询任务模板
     */
    Result<PageResponse<TaskTemplateResponse>> queryTaskTemplates(TaskTemplateQueryRequest request);

    /**
     * 获取所有启用的任务模板
     */
    Result<List<TaskTemplateResponse>> getAllActiveTasks();

    // =================== 用户任务管理 ===================

    /**
     * 获取用户今日任务列表
     */
    Result<List<UserTaskResponse>> getUserTodayTasks(Long userId);

    /**
     * 获取用户任务列表（支持分页）
     */
    Result<PageResponse<UserTaskResponse>> getUserTasks(UserTaskQueryRequest request);

    /**
     * 更新用户任务进度
     */
    Result<UserTaskResponse> updateTaskProgress(TaskProgressUpdateRequest request);

    /**
     * 领取任务奖励
     */
    Result<List<TaskRewardResponse>> claimTaskReward(Long userId, Long taskId);

    /**
     * 获取用户可领取奖励的任务
     */
    Result<List<UserTaskResponse>> getUserClaimableTasks(Long userId);

    /**
     * 初始化用户每日任务
     */
    Result<List<UserTaskResponse>> initializeDailyTasks(Long userId);

    // =================== 任务统计 ===================

    /**
     * 获取用户任务统计
     */
    Result<Map<String, Object>> getUserTaskStatistics(Long userId);

    /**
     * 获取用户奖励统计
     */
    Result<Map<String, Object>> getUserRewardStatistics(Long userId);

    /**
     * 获取任务完成排行榜
     */
    Result<List<Map<String, Object>>> getTaskCompletionRanking(String taskType, Integer limit);

    // =================== 系统管理 ===================

    /**
     * 处理用户行为触发的任务进度更新
     */
    Result<List<UserTaskResponse>> handleUserAction(Long userId, String actionType, Map<String, Object> actionData);

    /**
     * 重置每日任务（系统定时调用）
     */
    Result<Integer> resetDailyTasks();

    /**
     * 自动发放待发放奖励（系统定时调用）
     */
    Result<Integer> autoProcessPendingRewards();

    /**
     * 获取系统任务统计
     */
    Result<Map<String, Object>> getSystemTaskStatistics();
}