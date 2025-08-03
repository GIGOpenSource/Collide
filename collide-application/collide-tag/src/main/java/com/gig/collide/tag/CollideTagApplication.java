package com.gig.collide.tag;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 标签服务启动类
 * 标签管理、用户关注标签、内容标签、协同过滤推荐功能
 * 基于新设计的SQL架构（t_tag, t_user_tag_follow, t_content_tag）
 *
 * @author GIG Team
 * @version 1.0.0
 * @since 2024-12-19
 */
@SpringBootApplication(scanBasePackages = "com.gig.collide.tag")
@EnableDubbo
@EnableMethodCache(basePackages = "com.gig.collide.tag")
@EnableCreateCacheAnnotation
@EnableTransactionManagement
@EnableScheduling
public class CollideTagApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideTagApplication.class, args);
        System.out.println("""
            ====================================
            🎉 Collide Tag 模块启动成功！
            🏷️ 标签管理服务已就绪
            👥 用户关注标签功能启用
            📝 内容标签功能启用
            🤖 协同过滤推荐算法启用
            🚀 JetCache双级缓存已启用
            ⚡ 高性能标签查询与推荐
            🔍 端口: 9506
            ====================================
            """);
    }
} 