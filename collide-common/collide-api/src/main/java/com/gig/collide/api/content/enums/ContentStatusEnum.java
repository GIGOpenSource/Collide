package com.gig.collide.api.content.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 内容状态枚举
 * 定义内容的发布状态
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum ContentStatusEnum {

    /**
     * 草稿状态
     */
    DRAFT("DRAFT", "草稿", "内容尚未发布，处于编辑状态"),

    /**
     * 待审核状态
     */
    PENDING("PENDING", "待审核", "内容已提交，等待审核"),

    /**
     * 已发布状态
     */
    PUBLISHED("PUBLISHED", "已发布", "内容已通过审核并发布"),

    /**
     * 审核拒绝状态
     */
    REJECTED("REJECTED", "审核拒绝", "内容审核未通过"),

    /**
     * 下线状态
     */
    OFFLINE("OFFLINE", "已下线", "内容已从前台下线，但未删除");

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
    public static ContentStatusEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (ContentStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否为已发布状态
     *
     * @return true如果已发布
     */
    public boolean isPublished() {
        return this == PUBLISHED;
    }

    /**
     * 判断是否为可编辑状态
     *
     * @return true如果可编辑
     */
    public boolean isEditable() {
        return this == DRAFT || this == REJECTED;
    }

    /**
     * 判断是否为待处理状态
     *
     * @return true如果待处理
     */
    public boolean isPending() {
        return this == PENDING;
    }

    /**
     * 判断是否为终态（不可再变更）
     *
     * @return true如果为终态
     */
    public boolean isFinalState() {
        return this == OFFLINE;
    }
} 