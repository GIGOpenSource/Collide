package com.gig.collide.api.tag.constant;

/**
 * 统计维度枚举
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public enum StatisticsDimensionEnum {

    /**
     * 日统计
     */
    DAILY("日统计"),

    /**
     * 周统计
     */
    WEEKLY("周统计"),

    /**
     * 月统计
     */
    MONTHLY("月统计"),

    /**
     * 年统计
     */
    YEARLY("年统计");

    private final String description;

    StatisticsDimensionEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查是否为短期统计
     */
    public boolean isShortTerm() {
        return this == DAILY || this == WEEKLY;
    }

    /**
     * 检查是否为长期统计
     */
    public boolean isLongTerm() {
        return this == MONTHLY || this == YEARLY;
    }

    /**
     * 根据字符串获取统计维度
     */
    public static StatisticsDimensionEnum fromString(String dimension) {
        if (dimension == null || dimension.trim().isEmpty()) {
            return DAILY;
        }
        
        String normalized = dimension.trim().toLowerCase();
        switch (normalized) {
            case "daily":
            case "day":
                return DAILY;
            case "weekly":
            case "week":
                return WEEKLY;
            case "monthly":
            case "month":
                return MONTHLY;
            case "yearly":
            case "year":
                return YEARLY;
            default:
                return DAILY;
        }
    }
} 