package com.gig.collide.api.payment.service;

import com.gig.collide.api.payment.request.*;
import com.gig.collide.api.payment.response.*;
import com.gig.collide.api.payment.response.data.PaymentStatisticsInfo;
import com.gig.collide.base.response.PageResponse;

/**
 * 支付统计门面服务接口
 * 提供支付数据统计和分析功能
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public interface PaymentStatisticsFacadeService {

    /**
     * 生成支付统计数据
     * 
     * @param generateRequest 生成统计请求
     * @return 生成统计响应
     */
    PaymentStatisticsGenerateResponse generateStatistics(PaymentStatisticsGenerateRequest generateRequest);

    /**
     * 查询支付统计数据
     * 
     * @param queryRequest 查询请求
     * @return 查询响应
     */
    PaymentStatisticsQueryResponse queryStatistics(PaymentStatisticsQueryRequest queryRequest);

    /**
     * 分页查询支付统计
     * 
     * @param queryRequest 分页查询请求
     * @return 分页响应
     */
    PageResponse<PaymentStatisticsInfo> pageQueryStatistics(PaymentStatisticsQueryRequest queryRequest);

    /**
     * 获取实时统计数据
     * 
     * @param realtimeRequest 实时统计请求
     * @return 实时统计响应
     */
    PaymentStatisticsRealtimeResponse getRealtimeStatistics(PaymentStatisticsRealtimeRequest realtimeRequest);

    /**
     * 获取趋势分析数据
     * 
     * @param trendRequest 趋势分析请求
     * @return 趋势分析响应
     */
    PaymentStatisticsTrendResponse getTrendAnalysis(PaymentStatisticsTrendRequest trendRequest);

    /**
     * 获取排行榜数据
     * 
     * @param rankingRequest 排行榜请求
     * @return 排行榜响应
     */
    PaymentStatisticsRankingResponse getRankingStatistics(PaymentStatisticsRankingRequest rankingRequest);

    /**
     * 获取成功率统计
     * 
     * @param successRateRequest 成功率请求
     * @return 成功率响应
     */
    PaymentStatisticsSuccessRateResponse getSuccessRateStatistics(PaymentStatisticsSuccessRateRequest successRateRequest);

    /**
     * 生成统计报告
     * 
     * @param reportRequest 报告请求
     * @return 报告响应
     */
    PaymentStatisticsReportResponse generateStatisticsReport(PaymentStatisticsReportRequest reportRequest);

    /**
     * 导出统计数据
     * 
     * @param exportRequest 导出请求
     * @return 导出响应
     */
    PaymentStatisticsExportResponse exportStatistics(PaymentStatisticsExportRequest exportRequest);

    /**
     * 获取统计概览数据
     * 
     * @param overviewRequest 概览请求
     * @return 概览响应
     */
    PaymentStatisticsOverviewResponse getStatisticsOverview(PaymentStatisticsOverviewRequest overviewRequest);
} 