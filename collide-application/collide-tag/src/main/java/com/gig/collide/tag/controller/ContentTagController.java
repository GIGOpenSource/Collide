package com.gig.collide.tag.controller;

import com.gig.collide.api.tag.ContentTagFacadeService;
import com.gig.collide.api.tag.request.ContentTagRequest;
import com.gig.collide.api.tag.response.ContentTagResponse;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import java.util.Map;

/**
 * 内容标签管理控制器
 * 负责内容与标签的关联管理功能
 *
 * @author GIG Team
 * @version 3.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/content-tags")
@RequiredArgsConstructor
@Validated
@Tag(name = "内容标签管理", description = "内容与标签的关联管理功能")
public class ContentTagController {

    private final ContentTagFacadeService contentTagFacadeService;

    // =================== 内容标签基础操作 ===================

    @PostMapping
    @Operation(summary = "为内容添加标签", description = "为指定内容添加标签，包含重复检查和标签验证")
    public Result<ContentTagResponse> addContentTag(
            @Valid @RequestBody ContentTagRequest request) {
        log.info("为内容添加标签请求: {}", request);
        return contentTagFacadeService.addContentTag(request);
    }

    @DeleteMapping("/content/{contentId}/tag/{tagId}")
    @Operation(summary = "移除内容标签", description = "移除内容的指定标签")
    public Result<Void> removeContentTag(
            @Parameter(description = "内容ID", required = true)
            @PathVariable @NotNull @Positive Long contentId,
            @Parameter(description = "标签ID", required = true)
            @PathVariable @NotNull @Positive Long tagId,
            @Parameter(description = "操作人ID", required = true)
            @RequestParam @NotNull @Positive Long operatorId) {
        log.info("移除内容标签: 内容ID={}, 标签ID={}, 操作人={}", contentId, tagId, operatorId);
        return contentTagFacadeService.removeContentTag(contentId, tagId, operatorId);
    }

    @GetMapping("/content/{contentId}")
    @Operation(summary = "获取内容的标签列表", description = "获取指定内容的所有关联标签")
    public Result<List<ContentTagResponse>> getContentTags(
            @Parameter(description = "内容ID", required = true)
            @PathVariable @NotNull @Positive Long contentId) {
        log.debug("获取内容标签: 内容ID={}", contentId);
        return contentTagFacadeService.getContentTags(contentId);
    }

    @GetMapping("/tag/{tagId}/contents")
    @Operation(summary = "获取标签的内容列表", description = "获取使用指定标签的所有内容")
    public Result<List<ContentTagResponse>> getTagContents(
            @Parameter(description = "标签ID", required = true)
            @PathVariable @NotNull @Positive Long tagId) {
        log.debug("获取标签内容: 标签ID={}", tagId);
        return contentTagFacadeService.getTagContents(tagId);
    }

    // =================== 批量操作 ===================

    @PostMapping("/content/{contentId}/batch/add")
    @Operation(summary = "批量为内容添加标签", description = "批量为指定内容添加多个标签")
    public Result<Integer> batchAddContentTags(
            @Parameter(description = "内容ID", required = true)
            @PathVariable @NotNull @Positive Long contentId,
            @Parameter(description = "标签ID列表", required = true)
            @RequestBody List<Long> tagIds,
            @Parameter(description = "操作人ID", required = true)
            @RequestParam @NotNull @Positive Long operatorId) {
        log.info("批量为内容添加标签: 内容ID={}, 标签数量={}, 操作人={}", contentId, tagIds.size(), operatorId);
        return contentTagFacadeService.batchAddContentTags(contentId, tagIds, operatorId);
    }

    @PostMapping("/content/{contentId}/batch/remove")
    @Operation(summary = "批量移除内容标签", description = "批量移除内容的多个标签")
    public Result<Integer> batchRemoveContentTags(
            @Parameter(description = "内容ID", required = true)
            @PathVariable @NotNull @Positive Long contentId,
            @Parameter(description = "标签ID列表", required = true)
            @RequestBody List<Long> tagIds,
            @Parameter(description = "操作人ID", required = true)
            @RequestParam @NotNull @Positive Long operatorId) {
        log.info("批量移除内容标签: 内容ID={}, 标签数量={}, 操作人={}", contentId, tagIds.size(), operatorId);
        return contentTagFacadeService.batchRemoveContentTags(contentId, tagIds, operatorId);
    }

    @PostMapping("/content/{contentId}/replace")
    @Operation(summary = "替换内容的所有标签", description = "用新的标签列表替换内容的所有标签")
    public Result<Integer> replaceContentTags(
            @Parameter(description = "内容ID", required = true)
            @PathVariable @NotNull @Positive Long contentId,
            @Parameter(description = "新标签ID列表", required = true)
            @RequestBody List<Long> newTagIds,
            @Parameter(description = "操作人ID", required = true)
            @RequestParam @NotNull @Positive Long operatorId) {
        log.info("替换内容标签: 内容ID={}, 新标签数量={}, 操作人={}", contentId, newTagIds.size(), operatorId);
        return contentTagFacadeService.replaceContentTags(contentId, newTagIds, operatorId);
    }

    @PostMapping("/copy")
    @Operation(summary = "复制内容标签", description = "将源内容的标签复制到目标内容")
    public Result<Integer> copyContentTags(
            @Parameter(description = "源内容ID", required = true)
            @RequestParam @NotNull @Positive Long sourceContentId,
            @Parameter(description = "目标内容ID", required = true)
            @RequestParam @NotNull @Positive Long targetContentId,
            @Parameter(description = "操作人ID", required = true)
            @RequestParam @NotNull @Positive Long operatorId) {
        log.info("复制内容标签: 源内容ID={}, 目标内容ID={}, 操作人={}", sourceContentId, targetContentId, operatorId);
        return contentTagFacadeService.copyContentTags(sourceContentId, targetContentId, operatorId);
    }

    // =================== 关联查询 ===================

    @GetMapping("/content/{contentId}/related")
    @Operation(summary = "获取相关内容", description = "基于共同标签获取与指定内容相关的其他内容")
    public Result<List<Long>> getRelatedContents(
            @Parameter(description = "内容ID", required = true)
            @PathVariable @NotNull @Positive Long contentId,
            @Parameter(description = "限制数量", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {
        log.debug("获取相关内容: 内容ID={}, 限制数量={}", contentId, limit);
        return contentTagFacadeService.getRelatedContents(contentId, limit);
    }

    @GetMapping("/check/common-tags")
    @Operation(summary = "检查是否有共同标签", description = "检查两个内容是否有共同的标签")
    public Result<Boolean> hasCommonTags(
            @Parameter(description = "内容1 ID", required = true)
            @RequestParam @NotNull @Positive Long contentId1,
            @Parameter(description = "内容2 ID", required = true)
            @RequestParam @NotNull @Positive Long contentId2) {
        log.debug("检查共同标签: 内容1={}, 内容2={}", contentId1, contentId2);
        return contentTagFacadeService.hasCommonTags(contentId1, contentId2);
    }

    @GetMapping("/count/common-tags")
    @Operation(summary = "获取共同标签数量", description = "获取两个内容的共同标签数量")
    public Result<Integer> getCommonTagCount(
            @Parameter(description = "内容1 ID", required = true)
            @RequestParam @NotNull @Positive Long contentId1,
            @Parameter(description = "内容2 ID", required = true)
            @RequestParam @NotNull @Positive Long contentId2) {
        log.debug("获取共同标签数量: 内容1={}, 内容2={}", contentId1, contentId2);
        return contentTagFacadeService.getCommonTagCount(contentId1, contentId2);
    }

    // =================== 统计功能 ===================

    @GetMapping("/content/{contentId}/count")
    @Operation(summary = "统计内容标签数量", description = "统计指定内容的标签数量")
    public Result<Integer> countContentTags(
            @Parameter(description = "内容ID", required = true)
            @PathVariable @NotNull @Positive Long contentId) {
        log.debug("统计内容标签数量: 内容ID={}", contentId);
        return contentTagFacadeService.countContentTags(contentId);
    }

    @GetMapping("/tag/{tagId}/count")
    @Operation(summary = "统计标签内容数量", description = "统计使用指定标签的内容数量")
    public Result<Integer> countTagContents(
            @Parameter(description = "标签ID", required = true)
            @PathVariable @NotNull @Positive Long tagId) {
        log.debug("统计标签内容数量: 标签ID={}", tagId);
        return contentTagFacadeService.countTagContents(tagId);
    }

    @GetMapping("/recent")
    @Operation(summary = "获取最新的内容标签关联", description = "获取最近创建的内容标签关联记录")
    public Result<List<Map<String, Object>>> getRecentContentTags(
            @Parameter(description = "限制数量", example = "20")
            @RequestParam(defaultValue = "20") Integer limit) {
        log.debug("获取最新内容标签关联: 限制数量={}", limit);
        return contentTagFacadeService.getRecentContentTags(limit);
    }

    @PostMapping("/summary")
    @Operation(summary = "获取内容标签摘要", description = "批量获取多个内容的标签关联摘要信息")
    public Result<List<Map<String, Object>>> getContentTagSummary(
            @Parameter(description = "内容ID列表", required = true)
            @RequestBody List<Long> contentIds) {
        log.debug("获取内容标签摘要: 内容数量={}", contentIds.size());
        return contentTagFacadeService.getContentTagSummary(contentIds);
    }

    // =================== 内容推荐 ===================

    @GetMapping("/content/{contentId}/recommend-tags")
    @Operation(summary = "推荐内容标签", description = "基于内容特征和相似内容推荐适合的标签")
    public Result<List<Map<String, Object>>> recommendTagsForContent(
            @Parameter(description = "内容ID", required = true)
            @PathVariable @NotNull @Positive Long contentId,
            @Parameter(description = "限制数量", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {
        log.debug("推荐内容标签: 内容ID={}, 限制数量={}", contentId, limit);
        return contentTagFacadeService.recommendTagsForContent(contentId, limit);
    }

    @GetMapping("/content/{contentId}/analysis")
    @Operation(summary = "内容标签分析", description = "获取内容标签的统计分析信息")
    public Result<Map<String, Object>> getContentTagAnalysis(
            @Parameter(description = "内容ID", required = true)
            @PathVariable @NotNull @Positive Long contentId) {
        log.debug("内容标签分析: 内容ID={}", contentId);
        return contentTagFacadeService.getContentTagAnalysis(contentId);
    }

    @GetMapping("/content/{contentId}/tags-with-stats")
    @Operation(summary = "获取内容标签详细信息", description = "获取内容相关的完整标签信息，包含详情和使用统计")
    public Result<List<Map<String, Object>>> getContentTagsWithStats(
            @Parameter(description = "内容ID", required = true)
            @PathVariable @NotNull @Positive Long contentId) {
        log.debug("获取内容标签详细信息: 内容ID={}", contentId);
        return contentTagFacadeService.getContentTagsWithStats(contentId);
    }

    // =================== 数据维护 ===================

    @PostMapping("/maintenance/cleanup-invalid")
    @Operation(summary = "清理无效内容标签", description = "清理不存在的内容或标签的关联数据")
    public Result<Integer> cleanupInvalidContentTags(
            @Parameter(description = "操作人ID", required = true)
            @RequestParam @NotNull @Positive Long operatorId) {
        log.info("清理无效内容标签: 操作人={}", operatorId);
        return contentTagFacadeService.cleanupInvalidContentTags(operatorId);
    }

    @GetMapping("/health")
    @Operation(summary = "内容标签系统健康检查", description = "检查内容标签系统的健康状态")
    public Result<String> healthCheck() {
        log.debug("内容标签系统健康检查");
        return contentTagFacadeService.healthCheck();
    }
}