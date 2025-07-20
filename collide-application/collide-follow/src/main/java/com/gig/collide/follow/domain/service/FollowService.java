package com.gig.collide.follow.domain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.follow.constant.FollowStatusEnum;
import com.gig.collide.api.follow.constant.FollowTypeEnum;
import com.gig.collide.api.follow.response.data.FollowInfo;
import com.gig.collide.api.follow.response.data.FollowerInfo;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.follow.domain.entity.Follow;
import com.gig.collide.follow.domain.entity.FollowStatistics;
import com.gig.collide.follow.domain.entity.convertor.FollowConvertor;
import com.gig.collide.follow.domain.event.FollowEvent;
import com.gig.collide.follow.infrastructure.exception.FollowException;
import com.gig.collide.follow.infrastructure.mapper.FollowMapper;
import com.gig.collide.follow.infrastructure.mapper.FollowStatisticsMapper;
import com.gig.collide.follow.infrastructure.mq.FollowCacheService;
import com.gig.collide.follow.infrastructure.mq.FollowEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.gig.collide.follow.infrastructure.exception.FollowErrorCode.*;

/**
 * 关注业务服务（优化版本）
 * @author GIG
 */
@Slf4j
@Service
public class FollowService {

    @Autowired
    private FollowMapper followMapper;

    @Autowired
    private FollowStatisticsMapper followStatisticsMapper;

    @Autowired
    private FollowEventProducer followEventProducer;

    @Autowired
    private FollowCacheService followCacheService;

    /**
     * 关注用户（优化版本）
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean follow(Long followerUserId, Long followedUserId) {
        return follow(followerUserId, followedUserId, FollowTypeEnum.NORMAL);
    }

    /**
     * 关注用户（指定类型，优化版本）
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean follow(Long followerUserId, Long followedUserId, FollowTypeEnum followType) {
        log.info("用户关注：follower={}, followed={}, type={}", followerUserId, followedUserId, followType);
        
        // 检查是否已经关注
        Follow existingFollow = followMapper.selectByFollowerAndFollowed(followerUserId, followedUserId);
        if (existingFollow != null && existingFollow.getStatus() == 1) {
            throw new FollowException(ALREADY_FOLLOWED);
        }
        
        try {
            if (existingFollow != null) {
                // 重新关注（恢复已取消的关注）
                existingFollow.setStatus(1);
                existingFollow.setFollowType(followType);
                existingFollow.setUpdatedTime(new Date());
                followMapper.updateById(existingFollow);
            } else {
                // 新建关注关系
                Follow follow = new Follow();
                follow.setFollowerUserId(followerUserId);
                follow.setFollowedUserId(followedUserId);
                follow.setFollowType(followType);
                follow.setStatus(1);
                follow.setCreatedTime(new Date());
                follow.setUpdatedTime(new Date());
                followMapper.insert(follow);
            }
            
            // 🚀 异步处理：发送关注事件到MQ
            FollowEvent event = followEventProducer.createFollowEvent(
                FollowEvent.FollowEventType.FOLLOW, followerUserId, followedUserId);
            event.setFollowType(followType);
            followEventProducer.sendFollowEvent(event);
            
            // 🚀 立即更新缓存（提高用户体验）
            followCacheService.updateFollowStatusCache(followerUserId, followedUserId, true);
            
            log.info("关注成功：follower={}, followed={}", followerUserId, followedUserId);
            return true;
        } catch (Exception e) {
            log.error("关注失败：follower={}, followed={}, error={}", followerUserId, followedUserId, e.getMessage(), e);
            throw new FollowException(SYSTEM_ERROR, e.getMessage());
        }
    }

    /**
     * 取消关注用户（优化版本）
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean unfollow(Long followerUserId, Long followedUserId) {
        log.info("取消关注：follower={}, followed={}", followerUserId, followedUserId);
        
        Follow follow = followMapper.selectByFollowerAndFollowed(followerUserId, followedUserId);
        if (follow == null || follow.getStatus() == 0) {
            throw new FollowException(NOT_FOLLOWED);
        }
        
        try {
            // 软删除关注关系
            follow.setStatus(0);
            follow.setUpdatedTime(new Date());
            followMapper.updateById(follow);
            
            // 🚀 异步处理：发送取消关注事件到MQ
            FollowEvent event = followEventProducer.createFollowEvent(
                FollowEvent.FollowEventType.UNFOLLOW, followerUserId, followedUserId);
            followEventProducer.sendFollowEvent(event);
            
            // 🚀 立即更新缓存
            followCacheService.updateFollowStatusCache(followerUserId, followedUserId, false);
            
            log.info("取消关注成功：follower={}, followed={}", followerUserId, followedUserId);
            return true;
        } catch (Exception e) {
            log.error("取消关注失败：follower={}, followed={}, error={}", followerUserId, followedUserId, e.getMessage(), e);
            throw new FollowException(SYSTEM_ERROR, e.getMessage());
        }
    }

    /**
     * 检查是否已关注（优化版本）
     */
    public boolean isFollowed(Long followerUserId, Long followedUserId) {
        // 🚀 优先从缓存获取
        Boolean cachedStatus = followCacheService.getFollowStatus(followerUserId, followedUserId);
        if (cachedStatus != null) {
            log.debug("从缓存获取关注状态: follower={}, followed={}, status={}", 
                    followerUserId, followedUserId, cachedStatus);
            return cachedStatus;
        }
        
        // 缓存未命中，查询数据库
        Follow follow = followMapper.selectByFollowerAndFollowed(followerUserId, followedUserId);
        boolean isFollowed = follow != null && follow.getStatus() == 1;
        
        // 🚀 更新缓存
        followCacheService.cacheFollowStatus(followerUserId, followedUserId, isFollowed);
        
        return isFollowed;
    }

    /**
     * 获取关注列表（优化版本）
     */
    public PageResponse<FollowInfo> getFollowList(Long userId, Integer currentPage, Integer pageSize) {
        return getFollowList(userId, currentPage, pageSize, null);
    }

    /**
     * 获取关注列表（指定类型，优化版本）
     */
    @SuppressWarnings("unchecked")
    public PageResponse<FollowInfo> getFollowList(Long userId, Integer currentPage, Integer pageSize, FollowTypeEnum followType) {
        // 🚀 优先从缓存获取
        Object cachedResult = followCacheService.getFollowList(userId, currentPage, pageSize);
        if (cachedResult != null) {
            log.debug("从缓存获取关注列表: userId={}, page={}, size={}", userId, currentPage, pageSize);
            return (PageResponse<FollowInfo>) cachedResult;
        }
        
        // 缓存未命中，查询数据库
        Page<Follow> page = new Page<>(currentPage, pageSize);
        IPage<Follow> followPage = followMapper.selectFollowList(page, userId, followType);
        
        List<FollowInfo> followInfoList = followPage.getRecords().stream()
                .map(FollowConvertor::toFollowInfo)
                .collect(Collectors.toList());
        
        // TODO: 批量调用用户服务获取用户详细信息（可以考虑异步或使用本地缓存）
        
        PageResponse<FollowInfo> result = PageResponse.of(
            followInfoList, 
            (int) followPage.getTotal(), 
            (int) followPage.getSize(), 
            (int) followPage.getCurrent()
        );
        
        // 🚀 更新缓存
        followCacheService.cacheFollowList(userId, currentPage, pageSize, result);
        
        return result;
    }

    /**
     * 获取粉丝列表（优化版本）
     */
    @SuppressWarnings("unchecked")
    public PageResponse<FollowerInfo> getFollowerList(Long userId, Integer currentPage, Integer pageSize) {
        // 🚀 优先从缓存获取
        Object cachedResult = followCacheService.getFollowerList(userId, currentPage, pageSize);
        if (cachedResult != null) {
            log.debug("从缓存获取粉丝列表: userId={}, page={}, size={}", userId, currentPage, pageSize);
            return (PageResponse<FollowerInfo>) cachedResult;
        }
        
        // 缓存未命中，查询数据库
        Page<Follow> page = new Page<>(currentPage, pageSize);
        IPage<Follow> followerPage = followMapper.selectFollowerList(page, userId);
        
        List<FollowerInfo> followerInfoList = followerPage.getRecords().stream()
                .map(FollowConvertor::toFollowerInfo)
                .collect(Collectors.toList());
        
        // TODO: 批量调用用户服务获取用户详细信息
        
        PageResponse<FollowerInfo> result = PageResponse.of(
            followerInfoList, 
            (int) followerPage.getTotal(), 
            (int) followerPage.getSize(), 
            (int) followerPage.getCurrent()
        );
        
        // 🚀 更新缓存
        followCacheService.cacheFollowerList(userId, currentPage, pageSize, result);
        
        return result;
    }

    /**
     * 获取关注数量（优化版本）
     */
    public int getFollowingCount(Long userId) {
        // 🚀 优先从缓存获取
        Integer cachedCount = followCacheService.getFollowingCount(userId);
        if (cachedCount != null) {
            log.debug("从缓存获取关注数: userId={}, count={}", userId, cachedCount);
            return cachedCount;
        }
        
        // 缓存未命中，查询数据库
        FollowStatistics statistics = followStatisticsMapper.selectById(userId);
        int count = statistics != null ? statistics.getFollowingCount() : 0;
        
        // 🚀 更新缓存
        followCacheService.cacheFollowingCount(userId, count);
        
        return count;
    }

    /**
     * 获取粉丝数量（优化版本）
     */
    public int getFollowerCount(Long userId) {
        // 🚀 优先从缓存获取
        Integer cachedCount = followCacheService.getFollowerCount(userId);
        if (cachedCount != null) {
            log.debug("从缓存获取粉丝数: userId={}, count={}", userId, cachedCount);
            return cachedCount;
        }
        
        // 缓存未命中，查询数据库
        FollowStatistics statistics = followStatisticsMapper.selectById(userId);
        int count = statistics != null ? statistics.getFollowerCount() : 0;
        
        // 🚀 更新缓存
        followCacheService.cacheFollowerCount(userId, count);
        
        return count;
    }

    /**
     * 批量检查关注状态（新增方法）
     */
    public java.util.Map<Long, Boolean> batchCheckFollowStatus(Long followerUserId, List<Long> followedUserIds) {
        return followedUserIds.stream()
                .collect(java.util.stream.Collectors.toMap(
                    followedUserId -> followedUserId,
                    followedUserId -> isFollowed(followerUserId, followedUserId)
                ));
    }

    /**
     * 预热缓存（新增方法）
     */
    public void warmUpCache(Long userId) {
        try {
            // 预热统计数据
            int followingCount = getFollowingCount(userId);
            int followerCount = getFollowerCount(userId);
            
            log.info("缓存预热完成: userId={}, following={}, follower={}", 
                    userId, followingCount, followerCount);
        } catch (Exception e) {
            log.error("缓存预热失败: userId={}, error={}", userId, e.getMessage(), e);
        }
    }
} 