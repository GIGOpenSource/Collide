package com.gig.collide.business.facade;

import com.gig.collide.api.search.request.SearchRequest;
import com.gig.collide.api.search.request.SearchSuggestionRequest;
import com.gig.collide.api.search.response.SearchResponse;
import com.gig.collide.api.search.response.SearchSuggestionResponse;
import com.gig.collide.api.search.service.SearchFacadeService;
import com.gig.collide.business.domain.search.SearchDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 搜索服务 Facade 实现
 * 
 * @author GIG Team
 */
@Slf4j
@Component
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class SearchFacadeServiceImpl implements SearchFacadeService {

    private final SearchDomainService searchDomainService;

    @Override
    public SearchResponse search(SearchRequest searchRequest) {
        try {
            log.info("执行搜索服务，关键词：{}，类型：{}", 
                searchRequest.getKeyword(), searchRequest.getSearchType());

            // 执行搜索
            SearchResponse response = searchDomainService.search(searchRequest);

            // 记录搜索行为（异步）
            try {
                searchDomainService.recordSearch(
                    searchRequest.getKeyword(), 
                    null, // 这里可以从上下文获取用户ID
                    response.getTotalCount()
                );
            } catch (Exception e) {
                log.warn("记录搜索行为失败", e);
            }

            log.info("搜索完成，关键词：{}，结果数：{}，耗时：{}ms", 
                searchRequest.getKeyword(), response.getTotalCount(), response.getSearchTime());

            return response;

        } catch (Exception e) {
            log.error("搜索服务执行失败：{}", searchRequest.getKeyword(), e);
            
            // 返回空结果而不是抛出异常
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
                    .statistics(java.util.Map.of())
                    .suggestions(List.of())
                    .relatedSearches(List.of())
                    .build();
        }
    }

    @Override
    public SearchSuggestionResponse suggest(SearchSuggestionRequest suggestionRequest) {
        try {
            log.info("获取搜索建议，关键词：{}，类型：{}", 
                suggestionRequest.getKeyword(), suggestionRequest.getSuggestionType());

            SearchSuggestionResponse response = searchDomainService.getSuggestions(suggestionRequest);

            log.info("搜索建议获取完成，关键词：{}，建议数：{}", 
                suggestionRequest.getKeyword(), 
                response.getSuggestions() != null ? response.getSuggestions().size() : 0);

            return response;

        } catch (Exception e) {
            log.error("获取搜索建议失败：{}", suggestionRequest.getKeyword(), e);
            
            // 返回空建议而不是抛出异常
            return SearchSuggestionResponse.builder()
                    .keyword(suggestionRequest.getKeyword())
                    .suggestionType(suggestionRequest.getSuggestionType())
                    .suggestions(List.of())
                    .hotKeywords(getHotKeywords(10))
                    .build();
        }
    }

    @Override
    public List<String> getHotKeywords(Integer limit) {
        try {
            log.debug("获取热门搜索关键词，数量：{}", limit);

            List<String> hotKeywords = searchDomainService.getHotKeywords(limit);

            log.debug("热门关键词获取完成，数量：{}", hotKeywords.size());

            return hotKeywords;

        } catch (Exception e) {
            log.error("获取热门关键词失败", e);
            
            // 返回默认热门关键词
            return List.of("Java", "Spring", "数据库", "前端", "人工智能");
        }
    }

    @Override
    public void recordSearch(String keyword, Long userId, Long resultCount) {
        try {
            log.debug("记录搜索行为，关键词：{}，用户ID：{}，结果数：{}", 
                keyword, userId, resultCount);

            searchDomainService.recordSearch(keyword, userId, resultCount);

        } catch (Exception e) {
            log.error("记录搜索行为失败：keyword={}, userId={}", keyword, userId, e);
        }
    }
} 