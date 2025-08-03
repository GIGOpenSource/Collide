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

    // =================== C端必需的基础查询方法 ===================

    /**
     * 根据内容ID查询章节列表（按章节号排序）
     */
    List<ContentChapter> selectByContentId(@Param("contentId") Long contentId);

    /**
     * 根据内容ID查询已发布章节列表
     */
    List<ContentChapter> selectPublishedByContentId(@Param("contentId") Long contentId);

    /**
     * 根据内容ID分页查询章节
     */
    List<ContentChapter> selectByContentIdPaged(@Param("contentId") Long contentId,
                                               @Param("offset") Long offset,
                                               @Param("limit") Integer limit);

    /**
     * 根据内容ID和章节号查询章节
     */
    ContentChapter selectByContentIdAndChapterNum(@Param("contentId") Long contentId,
                                                 @Param("chapterNum") Integer chapterNum);

    /**
     * 查询内容的下一章节
     */
    ContentChapter selectNextChapter(@Param("contentId") Long contentId,
                                   @Param("currentChapterNum") Integer currentChapterNum);

    /**
     * 查询内容的上一章节
     */
    ContentChapter selectPreviousChapter(@Param("contentId") Long contentId,
                                       @Param("currentChapterNum") Integer currentChapterNum);

    /**
     * 查询内容的第一章节
     */
    ContentChapter selectFirstChapter(@Param("contentId") Long contentId);

    /**
     * 查询内容的最后一章节
     */
    ContentChapter selectLastChapter(@Param("contentId") Long contentId);

    /**
     * 根据状态查询章节列表
     */
    List<ContentChapter> selectByStatus(@Param("status") String status);

    /**
     * 根据章节标题搜索
     */
    List<ContentChapter> selectByTitleLike(@Param("titleKeyword") String titleKeyword);

    /**
     * 根据内容ID和字数范围查询章节
     */
    List<ContentChapter> selectByContentIdAndWordCountRange(@Param("contentId") Long contentId,
                                                           @Param("minWordCount") Integer minWordCount,
                                                           @Param("maxWordCount") Integer maxWordCount);

    /**
     * 查询字数最多的章节
     */
    ContentChapter selectMaxWordCountChapter(@Param("contentId") Long contentId);

    /**
     * 查询指定内容的最新章节
     */
    ContentChapter selectLatestChapterByContentId(@Param("contentId") Long contentId);

    /**
     * 查询最新更新的章节
     */
    List<ContentChapter> selectLatestChapters(@Param("limit") Integer limit);

    // =================== C端必需的统计方法 ===================

    /**
     * 统计内容的章节总数
     */
    Long countByContentId(@Param("contentId") Long contentId);

    /**
     * 统计内容的已发布章节数
     */
    Long countPublishedByContentId(@Param("contentId") Long contentId);

    /**
     * 统计内容的总字数
     */
    Long countTotalWordsByContentId(@Param("contentId") Long contentId);

    /**
     * 获取内容的章节统计信息
     */
    Map<String, Object> getChapterStats(@Param("contentId") Long contentId);

    // =================== C端必需的管理方法 ===================

    /**
     * 批量更新章节状态
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") String status);

    /**
     * 删除内容的所有章节
     */
    int deleteByContentId(@Param("contentId") Long contentId);

    /**
     * 重新排序章节号（用于章节删除后的重新编号）
     */
    int reorderChapterNumbers(@Param("contentId") Long contentId);
}