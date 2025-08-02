package com.gig.collide.task.domain.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.task.domain.entity.UserRewardRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户奖励记录业务服务接口 - 优化版
 * 基于优化后的task-simple.sql的单表设计
 * 支持数字常量和立即钱包同步功能
 * 
 * @author GIG Team
 * @version 2.0.0 (优化版)
 * @since 2024-01-16
 */
public interface UserRewardRecordService {

    // =================== 基础操作 ===================

    /**
     * 创建用户奖励记录
     */
    UserRewardRecord createUserRewardRecord(UserRewardRecord userRewardRecord);

    /**
     * 根据ID获取用户奖励记录
     */
    UserRewardRecord getUserRewardRecordById(Long id);

    /**
     * 更新用户奖励记录
     */
    boolean updateUserRewardRecord(UserRewardRecord userRewardRecord);

    /**
     * 删除用户奖励记录
     */
    boolean deleteUserRewardRecord(Long id);

    /**
     * 批量删除用户奖励记录
     */
    boolean batchDeleteUserRewardRecords(List<Long> ids);

    // =================== 奖励发放 ===================

    /**
     * 发放用户奖励（优化版 - 使用数字常量）
     * 金币奖励会立即同步到用户钱包
     */
    boolean grantUserReward(Long userId, Long taskRecordId, Integer rewardType, 
                           String rewardName, Integer rewardAmount, Map<String, Object> rewardData);

    /**
     * 发放用户奖励并立即同步到钱包
     * 这是核心方法，确保金币奖励立即到账
     */
    boolean grantUserRewardWithWalletSync(Long userId, Long taskRecordId, Integer rewardType, 
                                         String rewardName, Integer rewardAmount, Map<String, Object> rewardData);

    /**
     * 批量发放奖励
     * 支持批量钱包同步
     */
    boolean batchGrantRewards(List<UserRewardRecord> rewards);

    /**
     * 批量发放奖励并立即同步到钱包
     */
    int batchGrantRewardsWithWalletSync(List<UserRewardRecord> rewards);

    /**
     * 标记奖励发放成功
     */
    boolean markRewardSuccess(Long rewardId);

    /**
     * 标记奖励发放失败
     */
    boolean markRewardFailed(Long rewardId, String failReason);

    /**
     * 批量标记奖励状态（优化版 - 使用数字常量）
     */
    boolean batchMarkRewardStatus(List<Long> rewardIds, Integer status);

    /**
     * 重试失败的奖励发放
     */
    boolean retryFailedReward(Long rewardId);

    // =================== 查询操作 ===================

    /**
     * 分页查询用户奖励记录（优化版 - 使用数字常量）
     */
    Page<UserRewardRecord> queryUserRewardRecords(Long userId, Long taskRecordId, Integer rewardSource,
                                                 Integer rewardType, Integer status, LocalDateTime startTime,
                                                 LocalDateTime endTime, String orderBy, String orderDirection,
                                                 Integer currentPage, Integer pageSize);

    /**
     * 获取用户待发放奖励
     */
    List<UserRewardRecord> getUserPendingRewards(Long userId);

    /**
     * 获取用户已发放奖励（优化版 - 使用数字常量）
     */
    Page<UserRewardRecord> getUserGrantedRewards(Long userId, Integer rewardType, 
                                                LocalDateTime startTime, LocalDateTime endTime,
                                                Integer currentPage, Integer pageSize);

    /**
     * 根据任务记录查询奖励
     */
    List<UserRewardRecord> getRewardsByTaskRecord(Long taskRecordId);

    /**
     * 搜索用户奖励记录（优化版 - 使用数字常量）
     */
    Page<UserRewardRecord> searchUserRewardRecords(Long userId, String keyword, Integer rewardType,
                                                  Integer status, Integer currentPage, Integer pageSize);

    // =================== 统计操作 ===================

    /**
     * 获取用户奖励统计信息
     */
    Map<String, Object> getUserRewardStatistics(Long userId);

    /**
     * 统计用户指定类型奖励总量（优化版 - 使用数字常量）
     */
    Long sumUserRewardAmount(Long userId, Integer rewardType, Integer status, 
                            LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计用户各类型奖励数量（优化版 - 使用数字常量）
     */
    Map<String, Long> countUserRewardsByType(Long userId, Integer status);

    /**
     * 统计待发放奖励数量
     */
    Long countPendingRewards(Long userId);

    /**
     * 统计过期奖励数量
     */
    Long countExpiredRewards(Long userId);

    /**
     * 统计失败奖励数量
     */
    Long countFailedRewards(Long userId);

    /**
     * 获取用户指定日期奖励总量（优化版 - 使用数字常量）
     */
    Long getUserDailyRewardSum(Long userId, Integer rewardType, LocalDate date);

    // =================== 特殊查询 ===================

    /**
     * 查询即将过期的奖励
     */
    List<UserRewardRecord> getRewardsExpiringSoon(Integer hours);

    /**
     * 查询过期未发放的奖励
     */
    List<UserRewardRecord> getExpiredPendingRewards();

    /**
     * 查询需要重试的失败奖励
     */
    List<UserRewardRecord> getFailedRewardsForRetry(Integer retryAfterHours);

    /**
     * 获取用户金币奖励历史
     */
    Page<UserRewardRecord> getUserCoinRewardHistory(Long userId, LocalDateTime startTime, 
                                                   LocalDateTime endTime, Integer currentPage, Integer pageSize);

    /**
     * 获取用户VIP奖励历史
     */
    List<UserRewardRecord> getUserVipRewardHistory(Long userId);

    /**
     * 获取最近奖励记录
     */
    List<UserRewardRecord> getRecentRewards(Long userId, Integer limit);

    // =================== 排行榜 ===================

    /**
     * 获取用户金币奖励排行榜
     */
    List<Map<String, Object>> getCoinRewardRanking(LocalDateTime startTime, LocalDateTime endTime, Integer limit);

    /**
     * 获取活跃奖励用户排行
     */
    List<Map<String, Object>> getActiveRewardUsersRanking(Integer days, Integer limit);

    // =================== 奖励处理 ===================

    /**
     * 处理任务完成奖励
     * 支持立即钱包同步
     */
    boolean processTaskCompletionReward(Long userId, Long taskRecordId);

    /**
     * 处理金币奖励发放并立即同步到钱包
     * 这是核心方法，确保金币奖励立即到账
     */
    boolean processCoinRewardWithWalletSync(Long userId, Integer coinAmount, String source, Long taskRecordId);

    /**
     * 处理金币奖励发放（不立即同步）
     */
    boolean processCoinReward(Long userId, Integer coinAmount, String source);

    /**
     * 处理VIP奖励发放
     */
    boolean processVipReward(Long userId, String vipType, Integer durationDays);

    /**
     * 处理道具奖励发放
     */
    boolean processItemReward(Long userId, Long itemId, Integer quantity);

    /**
     * 处理经验奖励发放
     */
    boolean processExperienceReward(Long userId, Integer experience);

    /**
     * 处理徽章奖励发放
     */
    boolean processBadgeReward(Long userId, String badgeName, String badgeDesc);

    // =================== 系统管理 ===================

    /**
     * 清理过期奖励记录
     */
    int cleanExpiredRewards(Integer days);

    /**
     * 清理历史已发放奖励
     */
    int cleanOldGrantedRewards(Integer days);

    /**
     * 修复异常奖励数据
     */
    int fixAbnormalRewardData();

    /**
     * 自动处理待发放奖励
     */
    int autoProcessPendingRewards();

    // =================== 验证方法 ===================

    /**
     * 检查用户是否有待发放奖励
     */
    boolean hasPendingRewards(Long userId);

    /**
     * 检查任务记录是否已发放奖励
     */
    boolean hasRewardGranted(Long taskRecordId);

    /**
     * 验证奖励是否可以发放
     */
    boolean canGrantReward(UserRewardRecord reward);

    /**
     * 验证奖励发放限制（优化版 - 使用数字常量）
     */
    boolean validateRewardLimit(Long userId, Integer rewardType, Integer amount, LocalDate date);

    // =================== 钱包同步相关方法 ===================

    /**
     * 检查奖励是否已同步到钱包
     */
    boolean isRewardSyncedToWallet(Long userId, Long rewardId);

    /**
     * 批量检查奖励钱包同步状态
     */
    Map<Long, Boolean> batchCheckRewardSyncStatus(List<Long> rewardIds);

    /**
     * 重新同步失败的金币奖励到钱包
     */
    boolean resyncFailedCoinRewardsToWallet(List<Long> rewardIds);

    // =================== 报表统计 ===================

    /**
     * 获取奖励发放报表
     */
    Map<String, Object> getRewardGrantReport(LocalDate startDate, LocalDate endDate);

    /**
     * 获取用户奖励趋势报告
     */
    List<Map<String, Object>> getUserRewardTrend(Long userId, Integer days);

    /**
     * 获取系统奖励统计
     */
    Map<String, Object> getSystemRewardStatistics();
}