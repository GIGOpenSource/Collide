package com.gig.collide.tag.domain.service;

import com.gig.collide.api.tag.request.UserInterestTagRequest;
import com.gig.collide.tag.domain.entity.UserInterestTag;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户兴趣标签业务服务接口
 * 
 * @author GIG Team
 * @version 1.0.0
 * @since 2024-12-19
 */
public interface UserInterestTagService {

    /**
     * 获取用户兴趣标签
     * 
     * @param userId 用户ID
     * @return 用户兴趣标签列表
     */
    List<UserInterestTag> getUserInterestTags(Long userId);

    /**
     * 设置用户兴趣标签
     * 
     * @param request 用户兴趣标签请求
     * @return 是否成功
     */
    boolean setUserInterestTags(UserInterestTagRequest request);

    /**
     * 添加用户兴趣标签
     * 
     * @param userId 用户ID
     * @param tagId 标签ID
     * @param interestScore 兴趣分数
     * @return 是否成功
     */
    boolean addUserInterestTag(Long userId, Long tagId, Double interestScore);

    /**
     * 移除用户兴趣标签
     * 
     * @param userId 用户ID
     * @param tagId 标签ID
     * @return 是否成功
     */
    boolean removeUserInterestTag(Long userId, Long tagId);

    /**
     * 更新用户兴趣标签分数
     * 
     * @param userId 用户ID
     * @param tagId 标签ID
     * @param interestScore 兴趣分数
     * @return 是否成功
     */
    boolean updateInterestScore(Long userId, Long tagId, BigDecimal interestScore);

    /**
     * 根据用户ID和标签ID查询兴趣标签
     * 
     * @param userId 用户ID
     * @param tagId 标签ID
     * @return 用户兴趣标签
     */
    UserInterestTag getUserInterestTag(Long userId, Long tagId);

    /**
     * 根据标签ID查询感兴趣的用户
     * 
     * @param tagId 标签ID
     * @return 用户兴趣标签列表
     */
    List<UserInterestTag> getInterestedUsers(Long tagId);

    /**
     * 推荐标签给用户
     * 
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 推荐标签ID列表
     */
    List<Long> recommendTagsToUser(Long userId, Integer limit);

    /**
     * 计算用户对标签类型的兴趣分数
     * 
     * @param userId 用户ID
     * @return 各标签类型的平均兴趣分数
     */
    Map<String, BigDecimal> calculateUserInterestByType(Long userId);

    /**
     * 统计用户兴趣标签数量
     * 
     * @param userId 用户ID
     * @return 兴趣标签数量
     */
    long countUserInterestTags(Long userId);

    /**
     * 统计标签的感兴趣用户数量
     * 
     * @param tagId 标签ID
     * @return 感兴趣用户数量
     */
    long countInterestedUsers(Long tagId);

    /**
     * 批量删除用户所有兴趣标签
     * 
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean clearUserInterestTags(Long userId);

    /**
     * 批量删除标签相关的所有用户兴趣
     * 
     * @param tagId 标签ID
     * @return 是否成功
     */
    boolean clearTagInterestUsers(Long tagId);
} 