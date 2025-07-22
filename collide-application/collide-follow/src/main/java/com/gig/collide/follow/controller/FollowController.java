package com.gig.collide.follow.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.gig.collide.api.follow.constant.FollowType;
import com.gig.collide.api.follow.request.FollowRequest;
import com.gig.collide.api.follow.request.FollowQueryRequest;
import com.gig.collide.api.follow.request.UnfollowRequest;
import com.gig.collide.api.follow.response.FollowResponse;
import com.gig.collide.api.follow.response.FollowQueryResponse;
import com.gig.collide.api.follow.response.data.FollowInfo;
import com.gig.collide.api.follow.response.data.FollowStatistics;
import com.gig.collide.api.follow.service.FollowFacadeService;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.base.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 关注控制器
 * 提供用户关注相关的 REST API
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/follow")
@RequiredArgsConstructor
@Validated
@Tag(name = "关注管理", description = "用户关注关系管理相关接口")
public class FollowController {

    private final FollowFacadeService followFacadeService;

    @PostMapping("/follow")
    @Operation(summary = "关注用户", description = "关注指定用户")
    public Response<FollowResponse> follow(@Valid @RequestBody FollowParam param) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        log.info("用户关注操作，当前用户: {}, 被关注用户: {}", currentUserId, param.getFollowedUserId());

        FollowRequest followRequest = new FollowRequest();
        followRequest.setFollowerUserId(currentUserId);
        followRequest.setFollowedUserId(param.getFollowedUserId());
        followRequest.setFollowType(param.getFollowType() != null ? param.getFollowType() : FollowType.NORMAL);

        FollowResponse followResponse = followFacadeService.follow(followRequest);

        return Response.success(followResponse);
    }

    @PostMapping("/unfollow")
    @Operation(summary = "取消关注", description = "取消关注指定用户")
    public Response<FollowResponse> unfollow(@Valid @RequestBody UnfollowParam param) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        log.info("用户取消关注操作，当前用户: {}, 被关注用户: {}", currentUserId, param.getFollowedUserId());

        UnfollowRequest unfollowRequest = new UnfollowRequest();
        unfollowRequest.setFollowerUserId(currentUserId);
        unfollowRequest.setFollowedUserId(param.getFollowedUserId());

        FollowResponse followResponse = followFacadeService.unfollow(unfollowRequest);

        return Response.success(followResponse);
    }

    @GetMapping("/check/{followedUserId}")
    @Operation(summary = "检查关注关系", description = "检查当前用户是否关注指定用户")
    public Response<FollowInfo> checkFollow(
            @Parameter(description = "被关注用户ID") @PathVariable Long followedUserId,
            @Parameter(description = "是否检查相互关注") @RequestParam(defaultValue = "true") Boolean checkMutual) {
        
        Long currentUserId = StpUtil.getLoginIdAsLong();

        FollowQueryRequest queryRequest = new FollowQueryRequest();
        queryRequest.setFollowerUserId(currentUserId);
        queryRequest.setFollowedUserId(followedUserId);
        queryRequest.setCheckMutualFollow(checkMutual);

        FollowQueryResponse<FollowInfo> queryResponse = followFacadeService.queryFollow(queryRequest);

        return Response.success(queryResponse.getData());
    }

    @GetMapping("/following")
    @Operation(summary = "获取关注列表", description = "分页获取当前用户的关注列表")
    public Response<PageResponse<FollowInfo>> getFollowingList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) Integer pageNo,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer pageSize) {
        
        Long currentUserId = StpUtil.getLoginIdAsLong();

        FollowQueryRequest queryRequest = new FollowQueryRequest();
        queryRequest.setFollowerUserId(currentUserId);
        queryRequest.setPageNo(pageNo);
        queryRequest.setPageSize(pageSize);

        PageResponse<FollowInfo> pageResponse = followFacadeService.pageQueryFollowing(queryRequest);

        return Response.success(pageResponse);
    }

    @GetMapping("/followers")
    @Operation(summary = "获取粉丝列表", description = "分页获取当前用户的粉丝列表")
    public Response<PageResponse<FollowInfo>> getFollowersList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) Integer pageNo,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer pageSize) {
        
        Long currentUserId = StpUtil.getLoginIdAsLong();

        FollowQueryRequest queryRequest = new FollowQueryRequest();
        queryRequest.setFollowedUserId(currentUserId);
        queryRequest.setPageNo(pageNo);
        queryRequest.setPageSize(pageSize);

        PageResponse<FollowInfo> pageResponse = followFacadeService.pageQueryFollowers(queryRequest);

        return Response.success(pageResponse);
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取关注统计", description = "获取当前用户的关注统计信息")
    public Response<FollowStatistics> getFollowStatistics() {
        Long currentUserId = StpUtil.getLoginIdAsLong();

        FollowQueryResponse<FollowStatistics> queryResponse = 
            followFacadeService.getFollowStatistics(currentUserId);

        return Response.success(queryResponse.getData());
    }

    @GetMapping("/statistics/{userId}")
    @Operation(summary = "获取用户关注统计", description = "获取指定用户的关注统计信息")
    public Response<FollowStatistics> getUserFollowStatistics(
            @Parameter(description = "用户ID") @PathVariable Long userId) {

        FollowQueryResponse<FollowStatistics> queryResponse = 
            followFacadeService.getFollowStatistics(userId);

        return Response.success(queryResponse.getData());
    }

    /**
     * 关注参数
     */
    public static class FollowParam {
        @NotNull(message = "被关注用户ID不能为空")
        private Long followedUserId;
        
        private FollowType followType;

        public Long getFollowedUserId() {
            return followedUserId;
        }

        public void setFollowedUserId(Long followedUserId) {
            this.followedUserId = followedUserId;
        }

        public FollowType getFollowType() {
            return followType;
        }

        public void setFollowType(FollowType followType) {
            this.followType = followType;
        }
    }

    /**
     * 取消关注参数
     */
    public static class UnfollowParam {
        @NotNull(message = "被关注用户ID不能为空")
        private Long followedUserId;

        public Long getFollowedUserId() {
            return followedUserId;
        }

        public void setFollowedUserId(Long followedUserId) {
            this.followedUserId = followedUserId;
        }
    }
} 