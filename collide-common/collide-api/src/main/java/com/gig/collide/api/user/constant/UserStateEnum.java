package com.gig.collide.api.user.constant;

/**
 * 用户状态枚举
 * 与users模块保持一致
 * 
 * @author Collide Team  
 * @version 2.0
 * @since 2024-01-01
 */
public enum UserStateEnum {

    /**
     * 初始创建状态
     */
    INIT("初始创建"),
    
    /**
     * 待激活状态
     */
    PENDING("待激活"),
    
    /**
     * 正常激活状态
     */
    ACTIVE("已激活"),
    
    /**
     * 未激活状态
     */
    INACTIVE("未激活"),
    
    /**
     * 已封禁状态
     */
    BANNED("已封禁"),
    
    /**
     * 已冻结状态
     */
    FROZEN("已冻结"),
    
    /**
     * 已删除状态
     */
    DELETED("已删除");

    private final String description;

    UserStateEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查用户是否可用
     */
    public boolean isAvailable() {
        return this == ACTIVE;
    }

    /**
     * 检查用户是否被限制
     */
    public boolean isRestricted() {
        return this == BANNED || this == FROZEN || this == DELETED;
    }
}
