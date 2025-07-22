package com.gig.collide.api.content.constant;

/**
 * 内容审核状态枚举
 * 对应 t_content 表的 review_status 字段
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public enum ReviewStatus {

    /**
     * 待审核
     */
    PENDING("待审核"),

    /**
     * 审核通过
     */
    APPROVED("审核通过"),

    /**
     * 审核拒绝
     */
    REJECTED("审核拒绝");

    private final String description;

    ReviewStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 判断是否审核通过
     *
     * @return true if approved
     */
    public boolean isApproved() {
        return this == APPROVED;
    }

    /**
     * 判断是否需要审核
     *
     * @return true if needs review
     */
    public boolean needsReview() {
        return this == PENDING;
    }
} 