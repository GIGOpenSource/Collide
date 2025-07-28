package com.gig.collide.api.comment.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 评论类型枚举
 * 定义系统支持的评论类型
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum CommentTypeEnum {

    /**
     * 内容评论
     */
    CONTENT("CONTENT", "内容评论", "对内容（小说、漫画、视频等）的评论"),

    /**
     * 动态评论
     */
    DYNAMIC("DYNAMIC", "动态评论", "对用户动态的评论");

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
    public static CommentTypeEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (CommentTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 判断是否为内容评论
     *
     * @return true如果是内容评论
     */
    public boolean isContentComment() {
        return this == CONTENT;
    }

    /**
     * 判断是否为动态评论
     *
     * @return true如果是动态评论
     */
    public boolean isDynamicComment() {
        return this == DYNAMIC;
    }
} 