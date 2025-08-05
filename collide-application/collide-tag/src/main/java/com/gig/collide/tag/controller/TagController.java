package com.gig.collide.tag.controller;

import com.gig.collide.api.tag.TagFacadeService;
import com.gig.collide.api.tag.request.TagCreateRequest;
import com.gig.collide.api.tag.request.TagQueryRequest;
import com.gig.collide.api.tag.request.TagUpdateRequest;
import com.gig.collide.api.tag.response.TagResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import java.util.Map;

/**
 * 标签管理控制器
 * 负责基础标签的增删改查和管理功能
 *
 * @author GIG Team
 * @version 3.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
@Validated
@Tag(name = "标签管理", description = "基础标签的增删改查和管理功能")
public class TagController {

    private final TagFacadeService tagFacadeService;

    // =================== 标签基础管理 ===================

    @PostMapping
    @Operation(summary = "创建标签", description = "创建新的标签，包含唯一性验证")
    public Result<TagResponse> createTag(
            @Valid @RequestBody TagCreateRequest request) {
        log.info("创建标签请求: {}", request);
        return tagFacadeService.createTag(request);
    }

    @PutMapping("/{tagId}")
    @Operation(summary = "更新标签", description = "更新标签信息")
    public Result<TagResponse> updateTag(
            @Parameter(description = "标签ID", required = true)
            @PathVariable @NotNull @Positive Long tagId,
            @Valid @RequestBody TagUpdateRequest request) {
        log.info("更新标签请求: ID={}, request={}", tagId, request);
        request.setId(tagId);
        return tagFacadeService.updateTag(request);
    }

    @DeleteMapping("/{tagId}")
    @Operation(summary = "删除标签", description = "逻辑删除标签，包含关联数据检查")
    public Result<Void> deleteTag(
            @Parameter(description = "标签ID", required = true)
            @PathVariable @NotNull @Positive Long tagId,
            @Parameter(description = "操作人ID", required = true)
            @RequestParam @NotNull @Positive Long operatorId) {
        log.info("删除标签请求: ID={}, 操作人={}", tagId, operatorId);
        return tagFacadeService.deleteTag(tagId, operatorId);
    }

    @GetMapping("/{tagId}")
    @Operation(summary = "查询标签详情", description = "根据ID查询标签详细信息")
    public Result<TagResponse> getTagById(
            @Parameter(description = "标签ID", required = true)
            @PathVariable @NotNull @Positive Long tagId) {
        log.debug("查询标签详情: ID={}", tagId);
        return tagFacadeService.getTagById(tagId);
    }

    @PostMapping("/query")
    @Operation(summary = "分页查询标签", description = "支持多条件组合查询的标签分页列表")
    public Result<PageResponse<TagResponse>> queryTags(
            @Valid @RequestBody TagQueryRequest request) {
        log.debug("分页查询标签: {}", request);
        return tagFacadeService.queryTags(request);
    }

    // =================== 标签查询功能 ===================

    @GetMapping("/type/{tagType}")
    @Operation(summary = "根据类型查询标签", description = "获取指定类型的标签列表")
    public Result<List<TagResponse>> getTagsByType(
            @Parameter(description = "标签类型", required = true)
            @PathVariable @NotBlank String tagType) {
        log.debug("根据类型查询标签: type={}", tagType);
        return tagFacadeService.getTagsByType(tagType);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索标签", description = "智能搜索标签，先精确匹配再模糊搜索")
    public Result<List<TagResponse>> searchTags(
            @Parameter(description = "搜索关键词", required = true)
            @RequestParam @NotBlank String keyword,
            @Parameter(description = "限制数量", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {
        log.debug("搜索标签: keyword={}, limit={}", keyword, limit);
        return tagFacadeService.searchTags(keyword, limit);
    }

    @GetMapping("/search/exact")
    @Operation(summary = "精确搜索标签", description = "按名称精确搜索标签")
    public Result<List<TagResponse>> searchTagsByNameExact(
            @Parameter(description = "搜索关键词", required = true)
            @RequestParam @NotBlank String keyword,
            @Parameter(description = "限制数量", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {
        log.debug("精确搜索标签: keyword={}, limit={}", keyword, limit);
        return tagFacadeService.searchTagsByNameExact(keyword, limit);
    }

    @GetMapping("/hot")
    @Operation(summary = "获取热门标签", description = "按使用次数排序的热门标签")
    public Result<List<TagResponse>> getHotTags(
            @Parameter(description = "限制数量", example = "20")
            @RequestParam(defaultValue = "20") Integer limit) {
        log.debug("获取热门标签: limit={}", limit);
        return tagFacadeService.getHotTags(limit);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "根据分类查询标签", description = "获取指定分类下的标签列表")
    public Result<List<TagResponse>> getTagsByCategory(
            @Parameter(description = "分类ID", required = true)
            @PathVariable @NotNull @Positive Long categoryId) {
        log.debug("根据分类查询标签: categoryId={}", categoryId);
        return tagFacadeService.getTagsByCategory(categoryId);
    }

    @GetMapping("/active")
    @Operation(summary = "获取活跃标签", description = "获取指定分类下的活跃标签")
    public Result<List<TagResponse>> getActiveTags(
            @Parameter(description = "分类ID")
            @RequestParam(required = false) Long categoryId) {
        log.debug("获取活跃标签: categoryId={}", categoryId);
        return tagFacadeService.getActiveTags(categoryId);
    }

    // =================== 标签状态管理 ===================

    @PostMapping("/{tagId}/activate")
    @Operation(summary = "激活标签", description = "激活指定的标签")
    public Result<Void> activateTag(
            @Parameter(description = "标签ID", required = true)
            @PathVariable @NotNull @Positive Long tagId,
            @Parameter(description = "操作人ID", required = true)
            @RequestParam @NotNull @Positive Long operatorId) {
        log.info("激活标签: ID={}, 操作人={}", tagId, operatorId);
        return tagFacadeService.activateTag(tagId, operatorId);
    }

    @PostMapping("/{tagId}/deactivate")
    @Operation(summary = "停用标签", description = "停用指定的标签")
    public Result<Void> deactivateTag(
            @Parameter(description = "标签ID", required = true)
            @PathVariable @NotNull @Positive Long tagId,
            @Parameter(description = "操作人ID", required = true)
            @RequestParam @NotNull @Positive Long operatorId) {
        log.info("停用标签: ID={}, 操作人={}", tagId, operatorId);
        return tagFacadeService.deactivateTag(tagId, operatorId);
    }

    @PostMapping("/batch/status")
    @Operation(summary = "批量更新标签状态", description = "批量更新多个标签的状态")
    public Result<Integer> batchUpdateTagStatus(
            @Parameter(description = "标签ID列表", required = true)
            @RequestParam List<Long> tagIds,
            @Parameter(description = "状态", required = true)
            @RequestParam @NotBlank String status,
            @Parameter(description = "操作人ID", required = true)
            @RequestParam @NotNull @Positive Long operatorId) {
        log.info("批量更新标签状态: tagIds={}, status={}, 操作人={}", tagIds, status, operatorId);
        return tagFacadeService.batchUpdateTagStatus(tagIds, status, operatorId);
    }

    // =================== 标签使用统计 ===================

    @PostMapping("/{tagId}/usage/increase")
    @Operation(summary = "增加标签使用次数", description = "增加指定标签的使用次数")
    public Result<Void> increaseTagUsage(
            @Parameter(description = "标签ID", required = true)
            @PathVariable @NotNull @Positive Long tagId) {
        log.debug("增加标签使用次数: ID={}", tagId);
        return tagFacadeService.increaseTagUsage(tagId);
    }

    @PostMapping("/{tagId}/usage/decrease")
    @Operation(summary = "减少标签使用次数", description = "减少指定标签的使用次数")
    public Result<Void> decreaseTagUsage(
            @Parameter(description = "标签ID", required = true)
            @PathVariable @NotNull @Positive Long tagId) {
        log.debug("减少标签使用次数: ID={}", tagId);
        return tagFacadeService.decreaseTagUsage(tagId);
    }

    @GetMapping("/stats/usage")
    @Operation(summary = "获取标签使用统计", description = "获取标签使用情况的统计信息")
    public Result<List<Map<String, Object>>> getTagUsageStats(
            @Parameter(description = "标签类型")
            @RequestParam(required = false) String tagType,
            @Parameter(description = "限制数量", example = "50")
            @RequestParam(defaultValue = "50") Integer limit) {
        log.debug("获取标签使用统计: tagType={}, limit={}", tagType, limit);
        return tagFacadeService.getTagUsageStats(tagType, limit);
    }

    @PostMapping("/summary")
    @Operation(summary = "批量获取标签基本信息", description = "批量获取多个标签的基本信息")
    public Result<List<Map<String, Object>>> getTagSummary(
            @Parameter(description = "标签ID列表", required = true)
            @RequestBody List<Long> tagIds) {
        log.debug("批量获取标签基本信息: tagIds={}", tagIds);
        return tagFacadeService.getTagSummary(tagIds);
    }

    // =================== 标签检查功能 ===================

    @GetMapping("/check/exists")
    @Operation(summary = "检查标签是否存在", description = "检查指定名称和类型的标签是否已存在")
    public Result<Boolean> checkTagExists(
            @Parameter(description = "标签名称", required = true)
            @RequestParam @NotBlank String name,
            @Parameter(description = "标签类型", required = true)
            @RequestParam @NotBlank String tagType) {
        log.debug("检查标签是否存在: name={}, tagType={}", name, tagType);
        return tagFacadeService.checkTagExists(name, tagType);
    }

    @GetMapping("/{tagId}/check/deletable")
    @Operation(summary = "检查标签是否可删除", description = "检查标签是否有关联数据，是否可以删除")
    public Result<Boolean> canDeleteTag(
            @Parameter(description = "标签ID", required = true)
            @PathVariable @NotNull @Positive Long tagId) {
        log.debug("检查标签是否可删除: ID={}", tagId);
        return tagFacadeService.canDeleteTag(tagId);
    }

    // =================== 标签云和推荐 ===================

    @GetMapping("/cloud")
    @Operation(summary = "获取标签云", description = "获取标签云数据，包含标签和权重信息")
    public Result<List<Map<String, Object>>> getTagCloud(
            @Parameter(description = "标签类型")
            @RequestParam(required = false) String tagType,
            @Parameter(description = "限制数量", example = "100")
            @RequestParam(defaultValue = "100") Integer limit) {
        log.debug("获取标签云: tagType={}, limit={}", tagType, limit);
        return tagFacadeService.getTagCloud(tagType, limit);
    }

    @GetMapping("/{tagId}/similar")
    @Operation(summary = "获取相似标签", description = "基于标签使用模式和分类获取相似标签")
    public Result<List<TagResponse>> getSimilarTags(
            @Parameter(description = "标签ID", required = true)
            @PathVariable @NotNull @Positive Long tagId,
            @Parameter(description = "限制数量", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {
        log.debug("获取相似标签: tagId={}, limit={}", tagId, limit);
        return tagFacadeService.getSimilarTags(tagId, limit);
    }

    // =================== 数据维护 ===================

    @PostMapping("/maintenance/recalculate-usage")
    @Operation(summary = "重新计算标签使用次数", description = "基于实际关联数据重新统计标签使用次数")
    public Result<Integer> recalculateTagUsageCounts(
            @Parameter(description = "操作人ID", required = true)
            @RequestParam @NotNull @Positive Long operatorId) {
        log.info("重新计算标签使用次数: 操作人={}", operatorId);
        return tagFacadeService.recalculateTagUsageCounts(operatorId);
    }

    @PostMapping("/maintenance/merge-duplicates")
    @Operation(summary = "合并重复标签", description = "将重复的标签合并到主标签")
    public Result<Integer> mergeDuplicateTags(
            @Parameter(description = "主标签ID", required = true)
            @RequestParam @NotNull @Positive Long mainTagId,
            @Parameter(description = "重复标签ID列表", required = true)
            @RequestParam List<Long> duplicateTagIds,
            @Parameter(description = "操作人ID", required = true)
            @RequestParam @NotNull @Positive Long operatorId) {
        log.info("合并重复标签: mainTagId={}, duplicateTagIds={}, 操作人={}", mainTagId, duplicateTagIds, operatorId);
        return tagFacadeService.mergeDuplicateTags(mainTagId, duplicateTagIds, operatorId);
    }

    @PostMapping("/maintenance/cleanup-unused")
    @Operation(summary = "清理无效标签", description = "清理没有任何关联的废弃标签")
    public Result<Integer> cleanupUnusedTags(
            @Parameter(description = "操作人ID", required = true)
            @RequestParam @NotNull @Positive Long operatorId) {
        log.info("清理无效标签: 操作人={}", operatorId);
        return tagFacadeService.cleanupUnusedTags(operatorId);
    }

    // =================== 系统功能 ===================

    @GetMapping("/types")
    @Operation(summary = "获取所有标签类型", description = "获取系统中所有的标签类型列表")
    public Result<List<String>> getAllTagTypes() {
        log.debug("获取所有标签类型");
        return tagFacadeService.getAllTagTypes();
    }

    @GetMapping("/system/stats")
    @Operation(summary = "获取标签系统统计", description = "获取标签系统的整体统计信息")
    public Result<Map<String, Object>> getTagSystemStats() {
        log.debug("获取标签系统统计");
        return tagFacadeService.getTagSystemStats();
    }

    @GetMapping("/health")
    @Operation(summary = "标签系统健康检查", description = "检查标签系统的健康状态")
    public Result<String> healthCheck() {
        log.debug("标签系统健康检查");
        return tagFacadeService.healthCheck();
    }
}