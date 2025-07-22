package com.gig.collide.auth;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 认证服务启动类
 * 参考 nft-turbo-auth 设计
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@SpringBootApplication(scanBasePackages = {"com.gig.collide.auth"})
@EnableDubbo
public class CollideAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideAuthApplication.class, args);
    }

}
