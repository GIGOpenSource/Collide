package com.gig.collide.api.collection.service;

import com.gig.collide.api.collection.request.*;
import com.gig.collide.api.collection.response.CollectionAirdropResponse;
import com.gig.collide.api.collection.response.CollectionChainResponse;
import com.gig.collide.api.collection.response.CollectionModifyResponse;
import com.gig.collide.api.collection.response.CollectionRemoveResponse;

/**
 * 藏品管理门面服务
 *
 * @author GIG
 */
public interface CollectionManageFacadeService {

    /**
     * 创建藏品
     *
     * @param request
     * @return
     */
    public CollectionChainResponse create(CollectionCreateRequest request);


    /**
     * 藏品下架
     *
     * @param request
     * @return
     */
    public CollectionRemoveResponse remove(CollectionRemoveRequest request);

    /**
     * 空投
     *
     * @param request
     * @return
     */
    public CollectionAirdropResponse airDrop(CollectionAirDropRequest request);

    /**
     * 藏品库存修改
     *
     * @param request
     * @return
     */
    public CollectionModifyResponse modifyInventory(CollectionModifyInventoryRequest request);

    /**
     * 藏品价格修改
     *
     * @param request
     * @return
     */
    public CollectionModifyResponse modifyPrice(CollectionModifyPriceRequest request);
}
