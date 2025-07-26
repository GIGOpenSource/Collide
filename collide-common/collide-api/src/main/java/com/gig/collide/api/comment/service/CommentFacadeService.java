package com.gig.collide.api.comment.service;

import com.gig.collide.api.comment.request.CommentCreateRequest;
import com.gig.collide.api.comment.request.CommentQueryRequest;
import com.gig.collide.api.comment.response.CommentResponse;
import com.gig.collide.api.comment.response.CommentQueryResponse;
import com.gig.collide.api.comment.response.data.CommentInfo;
import com.gig.collide.base.response.PageResponse;

import java.util.List;
import java.util.Map;

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

    /**
     * 查询用户评论历史
     *
     * @param userId 用户ID
     * @param commentType 评论类型（可选）
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 用户评论列表
     */
    PageResponse<CommentInfo> queryUserComments(Long userId, String commentType, Integer pageNum, Integer pageSize);

    /**
     * 查询热门评论
     *
     * @param targetId 目标ID
     * @param commentType 评论类型
     * @param limit 查询数量限制
     * @return 热门评论列表
     */
    CommentQueryResponse<List<CommentInfo>> queryHotComments(Long targetId, String commentType, Integer limit);

    /**
     * 获取评论详细统计信息
     *
     * @param targetId 目标ID
     * @param commentType 评论类型
     * @return 详细统计信息
     */
    CommentQueryResponse<Map<String, Object>> getCommentStatistics(Long targetId, String commentType);

    /**
     * 检查用户是否已点赞评论
     *
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 点赞状态
     */
    CommentQueryResponse<Boolean> checkUserLiked(Long commentId, Long userId);

    /**
     * 查询子评论列表
     *
     * @param parentCommentId 父评论ID
     * @param limit 查询数量限制
     * @return 子评论列表
     */
    CommentQueryResponse<List<CommentInfo>> queryChildComments(Long parentCommentId, Integer limit);

    /**
     * 更新评论状态
     *
     * @param commentId 评论ID
     * @param userId 操作用户ID
     * @param status 目标状态
     * @return 更新结果
     */
    CommentResponse updateCommentStatus(Long commentId, Long userId, String status);
} 