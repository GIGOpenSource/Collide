package com.gig.collide.api.payment.request.condition;

import com.gig.collide.api.payment.constant.PaymentStatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 支付状态查询条件
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class PaymentStatusQueryCondition implements PaymentQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 支付状态（单个）
     */
    private PaymentStatusEnum payStatus;

    /**
     * 支付状态列表（多个）
     */
    private List<PaymentStatusEnum> payStatusList;

    public PaymentStatusQueryCondition(PaymentStatusEnum payStatus) {
        this.payStatus = payStatus;
    }

    public PaymentStatusQueryCondition(List<PaymentStatusEnum> payStatusList) {
        this.payStatusList = payStatusList;
    }
} 