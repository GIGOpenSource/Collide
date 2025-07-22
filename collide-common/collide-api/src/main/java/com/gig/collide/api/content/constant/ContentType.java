package com.gig.collide.api.content.constant;

/**
 * 内容类型枚举
 * 对应 t_content 表的 content_type 字段
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public enum ContentType {

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
     * 图文
     */
    ARTICLE("图文"),

    /**
     * 音频
     */
    AUDIO("音频");

    private final String description;

    ContentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 