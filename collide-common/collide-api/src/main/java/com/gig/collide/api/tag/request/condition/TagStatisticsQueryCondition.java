package com.gig.collide.api.tag.request.condition;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 标签统计查询条件
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TagStatisticsQueryCondition implements TagQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 标签ID列表
     */
    private List<Long> tagIds;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 统计日期
     */
    private LocalDate statDate;

    /**
     * 统计维度
     */
    private String statisticsDimension;
} 