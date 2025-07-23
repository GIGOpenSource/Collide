package com.gig.collide.like;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 点赞服务启动类
 * 
 * @author Collide
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = {
    "com.gig.collide.like",
    "com.gig.collide.base",
    "com.gig.collide.cache",
    "com.gig.collide.datasource",
    "com.gig.collide.rpc",
    "com.gig.collide.web"
})
@EnableDiscoveryClient
@MapperScan("com.gig.collide.like.infrastructure.mapper")
public class LikeApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(LikeApplication.class, args);
    }
} 