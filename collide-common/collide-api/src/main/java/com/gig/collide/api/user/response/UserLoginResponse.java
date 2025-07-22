package com.gig.collide.api.user.response;

import com.gig.collide.api.user.response.data.UserInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户登录响应
 * @author GIG
 */
@Getter
@Setter
public class UserLoginResponse extends BaseResponse {

    /**
     * 用户信息
     */
    private UserInfo user;

    /**
     * 是否为新注册用户
     */
    private Boolean isNewUser = false;

    /**
     * Token（如果需要的话）
     */
    private String token;
} 