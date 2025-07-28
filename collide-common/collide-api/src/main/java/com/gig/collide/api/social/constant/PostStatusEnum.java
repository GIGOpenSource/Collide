package com.gig.collide.api.social.constant;

/**
 * 社交动态状态枚举
 * @author GIG
 * @version 1.0.0 
 * @since 2024-01-01
 */
public enum PostStatusEnum {
    
    DRAFT("DRAFT", "草稿"),
    PUBLISHED("PUBLISHED", "已发布"),
    DELETED("DELETED", "已删除");
    
    private final String code;
    private final String description;
    
    PostStatusEnum(String code, String description) {
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
     * 是否可见状态
     */
    public boolean isVisible() {
        return this == PUBLISHED;
    }
    
    /**
     * 是否可编辑状态
     */
    public boolean isEditable() {
        return this == DRAFT;
    }
    
    /**
     * 是否已删除状态
     */
    public boolean isDeleted() {
        return this == DELETED;
    }
    
    /**
     * 是否可以发布
     */
    public boolean canPublish() {
        return this == DRAFT;
    }
    
    /**
     * 是否可以删除
     */
    public boolean canDelete() {
        return this == DRAFT || this == PUBLISHED;
    }
    
    /**
     * 是否可以修改
     */
    public boolean canModify() {
        return this == DRAFT;
    }
    
    /**
     * 是否可以互动（点赞、评论、分享等）
     */
    public boolean canInteract() {
        return this == PUBLISHED;
    }
    
    /**
     * 根据代码获取枚举
     */
    public static PostStatusEnum fromCode(String code) {
        for (PostStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown post status code: " + code);
    }
} 