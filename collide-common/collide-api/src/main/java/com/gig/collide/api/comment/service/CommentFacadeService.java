package com.gig.collide.api.comment.service;

import com.gig.collide.api.comment.request.*;
import com.gig.collide.api.comment.response.*;
import com.gig.collide.api.comment.response.data.*;
import com.gig.collide.api.comment.enums.CommentTypeEnum;
import com.gig.collide.base.response.PageResponse;

/**
 * 评论门面服务接口
 * 提供评论核心业务功能
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface CommentFacadeService {

    // ===================== 评论查询相关 =====================

    /**
     * 评论统一信息查询
     *
     * @param queryRequest 查询请求
     * @return 查询响应
     */
    CommentUnifiedQueryResponse<CommentUnifiedInfo> queryComment(CommentUnifiedQueryRequest queryRequest);

    /**
     * 基础评论信息查询（不包含敏感信息）
     *
     * @param queryRequest 查询请求
     * @return 基础评论信息响应
     */
    CommentUnifiedQueryResponse<BasicCommentInfo> queryBasicComment(CommentUnifiedQueryRequest queryRequest);

    /**
     * 分页查询评论信息
     *
     * @param queryRequest 分页查询请求
     * @return 分页响应
     */
    PageResponse<CommentUnifiedInfo> pageQueryComments(CommentUnifiedQueryRequest queryRequest);

    /**
     * 根据评论ID批量查询
     *
     * @param queryRequest 查询请求（包含评论ID列表）
     * @return 批量查询响应
     */
    CommentUnifiedQueryResponse<CommentUnifiedInfo> batchQueryComments(CommentUnifiedQueryRequest queryRequest);

    /**
     * 查询评论统计信息
     *
     * @param targetId 目标对象ID
     * @param commentType 评论类型
     * @return 统计信息响应
     */
    CommentUnifiedQueryResponse<CommentStatisticsInfo> queryCommentStatistics(Long targetId, CommentTypeEnum commentType);

    /**
     * 查询评论树（包含回复的层级结构）
     *
     * @param queryRequest 树形查询请求
     * @return 评论树响应
     */
    CommentUnifiedQueryResponse<CommentUnifiedInfo> queryCommentTree(CommentUnifiedQueryRequest queryRequest);

    // ===================== 评论操作相关 =====================

    /**
     * 创建评论
     *
     * @param createRequest 创建请求
     * @return 创建响应
     */
    CommentUnifiedOperateResponse createComment(CommentUnifiedCreateRequest createRequest);

    /**
     * 修改评论
     *
     * @param modifyRequest 修改请求
     * @return 修改响应
     */
    CommentUnifiedOperateResponse modifyComment(CommentUnifiedModifyRequest modifyRequest);

    /**
     * 删除评论（逻辑删除）
     *
     * @param commentId 评论ID
     * @param operatorId 操作员ID
     * @param reason 删除原因
     * @return 删除响应
     */
    CommentUnifiedOperateResponse deleteComment(Long commentId, Long operatorId, String reason);

    /**
     * 恢复已删除的评论
     *
     * @param commentId 评论ID
     * @param operatorId 操作员ID
     * @param reason 恢复原因
     * @return 恢复响应
     */
    CommentUnifiedOperateResponse restoreComment(Long commentId, Long operatorId, String reason);

    // ===================== 评论交互相关 =====================

    /**
     * 点赞评论
     *
     * @param interactionRequest 交互请求
     * @return 点赞响应
     */
    CommentUnifiedOperateResponse likeComment(CommentInteractionRequest interactionRequest);

    /**
     * 取消点赞
     *
     * @param interactionRequest 交互请求
     * @return 取消点赞响应
     */
    CommentUnifiedOperateResponse unlikeComment(CommentInteractionRequest interactionRequest);

    /**
     * 点踩评论
     *
     * @param interactionRequest 交互请求
     * @return 点踩响应
     */
    CommentUnifiedOperateResponse dislikeComment(CommentInteractionRequest interactionRequest);

    /**
     * 取消点踩
     *
     * @param interactionRequest 交互请求
     * @return 取消点踩响应
     */
    CommentUnifiedOperateResponse undislikeComment(CommentInteractionRequest interactionRequest);

    /**
     * 举报评论
     *
     * @param interactionRequest 交互请求
     * @return 举报响应
     */
    CommentUnifiedOperateResponse reportComment(CommentInteractionRequest interactionRequest);

    // ===================== 回复相关 =====================

    /**
     * 回复评论
     *
     * @param createRequest 回复创建请求
     * @return 回复响应
     */
    CommentUnifiedOperateResponse replyComment(CommentUnifiedCreateRequest createRequest);

    /**
     * 查询评论的回复列表
     *
     * @param parentCommentId 父评论ID
     * @param pageRequest 分页请求
     * @return 回复列表响应
     */
    PageResponse<CommentUnifiedInfo> queryReplies(Long parentCommentId, CommentUnifiedQueryRequest pageRequest);

    /**
     * 获取评论的回复数量
     *
     * @param commentId 评论ID
     * @return 回复数量
     */
    Integer getReplyCount(Long commentId);

    // ===================== 热门和推荐评论 =====================

    /**
     * 获取热门评论列表
     *
     * @param targetId 目标对象ID
     * @param commentType 评论类型
     * @param pageSize 每页大小
     * @return 热门评论响应
     */
    CommentUnifiedQueryResponse<CommentUnifiedInfo> getHotComments(Long targetId, CommentTypeEnum commentType, Integer pageSize);

    /**
     * 获取最新评论列表
     *
     * @param targetId 目标对象ID
     * @param commentType 评论类型
     * @param pageSize 每页大小
     * @return 最新评论响应
     */
    CommentUnifiedQueryResponse<CommentUnifiedInfo> getLatestComments(Long targetId, CommentTypeEnum commentType, Integer pageSize);

    /**
     * 获取精华评论列表
     *
     * @param targetId 目标对象ID
     * @param commentType 评论类型
     * @param pageSize 每页大小
     * @return 精华评论响应
     */
    CommentUnifiedQueryResponse<CommentUnifiedInfo> getEssenceComments(Long targetId, CommentTypeEnum commentType, Integer pageSize);

    /**
     * 获取置顶评论列表
     *
     * @param targetId 目标对象ID
     * @param commentType 评论类型
     * @return 置顶评论响应
     */
    CommentUnifiedQueryResponse<CommentUnifiedInfo> getPinnedComments(Long targetId, CommentTypeEnum commentType);

    // ===================== 用户相关 =====================

    /**
     * 获取用户的评论列表
     *
     * @param userId 用户ID
     * @param queryRequest 查询请求
     * @return 用户评论响应
     */
    PageResponse<CommentUnifiedInfo> getUserComments(Long userId, CommentUnifiedQueryRequest queryRequest);

    /**
     * 获取用户收到的回复列表
     *
     * @param userId 用户ID
     * @param queryRequest 查询请求
     * @return 回复列表响应
     */
    PageResponse<CommentUnifiedInfo> getUserReceivedReplies(Long userId, CommentUnifiedQueryRequest queryRequest);

    /**
     * 获取用户点赞的评论列表
     *
     * @param userId 用户ID
     * @param queryRequest 查询请求
     * @return 点赞评论响应
     */
    PageResponse<CommentUnifiedInfo> getUserLikedComments(Long userId, CommentUnifiedQueryRequest queryRequest);

    /**
     * 检查用户对评论的交互状态
     *
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return 交互状态（是否点赞、点踩、举报等）
     */
    CommentUnifiedQueryResponse<Object> getUserCommentInteractionStatus(Long userId, Long commentId);

    // ===================== 搜索和过滤 =====================

    /**
     * 搜索评论
     *
     * @param keyword 关键词
     * @param targetId 目标对象ID（可选）
     * @param commentType 评论类型（可选）
     * @param queryRequest 查询请求
     * @return 搜索结果响应
     */
    PageResponse<CommentUnifiedInfo> searchComments(String keyword, Long targetId, 
                                                   CommentTypeEnum commentType, CommentUnifiedQueryRequest queryRequest);

    /**
     * 根据质量分数过滤评论
     *
     * @param targetId 目标对象ID
     * @param commentType 评论类型
     * @param minQualityScore 最小质量分数
     * @param queryRequest 查询请求
     * @return 过滤结果响应
     */
    PageResponse<CommentUnifiedInfo> getHighQualityComments(Long targetId, CommentTypeEnum commentType, 
                                                           Double minQualityScore, CommentUnifiedQueryRequest queryRequest);

    // ===================== 统计相关 =====================

    /**
     * 获取目标对象的评论概览统计
     *
     * @param targetId 目标对象ID
     * @param commentType 评论类型
     * @return 统计概览响应
     */
    CommentUnifiedQueryResponse<CommentStatisticsInfo> getCommentOverview(Long targetId, CommentTypeEnum commentType);

    /**
     * 获取用户的评论统计
     *
     * @param userId 用户ID
     * @return 用户评论统计响应
     */
    CommentUnifiedQueryResponse<Object> getUserCommentStats(Long userId);

    // ===================== 验证相关 =====================

    /**
     * 验证评论内容是否包含敏感词
     *
     * @param content 评论内容
     * @return 验证结果（true-包含敏感词，false-不包含）
     */
    Boolean validateCommentContent(String content);

    /**
     * 检查用户是否有评论权限
     *
     * @param userId 用户ID
     * @param targetId 目标对象ID
     * @param commentType 评论类型
     * @return 权限检查结果
     */
    Boolean checkCommentPermission(Long userId, Long targetId, CommentTypeEnum commentType);

    /**
     * 检查评论是否需要审核
     *
     * @param userId 用户ID
     * @param content 评论内容
     * @return 是否需要审核
     */
    Boolean needAudit(Long userId, String content);
} 