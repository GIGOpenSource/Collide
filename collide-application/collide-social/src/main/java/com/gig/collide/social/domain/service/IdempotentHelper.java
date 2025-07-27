package com.gig.collide.social.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 幂等性处理工具类
 * 
 * <p>提供分布式锁和重复操作检查功能，确保关键操作的幂等性</p>
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IdempotentHelper {
    
    private final StringRedisTemplate stringRedisTemplate;
    
    /**
     * 尝试获取分布式锁
     *
     * @param lockKey 锁的key
     * @param expiration 过期时间
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, Duration expiration) {
        try {
            Boolean locked = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "1", expiration);
            log.debug("尝试获取锁，key: {}, 结果: {}", lockKey, locked);
            return Boolean.TRUE.equals(locked);
        } catch (Exception e) {
            log.error("获取分布式锁失败，key: {}", lockKey, e);
            return false;
        }
    }
    
    /**
     * 释放分布式锁
     *
     * @param lockKey 锁的key
     */
    public void releaseLock(String lockKey) {
        try {
            stringRedisTemplate.delete(lockKey);
            log.debug("释放锁，key: {}", lockKey);
        } catch (Exception e) {
            log.error("释放分布式锁失败，key: {}", lockKey, e);
        }
    }
    
    /**
     * 检查操作是否重复
     *
     * @param operationKey 操作的key
     * @return 是否重复
     */
    public boolean isDuplicate(String operationKey) {
        try {
            Boolean exists = stringRedisTemplate.hasKey(operationKey);
            log.debug("检查重复操作，key: {}, 结果: {}", operationKey, exists);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.error("检查重复操作失败，key: {}", operationKey, e);
            return false;
        }
    }
    
    /**
     * 记录操作状态
     *
     * @param operationKey 操作key
     * @param value 操作值
     * @param expiration 过期时间
     */
    public void recordOperation(String operationKey, String value, Duration expiration) {
        try {
            stringRedisTemplate.opsForValue().set(operationKey, value, expiration);
            log.debug("记录操作状态，key: {}, value: {}", operationKey, value);
        } catch (Exception e) {
            log.error("记录操作状态失败，key: {}", operationKey, e);
        }
    }
    
    /**
     * 获取操作状态
     *
     * @param operationKey 操作key
     * @return 操作状态
     */
    public String getOperationStatus(String operationKey) {
        try {
            String status = stringRedisTemplate.opsForValue().get(operationKey);
            log.debug("获取操作状态，key: {}, status: {}", operationKey, status);
            return status;
        } catch (Exception e) {
            log.error("获取操作状态失败，key: {}", operationKey, e);
            return null;
        }
    }
    
    /**
     * 执行幂等性操作
     *
     * @param lockKey 锁key
     * @param operationKey 操作key
     * @param expectedValue 期望值
     * @param operation 要执行的操作
     * @param lockExpiration 锁过期时间
     * @param statusExpiration 状态过期时间
     * @return 操作结果
     */
    public <T> T executeIdempotent(String lockKey, String operationKey, String expectedValue,
                                   IdempotentOperation<T> operation, 
                                   Duration lockExpiration, Duration statusExpiration) {
        // 1. 尝试获取分布式锁
        if (!tryLock(lockKey, lockExpiration)) {
            throw new RuntimeException("OPERATION_PENDING");
        }
        
        try {
            // 2. 检查是否已经执行过相同操作
            String currentStatus = getOperationStatus(operationKey);
            if (currentStatus != null && currentStatus.equals(expectedValue)) {
                throw new RuntimeException("ALREADY_OPERATED");
            }
            
            // 3. 执行业务操作
            T result;
            try {
                result = operation.execute();
            } catch (Exception e) {
                log.error("执行幂等性操作失败，operationKey: {}", operationKey, e);
                throw new RuntimeException("操作执行失败", e);
            }
            
            // 4. 记录操作状态
            recordOperation(operationKey, expectedValue, statusExpiration);
            
            return result;
            
        } finally {
            // 5. 释放锁
            releaseLock(lockKey);
        }
    }
    
    /**
     * 幂等性操作接口
     */
    @FunctionalInterface
    public interface IdempotentOperation<T> {
        T execute() throws Exception;
    }
} 