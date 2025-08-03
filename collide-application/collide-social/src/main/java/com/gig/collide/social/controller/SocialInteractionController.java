package com.gig.collide.social.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.social.domain.entity.*;
import com.gig.collide.social.domain.service.SocialInteractionService;
import com.gig.collide.social.controller.SocialContentController.ApiResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 社交互动控制器 - 简化版
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
@Slf4j
@RestController
@RequestMapping("/api/social/interaction")
@RequiredArgsConstructor
public class SocialInteractionController {

    private final SocialInteractionService interactionService;

    // ========== 点赞相关 ==========

    /**
     * 点赞内容
     */
    @PostMapping("/like")
    public ApiResult<Boolean> likeContent(@RequestParam Long userId, @RequestParam Long contentId) {
        boolean result = interactionService.likeContent(userId, contentId);
        return ApiResult.success(result);
    }

    /**
     * 取消点赞
     */
    @DeleteMapping("/like")
    public ApiResult<Boolean> unlikeContent(@RequestParam Long userId, @RequestParam Long contentId) {
        boolean result = interactionService.unlikeContent(userId, contentId);
        return ApiResult.success(result);
    }

    /**
     * 检查是否已点赞
     */
    @GetMapping("/like/check")
    public ApiResult<Boolean> checkLiked(@RequestParam Long userId, @RequestParam Long contentId) {
        boolean result = interactionService.isLiked(userId, contentId);
        return ApiResult.success(result);
    }

    /**
     * 获取内容的点赞用户列表
     */
    @GetMapping("/like/{contentId}")
    public ApiResult<IPage<SocialLike>> getContentLikes(
            @PathVariable Long contentId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        IPage<SocialLike> result = interactionService.getContentLikes(contentId, pageNum, pageSize);
        return ApiResult.success(result);
    }

    // ========== 收藏相关 ==========

    /**
     * 收藏内容
     */
    @PostMapping("/favorite")
    public ApiResult<Boolean> favoriteContent(
            @RequestParam Long userId, 
            @RequestParam Long contentId,
            @RequestParam(required = false) Long folderId) {
        boolean result = interactionService.favoriteContent(userId, contentId, folderId);
        return ApiResult.success(result);
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/favorite")
    public ApiResult<Boolean> unfavoriteContent(@RequestParam Long userId, @RequestParam Long contentId) {
        boolean result = interactionService.unfavoriteContent(userId, contentId);
        return ApiResult.success(result);
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/favorite/check")
    public ApiResult<Boolean> checkFavorited(@RequestParam Long userId, @RequestParam Long contentId) {
        boolean result = interactionService.isFavorited(userId, contentId);
        return ApiResult.success(result);
    }

    /**
     * 获取用户的收藏列表
     */
    @GetMapping("/favorite/user/{userId}")
    public ApiResult<IPage<SocialFavorite>> getUserFavorites(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        IPage<SocialFavorite> result = interactionService.getUserFavorites(userId, pageNum, pageSize);
        return ApiResult.success(result);
    }

    // ========== 分享相关 ==========

    /**
     * 分享内容
     */
    @PostMapping("/share")
    public ApiResult<Boolean> shareContent(@RequestBody ShareRequest request) {
        boolean result = interactionService.shareContent(
            request.getUserId(), 
            request.getContentId(), 
            request.getShareType(), 
            request.getSharePlatform(), 
            request.getShareComment()
        );
        return ApiResult.success(result);
    }

    /**
     * 获取内容的分享记录
     */
    @GetMapping("/share/{contentId}")
    public ApiResult<IPage<SocialShare>> getContentShares(
            @PathVariable Long contentId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        IPage<SocialShare> result = interactionService.getContentShares(contentId, pageNum, pageSize);
        return ApiResult.success(result);
    }

    // ========== 评论相关 ==========

    /**
     * 创建评论
     */
    @PostMapping("/comment")
    public ApiResult<Long> createComment(@RequestBody SocialComment comment) {
        Long commentId = interactionService.createComment(comment);
        return ApiResult.success(commentId);
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/comment/{commentId}")
    public ApiResult<Boolean> deleteComment(@PathVariable Long commentId, @RequestParam Long userId) {
        boolean result = interactionService.deleteComment(commentId, userId);
        return ApiResult.success(result);
    }

    /**
     * 获取内容的评论列表
     */
    @GetMapping("/comment/{contentId}")
    public ApiResult<IPage<SocialComment>> getContentComments(
            @PathVariable Long contentId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        IPage<SocialComment> result = interactionService.getContentComments(contentId, pageNum, pageSize);
        return ApiResult.success(result);
    }

    /**
     * 获取评论的回复列表
     */
    @GetMapping("/comment/{parentCommentId}/replies")
    public ApiResult<IPage<SocialComment>> getCommentReplies(
            @PathVariable Long parentCommentId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        IPage<SocialComment> result = interactionService.getCommentReplies(parentCommentId, pageNum, pageSize);
        return ApiResult.success(result);
    }

    // ========== 批量操作 ==========

    /**
     * 获取用户对内容的互动状态
     */
    @GetMapping("/status")
    public ApiResult<SocialInteractionService.UserInteractionStatus> getUserInteractionStatus(
            @RequestParam Long userId, @RequestParam Long contentId) {
        SocialInteractionService.UserInteractionStatus status = 
            interactionService.getUserInteractionStatus(userId, contentId);
        return ApiResult.success(status);
    }

    // ========== 请求对象 ==========

    /**
     * 分享请求对象
     */
    @lombok.Data
    public static class ShareRequest {
        private Long userId;
        private Long contentId;
        private Integer shareType;
        private String sharePlatform;
        private String shareComment;
    }
}