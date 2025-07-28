package com.gig.collide.tag;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 标签模块启动类
 * 
 * @author GIG Team
 * @version 1.0.0
 * @since 2024-12-19
 */
@SpringBootApplication(scanBasePackages = {"com.gig.collide.tag", "com.gig.collide.base", "com.gig.collide.web"})
@EnableDubbo
public class CollideTagApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideTagApplication.class, args);
    }
} 