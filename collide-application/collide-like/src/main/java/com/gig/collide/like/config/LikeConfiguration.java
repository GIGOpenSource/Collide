package com.gig.collide.like.config;

import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 点赞模块配置类 - 标准化设计
 * 统一管理点赞模块的各种配置
 * 
 * @author Collide Team
 * @since 2.0.0
 */
@Configuration
@EnableMethodCache(basePackages = "com.gig.collide.like")
@EnableTransactionManagement
public class LikeConfiguration {
    
    // 配置类可以在这里定义Bean或其他配置
    // 目前使用注解驱动，保持简洁
    
} 