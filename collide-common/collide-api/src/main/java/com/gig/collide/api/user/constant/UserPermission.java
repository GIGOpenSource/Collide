package com.gig.collide.api.user.constant;

/**
 * 用户权限枚举
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public enum UserPermission {

    /**
     * 基本权限
     */
    BASIC("基本权限"),

    /**
     * 已认证用户权限
     */
    AUTHENTICATED("已认证用户"),

    /**
     * VIP用户权限
     */
    VIP("VIP用户"),

    /**
     * 博主权限
     */
    BLOGGER("博主权限"),

    /**
     * 管理员权限
     */
    ADMIN("管理员权限"),

    /**
     * 已冻结权限
     */
    FROZEN("已冻结"),

    /**
     * 无任何权限
     */
    NONE("无权限");

    private final String description;

    UserPermission(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查是否有内容发布权限
     */
    public boolean canPublishContent() {
        return this == BLOGGER || this == ADMIN;
    }

    /**
     * 检查是否有管理权限
     */
    public boolean hasAdminPermission() {
        return this == ADMIN;
    }

    /**
     * 检查是否为高级权限
     */
    public boolean isPremiumPermission() {
        return this == VIP || this == BLOGGER || this == ADMIN;
    }

    /**
     * 检查权限是否被限制
     */
    public boolean isRestricted() {
        return this == FROZEN || this == NONE;
    }
}
