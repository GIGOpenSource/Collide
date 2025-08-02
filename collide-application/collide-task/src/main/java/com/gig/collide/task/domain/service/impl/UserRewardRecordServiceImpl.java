package com.gig.collide.task.domain.service.impl;

import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheUpdate;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
    public UserRewardRecord grantUserReward(Long userId, Long taskRecordId, Integer rewardSource, 
                                          Integer rewardType, String rewardName, Integer rewardAmount, 
                                          String rewardData) {
        log.info("发放用户奖励: userId={}, taskRecordId={}, rewardType={}, rewardAmount={}", 
                userId, taskRecordId, rewardType, rewardAmount);
                
        // 创建奖励记录
        UserRewardRecord rewardRecord = UserRewardRecord.createTaskReward(
            userId, taskRecordId, rewardType, rewardName, rewardAmount
        );
        rewardRecord.setRewardSource(rewardSource);
        if (rewardData != null) {
            rewardRecord.setRewardData(rewardData);
        }
        
        UserRewardRecord created = createUserRewardRecord(rewardRecord);
        
        // 如果是金币奖励，立即同步到钱包
        if (RewardTypeConstant.requiresImmediateWalletSync(rewardType)) {
            boolean syncSuccess = taskWalletSyncService.syncCoinRewardToWallet(
                userId, rewardAmount, "task_reward", taskRecordId
            );
            
            if (syncSuccess) {
                created.setStatus(RewardStatusConstant.SUCCESS);
                created.setGrantTime(LocalDateTime.now());
                updateUserRewardRecord(created);
                log.info("金币奖励已同步到钱包: userId={}, amount={}", userId, rewardAmount);
            } else {
                created.setStatus(RewardStatusConstant.FAILED);
                updateUserRewardRecord(created);
                log.error("金币奖励同步到钱包失败: userId={}, amount={}", userId, rewardAmount);
            }
        }
        
        return created;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserRewardRecord grantUserRewardWithWalletSync(Long userId, Long taskRecordId, Integer rewardType, 
                                                        String rewardName, Integer rewardAmount, String rewardData) {
        log.info("发放用户奖励并同步钱包: userId={}, rewardType={}, rewardAmount={}", 
                userId, rewardType, rewardAmount);
                
        return grantUserReward(userId, taskRecordId, RewardSourceConstant.TASK, 
                             rewardType, rewardName, rewardAmount, rewardData);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchGrantRewardsWithWalletSync(List<UserRewardRecord> rewardRecords) {
        log.info("批量发放奖励并同步钱包: recordCount={}", rewardRecords.size());
        
        int grantedCount = 0;
        List<UserRewardRecord> coinRewards = new ArrayList<>();
        
        for (UserRewardRecord reward : rewardRecords) {
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
    public boolean processCoinRewardWithWalletSync(Long userId, Long taskRecordId, Integer coinAmount) {
        log.info("处理金币奖励并同步钱包: userId={}, taskRecordId={}, coinAmount={}", 
                userId, taskRecordId, coinAmount);
                
        UserRewardRecord coinReward = grantUserRewardWithWalletSync(
            userId, taskRecordId, RewardTypeConstant.COIN, "任务金币奖励", coinAmount, null
        );
        
        return coinReward != null && RewardStatusConstant.isSuccess(coinReward.getStatus());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean processBadgeReward(Long userId, Long taskRecordId, String badgeName, String badgeData) {
        log.info("处理徽章奖励: userId={}, taskRecordId={}, badgeName={}", userId, taskRecordId, badgeName);
        
        UserRewardRecord badgeReward = grantUserReward(
            userId, taskRecordId, RewardSourceConstant.TASK, 
            RewardTypeConstant.BADGE, badgeName, 1, badgeData
        );
        
        if (badgeReward != null) {
            // 徽章奖励直接标记为成功
            badgeReward.setStatus(RewardStatusConstant.SUCCESS);
            badgeReward.setGrantTime(LocalDateTime.now());
            return updateUserRewardRecord(badgeReward);
        }
        
        return false;
    }

    @Override
    public boolean isRewardSyncedToWallet(Long userId, Long taskRecordId) {
        return taskWalletSyncService.checkWalletSyncStatus(userId, taskRecordId);
    }

    @Override
    public List<Boolean> batchCheckRewardSyncStatus(List<Long> userIds, List<Long> taskRecordIds) {
        List<Boolean> results = new ArrayList<>();
        for (int i = 0; i < userIds.size() && i < taskRecordIds.size(); i++) {
            results.add(isRewardSyncedToWallet(userIds.get(i), taskRecordIds.get(i)));
        }
        return results;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int resyncFailedCoinRewardsToWallet(Long userId, LocalDateTime since) {
        log.info("重新同步失败的金币奖励: userId={}, since={}", userId, since);
        
        // 查找失败的金币奖励
        LambdaQueryWrapper<UserRewardRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRewardRecord::getUserId, userId)
               .eq(UserRewardRecord::getRewardType, RewardTypeConstant.COIN)
               .eq(UserRewardRecord::getStatus, RewardStatusConstant.FAILED)
               .ge(since != null, UserRewardRecord::getCreateTime, since);
               
        List<UserRewardRecord> failedRewards = userRewardRecordMapper.selectList(wrapper);
        
        int resyncedCount = 0;
        for (UserRewardRecord reward : failedRewards) {
            boolean syncSuccess = taskWalletSyncService.syncCoinRewardToWallet(
                reward.getUserId(), reward.getRewardAmount(), "task_reward_retry", reward.getTaskRecordId()
            );
            
            if (syncSuccess) {
                reward.setStatus(RewardStatusConstant.SUCCESS);
                reward.setGrantTime(LocalDateTime.now());
                updateUserRewardRecord(reward);
                resyncedCount++;
            }
        }
        
        log.info("重新同步金币奖励完成: 总数={}, 成功={}", failedRewards.size(), resyncedCount);
        return resyncedCount;
    }

    // =================== 查询操作 ===================

    @Override
    @Cached(name = "task:user_reward_query", key = "#userId + ':' + #rewardSource + ':' + #rewardType + ':' + #status", 
            expire = 15, timeUnit = TimeUnit.MINUTES)
    public List<UserRewardRecord> queryUserRewardRecords(Long userId, Integer rewardSource, Integer rewardType, 
                                                       Integer status, LocalDateTime startTime, LocalDateTime endTime) {
        log.debug("查询用户奖励记录: userId={}, rewardType={}, status={}", userId, rewardType, status);
        
        Page<UserRewardRecord> page = new Page<>(1, 100); // 默认分页
        Page<UserRewardRecord> result = userRewardRecordMapper.findWithConditions(
            page, userId, null, rewardSource, rewardType, status, startTime, endTime, null, null
        );
        
        return result.getRecords();
    }

    @Override
    @Cached(name = "task:user_pending_rewards", key = "#userId", expire = 5, timeUnit = TimeUnit.MINUTES)
    public List<UserRewardRecord> getUserPendingRewards(Long userId) {
        return userRewardRecordMapper.findUserPendingRewards(userId);
    }

    @Override
    public Page<UserRewardRecord> getUserGrantedRewards(Page<UserRewardRecord> page, Long userId, 
                                                      Integer rewardType, LocalDateTime startTime, LocalDateTime endTime) {
        return userRewardRecordMapper.findUserGrantedRewards(page, userId, rewardType, startTime, endTime);
    }

    @Override
    public Page<UserRewardRecord> searchUserRewardRecords(Page<UserRewardRecord> page, Long userId, 
                                                        Integer rewardSource, Integer rewardType, Integer status,
                                                        LocalDateTime startTime, LocalDateTime endTime) {
        return userRewardRecordMapper.findWithConditions(
            page, userId, null, rewardSource, rewardType, status, startTime, endTime, "create_time", "DESC"
        );
    }

    @Override
    public List<UserRewardRecord> getRewardsByTaskRecord(Long taskRecordId) {
        return userRewardRecordMapper.findRewardsByTaskRecord(taskRecordId);
    }

    // =================== 统计操作 ===================

    @Override
    @Cached(name = "task:user_reward_sum", key = "#userId + ':' + #rewardType + ':' + #status", 
            expire = 30, timeUnit = TimeUnit.MINUTES)
    public Long sumUserRewardAmount(Long userId, Integer rewardType, Integer status, 
                                  LocalDateTime startTime, LocalDateTime endTime) {
        return userRewardRecordMapper.sumUserRewardAmount(userId, rewardType, status, startTime, endTime);
    }

    @Override
    public Map<String, Object> getUserRewardStatistics(Long userId) {
        return userRewardRecordMapper.getUserRewardStatistics(userId);
    }

    @Override
    public List<Map<String, Object>> countUserRewardsByType(Long userId, Integer status) {
        return userRewardRecordMapper.countRewardsByType(userId, status);
    }

    @Override
    @Cached(name = "task:user_daily_reward", key = "#userId + ':' + #rewardType + ':' + #date", 
            expire = 60, timeUnit = TimeUnit.MINUTES)
    public Long getUserDailyRewardSum(Long userId, Integer rewardType, LocalDate date) {
        return userRewardRecordMapper.getUserDailyRewardSum(userId, rewardType, RewardStatusConstant.SUCCESS, date);
    }

    @Override
    public boolean validateRewardLimit(Long userId, Integer rewardType, Integer dailyLimit, LocalDate date) {
        Long dailySum = getUserDailyRewardSum(userId, rewardType, date);
        return dailySum < dailyLimit;
    }

    // =================== 批量操作 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = "task:user_reward", key = "*")
    public int batchMarkRewardStatus(List<Long> rewardIds, Integer status) {
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
        
        return updatedCount;
    }

    @Override
    public List<UserRewardRecord> findExpiredPendingRewards(LocalDateTime beforeTime) {
        return userRewardRecordMapper.findExpiredPendingRewards(beforeTime);
    }

    @Override
    public List<UserRewardRecord> findFailedRewardsForRetry(LocalDateTime currentTime, Integer retryAfterHours) {
        return userRewardRecordMapper.findFailedRewardsForRetry(currentTime, retryAfterHours);
    }

    @Override
    public List<Map<String, Object>> getCoinRewardRanking(LocalDateTime startTime, LocalDateTime endTime, Integer limit) {
        return userRewardRecordMapper.getCoinRewardRanking(startTime, endTime, limit);
    }

    // =================== 特殊功能 ===================

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanExpiredRewards(Integer daysToKeep) {
        log.info("清理过期奖励记录: daysToKeep={}", daysToKeep);
        
        return userRewardRecordMapper.cleanExpiredRewards(daysToKeep);
    }
}