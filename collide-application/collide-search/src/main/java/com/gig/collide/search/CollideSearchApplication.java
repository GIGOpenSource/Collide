package com.gig.collide.search;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 搜索服务启动类 - 简洁版
 * 搜索历史与热门搜索功能
 * 基于简洁版SQL设计（t_search_history, t_hot_search）
 *
 * @author GIG Team
 * @version 2.0.0
 * @since 2024-12-19
 */
@SpringBootApplication(scanBasePackages = "com.gig.collide.search")
@EnableDubbo
@EnableMethodCache(basePackages = "com.gig.collide.search")
@EnableCreateCacheAnnotation
public class CollideSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideSearchApplication.class, args);
        System.out.println("""
            ====================================
            🎉 Collide Search v2.0 模块启动成功！
            🔍 简洁版搜索服务已就绪
            📈 支持搜索历史和热门搜索
            🎯 智能搜索建议与用户偏好
            ⚡ JetCache分布式缓存已启用
            🔄 支持跨模块Dubbo服务调用
            🏷️ 支持Tag混合搜索功能
            🔍 端口: 9604
            ====================================
            """);
    }
} 