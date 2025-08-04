package com.gig.collide.content.controller;

import com.gig.collide.api.content.ContentChapterFacadeService;
import com.gig.collide.api.content.response.ChapterResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 内容章节控制器
 * 提供章节查询、统计和管理的REST API接口
 * 
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
@Slf4j
@RestController
@RequestMapping("/api/content/chapters")
@RequiredArgsConstructor
@Validated
@Tag(name = "内容章节管理", description = "内容章节的查询、统计和管理接口")
public class ContentChapterController {

    private final ContentChapterFacadeService contentChapterFacadeService;

    // =================== 基础查询功能 ===================

    @GetMapping("/content/{contentId}")
    @Operation(summary = "获取内容的章节列表", description = "根据内容ID查询所有章节列表（按章节号排序）")
    public Result<List<ChapterResponse>> getChaptersByContentId(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId) {
        try {
            List<ChapterResponse> chapters = contentChapterFacadeService.getChaptersByContentId(contentId);
            return Result.success(chapters);
        } catch (Exception e) {
            log.error("获取章节列表失败: contentId={}", contentId, e);
            return Result.error("GET_CHAPTERS_FAILED", "获取章节列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/content/{contentId}/published")
    @Operation(summary = "获取内容的已发布章节", description = "根据内容ID查询已发布的章节列表")
    public Result<List<ChapterResponse>> getPublishedChaptersByContentId(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId) {
        try {
            List<ChapterResponse> chapters = contentChapterFacadeService.getPublishedChaptersByContentId(contentId);
            return Result.success(chapters);
        } catch (Exception e) {
            log.error("获取已发布章节列表失败: contentId={}", contentId, e);
            return Result.error("GET_PUBLISHED_CHAPTERS_FAILED", "获取已发布章节列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/content/{contentId}/paged")
    @Operation(summary = "分页获取章节列表", description = "根据内容ID分页查询章节")
    public Result<PageResponse<ChapterResponse>> getChaptersByContentIdPaged(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId,
            @Parameter(description = "当前页码", required = true)
            @RequestParam Integer currentPage,
            @Parameter(description = "页面大小", required = true)
            @RequestParam Integer pageSize) {
        try {
            PageResponse<ChapterResponse> pageResponse = contentChapterFacadeService.getChaptersByContentIdPaged(contentId, currentPage, pageSize);
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("分页获取章节列表失败: contentId={}", contentId, e);
            return Result.error("GET_CHAPTERS_PAGED_FAILED", "分页获取章节列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/content/{contentId}/chapter/{chapterNum}")
    @Operation(summary = "获取指定章节", description = "根据内容ID和章节号查询指定章节")
    public Result<ChapterResponse> getChapterByContentIdAndNum(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId,
            @Parameter(description = "章节号", required = true)
            @PathVariable Integer chapterNum) {
        try {
            ChapterResponse chapter = contentChapterFacadeService.getChapterByContentIdAndNum(contentId, chapterNum);
            if (chapter == null) {
                return Result.error("CHAPTER_NOT_FOUND", "章节不存在");
            }
            return Result.success(chapter);
        } catch (Exception e) {
            log.error("获取章节详情失败: contentId={}, chapterNum={}", contentId, chapterNum, e);
            return Result.error("GET_CHAPTER_FAILED", "获取章节详情失败: " + e.getMessage());
        }
    }

    @GetMapping("/content/{contentId}/chapter/{currentChapterNum}/next")
    @Operation(summary = "获取下一章节", description = "根据当前章节号获取下一章节")
    public Result<ChapterResponse> getNextChapter(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId,
            @Parameter(description = "当前章节号", required = true)
            @PathVariable Integer currentChapterNum) {
        try {
            ChapterResponse chapter = contentChapterFacadeService.getNextChapter(contentId, currentChapterNum);
            if (chapter == null) {
                return Result.error("NEXT_CHAPTER_NOT_FOUND", "没有下一章节");
            }
            return Result.success(chapter);
        } catch (Exception e) {
            log.error("获取下一章节失败: contentId={}, currentChapterNum={}", contentId, currentChapterNum, e);
            return Result.error("GET_NEXT_CHAPTER_FAILED", "获取下一章节失败: " + e.getMessage());
        }
    }

    @GetMapping("/content/{contentId}/chapter/{currentChapterNum}/previous")
    @Operation(summary = "获取上一章节", description = "根据当前章节号获取上一章节")
    public Result<ChapterResponse> getPreviousChapter(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId,
            @Parameter(description = "当前章节号", required = true)
            @PathVariable Integer currentChapterNum) {
        try {
            ChapterResponse chapter = contentChapterFacadeService.getPreviousChapter(contentId, currentChapterNum);
            if (chapter == null) {
                return Result.error("PREVIOUS_CHAPTER_NOT_FOUND", "没有上一章节");
            }
            return Result.success(chapter);
        } catch (Exception e) {
            log.error("获取上一章节失败: contentId={}, currentChapterNum={}", contentId, currentChapterNum, e);
            return Result.error("GET_PREVIOUS_CHAPTER_FAILED", "获取上一章节失败: " + e.getMessage());
        }
    }

    @GetMapping("/content/{contentId}/first")
    @Operation(summary = "获取第一章节", description = "获取内容的第一章节")
    public Result<ChapterResponse> getFirstChapter(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId) {
        try {
            ChapterResponse chapter = contentChapterFacadeService.getFirstChapter(contentId);
            if (chapter == null) {
                return Result.error("FIRST_CHAPTER_NOT_FOUND", "第一章节不存在");
            }
            return Result.success(chapter);
        } catch (Exception e) {
            log.error("获取第一章节失败: contentId={}", contentId, e);
            return Result.error("GET_FIRST_CHAPTER_FAILED", "获取第一章节失败: " + e.getMessage());
        }
    }

    @GetMapping("/content/{contentId}/last")
    @Operation(summary = "获取最后章节", description = "获取内容的最后一章节")
    public Result<ChapterResponse> getLastChapter(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId) {
        try {
            ChapterResponse chapter = contentChapterFacadeService.getLastChapter(contentId);
            if (chapter == null) {
                return Result.error("LAST_CHAPTER_NOT_FOUND", "最后章节不存在");
            }
            return Result.success(chapter);
        } catch (Exception e) {
            log.error("获取最后章节失败: contentId={}", contentId, e);
            return Result.error("GET_LAST_CHAPTER_FAILED", "获取最后章节失败: " + e.getMessage());
        }
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "根据状态查询章节", description = "根据章节状态查询章节列表")
    public Result<List<ChapterResponse>> getChaptersByStatus(
            @Parameter(description = "章节状态", required = true)
            @PathVariable String status) {
        try {
            List<ChapterResponse> chapters = contentChapterFacadeService.getChaptersByStatus(status);
            return Result.success(chapters);
        } catch (Exception e) {
            log.error("根据状态查询章节失败: status={}", status, e);
            return Result.error("GET_CHAPTERS_BY_STATUS_FAILED", "根据状态查询章节失败: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    @Operation(summary = "搜索章节", description = "根据标题关键词搜索章节")
    public Result<PageResponse<ChapterResponse>> searchChaptersByTitle(
            @Parameter(description = "标题关键词", required = true)
            @RequestParam String titleKeyword,
            @Parameter(description = "当前页码", required = true)
            @RequestParam Integer currentPage,
            @Parameter(description = "页面大小", required = true)
            @RequestParam Integer pageSize) {
        try {
            PageResponse<ChapterResponse> pageResponse = contentChapterFacadeService.searchChaptersByTitle(titleKeyword, currentPage, pageSize);
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("搜索章节失败: titleKeyword={}", titleKeyword, e);
            return Result.error("SEARCH_CHAPTERS_FAILED", "搜索章节失败: " + e.getMessage());
        }
    }

    @GetMapping("/content/{contentId}/word-count-range")
    @Operation(summary = "按字数范围查询章节", description = "根据字数范围查询章节")
    public Result<List<ChapterResponse>> getChaptersByWordCountRange(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId,
            @Parameter(description = "最小字数")
            @RequestParam(required = false) Integer minWordCount,
            @Parameter(description = "最大字数")
            @RequestParam(required = false) Integer maxWordCount) {
        try {
            List<ChapterResponse> chapters = contentChapterFacadeService.getChaptersByWordCountRange(contentId, minWordCount, maxWordCount);
            return Result.success(chapters);
        } catch (Exception e) {
            log.error("按字数范围查询章节失败: contentId={}", contentId, e);
            return Result.error("GET_CHAPTERS_BY_WORD_COUNT_FAILED", "按字数范围查询章节失败: " + e.getMessage());
        }
    }

    @GetMapping("/content/{contentId}/max-word-count")
    @Operation(summary = "获取字数最多的章节", description = "查询指定内容中字数最多的章节")
    public Result<ChapterResponse> getMaxWordCountChapter(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId) {
        try {
            ChapterResponse chapter = contentChapterFacadeService.getMaxWordCountChapter(contentId);
            if (chapter == null) {
                return Result.error("MAX_WORD_COUNT_CHAPTER_NOT_FOUND", "字数最多的章节不存在");
            }
            return Result.success(chapter);
        } catch (Exception e) {
            log.error("获取字数最多的章节失败: contentId={}", contentId, e);
            return Result.error("GET_MAX_WORD_COUNT_CHAPTER_FAILED", "获取字数最多的章节失败: " + e.getMessage());
        }
    }

    @GetMapping("/content/{contentId}/latest")
    @Operation(summary = "获取最新章节", description = "获取内容的最新更新章节")
    public Result<ChapterResponse> getLatestChapterByContentId(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId) {
        try {
            ChapterResponse chapter = contentChapterFacadeService.getLatestChapterByContentId(contentId);
            if (chapter == null) {
                return Result.error("LATEST_CHAPTER_NOT_FOUND", "最新章节不存在");
            }
            return Result.success(chapter);
        } catch (Exception e) {
            log.error("获取最新章节失败: contentId={}", contentId, e);
            return Result.error("GET_LATEST_CHAPTER_FAILED", "获取最新章节失败: " + e.getMessage());
        }
    }

    @GetMapping("/latest")
    @Operation(summary = "获取最新更新的章节", description = "分页获取最新更新的章节列表")
    public Result<PageResponse<ChapterResponse>> getLatestChapters(
            @Parameter(description = "当前页码", required = true)
            @RequestParam Integer currentPage,
            @Parameter(description = "页面大小", required = true)
            @RequestParam Integer pageSize) {
        try {
            PageResponse<ChapterResponse> pageResponse = contentChapterFacadeService.getLatestChapters(currentPage, pageSize);
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("获取最新更新的章节失败", e);
            return Result.error("GET_LATEST_CHAPTERS_FAILED", "获取最新更新的章节失败: " + e.getMessage());
        }
    }

    // =================== 统计功能 ===================

    @GetMapping("/content/{contentId}/count")
    @Operation(summary = "统计章节总数", description = "统计指定内容的章节总数")
    public Result<Long> countChaptersByContentId(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId) {
        try {
            Long count = contentChapterFacadeService.countChaptersByContentId(contentId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计章节总数失败: contentId={}", contentId, e);
            return Result.error("COUNT_CHAPTERS_FAILED", "统计章节总数失败: " + e.getMessage());
        }
    }

    @GetMapping("/content/{contentId}/published-count")
    @Operation(summary = "统计已发布章节数", description = "统计指定内容的已发布章节数量")
    public Result<Long> countPublishedChaptersByContentId(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId) {
        try {
            Long count = contentChapterFacadeService.countPublishedChaptersByContentId(contentId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计已发布章节数失败: contentId={}", contentId, e);
            return Result.error("COUNT_PUBLISHED_CHAPTERS_FAILED", "统计已发布章节数失败: " + e.getMessage());
        }
    }

    @GetMapping("/content/{contentId}/total-words")
    @Operation(summary = "统计总字数", description = "统计指定内容的总字数")
    public Result<Long> countTotalWordsByContentId(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId) {
        try {
            Long wordCount = contentChapterFacadeService.countTotalWordsByContentId(contentId);
            return Result.success(wordCount);
        } catch (Exception e) {
            log.error("统计总字数失败: contentId={}", contentId, e);
            return Result.error("COUNT_TOTAL_WORDS_FAILED", "统计总字数失败: " + e.getMessage());
        }
    }

    @GetMapping("/content/{contentId}/stats")
    @Operation(summary = "获取章节统计信息", description = "获取指定内容的章节统计信息")
    public Result<Map<String, Object>> getChapterStats(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId) {
        try {
            Map<String, Object> stats = contentChapterFacadeService.getChapterStats(contentId);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取章节统计信息失败: contentId={}", contentId, e);
            return Result.error("GET_CHAPTER_STATS_FAILED", "获取章节统计信息失败: " + e.getMessage());
        }
    }

    // =================== 管理功能 ===================

    @PutMapping("/batch-status")
    @Operation(summary = "批量更新章节状态", description = "批量更新指定章节的状态")
    public Result<Boolean> batchUpdateChapterStatus(
            @Parameter(description = "章节ID列表", required = true)
            @RequestParam List<Long> ids,
            @Parameter(description = "目标状态", required = true)
            @RequestParam String status) {
        try {
            boolean result = contentChapterFacadeService.batchUpdateChapterStatus(ids, status);
            return Result.success(result);
        } catch (Exception e) {
            log.error("批量更新章节状态失败: ids={}, status={}", ids, status, e);
            return Result.error("BATCH_UPDATE_STATUS_FAILED", "批量更新章节状态失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/content/{contentId}/all")
    @Operation(summary = "删除内容的所有章节", description = "删除指定内容的所有章节")
    public Result<Boolean> deleteAllChaptersByContentId(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId) {
        try {
            boolean result = contentChapterFacadeService.deleteAllChaptersByContentId(contentId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("删除内容的所有章节失败: contentId={}", contentId, e);
            return Result.error("DELETE_ALL_CHAPTERS_FAILED", "删除内容的所有章节失败: " + e.getMessage());
        }
    }

    @PutMapping("/content/{contentId}/reorder")
    @Operation(summary = "重新排序章节号", description = "重新排序指定内容的章节号")
    public Result<Boolean> reorderChapterNumbers(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId) {
        try {
            boolean result = contentChapterFacadeService.reorderChapterNumbers(contentId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("重新排序章节号失败: contentId={}", contentId, e);
            return Result.error("REORDER_CHAPTER_NUMBERS_FAILED", "重新排序章节号失败: " + e.getMessage());
        }
    }
}
