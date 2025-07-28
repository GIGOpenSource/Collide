package com.gig.collide.api.like.constant;

/**
 * 点赞目标类型枚举
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public enum TargetTypeEnum {

    /**
     * 内容
     */
    CONTENT("内容"),
    
    /**
     * 评论
     */
    COMMENT("评论"),
    
    /**
     * 用户
     */
    USER("用户"),
    
    /**
     * 商品
     */
    GOODS("商品");

    private final String description;

    TargetTypeEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查是否是内容相关类型
     */
    public boolean isContentRelated() {
        return this == CONTENT || this == COMMENT;
    }

    /**
     * 检查是否是用户相关类型
     */
    public boolean isUserRelated() {
        return this == USER;
    }

    /**
     * 检查是否是商品相关类型
     */
    public boolean isGoodsRelated() {
        return this == GOODS;
    }
} 