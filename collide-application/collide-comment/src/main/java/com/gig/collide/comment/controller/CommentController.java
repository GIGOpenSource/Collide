package com.gig.collide.comment.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.gig.collide.api.comment.constant.CommentType;
import com.gig.collide.api.comment.request.CommentCreateRequest;
import com.gig.collide.api.comment.request.CommentQueryRequest;
import com.gig.collide.api.comment.response.CommentQueryResponse;
import com.gig.collide.api.comment.response.CommentResponse;
import com.gig.collide.api.comment.response.data.CommentInfo;
import com.gig.collide.api.comment.service.CommentFacadeService;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 评论控制器
 * 提供评论相关的 REST API
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@Validated
public class CommentController {

    private final CommentFacadeService commentFacadeService;

    /**
     * 创建评论
     *
     * @param createRequest 创建请求
     * @return 创建结果
     */
    @PostMapping
    @SaCheckLogin
    public Result<CommentResponse> createComment(@Valid @RequestBody CommentCreateRequest createRequest) {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            log.info("用户创建评论，用户ID: {}, 目标ID: {}", currentUserId, createRequest.getTargetId());

            // 设置当前用户ID
            createRequest.setUserId(currentUserId);

            CommentResponse response = commentFacadeService.createComment(createRequest);
            
            if (response.getSuccess()) {
                return Result.success(response);
            } else {
                return Result.error(response.getResponseCode(), response.getResponseMessage());
            }

        } catch (Exception e) {
            log.error("评论创建失败", e);
            return Result.error("COMMENT_CREATE_ERROR", "评论创建失败，请稍后重试");
        }
    }

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @return 删除结果
     */
    @DeleteMapping("/{commentId}")
    @SaCheckLogin
    public Result<CommentResponse> deleteComment(@PathVariable Long commentId) {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            log.info("用户删除评论，用户ID: {}, 评论ID: {}", currentUserId, commentId);

            CommentResponse response = commentFacadeService.deleteComment(commentId, currentUserId);
            
            if (response.getSuccess()) {
                return Result.success(response);
            } else {
                return Result.error(response.getResponseCode(), response.getResponseMessage());
            }

        } catch (Exception e) {
            log.error("评论删除失败，评论ID: {}", commentId, e);
            return Result.error("COMMENT_DELETE_ERROR", "评论删除失败，请稍后重试");
        }
    }

    /**
     * 点赞/取消点赞评论
     *
     * @param commentId 评论ID
     * @param isLike 是否点赞
     * @return 点赞结果
     */
    @PostMapping("/{commentId}/like")
    @SaCheckLogin
    public Result<CommentResponse> likeComment(@PathVariable Long commentId, 
                                             @RequestParam Boolean isLike) {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            log.info("用户点赞评论，用户ID: {}, 评论ID: {}, 点赞: {}", currentUserId, commentId, isLike);

            CommentResponse response = commentFacadeService.likeComment(commentId, currentUserId, isLike);
            
            if (response.getSuccess()) {
                return Result.success(response);
            } else {
                return Result.error(response.getResponseCode(), response.getResponseMessage());
            }

        } catch (Exception e) {
            log.error("评论点赞失败，评论ID: {}", commentId, e);
            return Result.error("COMMENT_LIKE_ERROR", "点赞操作失败，请稍后重试");
        }
    }

    /**
     * 查询评论详情
     *
     * @param commentId 评论ID
     * @return 评论详情
     */
    @GetMapping("/{commentId}")
    public Result<CommentInfo> getComment(@PathVariable Long commentId) {
        try {
            log.info("查询评论详情，评论ID: {}", commentId);

            CommentQueryRequest queryRequest = new CommentQueryRequest();
            queryRequest.setCommentId(commentId);
            
            // 设置当前用户ID（如果已登录）
            try {
                Long currentUserId = StpUtil.getLoginIdAsLong();
                queryRequest.setCurrentUserId(currentUserId);
            } catch (Exception e) {
                // 用户未登录，忽略
            }

            CommentQueryResponse<CommentInfo> response = commentFacadeService.queryComment(queryRequest);
            
            if (response.getSuccess()) {
                return Result.success(response.getData());
            } else {
                return Result.error(response.getResponseCode(), response.getResponseMessage());
            }

        } catch (Exception e) {
            log.error("查询评论详情失败，评论ID: {}", commentId, e);
            return Result.error("COMMENT_QUERY_ERROR", "查询评论详情失败，请稍后重试");
        }
    }

    /**
     * 分页查询评论列表
     *
     * @param commentType 评论类型
     * @param targetId 目标ID
     * @param parentCommentId 父评论ID
     * @param pageNum 页码
     * @param pageSize 页大小
     * @param sortBy 排序字段
     * @param sortOrder 排序方向
     * @return 评论列表
     */
    @GetMapping
    public Result<PageResponse<CommentInfo>> getComments(
            @RequestParam(required = false) String commentType,
            @RequestParam(required = false) Long targetId,
            @RequestParam(required = false) Long parentCommentId,
            @RequestParam(defaultValue = "1") @Min(1) Integer pageNum,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer pageSize,
            @RequestParam(defaultValue = "time") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        try {
            log.info("分页查询评论列表，目标ID: {}, 类型: {}, 页码: {}", targetId, commentType, pageNum);

            CommentQueryRequest queryRequest = new CommentQueryRequest();
            queryRequest.setTargetId(targetId);
            queryRequest.setParentCommentId(parentCommentId);
            queryRequest.setCurrentPage(pageNum);
            queryRequest.setPageSize(pageSize);
            queryRequest.setSortBy(sortBy);
            queryRequest.setSortOrder(sortOrder);
            
            // 设置评论类型
            if (commentType != null) {
                try {
                    queryRequest.setCommentType(CommentType.valueOf(commentType.toUpperCase()));
                } catch (Exception e) {
                    return Result.error("INVALID_COMMENT_TYPE", "评论类型格式错误");
                }
            }
            
            // 设置当前用户ID（如果已登录）
            try {
                Long currentUserId = StpUtil.getLoginIdAsLong();
                queryRequest.setCurrentUserId(currentUserId);
            } catch (Exception e) {
                // 用户未登录，忽略
            }

            PageResponse<CommentInfo> response = commentFacadeService.pageQueryComments(queryRequest);
            
            if (response.getSuccess()) {
                return Result.success(response);
            } else {
                return Result.error(response.getResponseCode(), response.getResponseMessage());
            }

        } catch (Exception e) {
            log.error("分页查询评论列表失败", e);
            return Result.error("COMMENT_LIST_QUERY_ERROR", "查询评论列表失败，请稍后重试");
        }
    }

    /**
     * 查询评论树（包含子评论的树形结构）
     *
     * @param commentType 评论类型
     * @param targetId 目标ID
     * @param includeChildren 是否包含子评论
     * @param pageNum 页码
     * @param pageSize 页大小
     * @param sortBy 排序字段
     * @param sortOrder 排序方向
     * @return 评论树
     */
    @GetMapping("/tree")
    public Result<PageResponse<CommentInfo>> getCommentTree(
            @RequestParam(required = false) String commentType,
            @RequestParam(required = false) Long targetId,
            @RequestParam(defaultValue = "true") Boolean includeChildren,
            @RequestParam(defaultValue = "1") @Min(1) Integer pageNum,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) Integer pageSize,
            @RequestParam(defaultValue = "time") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        try {
            log.info("查询评论树，目标ID: {}, 类型: {}, 包含子评论: {}", targetId, commentType, includeChildren);

            CommentQueryRequest queryRequest = new CommentQueryRequest();
            queryRequest.setTargetId(targetId);
            queryRequest.setIncludeChildren(includeChildren);
            queryRequest.setCurrentPage(pageNum);
            queryRequest.setPageSize(pageSize);
            queryRequest.setSortBy(sortBy);
            queryRequest.setSortOrder(sortOrder);
            
            // 设置评论类型
            if (commentType != null) {
                try {
                    queryRequest.setCommentType(CommentType.valueOf(commentType.toUpperCase()));
                } catch (Exception e) {
                    return Result.error("INVALID_COMMENT_TYPE", "评论类型格式错误");
                }
            }
            
            // 设置当前用户ID（如果已登录）
            try {
                Long currentUserId = StpUtil.getLoginIdAsLong();
                queryRequest.setCurrentUserId(currentUserId);
            } catch (Exception e) {
                // 用户未登录，忽略
            }

            PageResponse<CommentInfo> response = commentFacadeService.queryCommentTree(queryRequest);
            
            if (response.getSuccess()) {
                return Result.success(response);
            } else {
                return Result.error(response.getResponseCode(), response.getResponseMessage());
            }

        } catch (Exception e) {
            log.error("查询评论树失败", e);
            return Result.error("COMMENT_TREE_QUERY_ERROR", "查询评论树失败，请稍后重试");
        }
    }

    /**
     * 统计评论数量
     *
     * @param targetId 目标ID
     * @param commentType 评论类型
     * @return 评论数量
     */
    @GetMapping("/count")
    public Result<Long> getCommentCount(@RequestParam @NotNull Long targetId,
                                       @RequestParam(required = false) String commentType) {
        try {
            log.info("统计评论数量，目标ID: {}, 类型: {}", targetId, commentType);

            CommentQueryResponse<Long> response = commentFacadeService.queryCommentCount(targetId, commentType);
            
            if (response.getSuccess()) {
                return Result.success(response.getData());
            } else {
                return Result.error(response.getResponseCode(), response.getResponseMessage());
            }

        } catch (Exception e) {
            log.error("统计评论数量失败，目标ID: {}", targetId, e);
            return Result.error("COMMENT_COUNT_ERROR", "统计评论数量失败，请稍后重试");
        }
    }
} 