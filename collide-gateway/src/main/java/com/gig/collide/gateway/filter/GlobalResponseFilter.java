package com.gig.collide.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 全局响应过滤器
 * 用于处理请求响应的统一格式化和日志记录
 *
 * @author GIG
 */
@Slf4j
@Component
public class GlobalResponseFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestPath = exchange.getRequest().getPath().value();
        String requestMethod = exchange.getRequest().getMethod().name();
        long startTime = System.currentTimeMillis();
        
        return chain.filter(exchange)
                .doOnSuccess(aVoid -> {
                    // 请求成功处理
                    long duration = System.currentTimeMillis() - startTime;
                    var statusCode = exchange.getResponse().getStatusCode();
                    
                    log.info("Gateway 请求完成 - {} {} - 状态码：{} - 耗时：{}ms", 
                            requestMethod, requestPath, 
                            statusCode != null ? statusCode.value() : "unknown", 
                            duration);
                })
                .doOnError(throwable -> {
                    // 请求异常处理
                    long duration = System.currentTimeMillis() - startTime;
                    
                    log.error("Gateway 请求异常 - {} {} - 耗时：{}ms - 异常：{}", 
                            requestMethod, requestPath, duration, throwable.getMessage());
                });
    }

    @Override
    public int getOrder() {
        // 设置过滤器执行顺序，数值越小优先级越高
        return Ordered.LOWEST_PRECEDENCE;
    }
} 