package com.gig.collide.order;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 订单服务启动类 - 简洁版
 * 基于order-simple.sql的单表设计
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@SpringBootApplication(scanBasePackages = "com.gig.collide.order")
@EnableDubbo
@EnableTransactionManagement
@MapperScan("com.gig.collide.order.infrastructure.mapper")
public class CollideOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideOrderApplication.class, args);
        
        log.info("=== Collide Order Service Started Successfully! ===");
        log.info("📦 订单服务 v2.0.0 (简洁版) 启动成功!");
        log.info("🚀 核心特性:");
        log.info("   • 无连表设计 - 商品信息冗余存储");
        log.info("   • 核心订单功能 - 创建/支付/取消/查询");
        log.info("   • 状态管理 - pending/paid/shipped/completed/cancelled");
        log.info("   • 支付集成 - alipay/wechat/balance");
        log.info("   • REST API - /api/v1/orders");
        log.info("   • Dubbo RPC - OrderFacadeService v2.0.0");
        log.info("=== Ready to serve! ===");
    }
}