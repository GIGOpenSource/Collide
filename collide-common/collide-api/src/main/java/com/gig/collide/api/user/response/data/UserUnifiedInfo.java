package com.gig.collide.api.user.response.data;

import com.gig.collide.api.user.constant.UserRole;
import com.gig.collide.api.user.constant.UserStateEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户统一信息传输对象
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class UserUnifiedInfo extends BasicUserInfo {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号（脱敏）
     */
    private String phone;

    /**
     * 用户状态
     */
    private UserStateEnum status;

    /**
     * 用户角色
     */
    private UserRole role;

    /**
     * 博主认证状态
     */
    private String bloggerStatus;

    /**
     * 邀请码
     */
    private String inviteCode;

    /**
     * 邀请人ID
     */
    private Long inviterId;

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
     * VIP等级
     */
    private Integer vipLevel;

    /**
     * 积分
     */
    private Integer points;

    /**
     * 注册时间
     */
    private LocalDateTime createTime;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 检查用户是否可以发布内容
     */
    public boolean canPublishContent() {
        return status != null && status == UserStateEnum.ACTIVE
                && (role == UserRole.BLOGGER || role == UserRole.ADMIN);
    }

    /**
     * 检查用户是否为博主
     */
    public boolean isBlogger() {
        return role == UserRole.BLOGGER && "approved".equals(bloggerStatus);
    }
} 