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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

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
@Tag(name = "评论管理", description = "评论发布、回复、管理相关接口")
public class CommentController {

    private final CommentFacadeService commentFacadeService;

    /**
     * 创建评论
     */
    @PostMapping
    @SaCheckLogin
    @Operation(summary = "创建评论", description = "创建新的评论或回复")
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
     */
    @DeleteMapping("/{commentId}")
    @SaCheckLogin
    @Operation(summary = "删除评论", description = "删除指定的评论")
    public Result<CommentResponse> deleteComment(
            @Parameter(description = "评论ID", required = true) @PathVariable Long commentId) {
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
            @Parameter(description = "评论类型") @RequestParam(required = false) 
            @Pattern(regexp = "CONTENT|DYNAMIC", message = "评论类型只能是 CONTENT 或 DYNAMIC") String commentType,
            @Parameter(description = "目标ID") @RequestParam(required = false) 
            @Min(value = 1, message = "目标ID必须大于0") Long targetId,
            @Parameter(description = "父评论ID") @RequestParam(required = false) Long parentCommentId,
            @RequestParam(defaultValue = "1") @Min(1) Integer pageNum,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer pageSize,
            @RequestParam(defaultValue = "time") 
            @Pattern(regexp = "time|like", message = "排序字段只能是 time 或 like") String sortBy,
            @RequestParam(defaultValue = "desc") 
            @Pattern(regexp = "desc|asc", message = "排序方向只能是 desc 或 asc") String sortOrder) {
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

    /**
     * 检查用户是否已点赞评论
     *
     * @param commentId 评论ID
     * @return 是否已点赞
     */
    @GetMapping("/{commentId}/liked")
    @SaCheckLogin
    @Operation(summary = "检查点赞状态", description = "检查当前用户是否已点赞指定评论")
    public Result<Boolean> checkUserLiked(@Parameter(description = "评论ID") @PathVariable Long commentId) {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            log.info("检查用户点赞状态，用户ID: {}, 评论ID: {}", currentUserId, commentId);

            // ✅ 修复：使用接口方法而不是强制类型转换
            CommentQueryResponse<Boolean> response = commentFacadeService.checkUserLiked(commentId, currentUserId);
            
            if (response.getSuccess()) {
                return Result.success(response.getData());
            } else {
                return Result.error(response.getResponseCode(), response.getResponseMessage());
            }

        } catch (Exception e) {
            log.error("检查用户点赞状态失败，评论ID: {}", commentId, e);
            return Result.error("CHECK_LIKE_STATUS_ERROR", "检查点赞状态失败，请稍后重试");
        }
    }

    /**
     * 查询用户评论历史
     *
     * @param userId 用户ID
     * @param commentType 评论类型
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 用户评论列表
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "查询用户评论历史", description = "查询指定用户的评论历史记录")
    public Result<PageResponse<CommentInfo>> getUserComments(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "评论类型") @RequestParam(required = false) 
            @Pattern(regexp = "CONTENT|DYNAMIC", message = "评论类型只能是 CONTENT 或 DYNAMIC") String commentType,
            @RequestParam(defaultValue = "1") @Min(1) Integer pageNum,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) Integer pageSize) {
        try {
            log.info("查询用户评论历史，用户ID: {}, 类型: {}, 页码: {}", userId, commentType, pageNum);

            PageResponse<CommentInfo> response = commentFacadeService.queryUserComments(userId, commentType, pageNum, pageSize);
            
            if (response.getSuccess()) {
                return Result.success(response);
            } else {
                return Result.error(response.getResponseCode(), response.getResponseMessage());
            }

        } catch (Exception e) {
            log.error("查询用户评论历史失败，用户ID: {}", userId, e);
            return Result.error("QUERY_USER_COMMENTS_ERROR", "查询用户评论历史失败，请稍后重试");
        }
    }

    /**
     * 查询热门评论
     *
     * @param targetId 目标ID
     * @param commentType 评论类型
     * @param limit 查询数量限制
     * @return 热门评论列表
     */
    @GetMapping("/hot")
    @Operation(summary = "查询热门评论", description = "查询指定目标的热门评论")
    public Result<List<CommentInfo>> getHotComments(
            @Parameter(description = "目标ID", required = true) @RequestParam @NotNull Long targetId,
            @Parameter(description = "评论类型", required = true) @RequestParam 
            @Pattern(regexp = "CONTENT|DYNAMIC", message = "评论类型只能是 CONTENT 或 DYNAMIC") String commentType,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) Integer limit) {
        try {
            log.info("查询热门评论，目标ID: {}, 类型: {}, 数量: {}", targetId, commentType, limit);

            CommentQueryResponse<List<CommentInfo>> response = commentFacadeService.queryHotComments(targetId, commentType, limit);
            
            if (response.getSuccess()) {
                return Result.success(response.getData());
            } else {
                return Result.error(response.getResponseCode(), response.getResponseMessage());
            }

        } catch (Exception e) {
            log.error("查询热门评论失败，目标ID: {}", targetId, e);
            return Result.error("QUERY_HOT_COMMENTS_ERROR", "查询热门评论失败，请稍后重试");
        }
    }

    /**
     * 获取评论详细统计信息
     *
     * @param targetId 目标ID
     * @param commentType 评论类型
     * @return 详细统计信息
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取评论统计信息", description = "获取指定目标的详细评论统计信息")
    public Result<Map<String, Object>> getCommentStatistics(
            @Parameter(description = "目标ID", required = true) @RequestParam @NotNull Long targetId,
            @Parameter(description = "评论类型", required = true) @RequestParam 
            @Pattern(regexp = "CONTENT|DYNAMIC", message = "评论类型只能是 CONTENT 或 DYNAMIC") String commentType) {
        try {
            log.info("获取评论统计信息，目标ID: {}, 类型: {}", targetId, commentType);

            CommentQueryResponse<Map<String, Object>> response = commentFacadeService.getCommentStatistics(targetId, commentType);
            
            if (response.getSuccess()) {
                return Result.success(response.getData());
            } else {
                return Result.error(response.getResponseCode(), response.getResponseMessage());
            }

        } catch (Exception e) {
            log.error("获取评论统计信息失败，目标ID: {}", targetId, e);
            return Result.error("GET_COMMENT_STATISTICS_ERROR", "获取评论统计信息失败，请稍后重试");
        }
    }
} 