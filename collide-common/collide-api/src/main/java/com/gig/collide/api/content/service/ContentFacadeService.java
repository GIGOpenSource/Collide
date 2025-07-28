package com.gig.collide.api.content.service;

import com.gig.collide.api.content.request.ContentUnifiedQueryRequest;
import com.gig.collide.api.content.request.ContentUnifiedCreateRequest;
import com.gig.collide.api.content.request.ContentUnifiedModifyRequest;
import com.gig.collide.api.content.request.ContentInteractionRequest;
import com.gig.collide.api.content.response.ContentUnifiedQueryResponse;
import com.gig.collide.api.content.response.ContentUnifiedOperateResponse;
import com.gig.collide.api.content.response.data.ContentUnifiedInfo;
import com.gig.collide.api.content.response.data.ContentStatisticsInfo;
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
     * @return 内容操作响应
     */
    ContentUnifiedOperateResponse createContent(ContentUnifiedCreateRequest createRequest);

    /**
     * 更新内容
     *
     * @param modifyRequest 修改请求
     * @return 内容操作响应
     */
    ContentUnifiedOperateResponse updateContent(ContentUnifiedModifyRequest modifyRequest);

    /**
     * 删除内容
     *
     * @param contentId 内容ID
     * @param operatorId 操作者ID
     * @param operatorName 操作者名称
     * @param deleteReason 删除原因
     * @return 内容操作响应
     */
    ContentUnifiedOperateResponse deleteContent(Long contentId, Long operatorId, String operatorName, String deleteReason);

    /**
     * 发布内容
     *
     * @param contentId 内容ID
     * @param operatorId 操作者ID
     * @param operatorName 操作者名称
     * @return 内容操作响应
     */
    ContentUnifiedOperateResponse publishContent(Long contentId, Long operatorId, String operatorName);

    /**
     * 查询内容详情
     *
     * @param queryRequest 查询请求
     * @return 内容查询响应
     */
    ContentUnifiedQueryResponse<ContentUnifiedInfo> queryContent(ContentUnifiedQueryRequest queryRequest);

    /**
     * 分页查询内容列表
     *
     * @param queryRequest 分页查询请求
     * @return 内容列表响应
     */
    PageResponse<ContentUnifiedInfo> pageQueryContents(ContentUnifiedQueryRequest queryRequest);

    /**
     * 查询用户的内容列表
     *
     * @param userId 用户ID
     * @param queryRequest 查询请求
     * @return 内容列表响应
     */
    PageResponse<ContentUnifiedInfo> queryUserContents(Long userId, ContentUnifiedQueryRequest queryRequest);

    /**
     * 获取内容统计信息
     *
     * @param contentId 内容ID
     * @return 内容统计响应
     */
    ContentUnifiedQueryResponse<ContentStatisticsInfo> getContentStatistics(Long contentId);

    /**
     * 点赞内容
     *
     * @param interactionRequest 交互请求
     * @return 内容操作响应
     */
    ContentUnifiedOperateResponse likeContent(ContentInteractionRequest interactionRequest);

    /**
     * 收藏内容
     *
     * @param interactionRequest 交互请求
     * @return 内容操作响应
     */
    ContentUnifiedOperateResponse favoriteContent(ContentInteractionRequest interactionRequest);

    /**
     * 分享内容
     *
     * @param interactionRequest 交互请求
     * @return 内容操作响应
     */
    ContentUnifiedOperateResponse shareContent(ContentInteractionRequest interactionRequest);
} 