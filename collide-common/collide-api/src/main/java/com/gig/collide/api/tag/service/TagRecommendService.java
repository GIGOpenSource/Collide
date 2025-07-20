package com.gig.collide.api.tag.service;

import com.gig.collide.api.tag.constant.TagRelationType;
import com.gig.collide.api.tag.response.data.TagInfo;
import com.gig.collide.api.tag.response.data.UserTagInfo;

import java.util.List;
import java.util.Map;

/**
 * 标签推荐服务接口（内部服务，为推荐系统提供支持）
 * @author GIG
 */
public interface TagRecommendService {

    /**
     * 根据用户行为推荐标签
     * @param userId 用户ID
     * @param behaviorData 用户行为数据
     * @param limit 推荐数量
     * @return 推荐标签列表
     */
    List<TagInfo> recommendByUserBehavior(Long userId, Map<String, Object> behaviorData, Integer limit);

    /**
     * 根据用户已有标签推荐相似标签
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐标签列表
     */
    List<TagInfo> recommendSimilarTags(Long userId, Integer limit);

    /**
     * 根据内容特征推荐标签
     * @param contentFeatures 内容特征
     * @param relationType 关联类型
     * @param limit 推荐数量
     * @return 推荐标签列表
     */
    List<TagInfo> recommendByContentFeatures(Map<String, Object> contentFeatures, TagRelationType relationType, Integer limit);

    /**
     * 计算用户标签偏好权重
     * @param userId 用户ID
     * @return 标签权重映射
     */
    Map<Long, Double> calculateUserTagWeights(Long userId);

    /**
     * 获取用户标签画像
     * @param userId 用户ID
     * @return 用户标签画像
     */
    Map<String, Object> getUserTagProfile(Long userId);

    /**
     * 根据协同过滤推荐标签
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐标签列表
     */
    List<TagInfo> recommendByCollaborativeFiltering(Long userId, Integer limit);

    /**
     * 获取标签相关度矩阵
     * @param tagIds 标签ID列表
     * @return 相关度矩阵
     */
    Map<String, Double> getTagCorrelationMatrix(List<Long> tagIds);

    /**
     * 实时更新用户标签偏好
     * @param userId 用户ID
     * @param tagId 标签ID
     * @param action 操作类型（add/remove/click等）
     * @param weight 权重变化
     */
    void updateUserTagPreference(Long userId, Long tagId, String action, Double weight);

    /**
     * 获取同类用户的热门标签
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 热门标签列表
     */
    List<TagInfo> getSimilarUsersHotTags(Long userId, Integer limit);

    /**
     * 根据时间趋势推荐标签
     * @param relationType 关联类型
     * @param timeRange 时间范围（天）
     * @param limit 推荐数量
     * @return 趋势标签列表
     */
    List<TagInfo> recommendTrendingTags(TagRelationType relationType, Integer timeRange, Integer limit);

    /**
     * 获取用户标签多样性分析
     * @param userId 用户ID
     * @return 多样性分析结果
     */
    Map<String, Object> analyzeUserTagDiversity(Long userId);
} 