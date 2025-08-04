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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 内容章节门面服务实现类
 * 提供章节查询、统计和管理功能的API接口
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

    // =================== 基础查询功能 ===================

    @Override
    public List<ChapterResponse> getChaptersByContentId(Long contentId) {
        try {
            log.debug("获取章节列表: contentId={}", contentId);
            
            List<ContentChapter> chapters = contentChapterService.getChaptersByContentId(contentId);
            
            if (CollectionUtils.isEmpty(chapters)) {
                return Collections.emptyList();
            }
            
            return chapters.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取章节列表失败: contentId={}", contentId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<ChapterResponse> getPublishedChaptersByContentId(Long contentId) {
        try {
            log.debug("获取已发布章节列表: contentId={}", contentId);
            
            List<ContentChapter> chapters = contentChapterService.getPublishedChaptersByContentId(contentId);
            
            if (CollectionUtils.isEmpty(chapters)) {
                return Collections.emptyList();
            }
            
            return chapters.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取已发布章节列表失败: contentId={}", contentId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public PageResponse<ChapterResponse> getChaptersByContentIdPaged(Long contentId, Integer currentPage, Integer pageSize) {
        try {
            log.debug("分页获取章节列表: contentId={}, currentPage={}, pageSize={}", contentId, currentPage, pageSize);
            
            List<ContentChapter> chapters = contentChapterService.getChaptersByContentIdPaged(contentId, currentPage, pageSize);
            
            // 构建分页响应
            PageResponse<ChapterResponse> pageResponse = new PageResponse<>();
            
            if (CollectionUtils.isEmpty(chapters)) {
                pageResponse.setDatas(Collections.emptyList());
                pageResponse.setTotal(0L);
            } else {
                List<ChapterResponse> responseList = chapters.stream()
                        .map(this::convertToResponse)
                        .collect(Collectors.toList());
                pageResponse.setDatas(responseList);
                // 注意：由于Service层只返回List，这里需要单独查询总数
                Long total = contentChapterService.countChaptersByContentId(contentId);
                pageResponse.setTotal(total);
            }
            
            pageResponse.setCurrentPage(currentPage);
            pageResponse.setPageSize(pageSize);
            pageResponse.setTotalPage((int) Math.ceil((double) pageResponse.getTotal() / pageSize));
            pageResponse.setSuccess(true);
            
            return pageResponse;
        } catch (Exception e) {
            log.error("分页获取章节列表失败: contentId={}", contentId, e);
            return createEmptyPageResponse(currentPage, pageSize);
        }
    }

    @Override
    public ChapterResponse getChapterByContentIdAndNum(Long contentId, Integer chapterNum) {
        try {
            ContentChapter chapter = contentChapterService.getChapterByContentIdAndNum(contentId, chapterNum);
            return chapter != null ? convertToResponse(chapter) : null;
        } catch (Exception e) {
            log.error("获取章节详情失败: contentId={}, chapterNum={}", contentId, chapterNum, e);
            return null;
        }
    }

    @Override
    public ChapterResponse getNextChapter(Long contentId, Integer currentChapterNum) {
        try {
            ContentChapter chapter = contentChapterService.getNextChapter(contentId, currentChapterNum);
            return chapter != null ? convertToResponse(chapter) : null;
        } catch (Exception e) {
            log.error("获取下一章节失败: contentId={}, currentChapterNum={}", contentId, currentChapterNum, e);
            return null;
        }
    }

    @Override
    public ChapterResponse getPreviousChapter(Long contentId, Integer currentChapterNum) {
        try {
            ContentChapter chapter = contentChapterService.getPreviousChapter(contentId, currentChapterNum);
            return chapter != null ? convertToResponse(chapter) : null;
        } catch (Exception e) {
            log.error("获取上一章节失败: contentId={}, currentChapterNum={}", contentId, currentChapterNum, e);
            return null;
        }
    }

    @Override
    public ChapterResponse getFirstChapter(Long contentId) {
        try {
            ContentChapter chapter = contentChapterService.getFirstChapter(contentId);
            return chapter != null ? convertToResponse(chapter) : null;
        } catch (Exception e) {
            log.error("获取第一章节失败: contentId={}", contentId, e);
            return null;
        }
    }

    @Override
    public ChapterResponse getLastChapter(Long contentId) {
        try {
            ContentChapter chapter = contentChapterService.getLastChapter(contentId);
            return chapter != null ? convertToResponse(chapter) : null;
        } catch (Exception e) {
            log.error("获取最后一章节失败: contentId={}", contentId, e);
            return null;
        }
    }

    @Override
    public List<ChapterResponse> getChaptersByStatus(String status) {
        try {
            List<ContentChapter> chapters = contentChapterService.getChaptersByStatus(status);
            if (CollectionUtils.isEmpty(chapters)) {
                return Collections.emptyList();
            }
            return chapters.stream().map(this::convertToResponse).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("根据状态获取章节列表失败: status={}", status, e);
            return Collections.emptyList();
        }
    }

    @Override
    public PageResponse<ChapterResponse> searchChaptersByTitle(String titleKeyword, Integer currentPage, Integer pageSize) {
        try {
            List<ContentChapter> chapters = contentChapterService.searchChaptersByTitle(titleKeyword, currentPage, pageSize);
            PageResponse<ChapterResponse> pageResponse = new PageResponse<>();
            
            if (CollectionUtils.isEmpty(chapters)) {
                pageResponse.setDatas(Collections.emptyList());
                pageResponse.setTotal(0L);
            } else {
                List<ChapterResponse> responseList = chapters.stream()
                        .map(this::convertToResponse)
                        .collect(Collectors.toList());
                pageResponse.setDatas(responseList);
                pageResponse.setTotal((long) chapters.size());
            }
            
            pageResponse.setCurrentPage(currentPage);
            pageResponse.setPageSize(pageSize);
            pageResponse.setTotalPage((int) Math.ceil((double) pageResponse.getTotal() / pageSize));
            pageResponse.setSuccess(true);
            
            return pageResponse;
        } catch (Exception e) {
            log.error("搜索章节失败: titleKeyword={}", titleKeyword, e);
            return createEmptyPageResponse(currentPage, pageSize);
        }
    }

    @Override
    public List<ChapterResponse> getChaptersByWordCountRange(Long contentId, Integer minWordCount, Integer maxWordCount) {
        try {
            List<ContentChapter> chapters = contentChapterService.getChaptersByWordCountRange(contentId, minWordCount, maxWordCount);
            if (CollectionUtils.isEmpty(chapters)) {
                return Collections.emptyList();
            }
            return chapters.stream().map(this::convertToResponse).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("根据字数范围获取章节失败: contentId={}", contentId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public ChapterResponse getMaxWordCountChapter(Long contentId) {
        try {
            ContentChapter chapter = contentChapterService.getMaxWordCountChapter(contentId);
            return chapter != null ? convertToResponse(chapter) : null;
        } catch (Exception e) {
            log.error("获取字数最多的章节失败: contentId={}", contentId, e);
            return null;
        }
    }

    @Override
    public ChapterResponse getLatestChapterByContentId(Long contentId) {
        try {
            ContentChapter chapter = contentChapterService.getLatestChapterByContentId(contentId);
            return chapter != null ? convertToResponse(chapter) : null;
        } catch (Exception e) {
            log.error("获取最新章节失败: contentId={}", contentId, e);
            return null;
        }
    }

    @Override
    public PageResponse<ChapterResponse> getLatestChapters(Integer currentPage, Integer pageSize) {
        try {
            List<ContentChapter> chapters = contentChapterService.getLatestChapters(currentPage, pageSize);
            PageResponse<ChapterResponse> pageResponse = new PageResponse<>();
            
            if (CollectionUtils.isEmpty(chapters)) {
                pageResponse.setDatas(Collections.emptyList());
                pageResponse.setTotal(0L);
            } else {
                List<ChapterResponse> responseList = chapters.stream()
                        .map(this::convertToResponse)
                        .collect(Collectors.toList());
                pageResponse.setDatas(responseList);
                pageResponse.setTotal((long) chapters.size());
            }
            
            pageResponse.setCurrentPage(currentPage);
            pageResponse.setPageSize(pageSize);
            pageResponse.setTotalPage((int) Math.ceil((double) pageResponse.getTotal() / pageSize));
            pageResponse.setSuccess(true);
            
            return pageResponse;
        } catch (Exception e) {
            log.error("获取最新更新的章节失败", e);
            return createEmptyPageResponse(currentPage, pageSize);
        }
    }

    // =================== 统计功能 ===================

    @Override
    public Long countChaptersByContentId(Long contentId) {
        try {
            return contentChapterService.countChaptersByContentId(contentId);
        } catch (Exception e) {
            log.error("统计章节总数失败: contentId={}", contentId, e);
            return 0L;
        }
    }

    @Override
    public Long countPublishedChaptersByContentId(Long contentId) {
        try {
            return contentChapterService.countPublishedChaptersByContentId(contentId);
        } catch (Exception e) {
            log.error("统计已发布章节数失败: contentId={}", contentId, e);
            return 0L;
        }
    }

    @Override
    public Long countTotalWordsByContentId(Long contentId) {
        try {
            return contentChapterService.countTotalWordsByContentId(contentId);
        } catch (Exception e) {
            log.error("统计总字数失败: contentId={}", contentId, e);
            return 0L;
        }
    }

    @Override
    public Map<String, Object> getChapterStats(Long contentId) {
        try {
            return contentChapterService.getChapterStats(contentId);
        } catch (Exception e) {
            log.error("获取章节统计信息失败: contentId={}", contentId, e);
            return Collections.emptyMap();
        }
    }

    // =================== 管理功能 ===================

    @Override
    public boolean batchUpdateChapterStatus(List<Long> ids, String status) {
        try {
            return contentChapterService.batchUpdateChapterStatus(ids, status);
        } catch (Exception e) {
            log.error("批量更新章节状态失败: ids={}, status={}", ids, status, e);
            return false;
        }
    }

    @Override
    public boolean deleteAllChaptersByContentId(Long contentId) {
        try {
            return contentChapterService.deleteAllChaptersByContentId(contentId);
        } catch (Exception e) {
            log.error("删除内容的所有章节失败: contentId={}", contentId, e);
            return false;
        }
    }

    @Override
    public boolean reorderChapterNumbers(Long contentId) {
        try {
            return contentChapterService.reorderChapterNumbers(contentId);
        } catch (Exception e) {
            log.error("重新排序章节号失败: contentId={}", contentId, e);
            return false;
        }
    }

    // =================== 私有辅助方法 ===================

    private ChapterResponse convertToResponse(ContentChapter chapter) {
        ChapterResponse response = new ChapterResponse();
        BeanUtils.copyProperties(chapter, response);
        return response;
    }

    private PageResponse<ChapterResponse> createEmptyPageResponse(Integer currentPage, Integer pageSize) {
        PageResponse<ChapterResponse> pageResponse = new PageResponse<>();
        pageResponse.setDatas(Collections.emptyList());
        pageResponse.setTotal(0L);
        pageResponse.setCurrentPage(currentPage);
        pageResponse.setPageSize(pageSize);
        pageResponse.setTotalPage(0);
        pageResponse.setSuccess(true);
        return pageResponse;
    }
}