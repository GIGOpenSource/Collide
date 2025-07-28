package com.gig.collide.api.search.constant;

/**
 * 搜索类型枚举
 * 与search模块SQL设计保持一致
 * 
 * @author Collide Team  
 * @version 2.0
 * @since 2024-01-01
 */
public enum SearchTypeEnum {

    /**
     * 综合搜索
     */
    ALL("综合搜索"),
    
    /**
     * 用户搜索
     */
    USER("用户搜索"),
    
    /**
     * 内容搜索
     */
    CONTENT("内容搜索"),
    
    /**
     * 评论搜索
     */
    COMMENT("评论搜索");

    private final String description;

    SearchTypeEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查是否为内容相关搜索
     */
    public boolean isContentRelated() {
        return this == CONTENT || this == COMMENT;
    }

    /**
     * 检查是否支持全文搜索
     */
    public boolean supportFullTextSearch() {
        return this == ALL || this == CONTENT || this == COMMENT;
    }
} 