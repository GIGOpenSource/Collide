package com.gig.collide.api.user.request;

import com.gig.collide.base.request.BaseRequest;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户邀请关系统计请求
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
public class UserInviteRelationStatisticsRequest extends BaseRequest {

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
     * 邀请层级列表（可选，为空时统计所有层级）
     */
    private List<Integer> inviteLevels;

    /**
     * 邀请人ID列表（可选，为空时统计所有邀请人）
     */
    private List<Long> inviterIds;

    /**
     * 是否包含详细数据（默认false）
     */
    private Boolean includeDetails = false;

    /**
     * 排行榜数量限制（默认20）
     */
    private Integer rankingLimit = 20;

    // ===================== 便捷构造器 =====================

    /**
     * 创建今日邀请统计请求
     */
    public static UserInviteRelationStatisticsRequest todayStatistics() {
        UserInviteRelationStatisticsRequest request = new UserInviteRelationStatisticsRequest();
        LocalDateTime now = LocalDateTime.now();
        request.setStartTime(now.withHour(0).withMinute(0).withSecond(0));
        request.setEndTime(now.withHour(23).withMinute(59).withSecond(59));
        request.setStatisticsDimension("day");
        return request;
    }

    /**
     * 创建本周邀请统计请求
     */
    public static UserInviteRelationStatisticsRequest thisWeekStatistics() {
        UserInviteRelationStatisticsRequest request = new UserInviteRelationStatisticsRequest();
        LocalDateTime now = LocalDateTime.now();
        request.setStartTime(now.minusDays(now.getDayOfWeek().getValue() - 1).withHour(0).withMinute(0).withSecond(0));
        request.setEndTime(now.withHour(23).withMinute(59).withSecond(59));
        request.setStatisticsDimension("week");
        return request;
    }

    /**
     * 创建本月邀请统计请求
     */
    public static UserInviteRelationStatisticsRequest thisMonthStatistics() {
        UserInviteRelationStatisticsRequest request = new UserInviteRelationStatisticsRequest();
        LocalDateTime now = LocalDateTime.now();
        request.setStartTime(now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0));
        request.setEndTime(now.withHour(23).withMinute(59).withSecond(59));
        request.setStatisticsDimension("month");
        return request;
    }

    /**
     * 创建邀请排行榜统计请求
     */
    public static UserInviteRelationStatisticsRequest inviteRankingStatistics(LocalDateTime startTime, LocalDateTime endTime, Integer limit) {
        UserInviteRelationStatisticsRequest request = new UserInviteRelationStatisticsRequest();
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setRankingLimit(limit);
        request.setIncludeDetails(true);
        return request;
    }
} 