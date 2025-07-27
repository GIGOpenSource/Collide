package com.gig.collide;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Collide 业务聚合应用启动类
 * 
 * <p>
 * 纯聚合启动模块，不包含具体业务逻辑，只负责启动和聚合各个业务服务：
 * <ul>
 *   <li>🔐 用户服务 (collide-users) - 用户管理、认证授权</li>
 *   <li>👥 关注服务 (collide-follow) - 用户关注关系管理</li>
 *   <li>📝 内容服务 (collide-content) - 内容创作与管理</li>
 *   <li>💬 评论服务 (collide-comment) - 评论互动功能</li>
 *   <li>❤️ 点赞服务 (collide-like) - 点赞点踩功能</li>
 *   <li>⭐ 收藏服务 (collide-favorite) - 内容收藏功能</li>
 *   <li>🌐 社交服务 (collide-social) - 社交互动功能</li>
 *   <li>🔍 搜索服务 (collide-search) - 纯搜索功能</li>
   <li>📂 分类服务 (collide-category) - 分类管理功能</li>
   <li>🏷️ 标签服务 (collide-tag) - 标签管理与用户兴趣功能</li>
 *   <li>🛒 商品服务 (collide-goods) - 商品管理功能</li>
 *   <li>📦 订单服务 (collide-order) - 订单处理功能</li>
 *   <li>💰 支付服务 (collide-payment) - 支付处理功能</li>
 * </ul>
 * </p>
 * 
 * <p>
 * 架构特点：
 * <ul>
 *   <li>🚀 去连表化设计 - 所有服务采用单表查询，提升性能</li>
 *   <li>🔗 微服务架构 - 服务间通过RPC调用，松耦合设计</li>
 *   <li>☁️ Spring Cloud - 基于Nacos的服务发现与配置管理</li>
 *   <li>🏗️ DDD分层 - 领域驱动设计，清晰的分层架构</li>
 *   <li>📊 完整监控 - 集成Prometheus监控和SkyWalking链路跟踪</li>
 *   <li>🔄 分布式事务 - 集成Seata和TCC事务补偿机制</li>
 * </ul>
 * </p>
 * 
 * <p>
 * 参考 nft-turbo-app 的聚合启动架构设计，采用模块化配置管理：
 * <ul>
 *   <li>🎯 单一职责 - 仅负责聚合启动，不包含业务控制器</li>
 *   <li>📁 配置文件分离 - bootstrap.yml + application.yml + 专用配置文件</li>
 *   <li>🔗 服务聚合 - 将各个独立服务模块聚合为统一应用</li>
 *   <li>⚙️ 启动参数设置 - 统一的应用名称和默认配置</li>
 * </ul>
 * </p>
 *
 * @author Collide Team
 * @version 2.0 (参考 nft-turbo-app 聚合启动架构，移除业务控制器)
 * @since 2024-01-01
 */
@SpringBootApplication(scanBasePackages = {
    // ================================
    // 核心业务模块扫描
    // ================================
    "com.gig.collide.users",       // ✅ 用户服务
    "com.gig.collide.follow",      // ✅ 关注服务
    "com.gig.collide.content",     // ✅ 内容服务
    "com.gig.collide.comment",     // ✅ 评论服务
    "com.gig.collide.like",        // ✅ 点赞服务
    "com.gig.collide.favorite",    // ✅ 收藏服务
    "com.gig.collide.social",      // ✅ 社交服务
    "com.gig.collide.search",      // ✅ 搜索服务
    "com.gig.collide.category",    // ✅ 分类服务
    "com.gig.collide.tag",         // ✅ 标签服务
    
    // ================================
    // 商业化模块扫描
    // ================================
    "com.gig.collide.goods",       // ✅ 商品服务
    "com.gig.collide.order",       // ✅ 订单服务
    "com.gig.collide.payment",     // ✅ 支付服务
    
    // ================================
    // 基础设施组件扫描
    // ================================
    "com.gig.collide.base",        // ✅ 基础组件
    "com.gig.collide.cache",       // ✅ 缓存组件
    "com.gig.collide.datasource",  // ✅ 数据源组件
    "com.gig.collide.rpc",         // ✅ RPC组件
    "com.gig.collide.web",         // ✅ Web组件
})
@EnableDiscoveryClient
@EnableDubbo(scanBasePackages = {
    // ================================
    // Dubbo 服务接口扫描
    // ================================
    "com.gig.collide.users.facade", 
    "com.gig.collide.follow.facade", 
    "com.gig.collide.content.facade",
    "com.gig.collide.comment.facade",
    "com.gig.collide.like.facade",
    "com.gig.collide.favorite.facade",
    "com.gig.collide.social.facade",
    "com.gig.collide.search.facade",
    "com.gig.collide.category.facade",
    "com.gig.collide.tag.facade",
    "com.gig.collide.goods.facade",
    "com.gig.collide.order.facade",
    "com.gig.collide.payment.facade"
})
public class CollideBusinessApplication {
    
    /**
     * 应用主入口
     * 参考 nft-turbo-app 的启动配置模式
     */
    public static void main(String[] args) {
        // 设置应用名称（参考 nft-turbo-app 的配置方式）
        System.setProperty("spring.application.name", "collide-business");
        
        // 启用JVM优化参数
        System.setProperty("spring.main.lazy-initialization", "true");
        
        SpringApplication application = new SpringApplication(CollideBusinessApplication.class);
        
        // 设置默认配置（参考 nft-turbo-app 的配置管理）
        application.setDefaultProperties(java.util.Map.of(
            "server.port", "9503",
            "spring.profiles.active", "dev",
            "management.endpoints.web.exposure.include", "health,info,prometheus",
            "dubbo.application.qos-port", "33334"
        ));
        
        // 启动应用
        long startTime = System.currentTimeMillis();
        var context = application.run(args);
        long endTime = System.currentTimeMillis();
        
        // 输出启动成功信息（参考 nft-turbo-app 的日志风格，但更适合 Collide）
        printStartupBanner(endTime - startTime);
        
        // 输出服务信息
        printServiceInfo();
    }
    
    /**
     * 打印启动横幅（参考 nft-turbo-app 但定制化）
     */
    private static void printStartupBanner(long startupTime) {
        System.out.println("\n" +
            "   ____      _ _ _     _        ____            _                   \n" +
            "  / ___|___ | | (_) __| | ___  | __ ) _   _ ___(_)_ __   ___  ___ ___ \n" +
            " | |   / _ \\| | | |/ _` |/ _ \\ |  _ \\| | | / __| | '_ \\ / _ \\/ __/ __|\n" +
            " | |__| (_) | | | | (_| |  __/ | |_) | |_| \\__ \\ | | | |  __/\\__ \\__ \\\n" +
            "  \\____\\___/|_|_|_|\\__,_|\\___| |____/ \\__,_|___/_|_| |_|\\___||___/___/\n" +
            "\n" +
            "🚀 Collide Business Application Started Successfully!\n" +
            "⏱️  Startup Time: " + startupTime + "ms\n" +
            "🌐 Server Port: 9503\n" +
            "📋 Environment: Development\n" +
            "🔗 Health Check: http://localhost:9503/actuator/health\n" +
            "📖 API Documentation: http://localhost:9503/swagger-ui.html\n"
        );
    }
    
    /**
     * 打印服务模块信息
     */
    private static void printServiceInfo() {
        System.out.println("📱 Business Services Loaded:");
        System.out.println("   🔐 Users Service      - 用户管理、认证授权");
        System.out.println("   👥 Follow Service     - 用户关注关系管理");
        System.out.println("   📝 Content Service    - 内容创作与管理");
        System.out.println("   💬 Comment Service    - 评论互动功能");
        System.out.println("   ❤️  Like Service       - 点赞点踩功能");
        System.out.println("   ⭐ Favorite Service   - 内容收藏功能");
        System.out.println("   🌐 Social Service     - 社交互动功能");
        System.out.println("   🔍 Search Service     - 纯搜索功能");
        System.out.println("   📂 Category Service   - 分类管理功能");
        System.out.println("   🏷️ Tag Service        - 标签管理与用户兴趣功能");
        System.out.println("   🛒 Goods Service      - 商品管理功能");
        System.out.println("   📦 Order Service      - 订单处理功能");
        System.out.println("   💰 Payment Service    - 支付处理功能");
        
        System.out.println("\n🔧 Infrastructure Services:");
        System.out.println("   📊 Prometheus         - 监控指标收集");
        System.out.println("   🔍 SkyWalking         - 分布式链路跟踪");
        System.out.println("   🔄 Seata              - 分布式事务管理");
        System.out.println("   ⚡ TCC                - 事务补偿机制");
        System.out.println("   💾 Cache              - 分布式缓存");
        System.out.println("   🗄️  DataSource        - 数据源管理");
        System.out.println("\n✅ All services initialized successfully!");
    }
} 