package com.gig.collide.message;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 私信消息模块启动类 - 简洁版
 * 基于message-simple.sql的单表设计，实现核心私信功能
 * 集成JetCache分布式缓存，提供高性能消息服务
 *
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
@Slf4j
@SpringBootApplication(scanBasePackages = "com.gig.collide.message")
@EnableDubbo
@EnableTransactionManagement
@MapperScan("com.gig.collide.message.infrastructure.mapper")
@EnableMethodCache(basePackages = "com.gig.collide.message")
@EnableCreateCacheAnnotation
public class CollideMessageApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(CollideMessageApplication.class, args);
            log.info("========================================");
            log.info("🚀 Collide Message 模块启动成功!");
            log.info("📱 私信消息服务已就绪");
            log.info("💬 支持功能: 私信、留言板、消息会话管理");
            log.info("🔧 技术栈: Spring Boot + Dubbo + MyBatis-Plus + JetCache");
            log.info("📊 数据库: 基于 message-simple.sql 单表设计");
            log.info("========================================");
        } catch (Exception e) {
            log.error("❌ Collide Message 模块启动失败: {}", e.getMessage(), e);
            System.exit(1);
        }
    }

    /**
     * 应用关闭钩子
     */
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("========================================");
            log.info("🛑 Collide Message 模块正在关闭...");
            log.info("📱 私信消息服务已停止");
            log.info("========================================");
        }));
    }
}