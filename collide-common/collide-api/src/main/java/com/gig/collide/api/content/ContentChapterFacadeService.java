package com.gig.collide.api.content;

import com.gig.collide.api.content.request.ChapterCreateRequest;
import com.gig.collide.api.content.response.ChapterResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import java.util.List;

/**
 * 内容章节外观服务接口 - 极简版
 * 专注于C端必需的章节查询功能，极简8个核心方法
 * 基于万能查询方法设计
 *
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
public interface ContentChapterFacadeService {

    // =================== 核心CRUD功能（2个方法）===================

    /**
     * 根据ID获取章节详情
     */
    Result<ChapterResponse> getChapterById(Long id);

    /**
     * 删除章节（逻辑删除）
     */
    Result<Boolean> deleteChapter(Long id);

    // =================== 万能查询功能（3个方法）===================

    /**
     * 万能条件查询章节列表 - 替代所有具体查询
     * 可实现：getChaptersByContentId, getPublishedChapters, getChaptersByWordCount等
     * 
     * @param contentId 内容ID（可选）
     * @param status 章节状态（可选）
     * @param chapterNumStart 章节号起始（可选）
     * @param chapterNumEnd 章节号结束（可选）
     * @param minWordCount 最小字数（可选）
     * @param maxWordCount 最大字数（可选）
     * @param orderBy 排序字段（可选：chapterNum、createTime、updateTime、wordCount）
     * @param orderDirection 排序方向（可选：ASC、DESC）
     * @param currentPage 当前页码（可选，不分页时传null）
     * @param pageSize 页面大小（可选，不分页时传null）
     * @return 章节列表或分页结果
     */
    Result<PageResponse<ChapterResponse>> getChaptersByConditions(Long contentId, String status, 
                                                                Integer chapterNumStart, Integer chapterNumEnd,
                                                                Integer minWordCount, Integer maxWordCount,
                                                                String orderBy, String orderDirection,
                                                                Integer currentPage, Integer pageSize);

    /**
     * 章节导航查询（next、previous、first、last）
     * 替代：getNextChapter, getPreviousChapter, getFirstChapter, getLastChapter
     */
    Result<ChapterResponse> getChapterByNavigation(Long contentId, Integer currentChapterNum, String direction);

    /**
     * 搜索章节（按标题、内容搜索）
     * 替代：searchChaptersByTitle
     */
    Result<PageResponse<ChapterResponse>> searchChapters(String keyword, Long contentId, String status, 
                                                       Integer currentPage, Integer pageSize);

    // =================== 统计功能（1个方法）===================

    /**
     * 获取章节统计信息 - 替代所有单个统计方法
     * 可返回：总数、已发布数、总字数、平均字数等
     */
    Result<java.util.Map<String, Object>> getChapterStats(Long contentId);

    // =================== 批量操作功能（2个方法）===================

    /**
     * 批量更新章节状态
     */
    Result<Boolean> batchUpdateChapterStatus(List<Long> ids, String status);

    /**
     * 批量删除章节
     */
    Result<Boolean> batchDeleteChapters(List<Long> ids);
}