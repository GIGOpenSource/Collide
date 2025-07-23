package com.gig.collide.api.social.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 社交动态状态枚举
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum SocialPostStatus {

    /**
     * 草稿状态
     */
    DRAFT("DRAFT", "草稿"),

    /**
     * 已发布
     */
    PUBLISHED("PUBLISHED", "已发布"),

    /**
     * 已隐藏
     */
    HIDDEN("HIDDEN", "已隐藏"),

    /**
     * 已删除
     */
    DELETED("DELETED", "已删除"),

    /**
     * 被举报
     */
    REPORTED("REPORTED", "被举报"),

    /**
     * 审核中
     */
    REVIEWING("REVIEWING", "审核中"),

    /**
     * 审核不通过
     */
    REJECTED("REJECTED", "审核不通过");

    /**
     * 状态代码
     */
    private final String code;

    /**
     * 状态描述
     */
    private final String description;
} 