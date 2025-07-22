package com.gig.collide.content.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.content.constant.ContentStatus;
import com.gig.collide.api.content.constant.ContentType;
import com.gig.collide.api.content.constant.ReviewStatus;
import com.gig.collide.content.domain.entity.Content;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 内容数据访问映射器
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Mapper
public interface ContentMapper extends BaseMapper<Content> {

    /**
     * 根据作者ID分页查询内容列表
     *
     * @param page     分页对象
     * @param authorId 作者ID
     * @param status   内容状态（可选）
     * @return 内容分页结果
     */
    IPage<Content> selectByAuthorId(Page<Content> page, 
                                   @Param("authorId") Long authorId, 
                                   @Param("status") ContentStatus status);

    /**
     * 根据分类ID分页查询内容列表
     *
     * @param page       分页对象
     * @param categoryId 分类ID
     * @param status     内容状态
     * @return 内容分页结果
     */
    IPage<Content> selectByCategoryId(Page<Content> page, 
                                     @Param("categoryId") Long categoryId, 
                                     @Param("status") ContentStatus status);

    /**
     * 根据内容类型分页查询内容列表
     *
     * @param page        分页对象
     * @param contentType 内容类型
     * @param status      内容状态
     * @return 内容分页结果
     */
    IPage<Content> selectByContentType(Page<Content> page, 
                                      @Param("contentType") ContentType contentType, 
                                      @Param("status") ContentStatus status);

    /**
     * 根据审核状态分页查询内容列表
     *
     * @param page         分页对象
     * @param reviewStatus 审核状态
     * @return 内容分页结果
     */
    IPage<Content> selectByReviewStatus(Page<Content> page, 
                                       @Param("reviewStatus") ReviewStatus reviewStatus);

    /**
     * 查询推荐内容列表
     *
     * @param page         分页对象
     * @param contentType  内容类型（可选）
     * @param isRecommended 是否推荐
     * @return 内容分页结果
     */
    IPage<Content> selectRecommendedContents(Page<Content> page, 
                                            @Param("contentType") ContentType contentType,
                                            @Param("isRecommended") Boolean isRecommended);

    /**
     * 查询热门内容列表（按互动数排序）
     *
     * @param page        分页对象
     * @param contentType 内容类型（可选）
     * @param days        统计天数（查询最近N天的热门内容）
     * @return 内容分页结果
     */
    IPage<Content> selectHotContents(Page<Content> page, 
                                    @Param("contentType") ContentType contentType,
                                    @Param("days") Integer days);

    /**
     * 查询最新内容列表
     *
     * @param page        分页对象
     * @param contentType 内容类型（可选）
     * @return 内容分页结表
     */
    IPage<Content> selectLatestContents(Page<Content> page, 
                                       @Param("contentType") ContentType contentType);

    /**
     * 关键词搜索内容
     *
     * @param page    分页对象
     * @param keyword 关键词
     * @param status  内容状态
     * @return 内容分页结果
     */
    IPage<Content> searchByKeyword(Page<Content> page, 
                                  @Param("keyword") String keyword,
                                  @Param("status") ContentStatus status);

    /**
     * 标签搜索内容
     *
     * @param page   分页对象
     * @param tag    标签
     * @param status 内容状态
     * @return 内容分页结果
     */
    IPage<Content> searchByTag(Page<Content> page, 
                              @Param("tag") String tag,
                              @Param("status") ContentStatus status);

    /**
     * 增加查看数
     *
     * @param contentId 内容ID
     * @return 影响行数
     */
    int incrementViewCount(@Param("contentId") Long contentId);

    /**
     * 增加点赞数
     *
     * @param contentId 内容ID
     * @return 影响行数
     */
    int incrementLikeCount(@Param("contentId") Long contentId);

    /**
     * 减少点赞数
     *
     * @param contentId 内容ID
     * @return 影响行数
     */
    int decrementLikeCount(@Param("contentId") Long contentId);

    /**
     * 增加评论数
     *
     * @param contentId 内容ID
     * @return 影响行数
     */
    int incrementCommentCount(@Param("contentId") Long contentId);

    /**
     * 减少评论数
     *
     * @param contentId 内容ID
     * @return 影响行数
     */
    int decrementCommentCount(@Param("contentId") Long contentId);

    /**
     * 增加分享数
     *
     * @param contentId 内容ID
     * @return 影响行数
     */
    int incrementShareCount(@Param("contentId") Long contentId);

    /**
     * 增加收藏数
     *
     * @param contentId 内容ID
     * @return 影响行数
     */
    int incrementFavoriteCount(@Param("contentId") Long contentId);

    /**
     * 减少收藏数
     *
     * @param contentId 内容ID
     * @return 影响行数
     */
    int decrementFavoriteCount(@Param("contentId") Long contentId);

    /**
     * 批量更新权重分数
     *
     * @param contentIds 内容ID列表
     * @return 影响行数
     */
    int batchUpdateWeightScore(@Param("contentIds") List<Long> contentIds);

    /**
     * 统计用户发布的内容数量
     *
     * @param authorId 作者ID
     * @param status   内容状态（可选）
     * @return 内容数量
     */
    int countByAuthorId(@Param("authorId") Long authorId, 
                       @Param("status") ContentStatus status);

    /**
     * 统计分类下的内容数量
     *
     * @param categoryId 分类ID
     * @param status     内容状态（可选）
     * @return 内容数量
     */
    int countByCategoryId(@Param("categoryId") Long categoryId, 
                         @Param("status") ContentStatus status);

    /**
     * 查询需要审核的内容数量
     *
     * @return 待审核内容数量
     */
    int countPendingReview();

    /**
     * 查询指定时间范围内的内容列表
     *
     * @param page      分页对象
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param status    内容状态（可选）
     * @return 内容分页结果
     */
    IPage<Content> selectByTimeRange(Page<Content> page,
                                    @Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime,
                                    @Param("status") ContentStatus status);

    /**
     * 查询用户的内容统计信息
     *
     * @param authorId 作者ID
     * @return 统计结果Map，包含各种状态的内容数量
     */
    List<java.util.Map<String, Object>> selectUserContentStatistics(@Param("authorId") Long authorId);
} 