package com.gig.collide.follow.infrastructure.mq;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.gig.collide.cache.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 关注模块缓存服务
 * @author GIG
 */
@Slf4j
@Service
@ConditionalOnClass(name = "com.alicp.jetcache.Cache")
public class FollowCacheService {

    /**
     * 关注状态缓存 - 本地+远程缓存
     */
    @CreateCache(expire = 3600, cacheType = CacheType.BOTH, localLimit = 10000)
    private Cache<String, Boolean> followStatusCache;

    /**
     * 用户统计缓存 - 远程缓存
     */
    @CreateCache(expire = 1800, cacheType = CacheType.REMOTE)
    private Cache<String, Integer> userStatisticsCache;

    /**
     * 关注列表缓存 - 远程缓存
     */
    @CreateCache(expire = 600, cacheType = CacheType.REMOTE)
    private Cache<String, Object> followListCache;

    /**
     * 缓存key前缀
     */
    private static final String FOLLOW_STATUS_PREFIX = "follow:status";
    private static final String USER_FOLLOWING_COUNT_PREFIX = "follow:following:count";
    private static final String USER_FOLLOWER_COUNT_PREFIX = "follow:follower:count";
    private static final String FOLLOW_LIST_PREFIX = "follow:list";
    private static final String FOLLOWER_LIST_PREFIX = "follower:list";

    /**
     * 获取关注状态缓存key
     */
    private String getFollowStatusKey(Long followerUserId, Long followedUserId) {
        return FOLLOW_STATUS_PREFIX + CacheConstant.CACHE_KEY_SEPARATOR + 
               followerUserId + CacheConstant.CACHE_KEY_SEPARATOR + followedUserId;
    }

    /**
     * 获取用户关注数缓存key
     */
    private String getFollowingCountKey(Long userId) {
        return USER_FOLLOWING_COUNT_PREFIX + CacheConstant.CACHE_KEY_SEPARATOR + userId;
    }

    /**
     * 获取用户粉丝数缓存key
     */
    private String getFollowerCountKey(Long userId) {
        return USER_FOLLOWER_COUNT_PREFIX + CacheConstant.CACHE_KEY_SEPARATOR + userId;
    }

    /**
     * 获取关注列表缓存key
     */
    private String getFollowListKey(Long userId, Integer page, Integer size) {
        return FOLLOW_LIST_PREFIX + CacheConstant.CACHE_KEY_SEPARATOR + 
               userId + CacheConstant.CACHE_KEY_SEPARATOR + page + CacheConstant.CACHE_KEY_SEPARATOR + size;
    }

    /**
     * 获取粉丝列表缓存key
     */
    private String getFollowerListKey(Long userId, Integer page, Integer size) {
        return FOLLOWER_LIST_PREFIX + CacheConstant.CACHE_KEY_SEPARATOR + 
               userId + CacheConstant.CACHE_KEY_SEPARATOR + page + CacheConstant.CACHE_KEY_SEPARATOR + size;
    }

    /**
     * 缓存关注状态
     */
    public void cacheFollowStatus(Long followerUserId, Long followedUserId, boolean isFollowed) {
        try {
            String key = getFollowStatusKey(followerUserId, followedUserId);
            followStatusCache.put(key, isFollowed);
            log.debug("缓存关注状态: follower={}, followed={}, status={}", 
                    followerUserId, followedUserId, isFollowed);
        } catch (Exception e) {
            log.error("缓存关注状态失败: follower={}, followed={}, error={}", 
                    followerUserId, followedUserId, e.getMessage(), e);
        }
    }

    /**
     * 获取关注状态
     */
    public Boolean getFollowStatus(Long followerUserId, Long followedUserId) {
        try {
            String key = getFollowStatusKey(followerUserId, followedUserId);
            return followStatusCache.get(key);
        } catch (Exception e) {
            log.error("获取关注状态缓存失败: follower={}, followed={}, error={}", 
                    followerUserId, followedUserId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 更新关注状态缓存
     */
    public void updateFollowStatusCache(Long followerUserId, Long followedUserId, boolean isFollowed) {
        cacheFollowStatus(followerUserId, followedUserId, isFollowed);
    }

    /**
     * 缓存用户关注数
     */
    public void cacheFollowingCount(Long userId, Integer count) {
        try {
            String key = getFollowingCountKey(userId);
            userStatisticsCache.put(key, count);
            log.debug("缓存用户关注数: userId={}, count={}", userId, count);
        } catch (Exception e) {
            log.error("缓存用户关注数失败: userId={}, error={}", userId, e.getMessage(), e);
        }
    }

    /**
     * 获取用户关注数
     */
    public Integer getFollowingCount(Long userId) {
        try {
            String key = getFollowingCountKey(userId);
            return userStatisticsCache.get(key);
        } catch (Exception e) {
            log.error("获取用户关注数缓存失败: userId={}, error={}", userId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 缓存用户粉丝数
     */
    public void cacheFollowerCount(Long userId, Integer count) {
        try {
            String key = getFollowerCountKey(userId);
            userStatisticsCache.put(key, count);
            log.debug("缓存用户粉丝数: userId={}, count={}", userId, count);
        } catch (Exception e) {
            log.error("缓存用户粉丝数失败: userId={}, error={}", userId, e.getMessage(), e);
        }
    }

    /**
     * 获取用户粉丝数
     */
    public Integer getFollowerCount(Long userId) {
        try {
            String key = getFollowerCountKey(userId);
            return userStatisticsCache.get(key);
        } catch (Exception e) {
            log.error("获取用户粉丝数缓存失败: userId={}, error={}", userId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 缓存关注列表
     */
    public void cacheFollowList(Long userId, Integer page, Integer size, Object data) {
        try {
            String key = getFollowListKey(userId, page, size);
            followListCache.put(key, data);
            log.debug("缓存关注列表: userId={}, page={}, size={}", userId, page, size);
        } catch (Exception e) {
            log.error("缓存关注列表失败: userId={}, error={}", userId, e.getMessage(), e);
        }
    }

    /**
     * 获取关注列表
     */
    public Object getFollowList(Long userId, Integer page, Integer size) {
        try {
            String key = getFollowListKey(userId, page, size);
            return followListCache.get(key);
        } catch (Exception e) {
            log.error("获取关注列表缓存失败: userId={}, error={}", userId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 缓存粉丝列表
     */
    public void cacheFollowerList(Long userId, Integer page, Integer size, Object data) {
        try {
            String key = getFollowerListKey(userId, page, size);
            followListCache.put(key, data);
            log.debug("缓存粉丝列表: userId={}, page={}, size={}", userId, page, size);
        } catch (Exception e) {
            log.error("缓存粉丝列表失败: userId={}, error={}", userId, e.getMessage(), e);
        }
    }

    /**
     * 获取粉丝列表
     */
    public Object getFollowerList(Long userId, Integer page, Integer size) {
        try {
            String key = getFollowerListKey(userId, page, size);
            return followListCache.get(key);
        } catch (Exception e) {
            log.error("获取粉丝列表缓存失败: userId={}, error={}", userId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 失效用户统计缓存
     */
    public void invalidateUserStatisticsCache(Long userId) {
        try {
            String followingKey = getFollowingCountKey(userId);
            String followerKey = getFollowerCountKey(userId);
            
            boolean followingRemoved = Boolean.TRUE.equals(userStatisticsCache.remove(followingKey));
            boolean followerRemoved = Boolean.TRUE.equals(userStatisticsCache.remove(followerKey));
            
            log.debug("失效用户统计缓存: userId={}, followingRemoved={}, followerRemoved={}", 
                    userId, followingRemoved, followerRemoved);
        } catch (Exception e) {
            log.error("失效用户统计缓存失败: userId={}, error={}", userId, e.getMessage(), e);
        }
    }

    /**
     * 失效用户列表缓存
     */
    public void invalidateUserListCache(Long userId) {
        try {
            // 精确删除该用户相关的缓存key
            // 删除关注列表缓存 (常见的分页组合)
            int removedCount = 0;
            for (int page = 1; page <= 10; page++) { // 假设最多缓存前10页
                for (int size : new int[]{10, 20, 50}) { // 常见的页面大小
                    String followKey = getFollowListKey(userId, page, size);
                    String followerKey = getFollowerListKey(userId, page, size);
                    
                    // JetCache的remove方法返回boolean表示是否成功删除
                    if (Boolean.TRUE.equals(followListCache.remove(followKey))) {
                        removedCount++;
                    }
                    if (Boolean.TRUE.equals(followListCache.remove(followerKey))) {
                        removedCount++;
                    }
                }
            }
            
            log.debug("失效用户列表缓存完成: userId={}, removedKeys={}", userId, removedCount);
        } catch (Exception e) {
            log.error("失效用户列表缓存失败: userId={}, error={}", userId, e.getMessage(), e);
            // JetCache没有批量清理方法，精确删除失败时只能记录日志
            log.warn("无法清理用户列表缓存，建议检查缓存服务状态: userId={}", userId);
        }
    }

    /**
     * 失效关注状态缓存
     */
    public void invalidateFollowStatusCache(Long followerUserId, Long followedUserId) {
        try {
            String key = getFollowStatusKey(followerUserId, followedUserId);
            boolean removed = Boolean.TRUE.equals(followStatusCache.remove(key));
            
            log.debug("失效关注状态缓存: follower={}, followed={}, removed={}", 
                    followerUserId, followedUserId, removed);
        } catch (Exception e) {
            log.error("失效关注状态缓存失败: follower={}, followed={}, error={}", 
                    followerUserId, followedUserId, e.getMessage(), e);
        }
    }
} 