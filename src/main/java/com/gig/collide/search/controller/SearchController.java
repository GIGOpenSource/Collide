package com.gig.collide.search.controller;

import com.gig.collide.api.search.request.SearchRequest;
import com.gig.collide.api.search.request.SearchSuggestionRequest;
import com.gig.collide.api.search.response.SearchResponse;
import com.gig.collide.api.search.response.SearchSuggestionResponse;
import com.gig.collide.base.result.Result;
import com.gig.collide.search.domain.service.SearchDomainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 搜索控制器
 * 提供RESTful搜索API接口
 *
 * @author Collide Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Tag(name = "搜索管理", description = "搜索相关API")
public class SearchController {

    private final SearchDomainService searchDomainService;

    @Operation(summary = "综合搜索", description = "支持用户、内容、评论的综合搜索")
    @PostMapping("/comprehensive")
    public Result<SearchResponse> search(@Validated @RequestBody SearchRequest request) {
        try {
            log.info("执行综合搜索：关键词={}，类型={}", request.getKeyword(), request.getSearchType());
            SearchResponse response = searchDomainService.search(request);
            return Result.success(response);
        } catch (Exception e) {
            log.error("综合搜索失败：request={}", request, e);
            return Result.error("搜索失败，请稍后重试");
        }
    }

    @Operation(summary = "搜索建议", description = "获取搜索关键词建议和热门关键词")
    @PostMapping("/suggestions")
    public Result<SearchSuggestionResponse> getSearchSuggestions(@Validated @RequestBody SearchSuggestionRequest request) {
        try {
            log.info("获取搜索建议：关键词={}", request.getKeyword());
            SearchSuggestionResponse response = searchDomainService.generateSearchSuggestions(request);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取搜索建议失败：request={}", request, e);
            return Result.error("获取搜索建议失败，请稍后重试");
        }
    }

    @Operation(summary = "热门搜索", description = "获取热门搜索关键词")
    @GetMapping("/hot-keywords")
    public Result<SearchSuggestionResponse> getHotKeywords(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            log.info("获取热门搜索关键词，限制：{}", limit);
            SearchSuggestionRequest request = SearchSuggestionRequest.builder()
                    .keyword("")
                    .limit(limit)
                    .build();
            SearchSuggestionResponse response = searchDomainService.getHotKeywords(request);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取热门搜索关键词失败", e);
            return Result.error("获取热门搜索关键词失败，请稍后重试");
        }
    }
} 