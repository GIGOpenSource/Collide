package com.gig.collide.api.payment.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * 支付统计排行榜响应
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
public class PaymentStatisticsRankingResponse extends BaseResponse {

    /**
     * 排行榜数据列表
     */
    private List<RankingItem> rankings;

    /**
     * 排行榜类型
     */
    private String rankingType;

    /**
     * 统计时间范围
     */
    private String timeRange;

    /**
     * 排行榜项
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class RankingItem {
        /**
         * 排名
         */
        private Integer rank;

        /**
         * 标识ID（用户ID、商户ID等）
         */
        private String itemId;

        /**
         * 标识名称
         */
        private String itemName;

        /**
         * 数值
         */
        private BigDecimal value;

        /**
         * 计数
         */
        private Long count;

        /**
         * 成功率
         */
        private BigDecimal successRate;

        /**
         * 占比
         */
        private BigDecimal percentage;
    }
} 