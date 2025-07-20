package com.gig.collide.api.goods.request;

import com.gig.collide.api.goods.constant.GoodsEvent;

/**
 * 冻结库存
 * @param identifier
 * @param goodsId
 * @param quantity
 *
 * @author GIG
 */
public record GoodsFreezeInventoryRequest(String identifier, Long goodsId, Integer quantity) {

    public GoodsEvent eventType() {
        return GoodsEvent.FREEZE_INVENTORY;
    }
}
