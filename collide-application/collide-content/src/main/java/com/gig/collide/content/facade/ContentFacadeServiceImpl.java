package com.gig.collide.content.facade;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.content.ContentFacadeService;
import com.gig.collide.api.content.request.ContentCreateRequest;
import com.gig.collide.api.content.request.ContentUpdateRequest;
import com.gig.collide.api.content.request.ContentQueryRequest;
import com.gig.collide.api.content.request.ChapterCreateRequest;
import com.gig.collide.api.content.response.ContentResponse;
import com.gig.collide.api.content.response.ChapterResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import com.gig.collide.content.domain.entity.Content;
import com.gig.collide.content.domain.entity.ContentChapter;
import com.gig.collide.content.domain.service.ContentService;
import com.gig.collide.content.domain.service.ContentChapterService;
import com.gig.collide.api.user.UserFacadeService;
import com.gig.collide.api.category.CategoryFacadeService;
import com.gig.collide.api.like.LikeFacadeService;
import com.gig.collide.api.favorite.FavoriteFacadeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheUpdate;
import com.alicp.jetcache.anno.CacheType;
import com.gig.collide.content.infrastructure.cache.ContentCacheConstant;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

/**
 * 内容门面服务实现类 - 简洁版
 * 基于content-simple.sql的双表设计，处理API层和业务层转换
 * 包含评分功能、错误处理、数据转换
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class ContentFacadeServiceImpl implements ContentFacadeService {

    private final ContentService contentService;
    private final ContentChapterService contentChapterService;
    
    // =================== 跨模块服务注入 ===================
    @Autowired
    private UserFacadeService userFacadeService;
    
    @Autowired
    private CategoryFacadeService categoryFacadeService;
    
    @Autowired
    private LikeFacadeService likeFacadeService;
    
    @Autowired
    private FavoriteFacadeService favoriteFacadeService;

    // =================== 内容管理 ===================

    @Override
    @CacheInvalidate(name = ContentCacheConstant.CONTENT_LIST_CACHE)
    @CacheInvalidate(name = ContentCacheConstant.CONTENT_LATEST_CACHE)
    public Result<Void> createContent(ContentCreateRequest request) {
        try {
            log.info("创建内容请求: {}", request.getTitle());
            
            // =================== 跨模块验证 ===================
            
            // 1. 验证作者存在性
            try {
                var userResult = userFacadeService.getUserById(request.getAuthorId());
                if (!userResult.getSuccess()) {
                    log.warn("作者不存在: authorId={}", request.getAuthorId());
                    return Result.error("AUTHOR_NOT_FOUND", "作者不存在");
                }
                log.debug("作者验证通过: {}", userResult.getData().getNickname());
            } catch (Exception e) {
                log.warn("作者验证失败: authorId={}, error={}", request.getAuthorId(), e.getMessage());
                return Result.error("AUTHOR_VALIDATION_FAILED", "作者验证失败");
            }
            
            // 2. 验证分类存在性
            try {
                var categoryResult = categoryFacadeService.getCategoryById(request.getCategoryId(), false);
                if (!categoryResult.getSuccess()) {
                    log.warn("分类不存在: categoryId={}", request.getCategoryId());
                    return Result.error("CATEGORY_NOT_FOUND", "分类不存在");
                }
                log.debug("分类验证通过: {}", categoryResult.getData().getName());
            } catch (Exception e) {
                log.warn("分类验证失败: categoryId={}, error={}", request.getCategoryId(), e.getMessage());
                return Result.error("CATEGORY_VALIDATION_FAILED", "分类验证失败");
            }
            
            // =================== 创建内容 ===================
            
            // 请求参数转换为实体
            Content content = convertToEntity(request);
            
            // 调用业务服务
            Content createdContent = contentService.createContent(content);
            
            log.info("内容创建成功: ID={}", createdContent.getId());
            return Result.success(null);
            
        } catch (IllegalArgumentException e) {
            log.warn("创建内容参数错误: {}", e.getMessage());
            return Result.error("INVALID_PARAMETER", e.getMessage());
        } catch (Exception e) {
            log.error("创建内容失败", e);
            return Result.error("CONTENT_CREATE_FAILED", "创建内容失败: " + e.getMessage());
        }
    }

    @Override
    @CacheUpdate(name = ContentCacheConstant.CONTENT_DETAIL_CACHE, 
                 key = ContentCacheConstant.CONTENT_DETAIL_KEY,
                 value = "#result.data")
    @CacheInvalidate(name = ContentCacheConstant.CONTENT_LIST_CACHE)
    @CacheInvalidate(name = ContentCacheConstant.CONTENT_STATISTICS_CACHE)
    @CacheInvalidate(name = ContentCacheConstant.CONTENT_BY_AUTHOR_CACHE)
    @CacheInvalidate(name = ContentCacheConstant.CONTENT_BY_CATEGORY_CACHE)
    public Result<ContentResponse> updateContent(ContentUpdateRequest request) {
        try {
            log.info("更新内容请求: ID={}", request.getId());
            
            // 请求参数转换为实体
            Content content = convertToEntity(request);
            
            // 调用业务服务
            Content updatedContent = contentService.updateContent(content);
            
            // 实体转换为响应
            ContentResponse response = convertToResponse(updatedContent);
            
            log.info("内容更新成功: ID={}", response.getId());
            return Result.success(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("更新内容参数错误: {}", e.getMessage());
            return Result.error("INVALID_PARAMETER", e.getMessage());
        } catch (Exception e) {
            log.error("更新内容失败", e);
            return Result.error("CONTENT_UPDATE_FAILED", "更新内容失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = ContentCacheConstant.CONTENT_DETAIL_CACHE)
    @CacheInvalidate(name = ContentCacheConstant.CONTENT_LIST_CACHE)
    @CacheInvalidate(name = ContentCacheConstant.CONTENT_STATISTICS_CACHE)
    @CacheInvalidate(name = ContentCacheConstant.CONTENT_BY_AUTHOR_CACHE)
    @CacheInvalidate(name = ContentCacheConstant.CONTENT_BY_CATEGORY_CACHE)
    @CacheInvalidate(name = ContentCacheConstant.CHAPTER_LIST_CACHE)
    public Result<Void> deleteContent(Long contentId, Long operatorId) {
        try {
            log.info("删除内容请求: ID={}, 操作人={}", contentId, operatorId);
            
            boolean deleted = contentService.deleteContent(contentId, operatorId);
            
            if (deleted) {
                log.info("内容删除成功: ID={}", contentId);
                return Result.success(null);
            } else {
                return Result.error("CONTENT_NOT_FOUND", "内容删除失败，内容不存在或无权限");
            }
            
        } catch (IllegalArgumentException e) {
            log.warn("删除内容参数错误: {}", e.getMessage());
            return Result.error("INVALID_PARAMETER", e.getMessage());
        } catch (Exception e) {
            log.error("删除内容失败", e);
            return Result.error("CONTENT_DELETE_FAILED", "删除内容失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = ContentCacheConstant.CONTENT_DETAIL_CACHE,
            key = ContentCacheConstant.CONTENT_DETAIL_KEY,
            expire = ContentCacheConstant.CONTENT_DETAIL_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<ContentResponse> getContentById(Long contentId, Boolean includeOffline) {
        try {
            log.debug("获取内容详情: ID={}", contentId);
            
            Content content = contentService.getContentById(contentId, includeOffline);
            
            if (content == null) {
                return Result.error("CONTENT_NOT_FOUND", "内容不存在");
            }
            
            ContentResponse response = convertToResponse(content);
            
            // =================== 跨模块信息增强 ===================
            
            // 1. 获取作者详细信息
            try {
                var userResult = userFacadeService.getUserById(content.getAuthorId());
                if (userResult.getSuccess()) {
                    var user = userResult.getData();
                    response.setAuthorNickname(user.getNickname());
                    response.setAuthorAvatar(user.getAvatar());
                    log.debug("作者信息获取成功: {}", user.getNickname());
                }
            } catch (Exception e) {
                log.warn("获取作者信息失败: authorId={}, error={}", content.getAuthorId(), e.getMessage());
                // 不影响主流程，继续执行
            }
            
            // 2. 获取分类详细信息
            try {
                var categoryResult = categoryFacadeService.getCategoryById(content.getCategoryId(), false);
                if (categoryResult.getSuccess()) {
                    var category = categoryResult.getData();
                    response.setCategoryName(category.getName());
                    log.debug("分类信息获取成功: {}", category.getName());
                }
            } catch (Exception e) {
                log.warn("获取分类信息失败: categoryId={}, error={}", content.getCategoryId(), e.getMessage());
                // 不影响主流程，继续执行
            }
            
            // 3. 获取实时点赞数
            try {
                var likeCountResult = likeFacadeService.getLikeCount("CONTENT", contentId);
                if (likeCountResult.getSuccess()) {
                    response.setLikeCount(likeCountResult.getData());
                    log.debug("点赞数获取成功: {}", likeCountResult.getData());
                }
            } catch (Exception e) {
                log.warn("获取点赞数失败: contentId={}, error={}", contentId, e.getMessage());
                // 使用数据库中的点赞数作为备用
            }
            
            // 4. 获取实时收藏数
            try {
                var favoriteCountResult = favoriteFacadeService.getTargetFavoriteCount("CONTENT", contentId);
                if (favoriteCountResult.getSuccess()) {
                    response.setFavoriteCount(favoriteCountResult.getData());
                    log.debug("收藏数获取成功: {}", favoriteCountResult.getData());
                }
            } catch (Exception e) {
                log.warn("获取收藏数失败: contentId={}, error={}", contentId, e.getMessage());
                // 使用数据库中的收藏数作为备用
            }
            
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("获取内容详情失败", e);
            return Result.error("CONTENT_GET_FAILED", "获取内容详情失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = ContentCacheConstant.CONTENT_LIST_CACHE,
            key = ContentCacheConstant.CONTENT_LIST_KEY,
            expire = ContentCacheConstant.CONTENT_LIST_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<PageResponse<ContentResponse>> queryContents(ContentQueryRequest request) {
        try {
            log.debug("分页查询内容: {}", request.getKeyword());
            
            // 创建分页对象
            Page<Content> page = new Page<>(request.getCurrentPage(), request.getPageSize());
            
            // 调用业务服务
            Page<Content> contentPage = contentService.queryContents(
                page, request.getTitle(), request.getContentType(),
                request.getAuthorId(), request.getCategoryId(),
                request.getStatus(), request.getReviewStatus(),
                request.getOrderBy(), request.getOrderDirection()
            );
            
            // 转换响应
            PageResponse<ContentResponse> pageResponse = convertToPageResponse(contentPage);
            
            return Result.success(pageResponse);
            
        } catch (Exception e) {
            log.error("查询内容失败", e);
            return Result.error("","查询内容失败: " + e.getMessage());
        }
    }

    @Override
    public Result<ContentResponse> publishContent(Long contentId, Long authorId) {
        try {
            log.info("发布内容: ID={}, 作者={}", contentId, authorId);
            
            Content publishedContent = contentService.publishContent(contentId, authorId);
            ContentResponse response = convertToResponse(publishedContent);
            
            log.info("内容发布成功: ID={}", contentId);
            return Result.success(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("发布内容参数错误: {}", e.getMessage());
            return Result.error("",e.getMessage());
        } catch (Exception e) {
            log.error("发布内容失败", e);
            return Result.error("","发布内容失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> offlineContent(Long contentId, Long operatorId) {
        try {
            log.info("下线内容: ID={}, 操作人={}", contentId, operatorId);
            
            boolean offlined = contentService.offlineContent(contentId, operatorId);
            
            if (offlined) {
                log.info("内容下线成功: ID={}", contentId);
                return Result.success(null);
            } else {
                return Result.error("","内容下线失败");
            }
            
        } catch (Exception e) {
            log.error("下线内容失败", e);
            return Result.error("","下线内容失败: " + e.getMessage());
        }
    }

    // =================== 章节管理 ===================

    @Override
    @CacheInvalidate(name = ContentCacheConstant.CHAPTER_LIST_CACHE)
    public Result<Void> createChapter(ChapterCreateRequest request) {
        try {
            log.info("创建章节: 内容ID={}, 章节号={}", request.getContentId(), request.getChapterNum());
            
            // 请求转换为实体
            ContentChapter chapter = convertToEntity(request);
            
            // 调用业务服务
            ContentChapter createdChapter = contentChapterService.createChapter(chapter);
            
            log.info("章节创建成功: ID={}", createdChapter.getId());
            return Result.success(null);
            
        } catch (IllegalArgumentException e) {
            log.warn("创建章节参数错误: {}", e.getMessage());
            return Result.error("INVALID_PARAMETER", e.getMessage());
        } catch (Exception e) {
            log.error("创建章节失败", e);
            return Result.error("CHAPTER_CREATE_FAILED", "创建章节失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = ContentCacheConstant.CHAPTER_LIST_CACHE,
            key = ContentCacheConstant.CHAPTER_LIST_KEY,
            expire = ContentCacheConstant.CHAPTER_LIST_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<PageResponse<ChapterResponse>> getContentChapters(Long contentId, String status, 
                                                                   Integer currentPage, Integer pageSize) {
        try {
            log.debug("获取内容章节: 内容ID={}", contentId);
            
            Page<ContentChapter> page = new Page<>(currentPage, pageSize);
            Page<ContentChapter> chapterPage = contentChapterService.getChaptersByContentId(page, contentId, status);
            
            PageResponse<ChapterResponse> pageResponse = convertToChapterPageResponse(chapterPage);
            
            return Result.success(pageResponse);
            
        } catch (Exception e) {
            log.error("获取内容章节失败", e);
            return Result.error("","获取内容章节失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = ContentCacheConstant.CHAPTER_DETAIL_CACHE,
            key = ContentCacheConstant.CHAPTER_DETAIL_KEY,
            expire = ContentCacheConstant.CHAPTER_DETAIL_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<ChapterResponse> getChapterById(Long chapterId) {
        try {
            log.debug("获取章节详情: ID={}", chapterId);
            
            ContentChapter chapter = contentChapterService.getChapterById(chapterId);
            
            if (chapter == null) {
                return Result.error("","章节不存在");
            }
            
            ChapterResponse response = convertToResponse(chapter);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("获取章节详情失败", e);
            return Result.error("","获取章节详情失败: " + e.getMessage());
        }
    }

    @Override
    public Result<ChapterResponse> publishChapter(Long chapterId, Long authorId) {
        try {
            log.info("发布章节: ID={}, 作者={}", chapterId, authorId);
            
            ContentChapter publishedChapter = contentChapterService.publishChapter(chapterId, authorId);
            ChapterResponse response = convertToResponse(publishedChapter);
            
            log.info("章节发布成功: ID={}", chapterId);
            return Result.success(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("发布章节参数错误: {}", e.getMessage());
            return Result.error("",e.getMessage());
        } catch (Exception e) {
            log.error("发布章节失败", e);
            return Result.error("","发布章节失败: " + e.getMessage());
        }
    }

    // =================== 统计管理 ===================

    @Override
    public Result<Long> increaseViewCount(Long contentId, Integer increment) {
        try {
            Long newCount = contentService.increaseViewCount(contentId, increment);
            return Result.success(newCount);
        } catch (Exception e) {
            log.error("增加浏览量失败", e);
            return Result.error("","增加浏览量失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Long> increaseLikeCount(Long contentId, Integer increment) {
        try {
            log.debug("增加内容点赞数: contentId={}, increment={}", contentId, increment);
            
            // 更新内容表中的点赞数
            Long newCount = contentService.increaseLikeCount(contentId, increment);
            
            log.debug("内容点赞数更新成功: contentId={}, newCount={}", contentId, newCount);
            return Result.success(newCount);
            
        } catch (Exception e) {
            log.error("增加点赞数失败", e);
            return Result.error("LIKE_COUNT_INCREASE_FAILED", "增加点赞数失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Long> increaseCommentCount(Long contentId, Integer increment) {
        try {
            Long newCount = contentService.increaseCommentCount(contentId, increment);
            return Result.success(newCount);
        } catch (Exception e) {
            log.error("增加评论数失败", e);
            return Result.error("","增加评论数失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Long> increaseFavoriteCount(Long contentId, Integer increment) {
        try {
            log.debug("增加内容收藏数: contentId={}, increment={}", contentId, increment);
            
            // 更新内容表中的收藏数
            Long newCount = contentService.increaseFavoriteCount(contentId, increment);
            
            log.debug("内容收藏数更新成功: contentId={}, newCount={}", contentId, newCount);
            return Result.success(newCount);
            
        } catch (Exception e) {
            log.error("增加收藏数失败", e);
            return Result.error("FAVORITE_COUNT_INCREASE_FAILED", "增加收藏数失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Double> updateScore(Long contentId, Integer score) {
        try {
            log.info("更新内容评分: ID={}, 评分={}", contentId, score);
            
            Double averageScore = contentService.updateScore(contentId, score);
            
            log.info("评分更新成功: ID={}, 平均评分={}", contentId, averageScore);
            return Result.success(averageScore);
            
        } catch (IllegalArgumentException e) {
            log.warn("更新评分参数错误: {}", e.getMessage());
            return Result.error("",e.getMessage());
        } catch (Exception e) {
            log.error("更新评分失败", e);
            return Result.error("","更新评分失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = ContentCacheConstant.CONTENT_STATISTICS_CACHE,
            key = ContentCacheConstant.CONTENT_STATISTICS_KEY,
            expire = ContentCacheConstant.STATISTICS_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<Map<String, Object>> getContentStatistics(Long contentId) {
        try {
            log.debug("获取内容统计: contentId={}", contentId);
            
            // 获取基础统计信息
            Map<String, Object> statistics = contentService.getContentStatistics(contentId);
            
            // =================== 跨模块实时统计增强 ===================
            
            // 1. 获取实时点赞统计
            try {
                var likeCountResult = likeFacadeService.getLikeCount("CONTENT", contentId);
                if (likeCountResult.getSuccess()) {
                    statistics.put("realTimeLikeCount", likeCountResult.getData());
                    log.debug("实时点赞数: {}", likeCountResult.getData());
                }
            } catch (Exception e) {
                log.warn("获取点赞统计失败: {}", e.getMessage());
            }
            
            // 2. 获取实时收藏统计
            try {
                var favoriteCountResult = favoriteFacadeService.getTargetFavoriteCount("CONTENT", contentId);
                if (favoriteCountResult.getSuccess()) {
                    statistics.put("realTimeFavoriteCount", favoriteCountResult.getData());
                    log.debug("实时收藏数: {}", favoriteCountResult.getData());
                }
            } catch (Exception e) {
                log.warn("获取收藏统计失败: {}", e.getMessage());
            }
            
            // 3. 计算热度评分（综合多个维度）
            try {
                Integer viewCount = (Integer) statistics.getOrDefault("viewCount", 0);
                Integer likeCount = (Integer) statistics.getOrDefault("realTimeLikeCount", 
                                                                       statistics.getOrDefault("likeCount", 0));
                Integer favoriteCount = (Integer) statistics.getOrDefault("realTimeFavoriteCount", 
                                                                         statistics.getOrDefault("favoriteCount", 0));
                Integer commentCount = (Integer) statistics.getOrDefault("commentCount", 0);
                
                // 热度计算公式：浏览量*0.1 + 点赞数*2 + 收藏数*5 + 评论数*3
                Double hotScore = viewCount * 0.1 + likeCount * 2 + favoriteCount * 5 + commentCount * 3;
                statistics.put("hotScore", Math.round(hotScore * 100.0) / 100.0);
                log.debug("热度评分: {}", hotScore);
            } catch (Exception e) {
                log.warn("计算热度评分失败: {}", e.getMessage());
            }
            
            // 4. 添加统计时间戳
            statistics.put("statisticsTime", System.currentTimeMillis());
            statistics.put("lastUpdateTime", java.time.LocalDateTime.now().toString());
            
            return Result.success(statistics);
            
        } catch (Exception e) {
            log.error("获取内容统计失败", e);
            return Result.error("CONTENT_STATISTICS_FAILED", "获取内容统计失败: " + e.getMessage());
        }
    }

    // =================== 内容查询 ===================

    @Override
    @Cached(name = ContentCacheConstant.CONTENT_BY_AUTHOR_CACHE,
            key = ContentCacheConstant.AUTHOR_CONTENT_KEY,
            expire = ContentCacheConstant.AUTHOR_CONTENT_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<PageResponse<ContentResponse>> getContentsByAuthor(Long authorId, String contentType, 
                                                                   String status, Integer currentPage, Integer pageSize) {
        try {
            Page<Content> page = new Page<>(currentPage, pageSize);
            Page<Content> contentPage = contentService.getContentsByAuthor(page, authorId, contentType, status);
            
            PageResponse<ContentResponse> pageResponse = convertToPageResponse(contentPage);
            return Result.success(pageResponse);
            
        } catch (Exception e) {
            log.error("根据作者查询内容失败", e);
            return Result.error("","查询失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = ContentCacheConstant.CONTENT_BY_CATEGORY_CACHE,
            key = ContentCacheConstant.CATEGORY_CONTENT_KEY,
            expire = ContentCacheConstant.CATEGORY_CONTENT_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<PageResponse<ContentResponse>> getContentsByCategory(Long categoryId, String contentType,
                                                                     Integer currentPage, Integer pageSize) {
        try {
            Page<Content> page = new Page<>(currentPage, pageSize);
            Page<Content> contentPage = contentService.getContentsByCategory(page, categoryId, contentType);
            
            PageResponse<ContentResponse> pageResponse = convertToPageResponse(contentPage);
            return Result.success(pageResponse);
            
        } catch (Exception e) {
            log.error("根据分类查询内容失败", e);
            return Result.error("","查询失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = ContentCacheConstant.CONTENT_SEARCH_CACHE,
            key = ContentCacheConstant.SEARCH_CONTENT_KEY,
            expire = ContentCacheConstant.SEARCH_RESULT_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<PageResponse<ContentResponse>> searchContents(String keyword, String contentType,
                                                              Integer currentPage, Integer pageSize) {
        try {
            Page<Content> page = new Page<>(currentPage, pageSize);
            Page<Content> contentPage = contentService.searchContents(page, keyword, contentType);
            
            PageResponse<ContentResponse> pageResponse = convertToPageResponse(contentPage);
            return Result.success(pageResponse);
            
        } catch (Exception e) {
            log.error("搜索内容失败", e);
            return Result.error("","搜索失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = ContentCacheConstant.CONTENT_POPULAR_CACHE,
            key = ContentCacheConstant.POPULAR_CONTENT_KEY,
            expire = ContentCacheConstant.POPULAR_CONTENT_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<PageResponse<ContentResponse>> getPopularContents(String contentType, Integer timeRange,
                                                                  Integer currentPage, Integer pageSize) {
        try {
            Page<Content> page = new Page<>(currentPage, pageSize);
            Page<Content> contentPage = contentService.getPopularContents(page, contentType, timeRange);
            
            PageResponse<ContentResponse> pageResponse = convertToPageResponse(contentPage);
            return Result.success(pageResponse);
            
        } catch (Exception e) {
            log.error("获取热门内容失败", e);
            return Result.error("","获取热门内容失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = ContentCacheConstant.CONTENT_LATEST_CACHE,
            key = ContentCacheConstant.LATEST_CONTENT_KEY,
            expire = ContentCacheConstant.LATEST_CONTENT_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<PageResponse<ContentResponse>> getLatestContents(String contentType, Integer currentPage, Integer pageSize) {
        try {
            Page<Content> page = new Page<>(currentPage, pageSize);
            Page<Content> contentPage = contentService.getLatestContents(page, contentType);
            
            PageResponse<ContentResponse> pageResponse = convertToPageResponse(contentPage);
            return Result.success(pageResponse);
            
        } catch (Exception e) {
            log.error("获取最新内容失败", e);
            return Result.error("","获取最新内容失败: " + e.getMessage());
        }
    }

    // =================== 数据同步 ===================

    @Override
    public Result<Integer> updateAuthorInfo(Long authorId, String nickname, String avatar) {
        try {
            Integer count = contentService.updateAuthorInfo(authorId, nickname, avatar);
            return Result.success(count);
        } catch (Exception e) {
            log.error("更新作者信息失败", e);
            return Result.error("","更新作者信息失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> updateCategoryInfo(Long categoryId, String categoryName) {
        try {
            Integer count = contentService.updateCategoryInfo(categoryId, categoryName);
            return Result.success(count);
        } catch (Exception e) {
            log.error("更新分类信息失败", e);
            return Result.error("","更新分类信息失败: " + e.getMessage());
        }
    }

    @Override
    public Result<ContentResponse> reviewContent(Long contentId, String reviewStatus, 
                                                Long reviewerId, String reviewComment) {
        try {
            Content reviewedContent = contentService.reviewContent(contentId, reviewStatus, reviewerId, reviewComment);
            ContentResponse response = convertToResponse(reviewedContent);
            return Result.success(response);
        } catch (Exception e) {
            log.error("审核内容失败", e);
            return Result.error("","审核失败: " + e.getMessage());
        }
    }

    // =================== 私有转换方法 ===================

    /**
     * 创建请求转换为实体
     */
    private Content convertToEntity(ContentCreateRequest request) {
        Content content = new Content();
        BeanUtils.copyProperties(request, content);
        return content;
    }

    /**
     * 更新请求转换为实体
     */
    private Content convertToEntity(ContentUpdateRequest request) {
        Content content = new Content();
        BeanUtils.copyProperties(request, content);
        return content;
    }

    /**
     * 章节请求转换为实体
     */
    private ContentChapter convertToEntity(ChapterCreateRequest request) {
        ContentChapter chapter = new ContentChapter();
        BeanUtils.copyProperties(request, chapter);
        
        // 自动计算字数
        if (chapter.getWordCount() == null && chapter.getContent() != null) {
            chapter.calculateWordCount();
        }
        
        return chapter;
    }

    /**
     * 内容实体转换为响应
     */
    private ContentResponse convertToResponse(Content content) {
        ContentResponse response = new ContentResponse();
        BeanUtils.copyProperties(content, response);
        return response;
    }

    /**
     * 章节实体转换为响应
     */
    private ChapterResponse convertToResponse(ContentChapter chapter) {
        ChapterResponse response = new ChapterResponse();
        BeanUtils.copyProperties(chapter, response);
        return response;
    }

    /**
     * 分页结果转换
     */
    private PageResponse<ContentResponse> convertToPageResponse(Page<Content> contentPage) {
        PageResponse<ContentResponse> pageResponse = new PageResponse<>();
        
        if (CollectionUtils.isEmpty(contentPage.getRecords())) {
            pageResponse.setDatas(Collections.emptyList());
        } else {
            List<ContentResponse> responseList = contentPage.getRecords().stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            pageResponse.setDatas(responseList);
        }
        
        pageResponse.setTotal(contentPage.getTotal());
        pageResponse.setCurrentPage((int) contentPage.getCurrent());
        pageResponse.setPageSize((int) contentPage.getSize());
        pageResponse.setTotalPage((int) contentPage.getPages());
        
        return pageResponse;
    }

    /**
     * 章节分页结果转换
     */
    private PageResponse<ChapterResponse> convertToChapterPageResponse(Page<ContentChapter> chapterPage) {
        PageResponse<ChapterResponse> pageResponse = new PageResponse<>();
        
        if (CollectionUtils.isEmpty(chapterPage.getRecords())) {
            pageResponse.setDatas(Collections.emptyList());
        } else {
            List<ChapterResponse> responseList = chapterPage.getRecords().stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            pageResponse.setDatas(responseList);
        }
        
        pageResponse.setTotal(chapterPage.getTotal());
        pageResponse.setCurrentPage((int) chapterPage.getCurrent());
        pageResponse.setPageSize((int) chapterPage.getSize());
        pageResponse.setTotalPage((int) chapterPage.getPages());
        
        return pageResponse;
    }
}