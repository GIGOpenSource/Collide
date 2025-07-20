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
import com.gig.collide.follow.infrastructure.exception.FollowException;
import com.gig.collide.follow.infrastructure.mapper.FollowMapper;
import com.gig.collide.follow.infrastructure.mapper.FollowStatisticsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.gig.collide.follow.infrastructure.exception.FollowErrorCode.*;

/**
 * 关注业务服务
 * @author GIG
 */
@Slf4j
@Service
public class FollowService {

    @Autowired
    private FollowMapper followMapper;

    @Autowired
    private FollowStatisticsMapper followStatisticsMapper;

    /**
     * 关注用户
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean follow(Long followerUserId, Long followedUserId) {
        return follow(followerUserId, followedUserId, FollowTypeEnum.NORMAL);
    }

    /**
     * 关注用户（指定类型）
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
            
            // 更新统计信息
            updateFollowStatistics(followerUserId, followedUserId, true);
            
            log.info("关注成功：follower={}, followed={}", followerUserId, followedUserId);
            return true;
        } catch (Exception e) {
            log.error("关注失败：follower={}, followed={}, error={}", followerUserId, followedUserId, e.getMessage(), e);
            throw new FollowException(SYSTEM_ERROR, e.getMessage());
        }
    }

    /**
     * 取消关注用户
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
            
            // 更新统计信息
            updateFollowStatistics(followerUserId, followedUserId, false);
            
            log.info("取消关注成功：follower={}, followed={}", followerUserId, followedUserId);
            return true;
        } catch (Exception e) {
            log.error("取消关注失败：follower={}, followed={}, error={}", followerUserId, followedUserId, e.getMessage(), e);
            throw new FollowException(SYSTEM_ERROR, e.getMessage());
        }
    }

    /**
     * 检查是否已关注
     */
    public boolean isFollowed(Long followerUserId, Long followedUserId) {
        Follow follow = followMapper.selectByFollowerAndFollowed(followerUserId, followedUserId);
        return follow != null && follow.getStatus() == 1;
    }

    /**
     * 获取关注列表
     */
    public PageResponse<FollowInfo> getFollowList(Long userId, Integer currentPage, Integer pageSize) {
        return getFollowList(userId, currentPage, pageSize, null);
    }

    /**
     * 获取关注列表（指定类型）
     */
    public PageResponse<FollowInfo> getFollowList(Long userId, Integer currentPage, Integer pageSize, FollowTypeEnum followType) {
        Page<Follow> page = new Page<>(currentPage, pageSize);
        IPage<Follow> followPage = followMapper.selectFollowList(page, userId, followType);
        
        List<FollowInfo> followInfoList = followPage.getRecords().stream()
                .map(FollowConvertor::toFollowInfo)
                .collect(Collectors.toList());
        
        // TODO: 调用用户服务获取用户详细信息
        
        return PageResponse.of(
            followInfoList, 
            (int) followPage.getTotal(), 
            (int) followPage.getSize(), 
            (int) followPage.getCurrent()
        );
    }

    /**
     * 获取粉丝列表
     */
    public PageResponse<FollowerInfo> getFollowerList(Long userId, Integer currentPage, Integer pageSize) {
        Page<Follow> page = new Page<>(currentPage, pageSize);
        IPage<Follow> followerPage = followMapper.selectFollowerList(page, userId);
        
        List<FollowerInfo> followerInfoList = followerPage.getRecords().stream()
                .map(FollowConvertor::toFollowerInfo)
                .collect(Collectors.toList());
        
        // TODO: 调用用户服务获取用户详细信息
        
        return PageResponse.of(
            followerInfoList, 
            (int) followerPage.getTotal(), 
            (int) followerPage.getSize(), 
            (int) followerPage.getCurrent()
        );
    }

    /**
     * 获取关注数量
     */
    public int getFollowingCount(Long userId) {
        FollowStatistics statistics = followStatisticsMapper.selectById(userId);
        return statistics != null ? statistics.getFollowingCount() : 0;
    }

    /**
     * 获取粉丝数量
     */
    public int getFollowerCount(Long userId) {
        FollowStatistics statistics = followStatisticsMapper.selectById(userId);
        return statistics != null ? statistics.getFollowerCount() : 0;
    }

    /**
     * 更新关注统计
     */
    private void updateFollowStatistics(Long followerUserId, Long followedUserId, boolean isFollow) {
        try {
            // 初始化统计记录（如果不存在）
            initStatisticsIfNotExists(followerUserId);
            initStatisticsIfNotExists(followedUserId);
            
            if (isFollow) {
                // 关注：增加关注者的关注数，增加被关注者的粉丝数
                followStatisticsMapper.incrementFollowingCount(followerUserId);
                followStatisticsMapper.incrementFollowerCount(followedUserId);
            } else {
                // 取消关注：减少关注者的关注数，减少被关注者的粉丝数
                followStatisticsMapper.decrementFollowingCount(followerUserId);
                followStatisticsMapper.decrementFollowerCount(followedUserId);
            }
        } catch (Exception e) {
            log.error("更新关注统计失败：follower={}, followed={}, isFollow={}, error={}", 
                    followerUserId, followedUserId, isFollow, e.getMessage(), e);
        }
    }

    /**
     * 初始化用户统计记录
     */
    private void initStatisticsIfNotExists(Long userId) {
        FollowStatistics statistics = followStatisticsMapper.selectById(userId);
        if (statistics == null) {
            followStatisticsMapper.initUserStatistics(userId);
        }
    }
} 