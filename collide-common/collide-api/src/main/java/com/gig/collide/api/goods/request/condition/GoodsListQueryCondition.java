package com.gig.collide.api.goods.request.condition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 商品列表查询条件
 * 用于通用的商品列表查询，支持多种筛选条件
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class GoodsListQueryCondition extends GoodsQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID列表
     */
    private List<Long> goodsIds;

    /**
     * 排除的商品ID列表
     */
    private List<Long> excludeGoodsIds;

    /**
     * 只查询推荐商品
     */
    private Boolean onlyRecommended;

    /**
     * 只查询热门商品
     */
    private Boolean onlyHot;

    /**
     * 只查询可购买商品
     */
    private Boolean onlyPurchasable;

    public GoodsListQueryCondition(List<Long> goodsIds) {
        this.goodsIds = goodsIds;
    }

    // ===================== 便捷构造方法 =====================

    /**
     * 查询推荐商品
     */
    public static GoodsListQueryCondition recommended() {
        GoodsListQueryCondition condition = new GoodsListQueryCondition();
        condition.setOnlyRecommended(true);
        return condition;
    }

    /**
     * 查询热门商品
     */
    public static GoodsListQueryCondition hot() {
        GoodsListQueryCondition condition = new GoodsListQueryCondition();
        condition.setOnlyHot(true);
        return condition;
    }

    /**
     * 查询可购买商品
     */
    public static GoodsListQueryCondition purchasable() {
        GoodsListQueryCondition condition = new GoodsListQueryCondition();
        condition.setOnlyPurchasable(true);
        return condition;
    }
} 