package com.gig.collide.api.user.constant;

/**
 * 性别枚举
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public enum GenderEnum {

    /**
     * 男性
     */
    MALE("男"),

    /**
     * 女性
     */
    FEMALE("女"),

    /**
     * 其他
     */
    OTHER("其他"),

    /**
     * 未知/不愿透露
     */
    UNKNOWN("未知");

    private final String description;

    GenderEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据字符串获取性别枚举
     */
    public static GenderEnum fromString(String gender) {
        if (gender == null || gender.trim().isEmpty()) {
            return UNKNOWN;
        }
        
        String normalized = gender.trim().toLowerCase();
        switch (normalized) {
            case "male":
            case "男":
            case "m":
                return MALE;
            case "female":
            case "女":
            case "f":
                return FEMALE;
            case "other":
            case "其他":
                return OTHER;
            default:
                return UNKNOWN;
        }
    }
} 