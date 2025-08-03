package com.gig.collide.tag.domain.service;

import com.gig.collide.base.response.PageResponse;
import com.gig.collide.tag.domain.entity.ContentTag;
import com.gig.collide.api.tag.request.ContentTagQueryRequest;

import java.util.List;
import java.util.Map;

/**
 * 内容标签领域服务接口
 * 
 * @author GIG Team
 * @version 1.0.0
 */
public interface ContentTagService {

    // =================== 内容标签管理 ===================

    /**
     * 为内容添加标签
     * 
     * @param contentId 内容ID
     * @param tagId 标签ID
     * @return 添加是否成功
     */
    boolean addContentTag(Long contentId, Long tagId);

    /**
     * 移除内容的标签
     * 
     * @param contentId 内容ID
     * @param tagId 标签ID
     * @return 移除是否成功
     */
    boolean removeContentTag(Long contentId, Long tagId);

    /**
     * 批量为内容添加标签
     * 
     * @param contentId 内容ID
     * @param tagIds 标签ID列表
     * @return 成功添加的标签ID列表
     */
    List<Long> batchAddContentTags(Long contentId, List<Long> tagIds);

    /**
     * 批量移除内容的标签
     * 
     * @param contentId 内容ID
     * @param tagIds 标签ID列表
     * @return 移除是否成功
     */
    boolean batchRemoveContentTags(Long contentId, List<Long> tagIds);

    /**
     * 替换内容的所有标签
     * 
     * @param contentId 内容ID
     * @param tagIds 新的标签ID列表
     * @return 替换是否成功
     */
    boolean replaceContentTags(Long contentId, List<Long> tagIds);

    // =================== 内容标签查询 ===================

    /**
     * 获取内容的标签ID列表
     * 
     * @param contentId 内容ID
     * @return 标签ID列表
     */
    List<Long> getContentTagIds(Long contentId);

    /**
     * 获取内容的标签详情列表
     * 
     * @param contentId 内容ID
     * @return 内容标签列表
     */
    List<ContentTag> getContentTags(Long contentId);

    /**
     * 获取内容的标签数量
     * 
     * @param contentId 内容ID
     * @return 标签数量
     */
    Integer getContentTagCount(Long contentId);

    /**
     * 检查内容是否包含指定标签
     * 
     * @param contentId 内容ID
     * @param tagId 标签ID
     * @return 是否包含
     */
    boolean hasContentTag(Long contentId, Long tagId);

    /**
     * 批量检查内容是否包含指定标签
     * 
     * @param contentId 内容ID
     * @param tagIds 标签ID列表
     * @return 标签包含状态映射（tagId -> hasTag）
     */
    Map<Long, Boolean> batchCheckContentTags(Long contentId, List<Long> tagIds);

    // =================== 基于标签的内容查询 ===================

    /**
     * 根据单个标签查询内容ID列表
     * 
     * @param tagId 标签ID
     * @param limit 数量限制
     * @return 内容ID列表
     */
    List<Long> getContentsByTag(Long tagId, Integer limit);

    /**
     * 根据多个标签查询内容ID列表（AND关系）
     * 
     * @param tagIds 标签ID列表
     * @param limit 数量限制
     * @return 内容ID列表
     */
    List<Long> getContentsByTagsAnd(List<Long> tagIds, Integer limit);

    /**
     * 根据多个标签查询内容ID列表（OR关系）
     * 
     * @param tagIds 标签ID列表
     * @param limit 数量限制
     * @return 内容ID列表
     */
    List<Long> getContentsByTagsOr(List<Long> tagIds, Integer limit);

    /**
     * 获取标签下的内容数量
     * 
     * @param tagId 标签ID
     * @return 内容数量
     */
    Long countContentsByTag(Long tagId);

    /**
     * 获取标签的最新内容
     * 
     * @param tagId 标签ID
     * @param days 天数范围
     * @param limit 数量限制
     * @return 最新内容ID列表
     */
    List<Long> getTagLatestContents(Long tagId, Integer days, Integer limit);

    /**
     * 获取标签的热门内容
     * 
     * @param tagId 标签ID
     * @param days 天数范围
     * @param limit 数量限制
     * @return 热门内容ID列表
     */
    List<Long> getTagHotContents(Long tagId, Integer days, Integer limit);

    // =================== 基于用户标签的内容推荐 ===================

    /**
     * 基于用户关注标签推荐内容
     * 
     * @param userId 用户ID
     * @param excludeContentIds 排除的内容ID列表
     * @param limit 推荐数量限制
     * @return 推荐内容ID列表
     */
    List<Long> getRecommendContentsByUserTags(Long userId, List<Long> excludeContentIds, Integer limit);

    /**
     * 基于用户关注标签推荐热门内容
     * 
     * @param userId 用户ID
     * @param days 天数范围
     * @param excludeContentIds 排除的内容ID列表
     * @param limit 推荐数量限制
     * @return 推荐热门内容ID列表
     */
    List<Long> getRecommendHotContentsByUserTags(Long userId, Integer days, List<Long> excludeContentIds, Integer limit);

    /**
     * 基于内容标签相似度推荐相关内容
     * 
     * @param contentId 内容ID
     * @param limit 推荐数量限制
     * @return 相关内容ID列表
     */
    List<Long> getRelatedContentsByTags(Long contentId, Integer limit);

    /**
     * 基于用户兴趣相似度推荐内容
     * 
     * @param userId 用户ID
     * @param similarUserIds 相似用户ID列表
     * @param excludeContentIds 排除的内容ID列表
     * @param limit 推荐数量限制
     * @return 推荐内容ID列表
     */
    List<Long> getRecommendContentsBySimilarUsers(Long userId, List<Long> similarUserIds, 
                                                 List<Long> excludeContentIds, Integer limit);

    // =================== 内容标签统计分析 ===================

    /**
     * 获取标签的内容统计信息
     * 
     * @param tagId 标签ID
     * @return 统计信息
     */
    Map<String, Object> getTagContentStatistics(Long tagId);

    /**
     * 获取标签内容的时间分布
     * 
     * @param tagId 标签ID
     * @param days 天数范围
     * @return 时间分布映射（日期 -> 内容数量）
     */
    Map<String, Integer> getTagContentTimeDistribution(Long tagId, Integer days);

    /**
     * 获取内容标签的权重分布
     * 
     * @param contentId 内容ID（可选）
     * @return 权重分布统计
     */
    Map<String, Integer> getContentTagWeightDistribution(Long contentId);

    /**
     * 获取标签共现分析
     * 
     * @param tagId 标签ID
     * @param limit 数量限制
     * @return 共现标签统计
     */
    Map<Long, Integer> getTagCooccurrenceAnalysis(Long tagId, Integer limit);

    // =================== 分页查询接口 ===================

    /**
     * 分页查询内容标签关系
     * 
     * @param request 查询请求
     * @return 分页内容标签关系列表
     */
    PageResponse<ContentTag> queryContentTags(ContentTagQueryRequest request);

    /**
     * 分页查询标签的内容列表
     * 
     * @param tagId 标签ID
     * @param request 查询请求
     * @return 分页内容标签关系列表
     */
    PageResponse<ContentTag> queryTagContents(Long tagId, ContentTagQueryRequest request);

    // =================== 智能推荐功能 ===================

    /**
     * 为内容智能推荐标签
     * 
     * @param contentId 内容ID
     * @param contentText 内容文本（可选，用于分析）
     * @param excludeTagIds 排除的标签ID列表
     * @param limit 推荐数量限制
     * @return 推荐标签ID列表
     */
    List<Long> getRecommendTagsForContent(Long contentId, String contentText, 
                                         List<Long> excludeTagIds, Integer limit);

    /**
     * 获取内容的相关内容（基于标签相似度）
     * 
     * @param contentId 内容ID
     * @param limit 推荐数量限制
     * @return 相关内容ID列表
     */
    List<Long> getRelatedContents(Long contentId, Integer limit);

    // =================== 权限验证 ===================

    /**
     * 检查内容是否可以添加更多标签
     * 
     * @param contentId 内容ID
     * @return 是否可以添加更多标签
     */
    boolean canContentHaveMoreTags(Long contentId);

    /**
     * 检查内容是否可以添加指定标签
     * 
     * @param contentId 内容ID
     * @param tagId 标签ID
     * @return 是否可以添加
     */
    boolean canContentHaveTag(Long contentId, Long tagId);

    /**
     * 获取内容还可以添加的标签数量
     * 
     * @param contentId 内容ID
     * @return 剩余可添加数量
     */
    Integer getContentRemainingTagCount(Long contentId);

    // =================== 数据清理和管理 ===================

    /**
     * 清理内容的无效标签
     * 
     * @param contentId 内容ID（为空时清理所有内容）
     * @return 清理的记录数
     */
    Integer cleanupInvalidContentTags(Long contentId);

    /**
     * 删除内容的所有标签
     * 
     * @param contentId 内容ID
     * @return 删除是否成功
     */
    boolean deleteAllContentTags(Long contentId);

    /**
     * 删除标签的所有内容关联
     * 
     * @param tagId 标签ID
     * @return 删除是否成功
     */
    boolean deleteAllTagContents(Long tagId);

    /**
     * 批量更新内容标签的权重影响
     * 
     * @param tagIds 标签ID列表
     * @return 更新是否成功
     */
    boolean updateContentTagWeights(List<Long> tagIds);

    // =================== 性能优化接口 ===================

    /**
     * 批量获取多个内容的标签
     * 
     * @param contentIds 内容ID列表
     * @return 内容标签映射
     */
    Map<Long, List<ContentTag>> batchGetContentTags(List<Long> contentIds);

    /**
     * 批量获取多个标签的内容数量
     * 
     * @param tagIds 标签ID列表
     * @return 标签内容数量映射
     */
    Map<Long, Long> batchGetTagContentCounts(List<Long> tagIds);
}