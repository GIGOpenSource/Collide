package com.gig.collide.social.controller;

import com.gig.collide.api.social.request.SocialPostCreateRequest;
import com.gig.collide.api.social.request.SocialPostQueryRequest;
import com.gig.collide.api.social.response.SocialPostResponse;
import com.gig.collide.api.social.response.data.SocialPostInfo;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.social.facade.SocialFacadeServiceImpl;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 社交动态 REST 控制器
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/social")
@RequiredArgsConstructor
@Tag(name = "社交动态", description = "社交动态相关接口")
public class SocialController {

    private final SocialFacadeServiceImpl socialFacadeService;

    @Operation(summary = "发布动态", description = "发布新的社交动态")
    @PostMapping("/posts")
    public Result<Long> publishPost(@Valid @RequestBody SocialPostCreateRequest request) {
        SocialPostResponse response = socialFacadeService.publishPost(request);
        if (response.isSuccess()) {
            return Result.success(response.getData());
        }
        return Result.error(response.getErrorCode(), response.getErrorMessage());
    }

    @Operation(summary = "分页查询动态", description = "分页查询社交动态列表")
    @GetMapping("/posts")
    public Result<PageResponse<SocialPostInfo>> pageQueryPosts(
            @Parameter(description = "查询类型") @RequestParam(defaultValue = "public") String queryType,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer pageNo,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "20") Integer pageSize,
            @Parameter(description = "作者ID") @RequestParam(required = false) Long authorId,
            @Parameter(description = "当前用户ID") @RequestParam(required = false) Long currentUserId,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        
        SocialPostQueryRequest queryRequest = new SocialPostQueryRequest();
        queryRequest.setQueryType(queryType);
        queryRequest.setCurrentPage(pageNo);
        queryRequest.setPageSize(pageSize);
        queryRequest.setAuthorId(authorId);
        queryRequest.setCurrentUserId(currentUserId);
        queryRequest.setKeyword(keyword);
        
        PageResponse<SocialPostInfo> response = socialFacadeService.pageQueryPosts(queryRequest);
        return Result.success(response);
    }

    @Operation(summary = "获取动态详情", description = "根据动态ID获取详细信息")
    @GetMapping("/posts/{postId}")
    public Result<SocialPostInfo> getPostDetail(
            @Parameter(description = "动态ID") @PathVariable Long postId,
            @Parameter(description = "当前用户ID") @RequestParam(required = false) Long currentUserId) {
        
        SocialPostInfo postInfo = socialFacadeService.queryPostDetail(postId, currentUserId);
        if (postInfo != null) {
            return Result.success(postInfo);
        }
        return Result.error("POST_NOT_FOUND", "动态不存在或无权限查看");
    }

    @Operation(summary = "点赞/取消点赞", description = "对动态进行点赞或取消点赞操作")
    @PostMapping("/posts/{postId}/like")
    public Result<Void> likePost(
            @Parameter(description = "动态ID") @PathVariable Long postId,
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "是否点赞") @RequestParam Boolean isLike) {
        
        SocialPostResponse response = socialFacadeService.likePost(postId, userId, isLike);
        if (response.isSuccess()) {
            return Result.success(null);
        }
        return Result.error(response.getErrorCode(), response.getErrorMessage());
    }

    @Operation(summary = "转发动态", description = "转发指定的社交动态")
    @PostMapping("/posts/{postId}/share")
    public Result<Long> sharePost(
            @Parameter(description = "动态ID") @PathVariable Long postId,
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "转发评论") @RequestParam(required = false) String comment) {
        
        SocialPostResponse response = socialFacadeService.sharePost(postId, userId, comment);
        if (response.isSuccess()) {
            return Result.success(response.getData());
        }
        return Result.error(response.getErrorCode(), response.getErrorMessage());
    }

    @Operation(summary = "删除动态", description = "删除指定的社交动态")
    @DeleteMapping("/posts/{postId}")
    public Result<Void> deletePost(
            @Parameter(description = "动态ID") @PathVariable Long postId,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        
        SocialPostResponse response = socialFacadeService.deletePost(postId, userId);
        if (response.isSuccess()) {
            return Result.success(null);
        }
        return Result.error(response.getErrorCode(), response.getErrorMessage());
    }

    @Operation(summary = "获取用户时间线", description = "获取指定用户的动态时间线")
    @GetMapping("/users/{userId}/timeline")
    public Result<PageResponse<SocialPostInfo>> getUserTimeline(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer pageNo,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        
        PageResponse<SocialPostInfo> response = socialFacadeService.getUserTimeline(userId, pageNo, pageSize);
        return Result.success(response);
    }

    @Operation(summary = "获取关注动态流", description = "获取当前用户关注的动态流")
    @GetMapping("/feed/following")
    public Result<PageResponse<SocialPostInfo>> getFollowingFeed(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer pageNo,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        
        PageResponse<SocialPostInfo> response = socialFacadeService.getFollowingFeed(userId, pageNo, pageSize);
        return Result.success(response);
    }

    @Operation(summary = "获取热门动态", description = "获取热门社交动态")
    @GetMapping("/feed/hot")
    public Result<PageResponse<SocialPostInfo>> getHotPosts(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer pageNo,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "20") Integer pageSize,
            @Parameter(description = "时间范围") @RequestParam(defaultValue = "day") String timeRange) {
        
        PageResponse<SocialPostInfo> response = socialFacadeService.getHotPosts(pageNo, pageSize, timeRange);
        return Result.success(response);
    }

    @Operation(summary = "获取附近动态", description = "根据地理位置获取附近的动态")
    @GetMapping("/feed/nearby")
    public Result<PageResponse<SocialPostInfo>> getNearbyPosts(
            @Parameter(description = "经度") @RequestParam Double longitude,
            @Parameter(description = "纬度") @RequestParam Double latitude,
            @Parameter(description = "搜索半径(公里)") @RequestParam(defaultValue = "10.0") Double radius,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer pageNo,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        
        PageResponse<SocialPostInfo> response = socialFacadeService.getNearbyPosts(
            longitude, latitude, radius, pageNo, pageSize);
        return Result.success(response);
    }

    @Operation(summary = "搜索动态", description = "根据关键词搜索动态")
    @GetMapping("/posts/search")
    public Result<PageResponse<SocialPostInfo>> searchPosts(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer pageNo,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        
        PageResponse<SocialPostInfo> response = socialFacadeService.searchPosts(keyword, pageNo, pageSize);
        return Result.success(response);
    }

    @Operation(summary = "统计用户动态数", description = "统计指定用户的动态总数")
    @GetMapping("/users/{userId}/posts/count")
    public Result<Long> countUserPosts(@Parameter(description = "用户ID") @PathVariable Long userId) {
        Long count = socialFacadeService.countUserPosts(userId);
        return Result.success(count);
    }
} 