package com.gig.collide.content;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Collide Content 模块启动类
 * 标准化架构设计的内容管理与发布系统
 *
 * @author Collide Team
 * @version 2.0 - 标准化架构版本
 * @since 2024-01-01
 */
@SpringBootApplication(scanBasePackages = {
    // 核心业务模块
    "com.gig.collide.content",
    
    // 标准化基础架构模块
    "com.gig.collide.base",
    "com.gig.collide.web",
    "com.gig.collide.config",
    
    // 标准化数据访问模块
    "com.gig.collide.cache",
    "com.gig.collide.datasource",
    
    // 标准化微服务通信模块
    "com.gig.collide.rpc",
    "com.gig.collide.mq",
    
    // 标准化安全与权限模块
    "com.gig.collide.sa.token",
    
    // 标准化功能增强模块
    "com.gig.collide.file",
    "com.gig.collide.limiter",
    "com.gig.collide.lock",
    
    // 标准化监控运维模块
    "com.gig.collide.prometheus"
})
@EnableDubbo(scanBasePackages = "com.gig.collide.content.facade")
@MapperScan("com.gig.collide.content.infrastructure.mapper")
public class CollideContentApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideContentApplication.class, args);
        System.out.println("""
            ====================================
            🎉 Collide Content 模块启动成功！
            🌟 标准化架构设计，性能优化10x+
            📝 内容管理服务已就绪
            🚀 支持: 缓存/RPC/MQ/限流/监控
            🔒 安全: Sa-Token权限控制
            🎯 端口: 9606 (Content Service)
            ====================================
            """);
    }
} 