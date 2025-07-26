package com.gig.collide.api.social.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 社交动态类型枚举
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum SocialPostType {

    /**
     * 纯文字动态
     */
    TEXT("TEXT", "文字动态"),

    /**
     * 图片动态
     */
    IMAGE("IMAGE", "图片动态"),

    /**
     * 视频动态
     */
    VIDEO("VIDEO", "视频动态"),

    /**
     * 链接分享
     */
    LINK("LINK", "链接分享"),

    /**
     * 文章分享
     */
    ARTICLE("ARTICLE", "文章分享"),

    /**
     * 音频动态
     */
    AUDIO("AUDIO", "音频动态"),

    /**
     * 投票动态
     */
    POLL("POLL", "投票动态"),

    /**
     * 位置动态
     */
    LOCATION("LOCATION", "位置动态"),

    /**
     * 转发动态
     */
    SHARE("SHARE", "转发动态");

    /**
     * 类型代码
     */
    private final String code;

    /**
     * 类型描述
     */
    private final String description;
} 