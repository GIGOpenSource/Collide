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
 * 任务模板实体类 - 简洁版
 * 基于task-simple.sql的t_task_template表结构
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
     * 任务类型：daily、weekly、achievement
     */
    @TableField("task_type")
    private String taskType;

    /**
     * 任务分类：login、content、social、consume
     */
    @TableField("task_category")
    private String taskCategory;

    /**
     * 任务动作：login、publish_content、like、comment、share、purchase
     */
    @TableField("task_action")
    private String taskAction;

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
        return "daily".equals(taskType);
    }

    /**
     * 判断任务是否为周常任务
     */
    public boolean isWeeklyTask() {
        return "weekly".equals(taskType);
    }

    /**
     * 判断任务是否为成就任务
     */
    public boolean isAchievementTask() {
        return "achievement".equals(taskType);
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
        return "login".equals(taskCategory);
    }

    /**
     * 判断是否为内容类任务
     */
    public boolean isContentTask() {
        return "content".equals(taskCategory);
    }

    /**
     * 判断是否为社交类任务
     */
    public boolean isSocialTask() {
        return "social".equals(taskCategory);
    }

    /**
     * 判断是否为消费类任务
     */
    public boolean isConsumeTask() {
        return "consume".equals(taskCategory);
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
}