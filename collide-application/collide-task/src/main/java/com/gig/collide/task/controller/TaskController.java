package com.gig.collide.task.controller;

import com.gig.collide.api.task.TaskFacadeService;
import com.gig.collide.api.task.request.TaskProgressUpdateRequest;
import com.gig.collide.api.task.request.UserTaskQueryRequest;
import com.gig.collide.api.task.response.UserTaskResponse;
import com.gig.collide.api.task.response.TaskRewardResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 任务控制器 - 简洁版
 * 基于task-simple.sql的单表设计，提供任务管理功能
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/task")
@RequiredArgsConstructor
@Validated
@Tag(name = "任务管理", description = "用户任务和奖励相关接口")
public class TaskController {

    private final TaskFacadeService taskFacadeService;

    // =================== 用户任务管理 ===================

    @GetMapping("/today/{userId}")
    @Operation(summary = "获取用户今日任务", description = "获取用户当天的所有任务列表")
    public Result<List<UserTaskResponse>> getUserTodayTasks(
            @PathVariable("userId") @NotNull @Min(1) Long userId) {
        log.debug("REST获取用户今日任务: userId={}", userId);
        return taskFacadeService.getUserTodayTasks(userId);
    }

    @PostMapping("/user/query")
    @Operation(summary = "分页查询用户任务", description = "根据条件分页查询用户任务记录")
    public Result<PageResponse<UserTaskResponse>> getUserTasks(
            @Valid @RequestBody UserTaskQueryRequest request) {
        log.debug("REST查询用户任务: userId={}, page={}", request.getUserId(), request.getCurrentPage());
        return taskFacadeService.getUserTasks(request);
    }

    @PostMapping("/progress/update")
    @Operation(summary = "更新任务进度", description = "更新用户任务完成进度")
    public Result<UserTaskResponse> updateTaskProgress(
            @Valid @RequestBody TaskProgressUpdateRequest request) {
        log.info("REST更新任务进度: userId={}, taskId={}, action={}", 
                request.getUserId(), request.getTaskId(), request.getTaskAction());
        return taskFacadeService.updateTaskProgress(request);
    }

    @PostMapping("/reward/claim")
    @Operation(summary = "领取任务奖励", description = "领取已完成任务的奖励")
    public Result<List<TaskRewardResponse>> claimTaskReward(
            @Parameter(description = "用户ID") @RequestParam @NotNull @Min(1) Long userId,
            @Parameter(description = "任务ID") @RequestParam @NotNull @Min(1) Long taskId) {
        log.info("REST领取任务奖励: userId={}, taskId={}", userId, taskId);
        return taskFacadeService.claimTaskReward(userId, taskId);
    }

    @GetMapping("/claimable/{userId}")
    @Operation(summary = "获取可领取奖励任务", description = "获取用户可以领取奖励的任务列表")
    public Result<List<UserTaskResponse>> getUserClaimableTasks(
            @PathVariable("userId") @NotNull @Min(1) Long userId) {
        log.debug("REST获取可领取奖励任务: userId={}", userId);
        return taskFacadeService.getUserClaimableTasks(userId);
    }

    @PostMapping("/daily/init")
    @Operation(summary = "初始化每日任务", description = "初始化用户当天的每日任务")
    public Result<List<UserTaskResponse>> initializeDailyTasks(
            @Parameter(description = "用户ID") @RequestParam @NotNull @Min(1) Long userId) {
        log.info("REST初始化每日任务: userId={}", userId);
        return taskFacadeService.initializeDailyTasks(userId);
    }

    // =================== 任务统计 ===================

    @GetMapping("/statistics/user/{userId}")
    @Operation(summary = "用户任务统计", description = "获取用户任务完成统计信息")
    public Result<Map<String, Object>> getUserTaskStatistics(
            @PathVariable("userId") @NotNull @Min(1) Long userId) {
        log.debug("REST获取用户任务统计: userId={}", userId);
        return taskFacadeService.getUserTaskStatistics(userId);
    }

    @GetMapping("/statistics/reward/{userId}")
    @Operation(summary = "用户奖励统计", description = "获取用户奖励获得统计信息")
    public Result<Map<String, Object>> getUserRewardStatistics(
            @PathVariable("userId") @NotNull @Min(1) Long userId) {
        log.debug("REST获取用户奖励统计: userId={}", userId);
        return taskFacadeService.getUserRewardStatistics(userId);
    }

    @GetMapping("/ranking")
    @Operation(summary = "任务完成排行榜", description = "获取任务完成情况排行榜")
    public Result<List<Map<String, Object>>> getTaskCompletionRanking(
            @Parameter(description = "任务类型") @RequestParam(required = false) String taskType,
            @Parameter(description = "排行榜条数") @RequestParam(defaultValue = "10") @Min(1) Integer limit) {
        log.debug("REST获取任务完成排行榜: taskType={}, limit={}", taskType, limit);
        return taskFacadeService.getTaskCompletionRanking(taskType, limit);
    }

    // =================== 用户行为处理 ===================

    @PostMapping("/action/handle")
    @Operation(summary = "处理用户行为", description = "处理用户行为触发的任务进度更新")
    public Result<List<UserTaskResponse>> handleUserAction(
            @Parameter(description = "用户ID") @RequestParam @NotNull @Min(1) Long userId,
            @Parameter(description = "行为类型") @RequestParam @NotNull String actionType,
            @Parameter(description = "行为数据") @RequestBody(required = false) Map<String, Object> actionData) {
        log.info("REST处理用户行为: userId={}, actionType={}", userId, actionType);
        return taskFacadeService.handleUserAction(userId, actionType, actionData);
    }

    // =================== 系统管理接口 ===================

    @PostMapping("/system/reset-daily")
    @Operation(summary = "重置每日任务", description = "系统定时调用 - 重置所有用户的每日任务")
    public Result<Integer> resetDailyTasks() {
        log.info("REST重置每日任务");
        return taskFacadeService.resetDailyTasks();
    }

    @PostMapping("/system/process-rewards")
    @Operation(summary = "自动发放奖励", description = "系统定时调用 - 自动发放待发放的奖励")
    public Result<Integer> autoProcessPendingRewards() {
        log.info("REST自动发放待发放奖励");
        return taskFacadeService.autoProcessPendingRewards();
    }

    @GetMapping("/system/statistics")
    @Operation(summary = "系统任务统计", description = "获取系统级任务统计信息")
    public Result<Map<String, Object>> getSystemTaskStatistics() {
        log.debug("REST获取系统任务统计");
        return taskFacadeService.getSystemTaskStatistics();
    }

    // =================== 健康检查 ===================

    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "任务模块健康状态检查")
    public Result<Map<String, Object>> healthCheck() {
        log.debug("REST任务模块健康检查");
        
        Map<String, Object> health = Map.of(
            "status", "UP",
            "module", "collide-task",
            "version", "2.0.0",
            "timestamp", System.currentTimeMillis()
        );
        
        return Result.success(health);
    }
}