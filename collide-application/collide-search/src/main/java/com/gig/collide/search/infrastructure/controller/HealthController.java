package com.gig.collide.business.infrastructure.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 * 提供简单的健康检查接口
 * 
 * @author GIG Team
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    /**
     * 健康检查接口
     */
    @GetMapping
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", "collide-application");
        result.put("timestamp", System.currentTimeMillis());
        result.put("version", "1.0.0");
        return result;
    }

    /**
     * 简单测试接口
     */
    @GetMapping("/test")
    public Map<String, String> test() {
        Map<String, String> result = new HashMap<>();
        result.put("message", "Collide Application is running!");
        result.put("status", "success");
        return result;
    }
} 