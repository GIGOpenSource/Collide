package com.gig.collide.api.artist.constant;

/**
 * 博主状态枚举
 * @author GIG
 */
public enum ArtistStatus {

    /**
     * 申请中
     */
    APPLYING("申请中"),

    /**
     * 审核中
     */
    REVIEWING("审核中"),

    /**
     * 审核通过，已激活
     */
    ACTIVE("已激活"),

    /**
     * 审核拒绝
     */
    REJECTED("审核拒绝"),

    /**
     * 已暂停
     */
    SUSPENDED("已暂停"),

    /**
     * 已禁用
     */
    DISABLED("已禁用"),

    /**
     * 已注销
     */
    CANCELLED("已注销");

    private final String desc;

    ArtistStatus(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 是否为活跃状态
     */
    public boolean isActive() {
        return this == ACTIVE;
    }

    /**
     * 是否需要审核
     */
    public boolean needsReview() {
        return this == APPLYING || this == REVIEWING;
    }

    /**
     * 是否可以进行博主活动
     */
    public boolean canOperate() {
        return this == ACTIVE;
    }

    /**
     * 是否为终结状态
     */
    public boolean isFinalStatus() {
        return this == REJECTED || this == DISABLED || this == CANCELLED;
    }
} 