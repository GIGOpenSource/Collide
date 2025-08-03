package com.gig.collide.gateway.auth;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sa-Token 网关统一鉴权配置 - 简洁版
 * 基于简洁版用户API和角色体系重构
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Configuration
@Slf4j
public class SaTokenConfigure {

    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 拦截所有路径
                .addInclude("/**")
                
                // 放行路径：无需登录的接口
                .addExclude("/favicon.ico")
                .addExclude("/actuator/health")
                .addExclude("/api/v1/auth/login")
                .addExclude("/api/v1/auth/register") 
                .addExclude("/api/v1/auth/login-or-register")
                .addExclude("/api/v1/auth/validate-invite-code")
                .addExclude("/api/v1/auth/test")
                
                // 鉴权方法：每次访问进入
                .setAuth(obj -> {
                    // ========== 认证服务：部分需要登录 ==========
                    SaRouter.match("/api/v1/auth/logout").check(r -> StpUtil.checkLogin());
                    SaRouter.match("/api/v1/auth/me").check(r -> StpUtil.checkLogin());
                    SaRouter.match("/api/v1/auth/verify-token").check(r -> StpUtil.checkLogin());
                    SaRouter.match("/api/v1/auth/my-invite-info").check(r -> StpUtil.checkLogin());
                    
                    // ========== 公开API：无需登录校验 ==========
                    SaRouter.match("/api/v1/content/list").stop();         // 内容列表
                    SaRouter.match("/api/v1/content/detail/**").stop();    // 内容详情  
                    SaRouter.match("/api/v1/content/search").stop();       // 内容搜索
                    SaRouter.match("/api/v1/social/posts/hot").stop();     // 热门动态
                    SaRouter.match("/api/v1/social/posts/search").stop();  // 搜索动态
                    SaRouter.match("/api/v1/social/posts/{postId}").stop(); // 动态详情
                    
                    // ========== 用户服务：需要登录 ==========
                    SaRouter.match("/api/v1/users/**").check(r -> StpUtil.checkLogin());
                    
                    // ========== 社交服务：需要登录 ==========
                    SaRouter.match("/api/v1/social/posts").check(r -> StpUtil.checkLogin());          
                    SaRouter.match("/api/v1/social/posts/{postId}/like").check(r -> StpUtil.checkLogin()); 
                    SaRouter.match("/api/v1/social/posts/{postId}/share").check(r -> StpUtil.checkLogin()); 
                    SaRouter.match("/api/v1/social/posts/timeline/**").check(r -> StpUtil.checkLogin()); 
                    SaRouter.match("/api/v1/social/posts/feed/**").check(r -> StpUtil.checkLogin());   
                    
                    // ========== 收藏服务：需要登录 ==========
                    SaRouter.match("/api/v1/favorite/**").check(r -> StpUtil.checkLogin());
                    
                    // ========== 点赞服务：需要登录 ==========
                    SaRouter.match("/api/v1/like/**").check(r -> StpUtil.checkLogin());
                    
                    // ========== 关注服务：需要登录 ==========
                    SaRouter.match("/api/v1/follow/**").check(r -> StpUtil.checkLogin());
                    
                    // ========== 评论服务：需要登录 ==========
                    SaRouter.match("/api/v1/comment/**").check(r -> StpUtil.checkLogin());
                    
                    // ========== 内容服务：创建和管理需要登录 ==========
                    SaRouter.match("/api/v1/content/create").check(r -> StpUtil.checkLogin());
                    SaRouter.match("/api/v1/content/update/**").check(r -> StpUtil.checkLogin());
                    SaRouter.match("/api/v1/content/delete/**").check(r -> StpUtil.checkLogin());
                    SaRouter.match("/api/v1/content/my").check(r -> StpUtil.checkLogin());
                    
                    // ========== 博主功能：需要blogger角色 ==========
                    SaRouter.match("/api/v1/content/blogger/**").check(r -> {
                        StpUtil.checkLogin();
                        StpUtil.checkRoleOr("blogger", "admin");
                    });
                    
                    // ========== VIP功能：需要vip权限 ==========
                    SaRouter.match("/api/v1/content/vip/**").check(r -> {
                        StpUtil.checkLogin();
                        StpUtil.checkPermissionOr("vip", "admin");
                    });
                    
                    // ========== 管理功能：需要管理员权限 ==========
                    SaRouter.match("/admin/**").check(r -> {
                        StpUtil.checkLogin();
                        StpUtil.checkRole("admin");
                    });
                    
                    // ========== 文件服务：需要登录 ==========
                    SaRouter.match("/api/v1/files/upload").check(r -> StpUtil.checkLogin());
                    SaRouter.match("/api/v1/files/batch-upload").check(r -> StpUtil.checkLogin());
                    SaRouter.match("/api/v1/files/config").stop(); // 配置接口可公开访问
                    
                    // ========== 订单支付：需要登录 ==========
                    SaRouter.match("/api/v1/order/**").check(r -> StpUtil.checkLogin());
                    SaRouter.match("/api/v1/payment/**").check(r -> StpUtil.checkLogin());
                    
                    // ========== 商品管理：需要blogger权限 ==========
                    SaRouter.match("/api/v1/goods/create").check(r -> {
                        StpUtil.checkLogin();
                        StpUtil.checkRoleOr("blogger", "admin");
                    });
                    SaRouter.match("/api/v1/goods/update/**").check(r -> {
                        StpUtil.checkLogin();
                        StpUtil.checkRoleOr("blogger", "admin");
                    });
                    SaRouter.match("/api/v1/goods/delete/**").check(r -> {
                        StpUtil.checkLogin();
                        StpUtil.checkRoleOr("blogger", "admin");
                    });
                    
                    // ========== 搜索服务：无需登录 ==========
                    SaRouter.match("/api/v1/search/**").stop();
                    
                    // ========== 标签分类：无需登录 ==========
                    SaRouter.match("/api/v1/tag/**").stop();
                    SaRouter.match("/api/v1/category/**").stop();
                })
                
                // 异常处理方法：每次setAuth函数出现异常时进入
                .setError(this::handleAuthException);
    }

    /**
     * 统一异常处理
     */
    private SaResult handleAuthException(Throwable e) {
        log.warn("Sa-Token 鉴权异常：{}", e.getMessage());
        
        return switch (e) {
            case NotLoginException ex -> {
                log.info("用户未登录，访问路径：{}", ex.getMessage());
                yield SaResult.error("请先登录").setCode(401);
            }
            case NotRoleException ex -> {
                log.warn("用户权限不足，缺少角色：{}", ex.getRole());
                String role = ex.getRole();
                if ("admin".equals(role)) {
                    yield SaResult.error("需要管理员权限").setCode(403);
                } else if ("blogger".equals(role)) {
                    yield SaResult.error("需要博主认证").setCode(403);
                } else if ("vip".equals(role)) {
                    yield SaResult.error("需要VIP权限").setCode(403);
                }
                yield SaResult.error("权限不足").setCode(403);
            }
            case NotPermissionException ex -> {
                log.warn("用户权限不足，缺少权限：{}", ex.getPermission());
                String permission = ex.getPermission();
                if ("vip".equals(permission)) {
                    yield SaResult.error("需要VIP权限").setCode(403);
                } else if ("blogger".equals(permission)) {
                    yield SaResult.error("需要博主认证").setCode(403);
                } else if ("admin".equals(permission)) {
                    yield SaResult.error("需要管理员权限").setCode(403);
                }
                yield SaResult.error("权限不足").setCode(403);
            }
            default -> {
                log.error("未知认证异常：", e);
                yield SaResult.error("认证失败").setCode(500);
            }
        };
    }
}