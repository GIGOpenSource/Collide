package com.gig.collide.task.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户任务记录实体类 - 简洁版
 * 基于task-simple.sql的t_user_task_record表结构
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
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
     * 任务类型（冗余）
     */
    @TableField("task_type")
    private String taskType;

    /**
     * 任务分类（冗余）
     */
    @TableField("task_category")
    private String taskCategory;

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
        return "daily".equals(taskType);
    }

    /**
     * 判断是否为周常任务
     */
    public boolean isWeeklyTask() {
        return "weekly".equals(taskType);
    }

    /**
     * 判断是否为成就任务
     */
    public boolean isAchievementTask() {
        return "achievement".equals(taskType);
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
        // 周常和成就任务暂不设置过期
        return false;
    }
}