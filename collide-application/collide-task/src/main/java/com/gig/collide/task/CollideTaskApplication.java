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
 * 任务模块启动类 - 签到专用版
 * 基于task-simple-optimized.sql设计，专注于每日签到功能
 * 集成JetCache双级缓存，提供高性能签到服务
 *
 * @author GIG Team
 * @version 1.0.0 (签到专用版)
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
            log.info("===============================================");
            log.info("🚀 Collide Task Service (签到专用版) 启动成功！");
            log.info("✅ 特性: 每日签到 | 金币奖励 | 连续奖励");
            log.info("🔥 缓存: JetCache双级缓存 | 高性能查询");
            log.info("💰 奖励: 基础10金币 | 连续7天翻倍 | 自动发放");
            log.info("🎯 功能: 签到打卡 | 历史查询 | 统计分析");
            log.info("⚡ 接口: Dubbo RPC + REST HTTP");
            log.info("📱 支持: 移动端 | Web端 | 管理后台");
            log.info("🔧 Version: 1.0.0 (Daily Check-in)");
            log.info("===============================================");
        } catch (Exception e) {
            log.error("❌ Collide Task Service 启动失败！", e);
            System.exit(1);
        }
    }

    /**
     * 应用关闭钩子
     */
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("===============================================");
            log.info("🛑 Collide Task Service 正在关闭...");
            log.info("✅ 签到服务已停止");
            log.info("💰 奖励发放服务已停止");
            log.info("🔥 缓存服务已清理");
            log.info("🎯 感谢使用 Collide 签到系统！");
            log.info("===============================================");
        }));
    }
}