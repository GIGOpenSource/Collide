package com.gig.collide.auth.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 登录或注册参数
 * 基于Code项目设计哲学：用户不存在时自动注册
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-16
 */
@Setter
@Getter
public class LoginOrRegisterParam {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20字符之间")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度必须在6-50字符之间")
    private String password;

    /**
     * 邀请码（可选）
     * 当用户不存在需要自动注册时使用
     */
    private String inviteCode;
} 