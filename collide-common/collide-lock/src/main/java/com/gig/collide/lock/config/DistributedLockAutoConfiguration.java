package com.gig.collide.lock.config;

import com.gig.collide.lock.DistributedLock;
import com.gig.collide.lock.impl.RedisDistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 分布式锁自动配置
 * 参考 nft-turbo-lock 设计
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass({StringRedisTemplate.class})
public class DistributedLockAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DistributedLock distributedLock(StringRedisTemplate redisTemplate) {
        log.info("初始化 Redis 分布式锁组件");
        return new RedisDistributedLock(redisTemplate);
    }
} 