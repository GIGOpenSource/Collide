package com.gig.collide.api.search.constant;

/**
 * 内容类型枚举
 * 与search SQL设计保持一致
 * 
 * @author Collide Team  
 * @version 2.0
 * @since 2024-01-01
 */
public enum ContentTypeEnum {

    /**
     * 小说
     */
    NOVEL("小说"),
    
    /**
     * 漫画
     */
    COMIC("漫画"),
    
    /**
     * 短视频
     */
    SHORT_VIDEO("短视频"),
    
    /**
     * 长视频
     */
    LONG_VIDEO("长视频"),
    
    /**
     * 文章
     */
    ARTICLE("文章"),
    
    /**
     * 音频
     */
    AUDIO("音频"),
    
    /**
     * 动态
     */
    DYNAMIC("动态"),
    
    /**
     * 图片
     */
    IMAGE("图片");

    private final String description;

    ContentTypeEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查是否为视频类型
     */
    public boolean isVideo() {
        return this == SHORT_VIDEO || this == LONG_VIDEO;
    }

    /**
     * 检查是否为文本类型
     */
    public boolean isText() {
        return this == NOVEL || this == ARTICLE || this == DYNAMIC;
    }

    /**
     * 检查是否为多媒体类型
     */
    public boolean isMultimedia() {
        return this == SHORT_VIDEO || this == LONG_VIDEO || this == AUDIO || this == IMAGE;
    }

    /**
     * 检查是否支持全文搜索
     */
    public boolean supportFullTextSearch() {
        return this == NOVEL || this == ARTICLE || this == DYNAMIC;
    }

    /**
     * 检查是否支持标签搜索
     */
    public boolean supportTagSearch() {
        return true; // 所有内容类型都支持标签搜索
    }
} 