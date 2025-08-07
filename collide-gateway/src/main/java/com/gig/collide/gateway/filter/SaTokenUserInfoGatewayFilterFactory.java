package com.gig.collide.gateway.filter;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.Map;

/**
 * Sa-Token用户信息传递Filter
 * 从Sa-Token会话中获取用户信息，并通过Header传递给下游服务
 */
@Slf4j
@Component
public class SaTokenUserInfoGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            try {
                // 检查用户是否已登录
                if (StpUtil.isLogin()) {
                    // 从Sa-Token Session中获取用户信息
                    Object userInfoObj = StpUtil.getSession().get("userInfo");
                    
                    if (userInfoObj instanceof Map) {
                        Map<String, Object> userInfo = (Map<String, Object>) userInfoObj;
                        
                        // 构建新的请求，添加用户信息Headers
                        ServerHttpRequest.Builder builder = exchange.getRequest().mutate();
                        
                        // 添加用户ID
                        Object userId = userInfo.get("user_id");
                        if (userId != null) {
                            builder.header("X-User-Id", userId.toString());
                        }
                        
                        // 添加用户名
                        Object username = userInfo.get("username");
                        if (username != null) {
                            builder.header("X-Username", username.toString());
                        }
                        
                        // 添加用户角色
                        Object role = userInfo.get("role");
                        if (role != null) {
                            builder.header("X-User-Role", role.toString());
                        }
                        
                        log.debug("传递用户信息到下游服务 - 用户ID: {}, 用户名: {}, 角色: {}", 
                                userId, username, role);
                        
                        // 创建新的exchange
                        ServerWebExchange newExchange = exchange.mutate()
                                .request(builder.build())
                                .build();
                        
                        return chain.filter(newExchange);
                    } else {
                        log.warn("用户已登录但Session中无用户信息");
                    }
                } else {
                    log.debug("用户未登录，跳过用户信息传递");
                }
            } catch (Exception e) {
                log.error("获取用户信息失败", e);
            }
            
            // 如果没有用户信息或出现异常，继续处理请求
            return chain.filter(exchange);
        };
    }
}
