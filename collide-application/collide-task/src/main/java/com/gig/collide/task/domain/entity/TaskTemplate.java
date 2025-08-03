package com.gig.collide.task.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gig.collide.api.task.constant.TaskStatusConstant;
import com.gig.collide.api.task.constant.TaskTypeConstant;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务模板实体 - 对应 t_task_template 表
 * 负责任务配置和奖励规则管理
 * 
 * @author GIG Team
 * @version 1.0.0
 */
@Data
@TableName("t_task_template")
public class TaskTemplate {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务类型：DAILY_CHECKIN-每日签到
     */
    private String taskType;

    /**
     * 任务描述
     */
    private String taskDesc;

    /**
     * 基础奖励金币数量
     */
    private Integer rewardCoins;

    /**
     * 奖励规则说明
     */
    private String bonusRule;

    /**
     * 是否可重复：1-是，0-否
     */
    private Integer isRepeatable;

    /**
     * 排序权重
     */
    private Integer sortOrder;

    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 初始化默认值
     */
    public void initDefaults() {
        if (this.taskType == null) {
            this.taskType = TaskTypeConstant.DAILY_CHECKIN;
        }
        if (this.rewardCoins == null) {
            this.rewardCoins = 10; // 默认10金币
        }
        if (this.isRepeatable == null) {
            this.isRepeatable = 1; // 默认可重复
        }
        if (this.sortOrder == null) {
            this.sortOrder = 0; // 默认排序权重
        }
        if (this.status == null) {
            this.status = TaskStatusConstant.ENABLED; // 默认启用
        }
        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }
        if (this.updateTime == null) {
            this.updateTime = LocalDateTime.now();
        }
    }

    /**
     * 检查任务是否启用
     */
    public boolean isEnabled() {
        return TaskStatusConstant.ENABLED.equals(this.status);
    }

    /**
     * 检查任务是否禁用
     */
    public boolean isDisabled() {
        return TaskStatusConstant.DISABLED.equals(this.status);
    }

    /**
     * 检查任务是否可重复
     */
    public boolean isRepeatableTask() {
        return Integer.valueOf(1).equals(this.isRepeatable);
    }

    /**
     * 检查是否为每日签到任务
     */
    public boolean isDailyCheckinTask() {
        return TaskTypeConstant.DAILY_CHECKIN.equals(this.taskType);
    }

    /**
     * 获取状态描述
     */
    public String getStatusDesc() {
        return TaskStatusConstant.getStatusDesc(this.status);
    }

    /**
     * 获取任务类型描述
     */
    public String getTaskTypeDesc() {
        return TaskTypeConstant.getTaskTypeDesc(this.taskType);
    }

    /**
     * 更新修改时间
     */
    public void updateModifyTime() {
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 启用任务
     */
    public void enable() {
        this.status = TaskStatusConstant.ENABLED;
        updateModifyTime();
    }

    /**
     * 禁用任务
     */
    public void disable() {
        this.status = TaskStatusConstant.DISABLED;
        updateModifyTime();
    }
}