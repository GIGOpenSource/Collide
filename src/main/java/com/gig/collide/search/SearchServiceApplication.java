package com.gig.collide.search;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 搜索服务启动类
 * 使用标准化架构配置
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
@SpringBootApplication(scanBasePackages = {
    "com.gig.collide.search",
    "com.gig.collide.cache",
    "com.gig.collide.base",
    "com.gig.collide.web",
    "com.gig.collide.config",
    "com.gig.collide.datasource",
    "com.gig.collide.lock",
    "com.gig.collide.limiter"
})
@EnableDubbo
@EnableCaching
public class SearchServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchServiceApplication.class, args);
    }
} 