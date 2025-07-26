package com.gig.collide.mq.config;

import com.gig.collide.mq.producer.StreamProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MQ配置类
 * 统一管理 Collide 项目的消息队列配置
 *
 * @author Collide Team
 */
@Configuration
public class StreamConfiguration {
    
    @Bean
    public StreamProducer streamProducer() {
        return new StreamProducer();
    }
} 