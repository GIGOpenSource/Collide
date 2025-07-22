package com.gig.collide.api.user.service;

import com.gig.collide.api.user.request.*;
import com.gig.collide.api.user.response.UserOperatorResponse;
import com.gig.collide.api.user.response.UserQueryResponse;
import com.gig.collide.api.user.response.data.UserInfo;
import com.gig.collide.base.response.PageResponse;

/**
 * 用户门面服务接口
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface UserFacadeService {

    /**
     * 用户信息查询
     * @param userQueryRequest 查询请求
     * @return 用户信息响应
     */
    UserQueryResponse<UserInfo> query(UserQueryRequest userQueryRequest);

    /**
     * 分页查询用户信息
     * @param userPageQueryRequest 分页查询请求
     * @return 分页用户信息响应
     */
    PageResponse<UserInfo> pageQuery(UserPageQueryRequest userPageQueryRequest);

    /**
     * 用户注册
     * @param userRegisterRequest 注册请求
     * @return 操作结果响应
     */
    UserOperatorResponse register(UserRegisterRequest userRegisterRequest);

    /**
     * 用户信息修改
     * @param userModifyRequest 修改请求
     * @return 操作结果响应
     */
    UserOperatorResponse modify(UserModifyRequest userModifyRequest);

    /**
     * 用户实名认证
     * @param userAuthRequest 认证请求
     * @return 操作结果响应
     */
    UserOperatorResponse auth(UserAuthRequest userAuthRequest);

    /**
     * 用户激活
     * @param userActiveRequest 激活请求
     * @return 操作结果响应
     */
    UserOperatorResponse active(UserActiveRequest userActiveRequest);
}
