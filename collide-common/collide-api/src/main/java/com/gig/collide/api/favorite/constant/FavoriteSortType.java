package com.gig.collide.api.favorite.constant;

/**
 * 收藏排序类型枚举
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public enum FavoriteSortType {

    /**
     * 按收藏时间排序
     */
    FAVORITE_TIME("favorite_time", "收藏时间"),

    /**
     * 按创建时间排序
     */
    CREATE_TIME("create_time", "创建时间"),

    /**
     * 按更新时间排序
     */
    UPDATE_TIME("update_time", "更新时间"),

    /**
     * 按目标发布时间排序
     */
    TARGET_PUBLISH_TIME("target_publish_time", "目标发布时间"),

    /**
     * 按目标标题排序
     */
    TARGET_TITLE("target_title", "目标标题"),

    /**
     * 按目标作者排序
     */
    TARGET_AUTHOR("target_author_name", "目标作者"),

    /**
     * 按收藏夹排序
     */
    FOLDER("folder_id", "收藏夹"),

    /**
     * 按收藏类型排序
     */
    TYPE("favorite_type", "收藏类型");

    private final String field;
    private final String description;

    FavoriteSortType(String field, String description) {
        this.field = field;
        this.description = description;
    }

    public String getField() {
        return field;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查是否为时间相关排序
     */
    public boolean isTimeSort() {
        return this == FAVORITE_TIME || this == CREATE_TIME || 
               this == UPDATE_TIME || this == TARGET_PUBLISH_TIME;
    }

    /**
     * 检查是否为文本相关排序
     */
    public boolean isTextSort() {
        return this == TARGET_TITLE || this == TARGET_AUTHOR;
    }
} 