package com.gig.collide.api.content.service;

import com.gig.collide.api.content.request.*;
import com.gig.collide.api.content.response.ContentOperatorResponse;
import com.gig.collide.api.content.response.ContentQueryResponse;

/**
 * 内容门面服务
 */
public interface ContentFacadeService {

    /**
     * 创建内容
     *
     * @param request 创建请求
     * @return 操作响应
     */
    ContentOperatorResponse createContent(ContentCreateRequest request);

    /**
     * 更新内容
     *
     * @param request 更新请求
     * @return 操作响应
     */
    ContentOperatorResponse updateContent(ContentUpdateRequest request);

    /**
     * 删除内容
     *
     * @param contentId 内容ID
     * @param operatorUserId 操作者用户ID
     * @return 操作响应
     */
    ContentOperatorResponse deleteContent(Long contentId, Long operatorUserId);

    /**
     * 提交审核
     *
     * @param contentId 内容ID
     * @param operatorUserId 操作者用户ID
     * @return 操作响应
     */
    ContentOperatorResponse submitForReview(Long contentId, Long operatorUserId);

    /**
     * 审核内容
     *
     * @param request 审核请求
     * @param operatorUserId 操作者用户ID
     * @return 操作响应
     */
    ContentOperatorResponse reviewContent(ContentReviewRequest request, Long operatorUserId);

    /**
     * 发布内容
     *
     * @param contentId 内容ID
     * @param operatorUserId 操作者用户ID
     * @return 操作响应
     */
    ContentOperatorResponse publishContent(Long contentId, Long operatorUserId);

    /**
     * 下架内容
     *
     * @param contentId 内容ID
     * @param operatorUserId 操作者用户ID
     * @param reason 下架原因
     * @return 操作响应
     */
    ContentOperatorResponse unpublishContent(Long contentId, Long operatorUserId, String reason);

    /**
     * 根据ID查询内容
     *
     * @param contentId 内容ID
     * @return 查询响应
     */
    ContentQueryResponse queryContentById(Long contentId);

    /**
     * 查询内容列表
     *
     * @param request 查询请求
     * @return 查询响应
     */
    ContentQueryResponse queryContentList(ContentQueryRequest request);

    /**
     * 分页查询内容
     *
     * @param request 分页查询请求
     * @return 查询响应
     */
    ContentQueryResponse queryContentPage(ContentPageQueryRequest request);

    /**
     * 查询用户内容
     *
     * @param userId 用户ID
     * @param request 分页查询请求
     * @return 查询响应
     */
    ContentQueryResponse queryUserContent(Long userId, ContentPageQueryRequest request);

    /**
     * 查询待审核内容
     *
     * @param request 分页查询请求
     * @return 查询响应
     */
    ContentQueryResponse queryPendingReviewContent(ContentPageQueryRequest request);

    /**
     * 查询内容审核记录
     *
     * @param contentId 内容ID
     * @return 查询响应
     */
    ContentQueryResponse queryContentReviewHistory(Long contentId);

    /**
     * 查询内容统计
     *
     * @param contentId 内容ID
     * @return 查询响应
     */
    ContentQueryResponse queryContentStatistics(Long contentId);

    /**
     * 点赞内容
     *
     * @param contentId 内容ID
     * @param userId 用户ID
     * @return 操作响应
     */
    ContentOperatorResponse likeContent(Long contentId, Long userId);

    /**
     * 取消点赞
     *
     * @param contentId 内容ID
     * @param userId 用户ID
     * @return 操作响应
     */
    ContentOperatorResponse unlikeContent(Long contentId, Long userId);

    /**
     * 收藏内容
     *
     * @param contentId 内容ID
     * @param userId 用户ID
     * @return 操作响应
     */
    ContentOperatorResponse collectContent(Long contentId, Long userId);

    /**
     * 取消收藏
     *
     * @param contentId 内容ID
     * @param userId 用户ID
     * @return 操作响应
     */
    ContentOperatorResponse uncollectContent(Long contentId, Long userId);

    /**
     * 查看内容（记录浏览）
     *
     * @param contentId 内容ID
     * @param userId 用户ID
     * @return 操作响应
     */
    ContentOperatorResponse viewContent(Long contentId, Long userId);

    /**
     * 分享内容
     *
     * @param contentId 内容ID
     * @param userId 用户ID
     * @return 操作响应
     */
    ContentOperatorResponse shareContent(Long contentId, Long userId);
} 