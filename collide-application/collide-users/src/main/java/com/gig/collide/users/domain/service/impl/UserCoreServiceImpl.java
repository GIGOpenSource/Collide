package com.gig.collide.users.domain.service.impl;

import com.gig.collide.base.response.PageResponse;
import com.gig.collide.users.domain.entity.UserCore;
import com.gig.collide.users.domain.service.UserCoreService;
import com.gig.collide.users.domain.service.UserWalletService;
import com.gig.collide.users.infrastructure.mapper.UserCoreMapper;
import com.gig.collide.api.user.request.users.main.UserCoreQueryRequest;
import com.gig.collide.api.user.constant.UserStatusConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 用户核心领域服务实现 - 对应 t_user 表
 * 负责用户基础信息和认证相关功能
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserCoreServiceImpl implements UserCoreService {

    private final UserCoreMapper userCoreMapper;
    private final UserWalletService userWalletService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();



    @Override
    @Transactional
    public UserCore createUser(UserCore userCore) {
        try {
            // 密码加密
            if (userCore.getPasswordHash() != null) {
                userCore.setPasswordHash(passwordEncoder.encode(userCore.getPasswordHash()));
            }
            
            // 生成邀请码
            if (userCore.getInviteCode() == null) {
                userCore.setInviteCode(generateInviteCode());
            }
            
            // 初始化默认值
            userCore.initDefaults();
            
            // 插入用户
            userCoreMapper.insert(userCore);
            
            log.info("用户核心信息创建成功: userId={}, username={}", userCore.getId(), userCore.getUsername());
            return userCore;
        } catch (Exception e) {
            log.error("创建用户核心信息失败: username={}", userCore.getUsername(), e);
            throw new RuntimeException("创建用户失败", e);
        }
    }

    @Override
    public UserCore updateUser(UserCore userCore) {
        try {
            userCore.updateModifyTime();
            userCoreMapper.updateById(userCore);
            log.info("用户核心信息更新成功: userId={}", userCore.getId());
            return userCore;
        } catch (Exception e) {
            log.error("更新用户核心信息失败: userId={}", userCore.getId(), e);
            throw new RuntimeException("更新用户失败", e);
        }
    }

    @Override
    public UserCore getUserById(Long userId) {
        return userCoreMapper.findById(userId);
    }

    @Override
    public UserCore getUserByUsername(String username) {
        return userCoreMapper.findByUsername(username);
    }

    @Override
    public UserCore getUserByEmail(String email) {
        return userCoreMapper.findByEmail(email);
    }

    @Override
    public UserCore getUserByPhone(String phone) {
        return userCoreMapper.findByPhone(phone);
    }

    @Override
    public boolean checkUsernameExists(String username) {
        return userCoreMapper.checkUsernameExists(username) > 0;
    }

    @Override
    public boolean checkEmailExists(String email) {
        return userCoreMapper.checkEmailExists(email) > 0;
    }

    @Override
    public boolean checkPhoneExists(String phone) {
        return userCoreMapper.checkPhoneExists(phone) > 0;
    }

    @Override
    public List<UserCore> batchGetUsers(List<Long> userIds) {
        return userCoreMapper.findUsersByIds(userIds);
    }

    @Override
    public UserCore login(String loginIdentifier, String password, String loginIp) {
        try {
            UserCore user = null;
            
            // 尝试不同的登录方式
            if (loginIdentifier.contains("@")) {
                user = userCoreMapper.findByEmail(loginIdentifier);
            } else if (loginIdentifier.matches("^[0-9]{11}$")) {
                user = userCoreMapper.findByPhone(loginIdentifier);
            } else {
                user = userCoreMapper.findByUsername(loginIdentifier);
            }
            
            if (user != null && passwordEncoder.matches(password, user.getPasswordHash())) {
                if (!user.isActive()) {
                    log.warn("用户登录失败，账户状态异常: userId={}, status={}", user.getId(), user.getStatus());
                    return null;
                }
                
                // 更新登录信息
                updateLoginInfo(user.getId(), loginIp);
                log.info("用户登录成功: userId={}, loginIdentifier={}", user.getId(), loginIdentifier);
                return user;
            }
            
            log.warn("用户登录失败，用户名或密码错误: loginIdentifier={}", loginIdentifier);
            return null;
        } catch (Exception e) {
            log.error("用户登录异常: loginIdentifier={}", loginIdentifier, e);
            return null;
        }
    }

    @Override
    public boolean verifyPassword(Long userId, String password) {
        try {
            String passwordHash = userCoreMapper.getPasswordHash(userId);
            return passwordHash != null && passwordEncoder.matches(password, passwordHash);
        } catch (Exception e) {
            log.error("验证密码失败: userId={}", userId, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        try {
            if (!verifyPassword(userId, oldPassword)) {
                log.warn("修改密码失败，旧密码错误: userId={}", userId);
                return false;
            }
            
            String newPasswordHash = passwordEncoder.encode(newPassword);
            int result = userCoreMapper.updatePassword(userId, newPasswordHash);
            
            if (result > 0) {
                log.info("用户密码修改成功: userId={}", userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("修改密码失败: userId={}", userId, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean resetPassword(Long userId, String newPassword) {
        try {
            String newPasswordHash = passwordEncoder.encode(newPassword);
            int result = userCoreMapper.updatePassword(userId, newPasswordHash);
            
            if (result > 0) {
                log.info("管理员重置用户密码成功: userId={}", userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("重置密码失败: userId={}", userId, e);
            return false;
        }
    }

    @Override
    public boolean updateUserStatus(Long userId, Integer status) {
        try {
            int result = userCoreMapper.updateUserStatus(userId, status);
            if (result > 0) {
                log.info("用户状态更新成功: userId={}, status={}", userId, status);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新用户状态失败: userId={}, status={}", userId, status, e);
            return false;
        }
    }

    @Override
    public boolean updateLoginInfo(Long userId, String loginIp) {
        try {
            UserCore user = getUserById(userId);
            if (user != null) {
                // 只更新用户修改时间，登录统计移到UserStats处理
                user.updateModifyTime();
                updateUser(user);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新登录信息失败: userId={}", userId, e);
            return false;
        }
    }

    @Override
    public String generateInviteCode(Long userId) {
        String inviteCode = "INV" + System.currentTimeMillis() + userId;
        UserCore user = getUserById(userId);
        if (user != null) {
            user.setInviteCode(inviteCode);
            updateUser(user);
        }
        return inviteCode;
    }

    @Override
    @Transactional
    public UserCore registerByInviteCode(String inviteCode, UserCore userCore) {
        try {
            // 验证邀请码（这里简化处理，实际应该有更复杂的验证逻辑）
            userCore.setInviteCode(generateInviteCode());
            return createUser(userCore);
        } catch (Exception e) {
            log.error("通过邀请码注册失败: inviteCode={}", inviteCode, e);
            throw new RuntimeException("注册失败", e);
        }
    }

    @Override
    public PageResponse<UserCore> queryUsers(UserCoreQueryRequest request) {
        try {
            int offset = (request.getCurrentPage() - 1) * request.getPageSize();
            
            List<UserCore> users = userCoreMapper.findUsersByCondition(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPhone(),
                    request.getStatus(),
                    request.getCreateTimeStart() != null ? request.getCreateTimeStart().toString() : null,
                    request.getCreateTimeEnd() != null ? request.getCreateTimeEnd().toString() : null,
                    offset,
                    request.getPageSize()
            );
            
            long total = userCoreMapper.countUsersByCondition(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPhone(),
                    request.getStatus(),
                    request.getCreateTimeStart() != null ? request.getCreateTimeStart().toString() : null,
                    request.getCreateTimeEnd() != null ? request.getCreateTimeEnd().toString() : null
            );
            
            PageResponse<UserCore> result = new PageResponse<>();
            result.setDatas(users);
            result.setCurrentPage(request.getCurrentPage());
            result.setPageSize(request.getPageSize());
            result.setTotal(total);
            
            return result;
        } catch (Exception e) {
            log.error("查询用户列表失败", e);
            throw new RuntimeException("查询用户失败", e);
        }
    }

    @Override
    @Transactional
    public boolean deleteUser(Long userId) {
        try {
            int result = userCoreMapper.deleteById(userId);
            if (result > 0) {
                log.warn("用户被物理删除: userId={}", userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("删除用户失败: userId={}", userId, e);
            return false;
        }
    }

    @Override
    public boolean isUserValid(Long userId) {
        UserCore user = getUserById(userId);
        return user != null && user.isActive();
    }

    @Override
    public boolean activateUser(Long userId) {
        return updateUserStatus(userId, UserStatusConstant.ACTIVE);
    }

    @Override
    public boolean disableUser(Long userId) {
        // 注意：根据新的状态定义，这里应该是设置为未激活状态
        // 如果需要真正的"禁用"，应该使用suspendUser或banUser
        log.warn("disableUser方法语义已变更，现在设置为INACTIVE状态: userId={}", userId);
        return updateUserStatus(userId, UserStatusConstant.INACTIVE);
    }

    @Override
    public boolean lockUser(Long userId) {
        // 注意：根据新的状态定义，这里应该是设置为暂停状态
        log.warn("lockUser方法语义已变更，现在设置为SUSPENDED状态: userId={}", userId);
        return updateUserStatus(userId, UserStatusConstant.SUSPENDED);
    }

    @Override
    public boolean unlockUser(Long userId) {
        return updateUserStatus(userId, UserStatusConstant.ACTIVE);
    }

    /**
     * 暂停用户（新增方法，语义更清晰）
     */
    public boolean suspendUser(Long userId) {
        return updateUserStatus(userId, UserStatusConstant.SUSPENDED);
    }

    /**
     * 封禁用户（新增方法）
     */
    public boolean banUser(Long userId) {
        return updateUserStatus(userId, UserStatusConstant.BANNED);
    }

    /**
     * 设置用户为未激活状态（新增方法）
     */
    public boolean inactiveUser(Long userId) {
        return updateUserStatus(userId, UserStatusConstant.INACTIVE);
    }

    /**
     * 生成邀请码
     */
    private String generateInviteCode() {
        return "INV" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}