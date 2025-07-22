package com.gig.collide.api.content.enums;

/**
 * 内容类型枚举
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public enum ContentType {
    /**
     * 文本内容
     */
    TEXT("text", "文本内容"),
    
    /**
     * 图片内容
     */
    IMAGE("image", "图片内容"),
    
    /**
     * 视频内容
     */
    VIDEO("video", "视频内容"),
    
    /**
     * 音频内容
     */
    AUDIO("audio", "音频内容"),
    
    /**
     * 链接分享
     */
    LINK("link", "链接分享"),
    
    /**
     * 混合内容
     */
    MIXED("mixed", "混合内容");

    private final String code;
    private final String description;

    ContentType(String code, String description) {
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
    public static ContentType fromCode(String code) {
        for (ContentType type : ContentType.values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown ContentType code: " + code);
    }

    /**
     * 判断是否为媒体内容
     */
    public boolean isMediaContent() {
        return this == IMAGE || this == VIDEO || this == AUDIO;
    }
} 