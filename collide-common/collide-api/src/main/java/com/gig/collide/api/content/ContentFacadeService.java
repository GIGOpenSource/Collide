package com.gig.collide.api.content;

import com.gig.collide.api.content.request.ContentCreateRequest;
import com.gig.collide.api.content.request.ContentQueryRequest;
import com.gig.collide.api.content.request.ContentUpdateRequest;
import com.gig.collide.api.content.response.ContentResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import java.util.List;

/**
 * 内容管理门面服务接口 - 简洁版
 * 基于简洁版SQL设计，保留核心功能
 *
 * @author GIG Team
 * @version 2.0.0
 */
public interface ContentFacadeService {

    /**
     * 创建内容
     */
    Result<ContentResponse> createContent(ContentCreateRequest request);

    /**
     * 更新内容
     */
    Result<ContentResponse> updateContent(ContentUpdateRequest request);

    /**
     * 删除内容
     */
    Result<Void> deleteContent(Long contentId);

    /**
     * 根据ID查询内容
     */
    Result<ContentResponse> getContentById(Long contentId);

    /**
     * 分页查询内容列表
     */
    Result<PageResponse<ContentResponse>> queryContents(ContentQueryRequest request);

    /**
     * 根据作者查询内容
     */
    Result<List<ContentResponse>> getContentsByAuthor(Long authorId, Integer limit);

    /**
     * 根据分类查询内容
     */
    Result<List<ContentResponse>> getContentsByCategory(Long categoryId, Integer limit);

    /**
     * 根据内容类型查询内容
     */
    Result<List<ContentResponse>> getContentsByType(String contentType, Integer limit);

    /**
     * 发布内容
     */
    Result<Void> publishContent(Long contentId);

    /**
     * 下线内容
     */
    Result<Void> offlineContent(Long contentId);

    /**
     * 更新内容统计数据
     */
    Result<Void> updateContentStats(Long contentId, String statsType, Integer increment);

    /**
     * 搜索内容（按标题模糊搜索）
     */
    Result<List<ContentResponse>> searchContents(String keyword, Integer limit);
} 