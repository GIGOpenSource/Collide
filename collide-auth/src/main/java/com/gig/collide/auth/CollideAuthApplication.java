package com.gig.collide.auth;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 认证服务启动类
 * 基于Code项目设计哲学，实现简化认证系统
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-16
 */
@SpringBootApplication(scanBasePackages = {"com.gig.collide.auth"})
@EnableDubbo
public class CollideAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideAuthApplication.class, args);
        System.out.println("""
            ====================================
            🎉 Collide Auth v2.0 启动成功！
            ✨ 简化认证系统已就绪
            🚀 支持用户名密码登录注册
            🎫 支持邀请码功能
            🔄 支持登录时自动注册
            🔗 端口: 9502
            ====================================
            """);
    }
}
