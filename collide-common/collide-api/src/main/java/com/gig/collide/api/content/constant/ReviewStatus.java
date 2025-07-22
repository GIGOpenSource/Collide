package com.gig.collide.api.content.constant;

/**
 * 审核状态枚举
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
     * 已通过
     */
    APPROVED("已通过"),

    /**
     * 已拒绝
     */
    REJECTED("已拒绝"),

    /**
     * 需要修改
     */
    NEED_REVISION("需要修改");

    private final String description;

    ReviewStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 判断是否为终态状态
     *
     * @return true if final status
     */
    public boolean isFinalStatus() {
        return this == APPROVED || this == REJECTED;
    }

    /**
     * 判断是否通过审核
     *
     * @return true if approved
     */
    public boolean isApproved() {
        return this == APPROVED;
    }
} 