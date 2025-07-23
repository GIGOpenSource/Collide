package com.gig.collide.like.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.like.constant.LikeAction;
import com.gig.collide.api.like.constant.LikeType;
import com.gig.collide.api.like.request.LikeQueryRequest;
import com.gig.collide.api.like.request.LikeRequest;
import com.gig.collide.like.domain.entity.Like;
import com.gig.collide.like.infrastructure.mapper.LikeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 点赞领域服务
 * 无连表设计，使用冗余字段进行统计
 * 
 * @author Collide
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LikeDomainService {
    
    private final LikeMapper likeMapper;
    
    /**
     * 执行点赞操作
     */
    @Transactional(rollbackFor = Exception.class)
    public Like performLikeAction(LikeRequest request) {
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
            likeMapper.insert(likeRecord);
        } else {
            // 更新现有点赞记录
            likeRecord = updateExistingLikeRecord(existingLike, actionType, request);
            likeMapper.updateById(likeRecord);
        }
        
        // 异步更新目标对象的统计字段（通过消息队列或异步任务）
        updateTargetStatistics(targetId, targetType, existingLike, actionType);
        
        return likeRecord;
    }
    
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
     * 这里可以通过消息队列异步处理，或者直接更新
     */
    private void updateTargetStatistics(Long targetId, String targetType, Like oldLike, Integer newActionType) {
        // 根据targetType更新不同表的统计字段
        switch (targetType) {
            case "CONTENT" -> updateContentStatistics(targetId, oldLike, newActionType);
            case "COMMENT" -> updateCommentStatistics(targetId, oldLike, newActionType);
            default -> log.warn("未知的目标类型: {}", targetType);
        }
    }
    
    /**
     * 更新内容统计
     */
    private void updateContentStatistics(Long contentId, Like oldLike, Integer newActionType) {
        // 这里可以直接执行SQL更新，或者发送消息队列异步处理
        // 示例：UPDATE t_content SET like_count = like_count + 1 WHERE id = #{contentId}
        log.debug("更新内容{}的点赞统计，操作类型：{}", contentId, newActionType);
    }
    
    /**
     * 更新评论统计
     */
    private void updateCommentStatistics(Long commentId, Like oldLike, Integer newActionType) {
        // 示例：UPDATE t_comment SET like_count = like_count + 1 WHERE id = #{commentId}
        log.debug("更新评论{}的点赞统计，操作类型：{}", commentId, newActionType);
    }
    
    /**
     * 查询用户对对象的点赞状态
     */
    public Like getUserLikeStatus(Long userId, Long targetId, LikeType likeType) {
        return likeMapper.selectByUserAndTarget(userId, targetId, likeType.getCode());
    }
    
    /**
     * 获取对象的点赞统计（直接从点赞表统计）
     */
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
     * 分页查询点赞记录
     */
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
     * 获取用户点赞历史
     */
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