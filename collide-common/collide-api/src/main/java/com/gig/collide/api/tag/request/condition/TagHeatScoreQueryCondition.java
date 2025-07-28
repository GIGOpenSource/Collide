package com.gig.collide.api.tag.request.condition;

import lombok.*;

import java.math.BigDecimal;

/**
 * 标签热度分数查询条件
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
public class TagHeatScoreQueryCondition implements TagQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 最小热度分数
     */
    private BigDecimal minHeatScore;

    /**
     * 最大热度分数
     */
    private BigDecimal maxHeatScore;

    /**
     * 精确热度分数
     */
    private BigDecimal heatScore;
} 