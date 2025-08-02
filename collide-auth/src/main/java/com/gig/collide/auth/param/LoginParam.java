package com.gig.collide.auth.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 登录参数 - 简洁版
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
public class LoginParam {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 1, max = 50, message = "用户名长度不能超过50个字符")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 1, max = 100, message = "密码长度不能超过100个字符")
    private String password;
} 