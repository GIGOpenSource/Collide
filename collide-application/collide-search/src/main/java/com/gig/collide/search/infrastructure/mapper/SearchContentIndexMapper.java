package com.gig.collide.search.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.search.domain.entity.SearchContentIndex;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 搜索内容索引 Mapper 接口
 * 
 * @author Collide Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Mapper
public interface SearchContentIndexMapper extends BaseMapper<SearchContentIndex> {

    /**
     * 全文搜索内容
     * 
     * @param page 分页参数
     * @param keyword 搜索关键词
     * @param contentType 内容类型过滤
     * @param categoryId 分类ID过滤
     * @param authorId 作者ID过滤
     * @param status 状态过滤
     * @param isHot 是否热门过滤
     * @param isRecommended 是否推荐过滤
     * @return 分页结果
     */
    IPage<SearchContentIndex> fullTextSearchContents(
            @Param("page") Page<SearchContentIndex> page,
            @Param("keyword") String keyword,
            @Param("contentType") String contentType,
            @Param("categoryId") Long categoryId,
            @Param("authorId") Long authorId,
            @Param("status") String status,
            @Param("isHot") Integer isHot,
            @Param("isRecommended") Integer isRecommended
    );

    /**
     * 高级搜索内容
     * 
     * @param page 分页参数
     * @param title 标题关键词
     * @param description 描述关键词
     * @param authorNickname 作者昵称
     * @param tags 标签列表
     * @param categoryPath 分类路径
     * @param contentType 内容类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param minViewCount 最小浏览数
     * @param minLikeCount 最小点赞数
     * @return 分页结果
     */
    IPage<SearchContentIndex> advancedSearchContents(
            @Param("page") Page<SearchContentIndex> page,
            @Param("title") String title,
            @Param("description") String description,
            @Param("authorNickname") String authorNickname,
            @Param("tags") List<String> tags,
            @Param("categoryPath") String categoryPath,
            @Param("contentType") String contentType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("minViewCount") Long minViewCount,
            @Param("minLikeCount") Long minLikeCount
    );

    /**
     * 根据标签搜索内容
     * 
     * @param page 分页参数
     * @param tags 标签列表
     * @param matchAll 是否需要匹配所有标签
     * @param contentType 内容类型过滤
     * @return 分页结果
     */
    IPage<SearchContentIndex> searchContentsByTags(
            @Param("page") Page<SearchContentIndex> page,
            @Param("tags") List<String> tags,
            @Param("matchAll") Boolean matchAll,
            @Param("contentType") String contentType
    );

    /**
     * 查询热门内容
     * 
     * @param contentType 内容类型过滤
     * @param categoryId 分类ID过滤
     * @param limit 限制数量
     * @return 热门内容列表
     */
    List<SearchContentIndex> queryHotContents(
            @Param("contentType") String contentType,
            @Param("categoryId") Long categoryId,
            @Param("limit") Integer limit
    );

    /**
     * 查询推荐内容
     * 
     * @param contentType 内容类型过滤
     * @param limit 限制数量
     * @return 推荐内容列表
     */
    List<SearchContentIndex> queryRecommendedContents(
            @Param("contentType") String contentType,
            @Param("limit") Integer limit
    );

    /**
     * 查询最新内容
     * 
     * @param contentType 内容类型过滤
     * @param categoryId 分类ID过滤
     * @param limit 限制数量
     * @return 最新内容列表
     */
    List<SearchContentIndex> queryLatestContents(
            @Param("contentType") String contentType,
            @Param("categoryId") Long categoryId,
            @Param("limit") Integer limit
    );

    /**
     * 同步内容索引数据
     * 
     * @param contentId 内容ID
     * @param contentIndex 内容索引信息
     * @return 更新数量
     */
    int syncContentIndex(@Param("contentId") Long contentId, @Param("contentIndex") SearchContentIndex contentIndex);

    /**
     * 批量同步内容索引数据
     * 
     * @param contentIndexList 内容索引列表
     * @return 更新数量
     */
    int batchSyncContentIndex(@Param("contentIndexList") List<SearchContentIndex> contentIndexList);

    /**
     * 更新内容统计信息
     * 
     * @param contentId 内容ID
     * @param viewCount 浏览数
     * @param likeCount 点赞数
     * @param dislikeCount 点踩数
     * @param commentCount 评论数
     * @param shareCount 分享数
     * @param favoriteCount 收藏数
     * @return 更新数量
     */
    int updateContentStatistics(
            @Param("contentId") Long contentId,
            @Param("viewCount") Long viewCount,
            @Param("likeCount") Long likeCount,
            @Param("dislikeCount") Long dislikeCount,
            @Param("commentCount") Long commentCount,
            @Param("shareCount") Long shareCount,
            @Param("favoriteCount") Long favoriteCount
    );

    /**
     * 更新内容评分
     * 
     * @param contentId 内容ID
     * @param qualityScore 质量评分
     * @param popularityScore 热度评分
     * @param searchWeight 搜索权重
     * @param relevanceScore 相关度评分
     * @return 更新数量
     */
    int updateContentScore(
            @Param("contentId") Long contentId,
            @Param("qualityScore") BigDecimal qualityScore,
            @Param("popularityScore") BigDecimal popularityScore,
            @Param("searchWeight") Double searchWeight,
            @Param("relevanceScore") Double relevanceScore
    );

    /**
     * 根据内容ID列表批量查询
     * 
     * @param contentIds 内容ID列表
     * @return 内容索引列表
     */
    List<SearchContentIndex> queryByContentIds(@Param("contentIds") List<Long> contentIds);

    /**
     * 查询作者的内容
     * 
     * @param page 分页参数
     * @param authorId 作者ID
     * @param contentType 内容类型过滤
     * @param status 状态过滤
     * @return 分页结果
     */
    IPage<SearchContentIndex> queryContentsByAuthor(
            @Param("page") Page<SearchContentIndex> page,
            @Param("authorId") Long authorId,
            @Param("contentType") String contentType,
            @Param("status") String status
    );

    /**
     * 查询需要更新的内容索引
     * 
     * @param lastUpdateTime 最后更新时间阈值
     * @param limit 限制数量
     * @return 内容索引列表
     */
    List<SearchContentIndex> queryNeedUpdateContents(
            @Param("lastUpdateTime") LocalDateTime lastUpdateTime,
            @Param("limit") Integer limit
    );

    /**
     * 清理无效内容索引
     * 
     * @param expireTime 过期时间
     * @return 清理数量
     */
    int cleanInvalidContentIndex(@Param("expireTime") LocalDateTime expireTime);
} 