package com.gig.collide.users.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.user.constant.UserRole;
import com.gig.collide.api.user.constant.UserStateEnum;
import com.gig.collide.users.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * 用户仓储实现
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            userMapper.insert(user);
        } else {
            userMapper.updateById(user);
        }
        return user;
    }

    @Override
    public Optional<User> findById(Long userId) {
        User user = userMapper.selectById(userId);
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        User user = userMapper.selectByUsername(username);
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        User user = userMapper.selectByEmail(email);
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        User user = userMapper.selectByPhone(phone);
        return Optional.ofNullable(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        return userMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        return userMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean existsByPhone(String phone) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        return userMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public void deleteById(Long userId) {
        userMapper.deleteById(userId);
    }

    @Override
    public IPage<User> pageQuery(Integer pageNum, Integer pageSize, String usernameKeyword, 
                                String status, String role) {
        
        log.info("执行用户分页查询，页码：{}，页大小：{}，关键词：{}，状态：{}，角色：{}", 
            pageNum, pageSize, usernameKeyword, status, role);

        // 设置分页参数
        Page<User> page = new Page<>(pageNum, pageSize);

        // 构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        // 用户名关键词搜索
        if (StringUtils.hasText(usernameKeyword)) {
            queryWrapper.like(User::getUsername, usernameKeyword)
                       .or()
                       .like(User::getNickname, usernameKeyword);
        }

        // 用户状态过滤
        if (StringUtils.hasText(status)) {
            try {
                UserStateEnum userStatus = UserStateEnum.valueOf(status);
                queryWrapper.eq(User::getStatus, userStatus);
            } catch (IllegalArgumentException e) {
                log.warn("无效的用户状态参数：{}", status);
            }
        }

        // 用户角色过滤
        if (StringUtils.hasText(role)) {
            try {
                UserRole userRole = UserRole.valueOf(role);
                queryWrapper.eq(User::getRole, userRole);
            } catch (IllegalArgumentException e) {
                log.warn("无效的用户角色参数：{}", role);
            }
        }

        // 按创建时间倒序排列
        queryWrapper.orderByDesc(User::getCreateTime);

        // 执行分页查询
        IPage<User> result = userMapper.selectPage(page, queryWrapper);
        
        log.info("分页查询完成，总记录数：{}，当前页记录数：{}", result.getTotal(), result.getRecords().size());
        
        return result;
    }
} 