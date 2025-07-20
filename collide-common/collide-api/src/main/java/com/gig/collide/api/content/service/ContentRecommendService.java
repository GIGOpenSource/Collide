package com.gig.collide.api.content.service;

import com.gig.collide.api.content.constant.ContentType;
import com.gig.collide.api.content.response.ContentQueryResponse;

import java.util.List;

/**
 * 内容推荐服务
 */
public interface ContentRecommendService {

    /**
     * 为用户推荐内容
     *
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 查询响应
     */
    ContentQueryResponse recommendForUser(Long userId, Integer limit);

    /**
     * 根据内容类型推荐
     *
     * @param userId 用户ID
     * @param contentType 内容类型
     * @param limit 推荐数量
     * @return 查询响应
     */
    ContentQueryResponse recommendByType(Long userId, ContentType contentType, Integer limit);

    /**
     * 根据标签推荐
     *
     * @param userId 用户ID
     * @param tags 标签列表
     * @param limit 推荐数量
     * @return 查询响应
     */
    ContentQueryResponse recommendByTags(Long userId, List<String> tags, Integer limit);

    /**
     * 热门内容推荐
     *
     * @param contentType 内容类型（可选）
     * @param limit 推荐数量
     * @return 查询响应
     */
    ContentQueryResponse recommendHotContent(ContentType contentType, Integer limit);

    /**
     * 最新内容推荐
     *
     * @param contentType 内容类型（可选）
     * @param limit 推荐数量
     * @return 查询响应
     */
    ContentQueryResponse recommendLatestContent(ContentType contentType, Integer limit);

    /**
     * 相关内容推荐
     *
     * @param contentId 内容ID
     * @param limit 推荐数量
     * @return 查询响应
     */
    ContentQueryResponse recommendRelatedContent(Long contentId, Integer limit);

    /**
     * 同类创作者内容推荐
     *
     * @param userId 用户ID
     * @param creatorUserId 创作者ID
     * @param limit 推荐数量
     * @return 查询响应
     */
    ContentQueryResponse recommendByCreator(Long userId, Long creatorUserId, Integer limit);

    /**
     * 用户可能喜欢的内容
     *
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 查询响应
     */
    ContentQueryResponse recommendMayLike(Long userId, Integer limit);

    /**
     * 根据分类推荐
     *
     * @param userId 用户ID
     * @param category 分类
     * @param limit 推荐数量
     * @return 查询响应
     */
    ContentQueryResponse recommendByCategory(Long userId, String category, Integer limit);

    /**
     * 获取个性化推荐
     *
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 查询响应
     */
    ContentQueryResponse getPersonalizedRecommendation(Long userId, Integer limit);
} 