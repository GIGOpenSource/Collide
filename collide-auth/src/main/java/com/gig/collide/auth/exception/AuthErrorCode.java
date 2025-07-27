package com.gig.collide.auth.exception;

import com.gig.collide.base.exception.ErrorCode;

/**
 * 认证错误码
 * 基于Code项目设计哲学，定义认证模块专用错误码
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-16
 */
public enum AuthErrorCode implements ErrorCode {

    /**
     * 用户状态不可用
     */
    USER_STATUS_IS_NOT_ACTIVE("USER_STATUS_IS_NOT_ACTIVE", "用户状态不可用"),

    /**
     * 验证码错误
     */
    VERIFICATION_CODE_WRONG("VERIFICATION_CODE_WRONG", "验证码错误"),

    /**
     * 用户信息查询失败
     */
    USER_QUERY_FAILED("USER_QUERY_FAILED", "用户信息查询失败"),

    /**
     * 用户未登录
     */
    USER_NOT_LOGIN("USER_NOT_LOGIN", "用户未登录"),

    /**
     * 用户不存在
     */
    USER_NOT_EXIST("USER_NOT_EXIST", "用户不存在"),

    /**
     * 密码错误
     */
    PASSWORD_WRONG("PASSWORD_WRONG", "密码错误"),

    /**
     * 邀请码无效
     */
    INVALID_INVITE_CODE("INVALID_INVITE_CODE", "邀请码无效"),

    /**
     * 注册失败
     */
    REGISTER_FAILED("REGISTER_FAILED", "注册失败"),

    /**
     * 登录失败
     */
    LOGIN_FAILED("LOGIN_FAILED", "登录失败"),

    /**
     * 登出失败
     */
    LOGOUT_FAILED("LOGOUT_FAILED", "登出失败"),

    /**
     * 用户名已存在
     */
    USERNAME_ALREADY_EXISTS("USERNAME_ALREADY_EXISTS", "用户名已存在");

    private final String code;
    private final String message;

    AuthErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
} 