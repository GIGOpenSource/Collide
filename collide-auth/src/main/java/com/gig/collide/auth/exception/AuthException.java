package com.gig.collide.auth.exception;

import com.gig.collide.base.exception.BizException;
import com.gig.collide.base.exception.ErrorCode;

/**
 * 认证异常
 * 基于Code项目设计哲学，继承通用业务异常
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-16
 */
public class AuthException extends BizException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public AuthException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public AuthException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public AuthException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode);
    }
} 