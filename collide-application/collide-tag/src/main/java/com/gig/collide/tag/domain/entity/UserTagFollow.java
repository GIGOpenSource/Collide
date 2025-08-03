package com.gig.collide.tag.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 用户关注标签实体 - 对应 t_user_tag_follow 表
 * 
 * @author GIG Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user_tag_follow")
public class UserTagFollow implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关系ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 关注时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime followTime;

    // =================== 业务方法 ===================

    /**
     * 初始化默认值
     */
    public void initDefaults() {
        if (this.followTime == null) {
            this.followTime = LocalDateTime.now();
        }
    }

    /**
     * 获取关注天数
     */
    public long getFollowDays() {
        if (this.followTime == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(this.followTime.toLocalDate(), LocalDateTime.now().toLocalDate());
    }

    /**
     * 检查是否为今日关注
     */
    public boolean isFollowedToday() {
        if (this.followTime == null) {
            return false;
        }
        return this.followTime.toLocalDate().equals(LocalDateTime.now().toLocalDate());
    }

    /**
     * 检查是否为最近关注（7天内）
     */
    public boolean isRecentFollow() {
        if (this.followTime == null) {
            return false;
        }
        return this.followTime.isAfter(LocalDateTime.now().minusDays(7));
    }

    /**
     * 检查是否为长期关注（30天以上）
     */
    public boolean isLongTermFollow() {
        return getFollowDays() >= 30;
    }

    /**
     * 获取关注时长描述
     */
    public String getFollowDurationDesc() {
        long days = getFollowDays();
        if (days == 0) {
            return "今日关注";
        } else if (days < 7) {
            return days + "天前关注";
        } else if (days < 30) {
            return (days / 7) + "周前关注";
        } else if (days < 365) {
            return (days / 30) + "个月前关注";
        } else {
            return (days / 365) + "年前关注";
        }
    }

    /**
     * 创建用户标签关注关系
     */
    public static UserTagFollow create(Long userId, Long tagId) {
        UserTagFollow follow = new UserTagFollow();
        follow.setUserId(userId);
        follow.setTagId(tagId);
        follow.initDefaults();
        return follow;
    }

    /**
     * 验证用户标签关注关系的有效性
     */
    public boolean isValid() {
        return this.userId != null && this.userId > 0 
                && this.tagId != null && this.tagId > 0
                && this.followTime != null;
    }

    /**
     * 获取关注权重（根据关注时长计算）
     */
    public double getFollowWeight() {
        long days = getFollowDays();
        if (days == 0) {
            return 1.0; // 新关注权重为1
        } else if (days < 7) {
            return 1.2; // 最近关注权重稍高
        } else if (days < 30) {
            return 1.5; // 短期关注权重中等
        } else if (days < 90) {
            return 2.0; // 中期关注权重较高
        } else {
            return 3.0; // 长期关注权重最高
        }
    }
}