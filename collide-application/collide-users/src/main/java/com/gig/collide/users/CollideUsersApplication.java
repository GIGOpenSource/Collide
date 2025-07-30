package com.gig.collide.users;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;

/**
 * 用户模块启动类 - 简洁版
 * 基于users-simple.sql设计，集成缓存和钱包功能
 * 
 * @author Collide Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-01
 */
@Slf4j
@SpringBootApplication(scanBasePackages = "com.gig.collide.users")
@EnableDubbo
@EnableTransactionManagement
@EnableMethodCache(basePackages = "com.gig.collide.users")
@EnableCreateCacheAnnotation
@MapperScan("com.gig.collide.users.infrastructure.mapper")
public class CollideUsersApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(CollideUsersApplication.class, args);
            log.info("===============================================");
            log.info("🚀 Collide Users Service (缓存增强版) 启动成功！");
            log.info("👤 特性: 用户管理 | 钱包系统 | 登录认证");
            log.info("🔥 缓存: JetCache双级缓存 | 高性能查询");
            log.info("💰 钱包: 充值/提现 | 冻结/解冻 | 余额检查");
            log.info("🎯 支持: 注册/登录 | 信息更新 | 状态管理");
            log.info("⚡ 功能: CRUD | 分页查询 | 个人信息增强");
            log.info("📱 接口: Dubbo RPC + REST HTTP");
            log.info("🔧 Version: 2.0.0 (Cache Enhanced)");
            log.info("===============================================");
        } catch (Exception e) {
            log.error("❌ Collide Users Service 启动失败！", e);
            System.exit(1);
        }
    }
} 