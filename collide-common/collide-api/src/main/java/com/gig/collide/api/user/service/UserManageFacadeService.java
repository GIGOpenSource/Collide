package com.gig.collide.api.user.service;

import com.gig.collide.api.user.request.UserUnifiedRegisterRequest;
import com.gig.collide.api.user.request.UserUnifiedBloggerApproveRequest;
import com.gig.collide.api.user.response.UserUnifiedRegisterResponse;
import com.gig.collide.api.user.response.UserUnifiedBloggerApproveResponse;
import com.gig.collide.base.response.BaseResponse;

/**
 * 用户管理门面服务接口
 * 提供管理员级别的用户管理功能
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public interface UserManageFacadeService {

    /**
     * 管理员创建用户
     * 
     * @param registerRequest 注册请求
     * @return 注册响应
     */
    UserUnifiedRegisterResponse createUserByAdmin(UserUnifiedRegisterRequest registerRequest);

    /**
     * 博主认证审批
     * 
     * @param approveRequest 审批请求
     * @return 审批响应
     */
    UserUnifiedBloggerApproveResponse approveBlogger(UserUnifiedBloggerApproveRequest approveRequest);

    /**
     * 冻结用户
     * 
     * @param userId 用户ID
     * @param reason 冻结原因
     * @param operatorId 操作员ID
     * @return 操作结果
     */
    BaseResponse freezeUser(Long userId, String reason, Long operatorId);

    /**
     * 解冻用户
     * 
     * @param userId 用户ID
     * @param reason 解冻原因
     * @param operatorId 操作员ID
     * @return 操作结果
     */
    BaseResponse unfreezeUser(Long userId, String reason, Long operatorId);

    /**
     * 封禁用户
     * 
     * @param userId 用户ID
     * @param reason 封禁原因
     * @param operatorId 操作员ID
     * @return 操作结果
     */
    BaseResponse banUser(Long userId, String reason, Long operatorId);

    /**
     * 解封用户
     * 
     * @param userId 用户ID
     * @param reason 解封原因
     * @param operatorId 操作员ID
     * @return 操作结果
     */
    BaseResponse unbanUser(Long userId, String reason, Long operatorId);

    /**
     * 强制激活用户
     * 
     * @param userId 用户ID
     * @param operatorId 操作员ID
     * @return 操作结果
     */
    BaseResponse forceActivateUser(Long userId, Long operatorId);

    /**
     * 重置用户密码
     * 
     * @param userId 用户ID
     * @param newPassword 新密码
     * @param operatorId 操作员ID
     * @return 操作结果
     */
    BaseResponse resetUserPassword(Long userId, String newPassword, Long operatorId);

    /**
     * 设置用户VIP等级
     * 
     * @param userId 用户ID
     * @param vipLevel VIP等级
     * @param operatorId 操作员ID
     * @return 操作结果
     */
    BaseResponse setUserVipLevel(Long userId, Integer vipLevel, Long operatorId);

    /**
     * 撤销博主认证
     * 
     * @param userId 用户ID
     * @param reason 撤销原因
     * @param operatorId 操作员ID
     * @return 操作结果
     */
    BaseResponse revokeBloggerStatus(Long userId, String reason, Long operatorId);

    /**
     * 批量操作用户状态
     * 
     * @param userIds 用户ID列表
     * @param operation 操作类型（freeze、unfreeze、ban、unban）
     * @param reason 操作原因
     * @param operatorId 操作员ID
     * @return 操作结果
     */
    BaseResponse batchOperateUsers(Long[] userIds, String operation, String reason, Long operatorId);
} 