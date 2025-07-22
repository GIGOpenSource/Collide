package com.gig.collide.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * Gateway 网关全局配置
 *
 * @author GIG
 */
@Configuration
public class GatewayConfig {

    /**
     * IP限流键解析器
     * 根据请求IP进行限流
     */
    @Bean
    @Primary
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String ip = Objects.requireNonNull(exchange.getRequest().getRemoteAddress())
                    .getAddress().getHostAddress();
            return Mono.just(ip);
        };
    }

    /**
     * 用户限流键解析器  
     * 根据用户ID进行限流（需要先登录）
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            // 从请求头或Session中获取用户ID
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            if (userId == null) {
                userId = "anonymous";
            }
            return Mono.just(userId);
        };
    }

    /**
     * 接口路径限流键解析器
     * 根据请求路径进行限流
     */
    @Bean  
    public KeyResolver apiKeyResolver() {
        return exchange -> {
            String path = exchange.getRequest().getPath().value();
            return Mono.just(path);
        };
    }
} 