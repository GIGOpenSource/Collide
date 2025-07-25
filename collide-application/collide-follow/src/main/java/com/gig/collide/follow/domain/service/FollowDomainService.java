package com.gig.collide.follow.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.follow.constant.FollowStatus;
import com.gig.collide.api.follow.constant.FollowType;
import com.gig.collide.base.exception.BizException;
import com.gig.collide.base.exception.CommonErrorCode;
import com.gig.collide.follow.domain.entity.Follow;
import com.gig.collide.follow.domain.entity.FollowStatistics;
import com.gig.collide.follow.infrastructure.mapper.FollowMapper;
import com.gig.collide.follow.infrastructure.mapper.FollowStatisticsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 关注业务服务
 * 处理关注相关的核心业务逻辑
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FollowDomainService {

    private final FollowMapper followMapper;
    private final FollowStatisticsMapper followStatisticsMapper;

    /**
     * 关注用户
     *
     * @param followerUserId 关注者用户ID
     * @param followedUserId 被关注者用户ID
     * @param followType 关注类型
     * @return 关注记录
     */
    @Transactional(rollbackFor = Exception.class)
    public Follow followUser(Long followerUserId, Long followedUserId, FollowType followType) {
        // 验证参数
        validateFollowParams(followerUserId, followedUserId);

        // 检查是否已经关注
        Follow existingFollow = followMapper.selectFollowRelation(
            followerUserId, followedUserId, FollowStatus.NORMAL);
        
        if (existingFollow != null) {
            log.warn("用户已关注，无需重复操作。关注者: {}, 被关注者: {}", followerUserId, followedUserId);
            return existingFollow;
        }

        // 检查是否存在已取消的关注记录
        Follow cancelledFollow = followMapper.selectFollowRelation(
            followerUserId, followedUserId, FollowStatus.CANCELLED);

        Follow follow;
        boolean isNewFollow = false;

        if (cancelledFollow != null) {
            // 重新激活关注关系
            cancelledFollow.setStatus(FollowStatus.NORMAL);
            cancelledFollow.setFollowType(followType);
            cancelledFollow.setUpdatedTime(LocalDateTime.now());
            followMapper.updateById(cancelledFollow);
            follow = cancelledFollow;
            log.info("重新激活关注关系。关注者: {}, 被关注者: {}", followerUserId, followedUserId);
        } else {
            // 创建新的关注记录
            follow = new Follow();
            follow.setFollowerUserId(followerUserId);
            follow.setFollowedUserId(followedUserId);
            follow.setFollowType(followType);
            follow.setStatus(FollowStatus.NORMAL);
            follow.setCreatedTime(LocalDateTime.now());
            follow.setUpdatedTime(LocalDateTime.now());
            
            followMapper.insert(follow);
            isNewFollow = true;
            log.info("创建新关注关系。关注者: {}, 被关注者: {}", followerUserId, followedUserId);
        }

        // 更新统计数据
        updateFollowStatistics(followerUserId, followedUserId, true);

        return follow;
    }

    /**
     * 取消关注用户
     *
     * @param followerUserId 关注者用户ID
     * @param followedUserId 被关注者用户ID
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean unfollowUser(Long followerUserId, Long followedUserId) {
        // 验证参数
        validateFollowParams(followerUserId, followedUserId);

        // 查询关注关系
        Follow follow = followMapper.selectFollowRelation(
            followerUserId, followedUserId, FollowStatus.NORMAL);
        
        if (follow == null) {
            log.warn("关注关系不存在。关注者: {}, 被关注者: {}", followerUserId, followedUserId);
            return false;
        }

        // 更新为取消状态
        follow.setStatus(FollowStatus.CANCELLED);
        follow.setUpdatedTime(LocalDateTime.now());
        followMapper.updateById(follow);

        // 更新统计数据
        updateFollowStatistics(followerUserId, followedUserId, false);

        log.info("取消关注成功。关注者: {}, 被关注者: {}", followerUserId, followedUserId);
        return true;
    }

    /**
     * 检查关注关系
     *
     * @param followerUserId 关注者用户ID
     * @param followedUserId 被关注者用户ID
     * @return 是否关注
     */
    public boolean isFollowing(Long followerUserId, Long followedUserId) {
        Follow follow = followMapper.selectFollowRelation(
            followerUserId, followedUserId, FollowStatus.NORMAL);
        return follow != null;
    }

    /**
     * 检查相互关注关系
     *
     * @param userId1 用户1
     * @param userId2 用户2
     * @return 是否相互关注
     */
    public boolean isMutualFollowing(Long userId1, Long userId2) {
        return isFollowing(userId1, userId2) && isFollowing(userId2, userId1);
    }

    /**
     * 分页查询关注列表
     *
     * @param followerUserId 关注者用户ID
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return 关注列表
     */
    public IPage<Follow> getFollowingList(Long followerUserId, int pageNo, int pageSize) {
        Page<Follow> page = new Page<>(pageNo, pageSize);
        return followMapper.selectFollowingPage(page, followerUserId, FollowStatus.NORMAL);
    }

    /**
     * 分页查询粉丝列表
     *
     * @param followedUserId 被关注者用户ID
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return 粉丝列表
     */
    public IPage<Follow> getFollowersList(Long followedUserId, int pageNo, int pageSize) {
        Page<Follow> page = new Page<>(pageNo, pageSize);
        return followMapper.selectFollowersPage(page, followedUserId, FollowStatus.NORMAL);
    }

    /**
     * 获取相互关注列表
     *
     * @param userId 用户ID
     * @return 相互关注列表
     */
    public List<Follow> getMutualFollows(Long userId) {
        return followMapper.selectMutualFollows(userId, FollowStatus.NORMAL);
    }

    /**
     * 批量查询关注关系
     *
     * @param followerUserId 关注者用户ID
     * @param followedUserIds 被关注者用户ID列表
     * @return 关注关系列表
     */
    public List<Follow> getFollowRelations(Long followerUserId, List<Long> followedUserIds) {
        if (followedUserIds == null || followedUserIds.isEmpty()) {
            return List.of();
        }
        return followMapper.selectFollowRelations(followerUserId, followedUserIds, FollowStatus.NORMAL);
    }

    /**
     * 获取关注统计
     *
     * @param userId 用户ID
     * @return 关注统计
     */
    public FollowStatistics getFollowStatistics(Long userId) {
        FollowStatistics statistics = followStatisticsMapper.selectById(userId);
        if (statistics == null) {
            // 如果不存在统计记录，则创建一个
            statistics = new FollowStatistics();
            statistics.setUserId(userId);
            statistics.setFollowingCount(0);
            statistics.setFollowerCount(0);
            statistics.setCreatedTime(LocalDateTime.now());
            statistics.setUpdatedTime(LocalDateTime.now());
            followStatisticsMapper.insert(statistics);
        }
        return statistics;
    }

    /**
     * 批量获取关注统计
     *
     * @param userIds 用户ID列表
     * @return 关注统计列表
     */
    public List<FollowStatistics> getFollowStatistics(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }
        return followStatisticsMapper.selectByUserIds(userIds);
    }

    /**
     * 重新计算关注统计
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean recalculateStatistics(Long userId) {
        try {
            followStatisticsMapper.recalculateStatistics(userId);
            log.info("重新计算关注统计成功。用户: {}", userId);
            return true;
        } catch (Exception e) {
            log.error("重新计算关注统计失败。用户: {}", userId, e);
            return false;
        }
    }

    /**
     * 验证关注参数
     */
    private void validateFollowParams(Long followerUserId, Long followedUserId) {
        if (followerUserId == null) {
            throw new BizException("关注者用户ID不能为空", CommonErrorCode.PARAM_INVALID);
        }
        if (followedUserId == null) {
            throw new BizException("被关注者用户ID不能为空", CommonErrorCode.PARAM_INVALID);
        }
        if (followerUserId.equals(followedUserId)) {
            throw new BizException("不能关注自己", CommonErrorCode.PARAM_INVALID);
        }
    }

    /**
     * 更新关注统计数据
     *
     * @param followerUserId 关注者用户ID
     * @param followedUserId 被关注者用户ID
     * @param isFollow 是否为关注操作
     */
    private void updateFollowStatistics(Long followerUserId, Long followedUserId, boolean isFollow) {
        try {
            if (isFollow) {
                // 关注操作：增加关注者的关注数，增加被关注者的粉丝数
                followStatisticsMapper.incrementFollowingCount(followerUserId);
                followStatisticsMapper.incrementFollowerCount(followedUserId);
            } else {
                // 取消关注操作：减少关注者的关注数，减少被关注者的粉丝数
                followStatisticsMapper.decrementFollowingCount(followerUserId);
                followStatisticsMapper.decrementFollowerCount(followedUserId);
            }
        } catch (Exception e) {
            log.error("更新关注统计失败。关注者: {}, 被关注者: {}, 操作: {}", 
                followerUserId, followedUserId, isFollow ? "关注" : "取消关注", e);
        }
    }
} 