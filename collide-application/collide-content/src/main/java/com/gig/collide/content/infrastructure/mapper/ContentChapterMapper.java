package com.gig.collide.content.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.content.domain.entity.ContentChapter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.*;

/**
 * 内容章节表数据映射接口
 * 用于小说、漫画等多章节内容的管理
 *
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
@Mapper
public interface ContentChapterMapper extends BaseMapper<ContentChapter> {

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
     * 批量更新章节状态
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") String status);

    /**
     * 删除内容的所有章节
     */
    int deleteByContentId(@Param("contentId") Long contentId);

    /**
     * 统计内容的章节总数
     */
    Long countByContentId(@Param("contentId") Long contentId, @Param("status") String status);

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
    Object getChapterStats(@Param("contentId") Long contentId);

    /**
     * 查询最新更新的章节
     */
    List<ContentChapter> selectLatestChapters(@Param("limit") Integer limit);

    /**
     * 重新排序章节号（用于章节删除后的重新编号）
     */
    int reorderChapterNumbers(@Param("contentId") Long contentId);

    // =================== 补充缺失的方法 ===================

    /**
     * 分页查询章节
     */
    List<ContentChapter> findByContentId(@Param("page") com.baomidou.mybatisplus.extension.plugins.pagination.Page<ContentChapter> page,
                                        @Param("contentId") Long contentId,
                                        @Param("status") String status);

    /**
     * 查询章节列表
     */
    List<ContentChapter> getChaptersByContentId(@Param("contentId") Long contentId,
                                               @Param("status") String status);

    /**
     * 查询下一章节
     */
    ContentChapter findNextChapter(@Param("contentId") Long contentId,
                                  @Param("currentChapterNum") Integer currentChapterNum);

    /**
     * 查询上一章节
     */
    ContentChapter findPreviousChapter(@Param("contentId") Long contentId,
                                      @Param("currentChapterNum") Integer currentChapterNum);

    /**
     * 按标题搜索章节
     */
    List<ContentChapter> searchByTitle(@Param("page") com.baomidou.mybatisplus.extension.plugins.pagination.Page<ContentChapter> page,
                                      @Param("contentId") Long contentId,
                                      @Param("keyword") String keyword,
                                      @Param("status") String status);

    /**
     * 发布章节
     */
    int publishChapter(@Param("chapterId") Long chapterId);

    /**
     * 批量发布章节
     */
    int batchPublishChapters(@Param("contentId") Long contentId,
                            @Param("chapterNums") List<Integer> chapterNums);

    /**
     * 设置为草稿
     */
    int setChapterToDraft(@Param("chapterId") Long chapterId);

    /**
     * 获取最大章节号
     */
    Integer getMaxChapterNum(@Param("contentId") Long contentId);

    /**
     * 检查章节号是否存在
     */
    boolean existsByContentIdAndChapterNum(@Param("contentId") Long contentId,
                                          @Param("chapterNum") Integer chapterNum);

    /**
     * 向后移动章节
     */
    int moveChapterBackward(@Param("contentId") Long contentId,
                           @Param("fromNum") Integer fromNum,
                           @Param("toNum") Integer toNum);

    /**
     * 向前移动章节
     */
    int moveChapterForward(@Param("contentId") Long contentId,
                          @Param("fromNum") Integer fromNum,
                          @Param("toNum") Integer toNum);

    /**
     * 查找缺失的章节号
     */
    List<Integer> findMissingChapterNums(@Param("contentId") Long contentId,
                                        @Param("maxNum") Integer maxNum);

    /**
     * 查找重复的章节号
     */
    List<Integer> findDuplicateChapterNums(@Param("contentId") Long contentId);

    /**
     * 获取总字数
     */
    Long getTotalWordCount(@Param("contentId") Long contentId,
                          @Param("status") String status);

    /**
     * 获取发布章节统计
     */
    Map<String, Object> getPublishedChapterStats(@Param("contentId") Long contentId);

    /**
     * 批量删除章节
     */
    int batchDeleteChapters(@Param("contentId") Long contentId,
                           @Param("chapterNums") List<Integer> chapterNums);

    /**
     * 按字数范围查找
     */
    List<ContentChapter> findByWordCountRange(@Param("contentId") Long contentId,
                                             @Param("minWordCount") Integer minWordCount,
                                             @Param("maxWordCount") Integer maxWordCount);

    /**
     * 查找草稿章节
     */
    List<ContentChapter> findDraftChapters(@Param("contentId") Long contentId);

    /**
     * 查找已发布章节
     */
    List<ContentChapter> findPublishedChapters(@Param("contentId") Long contentId);
}