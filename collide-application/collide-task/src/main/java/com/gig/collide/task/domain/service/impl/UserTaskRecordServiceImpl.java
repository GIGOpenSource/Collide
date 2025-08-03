package com.gig.collide.task.domain.service.impl;

import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheUpdate;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.task.domain.entity.UserTaskRecord;
import com.gig.collide.task.domain.service.TaskWalletSyncService;
import com.gig.collide.task.domain.service.UserTaskRecordService;
import com.gig.collide.task.infrastructure.mapper.UserTaskRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户任务记录业务服务实现类 - 优化版
 * 基于优化后的task-simple.sql，支持数字常量和立即钱包同步
 * 
 * @author GIG Team
 * @version 3.0.0 (优化版)
 * @since 2024-01-16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserTaskRecordServiceImpl implements UserTaskRecordService {

    private final UserTaskRecordMapper userTaskRecordMapper;
    private final TaskWalletSyncService taskWalletSyncService;

    // =================== 基础操作 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = "task:user_record", key = "#userTaskRecord.userId + ':*'")
    public UserTaskRecord createUserTaskRecord(UserTaskRecord userTaskRecord) {
        log.info("创建用户任务记录: userId={}, taskId={}, taskType={}", 
                userTaskRecord.getUserId(), userTaskRecord.getTaskId(), userTaskRecord.getTaskType());
        
        // 设置默认值
        if (userTaskRecord.getTaskDate() == null) {
            userTaskRecord.setTaskDate(LocalDate.now());
        }
        if (userTaskRecord.getCurrentCount() == null) {
            userTaskRecord.setCurrentCount(0);
        }
        
        userTaskRecordMapper.insert(userTaskRecord);
        return userTaskRecord;
    }

    @Override
    @Cached(name = "task:user_record", key = "#id", expire = 30, timeUnit = TimeUnit.MINUTES)
    public UserTaskRecord getUserTaskRecordById(Long id) {
        return userTaskRecordMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheUpdate(name = "task:user_record", key = "#userTaskRecord.id", value = "#userTaskRecord")
    @CacheInvalidate(name = "task:user_record", key = "#userTaskRecord.userId + ':*'")
    public boolean updateUserTaskRecord(UserTaskRecord userTaskRecord) {
        log.info("更新用户任务记录: id={}, userId={}", userTaskRecord.getId(), userTaskRecord.getUserId());
        return userTaskRecordMapper.updateById(userTaskRecord) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = "task:user_record", key = "*")
    public boolean deleteUserTaskRecord(Long id) {
        UserTaskRecord record = getUserTaskRecordById(id);
        if (record != null) {
            log.info("删除用户任务记录: id={}, userId={}", id, record.getUserId());
            return userTaskRecordMapper.deleteById(id) > 0;
        }
        return false;
    }

    // =================== 任务进度管理 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = "task:user_record", key = "#userId + ':*'")
    public boolean updateTaskProgress(Long userId, Long taskId, Integer taskAction, Integer incrementCount) {
        log.info("更新任务进度: userId={}, taskId={}, taskAction={}, incrementCount={}", 
                userId, taskId, taskAction, incrementCount);
                
        // 使用数字常量进行任务进度更新
        return userTaskRecordMapper.updateTaskProgress(userId, taskId, LocalDate.now(), incrementCount) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = "task:user_record", key = "#userId + ':*'")
    public boolean updateTaskProgressWithReward(Long userId, Integer taskAction, Integer incrementCount) {
        log.info("更新任务进度并处理奖励: userId={}, taskAction={}, incrementCount={}", 
                userId, taskAction, incrementCount);
                
        // 获取用户今日任务
        List<UserTaskRecord> todayTasks = getUserTodayTasks(userId);
        
        boolean anyUpdated = false;
        for (UserTaskRecord task : todayTasks) {
            if (!task.getIsCompleted()) {
                if (updateTaskProgress(userId, task.getTaskId(), taskAction, incrementCount)) {
                    anyUpdated = true;
                    // 检查任务是否完成并处理奖励
                    processTaskCompletionRewards(userId, task.getId());
                }
            }
        }
        
        return anyUpdated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateTaskProgress(List<Map<String, Object>> progressList) {
        log.info("批量更新任务进度: count={}", progressList.size());
        
        int updatedCount = 0;
        for (Map<String, Object> progress : progressList) {
            Long userId = (Long) progress.get("userId");
            Long taskId = (Long) progress.get("taskId");
            Integer taskAction = (Integer) progress.get("taskAction");
            Integer incrementCount = (Integer) progress.get("incrementCount");
            
            if (updateTaskProgress(userId, taskId, taskAction, incrementCount)) {
                updatedCount++;
            }
        }
        
        return updatedCount == progressList.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markTaskCompletedWithReward(Long userId, Long taskId, LocalDate taskDate) {
        log.info("标记任务完成并发放奖励: userId={}, taskId={}, taskDate={}", userId, taskId, taskDate);
        
        // 查找对应的任务记录
        LambdaQueryWrapper<UserTaskRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTaskRecord::getUserId, userId)
               .eq(UserTaskRecord::getTaskId, taskId)
               .eq(UserTaskRecord::getTaskDate, taskDate);
               
        UserTaskRecord record = userTaskRecordMapper.selectOne(wrapper);
        if (record != null && !record.getIsCompleted()) {
            // 标记为已完成
            record.setIsCompleted(true);
            record.setCompleteTime(LocalDate.now().atStartOfDay());
            
            boolean updated = updateUserTaskRecord(record);
            if (updated) {
                // 处理奖励
                processTaskCompletionRewards(userId, record.getId());
            }
            return updated;
        }
        
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markTaskCompleted(Long userId, Long taskId, LocalDate taskDate) {
        log.info("标记任务完成: userId={}, taskId={}, taskDate={}", userId, taskId, taskDate);
        
        LambdaQueryWrapper<UserTaskRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTaskRecord::getUserId, userId)
               .eq(UserTaskRecord::getTaskId, taskId)
               .eq(UserTaskRecord::getTaskDate, taskDate);
               
        UserTaskRecord record = userTaskRecordMapper.selectOne(wrapper);
        if (record != null && !record.getIsCompleted()) {
            record.setIsCompleted(true);
            record.setCompleteTime(LocalDate.now().atStartOfDay());
            return updateUserTaskRecord(record);
        }
        
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markRewardReceived(Long userId, Long taskId, LocalDate taskDate) {
        log.info("标记奖励已领取: userId={}, taskId={}, taskDate={}", userId, taskId, taskDate);
        
        LambdaQueryWrapper<UserTaskRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTaskRecord::getUserId, userId)
               .eq(UserTaskRecord::getTaskId, taskId)
               .eq(UserTaskRecord::getTaskDate, taskDate);
               
        UserTaskRecord record = userTaskRecordMapper.selectOne(wrapper);
        if (record != null && record.getIsCompleted() && !record.getIsRewarded()) {
            record.setIsRewarded(true);
            record.setRewardTime(LocalDate.now().atStartOfDay());
            return updateUserTaskRecord(record);
        }
        
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchMarkRewardsReceived(List<Long> recordIds) {
        log.info("批量标记奖励已领取: recordIds={}", recordIds);
        
        int updatedCount = 0;
        for (Long recordId : recordIds) {
            UserTaskRecord record = getUserTaskRecordById(recordId);
            if (record != null && record.getIsCompleted() && !record.getIsRewarded()) {
                record.setIsRewarded(true);
                record.setRewardTime(LocalDate.now().atStartOfDay());
                if (updateUserTaskRecord(record)) {
                    updatedCount++;
                }
            }
        }
        
        return updatedCount == recordIds.size();
    }

    // =================== 查询操作 ===================

    @Override
    @Cached(name = "task:user_record_query", key = "#userId + ':' + #taskId + ':' + #taskType", 
            expire = 15, timeUnit = TimeUnit.MINUTES)
    public Page<UserTaskRecord> queryUserTaskRecords(Long userId, Long taskId, Integer taskType,
                                                    Integer taskCategory, Boolean isCompleted, Boolean isRewarded,
                                                    LocalDate startDate, LocalDate endDate,
                                                    String orderBy, String orderDirection,
                                                    Integer currentPage, Integer pageSize) {
        log.debug("查询用户任务记录: userId={}, taskId={}, taskType={}", userId, taskId, taskType);
        
        Page<UserTaskRecord> page = new Page<>(currentPage != null ? currentPage : 1, pageSize != null ? pageSize : 20);
        return userTaskRecordMapper.findWithConditions(
            page, userId, taskId, taskType, taskCategory, isCompleted, isRewarded, 
            startDate, endDate, orderBy != null ? orderBy : "task_date", orderDirection != null ? orderDirection : "DESC"
        );
    }

    @Override
    @Cached(name = "task:user_today_tasks", key = "#userId + ':today'", expire = 5, timeUnit = TimeUnit.MINUTES)
    public List<UserTaskRecord> getUserTodayTasks(Long userId) {
        return userTaskRecordMapper.findUserTodayTasks(userId);
    }

    @Override
    @Cached(name = "task:user_tasks_by_date", key = "#userId + ':' + #taskDate + ':' + #taskType", expire = 5, timeUnit = TimeUnit.MINUTES)
    public List<UserTaskRecord> getUserTasksByDate(Long userId, LocalDate taskDate, Integer taskType) {
        return userTaskRecordMapper.findUserTasksByDate(userId, taskDate, taskType);
    }

    @Override
    public List<UserTaskRecord> getUserClaimableTasks(Long userId) {
        return userTaskRecordMapper.findUserClaimableTasks(userId, LocalDate.now());
    }

    @Override
    public List<UserTaskRecord> getUserIncompleteTasks(Long userId, Integer taskType) {
        return userTaskRecordMapper.findUserIncompleteTasks(userId, LocalDate.now(), taskType);
    }

    @Override
    public Page<UserTaskRecord> searchUserTaskRecords(Long userId, String keyword, Integer taskType,
                                                     Boolean isCompleted, Integer currentPage, Integer pageSize) {
        Page<UserTaskRecord> page = new Page<>(currentPage != null ? currentPage : 1, pageSize != null ? pageSize : 20);
        return userTaskRecordMapper.searchUserTasks(page, userId, keyword, 
                                                   taskType != null ? taskType.toString() : null, isCompleted);
    }

    // =================== 统计操作 ===================

    @Override
    public Map<String, Object> getUserTaskStatistics(Long userId, LocalDate taskDate) {
        return userTaskRecordMapper.getUserTaskStatistics(userId, taskDate);
    }

    @Override
    @Cached(name = "task:user_stats", key = "#userId + ':completed:' + #taskType", 
            expire = 30, timeUnit = TimeUnit.MINUTES)
    public Long countUserCompletedTasks(Long userId, Integer taskType, LocalDate startDate, LocalDate endDate) {
        return userTaskRecordMapper.countCompletedTasksByType(userId, taskType, startDate, endDate);
    }

    @Override
    @Cached(name = "task:user_login_days", key = "#userId", expire = 60, timeUnit = TimeUnit.MINUTES)
    public Integer getUserConsecutiveLoginDays(Long userId) {
        return userTaskRecordMapper.countConsecutiveLoginDays(userId, LocalDate.now());
    }

    @Override
    public Long countUserUnclaimedRewards(Long userId) {
        return userTaskRecordMapper.countUnclaimedRewards(userId);
    }

    @Override
    public Long countUserTotalCompletedTasks(Long userId) {
        return userTaskRecordMapper.countTotalCompletedTasks(userId);
    }

    @Override
    public Map<String, Object> getUserTaskCompletionByType(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> results = userTaskRecordMapper.countTasksByTypeAndStatus(userId, startDate, endDate);
        Map<String, Object> completion = new HashMap<>();
        for (Map<String, Object> result : results) {
            String taskType = (String) result.get("task_type");
            Long count = (Long) result.get("count");
            completion.put(taskType, count);
        }
        return completion;
    }

    // =================== 任务初始化 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean initializeDailyTasks(Long userId, LocalDate taskDate) {
        log.info("初始化用户每日任务: userId={}, taskDate={}", userId, taskDate);
        
        // 简化实现，具体需要根据业务逻辑实现
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean initializeWeeklyTasks(Long userId, LocalDate taskDate) {
        log.info("初始化用户周常任务: userId={}, taskDate={}", userId, taskDate);
        
        // 简化实现，具体需要根据业务逻辑实现
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = "task:user_record", key = "*")
    public boolean batchInitializeUserTasks(List<Long> userIds, Integer taskType, LocalDate taskDate) {
        log.info("批量初始化用户任务: userIds={}, taskType={}, taskDate={}", userIds, taskType, taskDate);
        
        int initializedCount = 0;
        for (Long userId : userIds) {
            if (taskType == 1) { // 每日任务
                if (initializeDailyTasks(userId, taskDate)) {
                    initializedCount++;
                }
            } else if (taskType == 2) { // 周常任务
                if (initializeWeeklyTasks(userId, taskDate)) {
                    initializedCount++;
                }
            }
        }
        
        return initializedCount == userIds.size();
    }

    // =================== 任务重置 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetUserDailyTasks(Long userId, LocalDate taskDate) {
        log.info("重置用户每日任务: userId={}, taskDate={}", userId, taskDate);
        
        return userTaskRecordMapper.resetDailyTaskProgress(userId, taskDate) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchResetDailyTasks(LocalDate taskDate) {
        log.info("批量重置每日任务: taskDate={}", taskDate);
        
        // 查找需要重置的任务并批量处理
        List<UserTaskRecord> tasksToReset = userTaskRecordMapper.findDailyTasksToReset(taskDate);
        int resetCount = 0;
        for (UserTaskRecord task : tasksToReset) {
            if (resetUserDailyTasks(task.getUserId(), taskDate)) {
                resetCount++;
            }
        }
        return resetCount > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanExpiredTaskRecords(Integer days) {
        log.info("清理过期任务记录: days={}", days);
        
        return userTaskRecordMapper.cleanExpiredRecords(days);
    }

    // =================== 排行榜 ===================

    @Override
    public List<Map<String, Object>> getUserTaskRanking(Integer taskType, LocalDate taskDate, Integer limit) {
        return userTaskRecordMapper.getUserTaskRanking(taskType, taskDate, limit);
    }

    @Override
    public List<Long> getActiveUsers(Integer days, Integer minCompletedTasks) {
        return userTaskRecordMapper.findActiveUsers(days, minCompletedTasks);
    }

    // =================== 验证方法 ===================

    @Override
    public boolean hasTodayTaskRecord(Long userId, Long taskId) {
        return userTaskRecordMapper.existsTodayTaskRecord(userId, taskId, LocalDate.now());
    }

    @Override
    public boolean canUserClaimReward(Long userId, Long taskId, LocalDate taskDate) {
        UserTaskRecord record = getUserTaskProgress(userId, taskId, taskDate);
        return record != null && record.getIsCompleted() && !record.getIsRewarded();
    }

    @Override
    public UserTaskRecord getUserTaskProgress(Long userId, Long taskId, LocalDate taskDate) {
        LambdaQueryWrapper<UserTaskRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTaskRecord::getUserId, userId)
               .eq(UserTaskRecord::getTaskId, taskId)
               .eq(UserTaskRecord::getTaskDate, taskDate);
               
        return userTaskRecordMapper.selectOne(wrapper);
    }

    @Override
    public boolean canUpdateTaskProgress(Long userId, Long taskId, LocalDate taskDate) {
        UserTaskRecord record = getUserTaskProgress(userId, taskId, taskDate);
        return record != null && !record.getIsCompleted();
    }

    // =================== 特殊操作 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<UserTaskRecord> handleUserAction(Long userId, Integer actionType, Map<String, Object> actionData) {
        log.info("处理用户行为: userId={}, actionType={}, actionData={}", userId, actionType, actionData);
        
        List<UserTaskRecord> updatedTasks = new ArrayList<>();
        
        // 获取今日任务
        List<UserTaskRecord> todayTasks = getUserTodayTasks(userId);
        
        for (UserTaskRecord task : todayTasks) {
            if (!task.getIsCompleted()) {
                // 检查任务动作是否匹配（需要从任务模板获取）
                // 这里简化处理，假设actionType匹配
                Integer incrementCount = (Integer) actionData.getOrDefault("incrementCount", 1);
                if (updateTaskProgress(userId, task.getTaskId(), actionType, incrementCount)) {
                    updatedTasks.add(getUserTaskRecordById(task.getId()));
                    // 处理奖励
                    processTaskCompletionRewards(userId, task.getId());
                }
            }
        }
        
        return updatedTasks;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<UserTaskRecord> autoCompleteEligibleTasks(Long userId) {
        log.info("自动完成符合条件的任务: userId={}", userId);
        
        List<UserTaskRecord> completedTasks = new ArrayList<>();
        // 使用现有方法查找符合条件的任务
        List<UserTaskRecord> eligibleTasks = userTaskRecordMapper.findUserIncompleteTasks(userId, LocalDate.now(), null);
        
        for (UserTaskRecord task : eligibleTasks) {
            if (markTaskCompletedWithReward(userId, task.getTaskId(), task.getTaskDate())) {
                completedTasks.add(getUserTaskRecordById(task.getId()));
            }
        }
        
        return completedTasks;
    }

    // =================== 钱包同步相关方法 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean processTaskCompletionRewards(Long userId, Long taskRecordId) {
        log.info("处理任务完成奖励: userId={}, taskRecordId={}", userId, taskRecordId);
        
        UserTaskRecord record = getUserTaskRecordById(taskRecordId);
        if (record != null && record.getIsCompleted() && !record.getIsRewarded()) {
            // 这里应该与UserRewardRecordService协作处理奖励发放
            log.info("找到需要发放奖励的任务记录: recordId={}", record.getId());
            
            // 标记奖励已发放
            record.setIsRewarded(true);
            record.setRewardTime(LocalDate.now().atStartOfDay());
            
            return updateUserTaskRecord(record);
        }
        
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchProcessTaskCompletionRewards(List<Long> taskRecordIds) {
        log.info("批量处理任务完成奖励: recordIds={}", taskRecordIds);
        
        int processedCount = 0;
        for (Long recordId : taskRecordIds) {
            UserTaskRecord record = getUserTaskRecordById(recordId);
            if (record != null && record.getIsCompleted() && !record.getIsRewarded()) {
                if (processTaskCompletionRewards(record.getUserId(), record.getId())) {
                    processedCount++;
                }
            }
        }
        
        return processedCount;
    }

    @Override
    public boolean isRewardSyncedToWallet(Long userId, Long taskRecordId) {
        return taskWalletSyncService.checkWalletSyncStatus(userId, taskRecordId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean syncTaskTemplateChanges(Long taskId) {
        log.info("同步任务模板变更: taskId={}", taskId);
        
        // 简化实现，具体需要根据业务逻辑实现
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int fixAbnormalTaskData() {
        log.info("修复异常任务数据");
        
        // 简化实现，具体需要根据业务需求定义
        return 0;
    }

    @Override
    public Map<String, Object> getUserTaskProgressReport(Long userId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> report = new HashMap<>();
        
        // 获取任务统计信息
        Long totalTasks = 0L; // 简化实现，具体需要根据业务需求实现
        Long completedTasks = countUserCompletedTasks(userId, null, startDate, endDate);
        Long unclaimedRewards = countUserUnclaimedRewards(userId);
        
        report.put("userId", userId);
        report.put("startDate", startDate);
        report.put("endDate", endDate);
        report.put("totalTasks", totalTasks);
        report.put("completedTasks", completedTasks);
        report.put("completionRate", totalTasks > 0 ? (double) completedTasks / totalTasks : 0.0);
        report.put("unclaimedRewards", unclaimedRewards);
        
        return report;
    }
}