package com.gig.collide.tag;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 标签服务启动类 - 简洁版
 * 标签管理与用户兴趣功能
 * 基于简洁版SQL设计（t_tag, t_user_interest_tag, t_content_tag）
 *
 * @author GIG Team
 * @version 2.0.0
 * @since 2024-12-19
 */
@SpringBootApplication
@EnableDubbo
public class CollideTagApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideTagApplication.class, args);
        System.out.println("""
            ====================================
            🎉 Collide Tag v2.0 模块启动成功！
            🏷️ 简洁版标签管理服务已就绪
            🔗 支持标签分类和用户兴趣
            📊 基于使用次数的热门标签
            🔍 端口: 9506
            ====================================
            """);
    }
} 