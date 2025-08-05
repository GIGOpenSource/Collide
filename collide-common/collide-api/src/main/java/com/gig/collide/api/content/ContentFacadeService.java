package com.gig.collide.api.content;

import com.gig.collide.api.content.request.ContentCreateRequest;
import com.gig.collide.api.content.request.ContentUpdateRequest;
import com.gig.collide.api.content.response.ContentResponse;
import com.gig.collide.web.vo.Result;
import com.gig.collide.base.response.PageResponse;

import java.util.List;

/**
 * 内容门面服务接口 - 极简版
 * 基于万能查询的12个核心方法设计
 * 支持多种内容类型：NOVEL、COMIC、VIDEO、ARTICLE、AUDIO
 * 
 * @author Collide
 * @version 2.0.0 (极简版)
 * @since 2024-01-01
 */
public interface ContentFacadeService {
    
    // =================== 核心CRUD功能（4个方法）===================
    
    /**
     * 创建内容
     */
    Result<Void> createContent(ContentCreateRequest request);
    
    /**
     * 更新内容
     */
    Result<ContentResponse> updateContent(ContentUpdateRequest request);
    
    /**
     * 根据ID获取内容详情
     */
    Result<ContentResponse> getContentById(Long contentId, Boolean includeOffline);
    
    /**
     * 删除内容（逻辑删除）
     */
    Result<Void> deleteContent(Long contentId, Long operatorId);
    
    // =================== 万能查询功能（3个方法）===================
    
    /**
     * 万能条件查询内容列表 - 替代所有具体查询
     * 可实现：getContentsByAuthor, getContentsByCategory, getPopularContents, getLatestContents等
     * 
     * @param authorId 作者ID（可选）
     * @param categoryId 分类ID（可选）
     * @param contentType 内容类型（可选）
     * @param status 状态（可选）
     * @param reviewStatus 审核状态（可选）
     * @param minScore 最小评分（可选）
     * @param timeRange 时间范围天数（可选，用于热门内容）
     * @param orderBy 排序字段（可选：createTime、updateTime、viewCount、likeCount、favoriteCount、shareCount、commentCount、score）
     * @param orderDirection 排序方向（可选：ASC、DESC）
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 内容列表
     */
    Result<PageResponse<ContentResponse>> queryContentsByConditions(Long authorId, Long categoryId, String contentType,
                                                                   String status, String reviewStatus, Double minScore,
                                                                   Integer timeRange, String orderBy, String orderDirection,
                                                                   Integer currentPage, Integer pageSize);
    
    /**
     * 搜索内容 - 根据标题、描述、标签进行搜索
     */
    Result<PageResponse<ContentResponse>> searchContents(String keyword, String contentType,
                                                       Integer currentPage, Integer pageSize);
    
    /**
     * 获取推荐内容 - 基于用户行为和内容特征
     */
    Result<List<ContentResponse>> getRecommendedContents(Long userId, List<Long> excludeContentIds, Integer limit);
    
    // =================== 状态管理功能（2个方法）===================
    
    /**
     * 更新内容状态 - 统一状态管理
     * 可实现：publishContent, reviewContent, offlineContent等
     */
    Result<Boolean> updateContentStatus(Long contentId, String status, String reviewStatus, 
                                       Long operatorId, String comment);
    
    /**
     * 批量更新状态
     */
    Result<Boolean> batchUpdateStatus(List<Long> ids, String status);
    
    // =================== 统计管理功能（2个方法）===================
    
    /**
     * 更新内容统计信息 - 统一统计管理
     * 可实现：increaseViewCount, increaseLikeCount, increaseCommentCount, increaseFavoriteCount, updateScore等
     */
    Result<Boolean> updateContentStats(Long contentId, Long viewCount, Long likeCount, 
                                      Long commentCount, Long favoriteCount, Double score);
    
    /**
     * 增加浏览量（最常用的统计操作单独提供）
     */
    Result<Long> increaseViewCount(Long contentId, Integer increment);
    
    // =================== 数据同步功能（1个方法）===================
    
    /**
     * 同步外部数据 - 统一数据同步
     * 可实现：updateAuthorInfo, updateCategoryInfo等
     * 
     * @param syncType 同步类型（AUTHOR、CATEGORY）
     * @param targetId 目标ID（作者ID或分类ID）
     * @param syncData 同步数据Map
     */
    Result<Integer> syncExternalData(String syncType, Long targetId, java.util.Map<String, Object> syncData);
}