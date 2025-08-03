package com.gig.collide.tag.controller;

import com.gig.collide.api.tag.ContentTagFacadeService;
import com.gig.collide.api.tag.request.ContentTagQueryRequest;
import com.gig.collide.api.tag.response.TagResponse;
import com.gig.collide.api.tag.response.ContentTagResponse;
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
 * 内容标签控制器
 * 
 * @author GIG Team
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/content-tags")
@RequiredArgsConstructor
@Tag(name = "内容标签管理", description = "内容打标签功能和基于标签的内容推荐")
public class ContentTagController {

    @DubboReference(version = "1.0.0")
    private ContentTagFacadeService contentTagFacadeService;

    // =================== 内容标签管理 ===================

    /**
     * 为内容添加标签
     */
    @PostMapping("/{contentId}/tags/{tagId}")
    @SaCheckLogin
    @Operation(summary = "为内容添加标签", description = "为指定内容添加一个标签")
    public Result<Void> addContentTag(
            @Parameter(description = "内容ID") @PathVariable @NotNull @Min(1) Long contentId,
            @Parameter(description = "标签ID") @PathVariable @NotNull @Min(1) Long tagId) {
        log.info("为内容添加标签: contentId={}, tagId={}", contentId, tagId);
        return contentTagFacadeService.addContentTag(contentId, tagId);
    }

    /**
     * 移除内容的标签
     */
    @DeleteMapping("/{contentId}/tags/{tagId}")
    @SaCheckLogin
    @Operation(summary = "移除内容标签", description = "移除内容的指定标签")
    public Result<Void> removeContentTag(
            @Parameter(description = "内容ID") @PathVariable @NotNull @Min(1) Long contentId,
            @Parameter(description = "标签ID") @PathVariable @NotNull @Min(1) Long tagId) {
        log.info("移除内容标签: contentId={}, tagId={}", contentId, tagId);
        return contentTagFacadeService.removeContentTag(contentId, tagId);
    }

    /**
     * 批量为内容添加标签
     */
    @PostMapping("/{contentId}/tags/batch")
    @SaCheckLogin
    @Operation(summary = "批量添加标签", description = "为内容批量添加多个标签（最多9个）")
    public Result<List<Long>> batchAddContentTags(
            @Parameter(description = "内容ID") @PathVariable @NotNull @Min(1) Long contentId,
            @Parameter(description = "标签ID列表") @RequestBody List<Long> tagIds) {
        log.info("批量为内容添加标签: contentId={}, tagIds={}", contentId, tagIds);
        return contentTagFacadeService.batchAddContentTags(contentId, tagIds);
    }

    /**
     * 批量移除内容的标签
     */
    @DeleteMapping("/{contentId}/tags/batch")
    @SaCheckLogin
    @Operation(summary = "批量移除标签", description = "批量移除内容的多个标签")
    public Result<Void> batchRemoveContentTags(
            @Parameter(description = "内容ID") @PathVariable @NotNull @Min(1) Long contentId,
            @Parameter(description = "标签ID列表") @RequestBody List<Long> tagIds) {
        log.info("批量移除内容标签: contentId={}, tagIds={}", contentId, tagIds);
        return contentTagFacadeService.batchRemoveContentTags(contentId, tagIds);
    }

    /**
     * 替换内容的所有标签
     */
    @PutMapping("/{contentId}/tags")
    @SaCheckLogin
    @Operation(summary = "替换内容标签", description = "替换内容的所有标签")
    public Result<Void> replaceContentTags(
            @Parameter(description = "内容ID") @PathVariable @NotNull @Min(1) Long contentId,
            @Parameter(description = "新的标签ID列表") @RequestBody List<Long> tagIds) {
        log.info("替换内容标签: contentId={}, newTagIds={}", contentId, tagIds);
        return contentTagFacadeService.replaceContentTags(contentId, tagIds);
    }

    // =================== 内容标签查询 ===================

    /**
     * 获取内容的标签列表
     */
    @GetMapping("/{contentId}/tags")
    @Operation(summary = "获取内容标签", description = "获取指定内容的所有标签")
    public Result<List<TagResponse>> getContentTags(
            @Parameter(description = "内容ID") @PathVariable @NotNull @Min(1) Long contentId) {
        log.debug("获取内容标签: contentId={}", contentId);
        return contentTagFacadeService.getContentTags(contentId);
    }

    /**
     * 获取内容的标签数量
     */
    @GetMapping("/{contentId}/tags/count")
    @Operation(summary = "获取内容标签数量", description = "获取内容拥有的标签总数")
    public Result<Integer> getContentTagCount(
            @Parameter(description = "内容ID") @PathVariable @NotNull @Min(1) Long contentId) {
        log.debug("获取内容标签数量: contentId={}", contentId);
        return contentTagFacadeService.getContentTagCount(contentId);
    }

    /**
     * 检查内容是否包含指定标签
     */
    @GetMapping("/{contentId}/tags/{tagId}/check")
    @Operation(summary = "检查内容标签", description = "检查内容是否包含指定标签")
    public Result<Boolean> hasContentTag(
            @Parameter(description = "内容ID") @PathVariable @NotNull @Min(1) Long contentId,
            @Parameter(description = "标签ID") @PathVariable @NotNull @Min(1) Long tagId) {
        log.debug("检查内容标签: contentId={}, tagId={}", contentId, tagId);
        return contentTagFacadeService.hasContentTag(contentId, tagId);
    }

    /**
     * 批量检查内容标签
     */
    @PostMapping("/{contentId}/tags/batch-check")
    @Operation(summary = "批量检查内容标签", description = "批量检查内容是否包含指定标签")
    public Result<Map<Long, Boolean>> batchCheckContentTags(
            @Parameter(description = "内容ID") @PathVariable @NotNull @Min(1) Long contentId,
            @Parameter(description = "标签ID列表") @RequestBody List<Long> tagIds) {
        log.debug("批量检查内容标签: contentId={}, tagIds={}", contentId, tagIds);
        return contentTagFacadeService.batchCheckContentTags(contentId, tagIds);
    }

    // =================== 基于标签的内容查询 ===================

    /**
     * 根据单个标签查询内容
     */
    @PostMapping("/tag/{tagId}/contents")
    @Operation(summary = "根据标签查询内容", description = "根据指定标签分页查询内容")
    public Result<PageResponse<Long>> getContentsByTag(
            @Parameter(description = "标签ID") @PathVariable @NotNull @Min(1) Long tagId,
            @Valid @RequestBody ContentTagQueryRequest request) {
        log.debug("根据标签查询内容: tagId={}", tagId);
        return contentTagFacadeService.getContentsByTag(tagId, request);
    }

    /**
     * 根据多个标签查询内容（AND关系）
     */
    @PostMapping("/tags/contents/and")
    @Operation(summary = "多标签AND查询内容", description = "根据多个标签查询包含所有标签的内容")
    public Result<PageResponse<Long>> getContentsByTagsAnd(
            @Parameter(description = "标签ID列表") @RequestBody List<Long> tagIds,
            @Valid @RequestBody ContentTagQueryRequest request) {
        log.debug("多标签AND查询内容: tagIds={}", tagIds);
        return contentTagFacadeService.getContentsByTagsAnd(tagIds, request);
    }

    /**
     * 根据多个标签查询内容（OR关系）
     */
    @PostMapping("/tags/contents/or")
    @Operation(summary = "多标签OR查询内容", description = "根据多个标签查询包含任一标签的内容")
    public Result<PageResponse<Long>> getContentsByTagsOr(
            @Parameter(description = "标签ID列表") @RequestBody List<Long> tagIds,
            @Valid @RequestBody ContentTagQueryRequest request) {
        log.debug("多标签OR查询内容: tagIds={}", tagIds);
        return contentTagFacadeService.getContentsByTagsOr(tagIds, request);
    }

    /**
     * 获取标签下的热门内容
     */
    @GetMapping("/tag/{tagId}/hot-contents")
    @Operation(summary = "获取标签热门内容", description = "获取标签下的热门内容列表")
    public Result<List<Long>> getHotContentsByTag(
            @Parameter(description = "标签ID") @PathVariable @NotNull @Min(1) Long tagId,
            @Parameter(description = "天数范围") @RequestParam(defaultValue = "7") @Min(1) Integer days,
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "20") @Min(1) Integer limit) {
        log.debug("获取标签热门内容: tagId={}, days={}, limit={}", tagId, days, limit);
        return contentTagFacadeService.getHotContentsByTag(tagId, days, limit);
    }

    // =================== 基于用户标签的内容推荐 ===================

    /**
     * 基于用户关注标签推荐内容
     */
    @GetMapping("/user/{userId}/recommend")
    @Operation(summary = "基于用户标签推荐内容", description = "根据用户关注的标签推荐内容")
    public Result<List<Long>> getRecommendContentsByUserTags(
            @Parameter(description = "用户ID") @PathVariable @NotNull @Min(1) Long userId,
            @Parameter(description = "推荐数量") @RequestParam(defaultValue = "20") @Min(1) Integer limit) {
        log.debug("基于用户标签推荐内容: userId={}, limit={}", userId, limit);
        return contentTagFacadeService.getRecommendContentsByUserTags(userId, limit);
    }

    /**
     * 基于用户关注标签推荐热门内容
     */
    @GetMapping("/user/{userId}/recommend/hot")
    @Operation(summary = "推荐用户热门内容", description = "根据用户关注标签推荐热门内容")
    public Result<List<Long>> getRecommendHotContentsByUserTags(
            @Parameter(description = "用户ID") @PathVariable @NotNull @Min(1) Long userId,
            @Parameter(description = "天数范围") @RequestParam(defaultValue = "7") @Min(1) Integer days,
            @Parameter(description = "推荐数量") @RequestParam(defaultValue = "20") @Min(1) Integer limit) {
        log.debug("基于用户标签推荐热门内容: userId={}, days={}, limit={}", userId, days, limit);
        return contentTagFacadeService.getRecommendHotContentsByUserTags(userId, days, limit);
    }

    /**
     * 基于用户兴趣相似度推荐内容
     */
    @GetMapping("/user/{userId}/recommend/similar")
    @Operation(summary = "相似用户推荐内容", description = "基于兴趣相似的用户推荐内容")
    public Result<List<Long>> getRecommendContentsBySimilarUsers(
            @Parameter(description = "用户ID") @PathVariable @NotNull @Min(1) Long userId,
            @Parameter(description = "推荐数量") @RequestParam(defaultValue = "20") @Min(1) Integer limit) {
        log.debug("基于相似用户推荐内容: userId={}, limit={}", userId, limit);
        return contentTagFacadeService.getRecommendContentsBySimilarUsers(userId, limit);
    }

    // =================== 内容标签统计分析 ===================

    /**
     * 获取标签的内容统计信息
     */
    @GetMapping("/tag/{tagId}/statistics")
    @Operation(summary = "获取标签内容统计", description = "获取指定标签的内容统计信息")
    public Result<Map<String, Object>> getTagContentStatistics(
            @Parameter(description = "标签ID") @PathVariable @NotNull @Min(1) Long tagId) {
        log.debug("获取标签内容统计: tagId={}", tagId);
        return contentTagFacadeService.getTagContentStatistics(tagId);
    }

    /**
     * 获取标签的最新内容
     */
    @GetMapping("/tag/{tagId}/latest-contents")
    @Operation(summary = "获取标签最新内容", description = "获取标签下的最新内容列表")
    public Result<List<Long>> getTagLatestContents(
            @Parameter(description = "标签ID") @PathVariable @NotNull @Min(1) Long tagId,
            @Parameter(description = "天数范围") @RequestParam(defaultValue = "7") @Min(1) Integer days,
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") @Min(1) Integer limit) {
        log.debug("获取标签最新内容: tagId={}, days={}, limit={}", tagId, days, limit);
        return contentTagFacadeService.getTagLatestContents(tagId, days, limit);
    }

    /**
     * 获取标签内容的时间分布
     */
    @GetMapping("/tag/{tagId}/time-distribution")
    @Operation(summary = "获取标签内容时间分布", description = "获取标签内容的时间分布统计")
    public Result<Map<String, Integer>> getTagContentTimeDistribution(
            @Parameter(description = "标签ID") @PathVariable @NotNull @Min(1) Long tagId,
            @Parameter(description = "天数范围") @RequestParam(defaultValue = "30") @Min(1) Integer days) {
        log.debug("获取标签内容时间分布: tagId={}, days={}", tagId, days);
        return contentTagFacadeService.getTagContentTimeDistribution(tagId, days);
    }

    // =================== 分页查询接口 ===================

    /**
     * 分页查询内容标签关系
     */
    @PostMapping("/query")
    @Operation(summary = "分页查询内容标签", description = "根据条件分页查询内容标签关系")
    public Result<PageResponse<ContentTagResponse>> queryContentTags(
            @Valid @RequestBody ContentTagQueryRequest request) {
        log.debug("分页查询内容标签: page={}, size={}", request.getCurrentPage(), request.getPageSize());
        return contentTagFacadeService.queryContentTags(request);
    }

    /**
     * 分页查询标签的内容列表
     */
    @PostMapping("/tag/{tagId}/query")
    @Operation(summary = "分页查询标签内容", description = "分页查询指定标签的内容列表")
    public Result<PageResponse<ContentTagResponse>> queryTagContents(
            @Parameter(description = "标签ID") @PathVariable @NotNull @Min(1) Long tagId,
            @Valid @RequestBody ContentTagQueryRequest request) {
        log.debug("分页查询标签内容: tagId={}", tagId);
        return contentTagFacadeService.queryTagContents(tagId, request);
    }

    // =================== 智能推荐功能 ===================

    /**
     * 为内容智能推荐标签
     */
    @GetMapping("/{contentId}/recommend-tags")
    @Operation(summary = "为内容推荐标签", description = "基于内容特征和相似内容推荐合适的标签")
    public Result<List<TagResponse>> getRecommendTagsForContent(
            @Parameter(description = "内容ID") @PathVariable @NotNull @Min(1) Long contentId,
            @Parameter(description = "内容文本") @RequestParam(required = false) String contentText,
            @Parameter(description = "推荐数量") @RequestParam(defaultValue = "5") @Min(1) Integer limit) {
        log.debug("为内容推荐标签: contentId={}, limit={}", contentId, limit);
        return contentTagFacadeService.getRecommendTagsForContent(contentId, contentText, limit);
    }

    /**
     * 获取内容的相关内容
     */
    @GetMapping("/{contentId}/related")
    @Operation(summary = "获取相关内容", description = "基于标签相似度获取相关内容")
    public Result<List<Long>> getRelatedContentsByTags(
            @Parameter(description = "内容ID") @PathVariable @NotNull @Min(1) Long contentId,
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") @Min(1) Integer limit) {
        log.debug("获取相关内容: contentId={}, limit={}", contentId, limit);
        return contentTagFacadeService.getRelatedContentsByTags(contentId, limit);
    }

    // =================== 管理功能 ===================

    /**
     * 清理内容的无效标签（管理员功能）
     */
    @PostMapping("/{contentId}/cleanup")
    @SaCheckRole("admin")
    @Operation(summary = "清理内容无效标签", description = "清理内容的无效标签记录（管理员权限）")
    public Result<Integer> cleanupInvalidContentTags(
            @Parameter(description = "内容ID") @PathVariable Long contentId) {
        log.info("清理内容无效标签: contentId={}", contentId);
        return contentTagFacadeService.cleanupInvalidContentTags(contentId);
    }

    /**
     * 检查内容是否可以添加指定标签
     */
    @GetMapping("/{contentId}/can-add/{tagId}")
    @Operation(summary = "检查添加标签权限", description = "检查内容是否可以添加指定标签")
    public Result<Boolean> canContentHaveTag(
            @Parameter(description = "内容ID") @PathVariable @NotNull @Min(1) Long contentId,
            @Parameter(description = "标签ID") @PathVariable @NotNull @Min(1) Long tagId) {
        log.debug("检查内容添加标签权限: contentId={}, tagId={}", contentId, tagId);
        return contentTagFacadeService.canContentHaveTag(contentId, tagId);
    }

    /**
     * 批量更新内容标签的权重影响（管理员功能）
     */
    @PostMapping("/batch/update-weights")
    @SaCheckRole("admin")
    @Operation(summary = "批量更新标签权重", description = "批量更新内容标签的权重影响（管理员权限）")
    public Result<Void> updateContentTagWeights(
            @Parameter(description = "标签ID列表") @RequestBody List<Long> tagIds) {
        log.info("批量更新内容标签权重: tagIds={}", tagIds);
        return contentTagFacadeService.updateContentTagWeights(tagIds);
    }

    // =================== 简化查询接口 ===================

    /**
     * 简单查询标签下的内容（GET方式）
     */
    @GetMapping("/tag/{tagId}/contents")
    @Operation(summary = "查询标签内容（简化）", description = "简单查询标签下的内容列表")
    public Result<PageResponse<Long>> getTagContentsSimple(
            @Parameter(description = "标签ID") @PathVariable @NotNull @Min(1) Long tagId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        log.debug("简单查询标签内容: tagId={}, page={}, size={}", tagId, page, size);
        
        ContentTagQueryRequest request = new ContentTagQueryRequest();
        request.setCurrentPage(page);
        request.setPageSize(size);
        
        return contentTagFacadeService.getContentsByTag(tagId, request);
    }
}