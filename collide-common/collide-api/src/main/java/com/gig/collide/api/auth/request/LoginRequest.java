package com.gig.collide.api.auth.request;

import com.gig.collide.api.auth.enums.LoginType;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 统一登录请求
 * 支持多种登录方式的统一入口
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LoginRequest extends BaseRequest {

    /**
     * 登录类型
     */
    @NotNull(message = "登录类型不能为空")
    private LoginType loginType;

    /**
     * 主要标识（用户名、手机号、邮箱）
     */
    @NotBlank(message = "登录标识不能为空")
    private String principal;

    /**
     * 凭证（密码、验证码、第三方token）
     */
    private String credentials;

    /**
     * 客户端ID（OAuth2.0应用标识）
     */
    private String clientId;

    /**
     * 设备信息
     */
    private String deviceId;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 扩展参数（用于第三方登录等特殊参数）
     */
    private Map<String, Object> extraParams;

    /**
     * 是否记住登录状态
     */
    private Boolean rememberMe = false;

    /**
     * 验证码ID（手机验证码登录时使用）
     */
    private String codeId;
} 