package com.gig.collide.search;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 搜索服务启动类
 * 全文搜索和智能推荐功能
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@SpringBootApplication
@EnableDubbo
public class SearchServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchServiceApplication.class, args);
        System.out.println("""
            ====================================
            🎉 Collide Search 模块启动成功！
            🔍 搜索服务已就绪
            🔗 端口: 9505
            ====================================
            """);
    }
} 