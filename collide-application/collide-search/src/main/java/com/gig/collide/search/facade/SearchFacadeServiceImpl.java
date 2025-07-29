package com.gig.collide.search.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.search.SearchFacadeService;
import com.gig.collide.api.search.request.SearchHistoryQueryRequest;
import com.gig.collide.api.search.request.SearchRequest;
import com.gig.collide.api.search.response.SearchHistoryResponse;
import com.gig.collide.api.search.response.SearchResponse;
import com.gig.collide.api.search.response.HotSearchResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.search.domain.entity.SearchHistory;
import com.gig.collide.search.domain.entity.HotSearch;
import com.gig.collide.search.domain.service.SearchService;
import com.gig.collide.web.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 搜索门面服务实现类 - 简洁版
 *
 * @author GIG Team
 * @version 2.0.0
 */
@Slf4j
@DubboService(version = "1.0.0")
public class SearchFacadeServiceImpl implements SearchFacadeService {

    @Autowired
    private SearchService searchService;

    @Override
    public Result<SearchResponse> search(SearchRequest request) {
        try {
            long startTime = System.currentTimeMillis();
            
            List<Object> results = searchService.search(
                request.getKeyword(),
                request.getSearchType(),
                request.getUserId(),
                request.getPageNum(),
                request.getPageSize(),
                request.getSortBy()
            );
            
            long duration = System.currentTimeMillis() - startTime;
            
            SearchResponse response = new SearchResponse();
            response.setKeyword(request.getKeyword());
            response.setSearchType(request.getSearchType());
            response.setTotalCount(results.size());
            response.setDuration(duration);
            response.setHasMore(results.size() >= request.getPageSize());
            
            // 获取搜索建议
            List<String> suggestions = searchService.getSearchSuggestions(request.getKeyword(), 5);
            response.setSuggestions(suggestions);
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("搜索失败", e);
            return Result.error("SEARCH_ERROR", "搜索失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<SearchHistoryResponse>> getSearchHistory(SearchHistoryQueryRequest request) {
        try {
            IPage<SearchHistory> page = searchService.getSearchHistory(
                request.getUserId(),
                request.getSearchType(),
                request.getKeyword(),
                request.getCurrentPage(),
                request.getPageSize(),
                request.getSortBy(),
                request.getSortDirection()
            );
            
            List<SearchHistoryResponse> responses = page.getRecords().stream()
                    .map(this::convertToHistoryResponse)
                    .collect(Collectors.toList());
            
            PageResponse<SearchHistoryResponse> result = new PageResponse<>();
            result.setDatas(responses);
            result.setTotal((int) page.getTotal());
            result.setCurrentPage(request.getCurrentPage());
            result.setPageSize(request.getPageSize());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取搜索历史失败", e);
            return Result.error("SEARCH_HISTORY_ERROR", "获取搜索历史失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> clearSearchHistory(Long userId) {
        try {
            searchService.clearSearchHistory(userId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("清空搜索历史失败", e);
            return Result.error("CLEAR_HISTORY_ERROR", "清空搜索历史失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> deleteSearchHistory(Long historyId) {
        try {
            searchService.deleteSearchHistory(historyId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("删除搜索历史失败", e);
            return Result.error("DELETE_HISTORY_ERROR", "删除搜索历史失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<HotSearchResponse>> getHotSearchKeywords(Integer limit) {
        try {
            List<HotSearch> hotSearches = searchService.getHotSearchKeywords(limit);
            List<HotSearchResponse> responses = hotSearches.stream()
                    .map(this::convertToHotSearchResponse)
                    .collect(Collectors.toList());
            
            // 设置排名
            for (int i = 0; i < responses.size(); i++) {
                responses.get(i).setRank(i + 1);
            }
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("获取热门搜索失败", e);
            return Result.error("HOT_SEARCH_ERROR", "获取热门搜索失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<String>> getSearchSuggestions(String keyword, Integer limit) {
        try {
            List<String> suggestions = searchService.getSearchSuggestions(keyword, limit);
            return Result.success(suggestions);
        } catch (Exception e) {
            log.error("获取搜索建议失败", e);
            return Result.error("SUGGESTION_ERROR", "获取搜索建议失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<HotSearchResponse>> getHotKeywordsByType(String searchType, Integer limit) {
        try {
            List<HotSearch> hotSearches = searchService.getHotKeywordsByType(searchType, limit);
            List<HotSearchResponse> responses = hotSearches.stream()
                    .map(this::convertToHotSearchResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("根据类型获取热搜失败", e);
            return Result.error("HOT_SEARCH_ERROR", "根据类型获取热搜失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<SearchHistoryResponse>> getUserSearchPreferences(Long userId) {
        try {
            List<SearchHistory> preferences = searchService.getUserSearchPreferences(userId);
            List<SearchHistoryResponse> responses = preferences.stream()
                    .map(this::convertToHistoryResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("获取用户搜索偏好失败", e);
            return Result.error("PREFERENCE_ERROR", "获取用户搜索偏好失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> updateHotSearchTrend(String keyword, Double trendScore) {
        try {
            searchService.updateHotSearchTrend(keyword, trendScore);
            return Result.success(null);
        } catch (Exception e) {
            log.error("更新热搜趋势失败", e);
            return Result.error("UPDATE_TREND_ERROR", "更新热搜趋势失败: " + e.getMessage());
        }
    }

    /**
     * 转换为搜索历史响应对象
     */
    private SearchHistoryResponse convertToHistoryResponse(SearchHistory history) {
        SearchHistoryResponse response = new SearchHistoryResponse();
        BeanUtils.copyProperties(history, response);
        return response;
    }

    /**
     * 转换为热搜响应对象
     */
    private HotSearchResponse convertToHotSearchResponse(HotSearch hotSearch) {
        HotSearchResponse response = new HotSearchResponse();
        BeanUtils.copyProperties(hotSearch, response);
        return response;
    }
} 