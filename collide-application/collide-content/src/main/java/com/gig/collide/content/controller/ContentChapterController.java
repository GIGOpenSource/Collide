package com.gig.collide.content.controller;

import com.gig.collide.api.content.ContentChapterFacadeService;
import com.gig.collide.api.content.request.ChapterCreateRequest;
import com.gig.collide.api.content.response.ChapterResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.content.facade.ContentChapterFacadeServiceImpl;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 内容章节REST控制器 - C端简洁版
 * 专注于C端必需的章节查询功能
 *
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/content/chapters")
@RequiredArgsConstructor
public class ContentChapterController {

    private final ContentChapterFacadeServiceImpl contentChapterFacadeService;

    // =================== 基础查询功能 ===================

    /**
     * 根据内容ID查询章节列表（按章节号排序）
     */
    @GetMapping("/content/{contentId}")
    public Result<List<ChapterResponse>> getChaptersByContentId(@PathVariable Long contentId) {
        log.info("REST请求 - 获取章节列表: contentId={}", contentId);
        try {
            List<ChapterResponse> chapters = contentChapterFacadeService.getChaptersByContentId(contentId);
            return Result.success(chapters);
        } catch (Exception e) {
            log.error("获取章节列表失败: contentId={}", contentId, e);
            return Result.error("","获取章节列表失败");
        }
    }

    /**
     * 根据内容ID查询已发布章节列表
     */
    @GetMapping("/content/{contentId}/published")
    public Result<List<ChapterResponse>> getPublishedChaptersByContentId(@PathVariable Long contentId) {
        log.info("REST请求 - 获取已发布章节列表: contentId={}", contentId);
        try {
            List<ChapterResponse> chapters = contentChapterFacadeService.getPublishedChaptersByContentId(contentId);
            return Result.success(chapters);
        } catch (Exception e) {
            log.error("获取已发布章节列表失败: contentId={}", contentId, e);
            return Result.error("","获取已发布章节列表失败");
        }
    }

    /**
     * 根据内容ID分页查询章节
     */
    @GetMapping("/content/{contentId}/page")
    public Result<PageResponse<ChapterResponse>> getChaptersByContentIdPaged(
            @PathVariable Long contentId,
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("REST请求 - 分页获取章节列表: contentId={}, currentPage={}, pageSize={}", contentId, currentPage, pageSize);
        try {
            PageResponse<ChapterResponse> chapters = contentChapterFacadeService.getChaptersByContentIdPaged(contentId, currentPage, pageSize);
            return Result.success(chapters);
        } catch (Exception e) {
            log.error("分页获取章节列表失败: contentId={}, currentPage={}, pageSize={}", contentId, currentPage, pageSize, e);
            return Result.error("","分页获取章节列表失败");
        }
    }

    /**
     * 根据内容ID和章节号查询章节
     */
    @GetMapping("/content/{contentId}/chapter/{chapterNum}")
    public Result<ChapterResponse> getChapterByContentIdAndNum(
            @PathVariable Long contentId,
            @PathVariable Integer chapterNum) {
        log.info("REST请求 - 获取章节详情: contentId={}, chapterNum={}", contentId, chapterNum);
        try {
            ChapterResponse chapter = contentChapterFacadeService.getChapterByContentIdAndNum(contentId, chapterNum);
            return Result.success(chapter);
        } catch (Exception e) {
            log.error("获取章节详情失败: contentId={}, chapterNum={}", contentId, chapterNum, e);
            return Result.error("","获取章节详情失败");
        }
    }

    /**
     * 查询内容的下一章节
     */
    @GetMapping("/content/{contentId}/next/{currentChapterNum}")
    public Result<ChapterResponse> getNextChapter(
            @PathVariable Long contentId,
            @PathVariable Integer currentChapterNum) {
        log.info("REST请求 - 获取下一章节: contentId={}, currentChapterNum={}", contentId, currentChapterNum);
        try {
            ChapterResponse chapter = contentChapterFacadeService.getNextChapter(contentId, currentChapterNum);
            return Result.success(chapter);
        } catch (Exception e) {
            log.error("获取下一章节失败: contentId={}, currentChapterNum={}", contentId, currentChapterNum, e);
            return Result.error("","获取下一章节失败");
        }
    }

    /**
     * 查询内容的上一章节
     */
    @GetMapping("/content/{contentId}/previous/{currentChapterNum}")
    public Result<ChapterResponse> getPreviousChapter(
            @PathVariable Long contentId,
            @PathVariable Integer currentChapterNum) {
        log.info("REST请求 - 获取上一章节: contentId={}, currentChapterNum={}", contentId, currentChapterNum);
        try {
            ChapterResponse chapter = contentChapterFacadeService.getPreviousChapter(contentId, currentChapterNum);
            return Result.success(chapter);
        } catch (Exception e) {
            log.error("获取上一章节失败: contentId={}, currentChapterNum={}", contentId, currentChapterNum, e);
            return Result.error("","获取上一章节失败");
        }
    }

    /**
     * 查询内容的第一章节
     */
    @GetMapping("/content/{contentId}/first")
    public Result<ChapterResponse> getFirstChapter(@PathVariable Long contentId) {
        log.info("REST请求 - 获取第一章节: contentId={}", contentId);
        try {
            ChapterResponse chapter = contentChapterFacadeService.getFirstChapter(contentId);
            return Result.success(chapter);
        } catch (Exception e) {
            log.error("获取第一章节失败: contentId={}", contentId, e);
            return Result.error("","获取第一章节失败");
        }
    }

    /**
     * 查询内容的最后一章节
     */
    @GetMapping("/content/{contentId}/last")
    public Result<ChapterResponse> getLastChapter(@PathVariable Long contentId) {
        log.info("REST请求 - 获取最后一章节: contentId={}", contentId);
        try {
            ChapterResponse chapter = contentChapterFacadeService.getLastChapter(contentId);
            return Result.success(chapter);
        } catch (Exception e) {
            log.error("获取最后一章节失败: contentId={}", contentId, e);
            return Result.error("","获取最后一章节失败");
        }
    }

    /**
     * 根据状态查询章节列表
     */
    @GetMapping("/status/{status}")
    public Result<List<ChapterResponse>> getChaptersByStatus(@PathVariable String status) {
        log.info("REST请求 - 根据状态获取章节列表: status={}", status);
        try {
            List<ChapterResponse> chapters = contentChapterFacadeService.getChaptersByStatus(status);
            return Result.success(chapters);
        } catch (Exception e) {
            log.error("根据状态获取章节列表失败: status={}", status, e);
            return Result.error("","根据状态获取章节列表失败");
        }
    }

    /**
     * 根据章节标题搜索
     */
    @GetMapping("/search")
    public Result<PageResponse<ChapterResponse>> searchChaptersByTitle(
            @RequestParam String titleKeyword,
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("REST请求 - 搜索章节: titleKeyword={}, currentPage={}, pageSize={}", titleKeyword, currentPage, pageSize);
        try {
            PageResponse<ChapterResponse> chapters = contentChapterFacadeService.searchChaptersByTitle(titleKeyword, currentPage, pageSize);
            return Result.success(chapters);
        } catch (Exception e) {
            log.error("搜索章节失败: titleKeyword={}, currentPage={}, pageSize={}", titleKeyword, currentPage, pageSize, e);
            return Result.error("","搜索章节失败");
        }
    }

    /**
     * 根据内容ID和字数范围查询章节
     */
    @GetMapping("/content/{contentId}/wordcount")
    public Result<List<ChapterResponse>> getChaptersByWordCountRange(
            @PathVariable Long contentId,
            @RequestParam Integer minWordCount,
            @RequestParam Integer maxWordCount) {
        log.info("REST请求 - 根据字数范围获取章节: contentId={}, minWordCount={}, maxWordCount={}", 
                contentId, minWordCount, maxWordCount);
        try {
            List<ChapterResponse> chapters = contentChapterFacadeService.getChaptersByWordCountRange(contentId, minWordCount, maxWordCount);
            return Result.success(chapters);
        } catch (Exception e) {
            log.error("根据字数范围获取章节失败: contentId={}, minWordCount={}, maxWordCount={}", 
                    contentId, minWordCount, maxWordCount, e);
            return Result.error("","根据字数范围获取章节失败");
        }
    }

    /**
     * 查询字数最多的章节
     */
    @GetMapping("/content/{contentId}/max-wordcount")
    public Result<ChapterResponse> getMaxWordCountChapter(@PathVariable Long contentId) {
        log.info("REST请求 - 获取字数最多的章节: contentId={}", contentId);
        try {
            ChapterResponse chapter = contentChapterFacadeService.getMaxWordCountChapter(contentId);
            return Result.success(chapter);
        } catch (Exception e) {
            log.error("获取字数最多的章节失败: contentId={}", contentId, e);
            return Result.error("","获取字数最多的章节失败");
        }
    }

    /**
     * 查询指定内容的最新章节
     */
    @GetMapping("/content/{contentId}/latest")
    public Result<ChapterResponse> getLatestChapterByContentId(@PathVariable Long contentId) {
        log.info("REST请求 - 获取最新章节: contentId={}", contentId);
        try {
            ChapterResponse chapter = contentChapterFacadeService.getLatestChapterByContentId(contentId);
            return Result.success(chapter);
        } catch (Exception e) {
            log.error("获取最新章节失败: contentId={}", contentId, e);
            return Result.error("","获取最新章节失败");
        }
    }

    /**
     * 查询最新更新的章节
     */
    @GetMapping("/latest")
    public Result<PageResponse<ChapterResponse>> getLatestChapters(
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("REST请求 - 获取最新更新的章节: currentPage={}, pageSize={}", currentPage, pageSize);
        try {
            PageResponse<ChapterResponse> chapters = contentChapterFacadeService.getLatestChapters(currentPage, pageSize);
            return Result.success(chapters);
        } catch (Exception e) {
            log.error("获取最新更新的章节失败: currentPage={}, pageSize={}", currentPage, pageSize, e);
            return Result.error("","获取最新更新的章节失败");
        }
    }

    // =================== 统计功能 ===================

    /**
     * 统计内容的章节总数
     */
    @GetMapping("/count/content/{contentId}")
    public Result<Long> countChaptersByContentId(@PathVariable Long contentId) {
        log.info("REST请求 - 统计章节总数: contentId={}", contentId);
        try {
            Long count = contentChapterFacadeService.countChaptersByContentId(contentId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计章节总数失败: contentId={}", contentId, e);
            return Result.error("","统计章节总数失败");
        }
    }

    /**
     * 统计内容的已发布章节数
     */
    @GetMapping("/count/content/{contentId}/published")
    public Result<Long> countPublishedChaptersByContentId(@PathVariable Long contentId) {
        log.info("REST请求 - 统计已发布章节数: contentId={}", contentId);
        try {
            Long count = contentChapterFacadeService.countPublishedChaptersByContentId(contentId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计已发布章节数失败: contentId={}", contentId, e);
            return Result.error("","统计已发布章节数失败");
        }
    }

    /**
     * 统计内容的总字数
     */
    @GetMapping("/wordcount/total/content/{contentId}")
    public Result<Long> countTotalWordsByContentId(@PathVariable Long contentId) {
        log.info("REST请求 - 统计总字数: contentId={}", contentId);
        try {
            Long wordCount = contentChapterFacadeService.countTotalWordsByContentId(contentId);
            return Result.success(wordCount);
        } catch (Exception e) {
            log.error("统计总字数失败: contentId={}", contentId, e);
            return Result.error("","统计总字数失败");
        }
    }

    /**
     * 获取内容的章节统计信息
     */
    @GetMapping("/stats/content/{contentId}")
    public Result<Map<String, Object>> getChapterStats(@PathVariable Long contentId) {
        log.info("REST请求 - 获取章节统计信息: contentId={}", contentId);
        try {
            Map<String, Object> stats = contentChapterFacadeService.getChapterStats(contentId);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取章节统计信息失败: contentId={}", contentId, e);
            return Result.error("","获取章节统计信息失败");
        }
    }

    // =================== 管理功能 ===================

    /**
     * 批量更新章节状态
     */
    @PutMapping("/batch/status")
    public Result<Boolean> batchUpdateChapterStatus(
            @RequestParam List<Long> ids,
            @RequestParam String status) {
        log.info("REST请求 - 批量更新章节状态: ids={}, status={}", ids, status);
        try {
            boolean result = contentChapterFacadeService.batchUpdateChapterStatus(ids, status);
            return Result.success(result);
        } catch (Exception e) {
            log.error("批量更新章节状态失败: ids={}, status={}", ids, status, e);
            return Result.error("","批量更新章节状态失败");
        }
    }

    /**
     * 删除内容的所有章节
     */
    @DeleteMapping("/content/{contentId}")
    public Result<Boolean> deleteAllChaptersByContentId(@PathVariable Long contentId) {
        log.info("REST请求 - 删除内容的所有章节: contentId={}", contentId);
        try {
            boolean result = contentChapterFacadeService.deleteAllChaptersByContentId(contentId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("删除内容的所有章节失败: contentId={}", contentId, e);
            return Result.error("","删除内容的所有章节失败");
        }
    }

    /**
     * 重新排序章节号（用于章节删除后的重新编号）
     */
    @PutMapping("/content/{contentId}/reorder")
    public Result<Boolean> reorderChapterNumbers(@PathVariable Long contentId) {
        log.info("REST请求 - 重新排序章节号: contentId={}", contentId);
        try {
            boolean result = contentChapterFacadeService.reorderChapterNumbers(contentId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("重新排序章节号失败: contentId={}", contentId, e);
            return Result.error("","重新排序章节号失败");
        }
    }
}
