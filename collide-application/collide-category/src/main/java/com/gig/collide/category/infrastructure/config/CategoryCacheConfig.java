package com.gig.collide.category.infrastructure.config;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.springframework.context.annotation.Configuration;

/**
 * 分类模块缓存配置
 * 
 * @author Collide
 * @version 1.0.0
 * @since 2024-01-01
 */
@Configuration
@EnableCreateCacheAnnotation
@EnableMethodCache(basePackages = "com.gig.collide.category")
public class CategoryCacheConfig {
    
    // JetCache 配置说明：
    // 1. @EnableCreateCacheAnnotation: 启用 @CreateCache 注解
    // 2. @EnableMethodCache: 启用方法缓存注解，指定扫描包路径
    // 3. 缓存策略：
    //    - 本地缓存：Caffeine，TTL 5分钟
    //    - 远程缓存：Redis，TTL 30分钟
    //    - 缓存更新：采用 Cache-Aside 模式
} 