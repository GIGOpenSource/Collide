package com.gig.collide.task.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户签到记录实体 - 对应 t_user_checkin_record 表
 * 负责用户签到行为和奖励记录管理
 * 
 * @author GIG Team
 * @version 1.0.0
 */
@Data
@TableName("t_user_checkin_record")
public class UserCheckinRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 任务模板ID
     */
    private Long taskTemplateId;

    /**
     * 签到日期
     */
    private LocalDate checkinDate;

    /**
     * 获得金币数量
     */
    private Integer rewardCoins;

    /**
     * 连续签到天数
     */
    private Integer continuousDays;

    /**
     * 是否获得连续奖励：1-是，0-否
     */
    private Integer isBonus;

    /**
     * 签到IP地址
     */
    private String checkinIp;

    /**
     * 签到时间
     */
    private LocalDateTime checkinTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 初始化默认值
     */
    public void initDefaults() {
        if (this.checkinDate == null) {
            this.checkinDate = LocalDate.now();
        }
        if (this.rewardCoins == null) {
            this.rewardCoins = 10; // 默认10金币
        }
        if (this.continuousDays == null) {
            this.continuousDays = 1; // 默认连续1天
        }
        if (this.isBonus == null) {
            this.isBonus = 0; // 默认无连续奖励
        }
        if (this.checkinTime == null) {
            this.checkinTime = LocalDateTime.now();
        }
        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }
    }

    /**
     * 检查是否获得连续奖励
     */
    public boolean hasBonus() {
        return Integer.valueOf(1).equals(this.isBonus);
    }

    /**
     * 检查是否今日签到
     */
    public boolean isTodayCheckin() {
        return LocalDate.now().equals(this.checkinDate);
    }

    /**
     * 检查是否连续签到达到奖励条件
     * 连续7天可获得双倍奖励
     */
    public boolean isBonusEligible() {
        return this.continuousDays != null && this.continuousDays >= 7 && this.continuousDays % 7 == 0;
    }

    /**
     * 设置连续奖励
     */
    public void setBonus() {
        this.isBonus = 1;
    }

    /**
     * 取消连续奖励
     */
    public void clearBonus() {
        this.isBonus = 0;
    }

    /**
     * 计算奖励金币（含连续奖励）
     */
    public Integer calculateRewardCoins(Integer baseReward) {
        if (baseReward == null) {
            baseReward = 10;
        }
        
        // 如果达到连续奖励条件，翻倍奖励
        if (isBonusEligible()) {
            setBonus();
            return baseReward * 2;
        } else {
            clearBonus();
            return baseReward;
        }
    }

    /**
     * 增加连续签到天数
     */
    public void incrementContinuousDays() {
        if (this.continuousDays == null) {
            this.continuousDays = 1;
        } else {
            this.continuousDays++;
        }
    }

    /**
     * 重置连续签到天数
     */
    public void resetContinuousDays() {
        this.continuousDays = 1;
    }

    /**
     * 获取奖励描述
     */
    public String getRewardDesc() {
        if (hasBonus()) {
            return String.format("连续签到%d天！获得%d金币（含连续奖励）", continuousDays, rewardCoins);
        } else {
            return String.format("签到成功！获得%d金币", rewardCoins);
        }
    }

    /**
     * 构建签到记录
     */
    public static UserCheckinRecord buildCheckinRecord(Long userId, Long taskTemplateId, 
                                                      Integer baseReward, Integer continuousDays, 
                                                      String checkinIp) {
        UserCheckinRecord record = new UserCheckinRecord();
        record.setUserId(userId);
        record.setTaskTemplateId(taskTemplateId);
        record.setContinuousDays(continuousDays);
        record.setCheckinIp(checkinIp);
        
        // 计算奖励金币
        Integer rewardCoins = record.calculateRewardCoins(baseReward);
        record.setRewardCoins(rewardCoins);
        
        // 初始化默认值
        record.initDefaults();
        
        return record;
    }
}