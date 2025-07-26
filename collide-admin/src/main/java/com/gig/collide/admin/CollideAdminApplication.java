package com.gig.collide.admin;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Collide 管理后台启动类
 * 
 * 功能特性：
 * - 独立的管理端应用，与用户端完全分离
 * - 集成Dubbo RPC调用各微服务
 * - 支持Redis缓存
 * - 集成Sa-Token权限认证
 * - 提供管理员专用的API接口
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@SpringBootApplication
@EnableDubbo
@EnableCaching
public class CollideAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideAdminApplication.class, args);
        System.out.println("""
            ========================================
            🛠️  Collide 管理后台启动成功！
            🔐 管理员专用后台系统已就绪
            📊 数据管理、权限控制、系统监控
            🌐 端口: 9999
            📖 文档地址: http://localhost:9999/swagger-ui.html
            ========================================
            """);
    }
}