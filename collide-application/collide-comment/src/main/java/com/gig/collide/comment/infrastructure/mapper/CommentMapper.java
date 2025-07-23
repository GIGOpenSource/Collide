package com.gig.collide.comment.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.comment.constant.CommentStatus;
import com.gig.collide.api.comment.constant.CommentType;
import com.gig.collide.comment.domain.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论数据访问映射器
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 分页查询评论列表
     *
     * @param page 分页参数
     * @param commentType 评论类型
     * @param targetId 目标ID
     * @param parentCommentId 父评论ID
     * @param status 状态
     * @param sortBy 排序字段
     * @param sortOrder 排序方向
     * @return 评论分页列表
     */
    IPage<Comment> selectCommentPage(Page<Comment> page,
                                   @Param("commentType") CommentType commentType,
                                   @Param("targetId") Long targetId,
                                   @Param("parentCommentId") Long parentCommentId,
                                   @Param("status") CommentStatus status,
                                   @Param("sortBy") String sortBy,
                                   @Param("sortOrder") String sortOrder);

    /**
     * 查询评论树（根评论及其子评论）
     *
     * @param page 分页参数
     * @param commentType 评论类型
     * @param targetId 目标ID
     * @param status 状态
     * @param sortBy 排序字段
     * @param sortOrder 排序方向
     * @return 评论树分页列表
     */
    IPage<Comment> selectCommentTree(Page<Comment> page,
                                   @Param("commentType") CommentType commentType,
                                   @Param("targetId") Long targetId,
                                   @Param("status") CommentStatus status,
                                   @Param("sortBy") String sortBy,
                                   @Param("sortOrder") String sortOrder);

    /**
     * 根据根评论ID查询所有子评论
     *
     * @param rootCommentId 根评论ID
     * @param status 状态
     * @return 子评论列表
     */
    List<Comment> selectChildComments(@Param("rootCommentId") Long rootCommentId,
                                    @Param("status") CommentStatus status);

    /**
     * 统计目标的评论数量
     *
     * @param commentType 评论类型
     * @param targetId 目标ID
     * @param status 状态
     * @return 评论数量
     */
    Long countCommentsByTarget(@Param("commentType") CommentType commentType,
                             @Param("targetId") Long targetId,
                             @Param("status") CommentStatus status);

    /**
     * 更新评论点赞数
     *
     * @param commentId 评论ID
     * @param increment 增量（可为负数）
     * @return 更新行数
     */
    int updateLikeCount(@Param("commentId") Long commentId,
                       @Param("increment") Integer increment);

    /**
     * 更新评论回复数
     *
     * @param commentId 评论ID
     * @param increment 增量（可为负数）
     * @return 更新行数
     */
    int updateReplyCount(@Param("commentId") Long commentId,
                        @Param("increment") Integer increment);

    /**
     * 批量更新评论状态
     *
     * @param commentIds 评论ID列表
     * @param status 新状态
     * @return 更新行数
     */
    int updateCommentStatus(@Param("commentIds") List<Long> commentIds,
                           @Param("status") CommentStatus status);
} 