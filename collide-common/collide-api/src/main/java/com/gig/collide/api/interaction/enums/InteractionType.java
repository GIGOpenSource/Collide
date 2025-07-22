package com.gig.collide.api.interaction.enums;

/**
 * 互动类型枚举
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public enum InteractionType {
    /**
     * 点赞
     */
    LIKE("like", "点赞"),
    
    /**
     * 取消点赞
     */
    UNLIKE("unlike", "取消点赞"),
    
    /**
     * 收藏
     */
    FAVORITE("favorite", "收藏"),
    
    /**
     * 取消收藏
     */
    UNFAVORITE("unfavorite", "取消收藏"),
    
    /**
     * 评论
     */
    COMMENT("comment", "评论"),
    
    /**
     * 删除评论
     */
    DELETE_COMMENT("delete_comment", "删除评论"),
    
    /**
     * 转发
     */
    REPOST("repost", "转发"),
    
    /**
     * 取消转发
     */
    UNREPOST("unrepost", "取消转发"),
    
    /**
     * 分享
     */
    SHARE("share", "分享"),
    
    /**
     * 关注
     */
    FOLLOW("follow", "关注"),
    
    /**
     * 取消关注
     */
    UNFOLLOW("unfollow", "取消关注"),
    
    /**
     * 举报
     */
    REPORT("report", "举报");

    private final String code;
    private final String description;

    InteractionType(String code, String description) {
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
     * 根据code获取枚举值
     */
    public static InteractionType fromCode(String code) {
        for (InteractionType type : InteractionType.values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown InteractionType code: " + code);
    }

    /**
     * 判断是否为正向互动（增加）
     */
    public boolean isPositiveAction() {
        return this == LIKE || this == FAVORITE || this == COMMENT || 
               this == REPOST || this == SHARE || this == FOLLOW;
    }

    /**
     * 判断是否为负向互动（取消）
     */
    public boolean isNegativeAction() {
        return this == UNLIKE || this == UNFAVORITE || this == DELETE_COMMENT || 
               this == UNREPOST || this == UNFOLLOW;
    }
} 