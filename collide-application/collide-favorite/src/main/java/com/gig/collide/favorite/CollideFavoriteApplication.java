package com.gig.collide.favorite;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 收藏模块启动类 - 简洁版
 * 基于favorite-simple.sql的单表设计，实现高性能收藏管理服务
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@SpringBootApplication(scanBasePackages = "com.gig.collide.favorite")
@EnableDubbo
@EnableTransactionManagement
@MapperScan("com.gig.collide.favorite.infrastructure.mapper")
public class CollideFavoriteApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(CollideFavoriteApplication.class, args);
            log.info("===============================================");
            log.info("🚀 Collide Favorite Service (简洁版) 启动成功！");
            log.info("💖 特性: 无连表设计 | 冗余存储 | 多类型支持");
            log.info("📚 支持: 内容收藏 | 商品收藏 | 热门统计");
            log.info("⚡ 功能: CRUD | 搜索 | 统计 | 批量操作");
            log.info("📱 接口: Dubbo RPC + REST HTTP");
            log.info("🔧 Version: 2.0.0 (Simple Edition)");
            log.info("===============================================");
        } catch (Exception e) {
            log.error("❌ Collide Favorite Service 启动失败！", e);
            System.exit(1);
        }
    }
}