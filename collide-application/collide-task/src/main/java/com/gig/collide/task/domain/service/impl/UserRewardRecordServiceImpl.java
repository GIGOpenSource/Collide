package com.gig.collide.task.domain.service.impl;

import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheUpdate;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.task.constant.RewardSourceConstant;
import com.gig.collide.api.task.constant.RewardStatusConstant;
import com.gig.collide.api.task.constant.RewardTypeConstant;
import com.gig.collide.task.domain.entity.UserRewardRecord;
import com.gig.collide.task.domain.service.TaskWalletSyncService;
import com.gig.collide.task.domain.service.UserRewardRecordService;
import com.gig.collide.task.infrastructure.mapper.UserRewardRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户奖励记录业务服务实现类 - 优化版
 * 基于优化后的task-simple.sql，支持数字常量和立即钱包同步
 * 
 * @author GIG Team
 * @version 3.0.0 (优化版)
 * @since 2024-01-16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRewardRecordServiceImpl implements UserRewardRecordService {

    private final UserRewardRecordMapper userRewardRecordMapper;
    private final TaskWalletSyncService taskWalletSyncService;

    // =================== 基础操作 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = "task:user_reward", key = "#userRewardRecord.userId + ':*'")
    public UserRewardRecord createUserRewardRecord(UserRewardRecord userRewardRecord) {
        log.info("创建用户奖励记录: userId={}, rewardType={}, rewardAmount={}", 
                userRewardRecord.getUserId(), userRewardRecord.getRewardType(), userRewardRecord.getRewardAmount());
        
        // 设置默认值
        if (userRewardRecord.getStatus() == null) {
            userRewardRecord.setStatus(RewardStatusConstant.PENDING);
        }
        if (userRewardRecord.getRewardSource() == null) {
            userRewardRecord.setRewardSource(RewardSourceConstant.TASK);
        }
        
        userRewardRecordMapper.insert(userRewardRecord);
        return userRewardRecord;
    }

    @Override
    @Cached(name = "task:user_reward", key = "#id", expire = 30, timeUnit = TimeUnit.MINUTES)
    public UserRewardRecord getUserRewardRecordById(Long id) {
        return userRewardRecordMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheUpdate(name = "task:user_reward", key = "#userRewardRecord.id", value = "#userRewardRecord")
    @CacheInvalidate(name = "task:user_reward", key = "#userRewardRecord.userId + ':*'")
    public boolean updateUserRewardRecord(UserRewardRecord userRewardRecord) {
        log.info("更新用户奖励记录: id={}, userId={}, status={}", 
                userRewardRecord.getId(), userRewardRecord.getUserId(), userRewardRecord.getStatus());
        return userRewardRecordMapper.updateById(userRewardRecord) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = "task:user_reward", key = "*")
    public boolean deleteUserRewardRecord(Long id) {
        UserRewardRecord record = getUserRewardRecordById(id);
        if (record != null) {
            log.info("删除用户奖励记录: id={}, userId={}", id, record.getUserId());
            return userRewardRecordMapper.deleteById(id) > 0;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = "task:user_reward", key = "*")
    public boolean batchDeleteUserRewardRecords(List<Long> ids) {
        log.info("批量删除用户奖励记录: ids={}", ids);
        return userRewardRecordMapper.deleteBatchIds(ids) > 0;
    }

    // =================== 奖励发放 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = "task:user_reward", key = "#userId + ':*'")
    public boolean grantUserReward(Long userId, Long taskRecordId, Integer rewardType, 
                                  String rewardName, Integer rewardAmount, Map<String, Object> rewardData) {
        log.info("发放用户奖励: userId={}, taskRecordId={}, rewardType={}, rewardAmount={}", 
                userId, taskRecordId, rewardType, rewardAmount);
                
        // 创建奖励记录
        UserRewardRecord rewardRecord = UserRewardRecord.createTaskReward(
            userId, taskRecordId, rewardType, rewardName, rewardAmount, rewardData
        );
        
        UserRewardRecord created = createUserRewardRecord(rewardRecord);
        return created != null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean grantUserRewardWithWalletSync(Long userId, Long taskRecordId, Integer rewardType, 
                                                String rewardName, Integer rewardAmount, Map<String, Object> rewardData) {
        log.info("发放用户奖励并同步钱包: userId={}, rewardType={}, rewardAmount={}", 
                userId, rewardType, rewardAmount);
                
        boolean granted = grantUserReward(userId, taskRecordId, rewardType, rewardName, rewardAmount, rewardData);
        
        if (granted && RewardTypeConstant.requiresImmediateWalletSync(rewardType)) {
            // 金币奖励立即同步到钱包
            return taskWalletSyncService.syncCoinRewardToWallet(
                userId, rewardAmount, "task_reward", taskRecordId
            );
        }
        
        return granted;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchGrantRewards(List<UserRewardRecord> rewards) {
        log.info("批量发放奖励: recordCount={}", rewards.size());
        
        int grantedCount = 0;
        for (UserRewardRecord reward : rewards) {
            UserRewardRecord created = createUserRewardRecord(reward);
            if (created != null) {
                grantedCount++;
            }
        }
        
        return grantedCount == rewards.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchGrantRewardsWithWalletSync(List<UserRewardRecord> rewards) {
        log.info("批量发放奖励并同步钱包: recordCount={}", rewards.size());
        
        int grantedCount = 0;
        List<UserRewardRecord> coinRewards = new ArrayList<>();
        
        for (UserRewardRecord reward : rewards) {
            UserRewardRecord created = createUserRewardRecord(reward);
            if (created != null) {
                grantedCount++;
                
                if (RewardTypeConstant.requiresImmediateWalletSync(reward.getRewardType())) {
                    coinRewards.add(created);
                }
            }
        }
        
        // 批量同步金币奖励到钱包
        if (!coinRewards.isEmpty()) {
            int syncedCount = taskWalletSyncService.batchSyncCoinRewards(coinRewards);
            log.info("批量同步金币奖励结果: 总数={}, 成功={}", coinRewards.size(), syncedCount);
        }
        
        return grantedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markRewardSuccess(Long rewardId) {
        UserRewardRecord record = getUserRewardRecordById(rewardId);
        if (record != null) {
            record.setStatus(RewardStatusConstant.SUCCESS);
            record.setGrantTime(LocalDateTime.now());
            return updateUserRewardRecord(record);
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markRewardFailed(Long rewardId, String failReason) {
        UserRewardRecord record = getUserRewardRecordById(rewardId);
        if (record != null) {
            record.setStatus(RewardStatusConstant.FAILED);
            record.setGrantTime(LocalDateTime.now());
            if (failReason != null) {
                record.setRewardDataValue("fail_reason", failReason);
            }
            return updateUserRewardRecord(record);
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = "task:user_reward", key = "*")
    public boolean batchMarkRewardStatus(List<Long> rewardIds, Integer status) {
        log.info("批量更新奖励状态: rewardIds={}, status={}", rewardIds, status);
        
        int updatedCount = 0;
        for (Long rewardId : rewardIds) {
            UserRewardRecord record = getUserRewardRecordById(rewardId);
            if (record != null) {
                record.setStatus(status);
                if (RewardStatusConstant.isSuccess(status)) {
                    record.setGrantTime(LocalDateTime.now());
                }
                if (updateUserRewardRecord(record)) {
                    updatedCount++;
                }
            }
        }
        
        return updatedCount == rewardIds.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean retryFailedReward(Long rewardId) {
        log.info("重试失败的奖励: rewardId={}", rewardId);
        
        UserRewardRecord record = getUserRewardRecordById(rewardId);
        if (record != null && RewardStatusConstant.isFailed(record.getStatus())) {
            // 如果是金币奖励，重新同步到钱包
            if (RewardTypeConstant.requiresImmediateWalletSync(record.getRewardType())) {
                boolean syncSuccess = taskWalletSyncService.syncCoinRewardToWallet(
                    record.getUserId(), record.getRewardAmount(), "task_reward_retry", record.getTaskRecordId()
                );
                
                if (syncSuccess) {
                    record.setStatus(RewardStatusConstant.SUCCESS);
                    record.setGrantTime(LocalDateTime.now());
                    return updateUserRewardRecord(record);
                }
            } else {
                // 非金币奖励直接标记为成功
                record.setStatus(RewardStatusConstant.SUCCESS);
                record.setGrantTime(LocalDateTime.now());
                return updateUserRewardRecord(record);
            }
        }
        
        return false;
    }

    // =================== 查询操作 ===================

    @Override
    @Cached(name = "task:user_reward_query", key = "#userId + ':' + #rewardSource + ':' + #rewardType + ':' + #status", 
            expire = 15, timeUnit = TimeUnit.MINUTES)
    public Page<UserRewardRecord> queryUserRewardRecords(Long userId, Long taskRecordId, Integer rewardSource,
                                                        Integer rewardType, Integer status, LocalDateTime startTime,
                                                        LocalDateTime endTime, String orderBy, String orderDirection,
                                                        Integer currentPage, Integer pageSize) {
        log.debug("查询用户奖励记录: userId={}, rewardType={}, status={}", userId, rewardType, status);
        
        Page<UserRewardRecord> page = new Page<>(currentPage != null ? currentPage : 1, pageSize != null ? pageSize : 20);
        return userRewardRecordMapper.findWithConditions(
            page, userId, taskRecordId, rewardSource, rewardType, status, startTime, endTime, 
            orderBy != null ? orderBy : "create_time", orderDirection != null ? orderDirection : "DESC"
        );
    }

    @Override
    @Cached(name = "task:user_pending_rewards", key = "#userId", expire = 5, timeUnit = TimeUnit.MINUTES)
    public List<UserRewardRecord> getUserPendingRewards(Long userId) {
        return userRewardRecordMapper.findUserPendingRewards(userId);
    }

    @Override
    public Page<UserRewardRecord> getUserGrantedRewards(Long userId, Integer rewardType, 
                                                       LocalDateTime startTime, LocalDateTime endTime,
                                                       Integer currentPage, Integer pageSize) {
        Page<UserRewardRecord> page = new Page<>(currentPage != null ? currentPage : 1, pageSize != null ? pageSize : 20);
        return userRewardRecordMapper.findUserGrantedRewards(page, userId, rewardType, startTime, endTime);
    }

    @Override
    public List<UserRewardRecord> getRewardsByTaskRecord(Long taskRecordId) {
        return userRewardRecordMapper.findRewardsByTaskRecord(taskRecordId);
    }

    @Override
    public Page<UserRewardRecord> searchUserRewardRecords(Long userId, String keyword, Integer rewardType,
                                                         Integer status, Integer currentPage, Integer pageSize) {
        Page<UserRewardRecord> page = new Page<>(currentPage != null ? currentPage : 1, pageSize != null ? pageSize : 20);
        return userRewardRecordMapper.searchRewards(page, userId, keyword, 
                                                   rewardType != null ? rewardType.toString() : null, 
                                                   status != null ? status.toString() : null);
    }

    // =================== 统计操作 ===================

    @Override
    public Map<String, Object> getUserRewardStatistics(Long userId) {
        return userRewardRecordMapper.getUserRewardStatistics(userId);
    }

    @Override
    @Cached(name = "task:user_reward_sum", key = "#userId + ':' + #rewardType + ':' + #status", 
            expire = 30, timeUnit = TimeUnit.MINUTES)
    public Long sumUserRewardAmount(Long userId, Integer rewardType, Integer status, 
                                   LocalDateTime startTime, LocalDateTime endTime) {
        return userRewardRecordMapper.sumUserRewardAmount(userId, rewardType, status, startTime, endTime);
    }

    @Override
    public Map<String, Long> countUserRewardsByType(Long userId, Integer status) {
        List<Map<String, Object>> results = userRewardRecordMapper.countRewardsByType(userId, status);
        Map<String, Long> counts = new HashMap<>();
        for (Map<String, Object> result : results) {
            String type = (String) result.get("reward_type");
            Long count = (Long) result.get("count");
            counts.put(type, count);
        }
        return counts;
    }

    @Override
    public Long countPendingRewards(Long userId) {
        return userRewardRecordMapper.countPendingRewards(userId);
    }

    @Override
    public Long countExpiredRewards(Long userId) {
        return userRewardRecordMapper.countExpiredRewards(userId, LocalDateTime.now());
    }

    @Override
    public Long countFailedRewards(Long userId) {
        return userRewardRecordMapper.countFailedRewards(userId);
    }

    @Override
    @Cached(name = "task:user_daily_reward", key = "#userId + ':' + #rewardType + ':' + #date", 
            expire = 60, timeUnit = TimeUnit.MINUTES)
    public Long getUserDailyRewardSum(Long userId, Integer rewardType, LocalDate date) {
        return userRewardRecordMapper.getUserDailyRewardSum(userId, rewardType.toString(), date);
    }

    // =================== 特殊查询 ===================

    @Override
    public List<UserRewardRecord> getRewardsExpiringSoon(Integer hours) {
        return userRewardRecordMapper.findRewardsExpiringSoon(hours, LocalDateTime.now());
    }

    @Override
    public List<UserRewardRecord> getExpiredPendingRewards() {
        return userRewardRecordMapper.findExpiredPendingRewards(LocalDateTime.now());
    }

    @Override
    public List<UserRewardRecord> getFailedRewardsForRetry(Integer retryAfterHours) {
        return userRewardRecordMapper.findFailedRewardsForRetry(retryAfterHours, LocalDateTime.now());
    }

    @Override
    public Page<UserRewardRecord> getUserCoinRewardHistory(Long userId, LocalDateTime startTime, 
                                                          LocalDateTime endTime, Integer currentPage, Integer pageSize) {
        Page<UserRewardRecord> page = new Page<>(currentPage != null ? currentPage : 1, pageSize != null ? pageSize : 20);
        return userRewardRecordMapper.findUserCoinRewardHistory(page, userId, startTime, endTime);
    }

    @Override
    public List<UserRewardRecord> getUserVipRewardHistory(Long userId) {
        return userRewardRecordMapper.findUserVipRewardHistory(userId);
    }

    @Override
    public List<UserRewardRecord> getRecentRewards(Long userId, Integer limit) {
        return userRewardRecordMapper.getRecentRewards(userId, limit != null ? limit : 10);
    }

    // =================== 排行榜 ===================

    @Override
    public List<Map<String, Object>> getCoinRewardRanking(LocalDateTime startTime, LocalDateTime endTime, Integer limit) {
        return userRewardRecordMapper.getCoinRewardRanking(startTime, endTime, limit);
    }

    @Override
    public List<Map<String, Object>> getActiveRewardUsersRanking(Integer days, Integer limit) {
        return userRewardRecordMapper.getActiveRewardUsersRanking(days, limit);
    }

    // =================== 奖励处理 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean processTaskCompletionReward(Long userId, Long taskRecordId) {
        log.info("处理任务完成奖励: userId={}, taskRecordId={}", userId, taskRecordId);
        // 这里应该根据任务类型确定奖励类型和数量
        // 简化实现，默认给100金币
        return grantUserRewardWithWalletSync(userId, taskRecordId, RewardTypeConstant.COIN, 
                                           "任务完成奖励", 100, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean processCoinRewardWithWalletSync(Long userId, Integer coinAmount, String source, Long taskRecordId) {
        log.info("处理金币奖励并同步钱包: userId={}, coinAmount={}, source={}", userId, coinAmount, source);
        
        return grantUserRewardWithWalletSync(userId, taskRecordId, RewardTypeConstant.COIN, 
                                           source, coinAmount, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean processCoinReward(Long userId, Integer coinAmount, String source) {
        log.info("处理金币奖励: userId={}, coinAmount={}, source={}", userId, coinAmount, source);
        
        return grantUserReward(userId, null, RewardTypeConstant.COIN, source, coinAmount, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean processVipReward(Long userId, String vipType, Integer durationDays) {
        log.info("处理VIP奖励: userId={}, vipType={}, durationDays={}", userId, vipType, durationDays);
        
        Map<String, Object> vipData = new HashMap<>();
        vipData.put("vip_type", vipType);
        vipData.put("duration_days", durationDays);
        
        return grantUserReward(userId, null, RewardTypeConstant.VIP, 
                             "VIP " + vipType, durationDays, vipData);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean processItemReward(Long userId, Long itemId, Integer quantity) {
        log.info("处理道具奖励: userId={}, itemId={}, quantity={}", userId, itemId, quantity);
        
        Map<String, Object> itemData = new HashMap<>();
        itemData.put("item_id", itemId);
        
        return grantUserReward(userId, null, RewardTypeConstant.ITEM, 
                             "道具奖励", quantity, itemData);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean processExperienceReward(Long userId, Integer experience) {
        log.info("处理经验奖励: userId={}, experience={}", userId, experience);
        
        return grantUserReward(userId, null, RewardTypeConstant.EXPERIENCE, 
                             "经验奖励", experience, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean processBadgeReward(Long userId, String badgeName, String badgeDesc) {
        log.info("处理徽章奖励: userId={}, badgeName={}, badgeDesc={}", userId, badgeName, badgeDesc);
        
        Map<String, Object> badgeData = new HashMap<>();
        badgeData.put("badge_desc", badgeDesc);
        
        return grantUserReward(userId, null, RewardTypeConstant.BADGE, 
                             badgeName, 1, badgeData);
    }

    // =================== 系统管理 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanExpiredRewards(Integer days) {
        log.info("清理过期奖励记录: daysToKeep={}", days);
        
        return userRewardRecordMapper.cleanExpiredRewards(days);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanOldGrantedRewards(Integer days) {
        log.info("清理历史已发放奖励: daysToKeep={}", days);
        
        return userRewardRecordMapper.cleanOldGrantedRewards(days);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int fixAbnormalRewardData() {
        log.info("修复异常奖励数据");
        
        // 暂时返回0，具体实现需要根据业务需求定义
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int autoProcessPendingRewards() {
        log.info("自动处理待发放奖励");
        
        List<UserRewardRecord> pendingRewards = userRewardRecordMapper.findUserPendingRewards(null);
        int processedCount = 0;
        
        for (UserRewardRecord reward : pendingRewards) {
            if (reward.canBeGranted()) {
                if (RewardTypeConstant.requiresImmediateWalletSync(reward.getRewardType())) {
                    boolean syncSuccess = taskWalletSyncService.syncCoinRewardToWallet(
                        reward.getUserId(), reward.getRewardAmount(), "auto_process", reward.getTaskRecordId()
                    );
                    
                    if (syncSuccess) {
                        reward.setStatus(RewardStatusConstant.SUCCESS);
                        reward.setGrantTime(LocalDateTime.now());
                        updateUserRewardRecord(reward);
                        processedCount++;
                    }
                } else {
                    reward.setStatus(RewardStatusConstant.SUCCESS);
                    reward.setGrantTime(LocalDateTime.now());
                    updateUserRewardRecord(reward);
                    processedCount++;
                }
            }
        }
        
        return processedCount;
    }

    // =================== 验证方法 ===================

    @Override
    public boolean hasPendingRewards(Long userId) {
        return userRewardRecordMapper.countPendingRewards(userId) > 0;
    }

    @Override
    public boolean hasRewardGranted(Long taskRecordId) {
        return !userRewardRecordMapper.findRewardsByTaskRecord(taskRecordId).isEmpty();
    }

    @Override
    public boolean canGrantReward(UserRewardRecord reward) {
        return reward != null && reward.isValidRecord() && reward.canBeGranted();
    }

    @Override
    public boolean validateRewardLimit(Long userId, Integer rewardType, Integer amount, LocalDate date) {
        Long dailySum = getUserDailyRewardSum(userId, rewardType, date);
        // 这里应该根据具体业务规则设置限制，暂时设为10000
        return dailySum + amount <= 10000;
    }

    // =================== 钱包同步相关方法 ===================

    @Override
    public boolean isRewardSyncedToWallet(Long userId, Long rewardId) {
        return taskWalletSyncService.checkWalletSyncStatus(userId, rewardId);
    }

    @Override
    public Map<Long, Boolean> batchCheckRewardSyncStatus(List<Long> rewardIds) {
        Map<Long, Boolean> results = new HashMap<>();
        for (Long rewardId : rewardIds) {
            UserRewardRecord reward = getUserRewardRecordById(rewardId);
            if (reward != null) {
                results.put(rewardId, isRewardSyncedToWallet(reward.getUserId(), rewardId));
            } else {
                results.put(rewardId, false);
            }
        }
        return results;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resyncFailedCoinRewardsToWallet(List<Long> rewardIds) {
        log.info("批量重新同步失败的金币奖励: rewardIds={}", rewardIds);
        
        int syncedCount = 0;
        for (Long rewardId : rewardIds) {
            UserRewardRecord reward = getUserRewardRecordById(rewardId);
            if (reward != null && RewardStatusConstant.isFailed(reward.getStatus()) 
                && RewardTypeConstant.requiresImmediateWalletSync(reward.getRewardType())) {
                
                boolean syncSuccess = taskWalletSyncService.syncCoinRewardToWallet(
                    reward.getUserId(), reward.getRewardAmount(), "batch_resync", reward.getTaskRecordId()
                );
                
                if (syncSuccess) {
                    reward.setStatus(RewardStatusConstant.SUCCESS);
                    reward.setGrantTime(LocalDateTime.now());
                    updateUserRewardRecord(reward);
                    syncedCount++;
                }
            }
        }
        
        log.info("批量重新同步完成: 总数={}, 成功={}", rewardIds.size(), syncedCount);
        return syncedCount == rewardIds.size();
    }

    // =================== 报表统计 ===================

    @Override
    public Map<String, Object> getRewardGrantReport(LocalDate startDate, LocalDate endDate) {
        // 暂时返回空的统计信息，具体实现需要根据业务需求定义
        Map<String, Object> report = new HashMap<>();
        report.put("startDate", startDate);
        report.put("endDate", endDate);
        report.put("totalRewards", 0L);
        report.put("successRewards", 0L);
        report.put("failedRewards", 0L);
        return report;
    }

    @Override
    public List<Map<String, Object>> getUserRewardTrend(Long userId, Integer days) {
        // 暂时返回空列表，具体实现需要根据业务需求定义
        return new ArrayList<>();
    }

    @Override
    public Map<String, Object> getSystemRewardStatistics() {
        // 暂时返回空的统计信息，具体实现需要根据业务需求定义
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRewards", 0L);
        stats.put("totalUsers", 0L);
        stats.put("totalAmount", 0L);
        return stats;
    }
}