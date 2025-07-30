package com.gig.collide.order;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 订单服务启动类 - 缓存增强版
 * 基于order-simple.sql的单表设计
 * 对齐payment模块设计风格，集成缓存功能
 * 
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-16
 */
@Slf4j
@SpringBootApplication(scanBasePackages = "com.gig.collide.order")
@EnableDubbo
@EnableTransactionManagement
@MapperScan("com.gig.collide.order.infrastructure.mapper")
@EnableMethodCache(basePackages = "com.gig.collide.order")
@EnableCreateCacheAnnotation
public class CollideOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideOrderApplication.class, args);
        System.out.println("""
            ====================================
            🎉 Collide Order v2.0 模块启动成功！
            📦 缓存增强版订单服务已就绪
            🛒 无连表设计 - 商品信息冗余存储
            🔄 核心订单功能 - 创建/支付/取消/查询
            📊 状态管理 - pending/paid/shipped/completed/cancelled
            💳 支付集成 - alipay/wechat/balance
            ⚡ JetCache分布式缓存已启用
            🚀 支持跨模块Dubbo服务调用
            📈 订单统计与分析功能
            🔄 支持异步订单状态同步
            🛡️ 分布式锁防重复操作
            🌐 REST API - /api/v1/orders
            🎯 Dubbo RPC - OrderFacadeService v2.0.0
            🌐 端口: 9606
            ====================================
            """);
    }
}