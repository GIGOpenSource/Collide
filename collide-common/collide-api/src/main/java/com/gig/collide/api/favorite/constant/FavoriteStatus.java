package com.gig.collide.api.favorite.constant;

/**
 * 收藏状态枚举
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public enum FavoriteStatus {

    /**
     * 已取消
     */
    CANCELLED("已取消"),

    /**
     * 正常
     */
    NORMAL("正常");

    private final String description;

    FavoriteStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查是否为有效状态（可显示）
     */
    public boolean isActive() {
        return this == NORMAL;
    }

    /**
     * 检查是否已取消
     */
    public boolean isCancelled() {
        return this == CANCELLED;
    }
} 