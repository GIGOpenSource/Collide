package com.gig.collide.api.search.service;

import com.gig.collide.api.search.request.SearchRequest;
import com.gig.collide.api.search.request.SearchSuggestionRequest;
import com.gig.collide.api.search.response.SearchResponse;
import com.gig.collide.api.search.response.SearchSuggestionResponse;

import java.util.List;

/**
 * 搜索门面服务接口
 *
 * @author GIG Team
 */
public interface SearchFacadeService {

    /**
     * 执行搜索
     *
     * @param searchRequest 搜索请求
     * @return 搜索响应
     */
    SearchResponse search(SearchRequest searchRequest);

    /**
     * 获取搜索建议
     *
     * @param suggestionRequest 建议请求
     * @return 建议响应
     */
    SearchSuggestionResponse suggest(SearchSuggestionRequest suggestionRequest);

    /**
     * 获取热门搜索关键词
     *
     * @param limit 数量限制
     * @return 热门关键词列表
     */
    List<String> getHotKeywords(Integer limit);

    /**
     * 记录搜索行为
     *
     * @param keyword 搜索关键词
     * @param userId 用户ID
     * @param resultCount 搜索结果数量
     */
    void recordSearch(String keyword, Long userId, Long resultCount);
} 