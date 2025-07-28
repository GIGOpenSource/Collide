package com.gig.collide.api.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;



/**
 * 用户创建请求 - 简洁版
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
public class UserCreateRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    @NotBlank(message = "昵称不能为空")
    @Size(max = 100, message = "昵称长度不能超过100字符")
    private String nickname;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20字符之间")
    private String password;

    private String avatar;

    private String bio;

    /**
     * 用户角色（默认为user）
     */
    private String role;

    /**
     * 邀请码（可选）
     */
    private String inviteCode;
} 