package com.gig.collide.cache.config;

import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.springframework.context.annotation.Configuration;

/**
 * 缓存配置
 *
 * @author GIGOpenSource
 */
@Configuration
@EnableMethodCache(basePackages = "com.md.nft.turbo")
public class CacheConfiguration {
}
