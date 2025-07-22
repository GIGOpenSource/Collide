package com.gig.collide.auth.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 注册参数
 * 参考 nft-turbo-auth 设计，但使用用户名密码方式
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Setter
@Getter
public class RegisterParam {

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
    @Size(min = 6, max = 20, message = "密码长度必须在6-20字符之间")
    private String password;

    /**
     * 邮箱（可选）
     */
    private String email;

    /**
     * 手机号（可选）
     */
    private String phone;

    /**
     * 邀请码（可选）
     */
    private String inviteCode;
} 