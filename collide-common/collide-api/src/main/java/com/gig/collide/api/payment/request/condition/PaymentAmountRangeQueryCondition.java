package com.gig.collide.api.payment.request.condition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 支付金额范围查询条件
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class PaymentAmountRangeQueryCondition implements PaymentQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 最小金额
     */
    private BigDecimal minAmount;

    /**
     * 最大金额
     */
    private BigDecimal maxAmount;

    /**
     * 金额字段类型：PAY_AMOUNT-支付金额，ACTUAL_PAY_AMOUNT-实际支付金额
     */
    private String amountField = "PAY_AMOUNT";

    public PaymentAmountRangeQueryCondition(BigDecimal minAmount, BigDecimal maxAmount) {
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    public PaymentAmountRangeQueryCondition(BigDecimal minAmount, BigDecimal maxAmount, String amountField) {
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.amountField = amountField;
    }
} 