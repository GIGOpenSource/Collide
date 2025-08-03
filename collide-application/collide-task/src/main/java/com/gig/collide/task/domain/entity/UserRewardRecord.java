package com.gig.collide.task.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.gig.collide.api.task.constant.RewardSourceConstant;
import com.gig.collide.api.task.constant.RewardStatusConstant;
import com.gig.collide.api.task.constant.RewardTypeConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 用户奖励记录实体类 - 优化版
 * 基于优化后的task-simple.sql的t_user_reward_record表结构
 * 使用数字常量管理奖励类型、来源和状态，提升性能和类型安全
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
     * 奖励来源：1-task, 2-event, 3-system, 4-admin
     */
    @TableField("reward_source")
    private Integer rewardSource;

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
     * 状态：1-pending, 2-success, 3-failed, 4-expired
     */
    @TableField("status")
    private Integer status;

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
     * 判断是否为任务奖励
     */
    public boolean isTaskReward() {
        return RewardSourceConstant.isTaskReward(rewardSource);
    }

    /**
     * 判断是否为事件奖励
     */
    public boolean isEventReward() {
        return RewardSourceConstant.isEventReward(rewardSource);
    }

    /**
     * 判断是否为系统奖励
     */
    public boolean isSystemReward() {
        return RewardSourceConstant.isSystemReward(rewardSource);
    }

    /**
     * 判断是否为管理员奖励
     */
    public boolean isAdminReward() {
        return RewardSourceConstant.isAdminReward(rewardSource);
    }

    /**
     * 判断奖励是否为待发放状态
     */
    public boolean isPending() {
        return RewardStatusConstant.isPending(status);
    }

    /**
     * 判断奖励是否已成功发放
     */
    public boolean isSuccess() {
        return RewardStatusConstant.isSuccess(status);
    }

    /**
     * 判断奖励发放是否失败
     */
    public boolean isFailed() {
        return RewardStatusConstant.isFailed(status);
    }

    /**
     * 判断奖励是否已过期
     */
    public boolean isStatusExpired() {
        return RewardStatusConstant.isExpired(status);
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
        this.status = RewardStatusConstant.SUCCESS;
        this.grantTime = LocalDateTime.now();
    }

    /**
     * 标记奖励为发放失败
     */
    public void markAsFailed() {
        this.status = RewardStatusConstant.FAILED;
        this.grantTime = LocalDateTime.now();
    }

    /**
     * 标记奖励为已过期
     */
    public void markAsExpired() {
        this.status = RewardStatusConstant.EXPIRED;
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
            this.status = RewardStatusConstant.PENDING;
            this.grantTime = null;
        }
    }

    // =================== 新增业务方法 ===================

    /**
     * 获取奖励类型名称
     */
    public String getRewardTypeName() {
        return RewardTypeConstant.getTypeName(rewardType);
    }

    /**
     * 获取奖励来源名称
     */
    public String getRewardSourceName() {
        return RewardSourceConstant.getSourceName(rewardSource);
    }

    /**
     * 获取奖励状态名称
     */
    public String getRewardStatusName() {
        return RewardStatusConstant.getStatusName(status);
    }

    /**
     * 判断是否需要立即同步到钱包
     */
    public boolean requiresImmediateWalletSync() {
        return RewardTypeConstant.requiresImmediateWalletSync(rewardType);
    }

    /**
     * 创建任务奖励记录
     */
    public static UserRewardRecord createTaskReward(Long userId, Long taskRecordId, 
                                                    Integer rewardType, String rewardName, 
                                                    Integer rewardAmount, Map<String, Object> rewardData) {
        UserRewardRecord record = new UserRewardRecord();
        record.setUserId(userId);
        record.setTaskRecordId(taskRecordId);
        record.setRewardSource(RewardSourceConstant.TASK);
        record.setRewardType(rewardType);
        record.setRewardName(rewardName);
        record.setRewardAmount(rewardAmount);
        record.setRewardData(rewardData);
        record.setStatus(RewardStatusConstant.PENDING);
        return record;
    }

    /**
     * 创建金币奖励记录
     */
    public static UserRewardRecord createCoinReward(Long userId, Long taskRecordId, 
                                                    Integer coinAmount, String description) {
        return createTaskReward(userId, taskRecordId, 
                               RewardTypeConstant.COIN, description, 
                               coinAmount, null);
    }

    /**
     * 验证奖励记录数据是否有效
     */
    public boolean isValidRecord() {
        return userId != null && 
               RewardSourceConstant.isValidSource(rewardSource) &&
               RewardTypeConstant.isValidType(rewardType) &&
               rewardName != null && !rewardName.trim().isEmpty() &&
               rewardAmount != null && rewardAmount > 0 &&
               RewardStatusConstant.isValidStatus(status);
    }
}