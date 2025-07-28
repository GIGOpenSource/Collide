package com.gig.collide.api.search.service;

import com.gig.collide.api.search.request.*;
import com.gig.collide.api.search.response.*;
import com.gig.collide.api.search.response.data.SearchResultInfo;
import com.gig.collide.base.response.BaseResponse;
import com.gig.collide.base.response.PageResponse;

/**
 * 搜索门面服务接口
 * 提供核心搜索业务功能
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public interface SearchFacadeService {

    /**
     * 统一搜索接口
     * 
     * @param searchRequest 搜索请求
     * @return 搜索响应
     */
    SearchUnifiedResponse search(SearchUnifiedRequest searchRequest);

    /**
     * 分页搜索接口
     * 
     * @param searchRequest 搜索请求
     * @return 分页搜索响应
     */
    PageResponse<SearchResultInfo> pageSearch(SearchUnifiedRequest searchRequest);

    /**
     * 用户搜索
     * 
     * @param searchRequest 搜索请求
     * @return 用户搜索响应
     */
    SearchUnifiedResponse searchUsers(SearchUnifiedRequest searchRequest);

    /**
     * 内容搜索
     * 
     * @param searchRequest 搜索请求
     * @return 内容搜索响应
     */
    SearchUnifiedResponse searchContents(SearchUnifiedRequest searchRequest);

    /**
     * 评论搜索
     * 
     * @param searchRequest 搜索请求
     * @return 评论搜索响应
     */
    SearchUnifiedResponse searchComments(SearchUnifiedRequest searchRequest);

    /**
     * 全文搜索
     * 
     * @param keyword 搜索关键词
     * @param contentType 内容类型过滤
     * @param userId 用户ID（可选）
     * @return 搜索响应
     */
    SearchUnifiedResponse fullTextSearch(String keyword, String contentType, Long userId);

    /**
     * 高级搜索
     * 
     * @param searchRequest 高级搜索请求
     * @return 搜索响应
     */
    SearchUnifiedResponse advancedSearch(SearchAdvancedRequest searchRequest);

    /**
     * 搜索建议
     * 
     * @param keyword 关键词
     * @param suggestionType 建议类型
     * @param limit 返回数量限制
     * @return 搜索建议响应
     */
    SearchSuggestionResponse getSuggestions(String keyword, String suggestionType, Integer limit);

    /**
     * 热门搜索关键词
     * 
     * @param limit 返回数量限制
     * @return 热门搜索响应
     */
    SearchHotKeywordsResponse getHotKeywords(Integer limit);

    /**
     * 搜索历史
     * 
     * @param userId 用户ID
     * @param limit 返回数量限制
     * @return 搜索历史响应
     */
    SearchHistoryQueryResponse getUserSearchHistory(Long userId, Integer limit);

    /**
     * 清空搜索历史
     * 
     * @param userId 用户ID
     * @return 操作结果
     */
    BaseResponse clearUserSearchHistory(Long userId);

    /**
     * 删除特定搜索历史
     * 
     * @param userId 用户ID
     * @param keyword 关键词
     * @return 操作结果
     */
    BaseResponse deleteSearchHistory(Long userId, String keyword);
} 