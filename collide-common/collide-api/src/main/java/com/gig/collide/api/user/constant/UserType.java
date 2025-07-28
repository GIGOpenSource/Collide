package com.gig.collide.api.user.constant;

/**
 * 用户类型枚举
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public enum UserType {

    /**
     * 普通用户
     */
    CUSTOMER("普通用户"),

    /**
     * 博主用户
     */
    BLOGGER("博主用户"),

    /**
     * 企业用户
     */
    ENTERPRISE("企业用户"),

    /**
     * 平台用户
     */
    PLATFORM("平台用户"),

    /**
     * 系统用户
     */
    SYSTEM("系统用户");

    private final String description;

    UserType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查是否为内部用户
     */
    public boolean isInternalUser() {
        return this == PLATFORM || this == SYSTEM;
    }

    /**
     * 检查是否为内容创作者
     */
    public boolean isContentCreator() {
        return this == BLOGGER || this == ENTERPRISE;
    }
} 