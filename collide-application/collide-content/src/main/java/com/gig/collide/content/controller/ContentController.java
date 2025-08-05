package com.gig.collide.content.controller;

import com.gig.collide.api.content.ContentFacadeService;
import com.gig.collide.api.content.request.ContentCreateRequest;
import com.gig.collide.api.content.request.ContentUpdateRequest;
import com.gig.collide.api.content.response.ContentResponse;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 内容管理控制器 - 极简版
 * 基于12个核心Facade方法设计的精简API
 * 
 * @author GIG Team
 * @version 2.0.0 (极简版)
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/content/core")
@RequiredArgsConstructor
@Validated
@Tag(name = "内容管理", description = "内容的创建、更新、查询、发布等管理接口（极简版）")
public class ContentController {

    private final ContentFacadeService contentFacadeService;

    // =================== 核心CRUD功能（4个API）===================

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

    // =================== 万能查询功能（3个API）===================

    @GetMapping("/query")
    @Operation(summary = "万能条件查询内容", description = "根据多种条件查询内容列表，替代所有具体查询API")
    public Result<PageResponse<ContentResponse>> queryContentsByConditions(
            @Parameter(description = "作者ID") @RequestParam(required = false) Long authorId,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "内容类型") @RequestParam(required = false) String contentType,
            @Parameter(description = "内容状态") @RequestParam(required = false) String status,
            @Parameter(description = "审核状态") @RequestParam(required = false) String reviewStatus,
            @Parameter(description = "最小评分") @RequestParam(required = false) Double minScore,
            @Parameter(description = "时间范围天数") @RequestParam(required = false) Integer timeRange,
            @Parameter(description = "排序字段（createTime、updateTime、viewCount、likeCount、favoriteCount、shareCount、commentCount、score）") @RequestParam(required = false, defaultValue = "createTime") String orderBy,
            @Parameter(description = "排序方向") @RequestParam(required = false, defaultValue = "DESC") String orderDirection,
            @Parameter(description = "当前页码", required = true) @RequestParam Integer currentPage,
            @Parameter(description = "页面大小", required = true) @RequestParam Integer pageSize) {
        try {
            return contentFacadeService.queryContentsByConditions(
                authorId, categoryId, contentType, status, reviewStatus, minScore,
                timeRange, orderBy, orderDirection, currentPage, pageSize
            );
        } catch (Exception e) {
            log.error("万能条件查询内容API调用失败", e);
            return Result.error("QUERY_CONTENTS_API_FAILED", "万能条件查询内容API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    @Operation(summary = "搜索内容", description = "根据关键词搜索内容")
    public Result<PageResponse<ContentResponse>> searchContents(
            @Parameter(description = "搜索关键词", required = true) @RequestParam String keyword,
            @Parameter(description = "内容类型") @RequestParam(required = false) String contentType,
            @Parameter(description = "当前页码", required = true) @RequestParam Integer currentPage,
            @Parameter(description = "页面大小", required = true) @RequestParam Integer pageSize) {
        try {
            return contentFacadeService.searchContents(keyword, contentType, currentPage, pageSize);
        } catch (Exception e) {
            log.error("搜索内容API调用失败: keyword={}", keyword, e);
            return Result.error("SEARCH_CONTENTS_API_FAILED", "搜索内容API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/recommended")
    @Operation(summary = "获取推荐内容", description = "基于用户行为和内容特征获取推荐内容")
    public Result<List<ContentResponse>> getRecommendedContents(
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "排除的内容ID列表") @RequestParam(required = false) List<Long> excludeContentIds,
            @Parameter(description = "返回数量限制", required = true) @RequestParam Integer limit) {
        try {
            return contentFacadeService.getRecommendedContents(userId, excludeContentIds, limit);
        } catch (Exception e) {
            log.error("获取推荐内容API调用失败", e);
            return Result.error("GET_RECOMMENDED_CONTENTS_API_FAILED", "获取推荐内容API调用失败: " + e.getMessage());
        }
    }

    // =================== 状态管理功能（2个API）===================

    @PutMapping("/{contentId}/status")
    @Operation(summary = "更新内容状态", description = "更新内容状态和审核状态")
    public Result<Boolean> updateContentStatus(
            @Parameter(description = "内容ID", required = true) @PathVariable Long contentId,
            @Parameter(description = "内容状态") @RequestParam(required = false) String status,
            @Parameter(description = "审核状态") @RequestParam(required = false) String reviewStatus,
            @Parameter(description = "操作人ID") @RequestParam(required = false) Long operatorId,
            @Parameter(description = "操作备注") @RequestParam(required = false) String comment) {
        try {
            return contentFacadeService.updateContentStatus(contentId, status, reviewStatus, operatorId, comment);
        } catch (Exception e) {
            log.error("更新内容状态API调用失败: contentId={}", contentId, e);
            return Result.error("UPDATE_CONTENT_STATUS_API_FAILED", "更新内容状态API调用失败: " + e.getMessage());
        }
    }

    @PutMapping("/batch-status")
    @Operation(summary = "批量更新状态", description = "批量更新内容状态")
    public Result<Boolean> batchUpdateStatus(
            @Parameter(description = "内容ID列表", required = true) @RequestParam List<Long> ids,
            @Parameter(description = "目标状态", required = true) @RequestParam String status) {
        try {
            return contentFacadeService.batchUpdateStatus(ids, status);
        } catch (Exception e) {
            log.error("批量更新内容状态API调用失败: ids={}, status={}", ids, status, e);
            return Result.error("BATCH_UPDATE_STATUS_API_FAILED", "批量更新内容状态API调用失败: " + e.getMessage());
        }
    }

    // =================== 统计管理功能（2个API）===================

    @PutMapping("/{contentId}/stats")
    @Operation(summary = "更新内容统计信息", description = "统一更新内容的各种统计信息")
    public Result<Boolean> updateContentStats(
            @Parameter(description = "内容ID", required = true) @PathVariable Long contentId,
            @Parameter(description = "浏览量") @RequestParam(required = false) Long viewCount,
            @Parameter(description = "点赞数") @RequestParam(required = false) Long likeCount,
            @Parameter(description = "评论数") @RequestParam(required = false) Long commentCount,
            @Parameter(description = "收藏数") @RequestParam(required = false) Long favoriteCount,
            @Parameter(description = "评分") @RequestParam(required = false) Double score) {
        try {
            return contentFacadeService.updateContentStats(contentId, viewCount, likeCount, commentCount, favoriteCount, score);
        } catch (Exception e) {
            log.error("更新内容统计信息API调用失败: contentId={}", contentId, e);
            return Result.error("UPDATE_CONTENT_STATS_API_FAILED", "更新内容统计信息API调用失败: " + e.getMessage());
        }
    }

    @PutMapping("/{contentId}/views")
    @Operation(summary = "增加浏览量", description = "增加内容的浏览量（最常用的统计操作）")
    public Result<Long> increaseViewCount(
            @Parameter(description = "内容ID", required = true) @PathVariable Long contentId,
            @Parameter(description = "增量", required = true) @RequestParam Integer increment) {
        try {
            return contentFacadeService.increaseViewCount(contentId, increment);
        } catch (Exception e) {
            log.error("增加浏览量API调用失败: contentId={}", contentId, e);
            return Result.error("INCREASE_VIEW_COUNT_API_FAILED", "增加浏览量API调用失败: " + e.getMessage());
        }
    }

    // =================== 数据同步功能（1个API）===================

    @PutMapping("/sync/{syncType}/{targetId}")
    @Operation(summary = "同步外部数据", description = "同步作者信息、分类信息等外部数据")
    public Result<Integer> syncExternalData(
            @Parameter(description = "同步类型：AUTHOR、CATEGORY", required = true) @PathVariable String syncType,
            @Parameter(description = "目标ID", required = true) @PathVariable Long targetId,
            @Parameter(description = "同步数据", required = true) @RequestBody Map<String, Object> syncData) {
        try {
            return contentFacadeService.syncExternalData(syncType, targetId, syncData);
        } catch (Exception e) {
            log.error("同步外部数据API调用失败: syncType={}, targetId={}", syncType, targetId, e);
            return Result.error("SYNC_EXTERNAL_DATA_API_FAILED", "同步外部数据API调用失败: " + e.getMessage());
        }
    }

    // =================== 便民快捷API（基于万能查询实现）===================

    @GetMapping("/author/{authorId}")
    @Operation(summary = "查询作者内容", description = "快捷API：分页查询指定作者的内容")
    public Result<PageResponse<ContentResponse>> getContentsByAuthor(
            @Parameter(description = "作者ID", required = true) @PathVariable Long authorId,
            @Parameter(description = "内容类型") @RequestParam(required = false) String contentType,
            @Parameter(description = "内容状态") @RequestParam(required = false) String status,
            @Parameter(description = "当前页码", required = true) @RequestParam Integer currentPage,
            @Parameter(description = "页面大小", required = true) @RequestParam Integer pageSize) {
        try {
            // 使用万能查询实现
            return contentFacadeService.queryContentsByConditions(
                authorId, null, contentType, status, null, null, null,
                "createTime", "DESC", currentPage, pageSize
            );
        } catch (Exception e) {
            log.error("查询作者内容API调用失败: authorId={}", authorId, e);
            return Result.error("GET_AUTHOR_CONTENTS_API_FAILED", "查询作者内容API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "查询分类内容", description = "快捷API：分页查询指定分类的内容")
    public Result<PageResponse<ContentResponse>> getContentsByCategory(
            @Parameter(description = "分类ID", required = true) @PathVariable Long categoryId,
            @Parameter(description = "内容类型") @RequestParam(required = false) String contentType,
            @Parameter(description = "当前页码", required = true) @RequestParam Integer currentPage,
            @Parameter(description = "页面大小", required = true) @RequestParam Integer pageSize) {
        try {
            // 使用万能查询实现
            return contentFacadeService.queryContentsByConditions(
                null, categoryId, contentType, "PUBLISHED", null, null, null,
                "createTime", "DESC", currentPage, pageSize
            );
        } catch (Exception e) {
            log.error("查询分类内容API调用失败: categoryId={}", categoryId, e);
            return Result.error("GET_CATEGORY_CONTENTS_API_FAILED", "查询分类内容API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/popular")
    @Operation(summary = "获取热门内容", description = "快捷API：分页获取热门内容")
    public Result<PageResponse<ContentResponse>> getPopularContents(
            @Parameter(description = "内容类型") @RequestParam(required = false) String contentType,
            @Parameter(description = "时间范围（天）") @RequestParam(required = false, defaultValue = "7") Integer timeRange,
            @Parameter(description = "当前页码", required = true) @RequestParam Integer currentPage,
            @Parameter(description = "页面大小", required = true) @RequestParam Integer pageSize) {
        try {
            // 使用万能查询实现
            return contentFacadeService.queryContentsByConditions(
                null, null, contentType, "PUBLISHED", null, null, timeRange,
                "viewCount", "DESC", currentPage, pageSize
            );
        } catch (Exception e) {
            log.error("获取热门内容API调用失败", e);
            return Result.error("GET_POPULAR_CONTENTS_API_FAILED", "获取热门内容API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/latest")
    @Operation(summary = "获取最新内容", description = "快捷API：分页获取最新发布的内容")
    public Result<PageResponse<ContentResponse>> getLatestContents(
            @Parameter(description = "内容类型") @RequestParam(required = false) String contentType,
            @Parameter(description = "当前页码", required = true) @RequestParam Integer currentPage,
            @Parameter(description = "页面大小", required = true) @RequestParam Integer pageSize) {
        try {
            // 使用万能查询实现
            return contentFacadeService.queryContentsByConditions(
                null, null, contentType, "PUBLISHED", null, null, null,
                "createTime", "DESC", currentPage, pageSize
            );
        } catch (Exception e) {
            log.error("获取最新内容API调用失败", e);
            return Result.error("GET_LATEST_CONTENTS_API_FAILED", "获取最新内容API调用失败: " + e.getMessage());
        }
    }

    @PutMapping("/{contentId}/publish")
    @Operation(summary = "发布内容", description = "快捷API：发布指定内容")
    public Result<Boolean> publishContent(
            @Parameter(description = "内容ID", required = true) @PathVariable Long contentId,
            @Parameter(description = "操作人ID", required = true) @RequestParam Long operatorId) {
        try {
            // 使用状态管理实现
            return contentFacadeService.updateContentStatus(contentId, "PUBLISHED", "APPROVED", operatorId, "发布内容");
        } catch (Exception e) {
            log.error("发布内容API调用失败: contentId={}", contentId, e);
            return Result.error("PUBLISH_CONTENT_API_FAILED", "发布内容API调用失败: " + e.getMessage());
        }
    }

    // =================== 传统同步API（基于同步功能实现）===================

    @PutMapping("/sync/author/{authorId}")
    @Operation(summary = "同步作者信息", description = "快捷API：同步更新作者信息到内容表")
    public Result<Integer> updateAuthorInfo(
            @Parameter(description = "作者ID", required = true) @PathVariable Long authorId,
            @Parameter(description = "作者昵称", required = true) @RequestParam String nickname,
            @Parameter(description = "作者头像") @RequestParam(required = false) String avatar) {
        try {
            // 使用同步功能实现
            Map<String, Object> syncData = new HashMap<>();
            syncData.put("nickname", nickname);
            if (avatar != null) {
                syncData.put("avatar", avatar);
            }
            return contentFacadeService.syncExternalData("AUTHOR", authorId, syncData);
        } catch (Exception e) {
            log.error("同步作者信息API调用失败: authorId={}", authorId, e);
            return Result.error("UPDATE_AUTHOR_INFO_API_FAILED", "同步作者信息API调用失败: " + e.getMessage());
        }
    }

    @PutMapping("/sync/category/{categoryId}")
    @Operation(summary = "同步分类信息", description = "快捷API：同步更新分类信息到内容表")
    public Result<Integer> updateCategoryInfo(
            @Parameter(description = "分类ID", required = true) @PathVariable Long categoryId,
            @Parameter(description = "分类名称", required = true) @RequestParam String categoryName) {
        try {
            // 使用同步功能实现
            Map<String, Object> syncData = new HashMap<>();
            syncData.put("categoryName", categoryName);
            return contentFacadeService.syncExternalData("CATEGORY", categoryId, syncData);
        } catch (Exception e) {
            log.error("同步分类信息API调用失败: categoryId={}", categoryId, e);
            return Result.error("UPDATE_CATEGORY_INFO_API_FAILED", "同步分类信息API调用失败: " + e.getMessage());
        }
    }
}