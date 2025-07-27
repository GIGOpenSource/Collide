package com.gig.collide.tag.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.tag.infrastructure.entity.ContentTagEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 内容标签关联 Mapper 接口
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
@Mapper
public interface ContentTagMapper extends BaseMapper<ContentTagEntity> {

    /**
     * 根据内容ID获取标签关联列表
     *
     * @param contentId 内容ID
     * @return 内容标签关联列表
     */
    List<ContentTagEntity> selectByContentId(@Param("contentId") Long contentId);

    /**
     * 根据标签ID获取内容关联列表
     *
     * @param tagId 标签ID
     * @param limit 限制数量
     * @return 内容标签关联列表
     */
    List<ContentTagEntity> selectByTagId(@Param("tagId") Long tagId, @Param("limit") Integer limit);

    /**
     * 批量插入内容标签关联
     *
     * @param entities 内容标签关联列表
     * @return 影响行数
     */
    int batchInsert(@Param("entities") List<ContentTagEntity> entities);

    /**
     * 批量删除内容的标签关联
     *
     * @param contentId 内容ID
     * @param tagIds    标签ID列表
     * @return 影响行数
     */
    int batchDeleteByContentIdAndTagIds(@Param("contentId") Long contentId, @Param("tagIds") List<Long> tagIds);

    /**
     * 删除内容的所有标签关联
     *
     * @param contentId 内容ID
     * @return 影响行数
     */
    int deleteAllByContentId(@Param("contentId") Long contentId);

    /**
     * 删除标签的所有内容关联
     *
     * @param tagId 标签ID
     * @return 影响行数
     */
    int deleteAllByTagId(@Param("tagId") Long tagId);

    /**
     * 查询指定内容和标签的关联记录
     *
     * @param contentId 内容ID
     * @param tagId     标签ID
     * @return 内容标签关联实体
     */
    ContentTagEntity selectByContentIdAndTagId(@Param("contentId") Long contentId, @Param("tagId") Long tagId);

    /**
     * 统计标签的关联内容数量
     *
     * @param tagId 标签ID
     * @return 内容数量
     */
    Long countByTagId(@Param("tagId") Long tagId);

    /**
     * 统计内容的关联标签数量
     *
     * @param contentId 内容ID
     * @return 标签数量
     */
    Long countByContentId(@Param("contentId") Long contentId);
} 