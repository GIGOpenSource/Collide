package com.gig.collide.api.user.service;

import com.gig.collide.api.user.request.UserRegisterRequest;
import com.gig.collide.api.user.response.UserOperatorResponse;

/**
 * @author GIG
 */
public interface UserManageFacadeService {

    /**
     * 管理用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    UserOperatorResponse registerAdmin(UserRegisterRequest userRegisterRequest);

    /**
     * 用户冻结
     *
     * @param userId
     * @return
     */
    UserOperatorResponse freeze(Long userId);

    /**
     * 用户解冻
     *
     * @param userId
     * @return
     */
    UserOperatorResponse unfreeze(Long userId);

}
