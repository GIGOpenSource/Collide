package com.gig.collide.api.payment.request.condition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 支付时间范围查询条件
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class PaymentTimeRangeQueryCondition implements PaymentQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 时间字段类型：CREATE_TIME-创建时间，PAY_TIME-支付时间，COMPLETE_TIME-完成时间
     */
    private String timeField = "CREATE_TIME";

    public PaymentTimeRangeQueryCondition(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public PaymentTimeRangeQueryCondition(LocalDateTime startTime, LocalDateTime endTime, String timeField) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeField = timeField;
    }
} 