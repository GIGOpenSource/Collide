package com.gig.collide.api.user.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户响应 - 简洁版
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
public class UserResponse {

    private Long id;

    private String username;

    private String nickname;

    private String avatar;

    private String email;

    private String phone;

    private String role;

    private String status;

    private String bio;

    private LocalDate birthday;

    private String gender;

    private String location;

    /**
     * 统计数据
     */
    private Long followerCount;

    private Long followingCount;

    private Long contentCount;

    private Long likeCount;

    /**
     * VIP信息
     */
    private LocalDateTime vipExpireTime;

    /**
     * 登录信息
     */
    private LocalDateTime lastLoginTime;

    private Long loginCount;

    /**
     * 邀请信息
     */
    private String inviteCode;

    private Long inviterId;

    private Long invitedCount;

    /**
     * 时间信息
     */
    private LocalDateTime createTime;

    private LocalDateTime updateTime;
} 