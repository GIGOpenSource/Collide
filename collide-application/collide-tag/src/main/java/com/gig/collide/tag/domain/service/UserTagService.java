package com.gig.collide.tag.domain.service;

import com.gig.collide.base.response.PageResponse;
import com.gig.collide.tag.domain.entity.UserTagFollow;
import com.gig.collide.api.tag.request.UserTagQueryRequest;

import java.util.List;
import java.util.Map;

/**
 * 用户标签领域服务接口
 * 
 * @author GIG Team
 * @version 1.0.0
 */
public interface UserTagService {

    // =================== 用户关注标签管理 ===================

    /**
     * 用户关注标签
     * 
     * @param userId 用户ID
     * @param tagId 标签ID
     * @return 关注是否成功
     */
    boolean followTag(Long userId, Long tagId);

    /**
     * 用户取消关注标签
     * 
     * @param userId 用户ID
     * @param tagId 标签ID
     * @return 取消关注是否成功
     */
    boolean unfollowTag(Long userId, Long tagId);

    /**
     * 批量关注标签
     * 
     * @param userId 用户ID
     * @param tagIds 标签ID列表
     * @return 关注结果（成功的标签ID列表）
     */
    List<Long> batchFollowTags(Long userId, List<Long> tagIds);

    /**
     * 批量取消关注标签
     * 
     * @param userId 用户ID
     * @param tagIds 标签ID列表
     * @return 取消关注是否成功
     */
    boolean batchUnfollowTags(Long userId, List<Long> tagIds);

    /**
     * 替换用户的所有关注标签
     * 
     * @param userId 用户ID
     * @param tagIds 新的标签ID列表
     * @return 替换是否成功
     */
    boolean replaceUserFollowedTags(Long userId, List<Long> tagIds);

    // =================== 用户关注标签查询 ===================

    /**
     * 检查用户是否关注了指定标签
     * 
     * @param userId 用户ID
     * @param tagId 标签ID
     * @return 是否关注
     */
    boolean isFollowing(Long userId, Long tagId);

    /**
     * 获取用户关注的标签ID列表
     * 
     * @param userId 用户ID
     * @return 标签ID列表
     */
    List<Long> getUserFollowedTagIds(Long userId);

    /**
     * 获取用户关注的标签详情列表
     * 
     * @param userId 用户ID
     * @return 用户标签关注列表
     */
    List<UserTagFollow> getUserFollowedTags(Long userId);

    /**
     * 获取用户关注标签数量
     * 
     * @param userId 用户ID
     * @return 关注标签数量
     */
    Integer getUserFollowedTagCount(Long userId);

    /**
     * 分页查询用户关注的标签
     * 
     * @param request 查询请求
     * @return 分页用户标签关注列表
     */
    PageResponse<UserTagFollow> queryUserFollowedTags(UserTagQueryRequest request);

    /**
     * 获取用户最近关注的标签
     * 
     * @param userId 用户ID
     * @param days 天数范围
     * @param limit 数量限制
     * @return 最近关注的标签列表
     */
    List<UserTagFollow> getUserRecentFollowedTags(Long userId, Integer days, Integer limit);

    /**
     * 批量检查用户是否关注了指定标签
     * 
     * @param userId 用户ID
     * @param tagIds 标签ID列表
     * @return 关注状态映射（tagId -> isFollowed）
     */
    Map<Long, Boolean> batchCheckFollowing(Long userId, List<Long> tagIds);

    // =================== 标签关注者查询 ===================

    /**
     * 获取标签的关注者数量
     * 
     * @param tagId 标签ID
     * @return 关注者数量
     */
    Long getTagFollowerCount(Long tagId);

    /**
     * 获取标签的关注者列表
     * 
     * @param tagId 标签ID
     * @param limit 数量限制
     * @return 关注者用户ID列表
     */
    List<Long> getTagFollowers(Long tagId, Integer limit);

    /**
     * 获取标签最近的关注者
     * 
     * @param tagId 标签ID
     * @param days 天数范围
     * @param limit 数量限制
     * @return 最近关注者用户ID列表
     */
    List<Long> getTagRecentFollowers(Long tagId, Integer days, Integer limit);

    // =================== 协同过滤基础支持 ===================

    /**
     * 获取活跃的标签用户
     * 
     * @param minTagCount 最小标签数量
     * @param days 活跃天数范围
     * @param limit 数量限制
     * @return 活跃用户ID列表
     */
    List<Long> getActiveTagUsers(Integer minTagCount, Integer days, Integer limit);

    /**
     * 查找关注了指定标签的其他用户
     * 
     * @param tagIds 标签ID列表
     * @param excludeUserId 排除的用户ID
     * @param limit 数量限制
     * @return 其他用户ID列表
     */
    List<Long> findUsersWithSimilarTags(List<Long> tagIds, Long excludeUserId, Integer limit);

    /**
     * 计算用户之间的共同标签数量
     * 
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @return 共同标签数量
     */
    Integer countCommonTags(Long userId1, Long userId2);

    /**
     * 获取两个用户的共同标签列表
     * 
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @return 共同标签ID列表
     */
    List<Long> getCommonTagIds(Long userId1, Long userId2);

    /**
     * 计算用户相似度（基于Jaccard相似度）
     * 
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @return 相似度分数（0.0-1.0）
     */
    Double calculateUserSimilarity(Long userId1, Long userId2);

    // =================== 用户标签统计分析 ===================

    /**
     * 获取用户标签统计信息
     * 
     * @param userId 用户ID
     * @return 统计信息
     */
    Map<String, Object> getUserTagStatistics(Long userId);

    /**
     * 获取用户标签关注的权重分布
     * 
     * @param userId 用户ID
     * @return 权重分布统计
     */
    Map<String, Integer> getUserTagWeightDistribution(Long userId);

    /**
     * 获取用户关注标签的时间分布
     * 
     * @param userId 用户ID
     * @param days 天数范围
     * @return 时间分布统计
     */
    Map<String, Integer> getUserFollowTimeDistribution(Long userId, Integer days);

    // =================== 权限验证 ===================

    /**
     * 检查用户是否可以关注更多标签
     * 
     * @param userId 用户ID
     * @return 是否可以关注更多标签
     */
    boolean canUserFollowMoreTags(Long userId);

    /**
     * 检查用户是否可以关注指定标签
     * 
     * @param userId 用户ID
     * @param tagId 标签ID
     * @return 是否可以关注
     */
    boolean canUserFollowTag(Long userId, Long tagId);

    /**
     * 获取用户还可以关注的标签数量
     * 
     * @param userId 用户ID
     * @return 剩余可关注数量
     */
    Integer getUserRemainingFollowCount(Long userId);

    // =================== 数据清理和管理 ===================

    /**
     * 清理用户的无效标签关注
     * 
     * @param userId 用户ID（为空时清理所有用户）
     * @return 清理的记录数
     */
    Integer cleanupInvalidUserTagFollows(Long userId);

    /**
     * 删除用户的所有标签关注
     * 
     * @param userId 用户ID
     * @return 删除是否成功
     */
    boolean deleteAllUserFollows(Long userId);

    /**
     * 删除标签的所有关注记录
     * 
     * @param tagId 标签ID
     * @return 删除是否成功
     */
    boolean deleteAllTagFollows(Long tagId);

    // =================== 智能推荐支持 ===================

    /**
     * 为新用户推荐热门标签
     * 
     * @param userId 用户ID
     * @param limit 推荐数量限制
     * @return 推荐标签ID列表
     */
    List<Long> getHotTagsForNewUser(Long userId, Integer limit);

    /**
     * 基于用户行为推荐标签
     * 
     * @param userId 用户ID
     * @param behaviorType 行为类型
     * @param limit 推荐数量限制
     * @return 推荐标签ID列表
     */
    List<Long> getTagsByUserBehavior(Long userId, String behaviorType, Integer limit);

    /**
     * 获取用户可能感兴趣的标签（基于兴趣相似的用户）
     * 
     * @param userId 用户ID
     * @param limit 推荐数量限制
     * @return 推荐标签ID列表
     */
    List<Long> getInterestingTagsForUser(Long userId, Integer limit);
}