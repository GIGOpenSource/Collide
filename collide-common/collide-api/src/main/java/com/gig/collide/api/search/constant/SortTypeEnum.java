package com.gig.collide.api.search.constant;

/**
 * 排序类型枚举
 * 与search SQL设计保持一致
 * 
 * @author Collide Team  
 * @version 2.0
 * @since 2024-01-01
 */
public enum SortTypeEnum {

    /**
     * 相关度排序（默认）
     */
    RELEVANCE("相关度排序"),
    
    /**
     * 时间排序（最新）
     */
    TIME("时间排序"),
    
    /**
     * 热度排序
     */
    POPULARITY("热度排序"),
    
    /**
     * 质量评分排序
     */
    QUALITY("质量评分排序"),
    
    /**
     * 点赞数排序
     */
    LIKE_COUNT("点赞数排序"),
    
    /**
     * 浏览数排序
     */
    VIEW_COUNT("浏览数排序"),
    
    /**
     * 评论数排序
     */
    COMMENT_COUNT("评论数排序"),
    
    /**
     * 收藏数排序
     */
    FAVORITE_COUNT("收藏数排序"),
    
    /**
     * 权重排序
     */
    WEIGHT("权重排序"),
    
    /**
     * 综合评分排序
     */
    COMPREHENSIVE("综合评分排序");

    private final String description;

    SortTypeEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查是否为默认排序
     */
    public boolean isDefault() {
        return this == RELEVANCE;
    }

    /**
     * 检查是否为统计类排序
     */
    public boolean isStatistical() {
        return this == LIKE_COUNT || this == VIEW_COUNT || 
               this == COMMENT_COUNT || this == FAVORITE_COUNT;
    }

    /**
     * 检查是否为评分类排序
     */
    public boolean isScore() {
        return this == QUALITY || this == WEIGHT || 
               this == COMPREHENSIVE || this == RELEVANCE;
    }

    /**
     * 检查是否为时间相关排序
     */
    public boolean isTimeRelated() {
        return this == TIME;
    }

    /**
     * 获取排序字段名
     */
    public String getSortField() {
        return switch (this) {
            case RELEVANCE -> "relevance_score";
            case TIME -> "create_time";
            case POPULARITY -> "popularity_score";
            case QUALITY -> "quality_score";
            case LIKE_COUNT -> "like_count";
            case VIEW_COUNT -> "view_count";
            case COMMENT_COUNT -> "comment_count";
            case FAVORITE_COUNT -> "favorite_count";
            case WEIGHT -> "search_weight";
            case COMPREHENSIVE -> "comprehensive_score";
        };
    }
} 