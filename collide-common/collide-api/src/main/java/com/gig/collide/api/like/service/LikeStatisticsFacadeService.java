package com.gig.collide.api.like.service;

import com.gig.collide.api.like.request.*;
import com.gig.collide.api.like.response.*;
import com.gig.collide.api.like.response.data.LikeStatisticsInfo;
import com.gig.collide.base.response.PageResponse;

/**
 * 点赞统计门面服务接口
 * 提供点赞统计分析功能
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface LikeStatisticsFacadeService {

    /**
     * 查询点赞统计数据
     * 
     * @param request 统计查询请求
     * @return 统计查询响应
     */
    LikeStatisticsQueryResponse queryStatistics(LikeStatisticsQueryRequest request);

    /**
     * 分页查询点赞统计数据
     * 
     * @param request 分页统计查询请求
     * @return 分页响应
     */
    PageResponse<LikeStatisticsInfo> pageQueryStatistics(LikeStatisticsQueryRequest request);

    /**
     * 获取实时统计数据
     * 
     * @param request 实时统计请求
     * @return 实时统计响应
     */
    LikeRealtimeStatisticsResponse getRealtimeStatistics(LikeRealtimeStatisticsRequest request);

    /**
     * 获取趋势分析数据
     * 
     * @param request 趋势分析请求
     * @return 趋势分析响应
     */
    LikeTrendAnalysisResponse getTrendAnalysis(LikeTrendAnalysisRequest request);

    /**
     * 获取排行榜数据
     * 
     * @param request 排行榜请求
     * @return 排行榜响应
     */
    LikeRankingResponse getRanking(LikeRankingRequest request);

    /**
     * 生成统计报告
     * 
     * @param request 统计报告请求
     * @return 统计报告响应
     */
    LikeStatisticsReportResponse generateReport(LikeStatisticsReportRequest request);

    /**
     * 刷新统计数据
     * 
     * @param request 刷新统计请求
     * @return 刷新统计响应
     */
    LikeStatisticsRefreshResponse refreshStatistics(LikeStatisticsRefreshRequest request);

    /**
     * 批量刷新统计数据
     * 
     * @param request 批量刷新统计请求
     * @return 批量刷新统计响应
     */
    LikeStatisticsBatchRefreshResponse batchRefreshStatistics(LikeStatisticsBatchRefreshRequest request);

    /**
     * 获取用户点赞统计
     * 
     * @param request 用户统计请求
     * @return 用户统计响应
     */
    LikeUserStatisticsResponse getUserStatistics(LikeUserStatisticsRequest request);

    /**
     * 获取目标对象点赞统计
     * 
     * @param request 目标统计请求
     * @return 目标统计响应
     */
    LikeTargetStatisticsResponse getTargetStatistics(LikeTargetStatisticsRequest request);

    /**
     * 导出统计数据
     * 
     * @param request 导出统计请求
     * @return 导出统计响应
     */
    LikeStatisticsExportResponse exportStatistics(LikeStatisticsExportRequest request);
} 