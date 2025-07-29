package com.gig.collide.like.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.like.domain.entity.Like;
import com.gig.collide.like.domain.service.LikeService;
import com.gig.collide.like.infrastructure.mapper.LikeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 点赞业务逻辑实现类 - 简洁版
 * 基于like-simple.sql的业务逻辑，实现核心点赞功能
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeMapper likeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Like addLike(Like like) {
        log.info("添加点赞: userId={}, likeType={}, targetId={}", 
                like.getUserId(), like.getLikeType(), like.getTargetId());

        // 验证请求参数
        String validationResult = validateLikeRequest(like);
        if (validationResult != null) {
            throw new IllegalArgumentException(validationResult);
        }

        // 检查是否已存在点赞记录
        Like existingLike = likeMapper.findByUserAndTarget(
                like.getUserId(), like.getLikeType(), like.getTargetId());

        if (existingLike != null) {
            if (existingLike.isActive()) {
                log.warn("用户已点赞该对象: userId={}, targetId={}", like.getUserId(), like.getTargetId());
                return existingLike;
            } else {
                // 重新激活已取消的点赞
                existingLike.activate();
                // 更新冗余信息
                existingLike.setTargetTitle(like.getTargetTitle());
                existingLike.setTargetAuthorId(like.getTargetAuthorId());
                existingLike.setUserNickname(like.getUserNickname());
                existingLike.setUserAvatar(like.getUserAvatar());
                
                likeMapper.updateById(existingLike);
                log.info("重新激活点赞记录: id={}", existingLike.getId());
                return existingLike;
            }
        }

        // 创建新的点赞记录
        like.setStatus("active");
        like.setCreateTime(LocalDateTime.now());
        like.setUpdateTime(LocalDateTime.now());

        int result = likeMapper.insert(like);
        if (result > 0) {
            log.info("创建点赞记录成功: id={}", like.getId());
            return like;
        } else {
            throw new RuntimeException("创建点赞记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelLike(Long userId, String likeType, Long targetId) {
        log.info("取消点赞: userId={}, likeType={}, targetId={}", userId, likeType, targetId);

        // 查找活跃的点赞记录
        Like existingLike = likeMapper.findByUserAndTarget(userId, likeType, targetId);
        if (existingLike == null || existingLike.isCancelled()) {
            log.warn("未找到活跃的点赞记录: userId={}, targetId={}", userId, targetId);
            return false;
        }

        // 更新状态为cancelled
        int result = likeMapper.updateLikeStatus(existingLike.getId(), "cancelled");
        if (result > 0) {
            log.info("取消点赞成功: id={}", existingLike.getId());
            return true;
        } else {
            log.error("取消点赞失败: id={}", existingLike.getId());
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Like toggleLike(Like like) {
        log.info("切换点赞状态: userId={}, likeType={}, targetId={}", 
                like.getUserId(), like.getLikeType(), like.getTargetId());

        // 检查当前状态
        boolean isLiked = checkLikeStatus(like.getUserId(), like.getLikeType(), like.getTargetId());

        if (isLiked) {
            // 如果已点赞，则取消
            boolean cancelled = cancelLike(like.getUserId(), like.getLikeType(), like.getTargetId());
            return cancelled ? null : like; // 取消成功返回null
        } else {
            // 如果未点赞，则添加
            return addLike(like);
        }
    }

    @Override
    public boolean checkLikeStatus(Long userId, String likeType, Long targetId) {
        Boolean exists = likeMapper.checkLikeExists(userId, likeType, targetId);
        return exists != null && exists;
    }

    @Override
    public Like getLikeRecord(Long userId, String likeType, Long targetId) {
        return likeMapper.findByUserAndTarget(userId, likeType, targetId);
    }

    @Override
    public IPage<Like> queryLikes(Integer pageNum, Integer pageSize, Long userId, String likeType,
                                 Long targetId, Long targetAuthorId, String status,
                                 String orderBy, String orderDirection) {
        log.info("分页查询点赞记录: pageNum={}, pageSize={}, userId={}, likeType={}", 
                pageNum, pageSize, userId, likeType);

        // 构建分页对象
        Page<Like> page = new Page<>(pageNum, pageSize);

        // 构建查询条件
        LambdaQueryWrapper<Like> queryWrapper = new LambdaQueryWrapper<>();
        
        if (userId != null) {
            queryWrapper.eq(Like::getUserId, userId);
        }
        if (StringUtils.hasText(likeType)) {
            queryWrapper.eq(Like::getLikeType, likeType);
        }
        if (targetId != null) {
            queryWrapper.eq(Like::getTargetId, targetId);
        }
        if (targetAuthorId != null) {
            queryWrapper.eq(Like::getTargetAuthorId, targetAuthorId);
        }
        if (StringUtils.hasText(status)) {
            queryWrapper.eq(Like::getStatus, status);
        }

        // 设置排序
        if ("ASC".equalsIgnoreCase(orderDirection)) {
            if ("update_time".equals(orderBy)) {
                queryWrapper.orderByAsc(Like::getUpdateTime);
            } else {
                queryWrapper.orderByAsc(Like::getCreateTime);
            }
        } else {
            if ("update_time".equals(orderBy)) {
                queryWrapper.orderByDesc(Like::getUpdateTime);
            } else {
                queryWrapper.orderByDesc(Like::getCreateTime);
            }
        }

        return likeMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Long getLikeCount(String likeType, Long targetId) {
        return likeMapper.countTargetLikes(targetId, likeType, "active");
    }

    @Override
    public Long getUserLikeCount(Long userId, String likeType) {
        return likeMapper.countUserLikes(userId, likeType, "active");
    }

    @Override
    public Map<Long, Boolean> batchCheckLikeStatus(Long userId, String likeType, List<Long> targetIds) {
        if (targetIds == null || targetIds.isEmpty()) {
            return new HashMap<>();
        }

        // 获取已点赞的目标ID列表
        List<Long> likedTargetIds = likeMapper.batchCheckLikeStatus(userId, likeType, targetIds);

        // 构建结果Map
        Map<Long, Boolean> resultMap = new HashMap<>();
        for (Long targetId : targetIds) {
            resultMap.put(targetId, likedTargetIds.contains(targetId));
        }

        return resultMap;
    }

    @Override
    public Like getLikeById(Long id) {
        return likeMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteLike(Long id) {
        log.warn("物理删除点赞记录: id={}", id);
        int result = likeMapper.deleteById(id);
        return result > 0;
    }

    @Override
    public String validateLikeRequest(Like like) {
        if (like == null) {
            return "点赞对象不能为空";
        }

        if (like.getUserId() == null) {
            return "用户ID不能为空";
        }

        if (!StringUtils.hasText(like.getLikeType())) {
            return "点赞类型不能为空";
        }

        if (like.getTargetId() == null) {
            return "目标对象ID不能为空";
        }

        // 验证点赞类型
        if (!"CONTENT".equals(like.getLikeType()) && 
            !"COMMENT".equals(like.getLikeType()) && 
            !"DYNAMIC".equals(like.getLikeType())) {
            return "点赞类型只能是CONTENT、COMMENT或DYNAMIC";
        }

        // 验证ID有效性
        if (like.getUserId() <= 0) {
            return "用户ID必须大于0";
        }

        if (like.getTargetId() <= 0) {
            return "目标对象ID必须大于0";
        }

        return null; // 验证通过
    }
}