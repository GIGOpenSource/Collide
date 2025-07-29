package com.gig.collide.follow;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 关注模块启动类 - 简洁版
 * 基于follow-simple.sql的单表设计，实现高性能关注管理服务
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@SpringBootApplication(scanBasePackages = "com.gig.collide.follow")
@EnableDubbo
@EnableTransactionManagement
@MapperScan("com.gig.collide.follow.infrastructure.mapper")
public class CollideFollowApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(CollideFollowApplication.class, args);
            log.info("===============================================");
            log.info("🚀 Collide Follow Service (简洁版) 启动成功！");
            log.info("👥 特性: 无连表设计 | 冗余存储 | 双向关注");
            log.info("💫 支持: 关注管理 | 粉丝统计 | 互关检测");
            log.info("⚡ 功能: CRUD | 搜索 | 统计 | 批量操作");
            log.info("📱 接口: Dubbo RPC + REST HTTP");
            log.info("🔧 Version: 2.0.0 (Simple Edition)");
            log.info("===============================================");
        } catch (Exception e) {
            log.error("❌ Collide Follow Service 启动失败！", e);
            System.exit(1);
        }
    }
} 