package com.gig.collide.task.domain.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.task.domain.entity.TaskTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 任务模板业务服务接口 - 简洁版
 * 基于task-simple.sql的单表设计
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
public interface TaskTemplateService {

    // =================== 基础操作 ===================

    /**
     * 创建任务模板
     */
    TaskTemplate createTaskTemplate(TaskTemplate taskTemplate);

    /**
     * 根据ID获取任务模板
     */
    TaskTemplate getTaskTemplateById(Long id);

    /**
     * 更新任务模板
     */
    boolean updateTaskTemplate(TaskTemplate taskTemplate);

    /**
     * 删除任务模板
     */
    boolean deleteTaskTemplate(Long id);

    /**
     * 批量删除任务模板
     */
    boolean batchDeleteTaskTemplates(List<Long> ids);

    // =================== 查询操作 ===================

    /**
     * 分页查询任务模板
     */
    Page<TaskTemplate> queryTaskTemplates(String taskName, String taskType, String taskCategory,
                                         String taskAction, Boolean isActive, LocalDate startDate, 
                                         LocalDate endDate, String orderBy, String orderDirection,
                                         Integer currentPage, Integer pageSize);

    /**
     * 查询所有启用的任务模板
     */
    List<TaskTemplate> getAllActiveTasks();

    /**
     * 根据类型查询可用任务模板
     */
    List<TaskTemplate> getAvailableTasksByType(String taskType);

    /**
     * 根据动作查询任务模板
     */
    List<TaskTemplate> getTasksByAction(String taskAction);

    /**
     * 根据分类查询任务模板
     */
    List<TaskTemplate> getTasksByCategory(String taskCategory);

    /**
     * 搜索任务模板
     */
    Page<TaskTemplate> searchTaskTemplates(String keyword, String taskType, String taskCategory,
                                          Boolean isActive, Integer currentPage, Integer pageSize);

    // =================== 状态管理 ===================

    /**
     * 启用任务模板
     */
    boolean enableTaskTemplate(Long id);

    /**
     * 禁用任务模板
     */
    boolean disableTaskTemplate(Long id);

    /**
     * 批量更新任务状态
     */
    boolean batchUpdateTaskStatus(List<Long> ids, Boolean isActive);

    /**
     * 更新任务排序
     */
    boolean updateTaskOrder(Long id, Integer sortOrder);

    /**
     * 批量更新任务排序
     */
    boolean batchUpdateTaskOrder(List<Map<String, Object>> taskOrderList);

    // =================== 统计操作 ===================

    /**
     * 统计各类型任务数量
     */
    Map<String, Long> countTasksByType();

    /**
     * 统计各分类任务数量
     */
    Map<String, Long> countTasksByCategory();

    /**
     * 统计启用任务总数
     */
    Long countActiveTasks();

    /**
     * 统计过期任务数量
     */
    Long countExpiredTasks();

    /**
     * 获取任务模板统计信息
     */
    Map<String, Object> getTaskTemplateStatistics();

    // =================== 特殊操作 ===================

    /**
     * 查询即将过期的任务
     */
    List<TaskTemplate> getTasksExpiringSoon(Integer days);

    /**
     * 处理定时任务激活
     */
    int activateScheduledTasks();

    /**
     * 处理过期任务停用
     */
    int deactivateExpiredTasks();

    /**
     * 复制任务模板
     */
    TaskTemplate copyTaskTemplate(Long id, String newTaskName);

    // =================== 验证方法 ===================

    /**
     * 验证任务名称是否存在
     */
    boolean isTaskNameExists(String taskName, Long excludeId);

    /**
     * 验证任务动作是否存在
     */
    boolean isTaskActionExists(String taskAction);

    /**
     * 验证任务模板是否可用
     */
    boolean isTaskTemplateAvailable(Long id);

    /**
     * 获取分类下的最大排序值
     */
    Integer getMaxOrderByCategory(String taskCategory);

    // =================== 工具方法 ===================

    /**
     * 根据排序值范围查询任务
     */
    List<TaskTemplate> getTasksByOrderRange(Integer minOrder, Integer maxOrder);

    /**
     * 获取用户可用的任务列表（根据日期和类型筛选）
     */
    List<TaskTemplate> getUserAvailableTasks(String taskType, LocalDate taskDate);

    /**
     * 检查任务模板配置是否完整
     */
    boolean isTaskTemplateConfigComplete(Long id);

    /**
     * 获取任务分类列表
     */
    List<String> getTaskCategories();

    /**
     * 获取任务动作列表
     */
    List<String> getTaskActions();
}