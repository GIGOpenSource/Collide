package com.gig.collide.social;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 社交模块启动类
 * 
 * @author Collide Team
 * @version 1.0.0
 * @since 2024-12-19
 */
@SpringBootApplication(scanBasePackages = {"com.gig.collide.social", "com.gig.collide.base", "com.gig.collide.web"})
@EnableDubbo
public class CollideSocialApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideSocialApplication.class, args);
    }
} 