package com.gig.collide.social;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 社交动态服务启动类 - 缓存增强版
 * 社交动态管理与互动功能，对齐content模块设计风格
 * 基于简洁版SQL设计（t_social_dynamic）
 * 
 * 核心功能：
 * - 创建动态
 * - 查询最新动态列表
 * - 根据userId查询动态
 * - 点赞评论记录
 * - 删除动态
 * - 更新动态内容（仅内容字段）
 *
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-16
 */
@SpringBootApplication(scanBasePackages = "com.gig.collide.social")
@EnableDubbo
@EnableMethodCache(basePackages = "com.gig.collide.social")
@EnableCreateCacheAnnotation
public class CollideSocialApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideSocialApplication.class, args);
        System.out.println("""
            =========================================
            🎉 Collide Social v2.0 模块启动成功！
            📱 缓存增强版社交动态服务已就绪
            💬 支持文本、图片、视频、分享
            📊 冗余统计避免复杂聚合查询
            🚀 JetCache缓存已启用
            🔄 对齐content模块设计风格
            🔍 端口: 9603
            =========================================
            """);
    }
} 