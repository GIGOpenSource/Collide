package com.gig.collide.auth.param;

import lombok.Getter;
import lombok.Setter;

/**
 * 登录参数
 * 参考 nft-turbo 设计，继承注册参数并增加记住我功能
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
public class LoginParam extends RegisterParam {

    /**
     * 记住我
     */
    private Boolean rememberMe = false;
} 