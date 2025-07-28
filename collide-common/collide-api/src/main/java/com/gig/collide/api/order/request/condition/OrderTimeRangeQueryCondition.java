package com.gig.collide.api.order.request.condition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 按时间范围查询条件
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderTimeRangeQueryCondition implements OrderQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 开始时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;

    @Override
    public String getConditionType() {
        return "TIME_RANGE";
    }
} 