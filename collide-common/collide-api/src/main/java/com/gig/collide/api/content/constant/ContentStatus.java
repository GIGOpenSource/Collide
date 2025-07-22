package com.gig.collide.api.content.constant;

/**
 * 内容状态枚举
 * 对应 t_content 表的 status 字段
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public enum ContentStatus {

    /**
     * 草稿
     */
    DRAFT("草稿"),

    /**
     * 待审核
     */
    PENDING("待审核"),

    /**
     * 已发布
     */
    PUBLISHED("已发布"),

    /**
     * 已拒绝
     */
    REJECTED("已拒绝"),

    /**
     * 已下架
     */
    OFFLINE("已下架");

    private final String description;

    ContentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 判断是否为可见状态
     *
     * @return true if visible
     */
    public boolean isVisible() {
        return this == PUBLISHED;
    }

    /**
     * 判断是否为可编辑状态
     *
     * @return true if editable
     */
    public boolean isEditable() {
        return this == DRAFT || this == REJECTED;
    }
} 