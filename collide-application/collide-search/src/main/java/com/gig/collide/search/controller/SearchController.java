package com.gig.collide.search.controller;

import com.gig.collide.api.search.request.SearchAdvancedRequest;
import com.gig.collide.api.search.request.SearchUnifiedRequest;
import com.gig.collide.api.search.response.SearchHotKeywordsResponse;
import com.gig.collide.api.search.response.SearchHistoryQueryResponse;
import com.gig.collide.api.search.response.SearchSuggestionResponse;
import com.gig.collide.api.search.response.SearchUnifiedResponse;
import com.gig.collide.api.search.response.data.SearchResultInfo;
import com.gig.collide.base.response.BaseResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.search.facade.SearchFacadeServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 搜索控制器
 * 
 * @author Collide Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/search")
@Tag(name = "搜索服务", description = "提供全文搜索、用户搜索、内容搜索等功能")
public class SearchController {

    @Autowired
    private SearchFacadeServiceImpl searchFacadeService;

    /**
     * 统一搜索接口
     */
    @PostMapping("/unified")
    @Operation(summary = "统一搜索", description = "支持用户、内容、评论的综合搜索")
    public SearchUnifiedResponse unifiedSearch(@Valid @RequestBody SearchUnifiedRequest request) {
        return searchFacadeService.search(request);
    }

    /**
     * 分页搜索接口
     */
    @PostMapping("/page")
    @Operation(summary = "分页搜索", description = "支持分页的统一搜索接口")
    public PageResponse<SearchResultInfo> pageSearch(@Valid @RequestBody SearchUnifiedRequest request) {
        return searchFacadeService.pageSearch(request);
    }

    /**
     * 用户搜索
     */
    @PostMapping("/users")
    @Operation(summary = "用户搜索", description = "专门搜索用户信息")
    public SearchUnifiedResponse searchUsers(@Valid @RequestBody SearchUnifiedRequest request) {
        return searchFacadeService.searchUsers(request);
    }

    /**
     * 内容搜索
     */
    @PostMapping("/contents")
    @Operation(summary = "内容搜索", description = "专门搜索内容信息")
    public SearchUnifiedResponse searchContents(@Valid @RequestBody SearchUnifiedRequest request) {
        return searchFacadeService.searchContents(request);
    }

    /**
     * 评论搜索
     */
    @PostMapping("/comments")
    @Operation(summary = "评论搜索", description = "专门搜索评论信息")
    public SearchUnifiedResponse searchComments(@Valid @RequestBody SearchUnifiedRequest request) {
        return searchFacadeService.searchComments(request);
    }

    /**
     * 全文搜索（简化接口）
     */
    @GetMapping("/fulltext")
    @Operation(summary = "全文搜索", description = "简化的全文搜索接口")
    public SearchUnifiedResponse fullTextSearch(
            @RequestParam String keyword,
            @RequestParam(required = false) String contentType,
            @RequestParam(required = false) Long userId) {
        return searchFacadeService.fullTextSearch(keyword, contentType, userId);
    }

    /**
     * 高级搜索
     */
    @PostMapping("/advanced")
    @Operation(summary = "高级搜索", description = "支持更多过滤条件的高级搜索")
    public SearchUnifiedResponse advancedSearch(@Valid @RequestBody SearchAdvancedRequest request) {
        return searchFacadeService.advancedSearch(request);
    }

    /**
     * 获取搜索建议
     */
    @GetMapping("/suggestions")
    @Operation(summary = "获取搜索建议", description = "根据关键词获取搜索建议")
    public SearchSuggestionResponse getSuggestions(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "KEYWORD") String suggestionType,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        return searchFacadeService.getSuggestions(keyword, suggestionType, limit);
    }

    /**
     * 获取热门搜索关键词
     */
    @GetMapping("/hot-keywords")
    @Operation(summary = "获取热门搜索关键词", description = "获取当前热门的搜索关键词")
    public SearchHotKeywordsResponse getHotKeywords(
            @RequestParam(required = false, defaultValue = "20") Integer limit) {
        return searchFacadeService.getHotKeywords(limit);
    }

    /**
     * 获取用户搜索历史
     */
    @GetMapping("/history/{userId}")
    @Operation(summary = "获取用户搜索历史", description = "获取指定用户的搜索历史记录")
    public SearchHistoryQueryResponse getUserSearchHistory(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "50") Integer limit) {
        return searchFacadeService.getUserSearchHistory(userId, limit);
    }

    /**
     * 清空用户搜索历史
     */
    @DeleteMapping("/history/{userId}")
    @Operation(summary = "清空用户搜索历史", description = "清空指定用户的所有搜索历史记录")
    public BaseResponse clearUserSearchHistory(@PathVariable Long userId) {
        return searchFacadeService.clearUserSearchHistory(userId);
    }

    /**
     * 删除特定搜索历史
     */
    @DeleteMapping("/history/{userId}/keyword")
    @Operation(summary = "删除特定搜索历史", description = "删除用户指定关键词的搜索历史")
    public BaseResponse deleteSearchHistory(
            @PathVariable Long userId,
            @RequestParam String keyword) {
        return searchFacadeService.deleteSearchHistory(userId, keyword);
    }
} 