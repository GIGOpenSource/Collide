package com.gig.collide.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderServiceApplication {

    public static void main(String[] args) {
        // 启用标准化启动横幅
        System.setProperty("spring.banner.location", "classpath:banner.txt");
        
        SpringApplication app = new SpringApplication(OrderServiceApplication.class);
        app.run(args);
        
        // 标准化启动成功提示
        System.out.println("""
            
            ================================================================
            🚀 Collide Order Service Started Successfully!
            ================================================================
            📋 服务名称: collide-order
            🛒 服务描述: 订单管理微服务
            🌐 服务端口: 9503
            📡 RPC端口: 20883
            🔧 管理端口: /actuator
            ================================================================
            ✅ 集成组件: datasource | cache | rpc | lock | mq | job | web
            ================================================================
            
            """);
    }
} 