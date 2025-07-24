package com.gig.collide.business.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.business.domain.tag.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 标签 Mapper 接口
 *
 * @author GIG Team
 * @version 1.0.0
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 根据标签类型查询标签列表
     */
    List<Tag> selectByTagType(@Param("tagType") String tagType);

    /**
     * 根据分类ID查询标签列表
     */
    List<Tag> selectByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 根据标签名称搜索标签
     */
    List<Tag> searchByName(@Param("keyword") String keyword, @Param("tagType") String tagType);

    /**
     * 获取热门标签（按热度分数排序）
     */
    List<Tag> selectHotTags(@Param("tagType") String tagType, @Param("limit") Integer limit);

    /**
     * 更新标签使用次数
     */
    int updateUsageCount(@Param("tagId") Long tagId, @Param("delta") Integer delta);

    /**
     * 更新标签热度分数
     */
    int updateHeatScore(@Param("tagId") Long tagId, @Param("heatScore") Double heatScore);

    /**
     * 批量更新标签状态
     */
    int updateStatusBatch(@Param("tagIds") List<Long> tagIds, @Param("status") String status);

    /**
     * 查询推荐标签（基于用户行为）
     */
    List<Tag> selectRecommendTags(@Param("userId") Long userId, @Param("limit") Integer limit);
} 