package com.gig.collide.search.domain;

import com.gig.collide.api.search.response.SearchResponse;
import com.gig.collide.api.search.response.SearchSuggestionResponse;
import com.gig.collide.api.search.response.data.SearchResult;
import com.gig.collide.api.search.response.data.SuggestionItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 搜索领域服务
 * 
 * @author Collide Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SearchDomainService {

    private final UserSearchRepository userSearchRepository;
    private final ContentSearchRepository contentSearchRepository;
    private final CommentSearchRepository commentSearchRepository;
    private final SearchRecordRepository searchRecordRepository;

    /**
     * 执行搜索（添加缓存支持）
     */
    @Cacheable(value = "search_results", key = "#request.keyword + ':' + #request.searchType + ':' + #request.pageNum + ':' + #request.pageSize", 
               condition = "#request.keyword != null && #request.keyword.length() > 1", unless = "#result.totalCount == 0")
    public SearchResponse search(SearchRequest request) {
        long startTime = System.currentTimeMillis();
        
        log.info("执行搜索，关键词：{}，类型：{}", request.getKeyword(), request.getSearchType());

        List<SearchResult> results = new ArrayList<>();
        long totalCount = 0;
        Map<String, Long> statistics = new HashMap<>();

        try {
            // 根据搜索类型执行不同的搜索策略
            switch (request.getSearchType().toUpperCase()) {
                case "USER":
                    results = searchUsers(request);
                    totalCount = countUsers(request);
                    break;
                case "CONTENT":
                    results = searchContent(request);
                    totalCount = countContent(request);
                    break;
                case "COMMENT":
                    results = searchComments(request);
                    totalCount = countComments(request);
                    break;
                case "ALL":
                default:
                    results = searchAll(request);
                    totalCount = results.size(); // 综合搜索使用结果列表大小
                    statistics = getSearchStatistics(request);
                    break;
            }

            // 排序处理
            results = sortResults(results, request.getSortBy());
            
            // 分页处理
            int fromIndex = (request.getPageNum() - 1) * request.getPageSize();
            int toIndex = Math.min(fromIndex + request.getPageSize(), results.size());
            if (fromIndex < results.size()) {
                results = results.subList(fromIndex, toIndex);
            } else {
                results = new ArrayList<>();
            }

        } catch (Exception e) {
            log.error("搜索执行失败", e);
            results = new ArrayList<>();
            totalCount = 0;
        }

        long searchTime = System.currentTimeMillis() - startTime;
        int totalPages = (int) Math.ceil((double) totalCount / request.getPageSize());

        // 异步记录搜索行为（不影响响应性能）
        recordSearchAsync(request.getKeyword(), null, totalCount);

        return SearchResponse.builder()
                .keyword(request.getKeyword())
                .searchType(request.getSearchType())
                .totalCount(totalCount)
                .searchTime(searchTime)
                .pageNum(request.getPageNum())
                .pageSize(request.getPageSize())
                .totalPages(totalPages)
                .hasNext(request.getPageNum() < totalPages)
                .results(results)
                .statistics(statistics)
                .suggestions(generateSearchSuggestions(request.getKeyword()))
                .relatedSearches(generateRelatedSearches(request.getKeyword()))
                .build();
    }

    /**
     * 搜索用户（添加缓存）
     */
    @Cacheable(value = "user_search", key = "#request.keyword + ':' + #request.pageNum + ':' + #request.pageSize", 
               condition = "#request.keyword != null && #request.keyword.length() > 1")
    private List<SearchResult> searchUsers(SearchRequest request) {
        return userSearchRepository.searchUsers(
                request.getKeyword(),
                request.getPageNum(),
                request.getPageSize(),
                request.getHighlight()
        );
    }

    /**
     * 搜索内容（添加缓存）
     */
    @Cacheable(value = "content_search", key = "#request.keyword + ':' + #request.contentType + ':' + #request.pageNum + ':' + #request.pageSize", 
               condition = "#request.keyword != null && #request.keyword.length() > 1")
    private List<SearchResult> searchContent(SearchRequest request) {
        return contentSearchRepository.searchContent(
                request.getKeyword(),
                request.getContentType(),
                request.getOnlyPublished(),
                request.getTimeRange(),
                request.getMinLikeCount(),
                request.getPageNum(),
                request.getPageSize(),
                request.getHighlight()
        );
    }

    /**
     * 搜索评论（添加缓存）
     */
    @Cacheable(value = "comment_search", key = "#request.keyword + ':' + #request.timeRange + ':' + #request.pageNum + ':' + #request.pageSize", 
               condition = "#request.keyword != null && #request.keyword.length() > 1")
    private List<SearchResult> searchComments(SearchRequest request) {
        return commentSearchRepository.searchComments(
                request.getKeyword(),
                request.getTimeRange(),
                request.getPageNum(),
                request.getPageSize(),
                request.getHighlight()
        );
    }

    /**
     * 综合搜索
     */
    private List<SearchResult> searchAll(SearchRequest request) {
        List<SearchResult> allResults = new ArrayList<>();
        
        // 搜索用户（限制数量）
        SearchRequest userRequest = SearchRequest.builder()
                .keyword(request.getKeyword())
                .searchType("USER")
                .pageNum(1)
                .pageSize(5)
                .highlight(request.getHighlight())
                .build();
        allResults.addAll(searchUsers(userRequest));

        // 搜索内容（限制数量）
        SearchRequest contentRequest = SearchRequest.builder()
                .keyword(request.getKeyword())
                .searchType("CONTENT")
                .contentType(request.getContentType())
                .onlyPublished(request.getOnlyPublished())
                .timeRange(request.getTimeRange())
                .minLikeCount(request.getMinLikeCount())
                .pageNum(1)
                .pageSize(10)
                .highlight(request.getHighlight())
                .build();
        allResults.addAll(searchContent(contentRequest));

        // 搜索评论（限制数量）
        SearchRequest commentRequest = SearchRequest.builder()
                .keyword(request.getKeyword())
                .searchType("COMMENT")
                .timeRange(request.getTimeRange())
                .pageNum(1)
                .pageSize(5)
                .highlight(request.getHighlight())
                .build();
        allResults.addAll(searchComments(commentRequest));

        return allResults;
    }

    /**
     * 统计各类型搜索结果数量（添加缓存）
     */
    @Cacheable(value = "search_statistics", key = "#request.keyword + ':stats'", 
               condition = "#request.keyword != null && #request.keyword.length() > 1")
    private Map<String, Long> getSearchStatistics(SearchRequest request) {
        Map<String, Long> statistics = new HashMap<>();
        
        try {
            statistics.put("userCount", countUsers(request));
            statistics.put("contentCount", countContent(request));
            statistics.put("commentCount", countComments(request));
        } catch (Exception e) {
            log.error("获取搜索统计失败", e);
        }
        
        return statistics;
    }

    /**
     * 统计用户搜索结果数量（添加缓存）
     */
    @Cacheable(value = "user_count", key = "#request.keyword", 
               condition = "#request.keyword != null && #request.keyword.length() > 1")
    private long countUsers(SearchRequest request) {
        return userSearchRepository.countUsers(request.getKeyword());
    }

    /**
     * 统计内容搜索结果数量（添加缓存）
     */
    @Cacheable(value = "content_count", key = "#request.keyword + ':' + #request.contentType", 
               condition = "#request.keyword != null && #request.keyword.length() > 1")
    private long countContent(SearchRequest request) {
        return contentSearchRepository.countContent(
                request.getKeyword(),
                request.getContentType(),
                request.getOnlyPublished(),
                request.getTimeRange(),
                request.getMinLikeCount()
        );
    }

    /**
     * 统计评论搜索结果数量（添加缓存）
     */
    @Cacheable(value = "comment_count", key = "#request.keyword + ':' + #request.timeRange", 
               condition = "#request.keyword != null && #request.keyword.length() > 1")
    private long countComments(SearchRequest request) {
        return commentSearchRepository.countComments(
                request.getKeyword(),
                request.getTimeRange()
        );
    }

    /**
     * 搜索结果排序
     */
    private List<SearchResult> sortResults(List<SearchResult> results, String sortBy) {
        if (results.isEmpty()) {
            return results;
        }

        switch (sortBy.toUpperCase()) {
            case "TIME":
                return results.stream()
                        .sorted((a, b) -> {
                            LocalDateTime timeA = a.getPublishTime() != null ? a.getPublishTime() : a.getCreateTime();
                            LocalDateTime timeB = b.getPublishTime() != null ? b.getPublishTime() : b.getCreateTime();
                            return timeB.compareTo(timeA); // 降序
                        })
                        .collect(Collectors.toList());
                        
            case "POPULARITY":
                return results.stream()
                        .sorted((a, b) -> {
                            long popularityA = calculatePopularity(a);
                            long popularityB = calculatePopularity(b);
                            return Long.compare(popularityB, popularityA); // 降序
                        })
                        .collect(Collectors.toList());
                        
            case "RELEVANCE":
            default:
                return results.stream()
                        .sorted((a, b) -> Double.compare(
                                b.getRelevanceScore() != null ? b.getRelevanceScore() : 0.0,
                                a.getRelevanceScore() != null ? a.getRelevanceScore() : 0.0
                        ))
                        .collect(Collectors.toList());
        }
    }

    /**
     * 计算热度分数
     */
    private long calculatePopularity(SearchResult result) {
        if (result.getStatistics() == null) {
            return 0;
        }
        
        SearchResult.StatisticsInfo stats = result.getStatistics();
        return (stats.getLikeCount() != null ? stats.getLikeCount() : 0) * 3
                + (stats.getViewCount() != null ? stats.getViewCount() : 0)
                + (stats.getCommentCount() != null ? stats.getCommentCount() : 0) * 2
                + (stats.getFavoriteCount() != null ? stats.getFavoriteCount() : 0) * 5;
    }

    /**
     * 生成搜索建议（添加缓存）
     */
    @Cacheable(value = "search_suggestions", key = "#keyword", 
               condition = "#keyword != null && #keyword.length() > 0")
    private List<String> generateSearchSuggestions(String keyword) {
        try {
            // 基于关键词获取相关建议
            List<SuggestionItem> suggestions = searchRecordRepository.getKeywordSuggestions(keyword, 5);
            return suggestions.stream()
                    .map(SuggestionItem::getText)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("生成搜索建议失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 生成相关搜索
     */
    private List<String> generateRelatedSearches(String keyword) {
        // TODO: 基于搜索模式和用户行为生成相关搜索
        return new ArrayList<>();
    }

    /**
     * 获取搜索建议（添加缓存）
     */
    @Cacheable(value = "suggestions", key = "#request.keyword + ':' + #request.suggestionType + ':' + #request.limit", 
               condition = "#request.keyword != null && #request.keyword.length() > 0")
    public SearchSuggestionResponse getSuggestions(SearchSuggestionRequest request) {
        log.info("获取搜索建议，关键词：{}，类型：{}", request.getKeyword(), request.getSuggestionType());

        List<SuggestionItem> suggestions = new ArrayList<>();
        
        try {
            switch (request.getSuggestionType().toUpperCase()) {
                case "USER":
                    suggestions = getUserSuggestions(request.getKeyword(), request.getLimit());
                    break;
                case "TAG":
                    suggestions = getTagSuggestions(request.getKeyword(), request.getLimit());
                    break;
                case "KEYWORD":
                default:
                    suggestions = getKeywordSuggestions(request.getKeyword(), request.getLimit());
                    break;
            }
        } catch (Exception e) {
            log.error("获取搜索建议失败", e);
        }

        return SearchSuggestionResponse.builder()
                .keyword(request.getKeyword())
                .suggestionType(request.getSuggestionType())
                .suggestions(suggestions)
                .hotKeywords(getHotKeywords(10))
                .build();
    }

    /**
     * 获取用户建议（添加缓存）
     */
    @Cacheable(value = "user_suggestions", key = "#keyword + ':' + #limit", 
               condition = "#keyword != null && #keyword.length() > 0")
    private List<SuggestionItem> getUserSuggestions(String keyword, Integer limit) {
        return userSearchRepository.getUserSuggestions(keyword, limit);
    }

    /**
     * 获取标签建议（添加缓存）
     */
    @Cacheable(value = "tag_suggestions", key = "#keyword + ':' + #limit", 
               condition = "#keyword != null && #keyword.length() > 0")
    private List<SuggestionItem> getTagSuggestions(String keyword, Integer limit) {
        return contentSearchRepository.getTagSuggestions(keyword, limit);
    }

    /**
     * 获取关键词建议（添加缓存）
     */
    @Cacheable(value = "keyword_suggestions", key = "#keyword + ':' + #limit", 
               condition = "#keyword != null && #keyword.length() > 0")
    private List<SuggestionItem> getKeywordSuggestions(String keyword, Integer limit) {
        return searchRecordRepository.getKeywordSuggestions(keyword, limit);
    }

    /**
     * 获取热门搜索关键词（添加缓存）
     */
    @Cacheable(value = "hot_keywords", key = "#limit", unless = "#result.size() == 0")
    public List<String> getHotKeywords(Integer limit) {
        try {
            return searchRecordRepository.getHotKeywords(limit != null ? limit : 10);
        } catch (Exception e) {
            log.error("获取热门关键词失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 记录搜索行为（无缓存，每次都记录）
     */
    public void recordSearch(String keyword, Long userId, Long resultCount) {
        try {
            if (StringUtils.hasText(keyword)) {
                searchRecordRepository.recordSearch(keyword, userId, resultCount);
                log.debug("记录搜索行为：关键词={}，用户ID={}，结果数={}", keyword, userId, resultCount);
            }
        } catch (Exception e) {
            log.error("记录搜索行为失败", e);
        }
    }

    /**
     * 异步记录搜索行为
     */
    private void recordSearchAsync(String keyword, Long userId, Long resultCount) {
        // 在实际项目中，这里可以使用 @Async 注解或消息队列来异步处理
        try {
            recordSearch(keyword, userId, resultCount);
        } catch (Exception e) {
            log.error("异步记录搜索行为失败", e);
        }
    }
} 