package com.gig.collide.tag.domain.service;

import java.util.List;
import java.util.Map;

/**
 * 推荐算法服务接口
 * 
 * @author GIG Team
 * @version 1.0.0
 */
public interface RecommendationService {

    // =================== 标签推荐 ===================

    /**
     * 基于协同过滤推荐标签
     * 
     * @param userId 用户ID
     * @param limit 推荐数量限制
     * @return 推荐标签ID列表（按推荐分数排序）
     */
    List<Long> recommendTagsByCollaborativeFiltering(Long userId, Integer limit);

    /**
     * 基于用户行为推荐标签
     * 
     * @param userId 用户ID
     * @param behaviorType 行为类型
     * @param limit 推荐数量限制
     * @return 推荐标签ID列表
     */
    List<Long> recommendTagsByUserBehavior(Long userId, String behaviorType, Integer limit);

    /**
     * 混合推荐标签
     * 
     * @param userId 用户ID
     * @param limit 推荐数量限制
     * @return 推荐标签ID列表
     */
    List<Long> recommendTagsHybrid(Long userId, Integer limit);

    // =================== 内容推荐 ===================

    /**
     * 基于用户标签偏好推荐内容
     * 
     * @param userId 用户ID
     * @param excludeContentIds 排除的内容ID列表
     * @param limit 推荐数量限制
     * @return 推荐内容ID列表
     */
    List<Long> recommendContentsByUserTags(Long userId, List<Long> excludeContentIds, Integer limit);

    /**
     * 基于协同过滤推荐内容
     * 
     * @param userId 用户ID
     * @param excludeContentIds 排除的内容ID列表
     * @param limit 推荐数量限制
     * @return 推荐内容ID列表
     */
    List<Long> recommendContentsByCollaborativeFiltering(Long userId, List<Long> excludeContentIds, Integer limit);

    /**
     * 基于内容相似性推荐内容
     * 
     * @param contentId 基准内容ID
     * @param limit 推荐数量限制
     * @return 推荐内容ID列表
     */
    List<Long> recommendContentsBySimilarity(Long contentId, Integer limit);

    /**
     * 混合推荐内容
     * 
     * @param userId 用户ID
     * @param excludeContentIds 排除的内容ID列表
     * @param limit 推荐数量限制
     * @return 推荐内容ID列表
     */
    List<Long> recommendContentsHybrid(Long userId, List<Long> excludeContentIds, Integer limit);

    // =================== 相似度计算 ===================

    /**
     * 计算用户相似度（基于Jaccard相似度）
     * 
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @return 相似度分数（0.0-1.0）
     */
    Double calculateUserSimilarity(Long userId1, Long userId2);

    /**
     * 计算内容相似度（基于标签相似度）
     * 
     * @param contentId1 内容1 ID
     * @param contentId2 内容2 ID
     * @return 相似度分数（0.0-1.0）
     */
    Double calculateContentSimilarity(Long contentId1, Long contentId2);

    /**
     * 获取与用户最相似的用户列表
     * 
     * @param userId 用户ID
     * @param limit 返回数量限制
     * @return 相似用户ID列表（按相似度排序）
     */
    List<Long> findSimilarUsers(Long userId, Integer limit);

    /**
     * 获取与内容最相似的内容列表
     * 
     * @param contentId 内容ID
     * @param limit 返回数量限制
     * @return 相似内容ID列表（按相似度排序）
     */
    List<Long> findSimilarContents(Long contentId, Integer limit);

    // =================== 推荐分数计算 ===================

    /**
     * 计算标签对用户的推荐分数
     * 
     * @param userId 用户ID
     * @param tagId 标签ID
     * @return 推荐分数
     */
    Double calculateTagRecommendScore(Long userId, Long tagId);

    /**
     * 计算内容对用户的推荐分数
     * 
     * @param userId 用户ID
     * @param contentId 内容ID
     * @return 推荐分数
     */
    Double calculateContentRecommendScore(Long userId, Long contentId);

    /**
     * 批量计算标签推荐分数
     * 
     * @param userId 用户ID
     * @param tagIds 标签ID列表
     * @return 标签ID到推荐分数的映射
     */
    Map<Long, Double> batchCalculateTagRecommendScores(Long userId, List<Long> tagIds);

    /**
     * 批量计算内容推荐分数
     * 
     * @param userId 用户ID
     * @param contentIds 内容ID列表
     * @return 内容ID到推荐分数的映射
     */
    Map<Long, Double> batchCalculateContentRecommendScores(Long userId, List<Long> contentIds);

    // =================== 多样性优化 ===================

    /**
     * 对推荐结果进行多样性优化
     * 
     * @param userId 用户ID
     * @param recommendIds 推荐ID列表
     * @param diversityFactor 多样性因子（0.0-1.0）
     * @return 多样性优化后的推荐列表
     */
    List<Long> optimizeRecommendationDiversity(Long userId, List<Long> recommendIds, Double diversityFactor);

    /**
     * 计算推荐列表的多样性分数
     * 
     * @param recommendIds 推荐ID列表
     * @param type 类型（"tag" 或 "content"）
     * @return 多样性分数（0.0-1.0）
     */
    Double calculateRecommendationDiversity(List<Long> recommendIds, String type);

    // =================== 推荐算法调优 ===================

    /**
     * 更新用户偏好模型
     * 
     * @param userId 用户ID
     * @param behaviorData 行为数据
     */
    void updateUserPreferenceModel(Long userId, Map<String, Object> behaviorData);

    /**
     * 获取推荐算法参数
     * 
     * @param algorithmType 算法类型
     * @return 算法参数
     */
    Map<String, Object> getRecommendationParameters(String algorithmType);

    /**
     * 更新推荐算法参数
     * 
     * @param algorithmType 算法类型
     * @param parameters 新参数
     */
    void updateRecommendationParameters(String algorithmType, Map<String, Object> parameters);

    // =================== 冷启动问题处理 ===================

    /**
     * 为新用户生成推荐（冷启动）
     * 
     * @param userId 新用户ID
     * @param userProfile 用户画像数据
     * @param limit 推荐数量限制
     * @return 推荐项目ID列表
     */
    List<Long> generateColdStartRecommendations(Long userId, Map<String, Object> userProfile, Integer limit);

    /**
     * 为新内容生成相似推荐（冷启动）
     * 
     * @param contentId 新内容ID
     * @param contentFeatures 内容特征数据
     * @param limit 推荐数量限制
     * @return 相似内容ID列表
     */
    List<Long> generateColdStartContentSimilarity(Long contentId, Map<String, Object> contentFeatures, Integer limit);

    // =================== 推荐效果评估 ===================

    /**
     * 计算推荐精准度
     * 
     * @param userId 用户ID
     * @param recommendIds 推荐ID列表
     * @param actualInteractionIds 实际交互ID列表
     * @return 精准度分数
     */
    Double calculateRecommendationPrecision(Long userId, List<Long> recommendIds, List<Long> actualInteractionIds);

    /**
     * 计算推荐召回率
     * 
     * @param userId 用户ID
     * @param recommendIds 推荐ID列表
     * @param actualInteractionIds 实际交互ID列表
     * @return 召回率分数
     */
    Double calculateRecommendationRecall(Long userId, List<Long> recommendIds, List<Long> actualInteractionIds);

    /**
     * 生成推荐效果报告
     * 
     * @param userId 用户ID
     * @param timeRange 时间范围（天数）
     * @return 效果报告
     */
    Map<String, Object> generateRecommendationReport(Long userId, Integer timeRange);
}