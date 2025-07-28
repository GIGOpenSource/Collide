package com.gig.collide.api.user;

import com.gig.collide.api.user.request.UserCreateRequest;
import com.gig.collide.api.user.request.UserQueryRequest;
import com.gig.collide.api.user.request.UserUpdateRequest;
import com.gig.collide.api.user.response.UserResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.*;

/**
 * 用户管理门面服务接口 - 简洁版
 * 基于简洁版SQL设计，保留核心功能
 * 
 * @author GIG Team
 * @version 2.0.0
 */
public interface UserFacadeService {

    /**
     * 创建用户（注册）
     */
    Result<UserResponse> createUser(UserCreateRequest request);

    /**
     * 更新用户信息
     */
    Result<UserResponse> updateUser(UserUpdateRequest request);

    /**
     * 根据ID查询用户
     */
    Result<UserResponse> getUserById(Long userId);

    /**
     * 根据用户名查询用户
     */
    Result<UserResponse> getUserByUsername(String username);

    /**
     * 分页查询用户列表
     */
    Result<PageResponse<UserResponse>> queryUsers(UserQueryRequest request);

    /**
     * 用户登录
     */
    Result<UserResponse> login(String username, String password);

    /**
     * 更新用户状态
     */
    Result<Void> updateUserStatus(Long userId, String status);

    /**
     * 删除用户（逻辑删除）
     */
    Result<Void> deleteUser(Long userId);

    /**
     * 更新用户统计数据（关注数、粉丝数、内容数等）
     */
    Result<Void> updateUserStats(Long userId, String statsType, Integer increment);
} 