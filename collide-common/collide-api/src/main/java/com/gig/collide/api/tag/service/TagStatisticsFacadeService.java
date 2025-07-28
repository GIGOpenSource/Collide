package com.gig.collide.api.tag.service;

import com.gig.collide.api.tag.request.*;
import com.gig.collide.api.tag.response.*;
import com.gig.collide.api.tag.response.data.TagStatisticsInfo;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.base.response.BaseResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * 标签统计门面服务接口
 * 提供标签统计分析功能
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public interface TagStatisticsFacadeService {

    /**
     * 获取标签统计信息
     * 
     * @param statisticsRequest 统计请求
     * @return 统计响应
     */
    TagStatisticsResponse getTagStatistics(TagStatisticsQueryRequest statisticsRequest);

    /**
     * 分页查询标签统计
     * 
     * @param statisticsRequest 分页统计请求
     * @return 分页响应
     */
    PageResponse<TagStatisticsInfo> pageQueryTagStatistics(TagStatisticsQueryRequest statisticsRequest);

    /**
     * 获取今日标签统计
     * 
     * @return 今日统计
     */
    TagStatisticsResponse getTodayTagStatistics();

    /**
     * 获取本周标签统计
     * 
     * @return 本周统计
     */
    TagStatisticsResponse getThisWeekTagStatistics();

    /**
     * 获取本月标签统计
     * 
     * @return 本月统计
     */
    TagStatisticsResponse getThisMonthTagStatistics();

    /**
     * 获取标签使用趋势
     * 
     * @param tagId 标签ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param dimension 统计维度
     * @return 趋势数据
     */
    TagStatisticsResponse getTagUsageTrend(Long tagId, LocalDate startDate, LocalDate endDate, String dimension);

    /**
     * 获取热门标签排行榜
     * 
     * @param tagType 标签类型
     * @param limit 排行数量
     * @param timeRange 时间范围
     * @return 排行榜
     */
    TagStatisticsResponse getHotTagRanking(String tagType, Integer limit, String timeRange);

    /**
     * 获取标签增长率排行
     * 
     * @param tagType 标签类型
     * @param limit 排行数量
     * @param compareDate 对比日期
     * @return 增长率排行
     */
    TagStatisticsResponse getTagGrowthRateRanking(String tagType, Integer limit, LocalDate compareDate);

    /**
     * 获取用户活跃度统计
     * 
     * @param tagId 标签ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 用户活跃度统计
     */
    TagStatisticsResponse getUserActivityStatistics(Long tagId, LocalDate startDate, LocalDate endDate);

    /**
     * 生成标签统计报告
     * 
     * @param tagIds 标签ID列表
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param reportType 报告类型（summary、detailed、trend）
     * @return 统计报告
     */
    TagStatisticsResponse generateTagStatisticsReport(List<Long> tagIds, LocalDate startDate, LocalDate endDate, String reportType);

    /**
     * 更新标签统计数据
     * 
     * @param tagId 标签ID
     * @param statDate 统计日期
     * @return 更新结果
     */
    BaseResponse updateTagStatistics(Long tagId, LocalDate statDate);

    /**
     * 批量更新标签统计数据
     * 
     * @param tagIds 标签ID列表
     * @param statDate 统计日期
     * @return 更新结果
     */
    BaseResponse batchUpdateTagStatistics(List<Long> tagIds, LocalDate statDate);

    /**
     * 清理过期统计数据
     * 
     * @param retentionDays 保留天数
     * @param operatorId 操作员ID
     * @return 清理结果
     */
    BaseResponse cleanExpiredStatistics(Integer retentionDays, Long operatorId);

    /**
     * 获取标签统计概览
     * 
     * @return 统计概览
     */
    TagStatisticsResponse getTagStatisticsOverview();

    /**
     * 导出标签统计数据
     * 
     * @param statisticsRequest 统计请求
     * @param exportFormat 导出格式（excel、csv、json）
     * @return 导出结果
     */
    BaseResponse exportTagStatistics(TagStatisticsQueryRequest statisticsRequest, String exportFormat);
} 