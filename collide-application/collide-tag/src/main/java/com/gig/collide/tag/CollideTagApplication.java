package com.gig.collide.tag;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 标签服务启动类 - 缓存增强版
 * 标签管理与用户兴趣功能，集成JetCache双级缓存
 * 基于简洁版SQL设计（t_tag, t_user_interest_tag, t_content_tag）
 *
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-12-19
 */
@SpringBootApplication(scanBasePackages = "com.gig.collide.tag")
@EnableDubbo
@EnableMethodCache(basePackages = "com.gig.collide.tag")
@EnableCreateCacheAnnotation
public class CollideTagApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideTagApplication.class, args);
        System.out.println("""
            ====================================
            🎉 Collide Tag v2.0 模块启动成功！
            🏷️ 缓存增强版标签管理服务已就绪
            🔗 支持标签分类和用户兴趣
            📊 基于使用次数的热门标签
            🚀 JetCache双级缓存已启用
            ⚡ 高性能标签查询服务
            🔍 端口: 9506
            ====================================
            """);
    }
} 