package com.gig.collide.users.controller;

import com.gig.collide.api.user.request.UserUnifiedRegisterRequest;
import com.gig.collide.api.user.request.UserUnifiedBloggerApproveRequest;
import com.gig.collide.api.user.response.UserUnifiedRegisterResponse;
import com.gig.collide.api.user.response.UserUnifiedBloggerApproveResponse;
import com.gig.collide.users.facade.UserManageFacadeServiceImpl;
import com.gig.collide.base.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 用户管理控制器
 * 提供管理员级别的用户管理功能
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/admin/users")
public class UserManageController {

    @Autowired
    private UserManageFacadeServiceImpl userManageFacadeService;

    /**
     * 管理员创建用户
     * 
     * @param registerRequest 注册请求
     * @return 创建结果
     */
    @PostMapping("/create")
    public UserUnifiedRegisterResponse createUserByAdmin(@Valid @RequestBody UserUnifiedRegisterRequest registerRequest) {
        return userManageFacadeService.createUserByAdmin(registerRequest);
    }

    /**
     * 博主认证审批
     * 
     * @param approveRequest 审批请求
     * @return 审批结果
     */
    @PostMapping("/approve-blogger")
    public UserUnifiedBloggerApproveResponse approveBlogger(@Valid @RequestBody UserUnifiedBloggerApproveRequest approveRequest) {
        return userManageFacadeService.approveBlogger(approveRequest);
    }

    /**
     * 冻结用户
     * 
     * @param userId 用户ID
     * @param reason 冻结原因
     * @param operatorId 操作员ID
     * @return 操作结果
     */
    @PostMapping("/{userId}/freeze")
    public BaseResponse freezeUser(@PathVariable Long userId, 
                                 @RequestParam String reason, 
                                 @RequestParam Long operatorId) {
        return userManageFacadeService.freezeUser(userId, reason, operatorId);
    }

    /**
     * 解冻用户
     * 
     * @param userId 用户ID
     * @param reason 解冻原因
     * @param operatorId 操作员ID
     * @return 操作结果
     */
    @PostMapping("/{userId}/unfreeze")
    public BaseResponse unfreezeUser(@PathVariable Long userId, 
                                   @RequestParam String reason, 
                                   @RequestParam Long operatorId) {
        return userManageFacadeService.unfreezeUser(userId, reason, operatorId);
    }

    /**
     * 封禁用户
     * 
     * @param userId 用户ID
     * @param reason 封禁原因
     * @param operatorId 操作员ID
     * @return 操作结果
     */
    @PostMapping("/{userId}/ban")
    public BaseResponse banUser(@PathVariable Long userId, 
                              @RequestParam String reason, 
                              @RequestParam Long operatorId) {
        return userManageFacadeService.banUser(userId, reason, operatorId);
    }

    /**
     * 解封用户
     * 
     * @param userId 用户ID
     * @param reason 解封原因
     * @param operatorId 操作员ID
     * @return 操作结果
     */
    @PostMapping("/{userId}/unban")
    public BaseResponse unbanUser(@PathVariable Long userId, 
                                @RequestParam String reason, 
                                @RequestParam Long operatorId) {
        return userManageFacadeService.unbanUser(userId, reason, operatorId);
    }

    /**
     * 强制激活用户
     * 
     * @param userId 用户ID
     * @param operatorId 操作员ID
     * @return 操作结果
     */
    @PostMapping("/{userId}/force-activate")
    public BaseResponse forceActivateUser(@PathVariable Long userId, 
                                        @RequestParam Long operatorId) {
        return userManageFacadeService.forceActivateUser(userId, operatorId);
    }

    /**
     * 重置用户密码
     * 
     * @param userId 用户ID
     * @param newPassword 新密码
     * @param operatorId 操作员ID
     * @return 操作结果
     */
    @PostMapping("/{userId}/reset-password")
    public BaseResponse resetUserPassword(@PathVariable Long userId, 
                                        @RequestParam String newPassword, 
                                        @RequestParam Long operatorId) {
        return userManageFacadeService.resetUserPassword(userId, newPassword, operatorId);
    }

    /**
     * 设置用户VIP等级
     * 
     * @param userId 用户ID
     * @param vipLevel VIP等级
     * @param operatorId 操作员ID
     * @return 操作结果
     */
    @PostMapping("/{userId}/set-vip")
    public BaseResponse setUserVipLevel(@PathVariable Long userId, 
                                      @RequestParam Integer vipLevel, 
                                      @RequestParam Long operatorId) {
        return userManageFacadeService.setUserVipLevel(userId, vipLevel, operatorId);
    }

    /**
     * 撤销博主认证
     * 
     * @param userId 用户ID
     * @param reason 撤销原因
     * @param operatorId 操作员ID
     * @return 操作结果
     */
    @PostMapping("/{userId}/revoke-blogger")
    public BaseResponse revokeBloggerStatus(@PathVariable Long userId, 
                                          @RequestParam String reason, 
                                          @RequestParam Long operatorId) {
        return userManageFacadeService.revokeBloggerStatus(userId, reason, operatorId);
    }

    /**
     * 批量操作用户状态
     * 
     * @param userIds 用户ID列表
     * @param operation 操作类型
     * @param reason 操作原因
     * @param operatorId 操作员ID
     * @return 操作结果
     */
    @PostMapping("/batch-operate")
    public BaseResponse batchOperateUsers(@RequestParam Long[] userIds, 
                                        @RequestParam String operation, 
                                        @RequestParam String reason, 
                                        @RequestParam Long operatorId) {
        return userManageFacadeService.batchOperateUsers(userIds, operation, reason, operatorId);
    }
} 