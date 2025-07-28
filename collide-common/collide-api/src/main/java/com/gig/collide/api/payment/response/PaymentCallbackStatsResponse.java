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
 * 支付回调统计响应
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
public class PaymentCallbackStatsResponse extends BaseResponse {

    /**
     * 总回调次数
     */
    private Long totalCallbacks;

    /**
     * 成功回调次数
     */
    private Long successCallbacks;

    /**
     * 失败回调次数
     */
    private Long failedCallbacks;

    /**
     * 成功率
     */
    private BigDecimal successRate;

    /**
     * 平均处理时间（毫秒）
     */
    private Long averageProcessTime;

    /**
     * 按渠道分组统计
     */
    private Map<String, Long> channelStats;

    /**
     * 按状态分组统计
     */
    private Map<String, Long> statusStats;

    /**
     * 统计时间范围
     */
    private String timeRange;
} 