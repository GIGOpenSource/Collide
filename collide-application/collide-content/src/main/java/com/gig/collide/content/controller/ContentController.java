package com.gig.collide.content.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.gig.collide.api.content.constant.ContentType;
import com.gig.collide.api.content.request.*;
import com.gig.collide.api.content.response.ContentQueryResponse;
import com.gig.collide.api.content.response.ContentResponse;
import com.gig.collide.api.content.response.data.ContentInfo;
import com.gig.collide.api.content.response.data.ContentStatistics;
import com.gig.collide.api.content.service.ContentFacadeService;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 内容管理控制器
 * 提供内容相关的 REST API 接口
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/content")
@RequiredArgsConstructor
@Validated
@Tag(name = "内容管理", description = "内容发布、管理、浏览相关接口")
public class ContentController {

    private final ContentFacadeService contentFacadeService;

    /**
     * 创建内容
     */
    @PostMapping
    @SaCheckLogin
    @Operation(summary = "创建内容", description = "创建新的内容")
    public Result<Long> createContent(@Valid @RequestBody ContentCreateParam param) {
        Long userId = StpUtil.getLoginIdAsLong();
        
        ContentCreateRequest request = new ContentCreateRequest();
        request.setTitle(param.getTitle());
        request.setDescription(param.getDescription());
        request.setContentType(param.getContentType());
        request.setContentData(param.getContentData());
        request.setCoverUrl(param.getCoverUrl());
        request.setAuthorId(userId);
        request.setCategoryId(param.getCategoryId());
        request.setTags(param.getTags());
        
        ContentResponse response = contentFacadeService.createContent(request);
        
        if (response.getSuccess()) {
            return Result.success(response.getContentId());
        } else {
            return Result.error(response.getResponseCode(), response.getResponseMessage());
        }
    }

    /**
     * 更新内容
     */
    @PutMapping("/{contentId}")
    @SaCheckLogin
    @Operation(summary = "更新内容", description = "更新已存在的内容")
    public Result<Boolean> updateContent(@PathVariable Long contentId,
                                        @Valid @RequestBody ContentUpdateParam param) {
        Long userId = StpUtil.getLoginIdAsLong();
        
        ContentUpdateRequest request = new ContentUpdateRequest();
        request.setContentId(contentId);
        request.setTitle(param.getTitle());
        request.setDescription(param.getDescription());
        request.setContentType(param.getContentType());
        request.setContentData(param.getContentData());
        request.setCoverUrl(param.getCoverUrl());
        request.setAuthorId(userId);
        request.setCategoryId(param.getCategoryId());
        request.setTags(param.getTags());
        
        ContentResponse response = contentFacadeService.updateContent(request);
        
        if (response.getSuccess()) {
            return Result.success(true);
        } else {
            return Result.error(response.getResponseCode(), response.getResponseMessage());
        }
    }

    /**
     * 删除内容
     */
    @DeleteMapping("/{contentId}")
    @SaCheckLogin
    @Operation(summary = "删除内容", description = "删除指定内容")
    public Result<Boolean> deleteContent(@PathVariable Long contentId) {
        Long userId = StpUtil.getLoginIdAsLong();
        
        ContentDeleteRequest request = new ContentDeleteRequest();
        request.setContentId(contentId);
        request.setAuthorId(userId);
        
        ContentResponse response = contentFacadeService.deleteContent(request);
        
        if (response.getSuccess()) {
            return Result.success(true);
        } else {
            return Result.error(response.getResponseCode(), response.getResponseMessage());
        }
    }

    /**
     * 发布内容
     */
    @PostMapping("/{contentId}/publish")
    @SaCheckLogin
    @Operation(summary = "发布内容", description = "发布指定内容")
    public Result<Boolean> publishContent(@PathVariable Long contentId) {
        Long userId = StpUtil.getLoginIdAsLong();
        
        ContentPublishRequest request = new ContentPublishRequest();
        request.setContentId(contentId);
        request.setAuthorId(userId);
        
        ContentResponse response = contentFacadeService.publishContent(request);
        
        if (response.getSuccess()) {
            return Result.success(true);
        } else {
            return Result.error(response.getResponseCode(), response.getResponseMessage());
        }
    }

    /**
     * 查看内容详情
     */
    @GetMapping("/{contentId}")
    @Operation(summary = "查看内容详情", description = "查看指定内容的详细信息")
    public Result<ContentInfo> getContentDetail(@PathVariable Long contentId) {
        ContentQueryRequest request = new ContentQueryRequest();
        request.setContentId(contentId);
        request.setViewContent(true); // 查看内容，增加查看数
        
        ContentQueryResponse<ContentInfo> response = contentFacadeService.queryContent(request);
        
        if (response.getSuccess()) {
            return Result.success(response.getData());
        } else {
            return Result.error(response.getResponseCode(), response.getResponseMessage());
        }
    }

    /**
     * 获取内容列表
     */
    @GetMapping
    @Operation(summary = "获取内容列表", description = "分页获取内容列表")
    public Result<PageResponse<ContentInfo>> getContentList(
            @RequestParam(defaultValue = "LATEST") String type,
            @RequestParam(required = false) ContentType contentType,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") @Min(1) Integer pageNo,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer pageSize) {
        
        ContentQueryRequest request = new ContentQueryRequest();
        request.setQueryType(type);
        request.setContentType(contentType);
        request.setKeyword(keyword);
        request.setPageNo(pageNo);
        request.setPageSize(pageSize);
        
        PageResponse<ContentInfo> response = contentFacadeService.pageQueryContents(request);
        return Result.success(response);
    }

    /**
     * 获取用户的内容列表
     */
    @GetMapping("/user/{authorId}")
    @Operation(summary = "获取用户内容列表", description = "获取指定用户的内容列表")
    public Result<PageResponse<ContentInfo>> getUserContents(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "1") @Min(1) Integer pageNo,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer pageSize) {
        
        ContentQueryRequest request = new ContentQueryRequest();
        request.setAuthorId(authorId);
        request.setPageNo(pageNo);
        request.setPageSize(pageSize);
        
        PageResponse<ContentInfo> response = contentFacadeService.queryUserContents(request);
        return Result.success(response);
    }

    /**
     * 获取当前用户的内容列表
     */
    @GetMapping("/my")
    @SaCheckLogin
    @Operation(summary = "获取我的内容列表", description = "获取当前用户的内容列表")
    public Result<PageResponse<ContentInfo>> getMyContents(
            @RequestParam(defaultValue = "1") @Min(1) Integer pageNo,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer pageSize) {
        
        Long userId = StpUtil.getLoginIdAsLong();
        
        ContentQueryRequest request = new ContentQueryRequest();
        request.setAuthorId(userId);
        request.setPageNo(pageNo);
        request.setPageSize(pageSize);
        
        PageResponse<ContentInfo> response = contentFacadeService.queryUserContents(request);
        return Result.success(response);
    }

    /**
     * 点赞内容
     */
    @PostMapping("/{contentId}/like")
    @SaCheckLogin
    @Operation(summary = "点赞内容", description = "对指定内容进行点赞")
    public Result<Boolean> likeContent(@PathVariable Long contentId) {
        Long userId = StpUtil.getLoginIdAsLong();
        
        ContentLikeRequest request = new ContentLikeRequest();
        request.setContentId(contentId);
        request.setUserId(userId);
        
        ContentResponse response = contentFacadeService.likeContent(request);
        
        if (response.getSuccess()) {
            return Result.success(true);
        } else {
            return Result.error(response.getResponseCode(), response.getResponseMessage());
        }
    }

    /**
     * 收藏内容
     */
    @PostMapping("/{contentId}/favorite")
    @SaCheckLogin
    @Operation(summary = "收藏内容", description = "收藏指定内容")
    public Result<Boolean> favoriteContent(@PathVariable Long contentId) {
        Long userId = StpUtil.getLoginIdAsLong();
        
        ContentFavoriteRequest request = new ContentFavoriteRequest();
        request.setContentId(contentId);
        request.setUserId(userId);
        
        ContentResponse response = contentFacadeService.favoriteContent(request);
        
        if (response.getSuccess()) {
            return Result.success(true);
        } else {
            return Result.error(response.getResponseCode(), response.getResponseMessage());
        }
    }

    /**
     * 分享内容
     */
    @PostMapping("/{contentId}/share")
    @SaCheckLogin
    @Operation(summary = "分享内容", description = "分享指定内容")
    public Result<Boolean> shareContent(@PathVariable Long contentId) {
        Long userId = StpUtil.getLoginIdAsLong();
        
        ContentShareRequest request = new ContentShareRequest();
        request.setContentId(contentId);
        request.setUserId(userId);
        
        ContentResponse response = contentFacadeService.shareContent(request);
        
        if (response.getSuccess()) {
            return Result.success(true);
        } else {
            return Result.error(response.getResponseCode(), response.getResponseMessage());
        }
    }

    /**
     * 获取内容统计信息
     */
    @GetMapping("/{contentId}/statistics")
    @Operation(summary = "获取内容统计", description = "获取指定内容的统计信息")
    public Result<ContentStatistics> getContentStatistics(@PathVariable Long contentId) {
        ContentQueryResponse<ContentStatistics> response = 
            contentFacadeService.getContentStatistics(contentId);
        
        if (response.getSuccess()) {
            return Result.success(response.getData());
        } else {
            return Result.error(response.getResponseCode(), response.getResponseMessage());
        }
    }

    // 内部参数类

    /**
     * 创建内容参数
     */
    public static class ContentCreateParam {
        @Parameter(description = "标题", required = true)
        private String title;
        
        @Parameter(description = "描述")
        private String description;
        
        @Parameter(description = "内容类型", required = true)
        private ContentType contentType;
        
        @Parameter(description = "内容数据")
        private Map<String, Object> contentData;
        
        @Parameter(description = "封面URL")
        private String coverUrl;
        
        @Parameter(description = "分类ID")
        private Long categoryId;
        
        @Parameter(description = "标签列表")
        private java.util.List<String> tags;

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public ContentType getContentType() { return contentType; }
        public void setContentType(ContentType contentType) { this.contentType = contentType; }
        
        public Map<String, Object> getContentData() { return contentData; }
        public void setContentData(Map<String, Object> contentData) { this.contentData = contentData; }
        
        public String getCoverUrl() { return coverUrl; }
        public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
        
        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
        
        public java.util.List<String> getTags() { return tags; }
        public void setTags(java.util.List<String> tags) { this.tags = tags; }
    }

    /**
     * 更新内容参数
     */
    public static class ContentUpdateParam {
        @Parameter(description = "标题", required = true)
        private String title;
        
        @Parameter(description = "描述")
        private String description;
        
        @Parameter(description = "内容类型", required = true)
        private ContentType contentType;
        
        @Parameter(description = "内容数据")
        private Map<String, Object> contentData;
        
        @Parameter(description = "封面URL")
        private String coverUrl;
        
        @Parameter(description = "分类ID")
        private Long categoryId;
        
        @Parameter(description = "标签列表")
        private java.util.List<String> tags;

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public ContentType getContentType() { return contentType; }
        public void setContentType(ContentType contentType) { this.contentType = contentType; }
        
        public Map<String, Object> getContentData() { return contentData; }
        public void setContentData(Map<String, Object> contentData) { this.contentData = contentData; }
        
        public String getCoverUrl() { return coverUrl; }
        public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
        
        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
        
        public java.util.List<String> getTags() { return tags; }
        public void setTags(java.util.List<String> tags) { this.tags = tags; }
    }
} 