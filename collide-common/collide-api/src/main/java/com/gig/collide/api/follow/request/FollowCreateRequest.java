package com.gig.collide.api.follow.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 关注创建请求 - 简洁版
 * 基于follow-simple.sql的无连表设计，包含关注者和被关注者信息冗余
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
public class FollowCreateRequest implements Serializable {

    /**
     * 关注者用户ID
     */
    @NotNull(message = "关注者ID不能为空")
    private Long followerId;

    /**
     * 被关注者用户ID
     */
    @NotNull(message = "被关注者ID不能为空")
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

    /**
     * 状态（默认为active）
     */
    private String status = "active";
} 