package com.gig.collide.api.content.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 内容审核状态枚举
 */
@Getter
@AllArgsConstructor
public enum ContentReviewStatus {

    /**
     * 待审核
     */
    PENDING(1, "待审核"),

    /**
     * 审核中
     */
    REVIEWING(2, "审核中"),

    /**
     * 审核通过
     */
    APPROVED(3, "审核通过"),

    /**
     * 审核拒绝
     */
    REJECTED(4, "审核拒绝"),

    /**
     * 需要重审
     */
    RE_REVIEW(5, "需要重审");

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
    public static ContentReviewStatus getByCode(Integer code) {
        for (ContentReviewStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 是否为终结状态
     */
    public boolean isFinalStatus() {
        return this == APPROVED || this == REJECTED;
    }
} 