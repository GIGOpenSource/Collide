package com.gig.collide.follow.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.follow.FollowFacadeService;
import com.gig.collide.api.follow.request.*;
import com.gig.collide.api.follow.response.FollowResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.follow.domain.entity.Follow;
import com.gig.collide.follow.domain.service.FollowService;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 关注门面服务实现类 - 简洁版
 * 基于Dubbo RPC，提供关注相关的远程服务调用
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@Service
@DubboService
@RequiredArgsConstructor
public class FollowFacadeServiceImpl implements FollowFacadeService {

    private final FollowService followService;

    @Override
    public Result<FollowResponse> followUser(FollowCreateRequest request) {
        try {
            log.info("RPC关注用户: followerId={}, followeeId={}", request.getFollowerId(), request.getFolloweeId());

            // 转换请求对象为实体
            Follow follow = convertCreateRequestToEntity(request);
            
            // 调用业务逻辑
            Follow savedFollow = followService.followUser(follow);
            
            // 转换响应对象
            FollowResponse response = convertToResponse(savedFollow);
            
            return Result.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("关注参数验证失败: {}", e.getMessage());
            return Result.error("FOLLOW_PARAM_ERROR", e.getMessage());
        } catch (IllegalStateException e) {
            log.warn("关注状态检查失败: {}", e.getMessage());
            return Result.error("FOLLOW_STATE_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("关注用户失败", e);
            return Result.error("FOLLOW_CREATE_ERROR", "关注用户失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> unfollowUser(FollowDeleteRequest request) {
        try {
            log.info("RPC取消关注: followerId={}, followeeId={}", request.getFollowerId(), request.getFolloweeId());

            boolean success = followService.unfollowUser(
                    request.getFollowerId(), 
                    request.getFolloweeId(),
                    request.getCancelReason(),
                    request.getOperatorId()
            );
            
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("UNFOLLOW_FAILED", "取消关注失败");
            }
        } catch (Exception e) {
            log.error("取消关注失败", e);
            return Result.error("UNFOLLOW_ERROR", "取消关注失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> checkFollowStatus(Long followerId, Long followeeId) {
        try {
            boolean isFollowing = followService.checkFollowStatus(followerId, followeeId);
            return Result.success(isFollowing);
        } catch (Exception e) {
            log.error("检查关注状态失败", e);
            return Result.error("CHECK_FOLLOW_ERROR", "检查关注状态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<FollowResponse> getFollowRelation(Long followerId, Long followeeId) {
        try {
            Follow follow = followService.getFollowRelation(followerId, followeeId);
            if (follow == null) {
                return Result.error("FOLLOW_NOT_FOUND", "关注关系不存在");
            }
            
            FollowResponse response = convertToResponse(follow);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取关注关系失败", e);
            return Result.error("GET_FOLLOW_ERROR", "获取关注关系失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<FollowResponse>> queryFollows(FollowQueryRequest request) {
        try {
            log.info("RPC分页查询关注记录: pageNum={}, pageSize={}", request.getPageNum(), request.getPageSize());

            IPage<Follow> followPage = followService.queryFollows(
                    request.getPageNum(),
                    request.getPageSize(),
                    request.getFollowerId(),
                    request.getFolloweeId(),
                    request.getFollowerNickname(),
                    request.getFolloweeNickname(),
                    request.getStatus(),
                    request.getQueryType(),
                    request.getOrderBy(),
                    request.getOrderDirection()
            );

            // 转换分页响应
            PageResponse<FollowResponse> pageResponse = buildPageResult(followPage);
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("分页查询关注记录失败", e);
            return Result.error("FOLLOW_QUERY_ERROR", "查询关注记录失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<FollowResponse>> getFollowing(Long followerId, Integer pageNum, Integer pageSize) {
        try {
            IPage<Follow> followPage = followService.getFollowing(followerId, pageNum, pageSize);
            return Result.success(buildPageResult(followPage));
        } catch (Exception e) {
            log.error("获取关注列表失败", e);
            return Result.error("GET_FOLLOWING_ERROR", "获取关注列表失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<FollowResponse>> getFollowers(Long followeeId, Integer pageNum, Integer pageSize) {
        try {
            IPage<Follow> followPage = followService.getFollowers(followeeId, pageNum, pageSize);
            return Result.success(buildPageResult(followPage));
        } catch (Exception e) {
            log.error("获取粉丝列表失败", e);
            return Result.error("GET_FOLLOWERS_ERROR", "获取粉丝列表失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Long> getFollowingCount(Long followerId) {
        try {
            Long count = followService.getFollowingCount(followerId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取关注数量失败", e);
            return Result.error("GET_FOLLOWING_COUNT_ERROR", "获取关注数量失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Long> getFollowersCount(Long followeeId) {
        try {
            Long count = followService.getFollowersCount(followeeId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取粉丝数量失败", e);
            return Result.error("GET_FOLLOWERS_COUNT_ERROR", "获取粉丝数量失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getFollowStatistics(Long userId) {
        try {
            Map<String, Object> statistics = followService.getFollowStatistics(userId);
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取关注统计失败", e);
            return Result.error("GET_STATISTICS_ERROR", "获取统计信息失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<Long, Boolean>> batchCheckFollowStatus(Long followerId, List<Long> followeeIds) {
        try {
            Map<Long, Boolean> statusMap = followService.batchCheckFollowStatus(followerId, followeeIds);
            return Result.success(statusMap);
        } catch (Exception e) {
            log.error("批量检查关注状态失败", e);
            return Result.error("BATCH_CHECK_ERROR", "批量检查关注状态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<FollowResponse>> getMutualFollows(Long userId, Integer pageNum, Integer pageSize) {
        try {
            IPage<Follow> followPage = followService.getMutualFollows(userId, pageNum, pageSize);
            return Result.success(buildPageResult(followPage));
        } catch (Exception e) {
            log.error("获取互关好友失败", e);
            return Result.error("GET_MUTUAL_ERROR", "获取互关好友失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> cleanCancelledFollows(Integer days) {
        try {
            log.info("RPC清理已取消的关注记录: days={}", days);
            int cleanedCount = followService.cleanCancelledFollows(days);
            return Result.success(cleanedCount);
        } catch (Exception e) {
            log.error("清理已取消关注记录失败", e);
            return Result.error("CLEAN_FOLLOWS_ERROR", "清理记录失败: " + e.getMessage());
        }
    }

    // =================== 私有辅助方法 ===================

    /**
     * 将FollowCreateRequest转换为Follow实体
     */
    private Follow convertCreateRequestToEntity(FollowCreateRequest request) {
        Follow follow = new Follow();
        BeanUtils.copyProperties(request, follow);
        return follow;
    }

    /**
     * 将Follow实体转换为FollowResponse
     */
    private FollowResponse convertToResponse(Follow follow) {
        FollowResponse response = new FollowResponse();
        BeanUtils.copyProperties(follow, response);
        return response;
    }

    /**
     * 构建分页结果
     */
    private PageResponse<FollowResponse> buildPageResult(IPage<Follow> followPage) {
        PageResponse<FollowResponse> pageResponse = new PageResponse<>();
        List<FollowResponse> responseList = followPage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        pageResponse.setDatas(responseList);
        pageResponse.setTotal(followPage.getTotal());
        pageResponse.setCurrentPage((int) followPage.getCurrent());
        pageResponse.setPageSize((int) followPage.getSize());
        pageResponse.setTotalPage((int) followPage.getPages());

        return pageResponse;
    }
}