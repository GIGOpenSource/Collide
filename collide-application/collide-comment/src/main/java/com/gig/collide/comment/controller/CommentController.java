package com.gig.collide.comment.controller;

import com.gig.collide.api.comment.request.CommentCreateRequest;
import com.gig.collide.api.comment.request.CommentUpdateRequest;
import com.gig.collide.api.comment.response.CommentResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.comment.facade.CommentFacadeServiceImpl;
import com.gig.collide.web.vo.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 评论REST控制器 - 规范版
 * 提供完整的评论功能HTTP接口：基础操作、高级查询、统计分析、管理功能
 * 支持评论类型：CONTENT、DYNAMIC
 * 
 * @author Collide
 * @version 5.0.0 (与Content模块一致版)
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentFacadeServiceImpl commentFacadeService;

    // =================== 评论基础操作 ===================

    /**
     * 创建评论
     */
    @PostMapping
    public Result<CommentResponse> createComment(@Valid @RequestBody CommentCreateRequest request) {
        log.info("REST请求 - 创建评论: {}", request);
        return commentFacadeService.createComment(request);
    }

    /**
     * 更新评论
     */
    @PutMapping("/{commentId}")
    public Result<CommentResponse> updateComment(@PathVariable Long commentId,
                                               @Valid @RequestBody CommentUpdateRequest request) {
        log.info("REST请求 - 更新评论: commentId={}, request={}", commentId, request);
        request.setId(commentId);
        return commentFacadeService.updateComment(request);
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{commentId}")
    public Result<Void> deleteComment(@PathVariable Long commentId,
                                    @RequestParam Long userId) {
        log.info("REST请求 - 删除评论: commentId={}, userId={}", commentId, userId);
        return commentFacadeService.deleteComment(commentId, userId);
    }

    /**
     * 获取评论详情
     */
    @GetMapping("/{commentId}")
    public Result<CommentResponse> getCommentById(@PathVariable Long commentId) {
        log.info("REST请求 - 获取评论详情: commentId={}", commentId);
        return commentFacadeService.getCommentById(commentId);
    }

    // =================== 目标对象评论查询 ===================

    /**
     * 获取目标对象的评论列表
     */
    @GetMapping("/target/{targetId}")
    public Result<PageResponse<CommentResponse>> getTargetComments(@PathVariable Long targetId,
                                                                 @RequestParam(required = false) String commentType,
                                                                 @RequestParam(defaultValue = "0") Long parentCommentId,
                                                                 @RequestParam(defaultValue = "1") Integer currentPage,
                                                                 @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("REST请求 - 获取目标评论: targetId={}, commentType={}, parentCommentId={}, currentPage={}, pageSize={}", 
                targetId, commentType, parentCommentId, currentPage, pageSize);
        return commentFacadeService.getTargetComments(targetId, commentType, parentCommentId, currentPage, pageSize);
    }

    /**
     * 获取评论回复列表
     */
    @GetMapping("/{commentId}/replies")
    public Result<PageResponse<CommentResponse>> getCommentReplies(@PathVariable Long commentId,
                                                                 @RequestParam(defaultValue = "1") Integer currentPage,
                                                                 @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("REST请求 - 获取评论回复: commentId={}, currentPage={}, pageSize={}", commentId, currentPage, pageSize);
        return commentFacadeService.getCommentReplies(commentId, currentPage, pageSize);
    }

    /**
     * 获取评论树形结构
     */
    @GetMapping("/tree/{targetId}")
    public Result<PageResponse<CommentResponse>> getCommentTree(@PathVariable Long targetId,
                                                              @RequestParam(required = false) String commentType,
                                                              @RequestParam(defaultValue = "3") Integer maxDepth,
                                                              @RequestParam(defaultValue = "1") Integer currentPage,
                                                              @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("REST请求 - 获取评论树: targetId={}, commentType={}, maxDepth={}, currentPage={}, pageSize={}", 
                targetId, commentType, maxDepth, currentPage, pageSize);
        return commentFacadeService.getCommentTree(targetId, commentType, maxDepth, currentPage, pageSize);
    }

    // =================== 用户评论查询 ===================

    /**
     * 获取用户评论列表
     */
    @GetMapping("/user/{userId}")
    public Result<PageResponse<CommentResponse>> getUserComments(@PathVariable Long userId,
                                                               @RequestParam(required = false) String commentType,
                                                               @RequestParam(defaultValue = "1") Integer currentPage,
                                                               @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("REST请求 - 获取用户评论: userId={}, commentType={}, currentPage={}, pageSize={}", 
                userId, commentType, currentPage, pageSize);
        return commentFacadeService.getUserComments(userId, commentType, currentPage, pageSize);
    }

    /**
     * 获取用户收到的回复
     */
    @GetMapping("/user/{userId}/replies")
    public Result<PageResponse<CommentResponse>> getUserReplies(@PathVariable Long userId,
                                                              @RequestParam(defaultValue = "1") Integer currentPage,
                                                              @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("REST请求 - 获取用户回复: userId={}, currentPage={}, pageSize={}", userId, currentPage, pageSize);
        return commentFacadeService.getUserReplies(userId, currentPage, pageSize);
    }

    // =================== 统计功能 ===================

    /**
     * 增加点赞数
     */
    @PutMapping("/{commentId}/like")
    public Result<Integer> increaseLikeCount(@PathVariable Long commentId,
                                           @RequestParam(defaultValue = "1") Integer increment) {
        log.info("REST请求 - 增加点赞数: commentId={}, increment={}", commentId, increment);
        return commentFacadeService.increaseLikeCount(commentId, increment);
    }

    /**
     * 减少点赞数
     */
    @PutMapping("/{commentId}/unlike")
    public Result<Integer> decreaseLikeCount(@PathVariable Long commentId,
                                           @RequestParam(defaultValue = "1") Integer decrement) {
        log.info("REST请求 - 减少点赞数: commentId={}, decrement={}", commentId, decrement);
        return commentFacadeService.increaseLikeCount(commentId, -decrement);
    }

    /**
     * 增加回复数
     */
    @PutMapping("/{commentId}/reply-count")
    public Result<Integer> increaseReplyCount(@PathVariable Long commentId,
                                            @RequestParam(defaultValue = "1") Integer increment) {
        log.info("REST请求 - 增加回复数: commentId={}, increment={}", commentId, increment);
        return commentFacadeService.increaseReplyCount(commentId, increment);
    }

    /**
     * 统计目标对象评论数
     */
    @GetMapping("/count/target/{targetId}")
    public Result<Long> countTargetComments(@PathVariable Long targetId,
                                          @RequestParam(required = false) String commentType) {
        log.info("REST请求 - 统计目标评论数: targetId={}, commentType={}", targetId, commentType);
        return commentFacadeService.countTargetComments(targetId, commentType);
    }

    /**
     * 统计用户评论数
     */
    @GetMapping("/count/user/{userId}")
    public Result<Long> countUserComments(@PathVariable Long userId,
                                        @RequestParam(required = false) String commentType) {
        log.info("REST请求 - 统计用户评论数: userId={}, commentType={}", userId, commentType);
        return commentFacadeService.countUserComments(userId, commentType);
    }

    // =================== 高级功能 ===================

    /**
     * 搜索评论
     */
    @GetMapping("/search")
    public Result<PageResponse<CommentResponse>> searchComments(@RequestParam String keyword,
                                                              @RequestParam(required = false) String commentType,
                                                              @RequestParam(required = false) Long targetId,
                                                              @RequestParam(defaultValue = "1") Integer currentPage,
                                                              @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("REST请求 - 搜索评论: keyword={}, commentType={}, targetId={}, currentPage={}, pageSize={}", 
                keyword, commentType, targetId, currentPage, pageSize);
        return commentFacadeService.searchComments(keyword, commentType, targetId, currentPage, pageSize);
    }

    /**
     * 获取热门评论
     */
    @GetMapping("/popular")
    public Result<PageResponse<CommentResponse>> getPopularComments(@RequestParam(required = false) Long targetId,
                                                                  @RequestParam(required = false) String commentType,
                                                                  @RequestParam(defaultValue = "7") Integer timeRange,
                                                                  @RequestParam(defaultValue = "1") Integer currentPage,
                                                                  @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("REST请求 - 获取热门评论: targetId={}, commentType={}, timeRange={}, currentPage={}, pageSize={}", 
                targetId, commentType, timeRange, currentPage, pageSize);
        return commentFacadeService.getPopularComments(targetId, commentType, timeRange, currentPage, pageSize);
    }

    /**
     * 获取最新评论
     */
    @GetMapping("/latest")
    public Result<PageResponse<CommentResponse>> getLatestComments(@RequestParam(required = false) Long targetId,
                                                                 @RequestParam(required = false) String commentType,
                                                                 @RequestParam(defaultValue = "1") Integer currentPage,
                                                                 @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("REST请求 - 获取最新评论: targetId={}, commentType={}, currentPage={}, pageSize={}", 
                targetId, commentType, currentPage, pageSize);
        return commentFacadeService.getLatestComments(targetId, commentType, currentPage, pageSize);
    }

    // =================== 新增查询功能 ===================

    /**
     * 根据点赞数范围查询评论
     */
    @GetMapping("/like-range")
    public Result<PageResponse<CommentResponse>> getCommentsByLikeCountRange(
            @RequestParam(required = false) Integer minLikeCount,
            @RequestParam(required = false) Integer maxLikeCount,
            @RequestParam(required = false) String commentType,
            @RequestParam(required = false) Long targetId,
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("REST请求 - 根据点赞数范围查询评论: minLike={}, maxLike={}, commentType={}, targetId={}, page={}/{}", 
                minLikeCount, maxLikeCount, commentType, targetId, currentPage, pageSize);
        return commentFacadeService.getCommentsByLikeCountRange(minLikeCount, maxLikeCount, 
                commentType, targetId, currentPage, pageSize);
    }

    /**
     * 根据时间范围查询评论
     */
    @GetMapping("/time-range")
    public Result<PageResponse<CommentResponse>> getCommentsByTimeRange(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false) String commentType,
            @RequestParam(required = false) Long targetId,
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("REST请求 - 根据时间范围查询评论: startTime={}, endTime={}, commentType={}, targetId={}, page={}/{}", 
                startTime, endTime, commentType, targetId, currentPage, pageSize);
        return commentFacadeService.getCommentsByTimeRange(startTime, endTime, 
                commentType, targetId, currentPage, pageSize);
    }

    // =================== 数据分析功能 ===================

    /**
     * 获取评论统计信息
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getCommentStatistics(
            @RequestParam(required = false) Long targetId,
            @RequestParam(required = false) String commentType,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        log.info("REST请求 - 获取评论统计信息: targetId={}, commentType={}, userId={}, startTime={}, endTime={}", 
                targetId, commentType, userId, startTime, endTime);
        return commentFacadeService.getCommentStatistics(targetId, commentType, userId, startTime, endTime);
    }

    /**
     * 查询用户回复关系
     */
    @GetMapping("/reply-relations")
    public Result<List<Map<String, Object>>> getUserReplyRelations(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        log.info("REST请求 - 查询用户回复关系: userId={}, startTime={}, endTime={}", userId, startTime, endTime);
        return commentFacadeService.getUserReplyRelations(userId, startTime, endTime);
    }

    /**
     * 查询评论热度排行
     */
    @GetMapping("/hot-ranking")
    public Result<List<Map<String, Object>>> getCommentHotRanking(
            @RequestParam(required = false) String commentType,
            @RequestParam(required = false) Long targetId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "10") Integer limit) {
        log.info("REST请求 - 查询评论热度排行: commentType={}, targetId={}, startTime={}, endTime={}, limit={}", 
                commentType, targetId, startTime, endTime, limit);
        return commentFacadeService.getCommentHotRanking(commentType, targetId, startTime, endTime, limit);
    }

    // =================== 管理功能（需要管理员权限） ===================

    /**
     * 批量更新评论状态
     */
    @PutMapping("/batch/status")
    public Result<Integer> batchUpdateCommentStatus(@RequestBody List<Long> commentIds,
                                                  @RequestParam String status) {
        log.info("REST请求 - 批量更新评论状态: commentIds={}, status={}", commentIds, status);
        return commentFacadeService.batchUpdateCommentStatus(commentIds, status);
    }

    /**
     * 批量删除目标对象的评论
     */
    @DeleteMapping("/batch/target/{targetId}")
    public Result<Integer> batchDeleteTargetComments(@PathVariable Long targetId,
                                                   @RequestParam(required = false) String commentType) {
        log.info("REST请求 - 批量删除目标评论: targetId={}, commentType={}", targetId, commentType);
        return commentFacadeService.batchDeleteTargetComments(targetId, commentType);
    }

    /**
     * 更新用户信息（同步冗余字段）
     */
    @PutMapping("/sync/user/{userId}")
    public Result<Integer> updateUserInfo(@PathVariable Long userId,
                                        @RequestParam(required = false) String nickname,
                                        @RequestParam(required = false) String avatar) {
        log.info("REST请求 - 更新用户信息: userId={}, nickname={}, avatar={}", userId, nickname, avatar);
        return commentFacadeService.updateUserInfo(userId, nickname, avatar);
    }

    /**
     * 更新回复目标用户信息（同步冗余字段）
     */
    @PutMapping("/sync/reply-to-user/{replyToUserId}")
    public Result<Integer> updateReplyToUserInfo(@PathVariable Long replyToUserId,
                                               @RequestParam(required = false) String nickname,
                                               @RequestParam(required = false) String avatar) {
        log.info("REST请求 - 更新回复目标用户信息: replyToUserId={}, nickname={}, avatar={}", 
                replyToUserId, nickname, avatar);
        return commentFacadeService.updateReplyToUserInfo(replyToUserId, nickname, avatar);
    }

    /**
     * 清理已删除的评论（物理删除）
     */
    @DeleteMapping("/cleanup")
    public Result<Integer> cleanDeletedComments(@RequestParam(defaultValue = "30") Integer days,
                                              @RequestParam(defaultValue = "1000") Integer limit) {
        log.info("REST请求 - 清理已删除评论: days={}, limit={}", days, limit);
        return commentFacadeService.cleanDeletedComments(days, limit);
    }
}