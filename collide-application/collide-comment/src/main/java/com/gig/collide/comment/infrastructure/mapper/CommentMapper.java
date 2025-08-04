package com.gig.collide.comment.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.comment.domain.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 评论数据访问层 - 简洁版
 * 基于comment-simple.sql的设计，支持多级评论查询
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    // =================== 基础查询 ===================

    /**
     * 根据目标对象获取评论列表
     * 
     * @param targetId 目标对象ID
     * @param commentType 评论类型
     * @param parentCommentId 父评论ID
     * @param status 状态
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 评论列表
     */
    List<Comment> selectTargetComments(@Param("targetId") Long targetId,
                                     @Param("commentType") String commentType,
                                     @Param("parentCommentId") Long parentCommentId,
                                     @Param("status") String status,
                                     @Param("orderBy") String orderBy,
                                     @Param("orderDirection") String orderDirection);

    /**
     * 分页获取目标对象的评论
     * 
     * @param page 分页对象
     * @param targetId 目标对象ID
     * @param commentType 评论类型
     * @param parentCommentId 父评论ID
     * @param status 状态
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @param includeDeleted 是否包含已删除
     * @return 分页评论列表
     */
    IPage<Comment> selectTargetCommentsPage(IPage<Comment> page,
                                          @Param("targetId") Long targetId,
                                          @Param("commentType") String commentType,
                                          @Param("parentCommentId") Long parentCommentId,
                                          @Param("status") String status,
                                          @Param("orderBy") String orderBy,
                                          @Param("orderDirection") String orderDirection,
                                          @Param("includeDeleted") Boolean includeDeleted);

    /**
     * 获取用户评论列表
     * 
     * @param page 分页对象
     * @param userId 用户ID
     * @param commentType 评论类型
     * @param status 状态
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @param includeDeleted 是否包含已删除
     * @return 分页评论列表
     */
    IPage<Comment> selectUserCommentsPage(IPage<Comment> page,
                                        @Param("userId") Long userId,
                                        @Param("commentType") String commentType,
                                        @Param("status") String status,
                                        @Param("orderBy") String orderBy,
                                        @Param("orderDirection") String orderDirection,
                                        @Param("includeDeleted") Boolean includeDeleted);

    /**
     * 获取用户收到的回复
     * 
     * @param page 分页对象
     * @param replyToUserId 回复目标用户ID
     * @param status 状态
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @param includeDeleted 是否包含已删除
     * @return 分页回复列表
     */
    IPage<Comment> selectUserRepliesPage(IPage<Comment> page,
                                       @Param("replyToUserId") Long replyToUserId,
                                       @Param("status") String status,
                                       @Param("orderBy") String orderBy,
                                       @Param("orderDirection") String orderDirection,
                                       @Param("includeDeleted") Boolean includeDeleted);

    /**
     * 根据点赞数范围查询评论
     * 
     * @param page 分页对象
     * @param minLikeCount 最小点赞数
     * @param maxLikeCount 最大点赞数
     * @param commentType 评论类型
     * @param targetId 目标对象ID
     * @param status 状态
     * @param includeDeleted 是否包含已删除
     * @return 评论列表
     */
    IPage<Comment> selectCommentsByLikeCountRange(IPage<Comment> page,
                                                @Param("minLikeCount") Integer minLikeCount,
                                                @Param("maxLikeCount") Integer maxLikeCount,
                                                @Param("commentType") String commentType,
                                                @Param("targetId") Long targetId,
                                                @Param("status") String status,
                                                @Param("includeDeleted") Boolean includeDeleted);

    /**
     * 查询待审核评论
     * 
     * @param page 分页对象
     * @param commentType 评论类型
     * @param limit 限制数量
     * @return 待审核评论列表
     */
    IPage<Comment> selectPendingComments(IPage<Comment> page,
                                       @Param("commentType") String commentType,
                                       @Param("limit") Integer limit);

    /**
     * 查询用户的回复关系
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 回复关系列表
     */
    List<Map<String, Object>> selectUserReplyRelations(@Param("userId") Long userId,
                                                      @Param("startTime") LocalDateTime startTime,
                                                      @Param("endTime") LocalDateTime endTime);

    /**
     * 查询评论热度排行
     * 
     * @param commentType 评论类型
     * @param targetId 目标对象ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制数量
     * @return 热度排行列表
     */
    List<Map<String, Object>> selectCommentHotRanking(@Param("commentType") String commentType,
                                                     @Param("targetId") Long targetId,
                                                     @Param("startTime") LocalDateTime startTime,
                                                     @Param("endTime") LocalDateTime endTime,
                                                     @Param("limit") Integer limit);

    // =================== 统计查询 ===================

    /**
     * 统计目标对象的评论数
     * 
     * @param targetId 目标对象ID
     * @param commentType 评论类型
     * @param parentCommentId 父评论ID
     * @param status 状态
     * @param includeDeleted 是否包含已删除
     * @return 评论数量
     */
    Long countTargetComments(@Param("targetId") Long targetId,
                           @Param("commentType") String commentType,
                           @Param("parentCommentId") Long parentCommentId,
                           @Param("status") String status,
                           @Param("includeDeleted") Boolean includeDeleted);

    /**
     * 统计用户评论数
     * 
     * @param userId 用户ID
     * @param commentType 评论类型
     * @param status 状态
     * @param includeDeleted 是否包含已删除
     * @return 评论数量
     */
    Long countUserComments(@Param("userId") Long userId,
                         @Param("commentType") String commentType,
                         @Param("status") String status,
                         @Param("includeDeleted") Boolean includeDeleted);

    /**
     * 统计评论的回复数
     * 
     * @param parentCommentId 父评论ID
     * @param status 状态
     * @param includeDeleted 是否包含已删除
     * @return 回复数量
     */
    Long countCommentReplies(@Param("parentCommentId") Long parentCommentId,
                           @Param("status") String status,
                           @Param("includeDeleted") Boolean includeDeleted);

    /**
     * 获取评论统计信息
     * 
     * @param commentId 评论ID
     * @return 统计信息
     */
    Map<String, Object> selectCommentStatistics(@Param("commentId") Long commentId);

    // =================== 更新操作 ===================

    /**
     * 增加评论点赞数
     * 
     * @param commentId 评论ID
     * @param increment 增加数量
     * @return 影响行数
     */
    int increaseLikeCount(@Param("commentId") Long commentId,
                         @Param("increment") Integer increment);

    /**
     * 增加回复数
     * 
     * @param commentId 评论ID
     * @param increment 增加数量
     * @return 影响行数
     */
    int increaseReplyCount(@Param("commentId") Long commentId,
                          @Param("increment") Integer increment);

    /**
     * 批量更新评论状态
     * 
     * @param commentIds 评论ID列表
     * @param status 新状态
     * @return 影响行数
     */
    int batchUpdateStatus(@Param("commentIds") List<Long> commentIds,
                         @Param("status") String status);

    /**
     * 批量删除目标对象的评论
     * 
     * @param targetId 目标对象ID
     * @param commentType 评论类型
     * @param status 新状态
     * @return 影响行数
     */
    int batchDeleteTargetComments(@Param("targetId") Long targetId,
                                 @Param("commentType") String commentType,
                                 @Param("status") String status);

    /**
     * 更新用户信息（冗余字段同步）
     * 
     * @param userId 用户ID
     * @param nickname 新昵称
     * @param avatar 新头像
     * @return 影响行数
     */
    int updateUserInfo(@Param("userId") Long userId,
                      @Param("nickname") String nickname,
                      @Param("avatar") String avatar);

    /**
     * 更新回复目标用户信息（冗余字段同步）
     * 
     * @param replyToUserId 回复目标用户ID
     * @param nickname 新昵称
     * @param avatar 新头像
     * @return 影响行数
     */
    int updateReplyToUserInfo(@Param("replyToUserId") Long replyToUserId,
                             @Param("nickname") String nickname,
                             @Param("avatar") String avatar);

    // =================== 高级查询 ===================

    /**
     * 搜索评论
     * 
     * @param page 分页对象
     * @param keyword 搜索关键词
     * @param commentType 评论类型
     * @param targetId 目标对象ID
     * @param status 状态
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 搜索结果
     */
    IPage<Comment> searchComments(IPage<Comment> page,
                                @Param("keyword") String keyword,
                                @Param("commentType") String commentType,
                                @Param("targetId") Long targetId,
                                @Param("status") String status,
                                @Param("orderBy") String orderBy,
                                @Param("orderDirection") String orderDirection);

    /**
     * 获取热门评论
     * 
     * @param page 分页对象
     * @param targetId 目标对象ID
     * @param commentType 评论类型
     * @param timeRange 时间范围（天）
     * @param minLikeCount 最小点赞数
     * @param status 状态
     * @return 热门评论列表
     */
    IPage<Comment> selectPopularComments(IPage<Comment> page,
                                       @Param("targetId") Long targetId,
                                       @Param("commentType") String commentType,
                                       @Param("timeRange") Integer timeRange,
                                       @Param("minLikeCount") Integer minLikeCount,
                                       @Param("status") String status);

    /**
     * 获取最新评论
     * 
     * @param page 分页对象
     * @param targetId 目标对象ID
     * @param commentType 评论类型
     * @param status 状态
     * @return 最新评论列表
     */
    IPage<Comment> selectLatestComments(IPage<Comment> page,
                                      @Param("targetId") Long targetId,
                                      @Param("commentType") String commentType,
                                      @Param("status") String status);

    /**
     * 获取评论树形结构数据
     * 用于构建树形展示
     * 
     * @param targetId 目标对象ID
     * @param commentType 评论类型
     * @param maxDepth 最大层级深度
     * @param status 状态
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 树形评论列表
     */
    List<Comment> selectCommentTree(@Param("targetId") Long targetId,
                                  @Param("commentType") String commentType,
                                  @Param("maxDepth") Integer maxDepth,
                                  @Param("status") String status,
                                  @Param("orderBy") String orderBy,
                                  @Param("orderDirection") String orderDirection);

    /**
     * 获取指定时间范围内的评论数据
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param commentType 评论类型
     * @param status 状态
     * @return 评论列表
     */
    List<Comment> selectCommentsByTimeRange(@Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime,
                                          @Param("commentType") String commentType,
                                          @Param("status") String status);

    /**
     * 清理已删除的评论（物理删除）
     * 删除指定时间之前的已删除评论
     * 
     * @param days 删除多少天前的数据
     * @param limit 限制删除数量
     * @return 删除数量
     */
    int cleanDeletedComments(@Param("days") Integer days,
                           @Param("limit") Integer limit);
}