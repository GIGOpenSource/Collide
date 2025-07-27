package com.gig.collide.search.infrastructure.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Favicon控制器
 * 处理favicon.ico请求，避免404错误
 * 
 * @author GIG Team
 */
@RestController
public class FaviconController {

    /**
     * 处理favicon.ico请求
     * 返回204状态码，表示无内容
     */
    @GetMapping("/favicon.ico")
    public ResponseEntity<Void> favicon() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
} 