package com.gig.collide.follow;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 关注模块启动类
 * @author GIG
 */
@SpringBootApplication(scanBasePackages = "com.gig.collide.follow")
@EnableDubbo
public class CollideFollowApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideFollowApplication.class, args);
    }
} 