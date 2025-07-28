package com.gig.collide.api.search.service;

import com.gig.collide.api.search.request.*;
import com.gig.collide.api.search.response.*;
import com.gig.collide.base.response.BaseResponse;
import com.gig.collide.base.response.PageResponse;

/**
 * 搜索历史门面服务接口
 * 提供搜索历史管理功能
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public interface SearchHistoryFacadeService {

    /**
     * 记录搜索历史
     * 
     * @param recordRequest 记录请求
     * @return 记录响应
     */
    SearchHistoryRecordResponse recordSearchHistory(SearchHistoryRecordRequest recordRequest);

    /**
     * 查询用户搜索历史
     * 
     * @param queryRequest 查询请求
     * @return 搜索历史响应
     */
    SearchHistoryQueryResponse queryUserSearchHistory(SearchHistoryQueryRequest queryRequest);

    /**
     * 分页查询搜索历史
     * 
     * @param queryRequest 分页查询请求
     * @return 分页响应
     */
    PageResponse<SearchHistoryInfo> pageQuerySearchHistory(SearchHistoryQueryRequest queryRequest);

    /**
     * 获取用户最近搜索记录
     * 
     * @param userId 用户ID
     * @param limit 返回数量限制
     * @return 搜索历史响应
     */
    SearchHistoryQueryResponse getRecentSearchHistory(Long userId, Integer limit);

    /**
     * 获取用户热门搜索记录
     * 
     * @param userId 用户ID
     * @param limit 返回数量限制
     * @return 搜索历史响应
     */
    SearchHistoryQueryResponse getHotSearchHistory(Long userId, Integer limit);

    /**
     * 清空用户搜索历史
     * 
     * @param userId 用户ID
     * @return 操作结果
     */
    BaseResponse clearUserSearchHistory(Long userId);

    /**
     * 删除特定搜索历史记录
     * 
     * @param historyId 历史记录ID
     * @param userId 用户ID
     * @return 操作结果
     */
    BaseResponse deleteSearchHistory(Long historyId, Long userId);

    /**
     * 批量删除搜索历史记录
     * 
     * @param deleteRequest 批量删除请求
     * @return 操作结果
     */
    BaseResponse batchDeleteSearchHistory(SearchHistoryBatchDeleteRequest deleteRequest);

    /**
     * 搜索历史统计
     * 
     * @param userId 用户ID
     * @param days 统计天数
     * @return 统计响应
     */
    SearchHistoryStatisticsResponse getSearchHistoryStatistics(Long userId, Integer days);

    /**
     * 导出搜索历史
     * 
     * @param exportRequest 导出请求
     * @return 导出响应
     */
    SearchHistoryExportResponse exportSearchHistory(SearchHistoryExportRequest exportRequest);
} 