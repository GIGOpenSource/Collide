package com.gig.collide.task.controller;

import com.gig.collide.api.task.TaskTemplateFacadeService;
import com.gig.collide.api.task.response.TaskTemplateResponse;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 任务模板控制器 - 签到专用版
 * 提供任务模板查询的HTTP接口
 * 
 * @author GIG Team
 * @version 1.0.0
 * @since 2024-01-16
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tasks/templates")
@RequiredArgsConstructor
@Validated
@Tag(name = "任务模板管理", description = "任务模板查询接口")
public class TaskTemplateController {

    @DubboReference
    private TaskTemplateFacadeService taskTemplateFacadeService;

    /**
     * 获取任务模板详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取任务模板详情", description = "根据ID查询任务模板详细信息")
    public Result<TaskTemplateResponse> getTaskTemplate(
            @PathVariable("id") @NotNull @Min(1) Long id) {
        try {
            log.info("REST查询任务模板详情: id={}", id);
            return taskTemplateFacadeService.getTaskTemplate(id);
        } catch (Exception e) {
            log.error("查询任务模板详情异常: id={}", id, e);
            return Result.error("GET_TASK_TEMPLATE_ERROR", "查询任务模板失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有可用的任务模板
     */
    @GetMapping("/available")
    @Operation(summary = "获取可用任务模板", description = "查询所有状态为启用的任务模板")
    public Result<List<TaskTemplateResponse>> getAvailableTaskTemplates() {
        try {
            log.info("REST查询可用任务模板列表");
            return taskTemplateFacadeService.getAvailableTaskTemplates();
        } catch (Exception e) {
            log.error("查询可用任务模板异常", e);
            return Result.error("GET_AVAILABLE_TEMPLATES_ERROR", "查询可用任务模板失败: " + e.getMessage());
        }
    }

    /**
     * 根据任务类型查询任务模板
     */
    @GetMapping("/type/{taskType}")
    @Operation(summary = "按类型查询任务模板", description = "根据任务类型查询任务模板列表")
    public Result<List<TaskTemplateResponse>> getTaskTemplatesByType(
            @PathVariable("taskType") @NotBlank String taskType) {
        try {
            log.info("REST按类型查询任务模板: taskType={}", taskType);
            return taskTemplateFacadeService.getTaskTemplatesByType(taskType);
        } catch (Exception e) {
            log.error("按类型查询任务模板异常: taskType={}", taskType, e);
            return Result.error("GET_TEMPLATES_BY_TYPE_ERROR", "按类型查询任务模板失败: " + e.getMessage());
        }
    }

    /**
     * 获取每日签到任务模板
     */
    @GetMapping("/daily-checkin")
    @Operation(summary = "获取每日签到任务", description = "查询每日签到任务模板配置")
    public Result<TaskTemplateResponse> getDailyCheckinTemplate() {
        try {
            log.info("REST查询每日签到任务模板");
            // 通过类型查询每日签到任务
            Result<List<TaskTemplateResponse>> result = taskTemplateFacadeService.getTaskTemplatesByType("DAILY_CHECKIN");
            if (result.getSuccess() && !result.getData().isEmpty()) {
                return Result.success(result.getData().get(0));
            } else {
                return Result.error("DAILY_CHECKIN_NOT_FOUND", "每日签到任务未配置");
            }
        } catch (Exception e) {
            log.error("查询每日签到任务模板异常", e);
            return Result.error("GET_DAILY_CHECKIN_ERROR", "查询每日签到任务失败: " + e.getMessage());
        }
    }
}