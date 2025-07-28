package com.gig.collide.like.domain.service;

import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.like.response.LikeResponse;
import com.gig.collide.cache.constant.CacheConstant;
import com.gig.collide.like.domain.entity.Like;
import com.gig.collide.like.infrastructure.mapper.LikeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 点赞领域服务 - 标准化设计
 * 使用去连表化设计，采用JetCache标准化缓存
 * 
 * @author Collide
 * @since 2.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LikeDomainService {
    
    private final LikeMapper likeMapper;
    
    /**
     * 执行点赞操作（带幂等性保证和缓存优化）
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = CacheConstant.LIKE_USER_STATUS_CACHE, key = "#request.userId + ':' + #request.targetId + ':' + #request.likeType.code")
    @CacheInvalidate(name = CacheConstant.LIKE_STATISTICS_CACHE, key = "#request.targetId + ':' + #request.likeType.code")
    public Like performLikeAction(LikeRequest request) {
        Long userId = request.getUserId();
        Long targetId = request.getTargetId();
        String targetType = request.getLikeType().getCode();
        Integer actionType = request.getAction().getValue();
        
        log.info("执行点赞操作，用户ID：{}，目标ID：{}，目标类型：{}，操作类型：{}", 
                userId, targetId, targetType, actionType);
        
        // 使用分布式锁确保幂等性
        String lockKey = String.format("like:lock:%d:%d:%s", userId, targetId, targetType);
        
        try {
            // 获取分布式锁（这里可以使用 collide-lock 组件）
            return executeWithLock(lockKey, () -> performLikeActionInternal(request));
        } catch (Exception e) {
            log.error("点赞操作执行失败", e);
            throw new RuntimeException("点赞操作失败：" + e.getMessage());
        }
    }
    
    /**
     * 内部执行点赞操作逻辑
     */
    private Like performLikeActionInternal(LikeRequest request) {
        Long userId = request.getUserId();
        Long targetId = request.getTargetId();
        String targetType = request.getLikeType().getCode();
        Integer actionType = request.getAction().getValue();
        
        // 查询现有点赞记录
        Like existingLike = likeMapper.selectByUserAndTarget(userId, targetId, targetType);
        
        Like likeRecord;
        if (existingLike == null) {
            // 创建新的点赞记录
            likeRecord = createNewLikeRecord(request);
            try {
                likeMapper.insert(likeRecord);
            } catch (DuplicateKeyException e) {
                // 处理唯一约束冲突
                log.warn("唯一约束冲突，重新查询记录，用户ID：{}，目标ID：{}", userId, targetId);
                existingLike = likeMapper.selectByUserAndTarget(userId, targetId, targetType);
                if (existingLike != null) {
                    likeRecord = updateExistingLikeRecord(existingLike, actionType, request);
                    likeMapper.updateById(likeRecord);
                } else {
                    throw new RuntimeException("数据异常，请重试");
                }
            }
        } else {
            // 更新现有点赞记录（幂等性检查）
            if (existingLike.getActionType().equals(actionType)) {
                log.info("用户{}对{}的操作类型{}未变化，保持幂等性", 
                    existingLike.getUserId(), existingLike.getTargetId(), actionType);
                return existingLike;
            }
            likeRecord = updateExistingLikeRecord(existingLike, actionType, request);
            likeMapper.updateById(likeRecord);
        }
        
        // 更新目标对象的统计字段
        updateTargetStatistics(targetId, targetType, existingLike, actionType);
        
        return likeRecord;
    }
    
    /**
     * 批量点赞操作（全局事务）
     */
    @Transactional(rollbackFor = Exception.class)
    public LikeResponse batchLikeAction(List<LikeRequest> likeRequests) {
        int successCount = 0;
        int failCount = 0;
        
        try {
            for (LikeRequest request : likeRequests) {
                try {
                    performLikeAction(request);
                    successCount++;
                } catch (Exception e) {
                    log.error("批量点赞中单个操作失败，用户ID：{}，目标对象ID：{}", 
                            request.getUserId(), request.getTargetId(), e);
                    failCount++;
                    
                    // 如果失败率超过50%，回滚整个事务
                    if (failCount > likeRequests.size() / 2) {
                        log.error("批量操作失败率过高，回滚事务。成功：{}，失败：{}", successCount, failCount);
                        throw new RuntimeException("批量操作失败率过高，已回滚");
                    }
                }
            }
            
            if (failCount == 0) {
                return LikeResponse.success(0L, 0L, "批量操作全部成功");
            } else {
                log.warn("批量操作部分成功，成功：{}，失败：{}", successCount, failCount);
                return LikeResponse.success(0L, 0L, 
                        String.format("批量操作完成，成功：%d，失败：%d", successCount, failCount));
            }
            
        } catch (Exception e) {
            log.error("批量点赞操作异常，成功：{}，失败：{}", successCount, failCount, e);
            throw e; // 重新抛出异常，触发事务回滚
        }
    }
    
    /**
     * 查询用户对对象的点赞状态（带缓存）
     */
    @Cached(name = CacheConstant.LIKE_USER_STATUS_CACHE, 
            key = "#userId + ':' + #targetId + ':' + #likeType.code",
            expire = CacheConstant.LIKE_STATUS_CACHE_EXPIRE, 
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Like getUserLikeStatus(Long userId, Long targetId, LikeType likeType) {
        return likeMapper.selectByUserAndTarget(userId, targetId, likeType.getCode());
    }
    
    /**
     * 获取对象的点赞统计（带缓存）
     */
    @Cached(name = CacheConstant.LIKE_STATISTICS_CACHE,
            key = "#targetId + ':' + #likeType.code",
            expire = CacheConstant.LIKE_STATISTICS_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public LikeStatistics getLikeStatistics(Long targetId, LikeType likeType) {
        String targetType = likeType.getCode();
        
        // 直接从点赞表统计，无需连表
        Long likeCount = likeMapper.countLikesByTarget(targetId, targetType, 1);
        Long dislikeCount = likeMapper.countLikesByTarget(targetId, targetType, -1);
        
        LikeStatistics statistics = new LikeStatistics();
        statistics.setTargetId(targetId);
        statistics.setTargetType(targetType);
        statistics.setTotalLikeCount(likeCount);
        statistics.setTotalDislikeCount(dislikeCount);
        
        return statistics;
    }
    
    /**
     * 分页查询点赞记录（带缓存）
     */
    @Cached(name = CacheConstant.LIKE_USER_HISTORY_CACHE,
            key = "#request.userId + ':' + #request.likeType?.code + ':' + #request.currentPage + ':' + #request.pageSize",
            expire = CacheConstant.LIKE_HISTORY_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.REMOTE,
            condition = "#request.userId != null")
    public IPage<Like> queryLikes(LikeQueryRequest request) {
        Page<Like> page = new Page<>(request.getCurrentPage(), request.getPageSize());
        
        String targetType = request.getLikeType() != null ? request.getLikeType().getCode() : null;
        Integer actionType = null; // 根据需要设置
        
        return likeMapper.selectLikePage(page, 
                                        request.getUserId(),
                                        request.getTargetId(),
                                        targetType,
                                        actionType,
                                        request.getStartTime(),
                                        request.getEndTime());
    }
    
    /**
     * 获取用户点赞历史（带缓存）
     */
    @Cached(name = CacheConstant.LIKE_USER_HISTORY_CACHE,
            key = "'history:' + #request.userId + ':' + #request.likeType?.code + ':' + #request.currentPage",
            expire = CacheConstant.LIKE_HISTORY_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.REMOTE)
    public IPage<Like> getUserLikeHistory(LikeQueryRequest request) {
        Page<Like> page = new Page<>(request.getCurrentPage(), request.getPageSize());
        
        String targetType = request.getLikeType() != null ? request.getLikeType().getCode() : null;
        Integer actionType = 1; // 只查询点赞记录
        
        return likeMapper.selectUserLikeHistory(page,
                                                request.getUserId(),
                                                targetType,
                                                actionType,
                                                request.getStartTime(),
                                                request.getEndTime());
    }
    
    /**
     * 批量查询用户点赞状态
     */
    public List<Like> getBatchUserLikeStatus(Long userId, List<Long> targetIds, LikeType likeType) {
        return likeMapper.selectBatchUserLikeStatus(userId, targetIds, likeType.getCode());
    }
    
    /**
     * 获取热门点赞内容（带缓存）
     */
    @Cached(name = CacheConstant.LIKE_HOT_CONTENT_CACHE,
            key = "#targetType + ':' + #limit",
            expire = CacheConstant.LIKE_STATISTICS_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public List<java.util.Map<String, Object>> getPopularTargets(String targetType, Integer limit) {
        return likeMapper.selectPopularTargets(targetType, null, null, limit);
    }
    
    /**
     * 获取活跃点赞用户（带缓存）
     */
    @Cached(name = CacheConstant.LIKE_ACTIVE_USERS_CACHE,
            key = "#targetType + ':' + #limit",
            expire = CacheConstant.LIKE_STATISTICS_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public List<java.util.Map<String, Object>> getActiveUsers(String targetType, Integer limit) {
        return likeMapper.selectActiveUsers(targetType, null, null, 1, limit);
    }
    
    // ========== 私有方法 ==========
    
    /**
     * 创建新的点赞记录
     */
    private Like createNewLikeRecord(LikeRequest request) {
        Like like = new Like();
        like.setUserId(request.getUserId());
        like.setTargetId(request.getTargetId());
        like.setTargetType(request.getLikeType().getCode());
        like.setActionType(request.getAction().getValue());
        like.setIpAddress(request.getIpAddress());
        like.setDeviceInfo(request.getDeviceInfo());
        like.setCreatedTime(LocalDateTime.now());
        like.setUpdatedTime(LocalDateTime.now());
        like.setStatus(1);
        like.setDeleted(0);
        return like;
    }
    
    /**
     * 更新现有点赞记录
     */
    private Like updateExistingLikeRecord(Like existingLike, Integer actionType, LikeRequest request) {
        existingLike.setActionType(actionType);
        existingLike.setIpAddress(request.getIpAddress());
        existingLike.setDeviceInfo(request.getDeviceInfo());
        existingLike.setUpdatedTime(LocalDateTime.now());
        return existingLike;
    }
    
    /**
     * 更新目标对象的统计字段
     */
    private void updateTargetStatistics(Long targetId, String targetType, Like oldLike, Integer newActionType) {
        // 计算统计变化量
        int likeDelta = 0;
        int dislikeDelta = 0;
        
        // 计算变化量
        if (oldLike == null) {
            // 新增操作
            if (newActionType == 1) likeDelta = 1;
            else if (newActionType == -1) dislikeDelta = 1;
        } else {
            // 更新操作
            Integer oldActionType = oldLike.getActionType();
            
            // 减去旧值
            if (oldActionType == 1) likeDelta -= 1;
            else if (oldActionType == -1) dislikeDelta -= 1;
            
            // 加上新值
            if (newActionType == 1) likeDelta += 1;
            else if (newActionType == -1) dislikeDelta += 1;
        }
        
        // 根据targetType更新不同表的统计字段
        switch (targetType) {
            case "CONTENT" -> updateContentStatistics(targetId, likeDelta, dislikeDelta);
            case "COMMENT" -> updateCommentStatistics(targetId, likeDelta, dislikeDelta);
            default -> log.warn("未知的目标类型: {}", targetType);
        }
    }
    
    /**
     * 更新内容统计
     */
    private void updateContentStatistics(Long contentId, int likeDelta, int dislikeDelta) {
        if (likeDelta != 0) {
            likeMapper.updateContentLikeCount(contentId, likeDelta);
            log.debug("更新内容{}的点赞统计，变化量：{}", contentId, likeDelta);
        }
        
        if (dislikeDelta != 0) {
            likeMapper.updateContentDislikeCount(contentId, dislikeDelta);
            log.debug("更新内容{}的点踩统计，变化量：{}", contentId, dislikeDelta);
        }
    }
    
    /**
     * 更新评论统计
     */
    private void updateCommentStatistics(Long commentId, int likeDelta, int dislikeDelta) {
        if (likeDelta != 0) {
            likeMapper.updateCommentLikeCount(commentId, likeDelta);
            log.debug("更新评论{}的点赞统计，变化量：{}", commentId, likeDelta);
        }
        
        if (dislikeDelta != 0) {
            likeMapper.updateCommentDislikeCount(commentId, dislikeDelta);
            log.debug("更新评论{}的点踩统计，变化量：{}", commentId, dislikeDelta);
        }
    }
    
    /**
     * 简单的分布式锁实现（应该使用 collide-lock 组件）
     */
    private <T> T executeWithLock(String lockKey, java.util.function.Supplier<T> action) {
        // 这里应该使用 collide-lock 组件实现分布式锁
        // 目前先用简单的同步实现
        synchronized (lockKey.intern()) {
            return action.get();
        }
    }
    
    /**
     * 简化的统计数据类
     */
    public static class LikeStatistics {
        private Long targetId;
        private String targetType;
        private Long totalLikeCount;
        private Long totalDislikeCount;
        
        // getters and setters
        public Long getTargetId() { return targetId; }
        public void setTargetId(Long targetId) { this.targetId = targetId; }
        
        public String getTargetType() { return targetType; }
        public void setTargetType(String targetType) { this.targetType = targetType; }
        
        public Long getTotalLikeCount() { return totalLikeCount; }
        public void setTotalLikeCount(Long totalLikeCount) { this.totalLikeCount = totalLikeCount; }
        
        public Long getTotalDislikeCount() { return totalDislikeCount; }
        public void setTotalDislikeCount(Long totalDislikeCount) { this.totalDislikeCount = totalDislikeCount; }
    }
} 