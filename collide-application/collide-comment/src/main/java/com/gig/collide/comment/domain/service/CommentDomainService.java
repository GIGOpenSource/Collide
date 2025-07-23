package com.gig.collide.comment.domain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.comment.constant.CommentStatus;
import com.gig.collide.api.comment.constant.CommentType;
import com.gig.collide.api.comment.request.CommentCreateRequest;
import com.gig.collide.base.exception.BizException;
import com.gig.collide.comment.domain.entity.Comment;
import com.gig.collide.comment.infrastructure.exception.CommentErrorCode;
import com.gig.collide.comment.infrastructure.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 评论领域服务
 * 实现评论相关的核心业务逻辑
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentDomainService {

    private final CommentMapper commentMapper;

    /**
     * 创建评论
     *
     * @param createRequest 创建请求
     * @return 评论实体
     */
    @Transactional(rollbackFor = Exception.class)
    public Comment createComment(CommentCreateRequest createRequest) {
        // 1. 参数校验
        validateCreateRequest(createRequest);

        // 2. 构建评论实体
        Comment comment = buildComment(createRequest);

        // 3. 设置根评论ID（如果是回复评论）
        if (createRequest.getParentCommentId() != null) {
            setupRootCommentId(comment, createRequest);
        }

        // 4. 保存评论
        int insertResult = commentMapper.insert(comment);
        if (insertResult <= 0) {
            throw new BizException(CommentErrorCode.COMMENT_CREATE_FAILED);
        }

        // 5. 更新父评论回复数
        if (comment.getParentCommentId() != null) {
            updateParentReplyCount(comment.getParentCommentId(), 1);
        }

        log.info("评论创建成功，评论ID: {}, 用户ID: {}", comment.getId(), comment.getUserId());
        return comment;
    }

    /**
     * 删除评论（软删除）
     *
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComment(Long commentId, Long userId) {
        // 1. 查询评论
        Comment comment = getCommentById(commentId);
        if (comment == null) {
            throw new BizException(CommentErrorCode.COMMENT_NOT_FOUND);
        }

        // 2. 权限检查
        if (!comment.isDeletable(userId, false)) { // TODO: 需要传入管理员状态
            throw new BizException(CommentErrorCode.COMMENT_DELETE_PERMISSION_DENIED);
        }

        // 3. 软删除评论
        comment.setStatus(CommentStatus.DELETED);
        int updateResult = commentMapper.updateById(comment);
        if (updateResult <= 0) {
            throw new BizException(CommentErrorCode.COMMENT_DELETE_FAILED);
        }

        // 4. 更新父评论回复数
        if (comment.getParentCommentId() != null) {
            updateParentReplyCount(comment.getParentCommentId(), -1);
        }

        // 5. 删除子评论（递归软删除）
        deleteChildComments(commentId);

        log.info("评论删除成功，评论ID: {}, 用户ID: {}", commentId, userId);
        return true;
    }

    /**
     * 根据ID查询评论
     *
     * @param commentId 评论ID
     * @return 评论实体
     */
    public Comment getCommentById(Long commentId) {
        if (commentId == null) {
            return null;
        }
        return commentMapper.selectById(commentId);
    }

    /**
     * 分页查询评论
     *
     * @param commentType 评论类型
     * @param targetId 目标ID
     * @param parentCommentId 父评论ID
     * @param pageNum 页码
     * @param pageSize 页大小
     * @param sortBy 排序字段
     * @param sortOrder 排序方向
     * @return 评论分页结果
     */
    public IPage<Comment> pageQueryComments(CommentType commentType, Long targetId, 
                                          Long parentCommentId, Integer pageNum, 
                                          Integer pageSize, String sortBy, String sortOrder) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        return commentMapper.selectCommentPage(page, commentType, targetId, 
                                             parentCommentId, CommentStatus.NORMAL, 
                                             sortBy, sortOrder);
    }

    /**
     * 查询评论树
     *
     * @param commentType 评论类型
     * @param targetId 目标ID
     * @param pageNum 页码
     * @param pageSize 页大小
     * @param sortBy 排序字段
     * @param sortOrder 排序方向
     * @return 评论树分页结果
     */
    public IPage<Comment> queryCommentTree(CommentType commentType, Long targetId,
                                         Integer pageNum, Integer pageSize, 
                                         String sortBy, String sortOrder) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        return commentMapper.selectCommentTree(page, commentType, targetId, 
                                             CommentStatus.NORMAL, sortBy, sortOrder);
    }

    /**
     * 根据根评论ID查询子评论
     *
     * @param rootCommentId 根评论ID
     * @return 子评论列表
     */
    public List<Comment> getChildComments(Long rootCommentId) {
        return commentMapper.selectChildComments(rootCommentId, CommentStatus.NORMAL);
    }

    /**
     * 统计评论数量
     *
     * @param commentType 评论类型
     * @param targetId 目标ID
     * @return 评论数量
     */
    public Long countComments(CommentType commentType, Long targetId) {
        return commentMapper.countCommentsByTarget(commentType, targetId, CommentStatus.NORMAL);
    }

    /**
     * 更新评论点赞数
     *
     * @param commentId 评论ID
     * @param increment 增量
     * @return 是否更新成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCommentLikeCount(Long commentId, Integer increment) {
        int updateResult = commentMapper.updateLikeCount(commentId, increment);
        return updateResult > 0;
    }

    // ==================== 私有方法 ====================

    /**
     * 校验创建请求
     */
    private void validateCreateRequest(CommentCreateRequest createRequest) {
        if (createRequest == null) {
            throw new BizException(CommentErrorCode.INVALID_PARAMETER);
        }
        if (createRequest.getCommentType() == null) {
            throw new BizException(CommentErrorCode.COMMENT_TYPE_REQUIRED);
        }
        if (createRequest.getTargetId() == null) {
            throw new BizException(CommentErrorCode.TARGET_ID_REQUIRED);
        }
        if (!StringUtils.hasText(createRequest.getContent())) {
            throw new BizException(CommentErrorCode.COMMENT_CONTENT_REQUIRED);
        }
        if (createRequest.getUserId() == null) {
            throw new BizException(CommentErrorCode.USER_ID_REQUIRED);
        }
    }

    /**
     * 构建评论实体
     */
    private Comment buildComment(CommentCreateRequest createRequest) {
        Comment comment = new Comment();
        comment.setCommentType(createRequest.getCommentType());
        comment.setTargetId(createRequest.getTargetId());
        comment.setParentCommentId(createRequest.getParentCommentId());
        comment.setContent(createRequest.getContent());
        comment.setUserId(createRequest.getUserId());
        comment.setReplyToUserId(createRequest.getReplyToUserId());
        comment.setStatus(CommentStatus.NORMAL);
        comment.setLikeCount(0);
        comment.setReplyCount(0);
        return comment;
    }

    /**
     * 设置根评论ID
     */
    private void setupRootCommentId(Comment comment, CommentCreateRequest createRequest) {
        if (createRequest.getRootCommentId() != null) {
            // 如果指定了根评论ID，直接使用
            comment.setRootCommentId(createRequest.getRootCommentId());
        } else {
            // 否则查询父评论的根评论ID
            Comment parentComment = getCommentById(createRequest.getParentCommentId());
            if (parentComment == null) {
                throw new BizException(CommentErrorCode.PARENT_COMMENT_NOT_FOUND);
            }
            // 如果父评论有根评论ID，使用父评论的根评论ID；否则使用父评论ID作为根评论ID
            Long rootCommentId = parentComment.getRootCommentId() != null ? 
                                parentComment.getRootCommentId() : parentComment.getId();
            comment.setRootCommentId(rootCommentId);
        }
    }

    /**
     * 更新父评论回复数
     */
    private void updateParentReplyCount(Long parentCommentId, Integer increment) {
        try {
            commentMapper.updateReplyCount(parentCommentId, increment);
        } catch (Exception e) {
            log.warn("更新父评论回复数失败，父评论ID: {}, 增量: {}", parentCommentId, increment, e);
        }
    }

    /**
     * 删除子评论（递归软删除）
     */
    private void deleteChildComments(Long commentId) {
        try {
            // 查询所有子评论
            LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Comment::getParentCommentId, commentId)
                       .eq(Comment::getStatus, CommentStatus.NORMAL);
            List<Comment> childComments = commentMapper.selectList(queryWrapper);

            // 递归删除子评论
            for (Comment childComment : childComments) {
                childComment.setStatus(CommentStatus.DELETED);
                commentMapper.updateById(childComment);
                deleteChildComments(childComment.getId()); // 递归删除
            }
        } catch (Exception e) {
            log.warn("删除子评论失败，父评论ID: {}", commentId, e);
        }
    }
} 