package com.gig.collide.api.like.constant;

/**
 * 点赞动作枚举
 * 定义用户对对象的点赞操作类型
 * 
 * @author Collide
 * @since 1.0.0
 */
public enum LikeAction {
    
    /**
     * 点赞
     */
    LIKE(1, "点赞"),
    
    /**
     * 取消点赞
     */
    UNLIKE(0, "取消点赞"),
    
    /**
     * 点踩
     */
    DISLIKE(-1, "点踩");
    
    private final int value;
    private final String description;
    
    LikeAction(int value, String description) {
        this.value = value;
        this.description = description;
    }
    
    public int getValue() {
        return value;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据值获取枚举
     */
    public static LikeAction fromValue(int value) {
        for (LikeAction action : values()) {
            if (action.getValue() == value) {
                return action;
            }
        }
        throw new IllegalArgumentException("未知的点赞动作值: " + value);
    }
} 