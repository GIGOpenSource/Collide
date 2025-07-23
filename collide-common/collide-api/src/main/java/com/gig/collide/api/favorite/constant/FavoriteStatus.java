package com.gig.collide.api.favorite.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 收藏状态枚举
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum FavoriteStatus {

    /**
     * 正常收藏
     */
    NORMAL("NORMAL", "正常"),

    /**
     * 已取消收藏
     */
    CANCELLED("CANCELLED", "已取消"),

    /**
     * 收藏内容已删除
     */
    TARGET_DELETED("TARGET_DELETED", "目标已删除"),

    /**
     * 收藏内容不可见（私有或屏蔽）
     */
    TARGET_INVISIBLE("TARGET_INVISIBLE", "目标不可见");

    /**
     * 状态代码
     */
    private final String code;

    /**
     * 状态描述
     */
    private final String description;
} 