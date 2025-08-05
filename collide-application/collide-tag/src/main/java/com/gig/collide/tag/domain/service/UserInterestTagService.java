package com.gig.collide.tag.domain.service;

import com.gig.collide.tag.domain.entity.UserInterestTag;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户兴趣标签服务接口 - 严格对应UserInterestTagMapper
 * 基于UserInterestTagMapper的所有方法，提供用户兴趣标签相关的业务逻辑
 *
 * @author GIG Team
 * @version 3.0.0
 */
public interface UserInterestTagService {

    // =================== 基础CRUD操作（继承自BaseMapper） ===================

    /**
     * 创建用户兴趣标签
     */
    UserInterestTag createUserInterestTag(UserInterestTag userInterestTag);

    /**
     * 更新用户兴趣标签
     */
    UserInterestTag updateUserInterestTag(UserInterestTag userInterestTag);

    /**
     * 根据ID删除用户兴趣标签
     */
    boolean deleteUserInterestTagById(Long id);

    /**
     * 根据ID查询用户兴趣标签
     */
    UserInterestTag getUserInterestTagById(Long id);

    /**
     * 查询所有用户兴趣标签
     */
    List<UserInterestTag> getAllUserInterestTags();

    // =================== 核心查询方法（对应Mapper自定义方法） ===================

    /**
     * 获取用户兴趣标签列表
     * 对应Mapper: selectByUserId
     */
    List<UserInterestTag> selectByUserId(Long userId);

    /**
     * 根据标签ID获取关注用户列表
     * 对应Mapper: selectByTagId
     */
    List<UserInterestTag> selectByTagId(Long tagId);

    /**
     * 获取用户高分兴趣标签（兴趣分数排序）
     * 对应Mapper: selectTopInterestsByUserId
     */
    List<UserInterestTag> selectTopInterestsByUserId(Long userId, Integer limit);

    // =================== 统计和计数方法 ===================

    /**
     * 检查用户是否已关注标签
     * 对应Mapper: countByUserIdAndTagId
     */
    boolean existsByUserIdAndTagId(Long userId, Long tagId);

    /**
     * 获取用户兴趣标签统计（覆盖索引优化）
     * 对应Mapper: getUserInterestStats
     */
    List<Map<String, Object>> getUserInterestStats(Long userId, BigDecimal minScore);

    /**
     * 获取标签的热门关注用户（覆盖索引优化）
     * 对应Mapper: getTagHotUsers
     */
    List<Map<String, Object>> getTagHotUsers(Long tagId, Integer limit);

    // =================== 更新操作方法 ===================

    /**
     * 更新用户兴趣分数
     * 对应Mapper: updateInterestScore
     */
    boolean updateInterestScore(Long userId, Long tagId, BigDecimal interestScore);

    /**
     * 批量更新用户标签状态
     * 对应Mapper: batchUpdateStatus
     */
    int batchUpdateStatus(Long userId, List<Long> tagIds, String status);

    // =================== 业务逻辑方法 ===================

    /**
     * 添加用户兴趣标签（带重复检查）
     */
    UserInterestTag addUserInterestSafely(Long userId, Long tagId, BigDecimal interestScore);

    /**
     * 移除用户兴趣标签
     */
    boolean removeUserInterest(Long userId, Long tagId);

    /**
     * 激活用户兴趣标签
     */
    boolean activateUserInterest(Long userId, Long tagId);

    /**
     * 停用用户兴趣标签
     */
    boolean deactivateUserInterest(Long userId, Long tagId);

    /**
     * 批量设置用户兴趣标签
     */
    int batchSetUserInterests(Long userId, List<Long> tagIds, BigDecimal defaultScore);

    /**
     * 获取用户活跃兴趣标签
     */
    List<UserInterestTag> getActiveUserInterests(Long userId);

    /**
     * 获取用户高兴趣标签（分数大于阈值）
     */
    List<UserInterestTag> getHighInterestTags(Long userId, BigDecimal minScore);

    /**
     * 增加用户对标签的兴趣分数
     */
    boolean increaseInterestScore(Long userId, Long tagId, BigDecimal increment);

    /**
     * 减少用户对标签的兴趣分数
     */
    boolean decreaseInterestScore(Long userId, Long tagId, BigDecimal decrement);

    /**
     * 重置用户对标签的兴趣分数
     */
    boolean resetInterestScore(Long userId, Long tagId, BigDecimal newScore);
}