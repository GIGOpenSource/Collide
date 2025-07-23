package com.gig.collide.api.comment.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 评论类型枚举
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum CommentType {

    /**
     * 内容评论
     */
    CONTENT("content", "内容评论"),

    /**
     * 回复评论
     */
    REPLY("reply", "回复评论"),

    /**
     * 朋友圈评论
     */
    SOCIAL("social", "朋友圈评论");

    /**
     * 类型码
     */
    private final String code;

    /**
     * 类型描述
     */
    private final String description;
} 