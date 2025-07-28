package com.gig.collide.search.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.search.domain.entity.SearchCommentIndex;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 搜索评论索引 Mapper 接口
 * 
 * @author Collide Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Mapper
public interface SearchCommentIndexMapper extends BaseMapper<SearchCommentIndex> {

    /**
     * 全文搜索评论
     * 
     * @param page 分页参数
     * @param keyword 搜索关键词
     * @param commentType 评论类型过滤
     * @param targetId 目标对象ID过滤
     * @param userId 用户ID过滤
     * @param status 状态过滤
     * @param isHot 是否热门过滤
     * @param isEssence 是否精华过滤
     * @return 分页结果
     */
    IPage<SearchCommentIndex> fullTextSearchComments(
            @Param("page") Page<SearchCommentIndex> page,
            @Param("keyword") String keyword,
            @Param("commentType") String commentType,
            @Param("targetId") Long targetId,
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("isHot") Integer isHot,
            @Param("isEssence") Integer isEssence
    );

    /**
     * 搜索用户评论
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @param commentType 评论类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<SearchCommentIndex> searchUserComments(
            @Param("page") Page<SearchCommentIndex> page,
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            @Param("commentType") String commentType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * 搜索内容的评论
     * 
     * @param page 分页参数
     * @param targetId 目标内容ID
     * @param keyword 搜索关键词
     * @param parentCommentId 父评论ID过滤
     * @param minLikeCount 最小点赞数
     * @return 分页结果
     */
    IPage<SearchCommentIndex> searchContentComments(
            @Param("page") Page<SearchCommentIndex> page,
            @Param("targetId") Long targetId,
            @Param("keyword") String keyword,
            @Param("parentCommentId") Long parentCommentId,
            @Param("minLikeCount") Integer minLikeCount
    );

    /**
     * 查询热门评论
     * 
     * @param commentType 评论类型过滤
     * @param targetId 目标对象ID过滤
     * @param limit 限制数量
     * @return 热门评论列表
     */
    List<SearchCommentIndex> queryHotComments(
            @Param("commentType") String commentType,
            @Param("targetId") Long targetId,
            @Param("limit") Integer limit
    );

    /**
     * 查询精华评论
     * 
     * @param commentType 评论类型过滤
     * @param targetId 目标对象ID过滤
     * @param limit 限制数量
     * @return 精华评论列表
     */
    List<SearchCommentIndex> queryEssenceComments(
            @Param("commentType") String commentType,
            @Param("targetId") Long targetId,
            @Param("limit") Integer limit
    );

    /**
     * 查询最新评论
     * 
     * @param commentType 评论类型过滤
     * @param targetId 目标对象ID过滤
     * @param limit 限制数量
     * @return 最新评论列表
     */
    List<SearchCommentIndex> queryLatestComments(
            @Param("commentType") String commentType,
            @Param("targetId") Long targetId,
            @Param("limit") Integer limit
    );

    /**
     * 同步评论索引数据
     * 
     * @param commentId 评论ID
     * @param commentIndex 评论索引信息
     * @return 更新数量
     */
    int syncCommentIndex(@Param("commentId") Long commentId, @Param("commentIndex") SearchCommentIndex commentIndex);

    /**
     * 批量同步评论索引数据
     * 
     * @param commentIndexList 评论索引列表
     * @return 更新数量
     */
    int batchSyncCommentIndex(@Param("commentIndexList") List<SearchCommentIndex> commentIndexList);

    /**
     * 更新评论统计信息
     * 
     * @param commentId 评论ID
     * @param likeCount 点赞数
     * @param replyCount 回复数
     * @param reportCount 举报数
     * @return 更新数量
     */
    int updateCommentStatistics(
            @Param("commentId") Long commentId,
            @Param("likeCount") Integer likeCount,
            @Param("replyCount") Integer replyCount,
            @Param("reportCount") Integer reportCount
    );

    /**
     * 更新评论评分
     * 
     * @param commentId 评论ID
     * @param qualityScore 质量评分
     * @param popularityScore 热度评分
     * @param searchWeight 搜索权重
     * @return 更新数量
     */
    int updateCommentScore(
            @Param("commentId") Long commentId,
            @Param("qualityScore") BigDecimal qualityScore,
            @Param("popularityScore") BigDecimal popularityScore,
            @Param("searchWeight") Double searchWeight
    );

    /**
     * 根据评论ID列表批量查询
     * 
     * @param commentIds 评论ID列表
     * @return 评论索引列表
     */
    List<SearchCommentIndex> queryByCommentIds(@Param("commentIds") List<Long> commentIds);

    /**
     * 查询用户的回复评论
     * 
     * @param page 分页参数
     * @param replyToUserId 被回复用户ID
     * @param keyword 搜索关键词
     * @return 分页结果
     */
    IPage<SearchCommentIndex> queryUserReplyComments(
            @Param("page") Page<SearchCommentIndex> page,
            @Param("replyToUserId") Long replyToUserId,
            @Param("keyword") String keyword
    );

    /**
     * 查询提及用户的评论
     * 
     * @param page 分页参数
     * @param mentionUserId 被提及用户ID
     * @param keyword 搜索关键词
     * @return 分页结果
     */
    IPage<SearchCommentIndex> queryMentionUserComments(
            @Param("page") Page<SearchCommentIndex> page,
            @Param("mentionUserId") Long mentionUserId,
            @Param("keyword") String keyword
    );

    /**
     * 查询需要更新的评论索引
     * 
     * @param lastUpdateTime 最后更新时间阈值
     * @param limit 限制数量
     * @return 评论索引列表
     */
    List<SearchCommentIndex> queryNeedUpdateComments(
            @Param("lastUpdateTime") LocalDateTime lastUpdateTime,
            @Param("limit") Integer limit
    );

    /**
     * 清理无效评论索引
     * 
     * @param expireTime 过期时间
     * @return 清理数量
     */
    int cleanInvalidCommentIndex(@Param("expireTime") LocalDateTime expireTime);
} 