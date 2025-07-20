package com.gig.collide.api.tag.response.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 标签统计信息
 * @author GIG
 */
@Getter
@Setter
@NoArgsConstructor
public class TagStatistics implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 总使用次数
     */
    private Long totalUsageCount;

    /**
     * 用户使用数量
     */
    private Long userCount;

    /**
     * 内容关联数量
     */
    private Long contentCount;

    /**
     * 今日使用次数
     */
    private Long todayUsageCount;

    /**
     * 本周使用次数
     */
    private Long weekUsageCount;

    /**
     * 本月使用次数
     */
    private Long monthUsageCount;

    /**
     * 热度趋势（近7天每日使用次数）
     */
    private Map<String, Long> hotTrend;

    /**
     * 相关标签列表
     */
    private String relatedTags;

    /**
     * 平均使用频率
     */
    private Double avgUsageFrequency;

    /**
     * 最后更新时间
     */
    private Date lastUpdated;

    /**
     * 统计周期开始时间
     */
    private Date periodStart;

    /**
     * 统计周期结束时间
     */
    private Date periodEnd;
} 