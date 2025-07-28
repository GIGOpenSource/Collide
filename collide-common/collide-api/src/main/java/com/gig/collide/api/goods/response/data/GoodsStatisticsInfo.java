package com.gig.collide.api.goods.response.data;

import com.gig.collide.api.goods.constant.GoodsType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品统计信息传输对象
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class GoodsStatisticsInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品类型
     */
    private GoodsType type;

    /**
     * 商品总数
     */
    private Long totalCount;

    /**
     * 在售商品数
     */
    private Long onSaleCount;

    /**
     * 下架商品数
     */
    private Long offSaleCount;

    /**
     * 售罄商品数
     */
    private Long soldOutCount;

    /**
     * 总销量
     */
    private Long totalSoldCount;

    /**
     * 总销售额
     */
    private BigDecimal totalSalesAmount;

    /**
     * 平均价格
     */
    private BigDecimal averagePrice;

    /**
     * 推荐商品数
     */
    private Long recommendedCount;

    /**
     * 热门商品数
     */
    private Long hotCount;

    // ===================== 计算属性 =====================

    /**
     * 上架率
     */
    public Double getOnSaleRate() {
        if (totalCount == null || totalCount == 0) {
            return 0.0;
        }
        return (double) (onSaleCount == null ? 0 : onSaleCount) / totalCount * 100;
    }

    /**
     * 推荐率
     */
    public Double getRecommendedRate() {
        if (totalCount == null || totalCount == 0) {
            return 0.0;
        }
        return (double) (recommendedCount == null ? 0 : recommendedCount) / totalCount * 100;
    }

    /**
     * 热门率
     */
    public Double getHotRate() {
        if (totalCount == null || totalCount == 0) {
            return 0.0;
        }
        return (double) (hotCount == null ? 0 : hotCount) / totalCount * 100;
    }

    /**
     * 平均销量
     */
    public Double getAverageSoldCount() {
        if (totalCount == null || totalCount == 0) {
            return 0.0;
        }
        return (double) (totalSoldCount == null ? 0 : totalSoldCount) / totalCount;
    }
} 