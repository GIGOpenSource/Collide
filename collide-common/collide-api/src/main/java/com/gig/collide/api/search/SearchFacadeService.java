package com.gig.collide.api.search;

import com.gig.collide.api.search.request.SearchHistoryQueryRequest;
import com.gig.collide.api.search.request.SearchRequest;
import com.gig.collide.api.search.response.SearchHistoryResponse;
import com.gig.collide.api.search.response.SearchResponse;
import com.gig.collide.api.search.response.HotSearchResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import java.util.List;

/**
 * 搜索门面服务接口 - 简洁版
 * 基于简洁版SQL设计（t_search_history, t_hot_search）
 *
 * @author GIG Team
 * @version 2.0.0
 */
public interface SearchFacadeService {

    /**
     * 执行搜索
     */
    Result<SearchResponse> search(SearchRequest request);

    /**
     * 获取用户搜索历史
     */
    Result<PageResponse<SearchHistoryResponse>> getSearchHistory(SearchHistoryQueryRequest request);

    /**
     * 清空用户搜索历史
     */
    Result<Void> clearSearchHistory(Long userId);

    /**
     * 删除指定搜索历史
     */
    Result<Void> deleteSearchHistory(Long historyId);

    /**
     * 获取热门搜索关键词
     */
    Result<List<HotSearchResponse>> getHotSearchKeywords(Integer limit);

    /**
     * 获取搜索建议/自动补全
     */
    Result<List<String>> getSearchSuggestions(String keyword, Integer limit);

    /**
     * 根据搜索类型获取热门关键词
     */
    Result<List<HotSearchResponse>> getHotKeywordsByType(String searchType, Integer limit);

    /**
     * 获取用户搜索偏好分析
     */
    Result<List<SearchHistoryResponse>> getUserSearchPreferences(Long userId);

    /**
     * 更新热搜关键词趋势分数
     */
    Result<Void> updateHotSearchTrend(String keyword, Double trendScore);
} 