package com.gig.collide.business.controller;

import com.gig.collide.api.search.request.SearchRequest;
import com.gig.collide.api.search.request.SearchSuggestionRequest;
import com.gig.collide.api.search.response.SearchResponse;
import com.gig.collide.api.search.response.SearchSuggestionResponse;
import com.gig.collide.api.search.service.SearchFacadeService;
import com.gig.collide.base.response.SingleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

// import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 搜索控制器
 * 
 * @author GIG Team
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Tag(name = "搜索服务", description = "内容搜索相关接口")
public class SearchController {

    private final SearchFacadeService searchFacadeService;

    @GetMapping
    @Operation(summary = "综合搜索", description = "支持用户、内容、评论的综合搜索")
    public SingleResponse<SearchResponse> search(
            @Parameter(description = "搜索关键词", required = true)
            @RequestParam String keyword,
            
            @Parameter(description = "搜索类型：ALL-综合搜索, USER-用户搜索, CONTENT-内容搜索, COMMENT-评论搜索")
            @RequestParam(defaultValue = "ALL") String searchType,
            
            @Parameter(description = "内容类型过滤：NOVEL-小说, COMIC-漫画, SHORT_VIDEO-短视频, LONG_VIDEO-长视频")
            @RequestParam(required = false) String contentType,
            
            @Parameter(description = "排序方式：RELEVANCE-相关度, TIME-时间, POPULARITY-热度")
            @RequestParam(defaultValue = "RELEVANCE") String sortBy,
            
            @Parameter(description = "页码")
            @RequestParam(defaultValue = "1") Integer pageNum,
            
            @Parameter(description = "每页大小")
            @RequestParam(defaultValue = "10") Integer pageSize,
            
            @Parameter(description = "是否高亮显示")
            @RequestParam(defaultValue = "true") Boolean highlight,
            
            @Parameter(description = "搜索时间范围（天数），0表示不限制")
            @RequestParam(defaultValue = "0") Integer timeRange,
            
            @Parameter(description = "最小点赞数过滤")
            @RequestParam(defaultValue = "0") Integer minLikeCount,
            
            @Parameter(description = "是否只搜索已发布的内容")
            @RequestParam(defaultValue = "true") Boolean onlyPublished) {

        try {
            SearchRequest searchRequest = SearchRequest.builder()
                    .keyword(keyword)
                    .searchType(searchType)
                    .contentType(contentType)
                    .sortBy(sortBy)
                    .pageNum(pageNum)
                    .pageSize(pageSize)
                    .highlight(highlight)
                    .timeRange(timeRange)
                    .minLikeCount(minLikeCount)
                    .onlyPublished(onlyPublished)
                    .build();

            SearchResponse response = searchFacadeService.search(searchRequest);
            return SingleResponse.of(response);

        } catch (Exception e) {
            log.error("搜索请求失败：keyword={}", keyword, e);
            return SingleResponse.fail("SEARCH_ERROR", "搜索失败，请稍后重试");
        }
    }

    @PostMapping
    @Operation(summary = "高级搜索", description = "使用POST方式提交复杂搜索请求")
    public SingleResponse<SearchResponse> advancedSearch(@RequestBody SearchRequest searchRequest) {
        try {
            SearchResponse response = searchFacadeService.search(searchRequest);
            return SingleResponse.of(response);

        } catch (Exception e) {
            log.error("高级搜索请求失败：{}", searchRequest.getKeyword(), e);
            return SingleResponse.fail("SEARCH_ERROR", "搜索失败，请稍后重试");
        }
    }

    @GetMapping("/suggestions")
    @Operation(summary = "搜索建议", description = "根据输入的关键词获取搜索建议")
    public SingleResponse<SearchSuggestionResponse> getSuggestions(
            @Parameter(description = "搜索关键词", required = true)
            @RequestParam String keyword,
            
            @Parameter(description = "建议类型：KEYWORD-关键词建议, USER-用户建议, TAG-标签建议")
            @RequestParam(defaultValue = "KEYWORD") String suggestionType,
            
            @Parameter(description = "建议数量")
            @RequestParam(defaultValue = "10") Integer limit) {

        try {
            SearchSuggestionRequest suggestionRequest = SearchSuggestionRequest.builder()
                    .keyword(keyword)
                    .suggestionType(suggestionType)
                    .limit(limit)
                    .build();

            SearchSuggestionResponse response = searchFacadeService.suggest(suggestionRequest);
            return SingleResponse.of(response);

        } catch (Exception e) {
            log.error("获取搜索建议失败：keyword={}", keyword, e);
            return SingleResponse.fail("SUGGESTION_ERROR", "获取建议失败，请稍后重试");
        }
    }

    @PostMapping("/suggestions")
    @Operation(summary = "搜索建议（POST）", description = "使用POST方式获取搜索建议")
    public SingleResponse<SearchSuggestionResponse> getSuggestionsPost(@RequestBody SearchSuggestionRequest suggestionRequest) {
        try {
            SearchSuggestionResponse response = searchFacadeService.suggest(suggestionRequest);
            return SingleResponse.of(response);

        } catch (Exception e) {
            log.error("获取搜索建议失败：{}", suggestionRequest.getKeyword(), e);
            return SingleResponse.fail("SUGGESTION_ERROR", "获取建议失败，请稍后重试");
        }
    }

    @GetMapping("/hot-keywords")
    @Operation(summary = "热门搜索关键词", description = "获取热门搜索关键词列表")
    public SingleResponse<List<String>> getHotKeywords(
            @Parameter(description = "数量限制")
            @RequestParam(defaultValue = "10") Integer limit) {

        try {
            List<String> hotKeywords = searchFacadeService.getHotKeywords(limit);
            return SingleResponse.of(hotKeywords);

        } catch (Exception e) {
            log.error("获取热门关键词失败", e);
            return SingleResponse.fail("HOT_KEYWORDS_ERROR", "获取热门关键词失败，请稍后重试");
        }
    }

    @PostMapping("/record")
    @Operation(summary = "记录搜索行为", description = "记录用户的搜索行为用于统计分析")
    public SingleResponse<String> recordSearch(
            @Parameter(description = "搜索关键词", required = true)
            @RequestParam String keyword,
            
            @Parameter(description = "用户ID")
            @RequestParam(required = false) Long userId,
            
            @Parameter(description = "搜索结果数量")
            @RequestParam(defaultValue = "0") Long resultCount) {

        try {
            searchFacadeService.recordSearch(keyword, userId, resultCount);
            return SingleResponse.of("记录成功");

        } catch (Exception e) {
            log.error("记录搜索行为失败：keyword={}, userId={}", keyword, userId, e);
            return SingleResponse.fail("RECORD_ERROR", "记录失败");
        }
    }

    @GetMapping("/stats")
    @Operation(summary = "搜索统计信息", description = "获取搜索相关的统计信息")
    public SingleResponse<Map<String, Object>> getSearchStats() {
        try {
            Map<String, Object> stats = Map.of(
                "hotKeywords", searchFacadeService.getHotKeywords(5),
                "searchTips", List.of(
                    "使用引号搜索精确短语，如：\"Spring Boot\"",
                    "使用空格分隔多个关键词",
                    "可以按内容类型筛选搜索结果",
                    "支持按时间范围和热度排序"
                ),
                "supportedTypes", List.of("ALL", "USER", "CONTENT", "COMMENT"),
                "supportedSorts", List.of("RELEVANCE", "TIME", "POPULARITY")
            );
            
            return SingleResponse.of(stats);

        } catch (Exception e) {
            log.error("获取搜索统计信息失败", e);
            return SingleResponse.fail("STATS_ERROR", "获取统计信息失败");
        }
    }
} 