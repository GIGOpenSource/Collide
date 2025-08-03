package com.gig.collide.tag.domain.service.impl;

import com.gig.collide.tag.domain.service.RecommendationService;
import com.gig.collide.tag.domain.service.TagService;
import com.gig.collide.tag.domain.service.UserTagService;
import com.gig.collide.tag.domain.service.ContentTagService;
import com.gig.collide.tag.domain.entity.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐算法服务实现
 * 
 * @author GIG Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final TagService tagService;
    private final UserTagService userTagService;
    private final ContentTagService contentTagService;

    // 推荐算法参数
    private static final double SIMILARITY_THRESHOLD = 0.1; // 相似度阈值
    private static final int MAX_SIMILAR_USERS = 50; // 最大相似用户数
    private static final double CONTENT_BASED_WEIGHT = 0.6; // 基于内容的权重
    private static final double COLLABORATIVE_WEIGHT = 0.4; // 协同过滤权重
    private static final double TIME_DECAY_FACTOR = 0.95; // 时间衰减因子

    // =================== 标签推荐 ===================

    @Override
    public List<Long> recommendTagsByCollaborativeFiltering(Long userId, Integer limit) {
        try {
            log.debug("基于协同过滤推荐标签: userId={}, limit={}", userId, limit);
            
            // 获取用户关注的标签
            List<Long> userTags = userTagService.getUserFollowedTagIds(userId);
            if (userTags.isEmpty()) {
                // 新用户推荐热门标签
                return userTagService.getHotTagsForNewUser(userId, limit);
            }
            
            // 查找相似用户
            List<Long> similarUsers = findSimilarUsers(userId, MAX_SIMILAR_USERS);
            if (similarUsers.isEmpty()) {
                return userTagService.getHotTagsForNewUser(userId, limit);
            }
            
            // 统计相似用户关注的标签
            Map<Long, Double> tagScores = new HashMap<>();
            Set<Long> userTagSet = new HashSet<>(userTags);
            
            for (Long similarUserId : similarUsers) {
                List<Long> similarUserTags = userTagService.getUserFollowedTagIds(similarUserId);
                double userSimilarity = calculateUserSimilarity(userId, similarUserId);
                
                for (Long tagId : similarUserTags) {
                    if (!userTagSet.contains(tagId)) { // 排除用户已关注的标签
                        tagScores.merge(tagId, userSimilarity, Double::sum);
                    }
                }
            }
            
            // 结合标签权重进行评分调整
            adjustTagScoresByWeight(tagScores);
            
            // 按分数排序并返回
            return tagScores.entrySet().stream()
                    .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                    .limit(limit)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("协同过滤推荐标签失败: userId={}", userId, e);
            return userTagService.getHotTagsForNewUser(userId, limit);
        }
    }

    @Override
    public List<Long> recommendTagsByUserBehavior(Long userId, String behaviorType, Integer limit) {
        try {
            log.debug("基于用户行为推荐标签: userId={}, behaviorType={}, limit={}", userId, behaviorType, limit);
            
            // 根据行为类型推荐不同的标签
            switch (behaviorType.toLowerCase()) {
                case "view":
                    return recommendTagsByViewBehavior(userId, limit);
                case "like":
                    return recommendTagsByLikeBehavior(userId, limit);
                case "share":
                    return recommendTagsByShareBehavior(userId, limit);
                case "comment":
                    return recommendTagsByCommentBehavior(userId, limit);
                default:
                    return userTagService.getHotTagsForNewUser(userId, limit);
            }
        } catch (Exception e) {
            log.error("基于用户行为推荐标签失败: userId={}, behaviorType={}", userId, behaviorType, e);
            return userTagService.getHotTagsForNewUser(userId, limit);
        }
    }

    @Override
    public List<Long> recommendTagsHybrid(Long userId, Integer limit) {
        try {
            log.debug("混合推荐标签: userId={}, limit={}", userId, limit);
            
            int collaborativeLimit = (int) (limit * COLLABORATIVE_WEIGHT);
            int contentBasedLimit = limit - collaborativeLimit;
            
            // 协同过滤推荐
            List<Long> collaborativeTags = recommendTagsByCollaborativeFiltering(userId, collaborativeLimit);
            
            // 基于内容的推荐（基于用户已关注标签的相关标签）
            List<Long> contentBasedTags = recommendTagsByContentBased(userId, contentBasedLimit);
            
            // 合并结果并去重
            Set<Long> resultSet = new LinkedHashSet<>();
            resultSet.addAll(collaborativeTags);
            resultSet.addAll(contentBasedTags);
            
            // 如果结果不足，用热门标签补充
            if (resultSet.size() < limit) {
                List<Long> hotTags = userTagService.getHotTagsForNewUser(userId, limit - resultSet.size());
                resultSet.addAll(hotTags);
            }
            
            return resultSet.stream().limit(limit).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("混合推荐标签失败: userId={}", userId, e);
            return userTagService.getHotTagsForNewUser(userId, limit);
        }
    }

    // =================== 内容推荐 ===================

    @Override
    public List<Long> recommendContentsByUserTags(Long userId, List<Long> excludeContentIds, Integer limit) {
        try {
            log.debug("基于用户标签推荐内容: userId={}, limit={}", userId, limit);
            
            return contentTagService.getRecommendContentsByUserTags(userId, excludeContentIds, limit);
        } catch (Exception e) {
            log.error("基于用户标签推荐内容失败: userId={}", userId, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Long> recommendContentsByCollaborativeFiltering(Long userId, List<Long> excludeContentIds, Integer limit) {
        try {
            log.debug("基于协同过滤推荐内容: userId={}, limit={}", userId, limit);
            
            // 获取相似用户
            List<Long> similarUsers = findSimilarUsers(userId, MAX_SIMILAR_USERS);
            if (similarUsers.isEmpty()) {
                return new ArrayList<>();
            }
            
            // 获取用户关注的标签
            List<Long> userTags = userTagService.getUserFollowedTagIds(userId);
            
            // 统计相似用户交互的内容
            Map<Long, Double> contentScores = new HashMap<>();
            Set<Long> excludeSet = excludeContentIds != null ? new HashSet<>(excludeContentIds) : new HashSet<>();
            
            for (Long similarUserId : similarUsers) {
                double userSimilarity = calculateUserSimilarity(userId, similarUserId);
                List<Long> similarUserTags = userTagService.getUserFollowedTagIds(similarUserId);
                
                // 为每个标签获取内容
                for (Long tagId : similarUserTags) {
                    List<Long> tagContents = contentTagService.getContentsByTag(tagId, 20);
                    for (Long contentId : tagContents) {
                        if (!excludeSet.contains(contentId)) {
                            double score = userSimilarity;
                            // 如果用户也关注了这个标签，增加权重
                            if (userTags.contains(tagId)) {
                                score *= 1.5;
                            }
                            contentScores.merge(contentId, score, Double::sum);
                        }
                    }
                }
            }
            
            // 按分数排序并返回
            return contentScores.entrySet().stream()
                    .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                    .limit(limit)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("协同过滤推荐内容失败: userId={}", userId, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Long> recommendContentsBySimilarity(Long contentId, Integer limit) {
        try {
            log.debug("基于内容相似性推荐: contentId={}, limit={}", contentId, limit);
            
            return contentTagService.getRelatedContentsByTags(contentId, limit);
        } catch (Exception e) {
            log.error("基于内容相似性推荐失败: contentId={}", contentId, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Long> recommendContentsHybrid(Long userId, List<Long> excludeContentIds, Integer limit) {
        try {
            log.debug("混合推荐内容: userId={}, limit={}", userId, limit);
            
            int tagBasedLimit = (int) (limit * CONTENT_BASED_WEIGHT);
            int collaborativeLimit = limit - tagBasedLimit;
            
            // 基于用户标签的推荐
            List<Long> tagBasedContents = recommendContentsByUserTags(userId, excludeContentIds, tagBasedLimit);
            
            // 协同过滤推荐
            List<Long> collaborativeContents = recommendContentsByCollaborativeFiltering(userId, excludeContentIds, collaborativeLimit);
            
            // 合并结果并去重
            Set<Long> resultSet = new LinkedHashSet<>();
            resultSet.addAll(tagBasedContents);
            resultSet.addAll(collaborativeContents);
            
            return resultSet.stream().limit(limit).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("混合推荐内容失败: userId={}", userId, e);
            return new ArrayList<>();
        }
    }

    // =================== 相似度计算 ===================

    @Override
    public Double calculateUserSimilarity(Long userId1, Long userId2) {
        try {
            if (userId1.equals(userId2)) {
                return 1.0;
            }
            
            // 获取两个用户的标签集合
            Set<Long> tags1 = new HashSet<>(userTagService.getUserFollowedTagIds(userId1));
            Set<Long> tags2 = new HashSet<>(userTagService.getUserFollowedTagIds(userId2));
            
            if (tags1.isEmpty() && tags2.isEmpty()) {
                return 0.0;
            }
            
            // 计算Jaccard相似度
            Set<Long> intersection = new HashSet<>(tags1);
            intersection.retainAll(tags2);
            
            Set<Long> union = new HashSet<>(tags1);
            union.addAll(tags2);
            
            return (double) intersection.size() / union.size();
        } catch (Exception e) {
            log.error("计算用户相似度失败: userId1={}, userId2={}", userId1, userId2, e);
            return 0.0;
        }
    }

    @Override
    public Double calculateContentSimilarity(Long contentId1, Long contentId2) {
        try {
            if (contentId1.equals(contentId2)) {
                return 1.0;
            }
            
            // 获取两个内容的标签集合
            Set<Long> tags1 = new HashSet<>(contentTagService.getContentTagIds(contentId1));
            Set<Long> tags2 = new HashSet<>(contentTagService.getContentTagIds(contentId2));
            
            if (tags1.isEmpty() && tags2.isEmpty()) {
                return 0.0;
            }
            
            // 计算加权Jaccard相似度（考虑标签权重）
            double weightedIntersection = 0.0;
            double weightedUnion = 0.0;
            
            Set<Long> allTags = new HashSet<>(tags1);
            allTags.addAll(tags2);
            
            for (Long tagId : allTags) {
                Tag tag = tagService.getTagById(tagId);
                if (tag != null) {
                    double weight = tag.getWeight() / 100.0;
                    
                    if (tags1.contains(tagId) && tags2.contains(tagId)) {
                        weightedIntersection += weight;
                    }
                    
                    if (tags1.contains(tagId) || tags2.contains(tagId)) {
                        weightedUnion += weight;
                    }
                }
            }
            
            return weightedUnion > 0 ? weightedIntersection / weightedUnion : 0.0;
        } catch (Exception e) {
            log.error("计算内容相似度失败: contentId1={}, contentId2={}", contentId1, contentId2, e);
            return 0.0;
        }
    }

    @Override
    public List<Long> findSimilarUsers(Long userId, Integer limit) {
        try {
            List<Long> userTags = userTagService.getUserFollowedTagIds(userId);
            if (userTags.isEmpty()) {
                return new ArrayList<>();
            }
            
            // 获取关注了相似标签的其他用户
            List<Long> candidateUsers = userTagService.findUsersWithSimilarTags(userTags, userId, limit * 3);
            
            // 计算相似度并排序
            Map<Long, Double> userSimilarities = new HashMap<>();
            for (Long candidateUserId : candidateUsers) {
                double similarity = calculateUserSimilarity(userId, candidateUserId);
                if (similarity > SIMILARITY_THRESHOLD) {
                    userSimilarities.put(candidateUserId, similarity);
                }
            }
            
            return userSimilarities.entrySet().stream()
                    .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                    .limit(limit)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查找相似用户失败: userId={}", userId, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Long> findSimilarContents(Long contentId, Integer limit) {
        try {
            return contentTagService.getRelatedContentsByTags(contentId, limit);
        } catch (Exception e) {
            log.error("查找相似内容失败: contentId={}", contentId, e);
            return new ArrayList<>();
        }
    }

    // =================== 推荐分数计算 ===================

    @Override
    public Double calculateTagRecommendScore(Long userId, Long tagId) {
        try {
            // 如果用户已关注，返回0
            if (userTagService.isFollowing(userId, tagId)) {
                return 0.0;
            }
            
            double score = 0.0;
            
            // 基于标签本身属性的分数
            Tag tag = tagService.getTagById(tagId);
            if (tag != null) {
                score += tag.getWeight() / 100.0 * 0.3; // 权重占30%
                score += Math.log(tag.getHotness() + 1) / Math.log(1000) * 0.2; // 热度占20%
                score += Math.log(tag.getFollowCount() + 1) / Math.log(1000) * 0.2; // 关注数占20%
            }
            
            // 基于协同过滤的分数
            List<Long> similarUsers = findSimilarUsers(userId, 20);
            double collaborativeScore = 0.0;
            for (Long similarUserId : similarUsers) {
                if (userTagService.isFollowing(similarUserId, tagId)) {
                    double similarity = calculateUserSimilarity(userId, similarUserId);
                    collaborativeScore += similarity;
                }
            }
            score += (collaborativeScore / Math.max(1, similarUsers.size())) * 0.3; // 协同过滤占30%
            
            return Math.min(1.0, score);
        } catch (Exception e) {
            log.error("计算标签推荐分数失败: userId={}, tagId={}", userId, tagId, e);
            return 0.0;
        }
    }

    @Override
    public Double calculateContentRecommendScore(Long userId, Long contentId) {
        try {
            double score = 0.0;
            
            // 基于用户标签偏好的分数
            List<Long> userTags = userTagService.getUserFollowedTagIds(userId);
            List<Long> contentTags = contentTagService.getContentTagIds(contentId);
            
            Set<Long> userTagSet = new HashSet<>(userTags);
            for (Long tagId : contentTags) {
                if (userTagSet.contains(tagId)) {
                    Tag tag = tagService.getTagById(tagId);
                    if (tag != null) {
                        score += tag.getWeight() / 100.0;
                    }
                }
            }
            
            // 归一化分数
            if (!contentTags.isEmpty()) {
                score = score / contentTags.size();
            }
            
            return Math.min(1.0, score);
        } catch (Exception e) {
            log.error("计算内容推荐分数失败: userId={}, contentId={}", userId, contentId, e);
            return 0.0;
        }
    }

    @Override
    public Map<Long, Double> batchCalculateTagRecommendScores(Long userId, List<Long> tagIds) {
        Map<Long, Double> scores = new HashMap<>();
        for (Long tagId : tagIds) {
            scores.put(tagId, calculateTagRecommendScore(userId, tagId));
        }
        return scores;
    }

    @Override
    public Map<Long, Double> batchCalculateContentRecommendScores(Long userId, List<Long> contentIds) {
        Map<Long, Double> scores = new HashMap<>();
        for (Long contentId : contentIds) {
            scores.put(contentId, calculateContentRecommendScore(userId, contentId));
        }
        return scores;
    }

    // =================== 多样性优化 ===================

    @Override
    public List<Long> optimizeRecommendationDiversity(Long userId, List<Long> recommendIds, Double diversityFactor) {
        try {
            if (recommendIds.isEmpty() || diversityFactor <= 0) {
                return recommendIds;
            }
            
            List<Long> optimizedList = new ArrayList<>();
            Set<Long> usedTags = new HashSet<>();
            
            for (Long id : recommendIds) {
                // 获取相关标签
                List<Long> tags = contentTagService.getContentTagIds(id);
                
                // 检查多样性
                boolean isNovel = true;
                for (Long tagId : tags) {
                    if (usedTags.contains(tagId)) {
                        isNovel = false;
                        break;
                    }
                }
                
                // 根据多样性因子决定是否添加
                if (isNovel || Math.random() > diversityFactor) {
                    optimizedList.add(id);
                    usedTags.addAll(tags);
                }
                
                if (optimizedList.size() >= recommendIds.size()) {
                    break;
                }
            }
            
            return optimizedList;
        } catch (Exception e) {
            log.error("优化推荐多样性失败: userId={}", userId, e);
            return recommendIds;
        }
    }

    @Override
    public Double calculateRecommendationDiversity(List<Long> recommendIds, String type) {
        try {
            if (recommendIds.size() <= 1) {
                return 1.0;
            }
            
            Set<Long> allTags = new HashSet<>();
            List<Set<Long>> itemTagSets = new ArrayList<>();
            
            // 收集所有项目的标签
            for (Long id : recommendIds) {
                List<Long> tags = "tag".equals(type) ? 
                        Arrays.asList(id) : contentTagService.getContentTagIds(id);
                Set<Long> tagSet = new HashSet<>(tags);
                itemTagSets.add(tagSet);
                allTags.addAll(tagSet);
            }
            
            if (allTags.isEmpty()) {
                return 0.0;
            }
            
            // 计算平均相似度
            double totalSimilarity = 0.0;
            int pairCount = 0;
            
            for (int i = 0; i < itemTagSets.size(); i++) {
                for (int j = i + 1; j < itemTagSets.size(); j++) {
                    Set<Long> tags1 = itemTagSets.get(i);
                    Set<Long> tags2 = itemTagSets.get(j);
                    
                    Set<Long> intersection = new HashSet<>(tags1);
                    intersection.retainAll(tags2);
                    
                    Set<Long> union = new HashSet<>(tags1);
                    union.addAll(tags2);
                    
                    double similarity = union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
                    totalSimilarity += similarity;
                    pairCount++;
                }
            }
            
            double averageSimilarity = pairCount > 0 ? totalSimilarity / pairCount : 0.0;
            return 1.0 - averageSimilarity; // 多样性 = 1 - 相似度
        } catch (Exception e) {
            log.error("计算推荐多样性失败: type={}", type, e);
            return 0.0;
        }
    }

    // =================== 其他方法的简化实现 ===================

    @Override
    public void updateUserPreferenceModel(Long userId, Map<String, Object> behaviorData) {
        // 简化实现：可以根据需要扩展
        log.debug("更新用户偏好模型: userId={}", userId);
    }

    @Override
    public Map<String, Object> getRecommendationParameters(String algorithmType) {
        Map<String, Object> params = new HashMap<>();
        params.put("similarityThreshold", SIMILARITY_THRESHOLD);
        params.put("maxSimilarUsers", MAX_SIMILAR_USERS);
        params.put("contentBasedWeight", CONTENT_BASED_WEIGHT);
        params.put("collaborativeWeight", COLLABORATIVE_WEIGHT);
        return params;
    }

    @Override
    public void updateRecommendationParameters(String algorithmType, Map<String, Object> parameters) {
        // 简化实现：可以根据需要扩展
        log.debug("更新推荐算法参数: algorithmType={}", algorithmType);
    }

    @Override
    public List<Long> generateColdStartRecommendations(Long userId, Map<String, Object> userProfile, Integer limit) {
        return userTagService.getHotTagsForNewUser(userId, limit);
    }

    @Override
    public List<Long> generateColdStartContentSimilarity(Long contentId, Map<String, Object> contentFeatures, Integer limit) {
        return new ArrayList<>();
    }

    @Override
    public Double calculateRecommendationPrecision(Long userId, List<Long> recommendIds, List<Long> actualInteractionIds) {
        Set<Long> recommendSet = new HashSet<>(recommendIds);
        Set<Long> actualSet = new HashSet<>(actualInteractionIds);
        
        Set<Long> intersection = new HashSet<>(recommendSet);
        intersection.retainAll(actualSet);
        
        return recommendSet.isEmpty() ? 0.0 : (double) intersection.size() / recommendSet.size();
    }

    @Override
    public Double calculateRecommendationRecall(Long userId, List<Long> recommendIds, List<Long> actualInteractionIds) {
        Set<Long> recommendSet = new HashSet<>(recommendIds);
        Set<Long> actualSet = new HashSet<>(actualInteractionIds);
        
        Set<Long> intersection = new HashSet<>(recommendSet);
        intersection.retainAll(actualSet);
        
        return actualSet.isEmpty() ? 0.0 : (double) intersection.size() / actualSet.size();
    }

    @Override
    public Map<String, Object> generateRecommendationReport(Long userId, Integer timeRange) {
        Map<String, Object> report = new HashMap<>();
        report.put("userId", userId);
        report.put("timeRange", timeRange);
        report.put("recommendationCount", 0);
        report.put("precision", 0.0);
        report.put("recall", 0.0);
        return report;
    }

    // =================== 私有辅助方法 ===================

    /**
     * 根据标签权重调整推荐分数
     */
    private void adjustTagScoresByWeight(Map<Long, Double> tagScores) {
        for (Map.Entry<Long, Double> entry : tagScores.entrySet()) {
            Tag tag = tagService.getTagById(entry.getKey());
            if (tag != null) {
                double weightFactor = tag.getWeight() / 100.0;
                entry.setValue(entry.getValue() * weightFactor);
            }
        }
    }

    /**
     * 基于查看行为推荐标签
     */
    private List<Long> recommendTagsByViewBehavior(Long userId, Integer limit) {
        // 简化实现：返回热门标签
        return userTagService.getHotTagsForNewUser(userId, limit);
    }

    /**
     * 基于点赞行为推荐标签
     */
    private List<Long> recommendTagsByLikeBehavior(Long userId, Integer limit) {
        // 简化实现：返回热门标签
        return userTagService.getHotTagsForNewUser(userId, limit);
    }

    /**
     * 基于分享行为推荐标签
     */
    private List<Long> recommendTagsByShareBehavior(Long userId, Integer limit) {
        // 简化实现：返回权重较高的标签
        return tagService.getTagsByWeightRange(70, 100, limit).stream()
                .map(Tag::getId)
                .collect(Collectors.toList());
    }

    /**
     * 基于评论行为推荐标签
     */
    private List<Long> recommendTagsByCommentBehavior(Long userId, Integer limit) {
        // 简化实现：返回关注数较高的标签
        return tagService.getFollowCountRanking(limit).stream()
                .map(Tag::getId)
                .collect(Collectors.toList());
    }

    /**
     * 基于内容的标签推荐
     */
    private List<Long> recommendTagsByContentBased(Long userId, Integer limit) {
        List<Long> userTags = userTagService.getUserFollowedTagIds(userId);
        if (userTags.isEmpty()) {
            return userTagService.getHotTagsForNewUser(userId, limit);
        }
        
        // 获取用户关注标签的相关标签
        Set<Long> relatedTags = new HashSet<>();
        for (Long tagId : userTags) {
            List<Long> tagContents = contentTagService.getContentsByTag(tagId, 10);
            for (Long contentId : tagContents) {
                List<Long> contentTags = contentTagService.getContentTagIds(contentId);
                relatedTags.addAll(contentTags);
            }
        }
        
        // 移除用户已关注的标签
        relatedTags.removeAll(userTags);
        
        return relatedTags.stream().limit(limit).collect(Collectors.toList());
    }
}