package com.gig.collide.follow.infrastructure.config;

import com.gig.collide.follow.infrastructure.mq.FollowCacheService;
import com.gig.collide.follow.infrastructure.mq.FollowEventConsumer;
import com.gig.collide.follow.infrastructure.mq.FollowEventProducer;
import com.gig.collide.follow.infrastructure.mapper.FollowStatisticsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Follow模块配置类
 * @author GIG
 */
@Slf4j
@Configuration
public class FollowConfiguration {

    /**
     * 条件化配置Follow事件消费者
     */
    @Bean
    @ConditionalOnClass(name = {
        "com.gig.collide.stream.producer.StreamProducer",
        "com.alicp.jetcache.Cache"
    })
    @ConditionalOnBean({FollowStatisticsMapper.class, FollowCacheService.class})
    public FollowEventConsumer followEventConsumer(FollowStatisticsMapper followStatisticsMapper,
                                                   FollowCacheService followCacheService) {
        log.info("初始化Follow事件消费者，启用MQ和缓存功能");
        return new FollowEventConsumer(followStatisticsMapper, followCacheService);
    }
} 