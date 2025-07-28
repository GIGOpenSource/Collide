package com.gig.collide.api.payment.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付实时统计响应
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
public class PaymentStatisticsRealtimeResponse extends BaseResponse {

    /**
     * 今日交易总额
     */
    private BigDecimal todayTotalAmount;

    /**
     * 今日交易笔数
     */
    private Long todayTotalCount;

    /**
     * 今日成功交易额
     */
    private BigDecimal todaySuccessAmount;

    /**
     * 今日成功交易笔数
     */
    private Long todaySuccessCount;

    /**
     * 今日成功率
     */
    private BigDecimal todaySuccessRate;

    /**
     * 实时交易额（最近1小时）
     */
    private BigDecimal realtimeAmount;

    /**
     * 实时交易笔数（最近1小时）
     */
    private Long realtimeCount;

    /**
     * 按支付方式分组统计
     */
    private Map<String, BigDecimal> paymentMethodStats;

    /**
     * 按状态分组统计
     */
    private Map<String, Long> statusStats;

    /**
     * 统计时间戳
     */
    private Long statisticsTimestamp;
} 