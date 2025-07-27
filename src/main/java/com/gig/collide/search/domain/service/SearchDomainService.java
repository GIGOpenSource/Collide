package com.gig.collide.search.domain.service;

import com.gig.collide.api.search.request.SearchRequest;
import com.gig.collide.api.search.request.SearchSuggestionRequest;
import com.gig.collide.api.search.response.SearchResponse;
import com.gig.collide.api.search.response.SearchSuggestionResponse;

/**
 * 搜索领域服务接口
 * 定义搜索业务核心逻辑
 *
 * @author Collide Team
 * @version 1.0
 */
public interface SearchDomainService {

    /**
     * 综合搜索
     * 支持用户、内容、评论的搜索
     *
     * @param request 搜索请求
     * @return 搜索响应
     */
    SearchResponse search(SearchRequest request);

    /**
     * 生成搜索建议
     * 包括关键词建议、用户建议、标签建议等
     *
     * @param request 搜索建议请求
     * @return 搜索建议响应
     */
    SearchSuggestionResponse generateSearchSuggestions(SearchSuggestionRequest request);

    /**
     * 获取热门关键词
     *
     * @param request 请求参数
     * @return 热门关键词响应
     */
    SearchSuggestionResponse getHotKeywords(SearchSuggestionRequest request);
} 