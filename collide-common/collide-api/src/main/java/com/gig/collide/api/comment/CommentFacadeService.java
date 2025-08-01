package com.gig.collide.api.comment;

import com.gig.collide.api.comment.request.CommentCreateRequest;
import com.gig.collide.api.comment.request.CommentUpdateRequest;
import com.gig.collide.api.comment.request.CommentQueryRequest;
import com.gig.collide.api.comment.response.CommentResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import java.util.List;
import java.util.Map;

/**
 * 评论门面服务接口 - 简洁版
 * 基于comment-simple.sql的设计，支持多级评论和回复功能
 * 支持评论类型：CONTENT、DYNAMIC
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
public interface CommentFacadeService {

    // =================== 评论管理 ===================

    /**
     * 创建评论
     * 支持根评论和回复评论，包含冗余用户信息
     * 
     * @param request 创建请求
     * @return 创建的评论
     */
    Result<CommentResponse> createComment(CommentCreateRequest request);

    /**
     * 更新评论
     * 支持评论内容和状态更新
     * 
     * @param request 更新请求
     * @return 更新后的评论
     */
    Result<CommentResponse> updateComment(CommentUpdateRequest request);

    /**
     * 删除评论
     * 逻辑删除，将状态更新为DELETED
     * 
     * @param commentId 评论ID
     * @param userId 操作用户ID
     * @return 删除结果
     */
    Result<Void> deleteComment(Long commentId, Long userId);

    /**
     * 根据ID获取评论详情
     * 
     * @param commentId 评论ID
     * @param includeDeleted 是否包含已删除评论
     * @return 评论详情
     */
    Result<CommentResponse> getCommentById(Long commentId, Boolean includeDeleted);

    /**
     * 分页查询评论
     * 支持按多种条件查询
     * 
     * @param request 查询请求
     * @return 评论列表
     */
    Result<PageResponse<CommentResponse>> queryComments(CommentQueryRequest request);

    // =================== 目标对象评论 ===================

    /**
     * 获取目标对象的评论列表
     * 获取指定内容或动态的评论
     * 
     * @param targetId 目标对象ID
     * @param commentType 评论类型：CONTENT、DYNAMIC
     * @param parentCommentId 父评论ID，0表示获取根评论
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @return 评论列表
     */
    Result<PageResponse<CommentResponse>> getTargetComments(Long targetId, String commentType, 
                                                           Long parentCommentId, Integer currentPage, Integer pageSize);

    /**
     * 获取评论的回复列表
     * 
     * @param parentCommentId 父评论ID
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @return 回复列表
     */
    Result<PageResponse<CommentResponse>> getCommentReplies(Long parentCommentId, Integer currentPage, Integer pageSize);

    /**
     * 获取目标对象的评论树
     * 返回带层级结构的评论列表
     * 
     * @param targetId 目标对象ID
     * @param commentType 评论类型
     * @param maxDepth 最大层级深度
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @return 评论树
     */
    Result<PageResponse<CommentResponse>> getCommentTree(Long targetId, String commentType, 
                                                        Integer maxDepth, Integer currentPage, Integer pageSize);

    // =================== 用户评论 ===================

    /**
     * 获取用户的评论列表
     * 
     * @param userId 用户ID
     * @param commentType 评论类型（可选）
     * @param status 状态（可选）
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @return 评论列表
     */
    Result<PageResponse<CommentResponse>> getUserComments(Long userId, String commentType, 
                                                         String status, Integer currentPage, Integer pageSize);

    /**
     * 获取用户收到的回复
     * 
     * @param userId 用户ID
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @return 回复列表
     */
    Result<PageResponse<CommentResponse>> getUserReplies(Long userId, Integer currentPage, Integer pageSize);

    // =================== 状态管理 ===================

    /**
     * 更新评论状态
     * 支持NORMAL、HIDDEN、DELETED状态
     * 
     * @param commentId 评论ID
     * @param status 新状态
     * @param operatorId 操作人ID
     * @return 更新结果
     */
    Result<Void> updateCommentStatus(Long commentId, String status, Long operatorId);

    /**
     * 批量更新评论状态
     * 
     * @param commentIds 评论ID列表
     * @param status 新状态
     * @param operatorId 操作人ID
     * @return 更新成功数量
     */
    Result<Integer> batchUpdateCommentStatus(List<Long> commentIds, String status, Long operatorId);

    /**
     * 隐藏评论
     * 
     * @param commentId 评论ID
     * @param operatorId 操作人ID
     * @return 操作结果
     */
    Result<Void> hideComment(Long commentId, Long operatorId);

    /**
     * 恢复评论
     * 
     * @param commentId 评论ID
     * @param operatorId 操作人ID
     * @return 操作结果
     */
    Result<Void> restoreComment(Long commentId, Long operatorId);

    // =================== 统计功能 ===================

    /**
     * 增加评论点赞数
     * 
     * @param commentId 评论ID
     * @param increment 增加数量
     * @return 更新后的点赞数
     */
    Result<Integer> increaseLikeCount(Long commentId, Integer increment);

    /**
     * 增加回复数
     * 
     * @param commentId 评论ID
     * @param increment 增加数量
     * @return 更新后的回复数
     */
    Result<Integer> increaseReplyCount(Long commentId, Integer increment);

    /**
     * 统计目标对象的评论数
     * 
     * @param targetId 目标对象ID
     * @param commentType 评论类型
     * @param status 状态（可选）
     * @return 评论数量
     */
    Result<Long> countTargetComments(Long targetId, String commentType, String status);

    /**
     * 统计用户评论数
     * 
     * @param userId 用户ID
     * @param commentType 评论类型（可选）
     * @param status 状态（可选）
     * @return 评论数量
     */
    Result<Long> countUserComments(Long userId, String commentType, String status);

    /**
     * 获取评论统计信息
     * 
     * @param commentId 评论ID
     * @return 统计信息
     */
    Result<Map<String, Object>> getCommentStatistics(Long commentId);

    // =================== 数据同步 ===================

    /**
     * 更新用户信息（冗余字段）
     * 当用户信息变更时，同步更新评论表中的冗余信息
     * 
     * @param userId 用户ID
     * @param nickname 新昵称
     * @param avatar 新头像
     * @return 更新成功的记录数
     */
    Result<Integer> updateUserInfo(Long userId, String nickname, String avatar);

    // =================== 高级功能 ===================

    /**
     * 搜索评论
     * 根据评论内容搜索
     * 
     * @param keyword 搜索关键词
     * @param commentType 评论类型（可选）
     * @param targetId 目标对象ID（可选）
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @return 搜索结果
     */
    Result<PageResponse<CommentResponse>> searchComments(String keyword, String commentType, 
                                                        Long targetId, Integer currentPage, Integer pageSize);

    /**
     * 获取热门评论
     * 根据点赞数排序
     * 
     * @param targetId 目标对象ID
     * @param commentType 评论类型
     * @param timeRange 时间范围（天）
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @return 热门评论列表
     */
    Result<PageResponse<CommentResponse>> getPopularComments(Long targetId, String commentType, 
                                                           Integer timeRange, Integer currentPage, Integer pageSize);

    /**
     * 获取最新评论
     * 
     * @param targetId 目标对象ID（可选）
     * @param commentType 评论类型（可选）
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @return 最新评论列表
     */
    Result<PageResponse<CommentResponse>> getLatestComments(Long targetId, String commentType, 
                                                          Integer currentPage, Integer pageSize);

    /**
     * 批量删除目标对象的评论
     * 当内容被删除时，清理相关评论
     * 
     * @param targetId 目标对象ID
     * @param commentType 评论类型
     * @param operatorId 操作人ID
     * @return 删除成功数量
     */
    Result<Integer> batchDeleteTargetComments(Long targetId, String commentType, Long operatorId);
} 