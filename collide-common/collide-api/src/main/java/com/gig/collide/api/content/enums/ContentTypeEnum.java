package com.gig.collide.api.content.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 内容类型枚举
 * 定义系统支持的内容类型
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum ContentTypeEnum {

    /**
     * 小说类型
     */
    NOVEL("NOVEL", "小说", "支持章节连载的文字小说内容"),

    /**
     * 漫画类型
     */
    COMIC("COMIC", "漫画", "支持图片序列的漫画内容"),

    /**
     * 短视频类型
     */
    SHORT_VIDEO("SHORT_VIDEO", "短视频", "时长较短的视频内容"),

    /**
     * 长视频类型
     */
    LONG_VIDEO("LONG_VIDEO", "长视频", "时长较长的视频内容"),

    /**
     * 文章类型
     */
    ARTICLE("ARTICLE", "文章", "图文混排的文章内容"),

    /**
     * 音频类型
     */
    AUDIO("AUDIO", "音频", "音频类型的内容，如播客、音乐等");

    /**
     * 类型代码
     */
    private final String code;

    /**
     * 类型名称
     */
    private final String name;

    /**
     * 类型描述
     */
    private final String description;

    /**
     * 根据代码获取枚举值
     *
     * @param code 类型代码
     * @return 对应的枚举值，未找到返回null
     */
    public static ContentTypeEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (ContentTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 判断是否为视频类型
     *
     * @return true如果是视频类型
     */
    public boolean isVideoType() {
        return this == SHORT_VIDEO || this == LONG_VIDEO;
    }

    /**
     * 判断是否为文字类型
     *
     * @return true如果是文字类型
     */
    public boolean isTextType() {
        return this == NOVEL || this == ARTICLE;
    }

    /**
     * 判断是否为媒体类型
     *
     * @return true如果是媒体类型（视频、音频）
     */
    public boolean isMediaType() {
        return isVideoType() || this == AUDIO;
    }
} 