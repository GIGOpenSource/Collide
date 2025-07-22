package com.gig.collide.users.domain.repository;

import com.gig.collide.users.domain.entity.UserProfile;

import java.util.Optional;

/**
 * 用户扩展信息仓储接口
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface UserProfileRepository {

    /**
     * 保存用户扩展信息
     *
     * @param userProfile 用户扩展信息实体
     * @return 保存后的实体
     */
    UserProfile save(UserProfile userProfile);

    /**
     * 根据用户ID查询用户扩展信息
     *
     * @param userId 用户ID
     * @return 用户扩展信息
     */
    Optional<UserProfile> findByUserId(Long userId);

    /**
     * 根据用户ID删除用户扩展信息
     *
     * @param userId 用户ID
     */
    void deleteByUserId(Long userId);

    /**
     * 增加用户粉丝数
     *
     * @param userId 用户ID
     * @param count  增加数量
     */
    void increaseFollowerCount(Long userId, Long count);

    /**
     * 减少用户粉丝数
     *
     * @param userId 用户ID
     * @param count  减少数量
     */
    void decreaseFollowerCount(Long userId, Long count);

    /**
     * 增加用户关注数
     *
     * @param userId 用户ID
     * @param count  增加数量
     */
    void increaseFollowingCount(Long userId, Long count);

    /**
     * 减少用户关注数
     *
     * @param userId 用户ID
     * @param count  减少数量
     */
    void decreaseFollowingCount(Long userId, Long count);

    /**
     * 增加用户内容数
     *
     * @param userId 用户ID
     * @param count  增加数量
     */
    void increaseContentCount(Long userId, Long count);

    /**
     * 减少用户内容数
     *
     * @param userId 用户ID
     * @param count  减少数量
     */
    void decreaseContentCount(Long userId, Long count);

    /**
     * 增加用户获得点赞数
     *
     * @param userId 用户ID
     * @param count  增加数量
     */
    void increaseLikeCount(Long userId, Long count);

    /**
     * 减少用户获得点赞数
     *
     * @param userId 用户ID
     * @param count  减少数量
     */
    void decreaseLikeCount(Long userId, Long count);
} 