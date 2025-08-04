package com.gig.collide.content.domain.service.impl;

import com.gig.collide.content.domain.entity.ContentChapter;
import com.gig.collide.content.domain.service.ContentChapterService;
import com.gig.collide.content.infrastructure.mapper.ContentChapterMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 内容章节业务服务实现 - C端简洁版
 * 专注于C端必需的章节查询功能，移除复杂的管理接口
 * 基于单表无连表设计
 * 
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentChapterServiceImpl implements ContentChapterService {

    private final ContentChapterMapper contentChapterMapper;

    // =================== 基础查询功能 ===================

    @Override
    public List<ContentChapter> getChaptersByContentId(Long contentId) {
        log.info("获取章节列表: contentId={}", contentId);
        
        // 参数验证
        if (contentId == null) {
            throw new IllegalArgumentException("内容ID不能为空");
        }
        
        List<ContentChapter> chapters = contentChapterMapper.selectByContentId(contentId);
        
        log.info("获取章节列表成功: contentId={}, 数量={}", contentId, chapters.size());
        return chapters;
    }

    @Override
    public List<ContentChapter> getPublishedChaptersByContentId(Long contentId) {
        log.info("获取已发布章节列表: contentId={}", contentId);
        
        // 参数验证
        if (contentId == null) {
            throw new IllegalArgumentException("内容ID不能为空");
        }
        
        List<ContentChapter> chapters = contentChapterMapper.selectPublishedByContentId(contentId);
        
        log.info("获取已发布章节列表成功: contentId={}, 数量={}", contentId, chapters.size());
        return chapters;
    }

    @Override
    public List<ContentChapter> getChaptersByContentIdPaged(Long contentId, Integer currentPage, Integer pageSize) {
        log.info("分页获取章节列表: contentId={}, currentPage={}, pageSize={}", contentId, currentPage, pageSize);
        
        // 参数验证
        if (contentId == null) {
            throw new IllegalArgumentException("内容ID不能为空");
        }
        
        // 设置默认值
        currentPage = currentPage == null ? 1 : currentPage;
        pageSize = pageSize == null ? 20 : pageSize;
        
        // 计算偏移量
        Long offset = (long) ((currentPage - 1) * pageSize);
        
        List<ContentChapter> chapters = contentChapterMapper.selectByContentIdPaged(contentId, currentPage, pageSize);
        
        log.info("分页获取章节列表成功: contentId={}, 数量={}", contentId, chapters.size());
        return chapters;
    }

    @Override
    public ContentChapter getChapterByContentIdAndNum(Long contentId, Integer chapterNum) {
        log.info("获取章节详情: contentId={}, chapterNum={}", contentId, chapterNum);
        
        // 参数验证
        if (contentId == null || chapterNum == null) {
            throw new IllegalArgumentException("内容ID和章节号不能为空");
        }
        
        ContentChapter chapter = contentChapterMapper.selectByContentIdAndChapterNum(contentId, chapterNum);
        
        log.info("获取章节详情成功: contentId={}, chapterNum={}", contentId, chapterNum);
        return chapter;
    }

    @Override
    public ContentChapter getNextChapter(Long contentId, Integer currentChapterNum) {
        log.info("获取下一章节: contentId={}, currentChapterNum={}", contentId, currentChapterNum);
        
        // 参数验证
        if (contentId == null || currentChapterNum == null) {
            throw new IllegalArgumentException("内容ID和当前章节号不能为空");
        }
        
        ContentChapter chapter = contentChapterMapper.selectNextChapter(contentId, currentChapterNum);
        
        log.info("获取下一章节成功: contentId={}, currentChapterNum={}", contentId, currentChapterNum);
        return chapter;
    }

    @Override
    public ContentChapter getPreviousChapter(Long contentId, Integer currentChapterNum) {
        log.info("获取上一章节: contentId={}, currentChapterNum={}", contentId, currentChapterNum);
        
        // 参数验证
        if (contentId == null || currentChapterNum == null) {
            throw new IllegalArgumentException("内容ID和当前章节号不能为空");
        }
        
        ContentChapter chapter = contentChapterMapper.selectPreviousChapter(contentId, currentChapterNum);
        
        log.info("获取上一章节成功: contentId={}, currentChapterNum={}", contentId, currentChapterNum);
        return chapter;
    }

    @Override
    public ContentChapter getFirstChapter(Long contentId) {
        log.info("获取第一章节: contentId={}", contentId);
        
        // 参数验证
        if (contentId == null) {
            throw new IllegalArgumentException("内容ID不能为空");
        }
        
        ContentChapter chapter = contentChapterMapper.selectFirstChapter(contentId);
        
        log.info("获取第一章节成功: contentId={}", contentId);
        return chapter;
    }

    @Override
    public ContentChapter getLastChapter(Long contentId) {
        log.info("获取最后一章节: contentId={}", contentId);
        
        // 参数验证
        if (contentId == null) {
            throw new IllegalArgumentException("内容ID不能为空");
        }
        
        ContentChapter chapter = contentChapterMapper.selectLastChapter(contentId);
        
        log.info("获取最后一章节成功: contentId={}", contentId);
        return chapter;
    }

    @Override
    public List<ContentChapter> getChaptersByStatus(String status) {
        log.info("根据状态获取章节列表: status={}", status);
        
        // 参数验证
        if (!StringUtils.hasText(status)) {
            throw new IllegalArgumentException("章节状态不能为空");
        }
        
        List<ContentChapter> chapters = contentChapterMapper.selectByStatus(status);
        
        log.info("根据状态获取章节列表成功: status={}, 数量={}", status, chapters.size());
        return chapters;
    }

    @Override
    public List<ContentChapter> searchChaptersByTitle(String titleKeyword, Integer currentPage, Integer pageSize) {
        log.info("搜索章节: titleKeyword={}, currentPage={}, pageSize={}", titleKeyword, currentPage, pageSize);
        
        // 参数验证
        if (!StringUtils.hasText(titleKeyword)) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }
        
        // 设置默认值
        currentPage = currentPage == null ? 1 : currentPage;
        pageSize = pageSize == null ? 20 : pageSize;
        
        // 计算偏移量
        Long offset = (long) ((currentPage - 1) * pageSize);
        
        List<ContentChapter> chapters = contentChapterMapper.searchChaptersByTitle(titleKeyword, currentPage, pageSize);
        
        log.info("搜索章节成功: titleKeyword={}, 数量={}", titleKeyword, chapters.size());
        return chapters;
    }

    @Override
    public List<ContentChapter> getChaptersByWordCountRange(Long contentId, Integer minWordCount, Integer maxWordCount) {
        log.info("根据字数范围获取章节: contentId={}, minWordCount={}, maxWordCount={}", contentId, minWordCount, maxWordCount);
        
        // 参数验证
        if (contentId == null) {
            throw new IllegalArgumentException("内容ID不能为空");
        }
        
        List<ContentChapter> chapters = contentChapterMapper.selectByContentIdAndWordCountRange(contentId, minWordCount, maxWordCount);
        
        log.info("根据字数范围获取章节成功: contentId={}, 数量={}", contentId, chapters.size());
        return chapters;
    }

    @Override
    public ContentChapter getMaxWordCountChapter(Long contentId) {
        log.info("获取字数最多的章节: contentId={}", contentId);
        
        // 参数验证
        if (contentId == null) {
            throw new IllegalArgumentException("内容ID不能为空");
        }
        
        ContentChapter chapter = contentChapterMapper.selectMaxWordCountChapter(contentId);
        
        log.info("获取字数最多的章节成功: contentId={}", contentId);
        return chapter;
    }

    @Override
    public ContentChapter getLatestChapterByContentId(Long contentId) {
        log.info("获取最新章节: contentId={}", contentId);
        
        // 参数验证
        if (contentId == null) {
            throw new IllegalArgumentException("内容ID不能为空");
        }
        
        ContentChapter chapter = contentChapterMapper.selectLatestChapterByContentId(contentId);
        
        log.info("获取最新章节成功: contentId={}", contentId);
        return chapter;
    }

    @Override
    public List<ContentChapter> getLatestChapters(Integer currentPage, Integer pageSize) {
        log.info("获取最新更新的章节: currentPage={}, pageSize={}", currentPage, pageSize);
        
        // 设置默认值
        currentPage = currentPage == null ? 1 : currentPage;
        pageSize = pageSize == null ? 20 : pageSize;
        
        // 计算偏移量
        Long offset = (long) ((currentPage - 1) * pageSize);
        
        List<ContentChapter> chapters = contentChapterMapper.selectLatestChapters(currentPage, pageSize);
        
        log.info("获取最新更新的章节成功: 数量={}", chapters.size());
        return chapters;
    }

    // =================== 统计功能 ===================

    @Override
    public Long countChaptersByContentId(Long contentId) {
        log.info("统计章节总数: contentId={}", contentId);
        
        // 参数验证
        if (contentId == null) {
            throw new IllegalArgumentException("内容ID不能为空");
        }
        
        Long count = contentChapterMapper.countByContentId(contentId);
        
        log.info("统计章节总数成功: contentId={}, count={}", contentId, count);
        return count != null ? count : 0L;
    }

    @Override
    public Long countPublishedChaptersByContentId(Long contentId) {
        log.info("统计已发布章节数: contentId={}", contentId);
        
        // 参数验证
        if (contentId == null) {
            throw new IllegalArgumentException("内容ID不能为空");
        }
        
        Long count = contentChapterMapper.countPublishedByContentId(contentId);
        
        log.info("统计已发布章节数成功: contentId={}, count={}", contentId, count);
        return count != null ? count : 0L;
    }

    @Override
    public Long countTotalWordsByContentId(Long contentId) {
        log.info("统计总字数: contentId={}", contentId);
        
        // 参数验证
        if (contentId == null) {
            throw new IllegalArgumentException("内容ID不能为空");
        }
        
        Long wordCount = contentChapterMapper.countTotalWordsByContentId(contentId);
        
        log.info("统计总字数成功: contentId={}, wordCount={}", contentId, wordCount);
        return wordCount != null ? wordCount : 0L;
    }

    @Override
    public Map<String, Object> getChapterStats(Long contentId) {
        log.info("获取章节统计信息: contentId={}", contentId);
        
        // 参数验证
        if (contentId == null) {
            throw new IllegalArgumentException("内容ID不能为空");
        }
        
        Map<String, Object> stats = contentChapterMapper.getChapterStats(contentId);
        
        log.info("获取章节统计信息成功: contentId={}", contentId);
        return stats;
    }

    // =================== 管理功能 ===================

    @Override
    @Transactional
    public boolean batchUpdateChapterStatus(List<Long> ids, String status) {
        log.info("批量更新章节状态: ids={}, status={}", ids, status);
        
        // 参数验证
        if (CollectionUtils.isEmpty(ids)) {
            throw new IllegalArgumentException("章节ID列表不能为空");
        }
        if (!StringUtils.hasText(status)) {
            throw new IllegalArgumentException("章节状态不能为空");
        }
        
        try {
            int result = contentChapterMapper.batchUpdateChapterStatus(ids, status);
            
            log.info("批量更新章节状态成功: 更新数量={}", result);
            return result > 0;
        } catch (Exception e) {
            log.error("批量更新章节状态失败", e);
            throw new RuntimeException("批量更新章节状态失败：" + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean deleteAllChaptersByContentId(Long contentId) {
        log.info("删除内容的所有章节: contentId={}", contentId);
        
        // 参数验证
        if (contentId == null) {
            throw new IllegalArgumentException("内容ID不能为空");
        }
        
        try {
            int result = contentChapterMapper.deleteAllChaptersByContentId(contentId);
            
            log.info("删除内容的所有章节成功: contentId={}, 删除数量={}", contentId, result);
            return result > 0;
        } catch (Exception e) {
            log.error("删除内容的所有章节失败: contentId={}", contentId, e);
            throw new RuntimeException("删除内容的所有章节失败：" + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean reorderChapterNumbers(Long contentId) {
        log.info("重新排序章节号: contentId={}", contentId);
        
        // 参数验证
        if (contentId == null) {
            throw new IllegalArgumentException("内容ID不能为空");
        }
        
        try {
            int result = contentChapterMapper.reorderChapterNumbers(contentId);
            
            log.info("重新排序章节号成功: contentId={}, 更新数量={}", contentId, result);
            return result > 0;
        } catch (Exception e) {
            log.error("重新排序章节号失败: contentId={}", contentId, e);
            throw new RuntimeException("重新排序章节号失败：" + e.getMessage(), e);
        }
    }
}