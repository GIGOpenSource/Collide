package com.gig.collide.api.tag.constant;

/**
 * 标签状态枚举
 * @author GIG
 */
public enum TagStatus {

    /**
     * 正常状态
     */
    ACTIVE("正常"),

    /**
     * 禁用状态
     */
    DISABLED("禁用"),

    /**
     * 草稿状态
     */
    DRAFT("草稿"),

    /**
     * 审核中
     */
    REVIEWING("审核中"),

    /**
     * 审核拒绝
     */
    REJECTED("审核拒绝");

    private final String desc;

    TagStatus(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 是否为可用状态
     */
    public boolean isAvailable() {
        return this == ACTIVE;
    }

    /**
     * 是否为草稿状态
     */
    public boolean isDraft() {
        return this == DRAFT;
    }

    /**
     * 是否需要审核
     */
    public boolean needsReview() {
        return this == REVIEWING;
    }
} 