package com.gig.collide.search;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 搜索服务启动类
 * 包含分类、标签、搜索功能
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
@SpringBootApplication(scanBasePackages = {"com.gig.collide.search", "com.gig.collide.cache", "com.gig.collide.base"})
@EnableDubbo
@MapperScan("com.gig.collide.search.infrastructure.mapper")
public class SearchServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchServiceApplication.class, args);
        System.out.println("""
            ====================================
            🎉 Collide Search 模块启动成功！
            🔍 搜索、分类、标签服务已就绪
            🔗 端口: 9504
            ====================================
            """);
    }
} 