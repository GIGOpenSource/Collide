package com.gig.collide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Collide 业务聚合应用启动类
 * 
 * <p>
 * 该应用整合了以下业务服务模块：
 * <ul>
 *   <li>用户服务 (collide-users) - 用户管理、认证授权</li>
 *   <li>关注服务 (collide-follow) - 用户关注关系管理</li>
 *   <li>内容服务 (collide-content) - 内容创作与管理</li>
 *   <li>评论服务 (collide-comment) - 评论互动功能</li>
 *   <li>点赞服务 (collide-like) - 点赞点踩功能</li>
 *   <li>收藏服务 (collide-favorite) - 内容收藏功能</li>
 * </ul>
 * </p>
 * 
 * <p>
 * 架构特点：
 * <ul>
 *   <li>去连表化设计 - 所有服务采用单表查询，提升性能</li>
 *   <li>微服务架构 - 服务间通过RPC调用，松耦合设计</li>
 *   <li>Spring Cloud - 基于Nacos的服务发现与配置管理</li>
 *   <li>DDD分层 - 领域驱动设计，清晰的分层架构</li>
 * </ul>
 * </p>
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@SpringBootApplication(scanBasePackages = {
    "com.gig.collide.users",       // 用户服务
    "com.gig.collide.follow",      // 关注服务  
    "com.gig.collide.content",     // 内容服务
    "com.gig.collide.comment",     // 评论服务
    "com.gig.collide.like",        // 点赞服务
    "com.gig.collide.favorite",    // 收藏服务
    "com.gig.collide.base",        // 基础组件
    "com.gig.collide.cache",       // 缓存组件
    "com.gig.collide.datasource",  // 数据源组件
    "com.gig.collide.rpc",         // RPC组件
    "com.gig.collide.web"          // Web组件
})
@EnableDiscoveryClient
public class CollideBusinessApplication {
    
    public static void main(String[] args) {
        System.setProperty("spring.application.name", "collide-business");
        
        SpringApplication application = new SpringApplication(CollideBusinessApplication.class);
        
        // 设置默认配置
        application.setDefaultProperties(java.util.Map.of(
            "server.port", "8080",
            "spring.profiles.active", "dev"
        ));
        
        application.run(args);
        
        System.out.println("\n" +
            "   ____      _ _ _     _        ____            _                   \n" +
            "  / ___|___ | | (_) __| | ___  | __ ) _   _ ___(_)_ __   ___  ___ ___ \n" +
            " | |   / _ \\| | | |/ _` |/ _ \\ |  _ \\| | | / __| | '_ \\ / _ \\/ __/ __|\n" +
            " | |__| (_) | | | | (_| |  __/ | |_) | |_| \\__ \\ | | | |  __/\\__ \\__ \\\n" +
            "  \\____\\___/|_|_|_|\\__,_|\\___| |____/ \\__,_|___/_|_| |_|\\___||___/___/\n" +
            "\n" +
            "🚀 Collide Business Application Started Successfully!\n" +
            "📱 Services: Users | Follow | Content | Comment | Like | Favorite\n" +
            "🌐 Server Port: 8080\n" +
            "📋 Environment: Development\n" +
            "🔗 Health Check: http://localhost:8080/actuator/health\n"
        );
    }
} 