package com.gig.collide.task.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.task.domain.entity.UserRewardRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户奖励记录数据访问接口 - 优化版
 * 基于task-simple.sql的数字常量设计，充分利用HASH索引
 * 
 * @author GIG Team
 * @version 3.0.0 (优化版)
 * @since 2024-01-16
 */
@Mapper
public interface UserRewardRecordMapper extends BaseMapper<UserRewardRecord> {

    // =================== 基础查询 ===================

    /**
     * 查询用户的奖励记录（数字常量优化版）
     */
    Page<UserRewardRecord> findUserRewards(Page<UserRewardRecord> page,
                                          @Param("userId") Long userId,
                                          @Param("rewardSource") Integer rewardSource,
                                          @Param("rewardType") Integer rewardType,
                                          @Param("status") Integer status);

    /**
     * 查询用户待发放的奖励（使用数字常量1=pending）
     */
    List<UserRewardRecord> findUserPendingRewards(@Param("userId") Long userId);

    /**
     * 查询用户已发放的奖励（使用数字常量2=success）
     */
    Page<UserRewardRecord> findUserGrantedRewards(Page<UserRewardRecord> page,
                                                 @Param("userId") Long userId,
                                                 @Param("rewardType") Integer rewardType,
                                                 @Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime);

    /**
     * 根据任务记录ID查询奖励记录
     */
    List<UserRewardRecord> findRewardsByTaskRecord(@Param("taskRecordId") Long taskRecordId);

    /**
     * 条件查询奖励记录（数字常量优化版）
     */
    Page<UserRewardRecord> findWithConditions(Page<UserRewardRecord> page,
                                             @Param("userId") Long userId,
                                             @Param("taskRecordId") Long taskRecordId,
                                             @Param("rewardSource") Integer rewardSource,
                                             @Param("rewardType") Integer rewardType,
                                             @Param("status") Integer status,
                                             @Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime,
                                             @Param("orderBy") String orderBy,
                                             @Param("orderDirection") String orderDirection);

    // =================== 统计查询 ===================

    /**
     * 统计用户奖励情况（使用数字常量优化）
     */
    java.util.Map<String, Object> getUserRewardStatistics(@Param("userId") Long userId);

    /**
     * 统计用户指定类型奖励总量（数字常量优化版）
     */
    Long sumUserRewardAmount(@Param("userId") Long userId,
                            @Param("rewardType") Integer rewardType,
                            @Param("status") Integer status,
                            @Param("startTime") LocalDateTime startTime,
                            @Param("endTime") LocalDateTime endTime);

    /**
     * 统计用户各类型奖励数量（数字常量优化版）
     */
    List<java.util.Map<String, Object>> countRewardsByType(@Param("userId") Long userId,
                                                           @Param("status") Integer status);

    /**
     * 统计待发放奖励数量
     */
    Long countPendingRewards(@Param("userId") Long userId);

    /**
     * 统计过期奖励数量
     */
    Long countExpiredRewards(@Param("userId") Long userId,
                            @Param("currentTime") LocalDateTime currentTime);

    /**
     * 统计失败奖励数量
     */
    Long countFailedRewards(@Param("userId") Long userId);

    // =================== 批量操作 ===================

    /**
     * 批量更新奖励状态为成功
     */
    int batchMarkRewardsSuccess(@Param("rewardIds") List<Long> rewardIds);

    /**
     * 批量更新奖励状态为失败
     */
    int batchMarkRewardsFailed(@Param("rewardIds") List<Long> rewardIds);

    /**
     * 批量设置奖励发放时间
     */
    int batchSetGrantTime(@Param("rewardIds") List<Long> rewardIds,
                         @Param("grantTime") LocalDateTime grantTime);

    /**
     * 批量删除用户奖励记录
     */
    int deleteUserRewards(@Param("userId") Long userId,
                         @Param("beforeTime") LocalDateTime beforeTime);

    // =================== 特殊查询 ===================

    /**
     * 查询即将过期的奖励
     */
    List<UserRewardRecord> findRewardsExpiringSoon(@Param("hours") Integer hours,
                                                  @Param("currentTime") LocalDateTime currentTime);

    /**
     * 查询过期未发放的奖励
     */
    List<UserRewardRecord> findExpiredPendingRewards(@Param("currentTime") LocalDateTime currentTime);

    /**
     * 查询发放失败需要重试的奖励
     */
    List<UserRewardRecord> findFailedRewardsForRetry(@Param("retryAfterHours") Integer retryAfterHours,
                                                    @Param("currentTime") LocalDateTime currentTime);

    /**
     * 查询用户金币奖励历史
     */
    Page<UserRewardRecord> findUserCoinRewardHistory(Page<UserRewardRecord> page,
                                                    @Param("userId") Long userId,
                                                    @Param("startTime") LocalDateTime startTime,
                                                    @Param("endTime") LocalDateTime endTime);

    /**
     * 查询用户VIP奖励历史
     */
    List<UserRewardRecord> findUserVipRewardHistory(@Param("userId") Long userId);

    /**
     * 查询系统奖励发放记录
     */
    Page<UserRewardRecord> findSystemRewards(Page<UserRewardRecord> page,
                                            @Param("rewardType") String rewardType,
                                            @Param("status") String status,
                                            @Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime);

    /**
     * 搜索奖励记录
     */
    Page<UserRewardRecord> searchRewards(Page<UserRewardRecord> page,
                                        @Param("userId") Long userId,
                                        @Param("keyword") String keyword,
                                        @Param("rewardType") String rewardType,
                                        @Param("status") String status);

    // =================== 排行统计 ===================

    /**
     * 查询用户金币奖励排行榜
     */
    List<java.util.Map<String, Object>> getCoinRewardRanking(@Param("startTime") LocalDateTime startTime,
                                                             @Param("endTime") LocalDateTime endTime,
                                                             @Param("limit") Integer limit);

    /**
     * 查询活跃奖励用户排行
     */
    List<java.util.Map<String, Object>> getActiveRewardUsersRanking(@Param("days") Integer days,
                                                                   @Param("limit") Integer limit);

    // =================== 数据清理 ===================

    /**
     * 清理过期的奖励记录
     */
    int cleanExpiredRewards(@Param("days") Integer days);

    /**
     * 清理已发放的历史奖励记录
     */
    int cleanOldGrantedRewards(@Param("days") Integer days);

    // =================== 验证查询 ===================

    /**
     * 检查用户是否有待发放奖励
     */
    boolean hasPendingRewards(@Param("userId") Long userId);

    /**
     * 检查任务记录是否已发放奖励
     */
    boolean hasRewardGranted(@Param("taskRecordId") Long taskRecordId);

    /**
     * 获取用户指定日期的奖励总量
     */
    Long getUserDailyRewardSum(@Param("userId") Long userId,
                              @Param("rewardType") String rewardType,
                              @Param("date") java.time.LocalDate date);

    /**
     * 获取最近发放的奖励记录
     */
    List<UserRewardRecord> getRecentRewards(@Param("userId") Long userId,
                                          @Param("limit") Integer limit);
}