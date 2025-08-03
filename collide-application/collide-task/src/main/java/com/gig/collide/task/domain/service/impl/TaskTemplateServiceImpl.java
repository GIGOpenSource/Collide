package com.gig.collide.task.domain.service.impl;

import com.gig.collide.api.task.constant.TaskStatusConstant;
import com.gig.collide.api.task.constant.TaskTypeConstant;
import com.gig.collide.task.domain.entity.TaskTemplate;
import com.gig.collide.task.domain.service.TaskTemplateService;
import com.gig.collide.task.infrastructure.mapper.TaskTemplateMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 任务模板领域服务实现 - 对应 t_task_template 表
 * 负责任务配置和奖励规则管理
 * 
 * @author GIG Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskTemplateServiceImpl implements TaskTemplateService {

    private final TaskTemplateMapper taskTemplateMapper;

    @Override
    public TaskTemplate getTaskTemplateById(Long id) {
        try {
            if (id == null || id <= 0) {
                log.warn("任务模板ID无效: {}", id);
                return null;
            }
            
            TaskTemplate taskTemplate = taskTemplateMapper.findById(id);
            log.debug("根据ID查询任务模板: id={}, result={}", id, taskTemplate != null ? "found" : "not found");
            return taskTemplate;
        } catch (Exception e) {
            log.error("根据ID查询任务模板失败: id={}", id, e);
            throw new RuntimeException("查询任务模板失败", e);
        }
    }

    @Override
    public List<TaskTemplate> getTaskTemplatesByType(String taskType) {
        try {
            if (taskType == null || taskType.trim().isEmpty()) {
                log.warn("任务类型无效: {}", taskType);
                return List.of();
            }
            
            List<TaskTemplate> templates = taskTemplateMapper.findByTaskType(taskType);
            log.debug("根据类型查询任务模板: taskType={}, count={}", taskType, templates.size());
            return templates;
        } catch (Exception e) {
            log.error("根据类型查询任务模板失败: taskType={}", taskType, e);
            throw new RuntimeException("查询任务模板失败", e);
        }
    }

    @Override
    public List<TaskTemplate> getAvailableTaskTemplates() {
        try {
            List<TaskTemplate> templates = taskTemplateMapper.findAvailableTaskTemplates();
            log.debug("查询可用任务模板: count={}", templates.size());
            return templates;
        } catch (Exception e) {
            log.error("查询可用任务模板失败", e);
            throw new RuntimeException("查询可用任务模板失败", e);
        }
    }

    @Override
    public TaskTemplate getDailyCheckinTemplate() {
        try {
            TaskTemplate template = taskTemplateMapper.getDailyCheckinTemplate();
            log.debug("查询每日签到任务模板: result={}", template != null ? "found" : "not found");
            
            if (template == null) {
                log.warn("未找到可用的每日签到任务模板");
            }
            
            return template;
        } catch (Exception e) {
            log.error("查询每日签到任务模板失败", e);
            throw new RuntimeException("查询每日签到任务模板失败", e);
        }
    }

    @Override
    public boolean isTaskTemplateAvailable(Long id) {
        try {
            TaskTemplate template = getTaskTemplateById(id);
            boolean available = template != null && template.isEnabled();
            log.debug("检查任务模板可用性: id={}, available={}", id, available);
            return available;
        } catch (Exception e) {
            log.error("检查任务模板可用性失败: id={}", id, e);
            return false;
        }
    }

    @Override
    public boolean isDailyCheckinTask(Long id) {
        try {
            TaskTemplate template = getTaskTemplateById(id);
            boolean isDailyCheckin = template != null && template.isDailyCheckinTask();
            log.debug("验证是否为每日签到任务: id={}, isDailyCheckin={}", id, isDailyCheckin);
            return isDailyCheckin;
        } catch (Exception e) {
            log.error("验证每日签到任务失败: id={}", id, e);
            return false;
        }
    }

    @Override
    public Long countTaskTemplates(String taskType, Integer status) {
        try {
            Long count = taskTemplateMapper.countTaskTemplates(taskType, status);
            log.debug("统计任务模板数量: taskType={}, status={}, count={}", taskType, status, count);
            return count;
        } catch (Exception e) {
            log.error("统计任务模板数量失败: taskType={}, status={}", taskType, status, e);
            throw new RuntimeException("统计任务模板数量失败", e);
        }
    }
}