package com.gig.collide.api.auth.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gig.collide.base.response.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 登录响应
 * 优化字段显示，隐藏不必要的字段
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LoginResponse extends BaseResponse {

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 令牌类型（通常为Bearer）
     * 隐藏此字段，因为固定为Bearer
     */
    @JsonIgnore
    private String tokenType = "Bearer";

    /**
     * 访问令牌过期时间（秒）
     */
    private Long expiresIn;

    /**
     * 作用域（隐藏，暂不使用）
     */
    @JsonIgnore
    private String scope;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 用户角色
     */
    private String role;

    /**
     * 是否为新用户（首次登录）
     */
    private Boolean isNewUser;

    /**
     * 登录时间
     */
    private LocalDateTime loginTime;

    /**
     * 隐藏BaseResponse中的字段
     */
    @Override
    @JsonIgnore
    public Boolean getSuccess() {
        return super.getSuccess();
    }

    @Override
    @JsonIgnore
    public String getResponseCode() {
        return super.getResponseCode();
    }

    @Override
    @JsonIgnore
    public String getResponseMessage() {
        return super.getResponseMessage();
    }

    /**
     * 创建成功的登录响应
     */
    public static LoginResponse success(String accessToken, String refreshToken, 
                                      Long expiresIn, Long userId, String username) {
        LoginResponse response = new LoginResponse();
        response.setSuccess(true);
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(expiresIn);
        response.setUserId(userId);
        response.setUsername(username);
        response.setLoginTime(LocalDateTime.now());
        return response;
    }

    /**
     * 创建失败的登录响应
     */
    public static LoginResponse error(String code, String message) {
        LoginResponse response = new LoginResponse();
        response.setSuccess(false);
        response.setResponseCode(code);
        response.setResponseMessage(message);
        return response;
    }
} 