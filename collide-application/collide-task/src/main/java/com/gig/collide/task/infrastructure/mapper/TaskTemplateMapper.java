package com.gig.collide.task.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.task.domain.entity.TaskTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 任务模板数据访问接口 - 优化版
 * 基于task-simple.sql的数字常量设计，充分利用HASH索引
 * 
 * @author GIG Team
 * @version 3.0.0 (优化版)
 * @since 2024-01-16
 */
@Mapper
public interface TaskTemplateMapper extends BaseMapper<TaskTemplate> {

    // =================== 基础查询 ===================

    /**
     * 根据任务类型查询可用任务模板（使用数字常量优化）
     */
    List<TaskTemplate> findAvailableTasksByType(@Param("taskType") Integer taskType,
                                               @Param("currentDate") LocalDate currentDate);

    /**
     * 根据任务动作查询任务模板（使用HASH索引优化）
     */
    List<TaskTemplate> findTasksByAction(@Param("taskAction") Integer taskAction,
                                        @Param("isActive") Boolean isActive,
                                        @Param("currentDate") LocalDate currentDate);

    /**
     * 条件查询任务模板（数字常量优化版）
     */
    Page<TaskTemplate> findWithConditions(Page<TaskTemplate> page,
                                         @Param("taskName") String taskName,
                                         @Param("taskType") Integer taskType,
                                         @Param("taskCategory") Integer taskCategory,
                                         @Param("taskAction") Integer taskAction,
                                         @Param("isActive") Boolean isActive,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate,
                                         @Param("orderBy") String orderBy,
                                         @Param("orderDirection") String orderDirection);

    /**
     * 查询所有启用的任务模板（按排序值排序）
     */
    List<TaskTemplate> findAllActiveTasks();

    /**
     * 查询指定分类的任务模板（使用数字常量优化）
     */
    List<TaskTemplate> findTasksByCategory(@Param("taskCategory") Integer taskCategory,
                                          @Param("isActive") Boolean isActive);

    // =================== 统计查询 ===================

    /**
     * 统计各类型任务数量
     */
    List<java.util.Map<String, Object>> countTasksByType(@Param("isActive") Boolean isActive);

    /**
     * 统计各分类任务数量
     */
    List<java.util.Map<String, Object>> countTasksByCategory(@Param("isActive") Boolean isActive);

    /**
     * 统计启用任务总数
     */
    Long countActiveTasks();

    /**
     * 统计过期任务数量
     */
    Long countExpiredTasks(@Param("currentDate") LocalDate currentDate);

    // =================== 批量操作 ===================

    /**
     * 批量更新任务状态
     */
    int batchUpdateTaskStatus(@Param("taskIds") List<Long> taskIds,
                             @Param("isActive") Boolean isActive);

    /**
     * 批量更新任务排序
     */
    int batchUpdateTaskOrder(@Param("taskOrderList") List<java.util.Map<String, Object>> taskOrderList);

    // =================== 特殊查询 ===================

    /**
     * 查询即将过期的任务（指定天数内）
     */
    List<TaskTemplate> findTasksExpiringSoon(@Param("days") Integer days,
                                           @Param("currentDate") LocalDate currentDate);

    /**
     * 查询需要激活的定时任务
     */
    List<TaskTemplate> findTasksToActivate(@Param("currentDate") LocalDate currentDate);

    /**
     * 查询需要停用的过期任务
     */
    List<TaskTemplate> findTasksToDeactivate(@Param("currentDate") LocalDate currentDate);

    /**
     * 根据排序值范围查询任务
     */
    List<TaskTemplate> findTasksByOrderRange(@Param("minOrder") Integer minOrder,
                                           @Param("maxOrder") Integer maxOrder,
                                           @Param("isActive") Boolean isActive);

    /**
     * 查询同一分类下的最大排序值（使用数字常量优化）
     */
    Integer findMaxOrderByCategory(@Param("taskCategory") Integer taskCategory);

    /**
     * 搜索任务模板（支持名称和描述模糊搜索，数字常量优化版）
     */
    Page<TaskTemplate> searchTasks(Page<TaskTemplate> page,
                                  @Param("keyword") String keyword,
                                  @Param("taskType") Integer taskType,
                                  @Param("taskCategory") Integer taskCategory,
                                  @Param("isActive") Boolean isActive);

    // =================== 验证查询 ===================

    /**
     * 检查任务名称是否存在（用于唯一性验证）
     */
    boolean existsByTaskName(@Param("taskName") String taskName,
                            @Param("excludeId") Long excludeId);

    /**
     * 检查指定动作的任务是否存在（使用数字常量优化）
     */
    boolean existsByTaskAction(@Param("taskAction") Integer taskAction,
                              @Param("isActive") Boolean isActive);
}