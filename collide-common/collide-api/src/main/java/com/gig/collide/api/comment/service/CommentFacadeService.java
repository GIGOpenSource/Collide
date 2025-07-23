package com.gig.collide.api.comment.service;

import com.gig.collide.api.comment.request.CommentCreateRequest;
import com.gig.collide.api.comment.request.CommentQueryRequest;
import com.gig.collide.api.comment.response.CommentResponse;
import com.gig.collide.api.comment.response.CommentQueryResponse;
import com.gig.collide.api.comment.response.data.CommentInfo;
import com.gig.collide.base.response.PageResponse;

/**
 * 评论服务 Facade 接口
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface CommentFacadeService {

    /**
     * 创建评论
     *
     * @param createRequest 创建请求
     * @return 评论响应
     */
    CommentResponse createComment(CommentCreateRequest createRequest);

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 评论响应
     */
    CommentResponse deleteComment(Long commentId, Long userId);

    /**
     * 点赞/取消点赞评论
     *
     * @param commentId 评论ID
     * @param userId 用户ID
     * @param isLike 是否点赞
     * @return 评论响应
     */
    CommentResponse likeComment(Long commentId, Long userId, Boolean isLike);

    /**
     * 查询评论详情
     *
     * @param queryRequest 查询请求
     * @return 评论查询响应
     */
    CommentQueryResponse<CommentInfo> queryComment(CommentQueryRequest queryRequest);

    /**
     * 分页查询评论列表
     *
     * @param queryRequest 查询请求
     * @return 评论列表响应
     */
    PageResponse<CommentInfo> pageQueryComments(CommentQueryRequest queryRequest);

    /**
     * 查询评论树（包含子评论的树形结构）
     *
     * @param queryRequest 查询请求
     * @return 评论树响应
     */
    PageResponse<CommentInfo> queryCommentTree(CommentQueryRequest queryRequest);

    /**
     * 查询评论统计信息
     *
     * @param targetId 目标ID
     * @param commentType 评论类型
     * @return 统计信息响应
     */
    CommentQueryResponse<Long> queryCommentCount(Long targetId, String commentType);
} 