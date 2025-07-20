package com.gig.collide.api.box.service;

import com.gig.collide.api.box.request.BlindBoxCreateRequest;
import com.gig.collide.api.box.response.BlindBoxCreateResponse;

/**
 * 盲盒管理门面服务
 *
 * @author GIG
 */
public interface BlindBoxManageFacadeService {

    /**
     * 创建盲盒
     *
     * @param request
     * @return
     */
    BlindBoxCreateResponse create(BlindBoxCreateRequest request);
}
