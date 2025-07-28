package com.gig.collide.tag;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 标签服务启动类
 * 标签管理与用户兴趣功能
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
@SpringBootApplication
@EnableDubbo
public class CollideTagApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideTagApplication.class, args);
        System.out.println("""
            ====================================
            🎉 Collide Tag 模块启动成功！
            🏷️ 标签管理服务已就绪
            🔗 端口: 9506
            ====================================
            """);
    }
} 