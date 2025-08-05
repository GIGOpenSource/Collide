package com.gig.collide.tag.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.tag.domain.entity.ContentTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 内容标签Mapper接口 - 简洁版
 *
 * @author GIG Team
 * @version 2.0.0
 */
@Mapper
public interface ContentTagMapper extends BaseMapper<ContentTag> {

    /**
     * 获取内容的标签列表
     */
    List<ContentTag> selectByContentId(@Param("contentId") Long contentId);

    /**
     * 获取标签的内容列表
     */
    List<ContentTag> selectByTagId(@Param("tagId") Long tagId);

    /**
     * 检查内容是否已有此标签
     */
    int countByContentIdAndTagId(@Param("contentId") Long contentId, @Param("tagId") Long tagId);

    /**
     * 批量删除内容的所有标签
     */
    int deleteByContentId(@Param("contentId") Long contentId);

    /**
     * 批量删除标签的所有关联
     */
    int deleteByTagId(@Param("tagId") Long tagId);

    /**
     * 统计标签被使用的内容数量
     */
    int countContentsByTagId(@Param("tagId") Long tagId);

    /**
     * 统计内容的标签数量
     */
    int countTagsByContentId(@Param("contentId") Long contentId);

    /**
     * 批量获取内容标签关联信息（覆盖索引优化）
     */
    List<Map<String, Object>> getContentTagSummary(@Param("contentIds") List<Long> contentIds);

    /**
     * 获取最新的内容标签关联（覆盖索引优化）
     */
    List<Map<String, Object>> getRecentContentTags(@Param("limit") Integer limit);
} 