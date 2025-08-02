package com.gig.collide.task.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.gig.collide.task.domain.constant.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户任务记录实体类 - 优化版
 * 基于优化后的task-simple.sql的t_user_task_record表结构
 * 使用数字常量存储任务类型和分类，保持数据一致性
 * 
 * @author GIG Team
 * @version 2.0.0 (优化版)
 * @since 2024-01-16
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@TableName("t_user_task_record")
public class UserTaskRecord {

    /**
     * 记录ID - 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 任务模板ID
     */
    @TableField("task_id")
    private Long taskId;

    /**
     * 任务日期（用于每日任务）
     */
    @TableField("task_date")
    private LocalDate taskDate;

    /**
     * 任务名称（冗余）
     */
    @TableField("task_name")
    private String taskName;

    /**
     * 任务类型（冗余）：1-daily, 2-weekly, 3-monthly, 4-achievement
     */
    @TableField("task_type")
    private Integer taskType;

    /**
     * 任务分类（冗余）：1-login, 2-content, 3-social, 4-consume, 5-invite
     */
    @TableField("task_category")
    private Integer taskCategory;

    /**
     * 目标完成次数（冗余）
     */
    @TableField("target_count")
    private Integer targetCount;

    /**
     * 当前完成次数
     */
    @TableField("current_count")
    private Integer currentCount;

    /**
     * 是否已完成
     */
    @TableField("is_completed")
    private Boolean isCompleted;

    /**
     * 是否已领取奖励
     */
    @TableField("is_rewarded")
    private Boolean isRewarded;

    /**
     * 完成时间
     */
    @TableField("complete_time")
    private LocalDateTime completeTime;

    /**
     * 奖励领取时间
     */
    @TableField("reward_time")
    private LocalDateTime rewardTime;

    /**
     * 创建时间 - 自动填充
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间 - 自动填充
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // =================== 业务方法 ===================

    /**
     * 判断任务是否已完成
     */
    public boolean isTaskCompleted() {
        return Boolean.TRUE.equals(isCompleted);
    }

    /**
     * 判断奖励是否已领取
     */
    public boolean isRewardReceived() {
        return Boolean.TRUE.equals(isRewarded);
    }

    /**
     * 判断是否为每日任务
     */
    public boolean isDailyTask() {
        return TaskTypeConstant.isDailyTask(taskType);
    }

    /**
     * 判断是否为周常任务
     */
    public boolean isWeeklyTask() {
        return TaskTypeConstant.isWeeklyTask(taskType);
    }

    /**
     * 判断是否为月度任务
     */
    public boolean isMonthlyTask() {
        return TaskTypeConstant.isMonthlyTask(taskType);
    }

    /**
     * 判断是否为成就任务
     */
    public boolean isAchievementTask() {
        return TaskTypeConstant.isAchievementTask(taskType);
    }

    /**
     * 获取任务完成进度百分比
     */
    public double getProgressPercentage() {
        if (targetCount == null || targetCount <= 0) {
            return 0.0;
        }
        if (currentCount == null) {
            return 0.0;
        }
        double progress = (double) Math.min(currentCount, targetCount) / targetCount * 100;
        return Math.round(progress * 100.0) / 100.0; // 保留两位小数
    }

    /**
     * 获取剩余需完成次数
     */
    public int getRemainingCount() {
        if (targetCount == null || currentCount == null) {
            return 0;
        }
        return Math.max(0, targetCount - currentCount);
    }

    /**
     * 判断是否可以领取奖励
     */
    public boolean canClaimReward() {
        return isTaskCompleted() && !isRewardReceived();
    }

    /**
     * 增加完成次数
     */
    public void incrementCurrentCount(int increment) {
        if (currentCount == null) {
            currentCount = 0;
        }
        currentCount += increment;
        
        // 检查是否达成任务目标
        if (targetCount != null && currentCount >= targetCount) {
            markAsCompleted();
        }
    }

    /**
     * 标记任务为已完成
     */
    public void markAsCompleted() {
        this.isCompleted = true;
        this.completeTime = LocalDateTime.now();
    }

    /**
     * 标记奖励为已领取
     */
    public void markAsRewarded() {
        this.isRewarded = true;
        this.rewardTime = LocalDateTime.now();
    }

    /**
     * 重置任务进度（用于每日任务重置）
     */
    public void resetProgress() {
        this.currentCount = 0;
        this.isCompleted = false;
        this.isRewarded = false;
        this.completeTime = null;
        this.rewardTime = null;
    }

    /**
     * 判断是否为今日任务
     */
    public boolean isTodayTask() {
        return taskDate != null && taskDate.equals(LocalDate.now());
    }

    /**
     * 判断任务是否过期
     */
    public boolean isExpired() {
        if (isDailyTask()) {
            return taskDate != null && taskDate.isBefore(LocalDate.now());
        }
        // 周常、月度和成就任务暂不设置过期
        return false;
    }

    // =================== 新增业务方法 ===================

    /**
     * 获取任务类型名称
     */
    public String getTaskTypeName() {
        return TaskTypeConstant.getTypeName(taskType);
    }

    /**
     * 获取任务分类名称
     */
    public String getTaskCategoryName() {
        return TaskCategoryConstant.getCategoryName(taskCategory);
    }

    /**
     * 从任务模板复制基础信息
     */
    public void copyFromTemplate(TaskTemplate template) {
        if (template != null) {
            this.taskId = template.getId();
            this.taskName = template.getTaskName();
            this.taskType = template.getTaskType();
            this.taskCategory = template.getTaskCategory();
            this.targetCount = template.getTargetCount();
        }
    }

    /**
     * 更新任务进度，如果达成目标则自动完成
     */
    public boolean updateProgress(int increment) {
        incrementCurrentCount(increment);
        return isTaskCompleted();
    }

    /**
     * 获取任务状态描述
     */
    public String getStatusDescription() {
        if (isTaskCompleted()) {
            return isRewardReceived() ? "已完成并领取奖励" : "已完成待领取奖励";
        } else if (isExpired()) {
            return "已过期";
        } else {
            return String.format("进行中 (%d/%d)", 
                currentCount != null ? currentCount : 0, 
                targetCount != null ? targetCount : 0);
        }
    }

    /**
     * 创建用户任务记录的工厂方法
     */
    public static UserTaskRecord createFromTemplate(Long userId, TaskTemplate template, LocalDate taskDate) {
        UserTaskRecord record = new UserTaskRecord();
        record.setUserId(userId);
        record.setTaskDate(taskDate);
        record.copyFromTemplate(template);
        record.setCurrentCount(0);
        record.setIsCompleted(false);
        record.setIsRewarded(false);
        return record;
    }
}