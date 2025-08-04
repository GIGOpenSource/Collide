package com.gig.collide.comment.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.comment.CommentFacadeService;
import com.gig.collide.api.comment.request.CommentCreateRequest;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论门面服务实现 - 规范版
 * 完整实现所有评论功能：基础操作、高级查询、统计分析、管理功能
 * 支持评论类型：CONTENT、DYNAMIC
 * 
 * @author Collide
 * @version 5.0.0 (与Content模块一致版)
 * @since 2024-01-01
 */
@Slf4j
@DubboService(version = "5.0.0", timeout = 5000)
@RequiredArgsConstructor
public class CommentFacadeServiceImpl implements CommentFacadeService {

    private final CommentService commentService;

    // =================== 评论基础操作 ===================

    @Override
    public Result<CommentResponse> createComment(CommentCreateRequest request) {
        log.info("创建评论: {}", request);
        
        try {
            // 参数验证
            if (request == null) {
                return Result.error("INVALID_REQUEST", "请求参数不能为空");
            }
            
            // 转换为实体
            Comment comment = new Comment();
            BeanUtils.copyProperties(request, comment);
            
            // 调用服务层
            Comment createdComment = commentService.createComment(comment);
            
            // 转换为响应对象
            CommentResponse response = convertToResponse(createdComment);
            
            log.info("评论创建成功: {}", createdComment.getId());
            return Result.success(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("创建评论参数错误: {}", e.getMessage());
            return Result.error("COMMENT_CREATE_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("创建评论失败", e);
            return Result.error("COMMENT_CREATE_ERROR", "创建评论失败");
        }
    }

    @Override
    public Result<CommentResponse> updateComment(CommentUpdateRequest request) {
        log.info("更新评论: {}", request);
        
        try {
            // 参数验证
            if (request == null || request.getId() == null) {
                return Result.error("INVALID_COMMENT_ID", "评论ID不能为空");
            }
            
            // 转换为实体
            Comment comment = new Comment();
            BeanUtils.copyProperties(request, comment);
            
            // 调用服务层
            Comment updatedComment = commentService.updateComment(comment);
            
            // 转换为响应对象
            CommentResponse response = convertToResponse(updatedComment);
            
            log.info("评论更新成功: {}", updatedComment.getId());
            return Result.success(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("更新评论参数错误: {}", e.getMessage());
            return Result.error("COMMENT_UPDATE_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("更新评论失败", e);
            return Result.error("COMMENT_UPDATE_ERROR", "更新评论失败");
        }
    }

    @Override
    public Result<Void> deleteComment(Long commentId, Long userId) {
        log.info("删除评论: commentId={}, userId={}", commentId, userId);
        
        try {
            // 参数验证
            if (commentId == null || userId == null) {
                return Result.error("INVALID_PARAMETERS", "评论ID和用户ID不能为空");
            }
            
            // 调用服务层
            boolean success = commentService.deleteComment(commentId, userId);
            
            if (success) {
                log.info("评论删除成功: {}", commentId);
                return Result.success();
            } else {
                return Result.error("COMMENT_DELETE_ERROR", "删除评论失败");
            }
            
        } catch (IllegalArgumentException e) {
            log.warn("删除评论参数错误: {}", e.getMessage());
            return Result.error("COMMENT_DELETE_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("删除评论失败", e);
            return Result.error("COMMENT_DELETE_ERROR", "删除评论失败");
        }
    }

    @Override
    public Result<CommentResponse> getCommentById(Long commentId) {
        log.info("获取评论详情: commentId={}", commentId);
        
        try {
            // 参数验证
            if (commentId == null) {
                return Result.error("INVALID_COMMENT_ID", "评论ID不能为空");
            }
            
            // 调用服务层
            Comment comment = commentService.getCommentById(commentId);
            
            if (comment == null) {
                return Result.error("COMMENT_NOT_FOUND", "评论不存在");
            }
            
            // 转换为响应对象
            CommentResponse response = convertToResponse(comment);
            
            log.info("获取评论详情成功: {}", commentId);
            return Result.success(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("获取评论详情参数错误: {}", e.getMessage());
            return Result.error("COMMENT_GET_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("获取评论详情失败", e);
            return Result.error("COMMENT_GET_ERROR", "获取评论详情失败");
        }
    }

    // =================== 目标对象评论查询 ===================

    @Override
    public Result<PageResponse<CommentResponse>> getTargetComments(Long targetId, String commentType, 
                                                                 Long parentCommentId, Integer currentPage, Integer pageSize) {
        log.info("获取目标评论: targetId={}, commentType={}, parentCommentId={}, currentPage={}, pageSize={}", 
                targetId, commentType, parentCommentId, currentPage, pageSize);
        
        try {
            // 参数验证
            if (targetId == null) {
                return Result.error("INVALID_TARGET_ID", "目标ID不能为空");
            }
            
            // 调用服务层
            IPage<Comment> page = commentService.getTargetComments(targetId, commentType, parentCommentId, currentPage, pageSize);
            
            // 转换为响应对象
            PageResponse<CommentResponse> response = convertToPageResponse(page);
            
            log.info("获取目标评论成功: 总数={}", response.getTotal());
            return Result.success(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("获取目标评论参数错误: {}", e.getMessage());
            return Result.error("COMMENT_OPERATION_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("获取目标评论失败", e);
            return Result.error("TARGET_COMMENTS_ERROR", "获取目标评论失败");
        }
    }

    @Override
    public Result<PageResponse<CommentResponse>> getCommentReplies(Long parentCommentId, Integer currentPage, Integer pageSize) {
        log.info("获取评论回复: parentCommentId={}, currentPage={}, pageSize={}", parentCommentId, currentPage, pageSize);
        
        try {
            // 参数验证
            if (parentCommentId == null || parentCommentId <= 0) {
                return Result.error("INVALID_PARENT_ID", "父评论ID不能为空");
            }
            
            // 调用服务层
            IPage<Comment> page = commentService.getCommentReplies(parentCommentId, currentPage, pageSize);
            
            // 转换为响应对象
            PageResponse<CommentResponse> response = convertToPageResponse(page);
            
            log.info("获取评论回复成功: 总数={}", response.getTotal());
            return Result.success(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("获取评论回复参数错误: {}", e.getMessage());
            return Result.error("COMMENT_OPERATION_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("获取评论回复失败", e);
            return Result.error("COMMENT_REPLIES_ERROR", "获取评论回复失败");
        }
    }

    @Override
    public Result<PageResponse<CommentResponse>> getCommentTree(Long targetId, String commentType, 
                                                              Integer maxDepth, Integer currentPage, Integer pageSize) {
        log.info("获取评论树: targetId={}, commentType={}, maxDepth={}, currentPage={}, pageSize={}", 
                targetId, commentType, maxDepth, currentPage, pageSize);
        
        try {
            // 参数验证
            if (targetId == null) {
                return Result.error("INVALID_TARGET_ID", "目标ID不能为空");
            }
            
            // 调用服务层
            IPage<Comment> page = commentService.getCommentTree(targetId, commentType, maxDepth, currentPage, pageSize);
            
            // 转换为响应对象
            PageResponse<CommentResponse> response = convertToPageResponse(page);
            
            log.info("获取评论树成功: 总数={}", response.getTotal());
            return Result.success(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("获取评论树参数错误: {}", e.getMessage());
            return Result.error("COMMENT_OPERATION_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("获取评论树失败", e);
            return Result.error("COMMENT_TREE_ERROR", "获取评论树失败");
        }
    }

    // =================== 用户评论查询 ===================

    @Override
    public Result<PageResponse<CommentResponse>> getUserComments(Long userId, String commentType, 
                                                               Integer currentPage, Integer pageSize) {
        log.info("获取用户评论: userId={}, commentType={}, currentPage={}, pageSize={}", 
                userId, commentType, currentPage, pageSize);
        
        try {
            // 参数验证
            if (userId == null) {
                return Result.error("INVALID_USER_ID", "用户ID不能为空");
            }
            
            // 调用服务层
            IPage<Comment> page = commentService.getUserComments(userId, commentType, currentPage, pageSize);
            
            // 转换为响应对象
            PageResponse<CommentResponse> response = convertToPageResponse(page);
            
            log.info("获取用户评论成功: 总数={}", response.getTotal());
            return Result.success(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("获取用户评论参数错误: {}", e.getMessage());
            return Result.error("COMMENT_OPERATION_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("获取用户评论失败", e);
            return Result.error("USER_COMMENTS_ERROR", "获取用户评论失败");
        }
    }

    @Override
    public Result<PageResponse<CommentResponse>> getUserReplies(Long userId, Integer currentPage, Integer pageSize) {
        log.info("获取用户回复: userId={}, currentPage={}, pageSize={}", userId, currentPage, pageSize);
        
        try {
            // 参数验证
            if (userId == null) {
                return Result.error("INVALID_USER_ID", "用户ID不能为空");
            }
            
            // 调用服务层
            IPage<Comment> page = commentService.getUserReplies(userId, currentPage, pageSize);
            
            // 转换为响应对象
            PageResponse<CommentResponse> response = convertToPageResponse(page);
            
            log.info("获取用户回复成功: 总数={}", response.getTotal());
            return Result.success(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("获取用户回复参数错误: {}", e.getMessage());
            return Result.error("COMMENT_OPERATION_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("获取用户回复失败", e);
            return Result.error("USER_REPLIES_ERROR", "获取用户回复失败");
        }
    }

    // =================== 统计功能 ===================

    @Override
    public Result<Integer> increaseLikeCount(Long commentId, Integer increment) {
        log.info("增加评论点赞数: commentId={}, increment={}", commentId, increment);
        
        try {
            // 参数验证
            if (commentId == null || increment == null) {
                return Result.error("INVALID_PARAMETERS", "评论ID和增量不能为空");
            }
            
            // 调用服务层
            int result = commentService.increaseLikeCount(commentId, increment);
            
            log.info("增加评论点赞数成功: commentId={}, result={}", commentId, result);
            return Result.success(result);
            
        } catch (IllegalArgumentException e) {
            log.warn("增加评论点赞数参数错误: {}", e.getMessage());
            return Result.error("COMMENT_OPERATION_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("增加评论点赞数失败", e);
            return Result.error("LIKE_COUNT_ERROR", "增加评论点赞数失败");
        }
    }

    @Override
    public Result<Integer> increaseReplyCount(Long commentId, Integer increment) {
        log.info("增加回复数: commentId={}, increment={}", commentId, increment);
        
        try {
            // 参数验证
            if (commentId == null || increment == null) {
                return Result.error("INVALID_PARAMETERS", "评论ID和增量不能为空");
            }
            
            // 调用服务层
            int result = commentService.increaseReplyCount(commentId, increment);
            
            log.info("增加回复数成功: commentId={}, result={}", commentId, result);
            return Result.success(result);
            
        } catch (IllegalArgumentException e) {
            log.warn("增加回复数参数错误: {}", e.getMessage());
            return Result.error("COMMENT_OPERATION_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("增加回复数失败", e);
            return Result.error("REPLY_COUNT_ERROR", "增加回复数失败");
        }
    }

    @Override
    public Result<Long> countTargetComments(Long targetId, String commentType) {
        log.info("统计目标评论数: targetId={}, commentType={}", targetId, commentType);
        
        try {
            // 参数验证
            if (targetId == null) {
                return Result.error("INVALID_TARGET_ID", "目标ID不能为空");
            }
            
            // 调用服务层
            long count = commentService.countTargetComments(targetId, commentType);
            
            log.info("统计目标评论数成功: count={}", count);
            return Result.success(count);
            
        } catch (IllegalArgumentException e) {
            log.warn("统计目标评论数参数错误: {}", e.getMessage());
            return Result.error("COMMENT_OPERATION_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("统计目标评论数失败", e);
            return Result.error("COUNT_TARGET_COMMENTS_ERROR", "统计目标评论数失败");
        }
    }

    @Override
    public Result<Long> countUserComments(Long userId, String commentType) {
        log.info("统计用户评论数: userId={}, commentType={}", userId, commentType);
        
        try {
            // 参数验证
            if (userId == null) {
                return Result.error("INVALID_USER_ID", "用户ID不能为空");
            }
            
            // 调用服务层
            long count = commentService.countUserComments(userId, commentType);
            
            log.info("统计用户评论数成功: count={}", count);
            return Result.success(count);
            
        } catch (IllegalArgumentException e) {
            log.warn("统计用户评论数参数错误: {}", e.getMessage());
            return Result.error("COMMENT_OPERATION_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("统计用户评论数失败", e);
            return Result.error("COUNT_USER_COMMENTS_ERROR", "统计用户评论数失败");
        }
    }

    // =================== 高级功能 ===================

    @Override
    public Result<PageResponse<CommentResponse>> searchComments(String keyword, String commentType, 
                                                              Long targetId, Integer currentPage, Integer pageSize) {
        log.info("搜索评论: keyword={}, commentType={}, targetId={}, currentPage={}, pageSize={}", 
                keyword, commentType, targetId, currentPage, pageSize);
        
        try {
            // 参数验证
            if (keyword == null || keyword.trim().isEmpty()) {
                return Result.error("INVALID_KEYWORD", "搜索关键词不能为空");
            }
            
            // 调用服务层
            IPage<Comment> page = commentService.searchComments(keyword, commentType, targetId, currentPage, pageSize);
            
            // 转换为响应对象
            PageResponse<CommentResponse> response = convertToPageResponse(page);
            
            log.info("搜索评论成功: 总数={}", response.getTotal());
            return Result.success(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("搜索评论参数错误: {}", e.getMessage());
            return Result.error("COMMENT_OPERATION_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("搜索评论失败", e);
            return Result.error("SEARCH_COMMENTS_ERROR", "搜索评论失败");
        }
    }

    @Override
    public Result<PageResponse<CommentResponse>> getPopularComments(Long targetId, String commentType, 
                                                                  Integer timeRange, Integer currentPage, Integer pageSize) {
        log.info("获取热门评论: targetId={}, commentType={}, timeRange={}, currentPage={}, pageSize={}", 
                targetId, commentType, timeRange, currentPage, pageSize);
        
        try {
            // 调用服务层
            IPage<Comment> page = commentService.getPopularComments(targetId, commentType, timeRange, currentPage, pageSize);
            
            // 转换为响应对象
            PageResponse<CommentResponse> response = convertToPageResponse(page);
            
            log.info("获取热门评论成功: 总数={}", response.getTotal());
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("获取热门评论失败", e);
            return Result.error("POPULAR_COMMENTS_ERROR", "获取热门评论失败");
        }
    }

    @Override
    public Result<PageResponse<CommentResponse>> getLatestComments(Long targetId, String commentType, 
                                                                 Integer currentPage, Integer pageSize) {
        log.info("获取最新评论: targetId={}, commentType={}, currentPage={}, pageSize={}", 
                targetId, commentType, currentPage, pageSize);
        
        try {
            // 调用服务层
            IPage<Comment> page = commentService.getLatestComments(targetId, commentType, currentPage, pageSize);
            
            // 转换为响应对象
            PageResponse<CommentResponse> response = convertToPageResponse(page);
            
            log.info("获取最新评论成功: 总数={}", response.getTotal());
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("获取最新评论失败", e);
            return Result.error("LATEST_COMMENTS_ERROR", "获取最新评论失败");
        }
    }

    @Override
    public Result<PageResponse<CommentResponse>> getCommentsByLikeCountRange(Integer minLikeCount, Integer maxLikeCount,
                                                                           String commentType, Long targetId,
                                                                           Integer currentPage, Integer pageSize) {
        try {
            log.debug("根据点赞数范围查询评论: minLike={}, maxLike={}, commentType={}, targetId={}, page={}/{}", 
                     minLikeCount, maxLikeCount, commentType, targetId, currentPage, pageSize);
            
            // 调用服务层
            IPage<Comment> page = commentService.getCommentsByLikeCountRange(minLikeCount, maxLikeCount, 
                    commentType, targetId, currentPage, pageSize);
            
            // 转换为响应对象
            PageResponse<CommentResponse> response = convertToPageResponse(page);
            
            log.info("根据点赞数范围查询评论成功: 总数={}", response.getTotal());
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("根据点赞数范围查询评论失败", e);
            return Result.error("LIKE_COUNT_RANGE_ERROR", "根据点赞数范围查询评论失败");
        }
    }

    @Override
    public Result<PageResponse<CommentResponse>> getCommentsByTimeRange(LocalDateTime startTime, LocalDateTime endTime,
                                                                       String commentType, Long targetId,
                                                                       Integer currentPage, Integer pageSize) {
        try {
            log.debug("根据时间范围查询评论: startTime={}, endTime={}, commentType={}, targetId={}, page={}/{}", 
                     startTime, endTime, commentType, targetId, currentPage, pageSize);
            
            // 调用服务层
            IPage<Comment> page = commentService.getCommentsByTimeRange(startTime, endTime, 
                    commentType, targetId, currentPage, pageSize);
            
            // 转换为响应对象
            PageResponse<CommentResponse> response = convertToPageResponse(page);
            
            log.info("根据时间范围查询评论成功: 总数={}", response.getTotal());
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("根据时间范围查询评论失败", e);
            return Result.error("TIME_RANGE_ERROR", "根据时间范围查询评论失败");
        }
    }

    // =================== 数据分析功能 ===================

    @Override
    public Result<Map<String, Object>> getCommentStatistics(Long targetId, String commentType, Long userId,
                                                           LocalDateTime startTime, LocalDateTime endTime) {
        try {
            log.debug("获取评论统计信息: targetId={}, commentType={}, userId={}, startTime={}, endTime={}", 
                     targetId, commentType, userId, startTime, endTime);
            
            // 调用服务层
            Map<String, Object> statistics = commentService.getCommentStatistics(targetId, commentType, userId, 
                    startTime, endTime);
            
            log.info("获取评论统计信息成功: {}", statistics);
            return Result.success(statistics);
            
        } catch (Exception e) {
            log.error("获取评论统计信息失败", e);
            return Result.error("COMMENT_STATISTICS_ERROR", "获取评论统计信息失败");
        }
    }

    @Override
    public Result<List<Map<String, Object>>> getUserReplyRelations(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            log.debug("查询用户回复关系: userId={}, startTime={}, endTime={}", userId, startTime, endTime);
            
            // 调用服务层
            List<Map<String, Object>> relations = commentService.getUserReplyRelations(userId, startTime, endTime);
            
            log.info("查询用户回复关系成功: 总数={}", relations.size());
            return Result.success(relations);
            
        } catch (Exception e) {
            log.error("查询用户回复关系失败", e);
            return Result.error("USER_REPLY_RELATIONS_ERROR", "查询用户回复关系失败");
        }
    }

    @Override
    public Result<List<Map<String, Object>>> getCommentHotRanking(String commentType, Long targetId,
                                                                 LocalDateTime startTime, LocalDateTime endTime, Integer limit) {
        try {
            log.debug("查询评论热度排行: commentType={}, targetId={}, startTime={}, endTime={}, limit={}", 
                     commentType, targetId, startTime, endTime, limit);
            
            // 调用服务层
            List<Map<String, Object>> ranking = commentService.getCommentHotRanking(commentType, targetId, 
                    startTime, endTime, limit);
            
            log.info("查询评论热度排行成功: 总数={}", ranking.size());
            return Result.success(ranking);
            
        } catch (Exception e) {
            log.error("查询评论热度排行失败", e);
            return Result.error("COMMENT_HOT_RANKING_ERROR", "查询评论热度排行失败");
        }
    }

    // =================== 管理功能（需要管理员权限） ===================

    @Override
    public Result<Integer> batchUpdateCommentStatus(List<Long> commentIds, String status) {
        try {
            log.debug("批量更新评论状态: commentIds={}, status={}", commentIds, status);
            
            // 参数验证
            if (commentIds == null || commentIds.isEmpty()) {
                return Result.error("INVALID_PARAMETERS", "评论ID列表不能为空");
            }
            
            // 调用服务层
            int result = commentService.batchUpdateCommentStatus(commentIds, status);
            
            log.info("批量更新评论状态成功: 影响行数={}", result);
            return Result.success(result);
            
        } catch (IllegalArgumentException e) {
            log.warn("批量更新评论状态参数错误: {}", e.getMessage());
            return Result.error("BATCH_UPDATE_STATUS_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("批量更新评论状态失败", e);
            return Result.error("BATCH_UPDATE_STATUS_ERROR", "批量更新评论状态失败");
        }
    }

    @Override
    public Result<Integer> batchDeleteTargetComments(Long targetId, String commentType) {
        try {
            log.debug("批量删除目标评论: targetId={}, commentType={}", targetId, commentType);
            
            // 参数验证
            if (targetId == null) {
                return Result.error("INVALID_TARGET_ID", "目标ID不能为空");
            }
            
            // 调用服务层
            int result = commentService.batchDeleteTargetComments(targetId, commentType);
            
            log.info("批量删除目标评论成功: 影响行数={}", result);
            return Result.success(result);
            
        } catch (IllegalArgumentException e) {
            log.warn("批量删除目标评论参数错误: {}", e.getMessage());
            return Result.error("BATCH_DELETE_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("批量删除目标评论失败", e);
            return Result.error("BATCH_DELETE_ERROR", "批量删除目标评论失败");
        }
    }

    @Override
    public Result<Integer> updateUserInfo(Long userId, String nickname, String avatar) {
        try {
            log.debug("更新用户信息: userId={}, nickname={}, avatar={}", userId, nickname, avatar);
            
            // 参数验证
            if (userId == null) {
                return Result.error("INVALID_USER_ID", "用户ID不能为空");
            }
            
            // 调用服务层
            int result = commentService.updateUserInfo(userId, nickname, avatar);
            
            log.info("更新用户信息成功: 影响行数={}", result);
            return Result.success(result);
            
        } catch (IllegalArgumentException e) {
            log.warn("更新用户信息参数错误: {}", e.getMessage());
            return Result.error("UPDATE_USER_INFO_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            return Result.error("UPDATE_USER_INFO_ERROR", "更新用户信息失败");
        }
    }

    @Override
    public Result<Integer> updateReplyToUserInfo(Long replyToUserId, String nickname, String avatar) {
        try {
            log.debug("更新回复目标用户信息: replyToUserId={}, nickname={}, avatar={}", replyToUserId, nickname, avatar);
            
            // 参数验证
            if (replyToUserId == null) {
                return Result.error("INVALID_REPLY_TO_USER_ID", "回复目标用户ID不能为空");
            }
            
            // 调用服务层
            int result = commentService.updateReplyToUserInfo(replyToUserId, nickname, avatar);
            
            log.info("更新回复目标用户信息成功: 影响行数={}", result);
            return Result.success(result);
            
        } catch (IllegalArgumentException e) {
            log.warn("更新回复目标用户信息参数错误: {}", e.getMessage());
            return Result.error("UPDATE_REPLY_TO_USER_INFO_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("更新回复目标用户信息失败", e);
            return Result.error("UPDATE_REPLY_TO_USER_INFO_ERROR", "更新回复目标用户信息失败");
        }
    }

    @Override
    public Result<Integer> cleanDeletedComments(Integer days, Integer limit) {
        try {
            log.debug("清理已删除评论: days={}, limit={}", days, limit);
            
            // 调用服务层
            int result = commentService.cleanDeletedComments(days, limit);
            
            log.info("清理已删除评论成功: 删除数量={}", result);
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("清理已删除评论失败", e);
            return Result.error("CLEAN_DELETED_COMMENTS_ERROR", "清理已删除评论失败");
        }
    }

    // =================== 私有方法 ===================

    /**
     * 转换为响应对象
     */
    private CommentResponse convertToResponse(Comment comment) {
        if (comment == null) {
            return null;
        }
        
        CommentResponse response = new CommentResponse();
        BeanUtils.copyProperties(comment, response);
        return response;
    }

    /**
     * 转换为分页响应对象
     */
    private PageResponse<CommentResponse> convertToPageResponse(IPage<Comment> page) {
        if (page == null) {
            return new PageResponse<>();
        }
        
        List<CommentResponse> records = page.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        PageResponse<CommentResponse> response = new PageResponse<>();
        response.setDatas(records);
        response.setTotal(page.getTotal());
        response.setCurrentPage((int) page.getCurrent());
        response.setPageSize((int) page.getSize());
        response.setTotalPage((int) page.getPages());
        
        return response;
    }
}