package com.gig.collide.task.domain.service;

import com.gig.collide.task.domain.entity.UserRewardRecord;

import java.util.List;

/**
 * 任务钱包同步服务接口 - 优化版
 * 专门处理任务完成后奖励立即同步到用户钱包
 * 
 * @author GIG Team
 * @version 2.0.0 (优化版)
 * @since 2024-01-16
 */
public interface TaskWalletSyncService {

    // =================== 钱包同步核心方法 ===================

    /**
     * 立即同步金币奖励到用户钱包
     * 这是任务完成后的核心方法，确保金币立即到账
     * 
     * @param userId 用户ID
     * @param coinAmount 金币数量
     * @param source 奖励来源描述
     * @param taskRecordId 任务记录ID（用于追踪）
     * @return 是否同步成功
     */
    boolean syncCoinRewardToWallet(Long userId, Integer coinAmount, String source, Long taskRecordId);

    /**
     * 批量同步金币奖励到用户钱包
     * 用于批量处理多个奖励记录
     * 
     * @param rewardRecords 奖励记录列表（必须是金币奖励）
     * @return 成功同步的数量
     */
    int batchSyncCoinRewards(List<UserRewardRecord> rewardRecords);

    /**
     * 检查钱包同步状态
     * 验证奖励是否已正确同步到钱包
     * 
     * @param userId 用户ID
     * @param taskRecordId 任务记录ID
     * @return 是否已同步
     */
    boolean checkWalletSyncStatus(Long userId, Long taskRecordId);

    // =================== 钱包余额查询 ===================

    /**
     * 查询用户当前金币余额
     * 用于验证奖励发放结果
     * 
     * @param userId 用户ID
     * @return 当前金币余额
     */
    Long getCurrentCoinBalance(Long userId);

    /**
     * 查询用户累计获得金币总数
     * 用于统计分析
     * 
     * @param userId 用户ID
     * @return 累计获得金币总数
     */
    Long getTotalEarnedCoins(Long userId);

    // =================== 同步记录管理 ===================

    /**
     * 记录钱包同步操作
     * 用于审计和问题追踪
     * 
     * @param userId 用户ID
     * @param amount 金币数量
     * @param source 来源
     * @param taskRecordId 任务记录ID
     * @param success 是否成功
     * @param errorMessage 错误消息（如果失败）
     */
    void recordSyncOperation(Long userId, Integer amount, String source, 
                           Long taskRecordId, boolean success, String errorMessage);

    /**
     * 获取同步失败的记录
     * 用于重试机制
     * 
     * @param retryCount 最大重试次数
     * @return 失败的同步记录
     */
    List<Long> getFailedSyncRecords(Integer retryCount);

    /**
     * 重试失败的钱包同步
     * 
     * @param taskRecordIds 失败的任务记录ID列表
     * @return 重试成功的数量
     */
    int retryFailedSyncOperations(List<Long> taskRecordIds);

    // =================== 钱包服务调用 ===================

    /**
     * 调用用户钱包服务发放金币
     * 这是与用户模块钱包服务的接口方法
     * 
     * @param userId 用户ID
     * @param amount 金币数量
     * @param source 来源描述
     * @return 是否调用成功
     */
    boolean callWalletServiceGrantCoin(Long userId, Integer amount, String source);

    /**
     * 验证钱包服务是否可用
     * 在同步前检查钱包服务状态
     * 
     * @return 钱包服务是否可用
     */
    boolean isWalletServiceAvailable();

    // =================== 同步统计和监控 ===================

    /**
     * 获取今日钱包同步统计
     * 
     * @return 同步统计数据
     */
    java.util.Map<String, Object> getTodaySyncStatistics();

    /**
     * 获取用户钱包同步历史
     * 
     * @param userId 用户ID
     * @param days 查询天数
     * @return 同步历史记录
     */
    List<java.util.Map<String, Object>> getUserSyncHistory(Long userId, Integer days);

    /**
     * 获取系统钱包同步健康状态
     * 用于监控和告警
     * 
     * @return 健康状态信息
     */
    java.util.Map<String, Object> getWalletSyncHealthStatus();
}