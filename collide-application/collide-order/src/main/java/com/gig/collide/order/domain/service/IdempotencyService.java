package com.gig.collide.order.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

/**
 * 幂等性控制服务
 * 
 * 用于防止重复操作，确保订单操作的幂等性
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String REQUEST_PREFIX = "order:request:";
    private static final String OPERATION_PREFIX = "order:operation:";
    private static final Duration DEFAULT_EXPIRATION = Duration.ofHours(24);

    /**
     * 检查请求是否已处理（基于请求ID）
     * 
     * @param requestId 请求ID
     * @return true-已处理，false-未处理
     */
    public boolean isRequestProcessed(String requestId) {
        if (requestId == null || requestId.trim().isEmpty()) {
            return false;
        }
        String key = REQUEST_PREFIX + requestId;
        Boolean exists = redisTemplate.hasKey(key);
        log.debug("检查请求是否已处理，requestId: {}, exists: {}", requestId, exists);
        return Boolean.TRUE.equals(exists);
    }

    /**
     * 标记请求已处理
     * 
     * @param requestId 请求ID
     * @param expiration 过期时间
     */
    public void markRequestProcessed(String requestId, Duration expiration) {
        if (requestId == null || requestId.trim().isEmpty()) {
            return;
        }
        String key = REQUEST_PREFIX + requestId;
        Duration exp = expiration != null ? expiration : DEFAULT_EXPIRATION;
        redisTemplate.opsForValue().set(key, "1", exp);
        log.debug("标记请求已处理，requestId: {}, expiration: {}", requestId, exp);
    }

    /**
     * 标记请求已处理（使用默认过期时间）
     * 
     * @param requestId 请求ID
     */
    public void markRequestProcessed(String requestId) {
        markRequestProcessed(requestId, DEFAULT_EXPIRATION);
    }

    /**
     * 检查操作是否已执行（基于业务标识）
     * 
     * @param businessKey 业务唯一标识（如：cancel_order_{orderNo}）
     * @return true-已执行，false-未执行
     */
    public boolean isOperationExecuted(String businessKey) {
        if (businessKey == null || businessKey.trim().isEmpty()) {
            return false;
        }
        String key = OPERATION_PREFIX + businessKey;
        Boolean exists = redisTemplate.hasKey(key);
        log.debug("检查操作是否已执行，businessKey: {}, exists: {}", businessKey, exists);
        return Boolean.TRUE.equals(exists);
    }

    /**
     * 标记操作已执行
     * 
     * @param businessKey 业务唯一标识
     * @param result 操作结果（可选）
     * @param expiration 过期时间
     */
    public void markOperationExecuted(String businessKey, String result, Duration expiration) {
        if (businessKey == null || businessKey.trim().isEmpty()) {
            return;
        }
        String key = OPERATION_PREFIX + businessKey;
        String value = result != null ? result : "executed";
        Duration exp = expiration != null ? expiration : DEFAULT_EXPIRATION;
        redisTemplate.opsForValue().set(key, value, exp);
        log.debug("标记操作已执行，businessKey: {}, result: {}, expiration: {}", businessKey, value, exp);
    }

    /**
     * 标记操作已执行（使用默认过期时间）
     * 
     * @param businessKey 业务唯一标识
     */
    public void markOperationExecuted(String businessKey) {
        markOperationExecuted(businessKey, null, DEFAULT_EXPIRATION);
    }

    /**
     * 获取操作结果
     * 
     * @param businessKey 业务唯一标识
     * @return 操作结果，null表示未执行
     */
    public String getOperationResult(String businessKey) {
        if (businessKey == null || businessKey.trim().isEmpty()) {
            return null;
        }
        String key = OPERATION_PREFIX + businessKey;
        String result = redisTemplate.opsForValue().get(key);
        log.debug("获取操作结果，businessKey: {}, result: {}", businessKey, result);
        return result;
    }

    /**
     * 尝试获取分布式锁
     * 
     * @param lockKey 锁键
     * @param expiration 锁过期时间
     * @return true-获取成功，false-获取失败
     */
    public boolean tryLock(String lockKey, Duration expiration) {
        if (lockKey == null || lockKey.trim().isEmpty()) {
            return false;
        }
        String key = "lock:" + lockKey;
        String value = UUID.randomUUID().toString();
        Duration exp = expiration != null ? expiration : Duration.ofMinutes(5);
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, value, exp);
        log.debug("尝试获取分布式锁，lockKey: {}, success: {}, expiration: {}", lockKey, success, exp);
        return Boolean.TRUE.equals(success);
    }

    /**
     * 释放分布式锁
     * 
     * @param lockKey 锁键
     */
    public void releaseLock(String lockKey) {
        if (lockKey == null || lockKey.trim().isEmpty()) {
            return;
        }
        String key = "lock:" + lockKey;
        Boolean deleted = redisTemplate.delete(key);
        log.debug("释放分布式锁，lockKey: {}, deleted: {}", lockKey, deleted);
    }

    /**
     * 执行幂等性操作
     * 
     * @param requestId 请求ID
     * @param operation 操作逻辑
     * @param <T> 返回类型
     * @return 操作结果
     */
    public <T> T executeIdempotent(String requestId, IdempotentOperation<T> operation) throws Exception {
        // 检查是否已处理
        if (isRequestProcessed(requestId)) {
            log.info("请求已处理，跳过执行，requestId: {}", requestId);
            throw new IllegalStateException("请求已处理，请勿重复提交");
        }

        // 执行操作
        try {
            T result = operation.execute();
            // 标记已处理
            markRequestProcessed(requestId);
            log.info("幂等性操作执行成功，requestId: {}", requestId);
            return result;
        } catch (Exception e) {
            log.error("幂等性操作执行失败，requestId: {}", requestId, e);
            throw e;
        }
    }

    /**
     * 执行带锁的幂等性操作
     * 
     * @param requestId 请求ID
     * @param lockKey 锁键
     * @param operation 操作逻辑
     * @param <T> 返回类型
     * @return 操作结果
     */
    public <T> T executeWithLock(String requestId, String lockKey, IdempotentOperation<T> operation) throws Exception {
        // 检查是否已处理
        if (isRequestProcessed(requestId)) {
            log.info("请求已处理，跳过执行，requestId: {}", requestId);
            throw new IllegalStateException("请求已处理，请勿重复提交");
        }

        // 尝试获取锁
        if (!tryLock(lockKey, Duration.ofMinutes(5))) {
            log.warn("获取锁失败，可能有并发操作，requestId: {}, lockKey: {}", requestId, lockKey);
            throw new IllegalStateException("系统繁忙，请稍后重试");
        }

        try {
            // 再次检查是否已处理（双重检查）
            if (isRequestProcessed(requestId)) {
                log.info("请求已处理（双重检查），跳过执行，requestId: {}", requestId);
                throw new IllegalStateException("请求已处理，请勿重复提交");
            }

            // 执行操作
            T result = operation.execute();
            
            // 标记已处理
            markRequestProcessed(requestId);
            log.info("带锁的幂等性操作执行成功，requestId: {}, lockKey: {}", requestId, lockKey);
            return result;
            
        } finally {
            // 释放锁
            releaseLock(lockKey);
        }
    }

    /**
     * 清理过期的幂等性记录
     * 
     * @param pattern 键模式
     * @return 清理数量
     */
    public long cleanExpiredRecords(String pattern) {
        // Redis会自动清理过期键，这里主要用于手动清理
        // 实际生产环境建议通过Redis的过期策略自动处理
        log.info("清理过期的幂等性记录，pattern: {}", pattern);
        return 0L;
    }

    /**
     * 幂等性操作接口
     * 
     * @param <T> 返回类型
     */
    @FunctionalInterface
    public interface IdempotentOperation<T> {
        T execute() throws Exception;
    }
} 