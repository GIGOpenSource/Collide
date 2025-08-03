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
    public boolean updateTaskProgressWithReward(Long userId, Long taskId, Integer taskAction, Integer incrementCount) {
        log.info("更新任务进度并处理奖励: userId={}, taskId={}, taskAction={}, incrementCount={}", 
                userId, taskId, taskAction, incrementCount);
                
        // 1. 更新任务进度
        boolean progressUpdated = updateTaskProgress(userId, taskId, taskAction, incrementCount);
        
        if (progressUpdated) {
            // 2. 检查任务是否完成并处理奖励
            processTaskCompletionRewards(userId, taskId, LocalDate.now());
        }
        
        return progressUpdated;
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
                processTaskCompletionRewards(userId, taskId, taskDate);
            }
            return updated;
        }
        
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean processTaskCompletionRewards(Long userId, Long taskId, LocalDate taskDate) {
        log.info("处理任务完成奖励: userId={}, taskId={}, taskDate={}", userId, taskId, taskDate);
        
        // 查找已完成但未发放奖励的任务
        LambdaQueryWrapper<UserTaskRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTaskRecord::getUserId, userId)
               .eq(UserTaskRecord::getTaskId, taskId)
               .eq(UserTaskRecord::getTaskDate, taskDate)
               .eq(UserTaskRecord::getIsCompleted, true)
               .eq(UserTaskRecord::getIsRewarded, false);
               
        UserTaskRecord record = userTaskRecordMapper.selectOne(wrapper);
        if (record != null) {
            // 调用奖励服务处理奖励发放
            // 这里需要与UserRewardRecordService协作
            log.info("找到需要发放奖励的任务记录: recordId={}", record.getId());
            
            // 标记奖励已发放（具体奖励发放逻辑在UserRewardRecordService中处理）
            record.setIsRewarded(true);
            record.setRewardTime(LocalDate.now().atStartOfDay());
            
            return updateUserTaskRecord(record);
        }
        
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchProcessTaskCompletionRewards(List<Long> userTaskRecordIds) {
        log.info("批量处理任务完成奖励: recordIds={}", userTaskRecordIds);
        
        int processedCount = 0;
        for (Long recordId : userTaskRecordIds) {
            UserTaskRecord record = getUserTaskRecordById(recordId);
            if (record != null && record.getIsCompleted() && !record.getIsRewarded()) {
                if (processTaskCompletionRewards(record.getUserId(), record.getTaskId(), record.getTaskDate())) {
                    processedCount++;
                }
            }
        }
        
        return processedCount;
    }

    @Override
    public boolean isRewardSyncedToWallet(Long userId, Long taskRecordId) {
        // 检查奖励是否已同步到钱包
        return taskWalletSyncService.checkWalletSyncStatus(userId, taskRecordId);
    }

    // =================== 查询操作 ===================

    @Override
    @Cached(name = "task:user_record_query", key = "#userId + ':' + #taskDate + ':' + #taskType", 
            expire = 15, timeUnit = TimeUnit.MINUTES)
    public List<UserTaskRecord> queryUserTaskRecords(Long userId, LocalDate taskDate, Integer taskType, 
                                                   Integer taskCategory, Boolean isCompleted, Boolean isRewarded) {
        log.debug("查询用户任务记录: userId={}, taskDate={}, taskType={}", userId, taskDate, taskType);
        
        Page<UserTaskRecord> page = new Page<>(1, 100); // 默认分页
        Page<UserTaskRecord> result = userTaskRecordMapper.findWithConditions(
            page, userId, null, taskType, taskCategory, isCompleted, isRewarded, 
            null, null, null, null
        );
        
        return result.getRecords();
    }

    @Override
    @Cached(name = "task:user_today_tasks", key = "#userId", expire = 5, timeUnit = TimeUnit.MINUTES)
    public List<UserTaskRecord> getUserTasksByDate(Long userId, LocalDate taskDate, Integer taskType) {
        return userTaskRecordMapper.findUserTasksByDate(userId, taskDate, taskType);
    }

    @Override
    @Cached(name = "task:user_today_tasks", key = "#userId + ':today'", expire = 5, timeUnit = TimeUnit.MINUTES)
    public List<UserTaskRecord> getUserTodayTasks(Long userId) {
        return userTaskRecordMapper.findUserTodayTasks(userId);
    }

    @Override
    public Page<UserTaskRecord> searchUserTaskRecords(Page<UserTaskRecord> page, Long userId, 
                                                    Integer taskType, Integer taskCategory, 
                                                    Boolean isCompleted, Boolean isRewarded,
                                                    LocalDate startDate, LocalDate endDate) {
        return userTaskRecordMapper.findWithConditions(
            page, userId, null, taskType, taskCategory, isCompleted, isRewarded,
            startDate, endDate, "task_date", "DESC"
        );
    }

    // =================== 统计操作 ===================

    @Override
    @Cached(name = "task:user_stats", key = "#userId + ':completed:' + #taskType", 
            expire = 30, timeUnit = TimeUnit.MINUTES)
    public Long countUserCompletedTasks(Long userId, Integer taskType, LocalDate startDate, LocalDate endDate) {
        return userTaskRecordMapper.countCompletedTasksByType(userId, taskType, startDate, endDate);
    }

    @Override
    public Map<String, Object> getUserTaskStatistics(Long userId, LocalDate taskDate) {
        return userTaskRecordMapper.getUserTaskStatistics(userId, taskDate);
    }

    @Override
    @Cached(name = "task:user_login_days", key = "#userId", expire = 60, timeUnit = TimeUnit.MINUTES)
    public Integer getUserConsecutiveLoginDays(Long userId, LocalDate endDate) {
        // 使用数字常量 1 表示登录任务分类
        return userTaskRecordMapper.countConsecutiveLoginDays(userId, endDate);
    }

    // =================== 批量操作 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = "task:user_record", key = "*")
    public int batchInitializeUserTasks(Long userId, List<Long> taskIds, LocalDate taskDate) {
        log.info("批量初始化用户任务: userId={}, taskIds={}, taskDate={}", userId, taskIds, taskDate);
        
        int initializedCount = 0;
        for (Long taskId : taskIds) {
            // 检查是否已存在
            LambdaQueryWrapper<UserTaskRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserTaskRecord::getUserId, userId)
                   .eq(UserTaskRecord::getTaskId, taskId)
                   .eq(UserTaskRecord::getTaskDate, taskDate);
                   
            if (userTaskRecordMapper.selectCount(wrapper) == 0) {
                UserTaskRecord record = new UserTaskRecord();
                record.setUserId(userId);
                record.setTaskId(taskId);
                record.setTaskDate(taskDate);
                record.setCurrentCount(0);
                record.setIsCompleted(false);
                record.setIsRewarded(false);
                
                if (createUserTaskRecord(record) != null) {
                    initializedCount++;
                }
            }
        }
        
        return initializedCount;
    }

    @Override
    public List<Map<String, Object>> getUserTaskRanking(Integer taskType, LocalDate taskDate, Integer limit) {
        return userTaskRecordMapper.getUserTaskRanking(taskType, taskDate, limit);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleUserAction(Long userId, Integer taskAction, Integer incrementCount) {
        log.info("处理用户行为: userId={}, taskAction={}, incrementCount={}", userId, taskAction, incrementCount);
        
        // 根据任务动作更新相关任务进度
        // 这里需要查找所有匹配该动作的活跃任务
        List<UserTaskRecord> todayTasks = getUserTodayTasks(userId);
        
        boolean anyUpdated = false;
        for (UserTaskRecord task : todayTasks) {
            // 检查任务动作是否匹配（需要从任务模板获取）
            // 这里简化处理，实际需要与TaskTemplateService协作
            if (!task.getIsCompleted()) {
                if (updateTaskProgressWithReward(userId, task.getTaskId(), taskAction, incrementCount)) {
                    anyUpdated = true;
                }
            }
        }
        
        return anyUpdated;
    }
}