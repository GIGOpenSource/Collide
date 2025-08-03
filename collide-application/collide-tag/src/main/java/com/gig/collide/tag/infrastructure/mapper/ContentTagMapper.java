package com.gig.collide.tag.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.tag.domain.entity.ContentTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
} 