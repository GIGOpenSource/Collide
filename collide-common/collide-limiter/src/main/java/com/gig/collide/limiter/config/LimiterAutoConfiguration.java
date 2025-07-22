package com.gig.collide.limiter.config;

import com.gig.collide.limiter.RateLimiter;
import com.gig.collide.limiter.impl.SlidingWindowRateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 限流器自动配置
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass({StringRedisTemplate.class})
public class LimiterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RateLimiter rateLimiter(StringRedisTemplate redisTemplate) {
        log.info("初始化滑动窗口限流器组件");
        return new SlidingWindowRateLimiter(redisTemplate);
    }
} 