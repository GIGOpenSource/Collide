package com.gig.collide.api.user.constant;

/**
 * 邀请状态枚举
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public enum InviteStatusEnum {

    /**
     * 待确认
     */
    PENDING("待确认"),

    /**
     * 已确认
     */
    CONFIRMED("已确认"),

    /**
     * 已注册
     */
    REGISTERED("已注册"),

    /**
     * 已激活
     */
    ACTIVATED("已激活"),

    /**
     * 已过期
     */
    EXPIRED("已过期"),

    /**
     * 已取消
     */
    CANCELLED("已取消"),

    /**
     * 已失效
     */
    INVALID("已失效");

    private final String description;

    InviteStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查邀请是否有效
     */
    public boolean isValid() {
        return this == PENDING || this == CONFIRMED || this == REGISTERED || this == ACTIVATED;
    }

    /**
     * 检查邀请是否完成
     */
    public boolean isCompleted() {
        return this == ACTIVATED;
    }

    /**
     * 检查邀请是否失效
     */
    public boolean isInvalid() {
        return this == EXPIRED || this == CANCELLED || this == INVALID;
    }

    /**
     * 检查是否可以使用
     */
    public boolean canUse() {
        return this == PENDING || this == CONFIRMED;
    }
} 