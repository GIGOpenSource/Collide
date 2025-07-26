package com.gig.collide.favorite;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Collide 收藏服务启动类
 * 
 * 基于标准化架构的微服务模块
 * - 集成Dubbo RPC通信
 * - 集成Nacos服务发现
 * - 集成标准化组件
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@SpringBootApplication(scanBasePackages = {
    // ===================== 业务模块 =====================
    "com.gig.collide.favorite",        // 收藏服务核心
    
    // ===================== Collide 标准化基础模块 =====================
    "com.gig.collide.base",            // 基础架构组件
    "com.gig.collide.web",             // Web标准化组件
    "com.gig.collide.config",          // 配置管理组件
    
    // ===================== Collide 数据访问标准化模块 =====================
    "com.gig.collide.datasource",      // 数据源标准化组件
    "com.gig.collide.cache",           // 缓存标准化组件
    
    // ===================== Collide 微服务通信标准化模块 =====================
    "com.gig.collide.rpc",             // RPC标准化组件(Dubbo)
    
    // ===================== Collide 功能增强标准化模块 =====================
    "com.gig.collide.lock",            // 分布式锁组件
    "com.gig.collide.limiter",         // 限流组件
    
    // ===================== Collide 监控运维标准化模块 =====================
    "com.gig.collide.prometheus",      // 监控指标组件
    "com.gig.collide.skywalking"       // 链路追踪组件
})
@EnableDubbo(scanBasePackages = {
    "com.gig.collide.favorite.facade", // 扫描Dubbo服务实现
    "com.gig.collide.rpc"              // 扫描RPC标准化组件
})
@EnableDiscoveryClient
@MapperScan("com.gig.collide.favorite.infrastructure.mapper")
public class FavoriteApplication {

    public static void main(String[] args) {
        SpringApplication.run(FavoriteApplication.class, args);
        System.out.println("==========================================");
        System.out.println("🚀 Collide Favorite Service Started");
        System.out.println("📦 Service: collide-favorite");
        System.out.println("🏗️  Architecture: Standardized Microservice");
        System.out.println("🔗 RPC: Dubbo + Nacos");
        System.out.println("💾 Cache: Redis + Caffeine");
        System.out.println("📊 Monitor: Prometheus + SkyWalking");
        System.out.println("==========================================");
    }
} 