package com.gig.collide.api.favorite.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 收藏夹类型枚举
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01  
 */
@Getter
@AllArgsConstructor
public enum FolderType {

    /**
     * 系统默认收藏夹
     */
    DEFAULT("DEFAULT", "默认收藏夹"),

    /**
     * 用户自定义收藏夹
     */
    CUSTOM("CUSTOM", "自定义收藏夹"),

    /**
     * 私密收藏夹
     */
    PRIVATE("PRIVATE", "私密收藏夹"),

    /**
     * 公开收藏夹
     */
    PUBLIC("PUBLIC", "公开收藏夹");

    /**
     * 类型代码
     */
    private final String code;

    /**
     * 类型描述
     */
    private final String description;
} 