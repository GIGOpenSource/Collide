package com.gig.collide.comment;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 评论服务启动类 - 简洁版
 * 基于单表无连表设计，支持多级评论和回复功能
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.gig.collide.comment.infrastructure.mapper")
public class CollideCommentApplication {

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext application = SpringApplication.run(CollideCommentApplication.class, args);
        Environment env = application.getEnvironment();
        
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        String path = env.getProperty("server.servlet.context-path", "");
        
        log.info("\n----------------------------------------------------------\n\t" +
                "🎉 评论服务启动成功！(Comment Service v2.0.0 - 简洁版)\n\t" +
                "📱 访问地址: \thttp://{}:{}{}\n\t" +
                "📚 接口文档: \thttp://{}:{}{}/doc.html\n\t" +
                "💡 核心特性:\n\t" +
                "   • 🔗 多级评论 - 支持parent_comment_id层级结构\n\t" +
                "   • 📝 双类型支持 - CONTENT/DYNAMIC评论\n\t" +
                "   • 🏷️ 冗余设计 - 用户信息和回复信息冗余存储\n\t" +
                "   • 📊 状态管理 - NORMAL/HIDDEN/DELETED状态\n\t" +
                "   • 📈 统计冗余 - like_count/reply_count直接存储\n\t" +
                "   • 🌳 树形展示 - 支持评论树形结构构建\n\t" +
                "   • 🔍 高级功能 - 搜索、热门、最新评论支持\n\t" +
                "🎯 设计原则:\n\t" +
                "   • 📊 单表设计 - 基于t_comment表的无连表架构\n\t" +
                "   • 🚀 高性能 - 冗余字段避免复杂JOIN查询\n\t" +
                "   • 🛡️ 数据一致性 - 自动维护统计计数和冗余信息\n\t" +
                "   • 🔧 简洁API - 30+个核心方法满足所有评论需求\n\t" +
                "   • 📱 REST & Dubbo - 同时支持HTTP和RPC访问\n" +
                "----------------------------------------------------------",
                ip, port, path, ip, port, path);
    }
} 