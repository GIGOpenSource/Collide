package com.gig.collide.task.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.task.domain.entity.UserTaskRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 用户任务记录数据访问接口 - 优化版
 * 基于task-simple.sql的数字常量设计，充分利用复合索引
 * 
 * @author GIG Team
 * @version 3.0.0 (优化版)
 * @since 2024-01-16
 */
@Mapper
public interface UserTaskRecordMapper extends BaseMapper<UserTaskRecord> {

    // =================== 基础查询 ===================

    /**
     * 查询用户指定日期的任务记录（利用用户日期复合索引）
     */
    List<UserTaskRecord> findUserTasksByDate(@Param("userId") Long userId,
                                           @Param("taskDate") LocalDate taskDate,
                                           @Param("taskType") Integer taskType);

    /**
     * 查询用户今日任务记录
     */
    List<UserTaskRecord> findUserTodayTasks(@Param("userId") Long userId);

    /**
     * 查询用户指定类型的任务记录（使用数字常量优化）
     */
    Page<UserTaskRecord> findUserTasksByType(Page<UserTaskRecord> page,
                                            @Param("userId") Long userId,
                                            @Param("taskType") Integer taskType,
                                            @Param("taskDate") LocalDate taskDate);

    /**
     * 条件查询用户任务记录（数字常量优化版）
     */
    Page<UserTaskRecord> findWithConditions(Page<UserTaskRecord> page,
                                           @Param("userId") Long userId,
                                           @Param("taskId") Long taskId,
                                           @Param("taskType") Integer taskType,
                                           @Param("taskCategory") Integer taskCategory,
                                           @Param("isCompleted") Boolean isCompleted,
                                           @Param("isRewarded") Boolean isRewarded,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate,
                                           @Param("orderBy") String orderBy,
                                           @Param("orderDirection") String orderDirection);

    /**
     * 查询用户可领取奖励的任务
     */
    List<UserTaskRecord> findUserClaimableTasks(@Param("userId") Long userId,
                                              @Param("taskDate") LocalDate taskDate);

    /**
     * 查询用户未完成的任务（使用数字常量优化）
     */
    List<UserTaskRecord> findUserIncompleteTasks(@Param("userId") Long userId,
                                                @Param("taskDate") LocalDate taskDate,
                                                @Param("taskType") Integer taskType);

    // =================== 统计查询 ===================

    /**
     * 统计用户任务完成情况
     */
    java.util.Map<String, Object> getUserTaskStatistics(@Param("userId") Long userId,
                                                        @Param("taskDate") LocalDate taskDate);

    /**
     * 统计用户指定类型任务完成数（使用数字常量优化）
     */
    Long countCompletedTasksByType(@Param("userId") Long userId,
                                  @Param("taskType") Integer taskType,
                                  @Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate);

    /**
     * 统计用户连续登录天数（使用数字常量1=login优化）
     */
    Integer countConsecutiveLoginDays(@Param("userId") Long userId,
                                     @Param("endDate") LocalDate endDate);

    /**
     * 统计用户未领取奖励数量
     */
    Long countUnclaimedRewards(@Param("userId") Long userId);

    /**
     * 统计用户历史任务完成总数
     */
    Long countTotalCompletedTasks(@Param("userId") Long userId);

    /**
     * 统计用户各类型任务完成情况
     */
    List<java.util.Map<String, Object>> countTasksByTypeAndStatus(@Param("userId") Long userId,
                                                                  @Param("startDate") LocalDate startDate,
                                                                  @Param("endDate") LocalDate endDate);

    // =================== 更新操作 ===================

    /**
     * 更新任务进度
     */
    int updateTaskProgress(@Param("userId") Long userId,
                          @Param("taskId") Long taskId,
                          @Param("taskDate") LocalDate taskDate,
                          @Param("incrementCount") Integer incrementCount);

    /**
     * 批量标记任务为已完成
     */
    int batchMarkTasksCompleted(@Param("recordIds") List<Long> recordIds);

    /**
     * 批量标记奖励为已领取
     */
    int batchMarkRewardsReceived(@Param("recordIds") List<Long> recordIds);

    /**
     * 重置每日任务进度
     */
    int resetDailyTaskProgress(@Param("userId") Long userId,
                              @Param("taskDate") LocalDate taskDate);

    // =================== 特殊查询 ===================

    /**
     * 查询用户任务进度排行（使用数字常量优化）
     */
    List<java.util.Map<String, Object>> getUserTaskRanking(@Param("taskType") Integer taskType,
                                                           @Param("taskDate") LocalDate taskDate,
                                                           @Param("limit") Integer limit);

    /**
     * 查询活跃用户（指定天数内完成任务的用户）
     */
    List<Long> findActiveUsers(@Param("days") Integer days,
                              @Param("minCompletedTasks") Integer minCompletedTasks);

    /**
     * 查询过期未完成的任务记录
     */
    List<UserTaskRecord> findExpiredIncompleteTasks(@Param("currentDate") LocalDate currentDate);

    /**
     * 查询需要重置的每日任务记录
     */
    List<UserTaskRecord> findDailyTasksToReset(@Param("resetDate") LocalDate resetDate);

    /**
     * 搜索用户任务记录
     */
    Page<UserTaskRecord> searchUserTasks(Page<UserTaskRecord> page,
                                        @Param("userId") Long userId,
                                        @Param("keyword") String keyword,
                                        @Param("taskType") String taskType,
                                        @Param("isCompleted") Boolean isCompleted);

    // =================== 数据清理 ===================

    /**
     * 清理过期的任务记录
     */
    int cleanExpiredRecords(@Param("days") Integer days);

    /**
     * 删除用户的历史任务记录
     */
    int deleteUserHistoryRecords(@Param("userId") Long userId,
                                @Param("beforeDate") LocalDate beforeDate);

    // =================== 验证查询 ===================

    /**
     * 检查用户今日是否已有指定任务记录
     */
    boolean existsTodayTaskRecord(@Param("userId") Long userId,
                                 @Param("taskId") Long taskId,
                                 @Param("taskDate") LocalDate taskDate);

    /**
     * 检查用户是否可以领取指定任务奖励
     */
    boolean canClaimTaskReward(@Param("userId") Long userId,
                              @Param("taskId") Long taskId,
                              @Param("taskDate") LocalDate taskDate);

    /**
     * 获取用户特定任务的当前进度
     */
    UserTaskRecord getUserTaskProgress(@Param("userId") Long userId,
                                     @Param("taskId") Long taskId,
                                     @Param("taskDate") LocalDate taskDate);
}