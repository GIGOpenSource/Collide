package com.gig.collide.users.infrastructure.exception;

import com.gig.collide.base.exception.BizException;
import com.gig.collide.base.exception.ErrorCode;

/**
 * 用户业务异常
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public class UserException extends BizException {

    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UserException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public UserException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public UserException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public UserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode);
    }
} 