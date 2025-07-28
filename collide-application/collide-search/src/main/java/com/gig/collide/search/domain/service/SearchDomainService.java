package com.gig.collide.search.domain.service;

import com.gig.collide.api.search.response.SearchResponse;
import com.gig.collide.api.search.response.SearchSuggestionResponse;

import java.util.List;

/**
 * 搜索领域服务接口
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
public interface SearchDomainService {

    /**
     * 执行搜索
     */
    SearchResponse search(SearchRequest searchRequest);

    /**
     * 获取搜索建议
     */
    SearchSuggestionResponse suggest(SearchSuggestionRequest suggestionRequest);

    /**
     * 获取热门关键词
     */
    List<String> getHotKeywords(Integer limit);

    /**
     * 记录搜索行为
     */
    void recordSearch(String keyword, Long userId, Long resultCount);
} 