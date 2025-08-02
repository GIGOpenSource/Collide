package com.gig.collide.task.facade;

import com.gig.collide.api.task.TaskFacadeService;
import com.gig.collide.api.task.constant.TaskActionConstant;
import com.gig.collide.api.task.constant.TaskTypeConstant;
import com.gig.collide.api.task.request.TaskTemplateCreateRequest;
import com.gig.collide.api.task.request.TaskTemplateQueryRequest;
import com.gig.collide.api.task.request.UserTaskQueryRequest;
import com.gig.collide.api.task.request.TaskProgressUpdateRequest;
import com.gig.collide.api.task.response.TaskTemplateResponse;
import com.gig.collide.api.task.response.UserTaskResponse;
import com.gig.collide.api.task.response.TaskRewardResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.task.domain.entity.TaskTemplate;
import com.gig.collide.task.domain.entity.UserTaskRecord;
import com.gig.collide.task.domain.service.TaskTemplateService;
import com.gig.collide.task.domain.service.UserTaskRecordService;
import com.gig.collide.task.domain.service.UserRewardRecordService;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 任务门面服务实现 - 优化版
 * 基于优化后的task-simple.sql，支持数字常量和立即钱包同步
 * 充分利用HASH索引和缓存策略，提升服务性能
 * 
 * @author GIG Team
 * @version 3.0.0 (优化版)
 * @since 2024-01-16
 */
@Slf4j
@DubboService(version = "3.0.0")
@RequiredArgsConstructor
public class TaskFacadeServiceImpl implements TaskFacadeService {

    private final TaskTemplateService taskTemplateService;
    private final UserTaskRecordService userTaskRecordService;
    private final UserRewardRecordService userRewardRecordService;

    // =================== 任务模板管理 ===================

    @Override
    public Result<TaskTemplateResponse> createTaskTemplate(TaskTemplateCreateRequest request) {
        try {
            log.info("门面创建任务模板: taskName={}, taskType={}", request.getTaskName(), request.getTaskType());
            
            TaskTemplate taskTemplate = convertToTaskTemplateEntity(request);
            TaskTemplate created = taskTemplateService.createTaskTemplate(taskTemplate);
            
            TaskTemplateResponse response = convertToTaskTemplateResponse(created);
            log.info("门面创建任务模板成功: id={}", created.getId());
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("门面创建任务模板失败: taskName={}", request.getTaskName(), e);
            return Result.failure("创建任务模板失败: " + e.getMessage());
        }
    }

    @Override
    public Result<TaskTemplateResponse> getTaskTemplateById(Long id) {
        try {
            log.debug("门面查询任务模板: id={}", id);
            
            TaskTemplate taskTemplate = taskTemplateService.getTaskTemplateById(id);
            if (taskTemplate == null) {
                return Result.failure("任务模板不存在");
            }
            
            TaskTemplateResponse response = convertToTaskTemplateResponse(taskTemplate);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("门面查询任务模板失败: id={}", id, e);
            return Result.failure("查询任务模板失败");
        }
    }

    @Override
    public Result<TaskTemplateResponse> updateTaskTemplate(Long id, TaskTemplateCreateRequest request) {
        try {
            log.info("门面更新任务模板: id={}, taskName={}", id, request.getTaskName());
            
            TaskTemplate taskTemplate = convertToTaskTemplateEntity(request);
            taskTemplate.setId(id);
            
            boolean updated = taskTemplateService.updateTaskTemplate(taskTemplate);
            if (!updated) {
                return Result.failure("更新任务模板失败");
            }
            
            TaskTemplate updatedTemplate = taskTemplateService.getTaskTemplateById(id);
            TaskTemplateResponse response = convertToTaskTemplateResponse(updatedTemplate);
            
            log.info("门面更新任务模板成功: id={}", id);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("门面更新任务模板失败: id={}", id, e);
            return Result.failure("更新任务模板失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> deleteTaskTemplate(Long id) {
        try {
            log.info("门面删除任务模板: id={}", id);
            
            boolean deleted = taskTemplateService.deleteTaskTemplate(id);
            if (!deleted) {
                return Result.failure("删除任务模板失败");
            }
            
            log.info("门面删除任务模板成功: id={}", id);
            return Result.success();
            
        } catch (Exception e) {
            log.error("门面删除任务模板失败: id={}", id, e);
            return Result.failure("删除任务模板失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<TaskTemplateResponse>> queryTaskTemplates(TaskTemplateQueryRequest request) {
        try {
            log.debug("门面分页查询任务模板: page={}", request.getCurrentPage());
            
            // 这里简化实现，实际应该根据request的参数调用service
            var page = taskTemplateService.queryTaskTemplates(
                null, request.getTaskType(), request.getTaskCategory(), null, 
                request.getIsActive(), null, null, null, null,
                request.getCurrentPage(), request.getPageSize()
            );
            
            List<TaskTemplateResponse> responseList = page.getRecords().stream()
                    .map(this::convertToTaskTemplateResponse)
                    .collect(Collectors.toList());
            
            PageResponse<TaskTemplateResponse> pageResponse = new PageResponse<>();
            pageResponse.setRecords(responseList);
            pageResponse.setTotal(page.getTotal());
            pageResponse.setCurrentPage((int) page.getCurrent());
            pageResponse.setPageSize((int) page.getSize());
            
            return Result.success(pageResponse);
            
        } catch (Exception e) {
            log.error("门面分页查询任务模板失败", e);
            return Result.failure("查询任务模板失败");
        }
    }

    @Override
    public Result<List<TaskTemplateResponse>> getAllActiveTasks() {
        try {
            log.debug("门面查询所有启用任务");
            
            List<TaskTemplate> activeTasks = taskTemplateService.getAllActiveTasks();
            List<TaskTemplateResponse> responseList = activeTasks.stream()
                    .map(this::convertToTaskTemplateResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responseList);
            
        } catch (Exception e) {
            log.error("门面查询所有启用任务失败", e);
            return Result.failure("查询启用任务失败");
        }
    }

    // =================== 用户任务管理 ===================

    @Override
    public Result<List<UserTaskResponse>> getUserTodayTasks(Long userId) {
        try {
            log.debug("门面查询用户今日任务: userId={}", userId);
            
            List<UserTaskRecord> todayTasks = userTaskRecordService.getUserTodayTasks(userId);
            List<UserTaskResponse> responseList = todayTasks.stream()
                    .map(this::convertToUserTaskResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responseList);
            
        } catch (Exception e) {
            log.error("门面查询用户今日任务失败: userId={}", userId, e);
            return Result.failure("查询今日任务失败");
        }
    }

    @Override
    public Result<PageResponse<UserTaskResponse>> getUserTasks(UserTaskQueryRequest request) {
        try {
            log.debug("门面查询用户任务: userId={}", request.getUserId());
            
            // 简化实现
            var page = userTaskRecordService.queryUserTaskRecords(
                request.getUserId(), null, request.getTaskType(), null,
                request.getIsCompleted(), request.getIsRewarded(),
                null, null, null, null,
                request.getCurrentPage(), request.getPageSize()
            );
            
            List<UserTaskResponse> responseList = page.getRecords().stream()
                    .map(this::convertToUserTaskResponse)
                    .collect(Collectors.toList());
            
            PageResponse<UserTaskResponse> pageResponse = new PageResponse<>();
            pageResponse.setRecords(responseList);
            pageResponse.setTotal(page.getTotal());
            pageResponse.setCurrentPage((int) page.getCurrent());
            pageResponse.setPageSize((int) page.getSize());
            
            return Result.success(pageResponse);
            
        } catch (Exception e) {
            log.error("门面查询用户任务失败: userId={}", request.getUserId(), e);
            return Result.failure("查询用户任务失败");
        }
    }

    @Override
    public Result<UserTaskResponse> updateTaskProgress(TaskProgressUpdateRequest request) {
        try {
            log.info("门面更新任务进度: userId={}, taskId={}, action={}", 
                    request.getUserId(), request.getTaskId(), request.getTaskAction());
            
            boolean updated = userTaskRecordService.updateTaskProgress(
                request.getUserId(), request.getTaskId(), 
                request.getTaskAction(), request.getIncrementCount()
            );
            
            if (!updated) {
                return Result.failure("更新任务进度失败");
            }
            
            // 获取更新后的任务记录
            UserTaskRecord userTask = userTaskRecordService.getUserTaskProgress(
                request.getUserId(), request.getTaskId(), LocalDate.now()
            );
            
            UserTaskResponse response = convertToUserTaskResponse(userTask);
            log.info("门面更新任务进度成功: userId={}, taskId={}", request.getUserId(), request.getTaskId());
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("门面更新任务进度失败: userId={}, taskId={}", 
                    request.getUserId(), request.getTaskId(), e);
            return Result.failure("更新任务进度失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<TaskRewardResponse>> claimTaskReward(Long userId, Long taskId) {
        try {
            log.info("门面领取任务奖励: userId={}, taskId={}", userId, taskId);
            
            // 标记奖励已领取
            boolean claimed = userTaskRecordService.markRewardReceived(userId, taskId, LocalDate.now());
            if (!claimed) {
                return Result.failure("领取奖励失败");
            }
            
            // 处理任务完成奖励
            boolean processed = userRewardRecordService.processTaskCompletionReward(userId, null);
            if (!processed) {
                return Result.failure("处理奖励失败");
            }
            
            // 返回奖励信息（简化实现）
            log.info("门面领取任务奖励成功: userId={}, taskId={}", userId, taskId);
            return Result.success(List.of());
            
        } catch (Exception e) {
            log.error("门面领取任务奖励失败: userId={}, taskId={}", userId, taskId, e);
            return Result.failure("领取奖励失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<UserTaskResponse>> getUserClaimableTasks(Long userId) {
        try {
            log.debug("门面查询可领取奖励任务: userId={}", userId);
            
            List<UserTaskRecord> claimableTasks = userTaskRecordService.getUserClaimableTasks(userId);
            List<UserTaskResponse> responseList = claimableTasks.stream()
                    .map(this::convertToUserTaskResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responseList);
            
        } catch (Exception e) {
            log.error("门面查询可领取奖励任务失败: userId={}", userId, e);
            return Result.failure("查询可领取奖励任务失败");
        }
    }

    @Override
    public Result<List<UserTaskResponse>> initializeDailyTasks(Long userId) {
        try {
            log.info("门面初始化用户每日任务: userId={}", userId);
            
            boolean initialized = userTaskRecordService.initializeDailyTasks(userId, LocalDate.now());
            if (!initialized) {
                return Result.failure("初始化每日任务失败");
            }
            
            // 返回今日任务列表
            return getUserTodayTasks(userId);
            
        } catch (Exception e) {
            log.error("门面初始化用户每日任务失败: userId={}", userId, e);
            return Result.failure("初始化每日任务失败: " + e.getMessage());
        }
    }

    // =================== 任务统计 ===================

    @Override
    public Result<Map<String, Object>> getUserTaskStatistics(Long userId) {
        try {
            log.debug("门面查询用户任务统计: userId={}", userId);
            
            Map<String, Object> statistics = userTaskRecordService.getUserTaskStatistics(userId, LocalDate.now());
            return Result.success(statistics);
            
        } catch (Exception e) {
            log.error("门面查询用户任务统计失败: userId={}", userId, e);
            return Result.failure("查询任务统计失败");
        }
    }

    @Override
    public Result<Map<String, Object>> getUserRewardStatistics(Long userId) {
        try {
            log.debug("门面查询用户奖励统计: userId={}", userId);
            
            Map<String, Object> statistics = userRewardRecordService.getUserRewardStatistics(userId);
            return Result.success(statistics);
            
        } catch (Exception e) {
            log.error("门面查询用户奖励统计失败: userId={}", userId, e);
            return Result.failure("查询奖励统计失败");
        }
    }

    @Override
    public Result<List<Map<String, Object>>> getTaskCompletionRanking(Integer taskType, Integer limit) {
        try {
            log.debug("门面查询任务完成排行榜: taskType={}, limit={}", taskType, limit);
            
            List<Map<String, Object>> ranking = userTaskRecordService.getUserTaskRanking(taskType, LocalDate.now(), limit);
            return Result.success(ranking);
            
        } catch (Exception e) {
            log.error("门面查询任务完成排行榜失败: taskType={}", taskType, e);
            return Result.failure("查询排行榜失败");
        }
    }

    // =================== 系统管理 ===================

    @Override
    public Result<List<UserTaskResponse>> handleUserAction(Long userId, Integer actionType, Map<String, Object> actionData) {
        try {
            log.info("门面处理用户行为: userId={}, actionType={}", userId, actionType);
            
            List<UserTaskRecord> updatedTasks = userTaskRecordService.handleUserAction(userId, actionType, actionData);
            List<UserTaskResponse> responseList = updatedTasks.stream()
                    .map(this::convertToUserTaskResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responseList);
            
        } catch (Exception e) {
            log.error("门面处理用户行为失败: userId={}, actionType={}", userId, actionType, e);
            return Result.failure("处理用户行为失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> resetDailyTasks() {
        try {
            log.info("门面重置每日任务");
            
            boolean reset = userTaskRecordService.batchResetDailyTasks(LocalDate.now());
            return Result.success(reset ? 1 : 0);
            
        } catch (Exception e) {
            log.error("门面重置每日任务失败", e);
            return Result.failure("重置每日任务失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> autoProcessPendingRewards() {
        try {
            log.info("门面自动发放待发放奖励");
            
            int processed = userRewardRecordService.autoProcessPendingRewards();
            return Result.success(processed);
            
        } catch (Exception e) {
            log.error("门面自动发放待发放奖励失败", e);
            return Result.failure("自动发放奖励失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getSystemTaskStatistics() {
        try {
            log.debug("门面查询系统任务统计");
            
            Map<String, Object> statistics = taskTemplateService.getTaskTemplateStatistics();
            return Result.success(statistics);
            
        } catch (Exception e) {
            log.error("门面查询系统任务统计失败", e);
            return Result.failure("查询系统统计失败");
        }
    }

    // =================== 转换方法 ===================

    private TaskTemplate convertToTaskTemplateEntity(TaskTemplateCreateRequest request) {
        TaskTemplate taskTemplate = new TaskTemplate();
        BeanUtils.copyProperties(request, taskTemplate);
        return taskTemplate;
    }

    private TaskTemplateResponse convertToTaskTemplateResponse(TaskTemplate taskTemplate) {
        TaskTemplateResponse response = new TaskTemplateResponse();
        BeanUtils.copyProperties(taskTemplate, response);
        return response;
    }

    private UserTaskResponse convertToUserTaskResponse(UserTaskRecord userTaskRecord) {
        UserTaskResponse response = new UserTaskResponse();
        BeanUtils.copyProperties(userTaskRecord, response);
        
        // 设置计算字段
        response.setProgressPercentage(userTaskRecord.getProgressPercentage());
        response.setRemainingCount(userTaskRecord.getRemainingCount());
        response.setCanClaimReward(userTaskRecord.canClaimReward());
        
        return response;
    }
}