package com.gig.collide.users.facade;

import com.gig.collide.api.user.request.UserUnifiedRegisterRequest;
import com.gig.collide.api.user.request.UserUnifiedBloggerApproveRequest;
import com.gig.collide.api.user.response.UserUnifiedRegisterResponse;
import com.gig.collide.api.user.response.UserUnifiedBloggerApproveResponse;
import com.gig.collide.api.user.service.UserManageFacadeService;
import com.gig.collide.users.domain.service.UserUnifiedService;
import com.gig.collide.base.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * 用户管理门面服务实现
 * 提供管理员级别的用户管理功能
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Slf4j
@DubboService(version = "1.0.0")
public class UserManageFacadeServiceImpl implements UserManageFacadeService {

    @Autowired
    private UserUnifiedService userUnifiedService;

    @Override
    public UserUnifiedRegisterResponse createUserByAdmin(UserUnifiedRegisterRequest registerRequest) {
        try {
            log.info("管理员创建用户，用户名: {}", registerRequest.getUsername());
            return userUnifiedService.register(registerRequest);
        } catch (Exception e) {
            log.error("管理员创建用户失败", e);
            UserUnifiedRegisterResponse response = new UserUnifiedRegisterResponse();
            response.setSuccess(false);
            response.setResponseCode("SYSTEM_ERROR");
            response.setResponseMessage("系统异常");
            return response;
        }
    }

    @Override
    public UserUnifiedBloggerApproveResponse approveBlogger(UserUnifiedBloggerApproveRequest approveRequest) {
        UserUnifiedBloggerApproveResponse response = new UserUnifiedBloggerApproveResponse();
        
        try {
            String approvalResult = approveRequest.getApprovalResult();
            String comment = approveRequest.getApprovalComment();
            boolean approved = "approved".equals(approvalResult);
            
            userUnifiedService.approveBloggerStatus(approveRequest.getUserId(), approved, comment);
            
            response.setSuccess(true);
            response.setResponseCode("SUCCESS");
            response.setResponseMessage(approved ? "博主认证通过" : "博主认证被拒绝");
            response.setApprovalResult(approvalResult);
            
        } catch (Exception e) {
            log.error("博主认证审批失败", e);
            response.setSuccess(false);
            response.setResponseCode("SYSTEM_ERROR");
            response.setResponseMessage("系统异常");
        }
        
        return response;
    }

    @Override
    public BaseResponse freezeUser(Long userId, String reason, Long operatorId) {
        BaseResponse response = new BaseResponse();
        
        try {
            userUnifiedService.updateUserStatus(userId, "FROZEN", reason, operatorId);
            response.setSuccess(true);
            response.setResponseCode("SUCCESS");
            response.setResponseMessage("用户冻结成功");
        } catch (Exception e) {
            log.error("冻结用户失败", e);
            response.setSuccess(false);
            response.setResponseCode("SYSTEM_ERROR");
            response.setResponseMessage("系统异常");
        }
        
        return response;
    }

    @Override
    public BaseResponse unfreezeUser(Long userId, String reason, Long operatorId) {
        BaseResponse response = new BaseResponse();
        
        try {
            userUnifiedService.updateUserStatus(userId, "ACTIVE", reason, operatorId);
            response.setSuccess(true);
            response.setResponseCode("SUCCESS");
            response.setResponseMessage("用户解冻成功");
        } catch (Exception e) {
            log.error("解冻用户失败", e);
            response.setSuccess(false);
            response.setResponseCode("SYSTEM_ERROR");
            response.setResponseMessage("系统异常");
        }
        
        return response;
    }

    @Override
    public BaseResponse banUser(Long userId, String reason, Long operatorId) {
        BaseResponse response = new BaseResponse();
        
        try {
            userUnifiedService.updateUserStatus(userId, "BANNED", reason, operatorId);
            response.setSuccess(true);
            response.setResponseCode("SUCCESS");
            response.setResponseMessage("用户封禁成功");
        } catch (Exception e) {
            log.error("封禁用户失败", e);
            response.setSuccess(false);
            response.setResponseCode("SYSTEM_ERROR");
            response.setResponseMessage("系统异常");
        }
        
        return response;
    }

    @Override
    public BaseResponse unbanUser(Long userId, String reason, Long operatorId) {
        BaseResponse response = new BaseResponse();
        
        try {
            userUnifiedService.updateUserStatus(userId, "ACTIVE", reason, operatorId);
            response.setSuccess(true);
            response.setResponseCode("SUCCESS");
            response.setResponseMessage("用户解封成功");
        } catch (Exception e) {
            log.error("解封用户失败", e);
            response.setSuccess(false);
            response.setResponseCode("SYSTEM_ERROR");
            response.setResponseMessage("系统异常");
        }
        
        return response;
    }

    @Override
    public BaseResponse forceActivateUser(Long userId, Long operatorId) {
        BaseResponse response = new BaseResponse();
        
        try {
            userUnifiedService.forceActivateUser(userId, operatorId);
            response.setSuccess(true);
            response.setResponseCode("SUCCESS");
            response.setResponseMessage("用户强制激活成功");
        } catch (Exception e) {
            log.error("强制激活用户失败", e);
            response.setSuccess(false);
            response.setResponseCode("SYSTEM_ERROR");
            response.setResponseMessage("系统异常");
        }
        
        return response;
    }

    @Override
    public BaseResponse resetUserPassword(Long userId, String newPassword, Long operatorId) {
        BaseResponse response = new BaseResponse();
        
        try {
            userUnifiedService.resetPassword(userId, newPassword, operatorId);
            response.setSuccess(true);
            response.setResponseCode("SUCCESS");
            response.setResponseMessage("用户密码重置成功");
        } catch (Exception e) {
            log.error("重置用户密码失败", e);
            response.setSuccess(false);
            response.setResponseCode("SYSTEM_ERROR");
            response.setResponseMessage("系统异常");
        }
        
        return response;
    }

    @Override
    public BaseResponse setUserVipLevel(Long userId, Integer vipLevel, Long operatorId) {
        BaseResponse response = new BaseResponse();
        
        try {
            userUnifiedService.setVipLevel(userId, vipLevel, operatorId);
            response.setSuccess(true);
            response.setResponseCode("SUCCESS");
            response.setResponseMessage("用户VIP等级设置成功");
        } catch (Exception e) {
            log.error("设置用户VIP等级失败", e);
            response.setSuccess(false);
            response.setResponseCode("SYSTEM_ERROR");
            response.setResponseMessage("系统异常");
        }
        
        return response;
    }

    @Override
    public BaseResponse revokeBloggerStatus(Long userId, String reason, Long operatorId) {
        BaseResponse response = new BaseResponse();
        
        try {
            userUnifiedService.revokeBloggerStatus(userId, reason, operatorId);
            response.setSuccess(true);
            response.setResponseCode("SUCCESS");
            response.setResponseMessage("博主认证撤销成功");
        } catch (Exception e) {
            log.error("撤销博主认证失败", e);
            response.setSuccess(false);
            response.setResponseCode("SYSTEM_ERROR");
            response.setResponseMessage("系统异常");
        }
        
        return response;
    }

    @Override
    public BaseResponse batchOperateUsers(Long[] userIds, String operation, String reason, Long operatorId) {
        BaseResponse response = new BaseResponse();
        
        try {
            List<Long> userIdList = Arrays.asList(userIds);
            userUnifiedService.batchOperateUsers(operation, userIdList, operatorId);
            response.setSuccess(true);
            response.setResponseCode("SUCCESS");
            response.setResponseMessage("批量操作用户成功");
        } catch (Exception e) {
            log.error("批量操作用户失败", e);
            response.setSuccess(false);
            response.setResponseCode("SYSTEM_ERROR");
            response.setResponseMessage("系统异常");
        }
        
        return response;
    }
} 