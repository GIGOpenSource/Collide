package com.gig.collide.search.domain.service;

import com.gig.collide.api.search.request.SearchAdvancedRequest;
import com.gig.collide.api.search.request.SearchUnifiedRequest;
import com.gig.collide.api.search.response.SearchUnifiedResponse;
import com.gig.collide.api.search.response.data.SearchResultInfo;
import com.gig.collide.base.response.PageResponse;

import java.util.List;

/**
 * 搜索业务服务接口
 * 
 * @author Collide Team
 * @version 1.0.0
 * @since 2024-01-01
 */
public interface SearchService {

    /**
     * 统一搜索
     * 
     * @param request 搜索请求
     * @return 搜索响应
     */
    SearchUnifiedResponse unifiedSearch(SearchUnifiedRequest request);

    /**
     * 分页搜索
     * 
     * @param request 搜索请求
     * @return 分页搜索结果
     */
    PageResponse<SearchResultInfo> pageSearch(SearchUnifiedRequest request);

    /**
     * 用户搜索
     * 
     * @param request 搜索请求
     * @return 用户搜索结果
     */
    SearchUnifiedResponse searchUsers(SearchUnifiedRequest request);

    /**
     * 内容搜索
     * 
     * @param request 搜索请求
     * @return 内容搜索结果
     */
    SearchUnifiedResponse searchContents(SearchUnifiedRequest request);

    /**
     * 评论搜索
     * 
     * @param request 搜索请求
     * @return 评论搜索结果
     */
    SearchUnifiedResponse searchComments(SearchUnifiedRequest request);

    /**
     * 全文搜索
     * 
     * @param keyword 搜索关键词
     * @param contentType 内容类型过滤
     * @param userId 用户ID（可选）
     * @return 搜索结果
     */
    SearchUnifiedResponse fullTextSearch(String keyword, String contentType, Long userId);

    /**
     * 高级搜索
     * 
     * @param request 高级搜索请求
     * @return 搜索结果
     */
    SearchUnifiedResponse advancedSearch(SearchAdvancedRequest request);

    /**
     * 热门内容搜索
     * 
     * @param contentType 内容类型
     * @param categoryId 分类ID
     * @param limit 限制数量
     * @return 热门内容列表
     */
    List<SearchResultInfo> searchHotContents(String contentType, Long categoryId, Integer limit);

    /**
     * 推荐内容搜索
     * 
     * @param contentType 内容类型
     * @param userId 用户ID（用于个性化推荐）
     * @param limit 限制数量
     * @return 推荐内容列表
     */
    List<SearchResultInfo> searchRecommendedContents(String contentType, Long userId, Integer limit);

    /**
     * 相关内容搜索
     * 
     * @param targetId 目标内容ID
     * @param contentType 内容类型
     * @param limit 限制数量
     * @return 相关内容列表
     */
    List<SearchResultInfo> searchRelatedContents(Long targetId, String contentType, Integer limit);

    /**
     * 根据标签搜索
     * 
     * @param tags 标签列表
     * @param contentType 内容类型
     * @param matchAll 是否需要匹配所有标签
     * @param page 页码
     * @param size 页大小
     * @return 搜索结果
     */
    PageResponse<SearchResultInfo> searchByTags(List<String> tags, String contentType, Boolean matchAll, Integer page, Integer size);

    /**
     * 搜索索引同步
     * 
     * @param indexType 索引类型（user/content/comment）
     * @param targetId 目标ID
     * @return 同步结果
     */
    boolean syncSearchIndex(String indexType, Long targetId);

    /**
     * 批量搜索索引同步
     * 
     * @param indexType 索引类型
     * @param targetIds 目标ID列表
     * @return 同步成功数量
     */
    int batchSyncSearchIndex(String indexType, List<Long> targetIds);

    /**
     * 重建搜索索引
     * 
     * @param indexType 索引类型
     * @return 重建结果
     */
    boolean rebuildSearchIndex(String indexType);
} 