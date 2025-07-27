package com.gig.collide.auth.vo;

import cn.dev33.satoken.stp.StpUtil;
import com.gig.collide.api.user.response.data.UserInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录响应VO
 * 基于Code项目设计哲学和简化认证系统，提供丰富的用户信息
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-16
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户信息
     */
    private Map<String, Object> user;

    /**
     * 访问令牌
     */
    private String token;

    /**
     * 令牌过期时间（秒）
     */
    private Long tokenExpiration;

    /**
     * 是否新用户（用于登录或注册接口）
     */
    private Boolean isNewUser;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 基于UserInfo构造LoginVO
     */
    public LoginVO(UserInfo userInfo) {
        this.user = buildUserInfo(userInfo);
        this.token = StpUtil.getTokenValue();
        this.tokenExpiration = StpUtil.getTokenSessionTimeout();
        this.isNewUser = false;
        this.message = "登录成功";
    }

    /**
     * 基于UserInfo和新用户标识构造LoginVO
     */
    public LoginVO(UserInfo userInfo, boolean isNewUser) {
        this.user = buildUserInfo(userInfo);
        this.token = StpUtil.getTokenValue();
        this.tokenExpiration = StpUtil.getTokenSessionTimeout();
        this.isNewUser = isNewUser;
        this.message = isNewUser ? "注册并登录成功" : "登录成功";
    }

    /**
     * 构建用户信息Map
     */
    private Map<String, Object> buildUserInfo(UserInfo userInfo) {
        Map<String, Object> user = new HashMap<>();
        user.put("id", userInfo.getUserId());
        user.put("username", userInfo.getUserName());
        user.put("nickname", userInfo.getNickName());
        user.put("avatar", userInfo.getProfilePhotoUrl());
        user.put("role", userInfo.getRole());
        user.put("status", userInfo.getStatus());
        user.put("createTime", userInfo.getCreateTime());
        // TODO: 当用户服务支持邀请码后，添加邀请相关字段
        // user.put("inviteCode", userInfo.getInviteCode());
        // user.put("invitedCount", userInfo.getInvitedCount());
        return user;
    }
} 