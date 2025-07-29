package com.gig.collide.content.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.content.domain.entity.Content;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 内容数据访问接口 - 简洁版
 * 基于content-simple.sql的无连表设计，支持评分功能
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Mapper
public interface ContentMapper extends BaseMapper<Content> {

    // =================== 基础查询 ===================

    /**
     * 根据作者ID查询内容
     */
    Page<Content> findByAuthor(Page<Content> page, 
                              @Param("authorId") Long authorId,
                              @Param("contentType") String contentType,
                              @Param("status") String status);

    /**
     * 根据分类查询内容
     */
    Page<Content> findByCategory(Page<Content> page,
                                @Param("categoryId") Long categoryId,
                                @Param("contentType") String contentType,
                                @Param("status") String status);

    /**
     * 搜索内容（标题、描述、标签）
     */
    Page<Content> searchContents(Page<Content> page,
                                @Param("keyword") String keyword,
                                @Param("contentType") String contentType,
                                @Param("status") String status);

    /**
     * 获取热门内容
     */
    Page<Content> findPopularContents(Page<Content> page,
                                     @Param("contentType") String contentType,
                                     @Param("timeRange") Integer timeRange,
                                     @Param("minViewCount") Long minViewCount,
                                     @Param("minLikeCount") Long minLikeCount);

    /**
     * 获取最新内容
     */
    Page<Content> findLatestContents(Page<Content> page,
                                    @Param("contentType") String contentType,
                                    @Param("status") String status);

    /**
     * 根据评分查询内容
     */
    Page<Content> findByScore(Page<Content> page,
                             @Param("minScore") Double minScore,
                             @Param("contentType") String contentType);

    // =================== 统计更新 ===================

    /**
     * 增加浏览量
     */
    int increaseViewCount(@Param("id") Long id, @Param("increment") Integer increment);

    /**
     * 增加点赞数
     */
    int increaseLikeCount(@Param("id") Long id, @Param("increment") Integer increment);

    /**
     * 增加评论数
     */
    int increaseCommentCount(@Param("id") Long id, @Param("increment") Integer increment);

    /**
     * 增加收藏数
     */
    int increaseFavoriteCount(@Param("id") Long id, @Param("increment") Integer increment);

    /**
     * 更新评分统计
     */
    int updateScoreStats(@Param("id") Long id, 
                        @Param("scoreCount") Long scoreCount,
                        @Param("scoreTotal") Long scoreTotal);

    /**
     * 添加单个评分
     */
    int addScore(@Param("id") Long id, @Param("score") Integer score);

    // =================== 状态管理 ===================

    /**
     * 发布内容
     */
    int publishContent(@Param("id") Long id, @Param("publishTime") LocalDateTime publishTime);

    /**
     * 下线内容
     */
    int offlineContent(@Param("id") Long id);

    /**
     * 更新审核状态
     */
    int updateReviewStatus(@Param("id") Long id, @Param("reviewStatus") String reviewStatus);

    // =================== 冗余字段更新 ===================

    /**
     * 更新作者信息（冗余字段）
     */
    int updateAuthorInfo(@Param("authorId") Long authorId,
                        @Param("nickname") String nickname,
                        @Param("avatar") String avatar);

    /**
     * 更新分类信息（冗余字段）
     */
    int updateCategoryInfo(@Param("categoryId") Long categoryId,
                          @Param("categoryName") String categoryName);

    // =================== 统计查询 ===================

    /**
     * 获取内容统计信息
     */
    Map<String, Object> getContentStatistics(@Param("id") Long id);

    /**
     * 获取作者内容数量
     */
    Long countByAuthor(@Param("authorId") Long authorId, @Param("status") String status);

    /**
     * 获取分类内容数量
     */
    Long countByCategory(@Param("categoryId") Long categoryId, @Param("status") String status);

    /**
     * 获取内容类型统计
     */
    List<Map<String, Object>> getContentTypeStats();

    /**
     * 获取今日发布数量
     */
    Long getTodayPublishCount();

    /**
     * 获取待审核内容数量
     */
    Long getPendingReviewCount();

    // =================== 复杂查询 ===================

    /**
     * 多条件查询内容
     */
    Page<Content> findWithConditions(Page<Content> page,
                                    @Param("title") String title,
                                    @Param("contentType") String contentType,
                                    @Param("authorId") Long authorId,
                                    @Param("categoryId") Long categoryId,
                                    @Param("status") String status,
                                    @Param("reviewStatus") String reviewStatus,
                                    @Param("minViewCount") Long minViewCount,
                                    @Param("minLikeCount") Long minLikeCount,
                                    @Param("minScore") Double minScore,
                                    @Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime,
                                    @Param("orderBy") String orderBy,
                                    @Param("orderDirection") String orderDirection);

    /**
     * 查找需要章节管理的内容
     */
    List<Content> findNeedsChapterManagement(@Param("authorId") Long authorId);

    /**
     * 获取推荐内容（综合评分）
     */
    Page<Content> findRecommendedContents(Page<Content> page, 
                                         @Param("contentType") String contentType,
                                         @Param("excludeAuthorId") Long excludeAuthorId);

    /**
     * 获取相似内容
     */
    List<Content> findSimilarContents(@Param("categoryId") Long categoryId,
                                     @Param("contentType") String contentType,
                                     @Param("excludeId") Long excludeId,
                                     @Param("limit") Integer limit);
}