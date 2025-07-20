package com.gig.collide.api.tag.constant;

/**
 * 标签类型枚举
 * @author GIG
 */
public enum TagType {

    /**
     * 系统标签（由系统预设）
     */
    SYSTEM("系统标签"),

    /**
     * 用户标签（用户创建）
     */
    USER("用户标签"),

    /**
     * 内容标签（内容相关）
     */
    CONTENT("内容标签"),

    /**
     * 分类标签（分类用途）
     */
    CATEGORY("分类标签"),

    /**
     * 个人标签（个人喜好）
     */
    PERSONAL("个人标签"),

    /**
     * 热门标签（热门推荐）
     */
    TRENDING("热门标签");

    private final String desc;

    TagType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 是否为系统标签
     */
    public boolean isSystem() {
        return this == SYSTEM;
    }

    /**
     * 是否为用户创建的标签
     */
    public boolean isUserCreated() {
        return this == USER || this == PERSONAL;
    }

    /**
     * 是否支持用户打标签
     */
    public boolean supportUserTag() {
        return this != SYSTEM;
    }
} 