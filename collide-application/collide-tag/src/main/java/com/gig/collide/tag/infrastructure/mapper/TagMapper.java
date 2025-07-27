package com.gig.collide.tag.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.tag.infrastructure.entity.TagEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 标签 Mapper 接口
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
@Mapper
public interface TagMapper extends BaseMapper<TagEntity> {

    /**
     * 根据类型获取标签列表
     *
     * @param tagType 标签类型
     * @param status  状态
     * @return 标签列表
     */
    List<TagEntity> selectByTagType(@Param("tagType") String tagType, @Param("status") String status);

    /**
     * 获取热门标签列表
     *
     * @param tagType 标签类型（可选）
     * @param limit   限制数量
     * @return 热门标签列表
     */
    List<TagEntity> selectHotTags(@Param("tagType") String tagType, @Param("limit") Integer limit);

    /**
     * 搜索标签
     *
     * @param keyword 关键词
     * @param tagType 标签类型（可选）
     * @return 标签列表
     */
    List<TagEntity> searchTags(@Param("keyword") String keyword, @Param("tagType") String tagType);

    /**
     * 分页查询标签
     *
     * @param page    分页参数
     * @param tagType 标签类型
     * @param status  状态
     * @param keyword 搜索关键词
     * @return 分页结果
     */
    IPage<TagEntity> selectTagPage(IPage<TagEntity> page,
                                   @Param("tagType") String tagType,
                                   @Param("status") String status,
                                   @Param("keyword") String keyword);

    /**
     * 增加标签使用次数
     *
     * @param tagId 标签ID
     * @param count 增加数量
     * @return 影响行数
     */
    int increaseUsageCount(@Param("tagId") Long tagId, @Param("count") Long count);

    /**
     * 更新标签热度分数
     *
     * @param tagId     标签ID
     * @param heatScore 热度分数
     * @return 影响行数
     */
    int updateHeatScore(@Param("tagId") Long tagId, @Param("heatScore") Double heatScore);

    /**
     * 批量查询标签
     *
     * @param tagIds 标签ID列表
     * @return 标签列表
     */
    List<TagEntity> selectByIds(@Param("tagIds") List<Long> tagIds);

    /**
     * 根据名称和类型查询标签
     *
     * @param name    标签名称
     * @param tagType 标签类型
     * @return 标签实体
     */
    TagEntity selectByNameAndType(@Param("name") String name, @Param("tagType") String tagType);
} 