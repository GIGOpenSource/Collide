package com.gig.collide.comment;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 评论服务启动类
 * 
 * 提供内容评论相关的核心功能：
 * - 评论发布和管理
 * - 多级回复支持
 * - 评论点赞/取消点赞
 * - 评论统计和排序
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@SpringBootApplication
public class CollideCommentApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideCommentApplication.class, args);
        System.out.println("========== Collide Comment Service Started ==========");
    }
} 