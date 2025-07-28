package com.gig.collide.api.social.constant;

/**
 * 社交互动状态枚举
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
public enum InteractionStatusEnum {
    
    CANCELLED(0, "已取消", "用户取消了该互动"),
    ACTIVE(1, "有效", "互动有效且计入统计");
    
    private final int status;
    private final String code;
    private final String description;
    
    InteractionStatusEnum(int status, String code, String description) {
        this.status = status;
        this.code = code;
        this.description = description;
    }
    
    public int getStatus() {
        return status;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 是否为有效状态
     */
    public boolean isActive() {
        return this == ACTIVE;
    }
    
    /**
     * 是否为取消状态
     */
    public boolean isCancelled() {
        return this == CANCELLED;
    }
    
    /**
     * 是否计入统计
     */
    public boolean countsInStatistics() {
        return this == ACTIVE;
    }
    
    /**
     * 是否可以切换状态
     */
    public boolean canToggle() {
        return true; // 所有状态都可以切换
    }
    
    /**
     * 获取切换后的状态
     */
    public InteractionStatusEnum toggle() {
        return this == ACTIVE ? CANCELLED : ACTIVE;
    }
    
    /**
     * 获取操作描述
     */
    public String getActionDescription(InteractionTypeEnum interactionType) {
        if (this == ACTIVE) {
            switch (interactionType) {
                case LIKE:
                    return "点赞";
                case FAVORITE:
                    return "收藏";
                case SHARE:
                    return "转发";
                case VIEW:
                    return "浏览";
                default:
                    return "互动";
            }
        } else {
            switch (interactionType) {
                case LIKE:
                    return "取消点赞";
                case FAVORITE:
                    return "取消收藏";
                case SHARE:
                    return "取消转发";
                default:
                    return "取消互动";
            }
        }
    }
    
    /**
     * 根据状态值获取枚举
     */
    public static InteractionStatusEnum fromStatus(int status) {
        for (InteractionStatusEnum statusEnum : values()) {
            if (statusEnum.status == status) {
                return statusEnum;
            }
        }
        throw new IllegalArgumentException("Unknown interaction status: " + status);
    }
    
    /**
     * 根据代码获取枚举
     */
    public static InteractionStatusEnum fromCode(String code) {
        for (InteractionStatusEnum statusEnum : values()) {
            if (statusEnum.code.equals(code)) {
                return statusEnum;
            }
        }
        throw new IllegalArgumentException("Unknown interaction status code: " + code);
    }
} 