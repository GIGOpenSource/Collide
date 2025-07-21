package com.gig.collide.artist.infrastructure.exception;

import com.gig.collide.base.exception.BizException;
import com.gig.collide.base.exception.ErrorCode;

/**
 * 博主模块异常
 * @author GIG
 */
public class ArtistException extends BizException {

    public ArtistException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ArtistException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public ArtistException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public ArtistException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public ArtistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode);
    }
} 