package com.gig.collide.search.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 搜索统计表实体
 * 
 * @author Collide Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_search_statistics")
public class SearchStatistics implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 统计ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 搜索关键词
     */
    @TableField("keyword")
    private String keyword;

    /**
     * 搜索总次数
     */
    @TableField("search_count")
    private Long searchCount;

    /**
     * 唯一用户搜索数
     */
    @TableField("unique_user_count")
    private Long uniqueUserCount;

    /**
     * 今日搜索次数
     */
    @TableField("today_count")
    private Long todayCount;

    /**
     * 本周搜索次数
     */
    @TableField("week_count")
    private Long weekCount;

    /**
     * 本月搜索次数
     */
    @TableField("month_count")
    private Long monthCount;

    /**
     * 平均结果数量
     */
    @TableField("avg_result_count")
    private Long avgResultCount;

    /**
     * 平均搜索耗时（毫秒）
     */
    @TableField("avg_search_time")
    private Long avgSearchTime;

    /**
     * 最大结果数量
     */
    @TableField("max_result_count")
    private Long maxResultCount;

    /**
     * 最小结果数量
     */
    @TableField("min_result_count")
    private Long minResultCount;

    /**
     * 综合搜索次数
     */
    @TableField("all_search_count")
    private Long allSearchCount;

    /**
     * 用户搜索次数
     */
    @TableField("user_search_count")
    private Long userSearchCount;

    /**
     * 内容搜索次数
     */
    @TableField("content_search_count")
    private Long contentSearchCount;

    /**
     * 评论搜索次数
     */
    @TableField("comment_search_count")
    private Long commentSearchCount;

    /**
     * 热度评分（基于搜索频次和时间衰减）
     */
    @TableField("hot_score")
    private BigDecimal hotScore;

    /**
     * 排名分数
     */
    @TableField("rank_score")
    private BigDecimal rankScore;

    /**
     * 趋势分数（上升/下降趋势）
     */
    @TableField("trend_score")
    private BigDecimal trendScore;

    /**
     * 首次搜索时间
     */
    @TableField("first_search_time")
    private LocalDateTime firstSearchTime;

    /**
     * 最后搜索时间
     */
    @TableField("last_search_time")
    private LocalDateTime lastSearchTime;

    /**
     * 搜索高峰时间
     */
    @TableField("peak_time")
    private LocalDateTime peakTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableField("deleted")
    @TableLogic
    private Integer deleted;
} 