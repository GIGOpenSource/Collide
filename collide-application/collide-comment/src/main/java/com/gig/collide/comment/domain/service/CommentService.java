package com.gig.collide.comment.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.comment.domain.entity.Comment;

import java.util.List;

/**
 * 评论业务服务接口 - C端简洁版
 * 只保留客户端使用的核心接口，移除复杂的管理接口
 * 基于单表无连表设计
 * 
 * @author Collide
 * @version 2.0.0 (C端简洁版)
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
     * 支持内容更新
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
     * @return 评论详情
     */
    Comment getCommentById(Long commentId);

    // =================== 查询功能 ===================

    /**
     * 获取目标对象的评论列表
     * 
     * @param targetId 目标对象ID
     * @param commentType 评论类型
     * @param parentCommentId 父评论ID
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 评论列表
     */
    IPage<Comment> getTargetComments(Long targetId, String commentType, Long parentCommentId,
                                   Integer currentPage, Integer pageSize);

    /**
     * 获取评论的回复列表
     * 
     * @param parentCommentId 父评论ID
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 回复列表
     */
    IPage<Comment> getCommentReplies(Long parentCommentId, Integer currentPage, Integer pageSize);

    /**
     * 获取目标对象的评论树
     * 构建树形结构的评论列表
     * 
     * @param targetId 目标对象ID
     * @param commentType 评论类型
     * @param maxDepth 最大层级深度
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 评论树
     */
    IPage<Comment> getCommentTree(Long targetId, String commentType, Integer maxDepth,
                                Integer currentPage, Integer pageSize);

    /**
     * 获取用户的评论列表
     * 
     * @param userId 用户ID
     * @param commentType 评论类型
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 评论列表
     */
    IPage<Comment> getUserComments(Long userId, String commentType,
                                 Integer currentPage, Integer pageSize);

    /**
     * 获取用户收到的回复
     * 
     * @param userId 用户ID
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 回复列表
     */
    IPage<Comment> getUserReplies(Long userId, Integer currentPage, Integer pageSize);

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
     * @return 评论数量
     */
    long countTargetComments(Long targetId, String commentType);

    /**
     * 统计用户评论数
     * 
     * @param userId 用户ID
     * @param commentType 评论类型
     * @return 评论数量
     */
    long countUserComments(Long userId, String commentType);

    // =================== 高级功能 ===================

    /**
     * 搜索评论
     * 
     * @param keyword 搜索关键词
     * @param commentType 评论类型
     * @param targetId 目标对象ID
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 搜索结果
     */
    IPage<Comment> searchComments(String keyword, String commentType, Long targetId,
                                Integer currentPage, Integer pageSize);

    /**
     * 获取热门评论
     * 
     * @param targetId 目标对象ID
     * @param commentType 评论类型
     * @param timeRange 时间范围（天）
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 热门评论列表
     */
    IPage<Comment> getPopularComments(Long targetId, String commentType, Integer timeRange,
                                    Integer currentPage, Integer pageSize);

    /**
     * 获取最新评论
     * 
     * @param targetId 目标对象ID
     * @param commentType 评论类型
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 最新评论列表
     */
    IPage<Comment> getLatestComments(Long targetId, String commentType,
                                   Integer currentPage, Integer pageSize);
}