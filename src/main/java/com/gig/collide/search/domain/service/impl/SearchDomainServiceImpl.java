package com.gig.collide.search.domain.service.impl;

import com.gig.collide.api.search.request.SearchRequest;
import com.gig.collide.api.search.request.SearchSuggestionRequest;
import com.gig.collide.api.search.response.SearchResponse;
import com.gig.collide.api.search.response.SearchSuggestionResponse;
import com.gig.collide.api.search.response.data.SearchResultItem;
import com.gig.collide.api.search.response.data.SuggestionItem;
import com.gig.collide.search.domain.repository.CommentSearchRepository;
import com.gig.collide.search.domain.repository.ContentSearchRepository;
import com.gig.collide.search.domain.repository.SearchRecordRepository;
import com.gig.collide.search.domain.repository.UserSearchRepository;
import com.gig.collide.search.domain.service.SearchDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 搜索领域服务实现
 * 基于标准化架构实现搜索业务逻辑
 * 支持缓存和异步处理
 *
 * @author Collide Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SearchDomainServiceImpl implements SearchDomainService {

    private final UserSearchRepository userSearchRepository;
    private final ContentSearchRepository contentSearchRepository;
    private final CommentSearchRepository commentSearchRepository;
    private final SearchRecordRepository searchRecordRepository;

    @Override
    @Cacheable(value = "search_results", key = "'search:' + #request.keyword + ':' + #request.searchType + ':' + #request.pageNum + ':' + #request.pageSize", unless = "#result.totalCount == 0")
    public SearchResponse search(SearchRequest request) {
        log.info("执行搜索：关键词={}，类型={}", request.getKeyword(), request.getSearchType());
        
        if (!StringUtils.hasText(request.getKeyword())) {
            return buildEmptyResponse(request);
        }

        long startTime = System.currentTimeMillis();
        
        try {
            SearchResponse response = performSearch(request);
            
            // 异步记录搜索行为  
            recordSearchAsync(request.getKeyword(), request.getUserId(), response.getTotalCount());
            
            long endTime = System.currentTimeMillis();
            response.setSearchTime(endTime - startTime);
            
            log.info("搜索完成：关键词={}，结果数={}，耗时={}ms", 
                    request.getKeyword(), response.getTotalCount(), response.getSearchTime());
            
            return response;
            
        } catch (Exception e) {
            log.error("搜索异常：关键词={}", request.getKeyword(), e);
            return buildEmptyResponse(request);
        }
    }

    @Override
    @Cacheable(value = "search_suggestions", key = "'suggestions:' + #request.keyword + ':' + #request.limit")
    public SearchSuggestionResponse generateSearchSuggestions(SearchSuggestionRequest request) {
        log.info("生成搜索建议：关键词={}, 限制={}", request.getKeyword(), request.getLimit());

        List<SuggestionItem> suggestions = new ArrayList<>();
        
        try {
            // 获取关键词建议
            List<SuggestionItem> keywordSuggestions = searchRecordRepository
                    .getKeywordSuggestions(request.getKeyword(), request.getLimit());
            suggestions.addAll(keywordSuggestions);
             
            // 获取用户建议
            List<SuggestionItem> userSuggestions = userSearchRepository
                    .getUserSuggestions(request.getKeyword(), Math.min(5, request.getLimit()));
            suggestions.addAll(userSuggestions);
            
            // 获取标签建议
            List<SuggestionItem> tagSuggestions = contentSearchRepository
                    .getTagSuggestions(request.getKeyword(), Math.min(5, request.getLimit()));
            suggestions.addAll(tagSuggestions);
            
            return SearchSuggestionResponse.builder()
                    .keyword(request.getKeyword())
                    .suggestions(suggestions)
                    .totalCount((long) suggestions.size())
                    .build();
                    
        } catch (Exception e) {
            log.error("生成搜索建议失败：keyword={}", request.getKeyword(), e);
            return SearchSuggestionResponse.builder()
                    .keyword(request.getKeyword())
                    .suggestions(new ArrayList<>())
                    .totalCount(0L)
                    .build();
        }
    }

    @Override
    @Cacheable(value = "hot_keywords", key = "'hot:' + #request.limit")
    public SearchSuggestionResponse getHotKeywords(SearchSuggestionRequest request) {
        log.info("获取热门关键词，限制：{}", request.getLimit());
        
        try {
            List<String> hotKeywords = searchRecordRepository.getHotKeywords(request.getLimit());
            
            List<SuggestionItem> suggestions = hotKeywords.stream()
                    .map(keyword -> SuggestionItem.builder()
                            .text(keyword)
                            .type("HOT_KEYWORD")
                            .build())
                    .toList();
                    
            return SearchSuggestionResponse.builder()
                    .keyword("")
                    .suggestions(suggestions)
                    .totalCount((long) suggestions.size())
                    .build();
                    
        } catch (Exception e) {
            log.error("获取热门关键词失败", e);
            return SearchSuggestionResponse.builder()
                    .keyword("")
                    .suggestions(new ArrayList<>())
                    .totalCount(0L)
                    .build();
        }
    }

    /**
     * 执行具体的搜索逻辑
     */
    private SearchResponse performSearch(SearchRequest request) {
        String searchType = request.getSearchType();
        int offset = (request.getPageNum() - 1) * request.getPageSize();
        int limit = request.getPageSize();
        
        List<SearchResultItem> results = new ArrayList<>();
        long totalCount = 0;
        
        switch (searchType.toUpperCase()) {
            case "USER":
                results = userSearchRepository.searchUsers(request.getKeyword(), offset, limit);
                totalCount = userSearchRepository.countUsers(request.getKeyword());
                break;
                
            case "CONTENT":
                results = contentSearchRepository.searchContent(request.getKeyword(), offset, limit);
                totalCount = contentSearchRepository.countContent(request.getKeyword());
                break;
                
            case "COMMENT":
                results = commentSearchRepository.searchComments(request.getKeyword(), offset, limit);
                totalCount = commentSearchRepository.countComments(request.getKeyword());
                break;
                
            case "ALL":
            default:
                // 综合搜索：用户、内容、评论各取一部分
                int itemsPerType = limit / 3;
                
                List<SearchResultItem> users = userSearchRepository.searchUsers(request.getKeyword(), 0, itemsPerType);
                List<SearchResultItem> contents = contentSearchRepository.searchContent(request.getKeyword(), 0, itemsPerType);
                List<SearchResultItem> comments = commentSearchRepository.searchComments(request.getKeyword(), 0, itemsPerType);
                
                results.addAll(users);
                results.addAll(contents);
                results.addAll(comments);
                
                totalCount = userSearchRepository.countUsers(request.getKeyword()) +
                           contentSearchRepository.countContent(request.getKeyword()) +
                           commentSearchRepository.countComments(request.getKeyword());
                break;
        }
        
        int totalPages = (int) Math.ceil((double) totalCount / request.getPageSize());
        boolean hasNext = request.getPageNum() < totalPages;
        
        return SearchResponse.builder()
                .keyword(request.getKeyword())
                .searchType(searchType)
                .results(results)
                .totalCount(totalCount)
                .pageNum(request.getPageNum())
                .pageSize(request.getPageSize())
                .totalPages(totalPages)
                .hasNext(hasNext)
                .searchTime(0L) // 将在外层方法中设置
                .build();
    }

    /**
     * 异步记录搜索行为
     */
    @Async
    public void recordSearchAsync(String keyword, Long userId, Long resultCount) {
        try {
            searchRecordRepository.recordSearch(keyword, userId, resultCount);
        } catch (Exception e) {
            log.error("异步记录搜索行为失败：keyword={}, userId={}", keyword, userId, e);
        }
    }

    /**
     * 构建空响应
     */
    private SearchResponse buildEmptyResponse(SearchRequest request) {
        return SearchResponse.builder()
                .keyword(request.getKeyword())
                .searchType(request.getSearchType())
                .results(new ArrayList<>())
                .totalCount(0L)
                .pageNum(request.getPageNum())
                .pageSize(request.getPageSize())
                .totalPages(0)
                .hasNext(false)
                .searchTime(0L)
                .build();
    }
} 