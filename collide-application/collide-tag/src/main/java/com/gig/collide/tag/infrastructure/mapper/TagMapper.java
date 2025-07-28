package com.gig.collide.tag.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.tag.domain.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

/**
 * 标签Mapper接口
 * 
 * @author GIG Team
 * @version 1.0.0
 * @since 2024-12-19
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 根据标签类型查询标签列表
     * 
     * @param tagType 标签类型
     * @param status 状态
     * @return 标签列表
     */
    @Select("SELECT * FROM t_tag WHERE tag_type = #{tagType} AND status = #{status} ORDER BY sort ASC, heat_score DESC")
    List<Tag> selectByTagType(@Param("tagType") String tagType, @Param("status") String status);

    /**
     * 查询热门标签
     * 
     * @param tagType 标签类型（可选）
     * @param status 状态
     * @param limit 限制数量
     * @return 热门标签列表
     */
    @Select("<script>" +
            "SELECT * FROM t_tag WHERE status = #{status} " +
            "<if test='tagType != null and tagType != \"\"'>" +
            "AND tag_type = #{tagType} " +
            "</if>" +
            "ORDER BY heat_score DESC, usage_count DESC " +
            "LIMIT #{limit}" +
            "</script>")
    List<Tag> selectHotTags(@Param("tagType") String tagType, @Param("status") String status, @Param("limit") Integer limit);

    /**
     * 搜索标签
     * 
     * @param keyword 关键词
     * @param tagType 标签类型（可选）
     * @param status 状态
     * @return 标签列表
     */
    @Select("<script>" +
            "SELECT * FROM t_tag WHERE status = #{status} " +
            "AND (name LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%')) " +
            "<if test='tagType != null and tagType != \"\"'>" +
            "AND tag_type = #{tagType} " +
            "</if>" +
            "ORDER BY heat_score DESC, usage_count DESC" +
            "</script>")
    List<Tag> searchTags(@Param("keyword") String keyword, @Param("tagType") String tagType, @Param("status") String status);

    /**
     * 分页查询标签（支持多条件）
     * 
     * @param page 分页参数
     * @param tagType 标签类型（可选）
     * @param categoryId 分类ID（可选）
     * @param status 状态（可选）
     * @param keyword 关键词（可选）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT * FROM t_tag WHERE 1=1 " +
            "<if test='tagType != null and tagType != \"\"'>" +
            "AND tag_type = #{tagType} " +
            "</if>" +
            "<if test='categoryId != null'>" +
            "AND category_id = #{categoryId} " +
            "</if>" +
            "<if test='status != null and status != \"\"'>" +
            "AND status = #{status} " +
            "</if>" +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (name LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "ORDER BY sort ASC, heat_score DESC, usage_count DESC" +
            "</script>")
    IPage<Tag> selectTagsPage(Page<Tag> page, @Param("tagType") String tagType, 
                             @Param("categoryId") Long categoryId, @Param("status") String status, 
                             @Param("keyword") String keyword);

    /**
     * 根据名称和类型查询标签（用于检查重复）
     * 
     * @param name 标签名称
     * @param tagType 标签类型
     * @return 标签
     */
    @Select("SELECT * FROM t_tag WHERE name = #{name} AND tag_type = #{tagType} LIMIT 1")
    Tag selectByNameAndType(@Param("name") String name, @Param("tagType") String tagType);

    /**
     * 更新标签使用次数
     * 
     * @param tagId 标签ID
     * @param increment 增量（可以为负数）
     * @return 更新行数
     */
    @Update("UPDATE t_tag SET usage_count = GREATEST(usage_count + #{increment}, 0), update_time = NOW() WHERE id = #{tagId}")
    int updateUsageCount(@Param("tagId") Long tagId, @Param("increment") Long increment);

    /**
     * 更新标签热度分数
     * 
     * @param tagId 标签ID
     * @param heatScore 新的热度分数
     * @return 更新行数
     */
    @Update("UPDATE t_tag SET heat_score = #{heatScore}, update_time = NOW() WHERE id = #{tagId}")
    int updateHeatScore(@Param("tagId") Long tagId, @Param("heatScore") BigDecimal heatScore);

    /**
     * 根据ID列表批量查询标签
     * 
     * @param tagIds 标签ID列表
     * @param status 状态（可选）
     * @return 标签列表
     */
    @Select("<script>" +
            "SELECT * FROM t_tag WHERE id IN " +
            "<foreach collection='tagIds' item='tagId' open='(' separator=',' close=')'>" +
            "#{tagId}" +
            "</foreach>" +
            "<if test='status != null and status != \"\"'>" +
            "AND status = #{status}" +
            "</if>" +
            "</script>")
    List<Tag> selectByIdList(@Param("tagIds") List<Long> tagIds, @Param("status") String status);
} 