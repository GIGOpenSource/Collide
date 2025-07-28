package com.gig.collide.auth.param;

import jakarta.validation.constraints.NotBlank;
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
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 邀请码（可选）
     */
    private String inviteCode;
} 