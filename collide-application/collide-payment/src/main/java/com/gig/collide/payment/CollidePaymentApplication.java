package com.gig.collide.payment;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 支付服务启动类 - 简洁版
 * 统一支付处理与状态管理
 * 基于简洁版SQL设计（t_payment）
 *
 * @author GIG Team
 * @version 2.0.0
 * @since 2024-12-19
 */
@SpringBootApplication
@EnableDubbo
public class CollidePaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollidePaymentApplication.class, args);
        System.out.println("""
            ====================================
            🎉 Collide Payment v2.0 模块启动成功！
            💰 简洁版支付服务已就绪
            🔒 支持多种支付方式
            📊 统一状态管理与回调处理
            🌐 端口: 9509
            ====================================
            """);
    }
} 