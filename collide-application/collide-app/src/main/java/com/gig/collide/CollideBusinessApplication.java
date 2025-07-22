package com.gig.collide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Collide 业务聚合启动类
 * 参考 nft-turbo-app 的 NfTurboBusinessApplication 设计
 * 
 * 作为所有业务模块的统一启动入口，包括：
 * - collide-users: 用户服务
 * - TODO: 后续添加其他业务模块
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@SpringBootApplication(scanBasePackages = "com.gig.collide")
public class CollideBusinessApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideBusinessApplication.class, args);
        System.out.println("========== Collide Business Application Started ==========");
    }
} 