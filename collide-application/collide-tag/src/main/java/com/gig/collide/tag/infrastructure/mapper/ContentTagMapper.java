package com.gig.collide.tag.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.tag.domain.entity.ContentTag;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 内容标签Mapper接口
 * 
 * @author GIG Team
 * @version 1.0.0
 * @since 2024-12-19
 */
@Mapper
public interface ContentTagMapper extends BaseMapper<ContentTag> {

    /**
     * 根据内容ID查询关联的标签ID列表
     * 
     * @param contentId 内容ID
     * @return 标签ID列表
     */
    @Select("SELECT tag_id FROM t_content_tag WHERE content_id = #{contentId}")
    List<Long> selectTagIdsByContentId(@Param("contentId") Long contentId);

    /**
     * 根据标签ID查询关联的内容ID列表
     * 
     * @param tagId 标签ID
     * @return 内容ID列表
     */
    @Select("SELECT content_id FROM t_content_tag WHERE tag_id = #{tagId}")
    List<Long> selectContentIdsByTagId(@Param("tagId") Long tagId);

    /**
     * 根据内容ID和标签ID查询关联记录
     * 
     * @param contentId 内容ID
     * @param tagId 标签ID
     * @return 内容标签关联记录
     */
    @Select("SELECT * FROM t_content_tag WHERE content_id = #{contentId} AND tag_id = #{tagId} LIMIT 1")
    ContentTag selectByContentIdAndTagId(@Param("contentId") Long contentId, @Param("tagId") Long tagId);

    /**
     * 根据内容ID删除所有关联标签
     * 
     * @param contentId 内容ID
     * @return 删除行数
     */
    @Delete("DELETE FROM t_content_tag WHERE content_id = #{contentId}")
    int deleteByContentId(@Param("contentId") Long contentId);

    /**
     * 根据标签ID删除所有关联内容
     * 
     * @param tagId 标签ID
     * @return 删除行数
     */
    @Delete("DELETE FROM t_content_tag WHERE tag_id = #{tagId}")
    int deleteByTagId(@Param("tagId") Long tagId);

    /**
     * 根据内容ID和标签ID列表查询关联记录
     * 
     * @param contentId 内容ID
     * @param tagIds 标签ID列表
     * @return 内容标签关联记录列表
     */
    @Select("<script>" +
            "SELECT * FROM t_content_tag WHERE content_id = #{contentId} AND tag_id IN " +
            "<foreach collection='tagIds' item='tagId' open='(' separator=',' close=')'>" +
            "#{tagId}" +
            "</foreach>" +
            "</script>")
    List<ContentTag> selectByContentIdAndTagIds(@Param("contentId") Long contentId, @Param("tagIds") List<Long> tagIds);

    /**
     * 根据标签ID列表查询关联的内容ID列表
     * 
     * @param tagIds 标签ID列表
     * @return 内容ID列表
     */
    @Select("<script>" +
            "SELECT DISTINCT content_id FROM t_content_tag WHERE tag_id IN " +
            "<foreach collection='tagIds' item='tagId' open='(' separator=',' close=')'>" +
            "#{tagId}" +
            "</foreach>" +
            "</script>")
    List<Long> selectContentIdsByTagIds(@Param("tagIds") List<Long> tagIds);

    /**
     * 统计内容的标签数量
     * 
     * @param contentId 内容ID
     * @return 标签数量
     */
    @Select("SELECT COUNT(*) FROM t_content_tag WHERE content_id = #{contentId}")
    Long countByContentId(@Param("contentId") Long contentId);

    /**
     * 统计标签的内容数量
     * 
     * @param tagId 标签ID
     * @return 内容数量
     */
    @Select("SELECT COUNT(*) FROM t_content_tag WHERE tag_id = #{tagId}")
    Long countByTagId(@Param("tagId") Long tagId);

    /**
     * 批量插入内容标签关联
     * 
     * @param contentTags 内容标签关联列表
     * @return 插入行数
     */
    int batchInsert(@Param("list") List<ContentTag> contentTags);
} 