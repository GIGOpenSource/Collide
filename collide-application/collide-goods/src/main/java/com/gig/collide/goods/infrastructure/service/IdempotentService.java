package com.gig.collide.goods.infrastructure.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

/**
 * 幂等性服务
 * 
 * 提供基于Redis的幂等性保障机制
 * 使用标准化的collide-cache组件进行Redis操作
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class IdempotentService {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    private static final String IDEMPOTENT_KEY_PREFIX = "goods:idempotent:";
    private static final Duration DEFAULT_TTL = Duration.ofMinutes(5);
    
    /**
     * 检查是否已经处理过
     * 
     * @param key 幂等键
     * @return 是否已处理
     */
    public boolean isProcessed(String key) {
        if (key == null || key.trim().isEmpty()) {
            return false;
        }
        
        String fullKey = IDEMPOTENT_KEY_PREFIX + key;
        Boolean exists = redisTemplate.hasKey(fullKey);
        
        log.debug("检查幂等性，key：{}，已处理：{}", fullKey, exists);
        return Boolean.TRUE.equals(exists);
    }
    
    /**
     * 标记为已处理
     * 
     * @param key 幂等键
     * @param value 存储值
     * @param ttl 过期时间
     * @return 是否设置成功
     */
    public boolean markAsProcessed(String key, String value, Duration ttl) {
        if (key == null || key.trim().isEmpty()) {
            return false;
        }
        
        String fullKey = IDEMPOTENT_KEY_PREFIX + key;
        Boolean success = redisTemplate.opsForValue().setIfAbsent(fullKey, value, ttl);
        
        log.debug("标记幂等性，key：{}，value：{}，ttl：{}，成功：{}", 
            fullKey, value, ttl, success);
        
        return Boolean.TRUE.equals(success);
    }
    
    /**
     * 标记为已处理（使用默认TTL）
     * 
     * @param key 幂等键
     * @param value 存储值
     * @return 是否设置成功
     */
    public boolean markAsProcessed(String key, String value) {
        return markAsProcessed(key, value, DEFAULT_TTL);
    }
    
    /**
     * 标记为已处理（使用UUID作为值）
     * 
     * @param key 幂等键
     * @return 是否设置成功
     */
    public boolean markAsProcessed(String key) {
        return markAsProcessed(key, UUID.randomUUID().toString(), DEFAULT_TTL);
    }
    
    /**
     * 尝试获取锁并执行操作
     * 
     * @param key 幂等键
     * @param processor 处理器
     * @param ttl 锁过期时间
     * @param <T> 返回值类型
     * @return 处理结果
     * @throws Exception 处理异常
     */
    public <T> T executeWithIdempotent(String key, IdempotentProcessor<T> processor, Duration ttl) throws Exception {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("幂等键不能为空");
        }
        
        String fullKey = IDEMPOTENT_KEY_PREFIX + key;
        String lockValue = UUID.randomUUID().toString();
        
        // 尝试获取锁
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(fullKey, lockValue, ttl);
        if (!Boolean.TRUE.equals(locked)) {
            log.warn("操作已处理或正在处理中，key：{}", fullKey);
            throw new IllegalStateException("重复操作，请勿重复提交");
        }
        
        try {
            log.info("获取幂等锁成功，开始处理，key：{}", fullKey);
            
            // 执行业务逻辑
            T result = processor.process();
            
            log.info("业务处理完成，key：{}", fullKey);
            return result;
            
        } catch (Exception e) {
            log.error("业务处理失败，释放锁，key：{}", fullKey, e);
            // 业务失败时释放锁，允许重试
            redisTemplate.delete(fullKey);
            throw e;
        }
        // 成功时不释放锁，保持幂等性
    }
    
    /**
     * 使用默认TTL执行幂等操作
     */
    public <T> T executeWithIdempotent(String key, IdempotentProcessor<T> processor) throws Exception {
        return executeWithIdempotent(key, processor, DEFAULT_TTL);
    }
    
    /**
     * 移除幂等标记
     * 
     * @param key 幂等键
     * @return 是否移除成功
     */
    public boolean removeIdempotentMark(String key) {
        if (key == null || key.trim().isEmpty()) {
            return false;
        }
        
        String fullKey = IDEMPOTENT_KEY_PREFIX + key;
        Boolean deleted = redisTemplate.delete(fullKey);
        
        log.debug("移除幂等标记，key：{}，成功：{}", fullKey, deleted);
        return Boolean.TRUE.equals(deleted);
    }
    
    /**
     * 生成幂等键
     * 
     * @param operation 操作类型
     * @param businessKey 业务键
     * @return 幂等键
     */
    public String generateIdempotentKey(String operation, String businessKey) {
        return operation + ":" + businessKey;
    }
    
    /**
     * 生成商品操作的幂等键
     * 
     * @param operation 操作类型
     * @param goodsId 商品ID
     * @param userId 用户ID
     * @return 幂等键
     */
    public String generateGoodsIdempotentKey(String operation, Long goodsId, Long userId) {
        return generateIdempotentKey(operation, goodsId + ":" + userId);
    }
    
    /**
     * 幂等处理器接口
     */
    @FunctionalInterface
    public interface IdempotentProcessor<T> {
        T process() throws Exception;
    }
} 