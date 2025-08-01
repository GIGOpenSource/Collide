package com.gig.collide.ads;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 广告模块启动类 - 简洁版
 * 基于ads-simple.sql的广告投放管理系统
 * 集成JetCache分布式缓存，提供高性能广告服务
 *
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
@Slf4j
@SpringBootApplication(scanBasePackages = "com.gig.collide.ads")
@EnableDubbo
@EnableTransactionManagement
@MapperScan("com.gig.collide.ads.infrastructure.mapper")
@EnableMethodCache(basePackages = "com.gig.collide.ads")
@EnableCreateCacheAnnotation
public class CollideAdsApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(CollideAdsApplication.class, args);
            log.info("========================================");
            log.info("🚀 Collide Ads 模块启动成功!");
            log.info("📺 广告投放管理服务已就绪");
            log.info("🎯 支持功能: 广告模板、广告位、投放管理、效果统计");
            log.info("📊 统计功能: 实时展示点击统计、收益分析、排行榜");
            log.info("🛡️ 安全功能: 异常检测、防刷量、用户行为分析");
            log.info("🤖 智能功能: 广告推荐、投放策略优化、效果预测");
            log.info("🔧 技术栈: Spring Boot + Dubbo + MyBatis-Plus + JetCache");
            log.info("📊 数据库: 基于 ads-simple.sql 单表设计");
            log.info("💰 收益系统: 集成点击展示计费，自动收益统计");
            log.info("========================================");
        } catch (Exception e) {
            log.error("❌ Collide Ads 模块启动失败: {}", e.getMessage(), e);
            System.exit(1);
        }
    }

    /**
     * 应用关闭钩子
     */
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("========================================");
            log.info("🛑 Collide Ads 模块正在关闭...");
            log.info("📺 广告投放服务已停止");
            log.info("📊 统计分析服务已停止");
            log.info("🛡️ 异常检测服务已停止");
            log.info("🤖 推荐系统已停止");
            log.info("========================================");
        }));
    }
}