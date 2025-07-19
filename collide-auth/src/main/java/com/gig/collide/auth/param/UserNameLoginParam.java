package com.gig.collide.auth.param;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserNameLoginParam extends UserNameRegisterParam {

    /**
     * 记住我
     */
    private Boolean rememberMe;
}
