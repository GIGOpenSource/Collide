package com.gig.collide.search.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.search.SearchFacadeService;
import com.gig.collide.api.search.request.SearchHistoryQueryRequest;
import com.gig.collide.api.search.request.SearchRequest;
import com.gig.collide.api.search.response.SearchHistoryResponse;
import com.gig.collide.api.search.response.SearchResponse;
import com.gig.collide.api.search.response.HotSearchResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.search.domain.entity.SearchHistory;
import com.gig.collide.search.domain.entity.HotSearch;
import com.gig.collide.search.domain.service.SearchService;
import com.gig.collide.search.infrastructure.cache.SearchCacheConstant;
import com.gig.collide.web.vo.Result;
import com.gig.collide.api.user.UserFacadeService;
import com.gig.collide.api.content.ContentFacadeService;
import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 搜索门面服务实现类 - 缓存增强版
 * 对齐social模块设计风格，提供完整的搜索服务
 * 包含缓存功能、跨模块集成、错误处理、数据转换
 * 
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-16
 */
@Slf4j
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class SearchFacadeServiceImpl implements SearchFacadeService {

    private final SearchService searchService;
    
    // =================== 跨模块服务注入 ===================
    @Autowired
    private UserFacadeService userFacadeService;
    
    @Autowired
    private ContentFacadeService contentFacadeService;

    // =================== 搜索核心功能 ===================

    @Override
    @Cached(name = SearchCacheConstant.SEARCH_RESULT_CACHE, key = SearchCacheConstant.SEARCH_RESULT_KEY, 
            expire = SearchCacheConstant.SEARCH_RESULT_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<SearchResponse> search(SearchRequest request) {
        try {
            log.info("执行搜索请求: 关键词={}, 类型={}", request.getKeyword(), request.getSearchType());
            long startTime = System.currentTimeMillis();
            
            List<Object> results = searchService.search(
                request.getKeyword(),
                request.getSearchType(),
                request.getUserId(),
                request.getCurrentPage(),
                request.getPageSize(),
                request.getSortBy()
            );
            
            long duration = System.currentTimeMillis() - startTime;
            
            SearchResponse response = new SearchResponse();
            response.setKeyword(request.getKeyword());
            response.setSearchType(request.getSearchType());
            response.setTotalCount(results.size());
            response.setDuration(duration);
            response.setHasMore(results.size() >= request.getPageSize());
            
            // 获取搜索建议
            List<String> suggestions = searchService.getSearchSuggestions(request.getKeyword(), 5);
            response.setSuggestions(suggestions);
            
            log.info("搜索执行完成: 关键词={}, 耗时={}ms, 结果数={}", 
                    request.getKeyword(), duration, results.size());
            return Result.success(response);
        } catch (Exception e) {
            log.error("搜索失败", e);
            return Result.error("SEARCH_ERROR", "搜索失败: " + e.getMessage());
        }
    }

    // =================== 搜索历史管理 ===================

    @Override
    @Cached(name = SearchCacheConstant.SEARCH_HISTORY_CACHE, key = SearchCacheConstant.SEARCH_HISTORY_KEY, 
            expire = SearchCacheConstant.SEARCH_HISTORY_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<PageResponse<SearchHistoryResponse>> getSearchHistory(SearchHistoryQueryRequest request) {
        try {
            log.debug("查询搜索历史: 用户={}, 页码={}", request.getUserId(), request.getCurrentPage());
            
            IPage<SearchHistory> page = searchService.getSearchHistory(
                request.getUserId(),
                request.getSearchType(),
                request.getKeyword(),
                request.getCurrentPage(),
                request.getPageSize(),
                request.getSortBy(),
                request.getSortDirection()
            );
            
            PageResponse<SearchHistoryResponse> result = convertToPageResponse(page);
            log.debug("搜索历史查询完成: 用户={}, 记录数={}", request.getUserId(), result.getTotal());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取搜索历史失败", e);
            return Result.error("SEARCH_HISTORY_ERROR", "获取搜索历史失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = SearchCacheConstant.SEARCH_HISTORY_CACHE)
    @CacheInvalidate(name = SearchCacheConstant.USER_PREFERENCE_CACHE)
    public Result<Void> clearSearchHistory(Long userId) {
        try {
            log.info("清空搜索历史: 用户={}", userId);
            searchService.clearSearchHistory(userId);
            log.info("搜索历史清空成功: 用户={}", userId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("清空搜索历史失败", e);
            return Result.error("CLEAR_HISTORY_ERROR", "清空搜索历史失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = SearchCacheConstant.SEARCH_HISTORY_CACHE)
    @CacheInvalidate(name = SearchCacheConstant.USER_PREFERENCE_CACHE)
    public Result<Void> deleteSearchHistory(Long historyId) {
        try {
            log.info("删除搜索历史: ID={}", historyId);
            searchService.deleteSearchHistory(historyId);
            log.info("搜索历史删除成功: ID={}", historyId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("删除搜索历史失败", e);
            return Result.error("DELETE_HISTORY_ERROR", "删除搜索历史失败: " + e.getMessage());
        }
    }

    // =================== 热门搜索功能 ===================

    @Override
    @Cached(name = SearchCacheConstant.HOT_SEARCH_CACHE, key = SearchCacheConstant.HOT_SEARCH_KEY, 
            expire = SearchCacheConstant.HOT_SEARCH_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<List<HotSearchResponse>> getHotSearchKeywords(Integer limit) {
        try {
            log.debug("获取热门搜索: 限制={}", limit);
            
            List<HotSearch> hotSearches = searchService.getHotSearchKeywords(limit);
            List<HotSearchResponse> responses = hotSearches.stream()
                    .map(this::convertToHotSearchResponse)
                    .collect(Collectors.toList());
            
            // 设置排名
            for (int i = 0; i < responses.size(); i++) {
                responses.get(i).setRank(i + 1);
            }
            
            log.debug("热门搜索获取完成: 数量={}", responses.size());
            return Result.success(responses);
        } catch (Exception e) {
            log.error("获取热门搜索失败", e);
            return Result.error("HOT_SEARCH_ERROR", "获取热门搜索失败: " + e.getMessage());
        }
    }

    // =================== 搜索建议功能 ===================

    @Override
    @Cached(name = SearchCacheConstant.SEARCH_SUGGESTION_CACHE, key = SearchCacheConstant.SEARCH_SUGGESTION_KEY, 
            expire = SearchCacheConstant.SEARCH_SUGGESTION_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<List<String>> getSearchSuggestions(String keyword, Integer limit) {
        try {
            log.debug("获取搜索建议: 关键词={}, 限制={}", keyword, limit);
            List<String> suggestions = searchService.getSearchSuggestions(keyword, limit);
            log.debug("搜索建议获取完成: 关键词={}, 建议数={}", keyword, suggestions.size());
            return Result.success(suggestions);
        } catch (Exception e) {
            log.error("获取搜索建议失败", e);
            return Result.error("SUGGESTION_ERROR", "获取搜索建议失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = SearchCacheConstant.HOT_SEARCH_TYPE_CACHE, key = SearchCacheConstant.HOT_SEARCH_TYPE_KEY, 
            expire = SearchCacheConstant.HOT_SEARCH_TYPE_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<List<HotSearchResponse>> getHotKeywordsByType(String searchType, Integer limit) {
        try {
            log.debug("按类型获取热搜: 类型={}, 限制={}", searchType, limit);
            List<HotSearch> hotSearches = searchService.getHotKeywordsByType(searchType, limit);
            List<HotSearchResponse> responses = hotSearches.stream()
                    .map(this::convertToHotSearchResponse)
                    .collect(Collectors.toList());
            
            log.debug("按类型热搜获取完成: 类型={}, 数量={}", searchType, responses.size());
            return Result.success(responses);
        } catch (Exception e) {
            log.error("根据类型获取热搜失败", e);
            return Result.error("HOT_SEARCH_ERROR", "根据类型获取热搜失败: " + e.getMessage());
        }
    }

    // =================== 用户偏好分析 ===================

    @Override
    @Cached(name = SearchCacheConstant.USER_PREFERENCE_CACHE, key = SearchCacheConstant.USER_PREFERENCE_KEY, 
            expire = SearchCacheConstant.USER_PREFERENCE_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<List<SearchHistoryResponse>> getUserSearchPreferences(Long userId) {
        try {
            log.debug("获取用户搜索偏好: 用户={}", userId);
            List<SearchHistory> preferences = searchService.getUserSearchPreferences(userId);
            List<SearchHistoryResponse> responses = preferences.stream()
                    .map(this::convertToHistoryResponse)
                    .collect(Collectors.toList());
            
            log.debug("用户搜索偏好获取完成: 用户={}, 偏好数={}", userId, responses.size());
            return Result.success(responses);
        } catch (Exception e) {
            log.error("获取用户搜索偏好失败", e);
            return Result.error("PREFERENCE_ERROR", "获取用户搜索偏好失败: " + e.getMessage());
        }
    }

    // =================== 管理功能 ===================

    @Override
    @CacheInvalidate(name = SearchCacheConstant.HOT_SEARCH_CACHE)
    @CacheInvalidate(name = SearchCacheConstant.HOT_SEARCH_TYPE_CACHE)
    public Result<Void> updateHotSearchTrend(String keyword, Double trendScore) {
        try {
            log.info("更新热搜趋势: 关键词={}, 分数={}", keyword, trendScore);
            searchService.updateHotSearchTrend(keyword, trendScore);
            log.info("热搜趋势更新成功: 关键词={}", keyword);
            return Result.success(null);
        } catch (Exception e) {
            log.error("更新热搜趋势失败", e);
            return Result.error("UPDATE_TREND_ERROR", "更新热搜趋势失败: " + e.getMessage());
        }
    }

    // =================== 混合搜索功能 ===================

    @Override
    @Cached(name = SearchCacheConstant.TAG_MIXED_SEARCH_CACHE, key = SearchCacheConstant.TAG_MIXED_SEARCH_KEY, 
            expire = SearchCacheConstant.TAG_MIXED_SEARCH_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public PageResponse<Object> searchByTagMixed(String tag, Integer currentPage, Integer pageSize) {
        try {
            log.info("Tag混合搜索: 标签={}, 页码={}, 大小={}", tag, currentPage, pageSize);
            
            List<Object> allResults = new ArrayList<>();
            
            // 1. 搜索用户（根据标签）
            PageResponse<Object> userResults = searchUsersByTag(tag, 1, pageSize / 2);
            if (userResults.getSuccess() && !CollectionUtils.isEmpty(userResults.getDatas())) {
                allResults.addAll(userResults.getDatas());
            }
            
            // 2. 搜索内容（根据标签）
            PageResponse<Object> contentResults = searchContentsByTag(tag, 1, pageSize / 2);
            if (contentResults.getSuccess() && !CollectionUtils.isEmpty(contentResults.getDatas())) {
                allResults.addAll(contentResults.getDatas());
            }
            
            // 3. 计算分页
            int total = allResults.size();
            int offset = (currentPage - 1) * pageSize;
            int end = Math.min(offset + pageSize, total);
            
            List<Object> pagedData;
            if (offset >= total) {
                pagedData = Collections.emptyList();
            } else {
                pagedData = allResults.subList(offset, end);
            }
            
            PageResponse<Object> response = new PageResponse<>();
            response.setDatas(pagedData);
            response.setTotal(total);
            response.setCurrentPage(currentPage);
            response.setPageSize(pageSize);
            response.setTotalPage((int) Math.ceil((double) total / pageSize));
            response.setSuccess(true);
            
            log.info("Tag混合搜索完成: 标签={}, 用户数={}, 内容数={}, 总数={}", 
                    tag, userResults.getTotal(), contentResults.getTotal(), total);
            return response;
        } catch (Exception e) {
            log.error("Tag混合搜索失败", e);
            return createErrorObjectPageResponse();
        }
    }

    @Override
    @Cached(name = SearchCacheConstant.USER_TAG_SEARCH_CACHE, key = SearchCacheConstant.USER_TAG_SEARCH_KEY, 
            expire = SearchCacheConstant.USER_TAG_SEARCH_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public PageResponse<Object> searchUsersByTag(String tag, Integer currentPage, Integer pageSize) {
        try {
            log.debug("Tag搜索用户: 标签={}, 页码={}, 大小={}", tag, currentPage, pageSize);
            
            // TODO: 调用用户服务进行Tag搜索
            // Result<PageResponse<UserResponse>> userResult = userFacadeService.searchUsersByTag(tag, currentPage, pageSize);
            
            // 临时实现，返回空结果
            PageResponse<Object> response = new PageResponse<>();
            response.setDatas(Collections.emptyList());
            response.setTotal(0);
            response.setCurrentPage(currentPage);
            response.setPageSize(pageSize);
            response.setTotalPage(0);
            response.setSuccess(true);
            
            log.debug("Tag搜索用户完成: 标签={}, 结果数={}", tag, 0);
            return response;
        } catch (Exception e) {
            log.error("Tag搜索用户失败", e);
            return createErrorObjectPageResponse();
        }
    }

    @Override
    @Cached(name = SearchCacheConstant.CONTENT_TAG_SEARCH_CACHE, key = SearchCacheConstant.CONTENT_TAG_SEARCH_KEY, 
            expire = SearchCacheConstant.CONTENT_TAG_SEARCH_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public PageResponse<Object> searchContentsByTag(String tag, Integer currentPage, Integer pageSize) {
        try {
            log.debug("Tag搜索内容: 标签={}, 页码={}, 大小={}", tag, currentPage, pageSize);
            
            // TODO: 调用内容服务进行Tag搜索
            // Result<PageResponse<ContentResponse>> contentResult = contentFacadeService.searchContentsByTag(tag, currentPage, pageSize);
            
            // 临时实现，返回空结果
            PageResponse<Object> response = new PageResponse<>();
            response.setDatas(Collections.emptyList());
            response.setTotal(0);
            response.setCurrentPage(currentPage);
            response.setPageSize(pageSize);
            response.setTotalPage(0);
            response.setSuccess(true);
            
            log.debug("Tag搜索内容完成: 标签={}, 结果数={}", tag, 0);
            return response;
        } catch (Exception e) {
            log.error("Tag搜索内容失败", e);
            return createErrorObjectPageResponse();
        }
    }

    // =================== 私有方法 ===================

    /**
     * 转换为PageResponse
     */
    private PageResponse<SearchHistoryResponse> convertToPageResponse(IPage<SearchHistory> page) {
        if (page == null) {
            return createEmptyPageResponse(1, 20);
        }
        
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return createEmptyPageResponse((int) page.getCurrent(), (int) page.getSize());
        }

        List<SearchHistoryResponse> responses = page.getRecords().stream()
                .map(this::convertToHistoryResponse)
                .collect(Collectors.toList());

        PageResponse<SearchHistoryResponse> response = new PageResponse<>();
        response.setDatas(responses);
        response.setTotal((int) page.getTotal());
        response.setCurrentPage((int) page.getCurrent());
        response.setPageSize((int) page.getSize());
        response.setTotalPage((int) page.getPages());
        response.setSuccess(true);

        return response;
    }

    /**
     * 创建空的分页响应
     */
    private PageResponse<SearchHistoryResponse> createEmptyPageResponse(int currentPage, int pageSize) {
        PageResponse<SearchHistoryResponse> response = new PageResponse<>();
        response.setDatas(Collections.emptyList());
        response.setTotal(0);
        response.setCurrentPage(currentPage);
        response.setPageSize(pageSize);
        response.setTotalPage(0);
        response.setSuccess(true);
        return response;
    }

    /**
     * 创建错误的Object分页响应
     */
    private PageResponse<Object> createErrorObjectPageResponse() {
        PageResponse<Object> response = new PageResponse<>();
        response.setDatas(Collections.emptyList());
        response.setTotal(0);
        response.setCurrentPage(1);
        response.setPageSize(20);
        response.setTotalPage(0);
        response.setSuccess(false);
        return response;
    }

    /**
     * 转换为搜索历史响应对象
     */
    private SearchHistoryResponse convertToHistoryResponse(SearchHistory history) {
        SearchHistoryResponse response = new SearchHistoryResponse();
        BeanUtils.copyProperties(history, response);
        return response;
    }

    /**
     * 转换为热搜响应对象
     */
    private HotSearchResponse convertToHotSearchResponse(HotSearch hotSearch) {
        HotSearchResponse response = new HotSearchResponse();
        BeanUtils.copyProperties(hotSearch, response);
        return response;
    }
} 