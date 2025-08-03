package com.gig.collide.social.controller;

import com.gig.collide.api.social.SocialInteractionFacadeService;
import com.gig.collide.api.social.request.InteractionQueryRequest;
import com.gig.collide.api.social.request.InteractionRequest;
import com.gig.collide.api.social.vo.InteractionVO;
import com.gig.collide.api.social.vo.UserInteractionStatusVO;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 社交互动控制器 - DDD重构版
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
@Slf4j
@RestController
@RequestMapping("/api/social/interaction")
@RequiredArgsConstructor
public class SocialInteractionController {

    private final SocialInteractionFacadeService interactionFacadeService;

    // ========== 点赞相关 ==========

    /**
     * 点赞内容
     */
    @PostMapping("/like")
    public Result<Boolean> likeContent(@Valid @RequestBody InteractionRequest request) {
        return interactionFacadeService.likeContent(request);
    }

    /**
     * 取消点赞
     */
    @DeleteMapping("/like")
    public Result<Boolean> unlikeContent(@RequestParam Long userId, @RequestParam Long contentId) {
        return interactionFacadeService.unlikeContent(userId, contentId);
    }

    /**
     * 检查是否已点赞
     */
    @GetMapping("/like/check")
    public Result<Boolean> checkLiked(@RequestParam Long userId, @RequestParam Long contentId) {
        return interactionFacadeService.checkLiked(userId, contentId);
    }

    /**
     * 获取内容的点赞用户列表
     */
    @GetMapping("/like/{contentId}")
    public PageResponse<InteractionVO> getContentLikes(
            @PathVariable Long contentId,
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "20") int pageSize) {
        InteractionQueryRequest request = new InteractionQueryRequest();
        request.setContentId(contentId);
        request.setInteractionType(InteractionQueryRequest.InteractionType.LIKE.getCode());
        request.setCurrentPage(currentPage);
        request.setPageSize(pageSize);
        return interactionFacadeService.getContentLikes(request);
    }

    // ========== 收藏相关 ==========

    /**
     * 收藏内容
     */
    @PostMapping("/favorite")
    public Result<Boolean> favoriteContent(@Valid @RequestBody InteractionRequest request) {
        return interactionFacadeService.favoriteContent(request);
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/favorite")
    public Result<Boolean> unfavoriteContent(@RequestParam Long userId, @RequestParam Long contentId) {
        return interactionFacadeService.unfavoriteContent(userId, contentId);
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/favorite/check")
    public Result<Boolean> checkFavorited(@RequestParam Long userId, @RequestParam Long contentId) {
        return interactionFacadeService.checkFavorited(userId, contentId);
    }

    /**
     * 获取用户的收藏列表
     */
    @GetMapping("/favorite/user/{userId}")
    public PageResponse<InteractionVO> getUserFavorites(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "20") int pageSize) {
        InteractionQueryRequest request = new InteractionQueryRequest();
        request.setUserId(userId);
        request.setInteractionType(InteractionQueryRequest.InteractionType.FAVORITE.getCode());
        request.setCurrentPage(currentPage);
        request.setPageSize(pageSize);
        return interactionFacadeService.getUserFavorites(request);
    }

    // ========== 分享相关 ==========

    /**
     * 分享内容
     */
    @PostMapping("/share")
    public Result<Boolean> shareContent(@Valid @RequestBody InteractionRequest request) {
        return interactionFacadeService.shareContent(request);
    }

    /**
     * 获取内容的分享记录
     */
    @GetMapping("/share/{contentId}")
    public PageResponse<InteractionVO> getContentShares(
            @PathVariable Long contentId,
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "20") int pageSize) {
        InteractionQueryRequest request = new InteractionQueryRequest();
        request.setContentId(contentId);
        request.setInteractionType(InteractionQueryRequest.InteractionType.SHARE.getCode());
        request.setCurrentPage(currentPage);
        request.setPageSize(pageSize);
        return interactionFacadeService.getContentShares(request);
    }

    // ========== 评论相关 ==========

    /**
     * 创建评论
     */
    @PostMapping("/comment")
    public Result<Long> createComment(@Valid @RequestBody InteractionRequest request) {
        return interactionFacadeService.createComment(request);
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/comment/{commentId}")
    public Result<Boolean> deleteComment(@PathVariable Long commentId, @RequestParam Long userId) {
        return interactionFacadeService.deleteComment(commentId, userId);
    }

    /**
     * 获取内容的评论列表
     */
    @GetMapping("/comment/{contentId}")
    public PageResponse<InteractionVO> getContentComments(
            @PathVariable Long contentId,
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "20") int pageSize) {
        InteractionQueryRequest request = new InteractionQueryRequest();
        request.setContentId(contentId);
        request.setInteractionType(InteractionQueryRequest.InteractionType.COMMENT.getCode());
        request.setCurrentPage(currentPage);
        request.setPageSize(pageSize);
        return interactionFacadeService.getContentComments(request);
    }

    /**
     * 获取评论的回复列表
     */
    @GetMapping("/comment/{parentCommentId}/replies")
    public PageResponse<InteractionVO> getCommentReplies(
            @PathVariable Long parentCommentId,
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "20") int pageSize) {
        InteractionQueryRequest request = new InteractionQueryRequest();
        request.setParentCommentId(parentCommentId);
        request.setInteractionType(InteractionQueryRequest.InteractionType.COMMENT.getCode());
        request.setCurrentPage(currentPage);
        request.setPageSize(pageSize);
        return interactionFacadeService.getCommentReplies(request);
    }

    // ========== 批量操作 ==========

    /**
     * 获取用户对内容的互动状态
     */
    @GetMapping("/status")
    public Result<UserInteractionStatusVO> getUserInteractionStatus(
            @RequestParam Long userId, @RequestParam Long contentId) {
        return interactionFacadeService.getUserInteractionStatus(userId, contentId);
    }

    /**
     * 批量获取用户对多个内容的互动状态
     */
    @PostMapping("/status/batch")
    public Result<Map<Long, UserInteractionStatusVO>> getBatchUserInteractionStatus(
            @RequestParam Long userId, @RequestBody List<Long> contentIds) {
        return interactionFacadeService.getBatchUserInteractionStatus(userId, contentIds);
    }
}