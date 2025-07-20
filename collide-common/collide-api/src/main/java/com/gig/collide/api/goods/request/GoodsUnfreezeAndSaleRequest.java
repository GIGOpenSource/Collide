package com.gig.collide.api.goods.request;

import com.gig.collide.api.goods.constant.GoodsEvent;

/**
 * @author GIG
 * @param identifier
 * @param goodsId
 * @param quantity
 */
public record GoodsUnfreezeAndSaleRequest(String identifier, Long goodsId, Integer quantity) {

    public GoodsEvent eventType() {
        return GoodsEvent.UNFREEZE_AND_SALE;
    }
}
