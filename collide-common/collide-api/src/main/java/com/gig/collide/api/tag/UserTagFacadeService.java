package com.gig.collide.api.tag;

import com.gig.collide.api.tag.request.UserTagQueryRequest;
import com.gig.collide.api.tag.response.TagResponse;
import com.gig.collide.api.tag.response.UserTagResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import java.util.List;
import java.util.Map;

/**
 * 用户标签门面服务接口 - 对应 t_user_tag_follow 表
 * 支持用户关注标签功能和协同过滤推荐
 * 
 * @author GIG Team
 * @version 1.0.0
 */
public interface UserTagFacadeService {

    // =================== 用户关注标签管理 ===================

    /**
     * 用户关注标签
     * 
     * @param userId 用户ID
     * @param tagId 标签ID
     * @return 关注结果
     */
    Result<Void> followTag(Long userId, Long tagId);

    /**
     * 用户取消关注标签
     * 
     * @param userId 用户ID
     * @param tagId 标签ID
     * @return 取消关注结果
     */
    Result<Void> unfollowTag(Long userId, Long tagId);

    /**
     * 批量关注标签
     * 
     * @param userId 用户ID
     * @param tagIds 标签ID列表（最多9个）
     * @return 关注结果
     */
    Result<List<String>> batchFollowTags(Long userId, List<Long> tagIds);

    /**
     * 批量取消关注标签
     * 
     * @param userId 用户ID
     * @param tagIds 标签ID列表
     * @return 取消关注结果
     */
    Result<Void> batchUnfollowTags(Long userId, List<Long> tagIds);

    // =================== 用户关注标签查询 ===================

    /**
     * 检查用户是否关注了指定标签
     * 
     * @param userId 用户ID
     * @param tagId 标签ID
     * @return 是否关注
     */
    Result<Boolean> isFollowing(Long userId, Long tagId);

    /**
     * 获取用户关注的标签列表
     * 
     * @param userId 用户ID
     * @return 关注的标签列表
     */
    Result<List<TagResponse>> getUserFollowedTags(Long userId);

    /**
     * 分页查询用户关注的标签
     * 
     * @param request 查询请求
     * @return 分页用户标签列表
     */
    Result<PageResponse<UserTagResponse>> queryUserFollowedTags(UserTagQueryRequest request);

    /**
     * 获取用户关注标签数量
     * 
     * @param userId 用户ID
     * @return 关注标签数量
     */
    Result<Integer> getUserFollowedTagCount(Long userId);

    /**
     * 批量检查用户是否关注了指定标签
     * 
     * @param userId 用户ID
     * @param tagIds 标签ID列表
     * @return 关注状态映射（tagId -> isFollowed）
     */
    Result<Map<Long, Boolean>> batchCheckFollowing(Long userId, List<Long> tagIds);

    // =================== 协同过滤推荐 ===================

    /**
     * 基于协同过滤为用户推荐标签
     * 
     * @param userId 用户ID
     * @param limit 推荐数量限制（默认9）
     * @return 推荐标签列表
     */
    Result<List<TagResponse>> getRecommendTagsForUser(Long userId, Integer limit);

    /**
     * 获取与用户兴趣相似的用户列表
     * 
     * @param userId 用户ID
     * @param limit 返回数量限制（默认10）
     * @return 相似用户ID列表
     */
    Result<List<Long>> getSimilarUsers(Long userId, Integer limit);

    /**
     * 计算两个用户的兴趣相似度
     * 
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @return 相似度分数（0.0-1.0）
     */
    Result<Double> calculateUserSimilarity(Long userId1, Long userId2);

    // =================== 用户标签统计分析 ===================

    /**
     * 获取用户标签统计信息
     * 
     * @param userId 用户ID
     * @return 统计信息
     */
    Result<Map<String, Object>> getUserTagStatistics(Long userId);

    /**
     * 获取用户最近关注的标签
     * 
     * @param userId 用户ID
     * @param days 天数范围（默认30天）
     * @param limit 返回数量限制（默认10）
     * @return 最近关注的标签列表
     */
    Result<List<TagResponse>> getUserRecentFollowedTags(Long userId, Integer days, Integer limit);

    /**
     * 获取用户兴趣权重分布
     * 
     * @param userId 用户ID
     * @return 权重分布映射（权重范围 -> 标签数量）
     */
    Result<Map<String, Integer>> getUserInterestWeightDistribution(Long userId);

    // =================== 标签关注者查询 ===================

    /**
     * 获取标签的关注者数量
     * 
     * @param tagId 标签ID
     * @return 关注者数量
     */
    Result<Long> getTagFollowerCount(Long tagId);

    /**
     * 获取标签的关注者列表
     * 
     * @param tagId 标签ID
     * @param limit 返回数量限制（默认20）
     * @return 关注者用户ID列表
     */
    Result<List<Long>> getTagFollowers(Long tagId, Integer limit);

    /**
     * 获取最近关注该标签的用户
     * 
     * @param tagId 标签ID
     * @param days 天数范围（默认7天）
     * @param limit 返回数量限制（默认10）
     * @return 最近关注者用户ID列表
     */
    Result<List<Long>> getTagRecentFollowers(Long tagId, Integer days, Integer limit);

    // =================== 个性化推荐 ===================

    /**
     * 为新用户推荐热门标签
     * 
     * @param userId 用户ID
     * @param limit 推荐数量限制（默认9）
     * @return 推荐标签列表
     */
    Result<List<TagResponse>> getHotTagsForNewUser(Long userId, Integer limit);

    /**
     * 基于用户行为推荐标签
     * 
     * @param userId 用户ID
     * @param behaviorType 行为类型：view, like, share, comment
     * @param limit 推荐数量限制（默认5）
     * @return 推荐标签列表
     */
    Result<List<TagResponse>> getTagsByUserBehavior(Long userId, String behaviorType, Integer limit);

    // =================== 管理功能 ===================

    /**
     * 清理用户的无效标签关注（针对已删除的标签）
     * 
     * @param userId 用户ID（为空时清理所有用户）
     * @return 清理结果
     */
    Result<Integer> cleanupInvalidUserTagFollows(Long userId);

    /**
     * 获取用户关注标签的权限检查
     * 
     * @param userId 用户ID
     * @param tagId 标签ID
     * @return 是否可以关注
     */
    Result<Boolean> canUserFollowTag(Long userId, Long tagId);
}