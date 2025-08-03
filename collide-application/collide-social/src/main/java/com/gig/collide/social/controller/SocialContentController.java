package com.gig.collide.social.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.social.domain.entity.SocialContent;
import com.gig.collide.social.domain.service.InteractionStatsService;
import com.gig.collide.social.domain.service.SocialContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 社交内容控制器 - 简化版
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
@Slf4j
@RestController
@RequestMapping("/api/social/content")
@RequiredArgsConstructor
public class SocialContentController {

    private final SocialContentService contentService;
    private final InteractionStatsService statsService;

    /**
     * 创建内容
     */
    @PostMapping("/create")
    public ApiResult<Long> createContent(@RequestBody SocialContent content) {
        Long contentId = contentService.createContent(content);
        return ApiResult.success(contentId);
    }

    /**
     * 更新内容
     */
    @PutMapping("/update")
    public ApiResult<Boolean> updateContent(@RequestBody SocialContent content) {
        boolean result = contentService.updateContent(content);
        return ApiResult.success(result);
    }

    /**
     * 删除内容
     */
    @DeleteMapping("/{contentId}")
    public ApiResult<Boolean> deleteContent(@PathVariable Long contentId, @RequestParam Long userId) {
        boolean result = contentService.deleteContent(contentId, userId);
        return ApiResult.success(result);
    }

    /**
     * 获取内容详情
     */
    @GetMapping("/{contentId}")
    public ApiResult<SocialContent> getContent(@PathVariable Long contentId, @RequestParam(required = false) Long viewerUserId) {
        SocialContent content = contentService.getById(contentId);
        if (content != null && viewerUserId != null) {
            // 增加浏览数
            statsService.incrementViewCount(contentId);
        }
        return ApiResult.success(content);
    }

    /**
     * 获取用户的内容列表
     */
    @GetMapping("/user/{userId}")
    public ApiResult<IPage<SocialContent>> getUserContent(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        IPage<SocialContent> result = contentService.getByUserId(userId, pageNum, pageSize);
        return ApiResult.success(result);
    }

    /**
     * 获取分类下的内容
     */
    @GetMapping("/category/{categoryId}")
    public ApiResult<IPage<SocialContent>> getCategoryContent(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        IPage<SocialContent> result = contentService.getByCategoryId(categoryId, pageNum, pageSize);
        return ApiResult.success(result);
    }

    /**
     * 获取热门内容
     */
    @GetMapping("/hot")
    public ApiResult<IPage<SocialContent>> getHotContent(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        IPage<SocialContent> result = contentService.getHotContent(pageNum, pageSize);
        return ApiResult.success(result);
    }

    /**
     * 获取最新内容
     */
    @GetMapping("/latest")
    public ApiResult<IPage<SocialContent>> getLatestContent(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        IPage<SocialContent> result = contentService.getLatestContent(pageNum, pageSize);
        return ApiResult.success(result);
    }

    /**
     * 搜索内容
     */
    @GetMapping("/search")
    public ApiResult<IPage<SocialContent>> searchContent(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        IPage<SocialContent> result = contentService.searchContent(keyword, pageNum, pageSize);
        return ApiResult.success(result);
    }

    /**
     * 批量获取内容
     */
    @PostMapping("/batch")
    public ApiResult<List<SocialContent>> getBatchContent(@RequestBody List<Long> contentIds) {
        List<SocialContent> result = contentService.getBatchByIds(contentIds);
        return ApiResult.success(result);
    }

    /**
     * 获取内容统计信息
     */
    @GetMapping("/{contentId}/stats")
    public ApiResult<InteractionStatsService.ContentStats> getContentStats(@PathVariable Long contentId) {
        InteractionStatsService.ContentStats stats = statsService.getContentStats(contentId);
        return ApiResult.success(stats);
    }

    /**
     * 通用API响应结果
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class ApiResult<T> {
        private int code;
        private String message;
        private T data;

        public static <T> ApiResult<T> success(T data) {
            return new ApiResult<>(200, "success", data);
        }

        public static <T> ApiResult<T> error(String message) {
            return new ApiResult<>(500, message, null);
        }
    }
}