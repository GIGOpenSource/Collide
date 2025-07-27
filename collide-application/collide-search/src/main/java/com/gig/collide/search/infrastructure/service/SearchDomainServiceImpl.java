package com.gig.collide.search.infrastructure.service;

import com.gig.collide.api.search.request.SearchRequest;
import com.gig.collide.api.search.request.SearchSuggestionRequest;
import com.gig.collide.api.search.response.SearchResponse;
import com.gig.collide.api.search.response.SearchSuggestionResponse;
import com.gig.collide.search.domain.service.SearchDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 搜索领域服务实现
 * 使用委托模式，将具体业务逻辑委托给SearchBusinessService
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SearchDomainServiceImpl implements SearchDomainService {

    private final com.gig.collide.search.domain.SearchDomainService searchBusinessService;

    @Override
    public SearchResponse search(SearchRequest searchRequest) {
        try {
            log.info("执行搜索：关键词={}，类型={}", searchRequest.getKeyword(), searchRequest.getSearchType());
            return searchBusinessService.search(searchRequest);
        } catch (Exception e) {
            log.error("搜索失败：searchRequest={}", searchRequest, e);
            return SearchResponse.builder()
                    .keyword(searchRequest.getKeyword())
                    .searchType(searchRequest.getSearchType())
                    .totalCount(0L)
                    .searchTime(0L)
                    .pageNum(searchRequest.getPageNum())
                    .pageSize(searchRequest.getPageSize())
                    .totalPages(0)
                    .hasNext(false)
                    .results(List.of())
                    .build();
        }
    }

    @Override
    public SearchSuggestionResponse suggest(SearchSuggestionRequest suggestionRequest) {
        try {
            log.info("获取搜索建议：关键词={}，类型={}", 
                suggestionRequest.getKeyword(), suggestionRequest.getSuggestionType());
            return searchBusinessService.getSuggestions(suggestionRequest);
        } catch (Exception e) {
            log.error("获取搜索建议失败：suggestionRequest={}", suggestionRequest, e);
            return SearchSuggestionResponse.builder()
                    .keyword(suggestionRequest.getKeyword())
                    .suggestionType(suggestionRequest.getSuggestionType())
                    .suggestions(List.of())
                    .build();
        }
    }

    @Override
    public List<String> getHotKeywords(Integer limit) {
        try {
            log.info("获取热门关键词：limit={}", limit);
            return searchBusinessService.getHotKeywords(limit);
        } catch (Exception e) {
            log.error("获取热门关键词失败：limit={}", limit, e);
            return List.of("Java", "Spring Boot", "微服务", "数据库", "Redis");
        }
    }

    @Override
    public void recordSearch(String keyword, Long userId, Long resultCount) {
        try {
            log.debug("记录搜索行为：关键词={}，用户ID={}，结果数={}", keyword, userId, resultCount);
            searchBusinessService.recordSearch(keyword, userId, resultCount);
        } catch (Exception e) {
            log.error("记录搜索行为失败：keyword={}, userId={}, resultCount={}", 
                keyword, userId, resultCount, e);
            // 记录失败不影响主流程，只记录日志
        }
    }
} 