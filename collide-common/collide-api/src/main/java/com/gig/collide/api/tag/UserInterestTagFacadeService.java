package com.gig.collide.api.tag;

import com.gig.collide.api.tag.request.UserInterestTagRequest;
import com.gig.collide.api.tag.response.UserInterestTagResponse;
import com.gig.collide.web.vo.Result;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户兴趣标签门面服务接口
 * 专注于用户与标签的兴趣关系管理
 *
 * @author GIG Team
 * @version 3.0.0
 * @since 2024-01-30
 */
public interface UserInterestTagFacadeService {

    // =================== 用户兴趣标签基础操作 ===================

    /**
     * 添加用户兴趣标签
     * 包含重复检查、分数验证
     */
    Result<UserInterestTagResponse> addUserInterest(UserInterestTagRequest request);

    /**
     * 移除用户兴趣标签
     */
    Result<Void> removeUserInterest(Long userId, Long tagId, Long operatorId);

    /**
     * 更新用户兴趣分数
     */
    Result<Void> updateUserInterestScore(Long userId, Long tagId, BigDecimal interestScore, Long operatorId);

    /**
     * 获取用户兴趣标签列表
     */
    Result<List<UserInterestTagResponse>> getUserInterests(Long userId);

    /**
     * 获取用户高分兴趣标签
     */
    Result<List<UserInterestTagResponse>> getUserTopInterests(Long userId, Integer limit);

    /**
     * 获取标签的关注用户
     */
    Result<List<UserInterestTagResponse>> getTagFollowers(Long tagId);

    // =================== 批量操作 ===================

    /**
     * 批量设置用户兴趣标签
     */
    Result<Integer> batchSetUserInterests(Long userId, List<Long> tagIds, BigDecimal defaultScore, Long operatorId);

    /**
     * 批量更新用户标签状态
     */
    Result<Integer> batchUpdateUserTagStatus(Long userId, List<Long> tagIds, String status, Long operatorId);

    /**
     * 批量激活用户兴趣标签
     */
    Result<Integer> batchActivateUserInterests(Long userId, List<Long> tagIds, Long operatorId);

    /**
     * 批量停用用户兴趣标签
     */
    Result<Integer> batchDeactivateUserInterests(Long userId, List<Long> tagIds, Long operatorId);

    // =================== 兴趣分数管理 ===================

    /**
     * 激活/停用用户兴趣标签
     */
    Result<Void> toggleUserInterest(Long userId, Long tagId, boolean active, Long operatorId);

    /**
     * 增加用户对标签的兴趣分数
     */
    Result<Void> increaseInterestScore(Long userId, Long tagId, BigDecimal increment, Long operatorId);

    /**
     * 减少用户对标签的兴趣分数
     */
    Result<Void> decreaseInterestScore(Long userId, Long tagId, BigDecimal decrement, Long operatorId);

    /**
     * 重置用户对标签的兴趣分数
     */
    Result<Void> resetInterestScore(Long userId, Long tagId, BigDecimal newScore, Long operatorId);

    // =================== 查询功能 ===================

    /**
     * 获取用户活跃兴趣标签
     */
    Result<List<UserInterestTagResponse>> getActiveUserInterests(Long userId);

    /**
     * 获取用户高兴趣标签（分数大于阈值）
     */
    Result<List<UserInterestTagResponse>> getHighInterestTags(Long userId, BigDecimal minScore);

    /**
     * 检查用户是否已关注标签
     */
    Result<Boolean> isUserInterestedInTag(Long userId, Long tagId);

    // =================== 统计分析 ===================

    /**
     * 获取用户兴趣统计
     */
    Result<List<Map<String, Object>>> getUserInterestStats(Long userId, BigDecimal minScore);

    /**
     * 获取标签的热门关注用户
     */
    Result<List<Map<String, Object>>> getTagHotUsers(Long tagId, Integer limit);

    /**
     * 获取用户兴趣标签的统计分析
     */
    Result<Map<String, Object>> getUserInterestAnalysis(Long userId);

    /**
     * 获取用户相关的完整标签信息
     * 包含标签详情和用户兴趣分数
     */
    Result<List<Map<String, Object>>> getUserTagsWithInterest(Long userId);

    // =================== 推荐系统 ===================

    /**
     * 推荐用户可能感兴趣的标签
     * 基于用户已有兴趣和热门标签
     */
    Result<List<Map<String, Object>>> recommendTagsForUser(Long userId, Integer limit);

    /**
     * 根据用户兴趣推荐相似用户
     */
    Result<List<Long>> recommendSimilarUsers(Long userId, Integer limit);

    /**
     * 获取用户兴趣标签的相关性分析
     */
    Result<Map<String, Object>> getUserInterestCorrelation(Long userId);

    // =================== 数据维护 ===================

    /**
     * 清理无效的用户兴趣标签
     * 清理不存在的用户或标签的关联数据
     */
    Result<Integer> cleanupInvalidUserInterests(Long operatorId);

    /**
     * 重新计算用户兴趣分数
     * 基于用户行为重新计算兴趣分数
     */
    Result<Integer> recalculateUserInterestScores(Long userId, Long operatorId);

    /**
     * 用户兴趣标签系统健康检查
     */
    Result<String> healthCheck();
}