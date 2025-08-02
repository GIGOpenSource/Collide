package com.gig.collide.task.domain.service.impl;

import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.task.domain.constant.*;
import com.gig.collide.task.domain.entity.TaskTemplate;
import com.gig.collide.task.domain.service.TaskTemplateService;
import com.gig.collide.task.infrastructure.mapper.TaskTemplateMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 任务模板业务服务实现类 - 优化版
 * 基于优化后的task-simple.sql，支持数字常量和高性能索引
 * 
 * @author GIG Team
 * @version 3.0.0 (优化版)
 * @since 2024-01-16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskTemplateServiceImpl implements TaskTemplateService {

    private final TaskTemplateMapper taskTemplateMapper;

    // =================== 基础操作 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = "task:template", key = "*")
    public TaskTemplate createTaskTemplate(TaskTemplate taskTemplate) {
        log.info("创建任务模板: taskName={}, taskType={}", taskTemplate.getTaskName(), taskTemplate.getTaskType());
        
        // 验证任务名称唯一性
        if (isTaskNameExists(taskTemplate.getTaskName(), null)) {
            throw new RuntimeException("任务名称已存在: " + taskTemplate.getTaskName());
        }
        
        // 设置默认值
        if (taskTemplate.getSortOrder() == null) {
            Integer maxOrder = getMaxOrderByCategory(taskTemplate.getTaskCategory());
            taskTemplate.setSortOrder(maxOrder != null ? maxOrder + 1 : 1);
        }
        
        if (taskTemplate.getIsActive() == null) {
            taskTemplate.setIsActive(true);
        }
        
        int result = taskTemplateMapper.insert(taskTemplate);
        if (result > 0) {
            log.info("任务模板创建成功: id={}", taskTemplate.getId());
            return taskTemplate;
        } else {
            throw new RuntimeException("任务模板创建失败");
        }
    }

    @Override
    @Cached(name = "task:template", key = "#id", expire = 30, timeUnit = TimeUnit.MINUTES)
    public TaskTemplate getTaskTemplateById(Long id) {
        log.debug("查询任务模板: id={}", id);
        return taskTemplateMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = "task:template", key = "*")
    public boolean updateTaskTemplate(TaskTemplate taskTemplate) {
        log.info("更新任务模板: id={}, taskName={}", taskTemplate.getId(), taskTemplate.getTaskName());
        
        // 验证任务名称唯一性
        if (isTaskNameExists(taskTemplate.getTaskName(), taskTemplate.getId())) {
            throw new RuntimeException("任务名称已存在: " + taskTemplate.getTaskName());
        }
        
        int result = taskTemplateMapper.updateById(taskTemplate);
        if (result > 0) {
            log.info("任务模板更新成功: id={}", taskTemplate.getId());
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = "task:template", key = "*")
    public boolean deleteTaskTemplate(Long id) {
        log.info("删除任务模板: id={}", id);
        
        int result = taskTemplateMapper.deleteById(id);
        if (result > 0) {
            log.info("任务模板删除成功: id={}", id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = "task:template", key = "*")
    public boolean batchDeleteTaskTemplates(List<Long> ids) {
        log.info("批量删除任务模板: ids={}", ids);
        
        int result = taskTemplateMapper.deleteBatchIds(ids);
        if (result > 0) {
            log.info("批量删除任务模板成功: count={}", result);
            return true;
        }
        return false;
    }

    // =================== 查询操作 ===================

    @Override
    public Page<TaskTemplate> queryTaskTemplates(String taskName, Integer taskType, Integer taskCategory,
                                                Integer taskAction, Boolean isActive, LocalDate startDate,
                                                LocalDate endDate, String orderBy, String orderDirection,
                                                Integer currentPage, Integer pageSize) {
        log.debug("分页查询任务模板: page={}, size={}, taskType={}, taskCategory={}", currentPage, pageSize, taskType, taskCategory);
        
        Page<TaskTemplate> page = new Page<>(currentPage, pageSize);
        return taskTemplateMapper.findWithConditions(page, taskName, taskType, taskCategory, taskAction,
                isActive, startDate, endDate, orderBy, orderDirection);
    }

    @Override
    @Cached(name = "task:template:active", expire = 10, timeUnit = TimeUnit.MINUTES)
    public List<TaskTemplate> getAllActiveTasks() {
        log.debug("查询所有启用的任务模板");
        return taskTemplateMapper.findAllActiveTasks();
    }

    @Override
    @Cached(name = "task:template:type", key = "#taskType", expire = 15, timeUnit = TimeUnit.MINUTES)
    public List<TaskTemplate> getAvailableTasksByType(Integer taskType) {
        log.debug("根据类型查询可用任务: taskType={} ({})", taskType, TaskTypeConstant.getTypeName(taskType));
        return taskTemplateMapper.findAvailableTasksByType(taskType, LocalDate.now());
    }

    @Override
    public List<TaskTemplate> getTasksByAction(Integer taskAction) {
        log.debug("根据动作查询任务: taskAction={} ({})", taskAction, TaskActionConstant.getActionName(taskAction));
        return taskTemplateMapper.findTasksByAction(taskAction, true, LocalDate.now());
    }

    @Override
    public List<TaskTemplate> getTasksByCategory(Integer taskCategory) {
        log.debug("根据分类查询任务: taskCategory={} ({})", taskCategory, TaskCategoryConstant.getCategoryName(taskCategory));
        return taskTemplateMapper.findTasksByCategory(taskCategory, true);
    }

    @Override
    public Page<TaskTemplate> searchTaskTemplates(String keyword, Integer taskType, Integer taskCategory,
                                                 Boolean isActive, Integer currentPage, Integer pageSize) {
        log.debug("搜索任务模板: keyword={}, taskType={}, taskCategory={}", keyword, taskType, taskCategory);
        
        Page<TaskTemplate> page = new Page<>(currentPage, pageSize);
        return taskTemplateMapper.searchTasks(page, keyword, taskType, taskCategory, isActive);
    }

    // =================== 状态管理 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = "task:template", key = "*")
    public boolean enableTaskTemplate(Long id) {
        log.info("启用任务模板: id={}", id);
        return updateTaskStatus(id, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = "task:template", key = "*")
    public boolean disableTaskTemplate(Long id) {
        log.info("禁用任务模板: id={}", id);
        return updateTaskStatus(id, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = "task:template", key = "*")
    public boolean batchUpdateTaskStatus(List<Long> ids, Boolean isActive) {
        log.info("批量更新任务状态: ids={}, isActive={}", ids, isActive);
        
        int result = taskTemplateMapper.batchUpdateTaskStatus(ids, isActive);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = "task:template", key = "*")
    public boolean updateTaskOrder(Long id, Integer sortOrder) {
        log.info("更新任务排序: id={}, sortOrder={}", id, sortOrder);
        
        TaskTemplate taskTemplate = new TaskTemplate();
        taskTemplate.setId(id);
        taskTemplate.setSortOrder(sortOrder);
        
        int result = taskTemplateMapper.updateById(taskTemplate);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = "task:template", key = "*")
    public boolean batchUpdateTaskOrder(List<Map<String, Object>> taskOrderList) {
        log.info("批量更新任务排序: count={}", taskOrderList.size());
        
        int result = taskTemplateMapper.batchUpdateTaskOrder(taskOrderList);
        return result > 0;
    }

    // =================== 统计操作 ===================

    @Override
    @Cached(name = "task:template:stat:type", expire = 30, timeUnit = TimeUnit.MINUTES)
    public Map<String, Long> countTasksByType() {
        log.debug("统计各类型任务数量");
        
        List<Map<String, Object>> result = taskTemplateMapper.countTasksByType(true);
        return result.stream().collect(Collectors.toMap(
                map -> (String) map.get("task_type"),
                map -> ((Number) map.get("count")).longValue()
        ));
    }

    @Override
    @Cached(name = "task:template:stat:category", expire = 30, timeUnit = TimeUnit.MINUTES)
    public Map<String, Long> countTasksByCategory() {
        log.debug("统计各分类任务数量");
        
        List<Map<String, Object>> result = taskTemplateMapper.countTasksByCategory(true);
        return result.stream().collect(Collectors.toMap(
                map -> (String) map.get("task_category"),
                map -> ((Number) map.get("count")).longValue()
        ));
    }

    @Override
    @Cached(name = "task:template:stat:active", expire = 15, timeUnit = TimeUnit.MINUTES)
    public Long countActiveTasks() {
        log.debug("统计启用任务总数");
        return taskTemplateMapper.countActiveTasks();
    }

    @Override
    public Long countExpiredTasks() {
        log.debug("统计过期任务数量");
        return taskTemplateMapper.countExpiredTasks(LocalDate.now());
    }

    @Override
    public Map<String, Object> getTaskTemplateStatistics() {
        log.debug("获取任务模板统计信息");
        
        Map<String, Object> statistics = new java.util.HashMap<>();
        statistics.put("totalTasks", taskTemplateMapper.selectCount(null));
        statistics.put("activeTasks", countActiveTasks());
        statistics.put("expiredTasks", countExpiredTasks());
        statistics.put("tasksByType", countTasksByType());
        statistics.put("tasksByCategory", countTasksByCategory());
        
        return statistics;
    }

    // =================== 特殊操作 ===================

    @Override
    public List<TaskTemplate> getTasksExpiringSoon(Integer days) {
        log.debug("查询即将过期的任务: days={}", days);
        return taskTemplateMapper.findTasksExpiringSoon(days, LocalDate.now());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = "task:template", key = "*")
    public int activateScheduledTasks() {
        log.info("处理定时任务激活");
        
        List<TaskTemplate> tasksToActivate = taskTemplateMapper.findTasksToActivate(LocalDate.now());
        if (!tasksToActivate.isEmpty()) {
            List<Long> taskIds = tasksToActivate.stream().map(TaskTemplate::getId).collect(Collectors.toList());
            batchUpdateTaskStatus(taskIds, true);
            log.info("激活定时任务完成: count={}", tasksToActivate.size());
        }
        
        return tasksToActivate.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = "task:template", key = "*")
    public int deactivateExpiredTasks() {
        log.info("处理过期任务停用");
        
        List<TaskTemplate> tasksToDeactivate = taskTemplateMapper.findTasksToDeactivate(LocalDate.now());
        if (!tasksToDeactivate.isEmpty()) {
            List<Long> taskIds = tasksToDeactivate.stream().map(TaskTemplate::getId).collect(Collectors.toList());
            batchUpdateTaskStatus(taskIds, false);
            log.info("停用过期任务完成: count={}", tasksToDeactivate.size());
        }
        
        return tasksToDeactivate.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskTemplate copyTaskTemplate(Long id, String newTaskName) {
        log.info("复制任务模板: id={}, newTaskName={}", id, newTaskName);
        
        TaskTemplate original = getTaskTemplateById(id);
        if (original == null) {
            throw new RuntimeException("原任务模板不存在: " + id);
        }
        
        TaskTemplate copy = new TaskTemplate();
        copy.setTaskName(newTaskName);
        copy.setTaskDesc(original.getTaskDesc());
        copy.setTaskType(original.getTaskType());
        copy.setTaskCategory(original.getTaskCategory());
        copy.setTaskAction(original.getTaskAction());
        copy.setTargetCount(original.getTargetCount());
        copy.setIsActive(false); // 默认设为未启用
        
        return createTaskTemplate(copy);
    }

    // =================== 验证方法 ===================

    @Override
    public boolean isTaskNameExists(String taskName, Long excludeId) {
        return taskTemplateMapper.existsByTaskName(taskName, excludeId);
    }

    @Override
    public boolean isTaskActionExists(Integer taskAction) {
        log.debug("检查任务动作是否存在: taskAction={} ({})", taskAction, TaskActionConstant.getActionName(taskAction));
        return taskTemplateMapper.existsByTaskAction(taskAction, true);
    }

    @Override
    public boolean isTaskTemplateAvailable(Long id) {
        TaskTemplate taskTemplate = getTaskTemplateById(id);
        return taskTemplate != null && taskTemplate.isAvailable();
    }

    @Override
    public Integer getMaxOrderByCategory(Integer taskCategory) {
        return taskTemplateMapper.findMaxOrderByCategory(taskCategory);
    }

    // =================== 工具方法 ===================

    @Override
    public List<TaskTemplate> getTasksByOrderRange(Integer minOrder, Integer maxOrder) {
        return taskTemplateMapper.findTasksByOrderRange(minOrder, maxOrder, true);
    }

    @Override
    public List<TaskTemplate> getUserAvailableTasks(Integer taskType, LocalDate taskDate) {
        log.debug("获取用户可用任务: taskType={} ({}), taskDate={}", 
                taskType, TaskTypeConstant.getTypeName(taskType), taskDate);
        return taskTemplateMapper.findAvailableTasksByType(taskType, taskDate);
    }

    @Override
    public boolean isTaskTemplateConfigComplete(Long id) {
        TaskTemplate taskTemplate = getTaskTemplateById(id);
        if (taskTemplate == null) {
            return false;
        }
        
        // 检查必要字段是否完整（使用数字常量验证）
        return taskTemplate.getTaskName() != null && !taskTemplate.getTaskName().trim().isEmpty() &&
               taskTemplate.getTaskType() != null && TaskTypeConstant.isValidType(taskTemplate.getTaskType()) &&
               taskTemplate.getTaskCategory() != null && TaskCategoryConstant.isValidCategory(taskTemplate.getTaskCategory()) &&
               taskTemplate.getTaskAction() != null && TaskActionConstant.isValidAction(taskTemplate.getTaskAction()) &&
               taskTemplate.getTargetCount() != null && taskTemplate.getTargetCount() > 0;
    }

    @Override
    @Cached(name = "task:template:categories", expire = 60, timeUnit = TimeUnit.MINUTES)
    public List<Integer> getTaskCategories() {
        LambdaQueryWrapper<TaskTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(TaskTemplate::getTaskCategory)
               .eq(TaskTemplate::getIsActive, true)
               .groupBy(TaskTemplate::getTaskCategory);
        
        return taskTemplateMapper.selectList(wrapper)
                .stream()
                .map(TaskTemplate::getTaskCategory)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    @Cached(name = "task:template:actions", expire = 60, timeUnit = TimeUnit.MINUTES)
    public List<Integer> getTaskActions() {
        LambdaQueryWrapper<TaskTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(TaskTemplate::getTaskAction)
               .eq(TaskTemplate::getIsActive, true)
               .groupBy(TaskTemplate::getTaskAction);
        
        return taskTemplateMapper.selectList(wrapper)
                .stream()
                .map(TaskTemplate::getTaskAction)
                .distinct()
                .collect(Collectors.toList());
    }

    // =================== 私有方法 ===================

    private boolean updateTaskStatus(Long id, Boolean isActive) {
        TaskTemplate taskTemplate = new TaskTemplate();
        taskTemplate.setId(id);
        taskTemplate.setIsActive(isActive);
        
        int result = taskTemplateMapper.updateById(taskTemplate);
        return result > 0;
    }
}