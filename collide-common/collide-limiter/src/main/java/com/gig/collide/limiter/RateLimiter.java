package com.gig.collide.limiter;

/**
 * 限流服务接口
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface RateLimiter {

    /**
     * 判断一个key是否可以通过限流检查
     *
     * @param key        限流的key（通常是用户ID、IP地址等）
     * @param limit      限流的数量（请求次数上限）
     * @param windowSize 窗口大小，单位为秒
     * @return true-允许通过，false-被限流
     */
    Boolean tryAcquire(String key, int limit, int windowSize);

    /**
     * 获取剩余可用次数
     *
     * @param key        限流的key
     * @param limit      限流的数量
     * @param windowSize 窗口大小，单位为秒
     * @return 剩余可用次数
     */
    Long getAvailableTokens(String key, int limit, int windowSize);

    /**
     * 重置指定key的限流计数
     *
     * @param key 限流的key
     */
    void reset(String key);
} 