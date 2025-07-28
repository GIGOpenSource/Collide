package com.gig.collide.users.domain.service;

import com.gig.collide.api.user.request.*;
import com.gig.collide.api.user.response.*;
import com.gig.collide.api.user.response.data.UserUnifiedInfo;
import com.gig.collide.users.domain.entity.UserUnified;
import com.gig.collide.base.response.BaseResponse;
import com.gig.collide.base.response.PageResponse;

import java.util.List;

/**
 * 用户统一服务接口
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public interface UserUnifiedService {

    /**
     * 根据ID查询用户
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    UserUnified findById(Long userId);

    /**
     * 根据用户名查询用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    UserUnified findByUsername(String username);

    /**
     * 根据邮箱查询用户
     * 
     * @param email 邮箱
     * @return 用户信息
     */
    UserUnified findByEmail(String email);

    /**
     * 根据手机号查询用户
     * 
     * @param phone 手机号
     * @return 用户信息
     */
    UserUnified findByPhone(String phone);

    /**
     * 根据手机号和密码查询用户
     * 
     * @param phone 手机号
     * @param password 密码
     * @return 用户信息
     */
    UserUnified findByPhoneAndPassword(String phone, String password);

    /**
     * 验证用户名和密码
     * 
     * @param username 用户名
     * @param password 密码
     * @return 是否验证成功
     */
    Boolean validateUsernameAndPassword(String username, String password);

    /**
     * 用户注册
     * 
     * @param registerRequest 注册请求
     * @return 注册响应
     */
    UserUnifiedRegisterResponse register(UserUnifiedRegisterRequest registerRequest);

    /**
     * 用户信息修改
     * 
     * @param modifyRequest 修改请求
     * @return 修改响应
     */
    UserUnifiedModifyResponse modify(UserUnifiedModifyRequest modifyRequest);

    /**
     * 用户激活
     * 
     * @param activateRequest 激活请求
     * @return 激活响应
     */
    UserUnifiedActivateResponse activate(UserUnifiedActivateRequest activateRequest);

    /**
     * 申请博主认证
     * 
     * @param applyRequest 申请请求
     * @return 申请响应
     */
    UserUnifiedBloggerApplyResponse applyBlogger(UserUnifiedBloggerApplyRequest applyRequest);

    /**
     * 检查用户名是否可用
     * 
     * @param username 用户名
     * @return 是否可用
     */
    Boolean checkUsernameAvailable(String username);

    /**
     * 检查邮箱是否可用
     * 
     * @param email 邮箱
     * @return 是否可用
     */
    Boolean checkEmailAvailable(String email);

    /**
     * 检查手机号是否可用
     * 
     * @param phone 手机号
     * @return 是否可用
     */
    Boolean checkPhoneAvailable(String phone);

    /**
     * 生成用户邀请码
     * 
     * @param userId 用户ID
     * @return 邀请码
     */
    String generateInviteCode(Long userId);

    /**
     * 分页查询用户
     * 
     * @param queryRequest 查询请求
     * @return 分页响应
     */
    PageResponse<UserUnifiedInfo> pageQuery(UserUnifiedQueryRequest queryRequest);

    /**
     * 更新用户统计信息
     * 
     * @param userId 用户ID
     * @param fieldName 字段名
     * @param incrementValue 增量值
     * @return 是否成功
     */
    Boolean updateUserStatistics(Long userId, String fieldName, Integer incrementValue);

    /**
     * 更新最后登录时间
     * 
     * @param userId 用户ID
     * @return 是否成功
     */
    Boolean updateLastLoginTime(Long userId);

    // 管理员相关方法
    /**
     * 批准博主状态
     * 
     * @param userId 用户ID
     * @param approved 是否批准
     * @param comment 审核意见
     */
    void approveBloggerStatus(Long userId, boolean approved, String comment);

    /**
     * 更新用户状态
     * 
     * @param userId 用户ID
     * @param status 状态
     * @param reason 原因
     * @param operatorId 操作员ID
     */
    void updateUserStatus(Long userId, String status, String reason, Long operatorId);

    /**
     * 强制激活用户
     * 
     * @param userId 用户ID
     * @param operatorId 操作员ID
     */
    void forceActivateUser(Long userId, Long operatorId);

    /**
     * 重置用户密码
     * 
     * @param userId 用户ID
     * @param newPassword 新密码
     * @param operatorId 操作员ID
     */
    void resetPassword(Long userId, String newPassword, Long operatorId);

    /**
     * 设置VIP等级
     * 
     * @param userId 用户ID
     * @param vipLevel VIP等级
     * @param operatorId 操作员ID
     */
    void setVipLevel(Long userId, Integer vipLevel, Long operatorId);

    /**
     * 撤销博主状态
     * 
     * @param userId 用户ID
     * @param reason 撤销原因
     * @param operatorId 操作员ID
     */
    void revokeBloggerStatus(Long userId, String reason, Long operatorId);

    /**
     * 批量操作用户
     * 
     * @param operation 操作类型
     * @param userIds 用户ID列表
     * @param operatorId 操作员ID
     */
    void batchOperateUsers(String operation, List<Long> userIds, Long operatorId);
} 