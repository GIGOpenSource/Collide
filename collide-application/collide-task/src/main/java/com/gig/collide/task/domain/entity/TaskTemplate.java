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
 * 任务模板实体类 - 优化版
 * 基于优化后的task-simple.sql的t_task_template表结构
 * 使用数字常量替代字符串枚举，提升性能和类型安全
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
@TableName("t_task_template")
public class TaskTemplate {

    /**
     * 任务模板ID - 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务名称
     */
    @TableField("task_name")
    private String taskName;

    /**
     * 任务描述
     */
    @TableField("task_desc")
    private String taskDesc;

    /**
     * 任务类型：1-daily, 2-weekly, 3-monthly, 4-achievement
     */
    @TableField("task_type")
    private Integer taskType;

    /**
     * 任务分类：1-login, 2-content, 3-social, 4-consume, 5-invite
     */
    @TableField("task_category")
    private Integer taskCategory;

    /**
     * 任务动作：1-login, 2-publish_content, 3-like, 4-comment, 5-share, 6-purchase, 7-invite_user
     */
    @TableField("task_action")
    private Integer taskAction;

    /**
     * 目标完成次数
     */
    @TableField("target_count")
    private Integer targetCount;

    /**
     * 排序值
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 是否启用
     */
    @TableField("is_active")
    private Boolean isActive;

    /**
     * 任务开始日期
     */
    @TableField("start_date")
    private LocalDate startDate;

    /**
     * 任务结束日期
     */
    @TableField("end_date")
    private LocalDate endDate;

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
     * 判断任务是否为每日任务
     */
    public boolean isDailyTask() {
        return TaskTypeConstant.isDailyTask(taskType);
    }

    /**
     * 判断任务是否为周常任务
     */
    public boolean isWeeklyTask() {
        return TaskTypeConstant.isWeeklyTask(taskType);
    }

    /**
     * 判断任务是否为月度任务
     */
    public boolean isMonthlyTask() {
        return TaskTypeConstant.isMonthlyTask(taskType);
    }

    /**
     * 判断任务是否为成就任务
     */
    public boolean isAchievementTask() {
        return TaskTypeConstant.isAchievementTask(taskType);
    }

    /**
     * 判断任务是否启用
     */
    public boolean isActiveTask() {
        return Boolean.TRUE.equals(isActive);
    }

    /**
     * 判断任务是否在有效期内
     */
    public boolean isInValidPeriod() {
        LocalDate now = LocalDate.now();
        boolean afterStart = startDate == null || !now.isBefore(startDate);
        boolean beforeEnd = endDate == null || !now.isAfter(endDate);
        return afterStart && beforeEnd;
    }

    /**
     * 判断任务是否可用（启用且在有效期内）
     */
    public boolean isAvailable() {
        return isActiveTask() && isInValidPeriod();
    }

    /**
     * 判断是否为登录类任务
     */
    public boolean isLoginTask() {
        return TaskCategoryConstant.isLoginTask(taskCategory);
    }

    /**
     * 判断是否为内容类任务
     */
    public boolean isContentTask() {
        return TaskCategoryConstant.isContentTask(taskCategory);
    }

    /**
     * 判断是否为社交类任务
     */
    public boolean isSocialTask() {
        return TaskCategoryConstant.isSocialTask(taskCategory);
    }

    /**
     * 判断是否为消费类任务
     */
    public boolean isConsumeTask() {
        return TaskCategoryConstant.isConsumeTask(taskCategory);
    }

    /**
     * 判断是否为邀请类任务
     */
    public boolean isInviteTask() {
        return TaskCategoryConstant.isInviteTask(taskCategory);
    }

    /**
     * 设置任务启用状态
     */
    public void setActiveStatus(boolean active) {
        this.isActive = active;
    }

    /**
     * 设置任务有效期
     */
    public void setValidPeriod(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
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
     * 获取任务动作名称
     */
    public String getTaskActionName() {
        return TaskActionConstant.getActionName(taskAction);
    }

    /**
     * 验证任务模板数据是否有效
     */
    public boolean isValidTemplate() {
        return taskName != null && !taskName.trim().isEmpty() &&
               TaskTypeConstant.isValidType(taskType) &&
               TaskCategoryConstant.isValidCategory(taskCategory) &&
               TaskActionConstant.isValidAction(taskAction) &&
               targetCount != null && targetCount > 0;
    }

    /**
     * 判断是否匹配指定的任务动作
     */
    public boolean matchesAction(Integer action) {
        return taskAction != null && taskAction.equals(action);
    }

    /**
     * 创建任务模板的简单工厂方法
     */
    public static TaskTemplate createDailyTask(String name, String desc, Integer category, Integer action, Integer targetCount) {
        TaskTemplate template = new TaskTemplate();
        template.setTaskName(name);
        template.setTaskDesc(desc);
        template.setTaskType(TaskTypeConstant.DAILY);
        template.setTaskCategory(category);
        template.setTaskAction(action);
        template.setTargetCount(targetCount);
        template.setIsActive(true);
        template.setSortOrder(0);
        return template;
    }
}