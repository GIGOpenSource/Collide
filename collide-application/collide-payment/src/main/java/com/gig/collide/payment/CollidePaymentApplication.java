package com.gig.collide.payment;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 支付服务启动类 - 缓存增强版
 * 统一支付处理与状态管理
 * 基于简洁版SQL设计（t_payment）
 * 对齐search模块设计风格，集成缓存功能
 *
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-12-19
 */
@SpringBootApplication(scanBasePackages = "com.gig.collide.payment")
@EnableDubbo
@EnableMethodCache(basePackages = "com.gig.collide.payment")
@EnableCreateCacheAnnotation
public class CollidePaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollidePaymentApplication.class, args);
        System.out.println("""
            ====================================
            🎉 Collide Payment v2.0 模块启动成功！
            💰 缓存增强版支付服务已就绪
            🔒 支持多种支付方式（支付宝、微信、余额）
            📊 统一状态管理与回调处理
            ⚡ JetCache分布式缓存已启用
            🚀 支持跨模块Dubbo服务调用
            💳 智能支付路由与风控
            📈 支付统计与分析功能
            🔄 支持异步支付回调处理
            🛡️ 分布式锁防重复支付
            🌐 端口: 9605
            ====================================
            """);
    }
} 