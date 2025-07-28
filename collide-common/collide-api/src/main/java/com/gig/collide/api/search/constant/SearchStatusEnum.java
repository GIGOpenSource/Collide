package com.gig.collide.api.search.constant;

/**
 * 搜索状态枚举
 * 用于搜索历史、建议等状态管理
 * 
 * @author Collide Team  
 * @version 2.0
 * @since 2024-01-01
 */
public enum SearchStatusEnum {

    /**
     * 正常状态
     */
    NORMAL("正常"),
    
    /**
     * 活跃状态
     */
    ACTIVE("活跃"),
    
    /**
     * 非活跃状态
     */
    INACTIVE("非活跃"),
    
    /**
     * 待审核状态
     */
    PENDING("待审核"),
    
    /**
     * 已过期状态
     */
    EXPIRED("已过期"),
    
    /**
     * 被禁用状态
     */
    DISABLED("被禁用"),
    
    /**
     * 已删除状态
     */
    DELETED("已删除");

    private final String description;

    SearchStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查状态是否可用
     */
    public boolean isAvailable() {
        return this == NORMAL || this == ACTIVE;
    }

    /**
     * 检查状态是否需要审核
     */
    public boolean needsReview() {
        return this == PENDING;
    }

    /**
     * 检查状态是否被限制
     */
    public boolean isRestricted() {
        return this == DISABLED || this == DELETED || this == EXPIRED;
    }
} 