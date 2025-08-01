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
 * 任务奖励实体类 - 简洁版
 * 基于task-simple.sql的t_task_reward表结构
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
@TableName("t_task_reward")
public class TaskReward {

    /**
     * 奖励ID - 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务模板ID
     */
    @TableField("task_id")
    private Long taskId;

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
     * 奖励描述
     */
    @TableField("reward_desc")
    private String rewardDesc;

    /**
     * 奖励数量
     */
    @TableField("reward_amount")
    private Integer rewardAmount;

    /**
     * 奖励扩展数据（商品信息等）
     */
    @TableField(value = "reward_data", typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private Map<String, Object> rewardData;

    /**
     * 是否主要奖励
     */
    @TableField("is_main_reward")
    private Boolean isMainReward;

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
     * 判断是否为主要奖励
     */
    public boolean isMainRewardFlag() {
        return Boolean.TRUE.equals(isMainReward);
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
     * 设置主要奖励标识
     */
    public void setMainRewardFlag(boolean isMain) {
        this.isMainReward = isMain;
    }

    /**
     * 验证奖励配置是否有效
     */
    public boolean isValidReward() {
        return rewardAmount != null && rewardAmount > 0 && 
               rewardType != null && !rewardType.trim().isEmpty() &&
               rewardName != null && !rewardName.trim().isEmpty();
    }
}