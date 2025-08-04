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
import com.gig.collide.api.user.UserFacadeService;
import com.gig.collide.api.user.response.UserResponse;
import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
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
    
    @DubboReference(version = "1.0.0", timeout = 10000, check = false)
    private UserFacadeService userFacadeService;

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

            // 验证关注者是否存在
            Result<UserResponse> followerResult = userFacadeService.getUserById(request.getFollowerId());
            if (followerResult == null || !followerResult.getSuccess()) {
                log.warn("关注者用户不存在，无法创建关注关系: followerId={}", request.getFollowerId());
                return Result.error("FOLLOWER_NOT_FOUND", "关注者用户不存在");
            }

            // 验证被关注者是否存在
            Result<UserResponse> followeeResult = userFacadeService.getUserById(request.getFolloweeId());
            if (followeeResult == null || !followeeResult.getSuccess()) {
                log.warn("被关注者用户不存在，无法创建关注关系: followeeId={}", request.getFolloweeId());
                return Result.error("FOLLOWEE_NOT_FOUND", "被关注者用户不存在");
            }

            // 转换请求对象为实体
            Follow follow = convertCreateRequestToEntity(request);
            
            // 调用业务逻辑
            Follow savedFollow = followService.followUser(follow);
            
            // 转换响应对象
            FollowResponse response = convertToResponse(savedFollow);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("关注用户成功: ID={}, 关注者={}({}), 被关注者={}({}), 耗时={}ms", 
                    savedFollow.getId(), 
                    request.getFollowerId(), followerResult.getData().getNickname(),
                    request.getFolloweeId(), followeeResult.getData().getNickname(),
                    duration);
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

            // 验证关注者是否存在
            Result<UserResponse> followerResult = userFacadeService.getUserById(request.getFollowerId());
            if (followerResult == null || !followerResult.getSuccess()) {
                log.warn("关注者用户不存在，无法取消关注: followerId={}", request.getFollowerId());
                return Result.error("FOLLOWER_NOT_FOUND", "关注者用户不存在");
            }

            // 验证被关注者是否存在
            Result<UserResponse> followeeResult = userFacadeService.getUserById(request.getFolloweeId());
            if (followeeResult == null || !followeeResult.getSuccess()) {
                log.warn("被关注者用户不存在，无法取消关注: followeeId={}", request.getFolloweeId());
                return Result.error("FOLLOWEE_NOT_FOUND", "被关注者用户不存在");
            }

            boolean success = followService.unfollowUser(
                    request.getFollowerId(), 
                    request.getFolloweeId(),
                    request.getCancelReason(),
                    request.getOperatorId()
            );
            
            long duration = System.currentTimeMillis() - startTime;
            if (success) {
                log.info("取消关注成功: 关注者={}({}), 被关注者={}({}), 耗时={}ms", 
                        request.getFollowerId(), followerResult.getData().getNickname(),
                        request.getFolloweeId(), followeeResult.getData().getNickname(),
                        duration);
                return Result.success(null);
            } else {
                log.warn("取消关注失败: 关注者={}({}), 被关注者={}({})", 
                        request.getFollowerId(), followerResult.getData().getNickname(),
                        request.getFolloweeId(), followeeResult.getData().getNickname());
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
            
            // 验证关注者是否存在
            Result<UserResponse> followerResult = userFacadeService.getUserById(followerId);
            if (followerResult == null || !followerResult.getSuccess()) {
                log.warn("关注者用户不存在，无法检查关注状态: followerId={}", followerId);
                return Result.error("FOLLOWER_NOT_FOUND", "关注者用户不存在");
            }

            // 验证被关注者是否存在
            Result<UserResponse> followeeResult = userFacadeService.getUserById(followeeId);
            if (followeeResult == null || !followeeResult.getSuccess()) {
                log.warn("被关注者用户不存在，无法检查关注状态: followeeId={}", followeeId);
                return Result.error("FOLLOWEE_NOT_FOUND", "被关注者用户不存在");
            }
            
            boolean isFollowing = followService.checkFollowStatus(followerId, followeeId);
            
            log.debug("关注状态检查完成: 关注者={}({}), 被关注者={}({}), 结果={}", 
                    followerId, followerResult.getData().getNickname(),
                    followeeId, followeeResult.getData().getNickname(),
                    isFollowing);
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
    public Result<PageResponse<FollowResponse>> getFollowing(Long followerId, Integer currentPage, Integer pageSize) {
        try {
            log.debug("获取关注列表: followerId={}, currentPage={}, pageSize={}", followerId, currentPage, pageSize);
            long startTime = System.currentTimeMillis();
            
            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(followerId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法获取关注列表: followerId={}", followerId);
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }
            
            IPage<Follow> followPage = followService.getFollowing(followerId, currentPage, pageSize);
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("关注列表查询成功: 用户={}({}), 总数={}, 耗时={}ms", 
                    followerId, userResult.getData().getNickname(), followPage.getTotal(), duration);
            return Result.success(buildPageResult(followPage));
        } catch (Exception e) {
            log.error("获取关注列表失败: followerId={}", followerId, e);
            return Result.error("GET_FOLLOWING_ERROR", "获取关注列表失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = FollowCacheConstant.FOLLOWERS_LIST_CACHE, key = FollowCacheConstant.FOLLOWERS_LIST_KEY,
            expire = FollowCacheConstant.FOLLOWERS_LIST_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<PageResponse<FollowResponse>> getFollowers(Long followeeId, Integer currentPage, Integer pageSize) {
        try {
            log.debug("获取粉丝列表: followeeId={}, currentPage={}, pageSize={}", followeeId, currentPage, pageSize);
            long startTime = System.currentTimeMillis();
            
            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(followeeId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法获取粉丝列表: followeeId={}", followeeId);
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }
            
            IPage<Follow> followPage = followService.getFollowers(followeeId, currentPage, pageSize);
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("粉丝列表查询成功: 用户={}({}), 总数={}, 耗时={}ms", 
                    followeeId, userResult.getData().getNickname(), followPage.getTotal(), duration);
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
            
            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(followerId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法获取关注数量: followerId={}", followerId);
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }
            
            Long count = followService.getFollowingCount(followerId);
            
            log.debug("关注数量查询成功: 用户={}({}), count={}", 
                    followerId, userResult.getData().getNickname(), count);
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
            
            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(followeeId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法获取粉丝数量: followeeId={}", followeeId);
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }
            
            Long count = followService.getFollowersCount(followeeId);
            
            log.debug("粉丝数量查询成功: 用户={}({}), count={}", 
                    followeeId, userResult.getData().getNickname(), count);
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
            
            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法获取关注统计: userId={}", userId);
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }
            
            Map<String, Object> statistics = followService.getFollowStatistics(userId);
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("关注统计查询成功: 用户={}({}), 耗时={}ms", 
                    userId, userResult.getData().getNickname(), duration);
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
            
            // 验证关注者是否存在
            Result<UserResponse> followerResult = userFacadeService.getUserById(followerId);
            if (followerResult == null || !followerResult.getSuccess()) {
                log.warn("关注者用户不存在，无法批量检查关注状态: followerId={}", followerId);
                return Result.error("FOLLOWER_NOT_FOUND", "关注者用户不存在");
            }
            
            Map<Long, Boolean> statusMap = followService.batchCheckFollowStatus(followerId, followeeIds);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("批量关注状态检查成功: 关注者={}({}), 检查数量={}, 耗时={}ms", 
                    followerId, followerResult.getData().getNickname(),
                    followeeIds != null ? followeeIds.size() : 0, duration);
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
    public Result<PageResponse<FollowResponse>> getMutualFollows(Long userId, Integer currentPage, Integer pageSize) {
        try {
            log.debug("获取互关好友: userId={}, currentPage={}, pageSize={}", userId, currentPage, pageSize);
            long startTime = System.currentTimeMillis();
            
            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法获取互关好友: userId={}", userId);
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }
            
            IPage<Follow> followPage = followService.getMutualFollows(userId, currentPage, pageSize);
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("互关好友查询成功: 用户={}({}), 总数={}, 耗时={}ms", 
                    userId, userResult.getData().getNickname(), followPage.getTotal(), duration);
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
    
    @Override
    @Cached(name = FollowCacheConstant.FOLLOWEES_LIST_CACHE, key = FollowCacheConstant.FOLLOWEES_LIST_KEY,
            expire = FollowCacheConstant.FOLLOWEES_LIST_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<PageResponse<FollowResponse>> searchByNickname(Long followerId, Long followeeId, String nicknameKeyword,
                                                               Integer currentPage, Integer pageSize) {
        try {
            log.info("RPC根据昵称搜索关注关系: followerId={}, followeeId={}, keyword={}, currentPage={}, pageSize={}", 
                    followerId, followeeId, nicknameKeyword, currentPage, pageSize);
            long startTime = System.currentTimeMillis();

            if (nicknameKeyword == null || nicknameKeyword.trim().isEmpty()) {
                log.warn("搜索关键词不能为空");
                return Result.error("NICKNAME_KEYWORD_EMPTY", "搜索关键词不能为空");
            }

            IPage<Follow> followPage = followService.searchByNickname(followerId, followeeId, nicknameKeyword, 
                    currentPage, pageSize);

            PageResponse<FollowResponse> pageResponse = buildPageResult(followPage);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("昵称搜索成功: 关键词={}, 总数={}, 耗时={}ms", 
                    nicknameKeyword, pageResponse.getTotal(), duration);
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("根据昵称搜索关注关系失败: keyword={}", nicknameKeyword, e);
            return Result.error("SEARCH_NICKNAME_ERROR", "昵称搜索失败: " + e.getMessage());
        }
    }
    
    @Override
    @CacheInvalidate(name = FollowCacheConstant.FOLLOW_RELATION_CACHE)
    @CacheInvalidate(name = FollowCacheConstant.FOLLOWERS_LIST_CACHE)
    @CacheInvalidate(name = FollowCacheConstant.FOLLOWEES_LIST_CACHE)
    @CacheInvalidate(name = FollowCacheConstant.FOLLOW_STATISTICS_CACHE)
    @CacheInvalidate(name = FollowCacheConstant.FOLLOW_STATUS_CACHE)
    public Result<Integer> updateUserInfo(Long userId, String nickname, String avatar) {
        try {
            log.info("RPC更新用户冗余信息: userId={}, nickname={}", userId, nickname);
            long startTime = System.currentTimeMillis();

            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法更新冗余信息: userId={}", userId);
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }

            int updateCount = followService.updateUserInfo(userId, nickname, avatar);

            long duration = System.currentTimeMillis() - startTime;
            log.info("用户冗余信息更新完成: 用户={}({}), 更新数量={}, 耗时={}ms",
                    userId, userResult.getData().getNickname(), updateCount, duration);

            return Result.success(updateCount);
        } catch (Exception e) {
            log.error("更新用户冗余信息失败: userId={}, nickname={}", userId, nickname, e);
            return Result.error("UPDATE_USER_INFO_ERROR", "更新用户信息失败: " + e.getMessage());
        }
    }
    
    @Override
    @Cached(name = FollowCacheConstant.FOLLOW_RELATION_CACHE, key = FollowCacheConstant.FOLLOW_RELATION_KEY,
            expire = FollowCacheConstant.FOLLOW_RELATION_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<List<FollowResponse>> getRelationChain(Long userIdA, Long userIdB) {
        try {
            log.debug("查询用户间关注关系链: userIdA={}, userIdB={}", userIdA, userIdB);
            long startTime = System.currentTimeMillis();

            // 验证用户是否存在
            Result<UserResponse> userAResult = userFacadeService.getUserById(userIdA);
            if (userAResult == null || !userAResult.getSuccess()) {
                log.warn("用户A不存在: userIdA={}", userIdA);
                return Result.error("USER_A_NOT_FOUND", "用户A不存在");
            }

            Result<UserResponse> userBResult = userFacadeService.getUserById(userIdB);
            if (userBResult == null || !userBResult.getSuccess()) {
                log.warn("用户B不存在: userIdB={}", userIdB);
                return Result.error("USER_B_NOT_FOUND", "用户B不存在");
            }

            List<Follow> relationChain = followService.getRelationChain(userIdA, userIdB);
            List<FollowResponse> responseList = relationChain.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            long duration = System.currentTimeMillis() - startTime;
            log.debug("关系链查询成功: 用户A={}({}), 用户B={}({}), 关系数量={}, 耗时={}ms",
                    userIdA, userAResult.getData().getNickname(),
                    userIdB, userBResult.getData().getNickname(),
                    responseList.size(), duration);

            return Result.success(responseList);
        } catch (Exception e) {
            log.error("查询用户间关注关系链失败: userIdA={}, userIdB={}", userIdA, userIdB, e);
            return Result.error("GET_RELATION_CHAIN_ERROR", "查询关系链失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<String> validateFollowRequest(FollowCreateRequest request) {
        try {
            log.debug("验证关注请求: followerId={}, followeeId={}", 
                    request != null ? request.getFollowerId() : null,
                    request != null ? request.getFolloweeId() : null);
            
            if (request == null) {
                return Result.error("REQUEST_NULL", "请求对象不能为空");
            }

            // 转换为实体对象进行验证
            Follow follow = convertCreateRequestToEntity(request);
            String validationResult = followService.validateFollowRequest(follow);

            if (validationResult != null) {
                log.warn("关注请求验证失败: followerId={}, followeeId={}, 错误={}",
                        request.getFollowerId(), request.getFolloweeId(), validationResult);
                return Result.error("VALIDATION_FAILED", validationResult);
            }

            log.debug("关注请求验证通过: followerId={}, followeeId={}", 
                    request.getFollowerId(), request.getFolloweeId());
            return Result.success("验证通过");
        } catch (Exception e) {
            log.error("验证关注请求失败: request={}", request, e);
            return Result.error("VALIDATION_ERROR", "验证请求失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<String> checkCanFollow(Long followerId, Long followeeId) {
        try {
            log.debug("检查是否可以关注: followerId={}, followeeId={}", followerId, followeeId);

            // 验证关注者是否存在
            Result<UserResponse> followerResult = userFacadeService.getUserById(followerId);
            if (followerResult == null || !followerResult.getSuccess()) {
                log.warn("关注者用户不存在: followerId={}", followerId);
                return Result.error("FOLLOWER_NOT_FOUND", "关注者用户不存在");
            }

            // 验证被关注者是否存在
            Result<UserResponse> followeeResult = userFacadeService.getUserById(followeeId);
            if (followeeResult == null || !followeeResult.getSuccess()) {
                log.warn("被关注者用户不存在: followeeId={}", followeeId);
                return Result.error("FOLLOWEE_NOT_FOUND", "被关注者用户不存在");
            }

            String checkResult = followService.checkCanFollow(followerId, followeeId);

            if (checkResult != null) {
                log.warn("不能关注: followerId={}, followeeId={}, 原因={}",
                        followerId, followeeId, checkResult);
                return Result.error("CANNOT_FOLLOW", checkResult);
            }

            log.debug("可以关注: 关注者={}({}), 被关注者={}({})",
                    followerId, followerResult.getData().getNickname(),
                    followeeId, followeeResult.getData().getNickname());
            return Result.success("可以关注");
        } catch (Exception e) {
            log.error("检查关注权限失败: followerId={}, followeeId={}", followerId, followeeId, e);
            return Result.error("CHECK_PERMISSION_ERROR", "检查权限失败: " + e.getMessage());
        }
    }
    
    @Override
    @Cached(name = FollowCacheConstant.FOLLOW_STATUS_CACHE, key = FollowCacheConstant.USER_FOLLOW_STATUS_KEY,
            expire = FollowCacheConstant.FOLLOW_STATUS_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<Boolean> existsFollowRelation(Long followerId, Long followeeId) {
        try {
            log.debug("检查关注关系是否存在: followerId={}, followeeId={}", followerId, followeeId);

            // 验证用户是否存在
            Result<UserResponse> followerResult = userFacadeService.getUserById(followerId);
            if (followerResult == null || !followerResult.getSuccess()) {
                log.warn("关注者用户不存在: followerId={}", followerId);
                return Result.error("FOLLOWER_NOT_FOUND", "关注者用户不存在");
            }

            Result<UserResponse> followeeResult = userFacadeService.getUserById(followeeId);
            if (followeeResult == null || !followeeResult.getSuccess()) {
                log.warn("被关注者用户不存在: followeeId={}", followeeId);
                return Result.error("FOLLOWEE_NOT_FOUND", "被关注者用户不存在");
            }

            boolean exists = followService.existsFollowRelation(followerId, followeeId);

            log.debug("关注关系检查完成: 关注者={}({}), 被关注者={}({}), 存在={}",
                    followerId, followerResult.getData().getNickname(),
                    followeeId, followeeResult.getData().getNickname(),
                    exists);

            return Result.success(exists);
        } catch (Exception e) {
            log.error("检查关注关系是否存在失败: followerId={}, followeeId={}", followerId, followeeId, e);
            return Result.error("CHECK_RELATION_ERROR", "检查关系失败: " + e.getMessage());
        }
    }
    
    @Override
    @CacheInvalidate(name = FollowCacheConstant.FOLLOW_RELATION_CACHE)
    @CacheInvalidate(name = FollowCacheConstant.FOLLOWERS_LIST_CACHE)
    @CacheInvalidate(name = FollowCacheConstant.FOLLOWEES_LIST_CACHE)
    @CacheInvalidate(name = FollowCacheConstant.FOLLOW_STATISTICS_CACHE)
    @CacheInvalidate(name = FollowCacheConstant.FOLLOW_STATUS_CACHE)
    @CacheInvalidate(name = FollowCacheConstant.MUTUAL_FOLLOW_CACHE)
    public Result<Boolean> reactivateFollow(Long followerId, Long followeeId) {
        try {
            log.info("RPC重新激活关注关系: followerId={}, followeeId={}", followerId, followeeId);
            long startTime = System.currentTimeMillis();

            // 验证用户是否存在
            Result<UserResponse> followerResult = userFacadeService.getUserById(followerId);
            if (followerResult == null || !followerResult.getSuccess()) {
                log.warn("关注者用户不存在: followerId={}", followerId);
                return Result.error("FOLLOWER_NOT_FOUND", "关注者用户不存在");
            }

            Result<UserResponse> followeeResult = userFacadeService.getUserById(followeeId);
            if (followeeResult == null || !followeeResult.getSuccess()) {
                log.warn("被关注者用户不存在: followeeId={}", followeeId);
                return Result.error("FOLLOWEE_NOT_FOUND", "被关注者用户不存在");
            }

            boolean success = followService.reactivateFollow(followerId, followeeId);

            long duration = System.currentTimeMillis() - startTime;
            if (success) {
                log.info("重新激活关注关系成功: 关注者={}({}), 被关注者={}({}), 耗时={}ms",
                        followerId, followerResult.getData().getNickname(),
                        followeeId, followeeResult.getData().getNickname(),
                        duration);
                return Result.success(true);
            } else {
                log.warn("重新激活关注关系失败: 关注者={}({}), 被关注者={}({})",
                        followerId, followerResult.getData().getNickname(),
                        followeeId, followeeResult.getData().getNickname());
                return Result.error("REACTIVATE_FAILED", "重新激活失败");
            }
        } catch (Exception e) {
            log.error("重新激活关注关系失败: followerId={}, followeeId={}", followerId, followeeId, e);
            return Result.error("REACTIVATE_ERROR", "重新激活失败: " + e.getMessage());
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