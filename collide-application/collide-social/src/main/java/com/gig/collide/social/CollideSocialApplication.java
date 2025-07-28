package com.gig.collide.social;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 社交动态服务启动类 - 简洁版
 * 社交动态管理与互动功能
 * 基于简洁版SQL设计（t_social_dynamic）
 *
 * @author GIG Team
 * @version 2.0.0
 * @since 2024-12-19
 */
@SpringBootApplication
@EnableDubbo
public class CollideSocialApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideSocialApplication.class, args);
        System.out.println("""
            ====================================
            🎉 Collide Social v2.0 模块启动成功！
            📱 简洁版社交动态服务已就绪
            💬 支持文本、图片、视频、分享
            📊 冗余统计避免复杂聚合查询
            🔍 端口: 9507
            ====================================
            """);
    }
} 