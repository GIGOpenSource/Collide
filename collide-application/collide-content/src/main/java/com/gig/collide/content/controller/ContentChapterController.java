package com.gig.collide.content.controller;

import com.gig.collide.api.content.ContentChapterFacadeService;
import com.gig.collide.api.content.response.ChapterResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 内容章节控制器 - 极简版
 * 基于8个核心Facade方法设计的精简API
 * 
 * @author GIG Team
 * @version 2.0.0 (极简版)
 * @since 2024-01-31
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/content/chapters")
@RequiredArgsConstructor
@Validated
@Tag(name = "内容章节管理", description = "内容章节的查询、统计和管理接口（极简版）")
public class ContentChapterController {

    private final ContentChapterFacadeService contentChapterFacadeService;

    // =================== 核心CRUD功能（2个API）===================

    @GetMapping("/{id}")
    @Operation(summary = "获取章节详情", description = "根据章节ID获取章节详情")
    public Result<ChapterResponse> getChapterById(
            @Parameter(description = "章节ID", required = true)
            @PathVariable Long id) {
        try {
            return contentChapterFacadeService.getChapterById(id);
        } catch (Exception e) {
            log.error("获取章节详情API调用失败: id={}", id, e);
            return Result.error("GET_CHAPTER_API_FAILED", "获取章节详情API调用失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除章节", description = "逻辑删除指定章节")
    public Result<Boolean> deleteChapter(
            @Parameter(description = "章节ID", required = true)
            @PathVariable Long id) {
        try {
            return contentChapterFacadeService.deleteChapter(id);
        } catch (Exception e) {
            log.error("删除章节API调用失败: id={}", id, e);
            return Result.error("DELETE_CHAPTER_API_FAILED", "删除章节API调用失败: " + e.getMessage());
        }
    }

    // =================== 万能查询功能（3个API）===================

    @GetMapping("/query")
    @Operation(summary = "万能条件查询章节", description = "根据多种条件查询章节列表，替代所有具体查询API")
    public Result<PageResponse<ChapterResponse>> getChaptersByConditions(
            @Parameter(description = "内容ID") @RequestParam(required = false) Long contentId,
            @Parameter(description = "章节状态") @RequestParam(required = false) String status,
            @Parameter(description = "章节号起始") @RequestParam(required = false) Integer chapterNumStart,
            @Parameter(description = "章节号结束") @RequestParam(required = false) Integer chapterNumEnd,
            @Parameter(description = "最小字数") @RequestParam(required = false) Integer minWordCount,
            @Parameter(description = "最大字数") @RequestParam(required = false) Integer maxWordCount,
            @Parameter(description = "排序字段") @RequestParam(required = false, defaultValue = "chapterNum") String orderBy,
            @Parameter(description = "排序方向") @RequestParam(required = false, defaultValue = "ASC") String orderDirection,
            @Parameter(description = "当前页码") @RequestParam(required = false) Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(required = false) Integer pageSize) {
        try {
            return contentChapterFacadeService.getChaptersByConditions(
                contentId, status, chapterNumStart, chapterNumEnd,
                minWordCount, maxWordCount, orderBy, orderDirection,
                currentPage, pageSize
            );
        } catch (Exception e) {
            log.error("万能条件查询章节API调用失败", e);
            return Result.error("QUERY_CHAPTERS_API_FAILED", "万能条件查询章节API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/navigation")
    @Operation(summary = "章节导航查询", description = "获取上一章、下一章、第一章、最后一章")
    public Result<ChapterResponse> getChapterByNavigation(
            @Parameter(description = "内容ID", required = true) @RequestParam Long contentId,
            @Parameter(description = "当前章节号", required = true) @RequestParam Integer currentChapterNum,
            @Parameter(description = "导航方向：PREVIOUS、NEXT、FIRST、LAST", required = true) @RequestParam String direction) {
        try {
            return contentChapterFacadeService.getChapterByNavigation(contentId, currentChapterNum, direction);
        } catch (Exception e) {
            log.error("章节导航查询API调用失败: contentId={}, direction={}", contentId, direction, e);
            return Result.error("CHAPTER_NAVIGATION_API_FAILED", "章节导航查询API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    @Operation(summary = "搜索章节", description = "根据关键词搜索章节")
    public Result<PageResponse<ChapterResponse>> searchChapters(
            @Parameter(description = "搜索关键词", required = true) @RequestParam String keyword,
            @Parameter(description = "内容ID") @RequestParam(required = false) Long contentId,
            @Parameter(description = "章节状态") @RequestParam(required = false) String status,
            @Parameter(description = "当前页码") @RequestParam(required = false) Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(required = false) Integer pageSize) {
        try {
            return contentChapterFacadeService.searchChapters(keyword, contentId, status, currentPage, pageSize);
        } catch (Exception e) {
            log.error("搜索章节API调用失败: keyword={}", keyword, e);
            return Result.error("SEARCH_CHAPTERS_API_FAILED", "搜索章节API调用失败: " + e.getMessage());
        }
    }

    // =================== 统计功能（1个API）===================

    @GetMapping("/{contentId}/stats")
    @Operation(summary = "获取章节统计信息", description = "获取指定内容的章节统计信息")
    public Result<Map<String, Object>> getChapterStats(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId) {
        try {
            return contentChapterFacadeService.getChapterStats(contentId);
        } catch (Exception e) {
            log.error("获取章节统计信息API调用失败: contentId={}", contentId, e);
            return Result.error("GET_CHAPTER_STATS_API_FAILED", "获取章节统计信息API调用失败: " + e.getMessage());
        }
    }

    // =================== 批量操作功能（2个API）===================

    @PutMapping("/batch-status")
    @Operation(summary = "批量更新章节状态", description = "批量更新指定章节的状态")
    public Result<Boolean> batchUpdateChapterStatus(
            @Parameter(description = "章节ID列表", required = true) @RequestParam List<Long> ids,
            @Parameter(description = "目标状态", required = true) @RequestParam String status) {
        try {
            return contentChapterFacadeService.batchUpdateChapterStatus(ids, status);
        } catch (Exception e) {
            log.error("批量更新章节状态API调用失败: ids={}, status={}", ids, status, e);
            return Result.error("BATCH_UPDATE_STATUS_API_FAILED", "批量更新章节状态API调用失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除章节", description = "批量删除指定章节")
    public Result<Boolean> batchDeleteChapters(
            @Parameter(description = "章节ID列表", required = true) @RequestParam List<Long> ids) {
        try {
            return contentChapterFacadeService.batchDeleteChapters(ids);
        } catch (Exception e) {
            log.error("批量删除章节API调用失败: ids={}", ids, e);
            return Result.error("BATCH_DELETE_CHAPTERS_API_FAILED", "批量删除章节API调用失败: " + e.getMessage());
        }
    }

    // =================== 便民快捷API（基于万能查询实现）===================
    
    @GetMapping("/content/{contentId}")
    @Operation(summary = "获取内容的章节列表", description = "快捷API：获取指定内容的所有章节")
    public Result<PageResponse<ChapterResponse>> getChaptersByContentId(
            @Parameter(description = "内容ID", required = true) @PathVariable Long contentId,
            @Parameter(description = "当前页码") @RequestParam(required = false) Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(required = false) Integer pageSize) {
        try {
            // 使用万能查询实现
            return contentChapterFacadeService.getChaptersByConditions(
                contentId, null, null, null, null, null,
                "chapterNum", "ASC", currentPage, pageSize
            );
        } catch (Exception e) {
            log.error("获取内容章节列表API调用失败: contentId={}", contentId, e);
            return Result.error("GET_CONTENT_CHAPTERS_API_FAILED", "获取内容章节列表API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/content/{contentId}/published")
    @Operation(summary = "获取已发布章节", description = "快捷API：获取指定内容的已发布章节")
    public Result<PageResponse<ChapterResponse>> getPublishedChaptersByContentId(
            @Parameter(description = "内容ID", required = true) @PathVariable Long contentId,
            @Parameter(description = "当前页码") @RequestParam(required = false) Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(required = false) Integer pageSize) {
        try {
            // 使用万能查询实现
            return contentChapterFacadeService.getChaptersByConditions(
                contentId, "PUBLISHED", null, null, null, null,
                "chapterNum", "ASC", currentPage, pageSize
            );
        } catch (Exception e) {
            log.error("获取已发布章节API调用失败: contentId={}", contentId, e);
            return Result.error("GET_PUBLISHED_CHAPTERS_API_FAILED", "获取已发布章节API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/content/{contentId}/latest")
    @Operation(summary = "获取最新章节", description = "快捷API：获取指定内容的最新章节")
    public Result<ChapterResponse> getLatestChapterByContentId(
            @Parameter(description = "内容ID", required = true) @PathVariable Long contentId) {
        try {
            // 使用导航查询实现
            return contentChapterFacadeService.getChapterByNavigation(contentId, 999999, "LAST");
        } catch (Exception e) {
            log.error("获取最新章节API调用失败: contentId={}", contentId, e);
            return Result.error("GET_LATEST_CHAPTER_API_FAILED", "获取最新章节API调用失败: " + e.getMessage());
        }
    }
}