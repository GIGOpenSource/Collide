package com.gig.collide.api.auth.service;

import com.gig.collide.api.auth.request.LoginRequest;
import com.gig.collide.api.auth.response.LoginResponse;
import com.gig.collide.api.common.response.BaseListResponse;
import com.gig.collide.base.response.BaseResponse;

/**
 * 认证门面服务接口
 * 提供认证相关的RPC服务
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface AuthFacadeService {

    /**
     * 用户登录（支持多种登录方式）
     *
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest loginRequest);

    /**
     * 刷新访问令牌
     *
     * @param refreshToken 刷新令牌
     * @param clientId 客户端ID
     * @return 新的访问令牌响应
     */
    LoginResponse refreshToken(String refreshToken, String clientId);

    /**
     * 用户登出
     *
     * @param accessToken 访问令牌
     * @param userId 用户ID
     * @return 操作结果
     */
    BaseResponse logout(String accessToken, Long userId);

    /**
     * 验证访问令牌
     *
     * @param accessToken 访问令牌
     * @return 验证结果和用户信息
     */
    BaseResponse validateToken(String accessToken);

    /**
     * 发送手机验证码
     *
     * @param phoneNumber 手机号
     * @param codeType 验证码类型（登录、注册、找回密码等）
     * @return 操作结果
     */
    BaseResponse sendSmsCode(String phoneNumber, String codeType);

    /**
     * 发送邮箱验证码
     *
     * @param email 邮箱地址
     * @param codeType 验证码类型
     * @return 操作结果
     */
    BaseResponse sendEmailCode(String email, String codeType);

    /**
     * 验证验证码
     *
     * @param codeId 验证码ID
     * @param code 验证码
     * @return 验证结果
     */
    BaseResponse verifyCode(String codeId, String code);

    /**
     * 获取OAuth2.0授权URL
     *
     * @param clientId 客户端ID
     * @param redirectUri 回调地址
     * @param scope 权限范围
     * @param state 状态参数
     * @return 授权URL
     */
    BaseResponse getAuthorizeUrl(String clientId, String redirectUri, String scope, String state);

    /**
     * OAuth2.0授权码换取访问令牌
     *
     * @param code 授权码
     * @param clientId 客户端ID
     * @param clientSecret 客户端密钥
     * @param redirectUri 回调地址
     * @return 访问令牌响应
     */
    LoginResponse exchangeToken(String code, String clientId, String clientSecret, String redirectUri);
} 