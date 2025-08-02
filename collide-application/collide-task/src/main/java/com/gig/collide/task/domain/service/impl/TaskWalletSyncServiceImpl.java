package com.gig.collide.task.domain.service.impl;

import com.gig.collide.task.domain.constant.RewardTypeConstant;
import com.gig.collide.task.domain.entity.UserRewardRecord;
import com.gig.collide.task.domain.service.TaskWalletSyncService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 任务钱包同步服务实现类 - 优化版
 * 负责任务完成后奖励立即同步到用户钱包
 * 
 * @author GIG Team
 * @version 2.0.0 (优化版)
 * @since 2024-01-16
 */
@Slf4j
@Service
public class TaskWalletSyncServiceImpl implements TaskWalletSyncService {

    // 注入用户钱包服务的Dubbo引用
    // TODO: 需要根据实际的用户模块钱包服务接口进行调整
    /*
    @DubboReference
    private UserWalletFacadeService userWalletFacadeService;
    */

    // 临时模拟钱包服务调用，实际需要替换为真实的Dubbo服务调用
    private final Map<Long, Long> mockUserCoinBalance = new HashMap<>();
    private final List<Map<String, Object>> syncOperationLog = new ArrayList<>();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean syncCoinRewardToWallet(Long userId, Integer coinAmount, String source, Long taskRecordId) {
        try {
            log.info("开始同步金币奖励到用户钱包: userId={}, coinAmount={}, source={}, taskRecordId={}", 
                    userId, coinAmount, source, taskRecordId);

            // 1. 验证参数
            if (userId == null || coinAmount == null || coinAmount <= 0) {
                log.error("钱包同步参数无效: userId={}, coinAmount={}", userId, coinAmount);
                recordSyncOperation(userId, coinAmount, source, taskRecordId, false, "参数无效");
                return false;
            }

            // 2. 检查钱包服务是否可用
            if (!isWalletServiceAvailable()) {
                log.error("钱包服务不可用，同步失败: userId={}", userId);
                recordSyncOperation(userId, coinAmount, source, taskRecordId, false, "钱包服务不可用");
                return false;
            }

            // 3. 调用钱包服务发放金币
            boolean success = callWalletServiceGrantCoin(userId, coinAmount, source);
            
            if (success) {
                log.info("金币奖励同步成功: userId={}, coinAmount={}, source={}", userId, coinAmount, source);
                recordSyncOperation(userId, coinAmount, source, taskRecordId, true, null);
                return true;
            } else {
                log.error("金币奖励同步失败: userId={}, coinAmount={}", userId, coinAmount);
                recordSyncOperation(userId, coinAmount, source, taskRecordId, false, "钱包服务调用失败");
                return false;
            }

        } catch (Exception e) {
            log.error("金币奖励同步异常: userId={}, coinAmount={}, error={}", userId, coinAmount, e.getMessage(), e);
            recordSyncOperation(userId, coinAmount, source, taskRecordId, false, e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchSyncCoinRewards(List<UserRewardRecord> rewardRecords) {
        if (rewardRecords == null || rewardRecords.isEmpty()) {
            return 0;
        }

        AtomicInteger successCount = new AtomicInteger(0);
        
        rewardRecords.stream()
                .filter(record -> RewardTypeConstant.isCoinReward(record.getRewardType()))
                .forEach(record -> {
                    try {
                        boolean success = syncCoinRewardToWallet(
                                record.getUserId(),
                                record.getRewardAmount(),
                                record.getRewardSourceName(),
                                record.getTaskRecordId()
                        );
                        
                        if (success) {
                            // 更新奖励记录状态为成功
                            record.markAsSuccess();
                            successCount.incrementAndGet();
                        } else {
                            // 更新奖励记录状态为失败
                            record.markAsFailed();
                        }
                        
                    } catch (Exception e) {
                        log.error("批量同步金币奖励异常: recordId={}, userId={}, error={}", 
                                record.getId(), record.getUserId(), e.getMessage(), e);
                        record.markAsFailed();
                    }
                });

        log.info("批量同步金币奖励完成: 总数={}, 成功={}", rewardRecords.size(), successCount.get());
        return successCount.get();
    }

    @Override
    public boolean checkWalletSyncStatus(Long userId, Long taskRecordId) {
        try {
            // 检查同步操作记录
            return syncOperationLog.stream()
                    .anyMatch(log -> Objects.equals(log.get("userId"), userId) &&
                                   Objects.equals(log.get("taskRecordId"), taskRecordId) &&
                                   Boolean.TRUE.equals(log.get("success")));
        } catch (Exception e) {
            log.error("检查钱包同步状态异常: userId={}, taskRecordId={}, error={}", 
                    userId, taskRecordId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Long getCurrentCoinBalance(Long userId) {
        try {
            // TODO: 调用真实的钱包服务查询余额
            // return userWalletFacadeService.getCoinBalance(userId);
            
            // 临时模拟实现
            return mockUserCoinBalance.getOrDefault(userId, 0L);
        } catch (Exception e) {
            log.error("查询用户金币余额异常: userId={}, error={}", userId, e.getMessage(), e);
            return 0L;
        }
    }

    @Override
    public Long getTotalEarnedCoins(Long userId) {
        try {
            // TODO: 调用真实的钱包服务查询累计金币
            // return userWalletFacadeService.getTotalEarnedCoins(userId);
            
            // 临时模拟实现
            return syncOperationLog.stream()
                    .filter(log -> Objects.equals(log.get("userId"), userId) &&
                                 Boolean.TRUE.equals(log.get("success")))
                    .mapToLong(log -> ((Integer) log.get("amount")).longValue())
                    .sum();
        } catch (Exception e) {
            log.error("查询用户累计金币异常: userId={}, error={}", userId, e.getMessage(), e);
            return 0L;
        }
    }

    @Override
    public void recordSyncOperation(Long userId, Integer amount, String source, 
                                  Long taskRecordId, boolean success, String errorMessage) {
        try {
            Map<String, Object> record = new HashMap<>();
            record.put("userId", userId);
            record.put("amount", amount);
            record.put("source", source);
            record.put("taskRecordId", taskRecordId);
            record.put("success", success);
            record.put("errorMessage", errorMessage);
            record.put("timestamp", LocalDateTime.now());
            
            syncOperationLog.add(record);
            
            // 保留最近1000条记录，避免内存溢出
            if (syncOperationLog.size() > 1000) {
                syncOperationLog.remove(0);
            }
            
        } catch (Exception e) {
            log.error("记录同步操作异常: error={}", e.getMessage(), e);
        }
    }

    @Override
    public List<Long> getFailedSyncRecords(Integer retryCount) {
        try {
            return syncOperationLog.stream()
                    .filter(log -> !Boolean.TRUE.equals(log.get("success")))
                    .filter(log -> {
                        // 简单的重试逻辑，实际应该从数据库查询
                        LocalDateTime timestamp = (LocalDateTime) log.get("timestamp");
                        return timestamp.isAfter(LocalDateTime.now().minusHours(24));
                    })
                    .map(log -> (Long) log.get("taskRecordId"))
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();
        } catch (Exception e) {
            log.error("获取失败同步记录异常: error={}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int retryFailedSyncOperations(List<Long> taskRecordIds) {
        if (taskRecordIds == null || taskRecordIds.isEmpty()) {
            return 0;
        }

        AtomicInteger retrySuccessCount = new AtomicInteger(0);
        
        taskRecordIds.forEach(taskRecordId -> {
            try {
                // 查找失败的同步记录
                Optional<Map<String, Object>> failedRecord = syncOperationLog.stream()
                        .filter(log -> Objects.equals(log.get("taskRecordId"), taskRecordId) &&
                                     !Boolean.TRUE.equals(log.get("success")))
                        .findFirst();
                
                if (failedRecord.isPresent()) {
                    Map<String, Object> record = failedRecord.get();
                    Long userId = (Long) record.get("userId");
                    Integer amount = (Integer) record.get("amount");
                    String source = (String) record.get("source");
                    
                    boolean success = syncCoinRewardToWallet(userId, amount, source + "(重试)", taskRecordId);
                    if (success) {
                        retrySuccessCount.incrementAndGet();
                    }
                }
                
            } catch (Exception e) {
                log.error("重试同步操作异常: taskRecordId={}, error={}", taskRecordId, e.getMessage(), e);
            }
        });

        log.info("重试失败同步操作完成: 总数={}, 成功={}", taskRecordIds.size(), retrySuccessCount.get());
        return retrySuccessCount.get();
    }

    @Override
    public boolean callWalletServiceGrantCoin(Long userId, Integer amount, String source) {
        try {
            // TODO: 调用真实的钱包服务
            /*
            Result<Boolean> result = userWalletFacadeService.grantCoin(userId, amount, source);
            return result != null && result.isSuccess() && Boolean.TRUE.equals(result.getData());
            */
            
            // 临时模拟实现 - 直接更新模拟的余额
            Long currentBalance = mockUserCoinBalance.getOrDefault(userId, 0L);
            mockUserCoinBalance.put(userId, currentBalance + amount);
            
            log.info("模拟钱包服务调用成功: userId={}, amount={}, newBalance={}", 
                    userId, amount, currentBalance + amount);
            return true;
            
        } catch (Exception e) {
            log.error("调用钱包服务异常: userId={}, amount={}, error={}", userId, amount, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean isWalletServiceAvailable() {
        try {
            // TODO: 实际检查钱包服务健康状态
            // return userWalletFacadeService.isHealthy();
            
            // 临时模拟实现
            return true;
        } catch (Exception e) {
            log.error("检查钱包服务可用性异常: error={}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getTodaySyncStatistics() {
        try {
            LocalDate today = LocalDate.now();
            
            long todaySuccessCount = syncOperationLog.stream()
                    .filter(log -> {
                        LocalDateTime timestamp = (LocalDateTime) log.get("timestamp");
                        return timestamp.toLocalDate().equals(today) && Boolean.TRUE.equals(log.get("success"));
                    })
                    .count();
            
            long todayFailCount = syncOperationLog.stream()
                    .filter(log -> {
                        LocalDateTime timestamp = (LocalDateTime) log.get("timestamp");
                        return timestamp.toLocalDate().equals(today) && !Boolean.TRUE.equals(log.get("success"));
                    })
                    .count();
            
            Integer todayTotalAmount = syncOperationLog.stream()
                    .filter(log -> {
                        LocalDateTime timestamp = (LocalDateTime) log.get("timestamp");
                        return timestamp.toLocalDate().equals(today) && Boolean.TRUE.equals(log.get("success"));
                    })
                    .mapToInt(log -> (Integer) log.get("amount"))
                    .sum();
            
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("date", today);
            statistics.put("successCount", todaySuccessCount);
            statistics.put("failCount", todayFailCount);
            statistics.put("totalCount", todaySuccessCount + todayFailCount);
            statistics.put("totalAmount", todayTotalAmount);
            statistics.put("successRate", todaySuccessCount + todayFailCount > 0 ? 
                          (double) todaySuccessCount / (todaySuccessCount + todayFailCount) : 0.0);
            
            return statistics;
            
        } catch (Exception e) {
            log.error("获取今日同步统计异常: error={}", e.getMessage(), e);
            return Collections.emptyMap();
        }
    }

    @Override
    public List<Map<String, Object>> getUserSyncHistory(Long userId, Integer days) {
        try {
            LocalDateTime startTime = LocalDateTime.now().minusDays(days);
            
            return syncOperationLog.stream()
                    .filter(log -> Objects.equals(log.get("userId"), userId))
                    .filter(log -> {
                        LocalDateTime timestamp = (LocalDateTime) log.get("timestamp");
                        return timestamp.isAfter(startTime);
                    })
                    .sorted((log1, log2) -> {
                        LocalDateTime time1 = (LocalDateTime) log1.get("timestamp");
                        LocalDateTime time2 = (LocalDateTime) log2.get("timestamp");
                        return time2.compareTo(time1); // 降序排列
                    })
                    .limit(100) // 限制返回数量
                    .toList();
                    
        } catch (Exception e) {
            log.error("获取用户同步历史异常: userId={}, error={}", userId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, Object> getWalletSyncHealthStatus() {
        try {
            Map<String, Object> healthStatus = new HashMap<>();
            
            // 计算最近24小时的统计数据
            LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
            List<Map<String, Object>> recentLogs = syncOperationLog.stream()
                    .filter(log -> {
                        LocalDateTime timestamp = (LocalDateTime) log.get("timestamp");
                        return timestamp.isAfter(last24Hours);
                    })
                    .toList();
            
            long successCount = recentLogs.stream()
                    .filter(log -> Boolean.TRUE.equals(log.get("success")))
                    .count();
            
            long failCount = recentLogs.size() - successCount;
            double successRate = recentLogs.size() > 0 ? (double) successCount / recentLogs.size() : 1.0;
            
            healthStatus.put("walletServiceAvailable", isWalletServiceAvailable());
            healthStatus.put("last24HoursTotal", recentLogs.size());
            healthStatus.put("last24HoursSuccess", successCount);
            healthStatus.put("last24HoursFail", failCount);
            healthStatus.put("successRate", successRate);
            healthStatus.put("status", successRate >= 0.95 ? "HEALTHY" : successRate >= 0.8 ? "WARNING" : "ERROR");
            healthStatus.put("lastCheckTime", LocalDateTime.now());
            
            return healthStatus;
            
        } catch (Exception e) {
            log.error("获取钱包同步健康状态异常: error={}", e.getMessage(), e);
            Map<String, Object> errorStatus = new HashMap<>();
            errorStatus.put("status", "ERROR");
            errorStatus.put("error", e.getMessage());
            errorStatus.put("lastCheckTime", LocalDateTime.now());
            return errorStatus;
        }
    }
}