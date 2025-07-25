package com.gig.collide.cache.config;

import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.springframework.context.annotation.Configuration;

/**
 * 缓存配置类
 * 统一管理 Collide 项目的缓存配置
 *
 * @author Collide Team
 */
@Configuration
@EnableMethodCache(basePackages = "com.gig.collide")
public class CacheConfiguration {
}
