package com.gig.collide.api.content;

import com.gig.collide.api.content.request.ContentCreateRequest;
import com.gig.collide.api.content.request.ContentUpdateRequest;
import com.gig.collide.api.content.request.ContentQueryRequest;
import com.gig.collide.api.content.request.ChapterCreateRequest;
import com.gig.collide.api.content.response.ContentResponse;
import com.gig.collide.api.content.response.ChapterResponse;
import com.gig.collide.web.vo.Result;
import com.gig.collide.base.response.PageResponse;

/**
 * 内容门面服务接口 - 简洁版
 * 基于content-simple.sql的双表设计，实现核心内容管理功能
 * 支持多种内容类型：NOVEL、COMIC、VIDEO、ARTICLE、AUDIO
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
public interface ContentFacadeService {
    
    // =================== 内容管理 ===================
    
    /**
     * 创建内容
     * 支持作者和分类信息冗余存储
     * 
     * @param request 创建请求
     * @return 创建结果（成功/失败状态）
     */
    Result<Void> createContent(ContentCreateRequest request);
    
    /**
     * 更新内容
     * 支持内容信息、状态、统计数据等更新
     * 
     * @param request 更新请求
     * @return 更新结果
     */
    Result<ContentResponse> updateContent(ContentUpdateRequest request);
    
    /**
     * 删除内容
     * 逻辑删除，将状态更新为OFFLINE
     * 
     * @param contentId 内容ID
     * @param operatorId 操作人ID
     * @return 删除结果
     */
    Result<Void> deleteContent(Long contentId, Long operatorId);
    
    /**
     * 根据ID获取内容详情
     * 
     * @param contentId 内容ID
     * @param includeOffline 是否包含下线内容
     * @return 内容详情
     */
    Result<ContentResponse> getContentById(Long contentId, Boolean includeOffline);
    
    /**
     * 分页查询内容
     * 支持按类型、作者、分类、状态等条件查询
     * 
     * @param request 查询请求
     * @return 内容列表
     */
    Result<PageResponse<ContentResponse>> queryContents(ContentQueryRequest request);
    
    /**
     * 发布内容
     * 将状态从DRAFT更新为PUBLISHED
     * 
     * @param contentId 内容ID
     * @param authorId 作者ID
     * @return 发布结果
     */
    Result<ContentResponse> publishContent(Long contentId, Long authorId);
    
    /**
     * 下线内容
     * 将状态更新为OFFLINE
     * 
     * @param contentId 内容ID
     * @param operatorId 操作人ID
     * @return 下线结果
     */
    Result<Void> offlineContent(Long contentId, Long operatorId);
    
    // =================== 章节管理 ===================
    
    /**
     * 创建章节
     * 用于小说、漫画等多章节内容
     * 
     * @param request 章节创建请求
     * @return 创建结果（成功/失败状态）
     */
    Result<Void> createChapter(ChapterCreateRequest request);
    
    /**
     * 获取内容的章节列表
     * 
     * @param contentId 内容ID
     * @param status 章节状态（可选）
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @return 章节列表
     */
    Result<PageResponse<ChapterResponse>> getContentChapters(Long contentId, String status, 
                                                           Integer pageNum, Integer pageSize);
    
    /**
     * 获取章节详情
     * 
     * @param chapterId 章节ID
     * @return 章节详情
     */
    Result<ChapterResponse> getChapterById(Long chapterId);
    
    /**
     * 发布章节
     * 
     * @param chapterId 章节ID
     * @param authorId 作者ID
     * @return 发布结果
     */
    Result<ChapterResponse> publishChapter(Long chapterId, Long authorId);
    
    // =================== 统计管理 ===================
    
    /**
     * 增加浏览量
     * 
     * @param contentId 内容ID
     * @param increment 增加数量
     * @return 更新后的浏览量
     */
    Result<Long> increaseViewCount(Long contentId, Integer increment);
    
    /**
     * 增加点赞数
     * 
     * @param contentId 内容ID
     * @param increment 增加数量
     * @return 更新后的点赞数
     */
    Result<Long> increaseLikeCount(Long contentId, Integer increment);
    
    /**
     * 增加评论数
     * 
     * @param contentId 内容ID
     * @param increment 增加数量
     * @return 更新后的评论数
     */
    Result<Long> increaseCommentCount(Long contentId, Integer increment);
    
    /**
     * 增加收藏数
     * 
     * @param contentId 内容ID
     * @param increment 增加数量
     * @return 更新后的收藏数
     */
    Result<Long> increaseFavoriteCount(Long contentId, Integer increment);
    
    /**
     * 更新评分
     * 新增评分时调用，更新评分总数和评分数量
     * 
     * @param contentId 内容ID
     * @param score 评分值（1-10）
     * @return 更新后的平均评分
     */
    Result<Double> updateScore(Long contentId, Integer score);
    
    /**
     * 获取内容统计信息
     * 
     * @param contentId 内容ID
     * @return 统计信息Map
     */
    Result<java.util.Map<String, Object>> getContentStatistics(Long contentId);
    
    // =================== 内容查询 ===================
    
    /**
     * 根据作者查询内容
     * 
     * @param authorId 作者ID
     * @param contentType 内容类型（可选）
     * @param status 状态（可选）
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @return 内容列表
     */
    Result<PageResponse<ContentResponse>> getContentsByAuthor(Long authorId, String contentType, 
                                                            String status, Integer pageNum, Integer pageSize);
    
    /**
     * 根据分类查询内容
     * 
     * @param categoryId 分类ID
     * @param contentType 内容类型（可选）
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @return 内容列表
     */
    Result<PageResponse<ContentResponse>> getContentsByCategory(Long categoryId, String contentType,
                                                              Integer pageNum, Integer pageSize);
    
    /**
     * 搜索内容
     * 根据标题、描述、标签进行搜索
     * 
     * @param keyword 搜索关键词
     * @param contentType 内容类型（可选）
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @return 搜索结果
     */
    Result<PageResponse<ContentResponse>> searchContents(String keyword, String contentType,
                                                       Integer pageNum, Integer pageSize);
    
    /**
     * 获取热门内容
     * 根据浏览量、点赞数等综合排序
     * 
     * @param contentType 内容类型（可选）
     * @param timeRange 时间范围（天）
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @return 热门内容列表
     */
    Result<PageResponse<ContentResponse>> getPopularContents(String contentType, Integer timeRange,
                                                           Integer pageNum, Integer pageSize);
    
    /**
     * 获取最新内容
     * 按发布时间排序
     * 
     * @param contentType 内容类型（可选）
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @return 最新内容列表
     */
    Result<PageResponse<ContentResponse>> getLatestContents(String contentType, Integer pageNum, Integer pageSize);
    
    // =================== 数据同步 ===================
    
    /**
     * 更新作者信息（冗余字段）
     * 当作者信息变更时，同步更新内容表中的冗余信息
     * 
     * @param authorId 作者ID
     * @param nickname 新昵称
     * @param avatar 新头像
     * @return 更新成功的记录数
     */
    Result<Integer> updateAuthorInfo(Long authorId, String nickname, String avatar);
    
    /**
     * 更新分类信息（冗余字段）
     * 当分类信息变更时，同步更新内容表中的冗余信息
     * 
     * @param categoryId 分类ID
     * @param categoryName 新分类名称
     * @return 更新成功的记录数
     */
    Result<Integer> updateCategoryInfo(Long categoryId, String categoryName);
    
    /**
     * 内容审核
     * 更新审核状态
     * 
     * @param contentId 内容ID
     * @param reviewStatus 审核状态：APPROVED、REJECTED
     * @param reviewerId 审核人ID
     * @param reviewComment 审核意见
     * @return 审核结果
     */
    Result<ContentResponse> reviewContent(Long contentId, String reviewStatus, 
                                        Long reviewerId, String reviewComment);
} 