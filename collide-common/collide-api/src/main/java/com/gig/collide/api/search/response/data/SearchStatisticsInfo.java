package com.gig.collide.api.search.response.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 搜索统计信息传输对象
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class SearchStatisticsInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 统计ID
     */
    private Long id;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 搜索总次数
     */
    private Long searchCount;

    /**
     * 唯一用户搜索数
     */
    private Long uniqueUserCount;

    /**
     * 今日搜索次数
     */
    private Long todayCount;

    /**
     * 本周搜索次数
     */
    private Long weekCount;

    /**
     * 本月搜索次数
     */
    private Long monthCount;

    /**
     * 平均结果数量
     */
    private Long avgResultCount;

    /**
     * 平均搜索耗时（毫秒）
     */
    private Long avgSearchTime;

    /**
     * 最大结果数量
     */
    private Long maxResultCount;

    /**
     * 最小结果数量
     */
    private Long minResultCount;

    /**
     * 综合搜索次数
     */
    private Long allSearchCount;

    /**
     * 用户搜索次数
     */
    private Long userSearchCount;

    /**
     * 内容搜索次数
     */
    private Long contentSearchCount;

    /**
     * 评论搜索次数
     */
    private Long commentSearchCount;

    /**
     * 热度评分
     */
    private BigDecimal hotScore;

    /**
     * 排名分数
     */
    private BigDecimal rankScore;

    /**
     * 趋势分数
     */
    private BigDecimal trendScore;

    /**
     * 首次搜索时间
     */
    private LocalDateTime firstSearchTime;

    /**
     * 最后搜索时间
     */
    private LocalDateTime lastSearchTime;

    /**
     * 搜索高峰时间
     */
    private LocalDateTime peakTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    // ===================== 便捷方法 =====================

    /**
     * 获取用户搜索活跃度
     */
    public Double getUserActivityRate() {
        if (searchCount == null || searchCount == 0) {
            return 0.0;
        }
        return uniqueUserCount != null ? (double) uniqueUserCount / searchCount : 0.0;
    }

    /**
     * 获取今日搜索增长率
     */
    public Double getTodayGrowthRate() {
        if (weekCount == null || weekCount == 0) {
            return 0.0;
        }
        Long avgWeeklyDaily = weekCount / 7;
        if (avgWeeklyDaily == 0) {
            return todayCount != null && todayCount > 0 ? 100.0 : 0.0;
        }
        return todayCount != null ? ((double) (todayCount - avgWeeklyDaily) / avgWeeklyDaily) * 100 : 0.0;
    }

    /**
     * 获取格式化的平均搜索耗时
     */
    public String getFormattedAvgSearchTime() {
        if (avgSearchTime == null) {
            return "未知";
        }
        if (avgSearchTime < 1000) {
            return avgSearchTime + "ms";
        } else {
            return String.format("%.2fs", avgSearchTime / 1000.0);
        }
    }

    /**
     * 获取搜索类型分布
     */
    public String getSearchTypeDistribution() {
        if (searchCount == null || searchCount == 0) {
            return "无数据";
        }
        
        StringBuilder sb = new StringBuilder();
        if (allSearchCount != null && allSearchCount > 0) {
            sb.append("综合:").append(String.format("%.1f%%", (double) allSearchCount / searchCount * 100));
        }
        if (userSearchCount != null && userSearchCount > 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("用户:").append(String.format("%.1f%%", (double) userSearchCount / searchCount * 100));
        }
        if (contentSearchCount != null && contentSearchCount > 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("内容:").append(String.format("%.1f%%", (double) contentSearchCount / searchCount * 100));
        }
        if (commentSearchCount != null && commentSearchCount > 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("评论:").append(String.format("%.1f%%", (double) commentSearchCount / searchCount * 100));
        }
        
        return sb.length() > 0 ? sb.toString() : "无数据";
    }

    /**
     * 检查是否为热门关键词
     */
    public boolean isHotKeyword() {
        return hotScore != null && hotScore.compareTo(BigDecimal.valueOf(80)) >= 0;
    }

    /**
     * 检查是否为上升趋势
     */
    public boolean isTrendingUp() {
        return trendScore != null && trendScore.compareTo(BigDecimal.ZERO) > 0;
    }
} 