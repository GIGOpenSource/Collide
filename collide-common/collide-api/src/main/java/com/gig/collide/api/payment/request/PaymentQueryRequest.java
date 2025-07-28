package com.gig.collide.api.payment.request;

import com.gig.collide.api.payment.request.condition.*;
import com.gig.collide.base.request.BaseRequest;
import lombok.*;

/**
 * 支付查询请求
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PaymentQueryRequest extends BaseRequest {

    /**
     * 查询条件
     */
    private PaymentQueryCondition paymentQueryCondition;

    // ===================== 便捷构造器 =====================

    /**
     * 根据订单号查询
     */
    public PaymentQueryRequest(String orderNo) {
        PaymentOrderNoQueryCondition condition = new PaymentOrderNoQueryCondition();
        condition.setOrderNo(orderNo);
        this.paymentQueryCondition = condition;
    }

    /**
     * 根据用户ID查询
     */
    public PaymentQueryRequest(Long userId) {
        PaymentUserIdQueryCondition condition = new PaymentUserIdQueryCondition();
        condition.setUserId(userId);
        this.paymentQueryCondition = condition;
    }

    /**
     * 根据内部交易流水号查询
     */
    public static PaymentQueryRequest byInternalTransactionNo(String internalTransactionNo) {
        PaymentQueryRequest request = new PaymentQueryRequest();
        PaymentInternalTransactionNoQueryCondition condition = new PaymentInternalTransactionNoQueryCondition();
        condition.setInternalTransactionNo(internalTransactionNo);
        request.setPaymentQueryCondition(condition);
        return request;
    }

    /**
     * 根据第三方交易流水号查询
     */
    public static PaymentQueryRequest byTransactionNo(String transactionNo) {
        PaymentQueryRequest request = new PaymentQueryRequest();
        PaymentTransactionNoQueryCondition condition = new PaymentTransactionNoQueryCondition();
        condition.setTransactionNo(transactionNo);
        request.setPaymentQueryCondition(condition);
        return request;
    }
} 