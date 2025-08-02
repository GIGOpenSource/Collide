package com.gig.collide.api.user.request.users.main;

import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户登录请求
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 登录标识（用户名、邮箱或手机号）
     */
    @NotBlank(message = "登录标识不能为空")
    private String loginId;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 登录类型：username、email、phone
     */
    private String loginType = "username";


}