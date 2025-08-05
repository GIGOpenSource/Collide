package com.gig.collide.content.controller;

import com.gig.collide.api.content.ContentFacadeService;
import com.gig.collide.api.content.request.ContentCreateRequest;
import com.gig.collide.api.content.request.ContentUpdateRequest;
import com.gig.collide.api.content.request.ContentQueryRequest;
import com.gig.collide.api.content.request.ChapterCreateRequest;
import com.gig.collide.api.content.response.ContentResponse;
import com.gig.collide.api.content.response.ChapterResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 内容管理控制器
 * 提供内容的增删改查、状态管理、统计分析等REST API接口
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/content")
@RequiredArgsConstructor
@Validated
@Tag(name = "内容管理", description = "内容的创建、更新、查询、发布等管理接口")
public class ContentController {

    private final ContentFacadeService contentFacadeService;

    // =================== 内容管理 ===================

    @PostMapping
    @Operation(summary = "创建内容", description = "创建新的内容，支持多种内容类型")
    public Result<Void> createContent(
            @Parameter(description = "内容创建请求", required = true)
            @Valid @RequestBody ContentCreateRequest request) {
        try {
            return contentFacadeService.createContent(request);
        } catch (Exception e) {
            log.error("创建内容API调用失败", e);
            return Result.error("CREATE_CONTENT_API_FAILED", "创建内容API调用失败: " + e.getMessage());
        }
    }

    @PutMapping
    @Operation(summary = "更新内容", description = "更新已有内容的信息")
    public Result<ContentResponse> updateContent(
            @Parameter(description = "内容更新请求", required = true)
            @Valid @RequestBody ContentUpdateRequest request) {
        try {
            return contentFacadeService.updateContent(request);
        } catch (Exception e) {
            log.error("更新内容API调用失败", e);
            return Result.error("UPDATE_CONTENT_API_FAILED", "更新内容API调用失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{contentId}")
    @Operation(summary = "删除内容", description = "逻辑删除指定内容")
    public Result<Void> deleteContent(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId,
            @Parameter(description = "操作人ID", required = true)
            @RequestParam Long operatorId) {
        try {
            return contentFacadeService.deleteContent(contentId, operatorId);
        } catch (Exception e) {
            log.error("删除内容API调用失败: contentId={}", contentId, e);
            return Result.error("DELETE_CONTENT_API_FAILED", "删除内容API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/{contentId}")
    @Operation(summary = "获取内容详情", description = "根据内容ID获取内容详细信息")
    public Result<ContentResponse> getContentById(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId,
            @Parameter(description = "是否包含下线内容")
            @RequestParam(defaultValue = "false") Boolean includeOffline) {
        try {
            return contentFacadeService.getContentById(contentId, includeOffline);
        } catch (Exception e) {
            log.error("获取内容详情API调用失败: contentId={}", contentId, e);
            return Result.error("GET_CONTENT_API_FAILED", "获取内容详情API调用失败: " + e.getMessage());
        }
    }

    @PostMapping("/query")
    @Operation(summary = "查询内容列表", description = "根据条件分页查询内容")
    public Result<PageResponse<ContentResponse>> queryContents(
            @Parameter(description = "内容查询请求", required = true)
            @Valid @RequestBody ContentQueryRequest request) {
        try {
            return contentFacadeService.queryContents(request);
        } catch (Exception e) {
            log.error("查询内容列表API调用失败", e);
            return Result.error("QUERY_CONTENTS_API_FAILED", "查询内容列表API调用失败: " + e.getMessage());
        }
    }

    @PutMapping("/{contentId}/publish")
    @Operation(summary = "发布内容", description = "发布指定内容")
    public Result<ContentResponse> publishContent(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId,
            @Parameter(description = "作者ID", required = true)
            @RequestParam Long authorId) {
        try {
            return contentFacadeService.publishContent(contentId, authorId);
        } catch (Exception e) {
            log.error("发布内容API调用失败: contentId={}", contentId, e);
            return Result.error("PUBLISH_CONTENT_API_FAILED", "发布内容API调用失败: " + e.getMessage());
        }
    }

    @PutMapping("/{contentId}/offline")
    @Operation(summary = "下线内容", description = "下线指定内容")
    public Result<Void> offlineContent(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId,
            @Parameter(description = "操作人ID", required = true)
            @RequestParam Long operatorId) {
        try {
            return contentFacadeService.offlineContent(contentId, operatorId);
        } catch (Exception e) {
            log.error("下线内容API调用失败: contentId={}", contentId, e);
            return Result.error("OFFLINE_CONTENT_API_FAILED", "下线内容API调用失败: " + e.getMessage());
        }
    }

    // =================== 章节管理 ===================

    @PostMapping("/chapters")
    @Operation(summary = "创建章节", description = "为内容创建新章节")
    public Result<Void> createChapter(
            @Parameter(description = "章节创建请求", required = true)
            @Valid @RequestBody ChapterCreateRequest request) {
        try {
            return contentFacadeService.createChapter(request);
        } catch (Exception e) {
            log.error("创建章节API调用失败", e);
            return Result.error("CREATE_CHAPTER_API_FAILED", "创建章节API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/{contentId}/chapters")
    @Operation(summary = "获取内容章节", description = "分页获取指定内容的章节列表")
    public Result<PageResponse<ChapterResponse>> getContentChapters(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId,
            @Parameter(description = "章节状态")
            @RequestParam(required = false) String status,
            @Parameter(description = "当前页码", required = true)
            @RequestParam Integer currentPage,
            @Parameter(description = "页面大小", required = true)
            @RequestParam Integer pageSize) {
        try {
            return contentFacadeService.getContentChapters(contentId, status, currentPage, pageSize);
        } catch (Exception e) {
            log.error("获取内容章节API调用失败: contentId={}", contentId, e);
            return Result.error("GET_CHAPTERS_API_FAILED", "获取内容章节API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/chapters/{chapterId}")
    @Operation(summary = "获取章节详情", description = "根据章节ID获取章节详细信息")
    public Result<ChapterResponse> getChapterById(
            @Parameter(description = "章节ID", required = true)
            @PathVariable Long chapterId) {
        try {
            return contentFacadeService.getChapterById(chapterId);
        } catch (Exception e) {
            log.error("获取章节详情API调用失败: chapterId={}", chapterId, e);
            return Result.error("GET_CHAPTER_API_FAILED", "获取章节详情API调用失败: " + e.getMessage());
        }
    }

    @PutMapping("/chapters/{chapterId}/publish")
    @Operation(summary = "发布章节", description = "发布指定章节")
    public Result<ChapterResponse> publishChapter(
            @Parameter(description = "章节ID", required = true)
            @PathVariable Long chapterId,
            @Parameter(description = "作者ID", required = true)
            @RequestParam Long authorId) {
        try {
            return contentFacadeService.publishChapter(chapterId, authorId);
        } catch (Exception e) {
            log.error("发布章节API调用失败: chapterId={}", chapterId, e);
            return Result.error("PUBLISH_CHAPTER_API_FAILED", "发布章节API调用失败: " + e.getMessage());
        }
    }

    // =================== 统计管理 ===================

    @PutMapping("/{contentId}/views")
    @Operation(summary = "增加浏览量", description = "增加内容的浏览量")
    public Result<Long> increaseViewCount(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId,
            @Parameter(description = "增量", required = true)
            @RequestParam Integer increment) {
        try {
            return contentFacadeService.increaseViewCount(contentId, increment);
        } catch (Exception e) {
            log.error("增加浏览量API调用失败: contentId={}", contentId, e);
            return Result.error("INCREASE_VIEW_COUNT_API_FAILED", "增加浏览量API调用失败: " + e.getMessage());
        }
    }

    @PutMapping("/{contentId}/likes")
    @Operation(summary = "增加点赞数", description = "增加内容的点赞数")
    public Result<Long> increaseLikeCount(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId,
            @Parameter(description = "增量", required = true)
            @RequestParam Integer increment) {
        try {
            return contentFacadeService.increaseLikeCount(contentId, increment);
        } catch (Exception e) {
            log.error("增加点赞数API调用失败: contentId={}", contentId, e);
            return Result.error("INCREASE_LIKE_COUNT_API_FAILED", "增加点赞数API调用失败: " + e.getMessage());
        }
    }

    @PutMapping("/{contentId}/comments")
    @Operation(summary = "增加评论数", description = "增加内容的评论数")
    public Result<Long> increaseCommentCount(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId,
            @Parameter(description = "增量", required = true)
            @RequestParam Integer increment) {
        try {
            return contentFacadeService.increaseCommentCount(contentId, increment);
        } catch (Exception e) {
            log.error("增加评论数API调用失败: contentId={}", contentId, e);
            return Result.error("INCREASE_COMMENT_COUNT_API_FAILED", "增加评论数API调用失败: " + e.getMessage());
        }
    }

    @PutMapping("/{contentId}/favorites")
    @Operation(summary = "增加收藏数", description = "增加内容的收藏数")
    public Result<Long> increaseFavoriteCount(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId,
            @Parameter(description = "增量", required = true)
            @RequestParam Integer increment) {
        try {
            return contentFacadeService.increaseFavoriteCount(contentId, increment);
        } catch (Exception e) {
            log.error("增加收藏数API调用失败: contentId={}", contentId, e);
            return Result.error("INCREASE_FAVORITE_COUNT_API_FAILED", "增加收藏数API调用失败: " + e.getMessage());
        }
    }

    @PutMapping("/{contentId}/score")
    @Operation(summary = "更新评分", description = "更新内容的评分")
    public Result<Double> updateScore(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId,
            @Parameter(description = "评分", required = true)
            @RequestParam Integer score) {
        try {
            return contentFacadeService.updateScore(contentId, score);
        } catch (Exception e) {
            log.error("更新评分API调用失败: contentId={}", contentId, e);
            return Result.error("UPDATE_SCORE_API_FAILED", "更新评分API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/{contentId}/statistics")
    @Operation(summary = "获取内容统计", description = "获取内容的统计信息")
    public Result<Map<String, Object>> getContentStatistics(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId) {
        try {
            return contentFacadeService.getContentStatistics(contentId);
        } catch (Exception e) {
            log.error("获取内容统计API调用失败: contentId={}", contentId, e);
            return Result.error("GET_STATISTICS_API_FAILED", "获取内容统计API调用失败: " + e.getMessage());
        }
    }

    // =================== 内容查询 ===================

    @GetMapping("/author/{authorId}")
    @Operation(summary = "查询作者内容", description = "分页查询指定作者的内容")
    public Result<PageResponse<ContentResponse>> getContentsByAuthor(
            @Parameter(description = "作者ID", required = true)
            @PathVariable Long authorId,
            @Parameter(description = "内容类型")
            @RequestParam(required = false) String contentType,
            @Parameter(description = "内容状态")
            @RequestParam(required = false) String status,
            @Parameter(description = "当前页码", required = true)
            @RequestParam Integer currentPage,
            @Parameter(description = "页面大小", required = true)
            @RequestParam Integer pageSize) {
        try {
            return contentFacadeService.getContentsByAuthor(authorId, contentType, status, currentPage, pageSize);
        } catch (Exception e) {
            log.error("查询作者内容API调用失败: authorId={}", authorId, e);
            return Result.error("GET_AUTHOR_CONTENTS_API_FAILED", "查询作者内容API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "查询分类内容", description = "分页查询指定分类的内容")
    public Result<PageResponse<ContentResponse>> getContentsByCategory(
            @Parameter(description = "分类ID", required = true)
            @PathVariable Long categoryId,
            @Parameter(description = "内容类型")
            @RequestParam(required = false) String contentType,
            @Parameter(description = "当前页码", required = true)
            @RequestParam Integer currentPage,
            @Parameter(description = "页面大小", required = true)
            @RequestParam Integer pageSize) {
        try {
            return contentFacadeService.getContentsByCategory(categoryId, contentType, currentPage, pageSize);
        } catch (Exception e) {
            log.error("查询分类内容API调用失败: categoryId={}", categoryId, e);
            return Result.error("GET_CATEGORY_CONTENTS_API_FAILED", "查询分类内容API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    @Operation(summary = "搜索内容", description = "根据关键词搜索内容")
    public Result<PageResponse<ContentResponse>> searchContents(
            @Parameter(description = "搜索关键词", required = true)
            @RequestParam String keyword,
            @Parameter(description = "内容类型")
            @RequestParam(required = false) String contentType,
            @Parameter(description = "当前页码", required = true)
            @RequestParam Integer currentPage,
            @Parameter(description = "页面大小", required = true)
            @RequestParam Integer pageSize) {
        try {
            return contentFacadeService.searchContents(keyword, contentType, currentPage, pageSize);
        } catch (Exception e) {
            log.error("搜索内容API调用失败: keyword={}", keyword, e);
            return Result.error("SEARCH_CONTENTS_API_FAILED", "搜索内容API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/popular")
    @Operation(summary = "获取热门内容", description = "分页获取热门内容")
    public Result<PageResponse<ContentResponse>> getPopularContents(
            @Parameter(description = "内容类型")
            @RequestParam(required = false) String contentType,
            @Parameter(description = "时间范围（天）")
            @RequestParam(required = false) Integer timeRange,
            @Parameter(description = "当前页码", required = true)
            @RequestParam Integer currentPage,
            @Parameter(description = "页面大小", required = true)
            @RequestParam Integer pageSize) {
        try {
            return contentFacadeService.getPopularContents(contentType, timeRange, currentPage, pageSize);
        } catch (Exception e) {
            log.error("获取热门内容API调用失败", e);
            return Result.error("GET_POPULAR_CONTENTS_API_FAILED", "获取热门内容API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/latest")
    @Operation(summary = "获取最新内容", description = "分页获取最新发布的内容")
    public Result<PageResponse<ContentResponse>> getLatestContents(
            @Parameter(description = "内容类型")
            @RequestParam(required = false) String contentType,
            @Parameter(description = "当前页码", required = true)
            @RequestParam Integer currentPage,
            @Parameter(description = "页面大小", required = true)
            @RequestParam Integer pageSize) {
        try {
            return contentFacadeService.getLatestContents(contentType, currentPage, pageSize);
        } catch (Exception e) {
            log.error("获取最新内容API调用失败", e);
            return Result.error("GET_LATEST_CONTENTS_API_FAILED", "获取最新内容API调用失败: " + e.getMessage());
        }
    }

    // =================== 数据同步 ===================

    @PutMapping("/sync/author/{authorId}")
    @Operation(summary = "同步作者信息", description = "同步更新作者信息到内容表")
    public Result<Integer> updateAuthorInfo(
            @Parameter(description = "作者ID", required = true)
            @PathVariable Long authorId,
            @Parameter(description = "作者昵称", required = true)
            @RequestParam String nickname,
            @Parameter(description = "作者头像")
            @RequestParam(required = false) String avatar) {
        try {
            return contentFacadeService.updateAuthorInfo(authorId, nickname, avatar);
        } catch (Exception e) {
            log.error("同步作者信息API调用失败: authorId={}", authorId, e);
            return Result.error("UPDATE_AUTHOR_INFO_API_FAILED", "同步作者信息API调用失败: " + e.getMessage());
        }
    }

    @PutMapping("/sync/category/{categoryId}")
    @Operation(summary = "同步分类信息", description = "同步更新分类信息到内容表")
    public Result<Integer> updateCategoryInfo(
            @Parameter(description = "分类ID", required = true)
            @PathVariable Long categoryId,
            @Parameter(description = "分类名称", required = true)
            @RequestParam String categoryName) {
        try {
            return contentFacadeService.updateCategoryInfo(categoryId, categoryName);
        } catch (Exception e) {
            log.error("同步分类信息API调用失败: categoryId={}", categoryId, e);
            return Result.error("UPDATE_CATEGORY_INFO_API_FAILED", "同步分类信息API调用失败: " + e.getMessage());
        }
    }

    @PutMapping("/{contentId}/review")
    @Operation(summary = "审核内容", description = "审核指定内容")
    public Result<ContentResponse> reviewContent(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId,
            @Parameter(description = "审核状态", required = true)
            @RequestParam String reviewStatus,
            @Parameter(description = "审核人ID", required = true)
            @RequestParam Long reviewerId,
            @Parameter(description = "审核意见")
            @RequestParam(required = false) String reviewComment) {
        try {
            return contentFacadeService.reviewContent(contentId, reviewStatus, reviewerId, reviewComment);
        } catch (Exception e) {
            log.error("审核内容API调用失败: contentId={}", contentId, e);
            return Result.error("REVIEW_CONTENT_API_FAILED", "审核内容API调用失败: " + e.getMessage());
        }
    }
}