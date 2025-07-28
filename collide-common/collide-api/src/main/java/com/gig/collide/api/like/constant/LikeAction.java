package com.gig.collide.api.like.constant;

/**
 * 点赞动作枚举
 * 定义用户可以执行的点赞相关操作
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public enum LikeAction {
    
    /**
     * 点赞操作
     */
    LIKE("LIKE", "点赞", 1),
    
    /**
     * 点踩操作
     */
    DISLIKE("DISLIKE", "点踩", -1),
    
    /**
     * 取消操作（取消点赞或点踩）
     */
    CANCEL("CANCEL", "取消", 0);
    
    private final String code;
    private final String description;
    private final int actionValue;
    
    LikeAction(String code, String description, int actionValue) {
        this.code = code;
        this.description = description;
        this.actionValue = actionValue;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getActionValue() {
        return actionValue;
    }
    
    /**
     * 根据代码获取枚举
     */
    public static LikeAction fromCode(String code) {
        for (LikeAction action : values()) {
            if (action.getCode().equals(code)) {
                return action;
            }
        }
        throw new IllegalArgumentException("未知的点赞动作: " + code);
    }
    
    /**
     * 根据动作值获取枚举
     */
    public static LikeAction fromActionValue(int actionValue) {
        for (LikeAction action : values()) {
            if (action.getActionValue() == actionValue) {
                return action;
            }
        }
        throw new IllegalArgumentException("未知的点赞动作值: " + actionValue);
    }
} 