package com.gig.collide.payment.infrastructure.service;

import com.gig.collide.payment.infrastructure.config.PaymentConfiguration;
import com.gig.collide.payment.infrastructure.exception.PaymentBusinessException;
import com.gig.collide.payment.infrastructure.exception.PaymentErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 幂等性服务
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class IdempotentService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final PaymentConfiguration paymentConfiguration;

    private static final String PROCESSING_SUFFIX = ":processing";
    private static final String RESULT_SUFFIX = ":result";

    /**
     * 执行幂等操作
     * 
     * @param idempotentKey 幂等键
     * @param supplier 业务逻辑供应器
     * @param <T> 返回类型
     * @return 执行结果
     */
    public <T> T executeIdempotent(String idempotentKey, Supplier<T> supplier) {
        if (!paymentConfiguration.getIdempotency().getEnabled()) {
            return supplier.get();
        }

        if (!StringUtils.hasText(idempotentKey)) {
            throw PaymentBusinessException.of(PaymentErrorCode.IDEMPOTENT_KEY_MISSING);
        }

        String redisKey = buildRedisKey(idempotentKey);
        String processingKey = redisKey + PROCESSING_SUFFIX;
        String resultKey = redisKey + RESULT_SUFFIX;

        try {
            // 1. 检查是否已有结果
            @SuppressWarnings("unchecked")
            T existingResult = (T) redisTemplate.opsForValue().get(resultKey);
            if (existingResult != null) {
                log.info("幂等性检查：返回已有结果，Key: {}", idempotentKey);
                return existingResult;
            }

            // 2. 尝试获取处理锁
            Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(
                processingKey, 
                "processing", 
                Duration.ofSeconds(paymentConfiguration.getIdempotency().getTimeout())
            );

            if (!Boolean.TRUE.equals(lockAcquired)) {
                // 正在处理中
                log.warn("幂等性检查：请求正在处理中，Key: {}", idempotentKey);
                throw PaymentBusinessException.of(PaymentErrorCode.IDEMPOTENT_PROCESSING);
            }

            // 3. 执行业务逻辑
            log.info("幂等性检查：开始执行业务逻辑，Key: {}", idempotentKey);
            T result = supplier.get();

            // 4. 保存结果
            redisTemplate.opsForValue().set(
                resultKey, 
                result, 
                Duration.ofSeconds(paymentConfiguration.getIdempotency().getTimeout())
            );

            log.info("幂等性检查：业务逻辑执行完成，Key: {}", idempotentKey);
            return result;

        } catch (PaymentBusinessException e) {
            log.error("幂等性处理业务异常，Key: {}, Error: {}", idempotentKey, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("幂等性处理系统异常，Key: {}", idempotentKey, e);
            throw PaymentBusinessException.of(PaymentErrorCode.SYSTEM_ERROR, "幂等性处理异常", e);
        } finally {
            // 5. 释放处理锁
            try {
                redisTemplate.delete(processingKey);
            } catch (Exception e) {
                log.warn("释放幂等性处理锁失败，Key: {}", processingKey, e);
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
        String processingKey = redisKey + PROCESSING_SUFFIX;
        String resultKey = redisKey + RESULT_SUFFIX;

        try {
            redisTemplate.delete(processingKey);
            redisTemplate.delete(resultKey);
            log.info("清除幂等性记录成功，Key: {}", idempotentKey);
        } catch (Exception e) {
            log.warn("清除幂等性记录失败，Key: {}", idempotentKey, e);
        }
    }

    /**
     * 检查是否存在幂等性记录
     * 
     * @param idempotentKey 幂等键
     * @return 是否存在
     */
    public boolean hasIdempotentRecord(String idempotentKey) {
        if (!StringUtils.hasText(idempotentKey)) {
            return false;
        }

        String redisKey = buildRedisKey(idempotentKey);
        String resultKey = redisKey + RESULT_SUFFIX;

        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(resultKey));
        } catch (Exception e) {
            log.warn("检查幂等性记录失败，Key: {}", idempotentKey, e);
            return false;
        }
    }

    /**
     * 获取幂等性结果
     * 
     * @param idempotentKey 幂等键
     * @param <T> 结果类型
     * @return 幂等性结果
     */
    @SuppressWarnings("unchecked")
    public <T> T getIdempotentResult(String idempotentKey) {
        if (!StringUtils.hasText(idempotentKey)) {
            return null;
        }

        String redisKey = buildRedisKey(idempotentKey);
        String resultKey = redisKey + RESULT_SUFFIX;

        try {
            return (T) redisTemplate.opsForValue().get(resultKey);
        } catch (Exception e) {
            log.warn("获取幂等性结果失败，Key: {}", idempotentKey, e);
            return null;
        }
    }

    /**
     * 构建Redis键
     * 
     * @param idempotentKey 幂等键
     * @return Redis键
     */
    private String buildRedisKey(String idempotentKey) {
        return paymentConfiguration.getIdempotency().getKeyPrefix() + idempotentKey;
    }

    /**
     * 生成幂等键
     * 
     * @param prefix 前缀
     * @param businessKey 业务键
     * @return 幂等键
     */
    public static String generateIdempotentKey(String prefix, String businessKey) {
        return prefix + ":" + businessKey;
    }

    /**
     * 生成支付幂等键
     * 
     * @param orderNo 订单号
     * @param userId 用户ID
     * @return 幂等键
     */
    public static String generatePaymentIdempotentKey(String orderNo, Long userId) {
        return generateIdempotentKey("payment", orderNo + ":" + userId);
    }

    /**
     * 生成回调幂等键
     * 
     * @param orderNo 订单号
     * @param transactionNo 交易流水号
     * @return 幂等键
     */
    public static String generateCallbackIdempotentKey(String orderNo, String transactionNo) {
        return generateIdempotentKey("callback", orderNo + ":" + transactionNo);
    }
} 