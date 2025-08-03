package com.gig.collide.social;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Social模块启动类 - DDD架构 + JetCache缓存
 * 
 * @author GIG Team
 * @version 2.0.0 (DDD + 缓存增强版)
 * @since 2024-01-16
 */
@SpringBootApplication
@EnableDubbo
@MapperScan("com.gig.collide.social.infrastructure.mapper")
@EnableTransactionManagement
@EnableMethodCache(basePackages = "com.gig.collide.social")
@EnableCreateCacheAnnotation
@EnableScheduling
@EnableAsync
public class CollideSocialApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideSocialApplication.class, args);
        System.out.println("""
            ====================================
            🎉 Collide Social 模块启动成功！
            📱 社交内容服务已就绪
            💬 互动功能(点赞收藏分享评论)启用
            📊 统计服务已启用
            🚀 JetCache双级缓存已启用
            ⚡ DDD架构 + 高性能查询
            🔍 端口: 8083
            ====================================
            """);
    }
}