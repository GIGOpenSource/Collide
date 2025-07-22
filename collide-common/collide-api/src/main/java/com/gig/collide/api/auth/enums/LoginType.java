package com.gig.collide.api.auth.enums;

/**
 * 登录类型枚举
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public enum LoginType {
    /**
     * 用户名密码登录
     */
    USERNAME_PASSWORD("username_password", "用户名密码登录"),
    
    /**
     * 手机验证码登录
     */
    PHONE_CODE("phone_code", "手机验证码登录"),
    
    /**
     * 邮箱密码登录
     */
    EMAIL_PASSWORD("email_password", "邮箱密码登录"),
    
    /**
     * 微信登录
     */
    WECHAT("wechat", "微信登录"),
    
    /**
     * QQ登录
     */
    QQ("qq", "QQ登录"),
    
    /**
     * 微博登录
     */
    WEIBO("weibo", "微博登录");

    private final String code;
    private final String description;

    LoginType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据code获取枚举值
     */
    public static LoginType fromCode(String code) {
        for (LoginType type : LoginType.values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown LoginType code: " + code);
    }

    /**
     * 判断是否为第三方登录
     */
    public boolean isThirdPartyLogin() {
        return this == WECHAT || this == QQ || this == WEIBO;
    }
} 