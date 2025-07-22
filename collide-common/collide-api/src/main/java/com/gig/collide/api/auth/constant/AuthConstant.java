package com.gig.collide.api.auth.constant;

/**
 * 认证相关常量
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public class AuthConstant {

    /**
     * OAuth2.0 相关常量
     */
    public static class OAuth {
        /** 授权码类型 */
        public static final String AUTHORIZATION_CODE = "authorization_code";
        /** 密码类型 */
        public static final String PASSWORD = "password";
        /** 刷新令牌类型 */
        public static final String REFRESH_TOKEN = "refresh_token";
        /** 客户端凭证类型 */
        public static final String CLIENT_CREDENTIALS = "client_credentials";

        /** 默认作用域 */
        public static final String DEFAULT_SCOPE = "read,write";
        /** 读权限作用域 */
        public static final String READ_SCOPE = "read";
        /** 写权限作用域 */
        public static final String WRITE_SCOPE = "write";
    }

    /**
     * 登录相关常量
     */
    public static class Login {
        /** 用户名密码登录 */
        public static final String USERNAME_PASSWORD = "username_password";
        /** 手机验证码登录 */
        public static final String PHONE_CODE = "phone_code";
        /** 邮箱密码登录 */
        public static final String EMAIL_PASSWORD = "email_password";
        /** 第三方OAuth登录 */
        public static final String OAUTH_LOGIN = "oauth_login";
    }

    /**
     * Token相关常量
     */
    public static class Token {
        /** 访问令牌默认过期时间（秒） */
        public static final int DEFAULT_ACCESS_TOKEN_EXPIRE = 3600 * 24; // 24小时
        /** 刷新令牌默认过期时间（秒） */
        public static final int DEFAULT_REFRESH_TOKEN_EXPIRE = 3600 * 24 * 30; // 30天
        
        /** Token前缀 */
        public static final String TOKEN_PREFIX = "Bearer ";
        /** 请求头名称 */
        public static final String TOKEN_HEADER = "Authorization";
    }

    /**
     * 应用类型常量
     */
    public static class AppType {
        /** Web应用 */
        public static final String WEB = "web";
        /** 移动应用 */
        public static final String MOBILE = "mobile";
        /** 桌面应用 */
        public static final String DESKTOP = "desktop";
        /** 服务端应用 */
        public static final String SERVER = "server";
    }
} 