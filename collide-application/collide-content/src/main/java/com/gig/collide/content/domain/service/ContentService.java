package com.gig.collide.content.domain.service;

import com.gig.collide.content.domain.entity.Content;

import java.util.List;

/**
 * 内容服务接口
 * 极简版 - 12个核心方法，使用通用查询
 * 
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
public interface ContentService {

    // =================== 核心CRUD功能（4个方法）===================

    /**
     * 创建内容
     */
    Content createContent(Content content);

    /**
     * 更新内容
     */
    Content updateContent(Content content);

    /**
     * 根据ID获取内容
     */
    Content getContentById(Long id, Boolean includeOffline);

    /**
     * 软删除内容
     */
    boolean deleteContent(Long contentId, Long operatorId);

    // =================== 万能查询功能（3个方法）===================

    /**
     * 万能条件查询内容列表 - 替代所有具体查询
     * 可实现：getContentsByAuthor, getContentsByCategory, getPublishedContents等
     * 
     * @param authorId 作者ID
     * @param categoryId 分类ID  
     * @param contentType 内容类型
     * @param status 内容状态
     * @param reviewStatus 审核状态
     * @param minScore 最小评分
     * @param timeRange 时间范围天数（用于热门内容筛选）
     * @param orderBy 排序字段（支持：createTime、updateTime、viewCount、likeCount、favoriteCount、shareCount、commentCount、score）
     * @param orderDirection 排序方向（ASC、DESC）
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 内容列表
     */
    List<Content> getContentsByConditions(Long authorId, Long categoryId, String contentType,
                                         String status, String reviewStatus, Double minScore,
                                         Integer timeRange, String orderBy, String orderDirection,
                                         Integer currentPage, Integer pageSize);

    /**
     * 搜索内容
     */
    List<Content> searchContents(String keyword, String contentType, Long categoryId,
                                Integer currentPage, Integer pageSize);

    /**
     * 获取推荐内容
     */
    List<Content> getRecommendedContents(Long userId, List<Long> excludeContentIds, Integer limit);

    // =================== 状态管理功能（3个方法）===================

    /**
     * 更新内容状态（发布、下线等）
     */
    boolean updateContentStatus(Long contentId, String status);

    /**
     * 更新审核状态
     */
    boolean updateReviewStatus(Long contentId, String reviewStatus);

    /**
     * 批量更新状态
     */
    boolean batchUpdateStatus(List<Long> ids, String status);

    // =================== 统计管理功能（2个方法）===================

    /**
     * 更新内容统计信息
     */
    boolean updateContentStats(Long contentId, Long viewCount, Long likeCount, 
                              Long commentCount, Long favoriteCount);

    /**
     * 增加浏览量（最常用的统计操作）
     */
    Long increaseViewCount(Long contentId, Integer increment);
}