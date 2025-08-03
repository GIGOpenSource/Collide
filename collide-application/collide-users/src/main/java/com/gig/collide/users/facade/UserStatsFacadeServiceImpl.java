package com.gig.collide.users.facade;

import com.gig.collide.api.user.UserStatsFacadeService;
import com.gig.collide.api.user.request.stats.UserStatsCreateRequest;
import com.gig.collide.api.user.request.stats.UserStatsQueryRequest;
import com.gig.collide.api.user.request.stats.UserStatsUpdateRequest;
import com.gig.collide.api.user.response.stats.UserStatsResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.users.domain.entity.UserStats;
import com.gig.collide.users.domain.service.UserStatsService;
import com.gig.collide.users.infrastructure.cache.UserCacheConstant;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.apache.dubbo.config.annotation.DubboService;
import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheUpdate;
import com.alicp.jetcache.anno.CacheType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户统计门面服务实现 - 对应 t_user_stats 表
 * Dubbo独立微服务提供者 - 负责用户统计数据管理
 * 
 * @author GIG Team
 * @version 2.0.0 (Dubbo微服务版 - 6表架构)
 */
@Slf4j
@DubboService(version = "1.0.0", interfaceClass = UserStatsFacadeService.class)
@RequiredArgsConstructor
public class UserStatsFacadeServiceImpl implements UserStatsFacadeService {

    private final UserStatsService userStatsService;

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_STATS_LIST_CACHE)
    public Result<UserStatsResponse> createStats(UserStatsCreateRequest request) {
        try {
            log.info("创建用户统计数据请求: userId={}", request.getUserId());
            
            UserStats userStats = new UserStats();
            BeanUtils.copyProperties(request, userStats);
            
            UserStats savedStats = userStatsService.createStats(userStats);
            UserStatsResponse response = convertToResponse(savedStats);
            
            log.info("用户统计数据创建成功: userId={}", savedStats.getUserId());
            return Result.success(response);
        } catch (Exception e) {
            log.error("创建用户统计数据失败", e);
            return Result.error("STATS_CREATE_ERROR", "创建用户统计数据失败: " + e.getMessage());
        }
    }

    @Override
    @CacheUpdate(name = UserCacheConstant.USER_STATS_DETAIL_CACHE,
                 key = UserCacheConstant.USER_STATS_DETAIL_KEY,
                 value = "#result.data")
    @CacheInvalidate(name = UserCacheConstant.USER_STATS_LIST_CACHE)
    public Result<UserStatsResponse> updateStats(UserStatsUpdateRequest request) {
        try {
            log.info("更新用户统计数据请求: userId={}", request.getUserId());
            
            UserStats userStats = userStatsService.getStatsByUserId(request.getUserId());
            if (userStats == null) {
                return Result.error("STATS_NOT_FOUND", "用户统计数据不存在");
            }
            
            BeanUtils.copyProperties(request, userStats);
            UserStats updatedStats = userStatsService.updateStats(userStats);
            UserStatsResponse response = convertToResponse(updatedStats);
            
            log.info("用户统计数据更新成功: userId={}", updatedStats.getUserId());
            return Result.success(response);
        } catch (Exception e) {
            log.error("更新用户统计数据失败", e);
            return Result.error("STATS_UPDATE_ERROR", "更新用户统计数据失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = UserCacheConstant.USER_STATS_DETAIL_CACHE,
            key = UserCacheConstant.USER_STATS_DETAIL_KEY,
            expire = UserCacheConstant.USER_STATS_DETAIL_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<UserStatsResponse> getStatsByUserId(Long userId) {
        try {
            log.debug("获取用户统计数据: userId={}", userId);
            
            UserStats stats = userStatsService.getStatsByUserId(userId);
            if (stats == null) {
                return Result.error("STATS_NOT_FOUND", "用户统计数据不存在");
            }
            
            UserStatsResponse response = convertToResponse(stats);
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询用户统计数据失败", e);
            return Result.error("STATS_NOT_FOUND","查询用户统计数据失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<UserStatsResponse>> batchGetStats(List<Long> userIds) {
        try {
            List<UserStats> statsList = userStatsService.batchGetStats(userIds);
            List<UserStatsResponse> responses = statsList.stream()
                    .map(this::convertToResponse)
                    .toList();
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("批量查询用户统计数据失败", e);
            return Result.error("BATCH_GET_STATS_ERROR", "批量查询用户统计数据失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_STATS_DETAIL_CACHE)
    public Result<Void> incrementFollowerCount(Long userId, Integer increment) {
        try {
            boolean success = userStatsService.incrementFollowerCount(userId, increment);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("FOLLOWER_COUNT_UPDATE_ERROR", "更新粉丝数失败");
            }
        } catch (Exception e) {
            log.error("更新粉丝数失败", e);
            return Result.error("FOLLOWER_COUNT_UPDATE_ERROR", "更新粉丝数失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_STATS_DETAIL_CACHE)
    public Result<Void> incrementFollowingCount(Long userId, Integer increment) {
        try {
            boolean success = userStatsService.incrementFollowingCount(userId, increment);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("FOLLOWING_COUNT_UPDATE_ERROR", "更新关注数失败");
            }
        } catch (Exception e) {
            log.error("更新关注数失败", e);
            return Result.error("FOLLOWING_COUNT_UPDATE_ERROR", "更新关注数失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_STATS_DETAIL_CACHE)
    public Result<Void> incrementContentCount(Long userId, Integer increment) {
        try {
            boolean success = userStatsService.incrementContentCount(userId, increment);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("CONTENT_COUNT_UPDATE_ERROR", "更新内容数失败");
            }
        } catch (Exception e) {
            log.error("更新内容数失败", e);
            return Result.error("CONTENT_COUNT_UPDATE_ERROR", "更新内容数失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_STATS_DETAIL_CACHE)
    public Result<Void> incrementLikeCount(Long userId, Integer increment) {
        try {
            boolean success = userStatsService.incrementLikeCount(userId, increment);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("LIKE_COUNT_UPDATE_ERROR", "更新点赞数失败");
            }
        } catch (Exception e) {
            log.error("更新点赞数失败", e);
            return Result.error("LIKE_COUNT_UPDATE_ERROR", "更新点赞数失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_STATS_DETAIL_CACHE)
    public Result<Void> incrementLoginCount(Long userId, Integer increment) {
        try {
            // 根据 increment 参数决定调用次数
            boolean success = true;
            if (increment != null && increment > 0) {
                for (int i = 0; i < increment; i++) {
                    success = success && userStatsService.incrementLoginCount(userId);
                }
            } else if (increment != null && increment == 1) {
                success = userStatsService.incrementLoginCount(userId);
            }
            
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("LOGIN_COUNT_UPDATE_ERROR", "更新登录次数失败");
            }
        } catch (Exception e) {
            log.error("更新登录次数失败", e);
            return Result.error("LOGIN_COUNT_UPDATE_ERROR", "更新登录次数失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_STATS_DETAIL_CACHE)
    public Result<Void> batchUpdateStats(Long userId, Integer followerIncrement, Integer followingIncrement, Integer contentIncrement, Integer likeIncrement) {
        try {
            if (followerIncrement != null && followerIncrement != 0) {
                userStatsService.incrementFollowerCount(userId, followerIncrement);
            }
            if (followingIncrement != null && followingIncrement != 0) {
                userStatsService.incrementFollowingCount(userId, followingIncrement);
            }
            if (contentIncrement != null && contentIncrement != 0) {
                userStatsService.incrementContentCount(userId, contentIncrement);
            }
            if (likeIncrement != null && likeIncrement != 0) {
                userStatsService.incrementLikeCount(userId, likeIncrement);
            }
            return Result.success(null);
        } catch (Exception e) {
            log.error("批量更新统计数据失败", e);
            return Result.error("BATCH_UPDATE_STATS_ERROR", "批量更新统计数据失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_STATS_DETAIL_CACHE)
    public Result<Void> resetStats(Long userId) {
        try {
            boolean success = userStatsService.resetUserStats(userId);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("RESET_STATS_ERROR", "重置用户统计数据失败");
            }
        } catch (Exception e) {
            log.error("重置用户统计数据失败", e);
            return Result.error("RESET_STATS_ERROR", "重置用户统计数据失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = UserCacheConstant.USER_STATS_LIST_CACHE,
            key = UserCacheConstant.USER_STATS_LIST_KEY,
            expire = UserCacheConstant.USER_STATS_LIST_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<PageResponse<UserStatsResponse>> queryStats(UserStatsQueryRequest request) {
        try {
            log.debug("分页查询用户统计数据: currentPage={}, pageSize={}", request.getCurrentPage(), request.getPageSize());
            
            PageResponse<UserStats> pageResult = userStatsService.queryStats(request);
            
            List<UserStatsResponse> responses = pageResult.getDatas().stream()
                    .map(this::convertToResponse)
                    .toList();
            
            PageResponse<UserStatsResponse> result = new PageResponse<>();
            result.setDatas(responses);
            result.setCurrentPage(pageResult.getCurrentPage());
            result.setPageSize(pageResult.getPageSize());
            result.setTotal(pageResult.getTotal());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询用户统计数据列表失败", e);
            return Result.error("STATS_LIST_QUERY_ERROR", "查询用户统计数据列表失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = UserCacheConstant.USER_STATS_RANKING_CACHE,
            key = "follower_ranking",
            expire = UserCacheConstant.USER_STATS_RANKING_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<List<UserStatsResponse>> getFollowerRanking(Integer limit) {
        try {
            List<UserStats> statsList = userStatsService.getTopFollowerUsers(limit);
            List<UserStatsResponse> responses = statsList.stream()
                    .map(this::convertToResponse)
                    .toList();
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("查询粉丝数排行榜失败", e);
            return Result.error("GET_FOLLOWER_RANKING_ERROR", "查询粉丝数排行榜失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = UserCacheConstant.USER_STATS_RANKING_CACHE,
            key = "content_ranking",
            expire = UserCacheConstant.USER_STATS_RANKING_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<List<UserStatsResponse>> getContentRanking(Integer limit) {
        try {
            List<UserStats> statsList = userStatsService.getTopContentUsers(limit);
            List<UserStatsResponse> responses = statsList.stream()
                    .map(this::convertToResponse)
                    .toList();
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("查询内容数排行榜失败", e);
            return Result.error("GET_CONTENT_RANKING_ERROR", "查询内容数排行榜失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = UserCacheConstant.USER_STATS_RANKING_CACHE,
            key = "like_ranking",
            expire = UserCacheConstant.USER_STATS_RANKING_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<List<UserStatsResponse>> getLikeRanking(Integer limit) {
        try {
            List<UserStats> statsList = userStatsService.getMostActiveUsers(limit);
            List<UserStatsResponse> responses = statsList.stream()
                    .map(this::convertToResponse)
                    .toList();
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("查询点赞数排行榜失败", e);
            return Result.error("GET_LIKE_RANKING_ERROR", "查询点赞数排行榜失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = UserCacheConstant.USER_STATS_RANKING_CACHE,
            key = "login_ranking",
            expire = UserCacheConstant.USER_STATS_RANKING_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<List<UserStatsResponse>> getLoginRanking(Integer limit) {
        try {
            List<UserStats> statsList = userStatsService.getMostActiveUsers(limit);
            List<UserStatsResponse> responses = statsList.stream()
                    .map(this::convertToResponse)
                    .toList();
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("查询登录数排行榜失败", e);
            return Result.error("GET_LOGIN_RANKING_ERROR", "查询登录数排行榜失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_STATS_DETAIL_CACHE)
    @CacheInvalidate(name = UserCacheConstant.USER_STATS_LIST_CACHE)
    public Result<Void> deleteStats(Long userId) {
        try {
            log.info("删除用户统计数据请求: userId={}", userId);
            
            boolean success = userStatsService.deleteStats(userId);
            if (success) {
                log.info("用户统计数据删除成功: userId={}", userId);
                return Result.success(null);
            } else {
                return Result.error("STATS_DELETE_ERROR", "删除用户统计数据失败");
            }
        } catch (Exception e) {
            log.error("删除用户统计数据失败", e);
            return Result.error("STATS_DELETE_ERROR", "删除用户统计数据失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> checkStatsExists(Long userId) {
        try {
            UserStats stats = userStatsService.getStatsByUserId(userId);
            return Result.success(stats != null);
        } catch (Exception e) {
            log.error("检查用户统计数据是否存在失败", e);
            return Result.error("CHECK_STATS_EXISTS_ERROR", "检查统计数据存在性失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = UserCacheConstant.USER_STATS_RANKING_CACHE,
            key = "platform_stats",
            expire = UserCacheConstant.USER_STATS_RANKING_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<Map<String, Object>> getPlatformStats() {
        try {
            Map<String, Object> platformStats = new HashMap<>();
            
            // 获取平台基础统计数据 (简化实现)
            List<UserStats> topFollowers = userStatsService.getTopFollowerUsers(1);
            List<UserStats> topContent = userStatsService.getTopContentUsers(1);
            List<UserStats> mostActive = userStatsService.getMostActiveUsers(1);
            
            platformStats.put("topFollower", topFollowers.isEmpty() ? 0 : topFollowers.get(0).getFollowerCount());
            platformStats.put("topContent", topContent.isEmpty() ? 0 : topContent.get(0).getContentCount());
            platformStats.put("mostActiveLikes", mostActive.isEmpty() ? 0 : mostActive.get(0).getLikeCount());
            platformStats.put("timestamp", System.currentTimeMillis());
            
            return Result.success(platformStats);
        } catch (Exception e) {
            log.error("获取平台统计数据失败", e);
            return Result.error("GET_PLATFORM_STATS_ERROR", "获取平台统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 转换为用户统计响应对象
     */
    private UserStatsResponse convertToResponse(UserStats stats) {
        UserStatsResponse response = new UserStatsResponse();
        BeanUtils.copyProperties(stats, response);
        return response;
    }
}