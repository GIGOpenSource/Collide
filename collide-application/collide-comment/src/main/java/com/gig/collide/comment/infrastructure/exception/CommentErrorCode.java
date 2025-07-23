package com.gig.collide.comment.infrastructure.exception;

import com.gig.collide.base.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 评论业务错误码枚举
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum CommentErrorCode implements ErrorCode {

    // 评论相关错误码 (COMMENT_xxx)
    COMMENT_NOT_FOUND("COMMENT_001", "评论不存在"),
    COMMENT_CREATE_FAILED("COMMENT_002", "评论创建失败"),
    COMMENT_DELETE_FAILED("COMMENT_003", "评论删除失败"),
    COMMENT_UPDATE_FAILED("COMMENT_004", "评论更新失败"),
    COMMENT_DELETE_PERMISSION_DENIED("COMMENT_005", "评论删除权限不足"),
    COMMENT_STATUS_INVALID("COMMENT_006", "评论状态无效"),
    PARENT_COMMENT_NOT_FOUND("COMMENT_007", "父评论不存在"),
    COMMENT_CONTENT_TOO_LONG("COMMENT_008", "评论内容过长"),
    COMMENT_ALREADY_DELETED("COMMENT_009", "评论已删除"),
    COMMENT_LIKE_FAILED("COMMENT_010", "评论点赞失败"),

    // 参数校验错误码 (PARAM_xxx)
    INVALID_PARAMETER("PARAM_001", "参数无效"),
    COMMENT_TYPE_REQUIRED("PARAM_002", "评论类型不能为空"),
    TARGET_ID_REQUIRED("PARAM_003", "目标ID不能为空"),
    COMMENT_CONTENT_REQUIRED("PARAM_004", "评论内容不能为空"),
    USER_ID_REQUIRED("PARAM_005", "用户ID不能为空"),
    COMMENT_ID_REQUIRED("PARAM_006", "评论ID不能为空"),
    PAGE_PARAM_INVALID("PARAM_007", "分页参数无效"),

    // 系统相关错误码 (SYSTEM_xxx)
    SYSTEM_ERROR("SYSTEM_001", "系统异常"),
    DATABASE_ERROR("SYSTEM_002", "数据库操作异常"),
    CACHE_ERROR("SYSTEM_003", "缓存操作异常");

    /**
     * 错误码
     */
    private final String code;

    /**
     * 错误信息
     */
    private final String message;

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
} 