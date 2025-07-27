package com.gig.collide.tag.domain.repository;

import com.gig.collide.tag.infrastructure.entity.ContentTagEntity;

import java.util.List;

/**
 * 内容标签关联仓储接口
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
public interface ContentTagRepository {

    /**
     * 保存内容标签关联
     *
     * @param entity 内容标签关联实体
     * @return 内容标签关联实体
     */
    ContentTagEntity save(ContentTagEntity entity);

    /**
     * 根据内容ID获取标签关联列表
     *
     * @param contentId 内容ID
     * @return 内容标签关联列表
     */
    List<ContentTagEntity> findByContentId(Long contentId);

    /**
     * 根据标签ID获取内容关联列表
     *
     * @param tagId 标签ID
     * @param limit 限制数量
     * @return 内容标签关联列表
     */
    List<ContentTagEntity> findByTagId(Long tagId, Integer limit);

    /**
     * 批量保存内容标签关联
     *
     * @param entities 内容标签关联列表
     * @return 是否成功
     */
    boolean batchSave(List<ContentTagEntity> entities);

    /**
     * 删除内容标签关联
     *
     * @param contentId 内容ID
     * @param tagId     标签ID
     * @return 是否成功
     */
    boolean deleteByContentIdAndTagId(Long contentId, Long tagId);

    /**
     * 批量删除内容的标签关联
     *
     * @param contentId 内容ID
     * @param tagIds    标签ID列表
     * @return 是否成功
     */
    boolean batchDeleteByContentIdAndTagIds(Long contentId, List<Long> tagIds);

    /**
     * 删除内容的所有标签关联
     *
     * @param contentId 内容ID
     * @return 是否成功
     */
    boolean deleteAllByContentId(Long contentId);

    /**
     * 删除标签的所有内容关联
     *
     * @param tagId 标签ID
     * @return 是否成功
     */
    boolean deleteAllByTagId(Long tagId);

    /**
     * 查询指定内容和标签的关联记录
     *
     * @param contentId 内容ID
     * @param tagId     标签ID
     * @return 内容标签关联实体
     */
    ContentTagEntity findByContentIdAndTagId(Long contentId, Long tagId);

    /**
     * 统计标签的关联内容数量
     *
     * @param tagId 标签ID
     * @return 内容数量
     */
    Long countByTagId(Long tagId);

    /**
     * 统计内容的关联标签数量
     *
     * @param contentId 内容ID
     * @return 标签数量
     */
    Long countByContentId(Long contentId);

    /**
     * 检查内容标签关联是否存在
     *
     * @param contentId 内容ID
     * @param tagId     标签ID
     * @return 是否存在
     */
    boolean existsByContentIdAndTagId(Long contentId, Long tagId);
} 