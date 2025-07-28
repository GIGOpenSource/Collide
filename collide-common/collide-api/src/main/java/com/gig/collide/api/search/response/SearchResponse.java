package com.gig.collide.api.search.response;

import lombok.Data;

import java.util.List;

/**
 * 搜索响应 - 简洁版
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
public class SearchResponse {

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 搜索类型
     */
    private String searchType;

    /**
     * 总结果数量
     */
    private Integer totalCount;

    /**
     * 搜索结果列表
     */
    private List<SearchResultItem> results;

    /**
     * 搜索建议
     */
    private List<String> suggestions;

    /**
     * 是否有更多结果
     */
    private Boolean hasMore;

    /**
     * 搜索耗时（毫秒）
     */
    private Long duration;

    /**
     * 搜索结果项
     */
    @Data
    public static class SearchResultItem {
        private Long id;
        private String title;
        private String content;
        private String type;
        private String thumbnail;
        private Double score;
        private String highlight;
    }
} 