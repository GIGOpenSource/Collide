package com.gig.collide.task.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.task.domain.entity.TaskTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务模板Mapper接口 - 对应 t_task_template 表
 * 负责任务配置和奖励规则管理
 * 
 * @author GIG Team
 * @version 1.0.0
 */
@Mapper
public interface TaskTemplateMapper extends BaseMapper<TaskTemplate> {

    /**
     * 根据ID查询任务模板
     */
    TaskTemplate findById(@Param("id") Long id);

    /**
     * 根据任务类型查询任务模板列表
     */
    List<TaskTemplate> findByTaskType(@Param("taskType") String taskType);

    /**
     * 查询所有可用的任务模板
     * 状态为启用(1)，按排序权重排序
     */
    List<TaskTemplate> findAvailableTaskTemplates();

    /**
     * 根据状态查询任务模板
     */
    List<TaskTemplate> findByStatus(@Param("status") Integer status);

    /**
     * 检查任务模板是否存在
     */
    int checkTaskTemplateExists(@Param("id") Long id);

    /**
     * 统计任务模板数量
     */
    Long countTaskTemplates(@Param("taskType") String taskType, @Param("status") Integer status);

    /**
     * 获取每日签到任务模板
     * 获取类型为DAILY_CHECKIN且状态为启用的任务模板
     */
    TaskTemplate getDailyCheckinTemplate();
}