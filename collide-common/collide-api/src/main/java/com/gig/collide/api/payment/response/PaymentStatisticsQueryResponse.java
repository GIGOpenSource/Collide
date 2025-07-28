package com.gig.collide.api.payment.response;

import com.gig.collide.api.payment.response.data.PaymentStatisticsInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * 支付统计查询响应
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
public class PaymentStatisticsQueryResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 统计记录列表
     */
    private List<PaymentStatisticsInfo> statisticsRecords;

    /**
     * 查询条件匹配的总数
     */
    private Long totalCount;

    /**
     * 是否还有更多数据
     */
    private Boolean hasMore;

    /**
     * 汇总信息
     */
    private SummaryInfo summaryInfo;

    /**
     * 汇总统计信息
     */
    @Getter
    @Setter
    public static class SummaryInfo {
        /**
         * 总交易笔数
         */
        private Long totalTransactionCount;

        /**
         * 总交易金额
         */
        private BigDecimal totalTransactionAmount;

        /**
         * 总成功笔数
         */
        private Long totalSuccessCount;

        /**
         * 总成功金额
         */
        private BigDecimal totalSuccessAmount;

        /**
         * 平均成功率
         */
        private BigDecimal averageSuccessRate;

        /**
         * 平均交易金额
         */
        private BigDecimal averageTransactionAmount;

        /**
         * 统计日期范围
         */
        private String dateRange;
    }
} 