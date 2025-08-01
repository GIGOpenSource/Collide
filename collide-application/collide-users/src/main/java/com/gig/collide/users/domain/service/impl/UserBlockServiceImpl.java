package com.gig.collide.users.domain.service.impl;

import com.gig.collide.api.user.request.UserBlockQueryRequest;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.users.domain.entity.UserBlock;
import com.gig.collide.users.domain.service.UserBlockService;
import com.gig.collide.users.infrastructure.mapper.UserBlockMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户拉黑领域服务实现 - 简洁版
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Slf4j
@Service
public class UserBlockServiceImpl implements UserBlockService {

    @Autowired
    private UserBlockMapper userBlockMapper;

    @Override
    public UserBlock blockUser(Long userId, Long blockedUserId, String userUsername, String blockedUsername, String reason) {
        log.info("用户拉黑操作：用户{}拉黑用户{}", userId, blockedUserId);
        
        // 检查是否已经拉黑
        UserBlock existingBlock = userBlockMapper.findByUserAndBlocked(userId, blockedUserId);
        if (existingBlock != null && "active".equals(existingBlock.getStatus())) {
            log.warn("用户{}已经拉黑了用户{}", userId, blockedUserId);
            return existingBlock;
        }
        
        // 创建新的拉黑记录
        UserBlock userBlock = new UserBlock();
        userBlock.setUserId(userId);
        userBlock.setBlockedUserId(blockedUserId);
        userBlock.setUserUsername(userUsername);
        userBlock.setBlockedUsername(blockedUsername);
        userBlock.setStatus("active");
        userBlock.setReason(reason);
        userBlock.setCreateTime(LocalDateTime.now());
        userBlock.setUpdateTime(LocalDateTime.now());
        
        int result = userBlockMapper.insert(userBlock);
        if (result > 0) {
            log.info("用户拉黑成功：用户{}拉黑用户{}", userId, blockedUserId);
            return userBlock;
        } else {
            log.error("用户拉黑失败：用户{}拉黑用户{}", userId, blockedUserId);
            throw new RuntimeException("拉黑操作失败");
        }
    }

    @Override
    public void unblockUser(Long userId, Long blockedUserId) {
        log.info("取消拉黑操作：用户{}取消拉黑用户{}", userId, blockedUserId);
        
        int result = userBlockMapper.cancelBlock(userId, blockedUserId);
        if (result > 0) {
            log.info("取消拉黑成功：用户{}取消拉黑用户{}", userId, blockedUserId);
        } else {
            log.warn("取消拉黑失败或记录不存在：用户{}取消拉黑用户{}", userId, blockedUserId);
        }
    }

    @Override
    public boolean isBlocked(Long userId, Long blockedUserId) {
        Integer count = userBlockMapper.checkBlockStatus(userId, blockedUserId, "active");
        return count != null && count > 0;
    }

    @Override
    public UserBlock getBlockRelation(Long userId, Long blockedUserId) {
        return userBlockMapper.findByUserAndBlocked(userId, blockedUserId);
    }

    @Override
    public UserBlock getBlockById(Long id) {
        return userBlockMapper.findById(id);
    }

    @Override
    public PageResponse<UserBlock> getUserBlockList(Long userId, Integer currentPage, Integer pageSize) {
        log.info("查询用户拉黑列表：用户ID={}, 页码={}, 页大小={}", userId, currentPage, pageSize);
        
        int offset = (currentPage - 1) * pageSize;
        List<UserBlock> blocks = userBlockMapper.findByUserId(userId, "active", offset, pageSize);
        Long total = userBlockMapper.countByUserId(userId, "active");
        
        return PageResponse.of(blocks, total, pageSize, currentPage);
    }

    @Override
    public PageResponse<UserBlock> getUserBlockedList(Long blockedUserId, Integer currentPage, Integer pageSize) {
        log.info("查询用户被拉黑列表：用户ID={}, 页码={}, 页大小={}", blockedUserId, currentPage, pageSize);
        
        int offset = (currentPage - 1) * pageSize;
        List<UserBlock> blocks = userBlockMapper.findByBlockedUserId(blockedUserId, "active", offset, pageSize);
        Long total = userBlockMapper.countByBlockedUserId(blockedUserId, "active");
        
        return PageResponse.of(blocks, total, pageSize, currentPage);
    }

    @Override
    public PageResponse<UserBlock> queryBlocks(UserBlockQueryRequest request) {
        log.info("分页查询拉黑记录：{}", request);
        
        int offset = (request.getCurrentPage() - 1) * request.getPageSize();
        
        // 处理模糊搜索条件
        String userUsername = StringUtils.hasText(request.getUserUsername()) ? 
            "%" + request.getUserUsername() + "%" : null;
        String blockedUsername = StringUtils.hasText(request.getBlockedUsername()) ? 
            "%" + request.getBlockedUsername() + "%" : null;
        
        List<UserBlock> blocks = userBlockMapper.findBlocksByCondition(
            request.getUserId(),
            request.getBlockedUserId(),
            userUsername,
            blockedUsername,
            request.getStatus(),
            offset,
            request.getPageSize()
        );
        
        Long total = userBlockMapper.countBlocksByCondition(
            request.getUserId(),
            request.getBlockedUserId(),
            userUsername,
            blockedUsername,
            request.getStatus()
        );
        
        return PageResponse.of(blocks, total, request.getPageSize(), request.getCurrentPage());
    }

    @Override
    public Long countUserBlocks(Long userId) {
        return userBlockMapper.countByUserId(userId, "active");
    }

    @Override
    public Long countUserBlocked(Long blockedUserId) {
        return userBlockMapper.countByBlockedUserId(blockedUserId, "active");
    }

    @Override
    public void updateBlockStatus(Long id, String status) {
        log.info("更新拉黑状态：ID={}, 状态={}", id, status);
        
        int result = userBlockMapper.updateStatus(id, status);
        if (result > 0) {
            log.info("更新拉黑状态成功：ID={}, 状态={}", id, status);
        } else {
            log.warn("更新拉黑状态失败：ID={}, 状态={}", id, status);
        }
    }

    @Override
    public void deleteBlock(Long id) {
        log.info("删除拉黑记录：ID={}", id);
        
        int result = userBlockMapper.deleteById(id);
        if (result > 0) {
            log.info("删除拉黑记录成功：ID={}", id);
        } else {
            log.warn("删除拉黑记录失败：ID={}", id);
        }
    }
}