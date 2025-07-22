package com.gig.collide.content;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Collide Content 模块启动类
 * 内容管理与发布系统
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@SpringBootApplication(scanBasePackages = "com.gig.collide")
@EnableDubbo
@MapperScan("com.gig.collide.content.infrastructure.mapper")
public class CollideContentApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideContentApplication.class, args);
        System.out.println("""
            ====================================
            🎉 Collide Content 模块启动成功！
            📝 内容管理服务已就绪
            🔗 端口: 9503
            ====================================
            """);
    }
} 