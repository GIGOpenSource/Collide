package com.gig.collide.api.goods.request;

import com.gig.collide.api.goods.request.condition.*;
import com.gig.collide.base.request.BaseRequest;
import lombok.*;

/**
 * 商品查询请求
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GoodsQueryRequest extends BaseRequest {

    /**
     * 查询条件
     */
    private GoodsQueryCondition goodsQueryCondition;

    // ===================== 便捷构造器 =====================

    /**
     * 根据商品ID查询
     */
    public GoodsQueryRequest(Long goodsId) {
        GoodsIdQueryCondition condition = new GoodsIdQueryCondition();
        condition.setGoodsId(goodsId);
        this.goodsQueryCondition = condition;
    }

    /**
     * 根据商品名称查询
     */
    public GoodsQueryRequest(String name) {
        GoodsNameQueryCondition condition = new GoodsNameQueryCondition();
        condition.setName(name);
        this.goodsQueryCondition = condition;
    }

    /**
     * 根据商品ID列表查询
     */
    public static GoodsQueryRequest byIds(java.util.List<Long> goodsIds) {
        GoodsQueryRequest request = new GoodsQueryRequest();
        GoodsListQueryCondition condition = new GoodsListQueryCondition();
        condition.setGoodsIds(goodsIds);
        request.setGoodsQueryCondition(condition);
        return request;
    }

    /**
     * 查询推荐商品
     */
    public static GoodsQueryRequest recommended() {
        GoodsQueryRequest request = new GoodsQueryRequest();
        request.setGoodsQueryCondition(GoodsListQueryCondition.recommended());
        return request;
    }

    /**
     * 查询热门商品
     */
    public static GoodsQueryRequest hot() {
        GoodsQueryRequest request = new GoodsQueryRequest();
        request.setGoodsQueryCondition(GoodsListQueryCondition.hot());
        return request;
    }

    /**
     * 查询可购买商品
     */
    public static GoodsQueryRequest purchasable() {
        GoodsQueryRequest request = new GoodsQueryRequest();
        request.setGoodsQueryCondition(GoodsListQueryCondition.purchasable());
        return request;
    }

    /**
     * 通用列表查询
     */
    public static GoodsQueryRequest list() {
        GoodsQueryRequest request = new GoodsQueryRequest();
        request.setGoodsQueryCondition(new GoodsListQueryCondition());
        return request;
    }
}