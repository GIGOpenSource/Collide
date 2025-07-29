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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 内容管理控制器 - 简洁版
 * 基于content-simple.sql的双表设计，提供HTTP REST接口
 * 支持评分功能、章节管理、内容审核
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/content")
@RequiredArgsConstructor
@Tag(name = "内容管理", description = "内容管理相关接口 - 简洁版")
public class ContentController {

    @Autowired
    private ContentFacadeService contentFacadeService;

    // =================== 内容管理 ===================

    @PostMapping("/create")
    @Operation(summary = "创建内容", description = "创建新内容，支持多种类型：NOVEL、COMIC、VIDEO、ARTICLE、AUDIO")
    public Result<ContentResponse> createContent(@Validated @RequestBody ContentCreateRequest request) {
        log.info("REST创建内容: {}", request.getTitle());
        return contentFacadeService.createContent(request);
    }

    @PutMapping("/update")
    @Operation(summary = "更新内容", description = "更新内容信息，支持部分字段更新")
    public Result<ContentResponse> updateContent(@Validated @RequestBody ContentUpdateRequest request) {
        log.info("REST更新内容: ID={}", request.getId());
        return contentFacadeService.updateContent(request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除内容", description = "逻辑删除内容（设为OFFLINE状态）")
    public Result<Void> deleteContent(@PathVariable("id") Long contentId,
                                     @Parameter(description = "操作人ID") @RequestParam Long operatorId) {
        log.info("REST删除内容: ID={}, 操作人={}", contentId, operatorId);
        return contentFacadeService.deleteContent(contentId, operatorId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取内容详情", description = "根据ID获取内容详情")
    public Result<ContentResponse> getContentById(@PathVariable("id") Long contentId,
                                                 @Parameter(description = "是否包含下线内容") @RequestParam(defaultValue = "false") Boolean includeOffline) {
        log.debug("REST获取内容详情: ID={}", contentId);
        return contentFacadeService.getContentById(contentId, includeOffline);
    }

    @PostMapping("/query")
    @Operation(summary = "分页查询内容", description = "根据条件分页查询内容列表")
    public Result<PageResponse<ContentResponse>> queryContents(@Validated @RequestBody ContentQueryRequest request) {
        log.debug("REST分页查询内容: 关键词={}", request.getKeyword());
        return contentFacadeService.queryContents(request);
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "发布内容", description = "将草稿状态的内容发布上线")
    public Result<ContentResponse> publishContent(@PathVariable("id") Long contentId,
                                                 @Parameter(description = "作者ID") @RequestParam Long authorId) {
        log.info("REST发布内容: ID={}, 作者={}", contentId, authorId);
        return contentFacadeService.publishContent(contentId, authorId);
    }

    @PostMapping("/{id}/offline")
    @Operation(summary = "下线内容", description = "将已发布的内容下线")
    public Result<Void> offlineContent(@PathVariable("id") Long contentId,
                                      @Parameter(description = "操作人ID") @RequestParam Long operatorId) {
        log.info("REST下线内容: ID={}, 操作人={}", contentId, operatorId);
        return contentFacadeService.offlineContent(contentId, operatorId);
    }

    // =================== 章节管理 ===================

    @PostMapping("/chapter/create")
    @Operation(summary = "创建章节", description = "为小说、漫画等多章节内容创建新章节")
    public Result<ChapterResponse> createChapter(@Validated @RequestBody ChapterCreateRequest request) {
        log.info("REST创建章节: 内容ID={}, 章节号={}", request.getContentId(), request.getChapterNum());
        return contentFacadeService.createChapter(request);
    }

    @GetMapping("/{contentId}/chapters")
    @Operation(summary = "获取内容章节列表", description = "分页获取指定内容的章节列表")
    public Result<PageResponse<ChapterResponse>> getContentChapters(@PathVariable("contentId") Long contentId,
                                                                   @Parameter(description = "章节状态") @RequestParam(required = false) String status,
                                                                   @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
                                                                   @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        log.debug("REST获取内容章节: 内容ID={}", contentId);
        return contentFacadeService.getContentChapters(contentId, status, pageNum, pageSize);
    }

    @GetMapping("/chapter/{id}")
    @Operation(summary = "获取章节详情", description = "根据章节ID获取章节详情")
    public Result<ChapterResponse> getChapterById(@PathVariable("id") Long chapterId) {
        log.debug("REST获取章节详情: ID={}", chapterId);
        return contentFacadeService.getChapterById(chapterId);
    }

    @PostMapping("/chapter/{id}/publish")
    @Operation(summary = "发布章节", description = "发布指定章节")
    public Result<ChapterResponse> publishChapter(@PathVariable("id") Long chapterId,
                                                 @Parameter(description = "作者ID") @RequestParam Long authorId) {
        log.info("REST发布章节: ID={}, 作者={}", chapterId, authorId);
        return contentFacadeService.publishChapter(chapterId, authorId);
    }

    // =================== 统计管理 ===================

    @PostMapping("/{id}/view")
    @Operation(summary = "增加浏览量", description = "增加内容的浏览量统计")
    public Result<Long> increaseViewCount(@PathVariable("id") Long contentId,
                                         @Parameter(description = "增加数量") @RequestParam(defaultValue = "1") Integer increment) {
        return contentFacadeService.increaseViewCount(contentId, increment);
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "增加点赞数", description = "增加内容的点赞数统计")
    public Result<Long> increaseLikeCount(@PathVariable("id") Long contentId,
                                         @Parameter(description = "增加数量") @RequestParam(defaultValue = "1") Integer increment) {
        return contentFacadeService.increaseLikeCount(contentId, increment);
    }

    @PostMapping("/{id}/comment")
    @Operation(summary = "增加评论数", description = "增加内容的评论数统计")
    public Result<Long> increaseCommentCount(@PathVariable("id") Long contentId,
                                            @Parameter(description = "增加数量") @RequestParam(defaultValue = "1") Integer increment) {
        return contentFacadeService.increaseCommentCount(contentId, increment);
    }

    @PostMapping("/{id}/favorite")
    @Operation(summary = "增加收藏数", description = "增加内容的收藏数统计")
    public Result<Long> increaseFavoriteCount(@PathVariable("id") Long contentId,
                                             @Parameter(description = "增加数量") @RequestParam(defaultValue = "1") Integer increment) {
        return contentFacadeService.increaseFavoriteCount(contentId, increment);
    }

    @PostMapping("/{id}/score")
    @Operation(summary = "更新评分", description = "为内容添加评分，支持1-10分评分系统")
    public Result<Double> updateScore(@PathVariable("id") Long contentId,
                                     @Parameter(description = "评分值(1-10)") @RequestParam Integer score) {
        log.info("REST更新内容评分: ID={}, 评分={}", contentId, score);
        return contentFacadeService.updateScore(contentId, score);
    }

    @GetMapping("/{id}/statistics")
    @Operation(summary = "获取内容统计", description = "获取内容的完整统计信息，包括评分")
    public Result<Map<String, Object>> getContentStatistics(@PathVariable("id") Long contentId) {
        log.debug("REST获取内容统计: ID={}", contentId);
        return contentFacadeService.getContentStatistics(contentId);
    }

    // =================== 内容查询 ===================

    @GetMapping("/author/{authorId}")
    @Operation(summary = "根据作者查询内容", description = "分页查询指定作者的内容列表")
    public Result<PageResponse<ContentResponse>> getContentsByAuthor(@PathVariable("authorId") Long authorId,
                                                                   @Parameter(description = "内容类型") @RequestParam(required = false) String contentType,
                                                                   @Parameter(description = "状态") @RequestParam(required = false) String status,
                                                                   @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
                                                                   @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        log.debug("REST根据作者查询内容: 作者ID={}", authorId);
        return contentFacadeService.getContentsByAuthor(authorId, contentType, status, pageNum, pageSize);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "根据分类查询内容", description = "分页查询指定分类的内容列表")
    public Result<PageResponse<ContentResponse>> getContentsByCategory(@PathVariable("categoryId") Long categoryId,
                                                                     @Parameter(description = "内容类型") @RequestParam(required = false) String contentType,
                                                                     @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
                                                                     @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        log.debug("REST根据分类查询内容: 分类ID={}", categoryId);
        return contentFacadeService.getContentsByCategory(categoryId, contentType, pageNum, pageSize);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索内容", description = "根据关键词搜索内容（标题、描述、标签）")
    public Result<PageResponse<ContentResponse>> searchContents(@Parameter(description = "搜索关键词") @RequestParam String keyword,
                                                              @Parameter(description = "内容类型") @RequestParam(required = false) String contentType,
                                                              @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
                                                              @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        log.debug("REST搜索内容: 关键词={}", keyword);
        return contentFacadeService.searchContents(keyword, contentType, pageNum, pageSize);
    }

    @GetMapping("/popular")
    @Operation(summary = "获取热门内容", description = "根据综合热度排序获取热门内容")
    public Result<PageResponse<ContentResponse>> getPopularContents(@Parameter(description = "内容类型") @RequestParam(required = false) String contentType,
                                                                  @Parameter(description = "时间范围(天)") @RequestParam(required = false) Integer timeRange,
                                                                  @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
                                                                  @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        log.debug("REST获取热门内容: 类型={}, 时间范围={}", contentType, timeRange);
        return contentFacadeService.getPopularContents(contentType, timeRange, pageNum, pageSize);
    }

    @GetMapping("/latest")
    @Operation(summary = "获取最新内容", description = "按发布时间排序获取最新内容")
    public Result<PageResponse<ContentResponse>> getLatestContents(@Parameter(description = "内容类型") @RequestParam(required = false) String contentType,
                                                                 @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
                                                                 @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        log.debug("REST获取最新内容: 类型={}", contentType);
        return contentFacadeService.getLatestContents(contentType, pageNum, pageSize);
    }

    // =================== 数据同步 ===================

    @PostMapping("/sync/author")
    @Operation(summary = "同步作者信息", description = "更新内容表中的冗余作者信息")
    public Result<Integer> updateAuthorInfo(@Parameter(description = "作者ID") @RequestParam Long authorId,
                                           @Parameter(description = "新昵称") @RequestParam String nickname,
                                           @Parameter(description = "新头像") @RequestParam(required = false) String avatar) {
        log.info("REST同步作者信息: ID={}, 昵称={}", authorId, nickname);
        return contentFacadeService.updateAuthorInfo(authorId, nickname, avatar);
    }

    @PostMapping("/sync/category")
    @Operation(summary = "同步分类信息", description = "更新内容表中的冗余分类信息")
    public Result<Integer> updateCategoryInfo(@Parameter(description = "分类ID") @RequestParam Long categoryId,
                                             @Parameter(description = "新分类名称") @RequestParam String categoryName) {
        log.info("REST同步分类信息: ID={}, 名称={}", categoryId, categoryName);
        return contentFacadeService.updateCategoryInfo(categoryId, categoryName);
    }

    @PostMapping("/{id}/review")
    @Operation(summary = "审核内容", description = "内容审核，更新审核状态")
    public Result<ContentResponse> reviewContent(@PathVariable("id") Long contentId,
                                                @Parameter(description = "审核状态：APPROVED、REJECTED") @RequestParam String reviewStatus,
                                                @Parameter(description = "审核人ID") @RequestParam Long reviewerId,
                                                @Parameter(description = "审核意见") @RequestParam(required = false) String reviewComment) {
        log.info("REST审核内容: ID={}, 状态={}, 审核人={}", contentId, reviewStatus, reviewerId);
        return contentFacadeService.reviewContent(contentId, reviewStatus, reviewerId, reviewComment);
    }
}