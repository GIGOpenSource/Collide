package com.gig.collide.base.exception;

/**
 * 通用错误码定义
 * 包含系统常用的基础错误码常量
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public enum CommonErrorCode implements ErrorCode {

    // ========== 成功状态码 ==========
    SUCCESS("SUCCESS", "操作成功"),

    // ========== 系统错误码 ==========
    SYSTEM_ERROR("SYSTEM_ERROR", "系统内部错误"),
    OPERATION_FAILED("OPERATION_FAILED", "操作失败"),
    SERVICE_UNAVAILABLE("SERVICE_UNAVAILABLE", "服务不可用"),
    NETWORK_ERROR("NETWORK_ERROR", "网络错误"),
    TIMEOUT("TIMEOUT", "操作超时"),

    // ========== 参数错误码 ==========
    PARAM_INVALID("PARAM_INVALID", "参数无效"),
    PARAM_REQUIRED("PARAM_REQUIRED", "必填参数不能为空"),
    PARAM_TYPE_ERROR("PARAM_TYPE_ERROR", "参数类型错误"),
    PARAM_FORMAT_ERROR("PARAM_FORMAT_ERROR", "参数格式错误"),
    PARAM_OUT_OF_RANGE("PARAM_OUT_OF_RANGE", "参数超出范围"),

    // ========== 资源错误码 ==========
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "资源不存在"),
    RESOURCE_ALREADY_EXISTS("RESOURCE_ALREADY_EXISTS", "资源已存在"),
    RESOURCE_CONFLICT("RESOURCE_CONFLICT", "资源冲突"),
    RESOURCE_EXPIRED("RESOURCE_EXPIRED", "资源已过期"),
    RESOURCE_LOCKED("RESOURCE_LOCKED", "资源被锁定"),

    // ========== 权限错误码 ==========
    ACCESS_DENIED("ACCESS_DENIED", "访问被拒绝"),
    UNAUTHORIZED("UNAUTHORIZED", "未授权"),
    PERMISSION_DENIED("PERMISSION_DENIED", "权限不足"),
    LOGIN_REQUIRED("LOGIN_REQUIRED", "需要登录"),
    TOKEN_INVALID("TOKEN_INVALID", "令牌无效"),
    TOKEN_EXPIRED("TOKEN_EXPIRED", "令牌已过期"),

    // ========== 业务错误码 ==========
    OPERATION_NOT_ALLOWED("OPERATION_NOT_ALLOWED", "操作不被允许"),
    BUSINESS_RULE_VIOLATION("BUSINESS_RULE_VIOLATION", "违反业务规则"),
    DATA_INTEGRITY_VIOLATION("DATA_INTEGRITY_VIOLATION", "数据完整性冲突"),
    CONCURRENT_OPERATION("CONCURRENT_OPERATION", "并发操作冲突"),
    QUOTA_EXCEEDED("QUOTA_EXCEEDED", "超出配额限制"),

    // ========== 数据错误码 ==========
    DATA_NOT_FOUND("DATA_NOT_FOUND", "数据不存在"),
    DATA_ALREADY_EXISTS("DATA_ALREADY_EXISTS", "数据已存在"),
    DATA_FORMAT_ERROR("DATA_FORMAT_ERROR", "数据格式错误"),
    DATA_TOO_LARGE("DATA_TOO_LARGE", "数据过大"),
    DATA_CORRUPTED("DATA_CORRUPTED", "数据损坏"),

    // ========== 外部服务错误码 ==========
    THIRD_PARTY_ERROR("THIRD_PARTY_ERROR", "第三方服务错误"),
    API_RATE_LIMIT("API_RATE_LIMIT", "API调用频率限制"),
    REMOTE_SERVICE_TIMEOUT("REMOTE_SERVICE_TIMEOUT", "远程服务超时"),
    REMOTE_SERVICE_ERROR("REMOTE_SERVICE_ERROR", "远程服务错误"),

    // ========== 文件错误码 ==========
    FILE_NOT_FOUND("FILE_NOT_FOUND", "文件不存在"),
    FILE_TOO_LARGE("FILE_TOO_LARGE", "文件过大"),
    FILE_TYPE_NOT_SUPPORTED("FILE_TYPE_NOT_SUPPORTED", "文件类型不支持"),
    FILE_UPLOAD_FAILED("FILE_UPLOAD_FAILED", "文件上传失败"),
    FILE_DOWNLOAD_FAILED("FILE_DOWNLOAD_FAILED", "文件下载失败"),

    // ========== 数据库错误码 ==========
    DATABASE_ERROR("DATABASE_ERROR", "数据库错误"),
    DATABASE_CONNECTION_FAILED("DATABASE_CONNECTION_FAILED", "数据库连接失败"),
    DATABASE_TIMEOUT("DATABASE_TIMEOUT", "数据库操作超时"),
    SQL_SYNTAX_ERROR("SQL_SYNTAX_ERROR", "SQL语法错误"),
    DATABASE_CONSTRAINT_VIOLATION("DATABASE_CONSTRAINT_VIOLATION", "数据库约束冲突"),

    // ========== 缓存错误码 ==========
    CACHE_ERROR("CACHE_ERROR", "缓存错误"),
    CACHE_KEY_NOT_FOUND("CACHE_KEY_NOT_FOUND", "缓存键不存在"),
    CACHE_TIMEOUT("CACHE_TIMEOUT", "缓存操作超时"),
    CACHE_CONNECTION_FAILED("CACHE_CONNECTION_FAILED", "缓存连接失败"),

    // ========== 配置错误码 ==========
    CONFIG_ERROR("CONFIG_ERROR", "配置错误"),
    CONFIG_NOT_FOUND("CONFIG_NOT_FOUND", "配置不存在"),
    CONFIG_FORMAT_ERROR("CONFIG_FORMAT_ERROR", "配置格式错误"),

    // ========== 验证错误码 ==========
    VALIDATION_FAILED("VALIDATION_FAILED", "验证失败"),
    CAPTCHA_INVALID("CAPTCHA_INVALID", "验证码无效"),
    CAPTCHA_EXPIRED("CAPTCHA_EXPIRED", "验证码已过期"),
    SIGNATURE_INVALID("SIGNATURE_INVALID", "签名无效");

    private final String code;
    private final String message;

    CommonErrorCode(String code, String message) {
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

    @Override
    public String toString() {
        return String.format("[%s] %s", code, message);
    }

    /**
     * 根据错误码字符串获取错误码枚举
     *
     * @param code 错误码字符串
     * @return 错误码枚举，如果未找到则返回 SYSTEM_ERROR
     */
    public static CommonErrorCode getByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return SYSTEM_ERROR;
        }
        
        for (CommonErrorCode errorCode : CommonErrorCode.values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode;
            }
        }
        
        return SYSTEM_ERROR;
    }
} 