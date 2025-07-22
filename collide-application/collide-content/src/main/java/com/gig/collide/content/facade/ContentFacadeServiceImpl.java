package com.gig.collide.content.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.content.request.*;
import com.gig.collide.api.content.response.*;
import com.gig.collide.api.content.response.data.ContentInfo;
import com.gig.collide.api.content.response.data.ContentStatistics;
import com.gig.collide.api.content.service.ContentFacadeService;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.content.domain.entity.Content;
import com.gig.collide.content.domain.entity.convertor.ContentConvertor;
import com.gig.collide.content.domain.service.ContentDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 内容服务 Facade 实现
 * 对外提供 RPC 服务接口
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@Component
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class ContentFacadeServiceImpl implements ContentFacadeService {

    private final ContentDomainService contentDomainService;

    @Override
    public ContentResponse createContent(ContentCreateRequest createRequest) {
        try {
            log.info("创建内容请求，标题：{}，作者：{}", 
                createRequest.getTitle(), createRequest.getAuthorId());

            // 构建内容实体
            Content content = buildContentFromCreateRequest(createRequest);

            // 调用领域服务创建内容
            Content createdContent = contentDomainService.createContent(content);

            // 构建成功响应
            ContentResponse response = new ContentResponse();
            response.setSuccess(true);
            response.setResponseMessage("内容创建成功");
            response.setContentId(createdContent.getId());

            log.info("内容创建成功，内容ID：{}", createdContent.getId());
            return response;

        } catch (Exception e) {
            log.error("内容创建失败", e);
            ContentResponse response = new ContentResponse();
            response.setSuccess(false);
            response.setResponseCode("CONTENT_CREATE_ERROR");
            response.setResponseMessage("内容创建失败: " + e.getMessage());
            return response;
        }
    }

    @Override
    public ContentResponse updateContent(ContentUpdateRequest updateRequest) {
        try {
            log.info("更新内容请求，内容ID：{}", updateRequest.getContentId());

            // 构建内容实体
            Content content = buildContentFromUpdateRequest(updateRequest);

            // 调用领域服务更新内容
            Content updatedContent = contentDomainService.updateContent(content);

            // 构建成功响应
            ContentResponse response = new ContentResponse();
            response.setSuccess(true);
            response.setResponseMessage("内容更新成功");
            response.setContentId(updatedContent.getId());

            log.info("内容更新成功，内容ID：{}", updatedContent.getId());
            return response;

        } catch (Exception e) {
            log.error("内容更新失败", e);
            ContentResponse response = new ContentResponse();
            response.setSuccess(false);
            response.setResponseCode("CONTENT_UPDATE_ERROR");
            response.setResponseMessage("内容更新失败: " + e.getMessage());
            return response;
        }
    }

    @Override
    public ContentResponse deleteContent(ContentDeleteRequest deleteRequest) {
        try {
            log.info("删除内容请求，内容ID：{}，作者ID：{}", 
                deleteRequest.getContentId(), deleteRequest.getAuthorId());

            // 调用领域服务删除内容
            contentDomainService.deleteContent(
                deleteRequest.getContentId(), 
                deleteRequest.getAuthorId()
            );

            // 构建成功响应
            ContentResponse response = new ContentResponse();
            response.setSuccess(true);
            response.setResponseMessage("内容删除成功");
            response.setContentId(deleteRequest.getContentId());

            log.info("内容删除成功，内容ID：{}", deleteRequest.getContentId());
            return response;

        } catch (Exception e) {
            log.error("内容删除失败", e);
            ContentResponse response = new ContentResponse();
            response.setSuccess(false);
            response.setResponseCode("CONTENT_DELETE_ERROR");
            response.setResponseMessage("内容删除失败: " + e.getMessage());
            return response;
        }
    }

    @Override
    public ContentResponse publishContent(ContentPublishRequest publishRequest) {
        try {
            log.info("发布内容请求，内容ID：{}", publishRequest.getContentId());

            // 调用领域服务发布内容
            Content publishedContent = contentDomainService.publishContent(
                publishRequest.getContentId(),
                publishRequest.getAuthorId()
            );

            // 构建成功响应
            ContentResponse response = new ContentResponse();
            response.setSuccess(true);
            response.setResponseMessage("内容发布成功");
            response.setContentId(publishedContent.getId());

            log.info("内容发布成功，内容ID：{}", publishedContent.getId());
            return response;

        } catch (Exception e) {
            log.error("内容发布失败", e);
            ContentResponse response = new ContentResponse();
            response.setSuccess(false);
            response.setResponseCode("CONTENT_PUBLISH_ERROR");
            response.setResponseMessage("内容发布失败: " + e.getMessage());
            return response;
        }
    }

    @Override
    public ContentQueryResponse<ContentInfo> queryContent(ContentQueryRequest queryRequest) {
        try {
            log.info("查询内容详情，内容ID：{}", queryRequest.getContentId());

            // 查询内容详情
            Content content;
            if (queryRequest.isViewContent()) {
                // 查看内容（增加查看数）
                content = contentDomainService.viewContent(queryRequest.getContentId());
            } else {
                // 普通查询
                content = contentDomainService.getContentById(queryRequest.getContentId());
            }

            ContentQueryResponse<ContentInfo> response = new ContentQueryResponse<>();
            if (content != null) {
                ContentInfo contentInfo = ContentConvertor.INSTANCE.toContentInfo(content);
                // TODO: 设置作者信息和分类名称
                // TODO: 设置用户相关状态（点赞、收藏）
                
                response.setSuccess(true);
                response.setResponseMessage("查询成功");
                response.setData(contentInfo);
            } else {
                response.setSuccess(false);
                response.setResponseCode("CONTENT_NOT_FOUND");
                response.setResponseMessage("内容不存在");
            }

            return response;

        } catch (Exception e) {
            log.error("内容查询失败", e);
            ContentQueryResponse<ContentInfo> response = new ContentQueryResponse<>();
            response.setSuccess(false);
            response.setResponseCode("CONTENT_QUERY_ERROR");
            response.setResponseMessage("内容查询失败: " + e.getMessage());
            return response;
        }
    }

    @Override
    public PageResponse<ContentInfo> pageQueryContents(ContentQueryRequest queryRequest) {
        try {
            log.info("分页查询内容列表，查询类型：{}", queryRequest.getQueryType());

            IPage<Content> contentPage;

            switch (queryRequest.getQueryType()) {
                case "RECOMMENDED":
                    contentPage = contentDomainService.getRecommendedContents(
                        queryRequest.getContentType(),
                        queryRequest.getPageNo(),
                        queryRequest.getPageSize()
                    );
                    break;
                case "HOT":
                    contentPage = contentDomainService.getHotContents(
                        queryRequest.getContentType(),
                        7, // 默认查询7天内的热门内容
                        queryRequest.getPageNo(),
                        queryRequest.getPageSize()
                    );
                    break;
                case "LATEST":
                    contentPage = contentDomainService.getLatestContents(
                        queryRequest.getContentType(),
                        queryRequest.getPageNo(),
                        queryRequest.getPageSize()
                    );
                    break;
                case "SEARCH":
                    contentPage = contentDomainService.searchContents(
                        queryRequest.getKeyword(),
                        queryRequest.getPageNo(),
                        queryRequest.getPageSize()
                    );
                    break;
                default:
                    contentPage = contentDomainService.getLatestContents(
                        queryRequest.getContentType(),
                        queryRequest.getPageNo(),
                        queryRequest.getPageSize()
                    );
                    break;
            }

            // 转换为DTO
            List<ContentInfo> contentInfoList = ContentConvertor.INSTANCE
                .toContentInfoList(contentPage.getRecords());

            // TODO: 批量设置作者信息和分类名称
            // TODO: 批量设置用户相关状态

            return PageResponse.of(
                contentInfoList,
                contentPage.getTotal(),
                contentPage.getSize(),
                contentPage.getCurrent()
            );

        } catch (Exception e) {
            log.error("内容分页查询失败", e);
            return PageResponse.error("内容查询失败: " + e.getMessage());
        }
    }

    @Override
    public PageResponse<ContentInfo> queryUserContents(ContentQueryRequest queryRequest) {
        try {
            log.info("查询用户内容列表，作者ID：{}", queryRequest.getAuthorId());

            IPage<Content> contentPage = contentDomainService.getUserContents(
                queryRequest.getAuthorId(),
                queryRequest.getStatus(),
                queryRequest.getPageNo(),
                queryRequest.getPageSize()
            );

            // 转换为DTO
            List<ContentInfo> contentInfoList = ContentConvertor.INSTANCE
                .toContentInfoList(contentPage.getRecords());

            return PageResponse.of(
                contentInfoList,
                contentPage.getTotal(),
                contentPage.getSize(),
                contentPage.getCurrent()
            );

        } catch (Exception e) {
            log.error("用户内容查询失败", e);
            return PageResponse.error("用户内容查询失败: " + e.getMessage());
        }
    }

    @Override
    public ContentQueryResponse<ContentStatistics> getContentStatistics(Long contentId) {
        try {
            log.info("查询内容统计信息，内容ID：{}", contentId);

            Content content = contentDomainService.getContentById(contentId);

            ContentQueryResponse<ContentStatistics> response = new ContentQueryResponse<>();
            if (content != null) {
                ContentStatistics statistics = ContentConvertor.INSTANCE
                    .toContentStatistics(content);
                
                response.setSuccess(true);
                response.setResponseMessage("查询成功");
                response.setData(statistics);
            } else {
                response.setSuccess(false);
                response.setResponseCode("CONTENT_NOT_FOUND");
                response.setResponseMessage("内容不存在");
            }

            return response;

        } catch (Exception e) {
            log.error("内容统计查询失败", e);
            ContentQueryResponse<ContentStatistics> response = new ContentQueryResponse<>();
            response.setSuccess(false);
            response.setResponseCode("CONTENT_STATISTICS_ERROR");
            response.setResponseMessage("内容统计查询失败: " + e.getMessage());
            return response;
        }
    }

    @Override
    public ContentResponse likeContent(ContentLikeRequest likeRequest) {
        try {
            log.info("点赞内容请求，内容ID：{}，用户ID：{}", 
                likeRequest.getContentId(), likeRequest.getUserId());

            boolean result = contentDomainService.likeContent(
                likeRequest.getContentId(),
                likeRequest.getUserId()
            );

            ContentResponse response = new ContentResponse();
            response.setSuccess(result);
            response.setResponseMessage(result ? "点赞成功" : "点赞失败");
            response.setContentId(likeRequest.getContentId());

            return response;

        } catch (Exception e) {
            log.error("内容点赞失败", e);
            ContentResponse response = new ContentResponse();
            response.setSuccess(false);
            response.setResponseCode("CONTENT_LIKE_ERROR");
            response.setResponseMessage("内容点赞失败: " + e.getMessage());
            return response;
        }
    }

    @Override
    public ContentResponse favoriteContent(ContentFavoriteRequest favoriteRequest) {
        try {
            log.info("收藏内容请求，内容ID：{}，用户ID：{}", 
                favoriteRequest.getContentId(), favoriteRequest.getUserId());

            boolean result = contentDomainService.favoriteContent(
                favoriteRequest.getContentId(),
                favoriteRequest.getUserId()
            );

            ContentResponse response = new ContentResponse();
            response.setSuccess(result);
            response.setResponseMessage(result ? "收藏成功" : "收藏失败");
            response.setContentId(favoriteRequest.getContentId());

            return response;

        } catch (Exception e) {
            log.error("内容收藏失败", e);
            ContentResponse response = new ContentResponse();
            response.setSuccess(false);
            response.setResponseCode("CONTENT_FAVORITE_ERROR");
            response.setResponseMessage("内容收藏失败: " + e.getMessage());
            return response;
        }
    }

    @Override
    public ContentResponse shareContent(ContentShareRequest shareRequest) {
        try {
            log.info("分享内容请求，内容ID：{}，用户ID：{}", 
                shareRequest.getContentId(), shareRequest.getUserId());

            boolean result = contentDomainService.shareContent(
                shareRequest.getContentId(),
                shareRequest.getUserId()
            );

            ContentResponse response = new ContentResponse();
            response.setSuccess(result);
            response.setResponseMessage(result ? "分享成功" : "分享失败");
            response.setContentId(shareRequest.getContentId());

            return response;

        } catch (Exception e) {
            log.error("内容分享失败", e);
            ContentResponse response = new ContentResponse();
            response.setSuccess(false);
            response.setResponseCode("CONTENT_SHARE_ERROR");
            response.setResponseMessage("内容分享失败: " + e.getMessage());
            return response;
        }
    }

    // 私有辅助方法

    /**
     * 从创建请求构建内容实体
     */
    private Content buildContentFromCreateRequest(ContentCreateRequest request) {
        Content content = new Content();
        content.setTitle(request.getTitle());
        content.setDescription(request.getDescription());
        content.setContentType(request.getContentType());
        content.setAuthorId(request.getAuthorId());
        content.setCategoryId(request.getCategoryId());
        content.setCoverUrl(request.getCoverUrl());

        // 设置内容数据
        if (request.getContentData() != null) {
            content.setContentDataJson(request.getContentData());
        }

        // 设置标签
        if (request.getTags() != null) {
            content.setTagList(request.getTags());
        }

        return content;
    }

    /**
     * 从更新请求构建内容实体
     */
    private Content buildContentFromUpdateRequest(ContentUpdateRequest request) {
        Content content = new Content();
        content.setId(request.getContentId());
        content.setTitle(request.getTitle());
        content.setDescription(request.getDescription());
        content.setContentType(request.getContentType());
        content.setAuthorId(request.getAuthorId());
        content.setCategoryId(request.getCategoryId());
        content.setCoverUrl(request.getCoverUrl());

        // 设置内容数据
        if (request.getContentData() != null) {
            content.setContentDataJson(request.getContentData());
        }

        // 设置标签
        if (request.getTags() != null) {
            content.setTagList(request.getTags());
        }

        return content;
    }
} 