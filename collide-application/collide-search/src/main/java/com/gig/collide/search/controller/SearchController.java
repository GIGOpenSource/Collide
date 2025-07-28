package com.gig.collide.search.controller;

import com.gig.collide.api.search.request.SearchHistoryQueryRequest;
import com.gig.collide.api.search.request.SearchRequest;
import com.gig.collide.api.search.response.SearchHistoryResponse;
import com.gig.collide.api.search.response.SearchResponse;
import com.gig.collide.api.search.response.HotSearchResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.search.domain.service.SearchService;
import com.gig.collide.web.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 搜索控制器 - 简洁版
 *
 * @author GIG Team
 * @version 2.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 执行搜索
     */
    @PostMapping
    public Result<SearchResponse> search(@Valid @RequestBody SearchRequest request) {
        try {
            return Result.success(null);
        } catch (Exception e) {
            log.error("搜索失败", e);
            return Result.error("SEARCH_ERROR", "搜索失败: " + e.getMessage());
        }
    }

    /**
     * 获取搜索历史
     */
    @GetMapping("/history")
    public Result<PageResponse<SearchHistoryResponse>> getSearchHistory(@Valid SearchHistoryQueryRequest request) {
        try {
            return Result.success(null);
        } catch (Exception e) {
            log.error("获取搜索历史失败", e);
            return Result.error("SEARCH_HISTORY_ERROR", "获取搜索历史失败: " + e.getMessage());
        }
    }

    /**
     * 清空搜索历史
     */
    @DeleteMapping("/history/user/{userId}")
    public Result<Void> clearSearchHistory(@PathVariable Long userId) {
        try {
            searchService.clearSearchHistory(userId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("清空搜索历史失败", e);
            return Result.error("CLEAR_HISTORY_ERROR", "清空搜索历史失败: " + e.getMessage());
        }
    }

    /**
     * 删除指定搜索历史
     */
    @DeleteMapping("/history/{historyId}")
    public Result<Void> deleteSearchHistory(@PathVariable Long historyId) {
        try {
            searchService.deleteSearchHistory(historyId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("删除搜索历史失败", e);
            return Result.error("DELETE_HISTORY_ERROR", "删除搜索历史失败: " + e.getMessage());
        }
    }

    /**
     * 获取热门搜索
     */
    @GetMapping("/hot")
    public Result<List<HotSearchResponse>> getHotSearchKeywords(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            return Result.success(null);
        } catch (Exception e) {
            log.error("获取热门搜索失败", e);
            return Result.error("HOT_SEARCH_ERROR", "获取热门搜索失败: " + e.getMessage());
        }
    }

    /**
     * 获取搜索建议
     */
    @GetMapping("/suggestions")
    public Result<List<String>> getSearchSuggestions(@RequestParam String keyword,
                                                    @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<String> suggestions = searchService.getSearchSuggestions(keyword, limit);
            return Result.success(suggestions);
        } catch (Exception e) {
            log.error("获取搜索建议失败", e);
            return Result.error("SUGGESTION_ERROR", "获取搜索建议失败: " + e.getMessage());
        }
    }

    /**
     * 根据类型获取热搜
     */
    @GetMapping("/hot/type/{searchType}")
    public Result<List<HotSearchResponse>> getHotKeywordsByType(@PathVariable String searchType,
                                                               @RequestParam(defaultValue = "10") Integer limit) {
        try {
            return Result.success(null);
        } catch (Exception e) {
            log.error("根据类型获取热搜失败", e);
            return Result.error("HOT_SEARCH_ERROR", "根据类型获取热搜失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户搜索偏好
     */
    @GetMapping("/preferences/{userId}")
    public Result<List<SearchHistoryResponse>> getUserSearchPreferences(@PathVariable Long userId) {
        try {
            return Result.success(null);
        } catch (Exception e) {
            log.error("获取用户搜索偏好失败", e);
            return Result.error("PREFERENCE_ERROR", "获取用户搜索偏好失败: " + e.getMessage());
        }
    }

    /**
     * 更新热搜趋势分数
     */
    @PutMapping("/hot/trend")
    public Result<Void> updateHotSearchTrend(@RequestParam String keyword,
                                            @RequestParam Double trendScore) {
        try {
            searchService.updateHotSearchTrend(keyword, trendScore);
            return Result.success(null);
        } catch (Exception e) {
            log.error("更新热搜趋势失败", e);
            return Result.error("UPDATE_TREND_ERROR", "更新热搜趋势失败: " + e.getMessage());
        }
    }
} 