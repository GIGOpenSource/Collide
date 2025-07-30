package com.gig.collide.comment.controller;

import com.gig.collide.api.comment.request.CommentCreateRequest;
import com.gig.collide.api.comment.request.CommentQueryRequest;
import com.gig.collide.api.comment.request.CommentUpdateRequest;
import com.gig.collide.api.comment.response.CommentResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.comment.facade.CommentFacadeServiceImpl;
import com.gig.collide.web.vo.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 评论REST控制器 - 简洁版
 * 提供HTTP接口访问评论功能
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {


    private final CommentFacadeServiceImpl commentFacadeService;

    // =================== 基础CRUD ===================

    /**
     * 创建评论
     */
    @PostMapping
    public Result<CommentResponse> createComment(@Valid @RequestBody CommentCreateRequest request) {
        log.info("REST请求 - 创建评论：{}", request);
        return commentFacadeService.createComment(request);
    }

    /**
     * 更新评论
     */
    @PutMapping("/{commentId}")
    public Result<CommentResponse> updateComment(@PathVariable Long commentId,
                                               @Valid @RequestBody CommentUpdateRequest request) {
        log.info("REST请求 - 更新评论，ID：{}，请求：{}", commentId, request);
        request.setId(commentId);
        return commentFacadeService.updateComment(request);
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{commentId}")
    public Result<Void> deleteComment(@PathVariable Long commentId,
                                    @RequestParam Long userId) {
        log.info("REST请求 - 删除评论，ID：{}，用户：{}", commentId, userId);
        return commentFacadeService.deleteComment(commentId, userId);
    }

    /**
     * 获取评论详情
     */
    @GetMapping("/{commentId}")
    public Result<CommentResponse> getCommentById(@PathVariable Long commentId,
                                                @RequestParam(defaultValue = "false") Boolean includeDeleted) {
        log.info("REST请求 - 获取评论详情，ID：{}", commentId);
        return commentFacadeService.getCommentById(commentId, includeDeleted);
    }

    /**
     * 查询评论列表
     */
    @PostMapping("/query")
    public Result<PageResponse<CommentResponse>> queryComments(@Valid @RequestBody CommentQueryRequest request) {
        log.info("REST请求 - 查询评论列表：{}", request);
        return commentFacadeService.queryComments(request);
    }

    // =================== 目标对象评论 ===================

    /**
     * 获取目标对象的评论列表
     */
    @GetMapping("/target/{targetId}")
    public PageResponse<CommentResponse> getTargetComments(@PathVariable Long targetId,
                                                         @RequestParam(required = false) String commentType,
                                                         @RequestParam(defaultValue = "0") Long parentCommentId,
                                                         @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
                                                         @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("REST请求 - 获取目标评论，目标：{}，类型：{}，页码：{}", targetId, commentType, currentPage);
        Result<PageResponse<CommentResponse>> result = commentFacadeService.getTargetComments(targetId, commentType, parentCommentId, currentPage, pageSize);
        return result.getData();
    }

    /**
     * 获取评论回复列表
     */
    @GetMapping("/{commentId}/replies")
    public PageResponse<CommentResponse> getCommentReplies(@PathVariable Long commentId,
                                                         @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
                                                         @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("REST请求 - 获取评论回复，评论：{}，页码：{}", commentId, currentPage);
        Result<PageResponse<CommentResponse>> result = commentFacadeService.getCommentReplies(commentId, currentPage, pageSize);
        return result.getData();
    }

    /**
     * 获取评论树形结构
     */
    @GetMapping("/tree/{targetId}")
    public PageResponse<CommentResponse> getCommentTree(@PathVariable Long targetId,
                                                      @RequestParam(required = false) String commentType,
                                                      @RequestParam(defaultValue = "3") Integer maxDepth,
                                                      @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
                                                      @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("REST请求 - 获取评论树，目标：{}，深度：{}，页码：{}", targetId, maxDepth, currentPage);
        Result<PageResponse<CommentResponse>> result = commentFacadeService.getCommentTree(targetId, commentType, maxDepth, currentPage, pageSize);
        return result.getData();
    }

    // =================== 用户评论 ===================

    /**
     * 获取用户评论列表
     */
    @GetMapping("/user/{userId}")
    public PageResponse<CommentResponse> getUserComments(@PathVariable Long userId,
                                                       @RequestParam(required = false) String commentType,
                                                       @RequestParam(defaultValue = "NORMAL") String status,
                                                       @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
                                                       @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("REST请求 - 获取用户评论，用户：{}，页码：{}", userId, currentPage);
        Result<PageResponse<CommentResponse>> result = commentFacadeService.getUserComments(userId, commentType, status, currentPage, pageSize);
        return result.getData();
    }

    /**
     * 获取用户收到的回复
     */
    @GetMapping("/user/{userId}/replies")
    public PageResponse<CommentResponse> getUserReplies(@PathVariable Long userId,
                                                      @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
                                                      @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("REST请求 - 获取用户回复，用户：{}，页码：{}", userId, currentPage);
        Result<PageResponse<CommentResponse>> result = commentFacadeService.getUserReplies(userId, currentPage, pageSize);
        return result.getData();
    }

    // =================== 状态管理 ===================

    /**
     * 更新评论状态
     */
    @PutMapping("/{commentId}/status")
    public Result<Void> updateCommentStatus(@PathVariable Long commentId,
                                           @RequestParam String status,
                                           @RequestParam Long operatorId) {
        log.info("REST请求 - 更新评论状态，ID：{}，状态：{}", commentId, status);
        return commentFacadeService.updateCommentStatus(commentId, status, operatorId);
    }

    /**
     * 批量更新评论状态
     */
    @PutMapping("/batch/status")
    public Result<Integer> batchUpdateCommentStatus(@RequestParam List<Long> commentIds,
                                                   @RequestParam String status,
                                                   @RequestParam Long operatorId) {
        log.info("REST请求 - 批量更新评论状态，数量：{}，状态：{}", commentIds.size(), status);
        return commentFacadeService.batchUpdateCommentStatus(commentIds, status, operatorId);
    }

    /**
     * 隐藏评论
     */
    @PutMapping("/{commentId}/hide")
    public Result<Void> hideComment(@PathVariable Long commentId,
                                  @RequestParam Long operatorId) {
        log.info("REST请求 - 隐藏评论，ID：{}", commentId);
        return commentFacadeService.hideComment(commentId, operatorId);
    }

    /**
     * 恢复评论
     */
    @PutMapping("/{commentId}/restore")
    public Result<Void> restoreComment(@PathVariable Long commentId,
                                     @RequestParam Long operatorId) {
        log.info("REST请求 - 恢复评论，ID：{}", commentId);
        return commentFacadeService.restoreComment(commentId, operatorId);
    }

    // =================== 统计功能 ===================

    /**
     * 增加点赞数
     */
    @PutMapping("/{commentId}/like")
    public Result<Integer> increaseLikeCount(@PathVariable Long commentId,
                                           @RequestParam(defaultValue = "1") Integer increment) {
        log.info("REST请求 - 增加点赞数，ID：{}，增量：{}", commentId, increment);
        return commentFacadeService.increaseLikeCount(commentId, increment);
    }

    /**
     * 减少点赞数
     */
    @PutMapping("/{commentId}/unlike")
    public Result<Integer> decreaseLikeCount(@PathVariable Long commentId,
                                           @RequestParam(defaultValue = "1") Integer decrement) {
        log.info("REST请求 - 减少点赞数，ID：{}，减量：{}", commentId, decrement);
        return commentFacadeService.increaseLikeCount(commentId, -decrement);
    }

    /**
     * 统计目标对象评论数
     */
    @GetMapping("/count/target/{targetId}")
    public Result<Long> countTargetComments(@PathVariable Long targetId,
                                          @RequestParam(required = false) String commentType,
                                          @RequestParam(defaultValue = "NORMAL") String status) {
        log.info("REST请求 - 统计目标评论数，目标：{}", targetId);
        return commentFacadeService.countTargetComments(targetId, commentType, status);
    }

    /**
     * 统计用户评论数
     */
    @GetMapping("/count/user/{userId}")
    public Result<Long> countUserComments(@PathVariable Long userId,
                                        @RequestParam(required = false) String commentType,
                                        @RequestParam(defaultValue = "NORMAL") String status) {
        log.info("REST请求 - 统计用户评论数，用户：{}", userId);
        return commentFacadeService.countUserComments(userId, commentType, status);
    }

    /**
     * 获取评论统计信息
     */
    @GetMapping("/{commentId}/statistics")
    public Result<Map<String, Object>> getCommentStatistics(@PathVariable Long commentId) {
        log.info("REST请求 - 获取评论统计，ID：{}", commentId);
        return commentFacadeService.getCommentStatistics(commentId);
    }

    // =================== 高级功能 ===================

    /**
     * 搜索评论
     */
    @GetMapping("/search")
    public PageResponse<CommentResponse> searchComments(@RequestParam String keyword,
                                                      @RequestParam(required = false) String commentType,
                                                      @RequestParam(required = false) Long targetId,
                                                      @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
                                                      @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("REST请求 - 搜索评论，关键词：{}，页码：{}", keyword, currentPage);
        Result<PageResponse<CommentResponse>> result = commentFacadeService.searchComments(keyword, commentType, targetId, currentPage, pageSize);
        return result.getData();
    }

    /**
     * 获取热门评论
     */
    @GetMapping("/popular")
    public PageResponse<CommentResponse> getPopularComments(@RequestParam(required = false) Long targetId,
                                                          @RequestParam(required = false) String commentType,
                                                          @RequestParam(defaultValue = "7") Integer timeRange,
                                                          @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
                                                          @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("REST请求 - 获取热门评论，时间范围：{}天，页码：{}", timeRange, currentPage);
        Result<PageResponse<CommentResponse>> result = commentFacadeService.getPopularComments(targetId, commentType, timeRange, currentPage, pageSize);
        return result.getData();
    }

    /**
     * 获取最新评论
     */
    @GetMapping("/latest")
    public PageResponse<CommentResponse> getLatestComments(@RequestParam(required = false) Long targetId,
                                                         @RequestParam(required = false) String commentType,
                                                         @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
                                                         @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("REST请求 - 获取最新评论，页码：{}", currentPage);
        Result<PageResponse<CommentResponse>> result = commentFacadeService.getLatestComments(targetId, commentType, currentPage, pageSize);
        return result.getData();
    }

    // =================== 管理功能 ===================

    /**
     * 批量删除目标对象的评论
     */
    @DeleteMapping("/target/{targetId}")
    public Result<Integer> batchDeleteTargetComments(@PathVariable Long targetId,
                                                    @RequestParam(required = false) String commentType,
                                                    @RequestParam Long operatorId) {
        log.info("REST请求 - 批量删除目标评论，目标：{}", targetId);
        return commentFacadeService.batchDeleteTargetComments(targetId, commentType, operatorId);
    }

    /**
     * 更新用户信息（冗余字段同步）
     */
    @PutMapping("/sync/user/{userId}")
    public Result<Integer> updateUserInfo(@PathVariable Long userId,
                                        @RequestParam String nickname,
                                        @RequestParam(required = false) String avatar) {
        log.info("REST请求 - 同步用户信息，用户：{}，昵称：{}", userId, nickname);
        return commentFacadeService.updateUserInfo(userId, nickname, avatar);
    }
}