package com.gig.collide.api.like.constant;

/**
 * 点赞状态枚举
 * 定义点赞记录的状态
 * 
 * @author Collide
 * @since 1.0.0
 */
public enum LikeStatus {
    
    /**
     * 已点赞
     */
    LIKED("LIKED", "已点赞"),
    
    /**
     * 已取消点赞
     */
    UNLIKED("UNLIKED", "已取消点赞"),
    
    /**
     * 已点踩
     */
    DISLIKED("DISLIKED", "已点踩");
    
    private final String code;
    private final String description;
    
    LikeStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据代码获取枚举
     */
    public static LikeStatus fromCode(String code) {
        for (LikeStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的点赞状态: " + code);
    }
    
    /**
     * 根据点赞动作获取状态
     */
    public static LikeStatus fromAction(LikeAction action) {
        return switch (action) {
            case LIKE -> LIKED;
            case UNLIKE -> UNLIKED;
            case DISLIKE -> DISLIKED;
        };
    }
} 