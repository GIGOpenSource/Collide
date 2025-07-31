package com.gig.collide.auth.service;

import com.gig.collide.auth.param.LoginParam;
import com.gig.collide.auth.param.RegisterParam;
import com.gig.collide.auth.param.LoginOrRegisterParam;
import com.gig.collide.web.vo.Result;

/**
 * 认证服务接口
 * 
 * @author GIG Team
 * @version 2.0.0
 */
public interface AuthService {

    /**
     * 用户注册
     */
    Result<Object> register(RegisterParam registerParam);

    /**
     * 用户登录
     */
    Result<Object> login(LoginParam loginParam);

    /**
     * 登录或注册
     */
    Result<Object> loginOrRegister(LoginOrRegisterParam loginParam);

    /**
     * 用户登出
     */
    Result<String> logout();

    /**
     * 验证邀请码
     */
    Result<Object> validateInviteCode(String inviteCode);

    /**
     * 获取我的邀请信息
     */
    Result<Object> getMyInviteInfo();

    /**
     * 获取当前用户信息
     */
    Result<Object> getCurrentUser();

    /**
     * 验证Token
     */
    Result<Object> verifyToken();
}