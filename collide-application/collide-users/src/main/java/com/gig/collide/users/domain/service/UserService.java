package com.gig.collide.users.domain.service;

import com.gig.collide.api.user.request.UserQueryRequest;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.users.domain.entity.User;

/**
 * 用户领域服务接口 - 简洁版
 * 
 * @author GIG Team
 * @version 2.0.0
 */
public interface UserService {

    /**
     * 创建用户
     */
    User createUser(User user);

    /**
     * 更新用户信息
     */
    User updateUser(User user);

    /**
     * 根据ID查询用户
     */
    User getUserById(Long userId);

    /**
     * 根据用户名查询用户
     */
    User getUserByUsername(String username);

    /**
     * 分页查询用户列表
     */
    PageResponse<User> queryUsers(UserQueryRequest request);

    /**
     * 用户登录
     */
    User login(String username, String password);

    /**
     * 更新用户状态
     */
    void updateUserStatus(Long userId, String status);

    /**
     * 删除用户（逻辑删除）
     */
    void deleteUser(Long userId);

    /**
     * 更新用户统计数据
     */
    void updateUserStats(Long userId, String statsType, Integer increment);
} 