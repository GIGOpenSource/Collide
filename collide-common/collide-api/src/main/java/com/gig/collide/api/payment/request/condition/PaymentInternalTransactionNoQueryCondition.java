package com.gig.collide.api.payment.request.condition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 内部交易流水号查询条件
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class PaymentInternalTransactionNoQueryCondition implements PaymentQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 内部交易流水号
     */
    private String internalTransactionNo;

    public PaymentInternalTransactionNoQueryCondition(String internalTransactionNo) {
        this.internalTransactionNo = internalTransactionNo;
    }
} 