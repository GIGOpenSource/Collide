package com.gig.collide.users;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 用户模块启动类
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@SpringBootApplication(scanBasePackages = "com.gig.collide.users")
@EnableDubbo
public class CollideUsersApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideUsersApplication.class, args);
    }
} 