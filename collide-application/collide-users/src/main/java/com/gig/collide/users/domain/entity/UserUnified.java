package com.gig.collide.users.domain.entity;

import com.gig.collide.datasource.domain.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户统一信息实体（去连表设计）
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@TableName("t_user_unified")
public class UserUnified extends BaseEntity {

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 密码哈希
     */
    private String passwordHash;

    /**
     * 密码盐值
     */
    private String salt;

    /**
     * 用户角色
     */
    private String role;

    /**
     * 用户状态
     */
    private String status;

    /**
     * 个人简介
     */
    private String bio;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 性别
     */
    private String gender;

    /**
     * 所在地
     */
    private String location;

    /**
     * 粉丝数
     */
    @TableField("follower_count")
    private Long followerCount;

    /**
     * 关注数
     */
    @TableField("following_count")
    private Long followingCount;

    /**
     * 内容数
     */
    @TableField("content_count")
    private Long contentCount;

    /**
     * 获得点赞数
     */
    @TableField("like_count")
    private Long likeCount;

    /**
     * VIP过期时间
     */
    @TableField("vip_expire_time")
    private LocalDateTime vipExpireTime;

    /**
     * 博主认证状态
     */
    @TableField("blogger_status")
    private String bloggerStatus;

    /**
     * 博主申请时间
     */
    @TableField("blogger_apply_time")
    private LocalDateTime bloggerApplyTime;

    /**
     * 博主认证时间
     */
    @TableField("blogger_approve_time")
    private LocalDateTime bloggerApproveTime;

    /**
     * 最后登录时间
     */
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * 登录次数
     */
    @TableField("login_count")
    private Long loginCount;

    /**
     * 邀请码
     */
    @TableField("invite_code")
    private String inviteCode;

    /**
     * 邀请人ID
     */
    @TableField("inviter_id")
    private Long inviterId;

    /**
     * 邀请人数
     */
    @TableField("invited_count")
    private Long invitedCount;
} 