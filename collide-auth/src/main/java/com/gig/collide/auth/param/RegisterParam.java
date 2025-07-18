package com.gig.collide.auth.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class RegisterParam {

    /**
     * 用户名
     */
    private String telephone;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String captcha;

    /**
     * 邀请码
     */
    private String inviteCode;

}
