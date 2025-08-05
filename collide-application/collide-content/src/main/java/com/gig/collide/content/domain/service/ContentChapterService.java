package com.gig.collide.content.domain.service;

import com.gig.collide.content.domain.entity.ContentChapter;

import java.util.List;

/**
 * 内容章节业务服务接口
 * 极简版 - 8个核心方法，使用通用查询
 * 
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
public interface ContentChapterService {

    // =================== 核心CRUD功能（4个方法）===================

    /**
     * 创建章节
     */
    ContentChapter createChapter(ContentChapter chapter);

    /**
     * 更新章节
     */
    ContentChapter updateChapter(ContentChapter chapter);

    /**
     * 根据ID获取章节
     */
    ContentChapter getChapterById(Long id);

    /**
     * 软删除章节
     */
    boolean deleteChapter(Long id);

    // =================== 万能查询功能（2个方法）===================

    /**
     * 万能条件查询章节列表 - 替代所有具体查询
     * 可实现：getChaptersByContentId, getPublishedChapters, getChaptersByWordCount等
     */
    List<ContentChapter> getChaptersByConditions(Long contentId, String status, 
                                                Integer chapterNumStart, Integer chapterNumEnd,
                                                Integer minWordCount, Integer maxWordCount,
                                                String orderBy, String orderDirection,
                                                Integer currentPage, Integer pageSize);

    /**
     * 章节导航查询（next、previous、first、last）
     */
    ContentChapter getChapterByNavigation(Long contentId, Integer currentChapterNum, String direction);

    // =================== 批量操作功能（2个方法）===================

    /**
     * 批量更新章节状态
     */
    boolean batchUpdateChapterStatus(List<Long> ids, String status);

    /**
     * 批量软删除章节
     */
    boolean batchDeleteChapters(List<Long> ids);
}