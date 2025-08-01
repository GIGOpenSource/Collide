package com.gig.collide.content.domain.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.content.domain.entity.Content;

import java.util.List;
import java.util.Map;

/**
 * 内容业务逻辑接口 - 简洁版
 * 基于content-simple.sql的设计，实现核心内容管理业务
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
public interface ContentService {

    // =================== 基础CRUD ===================

    /**
     * 创建内容
     * 包含数据验证、冗余字段处理、状态初始化
     * 
     * @param content 内容对象
     * @return 创建的内容
     */
    Content createContent(Content content);

    /**
     * 更新内容
     * 支持部分字段更新，保护关键字段
     * 
     * @param content 内容对象
     * @return 更新后的内容
     */
    Content updateContent(Content content);

    /**
     * 删除内容
     * 逻辑删除，验证删除权限
     * 
     * @param contentId 内容ID
     * @param operatorId 操作人ID
     * @return 是否删除成功
     */
    boolean deleteContent(Long contentId, Long operatorId);

    /**
     * 根据ID获取内容
     * 
     * @param id 内容ID
     * @param includeOffline 是否包含下线内容
     * @return 内容对象
     */
    Content getContentById(Long id, Boolean includeOffline);

    /**
     * 分页查询内容
     * 
     * @param page 分页对象
     * @param title 标题关键词
     * @param contentType 内容类型
     * @param authorId 作者ID
     * @param categoryId 分类ID
     * @param status 状态
     * @param reviewStatus 审核状态
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 分页结果
     */
    Page<Content> queryContents(Page<Content> page, String title, String contentType,
                               Long authorId, Long categoryId, String status, String reviewStatus,
                               String orderBy, String orderDirection);

    // =================== 状态管理 ===================

    /**
     * 发布内容
     * 验证发布权限和条件
     * 
     * @param contentId 内容ID
     * @param authorId 作者ID
     * @return 发布后的内容
     */
    Content publishContent(Long contentId, Long authorId);

    /**
     * 下线内容
     * 
     * @param contentId 内容ID
     * @param operatorId 操作人ID
     * @return 是否下线成功
     */
    boolean offlineContent(Long contentId, Long operatorId);

    /**
     * 审核内容
     * 
     * @param contentId 内容ID
     * @param reviewStatus 审核状态
     * @param reviewerId 审核人ID
     * @param reviewComment 审核意见
     * @return 审核后的内容
     */
    Content reviewContent(Long contentId, String reviewStatus, Long reviewerId, String reviewComment);

    // =================== 查询功能 ===================

    /**
     * 根据作者查询内容
     * 
     * @param page 分页对象
     * @param authorId 作者ID
     * @param contentType 内容类型
     * @param status 状态
     * @return 分页结果
     */
    Page<Content> getContentsByAuthor(Page<Content> page, Long authorId, String contentType, String status);

    /**
     * 根据分类查询内容
     * 
     * @param page 分页对象
     * @param categoryId 分类ID
     * @param contentType 内容类型
     * @return 分页结果
     */
    Page<Content> getContentsByCategory(Page<Content> page, Long categoryId, String contentType);

    /**
     * 搜索内容
     * 
     * @param page 分页对象
     * @param keyword 搜索关键词
     * @param contentType 内容类型
     * @return 搜索结果
     */
    Page<Content> searchContents(Page<Content> page, String keyword, String contentType);

    /**
     * 获取热门内容
     * 
     * @param page 分页对象
     * @param contentType 内容类型
     * @param timeRange 时间范围（天）
     * @return 热门内容
     */
    Page<Content> getPopularContents(Page<Content> page, String contentType, Integer timeRange);

    /**
     * 获取最新内容
     * 
     * @param page 分页对象
     * @param contentType 内容类型
     * @return 最新内容
     */
    Page<Content> getLatestContents(Page<Content> page, String contentType);

    /**
     * 根据评分查询内容
     * 
     * @param page 分页对象
     * @param minScore 最低评分
     * @param contentType 内容类型
     * @return 高评分内容
     */
    Page<Content> getContentsByScore(Page<Content> page, Double minScore, String contentType);

    // =================== 统计功能 ===================

    /**
     * 增加浏览量
     * 
     * @param contentId 内容ID
     * @param increment 增加数量
     * @return 更新后的浏览量
     */
    Long increaseViewCount(Long contentId, Integer increment);

    /**
     * 增加点赞数
     * 
     * @param contentId 内容ID
     * @param increment 增加数量
     * @return 更新后的点赞数
     */
    Long increaseLikeCount(Long contentId, Integer increment);

    /**
     * 增加评论数
     * 
     * @param contentId 内容ID
     * @param increment 增加数量
     * @return 更新后的评论数
     */
    Long increaseCommentCount(Long contentId, Integer increment);

    /**
     * 增加收藏数
     * 
     * @param contentId 内容ID
     * @param increment 增加数量
     * @return 更新后的收藏数
     */
    Long increaseFavoriteCount(Long contentId, Integer increment);

    /**
     * 更新评分
     * 
     * @param contentId 内容ID
     * @param score 评分值（1-10）
     * @return 更新后的平均评分
     */
    Double updateScore(Long contentId, Integer score);

    /**
     * 获取内容统计信息
     * 
     * @param contentId 内容ID
     * @return 统计信息
     */
    Map<String, Object> getContentStatistics(Long contentId);

    // =================== 数据同步 ===================

    /**
     * 更新作者信息（冗余字段）
     * 
     * @param authorId 作者ID
     * @param nickname 新昵称
     * @param avatar 新头像
     * @return 更新成功的记录数
     */
    Integer updateAuthorInfo(Long authorId, String nickname, String avatar);

    /**
     * 更新分类信息（冗余字段）
     * 
     * @param categoryId 分类ID
     * @param categoryName 新分类名称
     * @return 更新成功的记录数
     */
    Integer updateCategoryInfo(Long categoryId, String categoryName);

    // =================== 业务验证 ===================

    /**
     * 验证内容数据
     * 
     * @param content 内容对象
     * @return 是否有效
     */
    boolean validateContent(Content content);

    /**
     * 检查发布权限
     * 
     * @param contentId 内容ID
     * @param authorId 作者ID
     * @return 是否有权限
     */
    boolean canPublish(Long contentId, Long authorId);

    /**
     * 检查编辑权限
     * 
     * @param contentId 内容ID
     * @param userId 用户ID
     * @return 是否有权限
     */
    boolean canEdit(Long contentId, Long userId);

    /**
     * 检查删除权限
     * 
     * @param contentId 内容ID
     * @param userId 用户ID
     * @return 是否有权限
     */
    boolean canDelete(Long contentId, Long userId);

    // =================== 高级功能 ===================

    /**
     * 获取推荐内容
     * 
     * @param page 分页对象
     * @param contentType 内容类型
     * @param excludeAuthorId 排除的作者ID
     * @return 推荐内容
     */
    Page<Content> getRecommendedContents(Page<Content> page, String contentType, Long excludeAuthorId);

    /**
     * 获取相似内容
     * 
     * @param contentId 内容ID
     * @param limit 数量限制
     * @return 相似内容列表
     */
    List<Content> getSimilarContents(Long contentId, Integer limit);

    /**
     * 获取需要章节管理的内容
     * 
     * @param authorId 作者ID
     * @return 内容列表
     */
    List<Content> getNeedsChapterManagement(Long authorId);

    /**
     * 统计作者内容数量
     * 
     * @param authorId 作者ID
     * @param status 状态
     * @return 内容数量
     */
    Long countByAuthor(Long authorId, String status);

    /**
     * 统计分类内容数量
     * 
     * @param categoryId 分类ID
     * @param status 状态
     * @return 内容数量
     */
    Long countByCategory(Long categoryId, String status);

    /**
     * 获取内容类型统计
     * 
     * @return 统计信息列表
     */
    List<Map<String, Object>> getContentTypeStats();
}