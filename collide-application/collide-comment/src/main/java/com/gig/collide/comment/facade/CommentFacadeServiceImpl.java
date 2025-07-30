package com.gig.collide.comment.facade;

import com.gig.collide.api.comment.CommentFacadeService;
import com.gig.collide.api.comment.request.CommentCreateRequest;
import com.gig.collide.api.comment.request.CommentQueryRequest;
import com.gig.collide.api.comment.request.CommentUpdateRequest;
import com.gig.collide.api.comment.response.CommentResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.comment.domain.entity.Comment;
import com.gig.collide.comment.domain.service.CommentService;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论门面服务实现 - 简洁版
 * Dubbo服务实现，处理API请求和响应转换
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class CommentFacadeServiceImpl implements CommentFacadeService {

    private final CommentService commentService;

    @Override
    public Result<CommentResponse> createComment(CommentCreateRequest request) {
        try {
            log.info("创建评论请求：{}", request);
            
            // 转换为实体
            Comment comment = convertToEntity(request);
            
            // 创建评论
            Comment created = commentService.createComment(comment);
            
            // 转换为响应
            CommentResponse response = convertToResponse(created);
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("创建评论失败", e);
            return Result.error("COMMENT_CREATE_ERROR", "评论创建失败：" + e.getMessage());
        }
    }

    @Override
    public Result<CommentResponse> updateComment(CommentUpdateRequest request) {
        try {
            log.info("更新评论请求：{}", request);
            
            // 转换为实体
            Comment comment = new Comment();
            comment.setId(request.getId());
            if (request.getContent() != null) {
                comment.setContent(request.getContent());
            }
            if (request.getStatus() != null) {
                comment.setStatus(request.getStatus());
            }
            if (request.getLikeCount() != null) {
                comment.setLikeCount(request.getLikeCount());
            }
            if (request.getReplyCount() != null) {
                comment.setReplyCount(request.getReplyCount());
            }
            
            // 更新评论
            Comment updated = commentService.updateComment(comment);
            
            // 转换为响应
            CommentResponse response = convertToResponse(updated);
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("更新评论失败", e);
            return Result.error("COMMENT_UPDATE_ERROR", "评论更新失败：" + e.getMessage());
        }
    }

    @Override
    public Result<Void> deleteComment(Long commentId, Long userId) {
        try {
            log.info("删除评论，ID：{}，用户：{}", commentId, userId);
            
            boolean success = commentService.deleteComment(commentId, userId);
            
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("COMMENT_DELETE_ERROR", "评论删除失败");
            }
        } catch (Exception e) {
            log.error("删除评论失败", e);
            return Result.error("COMMENT_DELETE_ERROR", "评论删除失败：" + e.getMessage());
        }
    }

    @Override
    public Result<CommentResponse> getCommentById(Long commentId, Boolean includeDeleted) {
        try {
            Comment comment = commentService.getCommentById(commentId, includeDeleted);
            
            if (comment == null) {
                return Result.error("COMMENT_NOT_FOUND", "评论不存在");
            }
            
            CommentResponse response = convertToResponse(comment);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取评论失败", e);
            return Result.error("COMMENT_GET_ERROR", "获取评论失败：" + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<CommentResponse>> queryComments(CommentQueryRequest request) {
        try {
            log.info("查询评论请求：{}", request);
            
            var page = commentService.queryComments(
                request.getTargetId(),
                request.getCommentType(),
                request.getActualParentCommentId(),
                request.getStatus(),
                request.getCurrentPage(),
                request.getPageSize(),
                request.getOrderBy(),
                request.getOrderDirection()
            );
            
            PageResponse<CommentResponse> response = convertToPageResponse(page);
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询评论失败", e);
            return Result.error("COMMENT_QUERY_ERROR", "查询评论失败：" + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<CommentResponse>> getTargetComments(Long targetId, String commentType,
                                                                 Long parentCommentId, Integer pageNum, Integer pageSize) {
        try {
            var page = commentService.getTargetComments(targetId, commentType, parentCommentId,
                pageNum, pageSize, "create_time", "DESC");
            
            PageResponse<CommentResponse> response = convertToPageResponse(page);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取目标评论失败", e);
            return Result.error("COMMENT_GET_ERROR", "获取目标评论失败：" + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<CommentResponse>> getCommentReplies(Long parentCommentId, Integer pageNum, Integer pageSize) {
        try {
            var page = commentService.getCommentReplies(parentCommentId, pageNum, pageSize,
                "create_time", "ASC");
            
            PageResponse<CommentResponse> response = convertToPageResponse(page);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取评论回复失败", e);
            return Result.error("COMMENT_GET_ERROR", "获取评论回复失败：" + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<CommentResponse>> getCommentTree(Long targetId, String commentType,
                                                              Integer maxDepth, Integer pageNum, Integer pageSize) {
        try {
            var page = commentService.getCommentTree(targetId, commentType, maxDepth,
                pageNum, pageSize, "create_time", "DESC");
            
            PageResponse<CommentResponse> response = convertToPageResponse(page);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取评论树失败", e);
            return Result.error("COMMENT_GET_ERROR", "获取评论树失败：" + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<CommentResponse>> getUserComments(Long userId, String commentType,
                                                               String status, Integer pageNum, Integer pageSize) {
        try {
            var page = commentService.getUserComments(userId, commentType, status,
                pageNum, pageSize, "create_time", "DESC");
            
            PageResponse<CommentResponse> response = convertToPageResponse(page);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取用户评论失败", e);
            return Result.error("COMMENT_GET_ERROR", "获取用户评论失败：" + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<CommentResponse>> getUserReplies(Long userId, Integer pageNum, Integer pageSize) {
        try {
            var page = commentService.getUserReplies(userId, pageNum, pageSize,
                "create_time", "DESC");
            
            PageResponse<CommentResponse> response = convertToPageResponse(page);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取用户回复失败", e);
            return Result.error("COMMENT_GET_ERROR", "获取用户回复失败：" + e.getMessage());
        }
    }

    @Override
    public Result<Void> updateCommentStatus(Long commentId, String status, Long operatorId) {
        try {
            boolean success = commentService.updateCommentStatus(commentId, status, operatorId);
            
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("COMMENT_UPDATE_ERROR", "评论状态更新失败");
            }
        } catch (Exception e) {
            log.error("更新评论状态失败", e);
            return Result.error("COMMENT_UPDATE_ERROR", "评论状态更新失败：" + e.getMessage());
        }
    }

    @Override
    public Result<Integer> batchUpdateCommentStatus(List<Long> commentIds, String status, Long operatorId) {
        try {
            int count = commentService.batchUpdateCommentStatus(commentIds, status, operatorId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("批量更新评论状态失败", e);
            return Result.error("COMMENT_UPDATE_ERROR", "批量更新评论状态失败：" + e.getMessage());
        }
    }

    @Override
    public Result<Void> hideComment(Long commentId, Long operatorId) {
        return updateCommentStatus(commentId, "HIDDEN", operatorId);
    }

    @Override
    public Result<Void> restoreComment(Long commentId, Long operatorId) {
        return updateCommentStatus(commentId, "NORMAL", operatorId);
    }

    @Override
    public Result<Integer> increaseLikeCount(Long commentId, Integer increment) {
        try {
            int newCount = commentService.increaseLikeCount(commentId, increment);
            return Result.success(newCount);
        } catch (Exception e) {
            log.error("增加点赞数失败", e);
            return Result.error("COMMENT_UPDATE_ERROR", "增加点赞数失败：" + e.getMessage());
        }
    }

    @Override
    public Result<Integer> increaseReplyCount(Long commentId, Integer increment) {
        try {
            int newCount = commentService.increaseReplyCount(commentId, increment);
            return Result.success(newCount);
        } catch (Exception e) {
            log.error("增加回复数失败", e);
            return Result.error("COMMENT_UPDATE_ERROR", "增加回复数失败：" + e.getMessage());
        }
    }

    @Override
    public Result<Long> countTargetComments(Long targetId, String commentType, String status) {
        try {
            long count = commentService.countTargetComments(targetId, commentType, status);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计目标评论数失败", e);
            return Result.error("COMMENT_COUNT_ERROR", "统计目标评论数失败：" + e.getMessage());
        }
    }

    @Override
    public Result<Long> countUserComments(Long userId, String commentType, String status) {
        try {
            long count = commentService.countUserComments(userId, commentType, status);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计用户评论数失败", e);
            return Result.error("COMMENT_COUNT_ERROR", "统计用户评论数失败：" + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getCommentStatistics(Long commentId) {
        try {
            Map<String, Object> stats = commentService.getCommentStatistics(commentId);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取评论统计失败", e);
            return Result.error("COMMENT_STATS_ERROR", "获取评论统计失败：" + e.getMessage());
        }
    }

    @Override
    public Result<Integer> updateUserInfo(Long userId, String nickname, String avatar) {
        try {
            int count = commentService.updateUserInfo(userId, nickname, avatar);
            return Result.success(count);
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            return Result.error("COMMENT_UPDATE_ERROR", "更新用户信息失败：" + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<CommentResponse>> searchComments(String keyword, String commentType,
                                                              Long targetId, Integer pageNum, Integer pageSize) {
        try {
            var page = commentService.searchComments(keyword, commentType, targetId,
                pageNum, pageSize, "create_time", "DESC");
            
            PageResponse<CommentResponse> response = convertToPageResponse(page);
            return Result.success(response);
        } catch (Exception e) {
            log.error("搜索评论失败", e);
            return Result.error("COMMENT_SEARCH_ERROR", "搜索评论失败：" + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<CommentResponse>> getPopularComments(Long targetId, String commentType,
                                                                  Integer timeRange, Integer pageNum, Integer pageSize) {
        try {
            var page = commentService.getPopularComments(targetId, commentType, timeRange,
                pageNum, pageSize);
            
            PageResponse<CommentResponse> response = convertToPageResponse(page);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取热门评论失败", e);
            return Result.error("COMMENT_GET_ERROR", "获取热门评论失败：" + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<CommentResponse>> getLatestComments(Long targetId, String commentType,
                                                                 Integer pageNum, Integer pageSize) {
        try {
            var page = commentService.getLatestComments(targetId, commentType, pageNum, pageSize);
            
            PageResponse<CommentResponse> response = convertToPageResponse(page);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取最新评论失败", e);
            return Result.error("COMMENT_GET_ERROR", "获取最新评论失败：" + e.getMessage());
        }
    }

    @Override
    public Result<Integer> batchDeleteTargetComments(Long targetId, String commentType, Long operatorId) {
        try {
            int count = commentService.batchDeleteTargetComments(targetId, commentType, operatorId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("批量删除目标评论失败", e);
            return Result.error("COMMENT_DELETE_ERROR", "批量删除目标评论失败：" + e.getMessage());
        }
    }

    // =================== 转换方法 ===================

    private Comment convertToEntity(CommentCreateRequest request) {
        Comment comment = new Comment();
        BeanUtils.copyProperties(request, comment);
        return comment;
    }

    private CommentResponse convertToResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        BeanUtils.copyProperties(comment, response);
        return response;
    }

    private PageResponse<CommentResponse> convertToPageResponse(com.baomidou.mybatisplus.core.metadata.IPage<Comment> page) {
        List<CommentResponse> responses = page.getRecords().stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        PageResponse<CommentResponse> pageResponse = new PageResponse<>();
        pageResponse.setDatas(responses);
        pageResponse.setCurrentPage((int) page.getCurrent());
        pageResponse.setTotalPage((int) page.getPages());
        pageResponse.setTotal((int) page.getTotal());
        
        return pageResponse;
    }
}