package com.gig.collide.api.tag.constant;

/**
 * 标签状态枚举
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public enum TagStatusEnum {

    /**
     * 活跃状态
     */
    ACTIVE("活跃"),

    /**
     * 非活跃状态
     */
    INACTIVE("非活跃"),

    /**
     * 已删除状态
     */
    DELETED("已删除");

    private final String description;

    TagStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查标签是否可用
     */
    public boolean isAvailable() {
        return this == ACTIVE;
    }

    /**
     * 检查标签是否被删除
     */
    public boolean isDeleted() {
        return this == DELETED;
    }

    /**
     * 根据字符串获取标签状态
     */
    public static TagStatusEnum fromString(String status) {
        if (status == null || status.trim().isEmpty()) {
            return ACTIVE;
        }
        
        String normalized = status.trim().toLowerCase();
        switch (normalized) {
            case "active":
                return ACTIVE;
            case "inactive":
                return INACTIVE;
            case "deleted":
                return DELETED;
            default:
                return ACTIVE;
        }
    }
}