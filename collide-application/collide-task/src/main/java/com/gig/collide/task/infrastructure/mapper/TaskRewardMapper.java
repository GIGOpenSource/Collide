package com.gig.collide.task.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.task.domain.entity.TaskReward;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务奖励数据访问接口 - 简洁版
 * 基于task-simple.sql的无连表设计
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
@Mapper
public interface TaskRewardMapper extends BaseMapper<TaskReward> {

    // =================== 基础查询 ===================

    /**
     * 根据任务ID查询所有奖励
     */
    List<TaskReward> findRewardsByTaskId(@Param("taskId") Long taskId);

    /**
     * 根据任务ID查询主要奖励
     */
    List<TaskReward> findMainRewardsByTaskId(@Param("taskId") Long taskId);

    /**
     * 根据任务ID查询次要奖励
     */
    List<TaskReward> findSecondaryRewardsByTaskId(@Param("taskId") Long taskId);

    /**
     * 根据奖励类型查询奖励配置
     */
    Page<TaskReward> findRewardsByType(Page<TaskReward> page,
                                      @Param("rewardType") String rewardType,
                                      @Param("taskId") Long taskId);

    /**
     * 条件查询任务奖励
     */
    Page<TaskReward> findWithConditions(Page<TaskReward> page,
                                       @Param("taskId") Long taskId,
                                       @Param("rewardType") String rewardType,
                                       @Param("rewardName") String rewardName,
                                       @Param("isMainReward") Boolean isMainReward,
                                       @Param("minAmount") Integer minAmount,
                                       @Param("maxAmount") Integer maxAmount,
                                       @Param("orderBy") String orderBy,
                                       @Param("orderDirection") String orderDirection);

    // =================== 批量查询 ===================

    /**
     * 批量查询多个任务的奖励
     */
    List<TaskReward> findRewardsByTaskIds(@Param("taskIds") List<Long> taskIds);

    /**
     * 查询指定类型的所有奖励
     */
    List<TaskReward> findAllRewardsByType(@Param("rewardType") String rewardType);

    /**
     * 查询所有主要奖励
     */
    List<TaskReward> findAllMainRewards();

    // =================== 统计查询 ===================

    /**
     * 统计任务的奖励数量
     */
    Long countRewardsByTaskId(@Param("taskId") Long taskId);

    /**
     * 统计各类型奖励数量
     */
    List<java.util.Map<String, Object>> countRewardsByType();

    /**
     * 统计任务的总奖励价值（仅金币奖励）
     */
    Long sumCoinRewardsByTaskId(@Param("taskId") Long taskId);

    /**
     * 统计所有任务的平均奖励金额
     */
    Double getAverageRewardAmount(@Param("rewardType") String rewardType);

    // =================== 批量操作 ===================

    /**
     * 批量删除任务的奖励
     */
    int deleteRewardsByTaskId(@Param("taskId") Long taskId);

    /**
     * 批量删除指定任务的奖励
     */
    int deleteRewardsByTaskIds(@Param("taskIds") List<Long> taskIds);

    /**
     * 批量更新奖励数量
     */
    int batchUpdateRewardAmount(@Param("rewardList") List<java.util.Map<String, Object>> rewardList);

    // =================== 特殊查询 ===================

    /**
     * 查询金币奖励配置（按金额排序）
     */
    List<TaskReward> findCoinRewardsOrderByAmount(@Param("taskId") Long taskId,
                                                 @Param("orderDirection") String orderDirection);

    /**
     * 查询VIP奖励配置
     */
    List<TaskReward> findVipRewards(@Param("taskId") Long taskId);

    /**
     * 查询道具奖励配置
     */
    List<TaskReward> findItemRewards(@Param("taskId") Long taskId);

    /**
     * 查询经验奖励配置
     */
    List<TaskReward> findExperienceRewards(@Param("taskId") Long taskId);

    /**
     * 搜索奖励配置（支持名称和描述模糊搜索）
     */
    Page<TaskReward> searchRewards(Page<TaskReward> page,
                                  @Param("keyword") String keyword,
                                  @Param("rewardType") String rewardType,
                                  @Param("taskId") Long taskId);

    /**
     * 查询奖励金额范围
     */
    List<TaskReward> findRewardsByAmountRange(@Param("minAmount") Integer minAmount,
                                            @Param("maxAmount") Integer maxAmount,
                                            @Param("rewardType") String rewardType);

    // =================== 验证查询 ===================

    /**
     * 检查任务是否有主要奖励
     */
    boolean hasMainReward(@Param("taskId") Long taskId);

    /**
     * 检查任务是否有指定类型的奖励
     */
    boolean hasRewardOfType(@Param("taskId") Long taskId,
                           @Param("rewardType") String rewardType);

    /**
     * 获取任务的奖励类型列表
     */
    List<String> getRewardTypesByTaskId(@Param("taskId") Long taskId);
}