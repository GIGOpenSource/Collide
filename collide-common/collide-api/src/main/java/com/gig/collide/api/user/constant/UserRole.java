package com.gig.collide.api.user.constant;

/**
 * 用户角色枚举
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public enum UserRole {

    /**
     * 普通用户
     */
    USER("普通用户"),

    /**
     * VIP用户
     */
    VIP("VIP用户"),

    /**
     * 博主
     */
    BLOGGER("博主"),

    /**
     * 管理员
     */
    ADMIN("管理员");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查是否有发布内容权限
     */
    public boolean canPublishContent() {
        return this == BLOGGER || this == ADMIN;
    }

    /**
     * 检查是否为高级用户
     */
    public boolean isPremiumUser() {
        return this == VIP || this == BLOGGER || this == ADMIN;
    }
}
