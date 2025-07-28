package com.gig.collide.api.order.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 内容类型枚举
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum ContentTypeEnum {

    /**
     * 视频内容
     */
    VIDEO("VIDEO", "视频"),

    /**
     * 文章内容
     */
    ARTICLE("ARTICLE", "文章"),

    /**
     * 直播内容
     */
    LIVE("LIVE", "直播"),

    /**
     * 课程内容
     */
    COURSE("COURSE", "课程");

    /**
     * 枚举值
     */
    private final String code;

    /**
     * 枚举描述
     */
    private final String description;

    /**
     * 根据code获取枚举
     *
     * @param code 枚举值
     * @return 对应的枚举，如果不存在则返回null
     */
    public static ContentTypeEnum getByCode(String code) {
        for (ContentTypeEnum contentType : values()) {
            if (contentType.getCode().equals(code)) {
                return contentType;
            }
        }
        return null;
    }
} 