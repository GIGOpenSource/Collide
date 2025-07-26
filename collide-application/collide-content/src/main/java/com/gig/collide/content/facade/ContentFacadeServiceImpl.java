package com.gig.collide.content.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.content.constant.ContentType;
import com.gig.collide.api.content.constant.QueryType;
import com.gig.collide.api.content.request.*;
import com.gig.collide.api.content.response.ContentResponse;
import com.gig.collide.api.content.response.ContentQueryResponse;
import com.gig.collide.api.content.service.ContentFacadeService;
import com.gig.collide.api.content.response.data.ContentInfo;
import com.gig.collide.api.content.response.data.ContentStatistics;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.content.domain.entity.Content;
import com.gig.collide.content.domain.entity.convertor.ContentConvertor;
import com.gig.collide.content.domain.service.ContentDomainService;
import com.gig.collide.content.domain.service.ContentInfoExtensionService;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
    private final ContentInfoExtensionService contentInfoExtensionService;

    @Override
    public ContentResponse createContent(ContentCreateRequest createRequest) {
        try {
            log.info("创建内容请求，作者：{}，标题：{}", 
                createRequest.getAuthorId(), createRequest.getTitle());

            // 转换请求为领域实体
            Content content = convertToContent(createRequest);

            // 调用领域服务创建内容
            Content createdContent = contentDomainService.createContent(content);

            // 构建响应
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
            response.setResponseMessage("内容创建失败：" + e.getMessage());
            return response;
        }
    }

    @Override
    public ContentResponse updateContent(ContentUpdateRequest updateRequest) {
        try {
            log.info("更新内容请求，内容ID：{}，操作者：{}", 
                updateRequest.getContentId(), updateRequest.getOperatorId());

            // 转换请求为领域实体
            Content content = convertToContent(updateRequest);

            // 调用领域服务更新内容
            Content updatedContent = contentDomainService.updateContent(content);

            // 构建响应
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
            response.setResponseMessage("内容更新失败：" + e.getMessage());
            return response;
        }
    }

    @Override
    public ContentResponse deleteContent(ContentDeleteRequest deleteRequest) {
        try {
            log.info("删除内容请求，内容ID：{}，操作者ID：{}", 
                deleteRequest.getContentId(), deleteRequest.getOperatorId());

            // 调用领域服务删除内容
            contentDomainService.deleteContent(
                deleteRequest.getContentId(), 
                deleteRequest.getOperatorId()
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
            response.setResponseMessage("内容删除失败：" + e.getMessage());
            return response;
        }
    }

    @Override
    public ContentResponse publishContent(ContentPublishRequest publishRequest) {
        try {
            log.info("发布内容请求，内容ID：{}，操作者：{}", 
                publishRequest.getContentId(), publishRequest.getOperatorId());

            // 调用领域服务发布内容
            contentDomainService.publishContent(
                publishRequest.getContentId(), 
                publishRequest.getOperatorId()
            );

            // 构建成功响应
            ContentResponse response = new ContentResponse();
            response.setSuccess(true);
            response.setResponseMessage("内容发布成功");
            response.setContentId(publishRequest.getContentId());

            log.info("内容发布成功，内容ID：{}", publishRequest.getContentId());
            return response;

        } catch (Exception e) {
            log.error("内容发布失败", e);
            ContentResponse response = new ContentResponse();
            response.setSuccess(false);
            response.setResponseCode("CONTENT_PUBLISH_ERROR");
            response.setResponseMessage("内容发布失败：" + e.getMessage());
            return response;
        }
    }

    @Override
    public ContentQueryResponse<ContentInfo> queryContent(ContentQueryRequest queryRequest) {
        try {
            log.info("查询内容详情，内容ID：{}", queryRequest.getContentId());

            Content content;
            
            // 如果需要查看内容（增加访问量）
            if (Boolean.TRUE.equals(queryRequest.getViewContent())) {
                content = contentDomainService.viewContent(queryRequest.getContentId());
            } else {
                content = contentDomainService.getContentById(queryRequest.getContentId());
            }

            if (content == null) {
                return ContentQueryResponse.success(null);
            }

            // 转换为DTO
            ContentInfo contentInfo = ContentConvertor.INSTANCE.toContentInfo(content);

            // 设置作者信息、分类名称等扩展信息
            contentInfoExtensionService.setExtensionInfo(contentInfo, content, queryRequest.getUserId());

            return ContentQueryResponse.success(contentInfo);

        } catch (Exception e) {
            log.error("查询内容详情失败", e);
            return ContentQueryResponse.error("CONTENT_QUERY_ERROR", "内容查询失败：" + e.getMessage());
        }
    }

    @Override
    public PageResponse<ContentInfo> pageQueryContents(ContentQueryRequest queryRequest) {
        try {
            log.info("分页查询内容列表，查询类型：{}，页码：{}", 
                queryRequest.getQueryType(), queryRequest.getCurrentPage());

            IPage<Content> contentPage;

            // 根据查询类型获取内容
            QueryType queryType = queryRequest.getQueryType() != null ? queryRequest.getQueryType() : QueryType.LATEST;

            switch (queryType) {
                case RECOMMENDED:
                    contentPage = contentDomainService.getRecommendedContents(
                        queryRequest.getContentType(),
                        queryRequest.getCurrentPage(),
                        queryRequest.getPageSize()
                    );
                    break;
                    
                case HOT:
                    int hotDays = queryRequest.getHotDays() != null ? queryRequest.getHotDays() : 7;
                    contentPage = contentDomainService.getHotContents(
                        queryRequest.getContentType(),
                        hotDays,
                        queryRequest.getCurrentPage(),
                        queryRequest.getPageSize()
                    );
                    break;
                    
                case SEARCH:
                    contentPage = contentDomainService.searchContents(
                        queryRequest.getKeyword(),
                        queryRequest.getCurrentPage(),
                        queryRequest.getPageSize()
                    );
                    break;
                    
                case LATEST:
                default:
                    contentPage = contentDomainService.getLatestContents(
                        queryRequest.getContentType(),
                        queryRequest.getCurrentPage(),
                        queryRequest.getPageSize()
                    );
                    break;
            }

            // 转换为DTO
            List<ContentInfo> contentInfoList = ContentConvertor.INSTANCE
                .toContentInfoList(contentPage.getRecords());

            // 批量设置作者信息、分类名称和用户相关状态
            contentInfoExtensionService.batchSetExtensionInfo(
                contentInfoList, 
                contentPage.getRecords(), 
                queryRequest.getUserId()
            );

            return PageResponse.of(
                contentInfoList,
                (int) contentPage.getTotal(),
                (int) contentPage.getSize(),
                (int) contentPage.getCurrent()
            );

        } catch (Exception e) {
            log.error("内容分页查询失败", e);
            return PageResponse.error("CONTENT_PAGE_QUERY_ERROR", "内容查询失败: " + e.getMessage());
        }
    }

    @Override
    public PageResponse<ContentInfo> queryUserContents(ContentQueryRequest queryRequest) {
        try {
            log.info("查询用户内容列表，作者ID：{}", queryRequest.getAuthorId());

            IPage<Content> contentPage = contentDomainService.getUserContents(
                queryRequest.getAuthorId(),
                queryRequest.getStatus(),
                queryRequest.getCurrentPage(),
                queryRequest.getPageSize()
            );

            // 转换为DTO
            List<ContentInfo> contentInfoList = ContentConvertor.INSTANCE
                .toContentInfoList(contentPage.getRecords());

            // 批量设置扩展信息
            contentInfoExtensionService.batchSetExtensionInfo(
                contentInfoList, 
                contentPage.getRecords(), 
                queryRequest.getUserId()
            );

            return PageResponse.of(
                contentInfoList,
                (int) contentPage.getTotal(),
                (int) contentPage.getSize(),
                (int) contentPage.getCurrent()
            );

        } catch (Exception e) {
            log.error("用户内容查询失败", e);
            return PageResponse.error("USER_CONTENT_QUERY_ERROR", "用户内容查询失败: " + e.getMessage());
        }
    }

    @Override
    public ContentResponse likeContent(ContentLikeRequest likeRequest) {
        try {
            log.info("点赞内容，内容ID：{}，用户ID：{}", 
                likeRequest.getContentId(), likeRequest.getUserId());

            boolean success = contentDomainService.likeContent(
                likeRequest.getContentId(),
                likeRequest.getUserId()
            );

            ContentResponse response = new ContentResponse();
            response.setSuccess(success);
            response.setResponseMessage(success ? "点赞成功" : "点赞失败");
            response.setContentId(likeRequest.getContentId());

            return response;

        } catch (Exception e) {
            log.error("点赞内容失败", e);
            ContentResponse response = new ContentResponse();
            response.setSuccess(false);
            response.setResponseCode("CONTENT_LIKE_ERROR");
            response.setResponseMessage("点赞失败：" + e.getMessage());
            return response;
        }
    }

    @Override
    public ContentResponse favoriteContent(ContentFavoriteRequest favoriteRequest) {
        try {
            log.info("收藏内容，内容ID：{}，用户ID：{}", 
                favoriteRequest.getContentId(), favoriteRequest.getUserId());

            boolean success = contentDomainService.favoriteContent(
                favoriteRequest.getContentId(),
                favoriteRequest.getUserId()
            );

            ContentResponse response = new ContentResponse();
            response.setSuccess(success);
            response.setResponseMessage(success ? "收藏成功" : "收藏失败");
            response.setContentId(favoriteRequest.getContentId());

            return response;

        } catch (Exception e) {
            log.error("收藏内容失败", e);
            ContentResponse response = new ContentResponse();
            response.setSuccess(false);
            response.setResponseCode("CONTENT_FAVORITE_ERROR");
            response.setResponseMessage("收藏失败：" + e.getMessage());
            return response;
        }
    }

    @Override
    public ContentResponse shareContent(ContentShareRequest shareRequest) {
        try {
            log.info("分享内容，内容ID：{}，用户ID：{}", 
                shareRequest.getContentId(), shareRequest.getUserId());

            boolean success = contentDomainService.shareContent(
                shareRequest.getContentId(),
                shareRequest.getUserId(),
                shareRequest.getPlatform(),
                shareRequest.getShareText()
            );

            ContentResponse response = new ContentResponse();
            response.setSuccess(success);
            response.setResponseMessage(success ? "分享成功" : "分享失败");
            response.setContentId(shareRequest.getContentId());

            return response;

        } catch (Exception e) {
            log.error("分享内容失败", e);
            ContentResponse response = new ContentResponse();
            response.setSuccess(false);
            response.setResponseCode("CONTENT_SHARE_ERROR");
            response.setResponseMessage("分享失败：" + e.getMessage());
            return response;
        }
    }

    @Override
    public ContentQueryResponse<ContentStatistics> getContentStatistics(Long contentId) {
        try {
            log.info("获取内容统计，内容ID：{}", contentId);

            Content content = contentDomainService.getContentById(contentId);
            if (content == null) {
                return ContentQueryResponse.success(null);
            }

            // 转换为统计DTO
            ContentStatistics statistics = ContentConvertor.INSTANCE.toContentStatistics(content);

            return ContentQueryResponse.success(statistics);

        } catch (Exception e) {
            log.error("获取内容统计失败", e);
            return ContentQueryResponse.error("CONTENT_STATISTICS_ERROR", "获取内容统计失败：" + e.getMessage());
        }
    }

    // ========== 私有方法 ==========

    /**
     * 转换创建请求为领域实体
     */
    private Content convertToContent(ContentCreateRequest request) {
        Content content = new Content();
        content.setTitle(request.getTitle());
        content.setDescription(request.getDescription());
        content.setContentType(request.getContentType());
        
        // 处理contentData的Map到JSONObject转换
        if (request.getContentData() != null) {
            JSONObject contentDataJson = new JSONObject(request.getContentData());
            content.setContentDataJson(contentDataJson);
        }
        
        content.setCoverUrl(request.getCoverUrl());
        content.setAuthorId(request.getAuthorId());
        content.setCategoryId(request.getCategoryId());
        content.setTagList(request.getTags());
        content.setAllowComment(request.getAllowComment());
        content.setAllowShare(request.getAllowShare());

        return content;
    }

    /**
     * 转换更新请求为领域实体
     */
    private Content convertToContent(ContentUpdateRequest request) {
        Content content = new Content();
        content.setId(request.getContentId());
        content.setAuthorId(request.getOperatorId()); // 使用operatorId作为authorId进行权限校验
        content.setTitle(request.getTitle());
        content.setDescription(request.getDescription());
        
        // 处理contentData的Map到JSONObject转换
        if (request.getContentData() != null) {
            JSONObject contentDataJson = new JSONObject(request.getContentData());
            content.setContentDataJson(contentDataJson);
        }
        
        content.setCoverUrl(request.getCoverUrl());
        content.setCategoryId(request.getCategoryId());
        content.setTagList(request.getTags());
        content.setAllowComment(request.getAllowComment());
        content.setAllowShare(request.getAllowShare());

        return content;
    }
} 