package com.gig.collide.api.like.constant;

/**
 * 点赞状态枚举
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public enum LikeStatusEnum {

    /**
     * 正常状态
     */
    NORMAL(1, "正常"),
    
    /**
     * 无效状态
     */
    INVALID(0, "无效");

    private final int code;
    private final String description;

    LikeStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据code获取枚举
     * 
     * @param code 状态码
     * @return 对应的枚举
     */
    public static LikeStatusEnum fromCode(int code) {
        for (LikeStatusEnum status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的状态code: " + code);
    }

    /**
     * 是否是正常状态
     */
    public boolean isNormal() {
        return this == NORMAL;
    }

    /**
     * 是否是无效状态
     */
    public boolean isInvalid() {
        return this == INVALID;
    }
} 