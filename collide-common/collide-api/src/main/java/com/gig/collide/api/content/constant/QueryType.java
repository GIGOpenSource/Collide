package com.gig.collide.api.content.constant;

/**
 * 内容查询类型枚举
 * 用于内容列表查询的排序和筛选
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public enum QueryType {

    /**
     * 最新内容
     */
    LATEST("最新内容"),

    /**
     * 热门内容
     */
    HOT("热门内容"),

    /**
     * 推荐内容
     */
    RECOMMENDED("推荐内容"),

    /**
     * 关注的内容
     */
    FOLLOWING("关注的内容"),

    /**
     * 分类内容
     */
    CATEGORY("分类内容"),

    /**
     * 搜索结果
     */
    SEARCH("搜索结果");

    private final String description;

    QueryType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 