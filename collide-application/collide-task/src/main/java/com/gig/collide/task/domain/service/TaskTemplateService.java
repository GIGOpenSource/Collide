package com.gig.collide.task.domain.service;

import com.gig.collide.task.domain.entity.TaskTemplate;

import java.util.List;

/**
 * 任务模板领域服务接口 - 对应 t_task_template 表
 * 负责任务配置和奖励规则管理
 * 
 * @author GIG Team
 * @version 1.0.0
 */
public interface TaskTemplateService {

    /**
     * 根据ID查询任务模板
     */
    TaskTemplate getTaskTemplateById(Long id);

    /**
     * 根据任务类型查询任务模板列表
     */
    List<TaskTemplate> getTaskTemplatesByType(String taskType);

    /**
     * 获取所有可用的任务模板
     * 状态为启用，按排序权重排序
     */
    List<TaskTemplate> getAvailableTaskTemplates();

    /**
     * 获取每日签到任务模板
     * 获取类型为DAILY_CHECKIN且状态为启用的任务模板
     */
    TaskTemplate getDailyCheckinTemplate();

    /**
     * 检查任务模板是否存在且可用
     */
    boolean isTaskTemplateAvailable(Long id);

    /**
     * 验证任务模板是否为每日签到类型
     */
    boolean isDailyCheckinTask(Long id);

    /**
     * 统计任务模板数量
     */
    Long countTaskTemplates(String taskType, Integer status);
}