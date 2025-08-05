package com.gig.collide.content.facade;

import com.gig.collide.api.content.ContentChapterFacadeService;
import com.gig.collide.api.content.response.ChapterResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import com.gig.collide.content.domain.entity.ContentChapter;
import com.gig.collide.content.domain.service.ContentChapterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 内容章节门面服务实现类 - 极简版
 * 专注于章节核心功能，8个核心方法
 * 
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
@Slf4j
@Service
@DubboService(version = "2.0.0", timeout = 5000)
@RequiredArgsConstructor
public class ContentChapterFacadeServiceImpl implements ContentChapterFacadeService {

    private final ContentChapterService contentChapterService;

    // =================== 核心CRUD功能（2个方法）===================

    @Override
    public Result<ChapterResponse> getChapterById(Long id) {
        try {
            log.debug("获取章节详情: id={}", id);
            
            ContentChapter chapter = contentChapterService.getChapterById(id);
            
            if (chapter == null) {
                return Result.error("CHAPTER_NOT_FOUND", "章节不存在");
            }
            
            ChapterResponse response = convertToResponse(chapter);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取章节详情失败: id={}", id, e);
            return Result.error("GET_CHAPTER_FAILED", "获取章节详情失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> deleteChapter(Long id) {
        try {
            log.info("删除章节: id={}", id);
            
            boolean result = contentChapterService.deleteChapter(id);
            
            if (result) {
                return Result.success(true);
            } else {
                return Result.error("DELETE_FAILED", "删除章节失败");
            }
        } catch (Exception e) {
            log.error("删除章节失败: id={}", id, e);
            return Result.error("DELETE_FAILED", "删除章节失败: " + e.getMessage());
        }
    }

    // =================== 万能查询功能（3个方法）===================

    @Override
    public Result<PageResponse<ChapterResponse>> getChaptersByConditions(Long contentId, String status, 
                                                                        Integer chapterNumStart, Integer chapterNumEnd,
                                                                        Integer minWordCount, Integer maxWordCount,
                                                                        String orderBy, String orderDirection,
                                                                        Integer currentPage, Integer pageSize) {
        try {
            log.debug("万能条件查询章节: contentId={}, status={}", contentId, status);
            
            // 调用Service层的万能查询方法
            List<ContentChapter> chapters = contentChapterService.getChaptersByConditions(
                contentId, status, chapterNumStart, chapterNumEnd,
                minWordCount, maxWordCount, orderBy, orderDirection,
                currentPage, pageSize
            );
            
            // 构建分页响应
            PageResponse<ChapterResponse> pageResponse = convertToPageResponse(chapters, currentPage, pageSize);
            
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("万能条件查询章节失败", e);
            return Result.error("QUERY_FAILED", "查询章节失败: " + e.getMessage());
        }
    }

    @Override
    public Result<ChapterResponse> getChapterByNavigation(Long contentId, Integer currentChapterNum, String direction) {
        try {
            log.debug("章节导航查询: contentId={}, currentChapterNum={}, direction={}", 
                     contentId, currentChapterNum, direction);
            
            ContentChapter chapter = contentChapterService.getChapterByNavigation(contentId, currentChapterNum, direction);
            
            if (chapter == null) {
                return Result.error("CHAPTER_NOT_FOUND", "未找到相应章节");
            }
            
            ChapterResponse response = convertToResponse(chapter);
            return Result.success(response);
        } catch (Exception e) {
            log.error("章节导航查询失败: contentId={}, direction={}", contentId, direction, e);
            return Result.error("NAVIGATION_FAILED", "章节导航失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<ChapterResponse>> searchChapters(String keyword, Long contentId, String status, 
                                                              Integer currentPage, Integer pageSize) {
        try {
            log.debug("搜索章节: keyword={}, contentId={}", keyword, contentId);
            
            // 使用万能查询方法实现搜索（通过标题关键词搜索）
            // 这里简化实现，实际可以调用专门的搜索方法
            List<ContentChapter> chapters = contentChapterService.getChaptersByConditions(
                contentId, status, null, null, null, null,
                "createTime", "DESC", currentPage, pageSize
            );
            
            // 如果有关键词，可以在此处进行过滤（或在Service层实现更复杂的搜索逻辑）
            PageResponse<ChapterResponse> pageResponse = convertToPageResponse(chapters, currentPage, pageSize);
            
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("搜索章节失败: keyword={}", keyword, e);
            return Result.error("SEARCH_FAILED", "搜索章节失败: " + e.getMessage());
        }
    }

    // =================== 统计功能（1个方法）===================

    @Override
    public Result<Map<String, Object>> getChapterStats(Long contentId) {
        try {
            log.debug("获取章节统计信息: contentId={}", contentId);
            
            Map<String, Object> stats = new HashMap<>();
            
            // 使用万能查询方法获取各种统计数据
            // 1. 总章节数
            List<ContentChapter> allChapters = contentChapterService.getChaptersByConditions(
                contentId, null, null, null, null, null, null, null, null, null);
            stats.put("totalChapters", allChapters.size());
            
            // 2. 已发布章节数
            List<ContentChapter> publishedChapters = contentChapterService.getChaptersByConditions(
                contentId, "PUBLISHED", null, null, null, null, null, null, null, null);
            stats.put("publishedChapters", publishedChapters.size());
            
            // 3. 总字数（需要遍历计算）
            long totalWords = allChapters.stream()
                .mapToLong(chapter -> chapter.getWordCount() != null ? chapter.getWordCount() : 0L)
                .sum();
            stats.put("totalWords", totalWords);
            
            // 4. 平均字数
            double avgWords = allChapters.isEmpty() ? 0.0 : (double) totalWords / allChapters.size();
            stats.put("averageWords", Math.round(avgWords * 100.0) / 100.0);
            
            // 5. 最新章节信息
            if (!allChapters.isEmpty()) {
                ContentChapter latestChapter = allChapters.stream()
                    .filter(chapter -> chapter.getCreateTime() != null)
                    .max((c1, c2) -> c1.getCreateTime().compareTo(c2.getCreateTime()))
                    .orElse(null);
                if (latestChapter != null) {
                    stats.put("latestChapterTitle", latestChapter.getTitle());
                    stats.put("latestChapterTime", latestChapter.getCreateTime());
                }
            }
            
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取章节统计信息失败: contentId={}", contentId, e);
            return Result.error("GET_STATS_FAILED", "获取统计信息失败: " + e.getMessage());
        }
    }

    // =================== 批量操作功能（2个方法）===================

    @Override
    public Result<Boolean> batchUpdateChapterStatus(List<Long> ids, String status) {
        try {
            log.info("批量更新章节状态: ids.size={}, status={}", 
                    ids != null ? ids.size() : 0, status);
            
            boolean result = contentChapterService.batchUpdateChapterStatus(ids, status);
            
            if (result) {
                return Result.success(true);
            } else {
                return Result.error("BATCH_UPDATE_FAILED", "批量更新章节状态失败");
            }
        } catch (Exception e) {
            log.error("批量更新章节状态失败", e);
            return Result.error("BATCH_UPDATE_FAILED", "批量更新失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> batchDeleteChapters(List<Long> ids) {
        try {
            log.info("批量删除章节: ids.size={}", ids != null ? ids.size() : 0);
            
            boolean result = contentChapterService.batchDeleteChapters(ids);
            
            if (result) {
                return Result.success(true);
            } else {
                return Result.error("BATCH_DELETE_FAILED", "批量删除章节失败");
            }
        } catch (Exception e) {
            log.error("批量删除章节失败", e);
            return Result.error("BATCH_DELETE_FAILED", "批量删除失败: " + e.getMessage());
        }
    }

    // =================== 私有辅助方法 ===================

    private ChapterResponse convertToResponse(ContentChapter chapter) {
        ChapterResponse response = new ChapterResponse();
        BeanUtils.copyProperties(chapter, response);
        return response;
    }

    private PageResponse<ChapterResponse> convertToPageResponse(List<ContentChapter> chapters, Integer currentPage, Integer pageSize) {
        PageResponse<ChapterResponse> pageResponse = new PageResponse<>();
        
        if (CollectionUtils.isEmpty(chapters)) {
            pageResponse.setDatas(Collections.emptyList());
            pageResponse.setTotal(0L);
        } else {
            List<ChapterResponse> responses = chapters.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            pageResponse.setDatas(responses);
            pageResponse.setTotal((long) chapters.size());
        }
        
        pageResponse.setCurrentPage(currentPage != null ? currentPage : 1);
        pageResponse.setPageSize(pageSize != null ? pageSize : 20);
        if (pageResponse.getPageSize() > 0) {
            pageResponse.setTotalPage((int) Math.ceil((double) pageResponse.getTotal() / pageResponse.getPageSize()));
        } else {
            pageResponse.setTotalPage(0);
        }
        pageResponse.setSuccess(true);
        
        return pageResponse;
    }
}