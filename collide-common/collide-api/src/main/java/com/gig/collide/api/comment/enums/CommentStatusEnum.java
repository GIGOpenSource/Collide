package com.gig.collide.api.comment.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 评论状态枚举
 * 定义评论的状态
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum CommentStatusEnum {

    /**
     * 正常状态
     */
    NORMAL("NORMAL", "正常", "评论正常显示"),

    /**
     * 隐藏状态
     */
    HIDDEN("HIDDEN", "隐藏", "评论被隐藏，不对外显示"),

    /**
     * 已删除状态
     */
    DELETED("DELETED", "已删除", "评论已被删除"),

    /**
     * 待审核状态
     */
    PENDING("PENDING", "待审核", "评论等待审核");

    /**
     * 状态代码
     */
    private final String code;

    /**
     * 状态名称
     */
    private final String name;

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 根据代码获取枚举值
     *
     * @param code 状态代码
     * @return 对应的枚举值，未找到返回null
     */
    public static CommentStatusEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (CommentStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否为正常状态
     *
     * @return true如果是正常状态
     */
    public boolean isNormal() {
        return this == NORMAL;
    }

    /**
     * 判断是否为可见状态
     *
     * @return true如果可见
     */
    public boolean isVisible() {
        return this == NORMAL;
    }

    /**
     * 判断是否为已删除状态
     *
     * @return true如果已删除
     */
    public boolean isDeleted() {
        return this == DELETED;
    }

    /**
     * 判断是否为待审核状态
     *
     * @return true如果待审核
     */
    public boolean isPending() {
        return this == PENDING;
    }

    /**
     * 判断是否为隐藏状态
     *
     * @return true如果隐藏
     */
    public boolean isHidden() {
        return this == HIDDEN;
    }
} 