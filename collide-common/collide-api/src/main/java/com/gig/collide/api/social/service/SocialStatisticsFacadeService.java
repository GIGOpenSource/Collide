package com.gig.collide.api.social.service;

import com.gig.collide.api.social.request.SocialStatisticsRequest;
import com.gig.collide.api.social.response.SocialStatisticsResponse;
import com.gig.collide.api.social.response.data.SocialStatisticsInfo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 社交统计门面服务接口
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
public interface SocialStatisticsFacadeService {
    
    /**
     * 获取综合统计数据
     */
    SocialStatisticsResponse getStatistics(SocialStatisticsRequest request);
    
    /**
     * 获取今日统计数据
     */
    SocialStatisticsResponse getTodayStatistics();
    
    /**
     * 获取本周统计数据
     */
    SocialStatisticsResponse getThisWeekStatistics();
    
    /**
     * 获取本月统计数据
     */
    SocialStatisticsResponse getThisMonthStatistics();
    
    /**
     * 获取用户活跃度统计
     */
    SocialStatisticsResponse getUserActivityStatistics(LocalDateTime startTime, LocalDateTime endTime, Integer limit);
    
    /**
     * 获取热门动态排行
     */
    SocialStatisticsResponse getHotPostRanking(LocalDateTime startTime, LocalDateTime endTime, Integer limit);
    
    /**
     * 获取话题热度排行
     */
    SocialStatisticsResponse getTopicHeatRanking(LocalDateTime startTime, LocalDateTime endTime, Integer limit);
    
    /**
     * 获取实时统计数据
     */
    RealtimeStatistics getRealtimeStatistics();
    
    /**
     * 获取用户行为趋势分析
     */
    List<TrendData> getUserBehaviorTrend(LocalDateTime startTime, LocalDateTime endTime, String dimension);
    
    /**
     * 获取内容发布趋势分析
     */
    List<TrendData> getContentPublishTrend(LocalDateTime startTime, LocalDateTime endTime, String dimension);
    
    /**
     * 获取互动趋势分析
     */
    List<TrendData> getInteractionTrend(LocalDateTime startTime, LocalDateTime endTime, String dimension);
    
    /**
     * 获取平台增长指标
     */
    GrowthMetrics getGrowthMetrics(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取用户留存分析
     */
    RetentionAnalysis getUserRetentionAnalysis(LocalDateTime cohortStartTime, Integer periodDays);
    
    /**
     * 获取内容质量分析
     */
    ContentQualityAnalysis getContentQualityAnalysis(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 生成统计报告
     */
    StatisticsReport generateReport(LocalDateTime startTime, LocalDateTime endTime, String reportType);
    
    /**
     * 导出统计数据
     */
    String exportStatistics(SocialStatisticsRequest request, String format);
    
    /**
     * 刷新统计缓存
     */
    void refreshStatisticsCache();
    
    /**
     * 预计算统计数据（定时任务使用）
     */
    void precomputeStatistics(LocalDateTime targetDate);
    
    /**
     * 实时统计数据
     */
    class RealtimeStatistics {
        private Long onlineUsers;
        private Long postsToday;
        private Long interactionsToday;
        private Long newUsersToday;
        private Double avgResponseTime;
        private List<HotTopic> hotTopics;
        
        // getters and setters
        public Long getOnlineUsers() { return onlineUsers; }
        public void setOnlineUsers(Long onlineUsers) { this.onlineUsers = onlineUsers; }
        public Long getPostsToday() { return postsToday; }
        public void setPostsToday(Long postsToday) { this.postsToday = postsToday; }
        public Long getInteractionsToday() { return interactionsToday; }
        public void setInteractionsToday(Long interactionsToday) { this.interactionsToday = interactionsToday; }
        public Long getNewUsersToday() { return newUsersToday; }
        public void setNewUsersToday(Long newUsersToday) { this.newUsersToday = newUsersToday; }
        public Double getAvgResponseTime() { return avgResponseTime; }
        public void setAvgResponseTime(Double avgResponseTime) { this.avgResponseTime = avgResponseTime; }
        public List<HotTopic> getHotTopics() { return hotTopics; }
        public void setHotTopics(List<HotTopic> hotTopics) { this.hotTopics = hotTopics; }
    }
    
    /**
     * 趋势数据
     */
    class TrendData {
        private LocalDateTime time;
        private Long value;
        private Double growthRate;
        private String dimension;
        
        // getters and setters
        public LocalDateTime getTime() { return time; }
        public void setTime(LocalDateTime time) { this.time = time; }
        public Long getValue() { return value; }
        public void setValue(Long value) { this.value = value; }
        public Double getGrowthRate() { return growthRate; }
        public void setGrowthRate(Double growthRate) { this.growthRate = growthRate; }
        public String getDimension() { return dimension; }
        public void setDimension(String dimension) { this.dimension = dimension; }
    }
    
    /**
     * 增长指标
     */
    class GrowthMetrics {
        private Double userGrowthRate;
        private Double postGrowthRate;
        private Double interactionGrowthRate;
        private Double retentionRate;
        private Double engagementRate;
        private Integer dau; // 日活跃用户
        private Integer mau; // 月活跃用户
        
        // getters and setters
        public Double getUserGrowthRate() { return userGrowthRate; }
        public void setUserGrowthRate(Double userGrowthRate) { this.userGrowthRate = userGrowthRate; }
        public Double getPostGrowthRate() { return postGrowthRate; }
        public void setPostGrowthRate(Double postGrowthRate) { this.postGrowthRate = postGrowthRate; }
        public Double getInteractionGrowthRate() { return interactionGrowthRate; }
        public void setInteractionGrowthRate(Double interactionGrowthRate) { this.interactionGrowthRate = interactionGrowthRate; }
        public Double getRetentionRate() { return retentionRate; }
        public void setRetentionRate(Double retentionRate) { this.retentionRate = retentionRate; }
        public Double getEngagementRate() { return engagementRate; }
        public void setEngagementRate(Double engagementRate) { this.engagementRate = engagementRate; }
        public Integer getDau() { return dau; }
        public void setDau(Integer dau) { this.dau = dau; }
        public Integer getMau() { return mau; }
        public void setMau(Integer mau) { this.mau = mau; }
    }
    
    /**
     * 留存分析
     */
    class RetentionAnalysis {
        private LocalDateTime cohortStartTime;
        private Integer cohortSize;
        private Map<Integer, Double> retentionRates; // day -> retention rate
        private Double avgRetentionRate;
        
        // getters and setters
        public LocalDateTime getCohortStartTime() { return cohortStartTime; }
        public void setCohortStartTime(LocalDateTime cohortStartTime) { this.cohortStartTime = cohortStartTime; }
        public Integer getCohortSize() { return cohortSize; }
        public void setCohortSize(Integer cohortSize) { this.cohortSize = cohortSize; }
        public Map<Integer, Double> getRetentionRates() { return retentionRates; }
        public void setRetentionRates(Map<Integer, Double> retentionRates) { this.retentionRates = retentionRates; }
        public Double getAvgRetentionRate() { return avgRetentionRate; }
        public void setAvgRetentionRate(Double avgRetentionRate) { this.avgRetentionRate = avgRetentionRate; }
    }
    
    /**
     * 内容质量分析
     */
    class ContentQualityAnalysis {
        private Double avgEngagementRate;
        private Double highQualityContentRatio;
        private Long totalOriginalContent;
        private Long totalSharedContent;
        private List<QualityMetric> qualityMetrics;
        
        // getters and setters
        public Double getAvgEngagementRate() { return avgEngagementRate; }
        public void setAvgEngagementRate(Double avgEngagementRate) { this.avgEngagementRate = avgEngagementRate; }
        public Double getHighQualityContentRatio() { return highQualityContentRatio; }
        public void setHighQualityContentRatio(Double highQualityContentRatio) { this.highQualityContentRatio = highQualityContentRatio; }
        public Long getTotalOriginalContent() { return totalOriginalContent; }
        public void setTotalOriginalContent(Long totalOriginalContent) { this.totalOriginalContent = totalOriginalContent; }
        public Long getTotalSharedContent() { return totalSharedContent; }
        public void setTotalSharedContent(Long totalSharedContent) { this.totalSharedContent = totalSharedContent; }
        public List<QualityMetric> getQualityMetrics() { return qualityMetrics; }
        public void setQualityMetrics(List<QualityMetric> qualityMetrics) { this.qualityMetrics = qualityMetrics; }
    }
    
    /**
     * 质量指标
     */
    class QualityMetric {
        private String metricName;
        private Double value;
        private String description;
        
        // getters and setters
        public String getMetricName() { return metricName; }
        public void setMetricName(String metricName) { this.metricName = metricName; }
        public Double getValue() { return value; }
        public void setValue(Double value) { this.value = value; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    /**
     * 热门话题
     */
    class HotTopic {
        private String topic;
        private Long mentionCount;
        private Double heatScore;
        
        // getters and setters
        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }
        public Long getMentionCount() { return mentionCount; }
        public void setMentionCount(Long mentionCount) { this.mentionCount = mentionCount; }
        public Double getHeatScore() { return heatScore; }
        public void setHeatScore(Double heatScore) { this.heatScore = heatScore; }
    }
    
    /**
     * 统计报告
     */
    class StatisticsReport {
        private String reportId;
        private String reportType;
        private LocalDateTime generatedTime;
        private String summary;
        private List<ReportSection> sections;
        
        // getters and setters
        public String getReportId() { return reportId; }
        public void setReportId(String reportId) { this.reportId = reportId; }
        public String getReportType() { return reportType; }
        public void setReportType(String reportType) { this.reportType = reportType; }
        public LocalDateTime getGeneratedTime() { return generatedTime; }
        public void setGeneratedTime(LocalDateTime generatedTime) { this.generatedTime = generatedTime; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public List<ReportSection> getSections() { return sections; }
        public void setSections(List<ReportSection> sections) { this.sections = sections; }
    }
    
    /**
     * 报告章节
     */
    class ReportSection {
        private String title;
        private String content;
        private List<Chart> charts;
        
        // getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public List<Chart> getCharts() { return charts; }
        public void setCharts(List<Chart> charts) { this.charts = charts; }
    }
    
    /**
     * 图表数据
     */
    class Chart {
        private String chartType;
        private String title;
        private Object data;
        
        // getters and setters
        public String getChartType() { return chartType; }
        public void setChartType(String chartType) { this.chartType = chartType; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
    }
} 