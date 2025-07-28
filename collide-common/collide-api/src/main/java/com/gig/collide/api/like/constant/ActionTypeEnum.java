package com.gig.collide.api.like.constant;

/**
 * 点赞操作类型枚举
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public enum ActionTypeEnum {

    /**
     * 点赞
     */
    LIKE(1, "点赞"),
    
    /**
     * 取消操作
     */
    CANCEL(0, "取消"),
    
    /**
     * 点踩
     */
    DISLIKE(-1, "点踩");

    private final int code;
    private final String description;

    ActionTypeEnum(int code, String description) {
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
     * @param code 操作码
     * @return 对应的枚举
     */
    public static ActionTypeEnum fromCode(int code) {
        for (ActionTypeEnum actionType : values()) {
            if (actionType.getCode() == code) {
                return actionType;
            }
        }
        throw new IllegalArgumentException("未知的操作类型code: " + code);
    }

    /**
     * 是否是正面操作（点赞）
     */
    public boolean isPositive() {
        return this == LIKE;
    }

    /**
     * 是否是负面操作（点踩）
     */
    public boolean isNegative() {
        return this == DISLIKE;
    }

    /**
     * 是否是取消操作
     */
    public boolean isCancel() {
        return this == CANCEL;
    }
} 