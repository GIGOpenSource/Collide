package com.gig.collide.task.facade;

import com.gig.collide.api.task.TaskTemplateFacadeService;
import com.gig.collide.api.task.response.TaskTemplateResponse;
import com.gig.collide.task.domain.entity.TaskTemplate;
import com.gig.collide.task.domain.service.TaskTemplateService;
import com.gig.collide.task.infrastructure.cache.TaskCacheConstant;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.apache.dubbo.config.annotation.DubboService;
import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheType;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 任务模板门面服务实现 - 签到专用版
 * Dubbo独立微服务提供者 - 负责任务配置和奖励规则管理
 * 
 * @author GIG Team
 * @version 1.0.0 (签到专用版)
 */
@Slf4j
@DubboService(version = "1.0.0", interfaceClass = TaskTemplateFacadeService.class)
@RequiredArgsConstructor
public class TaskTemplateFacadeServiceImpl implements TaskTemplateFacadeService {

    private final TaskTemplateService taskTemplateService;

    @Override
    @Cached(name = TaskCacheConstant.TASK_TEMPLATE_DETAIL_CACHE,
            key = TaskCacheConstant.TASK_TEMPLATE_DETAIL_KEY,
            expire = TaskCacheConstant.TASK_TEMPLATE_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<TaskTemplateResponse> getTaskTemplate(Long id) {
        try {
            log.info("RPC查询任务模板详情: id={}", id);
            
            if (id == null || id <= 0) {
                return Result.error("INVALID_TASK_ID", "任务ID无效");
            }
            
            TaskTemplate taskTemplate = taskTemplateService.getTaskTemplateById(id);
            if (taskTemplate == null) {
                return Result.error("TASK_NOT_FOUND", "任务模板不存在");
            }
            
            TaskTemplateResponse response = convertToResponse(taskTemplate);
            log.info("任务模板查询成功: id={}, taskName={}", id, taskTemplate.getTaskName());
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询任务模板详情失败: id={}", id, e);
            return Result.error("GET_TASK_TEMPLATE_ERROR", "查询任务模板失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = TaskCacheConstant.TASK_TEMPLATE_LIST_CACHE,
            expire = TaskCacheConstant.TASK_TEMPLATE_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<List<TaskTemplateResponse>> getAvailableTaskTemplates() {
        try {
            log.info("RPC查询可用任务模板列表");
            
            List<TaskTemplate> templates = taskTemplateService.getAvailableTaskTemplates();
            List<TaskTemplateResponse> responses = templates.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            log.info("可用任务模板查询成功: count={}", responses.size());
            return Result.success(responses);
        } catch (Exception e) {
            log.error("查询可用任务模板列表失败", e);
            return Result.error("GET_AVAILABLE_TASKS_ERROR", "查询可用任务模板失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = TaskCacheConstant.DAILY_CHECKIN_TEMPLATE_CACHE,
            expire = TaskCacheConstant.TASK_TEMPLATE_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<List<TaskTemplateResponse>> getTaskTemplatesByType(String taskType) {
        try {
            log.info("RPC根据类型查询任务模板: taskType={}", taskType);
            
            if (taskType == null || taskType.trim().isEmpty()) {
                return Result.error("INVALID_TASK_TYPE", "任务类型无效");
            }
            
            List<TaskTemplate> templates = taskTemplateService.getTaskTemplatesByType(taskType);
            List<TaskTemplateResponse> responses = templates.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            log.info("按类型查询任务模板成功: taskType={}, count={}", taskType, responses.size());
            return Result.success(responses);
        } catch (Exception e) {
            log.error("按类型查询任务模板失败: taskType={}", taskType, e);
            return Result.error("GET_TASKS_BY_TYPE_ERROR", "按类型查询任务模板失败: " + e.getMessage());
        }
    }

    /**
     * 获取每日签到任务模板 - 便捷方法
     */
    @Cached(name = TaskCacheConstant.DAILY_CHECKIN_TEMPLATE_CACHE,
            expire = TaskCacheConstant.TASK_TEMPLATE_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<TaskTemplateResponse> getDailyCheckinTemplate() {
        try {
            log.info("RPC查询每日签到任务模板");
            
            TaskTemplate template = taskTemplateService.getDailyCheckinTemplate();
            if (template == null) {
                return Result.error("DAILY_CHECKIN_NOT_FOUND", "每日签到任务未配置");
            }
            
            TaskTemplateResponse response = convertToResponse(template);
            log.info("每日签到任务模板查询成功: id={}, taskName={}", template.getId(), template.getTaskName());
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询每日签到任务模板失败", e);
            return Result.error("GET_DAILY_CHECKIN_ERROR", "查询每日签到任务失败: " + e.getMessage());
        }
    }

    /**
     * 转换实体为响应DTO
     */
    private TaskTemplateResponse convertToResponse(TaskTemplate taskTemplate) {
        if (taskTemplate == null) {
            return null;
        }
        
        TaskTemplateResponse response = new TaskTemplateResponse();
        BeanUtils.copyProperties(taskTemplate, response);
        
        // 设置状态描述
        response.setStatusDesc(taskTemplate.getStatusDesc());
        
        return response;
    }
}