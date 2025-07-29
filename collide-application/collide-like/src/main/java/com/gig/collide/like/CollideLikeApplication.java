package com.gig.collide.like;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 点赞模块启动类 - 简洁版
 * 基于like-simple.sql的单表设计，实现高性能点赞服务
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@SpringBootApplication(scanBasePackages = "com.gig.collide.like")
@EnableDubbo
@EnableTransactionManagement
@MapperScan("com.gig.collide.like.infrastructure.mapper")
public class CollideLikeApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(CollideLikeApplication.class, args);
            log.info("===============================================");
            log.info("🚀 Collide Like Service (简洁版) 启动成功！");
            log.info("📊 特性: 无连表设计 | 冗余存储 | 高性能");
            log.info("🎯 支持: CONTENT、COMMENT、DYNAMIC 三种点赞类型");
            log.info("⚡ 架构: 单表 + 状态管理 + 批量操作");
            log.info("📱 接口: Dubbo RPC + REST HTTP");
            log.info("🔧 Version: 2.0.0 (Simple Edition)");
            log.info("===============================================");
        } catch (Exception e) {
            log.error("❌ Collide Like Service 启动失败！", e);
            System.exit(1);
        }
    }
}