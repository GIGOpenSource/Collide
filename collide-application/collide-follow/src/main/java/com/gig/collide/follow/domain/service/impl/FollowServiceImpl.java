package com.gig.collide.follow.domain.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.follow.domain.entity.Follow;
import com.gig.collide.follow.domain.service.FollowService;
import com.gig.collide.follow.infrastructure.mapper.FollowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 关注业务逻辑实现类 - 简洁版
 * 基于follow-simple.sql的业务逻辑，实现核心关注功能
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowMapper followMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Follow followUser(Follow follow) {
        log.info("用户关注: followerId={}, followeeId={}", follow.getFollowerId(), follow.getFolloweeId());

        // 验证请求参数
        String validationResult = validateFollowRequest(follow);
        if (validationResult != null) {
            throw new IllegalArgumentException(validationResult);
        }

        // 检查是否可以关注
        String checkResult = checkCanFollow(follow.getFollowerId(), follow.getFolloweeId());
        if (checkResult != null) {
            throw new IllegalStateException(checkResult);
        }

        // 检查是否已存在关注关系（包括已取消的）
        Follow existingFollow = followMapper.findByFollowerAndFollowee(
                follow.getFollowerId(), follow.getFolloweeId(), null);

        if (existingFollow != null) {
            if (existingFollow.isActive()) {
                throw new IllegalStateException("已经关注过该用户");
            } else {
                // 重新激活已取消的关注关系
                existingFollow.activate();
                existingFollow.setUpdateTime(LocalDateTime.now());
                
                // 更新冗余信息
                if (StringUtils.hasText(follow.getFollowerNickname())) {
                    existingFollow.setFollowerNickname(follow.getFollowerNickname());
                }
                if (StringUtils.hasText(follow.getFollowerAvatar())) {
                    existingFollow.setFollowerAvatar(follow.getFollowerAvatar());
                }
                if (StringUtils.hasText(follow.getFolloweeNickname())) {
                    existingFollow.setFolloweeNickname(follow.getFolloweeNickname());
                }
                if (StringUtils.hasText(follow.getFolloweeAvatar())) {
                    existingFollow.setFolloweeAvatar(follow.getFolloweeAvatar());
                }
                
                int result = followMapper.updateById(existingFollow);
                if (result > 0) {
                    log.info("重新激活关注关系成功: followerId={}, followeeId={}", 
                            follow.getFollowerId(), follow.getFolloweeId());
                    return existingFollow;
                } else {
                    throw new RuntimeException("重新激活关注关系失败");
                }
            }
        }

        // 创建新的关注关系
        follow.setStatus("active");
        follow.setCreateTime(LocalDateTime.now());
        follow.setUpdateTime(LocalDateTime.now());

        int result = followMapper.insert(follow);
        if (result > 0) {
            log.info("关注用户成功: followerId={}, followeeId={}", 
                    follow.getFollowerId(), follow.getFolloweeId());
            return follow;
        } else {
            throw new RuntimeException("关注用户失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unfollowUser(Long followerId, Long followeeId, String cancelReason, Long operatorId) {
        log.info("取消关注: followerId={}, followeeId={}", followerId, followeeId);

        if (followerId == null || followeeId == null) {
            throw new IllegalArgumentException("关注者ID和被关注者ID不能为空");
        }

        // 检查关注关系是否存在
        Follow follow = followMapper.findByFollowerAndFollowee(followerId, followeeId, "active");
        if (follow == null) {
            log.warn("关注关系不存在或已取消: followerId={}, followeeId={}", followerId, followeeId);
            return false;
        }

        // 更新状态为cancelled
        int result = followMapper.updateFollowStatus(followerId, followeeId, "cancelled");
        boolean success = result > 0;
        
        if (success) {
            log.info("取消关注成功: followerId={}, followeeId={}", followerId, followeeId);
        } else {
            log.error("取消关注失败: followerId={}, followeeId={}", followerId, followeeId);
        }
        
        return success;
    }

    @Override
    public boolean checkFollowStatus(Long followerId, Long followeeId) {
        if (followerId == null || followeeId == null) {
            return false;
        }
        return followMapper.checkFollowExists(followerId, followeeId, "active");
    }

    @Override
    public Follow getFollowRelation(Long followerId, Long followeeId) {
        if (followerId == null || followeeId == null) {
            return null;
        }
        return followMapper.findByFollowerAndFollowee(followerId, followeeId, "active");
    }

    @Override
    public IPage<Follow> queryFollows(Integer currentPage, Integer pageSize, Long followerId, Long followeeId,
                                     String followerNickname, String followeeNickname, String status,
                                     String queryType, String orderBy, String orderDirection) {
        log.info("分页查询关注记录: currentPage={}, pageSize={}, followerId={}, followeeId={}", 
                currentPage, pageSize, followerId, followeeId);

        // 参数验证
        currentPage = currentPage == null ? 1 : currentPage;
        pageSize = pageSize == null ? 10 : pageSize;

        // 构建分页对象
        Page<Follow> page = new Page<>(currentPage, pageSize);

        // 调用复合条件查询
        IPage<Follow> result = followMapper.findWithConditions(page, followerId, followeeId, followerNickname,
                followeeNickname, status, queryType, orderBy, orderDirection);
        
        log.info("分页查询关注记录成功: 总数={}", result.getTotal());
        return result;
    }

    @Override
    public IPage<Follow> getFollowing(Long followerId, Integer currentPage, Integer pageSize) {
        log.info("获取关注列表: followerId={}, currentPage={}, pageSize={}", followerId, currentPage, pageSize);
        
        if (followerId == null) {
            log.warn("关注者ID不能为空");
            return new Page<>();
        }

        currentPage = currentPage == null ? 1 : currentPage;
        pageSize = pageSize == null ? 10 : pageSize;

        Page<Follow> page = new Page<>(currentPage, pageSize);
        IPage<Follow> result = followMapper.findFollowing(page, followerId, "active");
        
        log.info("获取关注列表成功: 用户={}, 总数={}", followerId, result.getTotal());
        return result;
    }

    @Override
    public IPage<Follow> getFollowers(Long followeeId, Integer currentPage, Integer pageSize) {
        log.info("获取粉丝列表: followeeId={}, currentPage={}, pageSize={}", followeeId, currentPage, pageSize);
        
        if (followeeId == null) {
            log.warn("被关注者ID不能为空");
            return new Page<>();
        }

        currentPage = currentPage == null ? 1 : currentPage;
        pageSize = pageSize == null ? 10 : pageSize;

        Page<Follow> page = new Page<>(currentPage, pageSize);
        IPage<Follow> result = followMapper.findFollowers(page, followeeId, "active");
        
        log.info("获取粉丝列表成功: 用户={}, 总数={}", followeeId, result.getTotal());
        return result;
    }

    @Override
    public IPage<Follow> getMutualFollows(Long userId, Integer currentPage, Integer pageSize) {
        log.info("获取互关好友: userId={}, currentPage={}, pageSize={}", userId, currentPage, pageSize);
        
        if (userId == null) {
            log.warn("用户ID不能为空");
            return new Page<>();
        }

        currentPage = currentPage == null ? 1 : currentPage;
        pageSize = pageSize == null ? 10 : pageSize;

        Page<Follow> page = new Page<>(currentPage, pageSize);
        IPage<Follow> result = followMapper.findMutualFollows(page, userId, "active");
        
        log.info("获取互关好友成功: 用户={}, 总数={}", userId, result.getTotal());
        return result;
    }

    @Override
    public Long getFollowingCount(Long followerId) {
        return followMapper.countFollowing(followerId, "active");
    }

    @Override
    public Long getFollowersCount(Long followeeId) {
        return followMapper.countFollowers(followeeId, "active");
    }

    @Override
    public Map<String, Object> getFollowStatistics(Long userId) {
        if (userId == null) {
            return new HashMap<>();
        }
        return followMapper.getUserFollowStatistics(userId);
    }

    @Override
    public Map<Long, Boolean> batchCheckFollowStatus(Long followerId, List<Long> followeeIds) {
        if (followerId == null || followeeIds == null || followeeIds.isEmpty()) {
            return new HashMap<>();
        }

        List<Map<String, Object>> results = followMapper.batchCheckFollowStatus(followerId, followeeIds, "active");
        
        return results.stream().collect(Collectors.toMap(
                result -> Long.valueOf(result.get("followeeId").toString()),
                result -> Integer.valueOf(result.get("isFollowing").toString()) > 0
        ));
    }

    @Override
    public IPage<Follow> searchByNickname(Long followerId, Long followeeId, String nicknameKeyword, 
                                         Integer currentPage, Integer pageSize) {
        log.info("根据昵称搜索关注关系: followerId={}, followeeId={}, keyword={}, currentPage={}, pageSize={}", 
                followerId, followeeId, nicknameKeyword, currentPage, pageSize);

        if (nicknameKeyword == null || nicknameKeyword.trim().isEmpty()) {
            log.warn("搜索关键词不能为空");
            return new Page<>();
        }

        currentPage = currentPage == null ? 1 : currentPage;
        pageSize = pageSize == null ? 10 : pageSize;

        Page<Follow> page = new Page<>(currentPage, pageSize);
        IPage<Follow> result = followMapper.searchByNickname(page, followerId, followeeId, nicknameKeyword, "active");
        
        log.info("昵称搜索成功: 关键词={}, 总数={}", nicknameKeyword, result.getTotal());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateUserInfo(Long userId, String nickname, String avatar) {
        log.info("更新用户冗余信息: userId={}, nickname={}", userId, nickname);

        if (userId == null) {
            return 0;
        }

        // 同时更新作为关注者和被关注者的信息
        return followMapper.updateUserInfo(userId, nickname, avatar, true, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanCancelledFollows(Integer days) {
        log.info("清理已取消的关注记录: days={}", days);

        if (days == null || days <= 0) {
            days = 30; // 默认清理30天前的记录
        }

        int result = followMapper.cleanCancelledFollows(days);
        log.info("清理完成: 删除{}条已取消的关注记录", result);
        
        return result;
    }

    @Override
    public List<Follow> getRelationChain(Long userIdA, Long userIdB) {
        if (userIdA == null || userIdB == null) {
            return List.of();
        }
        return followMapper.findRelationChain(userIdA, userIdB);
    }

    @Override
    public String validateFollowRequest(Follow follow) {
        if (follow == null) {
            return "关注对象不能为空";
        }

        if (follow.getFollowerId() == null) {
            return "关注者ID不能为空";
        }

        if (follow.getFolloweeId() == null) {
            return "被关注者ID不能为空";
        }

        if (follow.isSelfFollow()) {
            return "不能关注自己";
        }

        return null; // 验证通过
    }

    @Override
    public String checkCanFollow(Long followerId, Long followeeId) {
        if (followerId == null || followeeId == null) {
            return "用户ID不能为空";
        }

        if (followerId.equals(followeeId)) {
            return "不能关注自己";
        }

        // 可以根据业务需求添加更多限制
        // 例如：检查用户是否存在、是否被封禁等

        return null; // 可以关注
    }

    @Override
    public boolean existsFollowRelation(Long followerId, Long followeeId) {
        return followMapper.checkFollowExists(followerId, followeeId, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reactivateFollow(Long followerId, Long followeeId) {
        log.info("重新激活关注关系: followerId={}, followeeId={}", followerId, followeeId);

        if (followerId == null || followeeId == null) {
            return false;
        }

        int result = followMapper.updateFollowStatus(followerId, followeeId, "active");
        boolean success = result > 0;
        
        if (success) {
            log.info("重新激活关注关系成功: followerId={}, followeeId={}", followerId, followeeId);
        } else {
            log.warn("重新激活关注关系失败: followerId={}, followeeId={}", followerId, followeeId);
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateFollowStatus(Long followerId, Long followeeId, String status) {
        log.info("更新关注状态: followerId={}, followeeId={}, status={}", followerId, followeeId, status);

        if (followerId == null || followeeId == null || status == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 验证状态值
        if (!"active".equals(status) && !"cancelled".equals(status)) {
            throw new IllegalArgumentException("无效的状态值: " + status);
        }

        int result = followMapper.updateFollowStatus(followerId, followeeId, status);
        boolean success = result > 0;
        
        if (success) {
            log.info("更新关注状态成功: followerId={}, followeeId={}, status={}", followerId, followeeId, status);
        } else {
            log.warn("更新关注状态失败: followerId={}, followeeId={}, status={}", followerId, followeeId, status);
        }
        
        return success;
    }
}