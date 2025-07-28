package com.gig.collide.users.domain.service.impl;

import com.gig.collide.api.user.request.UserQueryRequest;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.users.domain.entity.User;
import com.gig.collide.users.domain.service.UserService;
import com.gig.collide.users.infrastructure.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户领域服务实现 - 简洁版
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User createUser(User user) {
        // 密码加密
        if (user.getPasswordHash() != null) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }
        
        // 生成邀请码
        if (user.getInviteCode() == null) {
            user.setInviteCode(generateInviteCode());
        }
        
        // 设置默认值
        user.setFollowerCount(0L);
        user.setFollowingCount(0L);
        user.setContentCount(0L);
        user.setLikeCount(0L);
        user.setLoginCount(0L);
        user.setInvitedCount(0L);
        
        userMapper.insert(user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        userMapper.updateById(user);
        return user;
    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.findById(userId);
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public PageResponse<User> queryUsers(UserQueryRequest request) {
        int offset = (request.getCurrentPage() - 1) * request.getPageSize();
        
        List<User> users = userMapper.findUsersByCondition(
                request.getUsername(),
                request.getNickname(),
                request.getEmail(),
                request.getPhone(),
                request.getRole(),
                request.getStatus(),
                offset,
                request.getPageSize()
        );
        
        long total = userMapper.countUsersByCondition(
                request.getUsername(),
                request.getNickname(),
                request.getEmail(),
                request.getPhone(),
                request.getRole(),
                request.getStatus()
        );
        
        PageResponse<User> result = new PageResponse<>();
        result.setDatas(users);
        result.setCurrentPage(request.getCurrentPage());
        result.setPageSize(request.getPageSize());
        result.setTotal(total);
        
        return result;
    }

    @Override
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPasswordHash())) {
            // 更新登录时间
            userMapper.updateLastLoginTime(user.getId());
            return user;
        }
        return null;
    }

    @Override
    public void updateUserStatus(Long userId, String status) {
        userMapper.updateUserStatus(userId, status);
    }

    @Override
    public void deleteUser(Long userId) {
        userMapper.deleteById(userId);
    }

    @Override
    public void updateUserStats(Long userId, String statsType, Integer increment) {
        userMapper.updateUserStats(userId, statsType, increment);
    }

    /**
     * 生成邀请码
     */
    private String generateInviteCode() {
        return "INV" + System.currentTimeMillis();
    }
} 