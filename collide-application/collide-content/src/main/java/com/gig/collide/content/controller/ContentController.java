package com.gig.collide.content.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.gig.collide.api.content.constant.ContentType;
import com.gig.collide.api.content.constant.QueryType;
import com.gig.collide.api.content.request.*;
import com.gig.collide.api.content.response.ContentResponse;
import com.gig.collide.api.content.response.ContentQueryResponse;
import com.gig.collide.api.content.response.data.ContentInfo;
import com.gig.collide.api.content.response.data.ContentStatistics;
import com.gig.collide.api.content.service.ContentFacadeService;
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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 内容控制器
 * 提供内容管理相关的 REST API
 * 
 * 权限说明：
 * - 创建、更新、删除内容：需要博主角色权限
 * - 查看、点赞、收藏、分享：所有用户都可以使用
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
@Tag(name = "内容管理", description = "内容创作、发布、管理相关接口（博主权限）")
public class ContentController {

    private final ContentFacadeService contentFacadeService;

    /**
     * 创建内容（需要博主权限）
     */
    @PostMapping
    @SaCheckLogin
    @Operation(summary = "创建内容", description = "创建新的内容（草稿状态），需要博主角色权限")
    public Result<ContentResponse> createContent(@Valid @RequestBody ContentCreateParam param) {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            log.info("博主创建内容，用户ID: {}, 标题: {}", currentUserId, param.getTitle());

            ContentCreateRequest createRequest = new ContentCreateRequest();
            createRequest.setTitle(param.getTitle());
            createRequest.setDescription(param.getDescription());
            createRequest.setContentType(param.getContentType());
            createRequest.setContentData(param.getContentData());
            createRequest.setCoverUrl(param.getCoverUrl());
            createRequest.setAuthorId(currentUserId);
            createRequest.setCategoryId(param.getCategoryId());
            createRequest.setTags(param.getTags());
            createRequest.setAllowComment(param.getAllowComment());
            createRequest.setAllowShare(param.getAllowShare());

            ContentResponse contentResponse = contentFacadeService.createContent(createRequest);
            return Result.success(contentResponse);
        } catch (Exception e) {
            log.error("创建内容失败", e);
            return Result.error("CONTENT_CREATE_ERROR", "创建内容失败，请稍后重试");
        }
    }

    /**
     * 更新内容（需要博主权限）
     */
    @PutMapping("/{contentId}")
    @SaCheckLogin
    @Operation(summary = "更新内容", description = "更新指定内容的信息，需要博主角色权限")
    public Result<ContentResponse> updateContent(
            @Parameter(description = "内容ID", required = true) @PathVariable Long contentId,
            @Valid @RequestBody ContentUpdateParam param) {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            log.info("博主更新内容，用户ID: {}, 内容ID: {}", currentUserId, contentId);

            ContentUpdateRequest updateRequest = new ContentUpdateRequest();
            updateRequest.setContentId(contentId);
            updateRequest.setOperatorId(currentUserId);
            updateRequest.setTitle(param.getTitle());
            updateRequest.setDescription(param.getDescription());
            updateRequest.setContentData(param.getContentData());
            updateRequest.setCoverUrl(param.getCoverUrl());
            updateRequest.setCategoryId(param.getCategoryId());
            updateRequest.setTags(param.getTags());
            updateRequest.setAllowComment(param.getAllowComment());
            updateRequest.setAllowShare(param.getAllowShare());

            ContentResponse contentResponse = contentFacadeService.updateContent(updateRequest);
            return Result.success(contentResponse);
        } catch (Exception e) {
            log.error("更新内容失败", e);
            return Result.error("CONTENT_UPDATE_ERROR", "更新内容失败，请稍后重试");
        }
    }

    /**
     * 删除内容（需要博主权限）
     */
    @DeleteMapping("/{contentId}")
    @SaCheckLogin
    @Operation(summary = "删除内容", description = "删除指定的内容，需要博主角色权限")
    public Result<ContentResponse> deleteContent(
            @Parameter(description = "内容ID", required = true) @PathVariable Long contentId) {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            log.info("博主删除内容，用户ID: {}, 内容ID: {}", currentUserId, contentId);

            ContentDeleteRequest deleteRequest = new ContentDeleteRequest();
            deleteRequest.setContentId(contentId);
            deleteRequest.setOperatorId(currentUserId);

            ContentResponse contentResponse = contentFacadeService.deleteContent(deleteRequest);
            return Result.success(contentResponse);
        } catch (Exception e) {
            log.error("删除内容失败", e);
            return Result.error("CONTENT_DELETE_ERROR", "删除内容失败，请稍后重试");
        }
    }

    /**
     * 发布内容（需要博主权限）
     */
    @PostMapping("/{contentId}/publish")
    @SaCheckLogin
    @Operation(summary = "发布内容", description = "将草稿内容发布为公开状态，需要博主角色权限")
    public Result<ContentResponse> publishContent(
            @Parameter(description = "内容ID", required = true) @PathVariable Long contentId) {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            log.info("博主发布内容，用户ID: {}, 内容ID: {}", currentUserId, contentId);

            ContentPublishRequest publishRequest = new ContentPublishRequest();
            publishRequest.setContentId(contentId);
            publishRequest.setOperatorId(currentUserId);

            ContentResponse contentResponse = contentFacadeService.publishContent(publishRequest);
            return Result.success(contentResponse);
        } catch (Exception e) {
            log.error("发布内容失败", e);
            return Result.error("CONTENT_PUBLISH_ERROR", "发布内容失败，请稍后重试");
        }
    }

    /**
     * 获取内容详情（所有用户可用）
     */
    @GetMapping("/{contentId}")
    @Operation(summary = "获取内容详情", description = "根据内容ID获取内容详细信息，所有用户都可以查看")
    public Result<ContentInfo> getContent(
            @Parameter(description = "内容ID", required = true) @PathVariable Long contentId) {
        try {
            ContentQueryRequest queryRequest = new ContentQueryRequest();
            queryRequest.setContentId(contentId);

                    ContentQueryResponse<ContentInfo> response = contentFacadeService.queryContent(queryRequest);
        if (response.getSuccess()) {
            return Result.success(response.getData());
        } else {
            return Result.error(response.getResponseCode(), response.getResponseMessage());
        }
        } catch (Exception e) {
            log.error("获取内容详情失败", e);
            return Result.error("CONTENT_QUERY_ERROR", "获取内容详情失败，请稍后重试");
        }
    }

    /**
     * 获取内容列表（所有用户可用）
     */
    @GetMapping
    @Operation(summary = "获取内容列表", description = "分页获取内容列表，支持多种查询类型，所有用户都可以查看")
    public Result<PageResponse<ContentInfo>> getContentList(
            @Parameter(description = "查询类型：LATEST-最新, HOT-热门, RECOMMEND-推荐") 
            @RequestParam(defaultValue = "LATEST") String type,
            
            @Parameter(description = "内容类型过滤") 
            @RequestParam(required = false) ContentType contentType,
            
            @Parameter(description = "分类ID过滤") 
            @RequestParam(required = false) Long categoryId,
            
            @Parameter(description = "关键词搜索") 
            @RequestParam(required = false) String keyword,
            
            @Parameter(description = "页码", example = "1") 
            @RequestParam(defaultValue = "1") @Min(1) Integer pageNo,
            
            @Parameter(description = "每页大小", example = "20") 
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer pageSize) {
        try {
            ContentQueryRequest queryRequest = new ContentQueryRequest();
            queryRequest.setQueryType(QueryType.valueOf(type));
            queryRequest.setContentType(contentType);
            queryRequest.setCategoryId(categoryId);
            queryRequest.setKeyword(keyword);
            queryRequest.setCurrentPage(pageNo);
            queryRequest.setPageSize(pageSize);

            PageResponse<ContentInfo> response = contentFacadeService.pageQueryContents(queryRequest);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取内容列表失败", e);
            return Result.error("CONTENT_LIST_ERROR", "获取内容列表失败，请稍后重试");
        }
    }

    /**
     * 获取用户内容列表（所有用户可用）
     */
    @GetMapping("/user/{authorId}")
    @Operation(summary = "获取用户内容列表", description = "获取指定用户发布的内容列表，所有用户都可以查看")
    public Result<PageResponse<ContentInfo>> getUserContent(
            @Parameter(description = "作者ID", required = true) @PathVariable Long authorId,
            @Parameter(description = "页码", example = "1") 
            @RequestParam(defaultValue = "1") @Min(1) Integer pageNo,
            @Parameter(description = "每页大小", example = "20") 
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer pageSize) {
        try {
            ContentQueryRequest queryRequest = new ContentQueryRequest();
            queryRequest.setAuthorId(authorId);
            queryRequest.setCurrentPage(pageNo);
            queryRequest.setPageSize(pageSize);

            PageResponse<ContentInfo> response = contentFacadeService.queryUserContents(queryRequest);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取用户内容列表失败", e);
            return Result.error("USER_CONTENT_ERROR", "获取用户内容列表失败，请稍后重试");
        }
    }

    /**
     * 获取我的内容列表（需要博主权限）
     */
    @GetMapping("/my")
    @SaCheckLogin
    @Operation(summary = "获取我的内容列表", description = "获取当前博主发布的内容列表，需要博主角色权限")
    public Result<PageResponse<ContentInfo>> getMyContent(
            @Parameter(description = "页码", example = "1") 
            @RequestParam(defaultValue = "1") @Min(1) Integer pageNo,
            @Parameter(description = "每页大小", example = "20") 
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer pageSize) {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            ContentQueryRequest queryRequest = new ContentQueryRequest();
            queryRequest.setAuthorId(currentUserId);
            queryRequest.setCurrentPage(pageNo);
            queryRequest.setPageSize(pageSize);

            PageResponse<ContentInfo> response = contentFacadeService.queryUserContents(queryRequest);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取我的内容列表失败", e);
            return Result.error("MY_CONTENT_ERROR", "获取我的内容列表失败，请稍后重试");
        }
    }

    /**
     * 点赞内容（所有用户可用）
     */
    @PostMapping("/{contentId}/like")
    @SaCheckLogin
    @Operation(summary = "点赞内容", description = "对指定内容进行点赞操作，所有登录用户都可以使用")
    public Result<ContentResponse> likeContent(
            @Parameter(description = "内容ID", required = true) @PathVariable Long contentId) {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            log.info("用户点赞内容，用户ID: {}, 内容ID: {}", currentUserId, contentId);

            ContentLikeRequest likeRequest = new ContentLikeRequest();
            likeRequest.setContentId(contentId);
            likeRequest.setUserId(currentUserId);

            ContentResponse contentResponse = contentFacadeService.likeContent(likeRequest);
            return Result.success(contentResponse);
        } catch (Exception e) {
            log.error("点赞内容失败", e);
            return Result.error("CONTENT_LIKE_ERROR", "点赞内容失败，请稍后重试");
        }
    }

    /**
     * 收藏内容（所有用户可用）
     */
    @PostMapping("/{contentId}/favorite")
    @SaCheckLogin
    @Operation(summary = "收藏内容", description = "将指定内容添加到收藏夹，所有登录用户都可以使用")
    public Result<ContentResponse> favoriteContent(
            @Parameter(description = "内容ID", required = true) @PathVariable Long contentId) {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            log.info("用户收藏内容，用户ID: {}, 内容ID: {}", currentUserId, contentId);

            ContentFavoriteRequest favoriteRequest = new ContentFavoriteRequest();
            favoriteRequest.setContentId(contentId);
            favoriteRequest.setUserId(currentUserId);

            ContentResponse contentResponse = contentFacadeService.favoriteContent(favoriteRequest);
            return Result.success(contentResponse);
        } catch (Exception e) {
            log.error("收藏内容失败", e);
            return Result.error("CONTENT_FAVORITE_ERROR", "收藏内容失败，请稍后重试");
        }
    }

    /**
     * 分享内容（所有用户可用）
     */
    @PostMapping("/{contentId}/share")
    @SaCheckLogin
    @Operation(summary = "分享内容", description = "分享指定内容到其他平台，所有登录用户都可以使用")
    public Result<ContentResponse> shareContent(
            @Parameter(description = "内容ID", required = true) @PathVariable Long contentId,
            @Parameter(description = "分享平台") @RequestParam(required = false) String platform,
            @Parameter(description = "分享文案") @RequestParam(required = false) String shareText) {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            log.info("用户分享内容，用户ID: {}, 内容ID: {}, 平台: {}", currentUserId, contentId, platform);

            ContentShareRequest shareRequest = new ContentShareRequest();
            shareRequest.setContentId(contentId);
            shareRequest.setUserId(currentUserId);
            shareRequest.setPlatform(platform);
            shareRequest.setShareText(shareText);

            ContentResponse contentResponse = contentFacadeService.shareContent(shareRequest);
            return Result.success(contentResponse);
        } catch (Exception e) {
            log.error("分享内容失败", e);
            return Result.error("CONTENT_SHARE_ERROR", "分享内容失败，请稍后重试");
        }
    }

    /**
     * 获取内容统计信息（所有用户可用）
     */
    @GetMapping("/{contentId}/statistics")
    @Operation(summary = "获取内容统计信息", description = "获取指定内容的浏览、点赞、评论等统计信息，所有用户都可以查看")
    public Result<ContentStatistics> getContentStatistics(
            @Parameter(description = "内容ID", required = true) @PathVariable Long contentId) {
        try {
            ContentQueryResponse<ContentStatistics> response = contentFacadeService.getContentStatistics(contentId);
            if (response.getSuccess()) {
                return Result.success(response.getData());
            } else {
                return Result.error(response.getResponseCode(), response.getResponseMessage());
            }
        } catch (Exception e) {
            log.error("获取内容统计信息失败", e);
            return Result.error("CONTENT_STATISTICS_ERROR", "获取内容统计信息失败，请稍后重试");
        }
    }

    // 请求参数类
    public static class ContentCreateParam {
        @NotNull(message = "标题不能为空")
        private String title;
        
        private String description;
        
        @NotNull(message = "内容类型不能为空")
        private ContentType contentType;
        
        private Map<String, Object> contentData;
        private String coverUrl;
        private Long categoryId;
        private List<String> tags;
        private Boolean allowComment = true;
        private Boolean allowShare = true;

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
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        public Boolean getAllowComment() { return allowComment; }
        public void setAllowComment(Boolean allowComment) { this.allowComment = allowComment; }
        public Boolean getAllowShare() { return allowShare; }
        public void setAllowShare(Boolean allowShare) { this.allowShare = allowShare; }
    }

    public static class ContentUpdateParam {
        private String title;
        private String description;
        private Map<String, Object> contentData;
        private String coverUrl;
        private Long categoryId;
        private List<String> tags;
        private Boolean allowComment;
        private Boolean allowShare;

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Map<String, Object> getContentData() { return contentData; }
        public void setContentData(Map<String, Object> contentData) { this.contentData = contentData; }
        public String getCoverUrl() { return coverUrl; }
        public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        public Boolean getAllowComment() { return allowComment; }
        public void setAllowComment(Boolean allowComment) { this.allowComment = allowComment; }
        public Boolean getAllowShare() { return allowShare; }
        public void setAllowShare(Boolean allowShare) { this.allowShare = allowShare; }
    }
} 