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
import java.util.Map;

/**
 * 评论数据访问接口
 * 完全去连表化设计，所有查询都基于单表
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 分页查询评论列表（单表查询）
     */
    IPage<Comment> selectCommentPage(Page<Comment> page,
                                   @Param("commentType") CommentType commentType,
                                   @Param("targetId") Long targetId,
                                   @Param("parentCommentId") Long parentCommentId,
                                   @Param("status") CommentStatus status,
                                   @Param("orderBy") String orderBy);

    /**
     * 查询评论树（单表递归查询）
     */
    List<Comment> selectCommentTree(@Param("targetId") Long targetId,
                                  @Param("commentType") CommentType commentType,
                                  @Param("status") CommentStatus status);

    /**
     * 统计评论数量（单表统计）
     */
    Long countComments(@Param("targetId") Long targetId,
                      @Param("commentType") CommentType commentType,
                      @Param("parentCommentId") Long parentCommentId,
                      @Param("status") CommentStatus status);

    /**
     * 查询用户评论历史（单表查询）
     */
    IPage<Comment> selectUserComments(Page<Comment> page,
                                    @Param("userId") Long userId,
                                    @Param("commentType") CommentType commentType,
                                    @Param("status") CommentStatus status);

    /**
     * 查询热门评论（基于冗余统计字段）
     */
    List<Comment> selectHotComments(@Param("targetId") Long targetId,
                                  @Param("commentType") CommentType commentType,
                                  @Param("limit") Integer limit);

    /**
     * 更新回复数（冗余字段更新）
     */
    int updateReplyCount(@Param("commentId") Long commentId,
                        @Param("increment") Integer increment);

    /**
     * 更新点赞数（冗余字段更新）
     */
    int updateLikeCount(@Param("commentId") Long commentId,
                       @Param("increment") Integer increment);

    /**
     * 查询子评论列表（单表查询）
     */
    List<Comment> selectChildComments(@Param("parentCommentId") Long parentCommentId,
                                    @Param("limit") Integer limit);

    /**
     * 获取评论统计信息（基于冗余字段统计）
     */
    Map<String, Object> getCommentStatistics(@Param("targetId") Long targetId,
                                           @Param("commentType") CommentType commentType);
} 