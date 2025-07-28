package com.gig.collide.api.search.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 搜索性能统计响应
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
public class SearchPerformanceStatisticsResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 统计开始时间
     */
    private String startTime;

    /**
     * 统计结束时间
     */
    private String endTime;

    /**
     * 总体性能指标
     */
    private OverallPerformance overall;

    /**
     * 响应时间统计
     */
    private ResponseTimeStatistics responseTime;

    /**
     * 吞吐量统计
     */
    private ThroughputStatistics throughput;

    /**
     * 错误率统计
     */
    private ErrorRateStatistics errorRate;

    /**
     * 资源使用统计
     */
    private ResourceUsageStatistics resourceUsage;

    /**
     * 时段性能数据
     */
    private List<TimeSlotPerformance> timeSlotData;

    /**
     * 性能告警信息
     */
    private List<PerformanceAlert> alerts;

    /**
     * 总体性能指标
     */
    @Getter
    @Setter
    @ToString
    public static class OverallPerformance {
        private Long totalRequests;
        private Long successfulRequests;
        private Long failedRequests;
        private Double successRate;
        private Double avgResponseTime;
        private Long maxResponseTime;
        private Long minResponseTime;
        private Double requestsPerSecond;
    }

    /**
     * 响应时间统计
     */
    @Getter
    @Setter
    @ToString
    public static class ResponseTimeStatistics {
        private Double avgResponseTime;
        private Long p50ResponseTime;
        private Long p90ResponseTime;
        private Long p95ResponseTime;
        private Long p99ResponseTime;
        private Long maxResponseTime;
        private Long minResponseTime;
        private Double responseTimeStdDev;
    }

    /**
     * 吞吐量统计
     */
    @Getter
    @Setter
    @ToString
    public static class ThroughputStatistics {
        private Double avgRequestsPerSecond;
        private Double maxRequestsPerSecond;
        private Double minRequestsPerSecond;
        private Double avgRequestsPerMinute;
        private Double avgRequestsPerHour;
        private Double peakHourThroughput;
        private String peakHour;
    }

    /**
     * 错误率统计
     */
    @Getter
    @Setter
    @ToString
    public static class ErrorRateStatistics {
        private Double overallErrorRate;
        private Long totalErrors;
        private Long timeoutErrors;
        private Long serverErrors;
        private Long clientErrors;
        private List<ErrorTypeCount> errorBreakdown;
    }

    /**
     * 资源使用统计
     */
    @Getter
    @Setter
    @ToString
    public static class ResourceUsageStatistics {
        private Double avgCpuUsage;
        private Double maxCpuUsage;
        private Double avgMemoryUsage;
        private Double maxMemoryUsage;
        private Double avgDiskIo;
        private Double avgNetworkIo;
        private Integer activeConnections;
        private Integer maxConnections;
    }

    /**
     * 时段性能数据
     */
    @Getter
    @Setter
    @ToString
    public static class TimeSlotPerformance {
        private String timeSlot;
        private Long requestCount;
        private Double avgResponseTime;
        private Double errorRate;
        private Double throughput;
        private Double cpuUsage;
        private Double memoryUsage;
    }

    /**
     * 性能告警
     */
    @Getter
    @Setter
    @ToString
    public static class PerformanceAlert {
        private String alertType;
        private String alertLevel; // info/warning/error/critical
        private String message;
        private String metricName;
        private String thresholdValue;
        private String actualValue;
        private String alertTime;
    }

    /**
     * 错误类型统计
     */
    @Getter
    @Setter
    @ToString
    public static class ErrorTypeCount {
        private String errorType;
        private String errorCode;
        private Long count;
        private Double percentage;
    }
} 