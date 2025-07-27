package com.gig.collide.tag.infrastructure.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * 标签服务幂等性处理服务
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotentService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String KEY_PREFIX = "tag:idempotent:";
    private static final Duration EXPIRE_TIME = Duration.ofMinutes(10);
    private static final String PROCESSING_FLAG = "PROCESSING";

    /**
     * 执行幂等性操作
     *
     * @param idempotentKey 幂等键
     * @param supplier      业务执行器
     * @param <T>           返回类型
     * @return 执行结果
     */
    public <T> T executeIdempotent(String idempotentKey, Supplier<T> supplier) {
        // 1. 参数验证
        if (!StringUtils.hasText(idempotentKey)) {
            throw new IllegalArgumentException("幂等键不能为空");
        }

        String redisKey = buildRedisKey(idempotentKey);

        try {
            // 2. 检查是否已有结果
            Object existingResult = redisTemplate.opsForValue().get(redisKey);
            if (existingResult != null) {
                if (PROCESSING_FLAG.equals(existingResult)) {
                    log.warn("幂等性检查：请求正在处理中，Key: {}", idempotentKey);
                    throw new RuntimeException("请求正在处理中，请稍后重试");
                } else {
                    log.info("幂等性检查：返回已有结果，Key: {}", idempotentKey);
                    @SuppressWarnings("unchecked")
                    T result = (T) existingResult;
                    return result;
                }
            }

            // 3. 设置处理中标志
            Boolean lockSuccess = redisTemplate.opsForValue().setIfAbsent(redisKey, PROCESSING_FLAG, EXPIRE_TIME);
            if (Boolean.FALSE.equals(lockSuccess)) {
                log.warn("幂等性检查：获取锁失败，Key: {}", idempotentKey);
                throw new RuntimeException("请求正在处理中，请稍后重试");
            }

            log.info("幂等性检查：开始执行业务逻辑，Key: {}", idempotentKey);

            // 4. 执行业务逻辑
            T result = supplier.get();

            // 5. 缓存结果
            redisTemplate.opsForValue().set(redisKey, result, EXPIRE_TIME);

            log.info("幂等性检查：业务逻辑执行完成，Key: {}", idempotentKey);
            return result;

        } catch (Exception e) {
            // 6. 异常时清除处理中标志
            try {
                redisTemplate.delete(redisKey);
            } catch (Exception ex) {
                log.error("清除幂等性标志失败，Key: {}", idempotentKey, ex);
            }

            if (e instanceof RuntimeException) {
                log.error("幂等性处理业务异常，Key: {}, Error: {}", idempotentKey, e.getMessage());
                throw e;
            } else {
                log.error("幂等性处理系统异常，Key: {}", idempotentKey, e);
                throw new RuntimeException("系统异常，请稍后重试", e);
            }
        }
    }

    /**
     * 清除幂等性记录
     *
     * @param idempotentKey 幂等键
     */
    public void clearIdempotentRecord(String idempotentKey) {
        if (!StringUtils.hasText(idempotentKey)) {
            return;
        }

        String redisKey = buildRedisKey(idempotentKey);
        try {
            redisTemplate.delete(redisKey);
            log.info("清除幂等性记录成功，Key: {}", idempotentKey);
        } catch (Exception e) {
            log.warn("清除幂等性记录失败，Key: {}", idempotentKey, e);
        }
    }

    /**
     * 生成标签创建幂等键
     */
    public static String generateTagCreateIdempotentKey(String tagName, String tagType) {
        return String.format("create:tag:%s:%s", tagType, tagName);
    }

    /**
     * 生成标签更新幂等键
     */
    public static String generateTagUpdateIdempotentKey(Long tagId, Integer version) {
        return String.format("update:tag:%d:%d", tagId, version);
    }

    /**
     * 生成标签删除幂等键
     */
    public static String generateTagDeleteIdempotentKey(Long tagId) {
        return String.format("delete:tag:%d", tagId);
    }

    /**
     * 生成用户兴趣标签操作幂等键
     */
    public static String generateUserInterestTagIdempotentKey(Long userId, String operation) {
        return String.format("user:interest:tag:%d:%s", userId, operation);
    }

    /**
     * 构建Redis键
     */
    private String buildRedisKey(String idempotentKey) {
        return KEY_PREFIX + idempotentKey;
    }
} 