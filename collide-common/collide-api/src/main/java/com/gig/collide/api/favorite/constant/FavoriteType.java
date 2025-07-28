package com.gig.collide.api.favorite.constant;

/**
 * 收藏类型枚举
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public enum FavoriteType {

    /**
     * 内容收藏
     */
    CONTENT("内容"),

    /**
     * 用户收藏
     */
    USER("用户"),

    /**
     * 动态收藏
     */
    SOCIAL("动态"),

    /**
     * 评论收藏
     */
    COMMENT("评论"),

    /**
     * 话题收藏
     */
    TOPIC("话题");

    private final String description;

    FavoriteType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查是否为内容相关类型
     */
    public boolean isContentRelated() {
        return this == CONTENT || this == SOCIAL || this == COMMENT;
    }

    /**
     * 检查是否为用户相关类型
     */
    public boolean isUserRelated() {
        return this == USER;
    }

    /**
     * 检查是否为可评论的类型
     */
    public boolean isCommentable() {
        return this == CONTENT || this == SOCIAL;
    }
} 