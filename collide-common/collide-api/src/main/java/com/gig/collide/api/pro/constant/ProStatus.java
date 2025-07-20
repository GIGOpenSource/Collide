package com.gig.collide.api.pro.constant;

/**
 * 付费用户状态
 * @author GIG
 */
public enum ProStatus {

    /**
     * 普通用户
     */
    NORMAL,

    /**
     * 付费用户（有效期内）
     */
    ACTIVE,

    /**
     * 付费用户（已过期）
     */
    EXPIRED,

    /**
     * 付费用户（已暂停）
     */
    SUSPENDED,

    /**
     * 付费用户（已取消）
     */
    CANCELLED;
} 