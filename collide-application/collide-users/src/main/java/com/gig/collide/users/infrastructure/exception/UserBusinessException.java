package com.gig.collide.users.infrastructure.exception;

import lombok.Getter;

/**
 * 用户业务异常
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
public class UserBusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final UserErrorCode errorCode;

    /**
     * 构造函数
     *
     * @param errorCode 错误码
     */
    public UserBusinessException(UserErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     *
     * @param errorCode 错误码
     * @param message   自定义错误信息
     */
    public UserBusinessException(UserErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     *
     * @param errorCode 错误码
     * @param cause     原始异常
     */
    public UserBusinessException(UserErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     *
     * @param errorCode 错误码
     * @param message   自定义错误信息
     * @param cause     原始异常
     */
    public UserBusinessException(UserErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
} 