package com.gig.collide.tag.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.tag.domain.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 标签Mapper接口 - 简洁版
 *
 * @author GIG Team
 * @version 2.0.0
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 根据类型查询标签列表
     */
    List<Tag> selectByTagType(@Param("tagType") String tagType);

    /**
     * 按名称模糊搜索标签
     */
    List<Tag> searchByName(@Param("keyword") String keyword, @Param("limit") Integer limit);

    /**
     * 获取热门标签（按使用次数排序）
     */
    List<Tag> selectHotTags(@Param("limit") Integer limit);

    /**
     * 增加标签使用次数
     */
    int increaseUsageCount(@Param("tagId") Long tagId);

    /**
     * 检查标签名称是否存在
     */
    int countByNameAndType(@Param("name") String name, @Param("tagType") String tagType);
} 