package com.gig.collide.base.exception;

/**
 * 用户服务错误码枚举
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public enum UserErrorCode implements ErrorCode {

    /**
     * 用户不存在
     */
    USER_NOT_FOUND("USER_001", "用户不存在"),

    /**
     * 无效的性别参数
     */
    INVALID_GENDER("USER_002", "无效的性别参数"),

    /**
     * 已经是博主
     */
    ALREADY_BLOGGER("USER_003", "您已经是认证博主"),

    /**
     * 博主申请待审核
     */
    BLOGGER_APPLICATION_PENDING("USER_004", "您的博主认证申请正在审核中"),

    /**
     * 博主申请被拒绝
     */
    BLOGGER_APPLICATION_REJECTED("USER_005", "您的博主认证申请已被拒绝"),

    /**
     * VIP已过期
     */
    VIP_EXPIRED("USER_006", "VIP会员已过期"),

    /**
     * 用户状态异常
     */
    USER_STATUS_INVALID("USER_007", "用户状态异常"),

    /**
     * 权限不足
     */
    PERMISSION_DENIED("USER_008", "权限不足"),

    /**
     * 文件上传失败
     */
    FILE_UPLOAD_FAILED("USER_009", "文件上传失败"),

    /**
     * 文件格式不支持
     */
    UNSUPPORTED_FILE_TYPE("USER_010", "不支持的文件格式"),

    /**
     * 用户查询失败
     */
    USER_QUERY_ERROR("USER_011", "用户查询失败"),

    /**
     * 用户更新失败
     */
    USER_UPDATE_ERROR("USER_012", "用户更新失败"),

    /**
     * 博主申请失败
     */
    BLOGGER_APPLICATION_ERROR("USER_013", "博主申请失败"),

    /**
     * 邮箱已被注册
     */
    EMAIL_ALREADY_EXISTS("USER_014", "邮箱已被注册"),

    /**
     * 手机号已被注册
     */
    PHONE_ALREADY_EXISTS("USER_015", "手机号已被注册");

    private final String code;
    private final String message;

    UserErrorCode(String code, String message) {
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