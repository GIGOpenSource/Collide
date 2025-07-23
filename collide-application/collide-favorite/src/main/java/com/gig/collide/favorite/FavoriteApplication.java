package com.gig.collide.favorite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Collide 收藏服务启动类
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@SpringBootApplication(scanBasePackages = {
    "com.gig.collide.favorite",    // 收藏服务
    "com.gig.collide.base",        // 基础组件
    "com.gig.collide.cache",       // 缓存组件
    "com.gig.collide.datasource",  // 数据源组件
    "com.gig.collide.rpc",         // RPC组件
    "com.gig.collide.web"          // Web组件
})
@EnableDiscoveryClient
public class FavoriteApplication {

    public static void main(String[] args) {
        SpringApplication.run(FavoriteApplication.class, args);
        System.out.println("========== Collide Favorite Service Started ==========");
    }
} 