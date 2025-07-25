package com.gig.collide.gateway.config;

import cn.dev33.satoken.stp.StpUtil;
import com.gig.collide.gateway.exception.BloggerPermissionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * 博主权限控制配置
 * 在网关层统一处理博主权限验证
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@Component
public class BloggerPermissionConfig implements GlobalFilter, Ordered {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 需要博主权限的路径模式
     */
    private static final List<String> BLOGGER_REQUIRED_PATTERNS = Arrays.asList(
        "/api/v1/content",                    // 创建内容
        "/api/v1/content/*",                  // 更新/删除内容
        "/api/v1/content/*/publish",          // 发布内容
        "/api/v1/content/my"                  // 我的内容列表
    );

    /**
     * 不需要博主权限的路径模式（白名单）
     */
    private static final List<String> BLOGGER_EXCLUDED_PATTERNS = Arrays.asList(
        "/api/v1/content",                    // 查看内容列表
        "/api/v1/content/*",                  // 查看内容详情
        "/api/v1/content/user/*",             // 查看用户内容列表
        "/api/v1/content/*/like",             // 点赞内容
        "/api/v1/content/*/favorite",         // 收藏内容
        "/api/v1/content/*/share",            // 分享内容
        "/api/v1/content/*/statistics"        // 查看统计信息
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String method = request.getMethod().name();

        // 检查是否需要博主权限
        if (isBloggerRequired(path, method)) {
            return checkBloggerPermission(exchange, chain);
        }

        return chain.filter(exchange);
    }

    /**
     * 检查是否需要博主权限
     */
    private boolean isBloggerRequired(String path, String method) {
        // 只对POST、PUT、DELETE方法进行博主权限检查
        if (!Arrays.asList("POST", "PUT", "DELETE").contains(method)) {
            return false;
        }

        // 检查是否匹配需要博主权限的路径
        for (String pattern : BLOGGER_REQUIRED_PATTERNS) {
            if (pathMatcher.match(pattern, path)) {
                // 检查是否在白名单中
                for (String excludedPattern : BLOGGER_EXCLUDED_PATTERNS) {
                    if (pathMatcher.match(excludedPattern, path)) {
                        return false; // 在白名单中，不需要博主权限
                    }
                }
                return true; // 需要博主权限
            }
        }

        return false;
    }

    /**
     * 检查博主权限
     */
    private Mono<Void> checkBloggerPermission(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            // 检查用户是否登录
            if (!StpUtil.isLogin()) {
                return unauthorized(exchange, "用户未登录");
            }

            // 检查用户是否具有博主角色
            if (!StpUtil.hasRole("blogger")) {
                log.warn("用户尝试访问博主功能，用户ID: {}, 路径: {}", 
                    StpUtil.getLoginId(), exchange.getRequest().getPath());
                return forbidden(exchange, "需要博主权限，请先申请成为博主");
            }

            log.info("博主权限验证通过，用户ID: {}, 路径: {}", 
                StpUtil.getLoginId(), exchange.getRequest().getPath());
            return chain.filter(exchange);

        } catch (Exception e) {
            log.error("博主权限检查异常", e);
            return unauthorized(exchange, "权限验证失败");
        }
    }

    /**
     * 返回未授权响应
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    /**
     * 返回禁止访问响应
     */
    private Mono<Void> forbidden(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        // 确保在认证过滤器之后执行
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }
} 