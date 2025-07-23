package com.gig.collide.api.favorite.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 收藏类型枚举
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum FavoriteType {

    /**
     * 内容收藏 - 收藏视频、图片、文章等内容
     */
    CONTENT("CONTENT", "内容收藏"),

    /**
     * 用户收藏 - 收藏感兴趣的用户/博主
     */
    USER("USER", "用户收藏"),

    /**
     * 评论收藏 - 收藏精彩评论
     */
    COMMENT("COMMENT", "评论收藏"),

    /**
     * 话题收藏 - 收藏话题标签
     */
    TOPIC("TOPIC", "话题收藏"),

    /**
     * 动态收藏 - 收藏朋友圈动态
     */
    SOCIAL("SOCIAL", "动态收藏");

    /**
     * 类型代码
     */
    private final String code;

    /**
     * 类型描述
     */
    private final String description;
} 