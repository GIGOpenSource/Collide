package com.gig.collide.content.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.content.domain.entity.Content;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 内容表数据映射接口
 * 基于无连表设计原则，避免复杂JOIN查询
 *
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
@Mapper
public interface ContentMapper extends BaseMapper<Content> {

    /**
     * 根据作者ID查询内容列表
     */
    List<Content> selectByAuthorId(@Param("authorId") Long authorId);

    /**
     * 根据分类ID查询内容列表
     */
    List<Content> selectByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 根据内容类型查询内容列表
     */
    List<Content> selectByContentType(@Param("contentType") String contentType);

    /**
     * 根据状态查询内容列表
     */
    List<Content> selectByStatus(@Param("status") String status);

    /**
     * 根据审核状态查询内容列表
     */
    List<Content> selectByReviewStatus(@Param("reviewStatus") String reviewStatus);

    /**
     * 分页查询已发布且审核通过的内容
     */
    List<Content> selectPublishedContent(@Param("offset") Long offset, 
                                       @Param("limit") Integer limit);

    /**
     * 根据标题模糊搜索内容
     */
    List<Content> searchByTitle(@Param("title") String title,
                               @Param("offset") Long offset,
                               @Param("limit") Integer limit);

    /**
     * 根据标签搜索内容
     */
    List<Content> searchByTags(@Param("tags") String tags,
                              @Param("offset") Long offset,
                              @Param("limit") Integer limit);

    /**
     * 查询热门内容（按查看数排序）
     */
    List<Content> selectHotContent(@Param("limit") Integer limit);

    /**
     * 查询最新内容（按发布时间排序）
     */
    List<Content> selectLatestContent(@Param("limit") Integer limit);

    /**
     * 查询高评分内容
     */
    List<Content> selectTopRatedContent(@Param("minScore") Double minScore,
                                       @Param("limit") Integer limit);

    /**
     * 更新查看数
     */
    int updateViewCount(@Param("id") Long id, @Param("increment") Long increment);

    /**
     * 更新点赞数
     */
    int updateLikeCount(@Param("id") Long id, @Param("increment") Long increment);

    /**
     * 更新评论数
     */
    int updateCommentCount(@Param("id") Long id, @Param("increment") Long increment);

    /**
     * 更新收藏数
     */
    int updateFavoriteCount(@Param("id") Long id, @Param("increment") Long increment);

    /**
     * 更新评分统计
     */
    int updateScoreStats(@Param("id") Long id, 
                        @Param("scoreCount") Long scoreCount,
                        @Param("scoreTotal") Long scoreTotal);

    /**
     * 批量更新内容状态
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") String status);

    /**
     * 批量更新审核状态
     */
    int batchUpdateReviewStatus(@Param("ids") List<Long> ids, @Param("reviewStatus") String reviewStatus);

    /**
     * 统计内容总数
     */
    Long countTotal();

    /**
     * 根据条件统计内容数量
     */
    Long countByCondition(@Param("authorId") Long authorId,
                         @Param("categoryId") Long categoryId,
                         @Param("contentType") String contentType,
                         @Param("status") String status,
                         @Param("reviewStatus") String reviewStatus);

    /**
     * 获取作者的内容统计
     */
    List<Object> getAuthorContentStats(@Param("authorId") Long authorId);

    /**
     * 获取分类的内容统计
     */
    List<Object> getCategoryContentStats(@Param("categoryId") Long categoryId);
}