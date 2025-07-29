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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
@DubboService
@RequiredArgsConstructor
public class ContentFacadeServiceImpl implements ContentFacadeService {

    private final ContentService contentService;
    private final ContentChapterService contentChapterService;

    // =================== 内容管理 ===================

    @Override
    public Result<ContentResponse> createContent(ContentCreateRequest request) {
        try {
            log.info("创建内容请求: {}", request.getTitle());
            
            // 请求参数转换为实体
            Content content = convertToEntity(request);
            
            // 调用业务服务
            Content createdContent = contentService.createContent(content);
            
            // 实体转换为响应
            ContentResponse response = convertToResponse(createdContent);
            
            log.info("内容创建成功: ID={}", response.getId());
            return Result.success(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("创建内容参数错误: {}", e.getMessage());
            return Result.error("",e.getMessage());
        } catch (Exception e) {
            log.error("创建内容失败", e);
            return Result.error("","创建内容失败: " + e.getMessage());
        }
    }

    @Override
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
            return Result.error("",e.getMessage());
        } catch (Exception e) {
            log.error("更新内容失败", e);
            return Result.error("","更新内容失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> deleteContent(Long contentId, Long operatorId) {
        try {
            log.info("删除内容请求: ID={}, 操作人={}", contentId, operatorId);
            
            boolean deleted = contentService.deleteContent(contentId, operatorId);
            
            if (deleted) {
                log.info("内容删除成功: ID={}", contentId);
                return Result.success(null);
            } else {
                return Result.error("","内容删除失败，内容不存在或无权限");
            }
            
        } catch (IllegalArgumentException e) {
            log.warn("删除内容参数错误: {}", e.getMessage());
            return Result.error("",e.getMessage());
        } catch (Exception e) {
            log.error("删除内容失败", e);
            return Result.error("","删除内容失败: " + e.getMessage());
        }
    }

    @Override
    public Result<ContentResponse> getContentById(Long contentId, Boolean includeOffline) {
        try {
            log.debug("获取内容详情: ID={}", contentId);
            
            Content content = contentService.getContentById(contentId, includeOffline);
            
            if (content == null) {
                return Result.error("","内容不存在");
            }
            
            ContentResponse response = convertToResponse(content);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("获取内容详情失败", e);
            return Result.error("","获取内容详情失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<ContentResponse>> queryContents(ContentQueryRequest request) {
        try {
            log.debug("分页查询内容: {}", request.getKeyword());
            
            // 创建分页对象
            Page<Content> page = new Page<>(request.getPageNum(), request.getPageSize());
            
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
    public Result<ChapterResponse> createChapter(ChapterCreateRequest request) {
        try {
            log.info("创建章节: 内容ID={}, 章节号={}", request.getContentId(), request.getChapterNum());
            
            // 请求转换为实体
            ContentChapter chapter = convertToEntity(request);
            
            // 调用业务服务
            ContentChapter createdChapter = contentChapterService.createChapter(chapter);
            
            // 实体转换为响应
            ChapterResponse response = convertToResponse(createdChapter);
            
            log.info("章节创建成功: ID={}", response.getId());
            return Result.success(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("创建章节参数错误: {}", e.getMessage());
            return Result.error("",e.getMessage());
        } catch (Exception e) {
            log.error("创建章节失败", e);
            return Result.error("","创建章节失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<ChapterResponse>> getContentChapters(Long contentId, String status, 
                                                                   Integer pageNum, Integer pageSize) {
        try {
            log.debug("获取内容章节: 内容ID={}", contentId);
            
            Page<ContentChapter> page = new Page<>(pageNum, pageSize);
            Page<ContentChapter> chapterPage = contentChapterService.getChaptersByContentId(page, contentId, status);
            
            PageResponse<ChapterResponse> pageResponse = convertToChapterPageResponse(chapterPage);
            
            return Result.success(pageResponse);
            
        } catch (Exception e) {
            log.error("获取内容章节失败", e);
            return Result.error("","获取内容章节失败: " + e.getMessage());
        }
    }

    @Override
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
            Long newCount = contentService.increaseLikeCount(contentId, increment);
            return Result.success(newCount);
        } catch (Exception e) {
            log.error("增加点赞数失败", e);
            return Result.error("","增加点赞数失败: " + e.getMessage());
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
            Long newCount = contentService.increaseFavoriteCount(contentId, increment);
            return Result.success(newCount);
        } catch (Exception e) {
            log.error("增加收藏数失败", e);
            return Result.error("","增加收藏数失败: " + e.getMessage());
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
    public Result<Map<String, Object>> getContentStatistics(Long contentId) {
        try {
            Map<String, Object> statistics = contentService.getContentStatistics(contentId);
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取内容统计失败", e);
            return Result.error("","获取内容统计失败: " + e.getMessage());
        }
    }

    // =================== 内容查询 ===================

    @Override
    public Result<PageResponse<ContentResponse>> getContentsByAuthor(Long authorId, String contentType, 
                                                                   String status, Integer pageNum, Integer pageSize) {
        try {
            Page<Content> page = new Page<>(pageNum, pageSize);
            Page<Content> contentPage = contentService.getContentsByAuthor(page, authorId, contentType, status);
            
            PageResponse<ContentResponse> pageResponse = convertToPageResponse(contentPage);
            return Result.success(pageResponse);
            
        } catch (Exception e) {
            log.error("根据作者查询内容失败", e);
            return Result.error("","查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<ContentResponse>> getContentsByCategory(Long categoryId, String contentType,
                                                                     Integer pageNum, Integer pageSize) {
        try {
            Page<Content> page = new Page<>(pageNum, pageSize);
            Page<Content> contentPage = contentService.getContentsByCategory(page, categoryId, contentType);
            
            PageResponse<ContentResponse> pageResponse = convertToPageResponse(contentPage);
            return Result.success(pageResponse);
            
        } catch (Exception e) {
            log.error("根据分类查询内容失败", e);
            return Result.error("","查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<ContentResponse>> searchContents(String keyword, String contentType,
                                                              Integer pageNum, Integer pageSize) {
        try {
            Page<Content> page = new Page<>(pageNum, pageSize);
            Page<Content> contentPage = contentService.searchContents(page, keyword, contentType);
            
            PageResponse<ContentResponse> pageResponse = convertToPageResponse(contentPage);
            return Result.success(pageResponse);
            
        } catch (Exception e) {
            log.error("搜索内容失败", e);
            return Result.error("","搜索失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<ContentResponse>> getPopularContents(String contentType, Integer timeRange,
                                                                  Integer pageNum, Integer pageSize) {
        try {
            Page<Content> page = new Page<>(pageNum, pageSize);
            Page<Content> contentPage = contentService.getPopularContents(page, contentType, timeRange);
            
            PageResponse<ContentResponse> pageResponse = convertToPageResponse(contentPage);
            return Result.success(pageResponse);
            
        } catch (Exception e) {
            log.error("获取热门内容失败", e);
            return Result.error("","获取热门内容失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<ContentResponse>> getLatestContents(String contentType, Integer pageNum, Integer pageSize) {
        try {
            Page<Content> page = new Page<>(pageNum, pageSize);
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