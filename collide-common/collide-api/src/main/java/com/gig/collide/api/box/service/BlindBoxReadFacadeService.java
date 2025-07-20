package com.gig.collide.api.box.service;

import com.gig.collide.api.box.model.BlindBoxItemVO;
import com.gig.collide.api.box.model.BlindBoxVO;
import com.gig.collide.api.box.model.HeldBlindBoxVO;
import com.gig.collide.api.box.request.BlindBoxItemPageQueryRequest;
import com.gig.collide.api.box.request.BlindBoxPageQueryRequest;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.base.response.SingleResponse;

/**
 * 盲盒门面服务
 *
 * @author GIG
 */
public interface BlindBoxReadFacadeService {

    /**
     * 根据Id查询藏品
     *
     * @param blindBoxId
     * @return
     */
    SingleResponse<BlindBoxVO> queryById(Long blindBoxId);

    /**
     * 根据id查询盲盒条目
     *
     * @param blindBoxItemId
     * @return
     */
    SingleResponse<BlindBoxItemVO> queryBlindBoxItemById(Long blindBoxItemId);

    /**
     * 盲盒分页查询
     *
     * @param request
     * @return
     */
    public PageResponse<BlindBoxVO> pageQueryBlindBox(BlindBoxPageQueryRequest request);

    /**
     * 盲盒条目分页查询
     *
     * @param request
     * @return
     */
    public PageResponse<HeldBlindBoxVO> pageQueryBlindBoxItem(BlindBoxItemPageQueryRequest request);
}
