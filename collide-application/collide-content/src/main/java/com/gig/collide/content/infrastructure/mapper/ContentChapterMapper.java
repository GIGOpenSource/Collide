package com.gig.collide.content.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.content.domain.entity.ContentChapter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 内容章节表数据映射接口
 * 专注于C端必需的章节查询功能
 *
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
@Mapper
public interface ContentChapterMapper extends BaseMapper<ContentChapter> {

    // =================== C端必需的通用查询方法 ===================

    /**
     * 通用条件查询章节列表
     * @param contentId 内容ID（可选）
     * @param status 状态（可选）
     * @param chapterNumStart 章节号开始范围（可选）
     * @param chapterNumEnd 章节号结束范围（可选）
     * @param minWordCount 最小字数（可选）
     * @param maxWordCount 最大字数（可选）
     * @param orderBy 排序字段（可选：chapterNum、createTime、updateTime、wordCount）
     * @param orderDirection 排序方向（可选：ASC、DESC）
     * @param currentPage 当前页码（可选，不分页时传null）
     * @param pageSize 页面大小（可选，不分页时传null）
     */
    List<ContentChapter> selectChaptersByConditions(@Param("contentId") Long contentId,
                                                   @Param("status") String status,
                                                   @Param("chapterNumStart") Integer chapterNumStart,
                                                   @Param("chapterNumEnd") Integer chapterNumEnd,
                                                   @Param("minWordCount") Integer minWordCount,
                                                   @Param("maxWordCount") Integer maxWordCount,
                                                   @Param("orderBy") String orderBy,
                                                   @Param("orderDirection") String orderDirection,
                                                   @Param("currentPage") Integer currentPage,
                                                   @Param("pageSize") Integer pageSize);

    /**
     * 章节导航查询
     * @param contentId 内容ID
     * @param currentChapterNum 当前章节号
     * @param direction 导航方向（next、previous、first、last）
     */
    ContentChapter selectChapterByNavigation(@Param("contentId") Long contentId,
                                           @Param("currentChapterNum") Integer currentChapterNum,
                                           @Param("direction") String direction);

    /**
     * 搜索章节
     * @param keyword 搜索关键词（章节标题、内容）
     * @param contentId 内容ID（可选）
     * @param status 状态（可选）
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     */
    List<ContentChapter> searchChapters(@Param("keyword") String keyword,
                                       @Param("contentId") Long contentId,
                                       @Param("status") String status,
                                       @Param("currentPage") Integer currentPage,
                                       @Param("pageSize") Integer pageSize);

    // =================== C端必需的CRUD操作方法 ===================

    /**
     * 更新章节状态
     */
    int updateChapterStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 更新章节基本信息
     */
    int updateChapterInfo(@Param("id") Long id,
                         @Param("title") String title,
                         @Param("content") String content,
                         @Param("wordCount") Integer wordCount);

    /**
     * 批量更新章节状态
     */
    int batchUpdateChapterStatus(@Param("ids") List<Long> ids, @Param("status") String status);

    /**
     * 软删除章节
     */
    int softDeleteChapter(@Param("id") Long id);

    /**
     * 批量软删除章节
     */
    int batchSoftDeleteChapters(@Param("ids") List<Long> ids);

    /**
     * 删除内容的所有章节
     */
    int deleteAllChaptersByContentId(@Param("contentId") Long contentId);
}