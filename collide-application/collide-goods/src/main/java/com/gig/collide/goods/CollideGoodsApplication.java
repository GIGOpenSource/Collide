package com.gig.collide.goods;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 商品模块启动类 - 简洁版
 * 基于goods-simple.sql的单表设计，实现高性能商品管理服务
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@SpringBootApplication(scanBasePackages = "com.gig.collide.goods")
@EnableDubbo
@EnableTransactionManagement
@MapperScan("com.gig.collide.goods.infrastructure.mapper")
public class CollideGoodsApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(CollideGoodsApplication.class, args);
            log.info("===============================================");
            log.info("🚀 Collide Goods Service (简洁版) 启动成功！");
            log.info("📦 特性: 无连表设计 | 冗余存储 | 高性能");
            log.info("🛍️ 支持: 分类管理 | 商家管理 | 库存控制");
            log.info("⚡ 功能: CRUD | 搜索 | 统计 | 批量操作");
            log.info("📱 接口: Dubbo RPC + REST HTTP");
            log.info("🔧 Version: 2.0.0 (Simple Edition)");
            log.info("===============================================");
        } catch (Exception e) {
            log.error("❌ Collide Goods Service 启动失败！", e);
            System.exit(1);
        }
    }
}