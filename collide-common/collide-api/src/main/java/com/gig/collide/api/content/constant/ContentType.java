package com.gig.collide.api.content.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 内容类型枚举
 */
@Getter
@AllArgsConstructor
public enum ContentType {

    /**
     * 小说
     */
    NOVEL("novel", "小说"),

    /**
     * 漫画
     */
    COMIC("comic", "漫画"),

    /**
     * 短视频
     */
    SHORT_VIDEO("short_video", "短视频"),

    /**
     * 长视频
     */
    LONG_VIDEO("long_video", "长视频");

    /**
     * 编码
     */
    private final String code;

    /**
     * 描述
     */
    private final String desc;

    /**
     * 根据code获取枚举
     */
    public static ContentType getByCode(String code) {
        for (ContentType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
} 