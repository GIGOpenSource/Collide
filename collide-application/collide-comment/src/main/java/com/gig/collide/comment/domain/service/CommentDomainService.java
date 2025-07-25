package com.gig.collide.comment.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.comment.constant.CommentStatus;
import com.gig.collide.api.comment.constant.CommentType;
import com.gig.collide.api.comment.request.CommentCreateRequest;
import com.gig.collide.comment.domain.entity.Comment;
import com.gig.collide.comment.infrastructure.exception.CommentErrorCode;
import com.gig.collide.comment.infrastructure.mapper.CommentMapper;
import com.gig.collide.base.exception.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 评论领域服务
 * 完全去连表化设计，统计基于冗余字段
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommentDomainService {

    private final CommentMapper commentMapper;

    /**
     * 创建评论（去连表化设计）
     */
    @Transactional(rollbackFor = Exception.class)
    public Comment createComment(CommentCreateRequest createRequest) {
        // 参数验证
        validateCreateRequest(createRequest);

        // 构建评论实体
        Comment comment = buildCommentFromRequest(createRequest);

        // 设置父评论关系
        if (createRequest.getParentCommentId() != null && createRequest.getParentCommentId() > 0) {
            Comment parentComment = commentMapper.selectById(createRequest.getParentCommentId());
            if (parentComment == null) {
                throw new BizException(CommentErrorCode.PARENT_COMMENT_NOT_FOUND);
            }
            comment.setParentCommentId(createRequest.getParentCommentId());
            comment.setRootCommentId(parentComment.getRootCommentId() != null && parentComment.getRootCommentId() > 0 
                ? parentComment.getRootCommentId() : parentComment.getId());
            comment.setReplyToUserId(createRequest.getReplyToUserId());
        } else {
            comment.setParentCommentId(0L);
            comment.setRootCommentId(0L);
        }

        // 保存评论
        int result = commentMapper.insert(comment);
        if (result <= 0) {
            throw new BizException(CommentErrorCode.COMMENT_CREATE_FAILED);
        }

        // 异步更新父评论的回复数（冗余字段更新）
        if (comment.getParentCommentId() != null && comment.getParentCommentId() > 0) {
            updateParentReplyCount(comment.getParentCommentId(), 1);
        }

        return comment;
    }

    /**
     * 删除评论（软删除，更新冗余统计）
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComment(Long commentId, Long currentUserId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BizException(CommentErrorCode.COMMENT_NOT_FOUND);
        }

        // 权限检查
        if (!comment.getUserId().equals(currentUserId)) {
            throw new BizException(CommentErrorCode.NO_PERMISSION_TO_DELETE);
        }

        // 使用MyBatis Plus的逻辑删除
        comment.setDeleted(1);
        comment.setUpdateTime(LocalDateTime.now());
        int result = commentMapper.updateById(comment);

        if (result > 0) {
            // 异步更新父评论的回复数
            if (comment.getParentCommentId() != null && comment.getParentCommentId() > 0) {
                updateParentReplyCount(comment.getParentCommentId(), -1);
            }
        }

        return result > 0;
    }

    /**
     * 分页查询评论（单表查询，无连表）
     */
    public IPage<Comment> queryCommentPage(CommentType commentType, Long targetId, 
                                         Long parentCommentId, CommentStatus status,
                                         int pageNum, int pageSize, String orderBy) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        return commentMapper.selectCommentPage(page, commentType, targetId, parentCommentId, status, orderBy);
    }

    /**
     * 查询评论树（单表递归查询）
     */
    public List<Comment> queryCommentTree(Long targetId, CommentType commentType, CommentStatus status) {
        return commentMapper.selectCommentTree(targetId, commentType, status);
    }
    
    /**
     * 查询评论树（重载方法，适配Facade调用）
     */
    public List<Comment> queryCommentTree(CommentType commentType, Long targetId) {
        return commentMapper.selectCommentTree(targetId, commentType, CommentStatus.NORMAL);
    }

    /**
     * 统计评论数量（单表统计）
     */
    public Long countComments(Long targetId, CommentType commentType, 
                            Long parentCommentId, CommentStatus status) {
        return commentMapper.countComments(targetId, commentType, parentCommentId, status);
    }
    
    /**
     * 统计评论数量（重载方法，适配Facade调用）
     */
    public Long countComments(CommentType commentType, Long targetId) {
        return commentMapper.countComments(targetId, commentType, null, CommentStatus.NORMAL);
    }
    
    /**
     * 查询评论列表（适配Facade调用）
     */
    public List<Comment> queryComments(CommentType commentType, Long targetId, 
                                     CommentStatus status, Long parentCommentId, 
                                     int pageNum, int pageSize) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        IPage<Comment> result = commentMapper.selectCommentPage(page, commentType, targetId, parentCommentId, status, "create_time DESC");
        return result.getRecords();
    }
    
    /**
     * 分页查询评论（适配Facade调用）
     */
    public IPage<Comment> pageQueryComments(CommentType commentType, CommentStatus status, 
                                          int pageNum, int pageSize) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        return commentMapper.selectCommentPage(page, commentType, null, null, status, "create_time DESC");
    }
    
    /**
     * 更新评论状态（适配Facade调用）
     */
    public void updateCommentStatus(Long commentId, Long userId, CommentStatus status) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment != null) {
            comment.setStatus(status);
            comment.setUpdateTime(LocalDateTime.now());
            commentMapper.updateById(comment);
        }
    }

    /**
     * 查询用户评论历史（单表查询）
     */
    public IPage<Comment> queryUserComments(Long userId, CommentType commentType, 
                                          CommentStatus status, int pageNum, int pageSize) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        return commentMapper.selectUserComments(page, userId, commentType, status);
    }

    /**
     * 查询热门评论（基于冗余统计字段）
     */
    public List<Comment> queryHotComments(Long targetId, CommentType commentType, Integer limit) {
        return commentMapper.selectHotComments(targetId, commentType, limit);
    }

    /**
     * 获取评论统计信息（基于冗余字段，无连表）
     */
    public Map<String, Object> getCommentStatistics(Long targetId, CommentType commentType) {
        return commentMapper.getCommentStatistics(targetId, commentType);
    }

    /**
     * 更新评论点赞数（冗余字段更新）
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCommentLikeCount(Long commentId, Integer increment) {
        int result = commentMapper.updateLikeCount(commentId, increment);
        return result > 0;
    }

    /**
     * 验证创建请求
     */
    private void validateCreateRequest(CommentCreateRequest request) {
        if (request.getTargetId() == null || request.getTargetId() <= 0) {
            throw new BizException(CommentErrorCode.INVALID_TARGET_ID);
        }
        if (request.getUserId() == null || request.getUserId() <= 0) {
            throw new BizException(CommentErrorCode.INVALID_USER_ID);
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new BizException(CommentErrorCode.COMMENT_CONTENT_EMPTY);
        }
        if (request.getContent().length() > 2000) {
            throw new BizException(CommentErrorCode.COMMENT_CONTENT_TOO_LONG);
        }
    }

    /**
     * 从请求构建评论实体
     */
    private Comment buildCommentFromRequest(CommentCreateRequest request) {
        Comment comment = new Comment();
        comment.setCommentType(request.getCommentType());
        comment.setTargetId(request.getTargetId());
        comment.setContent(request.getContent().trim());
        comment.setUserId(request.getUserId());
        comment.setStatus(CommentStatus.NORMAL);
        comment.setLikeCount(0);
        comment.setReplyCount(0);
        comment.setIsPinned(false);
        comment.setIsHot(false);
        // 这些字段从请求中获取不到，使用默认值或从上下文获取
        // comment.setIpAddress(getClientIpAddress());
        // comment.setDeviceInfo(getClientDeviceInfo());
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        comment.setDeleted(0);
        return comment;
    }

    /**
     * 更新父评论的回复数（异步处理冗余字段）
     */
    private void updateParentReplyCount(Long parentCommentId, Integer increment) {
        try {
            commentMapper.updateReplyCount(parentCommentId, increment);
        } catch (Exception e) {
            log.error("更新父评论回复数失败，parentCommentId: {}, increment: {}", 
                    parentCommentId, increment, e);
            // 这里可以发送消息队列进行异步重试
        }
    }
} 