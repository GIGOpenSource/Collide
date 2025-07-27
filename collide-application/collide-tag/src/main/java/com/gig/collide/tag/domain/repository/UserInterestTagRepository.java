package com.gig.collide.tag.domain.repository;

import com.gig.collide.tag.infrastructure.entity.UserInterestTagEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户兴趣标签仓储接口
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
public interface UserInterestTagRepository {

    /**
     * 保存用户兴趣标签
     *
     * @param entity 用户兴趣标签实体
     * @return 用户兴趣标签实体
     */
    UserInterestTagEntity save(UserInterestTagEntity entity);

    /**
     * 根据用户ID获取兴趣标签列表
     *
     * @param userId 用户ID
     * @return 用户兴趣标签列表
     */
    List<UserInterestTagEntity> findByUserId(Long userId);

    /**
     * 根据标签ID获取感兴趣的用户列表
     *
     * @param tagId 标签ID
     * @param limit 限制数量
     * @return 用户兴趣标签列表
     */
    List<UserInterestTagEntity> findByTagId(Long tagId, Integer limit);

    /**
     * 查询用户对指定标签的兴趣记录
     *
     * @param userId 用户ID
     * @param tagId  标签ID
     * @return 用户兴趣标签实体
     */
    UserInterestTagEntity findByUserIdAndTagId(Long userId, Long tagId);

    /**
     * 批量保存用户兴趣标签
     *
     * @param entities 用户兴趣标签列表
     * @return 是否成功
     */
    boolean batchSave(List<UserInterestTagEntity> entities);

    /**
     * 删除用户兴趣标签
     *
     * @param userId 用户ID
     * @param tagId  标签ID
     * @return 是否成功
     */
    boolean deleteByUserIdAndTagId(Long userId, Long tagId);

    /**
     * 批量删除用户兴趣标签
     *
     * @param userId 用户ID
     * @param tagIds 标签ID列表
     * @return 是否成功
     */
    boolean batchDeleteByUserIdAndTagIds(Long userId, List<Long> tagIds);

    /**
     * 更新用户对标签的兴趣分数
     *
     * @param userId        用户ID
     * @param tagId         标签ID
     * @param interestScore 兴趣分数
     * @return 是否成功
     */
    boolean updateInterestScore(Long userId, Long tagId, BigDecimal interestScore);

    /**
     * 获取用户的高兴趣标签
     *
     * @param userId   用户ID
     * @param minScore 最小兴趣分数
     * @param limit    限制数量
     * @return 用户兴趣标签列表
     */
    List<UserInterestTagEntity> findHighInterestTags(Long userId, BigDecimal minScore, Integer limit);

    /**
     * 清空用户的所有兴趣标签
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteAllByUserId(Long userId);

    /**
     * 检查用户是否对标签感兴趣
     *
     * @param userId 用户ID
     * @param tagId  标签ID
     * @return 是否感兴趣
     */
    boolean existsByUserIdAndTagId(Long userId, Long tagId);
} 