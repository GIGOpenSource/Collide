package com.gig.collide.api.user.constant;

/**
 * 性别枚举 - 简洁版
 * 
 * @author GIG Team
 * @version 2.0.0
 */
public enum GenderEnum {
    
    MALE("male", "男"),
    FEMALE("female", "女"),
    UNKNOWN("unknown", "未知");

    private final String code;
    private final String desc;

    GenderEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
} 