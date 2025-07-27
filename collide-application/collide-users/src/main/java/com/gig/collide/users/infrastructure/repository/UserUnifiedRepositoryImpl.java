package com.gig.collide.users.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.user.constant.UserRole;
import com.gig.collide.api.user.constant.UserStateEnum;
import com.gig.collide.users.domain.entity.UserUnified;
import com.gig.collide.users.domain.repository.UserUnifiedRepository;
import com.gig.collide.users.infrastructure.mapper.UserUnifiedMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 用户统一仓储实现（去连表设计）
 * 所有数据访问操作基于单表，无连表查询
 *
 * @author GIG Team
 * @version 2.0 (重构版本)
 * @since 2024-01-01
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class UserUnifiedRepositoryImpl implements UserUnifiedRepository {

    private final UserUnifiedMapper userUnifiedMapper;

    // ================================ 基础CRUD操作 ================================

    @Override
    public UserUnified save(UserUnified user) {
        if (user.getId() == null) {
            log.debug("插入新用户：{}", user.getUsername());
            userUnifiedMapper.insert(user);
        } else {
            log.debug("更新用户信息：{}", user.getId());
            userUnifiedMapper.updateById(user);
        }
        return user;
    }

    @Override
    public Optional<UserUnified> findById(Long userId) {
        UserUnified user = userUnifiedMapper.selectById(userId);
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<UserUnified> findByUsername(String username) {
        UserUnified user = userUnifiedMapper.selectByUsername(username);
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<UserUnified> findByEmail(String email) {
        UserUnified user = userUnifiedMapper.selectByEmail(email);
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<UserUnified> findByPhone(String phone) {
        UserUnified user = userUnifiedMapper.selectByPhone(phone);
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<UserUnified> findByInviteCode(String inviteCode) {
        UserUnified user = userUnifiedMapper.selectByInviteCode(inviteCode);
        return Optional.ofNullable(user);
    }

    @Override
    public void deleteById(Long userId) {
        log.info("逻辑删除用户：{}", userId);
        userUnifiedMapper.deleteById(userId);
    }

    // ================================ 存在性检查 ================================

    @Override
    public boolean existsByUsername(String username) {
        return userUnifiedMapper.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userUnifiedMapper.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return userUnifiedMapper.existsByPhone(phone);
    }

    @Override
    public boolean existsByInviteCode(String inviteCode) {
        return userUnifiedMapper.existsByInviteCode(inviteCode);
    }

    // ================================ 搜索和分页查询 ================================

    @Override
    public IPage<UserUnified> searchUsers(String keyword, Integer pageNum, Integer pageSize) {
        log.info("搜索用户，关键词：{}，页码：{}，页大小：{}", keyword, pageNum, pageSize);
        
        // 参数校验和默认值设置
        pageNum = (pageNum == null || pageNum < 1) ? 1 : pageNum;
        pageSize = (pageSize == null || pageSize < 1 || pageSize > 100) ? 10 : pageSize;
        
        int offset = (pageNum - 1) * pageSize;
        
        // 执行搜索查询
        List<UserUnified> users = userUnifiedMapper.searchUsers(keyword, offset, pageSize);
        long total = userUnifiedMapper.countSearchUsers(keyword);
        
        // 构建分页结果
        Page<UserUnified> page = new Page<>(pageNum, pageSize);
        page.setRecords(users);
        page.setTotal(total);
        
        log.info("搜索完成，找到 {} 条记录", total);
        return page;
    }

    @Override
    public IPage<UserUnified> pageQuery(Integer pageNum, Integer pageSize, 
                                       String usernameKeyword, String status, String role) {
        
        log.info("分页查询用户，页码：{}，页大小：{}，关键词：{}，状态：{}，角色：{}", 
            pageNum, pageSize, usernameKeyword, status, role);

        // 参数验证
        pageNum = (pageNum == null || pageNum < 1) ? 1 : pageNum;
        pageSize = (pageSize == null || pageSize < 1 || pageSize > 100) ? 10 : pageSize;

        // 设置分页参数
        Page<UserUnified> page = new Page<>(pageNum, pageSize);

        // 构建查询条件
        LambdaQueryWrapper<UserUnified> queryWrapper = new LambdaQueryWrapper<>();

        // 用户名关键词搜索
        if (StringUtils.hasText(usernameKeyword)) {
            queryWrapper.like(UserUnified::getUsername, usernameKeyword)
                       .or()
                       .like(UserUnified::getNickname, usernameKeyword);
        }

        // 用户状态过滤
        if (StringUtils.hasText(status)) {
            try {
                UserStateEnum userStatus = UserStateEnum.valueOf(status);
                queryWrapper.eq(UserUnified::getStatus, userStatus);
            } catch (IllegalArgumentException e) {
                log.warn("无效的用户状态参数：{}", status);
            }
        }

        // 用户角色过滤
        if (StringUtils.hasText(role)) {
            try {
                UserRole userRole = UserRole.valueOf(role);
                queryWrapper.eq(UserUnified::getRole, userRole);
            } catch (IllegalArgumentException e) {
                log.warn("无效的用户角色参数：{}", role);
            }
        }

        // 按创建时间倒序排列
        queryWrapper.orderByDesc(UserUnified::getCreateTime);

        // 执行分页查询
        IPage<UserUnified> result = userUnifiedMapper.selectPage(page, queryWrapper);
        
        log.info("分页查询完成，总记录数：{}，当前页记录数：{}", result.getTotal(), result.getRecords().size());
        
        return result;
    }

    // ================================ 博主相关查询 ================================

    @Override
    public IPage<UserUnified> findByBloggerStatus(String bloggerStatus, Integer pageNum, Integer pageSize) {
        log.info("根据博主状态分页查询：{}，页码：{}，页大小：{}", bloggerStatus, pageNum, pageSize);
        
        pageNum = (pageNum == null || pageNum < 1) ? 1 : pageNum;
        pageSize = (pageSize == null || pageSize < 1 || pageSize > 100) ? 10 : pageSize;
        
        int offset = (pageNum - 1) * pageSize;
        
        List<UserUnified> users = userUnifiedMapper.selectByBloggerStatus(bloggerStatus, offset, pageSize);
        long total = userUnifiedMapper.countByBloggerStatus(bloggerStatus);
        
        Page<UserUnified> page = new Page<>(pageNum, pageSize);
        page.setRecords(users);
        page.setTotal(total);
        
        return page;
    }

    @Override
    public long countByBloggerStatus(String bloggerStatus) {
        return userUnifiedMapper.countByBloggerStatus(bloggerStatus);
    }

    // ================================ 邀请相关查询 ================================

    @Override
    public IPage<UserUnified> findByInviterId(Long inviterId, Integer pageNum, Integer pageSize) {
        log.info("根据邀请人ID分页查询：{}，页码：{}，页大小：{}", inviterId, pageNum, pageSize);
        
        pageNum = (pageNum == null || pageNum < 1) ? 1 : pageNum;
        pageSize = (pageSize == null || pageSize < 1 || pageSize > 100) ? 10 : pageSize;
        
        int offset = (pageNum - 1) * pageSize;
        
        List<UserUnified> users = userUnifiedMapper.selectByInviterId(inviterId, offset, pageSize);
        
        // 计算总数
        LambdaQueryWrapper<UserUnified> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(UserUnified::getInviterId, inviterId);
        long total = userUnifiedMapper.selectCount(countWrapper);
        
        Page<UserUnified> page = new Page<>(pageNum, pageSize);
        page.setRecords(users);
        page.setTotal(total);
        
        return page;
    }

    @Override
    public List<UserUnified> getInviteRanking(int limit) {
        log.info("获取邀请排行榜，限制：{}", limit);
        return userUnifiedMapper.getInviteRanking(limit);
    }

    // ================================ 统计字段更新 ================================

    @Override
    public boolean updateStatisticsField(Long userId, String field, long increment) {
        log.debug("更新用户统计字段，用户ID：{}，字段：{}，增量：{}", userId, field, increment);
        int result = userUnifiedMapper.updateStatisticsField(userId, field, increment);
        return result > 0;
    }

    @Override
    public int batchUpdateStatisticsField(List<Long> userIds, String field, long increment) {
        log.info("批量更新用户统计字段，用户数：{}，字段：{}，增量：{}", userIds.size(), field, increment);
        return userUnifiedMapper.batchUpdateStatisticsField(userIds, field, increment);
    }

    // ================================ 缓存优化方法 ================================

    @Override
    public Optional<UserUnified> findKeyInfoById(Long userId) {
        UserUnified user = userUnifiedMapper.selectKeyInfoById(userId);
        return Optional.ofNullable(user);
    }

    // ================================ 统计相关方法 ================================

    @Override
    public long countActiveUsers(int days) {
        return userUnifiedMapper.countActiveUsers(days);
    }

    @Override
    public Map<String, Long> getUserStatistics() {
        return userUnifiedMapper.getUserStatistics();
    }

    // ================================ 批量操作方法 ================================

    @Override
    public int batchSave(List<UserUnified> users) {
        log.info("批量保存用户，数量：{}", users.size());
        
        int successCount = 0;
        for (UserUnified user : users) {
            try {
                save(user);
                successCount++;
            } catch (Exception e) {
                log.error("批量保存用户失败，用户名：{}", user.getUsername(), e);
            }
        }
        
        log.info("批量保存完成，成功：{}，总计：{}", successCount, users.size());
        return successCount;
    }

    @Override
    public int batchUpdateStatus(List<Long> userIds, String status) {
        log.info("批量更新用户状态，用户数：{}，新状态：{}", userIds.size(), status);
        
        try {
            UserStateEnum userStatus = UserStateEnum.valueOf(status);
            
            LambdaUpdateWrapper<UserUnified> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(UserUnified::getStatus, userStatus)
                        .in(UserUnified::getId, userIds);
            
            int result = userUnifiedMapper.update(null, updateWrapper);
            log.info("批量更新状态完成，影响行数：{}", result);
            return result;
            
        } catch (IllegalArgumentException e) {
            log.error("无效的用户状态：{}", status, e);
            return 0;
        }
    }

    // ================================ 幂等性操作方法 ================================

    @Override
    public boolean updateStatusIdempotent(Long userId, String expectedStatus, String newStatus, Integer version) {
        log.info("幂等性更新用户状态，用户ID：{}，期望状态：{}，新状态：{}，版本：{}", 
            userId, expectedStatus, newStatus, version);
        
        int result = userUnifiedMapper.updateStatusIdempotent(userId, expectedStatus, newStatus, version);
        boolean success = result > 0;
        
        if (success) {
            log.info("用户状态更新成功，用户ID：{}，新状态：{}", userId, newStatus);
        } else {
            log.warn("用户状态更新失败，可能是版本冲突或状态不匹配，用户ID：{}，期望状态：{}，新状态：{}，版本：{}", 
                userId, expectedStatus, newStatus, version);
        }
        
        return success;
    }

    @Override
    public boolean updateRoleIdempotent(Long userId, String expectedRole, String newRole, Integer version) {
        log.info("幂等性更新用户角色，用户ID：{}，期望角色：{}，新角色：{}，版本：{}", 
            userId, expectedRole, newRole, version);
        
        int result = userUnifiedMapper.updateRoleIdempotent(userId, expectedRole, newRole, version);
        boolean success = result > 0;
        
        if (success) {
            log.info("用户角色更新成功，用户ID：{}，新角色：{}", userId, newRole);
        } else {
            log.warn("用户角色更新失败，可能是版本冲突或角色不匹配，用户ID：{}，期望角色：{}，新角色：{}，版本：{}", 
                userId, expectedRole, newRole, version);
        }
        
        return success;
    }

    @Override
    public boolean updateBloggerStatusIdempotent(Long userId, String expectedBloggerStatus, 
                                                String newBloggerStatus, Integer version) {
        log.info("幂等性更新博主认证状态，用户ID：{}，期望状态：{}，新状态：{}，版本：{}", 
            userId, expectedBloggerStatus, newBloggerStatus, version);
        
        int result = userUnifiedMapper.updateBloggerStatusIdempotent(userId, expectedBloggerStatus, newBloggerStatus, version);
        boolean success = result > 0;
        
        if (success) {
            log.info("博主认证状态更新成功，用户ID：{}，新状态：{}", userId, newBloggerStatus);
        } else {
            log.warn("博主认证状态更新失败，可能是版本冲突或状态不匹配，用户ID：{}，期望状态：{}，新状态：{}，版本：{}", 
                userId, expectedBloggerStatus, newBloggerStatus, version);
        }
        
        return success;
    }
} 