package com.gig.collide.api.follow.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 关注统一响应对象 - 简洁版
 * 基于follow-simple.sql的字段结构，包含所有冗余信息
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
public class FollowResponse {

    /**
     * 关注ID
     */
    private Long id;

    /**
     * 关注者用户ID
     */
    private Long followerId;

    /**
     * 被关注者用户ID
     */
    private Long followeeId;

    // =================== 关注者信息（冗余字段） ===================

    /**
     * 关注者昵称（冗余）
     */
    private String followerNickname;

    /**
     * 关注者头像（冗余）
     */
    private String followerAvatar;

    // =================== 被关注者信息（冗余字段） ===================

    /**
     * 被关注者昵称（冗余）
     */
    private String followeeNickname;

    /**
     * 被关注者头像（冗余）
     */
    private String followeeAvatar;

    // =================== 状态和时间信息 ===================

    /**
     * 状态：active、cancelled
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    // =================== 计算属性 ===================

    /**
     * 是否为活跃关注
     */
    public boolean isActive() {
        return "active".equals(status);
    }

    /**
     * 是否已取消关注
     */
    public boolean isCancelled() {
        return "cancelled".equals(status);
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
}