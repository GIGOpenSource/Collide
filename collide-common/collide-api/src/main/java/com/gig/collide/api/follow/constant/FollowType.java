package com.gig.collide.api.follow.constant;

/**
 * 关注类型枚举
 * 对应 t_follow 表的 follow_type 字段
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public enum FollowType {

    /**
     * 普通关注
     */
    NORMAL("普通关注"),

    /**
     * 特别关注（预留）
     */
    SPECIAL("特别关注");

    private final String description;

    FollowType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 