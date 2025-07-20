package com.gig.collide.api.tag.constant;

/**
 * 标签关联类型枚举
 * @author GIG
 */
public enum TagRelationType {

    /**
     * 用户标签（用户的个人标签）
     */
    USER("用户"),

    /**
     * 内容标签（文章、帖子等内容）
     */
    CONTENT("内容"),

    /**
     * 商品标签
     */
    GOODS("商品"),

    /**
     * 藏品标签
     */
    COLLECTION("藏品"),

    /**
     * 活动标签
     */
    ACTIVITY("活动"),

    /**
     * 话题标签
     */
    TOPIC("话题"),

    /**
     * 频道标签
     */
    CHANNEL("频道"),

    /**
     * 专题标签
     */
    SPECIAL("专题");

    private final String desc;

    TagRelationType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 是否为用户相关标签
     */
    public boolean isUserRelated() {
        return this == USER;
    }

    /**
     * 是否为内容相关标签
     */
    public boolean isContentRelated() {
        return this == CONTENT || this == TOPIC || this == SPECIAL;
    }

    /**
     * 是否为商品相关标签
     */
    public boolean isGoodsRelated() {
        return this == GOODS || this == COLLECTION;
    }
} 