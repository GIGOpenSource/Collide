package com.gig.collide.task.domain.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.task.domain.entity.TaskReward;

import java.util.List;
import java.util.Map;

/**
 * 任务奖励业务服务接口 - 简洁版
 * 基于task-simple.sql的单表设计
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
public interface TaskRewardService {

    // =================== 基础操作 ===================

    /**
     * 创建任务奖励
     */
    TaskReward createTaskReward(TaskReward taskReward);

    /**
     * 根据ID获取任务奖励
     */
    TaskReward getTaskRewardById(Long id);

    /**
     * 更新任务奖励
     */
    boolean updateTaskReward(TaskReward taskReward);

    /**
     * 删除任务奖励
     */
    boolean deleteTaskReward(Long id);

    /**
     * 批量删除任务奖励
     */
    boolean batchDeleteTaskRewards(List<Long> ids);

    // =================== 查询操作 ===================

    /**
     * 分页查询任务奖励
     */
    Page<TaskReward> queryTaskRewards(Long taskId, String rewardType, String rewardName,
                                     Boolean isMainReward, Integer minAmount, Integer maxAmount,
                                     String orderBy, String orderDirection,
                                     Integer currentPage, Integer pageSize);

    /**
     * 根据任务ID查询所有奖励
     */
    List<TaskReward> getRewardsByTaskId(Long taskId);

    /**
     * 根据任务ID查询主要奖励
     */
    List<TaskReward> getMainRewardsByTaskId(Long taskId);

    /**
     * 根据任务ID查询次要奖励
     */
    List<TaskReward> getSecondaryRewardsByTaskId(Long taskId);

    /**
     * 根据奖励类型查询奖励
     */
    Page<TaskReward> getRewardsByType(String rewardType, Long taskId, Integer currentPage, Integer pageSize);

    /**
     * 批量查询多个任务的奖励
     */
    List<TaskReward> getRewardsByTaskIds(List<Long> taskIds);

    /**
     * 搜索任务奖励
     */
    Page<TaskReward> searchTaskRewards(String keyword, String rewardType, Long taskId,
                                      Integer currentPage, Integer pageSize);

    // =================== 统计操作 ===================

    /**
     * 统计任务的奖励数量
     */
    Long countRewardsByTaskId(Long taskId);

    /**
     * 统计各类型奖励数量
     */
    Map<String, Long> countRewardsByType();

    /**
     * 统计任务的总奖励价值（仅金币奖励）
     */
    Long sumCoinRewardsByTaskId(Long taskId);

    /**
     * 统计所有任务的平均奖励金额
     */
    Double getAverageRewardAmount(String rewardType);

    // =================== 批量操作 ===================

    /**
     * 批量删除任务的奖励
     */
    boolean deleteRewardsByTaskId(Long taskId);

    /**
     * 批量删除指定任务的奖励
     */
    boolean deleteRewardsByTaskIds(List<Long> taskIds);

    /**
     * 批量更新奖励数量
     */
    boolean batchUpdateRewardAmount(List<Map<String, Object>> rewardList);

    // =================== 特殊查询 ===================

    /**
     * 查询金币奖励配置（按金额排序）
     */
    List<TaskReward> getCoinRewardsOrderByAmount(Long taskId, String orderDirection);

    /**
     * 查询VIP奖励配置
     */
    List<TaskReward> getVipRewards(Long taskId);

    /**
     * 查询道具奖励配置
     */
    List<TaskReward> getItemRewards(Long taskId);

    /**
     * 查询经验奖励配置
     */
    List<TaskReward> getExperienceRewards(Long taskId);

    /**
     * 查询奖励金额范围
     */
    List<TaskReward> getRewardsByAmountRange(Integer minAmount, Integer maxAmount, String rewardType);

    // =================== 验证方法 ===================

    /**
     * 检查任务是否有主要奖励
     */
    boolean hasMainReward(Long taskId);

    /**
     * 检查任务是否有指定类型的奖励
     */
    boolean hasRewardOfType(Long taskId, String rewardType);

    /**
     * 获取任务的奖励类型列表
     */
    List<String> getRewardTypesByTaskId(Long taskId);

    /**
     * 验证奖励配置是否有效
     */
    boolean isValidReward(TaskReward reward);

    // =================== 工具方法 ===================

    /**
     * 复制任务奖励配置
     */
    List<TaskReward> copyTaskRewards(Long fromTaskId, Long toTaskId);

    /**
     * 同步任务奖励配置
     */
    boolean syncTaskRewards(Long taskId, List<TaskReward> rewards);
}