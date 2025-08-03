package com.gig.collide.follow.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.follow.FollowFacadeService;
import com.gig.collide.api.follow.request.*;
import com.gig.collide.api.follow.response.FollowResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.follow.domain.entity.Follow;
import com.gig.collide.follow.domain.service.FollowService;
import com.gig.collide.follow.infrastructure.cache.FollowCacheConstant;
import com.gig.collide.web.vo.Result;
import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 关注门面服务实现类 - 缓存增强版
 * 对齐goods模块设计风格，提供完整的关注服务
 * 包含缓存功能、跨模块集成、错误处理、数据转换
 * 
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-16
 */
@Slf4j
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class FollowFacadeServiceImpl implements FollowFacadeService {

    private final FollowService followService;

    @Override
    @CacheInvalidate(name = FollowCacheConstant.FOLLOW_RELATION_CACHE)
    @CacheInvalidate(name = FollowCacheConstant.FOLLOWERS_LIST_CACHE)
    @CacheInvalidate(name = FollowCacheConstant.FOLLOWEES_LIST_CACHE)
    @CacheInvalidate(name = FollowCacheConstant.FOLLOW_STATISTICS_CACHE)
    @CacheInvalidate(name = FollowCacheConstant.FOLLOW_STATUS_CACHE)
    public Result<FollowResponse> followUser(FollowCreateRequest request) {
        try {
            log.info("RPC关注用户: followerId={}, followeeId={}", request.getFollowerId(), request.getFolloweeId());
            long startTime = System.currentTimeMillis();

            // 转换请求对象为实体
            Follow follow = convertCreateRequestToEntity(request);
            
            // 调用业务逻辑
            Follow savedFollow = followService.followUser(follow);
            
            // 转换响应对象
            FollowResponse response = convertToResponse(savedFollow);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("关注用户成功: ID={}, followerId={}, followeeId={}, 耗时={}ms", 
                    savedFollow.getId(), request.getFollowerId(), request.getFolloweeId(), duration);
            return Result.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("关注参数验证失败: followerId={}, followeeId={}, 错误={}", 
                    request.getFollowerId(), request.getFolloweeId(), e.getMessage());
            return Result.error("FOLLOW_PARAM_ERROR", e.getMessage());
        } catch (IllegalStateException e) {
            log.warn("关注状态检查失败: followerId={}, followeeId={}, 错误={}", 
                    request.getFollowerId(), request.getFolloweeId(), e.getMessage());
            return Result.error("FOLLOW_STATE_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("关注用户失败: followerId={}, followeeId={}", request.getFollowerId(), request.getFolloweeId(), e);
            return Result.error("FOLLOW_CREATE_ERROR", "关注用户失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = FollowCacheConstant.FOLLOW_RELATION_CACHE)
    @CacheInvalidate(name = FollowCacheConstant.FOLLOWERS_LIST_CACHE)
    @CacheInvalidate(name = FollowCacheConstant.FOLLOWEES_LIST_CACHE)
    @CacheInvalidate(name = FollowCacheConstant.FOLLOW_STATISTICS_CACHE)
    @CacheInvalidate(name = FollowCacheConstant.FOLLOW_STATUS_CACHE)
    @CacheInvalidate(name = FollowCacheConstant.MUTUAL_FOLLOW_CACHE)
    public Result<Void> unfollowUser(FollowDeleteRequest request) {
        try {
            log.info("RPC取消关注: followerId={}, followeeId={}", request.getFollowerId(), request.getFolloweeId());
            long startTime = System.currentTimeMillis();

            boolean success = followService.unfollowUser(
                    request.getFollowerId(), 
                    request.getFolloweeId(),
                    request.getCancelReason(),
                    request.getOperatorId()
            );
            
            long duration = System.currentTimeMillis() - startTime;
            if (success) {
                log.info("取消关注成功: followerId={}, followeeId={}, 耗时={}ms", 
                        request.getFollowerId(), request.getFolloweeId(), duration);
                return Result.success(null);
            } else {
                log.warn("取消关注失败: followerId={}, followeeId={}", 
                        request.getFollowerId(), request.getFolloweeId());
                return Result.error("UNFOLLOW_FAILED", "取消关注失败");
            }
        } catch (Exception e) {
            log.error("取消关注失败: followerId={}, followeeId={}", request.getFollowerId(), request.getFolloweeId(), e);
            return Result.error("UNFOLLOW_ERROR", "取消关注失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = FollowCacheConstant.FOLLOW_STATUS_CACHE, key = FollowCacheConstant.USER_FOLLOW_STATUS_KEY,
            expire = FollowCacheConstant.FOLLOW_STATUS_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<Boolean> checkFollowStatus(Long followerId, Long followeeId) {
        try {
            log.debug("检查关注状态: followerId={}, followeeId={}", followerId, followeeId);
            
            boolean isFollowing = followService.checkFollowStatus(followerId, followeeId);
            
            log.debug("关注状态检查完成: followerId={}, followeeId={}, 结果={}", 
                    followerId, followeeId, isFollowing);
            return Result.success(isFollowing);
        } catch (Exception e) {
            log.error("检查关注状态失败: followerId={}, followeeId={}", followerId, followeeId, e);
            return Result.error("CHECK_FOLLOW_ERROR", "检查关注状态失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = FollowCacheConstant.FOLLOW_RELATION_CACHE, key = FollowCacheConstant.FOLLOW_RELATION_KEY,
            expire = FollowCacheConstant.FOLLOW_RELATION_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<FollowResponse> getFollowRelation(Long followerId, Long followeeId) {
        try {
            log.debug("获取关注关系: followerId={}, followeeId={}", followerId, followeeId);
            
            Follow follow = followService.getFollowRelation(followerId, followeeId);
            if (follow == null) {
                log.warn("关注关系不存在: followerId={}, followeeId={}", followerId, followeeId);
                return Result.error("FOLLOW_NOT_FOUND", "关注关系不存在");
            }
            
            FollowResponse response = convertToResponse(follow);
            log.debug("关注关系查询成功: followerId={}, followeeId={}, status={}", 
                    followerId, followeeId, follow.getStatus());
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取关注关系失败: followerId={}, followeeId={}", followerId, followeeId, e);
            return Result.error("GET_FOLLOW_ERROR", "获取关注关系失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = FollowCacheConstant.FOLLOWEES_LIST_CACHE, key = FollowCacheConstant.FOLLOWEES_LIST_KEY,
            expire = FollowCacheConstant.FOLLOWEES_LIST_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<PageResponse<FollowResponse>> queryFollows(FollowQueryRequest request) {
        try {
            log.info("RPC分页查询关注记录: pageNum={}, pageSize={}, followerId={}, followeeId={}", 
                    request.getCurrentPage(), request.getPageSize(), request.getFollowerId(), request.getFolloweeId());
            long startTime = System.currentTimeMillis();

            IPage<Follow> followPage = followService.queryFollows(
                    request.getCurrentPage(),
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
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("关注记录分页查询成功: 总数={}, 当前页={}, 耗时={}ms", 
                    pageResponse.getTotal(), pageResponse.getCurrentPage(), duration);
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("分页查询关注记录失败: 页码={}, 页大小={}", request.getCurrentPage(), request.getPageSize(), e);
            return Result.error("FOLLOW_QUERY_ERROR", "查询关注记录失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = FollowCacheConstant.FOLLOWEES_LIST_CACHE, key = FollowCacheConstant.FOLLOWEES_LIST_KEY,
            expire = FollowCacheConstant.FOLLOWEES_LIST_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<PageResponse<FollowResponse>> getFollowing(Long followerId, Integer pageNum, Integer pageSize) {
        try {
            log.debug("获取关注列表: followerId={}, pageNum={}, pageSize={}", followerId, pageNum, pageSize);
            long startTime = System.currentTimeMillis();
            
            IPage<Follow> followPage = followService.getFollowing(followerId, pageNum, pageSize);
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("关注列表查询成功: followerId={}, 总数={}, 耗时={}ms", 
                    followerId, followPage.getTotal(), duration);
            return Result.success(buildPageResult(followPage));
        } catch (Exception e) {
            log.error("获取关注列表失败: followerId={}", followerId, e);
            return Result.error("GET_FOLLOWING_ERROR", "获取关注列表失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = FollowCacheConstant.FOLLOWERS_LIST_CACHE, key = FollowCacheConstant.FOLLOWERS_LIST_KEY,
            expire = FollowCacheConstant.FOLLOWERS_LIST_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<PageResponse<FollowResponse>> getFollowers(Long followeeId, Integer pageNum, Integer pageSize) {
        try {
            log.debug("获取粉丝列表: followeeId={}, pageNum={}, pageSize={}", followeeId, pageNum, pageSize);
            long startTime = System.currentTimeMillis();
            
            IPage<Follow> followPage = followService.getFollowers(followeeId, pageNum, pageSize);
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("粉丝列表查询成功: followeeId={}, 总数={}, 耗时={}ms", 
                    followeeId, followPage.getTotal(), duration);
            return Result.success(buildPageResult(followPage));
        } catch (Exception e) {
            log.error("获取粉丝列表失败: followeeId={}", followeeId, e);
            return Result.error("GET_FOLLOWERS_ERROR", "获取粉丝列表失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = FollowCacheConstant.FOLLOW_STATISTICS_CACHE, key = FollowCacheConstant.USER_FOLLOWEE_COUNT_KEY,
            expire = FollowCacheConstant.FOLLOW_STATISTICS_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<Long> getFollowingCount(Long followerId) {
        try {
            log.debug("获取关注数量: followerId={}", followerId);
            
            Long count = followService.getFollowingCount(followerId);
            
            log.debug("关注数量查询成功: followerId={}, count={}", followerId, count);
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取关注数量失败: followerId={}", followerId, e);
            return Result.error("GET_FOLLOWING_COUNT_ERROR", "获取关注数量失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = FollowCacheConstant.FOLLOW_STATISTICS_CACHE, key = FollowCacheConstant.USER_FOLLOWER_COUNT_KEY,
            expire = FollowCacheConstant.FOLLOW_STATISTICS_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<Long> getFollowersCount(Long followeeId) {
        try {
            log.debug("获取粉丝数量: followeeId={}", followeeId);
            
            Long count = followService.getFollowersCount(followeeId);
            
            log.debug("粉丝数量查询成功: followeeId={}, count={}", followeeId, count);
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取粉丝数量失败: followeeId={}", followeeId, e);
            return Result.error("GET_FOLLOWERS_COUNT_ERROR", "获取粉丝数量失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = FollowCacheConstant.FOLLOW_STATISTICS_CACHE, key = FollowCacheConstant.USER_FOLLOW_STATS_KEY,
            expire = FollowCacheConstant.FOLLOW_STATISTICS_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<Map<String, Object>> getFollowStatistics(Long userId) {
        try {
            log.debug("获取关注统计: userId={}", userId);
            long startTime = System.currentTimeMillis();
            
            Map<String, Object> statistics = followService.getFollowStatistics(userId);
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("关注统计查询成功: userId={}, 耗时={}ms", userId, duration);
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取关注统计失败: userId={}", userId, e);
            return Result.error("GET_STATISTICS_ERROR", "获取统计信息失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = FollowCacheConstant.FOLLOW_STATUS_CACHE, key = FollowCacheConstant.BATCH_FOLLOW_STATUS_KEY,
            expire = FollowCacheConstant.FOLLOW_STATUS_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<Map<Long, Boolean>> batchCheckFollowStatus(Long followerId, List<Long> followeeIds) {
        try {
            log.info("批量检查关注状态: followerId={}, 目标数量={}", 
                    followerId, followeeIds != null ? followeeIds.size() : 0);
            long startTime = System.currentTimeMillis();
            
            Map<Long, Boolean> statusMap = followService.batchCheckFollowStatus(followerId, followeeIds);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("批量关注状态检查成功: followerId={}, 检查数量={}, 耗时={}ms", 
                    followerId, followeeIds != null ? followeeIds.size() : 0, duration);
            return Result.success(statusMap);
        } catch (Exception e) {
            log.error("批量检查关注状态失败: followerId={}, 目标数量={}", 
                    followerId, followeeIds != null ? followeeIds.size() : 0, e);
            return Result.error("BATCH_CHECK_ERROR", "批量检查关注状态失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = FollowCacheConstant.MUTUAL_FOLLOW_CACHE, key = FollowCacheConstant.MUTUAL_FOLLOW_KEY,
            expire = FollowCacheConstant.MUTUAL_FOLLOW_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<PageResponse<FollowResponse>> getMutualFollows(Long userId, Integer pageNum, Integer pageSize) {
        try {
            log.debug("获取互关好友: userId={}, pageNum={}, pageSize={}", userId, pageNum, pageSize);
            long startTime = System.currentTimeMillis();
            
            IPage<Follow> followPage = followService.getMutualFollows(userId, pageNum, pageSize);
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("互关好友查询成功: userId={}, 总数={}, 耗时={}ms", 
                    userId, followPage.getTotal(), duration);
            return Result.success(buildPageResult(followPage));
        } catch (Exception e) {
            log.error("获取互关好友失败: userId={}", userId, e);
            return Result.error("GET_MUTUAL_ERROR", "获取互关好友失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = FollowCacheConstant.FOLLOW_RELATION_CACHE)
    @CacheInvalidate(name = FollowCacheConstant.FOLLOWERS_LIST_CACHE)
    @CacheInvalidate(name = FollowCacheConstant.FOLLOWEES_LIST_CACHE)
    @CacheInvalidate(name = FollowCacheConstant.FOLLOW_STATISTICS_CACHE)
    public Result<Integer> cleanCancelledFollows(Integer days) {
        try {
            log.info("RPC清理已取消的关注记录: days={}", days);
            long startTime = System.currentTimeMillis();
            
            int cleanedCount = followService.cleanCancelledFollows(days);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("清理已取消关注记录成功: days={}, 清理数量={}, 耗时={}ms", 
                    days, cleanedCount, duration);
            return Result.success(cleanedCount);
        } catch (Exception e) {
            log.error("清理已取消关注记录失败: days={}", days, e);
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