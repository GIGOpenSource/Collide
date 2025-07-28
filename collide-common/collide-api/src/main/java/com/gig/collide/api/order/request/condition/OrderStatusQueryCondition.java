package com.gig.collide.api.order.request.condition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 按订单状态查询条件
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
public class OrderStatusQueryCondition implements OrderQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 订单状态
     */
    private String status;

    @Override
    public String getConditionType() {
        return "STATUS";
    }
} 