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
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/content")
@RequiredArgsConstructor
@Validated
public class ContentController {

    private final ContentFacadeService contentFacadeService;

    @PostMapping
    @SaCheckLogin
    public Result<ContentResponse> createContent(@Valid @RequestBody ContentCreateParam param) {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            log.info("用户创建内容，用户ID: {}, 标题: {}", currentUserId, param.getTitle());

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

    @PutMapping("/{contentId}")
    @SaCheckLogin
    public Result<ContentResponse> updateContent(@PathVariable Long contentId,
                                                @Valid @RequestBody ContentUpdateParam param) {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            log.info("用户更新内容，用户ID: {}, 内容ID: {}", currentUserId, contentId);

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

    @DeleteMapping("/{contentId}")
    @SaCheckLogin
    public Result<ContentResponse> deleteContent(@PathVariable Long contentId) {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            log.info("用户删除内容，用户ID: {}, 内容ID: {}", currentUserId, contentId);

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

    @PostMapping("/{contentId}/publish")
    @SaCheckLogin
    public Result<ContentResponse> publishContent(@PathVariable Long contentId) {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            log.info("用户发布内容，用户ID: {}, 内容ID: {}", currentUserId, contentId);

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

    @GetMapping("/{contentId}")
    public Result<ContentInfo> getContent(@PathVariable Long contentId) {
        try {
            ContentQueryRequest queryRequest = new ContentQueryRequest();
            queryRequest.setContentId(contentId);
            queryRequest.setViewContent(true);

            ContentQueryResponse<ContentInfo> queryResponse = contentFacadeService.queryContent(queryRequest);
            return Result.success(queryResponse.getData());
        } catch (Exception e) {
            log.error("获取内容详情失败", e);
            return Result.error("CONTENT_GET_ERROR", "获取内容详情失败，请稍后重试");
        }
    }

    @GetMapping
    public Result<PageResponse<ContentInfo>> getContentList(
            @RequestParam(defaultValue = "LATEST") String type,
            @RequestParam(required = false) ContentType contentType,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") @Min(1) Integer pageNo,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer pageSize) {
        try {
            ContentQueryRequest queryRequest = new ContentQueryRequest();
            queryRequest.setQueryType(QueryType.valueOf(type));
            queryRequest.setContentType(contentType);
            queryRequest.setCategoryId(categoryId);
            queryRequest.setKeyword(keyword);
            queryRequest.setCurrentPage(pageNo);
            queryRequest.setPageSize(pageSize);

            PageResponse<ContentInfo> pageResponse = contentFacadeService.pageQueryContents(queryRequest);
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("获取内容列表失败", e);
            return Result.error("CONTENT_LIST_ERROR", "获取内容列表失败，请稍后重试");
        }
    }

    @GetMapping("/user/{authorId}")
    public Result<PageResponse<ContentInfo>> getUserContent(@PathVariable Long authorId,
                                                           @RequestParam(defaultValue = "1") @Min(1) Integer pageNo,
                                                           @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer pageSize) {
        try {
            ContentQueryRequest queryRequest = new ContentQueryRequest();
            queryRequest.setAuthorId(authorId);
            queryRequest.setCurrentPage(pageNo);
            queryRequest.setPageSize(pageSize);

            PageResponse<ContentInfo> pageResponse = contentFacadeService.queryUserContents(queryRequest);
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("获取用户内容失败", e);
            return Result.error("USER_CONTENT_ERROR", "获取用户内容失败，请稍后重试");
        }
    }

    @GetMapping("/my")
    @SaCheckLogin
    public Result<PageResponse<ContentInfo>> getMyContent(
            @RequestParam(defaultValue = "1") @Min(1) Integer pageNo,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer pageSize) {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();

            ContentQueryRequest queryRequest = new ContentQueryRequest();
            queryRequest.setAuthorId(currentUserId);
            queryRequest.setCurrentPage(pageNo);
            queryRequest.setPageSize(pageSize);

            PageResponse<ContentInfo> pageResponse = contentFacadeService.queryUserContents(queryRequest);
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("获取我的内容失败", e);
            return Result.error("MY_CONTENT_ERROR", "获取我的内容失败，请稍后重试");
        }
    }

    @PostMapping("/{contentId}/like")
    @SaCheckLogin
    public Result<ContentResponse> likeContent(@PathVariable Long contentId) {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            log.info("用户点赞内容，用户ID: {}, 内容ID: {}", currentUserId, contentId);

            ContentLikeRequest likeRequest = new ContentLikeRequest();
            likeRequest.setContentId(contentId);
            likeRequest.setUserId(currentUserId);
            likeRequest.setLiked(true);

            ContentResponse contentResponse = contentFacadeService.likeContent(likeRequest);
            return Result.success(contentResponse);
        } catch (Exception e) {
            log.error("点赞内容失败", e);
            return Result.error("CONTENT_LIKE_ERROR", "点赞失败，请稍后重试");
        }
    }

    @PostMapping("/{contentId}/favorite")
    @SaCheckLogin
    public Result<ContentResponse> favoriteContent(@PathVariable Long contentId) {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            log.info("用户收藏内容，用户ID: {}, 内容ID: {}", currentUserId, contentId);

            ContentFavoriteRequest favoriteRequest = new ContentFavoriteRequest();
            favoriteRequest.setContentId(contentId);
            favoriteRequest.setUserId(currentUserId);
            favoriteRequest.setFavorited(true);

            ContentResponse contentResponse = contentFacadeService.favoriteContent(favoriteRequest);
            return Result.success(contentResponse);
        } catch (Exception e) {
            log.error("收藏内容失败", e);
            return Result.error("CONTENT_FAVORITE_ERROR", "收藏失败，请稍后重试");
        }
    }

    @PostMapping("/{contentId}/share")
    @SaCheckLogin
    public Result<ContentResponse> shareContent(@PathVariable Long contentId,
                                               @RequestParam(required = false) String platform,
                                               @RequestParam(required = false) String shareText) {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            log.info("用户分享内容，用户ID: {}, 内容ID: {}", currentUserId, contentId);

            ContentShareRequest shareRequest = new ContentShareRequest();
            shareRequest.setContentId(contentId);
            shareRequest.setUserId(currentUserId);
            shareRequest.setPlatform(platform);
            shareRequest.setShareText(shareText);

            ContentResponse contentResponse = contentFacadeService.shareContent(shareRequest);
            return Result.success(contentResponse);
        } catch (Exception e) {
            log.error("分享内容失败", e);
            return Result.error("CONTENT_SHARE_ERROR", "分享失败，请稍后重试");
        }
    }

    @GetMapping("/{contentId}/statistics")
    public Result<ContentStatistics> getContentStatistics(@PathVariable Long contentId) {
        try {
            ContentQueryResponse<ContentStatistics> queryResponse = 
                contentFacadeService.getContentStatistics(contentId);
            return Result.success(queryResponse.getData());
        } catch (Exception e) {
            log.error("获取内容统计失败", e);
            return Result.error("CONTENT_STATISTICS_ERROR", "获取内容统计失败，请稍后重试");
        }
    }

    /**
     * 内容创建参数
     */
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

    /**
     * 内容更新参数
     */
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