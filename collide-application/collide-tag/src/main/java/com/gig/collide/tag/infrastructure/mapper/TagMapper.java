package com.gig.collide.tag.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.tag.domain.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 标签Mapper接口 - 对应 t_tag 表
 * 
 * @author GIG Team
 * @version 1.0.0
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    // =================== 基础查询 ===================

    /**
     * 根据标签名称查询标签
     * 
     * @param tagName 标签名称
     * @return 标签信息
     */
    Tag findByTagName(@Param("tagName") String tagName);

    /**
     * 查询所有启用的标签
     * 
     * @return 启用的标签列表
     */
    List<Tag> findAllActiveTags();

    /**
     * 根据权重范围查询标签
     * 
     * @param minWeight 最小权重
     * @param maxWeight 最大权重
     * @param limit 数量限制
     * @return 标签列表
     */
    List<Tag> findTagsByWeightRange(@Param("minWeight") Integer minWeight, 
                                   @Param("maxWeight") Integer maxWeight, 
                                   @Param("limit") Integer limit);

    /**
     * 根据状态查询标签
     * 
     * @param status 状态
     * @param limit 数量限制
     * @return 标签列表
     */
    List<Tag> findTagsByStatus(@Param("status") Integer status, @Param("limit") Integer limit);

    // =================== 热度和排行查询 ===================

    /**
     * 查询热门标签（按热度排序）
     * 
     * @param limit 数量限制
     * @return 热门标签列表
     */
    List<Tag> findHotTags(@Param("limit") Integer limit);

    /**
     * 查询按关注数排序的标签
     * 
     * @param limit 数量限制
     * @return 标签列表
     */
    List<Tag> findTagsByFollowCount(@Param("limit") Integer limit);

    /**
     * 查询按内容数排序的标签
     * 
     * @param limit 数量限制
     * @return 标签列表
     */
    List<Tag> findTagsByContentCount(@Param("limit") Integer limit);

    /**
     * 查询推荐标签（综合权重、热度等因素）
     * 
     * @param limit 数量限制
     * @return 推荐标签列表
     */
    List<Tag> findRecommendTags(@Param("limit") Integer limit);

    // =================== 搜索查询 ===================

    /**
     * 模糊搜索标签
     * 
     * @param keyword 搜索关键词
     * @param status 状态过滤（可选）
     * @param limit 数量限制
     * @return 匹配的标签列表
     */
    List<Tag> searchTags(@Param("keyword") String keyword, 
                        @Param("status") Integer status, 
                        @Param("limit") Integer limit);

    /**
     * 根据ID列表查询标签
     * 
     * @param tagIds 标签ID列表
     * @return 标签列表
     */
    List<Tag> findTagsByIds(@Param("tagIds") List<Long> tagIds);

    // =================== 条件查询 ===================

    /**
     * 根据条件查询标签
     * 
     * @param tagName 标签名称（模糊查询）
     * @param status 状态
     * @param minWeight 最小权重
     * @param maxWeight 最大权重
     * @param minFollowCount 最小关注数
     * @param maxFollowCount 最大关注数
     * @param minContentCount 最小内容数
     * @param maxContentCount 最大内容数
     * @param sortField 排序字段
     * @param sortDirection 排序方向
     * @param offset 偏移量
     * @param pageSize 页面大小
     * @return 标签列表
     */
    List<Tag> findTagsByCondition(@Param("tagName") String tagName,
                                 @Param("status") Integer status,
                                 @Param("minWeight") Integer minWeight,
                                 @Param("maxWeight") Integer maxWeight,
                                 @Param("minFollowCount") Long minFollowCount,
                                 @Param("maxFollowCount") Long maxFollowCount,
                                 @Param("minContentCount") Long minContentCount,
                                 @Param("maxContentCount") Long maxContentCount,
                                 @Param("sortField") String sortField,
                                 @Param("sortDirection") String sortDirection,
                                 @Param("offset") Integer offset,
                                 @Param("pageSize") Integer pageSize);

    /**
     * 根据条件统计标签数量
     * 
     * @param tagName 标签名称（模糊查询）
     * @param status 状态
     * @param minWeight 最小权重
     * @param maxWeight 最大权重
     * @param minFollowCount 最小关注数
     * @param maxFollowCount 最大关注数
     * @param minContentCount 最小内容数
     * @param maxContentCount 最大内容数
     * @return 标签数量
     */
    Long countTagsByCondition(@Param("tagName") String tagName,
                             @Param("status") Integer status,
                             @Param("minWeight") Integer minWeight,
                             @Param("maxWeight") Integer maxWeight,
                             @Param("minFollowCount") Long minFollowCount,
                             @Param("maxFollowCount") Long maxFollowCount,
                             @Param("minContentCount") Long minContentCount,
                             @Param("maxContentCount") Long maxContentCount);

    // =================== 统计查询 ===================

    /**
     * 统计启用标签数量
     * 
     * @return 启用标签数量
     */
    Long countActiveTags();

    /**
     * 统计总关注数
     * 
     * @return 总关注数
     */
    Long sumTotalFollowCount();

    /**
     * 统计总内容数
     * 
     * @return 总内容数
     */
    Long sumTotalContentCount();

    /**
     * 获取平均热度值
     * 
     * @return 平均热度值
     */
    Double getAverageHotness();

    // =================== 数据更新 ===================

    /**
     * 更新标签热度值
     * 
     * @param tagId 标签ID
     * @param hotness 新的热度值
     * @return 更新行数
     */
    int updateTagHotness(@Param("tagId") Long tagId, @Param("hotness") Long hotness);

    /**
     * 增加标签关注数
     * 
     * @param tagId 标签ID
     * @param increment 增加数量（默认1）
     * @return 更新行数
     */
    int incrementFollowCount(@Param("tagId") Long tagId, @Param("increment") Integer increment);

    /**
     * 减少标签关注数
     * 
     * @param tagId 标签ID
     * @param decrement 减少数量（默认1）
     * @return 更新行数
     */
    int decrementFollowCount(@Param("tagId") Long tagId, @Param("decrement") Integer decrement);

    /**
     * 增加标签内容数
     * 
     * @param tagId 标签ID
     * @param increment 增加数量（默认1）
     * @return 更新行数
     */
    int incrementContentCount(@Param("tagId") Long tagId, @Param("increment") Integer increment);

    /**
     * 减少标签内容数
     * 
     * @param tagId 标签ID
     * @param decrement 减少数量（默认1）
     * @return 更新行数
     */
    int decrementContentCount(@Param("tagId") Long tagId, @Param("decrement") Integer decrement);

    /**
     * 批量更新标签状态
     * 
     * @param tagIds 标签ID列表
     * @param status 新状态
     * @return 更新行数
     */
    int batchUpdateStatus(@Param("tagIds") List<Long> tagIds, @Param("status") Integer status);

    // =================== 验证查询 ===================

    /**
     * 检查标签名称是否存在
     * 
     * @param tagName 标签名称
     * @param excludeId 排除的标签ID
     * @return 是否存在
     */
    boolean existsByTagName(@Param("tagName") String tagName, @Param("excludeId") Long excludeId);

    /**
     * 检查标签是否存在且启用
     * 
     * @param tagId 标签ID
     * @return 是否存在且启用
     */
    boolean existsActiveTag(@Param("tagId") Long tagId);
}