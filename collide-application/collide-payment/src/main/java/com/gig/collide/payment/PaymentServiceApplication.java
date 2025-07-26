package com.gig.collide.payment;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Collide 支付服务启动类
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@SpringBootApplication
@EnableDubbo
@MapperScan("com.gig.collide.payment.infrastructure.mapper")
public class PaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
        System.out.println("""
            ====================================
            💳 Collide 支付服务启动成功！
            💰 支付处理微服务已就绪
            🔗 端口: 9503
            ====================================
            """);
    }
} 