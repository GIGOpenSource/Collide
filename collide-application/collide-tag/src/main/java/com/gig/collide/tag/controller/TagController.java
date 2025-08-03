package com.gig.collide.tag.controller;

import com.gig.collide.api.tag.TagFacadeService;
import com.gig.collide.api.tag.request.TagCreateRequest;
import com.gig.collide.api.tag.request.TagQueryRequest;
import com.gig.collide.api.tag.request.TagUpdateRequest;
import com.gig.collide.api.tag.response.TagResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 标签管理控制器
 * 
 * @author GIG Team
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
@Tag(name = "标签管理", description = "标签的增删改查和统计分析")
public class TagController {

    @DubboReference(version = "1.0.0")
    private TagFacadeService tagFacadeService;

    // =================== 标签基础管理 ===================

    /**
     * 创建标签（需要管理员权限）
     */
    @PostMapping
    @SaCheckRole("admin")
    @Operation(summary = "创建标签", description = "创建新的标签（管理员权限）")
    public Result<TagResponse> createTag(@Valid @RequestBody TagCreateRequest request) {
        log.info("创建标签: {}", request.getTagName());
        return tagFacadeService.createTag(request);
    }

    /**
     * 更新标签（需要管理员权限）
     */
    @PutMapping("/{tagId}")
    @SaCheckRole("admin")
    @Operation(summary = "更新标签", description = "更新标签信息（管理员权限）")
    public Result<TagResponse> updateTag(
            @Parameter(description = "标签ID") @PathVariable @NotNull @Min(1) Long tagId,
            @Valid @RequestBody TagUpdateRequest request) {
        log.info("更新标签: tagId={}", tagId);
        request.setId(tagId);
        return tagFacadeService.updateTag(request);
    }

    /**
     * 删除标签（需要管理员权限）
     */
    @DeleteMapping("/{tagId}")
    @SaCheckRole("admin")
    @Operation(summary = "删除标签", description = "删除标签（管理员权限）")
    public Result<Void> deleteTag(
            @Parameter(description = "标签ID") @PathVariable @NotNull @Min(1) Long tagId) {
        log.info("删除标签: tagId={}", tagId);
        return tagFacadeService.deleteTag(tagId);
    }

    /**
     * 根据ID查询标签
     */
    @GetMapping("/{tagId}")
    @Operation(summary = "查询标签详情", description = "根据ID获取标签详细信息")
    public Result<TagResponse> getTag(
            @Parameter(description = "标签ID") @PathVariable @NotNull @Min(1) Long tagId) {
        log.debug("查询标签: tagId={}", tagId);
        return tagFacadeService.getTag(tagId);
    }

    /**
     * 根据名称查询标签
     */
    @GetMapping("/name/{tagName}")
    @Operation(summary = "根据名称查询标签", description = "根据标签名称获取标签信息")
    public Result<TagResponse> getTagByName(
            @Parameter(description = "标签名称") @PathVariable @NotBlank String tagName) {
        log.debug("根据名称查询标签: tagName={}", tagName);
        return tagFacadeService.getTagByName(tagName);
    }

    // =================== 标签列表查询 ===================

    /**
     * 获取所有启用的标签
     */
    @GetMapping("/active")
    @Operation(summary = "获取所有启用标签", description = "获取所有状态为启用的标签")
    public Result<List<TagResponse>> getAllActiveTags() {
        log.debug("获取所有启用标签");
        return tagFacadeService.getAllActiveTags();
    }

    /**
     * 分页查询标签
     */
    @PostMapping("/query")
    @Operation(summary = "分页查询标签", description = "根据条件分页查询标签")
    public Result<PageResponse<TagResponse>> queryTags(@Valid @RequestBody TagQueryRequest request) {
        log.debug("分页查询标签: page={}, size={}", request.getCurrentPage(), request.getPageSize());
        return tagFacadeService.queryTags(request);
    }

    /**
     * 获取热门标签
     */
    @GetMapping("/hot")
    @Operation(summary = "获取热门标签", description = "获取按热度排序的标签列表")
    public Result<List<TagResponse>> getHotTags(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") @Min(1) Integer limit) {
        log.debug("获取热门标签: limit={}", limit);
        return tagFacadeService.getHotTags(limit);
    }

    /**
     * 获取推荐标签
     */
    @GetMapping("/recommend")
    @Operation(summary = "获取推荐标签", description = "获取系统推荐的标签列表")
    public Result<List<TagResponse>> getRecommendTags(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") @Min(1) Integer limit) {
        log.debug("获取推荐标签: limit={}", limit);
        return tagFacadeService.getRecommendTags(limit);
    }

    /**
     * 搜索标签
     */
    @GetMapping("/search")
    @Operation(summary = "搜索标签", description = "根据关键词搜索匹配的标签")
    public Result<List<TagResponse>> searchTags(
            @Parameter(description = "搜索关键词") @RequestParam @NotBlank String keyword,
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") @Min(1) Integer limit) {
        log.debug("搜索标签: keyword={}, limit={}", keyword, limit);
        return tagFacadeService.searchTags(keyword, limit);
    }

    /**
     * 根据权重范围查询标签
     */
    @GetMapping("/weight")
    @Operation(summary = "根据权重查询标签", description = "根据权重范围查询标签")
    public Result<List<TagResponse>> getTagsByWeightRange(
            @Parameter(description = "最小权重") @RequestParam(required = false) @Min(1) Integer minWeight,
            @Parameter(description = "最大权重") @RequestParam(required = false) @Min(1) Integer maxWeight,
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "20") @Min(1) Integer limit) {
        log.debug("根据权重查询标签: minWeight={}, maxWeight={}, limit={}", minWeight, maxWeight, limit);
        return tagFacadeService.getTagsByWeightRange(minWeight, maxWeight, limit);
    }

    // =================== 标签统计分析 ===================

    /**
     * 获取标签统计信息
     */
    @GetMapping("/{tagId}/statistics")
    @Operation(summary = "获取标签统计", description = "获取指定标签的统计信息")
    public Result<Map<String, Object>> getTagStatistics(
            @Parameter(description = "标签ID") @PathVariable @NotNull @Min(1) Long tagId) {
        log.debug("获取标签统计: tagId={}", tagId);
        return tagFacadeService.getTagStatistics(tagId);
    }

    /**
     * 获取热度排行榜
     */
    @GetMapping("/ranking/hotness")
    @Operation(summary = "热度排行榜", description = "获取按热度排序的标签排行榜")
    public Result<List<TagResponse>> getHotnessRanking(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "20") @Min(1) Integer limit) {
        log.debug("获取热度排行榜: limit={}", limit);
        return tagFacadeService.getHotnessRanking(limit);
    }

    /**
     * 获取关注数排行榜
     */
    @GetMapping("/ranking/follow")
    @Operation(summary = "关注数排行榜", description = "获取按关注数排序的标签排行榜")
    public Result<List<TagResponse>> getFollowCountRanking(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "20") @Min(1) Integer limit) {
        log.debug("获取关注数排行榜: limit={}", limit);
        return tagFacadeService.getFollowCountRanking(limit);
    }

    /**
     * 获取内容数排行榜
     */
    @GetMapping("/ranking/content")
    @Operation(summary = "内容数排行榜", description = "获取按内容数排序的标签排行榜")
    public Result<List<TagResponse>> getContentCountRanking(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "20") @Min(1) Integer limit) {
        log.debug("获取内容数排行榜: limit={}", limit);
        return tagFacadeService.getContentCountRanking(limit);
    }

    // =================== 标签状态管理（管理员功能） ===================

    /**
     * 更新标签状态
     */
    @PutMapping("/{tagId}/status")
    @SaCheckRole("admin")
    @Operation(summary = "更新标签状态", description = "更新标签的启用/禁用状态（管理员权限）")
    public Result<Void> updateTagStatus(
            @Parameter(description = "标签ID") @PathVariable @NotNull @Min(1) Long tagId,
            @Parameter(description = "状态(1-启用,0-禁用)") @RequestParam @NotNull Integer status) {
        log.info("更新标签状态: tagId={}, status={}", tagId, status);
        return tagFacadeService.updateTagStatus(tagId, status);
    }

    /**
     * 更新标签权重
     */
    @PutMapping("/{tagId}/weight")
    @SaCheckRole("admin")
    @Operation(summary = "更新标签权重", description = "更新标签的权重值（管理员权限）")
    public Result<Void> updateTagWeight(
            @Parameter(description = "标签ID") @PathVariable @NotNull @Min(1) Long tagId,
            @Parameter(description = "权重值(1-100)") @RequestParam @NotNull @Min(1) Integer weight) {
        log.info("更新标签权重: tagId={}, weight={}", tagId, weight);
        return tagFacadeService.updateTagWeight(tagId, weight);
    }

    /**
     * 批量更新标签状态
     */
    @PutMapping("/batch/status")
    @SaCheckRole("admin")
    @Operation(summary = "批量更新标签状态", description = "批量更新多个标签的状态（管理员权限）")
    public Result<Void> batchUpdateTagStatus(
            @Parameter(description = "标签ID列表") @RequestParam List<Long> tagIds,
            @Parameter(description = "状态(1-启用,0-禁用)") @RequestParam @NotNull Integer status) {
        log.info("批量更新标签状态: tagIds={}, status={}", tagIds, status);
        return tagFacadeService.batchUpdateTagStatus(tagIds, status);
    }

    // =================== 系统管理功能（管理员功能） ===================

    /**
     * 手动更新标签热度
     */
    @PostMapping("/hotness/update")
    @SaCheckRole("admin")
    @Operation(summary = "更新标签热度", description = "手动触发标签热度值更新（管理员权限）")
    public Result<Void> updateTagHotness(
            @Parameter(description = "标签ID（为空时更新所有）") @RequestParam(required = false) Long tagId) {
        log.info("手动更新标签热度: tagId={}", tagId);
        return tagFacadeService.updateTagHotness(tagId);
    }

    /**
     * 检查标签名称可用性
     */
    @GetMapping("/check-name")
    @Operation(summary = "检查标签名称", description = "检查标签名称是否可用")
    public Result<Boolean> checkTagNameAvailable(
            @Parameter(description = "标签名称") @RequestParam @NotBlank String tagName,
            @Parameter(description = "排除的标签ID") @RequestParam(required = false) Long excludeId) {
        log.debug("检查标签名称可用性: tagName={}, excludeId={}", tagName, excludeId);
        return tagFacadeService.checkTagNameAvailable(tagName, excludeId);
    }
} 