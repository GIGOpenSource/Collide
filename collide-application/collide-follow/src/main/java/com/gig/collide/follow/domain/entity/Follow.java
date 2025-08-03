package com.gig.collide.follow.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 关注实体类 - 简洁版
 * 基于follow-simple.sql的t_follow表结构
 * 采用无连表设计，包含关注者和被关注者信息冗余字段
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@TableName("t_follow")
public class Follow {

    /**
     * 关注ID - 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关注者用户ID
     */
    @TableField("follower_id")
    private Long followerId;

    /**
     * 被关注者用户ID
     */
    @TableField("followee_id")
    private Long followeeId;

    // =================== 关注者信息（冗余字段，避免连表） ===================

    /**
     * 关注者昵称（冗余）
     */
    @TableField("follower_nickname")
    private String followerNickname;

    /**
     * 关注者头像（冗余）
     */
    @TableField("follower_avatar")
    private String followerAvatar;

    // =================== 被关注者信息（冗余字段，避免连表） ===================

    /**
     * 被关注者昵称（冗余）
     */
    @TableField("followee_nickname")
    private String followeeNickname;

    /**
     * 被关注者头像（冗余）
     */
    @TableField("followee_avatar")
    private String followeeAvatar;

    /**
     * 状态：active、cancelled
     */
    @TableField("status")
    private String status;

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
     * 判断是否为活跃关注
     */
    public boolean isActive() {
        return "active".equals(status);
    }

    /**
     * 判断是否已取消关注
     */
    public boolean isCancelled() {
        return "cancelled".equals(status);
    }

    /**
     * 激活关注关系
     */
    public void activate() {
        this.status = "active";
    }

    /**
     * 取消关注关系
     */
    public void cancel() {
        this.status = "cancelled";
    }

    /**
     * 检查是否为自己关注自己
     */
    public boolean isSelfFollow() {
        return followerId != null && followerId.equals(followeeId);
    }

    /**
     * 获取关注天数
     */
    public long getFollowDays() {
        if (createTime == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(createTime, LocalDateTime.now());
    }

    /**
     * 检查关注关系是否有效
     */
    public boolean isValidRelation() {
        return followerId != null && followeeId != null && !isSelfFollow();
    }

    /**
     * 更新关注者信息
     */
    public void updateFollowerInfo(String nickname, String avatar) {
        this.followerNickname = nickname;
        this.followerAvatar = avatar;
    }

    /**
     * 更新被关注者信息
     */
    public void updateFolloweeInfo(String nickname, String avatar) {
        this.followeeNickname = nickname;
        this.followeeAvatar = avatar;
    }
}