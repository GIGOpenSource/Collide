package com.gig.collide.base.exception;

/**
 * 认证服务错误码
 * 参考 nft-turbo-base 设计模式
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public enum AuthErrorCode implements ErrorCode {

    VERIFICATION_CODE_WRONG("AUTH_001", "验证码错误"),
    USER_NOT_FOUND("AUTH_002", "用户不存在"),
    PASSWORD_WRONG("AUTH_003", "密码错误"),
    USER_DISABLED("AUTH_004", "用户已被禁用"),
    TOKEN_EXPIRED("AUTH_005", "令牌已过期"),
    TOKEN_INVALID("AUTH_006", "令牌无效"),
    PHONE_NOT_VERIFIED("AUTH_007", "手机号未验证"),
    CAPTCHA_EXPIRED("AUTH_008", "验证码已过期"),
    CAPTCHA_SEND_FAILED("AUTH_009", "验证码发送失败"),
    LOGIN_FAILED("AUTH_010", "登录失败"),
    REGISTER_FAILED("AUTH_011", "注册失败"),
    NOT_LOGGED_IN("AUTH_012", "用户未登录"),
    SYSTEM_ERROR("AUTH_999", "系统异常");

    private final String code;
    private final String message;

    AuthErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
} 