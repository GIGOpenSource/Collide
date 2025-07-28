package com.gig.collide.api.comment.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 点赞类型枚举
 * 定义评论的点赞类型
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum LikeTypeEnum {

    /**
     * 点赞
     */
    LIKE("LIKE", "点赞", "对评论表示赞同"),

    /**
     * 点踩
     */
    DISLIKE("DISLIKE", "点踩", "对评论表示反对");

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
    public static LikeTypeEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (LikeTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 判断是否为点赞
     *
     * @return true如果是点赞
     */
    public boolean isLike() {
        return this == LIKE;
    }

    /**
     * 判断是否为点踩
     *
     * @return true如果是点踩
     */
    public boolean isDislike() {
        return this == DISLIKE;
    }
} 