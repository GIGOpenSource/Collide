package com.gig.collide.category;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 分类服务启动类
 * 内容分类管理功能
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
@SpringBootApplication
public class CategoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CategoryServiceApplication.class, args);
        System.out.println("""
            ====================================
            🎉 Collide Category 模块启动成功！
            📂 分类管理服务已就绪
            🔗 端口: 9505
            ====================================
            """);
    }
} 