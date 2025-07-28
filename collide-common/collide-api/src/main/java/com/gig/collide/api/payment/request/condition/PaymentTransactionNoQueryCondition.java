package com.gig.collide.api.payment.request.condition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 第三方交易流水号查询条件
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class PaymentTransactionNoQueryCondition implements PaymentQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 第三方交易流水号
     */
    private String transactionNo;

    public PaymentTransactionNoQueryCondition(String transactionNo) {
        this.transactionNo = transactionNo;
    }
} 