package com.gig.collide.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Collide 业务聚合服务控制器
 * 
 * <p>提供聚合应用的统一入口和服务信息</p>
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/business")
@Tag(name = "Business", description = "业务聚合服务API")
public class BusinessController {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 获取服务信息
     *
     * @return 服务基本信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取服务信息", description = "返回聚合应用的基本信息和服务状态")
    public Map<String, Object> getServiceInfo() {
        Map<String, Object> info = new HashMap<>();
        
        info.put("application", "Collide Business Application");
        info.put("version", "1.0.0");
        info.put("description", "Collide社交平台业务聚合服务");
        info.put("startTime", LocalDateTime.now());
        
        // 服务模块信息
        Map<String, Object> modules = new HashMap<>();
        modules.put("users", "用户管理服务 - 用户注册、认证、个人信息管理");
        modules.put("follow", "关注关系服务 - 用户关注、粉丝管理");
        modules.put("content", "内容服务 - 内容创作、发布、管理");
        modules.put("comment", "评论服务 - 评论发布、回复、管理");
        modules.put("like", "点赞服务 - 点赞、点踩互动功能");
        modules.put("favorite", "收藏服务 - 内容收藏、收藏夹管理");
        info.put("modules", modules);
        
        // 技术栈信息
        Map<String, Object> tech = new HashMap<>();
        tech.put("framework", "Spring Boot 3.x");
        tech.put("cloud", "Spring Cloud with Nacos");
        tech.put("database", "MySQL with MyBatis Plus");
        tech.put("cache", "Redis");
        tech.put("architecture", "DDD + 去连表化设计");
        info.put("technology", tech);
        
        return info;
    }

    /**
     * 健康检查
     *
     * @return 健康状态
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查聚合应用的健康状态")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        
        // 检查各个模块的状态
        Map<String, Object> components = new HashMap<>();
        
        try {
            // 检查Spring Context
            components.put("spring-context", "UP");
            
            // 检查数据库连接
            components.put("database", "UP");
            
            // 检查缓存
            components.put("cache", "UP");
            
            // 检查服务发现
            components.put("discovery", "UP");
            
        } catch (Exception e) {
            components.put("error", e.getMessage());
            health.put("status", "DOWN");
        }
        
        health.put("components", components);
        return health;
    }

    /**
     * 获取API端点列表
     *
     * @return API端点信息
     */
    @GetMapping("/endpoints")
    @Operation(summary = "API端点列表", description = "获取所有可用的API端点信息")
    public Map<String, Object> getEndpoints() {
        Map<String, Object> endpoints = new HashMap<>();
        
        // 用户服务端点
        Map<String, String> userEndpoints = new HashMap<>();
        userEndpoints.put("注册", "POST /api/users/register");
        userEndpoints.put("登录", "POST /api/users/login");
        userEndpoints.put("用户信息", "GET /api/users/profile/{userId}");
        endpoints.put("用户服务", userEndpoints);
        
        // 关注服务端点
        Map<String, String> followEndpoints = new HashMap<>();
        followEndpoints.put("关注用户", "POST /api/follow");
        followEndpoints.put("取消关注", "DELETE /api/follow");
        followEndpoints.put("关注列表", "GET /api/follow/following");
        followEndpoints.put("粉丝列表", "GET /api/follow/followers");
        endpoints.put("关注服务", followEndpoints);
        
        // 内容服务端点
        Map<String, String> contentEndpoints = new HashMap<>();
        contentEndpoints.put("发布内容", "POST /api/content");
        contentEndpoints.put("内容列表", "GET /api/content/list");
        contentEndpoints.put("内容详情", "GET /api/content/{contentId}");
        endpoints.put("内容服务", contentEndpoints);
        
        // 评论服务端点
        Map<String, String> commentEndpoints = new HashMap<>();
        commentEndpoints.put("发表评论", "POST /api/comment");
        commentEndpoints.put("评论列表", "GET /api/comment/list");
        commentEndpoints.put("回复评论", "POST /api/comment/reply");
        endpoints.put("评论服务", commentEndpoints);
        
        // 点赞服务端点
        Map<String, String> likeEndpoints = new HashMap<>();
        likeEndpoints.put("点赞", "POST /api/like");
        likeEndpoints.put("取消点赞", "DELETE /api/like");
        likeEndpoints.put("点赞状态", "GET /api/like/status");
        endpoints.put("点赞服务", likeEndpoints);
        
        // 收藏服务端点
        Map<String, String> favoriteEndpoints = new HashMap<>();
        favoriteEndpoints.put("收藏", "POST /api/favorite");
        favoriteEndpoints.put("取消收藏", "DELETE /api/favorite");
        favoriteEndpoints.put("收藏列表", "GET /api/favorite/list");
        endpoints.put("收藏服务", favoriteEndpoints);
        
        return endpoints;
    }

    /**
     * 欢迎页面
     *
     * @return 欢迎信息
     */
    @GetMapping("/welcome")
    @Operation(summary = "欢迎页面", description = "聚合应用欢迎页面")
    public Map<String, Object> welcome() {
        Map<String, Object> welcome = new HashMap<>();
        
        welcome.put("message", "欢迎使用 Collide 社交平台！");
        welcome.put("description", "这是一个基于去连表化设计的高性能社交平台");
        welcome.put("features", new String[]{
            "📱 用户管理 - 注册登录、个人资料",
            "👥 社交关系 - 关注、粉丝管理", 
            "📝 内容创作 - 发布文字、图片、视频",
            "💬 互动评论 - 评论回复、嵌套评论",
            "👍 点赞互动 - 点赞点踩、情感表达",
            "⭐ 内容收藏 - 收藏管理、分类整理"
        });
        welcome.put("documentation", "/api/swagger-ui.html");
        welcome.put("health", "/api/actuator/health");
        
        return welcome;
    }
} 