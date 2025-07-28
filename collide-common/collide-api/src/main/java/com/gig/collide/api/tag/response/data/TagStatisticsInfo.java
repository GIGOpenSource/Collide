package com.gig.collide.api.tag.response.data;

import com.gig.collide.api.tag.constant.StatisticsDimensionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 标签统计信息传输对象
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TagStatisticsInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 统计ID
     */
    private Long id;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 标签类型
     */
    private String tagType;

    /**
     * 日使用次数
     */
    private Long dailyUsageCount;

    /**
     * 周使用次数
     */
    private Long weeklyUsageCount;

    /**
     * 月使用次数
     */
    private Long monthlyUsageCount;

    /**
     * 总用户数
     */
    private Long totalUserCount;

    /**
     * 活跃用户数
     */
    private Long activeUserCount;

    /**
     * 关联内容数
     */
    private Long contentCount;

    /**
     * 统计日期
     */
    private LocalDate statDate;

    /**
     * 统计维度
     */
    private StatisticsDimensionEnum statisticsDimension;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 趋势数据（按日期统计）
     */
    private Map<String, Long> trendData;

    /**
     * 排行榜数据
     */
    private List<TagRankInfo> rankingData;

    /**
     * 用户活跃度分布
     */
    private Map<String, Long> userActivityDistribution;

    /**
     * 获取用户活跃率
     */
    public Double getUserActiveRate() {
        if (totalUserCount == null || totalUserCount == 0) {
            return 0.0;
        }
        return (double) (activeUserCount != null ? activeUserCount : 0) / totalUserCount * 100;
    }

    /**
     * 获取平均使用次数
     */
    public Double getAverageUsageCount() {
        if (totalUserCount == null || totalUserCount == 0) {
            return 0.0;
        }
        Long totalUsage = (dailyUsageCount != null ? dailyUsageCount : 0) +
                         (weeklyUsageCount != null ? weeklyUsageCount : 0) +
                         (monthlyUsageCount != null ? monthlyUsageCount : 0);
        return (double) totalUsage / totalUserCount;
    }

    /**
     * 标签排行信息
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class TagRankInfo implements Serializable {
        private Long tagId;
        private String tagName;
        private Long usageCount;
        private Integer rank;
        private Double growthRate;
    }
} 