package com.gig.collide.search.facade;

import com.gig.collide.api.search.request.SearchRequest;
import com.gig.collide.api.search.request.SearchSuggestionRequest;
import com.gig.collide.api.search.response.SearchResponse;
import com.gig.collide.api.search.response.SearchSuggestionResponse;
import com.gig.collide.api.search.service.SearchFacadeService;
import com.gig.collide.search.domain.service.SearchDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 搜索服务 Dubbo RPC 接口实现
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
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
            return searchDomainService.search(searchRequest);
        } catch (Exception e) {
            log.error("搜索失败", e);
            return SearchResponse.fail("SEARCH_FAILED", "搜索失败，请稍后重试");
        }
    }

    @Override
    public SearchSuggestionResponse suggest(SearchSuggestionRequest suggestionRequest) {
        try {
            return searchDomainService.suggest(suggestionRequest);
        } catch (Exception e) {
            log.error("获取搜索建议失败", e);
            return SearchSuggestionResponse.fail("SUGGESTION_FAILED", "获取建议失败，请稍后重试");
        }
    }

    @Override
    public List<String> getHotKeywords(Integer limit) {
        try {
            return searchDomainService.getHotKeywords(limit);
        } catch (Exception e) {
            log.error("获取热门关键词失败", e);
            return List.of();
        }
    }

    @Override
    public void recordSearch(String keyword, Long userId, Long resultCount) {
        try {
            searchDomainService.recordSearch(keyword, userId, resultCount);
        } catch (Exception e) {
            log.error("记录搜索行为失败", e);
            // 记录失败不影响主流程，只记录日志
        }
    }
} 