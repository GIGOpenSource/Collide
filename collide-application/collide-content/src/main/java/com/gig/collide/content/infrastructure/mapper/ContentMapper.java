package com.gig.collide.content.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.content.domain.entity.Content;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 内容表数据映射接口
 * 专注于C端必需的内容查询功能
 *
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
@Mapper
public interface ContentMapper extends BaseMapper<Content> {

    // =================== C端必需的通用查询方法 ===================

    /**
     * 通用条件查询内容列表
     * @param authorId 作者ID（可选）
     * @param categoryId 分类ID（可选）
     * @param contentType 内容类型（可选）
     * @param status 状态（可选）
     * @param reviewStatus 审核状态（可选）
     * @param minScore 最低评分（可选）
     * @param timeRange 时间范围天数（可选，用于热门内容筛选）
     * @param orderBy 排序字段（可选：createTime、updateTime、viewCount、likeCount、favoriteCount、shareCount、commentCount、score）
     * @param orderDirection 排序方向（可选：ASC、DESC）
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     */
    List<Content> selectContentsByConditions(@Param("authorId") Long authorId,
                                           @Param("categoryId") Long categoryId,
                                           @Param("contentType") String contentType,
                                           @Param("status") String status,
                                           @Param("reviewStatus") String reviewStatus,
                                           @Param("minScore") Double minScore,
                                           @Param("timeRange") Integer timeRange,
                                           @Param("orderBy") String orderBy,
                                           @Param("orderDirection") String orderDirection,
                                           @Param("currentPage") Integer currentPage,
                                           @Param("pageSize") Integer pageSize);

    /**
     * 通用搜索内容
     * @param keyword 搜索关键词（标题、内容、标签）
     * @param contentType 内容类型（可选）
     * @param categoryId 分类ID（可选）
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     */
    List<Content> searchContents(@Param("keyword") String keyword,
                               @Param("contentType") String contentType,
                               @Param("categoryId") Long categoryId,
                               @Param("currentPage") Integer currentPage,
                               @Param("pageSize") Integer pageSize);

    // =================== C端必需的特殊查询方法 ===================

    /**
     * 推荐内容（基于用户行为的个性化推荐）
     */
    List<Content> getRecommendedContents(@Param("userId") Long userId,
                                        @Param("excludeContentIds") List<Long> excludeContentIds,
                                        @Param("limit") Integer limit);

    /**
     * 相似内容（基于分类和标签的相似度推荐）
     */
    List<Content> getSimilarContents(@Param("contentId") Long contentId,
                                    @Param("limit") Integer limit);

    // =================== C端必需的CRUD操作方法 ===================

    /**
     * 更新内容状态
     */
    int updateContentStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 更新审核状态
     */
    int updateReviewStatus(@Param("id") Long id, @Param("reviewStatus") String reviewStatus);

    /**
     * 更新内容基本信息
     */
    int updateContentInfo(@Param("id") Long id,
                         @Param("title") String title,
                         @Param("description") String description,
                         @Param("tags") String tags,
                         @Param("coverImage") String coverImage);

    /**
     * 更新内容统计信息
     */
    int updateContentStats(@Param("id") Long id,
                          @Param("viewCount") Long viewCount,
                          @Param("likeCount") Long likeCount,
                          @Param("commentCount") Long commentCount,
                          @Param("favoriteCount") Long favoriteCount);

    /**
     * 批量更新状态
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") String status);

    /**
     * 软删除内容
     */
    int softDeleteContent(@Param("id") Long id);

    /**
     * 批量软删除内容
     */
    int batchSoftDeleteContent(@Param("ids") List<Long> ids);
}