package com.gig.collide.lock.impl;

import com.gig.collide.lock.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Redis 分布式锁实现
 * 参考 nft-turbo-lock 设计
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisDistributedLock implements DistributedLock {

    private final StringRedisTemplate redisTemplate;
    
    private final ConcurrentHashMap<String, String> lockMap = new ConcurrentHashMap<>();
    
    private static final String LOCK_PREFIX = "collide:lock:";
    
    private static final String UNLOCK_SCRIPT = 
        "if redis.call('get', KEYS[1]) == ARGV[1] then " +
        "  return redis.call('del', KEYS[1]) " +
        "else " +
        "  return 0 " +
        "end";

    @Override
    public boolean tryLock(String key, long timeout, TimeUnit unit) {
        try {
            String lockKey = LOCK_PREFIX + key;
            String lockValue = UUID.randomUUID().toString();
            long timeoutInMillis = unit.toMillis(timeout);
            
            Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, timeoutInMillis, TimeUnit.MILLISECONDS);
            
            if (Boolean.TRUE.equals(success)) {
                lockMap.put(key, lockValue);
                log.debug("获取分布式锁成功: key={}, value={}", key, lockValue);
                return true;
            }
            
            log.debug("获取分布式锁失败: key={}", key);
            return false;
        } catch (Exception e) {
            log.error("获取分布式锁异常: key={}", key, e);
            return false;
        }
    }

    @Override
    public void unlock(String key) {
        try {
            String lockKey = LOCK_PREFIX + key;
            String lockValue = lockMap.get(key);
            
            if (lockValue != null) {
                DefaultRedisScript<Long> script = new DefaultRedisScript<>(UNLOCK_SCRIPT, Long.class);
                Long result = redisTemplate.execute(script, Collections.singletonList(lockKey), lockValue);
                
                if (result != null && result == 1) {
                    lockMap.remove(key);
                    log.debug("释放分布式锁成功: key={}", key);
                } else {
                    log.warn("释放分布式锁失败，可能已过期: key={}", key);
                }
            }
        } catch (Exception e) {
            log.error("释放分布式锁异常: key={}", key, e);
        }
    }
} 