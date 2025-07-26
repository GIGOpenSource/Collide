package com.gig.collide.order;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Collide 订单服务启动类
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@SpringBootApplication
@EnableDubbo
@MapperScan("com.gig.collide.order.infrastructure.mapper")
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
        System.out.println("""
            ====================================
            📋 Collide 订单服务启动成功！
            🛒 订单管理微服务已就绪
            🔗 端口: 9502
            ====================================
            """);
    }
} 