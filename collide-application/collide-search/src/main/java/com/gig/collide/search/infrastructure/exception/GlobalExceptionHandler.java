package com.gig.collide.business.infrastructure.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理器
 * 处理系统异常和静态资源404错误
 * 
 * @author GIG Team
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理静态资源未找到异常
     * 避免favicon.ico等静态资源404被记录为系统异常
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResourceFoundException(NoResourceFoundException e) {
        // 对于静态资源404，返回204状态码，不记录错误日志
        String requestPath = e.getMessage();
        if (requestPath != null && (
            requestPath.contains("favicon.ico") || 
            requestPath.contains("swagger-ui") ||
            requestPath.contains("robots.txt") ||
            requestPath.contains(".ico") ||
            requestPath.contains(".png") ||
            requestPath.contains(".jpg") ||
            requestPath.contains(".css") ||
            requestPath.contains(".js")
        )) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        
        // 其他资源404，记录日志并返回404
        log.warn("资源未找到: {}", requestPath);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * 处理其他系统异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error("系统异常", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("系统异常，请稍后重试");
    }
} 