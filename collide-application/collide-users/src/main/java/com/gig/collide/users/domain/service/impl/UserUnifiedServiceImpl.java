package com.gig.collide.users.domain.service.impl;

import com.gig.collide.api.user.request.*;
import com.gig.collide.api.user.response.*;
import com.gig.collide.api.user.response.data.UserUnifiedInfo;
import com.gig.collide.api.user.request.condition.*;
import com.gig.collide.users.domain.entity.UserUnified;
import com.gig.collide.users.domain.entity.UserOperateLog;
import com.gig.collide.users.domain.service.UserUnifiedService;
import com.gig.collide.users.infrastructure.mapper.UserUnifiedMapper;
import com.gig.collide.users.infrastructure.mapper.UserOperateLogMapper;
import com.gig.collide.users.domain.entity.convertor.UserUnifiedConvertor;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.base.exception.BizException;
import com.gig.collide.base.exception.ErrorCode;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户统一服务实现类
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Slf4j
@Service
public class UserUnifiedServiceImpl implements UserUnifiedService {

    @Autowired
    private UserUnifiedMapper userUnifiedMapper;

    @Autowired
    private UserOperateLogMapper userOperateLogMapper;

    private static final String DEFAULT_NICKNAME_PREFIX = "用户_";
    private static final String DEFAULT_ROLE = "user";
    private static final String DEFAULT_STATUS = "inactive";
    private static final String DEFAULT_GENDER = "unknown";

    @Override
    public UserUnified findById(Long userId) {
        if (userId == null) {
            return null;
        }
        return userUnifiedMapper.selectById(userId);
    }

    @Override
    public UserUnified findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        return userUnifiedMapper.findByUsername(username);
    }

    @Override
    public UserUnified findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return userUnifiedMapper.findByEmail(email);
    }

    @Override
    public UserUnified findByPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return null;
        }
        return userUnifiedMapper.findByPhone(phone);
    }

    @Override
    public UserUnified findByPhoneAndPassword(String phone, String password) {
        if (phone == null || password == null) {
            return null;
        }
        
        // 查找用户获取盐值
        UserUnified user = findByPhone(phone);
        if (user == null) {
            return null;
        }
        
        // 使用盐值加密密码
        String passwordHash = hashPassword(password, user.getSalt());
        return userUnifiedMapper.findByPhoneAndPassword(phone, passwordHash);
    }

    @Override
    public Boolean validateUsernameAndPassword(String username, String password) {
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            return false;
        }
        
        try {
            // 查找用户
            UserUnified user = findByUsername(username);
            if (user == null) {
                log.warn("用户不存在，用户名：{}", username);
                return false;
            }
            
            // 验证密码
            String inputPasswordHash = hashPassword(password, user.getSalt());
            boolean isValid = inputPasswordHash.equals(user.getPasswordHash());
            
            if (isValid) {
                log.info("用户密码验证成功，用户名：{}", username);
                // 更新最后登录时间
                updateLastLoginTime(user.getId());
            } else {
                log.warn("用户密码验证失败，用户名：{}", username);
            }
            
            return isValid;
        } catch (Exception e) {
            log.error("验证用户密码时发生异常，用户名：{}", username, e);
            return false;
        }
    }

    @Override
    @Transactional
    public UserUnifiedRegisterResponse register(UserUnifiedRegisterRequest registerRequest) {
        UserUnifiedRegisterResponse response = new UserUnifiedRegisterResponse();
        
        try {
            // 检查用户名是否已存在 - 参考 nft-turbo 设计，只检查主要标识字段
            if (userUnifiedMapper.findByUsername(registerRequest.getUsername()) != null) {
                response.setSuccess(false);
                response.setResponseCode("USERNAME_ALREADY_EXISTS");
                response.setResponseMessage("用户名已存在");
                return response;
            }
            
            // 如果提供了手机号，检查是否已存在（可选检查）
            if (registerRequest.getPhone() != null && !registerRequest.getPhone().trim().isEmpty()) {
                if (userUnifiedMapper.findByPhone(registerRequest.getPhone()) != null) {
                    response.setSuccess(false);
                    response.setResponseCode("PHONE_ALREADY_EXISTS");
                    response.setResponseMessage("手机号已存在");
                    return response;
                }
            }
            
            // 如果提供了邮箱，检查是否已存在（可选检查）
            if (registerRequest.getEmail() != null && !registerRequest.getEmail().trim().isEmpty()) {
                if (userUnifiedMapper.findByEmail(registerRequest.getEmail()) != null) {
                    response.setSuccess(false);
                    response.setResponseCode("EMAIL_ALREADY_EXISTS");
                    response.setResponseMessage("邮箱已存在");
                    return response;
                }
            }
            
            UserUnified userUnified = buildUserFromRegisterRequest(registerRequest);
            int result = userUnifiedMapper.insert(userUnified);
            if (result <= 0) {
                // 创建自定义ErrorCode
                ErrorCode errorCode = new ErrorCode() {
                    @Override
                    public String getCode() { return "DATABASE_ERROR"; }
                    @Override
                    public String getMessage() { return "用户注册失败"; }
                };
                throw new BizException("数据库操作失败", errorCode);
            }
            
            // 设置成功响应
            response.setSuccess(true);
            response.setResponseCode("SUCCESS");
            response.setResponseMessage("注册成功");
            response.setUserInfo(UserUnifiedConvertor.INSTANCE.toBasicUserUnifiedInfo(userUnified));
            return response;
            
        } catch (Exception e) {
            log.error("用户注册失败", e);
            response.setSuccess(false);
            response.setResponseCode("SYSTEM_ERROR");
            response.setResponseMessage("系统异常");
            return response;
        }
    }

    @Override
    @Transactional
    public UserUnifiedModifyResponse modify(UserUnifiedModifyRequest modifyRequest) {
        UserUnifiedModifyResponse response = new UserUnifiedModifyResponse();
        
        try {
            UserUnified existingUser = userUnifiedMapper.selectById(modifyRequest.getUserId());
            if (existingUser == null) {
                response.setSuccess(false);
                response.setResponseCode("USER_NOT_FOUND");
                response.setResponseMessage("用户不存在");
                return response;
            }
            
            updateUserFromModifyRequest(existingUser, modifyRequest);
            int result = userUnifiedMapper.updateById(existingUser);
            if (result <= 0) {
                ErrorCode errorCode = new ErrorCode() {
                    @Override
                    public String getCode() { return "DATABASE_ERROR"; }
                    @Override
                    public String getMessage() { return "用户信息修改失败"; }
                };
                throw new BizException("数据库操作失败", errorCode);
            }
            
            // 设置成功响应
            response.setSuccess(true);
            response.setResponseCode("SUCCESS");
            response.setResponseMessage("修改成功");
            response.setUserInfo(UserUnifiedConvertor.INSTANCE.toUserUnifiedInfo(existingUser));
            return response;
            
        } catch (Exception e) {
            log.error("用户信息修改失败", e);
            response.setSuccess(false);
            response.setResponseCode("SYSTEM_ERROR");
            response.setResponseMessage("系统异常");
            return response;
        }
    }

    @Override
    @Transactional
    public UserUnifiedActivateResponse activate(UserUnifiedActivateRequest activateRequest) {
        UserUnifiedActivateResponse response = new UserUnifiedActivateResponse();
        
        try {
            UserUnified user = userUnifiedMapper.selectById(activateRequest.getUserId());
            if (user == null) {
                response.setSuccess(false);
                response.setResponseCode("USER_NOT_FOUND");
                response.setResponseMessage("用户不存在");
                return response;
            }
            
            user.setStatus("ACTIVE");
            int result = userUnifiedMapper.updateById(user);
            if (result <= 0) {
                ErrorCode errorCode = new ErrorCode() {
                    @Override
                    public String getCode() { return "DATABASE_ERROR"; }
                    @Override
                    public String getMessage() { return "用户激活失败"; }
                };
                throw new BizException("数据库操作失败", errorCode);
            }
            
            // 设置成功响应
            response.setSuccess(true);
            response.setResponseCode("SUCCESS");
            response.setResponseMessage("激活成功");
            response.setUserInfo(UserUnifiedConvertor.INSTANCE.toBasicUserUnifiedInfo(user));
            return response;
            
        } catch (Exception e) {
            log.error("用户激活失败", e);
            response.setSuccess(false);
            response.setResponseCode("SYSTEM_ERROR");
            response.setResponseMessage("系统异常");
            return response;
        }
    }

    @Override
    @Transactional
    public UserUnifiedBloggerApplyResponse applyBlogger(UserUnifiedBloggerApplyRequest applyRequest) {
        UserUnifiedBloggerApplyResponse response = new UserUnifiedBloggerApplyResponse();
        
        try {
            UserUnified user = userUnifiedMapper.selectById(applyRequest.getUserId());
            if (user == null) {
                response.setSuccess(false);
                response.setResponseCode("USER_NOT_FOUND");
                response.setResponseMessage("用户不存在");
                return response;
            }
            
            user.setBloggerStatus("applying");
            user.setBloggerApplyTime(LocalDateTime.now());
            int result = userUnifiedMapper.updateById(user);
            if (result <= 0) {
                ErrorCode errorCode = new ErrorCode() {
                    @Override
                    public String getCode() { return "DATABASE_ERROR"; }
                    @Override
                    public String getMessage() { return "博主申请失败"; }
                };
                throw new BizException("数据库操作失败", errorCode);
            }
            
            // 设置成功响应
            response.setSuccess(true);
            response.setResponseCode("SUCCESS");
            response.setResponseMessage("申请成功");
            response.setApplyStatus("applying");
            return response;
            
        } catch (Exception e) {
            log.error("博主申请失败", e);
            response.setSuccess(false);
            response.setResponseCode("SYSTEM_ERROR");
            response.setResponseMessage("系统异常");
            return response;
        }
    }

    @Override
    public Boolean checkUsernameAvailable(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        UserUnified user = userUnifiedMapper.findByUsername(username);
        return user == null;
    }

    @Override
    public Boolean checkEmailAvailable(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        UserUnified user = userUnifiedMapper.findByEmail(email);
        return user == null;
    }

    @Override
    public Boolean checkPhoneAvailable(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        UserUnified user = userUnifiedMapper.findByPhone(phone);
        return user == null;
    }

    @Override
    public String generateInviteCode(Long userId) {
        if (userId == null) {
            return null;
        }
        
        // 生成唯一邀请码
        String inviteCode;
        do {
            inviteCode = generateRandomCode(8);
        } while (userUnifiedMapper.findByInviteCode(inviteCode) != null);
        
        // 更新用户邀请码
        UserUnified user = findById(userId);
        if (user != null) {
            user.setInviteCode(inviteCode);
            userUnifiedMapper.updateById(user);
        }
        
        return inviteCode;
    }

    @Override
    public PageResponse<UserUnifiedInfo> pageQuery(UserUnifiedQueryRequest queryRequest) {
        PageResponse<UserUnifiedInfo> response = new PageResponse<>();
        
        try {
            // 默认分页参数
            int currentPage = 1;
            int pageSize = 10;
            
            Page<UserUnified> page = new Page<>(currentPage, pageSize);
            QueryWrapper<UserUnified> queryWrapper = new QueryWrapper<>();
            
            // 根据查询条件构建查询
            UserQueryCondition condition = queryRequest.getUserQueryCondition();
            if (condition instanceof UserUsernameQueryCondition) {
                UserUsernameQueryCondition usernameCondition = (UserUsernameQueryCondition) condition;
                if (usernameCondition.getUsername() != null) {
                    queryWrapper.like("username", usernameCondition.getUsername());
                }
            } else if (condition instanceof UserEmailQueryCondition) {
                UserEmailQueryCondition emailCondition = (UserEmailQueryCondition) condition;
                if (emailCondition.getEmail() != null) {
                    queryWrapper.like("email", emailCondition.getEmail());
                }
            } else if (condition instanceof UserPhoneQueryCondition) {
                UserPhoneQueryCondition phoneCondition = (UserPhoneQueryCondition) condition;
                if (phoneCondition.getPhone() != null) {
                    queryWrapper.like("phone", phoneCondition.getPhone());
                }
            }
            
            IPage<UserUnified> pageResult = userUnifiedMapper.selectPage(page, queryWrapper);
            
            // 转换结果
            List<UserUnifiedInfo> userInfoList = pageResult.getRecords().stream()
                    .map(UserUnifiedConvertor.INSTANCE::toUserUnifiedInfo)
                    .collect(Collectors.toList());
                    
            response.setDatas(userInfoList);
            response.setCurrentPage((int) pageResult.getCurrent());
            response.setPageSize((int) pageResult.getSize());
            response.setTotal(pageResult.getTotal());
            response.setTotalPage((int) pageResult.getPages());
            response.setSuccess(true);
            response.setResponseCode("SUCCESS");
            response.setResponseMessage("查询成功");
            
        } catch (Exception e) {
            log.error("分页查询用户失败", e);
            response.setSuccess(false);
            response.setResponseCode("SYSTEM_ERROR");
            response.setResponseMessage("系统异常");
        }
        
        return response;
    }

    @Override
    public Boolean updateUserStatistics(Long userId, String fieldName, Integer incrementValue) {
        if (userId == null || fieldName == null || incrementValue == null) {
            return false;
        }
        
        try {
            int result = userUnifiedMapper.updateUserStatistics(userId, fieldName, incrementValue);
            return result > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean updateLastLoginTime(Long userId) {
        if (userId == null) {
            return false;
        }
        
        try {
            int result = userUnifiedMapper.updateLastLoginTime(userId);
            return result > 0;
        } catch (Exception e) {
            return false;
        }
    }

    // 新增：管理员相关的方法（这些在接口中需要定义）
    public void approveBloggerStatus(Long userId, boolean approved, String comment) {
        UserUnified user = userUnifiedMapper.selectById(userId);
        if (user != null) {
            user.setBloggerStatus(approved ? "approved" : "rejected");
            if (approved) {
                user.setRole("blogger");
                user.setBloggerApproveTime(LocalDateTime.now());
            }
            userUnifiedMapper.updateById(user);
        }
    }

    public void updateUserStatus(Long userId, String status, String reason, Long operatorId) {
        UserUnified user = userUnifiedMapper.selectById(userId);
        if (user != null) {
            user.setStatus(status);
            userUnifiedMapper.updateById(user);
            // TODO: 记录操作日志
        }
    }

    public void forceActivateUser(Long userId, Long operatorId) {
        updateUserStatus(userId, "ACTIVE", "管理员强制激活", operatorId);
    }

    public void resetPassword(Long userId, String newPassword, Long operatorId) {
        UserUnified user = userUnifiedMapper.selectById(userId);
        if (user != null) {
            String newSalt = generateSalt();
            user.setSalt(newSalt);
            user.setPasswordHash(hashPassword(newPassword, newSalt));
            userUnifiedMapper.updateById(user);
            // TODO: 记录操作日志
        }
    }

    public void setVipLevel(Long userId, Integer vipLevel, Long operatorId) {
        UserUnified user = userUnifiedMapper.selectById(userId);
        if (user != null) {
            // 由于实体中没有vipLevel字段，我们先注释这部分
            // user.setVipLevel(vipLevel);
            if (vipLevel > 0) {
                user.setRole("vip");
                // TODO: 设置VIP过期时间
            } else {
                user.setRole("user");
                user.setVipExpireTime(null);
            }
            userUnifiedMapper.updateById(user);
            // TODO: 记录操作日志
        }
    }

    public void revokeBloggerStatus(Long userId, String reason, Long operatorId) {
        UserUnified user = userUnifiedMapper.selectById(userId);
        if (user != null) {
            user.setBloggerStatus("none");
            user.setRole("user");
            userUnifiedMapper.updateById(user);
            // TODO: 记录操作日志
        }
    }

    public void batchOperateUsers(String operation, List<Long> userIds, Long operatorId) {
        // TODO: 实现批量操作逻辑
        log.info("批量操作用户: operation={}, userIds={}, operatorId={}", operation, userIds, operatorId);
    }

    /**
     * 从注册请求构建用户实体
     * 参考 nft-turbo 设计，简化用户注册流程
     */
    private UserUnified buildUserFromRegisterRequest(UserUnifiedRegisterRequest request) {
        UserUnified userUnified = new UserUnified();
        
        String salt = generateSalt();
        userUnified.setUsername(request.getUsername());
        
        // 如果没有提供昵称，使用用户名作为默认昵称（参考 nft-turbo 设计）
        String nickname = request.getNickname();
        if (nickname == null || nickname.trim().isEmpty()) {
            nickname = request.getUsername();
        }
        userUnified.setNickname(nickname);
        
        // 可选字段，只有在提供时才设置
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            userUnified.setEmail(request.getEmail());
        }
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            userUnified.setPhone(request.getPhone());
        }
        if (request.getAvatar() != null && !request.getAvatar().trim().isEmpty()) {
            userUnified.setAvatar(request.getAvatar());
        }
        if (request.getBio() != null && !request.getBio().trim().isEmpty()) {
            userUnified.setBio(request.getBio());
        }
        if (request.getLocation() != null && !request.getLocation().trim().isEmpty()) {
            userUnified.setLocation(request.getLocation());
        }
        if (request.getGender() != null && !request.getGender().trim().isEmpty()) {
            userUnified.setGender(request.getGender());
        } else {
            userUnified.setGender(DEFAULT_GENDER);
        }
        
        // 邀请相关
        if (request.getInviteCode() != null && !request.getInviteCode().trim().isEmpty()) {
            // TODO: 这里需要验证邀请码并设置邀请人信息
            // userUnified.setInviterId(request.getInviterId());
        }
        
        // 密码处理
        userUnified.setSalt(salt);
        userUnified.setPasswordHash(hashPassword(request.getPassword(), salt));
        
        // 默认设置
        userUnified.setRole(DEFAULT_ROLE);
        userUnified.setStatus(DEFAULT_STATUS);
        userUnified.setBloggerStatus("none");
        
        // 统计字段初始化
        userUnified.setFollowerCount(0L);
        userUnified.setFollowingCount(0L);
        userUnified.setContentCount(0L);
        userUnified.setLikeCount(0L);
        userUnified.setLoginCount(0L);
        userUnified.setInvitedCount(0L);
        
        return userUnified;
    }

    /**
     * 从修改请求更新用户信息
     */
    private void updateUserFromModifyRequest(UserUnified existingUser, UserUnifiedModifyRequest request) {
        if (request.getNickname() != null) {
            existingUser.setNickname(request.getNickname());
        }
        if (request.getAvatar() != null) {
            existingUser.setAvatar(request.getAvatar());
        }
        if (request.getBio() != null) {
            existingUser.setBio(request.getBio());
        }
        if (request.getBirthday() != null) {
            existingUser.setBirthday(request.getBirthday());
        }
        if (request.getGender() != null) {
            existingUser.setGender(request.getGender());
        }
        if (request.getLocation() != null) {
            existingUser.setLocation(request.getLocation());
        }
    }

    /**
     * 生成盐值
     */
    private String generateSalt() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    /**
     * 密码加密
     */
    private String hashPassword(String password, String salt) {
        return DigestUtils.md5Hex((password + salt).getBytes());
    }

    /**
     * 生成随机码
     */
    private String generateRandomCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return sb.toString();
    }
} 