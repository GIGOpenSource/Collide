package com.gig.collide.api.social.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 社交互动类型枚举
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum SocialInteractionType {

    /**
     * 点赞
     */
    LIKE("LIKE", "点赞"),

    /**
     * 点踩
     */
    DISLIKE("DISLIKE", "点踩"),

    /**
     * 评论
     */
    COMMENT("COMMENT", "评论"),

    /**
     * 转发
     */
    SHARE("SHARE", "转发"),

    /**
     * 收藏
     */
    FAVORITE("FAVORITE", "收藏"),

    /**
     * 举报
     */
    REPORT("REPORT", "举报"),

    /**
     * 查看
     */
    VIEW("VIEW", "查看");

    /**
     * 互动类型代码
     */
    private final String code;

    /**
     * 互动类型描述
     */
    private final String description;
} 