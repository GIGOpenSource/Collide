package com.gig.collide.api.content;

import com.gig.collide.api.content.request.ChapterCreateRequest;
import com.gig.collide.api.content.response.ChapterResponse;
import com.gig.collide.base.response.PageResponse;

import java.util.List;
import java.util.Map;

/**
 * 内容章节外观服务接口 - C端简洁版
 * 专注于C端必需的章节查询功能，移除复杂的管理接口
 * 基于单表无连表设计
 *
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
public interface ContentChapterFacadeService {

    // =================== 基础查询功能 ===================

    /**
     * 根据内容ID查询章节列表（按章节号排序）
     * 
     * @param contentId 内容ID
     * @return 章节列表
     */
    List<ChapterResponse> getChaptersByContentId(Long contentId);

    /**
     * 根据内容ID查询已发布章节列表
     * 
     * @param contentId 内容ID
     * @return 已发布章节列表
     */
    List<ChapterResponse> getPublishedChaptersByContentId(Long contentId);

    /**
     * 根据内容ID分页查询章节
     * 
     * @param contentId 内容ID
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 分页章节列表
     */
    PageResponse<ChapterResponse> getChaptersByContentIdPaged(Long contentId, Integer currentPage, Integer pageSize);

    /**
     * 根据内容ID和章节号查询章节
     * 
     * @param contentId 内容ID
     * @param chapterNum 章节号
     * @return 章节详情
     */
    ChapterResponse getChapterByContentIdAndNum(Long contentId, Integer chapterNum);

    /**
     * 查询内容的下一章节
     * 
     * @param contentId 内容ID
     * @param currentChapterNum 当前章节号
     * @return 下一章节
     */
    ChapterResponse getNextChapter(Long contentId, Integer currentChapterNum);

    /**
     * 查询内容的上一章节
     * 
     * @param contentId 内容ID
     * @param currentChapterNum 当前章节号
     * @return 上一章节
     */
    ChapterResponse getPreviousChapter(Long contentId, Integer currentChapterNum);

    /**
     * 查询内容的第一章节
     * 
     * @param contentId 内容ID
     * @return 第一章节
     */
    ChapterResponse getFirstChapter(Long contentId);

    /**
     * 查询内容的最后一章节
     * 
     * @param contentId 内容ID
     * @return 最后一章节
     */
    ChapterResponse getLastChapter(Long contentId);

    /**
     * 根据状态查询章节列表
     * 
     * @param status 章节状态
     * @return 章节列表
     */
    List<ChapterResponse> getChaptersByStatus(String status);

    /**
     * 根据章节标题搜索
     * 
     * @param titleKeyword 标题关键词
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 分页搜索结果
     */
    PageResponse<ChapterResponse> searchChaptersByTitle(String titleKeyword, Integer currentPage, Integer pageSize);

    /**
     * 根据内容ID和字数范围查询章节
     * 
     * @param contentId 内容ID
     * @param minWordCount 最小字数
     * @param maxWordCount 最大字数
     * @return 章节列表
     */
    List<ChapterResponse> getChaptersByWordCountRange(Long contentId, Integer minWordCount, Integer maxWordCount);

    /**
     * 查询字数最多的章节
     * 
     * @param contentId 内容ID
     * @return 字数最多的章节
     */
    ChapterResponse getMaxWordCountChapter(Long contentId);

    /**
     * 查询指定内容的最新章节
     * 
     * @param contentId 内容ID
     * @return 最新章节
     */
    ChapterResponse getLatestChapterByContentId(Long contentId);

    /**
     * 查询最新更新的章节
     * 
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 分页最新章节列表
     */
    PageResponse<ChapterResponse> getLatestChapters(Integer currentPage, Integer pageSize);

    // =================== 统计功能 ===================

    /**
     * 统计内容的章节总数
     * 
     * @param contentId 内容ID
     * @return 章节总数
     */
    Long countChaptersByContentId(Long contentId);

    /**
     * 统计内容的已发布章节数
     * 
     * @param contentId 内容ID
     * @return 已发布章节数
     */
    Long countPublishedChaptersByContentId(Long contentId);

    /**
     * 统计内容的总字数
     * 
     * @param contentId 内容ID
     * @return 总字数
     */
    Long countTotalWordsByContentId(Long contentId);

    /**
     * 获取内容的章节统计信息
     * 
     * @param contentId 内容ID
     * @return 统计信息
     */
    Map<String, Object> getChapterStats(Long contentId);

    // =================== 管理功能 ===================

    /**
     * 批量更新章节状态
     * 
     * @param ids 章节ID列表
     * @param status 新状态
     * @return 是否成功
     */
    boolean batchUpdateChapterStatus(List<Long> ids, String status);

    /**
     * 删除内容的所有章节
     * 
     * @param contentId 内容ID
     * @return 是否成功
     */
    boolean deleteAllChaptersByContentId(Long contentId);

    /**
     * 重新排序章节号（用于章节删除后的重新编号）
     * 
     * @param contentId 内容ID
     * @return 是否成功
     */
    boolean reorderChapterNumbers(Long contentId);
}
