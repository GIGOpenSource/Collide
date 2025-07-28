package com.gig.collide.api.search.service;

import com.gig.collide.api.search.request.*;
import com.gig.collide.api.search.response.*;
import com.gig.collide.api.search.response.data.SearchStatisticsInfo;
import com.gig.collide.base.response.BaseResponse;
import com.gig.collide.base.response.PageResponse;

/**
 * 搜索统计门面服务接口
 * 提供搜索数据统计和分析功能
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public interface SearchStatisticsFacadeService {

    /**
     * 查询搜索统计
     * 
     * @param queryRequest 查询请求
     * @return 统计响应
     */
    SearchStatisticsQueryResponse querySearchStatistics(SearchStatisticsQueryRequest queryRequest);

    /**
     * 分页查询搜索统计
     * 
     * @param queryRequest 分页查询请求
     * @return 分页响应
     */
    PageResponse<SearchStatisticsInfo> pageQuerySearchStatistics(SearchStatisticsQueryRequest queryRequest);

    /**
     * 获取热门搜索关键词
     * 
     * @param limit 返回数量限制
     * @param timeRange 时间范围（今日/本周/本月）
     * @return 热门关键词响应
     */
    SearchHotKeywordsResponse getHotKeywords(Integer limit, String timeRange);

    /**
     * 获取搜索趋势
     * 
     * @param keyword 关键词
     * @param days 统计天数
     * @return 搜索趋势响应
     */
    SearchTrendResponse getSearchTrend(String keyword, Integer days);

    /**
     * 获取搜索关键词排行榜
     * 
     * @param rankType 排行类型（搜索次数/用户数/热度）
     * @param limit 返回数量限制
     * @param timeRange 时间范围
     * @return 排行榜响应
     */
    SearchRankingResponse getSearchRanking(String rankType, Integer limit, String timeRange);

    /**
     * 更新搜索统计
     * 
     * @param updateRequest 更新请求
     * @return 更新响应
     */
    SearchStatisticsUpdateResponse updateSearchStatistics(SearchStatisticsUpdateRequest updateRequest);

    /**
     * 获取实时搜索统计
     * 
     * @param keyword 关键词
     * @return 实时统计响应
     */
    SearchRealtimeStatisticsResponse getRealtimeStatistics(String keyword);

    /**
     * 获取搜索分析报告
     * 
     * @param reportRequest 报告请求
     * @return 分析报告响应
     */
    SearchAnalysisReportResponse getSearchAnalysisReport(SearchAnalysisReportRequest reportRequest);

    /**
     * 获取用户搜索偏好分析
     * 
     * @param userId 用户ID
     * @param days 分析天数
     * @return 偏好分析响应
     */
    SearchPreferenceAnalysisResponse getUserSearchPreference(Long userId, Integer days);

    /**
     * 获取搜索性能统计
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 性能统计响应
     */
    SearchPerformanceStatisticsResponse getSearchPerformanceStatistics(String startTime, String endTime);

    /**
     * 重新计算搜索热度评分
     * 
     * @return 计算结果
     */
    BaseResponse recalculateHotScore();

    /**
     * 清理过期统计数据
     * 
     * @param days 保留天数
     * @return 清理结果
     */
    BaseResponse cleanExpiredStatistics(Integer days);
} 