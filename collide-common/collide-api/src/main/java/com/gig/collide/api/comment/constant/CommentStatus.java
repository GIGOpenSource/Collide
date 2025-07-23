package com.gig.collide.api.comment.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 评论状态枚举
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum CommentStatus {

    /**
     * 正常
     */
    NORMAL("normal", "正常"),

    /**
     * 已删除
     */
    DELETED("deleted", "已删除"),

    /**
     * 已屏蔽
     */
    BLOCKED("blocked", "已屏蔽"),

    /**
     * 审核中
     */
    REVIEWING("reviewing", "审核中"),

    /**
     * 审核拒绝
     */
    REJECTED("rejected", "审核拒绝");

    /**
     * 状态码
     */
    private final String code;

    /**
     * 状态描述
     */
    private final String description;
} 