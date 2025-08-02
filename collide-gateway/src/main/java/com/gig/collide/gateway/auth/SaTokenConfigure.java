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
 * Sa-Token 网关统一鉴权配置 - 重构版
 * 基于重构后的用户API文档和权限体系精确配置
 * 
 * 权限分级：
 * 1. 公开接口：无需登录（查询类、注册等）
 * 2. 用户接口：需要登录（个人信息、钱包操作等） 
 * 3. 管理员接口：需要管理员权限（用户管理、系统管理等）
 * 
 * @author GIG Team
 * @version 2.0.0 (根据users-api.md重构)
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
                .addExclude("/api/v1/auth/test")
                
                // 鉴权方法：每次访问进入
                .setAuth(obj -> {
                    // ========== 认证服务：部分需要登录 ==========
                    SaRouter.match("/api/v1/auth/logout").check(r -> StpUtil.checkLogin());
                    SaRouter.match("/api/v1/auth/verify-token").check(r -> StpUtil.checkLogin());
                    
                    // ========== 公开API：无需登录校验 ==========
                    SaRouter.match("/api/v1/content/list").stop();         // 内容列表
                    SaRouter.match("/api/v1/content/detail/**").stop();    // 内容详情  
                    SaRouter.match("/api/v1/content/search").stop();       // 内容搜索
                    SaRouter.match("/api/v1/social/posts/hot").stop();     // 热门动态
                    SaRouter.match("/api/v1/social/posts/search").stop();  // 搜索动态
                    SaRouter.match("/api/v1/social/posts/{postId}").stop(); // 动态详情
                    
                    // ========== 用户服务公开接口：无需登录 ==========
                    SaRouter.match("/api/v1/users", "GET").stop();                           // 查询用户（如果有GET实现）
                    SaRouter.match("/api/v1/users", "POST").stop();                          // 用户注册
                    SaRouter.match("/api/v1/users/{userId}", "GET").stop();                  // 查询用户信息
                    SaRouter.match("/api/v1/users/username/{username}", "GET").stop();       // 根据用户名查询
                    SaRouter.match("/api/v1/users/query", "POST").stop();                    // 分页查询用户
                    SaRouter.match("/api/v1/users/batch", "POST").stop();                    // 批量查询用户
                    SaRouter.match("/api/v1/users/check/username/**", "GET").stop();         // 检查用户名
                    SaRouter.match("/api/v1/users/check/email/**", "GET").stop();            // 检查邮箱
                    SaRouter.match("/api/v1/users/{userId}/profile", "GET").stop();          // 获取用户资料
                    SaRouter.match("/api/v1/users/profiles/search", "GET").stop();           // 搜索用户资料
                    SaRouter.match("/api/v1/users/{userId}/stats", "GET").stop();            // 获取统计数据
                    SaRouter.match("/api/v1/users/ranking/followers", "GET").stop();         // 粉丝排行榜
                    SaRouter.match("/api/v1/users/ranking/content", "GET").stop();           // 内容排行榜
                    SaRouter.match("/api/v1/users/platform/stats", "GET").stop();            // 平台统计
                    SaRouter.match("/api/v1/users/health", "GET").stop();                    // 健康检查
                    SaRouter.match("/api/v1/users/wallet/health", "GET").stop();             // 钱包健康检查
                    
                    // ========== 用户服务需要登录接口：个人信息管理 ==========
                    SaRouter.match("/api/v1/users/{userId}", "PUT").check(r -> StpUtil.checkLogin());           // 更新用户信息
                    SaRouter.match("/api/v1/users/{userId}/password", "PUT").check(r -> StpUtil.checkLogin());  // 修改密码
                    SaRouter.match("/api/v1/users/{userId}/profile", "POST").check(r -> StpUtil.checkLogin());  // 创建用户资料
                    SaRouter.match("/api/v1/users/{userId}/profile", "PUT").check(r -> StpUtil.checkLogin());   // 更新用户资料
                    SaRouter.match("/api/v1/users/{userId}/profile/avatar", "PUT").check(r -> StpUtil.checkLogin()); // 更新头像
                    SaRouter.match("/api/v1/users/{userId}/profile/nickname", "PUT").check(r -> StpUtil.checkLogin()); // 更新昵称
                    
                    // ========== 当前用户接口：需要登录 ==========
                    SaRouter.match("/api/v1/users/me/**").check(r -> StpUtil.checkLogin());                     // 当前用户所有接口
                    
                    // ========== 钱包服务：需要登录 ==========
                    SaRouter.match("/api/v1/users/{userId}/wallet/**").check(r -> StpUtil.checkLogin());        // 用户钱包操作
                    SaRouter.match("/api/v1/users/wallets/batch", "POST").check(r -> {                          // 批量查询钱包（管理员）
                        StpUtil.checkLogin();
                        StpUtil.checkRole("admin");
                    });
                    SaRouter.match("/api/v1/users/{userId}/wallet/cash/freeze", "POST").check(r -> {             // 冻结现金（管理员）
                        StpUtil.checkLogin();
                        StpUtil.checkRole("admin");
                    });
                    SaRouter.match("/api/v1/users/{userId}/wallet/cash/unfreeze", "POST").check(r -> {           // 解冻现金（管理员）
                        StpUtil.checkLogin();
                        StpUtil.checkRole("admin");
                    });
                    SaRouter.match("/api/v1/users/{userId}/wallet/status", "PUT").check(r -> {                   // 更新钱包状态（管理员）
                        StpUtil.checkLogin();
                        StpUtil.checkRole("admin");
                    });
                    SaRouter.match("/api/v1/users/{userId}/wallet/freeze", "POST").check(r -> {                  // 冻结钱包（管理员）
                        StpUtil.checkLogin();
                        StpUtil.checkRole("admin");
                    });
                    SaRouter.match("/api/v1/users/{userId}/wallet/unfreeze", "POST").check(r -> {                // 解冻钱包（管理员）
                        StpUtil.checkLogin();
                        StpUtil.checkRole("admin");
                    });
                    
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
                    
                    // ========== 用户管理功能：需要管理员权限 ==========
                    SaRouter.match("/api/admin/users/**").check(r -> {
                        StpUtil.checkLogin();
                        StpUtil.checkRole("admin");
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