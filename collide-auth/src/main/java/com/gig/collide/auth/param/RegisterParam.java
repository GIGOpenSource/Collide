package com.gig.collide.auth.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 注册参数 - 简洁版
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
public class RegisterParam {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;

    /**
     * 邀请码（可选）
     */
    @Size(max = 20, message = "邀请码长度不能超过20个字符")
    private String inviteCode;
} 