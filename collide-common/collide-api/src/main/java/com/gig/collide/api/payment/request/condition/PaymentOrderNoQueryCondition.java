package com.gig.collide.api.payment.request.condition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 订单号查询条件
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class PaymentOrderNoQueryCondition implements PaymentQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    private String orderNo;

    public PaymentOrderNoQueryCondition(String orderNo) {
        this.orderNo = orderNo;
    }
} 