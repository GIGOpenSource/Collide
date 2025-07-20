package com.gig.collide.api.goods.request;

import com.gig.collide.api.goods.constant.GoodsEvent;

/**
 * @param identifier
 * @param goodsId
 * @param quantity
 * @author GIG
 */
public record GoodsTrySaleRequest(String identifier, Long goodsId, Integer quantity) {

    public GoodsEvent eventType() {
        return GoodsEvent.TRY_SALE;
    }
}
