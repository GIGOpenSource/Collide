package com.gig.collide.tag.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.tag.domain.entity.ContentTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 内容标签Mapper接口 - 对应 t_content_tag 表
 * 
 * @author GIG Team
 * @version 1.0.0
 */
@Mapper
public interface ContentTagMapper extends BaseMapper<ContentTag> {

    // =================== 内容标签查询 ===================

    /**
     * 查询内容是否包含指定标签
     * 
     * @param contentId 内容ID
     * @param tagId 标签ID
     * @return 是否包含
     */
    boolean hasContentTag(@Param("contentId") Long contentId, @Param("tagId") Long tagId);

    /**
     * 获取内容的标签ID列表
     * 
     * @param contentId 内容ID
     * @return 标签ID列表
     */
    List<Long> getContentTagIds(@Param("contentId") Long contentId);

    /**
     * 获取内容的标签详情列表
     * 
     * @param contentId 内容ID
     * @return 内容标签列表（含标签详情）
     */
    List<ContentTag> getContentTagsWithDetails(@Param("contentId") Long contentId);

    /**
     * 统计内容的标签数量
     * 
     * @param contentId 内容ID
     * @return 标签数量
     */
    Integer countContentTags(@Param("contentId") Long contentId);

    // =================== 标签内容查询 ===================

    /**
     * 根据单个标签查询内容ID列表
     * 
     * @param tagId 标签ID
     * @param limit 数量限制
     * @return 内容ID列表
     */
    List<Long> getContentsByTag(@Param("tagId") Long tagId, @Param("limit") Integer limit);

    /**
     * 根据多个标签查询内容ID列表（AND关系）
     * 
     * @param tagIds 标签ID列表
     * @param limit 数量限制
     * @return 内容ID列表
     */
    List<Long> getContentsByTagsAnd(@Param("tagIds") List<Long> tagIds, @Param("limit") Integer limit);

    /**
     * 根据多个标签查询内容ID列表（OR关系）
     * 
     * @param tagIds 标签ID列表
     * @param limit 数量限制
     * @return 内容ID列表
     */
    List<Long> getContentsByTagsOr(@Param("tagIds") List<Long> tagIds, @Param("limit") Integer limit);

    /**
     * 统计标签下的内容数量
     * 
     * @param tagId 标签ID
     * @return 内容数量
     */
    Long countContentsByTag(@Param("tagId") Long tagId);

    /**
     * 获取标签的最新内容
     * 
     * @param tagId 标签ID
     * @param days 天数范围
     * @param limit 数量限制
     * @return 最新内容ID列表
     */
    List<Long> getTagLatestContents(@Param("tagId") Long tagId, 
                                   @Param("days") Integer days, 
                                   @Param("limit") Integer limit);

    /**
     * 获取标签的热门内容（基于时间新鲜度）
     * 
     * @param tagId 标签ID
     * @param days 天数范围
     * @param limit 数量限制
     * @return 热门内容ID列表
     */
    List<Long> getTagHotContents(@Param("tagId") Long tagId, 
                                @Param("days") Integer days, 
                                @Param("limit") Integer limit);

    // =================== 基于用户标签的内容推荐 ===================

    /**
     * 根据用户关注的标签推荐内容
     * 
     * @param userId 用户ID
     * @param excludeContentIds 排除的内容ID列表
     * @param limit 推荐数量限制
     * @return 推荐内容ID列表
     */
    List<Long> getRecommendContentsByUserTags(@Param("userId") Long userId,
                                             @Param("excludeContentIds") List<Long> excludeContentIds,
                                             @Param("limit") Integer limit);

    /**
     * 根据用户关注的标签推荐热门内容
     * 
     * @param userId 用户ID
     * @param days 天数范围
     * @param excludeContentIds 排除的内容ID列表
     * @param limit 推荐数量限制
     * @return 推荐热门内容ID列表
     */
    List<Long> getRecommendHotContentsByUserTags(@Param("userId") Long userId,
                                                @Param("days") Integer days,
                                                @Param("excludeContentIds") List<Long> excludeContentIds,
                                                @Param("limit") Integer limit);

    /**
     * 基于内容标签相似度推荐相关内容
     * 
     * @param contentId 内容ID
     * @param limit 推荐数量限制
     * @return 相关内容ID列表
     */
    List<Long> getRelatedContentsByTags(@Param("contentId") Long contentId, @Param("limit") Integer limit);

    // =================== 条件查询 ===================

    /**
     * 根据条件分页查询内容标签关系
     * 
     * @param tagId 标签ID（可选）
     * @param contentId 内容ID（可选）
     * @param tagStartDate 打标签开始日期
     * @param tagEndDate 打标签结束日期
     * @param tagStatus 标签状态
     * @param sortField 排序字段
     * @param sortDirection 排序方向
     * @param offset 偏移量
     * @param pageSize 页面大小
     * @return 内容标签关系列表
     */
    List<ContentTag> findContentTagsByCondition(@Param("tagId") Long tagId,
                                               @Param("contentId") Long contentId,
                                               @Param("tagStartDate") LocalDate tagStartDate,
                                               @Param("tagEndDate") LocalDate tagEndDate,
                                               @Param("tagStatus") Integer tagStatus,
                                               @Param("sortField") String sortField,
                                               @Param("sortDirection") String sortDirection,
                                               @Param("offset") Integer offset,
                                               @Param("pageSize") Integer pageSize);

    /**
     * 根据条件统计内容标签关系数量
     * 
     * @param tagId 标签ID（可选）
     * @param contentId 内容ID（可选）
     * @param tagStartDate 打标签开始日期
     * @param tagEndDate 打标签结束日期
     * @param tagStatus 标签状态
     * @return 记录数量
     */
    Long countContentTagsByCondition(@Param("tagId") Long tagId,
                                    @Param("contentId") Long contentId,
                                    @Param("tagStartDate") LocalDate tagStartDate,
                                    @Param("tagEndDate") LocalDate tagEndDate,
                                    @Param("tagStatus") Integer tagStatus);

    // =================== 批量操作 ===================

    /**
     * 批量检查内容是否包含指定标签
     * 
     * @param contentId 内容ID
     * @param tagIds 标签ID列表
     * @return 已包含的标签ID列表
     */
    List<Long> batchCheckContentTags(@Param("contentId") Long contentId, @Param("tagIds") List<Long> tagIds);

    /**
     * 批量删除内容的标签
     * 
     * @param contentId 内容ID
     * @param tagIds 标签ID列表
     * @return 删除行数
     */
    int batchDeleteContentTags(@Param("contentId") Long contentId, @Param("tagIds") List<Long> tagIds);

    /**
     * 批量插入内容标签关系
     * 
     * @param contentTags 内容标签关系列表
     * @return 插入行数
     */
    int batchInsertContentTags(@Param("contentTags") List<ContentTag> contentTags);

    /**
     * 替换内容的所有标签
     * 
     * @param contentId 内容ID
     * @param newTagIds 新的标签ID列表
     * @return 操作结果
     */
    int replaceContentTags(@Param("contentId") Long contentId, @Param("newTagIds") List<Long> newTagIds);

    // =================== 统计分析 ===================

    /**
     * 获取标签的内容时间分布
     * 
     * @param tagId 标签ID
     * @param days 天数范围
     * @return 时间分布统计
     */
    List<Map<String, Object>> getTagContentTimeDistribution(@Param("tagId") Long tagId, @Param("days") Integer days);

    /**
     * 统计每日新增内容标签数
     * 
     * @param tagId 标签ID（可选）
     * @param days 天数范围
     * @return 每日统计数据
     */
    List<Map<String, Object>> getDailyContentTagStats(@Param("tagId") Long tagId, @Param("days") Integer days);

    /**
     * 获取内容标签的权重分布
     * 
     * @param contentId 内容ID（可选）
     * @return 权重分布统计
     */
    List<Map<String, Object>> getContentTagWeightDistribution(@Param("contentId") Long contentId);

    /**
     * 获取标签共现分析（经常一起出现的标签）
     * 
     * @param tagId 标签ID
     * @param limit 数量限制
     * @return 共现标签统计
     */
    List<Map<String, Object>> getTagCooccurrenceAnalysis(@Param("tagId") Long tagId, @Param("limit") Integer limit);

    // =================== 智能推荐支持 ===================

    /**
     * 为内容推荐标签（基于相似内容的标签）
     * 
     * @param contentId 内容ID
     * @param excludeTagIds 排除的标签ID列表
     * @param limit 推荐数量限制
     * @return 推荐标签ID列表
     */
    List<Long> getRecommendTagsForContent(@Param("contentId") Long contentId,
                                         @Param("excludeTagIds") List<Long> excludeTagIds,
                                         @Param("limit") Integer limit);

    /**
     * 获取用户可能感兴趣的内容（基于行为相似的用户）
     * 
     * @param userId 用户ID
     * @param similarUserIds 相似用户ID列表
     * @param excludeContentIds 排除的内容ID列表
     * @param limit 推荐数量限制
     * @return 推荐内容ID列表
     */
    List<Long> getRecommendContentsBySimilarUsers(@Param("userId") Long userId,
                                                 @Param("similarUserIds") List<Long> similarUserIds,
                                                 @Param("excludeContentIds") List<Long> excludeContentIds,
                                                 @Param("limit") Integer limit);

    // =================== 数据清理 ===================

    /**
     * 清理无效的内容标签（针对已删除的标签）
     * 
     * @param contentId 内容ID（可选，为空时清理所有内容）
     * @return 清理行数
     */
    int cleanupInvalidContentTags(@Param("contentId") Long contentId);

    /**
     * 删除内容的所有标签
     * 
     * @param contentId 内容ID
     * @return 删除行数
     */
    int deleteAllContentTags(@Param("contentId") Long contentId);

    /**
     * 删除标签的所有内容关联
     * 
     * @param tagId 标签ID
     * @return 删除行数
     */
    int deleteAllTagContents(@Param("tagId") Long tagId);

    // =================== 性能优化查询 ===================

    /**
     * 批量获取多个内容的标签
     * 
     * @param contentIds 内容ID列表
     * @return 内容标签映射
     */
    List<ContentTag> batchGetContentTags(@Param("contentIds") List<Long> contentIds);

    /**
     * 批量获取多个标签的内容数量
     * 
     * @param tagIds 标签ID列表
     * @return 标签内容数量映射
     */
    List<Map<String, Object>> batchGetTagContentCounts(@Param("tagIds") List<Long> tagIds);
}