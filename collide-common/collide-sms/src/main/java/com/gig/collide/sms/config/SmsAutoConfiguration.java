package com.gig.collide.sms.config;

import com.gig.collide.sms.SmsService;
import com.gig.collide.sms.impl.MockSmsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * 短信服务自动配置
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@AutoConfiguration
public class SmsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "collide.sms.provider", havingValue = "mock", matchIfMissing = true)
    public SmsService mockSmsService() {
        log.info("初始化 Mock 短信服务组件");
        return new MockSmsServiceImpl();
    }
} 