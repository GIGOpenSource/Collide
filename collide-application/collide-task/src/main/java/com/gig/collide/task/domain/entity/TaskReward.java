package com.gig.collide.task.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.gig.collide.api.task.constant.RewardTypeConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 任务奖励实体类 - 优化版
 * 基于优化后的task-simple.sql的t_task_reward表结构
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
     * 奖励类型：1-coin, 2-item, 3-vip, 4-experience, 5-badge
     */
    @TableField("reward_type")
    private Integer rewardType;

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
        return RewardTypeConstant.isCoinReward(rewardType);
    }

    /**
     * 判断是否为道具奖励
     */
    public boolean isItemReward() {
        return RewardTypeConstant.isItemReward(rewardType);
    }

    /**
     * 判断是否为VIP奖励
     */
    public boolean isVipReward() {
        return RewardTypeConstant.isVipReward(rewardType);
    }

    /**
     * 判断是否为经验奖励
     */
    public boolean isExperienceReward() {
        return RewardTypeConstant.isExperienceReward(rewardType);
    }

    /**
     * 判断是否为徽章奖励
     */
    public boolean isBadgeReward() {
        return RewardTypeConstant.isBadgeReward(rewardType);
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
               RewardTypeConstant.isValidType(rewardType) &&
               rewardName != null && !rewardName.trim().isEmpty();
    }

    // =================== 新增业务方法 ===================

    /**
     * 获取奖励类型名称
     */
    public String getRewardTypeName() {
        return RewardTypeConstant.getTypeName(rewardType);
    }

    /**
     * 判断是否需要立即同步到钱包
     * 金币奖励需要立即发放到用户钱包
     */
    public boolean requiresImmediateWalletSync() {
        return RewardTypeConstant.requiresImmediateWalletSync(rewardType);
    }

    /**
     * 创建金币奖励的工厂方法
     */
    public static TaskReward createCoinReward(Long taskId, String name, String desc, Integer amount, boolean isMain) {
        TaskReward reward = new TaskReward();
        reward.setTaskId(taskId);
        reward.setRewardType(RewardTypeConstant.COIN);
        reward.setRewardName(name);
        reward.setRewardDesc(desc);
        reward.setRewardAmount(amount);
        reward.setIsMainReward(isMain);
        return reward;
    }

    /**
     * 创建徽章奖励的工厂方法
     */
    public static TaskReward createBadgeReward(Long taskId, String name, String desc, boolean isMain) {
        TaskReward reward = new TaskReward();
        reward.setTaskId(taskId);
        reward.setRewardType(RewardTypeConstant.BADGE);
        reward.setRewardName(name);
        reward.setRewardDesc(desc);
        reward.setRewardAmount(1);
        reward.setIsMainReward(isMain);
        return reward;
    }
}