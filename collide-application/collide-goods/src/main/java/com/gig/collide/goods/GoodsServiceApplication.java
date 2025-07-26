package com.gig.collide.goods;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Collide 商品服务启动类
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@SpringBootApplication
@EnableDubbo
@MapperScan("com.gig.collide.goods.infrastructure.mapper")
public class GoodsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoodsServiceApplication.class, args);
        System.out.println("""
            ====================================
            🛍️  Collide 商品服务启动成功！
            📦 商品管理微服务已就绪
            🔗 端口: 9501
            ====================================
            """);
    }
} 