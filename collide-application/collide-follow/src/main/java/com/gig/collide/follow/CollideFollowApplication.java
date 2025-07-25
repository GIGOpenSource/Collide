package com.gig.collide.follow;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Collide Follow 模块启动类
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@SpringBootApplication(scanBasePackages = {"com.gig.collide.follow", "com.gig.collide.cache"})
@EnableDubbo
@MapperScan("com.gig.collide.follow.infrastructure.mapper")
public class CollideFollowApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideFollowApplication.class, args);
        System.out.println("""
            ====================================
            🎉 Collide Follow 模块启动成功！
            📝 关注服务已就绪
            🔗 端口: 9502
            ====================================
            """);
    }
} 