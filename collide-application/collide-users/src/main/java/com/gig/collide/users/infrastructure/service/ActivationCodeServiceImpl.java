package com.gig.collide.users.infrastructure.service;

import com.gig.collide.users.domain.service.ActivationCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 激活码服务Redis实现类
 * 使用Redis存储和管理用户激活码
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@Service
public class ActivationCodeServiceImpl implements ActivationCodeService {

    private final StringRedisTemplate redisTemplate;
    private final SecureRandom secureRandom;

    @Value("${collide.activation-code.prefix:collide:activation:}")
    private String redisKeyPrefix;

    @Value("${collide.activation-code.length:6}")
    private int codeLength;

    public ActivationCodeServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.secureRandom = new SecureRandom();
    }

    @Override
    public String generateAndStoreCode(Long userId, CodeType codeType, int expireMinutes) {
        log.info("生成激活码，用户ID：{}，类型：{}，过期时间：{}分钟", userId, codeType, expireMinutes);
        
        try {
            // 生成激活码
            String activationCode = generateSecureCode();
            
            // 构建Redis Key
            String redisKey = buildRedisKey(userId, codeType);
            
            // 存储到Redis，设置过期时间
            redisTemplate.opsForValue().set(redisKey, activationCode, expireMinutes, TimeUnit.MINUTES);
            
            // 存储生成时间戳（用于频率限制）
            String timestampKey = buildTimestampKey(userId, codeType);
            redisTemplate.opsForValue().set(timestampKey, String.valueOf(System.currentTimeMillis()), 
                expireMinutes + 5, TimeUnit.MINUTES); // 时间戳比激活码多存储5分钟
            
            log.info("激活码生成成功，用户ID：{}，类型：{}", userId, codeType);
            return activationCode;
            
        } catch (Exception e) {
            log.error("生成激活码失败，用户ID：{}，类型：{}", userId, codeType, e);
            throw new RuntimeException("激活码生成失败", e);
        }
    }

    @Override
    public ActivationCodeValidationResult validateCode(Long userId, String code, CodeType codeType) {
        log.debug("验证激活码，用户ID：{}，类型：{}", userId, codeType);
        
        try {
            if (code == null || code.trim().isEmpty()) {
                return ActivationCodeValidationResult.failure("激活码不能为空", 
                    ActivationCodeValidationResult.ValidationFailureReason.CODE_MISMATCH);
            }
            
            // 构建Redis Key
            String redisKey = buildRedisKey(userId, codeType);
            
            // 从Redis获取存储的激活码
            String storedCode = redisTemplate.opsForValue().get(redisKey);
            
            if (storedCode == null) {
                log.warn("激活码不存在或已过期，用户ID：{}，类型：{}", userId, codeType);
                return ActivationCodeValidationResult.failure("激活码不存在或已过期", 
                    ActivationCodeValidationResult.ValidationFailureReason.CODE_NOT_FOUND);
            }
            
            // 验证激活码是否匹配
            if (!storedCode.equals(code)) {
                log.warn("激活码不匹配，用户ID：{}，类型：{}", userId, codeType);
                return ActivationCodeValidationResult.failure("激活码不正确", 
                    ActivationCodeValidationResult.ValidationFailureReason.CODE_MISMATCH);
            }
            
            log.info("激活码验证成功，用户ID：{}，类型：{}", userId, codeType);
            return ActivationCodeValidationResult.success();
            
        } catch (Exception e) {
            log.error("验证激活码异常，用户ID：{}，类型：{}", userId, codeType, e);
            return ActivationCodeValidationResult.failure("系统异常，请稍后重试", 
                ActivationCodeValidationResult.ValidationFailureReason.SYSTEM_ERROR);
        }
    }

    @Override
    public ActivationCodeValidationResult validateAndConsumeCode(Long userId, String code, CodeType codeType) {
        log.info("验证并消费激活码，用户ID：{}，类型：{}", userId, codeType);
        
        // 先验证激活码
        ActivationCodeValidationResult validationResult = validateCode(userId, code, codeType);
        
        if (validationResult.isValid()) {
            // 验证成功，删除激活码
            boolean removed = removeCode(userId, codeType);
            if (!removed) {
                log.warn("激活码验证成功但删除失败，用户ID：{}，类型：{}", userId, codeType);
            } else {
                log.info("激活码验证并消费成功，用户ID：{}，类型：{}", userId, codeType);
            }
        }
        
        return validationResult;
    }

    @Override
    public boolean removeCode(Long userId, CodeType codeType) {
        log.debug("删除激活码，用户ID：{}，类型：{}", userId, codeType);
        
        try {
            String redisKey = buildRedisKey(userId, codeType);
            String timestampKey = buildTimestampKey(userId, codeType);
            
            // 删除激活码和时间戳
            Long deletedCount = redisTemplate.delete(java.util.Arrays.asList(redisKey, timestampKey));
            
            boolean success = deletedCount != null && deletedCount > 0;
            log.debug("删除激活码结果，用户ID：{}，类型：{}，成功：{}", userId, codeType, success);
            return success;
            
        } catch (Exception e) {
            log.error("删除激活码异常，用户ID：{}，类型：{}", userId, codeType, e);
            return false;
        }
    }

    @Override
    public boolean canGenerateNewCode(Long userId, CodeType codeType, int intervalSeconds) {
        log.debug("检查是否可以生成新激活码，用户ID：{}，类型：{}，间隔：{}秒", userId, codeType, intervalSeconds);
        
        try {
            String timestampKey = buildTimestampKey(userId, codeType);
            String lastGenerateTimeStr = redisTemplate.opsForValue().get(timestampKey);
            
            if (lastGenerateTimeStr == null) {
                // 没有记录，可以生成
                return true;
            }
            
            long lastGenerateTime = Long.parseLong(lastGenerateTimeStr);
            long currentTime = System.currentTimeMillis();
            long elapsedSeconds = (currentTime - lastGenerateTime) / 1000;
            
            boolean canGenerate = elapsedSeconds >= intervalSeconds;
            log.debug("激活码生成检查结果，用户ID：{}，类型：{}，已过去：{}秒，可生成：{}", 
                userId, codeType, elapsedSeconds, canGenerate);
            
            return canGenerate;
            
        } catch (Exception e) {
            log.error("检查激活码生成权限异常，用户ID：{}，类型：{}", userId, codeType, e);
            // 异常情况下允许生成，避免阻塞正常业务
            return true;
        }
    }

    /**
     * 生成安全的激活码
     */
    private String generateSecureCode() {
        StringBuilder code = new StringBuilder();
        
        // 生成指定长度的数字激活码
        for (int i = 0; i < codeLength; i++) {
            code.append(secureRandom.nextInt(10));
        }
        
        return code.toString();
    }

    /**
     * 构建Redis Key
     */
    private String buildRedisKey(Long userId, CodeType codeType) {
        return redisKeyPrefix + codeType.name().toLowerCase() + ":" + userId;
    }

    /**
     * 构建时间戳Redis Key
     */
    private String buildTimestampKey(Long userId, CodeType codeType) {
        return redisKeyPrefix + "timestamp:" + codeType.name().toLowerCase() + ":" + userId;
    }

    /**
     * 获取激活码剩余过期时间（秒）
     * 用于调试和监控
     */
    public long getCodeRemainingTime(Long userId, CodeType codeType) {
        try {
            String redisKey = buildRedisKey(userId, codeType);
            Long expireTime = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
            return expireTime != null ? expireTime : -1;
        } catch (Exception e) {
            log.error("获取激活码剩余时间异常，用户ID：{}，类型：{}", userId, codeType, e);
            return -1;
        }
    }

    /**
     * 清理用户所有类型的激活码
     * 用于用户注销或其他清理场景
     */
    public void clearAllUserCodes(Long userId) {
        log.info("清理用户所有激活码，用户ID：{}", userId);
        
        try {
            for (CodeType codeType : CodeType.values()) {
                removeCode(userId, codeType);
            }
            log.info("用户激活码清理完成，用户ID：{}", userId);
        } catch (Exception e) {
            log.error("清理用户激活码异常，用户ID：{}", userId, e);
        }
    }
} 