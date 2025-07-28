package com.gig.collide.api.user.request;

import com.gig.collide.base.request.BaseRequest;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户操作日志统计请求
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserOperateLogStatisticsRequest extends BaseRequest {

    /**
     * 统计开始时间（必填）
     */
    private LocalDateTime startTime;

    /**
     * 统计结束时间（必填）
     */
    private LocalDateTime endTime;

    /**
     * 统计维度（可选）：day-按天，week-按周，month-按月
     */
    private String statisticsDimension = "day";

    /**
     * 操作类型列表（可选，为空时统计所有类型）
     */
    private List<String> operateTypes;

    /**
     * 用户ID列表（可选，为空时统计所有用户）
     */
    private List<Long> userIds;

    /**
     * IP地址列表（可选，为空时统计所有IP）
     */
    private List<String> ipAddresses;

    /**
     * 是否包含详细数据（默认false）
     */
    private Boolean includeDetails = false;

    // ===================== 便捷构造器 =====================

    /**
     * 创建今日统计请求
     */
    public static UserOperateLogStatisticsRequest todayStatistics() {
        UserOperateLogStatisticsRequest request = new UserOperateLogStatisticsRequest();
        LocalDateTime now = LocalDateTime.now();
        request.setStartTime(now.withHour(0).withMinute(0).withSecond(0));
        request.setEndTime(now.withHour(23).withMinute(59).withSecond(59));
        request.setStatisticsDimension("day");
        return request;
    }

    /**
     * 创建本周统计请求
     */
    public static UserOperateLogStatisticsRequest thisWeekStatistics() {
        UserOperateLogStatisticsRequest request = new UserOperateLogStatisticsRequest();
        LocalDateTime now = LocalDateTime.now();
        request.setStartTime(now.minusDays(now.getDayOfWeek().getValue() - 1).withHour(0).withMinute(0).withSecond(0));
        request.setEndTime(now.withHour(23).withMinute(59).withSecond(59));
        request.setStatisticsDimension("week");
        return request;
    }

    /**
     * 创建本月统计请求
     */
    public static UserOperateLogStatisticsRequest thisMonthStatistics() {
        UserOperateLogStatisticsRequest request = new UserOperateLogStatisticsRequest();
        LocalDateTime now = LocalDateTime.now();
        request.setStartTime(now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0));
        request.setEndTime(now.withHour(23).withMinute(59).withSecond(59));
        request.setStatisticsDimension("month");
        return request;
    }
} 