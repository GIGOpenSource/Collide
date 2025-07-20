package com.gig.collide.follow.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.gig.collide.api.follow.response.data.FollowInfo;
import com.gig.collide.api.follow.response.data.FollowerInfo;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.follow.domain.entity.Follow;
import com.gig.collide.follow.domain.entity.convertor.FollowConvertor;
import com.gig.collide.follow.domain.service.FollowService;
import com.gig.collide.follow.infrastructure.exception.FollowException;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.gig.collide.follow.infrastructure.exception.FollowErrorCode.*;

/**
 * 关注控制器
 * @author GIG
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("follow")
public class FollowController {

    @Autowired
    private FollowService followService;

    /**
     * 关注用户
     */
    @PostMapping("/follow/{followedUserId}")
    public Result<Boolean> follow(@PathVariable Long followedUserId) {
        String currentUserId = (String) StpUtil.getLoginId();
        Long followerUserId = Long.valueOf(currentUserId);
        
        if (followerUserId.equals(followedUserId)) {
            throw new FollowException(CANNOT_FOLLOW_SELF);
        }
        
        boolean result = followService.follow(followerUserId, followedUserId);
        return Result.success(result);
    }

    /**
     * 取消关注用户
     */
    @DeleteMapping("/unfollow/{followedUserId}")
    public Result<Boolean> unfollow(@PathVariable Long followedUserId) {
        String currentUserId = (String) StpUtil.getLoginId();
        Long followerUserId = Long.valueOf(currentUserId);
        
        boolean result = followService.unfollow(followerUserId, followedUserId);
        return Result.success(result);
    }

    /**
     * 查询关注状态
     */
    @GetMapping("/status/{followedUserId}")
    public Result<Boolean> getFollowStatus(@PathVariable Long followedUserId) {
        String currentUserId = (String) StpUtil.getLoginId();
        Long followerUserId = Long.valueOf(currentUserId);
        
        boolean isFollowed = followService.isFollowed(followerUserId, followedUserId);
        return Result.success(isFollowed);
    }

    /**
     * 获取关注列表
     */
    @GetMapping("/following")
    public Result<PageResponse<FollowInfo>> getFollowList(
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        String currentUserId = (String) StpUtil.getLoginId();
        Long userId = Long.valueOf(currentUserId);
        
        PageResponse<FollowInfo> result = followService.getFollowList(userId, currentPage, pageSize);
        return Result.success(result);
    }

    /**
     * 获取粉丝列表
     */
    @GetMapping("/followers")
    public Result<PageResponse<FollowerInfo>> getFollowerList(
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        String currentUserId = (String) StpUtil.getLoginId();
        Long userId = Long.valueOf(currentUserId);
        
        PageResponse<FollowerInfo> result = followService.getFollowerList(userId, currentPage, pageSize);
        return Result.success(result);
    }

    /**
     * 获取关注统计
     */
    @GetMapping("/statistics")
    public Result<FollowStatisticsVO> getFollowStatistics() {
        String currentUserId = (String) StpUtil.getLoginId();
        Long userId = Long.valueOf(currentUserId);
        
        int followingCount = followService.getFollowingCount(userId);
        int followerCount = followService.getFollowerCount(userId);
        
        FollowStatisticsVO statistics = new FollowStatisticsVO();
        statistics.setFollowingCount(followingCount);
        statistics.setFollowerCount(followerCount);
        
        return Result.success(statistics);
    }

    /**
     * 关注统计VO
     */
    public static class FollowStatisticsVO {
        private Integer followingCount;
        private Integer followerCount;

        public Integer getFollowingCount() {
            return followingCount;
        }

        public void setFollowingCount(Integer followingCount) {
            this.followingCount = followingCount;
        }

        public Integer getFollowerCount() {
            return followerCount;
        }

        public void setFollowerCount(Integer followerCount) {
            this.followerCount = followerCount;
        }
    }
} 