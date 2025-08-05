package com.gig.collide.tag.domain.service;

import com.gig.collide.tag.domain.entity.ContentTag;

import java.util.List;
import java.util.Map;

/**
 * 内容标签服务接口 - 严格对应ContentTagMapper
 * 基于ContentTagMapper的所有方法，提供内容标签关联的业务逻辑
 *
 * @author GIG Team
 * @version 3.0.0
 */
public interface ContentTagService {

    // =================== 基础CRUD操作（继承自BaseMapper） ===================

    /**
     * 创建内容标签关联
     */
    ContentTag createContentTag(ContentTag contentTag);

    /**
     * 更新内容标签关联
     */
    ContentTag updateContentTag(ContentTag contentTag);

    /**
     * 根据ID删除内容标签关联
     */
    boolean deleteContentTagById(Long id);

    /**
     * 根据ID查询内容标签关联
     */
    ContentTag getContentTagById(Long id);

    /**
     * 查询所有内容标签关联
     */
    List<ContentTag> getAllContentTags();

    // =================== 核心查询方法（对应Mapper自定义方法） ===================

    /**
     * 获取内容的标签列表
     * 对应Mapper: selectByContentId
     */
    List<ContentTag> selectByContentId(Long contentId);

    /**
     * 获取标签的内容列表
     * 对应Mapper: selectByTagId
     */
    List<ContentTag> selectByTagId(Long tagId);

    // =================== 统计和计数方法 ===================

    /**
     * 检查内容是否已有此标签
     * 对应Mapper: countByContentIdAndTagId
     */
    boolean existsByContentIdAndTagId(Long contentId, Long tagId);

    /**
     * 统计标签被使用的内容数量
     * 对应Mapper: countContentsByTagId
     */
    int countContentsByTagId(Long tagId);

    /**
     * 统计内容的标签数量
     * 对应Mapper: countTagsByContentId
     */
    int countTagsByContentId(Long contentId);

    /**
     * 批量获取内容标签关联信息（覆盖索引优化）
     * 对应Mapper: getContentTagSummary
     */
    List<Map<String, Object>> getContentTagSummary(List<Long> contentIds);

    /**
     * 获取最新的内容标签关联（覆盖索引优化）
     * 对应Mapper: getRecentContentTags
     */
    List<Map<String, Object>> getRecentContentTags(Integer limit);

    // =================== 删除操作方法 ===================

    /**
     * 批量删除内容的所有标签
     * 对应Mapper: deleteByContentId
     */
    int deleteByContentId(Long contentId);

    /**
     * 批量删除标签的所有关联
     * 对应Mapper: deleteByTagId
     */
    int deleteByTagId(Long tagId);

    // =================== 业务逻辑方法 ===================

    /**
     * 为内容添加标签（带重复检查）
     */
    ContentTag addContentTagSafely(Long contentId, Long tagId);

    /**
     * 移除内容标签
     */
    boolean removeContentTag(Long contentId, Long tagId);

    /**
     * 批量为内容添加标签
     */
    int batchAddContentTags(Long contentId, List<Long> tagIds);

    /**
     * 批量移除内容标签
     */
    int batchRemoveContentTags(Long contentId, List<Long> tagIds);

    /**
     * 替换内容的所有标签
     */
    int replaceContentTags(Long contentId, List<Long> newTagIds);

    /**
     * 复制内容标签到另一个内容
     */
    int copyContentTags(Long sourceContentId, Long targetContentId);

    /**
     * 获取内容标签ID列表
     */
    List<Long> getContentTagIds(Long contentId);

    /**
     * 获取标签关联的内容ID列表
     */
    List<Long> getTagContentIds(Long tagId);

    /**
     * 检查两个内容是否有共同标签
     */
    boolean hasCommonTags(Long contentId1, Long contentId2);

    /**
     * 获取两个内容的共同标签数量
     */
    int getCommonTagCount(Long contentId1, Long contentId2);

    /**
     * 获取内容的相关内容（基于共同标签）
     */
    List<Long> getRelatedContentIds(Long contentId, Integer limit);

    /**
     * 清理无效的内容标签关联（内容或标签不存在）
     */
    int cleanupInvalidContentTags();
}