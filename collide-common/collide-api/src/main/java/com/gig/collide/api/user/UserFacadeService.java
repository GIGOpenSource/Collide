package com.gig.collide.api.user;

import com.gig.collide.api.user.request.UserCreateRequest;
import com.gig.collide.api.user.request.UserQueryRequest;
import com.gig.collide.api.user.request.UserUpdateRequest;
import com.gig.collide.api.user.request.WalletOperationRequest;
import com.gig.collide.api.user.request.UserBlockCreateRequest;
import com.gig.collide.api.user.request.UserBlockQueryRequest;
import com.gig.collide.api.user.response.UserResponse;
import com.gig.collide.api.user.response.WalletResponse;
import com.gig.collide.api.user.response.UserBlockResponse;
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
    Result<Void> createUser(UserCreateRequest request);

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
     * 获取个人用户信息
     * 包含详细信息、统计数据和钱包信息
     */
    Result<UserResponse> getUserProfile(Long userId);

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

    // =================== 钱包管理功能 ===================

    /**
     * 获取用户钱包信息
     */
    Result<WalletResponse> getUserWallet(Long userId);

    /**
     * 创建用户钱包（注册时自动创建）
     */
    Result<WalletResponse> createUserWallet(Long userId);

    /**
     * 钱包余额操作（充值、提现、冻结、解冻）
     */
    Result<WalletResponse> walletOperation(WalletOperationRequest request);

    /**
     * 检查钱包余额是否充足
     */
    Result<Boolean> checkWalletBalance(Long userId, java.math.BigDecimal amount);

    /**
     * 钱包扣款（内部接口，供其他服务调用）
     */
    Result<Void> deductWalletBalance(Long userId, java.math.BigDecimal amount, String businessId, String description);

    /**
     * 钱包充值（内部接口，供其他服务调用）
     */
    Result<Void> addWalletBalance(Long userId, java.math.BigDecimal amount, String businessId, String description);

    // =================== 用户拉黑功能 ===================

    /**
     * 拉黑用户
     */
    Result<UserBlockResponse> blockUser(Long userId, UserBlockCreateRequest request);

    /**
     * 取消拉黑
     */
    Result<Void> unblockUser(Long userId, Long blockedUserId);

    /**
     * 检查拉黑状态
     */
    Result<Boolean> checkBlockStatus(Long userId, Long blockedUserId);

    /**
     * 获取拉黑关系详情
     */
    Result<UserBlockResponse> getBlockRelation(Long userId, Long blockedUserId);

    /**
     * 获取用户拉黑列表
     */
    Result<PageResponse<UserBlockResponse>> getUserBlockList(Long userId, Integer currentPage, Integer pageSize);

    /**
     * 获取用户被拉黑列表
     */
    Result<PageResponse<UserBlockResponse>> getUserBlockedList(Long blockedUserId, Integer currentPage, Integer pageSize);

    /**
     * 分页查询拉黑记录
     */
    Result<PageResponse<UserBlockResponse>> queryBlocks(UserBlockQueryRequest request);

    /**
     * 统计用户拉黑数量
     */
    Result<Long> countUserBlocks(Long userId);

    /**
     * 统计用户被拉黑数量
     */
    Result<Long> countUserBlocked(Long blockedUserId);
} 