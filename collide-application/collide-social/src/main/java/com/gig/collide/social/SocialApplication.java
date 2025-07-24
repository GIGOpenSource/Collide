package com.gig.collide.social;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 社交服务启动类
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@SpringBootApplication(scanBasePackages = "com.gig.collide.social")
@EnableDubbo
@EnableDiscoveryClient
public class SocialApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialApplication.class, args);
    }
} 