package com.gig.collide.api.user;

import com.gig.collide.api.user.request.main.UserCoreCreateRequest;
import com.gig.collide.api.user.request.main.UserCoreUpdateRequest;
import com.gig.collide.api.user.request.main.UserCoreQueryRequest;
import com.gig.collide.api.user.request.main.UserLoginRequest;
import com.gig.collide.api.user.response.main.UserCoreResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

/**
 * 用户核心服务接口 - 对应 t_user 表
 * 负责用户基础信息和认证相关功能
 * 
 * @author GIG Team
 * @version 2.0.0
 */
public interface UserFacadeService {

    /**
     * 用户注册 - 创建核心用户记录
     */
    Result<UserCoreResponse> createUser(UserCoreCreateRequest request);

    /**
     * 更新用户核心信息
     */
    Result<UserCoreResponse> updateUser(UserCoreUpdateRequest request);

    /**
     * 用户登录验证
     */
    Result<UserCoreResponse> login(UserLoginRequest request);

    /**
     * 根据用户ID查询核心信息
     */
    Result<UserCoreResponse> getUserById(Long userId);

    /**
     * 根据用户名查询核心信息
     */
    Result<UserCoreResponse> getUserByUsername(String username);

    /**
     * 根据邮箱查询核心信息
     */
    Result<UserCoreResponse> getUserByEmail(String email);

    /**
     * 根据手机号查询核心信息
     */
    Result<UserCoreResponse> getUserByPhone(String phone);

    /**
     * 检查用户名是否存在
     */
    Result<Boolean> checkUsernameExists(String username);

    /**
     * 检查邮箱是否存在
     */
    Result<Boolean> checkEmailExists(String email);

    /**
     * 检查手机号是否存在
     */
    Result<Boolean> checkPhoneExists(String phone);

    /**
     * 更新用户状态
     */
    Result<Void> updateUserStatus(Long userId, Integer status);

    /**
     * 修改用户密码
     */
    Result<Void> changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 重置用户密码（管理员操作）
     */
    Result<Void> resetPassword(Long userId, String newPassword);

    /**
     * 分页查询用户核心信息
     */
    Result<PageResponse<UserCoreResponse>> queryUsers(UserCoreQueryRequest request);

    /**
     * 批量查询用户核心信息
     */
    Result<java.util.List<UserCoreResponse>> batchGetUsers(java.util.List<Long> userIds);

    /**
     * 删除用户（物理删除，谨慎使用）
     */
    Result<Void> deleteUser(Long userId);

    /**
     * 验证用户密码
     */
    Result<Boolean> verifyPassword(Long userId, String password);
} 