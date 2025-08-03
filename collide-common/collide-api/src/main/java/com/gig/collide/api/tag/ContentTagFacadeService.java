package com.gig.collide.api.tag;

import com.gig.collide.api.tag.request.ContentTagQueryRequest;
import com.gig.collide.api.tag.response.TagResponse;
import com.gig.collide.api.tag.response.ContentTagResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import java.util.List;
import java.util.Map;

/**
 * 内容标签门面服务接口 - 对应 t_content_tag 表
 * 支持内容打标签功能和基于标签的内容推荐
 * 
 * @author GIG Team
 * @version 1.0.0
 */
public interface ContentTagFacadeService {

    // =================== 内容标签管理 ===================

    /**
     * 为内容添加标签
     * 
     * @param contentId 内容ID
     * @param tagId 标签ID
     * @return 添加结果
     */
    Result<Void> addContentTag(Long contentId, Long tagId);

    /**
     * 移除内容的标签
     * 
     * @param contentId 内容ID
     * @param tagId 标签ID
     * @return 移除结果
     */
    Result<Void> removeContentTag(Long contentId, Long tagId);

    /**
     * 批量为内容添加标签
     * 
     * @param contentId 内容ID
     * @param tagIds 标签ID列表（最多9个）
     * @return 添加结果（成功的标签ID列表）
     */
    Result<List<Long>> batchAddContentTags(Long contentId, List<Long> tagIds);

    /**
     * 批量移除内容的标签
     * 
     * @param contentId 内容ID
     * @param tagIds 标签ID列表
     * @return 移除结果
     */
    Result<Void> batchRemoveContentTags(Long contentId, List<Long> tagIds);

    /**
     * 替换内容的所有标签
     * 
     * @param contentId 内容ID
     * @param tagIds 新的标签ID列表（最多9个）
     * @return 替换结果
     */
    Result<Void> replaceContentTags(Long contentId, List<Long> tagIds);

    // =================== 内容标签查询 ===================

    /**
     * 获取内容的标签列表
     * 
     * @param contentId 内容ID
     * @return 标签列表
     */
    Result<List<TagResponse>> getContentTags(Long contentId);

    /**
     * 获取内容的标签数量
     * 
     * @param contentId 内容ID
     * @return 标签数量
     */
    Result<Integer> getContentTagCount(Long contentId);

    /**
     * 检查内容是否包含指定标签
     * 
     * @param contentId 内容ID
     * @param tagId 标签ID
     * @return 是否包含
     */
    Result<Boolean> hasContentTag(Long contentId, Long tagId);

    /**
     * 批量检查内容是否包含指定标签
     * 
     * @param contentId 内容ID
     * @param tagIds 标签ID列表
     * @return 标签包含状态映射（tagId -> hasTag）
     */
    Result<Map<Long, Boolean>> batchCheckContentTags(Long contentId, List<Long> tagIds);

    // =================== 基于标签的内容查询 ===================

    /**
     * 根据单个标签查询内容
     * 
     * @param tagId 标签ID
     * @param request 查询请求
     * @return 分页内容ID列表
     */
    Result<PageResponse<Long>> getContentsByTag(Long tagId, ContentTagQueryRequest request);

    /**
     * 根据多个标签查询内容（AND关系）
     * 
     * @param tagIds 标签ID列表
     * @param request 查询请求
     * @return 分页内容ID列表
     */
    Result<PageResponse<Long>> getContentsByTagsAnd(List<Long> tagIds, ContentTagQueryRequest request);

    /**
     * 根据多个标签查询内容（OR关系）
     * 
     * @param tagIds 标签ID列表
     * @param request 查询请求
     * @return 分页内容ID列表
     */
    Result<PageResponse<Long>> getContentsByTagsOr(List<Long> tagIds, ContentTagQueryRequest request);

    /**
     * 获取标签下的热门内容
     * 
     * @param tagId 标签ID
     * @param days 天数范围（默认7天）
     * @param limit 返回数量限制（默认20）
     * @return 热门内容ID列表
     */
    Result<List<Long>> getHotContentsByTag(Long tagId, Integer days, Integer limit);

    // =================== 基于用户标签的内容推荐 ===================

    /**
     * 基于用户关注标签推荐内容
     * 
     * @param userId 用户ID
     * @param limit 推荐数量限制（默认20）
     * @return 推荐内容ID列表
     */
    Result<List<Long>> getRecommendContentsByUserTags(Long userId, Integer limit);

    /**
     * 基于用户关注标签推荐热门内容
     * 
     * @param userId 用户ID
     * @param days 天数范围（默认7天）
     * @param limit 推荐数量限制（默认20）
     * @return 推荐热门内容ID列表
     */
    Result<List<Long>> getRecommendHotContentsByUserTags(Long userId, Integer days, Integer limit);

    /**
     * 基于用户兴趣相似度推荐内容
     * 
     * @param userId 用户ID
     * @param limit 推荐数量限制（默认20）
     * @return 推荐内容ID列表
     */
    Result<List<Long>> getRecommendContentsBySimilarUsers(Long userId, Integer limit);

    // =================== 内容标签统计分析 ===================

    /**
     * 获取标签的内容统计信息
     * 
     * @param tagId 标签ID
     * @return 统计信息
     */
    Result<Map<String, Object>> getTagContentStatistics(Long tagId);

    /**
     * 获取标签的最新内容
     * 
     * @param tagId 标签ID
     * @param days 天数范围（默认7天）
     * @param limit 返回数量限制（默认10）
     * @return 最新内容ID列表
     */
    Result<List<Long>> getTagLatestContents(Long tagId, Integer days, Integer limit);

    /**
     * 获取标签内容的时间分布
     * 
     * @param tagId 标签ID
     * @param days 天数范围（默认30天）
     * @return 时间分布映射（日期 -> 内容数量）
     */
    Result<Map<String, Integer>> getTagContentTimeDistribution(Long tagId, Integer days);

    // =================== 分页查询接口 ===================

    /**
     * 分页查询内容标签关系
     * 
     * @param request 查询请求
     * @return 分页内容标签关系列表
     */
    Result<PageResponse<ContentTagResponse>> queryContentTags(ContentTagQueryRequest request);

    /**
     * 分页查询标签的内容列表（带详情）
     * 
     * @param tagId 标签ID
     * @param request 查询请求
     * @return 分页内容标签关系列表
     */
    Result<PageResponse<ContentTagResponse>> queryTagContents(Long tagId, ContentTagQueryRequest request);

    // =================== 智能推荐功能 ===================

    /**
     * 为内容智能推荐标签
     * 
     * @param contentId 内容ID
     * @param contentText 内容文本（可选，用于分析）
     * @param limit 推荐数量限制（默认5）
     * @return 推荐标签列表
     */
    Result<List<TagResponse>> getRecommendTagsForContent(Long contentId, String contentText, Integer limit);

    /**
     * 基于内容相似度推荐相关内容
     * 
     * @param contentId 内容ID
     * @param limit 推荐数量限制（默认10）
     * @return 相关内容ID列表
     */
    Result<List<Long>> getRelatedContentsByTags(Long contentId, Integer limit);

    // =================== 管理功能 ===================

    /**
     * 清理内容的无效标签（针对已删除的标签）
     * 
     * @param contentId 内容ID（为空时清理所有内容）
     * @return 清理结果
     */
    Result<Integer> cleanupInvalidContentTags(Long contentId);

    /**
     * 获取内容添加标签的权限检查
     * 
     * @param contentId 内容ID
     * @param tagId 标签ID
     * @return 是否可以添加
     */
    Result<Boolean> canContentHaveTag(Long contentId, Long tagId);

    /**
     * 批量更新内容标签的权重影响
     * 
     * @param tagIds 标签ID列表
     * @return 更新结果
     */
    Result<Void> updateContentTagWeights(List<Long> tagIds);
}