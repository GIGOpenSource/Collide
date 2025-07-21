package com.gig.collide.chain.infrastructure.exception;

import com.gig.collide.base.exception.BizException;
import com.gig.collide.base.exception.ErrorCode;

/**
 * 链异常
 *
 * @author GIG
 */
public class ChainException extends BizException {

    public ChainException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ChainException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public ChainException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public ChainException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public ChainException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode);
    }

}
