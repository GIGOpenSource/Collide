package com.gig.collide.api.goods.request;

import com.gig.collide.api.goods.constant.GoodsEvent;
/**
 * 商品取消售卖请求
 *
 * @author GIG
 */
public record GoodsCancelSaleRequest(String identifier, Long collectionId, Integer quantity) {

    public GoodsEvent eventType() {
        return GoodsEvent.CANCEL_SALE;
    }
}
