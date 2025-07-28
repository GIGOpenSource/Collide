package com.gig.collide.api.like.constant;

/**
 * 点赞类型枚举
 * 定义系统中可以被点赞的对象类型
 * 与TargetTypeEnum保持一致
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public enum LikeType {
    
    /**
     * 内容点赞 - 对小说、视频、文章等内容的点赞
     */
    CONTENT("CONTENT", "内容点赞"),
    
    /**
     * 评论点赞 - 对评论的点赞
     */
    COMMENT("COMMENT", "评论点赞"),
    
    /**
     * 用户点赞 - 对用户本身的点赞（关注推荐等）
     */
    USER("USER", "用户点赞"),
    
    /**
     * 商品点赞 - 对商品的点赞
     */
    GOODS("GOODS", "商品点赞");
    
    private final String code;
    private final String description;
    
    LikeType(String code, String description) {
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
    public static LikeType fromCode(String code) {
        for (LikeType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的点赞类型: " + code);
    }
} 