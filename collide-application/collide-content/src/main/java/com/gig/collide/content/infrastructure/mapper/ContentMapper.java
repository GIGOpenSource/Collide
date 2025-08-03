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

    // =================== C端必需的基础查询方法 ===================

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

    // =================== C端必需的统计增加方法 ===================

    /**
     * 增加浏览量
     */
    int increaseViewCount(@Param("contentId") Long contentId, @Param("increment") Integer increment);

    /**
     * 增加点赞数
     */
    int increaseLikeCount(@Param("contentId") Long contentId, @Param("increment") Integer increment);

    /**
     * 增加评论数
     */
    int increaseCommentCount(@Param("contentId") Long contentId, @Param("increment") Integer increment);

    /**
     * 增加收藏数
     */
    int increaseFavoriteCount(@Param("contentId") Long contentId, @Param("increment") Integer increment);

    /**
     * 添加评分
     */
    int addScore(@Param("contentId") Long contentId, @Param("score") Integer score);

    // =================== C端必需的统计减少方法 ===================

    /**
     * 减少浏览量
     */
    int decreaseViewCount(@Param("contentId") Long contentId, @Param("decrement") Integer decrement);

    /**
     * 减少点赞数
     */
    int decreaseLikeCount(@Param("contentId") Long contentId, @Param("decrement") Integer decrement);

    /**
     * 减少评论数
     */
    int decreaseCommentCount(@Param("contentId") Long contentId, @Param("decrement") Integer decrement);

    /**
     * 减少收藏数
     */
    int decreaseFavoriteCount(@Param("contentId") Long contentId, @Param("decrement") Integer decrement);

    /**
     * 移除评分
     */
    int removeScore(@Param("contentId") Long contentId, @Param("score") Integer score);

    // =================== C端必需的统计更新方法 ===================

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

    // =================== C端必需的数据同步方法 ===================

    /**
     * 更新作者信息
     */
    int updateAuthorInfo(@Param("authorId") Long authorId,
                        @Param("nickname") String nickname,
                        @Param("avatar") String avatar);

    /**
     * 更新分类信息
     */
    int updateCategoryInfo(@Param("categoryId") Long categoryId,
                          @Param("categoryName") String categoryName);

    // =================== C端必需的高级查询方法 ===================

    /**
     * 推荐内容
     */
    List<Content> findRecommendedContents(@Param("page") com.baomidou.mybatisplus.extension.plugins.pagination.Page<Content> page,
                                         @Param("contentType") String contentType,
                                         @Param("excludeAuthorId") Long excludeAuthorId);

    /**
     * 相似内容
     */
    List<Content> findSimilarContents(@Param("categoryId") Long categoryId,
                                     @Param("contentType") String contentType,
                                     @Param("contentId") Long contentId,
                                     @Param("limit") Integer limit);

    /**
     * 需要章节管理的内容
     */
    List<Content> findNeedsChapterManagement(@Param("authorId") Long authorId);

    /**
     * 按作者统计
     */
    Long countByAuthor(@Param("authorId") Long authorId, @Param("status") String status);

    /**
     * 按分类统计
     */
    Long countByCategory(@Param("categoryId") Long categoryId, @Param("status") String status);

    /**
     * 内容类型统计
     */
    List<Map<String, Object>> getContentTypeStats();
}