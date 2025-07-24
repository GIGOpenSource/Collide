package com.gig.collide.social.controller;

import com.gig.collide.api.social.request.SocialPostCreateRequest;
import com.gig.collide.api.social.request.SocialPostQueryRequest;
import com.gig.collide.api.social.response.SocialPostResponse;
import com.gig.collide.api.social.response.data.SocialPostInfo;
import com.gig.collide.api.social.service.SocialFacadeService;
import com.gig.collide.base.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 社交动态服务控制器
 * 
 * @author Collide
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/social")
@RequiredArgsConstructor
@Tag(name = "社交动态服务", description = "社交动态相关接口")
public class SocialController {
    
    private final SocialFacadeService socialFacadeService;
    
    @PostMapping("/posts")
    @Operation(summary = "发布社交动态", description = "创建并发布新的社交动态")
    public SocialPostResponse publishPost(@Valid @RequestBody SocialPostCreateRequest createRequest) {
        return socialFacadeService.publishPost(createRequest);
    }
    
    @PutMapping("/posts/{postId}")
    @Operation(summary = "更新社交动态", description = "更新指定的社交动态")
    public SocialPostResponse updatePost(
            @Parameter(description = "动态ID") @PathVariable Long postId,
            @Valid @RequestBody SocialPostCreateRequest updateRequest) {
        return socialFacadeService.updatePost(postId, updateRequest);
    }
    
    @DeleteMapping("/posts/{postId}")
    @Operation(summary = "删除社交动态", description = "删除指定的社交动态")
    public SocialPostResponse deletePost(
            @Parameter(description = "动态ID") @PathVariable Long postId,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        return socialFacadeService.deletePost(postId, userId);
    }
    
    @PostMapping("/posts/query")
    @Operation(summary = "分页查询社交动态", description = "根据条件分页查询社交动态列表")
    public PageResponse<SocialPostInfo> pageQueryPosts(@Valid @RequestBody SocialPostQueryRequest queryRequest) {
        return socialFacadeService.pageQueryPosts(queryRequest);
    }
    
    @GetMapping("/posts/{postId}")
    @Operation(summary = "查询动态详情", description = "获取指定社交动态的详细信息")
    public SocialPostInfo queryPostDetail(
            @Parameter(description = "动态ID") @PathVariable Long postId,
            @Parameter(description = "当前用户ID") @RequestParam(required = false) Long currentUserId) {
        return socialFacadeService.queryPostDetail(postId, currentUserId);
    }
    
    @GetMapping("/posts/timeline/{userId}")
    @Operation(summary = "获取用户时间线", description = "获取指定用户的时间线动态")
    public PageResponse<SocialPostInfo> getUserTimeline(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer currentPage,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        return socialFacadeService.getUserTimeline(userId, currentPage, pageSize);
    }
    
    @GetMapping("/posts/feed/{userId}")
    @Operation(summary = "获取关注动态流", description = "获取用户关注的人发布的动态")
    public PageResponse<SocialPostInfo> getFollowingFeed(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer currentPage,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        return socialFacadeService.getFollowingFeed(userId, currentPage, pageSize);
    }
    
    @GetMapping("/posts/hot")
    @Operation(summary = "获取热门动态", description = "获取指定时间范围内的热门动态")
    public PageResponse<SocialPostInfo> getHotPosts(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer currentPage,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "20") Integer pageSize,
            @Parameter(description = "时间范围(hour/day/week)") @RequestParam(defaultValue = "day") String timeRange) {
        return socialFacadeService.getHotPosts(currentPage, pageSize, timeRange);
    }
    
    @GetMapping("/posts/nearby")
    @Operation(summary = "获取附近动态", description = "获取指定地理位置附近的动态")
    public PageResponse<SocialPostInfo> getNearbyPosts(
            @Parameter(description = "经度") @RequestParam Double longitude,
            @Parameter(description = "纬度") @RequestParam Double latitude,
            @Parameter(description = "搜索半径(公里)") @RequestParam(defaultValue = "10.0") Double radius,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer currentPage,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        return socialFacadeService.getNearbyPosts(longitude, latitude, radius, currentPage, pageSize);
    }
    
    @GetMapping("/posts/search")
    @Operation(summary = "搜索动态", description = "根据关键词搜索社交动态")
    public PageResponse<SocialPostInfo> searchPosts(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer currentPage,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        return socialFacadeService.searchPosts(keyword, currentPage, pageSize);
    }
    
    @PostMapping("/posts/{postId}/like")
    @Operation(summary = "点赞/取消点赞动态", description = "对指定动态进行点赞或取消点赞操作")
    public SocialPostResponse likePost(
            @Parameter(description = "动态ID") @PathVariable Long postId,
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "是否点赞") @RequestParam Boolean isLike) {
        return socialFacadeService.likePost(postId, userId, isLike);
    }
    
    @PostMapping("/posts/{postId}/share")
    @Operation(summary = "转发动态", description = "转发指定的社交动态")
    public SocialPostResponse sharePost(
            @Parameter(description = "动态ID") @PathVariable Long postId,
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "转发评论") @RequestParam(required = false) String comment) {
        return socialFacadeService.sharePost(postId, userId, comment);
    }
    
    @PostMapping("/posts/{postId}/report")
    @Operation(summary = "举报动态", description = "举报不当的社交动态")
    public SocialPostResponse reportPost(
            @Parameter(description = "动态ID") @PathVariable Long postId,
            @Parameter(description = "举报用户ID") @RequestParam Long userId,
            @Parameter(description = "举报原因") @RequestParam String reason) {
        return socialFacadeService.reportPost(postId, userId, reason);
    }
    
    @GetMapping("/users/{userId}/posts/count")
    @Operation(summary = "统计用户动态数量", description = "获取指定用户发布的动态总数")
    public Long countUserPosts(@Parameter(description = "用户ID") @PathVariable Long userId) {
        return socialFacadeService.countUserPosts(userId);
    }
    
    @PostMapping("/posts/{postId}/view")
    @Operation(summary = "增加浏览数", description = "记录动态被浏览，增加浏览计数")
    public void incrementViewCount(
            @Parameter(description = "动态ID") @PathVariable Long postId,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId) {
        socialFacadeService.incrementViewCount(postId, userId);
    }
    
    @GetMapping("/posts/{postId}/hot-score")
    @Operation(summary = "计算热度分数", description = "计算指定动态的热度分数")
    public Double calculateHotScore(@Parameter(description = "动态ID") @PathVariable Long postId) {
        return socialFacadeService.calculateHotScore(postId);
    }
} 