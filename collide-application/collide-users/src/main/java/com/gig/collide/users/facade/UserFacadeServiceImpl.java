package com.gig.collide.users.facade;

import com.gig.collide.api.user.request.*;
import com.gig.collide.api.user.response.*;
import com.gig.collide.api.user.response.data.UserUnifiedInfo;
import com.gig.collide.api.user.response.data.BasicUserUnifiedInfo;
import com.gig.collide.api.user.request.condition.*;
import com.gig.collide.api.user.service.UserFacadeService;
import com.gig.collide.users.domain.service.UserUnifiedService;
import com.gig.collide.users.domain.entity.UserUnified;
import com.gig.collide.users.domain.entity.convertor.UserUnifiedConvertor;
import com.gig.collide.base.response.PageResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 用户门面服务实现
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Slf4j
@DubboService(version = "1.0.0")
public class UserFacadeServiceImpl implements UserFacadeService {

    @Autowired
    private UserUnifiedService userUnifiedService;

    @Override
    public UserUnifiedQueryResponse<UserUnifiedInfo> queryUser(UserUnifiedQueryRequest userQueryRequest) {
        UserUnifiedQueryResponse<UserUnifiedInfo> response = new UserUnifiedQueryResponse<>();
        
        try {
            UserQueryCondition condition = userQueryRequest.getUserQueryCondition();
            UserUnified user = null;
            
            if (condition instanceof UserIdQueryCondition) {
                UserIdQueryCondition idCondition = (UserIdQueryCondition) condition;
                user = userUnifiedService.findById(idCondition.getUserId());
            } else if (condition instanceof UserUsernameQueryCondition) {
                UserUsernameQueryCondition usernameCondition = (UserUsernameQueryCondition) condition;
                user = userUnifiedService.findByUsername(usernameCondition.getUsername());
            } else if (condition instanceof UserEmailQueryCondition) {
                UserEmailQueryCondition emailCondition = (UserEmailQueryCondition) condition;
                user = userUnifiedService.findByEmail(emailCondition.getEmail());
            } else if (condition instanceof UserPhoneQueryCondition) {
                UserPhoneQueryCondition phoneCondition = (UserPhoneQueryCondition) condition;
                user = userUnifiedService.findByPhone(phoneCondition.getPhone());
            }
            
            if (user != null) {
                response.setSuccess(true);
                response.setResponseCode("SUCCESS");
                response.setResponseMessage("查询成功");
                response.setData(UserUnifiedConvertor.INSTANCE.toUserUnifiedInfo(user));
            } else {
                response.setSuccess(false);
                response.setResponseCode("USER_NOT_FOUND");
                response.setResponseMessage("用户不存在");
            }
            
        } catch (Exception e) {
            log.error("查询用户失败", e);
            response.setSuccess(false);
            response.setResponseCode("SYSTEM_ERROR");
            response.setResponseMessage("系统异常");
        }
        
        return response;
    }

    @Override
    public UserUnifiedQueryResponse<BasicUserUnifiedInfo> queryBasicUser(UserUnifiedQueryRequest userQueryRequest) {
        UserUnifiedQueryResponse<BasicUserUnifiedInfo> response = new UserUnifiedQueryResponse<>();
        
        try {
            UserQueryCondition condition = userQueryRequest.getUserQueryCondition();
            UserUnified user = null;
            
            if (condition instanceof UserIdQueryCondition) {
                UserIdQueryCondition idCondition = (UserIdQueryCondition) condition;
                user = userUnifiedService.findById(idCondition.getUserId());
            } else if (condition instanceof UserUsernameQueryCondition) {
                UserUsernameQueryCondition usernameCondition = (UserUsernameQueryCondition) condition;
                user = userUnifiedService.findByUsername(usernameCondition.getUsername());
            } else if (condition instanceof UserEmailQueryCondition) {
                UserEmailQueryCondition emailCondition = (UserEmailQueryCondition) condition;
                user = userUnifiedService.findByEmail(emailCondition.getEmail());
            } else if (condition instanceof UserPhoneQueryCondition) {
                UserPhoneQueryCondition phoneCondition = (UserPhoneQueryCondition) condition;
                user = userUnifiedService.findByPhone(phoneCondition.getPhone());
            }
            
            if (user != null) {
                response.setSuccess(true);
                response.setResponseCode("SUCCESS");
                response.setResponseMessage("查询成功");
                response.setData(UserUnifiedConvertor.INSTANCE.toBasicUserUnifiedInfo(user));
            } else {
                response.setSuccess(false);
                response.setResponseCode("USER_NOT_FOUND");
                response.setResponseMessage("用户不存在");
            }
            
        } catch (Exception e) {
            log.error("查询基础用户信息失败", e);
            response.setSuccess(false);
            response.setResponseCode("SYSTEM_ERROR");
            response.setResponseMessage("系统异常");
        }
        
        return response;
    }

    @Override
    public PageResponse<UserUnifiedInfo> pageQueryUsers(UserUnifiedQueryRequest userQueryRequest) {
        try {
            return userUnifiedService.pageQuery(userQueryRequest);
        } catch (Exception e) {
            log.error("分页查询用户失败", e);
            PageResponse<UserUnifiedInfo> response = new PageResponse<>();
            response.setSuccess(false);
            response.setResponseCode("SYSTEM_ERROR");
            response.setResponseMessage("系统异常");
            return response;
        }
    }

    @Override
    public UserUnifiedRegisterResponse register(UserUnifiedRegisterRequest registerRequest) {
        try {
            return userUnifiedService.register(registerRequest);
        } catch (Exception e) {
            log.error("用户注册失败", e);
            UserUnifiedRegisterResponse response = new UserUnifiedRegisterResponse();
            response.setSuccess(false);
            response.setResponseCode("SYSTEM_ERROR");
            response.setResponseMessage("系统异常");
            return response;
        }
    }

    @Override
    public UserUnifiedModifyResponse modify(UserUnifiedModifyRequest modifyRequest) {
        try {
            return userUnifiedService.modify(modifyRequest);
        } catch (Exception e) {
            log.error("用户信息修改失败", e);
            UserUnifiedModifyResponse response = new UserUnifiedModifyResponse();
            response.setSuccess(false);
            response.setResponseCode("SYSTEM_ERROR");
            response.setResponseMessage("系统异常");
            return response;
        }
    }

    @Override
    public UserUnifiedActivateResponse activate(UserUnifiedActivateRequest activateRequest) {
        try {
            return userUnifiedService.activate(activateRequest);
        } catch (Exception e) {
            log.error("用户激活失败", e);
            UserUnifiedActivateResponse response = new UserUnifiedActivateResponse();
            response.setSuccess(false);
            response.setResponseCode("SYSTEM_ERROR");
            response.setResponseMessage("系统异常");
            return response;
        }
    }

    @Override
    public UserUnifiedBloggerApplyResponse applyBlogger(UserUnifiedBloggerApplyRequest applyRequest) {
        try {
            return userUnifiedService.applyBlogger(applyRequest);
        } catch (Exception e) {
            log.error("博主申请失败", e);
            UserUnifiedBloggerApplyResponse response = new UserUnifiedBloggerApplyResponse();
            response.setSuccess(false);
            response.setResponseCode("SYSTEM_ERROR");
            response.setResponseMessage("系统异常");
            return response;
        }
    }

    @Override
    public UserUnifiedQueryResponse<UserUnifiedInfo> getUserWithBloggerInfo(Long userId) {
        UserUnifiedQueryResponse<UserUnifiedInfo> response = new UserUnifiedQueryResponse<>();
        
        try {
            UserUnified user = userUnifiedService.findById(userId);
            if (user != null) {
                response.setSuccess(true);
                response.setResponseCode("SUCCESS");
                response.setResponseMessage("查询成功");
                response.setData(UserUnifiedConvertor.INSTANCE.toUserUnifiedInfo(user));
            } else {
                response.setSuccess(false);
                response.setResponseCode("USER_NOT_FOUND");
                response.setResponseMessage("用户不存在");
            }
        } catch (Exception e) {
            log.error("获取用户博主信息失败", e);
            response.setSuccess(false);
            response.setResponseCode("SYSTEM_ERROR");
            response.setResponseMessage("系统异常");
        }
        
        return response;
    }

    @Override
    public Boolean checkUsernameAvailable(String username) {
        try {
            return userUnifiedService.checkUsernameAvailable(username);
        } catch (Exception e) {
            log.error("检查用户名可用性失败", e);
            return false;
        }
    }

    @Override
    public Boolean checkEmailAvailable(String email) {
        try {
            return userUnifiedService.checkEmailAvailable(email);
        } catch (Exception e) {
            log.error("检查邮箱可用性失败", e);
            return false;
        }
    }

    @Override
    public Boolean checkPhoneAvailable(String phone) {
        try {
            return userUnifiedService.checkPhoneAvailable(phone);
        } catch (Exception e) {
            log.error("检查手机号可用性失败", e);
            return false;
        }
    }

    @Override
    public String generateInviteCode(Long userId) {
        try {
            log.info("生成用户邀请码，用户ID：{}", userId);
            return userUnifiedService.generateInviteCode(userId);
        } catch (Exception e) {
            log.error("生成用户邀请码失败，用户ID：{}", userId, e);
            return null;
        }
    }

    @Override
    public Boolean validatePassword(String username, String password) {
        try {
            log.info("验证用户密码，用户名：{}", username);
            return userUnifiedService.validateUsernameAndPassword(username, password);
        } catch (Exception e) {
            log.error("验证用户密码失败，用户名：{}", username, e);
            return false;
        }
    }
} 