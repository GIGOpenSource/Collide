package com.gig.collide.api.tag;

import com.gig.collide.api.tag.request.ContentTagRequest;
import com.gig.collide.api.tag.response.ContentTagResponse;
import com.gig.collide.web.vo.Result;

import java.util.List;
import java.util.Map;

/**
 * 内容标签门面服务接口
 * 专注于内容与标签的关联管理
 *
 * @author GIG Team
 * @version 3.0.0
 * @since 2024-01-30
 */
public interface ContentTagFacadeService {

    // =================== 内容标签基础操作 ===================

    /**
     * 为内容添加标签
     * 包含重复检查、标签验证
     */
    Result<ContentTagResponse> addContentTag(ContentTagRequest request);

    /**
     * 移除内容标签
     */
    Result<Void> removeContentTag(Long contentId, Long tagId, Long operatorId);

    /**
     * 获取内容的标签列表
     */
    Result<List<ContentTagResponse>> getContentTags(Long contentId);

    /**
     * 获取标签的内容列表
     */
    Result<List<ContentTagResponse>> getTagContents(Long tagId);

    // =================== 批量操作 ===================

    /**
     * 批量为内容添加标签
     */
    Result<Integer> batchAddContentTags(Long contentId, List<Long> tagIds, Long operatorId);

    /**
     * 批量移除内容标签
     */
    Result<Integer> batchRemoveContentTags(Long contentId, List<Long> tagIds, Long operatorId);

    /**
     * 替换内容的所有标签
     */
    Result<Integer> replaceContentTags(Long contentId, List<Long> newTagIds, Long operatorId);

    /**
     * 复制内容标签
     */
    Result<Integer> copyContentTags(Long sourceContentId, Long targetContentId, Long operatorId);

    // =================== 关联查询 ===================

    /**
     * 获取内容的相关内容（基于共同标签）
     */
    Result<List<Long>> getRelatedContents(Long contentId, Integer limit);

    /**
     * 检查两个内容是否有共同标签
     */
    Result<Boolean> hasCommonTags(Long contentId1, Long contentId2);

    /**
     * 获取两个内容的共同标签数量
     */
    Result<Integer> getCommonTagCount(Long contentId1, Long contentId2);

    // =================== 统计功能 ===================

    /**
     * 统计内容标签数量
     */
    Result<Integer> countContentTags(Long contentId);

    /**
     * 统计标签使用的内容数量
     */
    Result<Integer> countTagContents(Long tagId);

    /**
     * 获取最新的内容标签关联
     */
    Result<List<Map<String, Object>>> getRecentContentTags(Integer limit);

    /**
     * 获取内容标签关联摘要信息
     */
    Result<List<Map<String, Object>>> getContentTagSummary(List<Long> contentIds);

    // =================== 内容推荐 ===================

    /**
     * 推荐内容适合的标签
     * 基于内容特征和相似内容的标签
     */
    Result<List<Map<String, Object>>> recommendTagsForContent(Long contentId, Integer limit);

    /**
     * 获取内容标签的统计分析
     */
    Result<Map<String, Object>> getContentTagAnalysis(Long contentId);

    /**
     * 获取内容相关的完整标签信息
     * 包含标签详情和使用统计
     */
    Result<List<Map<String, Object>>> getContentTagsWithStats(Long contentId);

    // =================== 数据维护 ===================

    /**
     * 清理无效的内容标签关联
     * 清理不存在的内容或标签的关联数据
     */
    Result<Integer> cleanupInvalidContentTags(Long operatorId);

    /**
     * 内容标签系统健康检查
     */
    Result<String> healthCheck();
}