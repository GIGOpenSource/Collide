package com.gig.collide.api.user.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * 用户登录请求
 * @author GIG
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequest extends BaseRequest {

    @NotBlank(message = "用户名不能为空")
    private String userName;

    @NotBlank(message = "密码不能为空") 
    private String password;

    /**
     * 邀请码（仅在自动注册时使用）
     */
    private String inviteCode;
} 