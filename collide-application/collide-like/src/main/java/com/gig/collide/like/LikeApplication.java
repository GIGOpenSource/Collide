package com.gig.collide.like;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * 点赞服务启动类 - 标准化设计
 * 
 * 基于 Code 项目标准化思想构建：
 * - 集成 collide-cache (JetCache)
 * - 集成 collide-rpc (Dubbo)
 * - 集成 collide-datasource (MyBatis Plus)
 * - 去连表化高性能设计
 * - 统一响应格式
 * 
 * @author Collide Team
 * @since 2.0.0
 */
@SpringBootApplication
public class LikeApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(LikeApplication.class, args);
    }
} 