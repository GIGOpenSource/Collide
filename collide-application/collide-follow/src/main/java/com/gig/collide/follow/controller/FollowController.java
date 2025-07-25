package com.gig.collide.follow.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
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
import com.gig.collide.web.vo.Result;
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
@Tag(name = "关注管理", description = "用户关注、粉丝管理相关接口")
public class FollowController {

    private final FollowFacadeService followFacadeService;

    @PostMapping
    @SaCheckLogin
    public Result<FollowResponse> follow(@Valid @RequestBody FollowParam param) {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            log.info("用户关注操作，当前用户: {}, 被关注用户: {}", currentUserId, param.getFollowedUserId());

            FollowRequest followRequest = new FollowRequest();
            followRequest.setFollowerUserId(currentUserId);
            followRequest.setFollowedUserId(param.getFollowedUserId());
            followRequest.setFollowType(param.getFollowType());

            FollowResponse followResponse = followFacadeService.follow(followRequest);
            return Result.success(followResponse);
        } catch (Exception e) {
            log.error("关注操作失败", e);
            return Result.error("FOLLOW_ERROR", "关注失败，请稍后重试");
        }
    }

    @DeleteMapping("/{followedUserId}")
    @SaCheckLogin
    public Result<FollowResponse> unfollow(@PathVariable Long followedUserId) {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            log.info("用户取消关注操作，当前用户: {}, 被关注用户: {}", currentUserId, followedUserId);

            UnfollowRequest unfollowRequest = new UnfollowRequest();
            unfollowRequest.setFollowerUserId(currentUserId);
            unfollowRequest.setFollowedUserId(followedUserId);

            FollowResponse followResponse = followFacadeService.unfollow(unfollowRequest);
            return Result.success(followResponse);
        } catch (Exception e) {
            log.error("取消关注操作失败", e);
            return Result.error("UNFOLLOW_ERROR", "取消关注失败，请稍后重试");
        }
    }

    @GetMapping("/check/{followedUserId}")
    @SaCheckLogin
    public Result<FollowInfo> checkFollow(@PathVariable Long followedUserId,
                                         @RequestParam(defaultValue = "true") Boolean checkMutual) {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();

            FollowQueryRequest queryRequest = new FollowQueryRequest();
            queryRequest.setFollowerUserId(currentUserId);
            queryRequest.setFollowedUserId(followedUserId);
            queryRequest.setCheckMutualFollow(checkMutual);

            FollowQueryResponse<FollowInfo> queryResponse = followFacadeService.queryFollow(queryRequest);
            return Result.success(queryResponse.getData());
        } catch (Exception e) {
            log.error("检查关注关系失败", e);
            return Result.error("CHECK_FOLLOW_ERROR", "检查关注关系失败，请稍后重试");
        }
    }

    @GetMapping("/following")
    @SaCheckLogin
    public Result<PageResponse<FollowInfo>> getFollowingList(
            @RequestParam(defaultValue = "1") @Min(1) Integer pageNo,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer pageSize) {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();

            FollowQueryRequest queryRequest = new FollowQueryRequest();
            queryRequest.setFollowerUserId(currentUserId);
            queryRequest.setPageNo(pageNo);
            queryRequest.setPageSize(pageSize);

            PageResponse<FollowInfo> pageResponse = followFacadeService.pageQueryFollowing(queryRequest);
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("获取关注列表失败", e);
            return Result.error("GET_FOLLOWING_ERROR", "获取关注列表失败，请稍后重试");
        }
    }

    @GetMapping("/followers")
    @SaCheckLogin
    public Result<PageResponse<FollowInfo>> getFollowersList(
            @RequestParam(defaultValue = "1") @Min(1) Integer pageNo,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer pageSize) {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();

            FollowQueryRequest queryRequest = new FollowQueryRequest();
            queryRequest.setFollowedUserId(currentUserId);
            queryRequest.setPageNo(pageNo);
            queryRequest.setPageSize(pageSize);

            PageResponse<FollowInfo> pageResponse = followFacadeService.pageQueryFollowers(queryRequest);
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("获取粉丝列表失败", e);
            return Result.error("GET_FOLLOWERS_ERROR", "获取粉丝列表失败，请稍后重试");
        }
    }

    @GetMapping("/statistics")
    @SaCheckLogin
    public Result<FollowStatistics> getFollowStatistics() {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            FollowQueryResponse<FollowStatistics> queryResponse = 
                followFacadeService.getFollowStatistics(currentUserId);
            return Result.success(queryResponse.getData());
        } catch (Exception e) {
            log.error("获取关注统计失败", e);
            return Result.error("GET_STATISTICS_ERROR", "获取关注统计失败，请稍后重试");
        }
    }

    @GetMapping("/statistics/{userId}")
    public Result<FollowStatistics> getUserFollowStatistics(@PathVariable Long userId) {
        try {
            FollowQueryResponse<FollowStatistics> queryResponse = 
                followFacadeService.getFollowStatistics(userId);
            return Result.success(queryResponse.getData());
        } catch (Exception e) {
            log.error("获取用户关注统计失败", e);
            return Result.error("GET_USER_STATISTICS_ERROR", "获取用户关注统计失败，请稍后重试");
        }
    }

    /**
     * 关注请求参数
     */
    public static class FollowParam {
        @NotNull(message = "被关注用户ID不能为空")
        private Long followedUserId;
        
        private FollowType followType = FollowType.NORMAL;

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
} 