package com.gig.collide.task.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 用户奖励记录实体类 - 简洁版
 * 基于task-simple.sql的t_user_reward_record表结构
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
@TableName("t_user_reward_record")
public class UserRewardRecord {

    /**
     * 奖励记录ID - 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 任务记录ID
     */
    @TableField("task_record_id")
    private Long taskRecordId;

    /**
     * 奖励来源：task、event、system
     */
    @TableField("reward_source")
    private String rewardSource;

    /**
     * 奖励类型：coin、item、vip、experience
     */
    @TableField("reward_type")
    private String rewardType;

    /**
     * 奖励名称
     */
    @TableField("reward_name")
    private String rewardName;

    /**
     * 奖励数量
     */
    @TableField("reward_amount")
    private Integer rewardAmount;

    /**
     * 奖励扩展数据
     */
    @TableField(value = "reward_data", typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private Map<String, Object> rewardData;

    /**
     * 状态：pending、success、failed
     */
    @TableField("status")
    private String status;

    /**
     * 发放时间
     */
    @TableField("grant_time")
    private LocalDateTime grantTime;

    /**
     * 过期时间
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;

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
     * 判断是否为金币奖励
     */
    public boolean isCoinReward() {
        return "coin".equals(rewardType);
    }

    /**
     * 判断是否为道具奖励
     */
    public boolean isItemReward() {
        return "item".equals(rewardType);
    }

    /**
     * 判断是否为VIP奖励
     */
    public boolean isVipReward() {
        return "vip".equals(rewardType);
    }

    /**
     * 判断是否为经验奖励
     */
    public boolean isExperienceReward() {
        return "experience".equals(rewardType);
    }

    /**
     * 判断是否为任务奖励
     */
    public boolean isTaskReward() {
        return "task".equals(rewardSource);
    }

    /**
     * 判断是否为事件奖励
     */
    public boolean isEventReward() {
        return "event".equals(rewardSource);
    }

    /**
     * 判断是否为系统奖励
     */
    public boolean isSystemReward() {
        return "system".equals(rewardSource);
    }

    /**
     * 判断奖励是否为待发放状态
     */
    public boolean isPending() {
        return "pending".equals(status);
    }

    /**
     * 判断奖励是否已成功发放
     */
    public boolean isSuccess() {
        return "success".equals(status);
    }

    /**
     * 判断奖励发放是否失败
     */
    public boolean isFailed() {
        return "failed".equals(status);
    }

    /**
     * 判断奖励是否已过期
     */
    public boolean isExpired() {
        return expireTime != null && expireTime.isBefore(LocalDateTime.now());
    }

    /**
     * 判断奖励是否可以发放
     */
    public boolean canBeGranted() {
        return isPending() && !isExpired();
    }

    /**
     * 标记奖励为发放成功
     */
    public void markAsSuccess() {
        this.status = "success";
        this.grantTime = LocalDateTime.now();
    }

    /**
     * 标记奖励为发放失败
     */
    public void markAsFailed() {
        this.status = "failed";
        this.grantTime = LocalDateTime.now();
    }

    /**
     * 获取奖励数据中的指定字段
     */
    public Object getRewardDataValue(String key) {
        return rewardData != null ? rewardData.get(key) : null;
    }

    /**
     * 设置奖励数据中的指定字段
     */
    public void setRewardDataValue(String key, Object value) {
        if (rewardData == null) {
            rewardData = new java.util.HashMap<>();
        }
        rewardData.put(key, value);
    }

    /**
     * 获取道具ID（道具奖励专用）
     */
    public Long getItemId() {
        if (isItemReward() && rewardData != null) {
            Object itemId = rewardData.get("item_id");
            return itemId instanceof Number ? ((Number) itemId).longValue() : null;
        }
        return null;
    }

    /**
     * 获取VIP时长天数（VIP奖励专用）
     */
    public Integer getVipDurationDays() {
        if (isVipReward() && rewardData != null) {
            Object duration = rewardData.get("duration_days");
            return duration instanceof Number ? ((Number) duration).intValue() : null;
        }
        return null;
    }

    /**
     * 获取VIP类型（VIP奖励专用）
     */
    public String getVipType() {
        if (isVipReward() && rewardData != null) {
            Object vipType = rewardData.get("vip_type");
            return vipType instanceof String ? (String) vipType : null;
        }
        return null;
    }

    /**
     * 设置过期时间（相对于当前时间的天数）
     */
    public void setExpireAfterDays(int days) {
        if (days > 0) {
            this.expireTime = LocalDateTime.now().plusDays(days);
        }
    }

    /**
     * 重试发放（将失败状态重置为待发放）
     */
    public void retryGrant() {
        if (isFailed()) {
            this.status = "pending";
            this.grantTime = null;
        }
    }
}