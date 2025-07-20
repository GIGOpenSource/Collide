package com.gig.collide.follow.infrastructure.exception;

import com.gig.collide.base.exception.BizException;

/**
 * 关注异常
 * @author GIG
 */
public class FollowException extends BizException {

    public FollowException(FollowErrorCode errorCode) {
        super(errorCode);
    }

    public FollowException(FollowErrorCode errorCode, String message) {
        super(message, errorCode);
    }

    public FollowException(FollowErrorCode errorCode, Throwable cause) {
        super(cause, errorCode);
    }
} 