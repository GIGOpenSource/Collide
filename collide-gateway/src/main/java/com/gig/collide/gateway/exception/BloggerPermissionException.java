package com.gig.collide.gateway.exception;

/**
 * 博主权限异常
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public class BloggerPermissionException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "需要博主权限，请先申请成为博主";

    public BloggerPermissionException() {
        super(DEFAULT_MESSAGE);
    }

    public BloggerPermissionException(String message) {
        super(message);
    }

    public BloggerPermissionException(String message, Throwable cause) {
        super(message, cause);
    }
}