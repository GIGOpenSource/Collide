package com.gig.collide.content.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.content.domain.entity.ContentChapter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 内容章节数据访问接口 - 简洁版
 * 基于content-simple.sql的t_content_chapter表结构
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Mapper
public interface ContentChapterMapper extends BaseMapper<ContentChapter> {

    // =================== 基础查询 ===================

    /**
     * 根据内容ID查询章节列表
     */
    Page<ContentChapter> findByContentId(Page<ContentChapter> page,
                                        @Param("contentId") Long contentId,
                                        @Param("status") String status);

    /**
     * 获取内容的章节列表（不分页）
     */
    List<ContentChapter> getChaptersByContentId(@Param("contentId") Long contentId,
                                               @Param("status") String status);

    /**
     * 获取下一章节
     */
    ContentChapter findNextChapter(@Param("contentId") Long contentId,
                                  @Param("currentChapterNum") Integer currentChapterNum);

    /**
     * 获取上一章节
     */
    ContentChapter findPreviousChapter(@Param("contentId") Long contentId,
                                      @Param("currentChapterNum") Integer currentChapterNum);

    /**
     * 检查章节号是否存在
     */
    boolean existsByContentIdAndChapterNum(@Param("contentId") Long contentId,
                                          @Param("chapterNum") Integer chapterNum);

    // =================== 状态管理 ===================

    /**
     * 发布章节
     */
    int publishChapter(@Param("id") Long id);

    /**
     * 批量发布章节
     */
    int batchPublishChapters(@Param("contentId") Long contentId,
                            @Param("chapterNums") List<Integer> chapterNums);

    /**
     * 设置章节为草稿
     */
    int setChapterToDraft(@Param("id") Long id);

    // =================== 统计查询 ===================

    /**
     * 获取内容的章节总数
     */
    Long countByContentId(@Param("contentId") Long contentId,
                         @Param("status") String status);

    /**
     * 获取内容的总字数
     */
    Long getTotalWordCount(@Param("contentId") Long contentId,
                          @Param("status") String status);

    /**
     * 获取最新章节号
     */
    Integer getMaxChapterNum(@Param("contentId") Long contentId);

    /**
     * 获取已发布章节统计
     */
    Map<String, Object> getPublishedChapterStats(@Param("contentId") Long contentId);

    // =================== 排序和重排 ===================

    /**
     * 获取章节号范围
     */
    List<Integer> getChapterNumRange(@Param("contentId") Long contentId,
                                    @Param("startNum") Integer startNum,
                                    @Param("endNum") Integer endNum);

    /**
     * 重排章节号（向前移动）
     */
    int moveChapterForward(@Param("contentId") Long contentId,
                          @Param("fromNum") Integer fromNum,
                          @Param("toNum") Integer toNum);

    /**
     * 重排章节号（向后移动）
     */
    int moveChapterBackward(@Param("contentId") Long contentId,
                           @Param("fromNum") Integer fromNum,
                           @Param("toNum") Integer toNum);

    // =================== 批量操作 ===================

    /**
     * 批量删除章节
     */
    int batchDeleteChapters(@Param("contentId") Long contentId,
                           @Param("chapterNums") List<Integer> chapterNums);

    /**
     * 根据内容ID删除所有章节
     */
    int deleteByContentId(@Param("contentId") Long contentId);

    /**
     * 批量更新章节状态
     */
    int batchUpdateStatus(@Param("contentId") Long contentId,
                         @Param("chapterNums") List<Integer> chapterNums,
                         @Param("status") String status);

    // =================== 搜索和过滤 ===================

    /**
     * 根据标题搜索章节
     */
    Page<ContentChapter> searchByTitle(Page<ContentChapter> page,
                                      @Param("contentId") Long contentId,
                                      @Param("keyword") String keyword,
                                      @Param("status") String status);

    /**
     * 获取字数范围内的章节
     */
    List<ContentChapter> findByWordCountRange(@Param("contentId") Long contentId,
                                             @Param("minWordCount") Integer minWordCount,
                                             @Param("maxWordCount") Integer maxWordCount);

    /**
     * 获取草稿章节列表
     */
    List<ContentChapter> findDraftChapters(@Param("contentId") Long contentId);

    /**
     * 获取已发布章节列表
     */
    List<ContentChapter> findPublishedChapters(@Param("contentId") Long contentId);

    // =================== 验证方法 ===================

    /**
     * 验证章节顺序
     */
    List<Integer> findMissingChapterNums(@Param("contentId") Long contentId,
                                        @Param("maxChapterNum") Integer maxChapterNum);

    /**
     * 查找重复章节号
     */
    List<Integer> findDuplicateChapterNums(@Param("contentId") Long contentId);
}