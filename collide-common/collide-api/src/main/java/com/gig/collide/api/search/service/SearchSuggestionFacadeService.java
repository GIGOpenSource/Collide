package com.gig.collide.api.search.service;

import com.gig.collide.api.search.request.*;
import com.gig.collide.api.search.response.*;
import com.gig.collide.api.search.response.data.SearchSuggestionInfo;
import com.gig.collide.base.response.BaseResponse;
import com.gig.collide.base.response.PageResponse;

/**
 * 搜索建议门面服务接口
 * 提供搜索建议和自动完成功能
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public interface SearchSuggestionFacadeService {

    /**
     * 获取搜索建议
     * 
     * @param suggestionRequest 建议请求
     * @return 建议响应
     */
    SearchSuggestionResponse getSuggestions(SearchSuggestionRequest suggestionRequest);

    /**
     * 获取自动完成建议
     * 
     * @param autocompleteRequest 自动完成请求
     * @return 自动完成响应
     */
    SearchAutocompleteResponse getAutocompleteSuggestions(SearchAutocompleteRequest autocompleteRequest);

    /**
     * 分页查询搜索建议
     * 
     * @param queryRequest 分页查询请求
     * @return 分页响应
     */
    PageResponse<SearchSuggestionInfo> pageQuerySuggestions(SearchSuggestionQueryRequest queryRequest);

    /**
     * 创建搜索建议
     * 
     * @param createRequest 创建请求
     * @return 创建响应
     */
    SearchSuggestionCreateResponse createSuggestion(SearchSuggestionCreateRequest createRequest);

    /**
     * 更新搜索建议
     * 
     * @param updateRequest 更新请求
     * @return 更新响应
     */
    SearchSuggestionUpdateResponse updateSuggestion(SearchSuggestionUpdateRequest updateRequest);

    /**
     * 删除搜索建议
     * 
     * @param suggestionId 建议ID
     * @param operatorId 操作员ID
     * @return 删除结果
     */
    BaseResponse deleteSuggestion(Long suggestionId, Long operatorId);

    /**
     * 批量创建搜索建议
     * 
     * @param batchCreateRequest 批量创建请求
     * @return 批量创建响应
     */
    SearchSuggestionBatchCreateResponse batchCreateSuggestions(SearchSuggestionBatchCreateRequest batchCreateRequest);

    /**
     * 启用/禁用搜索建议
     * 
     * @param suggestionId 建议ID
     * @param enabled 是否启用
     * @param operatorId 操作员ID
     * @return 操作结果
     */
    BaseResponse toggleSuggestionStatus(Long suggestionId, Boolean enabled, Long operatorId);

    /**
     * 获取热门搜索建议
     * 
     * @param suggestionType 建议类型
     * @param limit 返回数量限制
     * @return 热门建议响应
     */
    SearchSuggestionResponse getHotSuggestions(String suggestionType, Integer limit);

    /**
     * 获取个性化搜索建议
     * 
     * @param userId 用户ID
     * @param limit 返回数量限制
     * @return 个性化建议响应
     */
    SearchSuggestionResponse getPersonalizedSuggestions(Long userId, Integer limit);

    /**
     * 记录建议点击
     * 
     * @param clickRequest 点击记录请求
     * @return 记录结果
     */
    BaseResponse recordSuggestionClick(SearchSuggestionClickRequest clickRequest);

    /**
     * 获取建议统计信息
     * 
     * @param suggestionId 建议ID
     * @return 统计信息响应
     */
    SearchSuggestionStatisticsResponse getSuggestionStatistics(Long suggestionId);

    /**
     * 刷新建议权重
     * 
     * @param suggestionType 建议类型（可选）
     * @return 刷新结果
     */
    BaseResponse refreshSuggestionWeights(String suggestionType);

    /**
     * 导入搜索建议
     * 
     * @param importRequest 导入请求
     * @return 导入结果
     */
    SearchSuggestionImportResponse importSuggestions(SearchSuggestionImportRequest importRequest);
} 