package com.gig.collide.api.user.constant;

/**
 * 博主认证状态枚举
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public enum BloggerStatusEnum {

    /**
     * 无认证
     */
    NONE("无认证"),

    /**
     * 申请中
     */
    APPLYING("申请中"),

    /**
     * 已通过
     */
    APPROVED("已通过"),

    /**
     * 已拒绝
     */
    REJECTED("已拒绝"),

    /**
     * 已暂停
     */
    SUSPENDED("已暂停"),

    /**
     * 已撤销
     */
    REVOKED("已撤销");

    private final String description;

    BloggerStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查是否为有效博主
     */
    public boolean isValidBlogger() {
        return this == APPROVED;
    }

    /**
     * 检查是否可以申请
     */
    public boolean canApply() {
        return this == NONE || this == REJECTED;
    }

    /**
     * 检查是否为处理中状态
     */
    public boolean isPending() {
        return this == APPLYING;
    }

    /**
     * 检查是否被限制
     */
    public boolean isRestricted() {
        return this == SUSPENDED || this == REVOKED;
    }
} 