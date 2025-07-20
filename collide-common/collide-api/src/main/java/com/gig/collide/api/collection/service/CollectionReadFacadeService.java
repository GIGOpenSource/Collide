package com.gig.collide.api.collection.service;

import com.gig.collide.api.collection.model.AirDropStreamVO;
import com.gig.collide.api.collection.model.CollectionVO;
import com.gig.collide.api.collection.model.HeldCollectionVO;
import com.gig.collide.api.collection.request.*;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.base.response.SingleResponse;

/**
 * 藏品门面服务
 *
 * @author GIG
 */
public interface CollectionReadFacadeService {

    /**
     * 根据Id查询藏品
     *
     * @param collectionId
     * @return
     */
    public SingleResponse<CollectionVO> queryById(Long collectionId);

    /**
     * 藏品分页查询
     *
     * @param request
     * @return
     */
    public PageResponse<CollectionVO> pageQuery(CollectionPageQueryRequest request);

    /**
     * 持有藏品分页查询
     *
     * @param request
     * @return
     */
    public PageResponse<HeldCollectionVO> pageQueryHeldCollection(HeldCollectionPageQueryRequest request);


    /**
     * 空投列表分页查询
     * @param request
     * @return
     */
    public PageResponse<AirDropStreamVO> pageQueryAirDropList(AirDropPageQueryRequest request);


    /**
     * 持有藏品数量查询
     * @param userId
     * @return
     */
    public SingleResponse<Long> queryHeldCollectionCount(String userId);

    /**
     * 根据id查询持有藏品
     *
     * @param heldCollectionId
     * @return
     */
    public SingleResponse<HeldCollectionVO> queryHeldCollectionById(Long heldCollectionId);

}
