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
import com.gig.collide.api.like.LikeFacadeService;
import com.gig.collide.api.favorite.FavoriteFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

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
    
    @Autowired
    private LikeFacadeService likeFacadeService;
    
    @Autowired
    private FavoriteFacadeService favoriteFacadeService;

    // =================== 内容管理 ===================

    @PostMapping("/create")
    @Operation(summary = "创建内容", description = "创建新内容，支持多种类型：NOVEL、COMIC、VIDEO、ARTICLE、AUDIO")
    public Result<Void> createContent(@Validated @RequestBody ContentCreateRequest request) {
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
    public Result<Void> createChapter(@Validated @RequestBody ChapterCreateRequest request) {
        log.info("REST创建章节: 内容ID={}, 章节号={}", request.getContentId(), request.getChapterNum());
        return contentFacadeService.createChapter(request);
    }

    @GetMapping("/{contentId}/chapters")
    @Operation(summary = "获取内容章节列表", description = "分页获取指定内容的章节列表")
    public PageResponse<ChapterResponse> getContentChapters(@PathVariable("contentId") Long contentId,
                                                           @Parameter(description = "章节状态") @RequestParam(required = false) String status,
                                                           @Parameter(description = "页码") @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
                                                           @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        log.debug("REST获取内容章节: 内容ID={}，页码：{}", contentId, currentPage);
        Result<PageResponse<ChapterResponse>> result = contentFacadeService.getContentChapters(contentId, status, currentPage, pageSize);
        return result.getData();
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

    @PostMapping("/{id}/favorite-count")
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
    public PageResponse<ContentResponse> getContentsByAuthor(@PathVariable("authorId") Long authorId,
                                                           @Parameter(description = "内容类型") @RequestParam(required = false) String contentType,
                                                           @Parameter(description = "状态") @RequestParam(required = false) String status,
                                                           @Parameter(description = "页码") @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
                                                           @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        log.debug("REST根据作者查询内容: 作者ID={}，页码：{}", authorId, currentPage);
        Result<PageResponse<ContentResponse>> result = contentFacadeService.getContentsByAuthor(authorId, contentType, status, currentPage, pageSize);
        return result.getData();
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "根据分类查询内容", description = "分页查询指定分类的内容列表")
    public PageResponse<ContentResponse> getContentsByCategory(@PathVariable("categoryId") Long categoryId,
                                                             @Parameter(description = "内容类型") @RequestParam(required = false) String contentType,
                                                             @Parameter(description = "页码") @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
                                                             @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        log.debug("REST根据分类查询内容: 分类ID={}，页码：{}", categoryId, currentPage);
        Result<PageResponse<ContentResponse>> result = contentFacadeService.getContentsByCategory(categoryId, contentType, currentPage, pageSize);
        return result.getData();
    }

    @GetMapping("/search")
    @Operation(summary = "搜索内容", description = "根据关键词搜索内容（标题、描述、标签）")
    public PageResponse<ContentResponse> searchContents(@Parameter(description = "搜索关键词") @RequestParam String keyword,
                                                      @Parameter(description = "内容类型") @RequestParam(required = false) String contentType,
                                                      @Parameter(description = "页码") @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
                                                      @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        log.debug("REST搜索内容: 关键词={}，页码：{}", keyword, currentPage);
        Result<PageResponse<ContentResponse>> result = contentFacadeService.searchContents(keyword, contentType, currentPage, pageSize);
        return result.getData();
    }

    @GetMapping("/popular")
    @Operation(summary = "获取热门内容", description = "根据综合热度排序获取热门内容")
    public PageResponse<ContentResponse> getPopularContents(@Parameter(description = "内容类型") @RequestParam(required = false) String contentType,
                                                          @Parameter(description = "时间范围(天)") @RequestParam(required = false) Integer timeRange,
                                                          @Parameter(description = "页码") @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
                                                          @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        log.debug("REST获取热门内容: 类型={}, 时间范围={}，页码：{}", contentType, timeRange, currentPage);
        Result<PageResponse<ContentResponse>> result = contentFacadeService.getPopularContents(contentType, timeRange, currentPage, pageSize);
        return result.getData();
    }

    @GetMapping("/latest")
    @Operation(summary = "获取最新内容", description = "按发布时间排序获取最新内容")
    public PageResponse<ContentResponse> getLatestContents(@Parameter(description = "内容类型") @RequestParam(required = false) String contentType,
                                                         @Parameter(description = "页码") @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
                                                         @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        log.debug("REST获取最新内容: 类型={}，页码：{}", contentType, currentPage);
        Result<PageResponse<ContentResponse>> result = contentFacadeService.getLatestContents(contentType, currentPage, pageSize);
        return result.getData();
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

    // =================== 跨模块功能增强 ===================

    @GetMapping("/{id}/like/status")
    @Operation(summary = "获取用户点赞状态", description = "检查用户是否已点赞该内容")
    public Result<Boolean> getUserLikeStatus(@PathVariable("id") Long contentId,
                                           @Parameter(description = "用户ID") @RequestParam Long userId) {
        log.debug("REST获取用户点赞状态: 内容ID={}, 用户ID={}", contentId, userId);
        return likeFacadeService.checkLikeStatus(userId, "CONTENT", contentId);
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "点赞内容", description = "用户点赞/取消点赞内容") 
    public Result<Boolean> toggleContentLike(@PathVariable("id") Long contentId,
                                           @Parameter(description = "用户ID") @RequestParam Long userId) {
        log.info("REST切换内容点赞状态: 内容ID={}, 用户ID={}", contentId, userId);
        
        // 首先检查当前点赞状态
        var statusResult = likeFacadeService.checkLikeStatus(userId, "CONTENT", contentId);
        boolean currentlyLiked = statusResult.getSuccess() && statusResult.getData();
        
        try {
            if (currentlyLiked) {
                // 取消点赞
                var cancelRequest = new com.gig.collide.api.like.request.LikeCancelRequest();
                cancelRequest.setUserId(userId);
                cancelRequest.setLikeType("CONTENT");  
                cancelRequest.setTargetId(contentId);
                
                var result = likeFacadeService.cancelLike(cancelRequest);
                if (result.getSuccess()) {
                    contentFacadeService.increaseLikeCount(contentId, -1);
                    return Result.success(false);
                }
                return Result.error("CANCEL_LIKE_FAILED", "取消点赞失败");
            } else {
                // 添加点赞
                var likeRequest = new com.gig.collide.api.like.request.LikeRequest();
                likeRequest.setUserId(userId);
                likeRequest.setLikeType("CONTENT");
                likeRequest.setTargetId(contentId);
                
                var result = likeFacadeService.addLike(likeRequest);
                if (result.getSuccess()) {
                    contentFacadeService.increaseLikeCount(contentId, 1);
                    return Result.success(true);
                }
                return Result.error("ADD_LIKE_FAILED", "点赞失败");
            }
        } catch (Exception e) {
            log.error("切换点赞状态失败", e);
            return Result.error("TOGGLE_LIKE_FAILED", "切换点赞状态失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/favorite/status")
    @Operation(summary = "获取用户收藏状态", description = "检查用户是否已收藏该内容")
    public Result<Boolean> getUserFavoriteStatus(@PathVariable("id") Long contentId,
                                                @Parameter(description = "用户ID") @RequestParam Long userId) {
        log.debug("REST获取用户收藏状态: 内容ID={}, 用户ID={}", contentId, userId);
        return favoriteFacadeService.checkFavoriteStatus(userId, "CONTENT", contentId);
    }

    @PostMapping("/{id}/favorite")
    @Operation(summary = "收藏内容", description = "用户收藏/取消收藏内容")
    public Result<Boolean> toggleContentFavorite(@PathVariable("id") Long contentId,
                                               @Parameter(description = "用户ID") @RequestParam Long userId) {
        log.info("REST切换内容收藏状态: 内容ID={}, 用户ID={}", contentId, userId);
        
        // 首先检查当前收藏状态
        var statusResult = favoriteFacadeService.checkFavoriteStatus(userId, "CONTENT", contentId);
        boolean currentlyFavorited = statusResult.getSuccess() && statusResult.getData();
        
        try {
            if (currentlyFavorited) {
                // 取消收藏
                var deleteRequest = new com.gig.collide.api.favorite.request.FavoriteDeleteRequest();
                deleteRequest.setUserId(userId);
                deleteRequest.setFavoriteType("CONTENT");
                deleteRequest.setTargetId(contentId);
                
                var result = favoriteFacadeService.removeFavorite(deleteRequest);
                if (result.getSuccess()) {
                    contentFacadeService.increaseFavoriteCount(contentId, -1);
                    return Result.success(false);
                }
                return Result.error("REMOVE_FAVORITE_FAILED", "取消收藏失败");
            } else {
                // 添加收藏
                var favoriteRequest = new com.gig.collide.api.favorite.request.FavoriteCreateRequest();
                favoriteRequest.setUserId(userId);
                favoriteRequest.setFavoriteType("CONTENT");
                favoriteRequest.setTargetId(contentId);
                
                var result = favoriteFacadeService.addFavorite(favoriteRequest);
                if (result.getSuccess()) {
                    contentFacadeService.increaseFavoriteCount(contentId, 1);
                    return Result.success(true);
                }
                return Result.error("ADD_FAVORITE_FAILED", "收藏失败");
            }
        } catch (Exception e) {
            log.error("切换收藏状态失败", e);
            return Result.error("TOGGLE_FAVORITE_FAILED", "切换收藏状态失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/interaction")
    @Operation(summary = "获取用户互动状态", description = "一次性获取用户对该内容的点赞、收藏状态")
    public Result<Map<String, Object>> getUserInteractionStatus(@PathVariable("id") Long contentId,
                                                               @Parameter(description = "用户ID") @RequestParam Long userId) {
        log.debug("REST获取用户互动状态: 内容ID={}, 用户ID={}", contentId, userId);
        
        Map<String, Object> interaction = new HashMap<>();
        
        try {
            // 获取点赞状态
            var likeResult = likeFacadeService.checkLikeStatus(userId, "CONTENT", contentId);
            interaction.put("isLiked", likeResult.getSuccess() ? likeResult.getData() : false);
            
            // 获取收藏状态
            var favoriteResult = favoriteFacadeService.checkFavoriteStatus(userId, "CONTENT", contentId);
            interaction.put("isFavorited", favoriteResult.getSuccess() ? favoriteResult.getData() : false);
            
            // 获取实时计数
            var likeCountResult = likeFacadeService.getLikeCount("CONTENT", contentId);
            interaction.put("likeCount", likeCountResult.getSuccess() ? likeCountResult.getData() : 0);
            
            var favoriteCountResult = favoriteFacadeService.getTargetFavoriteCount("CONTENT", contentId);
            interaction.put("favoriteCount", favoriteCountResult.getSuccess() ? favoriteCountResult.getData() : 0);
            
            interaction.put("contentId", contentId);
            interaction.put("userId", userId);
            
            return Result.success(interaction);
            
        } catch (Exception e) {
            log.error("获取用户互动状态失败", e);
            return Result.error("INTERACTION_STATUS_FAILED", "获取用户互动状态失败: " + e.getMessage());
        }
    }
}