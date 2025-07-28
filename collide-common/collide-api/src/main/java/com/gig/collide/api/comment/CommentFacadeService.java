package com.gig.collide.api.comment;

import com.gig.collide.api.comment.request.CommentCreateRequest;
import com.gig.collide.api.comment.request.CommentQueryRequest;
import com.gig.collide.api.comment.response.CommentResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

/**
 * 评论管理门面服务接口 - 简洁版
 * 基于简洁版SQL设计，保留核心功能
 *
 * @author GIG Team
 * @version 2.0.0
 */
public interface CommentFacadeService {

    /**
     * 创建评论
     */
    Result<CommentResponse> createComment(CommentCreateRequest request);

    /**
     * 删除评论
     */
    Result<Void> deleteComment(Long commentId, Long userId);

    /**
     * 根据ID查询评论
     */
    Result<CommentResponse> getCommentById(Long commentId);

    /**
     * 分页查询评论列表
     */
    Result<PageResponse<CommentResponse>> queryComments(CommentQueryRequest request);

    /**
     * 获取目标对象的评论列表
     */
    Result<PageResponse<CommentResponse>> getTargetComments(Long targetId, String commentType, Integer page, Integer size);

    /**
     * 获取用户的评论列表
     */
    Result<PageResponse<CommentResponse>> getUserComments(Long userId, Integer page, Integer size);

    /**
     * 更新评论状态
     */
    Result<Void> updateCommentStatus(Long commentId, String status);

    /**
     * 统计目标对象的评论数
     */
    Result<Long> countTargetComments(Long targetId, String commentType);
} 