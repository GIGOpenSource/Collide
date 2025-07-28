package com.gig.collide.api.tag.constant;

/**
 * 兴趣来源枚举
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public enum InterestSourceEnum {

    /**
     * 手动设置
     */
    MANUAL("手动设置"),

    /**
     * 行为分析
     */
    BEHAVIOR("行为分析"),

    /**
     * 系统推荐
     */
    SYSTEM("系统推荐"),

    /**
     * 算法计算
     */
    ALGORITHM("算法计算");

    private final String description;

    InterestSourceEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查是否为自动来源
     */
    public boolean isAutomatic() {
        return this == BEHAVIOR || this == SYSTEM || this == ALGORITHM;
    }

    /**
     * 检查是否为手动来源
     */
    public boolean isManual() {
        return this == MANUAL;
    }

    /**
     * 根据字符串获取兴趣来源
     */
    public static InterestSourceEnum fromString(String source) {
        if (source == null || source.trim().isEmpty()) {
            return MANUAL;
        }
        
        String normalized = source.trim().toLowerCase();
        switch (normalized) {
            case "manual":
                return MANUAL;
            case "behavior":
                return BEHAVIOR;
            case "system":
                return SYSTEM;
            case "algorithm":
                return ALGORITHM;
            default:
                return MANUAL;
        }
    }
} 