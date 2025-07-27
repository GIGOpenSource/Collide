package com.gig.collide.search.facade;

import com.gig.collide.api.search.facade.SearchFacadeService;
import com.gig.collide.api.search.request.SearchRequest;
import com.gig.collide.api.search.request.SearchSuggestionRequest;
import com.gig.collide.api.search.response.SearchResponse;
import com.gig.collide.api.search.response.SearchSuggestionResponse;
import com.gig.collide.search.domain.service.SearchDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.validation.annotation.Validated;

/**
 * 搜索门面服务实现
 * 基于Dubbo提供RPC搜索服务
 * 
 * @author Collide Team
 * @version 1.0
 */
@Slf4j
@DubboService(version = "1.0.0", timeout = 10000)
@RequiredArgsConstructor
@Validated
public class SearchFacadeServiceImpl implements SearchFacadeService {

    private final SearchDomainService searchDomainService;

    @Override
    public SearchResponse search(SearchRequest request) {
        try {
            log.info("RPC搜索请求：关键词={}，类型={}", request.getKeyword(), request.getSearchType());
            
            // 参数验证
            if (request == null) {
                log.warn("搜索请求参数为空");
                return buildErrorResponse("搜索请求参数不能为空");
            }
            
            // 调用领域服务
            SearchResponse response = searchDomainService.search(request);
            
            log.info("RPC搜索完成：关键词={}，结果数={}", 
                    request.getKeyword(), response.getTotalCount());
            
            return response;
            
        } catch (Exception e) {
            log.error("RPC搜索异常：request={}", request, e);
            return buildErrorResponse("搜索服务异常，请稍后重试");
        }
    }

    @Override
    public SearchSuggestionResponse generateSearchSuggestions(SearchSuggestionRequest request) {
        try {
            log.info("RPC搜索建议请求：关键词={}", request.getKeyword());
            
            // 参数验证
            if (request == null) {
                log.warn("搜索建议请求参数为空");
                return buildEmptySuggestionResponse("搜索建议请求参数不能为空");
            }
            
            // 调用领域服务
            SearchSuggestionResponse response = searchDomainService.generateSearchSuggestions(request);
            
            log.info("RPC搜索建议完成：关键词={}，建议数={}", 
                    request.getKeyword(), response.getTotalCount());
            
            return response;
            
        } catch (Exception e) {
            log.error("RPC搜索建议异常：request={}", request, e);
            return buildEmptySuggestionResponse("搜索建议服务异常，请稍后重试");
        }
    }

    @Override
    public SearchSuggestionResponse getHotKeywords(SearchSuggestionRequest request) {
        try {
            log.info("RPC热门关键词请求：限制={}", request.getLimit());
            
            // 参数验证
            if (request == null) {
                log.warn("热门关键词请求参数为空");
                return buildEmptySuggestionResponse("热门关键词请求参数不能为空");
            }
            
            // 调用领域服务
            SearchSuggestionResponse response = searchDomainService.getHotKeywords(request);
            
            log.info("RPC热门关键词完成：关键词数={}", response.getTotalCount());
            
            return response;
            
        } catch (Exception e) {
            log.error("RPC热门关键词异常：request={}", request, e);
            return buildEmptySuggestionResponse("热门关键词服务异常，请稍后重试");
        }
    }

    /**
     * 构建错误搜索响应
     */
    private SearchResponse buildErrorResponse(String errorMessage) {
        return SearchResponse.builder()
                .keyword("")
                .searchType("ALL")
                .totalCount(0L)
                .searchTime(0L) 
                .pageNum(1)
                .pageSize(20)
                .totalPages(0)
                .hasNext(false)
                .results(java.util.List.of())
                .build();
    }

    /**
     * 构建空搜索建议响应
     */
    private SearchSuggestionResponse buildEmptySuggestionResponse(String errorMessage) {
        return SearchSuggestionResponse.builder()
                .keyword("")
                .suggestions(java.util.List.of())
                .totalCount(0L)
                .build();
    }
} 