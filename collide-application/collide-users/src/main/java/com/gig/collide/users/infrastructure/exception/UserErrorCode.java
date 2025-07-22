package com.gig.collide.users.infrastructure.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户业务错误码枚举
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum UserErrorCode {

    // 用户相关错误码 (USER_xxx)
    USER_NOT_FOUND("USER_001", "用户不存在"),
    USER_STATUS_INVALID("USER_002", "用户状态无效"),
    USER_ALREADY_EXISTS("USER_003", "用户已存在"),
    USERNAME_ALREADY_EXISTS("USER_004", "用户名已存在"),
    EMAIL_ALREADY_EXISTS("USER_005", "邮箱已被注册"),
    PHONE_ALREADY_EXISTS("USER_006", "手机号已被注册"),
    PASSWORD_INCORRECT("USER_007", "密码错误"),
    USER_DISABLED("USER_008", "用户已被禁用"),
    USER_NOT_ACTIVATED("USER_009", "用户未激活"),

    // 博主认证相关错误码 (BLOGGER_xxx)
    BLOGGER_APPLY_REJECTED("BLOGGER_001", "博主申请已被拒绝"),
    BLOGGER_APPLY_PENDING("BLOGGER_002", "博主申请审核中"),
    BLOGGER_ALREADY_APPROVED("BLOGGER_003", "已是认证博主"),
    BLOGGER_APPLY_TOO_FREQUENT("BLOGGER_004", "申请过于频繁"),

    // VIP相关错误码 (VIP_xxx)
    VIP_EXPIRED("VIP_001", "VIP已过期"),
    VIP_NOT_ACTIVATED("VIP_002", "VIP未激活"),

    // 权限相关错误码 (PERMISSION_xxx)
    PERMISSION_DENIED("PERMISSION_001", "权限不足"),
    UNAUTHORIZED("PERMISSION_002", "未授权访问"),
    TOKEN_INVALID("PERMISSION_003", "Token无效"),
    TOKEN_EXPIRED("PERMISSION_004", "Token已过期"),

    // 系统相关错误码 (SYSTEM_xxx)
    SYSTEM_ERROR("SYSTEM_001", "系统异常"),
    PARAMETER_ERROR("SYSTEM_002", "参数错误"),
    DATA_NOT_FOUND("SYSTEM_003", "数据不存在");

    /**
     * 错误码
     */
    private final String code;

    /**
     * 错误信息
     */
    private final String message;
} 