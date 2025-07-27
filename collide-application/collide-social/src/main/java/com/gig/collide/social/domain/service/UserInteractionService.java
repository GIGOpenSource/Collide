package com.gig.collide.social.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户互动状态查询服务
 * 
 * <p>负责查询用户对动态的互动状态，如是否已点赞、收藏、关注等</p>
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserInteractionService {
    
    private final StringRedisTemplate stringRedisTemplate;
    
    // 缓存Key前缀
    private static final String LIKE_KEY_PREFIX = "user:like:";
    private static final String FAVORITE_KEY_PREFIX = "user:favorite:";
    private static final String FOLLOW_KEY_PREFIX = "user:follow:";
    
    // 缓存过期时间
    private static final Duration CACHE_EXPIRATION = Duration.ofHours(1);
    
    /**
     * 获取用户对单个动态的互动状态
     *
     * @param userId 用户ID
     * @param postId 动态ID
     * @return 互动状态
     */
    public UserInteractionStatus getUserInteractionStatus(Long userId, Long postId) {
        if (userId == null || postId == null) {
            return UserInteractionStatus.empty();
        }
        
        try {
            String likeKey = LIKE_KEY_PREFIX + userId + ":" + postId;
            String favoriteKey = FAVORITE_KEY_PREFIX + userId + ":" + postId;
            
            // 批量检查缓存状态
            Map<String, Boolean> statuses = batchCheckCache(List.of(likeKey, favoriteKey));
            
            return UserInteractionStatus.builder()
                .liked(statuses.getOrDefault(likeKey, false))
                .favorited(statuses.getOrDefault(favoriteKey, false))
                .build();
                
        } catch (Exception e) {
            log.error("获取用户互动状态失败，用户ID: {}, 动态ID: {}", userId, postId, e);
            return UserInteractionStatus.empty();
        }
    }
    
    /**
     * 批量获取用户对多个动态的互动状态
     *
     * @param userId 用户ID
     * @param postIds 动态ID列表
     * @return 互动状态映射
     */
    public Map<Long, UserInteractionStatus> batchGetUserInteractionStatus(Long userId, List<Long> postIds) {
        Map<Long, UserInteractionStatus> result = new HashMap<>();
        
        if (userId == null || postIds == null || postIds.isEmpty()) {
            return result;
        }
        
        try {
            // 构建所有需要检查的缓存key
            Map<String, Long> keyToPostIdMap = new HashMap<>();
            for (Long postId : postIds) {
                String likeKey = LIKE_KEY_PREFIX + userId + ":" + postId;
                String favoriteKey = FAVORITE_KEY_PREFIX + userId + ":" + postId;
                keyToPostIdMap.put(likeKey, postId);
                keyToPostIdMap.put(favoriteKey, postId);
            }
            
            // 批量检查缓存
            Map<String, Boolean> cacheStatuses = batchCheckCache(keyToPostIdMap.keySet().stream().toList());
            
            // 组装结果
            for (Long postId : postIds) {
                String likeKey = LIKE_KEY_PREFIX + userId + ":" + postId;
                String favoriteKey = FAVORITE_KEY_PREFIX + userId + ":" + postId;
                
                UserInteractionStatus status = UserInteractionStatus.builder()
                    .liked(cacheStatuses.getOrDefault(likeKey, false))
                    .favorited(cacheStatuses.getOrDefault(favoriteKey, false))
                    .build();
                    
                result.put(postId, status);
            }
            
        } catch (Exception e) {
            log.error("批量获取用户互动状态失败，用户ID: {}, 动态数量: {}", userId, postIds.size(), e);
        }
        
        return result;
    }
    
    /**
     * 记录用户点赞状态
     *
     * @param userId 用户ID
     * @param postId 动态ID
     * @param isLiked 是否点赞
     */
    public void recordLikeStatus(Long userId, Long postId, boolean isLiked) {
        if (userId == null || postId == null) {
            return;
        }
        
        try {
            String likeKey = LIKE_KEY_PREFIX + userId + ":" + postId;
            
            if (isLiked) {
                stringRedisTemplate.opsForValue().set(likeKey, "1", CACHE_EXPIRATION);
                log.debug("记录点赞状态: userId={}, postId={}, liked=true", userId, postId);
            } else {
                stringRedisTemplate.delete(likeKey);
                log.debug("移除点赞状态: userId={}, postId={}", userId, postId);
            }
            
        } catch (Exception e) {
            log.error("记录点赞状态失败，用户ID: {}, 动态ID: {}", userId, postId, e);
        }
    }
    
    /**
     * 记录用户收藏状态
     *
     * @param userId 用户ID
     * @param postId 动态ID
     * @param isFavorited 是否收藏
     */
    public void recordFavoriteStatus(Long userId, Long postId, boolean isFavorited) {
        if (userId == null || postId == null) {
            return;
        }
        
        try {
            String favoriteKey = FAVORITE_KEY_PREFIX + userId + ":" + postId;
            
            if (isFavorited) {
                stringRedisTemplate.opsForValue().set(favoriteKey, "1", CACHE_EXPIRATION);
                log.debug("记录收藏状态: userId={}, postId={}, favorited=true", userId, postId);
            } else {
                stringRedisTemplate.delete(favoriteKey);
                log.debug("移除收藏状态: userId={}, postId={}", userId, postId);
            }
            
        } catch (Exception e) {
            log.error("记录收藏状态失败，用户ID: {}, 动态ID: {}", userId, postId, e);
        }
    }
    
    /**
     * 检查用户是否关注某个作者
     *
     * @param followerId 关注者ID
     * @param authorId 作者ID
     * @return 是否关注
     */
    public boolean isFollowing(Long followerId, Long authorId) {
        if (followerId == null || authorId == null || followerId.equals(authorId)) {
            return false;
        }
        
        try {
            String followKey = FOLLOW_KEY_PREFIX + followerId + ":" + authorId;
            Boolean isFollowing = stringRedisTemplate.hasKey(followKey);
            return Boolean.TRUE.equals(isFollowing);
        } catch (Exception e) {
            log.error("检查关注状态失败，关注者ID: {}, 作者ID: {}", followerId, authorId, e);
            return false;
        }
    }
    
    /**
     * 记录关注状态
     *
     * @param followerId 关注者ID
     * @param authorId 作者ID
     * @param isFollowing 是否关注
     */
    public void recordFollowStatus(Long followerId, Long authorId, boolean isFollowing) {
        if (followerId == null || authorId == null) {
            return;
        }
        
        try {
            String followKey = FOLLOW_KEY_PREFIX + followerId + ":" + authorId;
            
            if (isFollowing) {
                stringRedisTemplate.opsForValue().set(followKey, "1", CACHE_EXPIRATION);
                log.debug("记录关注状态: followerId={}, authorId={}, following=true", followerId, authorId);
            } else {
                stringRedisTemplate.delete(followKey);
                log.debug("移除关注状态: followerId={}, authorId={}", followerId, authorId);
            }
            
        } catch (Exception e) {
            log.error("记录关注状态失败，关注者ID: {}, 作者ID: {}", followerId, authorId, e);
        }
    }
    
    /**
     * 批量检查缓存状态
     *
     * @param keys 缓存key列表
     * @return key->存在状态的映射
     */
    private Map<String, Boolean> batchCheckCache(List<String> keys) {
        Map<String, Boolean> result = new HashMap<>();
        
        if (keys == null || keys.isEmpty()) {
            return result;
        }
        
        try {
            // 使用pipeline批量检查
            List<Object> results = stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                for (String key : keys) {
                    connection.exists(key.getBytes());
                }
                return null;
            });
            
            // 组装结果
            for (int i = 0; i < keys.size() && i < results.size(); i++) {
                Boolean exists = (Boolean) results.get(i);
                result.put(keys.get(i), Boolean.TRUE.equals(exists));
            }
            
        } catch (Exception e) {
            log.error("批量检查缓存状态失败", e);
            // 降级处理：逐个检查
            for (String key : keys) {
                try {
                    Boolean exists = stringRedisTemplate.hasKey(key);
                    result.put(key, Boolean.TRUE.equals(exists));
                } catch (Exception ex) {
                    log.error("检查单个缓存状态失败, key: {}", key, ex);
                    result.put(key, false);
                }
            }
        }
        
        return result;
    }
    
    /**
     * 用户互动状态数据类
     */
    public static class UserInteractionStatus {
        private boolean liked;
        private boolean favorited;
        private boolean followed;
        
        public static UserInteractionStatus empty() {
            return new UserInteractionStatus();
        }
        
        public static UserInteractionStatusBuilder builder() {
            return new UserInteractionStatusBuilder();
        }
        
        // Getters and Setters
        public boolean isLiked() {
            return liked;
        }
        
        public void setLiked(boolean liked) {
            this.liked = liked;
        }
        
        public boolean isFavorited() {
            return favorited;
        }
        
        public void setFavorited(boolean favorited) {
            this.favorited = favorited;
        }
        
        public boolean isFollowed() {
            return followed;
        }
        
        public void setFollowed(boolean followed) {
            this.followed = followed;
        }
    }
    
    /**
     * Builder类
     */
    public static class UserInteractionStatusBuilder {
        private boolean liked;
        private boolean favorited;
        private boolean followed;
        
        public UserInteractionStatusBuilder liked(boolean liked) {
            this.liked = liked;
            return this;
        }
        
        public UserInteractionStatusBuilder favorited(boolean favorited) {
            this.favorited = favorited;
            return this;
        }
        
        public UserInteractionStatusBuilder followed(boolean followed) {
            this.followed = followed;
            return this;
        }
        
        public UserInteractionStatus build() {
            UserInteractionStatus status = new UserInteractionStatus();
            status.setLiked(this.liked);
            status.setFavorited(this.favorited);
            status.setFollowed(this.followed);
            return status;
        }
    }
} 