package com.gig.collide.api.content.service;

import com.gig.collide.api.content.request.*;
import com.gig.collide.api.content.response.*;
import com.gig.collide.api.content.response.data.ContentInfo;
import com.gig.collide.api.content.response.data.ContentStatistics;
import com.gig.collide.base.response.PageResponse;

/**
 * 内容服务 Facade 接口
 * 基于 t_content 表设计
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface ContentFacadeService {

    /**
     * 创建内容
     *
     * @param createRequest 创建请求
     * @return 内容响应
     */
    ContentResponse createContent(ContentCreateRequest createRequest);

    /**
     * 更新内容
     *
     * @param updateRequest 更新请求
     * @return 内容响应
     */
    ContentResponse updateContent(ContentUpdateRequest updateRequest);

    /**
     * 删除内容
     *
     * @param deleteRequest 删除请求
     * @return 内容响应
     */
    ContentResponse deleteContent(ContentDeleteRequest deleteRequest);

    /**
     * 发布内容
     *
     * @param publishRequest 发布请求
     * @return 内容响应
     */
    ContentResponse publishContent(ContentPublishRequest publishRequest);

    /**
     * 查询内容详情
     *
     * @param queryRequest 查询请求
     * @return 内容查询响应
     */
    ContentQueryResponse<ContentInfo> queryContent(ContentQueryRequest queryRequest);

    /**
     * 分页查询内容列表
     *
     * @param queryRequest 分页查询请求
     * @return 内容列表响应
     */
    PageResponse<ContentInfo> pageQueryContents(ContentQueryRequest queryRequest);

    /**
     * 查询用户的内容列表
     *
     * @param queryRequest 查询请求
     * @return 内容列表响应
     */
    PageResponse<ContentInfo> queryUserContents(ContentQueryRequest queryRequest);

    /**
     * 获取内容统计信息
     *
     * @param contentId 内容ID
     * @return 内容统计响应
     */
    ContentQueryResponse<ContentStatistics> getContentStatistics(Long contentId);

    /**
     * 点赞内容
     *
     * @param likeRequest 点赞请求
     * @return 内容响应
     */
    ContentResponse likeContent(ContentLikeRequest likeRequest);

    /**
     * 收藏内容
     *
     * @param favoriteRequest 收藏请求
     * @return 内容响应
     */
    ContentResponse favoriteContent(ContentFavoriteRequest favoriteRequest);

    /**
     * 分享内容
     *
     * @param shareRequest 分享请求
     * @return 内容响应
     */
    ContentResponse shareContent(ContentShareRequest shareRequest);
} 