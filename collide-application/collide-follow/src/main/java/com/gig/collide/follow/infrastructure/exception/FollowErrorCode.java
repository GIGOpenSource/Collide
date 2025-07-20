package com.gig.collide.follow.infrastructure.exception;

import com.gig.collide.base.exception.ErrorCode;

/**
 * 关注错误码
 * @author GIG
 */
public enum FollowErrorCode implements ErrorCode {

    CANNOT_FOLLOW_SELF("FOLLOW_001", "不能关注自己"),
    ALREADY_FOLLOWED("FOLLOW_002", "已经关注了该用户"),
    NOT_FOLLOWED("FOLLOW_003", "尚未关注该用户"),
    USER_NOT_EXIST("FOLLOW_004", "用户不存在"),
    FOLLOW_LIMIT_EXCEEDED("FOLLOW_005", "关注数量超过限制"),
    SYSTEM_ERROR("FOLLOW_999", "系统异常");

    private final String code;
    private final String message;

    FollowErrorCode(String code, String message) {
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