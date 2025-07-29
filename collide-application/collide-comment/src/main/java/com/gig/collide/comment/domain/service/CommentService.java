package com.gig.collide.comment.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.comment.domain.entity.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 评论业务服务接口 - 简洁版
 * 定义核心评论业务逻辑，基于单表无连表设计
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
public interface CommentService {

    // =================== 基础CRUD ===================

    /**
     * 创建评论
     * 包含参数验证、冗余字段填充、回复数更新等业务逻辑
     * 
     * @param comment 评论实体
     * @return 创建的评论
     */
    Comment createComment(Comment comment);

    /**
     * 更新评论
     * 支持内容、状态、统计数据更新
     * 
     * @param comment 评论实体
     * @return 更新的评论
     */
    Comment updateComment(Comment comment);

    /**
     * 删除评论（逻辑删除）
     * 同时更新父评论的回复数
     * 
     * @param commentId 评论ID
     * @param userId 操作用户ID
     * @return 是否成功
     */
    boolean deleteComment(Long commentId, Long userId);

    /**
     * 根据ID获取评论
     * 
     * @param commentId 评论ID
     * @param includeDeleted 是否包含已删除评论
     * @return 评论详情
     */
    Comment getCommentById(Long commentId, Boolean includeDeleted);

    /**
     * 根据ID列表获取评论
     * 
     * @param commentIds 评论ID列表
     * @param includeDeleted 是否包含已删除评论
     * @return 评论列表
     */
    List<Comment> getCommentsByIds(List<Long> commentIds, Boolean includeDeleted);

    // =================== 查询功能 ===================

    /**
     * 分页查询评论
     * 
     * @param targetId 目标对象ID
     * @param commentType 评论类型
     * @param parentCommentId 父评论ID
     * @param status 状态
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 分页评论列表
     */
    IPage<Comment> queryComments(Long targetId, String commentType, Long parentCommentId, 
                               String status, Integer pageNum, Integer pageSize, 
                               String orderBy, String orderDirection);

    /**
     * 获取目标对象的评论列表
     * 
     * @param targetId 目标对象ID
     * @param commentType 评论类型
     * @param parentCommentId 父评论ID
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 评论列表
     */
    IPage<Comment> getTargetComments(Long targetId, String commentType, Long parentCommentId,
                                   Integer pageNum, Integer pageSize, String orderBy, String orderDirection);

    /**
     * 获取评论的回复列表
     * 
     * @param parentCommentId 父评论ID
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 回复列表
     */
    IPage<Comment> getCommentReplies(Long parentCommentId, Integer pageNum, Integer pageSize,
                                   String orderBy, String orderDirection);

    /**
     * 获取目标对象的评论树
     * 构建树形结构的评论列表
     * 
     * @param targetId 目标对象ID
     * @param commentType 评论类型
     * @param maxDepth 最大层级深度
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 评论树
     */
    IPage<Comment> getCommentTree(Long targetId, String commentType, Integer maxDepth,
                                Integer pageNum, Integer pageSize, String orderBy, String orderDirection);

    /**
     * 获取用户的评论列表
     * 
     * @param userId 用户ID
     * @param commentType 评论类型
     * @param status 状态
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 评论列表
     */
    IPage<Comment> getUserComments(Long userId, String commentType, String status,
                                 Integer pageNum, Integer pageSize, String orderBy, String orderDirection);

    /**
     * 获取用户收到的回复
     * 
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 回复列表
     */
    IPage<Comment> getUserReplies(Long userId, Integer pageNum, Integer pageSize,
                                String orderBy, String orderDirection);

    // =================== 状态管理 ===================

    /**
     * 更新评论状态
     * 
     * @param commentId 评论ID
     * @param status 新状态
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    boolean updateCommentStatus(Long commentId, String status, Long operatorId);

    /**
     * 批量更新评论状态
     * 
     * @param commentIds 评论ID列表
     * @param status 新状态
     * @param operatorId 操作人ID
     * @return 更新成功数量
     */
    int batchUpdateCommentStatus(List<Long> commentIds, String status, Long operatorId);

    /**
     * 隐藏评论
     * 
     * @param commentId 评论ID
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    boolean hideComment(Long commentId, Long operatorId);

    /**
     * 恢复评论
     * 
     * @param commentId 评论ID
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    boolean restoreComment(Long commentId, Long operatorId);

    // =================== 统计功能 ===================

    /**
     * 增加评论点赞数
     * 
     * @param commentId 评论ID
     * @param increment 增加数量
     * @return 更新后的点赞数
     */
    int increaseLikeCount(Long commentId, Integer increment);

    /**
     * 增加回复数
     * 
     * @param commentId 评论ID
     * @param increment 增加数量
     * @return 更新后的回复数
     */
    int increaseReplyCount(Long commentId, Integer increment);

    /**
     * 统计目标对象的评论数
     * 
     * @param targetId 目标对象ID
     * @param commentType 评论类型
     * @param status 状态
     * @return 评论数量
     */
    long countTargetComments(Long targetId, String commentType, String status);

    /**
     * 统计用户评论数
     * 
     * @param userId 用户ID
     * @param commentType 评论类型
     * @param status 状态
     * @return 评论数量
     */
    long countUserComments(Long userId, String commentType, String status);

    /**
     * 获取评论统计信息
     * 
     * @param commentId 评论ID
     * @return 统计信息
     */
    Map<String, Object> getCommentStatistics(Long commentId);

    // =================== 数据同步 ===================

    /**
     * 更新用户信息（冗余字段同步）
     * 当用户信息变更时，同步更新评论表中的冗余信息
     * 
     * @param userId 用户ID
     * @param nickname 新昵称
     * @param avatar 新头像
     * @return 更新成功的记录数
     */
    int updateUserInfo(Long userId, String nickname, String avatar);

    // =================== 高级功能 ===================

    /**
     * 搜索评论
     * 
     * @param keyword 搜索关键词
     * @param commentType 评论类型
     * @param targetId 目标对象ID
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 搜索结果
     */
    IPage<Comment> searchComments(String keyword, String commentType, Long targetId,
                                Integer pageNum, Integer pageSize, String orderBy, String orderDirection);

    /**
     * 获取热门评论
     * 
     * @param targetId 目标对象ID
     * @param commentType 评论类型
     * @param timeRange 时间范围（天）
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @return 热门评论列表
     */
    IPage<Comment> getPopularComments(Long targetId, String commentType, Integer timeRange,
                                    Integer pageNum, Integer pageSize);

    /**
     * 获取最新评论
     * 
     * @param targetId 目标对象ID
     * @param commentType 评论类型
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @return 最新评论列表
     */
    IPage<Comment> getLatestComments(Long targetId, String commentType,
                                   Integer pageNum, Integer pageSize);

    /**
     * 批量删除目标对象的评论
     * 当内容被删除时，清理相关评论
     * 
     * @param targetId 目标对象ID
     * @param commentType 评论类型
     * @param operatorId 操作人ID
     * @return 删除成功数量
     */
    int batchDeleteTargetComments(Long targetId, String commentType, Long operatorId);

    /**
     * 构建评论树形结构
     * 将平铺的评论列表构建成树形结构
     * 
     * @param comments 评论列表
     * @param maxDepth 最大层级深度
     * @return 树形结构评论列表
     */
    List<Comment> buildCommentTree(List<Comment> comments, Integer maxDepth);

    /**
     * 验证评论权限
     * 检查用户是否有权限操作指定评论
     * 
     * @param commentId 评论ID
     * @param userId 用户ID
     * @param operation 操作类型：edit、delete、hide、restore
     * @return 是否有权限
     */
    boolean validateCommentPermission(Long commentId, Long userId, String operation);

    /**
     * 清理已删除的评论
     * 物理删除指定时间之前的已删除评论
     * 
     * @param beforeDays 保留天数
     * @return 清理数量
     */
    int cleanDeletedComments(Integer beforeDays);
}