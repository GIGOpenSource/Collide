package com.gig.collide.api.task;

import com.gig.collide.api.task.response.TaskTemplateResponse;
import com.gig.collide.web.vo.Result;

import java.util.List;

/**
 * 任务模板门面服务接口 - 签到专用版
 * 专注于签到任务的查询功能
 * 
 * @author GIG Team
 * @version 1.0.0 (签到专用版)
 * @since 2024-01-16
 */
public interface TaskTemplateFacadeService {

    /**
     * 获取任务模板详情
     */
    Result<TaskTemplateResponse> getTaskTemplate(Long id);

    /**
     * 获取所有可用的任务模板
     */
    Result<List<TaskTemplateResponse>> getAvailableTaskTemplates();

    /**
     * 获取指定类型的任务模板
     */
    Result<List<TaskTemplateResponse>> getTaskTemplatesByType(String taskType);
}