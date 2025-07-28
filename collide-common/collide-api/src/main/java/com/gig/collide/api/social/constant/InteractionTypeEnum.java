package com.gig.collide.api.social.constant;

/**
 * 社交互动类型枚举
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
public enum InteractionTypeEnum {
    
    LIKE("LIKE", "点赞", 1.0),
    FAVORITE("FAVORITE", "收藏", 1.5),
    SHARE("SHARE", "转发", 3.0),
    VIEW("VIEW", "浏览", 0.1);
    
    private final String code;
    private final String description;
    private final double weight;
    
    InteractionTypeEnum(String code, String description, double weight) {
        this.code = code;
        this.description = description;
        this.weight = weight;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public double getWeight() {
        return weight;
    }
    
    /**
     * 是否为主动互动（需要用户显式操作）
     */
    public boolean isActiveInteraction() {
        return this != VIEW;
    }
    
    /**
     * 是否为被动互动（系统自动记录）
     */
    public boolean isPassiveInteraction() {
        return this == VIEW;
    }
    
    /**
     * 是否计入热度分数
     */
    public boolean countsTowardHotScore() {
        return true; // 所有互动都计入热度
    }
    
    /**
     * 是否可撤销
     */
    public boolean isRevocable() {
        return this == LIKE || this == FAVORITE;
    }
    
    /**
     * 是否需要通知作者
     */
    public boolean shouldNotifyAuthor() {
        return this == LIKE || this == FAVORITE || this == SHARE;
    }
    
    /**
     * 是否是内容传播类型
     */
    public boolean isContentSpreadType() {
        return this == SHARE;
    }
    
    /**
     * 是否是收藏类型
     */
    public boolean isCollectionType() {
        return this == FAVORITE;
    }
    
    /**
     * 获取互动动作描述
     */
    public String getActionDescription() {
        switch (this) {
            case LIKE:
                return "赞了这条动态";
            case FAVORITE:
                return "收藏了这条动态";
            case SHARE:
                return "转发了这条动态";
            case VIEW:
                return "浏览了这条动态";
            default:
                return "与这条动态进行了互动";
        }
    }
    
    /**
     * 根据代码获取枚举
     */
    public static InteractionTypeEnum fromCode(String code) {
        for (InteractionTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown interaction type code: " + code);
    }
} 