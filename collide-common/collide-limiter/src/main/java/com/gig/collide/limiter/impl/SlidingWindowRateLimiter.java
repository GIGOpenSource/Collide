package com.gig.collide.limiter.impl;

import com.gig.collide.limiter.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * 滑动窗口限流器实现
 * 基于 Redis 实现的滑动窗口限流算法
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@RequiredArgsConstructor
public class SlidingWindowRateLimiter implements RateLimiter {

    private final StringRedisTemplate redisTemplate;

    private static final String LIMIT_KEY_PREFIX = "collide:limit:";

    /**
     * Redis Lua 脚本：滑动窗口限流
     * 使用 ZSET 存储请求时间戳，实现精确的滑动窗口
     */
    private static final String SLIDING_WINDOW_SCRIPT = """
        local key = KEYS[1]
        local limit = tonumber(ARGV[1])
        local windowSize = tonumber(ARGV[2])
        local currentTime = tonumber(ARGV[3])
        
        -- 移除过期的记录
        local expireTime = currentTime - windowSize * 1000
        redis.call('ZREMRANGEBYSCORE', key, '-inf', expireTime)
        
        -- 获取当前窗口内的请求数量
        local currentCount = redis.call('ZCARD', key)
        
        if currentCount < limit then
            -- 添加当前请求记录
            redis.call('ZADD', key, currentTime, currentTime)
            -- 设置过期时间
            redis.call('EXPIRE', key, windowSize)
            return {1, limit - currentCount - 1}
        else
            return {0, 0}
        end
        """;

    private final DefaultRedisScript<java.util.List> slidingWindowScript = 
        new DefaultRedisScript<>(SLIDING_WINDOW_SCRIPT, java.util.List.class);

    @Override
    public Boolean tryAcquire(String key, int limit, int windowSize) {
        try {
            String limitKey = LIMIT_KEY_PREFIX + key;
            long currentTime = System.currentTimeMillis();
            
            java.util.List<Long> result = redisTemplate.execute(
                slidingWindowScript,
                Collections.singletonList(limitKey),
                String.valueOf(limit),
                String.valueOf(windowSize),
                String.valueOf(currentTime)
            );
            
            if (result != null && result.size() >= 1) {
                boolean allowed = result.get(0) == 1L;
                if (!allowed) {
                    log.debug("限流触发: key={}, limit={}, windowSize={}s", key, limit, windowSize);
                }
                return allowed;
            }
            
            return false;
        } catch (Exception e) {
            log.error("限流检查异常: key={}", key, e);
            // 异常情况下默认允许通过
            return true;
        }
    }

    @Override
    public Long getAvailableTokens(String key, int limit, int windowSize) {
        try {
            String limitKey = LIMIT_KEY_PREFIX + key;
            long currentTime = System.currentTimeMillis();
            long expireTime = currentTime - windowSize * 1000L;
            
            // 移除过期记录
            redisTemplate.opsForZSet().removeRangeByScore(limitKey, Double.NEGATIVE_INFINITY, expireTime);
            
            // 获取当前窗口内的请求数量
            Long currentCount = redisTemplate.opsForZSet().zCard(limitKey);
            if (currentCount == null) {
                currentCount = 0L;
            }
            
            return Math.max(0L, limit - currentCount);
        } catch (Exception e) {
            log.error("获取可用令牌数异常: key={}", key, e);
            return (long) limit;
        }
    }

    @Override
    public void reset(String key) {
        try {
            String limitKey = LIMIT_KEY_PREFIX + key;
            redisTemplate.delete(limitKey);
            log.debug("重置限流计数: key={}", key);
        } catch (Exception e) {
            log.error("重置限流计数异常: key={}", key, e);
        }
    }
} 