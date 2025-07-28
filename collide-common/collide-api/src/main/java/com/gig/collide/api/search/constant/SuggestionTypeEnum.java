package com.gig.collide.api.search.constant;

/**
 * 搜索建议类型枚举
 * 与search SQL设计保持一致
 * 
 * @author Collide Team  
 * @version 2.0
 * @since 2024-01-01
 */
public enum SuggestionTypeEnum {

    /**
     * 关键词建议
     */
    KEYWORD("关键词建议"),
    
    /**
     * 用户建议
     */
    USER("用户建议"),
    
    /**
     * 标签建议
     */
    TAG("标签建议"),
    
    /**
     * 内容建议
     */
    CONTENT("内容建议"),
    
    /**
     * 分类建议
     */
    CATEGORY("分类建议"),
    
    /**
     * 热门搜索建议
     */
    HOT("热门搜索建议"),
    
    /**
     * 历史搜索建议
     */
    HISTORY("历史搜索建议");

    private final String description;

    SuggestionTypeEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查是否为实体相关建议
     */
    public boolean isEntityRelated() {
        return this == USER || this == CONTENT || this == TAG || this == CATEGORY;
    }

    /**
     * 检查是否为动态建议
     */
    public boolean isDynamic() {
        return this == HOT || this == HISTORY;
    }

    /**
     * 检查是否支持高亮显示
     */
    public boolean supportHighlight() {
        return this == KEYWORD || this == USER || this == CONTENT;
    }
} 