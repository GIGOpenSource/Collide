package com.gig.collide.api.user.response.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户操作日志统计信息传输对象
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class UserOperateLogStatisticsInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 统计时间范围开始
     */
    private LocalDateTime startTime;

    /**
     * 统计时间范围结束
     */
    private LocalDateTime endTime;

    /**
     * 总操作次数
     */
    private Long totalCount;

    /**
     * 今日操作次数
     */
    private Long todayCount;

    /**
     * 按操作类型统计
     */
    private Map<String, Long> operateTypeStats;

    /**
     * 按时间段统计（小时级别）
     */
    private Map<String, Long> hourlyStats;

    /**
     * 按IP地址统计（TOP10）
     */
    private Map<String, Long> ipAddressStats;

    /**
     * 活跃用户排行（TOP10）
     */
    private List<UserActivityRankInfo> activeUserRanking;

    /**
     * 热门操作类型排行
     */
    private List<OperateTypeRankInfo> operateTypeRanking;

    /**
     * 用户活跃度排行信息
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserActivityRankInfo implements Serializable {
        private Long userId;
        private String username;
        private Long operateCount;
        private String lastOperateType;
        private LocalDateTime lastOperateTime;
    }

    /**
     * 操作类型排行信息
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class OperateTypeRankInfo implements Serializable {
        private String operateType;
        private Long count;
        private Double percentage;
    }
} 