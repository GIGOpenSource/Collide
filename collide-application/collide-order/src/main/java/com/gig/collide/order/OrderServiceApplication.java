package com.gig.collide.order;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Collide Order Service Application
 * 
 * 订单服务启动类 - 标准化微服务启动配置
 * 
 * 集成的标准化组件：
 * - collide-datasource: 数据源和MyBatis-Plus配置
 * - collide-cache: Redis缓存和本地缓存配置  
 * - collide-rpc: Dubbo RPC配置
 * - collide-lock: 分布式锁配置
 * - collide-mq: 消息队列配置
 * - collide-job: 任务调度配置
 * - collide-web: Web框架配置
 * - collide-prometheus: 监控配置
 * 
 * @author Collide Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@SpringBootApplication(scanBasePackages = {
    "com.gig.collide.order",           // 订单业务包
    "com.gig.collide.common",          // 公共组件包
    "com.gig.collide.cache",           // 缓存组件包
    "com.gig.collide.rpc",             // RPC组件包
    "com.gig.collide.lock",            // 分布式锁组件包
    "com.gig.collide.web"              // Web组件包
})
@EnableDubbo(scanBasePackages = "com.gig.collide.order")
@EnableCaching
@EnableScheduling  
@EnableTransactionManagement
@MapperScan("com.gig.collide.order.infrastructure.mapper")
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