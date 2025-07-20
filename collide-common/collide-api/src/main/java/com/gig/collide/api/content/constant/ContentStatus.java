package com.gig.collide.api.content.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 内容状态枚举
 */
@Getter
@AllArgsConstructor
public enum ContentStatus {

    /**
     * 草稿
     */
    DRAFT(1, "草稿"),

    /**
     * 待审核
     */
    PENDING_REVIEW(2, "待审核"),

    /**
     * 审核通过-已发布
     */
    PUBLISHED(3, "已发布"),

    /**
     * 审核拒绝
     */
    REJECTED(4, "审核拒绝"),

    /**
     * 已下架
     */
    UNPUBLISHED(5, "已下架"),

    /**
     * 已删除
     */
    DELETED(6, "已删除");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 描述
     */
    private final String desc;

    /**
     * 根据code获取枚举
     */
    public static ContentStatus getByCode(Integer code) {
        for (ContentStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 是否可以审核
     */
    public boolean canReview() {
        return this == PENDING_REVIEW;
    }

    /**
     * 是否已发布
     */
    public boolean isPublished() {
        return this == PUBLISHED;
    }

    /**
     * 是否可以编辑
     */
    public boolean canEdit() {
        return this == DRAFT || this == REJECTED;
    }
} 