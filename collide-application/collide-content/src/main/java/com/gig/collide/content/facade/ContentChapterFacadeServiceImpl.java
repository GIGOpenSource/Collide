package com.gig.collide.content.facade;

import com.gig.collide.api.content.ContentChapterFacadeService;
import com.gig.collide.api.content.request.ChapterCreateRequest;
import com.gig.collide.api.content.response.ChapterResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.content.domain.entity.ContentChapter;
import com.gig.collide.content.domain.service.ContentChapterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 内容章节外观服务实现类 - C端简洁版
 * 专注于C端必需的章节查询功能，移除复杂的管理接口
 * 基于单表无连表设计
 *
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
@Slf4j
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class ContentChapterFacadeServiceImpl implements ContentChapterFacadeService {

    private final ContentChapterService contentChapterService;

    // =================== 基础查询功能 ===================

    @Override
    public List<ChapterResponse> getChaptersByContentId(Long contentId) {
        log.info("Facade - 获取章节列表: contentId={}", contentId);
        try {
            List<ContentChapter> chapters = contentChapterService.getChaptersByContentId(contentId);
            return convertToChapterResponseList(chapters);
        } catch (Exception e) {
            log.error("Facade - 获取章节列表失败: contentId={}", contentId, e);
            throw new RuntimeException("获取章节列表失败：" + e.getMessage(), e);
        }
    }

    @Override
    public List<ChapterResponse> getPublishedChaptersByContentId(Long contentId) {
        log.info("Facade - 获取已发布章节列表: contentId={}", contentId);
        try {
            List<ContentChapter> chapters = contentChapterService.getPublishedChaptersByContentId(contentId);
            return convertToChapterResponseList(chapters);
        } catch (Exception e) {
            log.error("Facade - 获取已发布章节列表失败: contentId={}", contentId, e);
            throw new RuntimeException("获取已发布章节列表失败：" + e.getMessage(), e);
        }
    }

    @Override
    public PageResponse<ChapterResponse> getChaptersByContentIdPaged(Long contentId, Integer currentPage, Integer pageSize) {
        log.info("Facade - 分页获取章节列表: contentId={}, currentPage={}, pageSize={}", contentId, currentPage, pageSize);
        try {
            List<ContentChapter> chapters = contentChapterService.getChaptersByContentIdPaged(contentId, currentPage, pageSize);
            Long total = contentChapterService.countChaptersByContentId(contentId);
            
            List<ChapterResponse> chapterResponses = convertToChapterResponseList(chapters);
            return PageResponse.of(chapterResponses, total, pageSize, currentPage);
        } catch (Exception e) {
            log.error("Facade - 分页获取章节列表失败: contentId={}, currentPage={}, pageSize={}", contentId, currentPage, pageSize, e);
            throw new RuntimeException("分页获取章节列表失败：" + e.getMessage(), e);
        }
    }

    @Override
    public ChapterResponse getChapterByContentIdAndNum(Long contentId, Integer chapterNum) {
        log.info("Facade - 获取章节详情: contentId={}, chapterNum={}", contentId, chapterNum);
        try {
            ContentChapter chapter = contentChapterService.getChapterByContentIdAndNum(contentId, chapterNum);
            return convertToChapterResponse(chapter);
        } catch (Exception e) {
            log.error("Facade - 获取章节详情失败: contentId={}, chapterNum={}", contentId, chapterNum, e);
            throw new RuntimeException("获取章节详情失败：" + e.getMessage(), e);
        }
    }

    @Override
    public ChapterResponse getNextChapter(Long contentId, Integer currentChapterNum) {
        log.info("Facade - 获取下一章节: contentId={}, currentChapterNum={}", contentId, currentChapterNum);
        try {
            ContentChapter chapter = contentChapterService.getNextChapter(contentId, currentChapterNum);
            return convertToChapterResponse(chapter);
        } catch (Exception e) {
            log.error("Facade - 获取下一章节失败: contentId={}, currentChapterNum={}", contentId, currentChapterNum, e);
            throw new RuntimeException("获取下一章节失败：" + e.getMessage(), e);
        }
    }

    @Override
    public ChapterResponse getPreviousChapter(Long contentId, Integer currentChapterNum) {
        log.info("Facade - 获取上一章节: contentId={}, currentChapterNum={}", contentId, currentChapterNum);
        try {
            ContentChapter chapter = contentChapterService.getPreviousChapter(contentId, currentChapterNum);
            return convertToChapterResponse(chapter);
        } catch (Exception e) {
            log.error("Facade - 获取上一章节失败: contentId={}, currentChapterNum={}", contentId, currentChapterNum, e);
            throw new RuntimeException("获取上一章节失败：" + e.getMessage(), e);
        }
    }

    @Override
    public ChapterResponse getFirstChapter(Long contentId) {
        log.info("Facade - 获取第一章节: contentId={}", contentId);
        try {
            ContentChapter chapter = contentChapterService.getFirstChapter(contentId);
            return convertToChapterResponse(chapter);
        } catch (Exception e) {
            log.error("Facade - 获取第一章节失败: contentId={}", contentId, e);
            throw new RuntimeException("获取第一章节失败：" + e.getMessage(), e);
        }
    }

    @Override
    public ChapterResponse getLastChapter(Long contentId) {
        log.info("Facade - 获取最后一章节: contentId={}", contentId);
        try {
            ContentChapter chapter = contentChapterService.getLastChapter(contentId);
            return convertToChapterResponse(chapter);
        } catch (Exception e) {
            log.error("Facade - 获取最后一章节失败: contentId={}", contentId, e);
            throw new RuntimeException("获取最后一章节失败：" + e.getMessage(), e);
        }
    }

    @Override
    public List<ChapterResponse> getChaptersByStatus(String status) {
        log.info("Facade - 根据状态获取章节列表: status={}", status);
        try {
            List<ContentChapter> chapters = contentChapterService.getChaptersByStatus(status);
            return convertToChapterResponseList(chapters);
        } catch (Exception e) {
            log.error("Facade - 根据状态获取章节列表失败: status={}", status, e);
            throw new RuntimeException("根据状态获取章节列表失败：" + e.getMessage(), e);
        }
    }

    @Override
    public PageResponse<ChapterResponse> searchChaptersByTitle(String titleKeyword, Integer currentPage, Integer pageSize) {
        log.info("Facade - 搜索章节: titleKeyword={}, currentPage={}, pageSize={}", titleKeyword, currentPage, pageSize);
        try {
            List<ContentChapter> chapters = contentChapterService.searchChaptersByTitle(titleKeyword, currentPage, pageSize);
            // 这里需要根据实际需求计算总数，暂时使用列表大小
            Long total = (long) chapters.size();
            
            List<ChapterResponse> chapterResponses = convertToChapterResponseList(chapters);
            return PageResponse.of(chapterResponses, total, pageSize, currentPage);
        } catch (Exception e) {
            log.error("Facade - 搜索章节失败: titleKeyword={}, currentPage={}, pageSize={}", titleKeyword, currentPage, pageSize, e);
            throw new RuntimeException("搜索章节失败：" + e.getMessage(), e);
        }
    }

    @Override
    public List<ChapterResponse> getChaptersByWordCountRange(Long contentId, Integer minWordCount, Integer maxWordCount) {
        log.info("Facade - 根据字数范围获取章节: contentId={}, minWordCount={}, maxWordCount={}", contentId, minWordCount, maxWordCount);
        try {
            List<ContentChapter> chapters = contentChapterService.getChaptersByWordCountRange(contentId, minWordCount, maxWordCount);
            return convertToChapterResponseList(chapters);
        } catch (Exception e) {
            log.error("Facade - 根据字数范围获取章节失败: contentId={}, minWordCount={}, maxWordCount={}", contentId, minWordCount, maxWordCount, e);
            throw new RuntimeException("根据字数范围获取章节失败：" + e.getMessage(), e);
        }
    }

    @Override
    public ChapterResponse getMaxWordCountChapter(Long contentId) {
        log.info("Facade - 获取字数最多的章节: contentId={}", contentId);
        try {
            ContentChapter chapter = contentChapterService.getMaxWordCountChapter(contentId);
            return convertToChapterResponse(chapter);
        } catch (Exception e) {
            log.error("Facade - 获取字数最多的章节失败: contentId={}", contentId, e);
            throw new RuntimeException("获取字数最多的章节失败：" + e.getMessage(), e);
        }
    }

    @Override
    public ChapterResponse getLatestChapterByContentId(Long contentId) {
        log.info("Facade - 获取最新章节: contentId={}", contentId);
        try {
            ContentChapter chapter = contentChapterService.getLatestChapterByContentId(contentId);
            return convertToChapterResponse(chapter);
        } catch (Exception e) {
            log.error("Facade - 获取最新章节失败: contentId={}", contentId, e);
            throw new RuntimeException("获取最新章节失败：" + e.getMessage(), e);
        }
    }

    @Override
    public PageResponse<ChapterResponse> getLatestChapters(Integer currentPage, Integer pageSize) {
        log.info("Facade - 获取最新更新的章节: currentPage={}, pageSize={}", currentPage, pageSize);
        try {
            List<ContentChapter> chapters = contentChapterService.getLatestChapters(currentPage, pageSize);
            // 这里需要根据实际需求计算总数，暂时使用列表大小
            Long total = (long) chapters.size();
            
            List<ChapterResponse> chapterResponses = convertToChapterResponseList(chapters);
            return PageResponse.of(chapterResponses, total, pageSize, currentPage);
        } catch (Exception e) {
            log.error("Facade - 获取最新更新的章节失败: currentPage={}, pageSize={}", currentPage, pageSize, e);
            throw new RuntimeException("获取最新更新的章节失败：" + e.getMessage(), e);
        }
    }

    // =================== 统计功能 ===================

    @Override
    public Long countChaptersByContentId(Long contentId) {
        log.info("Facade - 统计章节总数: contentId={}", contentId);
        try {
            return contentChapterService.countChaptersByContentId(contentId);
        } catch (Exception e) {
            log.error("Facade - 统计章节总数失败: contentId={}", contentId, e);
            throw new RuntimeException("统计章节总数失败：" + e.getMessage(), e);
        }
    }

    @Override
    public Long countPublishedChaptersByContentId(Long contentId) {
        log.info("Facade - 统计已发布章节数: contentId={}", contentId);
        try {
            return contentChapterService.countPublishedChaptersByContentId(contentId);
        } catch (Exception e) {
            log.error("Facade - 统计已发布章节数失败: contentId={}", contentId, e);
            throw new RuntimeException("统计已发布章节数失败：" + e.getMessage(), e);
        }
    }

    @Override
    public Long countTotalWordsByContentId(Long contentId) {
        log.info("Facade - 统计总字数: contentId={}", contentId);
        try {
            return contentChapterService.countTotalWordsByContentId(contentId);
        } catch (Exception e) {
            log.error("Facade - 统计总字数失败: contentId={}", contentId, e);
            throw new RuntimeException("统计总字数失败：" + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> getChapterStats(Long contentId) {
        log.info("Facade - 获取章节统计信息: contentId={}", contentId);
        try {
            return contentChapterService.getChapterStats(contentId);
        } catch (Exception e) {
            log.error("Facade - 获取章节统计信息失败: contentId={}", contentId, e);
            throw new RuntimeException("获取章节统计信息失败：" + e.getMessage(), e);
        }
    }

    // =================== 管理功能 ===================

    @Override
    public boolean batchUpdateChapterStatus(List<Long> ids, String status) {
        log.info("Facade - 批量更新章节状态: ids={}, status={}", ids, status);
        try {
            return contentChapterService.batchUpdateChapterStatus(ids, status);
        } catch (Exception e) {
            log.error("Facade - 批量更新章节状态失败: ids={}, status={}", ids, status, e);
            throw new RuntimeException("批量更新章节状态失败：" + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteAllChaptersByContentId(Long contentId) {
        log.info("Facade - 删除内容的所有章节: contentId={}", contentId);
        try {
            return contentChapterService.deleteAllChaptersByContentId(contentId);
        } catch (Exception e) {
            log.error("Facade - 删除内容的所有章节失败: contentId={}", contentId, e);
            throw new RuntimeException("删除内容的所有章节失败：" + e.getMessage(), e);
        }
    }

    @Override
    public boolean reorderChapterNumbers(Long contentId) {
        log.info("Facade - 重新排序章节号: contentId={}", contentId);
        try {
            return contentChapterService.reorderChapterNumbers(contentId);
        } catch (Exception e) {
            log.error("Facade - 重新排序章节号失败: contentId={}", contentId, e);
            throw new RuntimeException("重新排序章节号失败：" + e.getMessage(), e);
        }
    }

    // =================== 私有转换方法 ===================

    /**
     * 将ContentChapter实体转换为ChapterResponse DTO
     */
    private ChapterResponse convertToChapterResponse(ContentChapter chapter) {
        if (chapter == null) {
            return null;
        }
        
        ChapterResponse response = new ChapterResponse();
        BeanUtils.copyProperties(chapter, response);
        return response;
    }

    /**
     * 将ContentChapter实体列表转换为ChapterResponse DTO列表
     */
    private List<ChapterResponse> convertToChapterResponseList(List<ContentChapter> chapters) {
        if (CollectionUtils.isEmpty(chapters)) {
            return List.of();
        }
        
        return chapters.stream()
                .map(this::convertToChapterResponse)
                .collect(Collectors.toList());
    }
} 