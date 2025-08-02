package com.gig.collide.users.domain.service;

import com.gig.collide.base.response.PageResponse;
import com.gig.collide.users.domain.entity.UserCore;
import com.gig.collide.api.user.request.users.main.UserCoreQueryRequest;

import java.util.List;

/**
 * 用户核心领域服务接口 - 对应 t_user 表
 * 负责用户基础信息和认证相关功能
 * 
 * @author GIG Team
 * @version 2.0.0
 */
public interface UserCoreService {

    /**
     * 创建用户核心信息
     */
    UserCore createUser(UserCore userCore);

    /**
     * 更新用户核心信息
     */
    UserCore updateUser(UserCore userCore);

    /**
     * 根据ID查询用户
     */
    UserCore getUserById(Long userId);

    /**
     * 根据用户名查询用户
     */
    UserCore getUserByUsername(String username);

    /**
     * 根据邮箱查询用户
     */
    UserCore getUserByEmail(String email);

    /**
     * 根据手机号查询用户
     */
    UserCore getUserByPhone(String phone);

    /**
     * 检查用户名是否存在
     */
    boolean checkUsernameExists(String username);

    /**
     * 检查邮箱是否存在
     */
    boolean checkEmailExists(String email);

    /**
     * 检查手机号是否存在
     */
    boolean checkPhoneExists(String phone);

    /**
     * 批量查询用户核心信息
     */
    List<UserCore> batchGetUsers(List<Long> userIds);

    /**
     * 用户登录验证
     */
    UserCore login(String loginIdentifier, String password, String loginIp);

    /**
     * 验证用户密码
     */
    boolean verifyPassword(Long userId, String password);

    /**
     * 修改用户密码
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 重置用户密码（管理员操作）
     */
    boolean resetPassword(Long userId, String newPassword);

    /**
     * 更新用户状态
     */
    boolean updateUserStatus(Long userId, Integer status);

    /**
     * 更新登录信息
     */
    boolean updateLoginInfo(Long userId, String loginIp);

    /**
     * 生成用户邀请码
     */
    String generateInviteCode(Long userId);

    /**
     * 通过邀请码注册用户
     */
    UserCore registerByInviteCode(String inviteCode, UserCore userCore);

    /**
     * 分页查询用户核心信息
     */
    PageResponse<UserCore> queryUsers(UserCoreQueryRequest request);

    /**
     * 删除用户（物理删除，谨慎使用）
     */
    boolean deleteUser(Long userId);

    /**
     * 检查用户是否有效
     */
    boolean isUserValid(Long userId);

    /**
     * 激活用户
     */
    boolean activateUser(Long userId);

    /**
     * 禁用用户
     */
    boolean disableUser(Long userId);

    /**
     * 锁定用户
     */
    boolean lockUser(Long userId);

    /**
     * 解锁用户
     */
    boolean unlockUser(Long userId);
}