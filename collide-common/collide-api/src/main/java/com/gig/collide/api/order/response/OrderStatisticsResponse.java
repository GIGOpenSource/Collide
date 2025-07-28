package com.gig.collide.api.order.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 订单统计响应
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class OrderStatisticsResponse extends BaseResponse {

    /**
     * 订单总数
     */
    private Long totalOrderCount;

    /**
     * 已支付订单数
     */
    private Long paidOrderCount;

    /**
     * 已取消订单数
     */
    private Long cancelledOrderCount;

    /**
     * 退款订单数
     */
    private Long refundedOrderCount;

    /**
     * 订单总金额
     */
    private BigDecimal totalOrderAmount;

    /**
     * 已支付金额
     */
    private BigDecimal paidAmount;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 按订单类型分组统计
     */
    private Map<String, Long> orderTypeStats;

    /**
     * 按订单状态分组统计
     */
    private Map<String, Long> orderStatusStats;

    /**
     * 按支付方式分组统计
     */
    private Map<String, BigDecimal> payTypeStats;

    /**
     * 统计时间戳
     */
    private Long statisticsTimestamp;
} 