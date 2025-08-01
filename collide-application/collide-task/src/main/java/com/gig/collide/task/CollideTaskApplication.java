package com.gig.collide.task;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 任务模块启动类 - 简洁版
 * 基于task-simple.sql的单表设计，实现核心任务功能
 * 集成JetCache分布式缓存，提供高性能任务服务
 *
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
@Slf4j
@SpringBootApplication(scanBasePackages = "com.gig.collide.task")
@EnableDubbo
@EnableTransactionManagement
@MapperScan("com.gig.collide.task.infrastructure.mapper")
@EnableMethodCache(basePackages = "com.gig.collide.task")
@EnableCreateCacheAnnotation
public class CollideTaskApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(CollideTaskApplication.class, args);
            log.info("========================================");
            log.info("🚀 Collide Task 模块启动成功!");
            log.info("🎯 每日任务服务已就绪");
            log.info("🏆 支持功能: 每日任务、周常任务、成就任务、金币奖励");
            log.info("🔧 技术栈: Spring Boot + Dubbo + MyBatis-Plus + JetCache");
            log.info("📊 数据库: 基于 task-simple.sql 单表设计");
            log.info("💰 奖励系统: 集成用户钱包，自动发放金币奖励");
            log.info("========================================");
        } catch (Exception e) {
            log.error("❌ Collide Task 模块启动失败: {}", e.getMessage(), e);
            System.exit(1);
        }
    }

    /**
     * 应用关闭钩子
     */
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("========================================");
            log.info("🛑 Collide Task 模块正在关闭...");
            log.info("🎯 每日任务服务已停止");
            log.info("🏆 奖励发放服务已停止");
            log.info("========================================");
        }));
    }
}