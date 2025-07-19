package com.gig.collide.auth.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserNameRegisterParam {

    /**
     * 用户名
     */
    @NotBlank
    private String userName;

    /**
     * 密码
     */
    @NotBlank
    private String password;

    /**
     * 邀请码
     */
    private String inviteCode;

}
