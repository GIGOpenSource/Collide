package com.gig.collide.api.social.constant;

/**
 * 社交动态类型枚举
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
public enum PostTypeEnum {
    
    TEXT("TEXT", "文本动态"),
    IMAGE("IMAGE", "图片动态"), 
    VIDEO("VIDEO", "视频动态"),
    SHARE("SHARE", "转发动态");
    
    private final String code;
    private final String description;
    
    PostTypeEnum(String code, String description) {
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
     * 是否为媒体类型动态
     */
    public boolean isMediaType() {
        return this == IMAGE || this == VIDEO;
    }
    
    /**
     * 是否需要上传文件
     */
    public boolean requiresUpload() {
        return this == IMAGE || this == VIDEO;
    }
    
    /**
     * 是否为原创内容
     */
    public boolean isOriginalContent() {
        return this != SHARE;
    }
    
    /**
     * 获取最大内容长度限制
     */
    public int getMaxContentLength() {
        switch (this) {
            case TEXT:
                return 5000;
            case IMAGE:
                return 1000;
            case VIDEO:
                return 500;
            case SHARE:
                return 200;
            default:
                return 1000;
        }
    }
    
    /**
     * 根据代码获取枚举
     */
    public static PostTypeEnum fromCode(String code) {
        for (PostTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown post type code: " + code);
    }
} 