package com.gig.collide.content.domain.service.impl;

import com.gig.collide.content.domain.entity.ContentChapter;
import com.gig.collide.content.domain.service.ContentChapterService;
import com.gig.collide.content.infrastructure.mapper.ContentChapterMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 内容章节业务服务实现
 * 极简版 - 8个核心方法，使用通用查询
 * 
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ContentChapterServiceImpl implements ContentChapterService {

    private final ContentChapterMapper contentChapterMapper;

    // =================== 核心CRUD功能（4个方法）===================

    @Override
    public ContentChapter createChapter(ContentChapter chapter) {
        log.info("创建章节: contentId={}, title={}", chapter.getContentId(), chapter.getTitle());
        
        // 基础验证
        if (chapter.getContentId() == null || !StringUtils.hasText(chapter.getTitle())) {
            throw new IllegalArgumentException("内容ID和章节标题不能为空");
        }
        
        // 设置默认值
        if (chapter.getCreateTime() == null) {
            chapter.setCreateTime(LocalDateTime.now());
        }
        if (chapter.getUpdateTime() == null) {
            chapter.setUpdateTime(LocalDateTime.now());
        }
        if (!StringUtils.hasText(chapter.getStatus())) {
            chapter.setStatus("DRAFT");
        }
        
        contentChapterMapper.insert(chapter);
        log.info("章节创建成功: id={}", chapter.getId());
        return chapter;
    }

    @Override
    public ContentChapter updateChapter(ContentChapter chapter) {
        log.info("更新章节: id={}", chapter.getId());
        
        if (chapter.getId() == null) {
            throw new IllegalArgumentException("章节ID不能为空");
        }
        
        chapter.setUpdateTime(LocalDateTime.now());
        contentChapterMapper.updateById(chapter);
        
        log.info("章节更新成功: id={}", chapter.getId());
        return chapter;
    }

    @Override
    public ContentChapter getChapterById(Long id) {
        log.debug("获取章节详情: id={}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("章节ID不能为空");
        }
        
        return contentChapterMapper.selectById(id);
    }

    @Override
    public boolean deleteChapter(Long id) {
        log.info("软删除章节: id={}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("章节ID不能为空");
        }
        
        try {
            int result = contentChapterMapper.softDeleteChapter(id);
            boolean success = result > 0;
            if (success) {
                log.info("章节软删除成功: id={}", id);
            }
            return success;
        } catch (Exception e) {
            log.error("章节软删除失败: id={}", id, e);
            return false;
        }
    }

    // =================== 万能查询功能（2个方法）===================

    @Override
    public List<ContentChapter> getChaptersByConditions(Long contentId, String status, 
                                                       Integer chapterNumStart, Integer chapterNumEnd,
                                                       Integer minWordCount, Integer maxWordCount,
                                                       String orderBy, String orderDirection,
                                                       Integer currentPage, Integer pageSize) {
        log.debug("万能条件查询章节: contentId={}, status={}", contentId, status);
        
        return contentChapterMapper.selectChaptersByConditions(
            contentId, status, chapterNumStart, chapterNumEnd,
            minWordCount, maxWordCount, orderBy, orderDirection,
            currentPage, pageSize
        );
    }

    @Override
    public ContentChapter getChapterByNavigation(Long contentId, Integer currentChapterNum, String direction) {
        log.debug("章节导航查询: contentId={}, currentChapterNum={}, direction={}", 
                 contentId, currentChapterNum, direction);
        
        if (contentId == null) {
            throw new IllegalArgumentException("内容ID不能为空");
        }
        
        return contentChapterMapper.selectChapterByNavigation(contentId, currentChapterNum, direction);
    }

    // =================== 批量操作功能（2个方法）===================

    @Override
    public boolean batchUpdateChapterStatus(List<Long> ids, String status) {
        log.info("批量更新章节状态: ids.size={}, status={}", 
                ids != null ? ids.size() : 0, status);
        
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("章节ID列表不能为空");
        }
        if (!StringUtils.hasText(status)) {
            throw new IllegalArgumentException("状态不能为空");
        }
        
        try {
            int result = contentChapterMapper.batchUpdateChapterStatus(ids, status);
            boolean success = result > 0;
            if (success) {
                log.info("批量更新章节状态成功: 影响行数={}", result);
            }
            return success;
        } catch (Exception e) {
            log.error("批量更新章节状态失败", e);
            return false;
        }
    }

    @Override
    public boolean batchDeleteChapters(List<Long> ids) {
        log.info("批量软删除章节: ids.size={}", ids != null ? ids.size() : 0);
        
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("章节ID列表不能为空");
        }
        
        try {
            int result = contentChapterMapper.batchSoftDeleteChapters(ids);
            boolean success = result > 0;
            if (success) {
                log.info("批量软删除章节成功: 影响行数={}", result);
            }
            return success;
        } catch (Exception e) {
            log.error("批量软删除章节失败", e);
            return false;
        }
    }
}