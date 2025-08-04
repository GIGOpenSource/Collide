package com.gig.collide.content;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.alicp.jetcache.anno.config.EnableMethodCache;

/**
 * 内容模块启动类 - 简洁版
 * 基于content-simple.sql的双表设计，实现高性能内容管理服务
 * 支持多种内容类型和评分功能
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@SpringBootApplication(scanBasePackages = "com.gig.collide.content")
@EnableDubbo
@EnableTransactionManagement
@EnableMethodCache(basePackages = "com.gig.collide.content")
@MapperScan("com.gig.collide.content.infrastructure.mapper")
public class CollideContentApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(CollideContentApplication.class, args);
            log.info("===============================================");
            log.info("🚀 Collide Content Service (简洁版) 启动成功！");
            log.info("📚 特性: 双表设计 | 评分系统 | 章节管理");
            log.info("🎯 支持: 小说/漫画/视频/文章/音频");
            log.info("⭐ 评分: score_count/score_total 智能统计");
            log.info("📖 章节: t_content_chapter 独立管理");
            log.info("⚡ 功能: CRUD | 搜索 | 热门 | 审核");
            log.info("📱 接口: Dubbo RPC + REST HTTP");
            log.info("🔧 Version: 2.0.0 (Simple Edition)");
            log.info("===============================================");
        } catch (Exception e) {
            log.error("❌ Collide Content Service 启动失败！", e);
            System.exit(1);
        }
    }
} 