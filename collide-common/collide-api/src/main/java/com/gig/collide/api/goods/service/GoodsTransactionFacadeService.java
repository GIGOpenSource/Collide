package com.gig.collide.api.goods.service;

import com.gig.collide.api.goods.request.GoodsSaleRequest;
import com.gig.collide.api.goods.response.GoodsSaleResponse;

/**
 * @author GIG
 */
public interface GoodsTransactionFacadeService {

    /**
     * 锁定库存
     * @param request
     * @return
     */
    public GoodsSaleResponse tryDecreaseInventory(GoodsSaleRequest request);

    /**
     * 解锁并扣减库存
     * @param request
     * @return
     */
    public GoodsSaleResponse confirmDecreaseInventory(GoodsSaleRequest request);

    /**
     * 解锁库存
     * @param request
     * @return
     */
    public GoodsSaleResponse cancelDecreaseInventory(GoodsSaleRequest request);
}
