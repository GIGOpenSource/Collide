package com.gig.collide.api.favorite.constant;

/**
 * 收藏夹类型枚举
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public enum FolderType {

    /**
     * 默认收藏夹
     */
    DEFAULT("默认"),

    /**
     * 公开收藏夹
     */
    PUBLIC("公开"),

    /**
     * 私密收藏夹
     */
    PRIVATE("私密"),

    /**
     * 自定义收藏夹
     */
    CUSTOM("自定义");

    private final String description;

    FolderType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查是否为系统收藏夹
     */
    public boolean isSystemFolder() {
        return this == DEFAULT;
    }

    /**
     * 检查是否为用户自定义收藏夹
     */
    public boolean isUserCustom() {
        return this == PUBLIC || this == PRIVATE || this == CUSTOM;
    }

    /**
     * 检查是否可被其他用户访问
     */
    public boolean isPublicVisible() {
        return this == PUBLIC || this == DEFAULT;
    }

    /**
     * 检查是否为私密收藏夹
     */
    public boolean isPrivate() {
        return this == PRIVATE;
    }
} 