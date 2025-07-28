package com.gig.collide.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.context.annotation.Bean;

/**
 * 网关服务启动类 - 简洁版
 * 基于简洁版用户API的统一鉴权网关
 *
 * @author GIG Team
 * @version 2.0.0
 */
@SpringBootApplication
public class CollideGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideGatewayApplication.class, args);
        System.out.println("""
            ====================================
            🎉 Collide Gateway v2.0 启动成功！
            ✨ 简洁版统一鉴权网关已就绪
            🔐 支持基于角色的权限控制
            🚀 集成简洁版用户API 2.0
            🔍 统一路由和权限管理
            🏃 端口: 9500
            ====================================
            """);
    }
}
