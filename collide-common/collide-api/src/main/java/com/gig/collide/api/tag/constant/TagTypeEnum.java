package com.gig.collide.api.tag.constant;

/**
 * 标签类型枚举
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public enum TagTypeEnum {

    /**
     * 内容标签
     */
    CONTENT("内容标签"),

    /**
     * 兴趣标签
     */
    INTEREST("兴趣标签"),

    /**
     * 系统标签
     */
    SYSTEM("系统标签");

    private final String description;

    TagTypeEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查是否为用户可见标签
     */
    public boolean isUserVisible() {
        return this == CONTENT || this == INTEREST;
    }

    /**
     * 检查是否为系统标签
     */
    public boolean isSystem() {
        return this == SYSTEM;
    }

    /**
     * 根据字符串获取标签类型
     */
    public static TagTypeEnum fromString(String type) {
        if (type == null || type.trim().isEmpty()) {
            return CONTENT;
        }
        
        String normalized = type.trim().toLowerCase();
        switch (normalized) {
            case "content":
                return CONTENT;
            case "interest":
                return INTEREST;
            case "system":
                return SYSTEM;
            default:
                return CONTENT;
        }
    }
} 