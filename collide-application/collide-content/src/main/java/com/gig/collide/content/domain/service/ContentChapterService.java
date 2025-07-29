package com.gig.collide.content.domain.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.content.domain.entity.ContentChapter;

import java.util.List;
import java.util.Map;

/**
 * 内容章节业务逻辑接口 - 简洁版
 * 基于content-simple.sql的t_content_chapter表，实现章节管理业务
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
public interface ContentChapterService {

    // =================== 基础CRUD ===================

    /**
     * 创建章节
     * 包含章节号验证、字数计算、状态初始化
     * 
     * @param chapter 章节对象
     * @return 创建的章节
     */
    ContentChapter createChapter(ContentChapter chapter);

    /**
     * 更新章节
     * 支持内容、标题、状态等更新
     * 
     * @param chapter 章节对象
     * @return 更新后的章节
     */
    ContentChapter updateChapter(ContentChapter chapter);

    /**
     * 删除章节
     * 物理删除，验证删除权限
     * 
     * @param chapterId 章节ID
     * @param authorId 作者ID
     * @return 是否删除成功
     */
    boolean deleteChapter(Long chapterId, Long authorId);

    /**
     * 根据ID获取章节
     * 
     * @param id 章节ID
     * @return 章节对象
     */
    ContentChapter getChapterById(Long id);

    /**
     * 根据内容ID和章节号获取章节
     * 
     * @param contentId 内容ID
     * @param chapterNum 章节号
     * @return 章节对象
     */
    ContentChapter getChapterByNum(Long contentId, Integer chapterNum);

    // =================== 查询功能 ===================

    /**
     * 分页查询内容的章节列表
     * 
     * @param page 分页对象
     * @param contentId 内容ID
     * @param status 章节状态
     * @return 分页结果
     */
    Page<ContentChapter> getChaptersByContentId(Page<ContentChapter> page, Long contentId, String status);

    /**
     * 获取内容的所有章节（不分页）
     * 
     * @param contentId 内容ID
     * @param status 章节状态
     * @return 章节列表
     */
    List<ContentChapter> getAllChaptersByContentId(Long contentId, String status);

    /**
     * 获取下一章节
     * 
     * @param contentId 内容ID
     * @param currentChapterNum 当前章节号
     * @return 下一章节
     */
    ContentChapter getNextChapter(Long contentId, Integer currentChapterNum);

    /**
     * 获取上一章节
     * 
     * @param contentId 内容ID
     * @param currentChapterNum 当前章节号
     * @return 上一章节
     */
    ContentChapter getPreviousChapter(Long contentId, Integer currentChapterNum);

    /**
     * 根据标题搜索章节
     * 
     * @param page 分页对象
     * @param contentId 内容ID
     * @param keyword 搜索关键词
     * @param status 章节状态
     * @return 搜索结果
     */
    Page<ContentChapter> searchChaptersByTitle(Page<ContentChapter> page, Long contentId, 
                                              String keyword, String status);

    // =================== 状态管理 ===================

    /**
     * 发布章节
     * 验证发布条件和权限
     * 
     * @param chapterId 章节ID
     * @param authorId 作者ID
     * @return 发布后的章节
     */
    ContentChapter publishChapter(Long chapterId, Long authorId);

    /**
     * 批量发布章节
     * 
     * @param contentId 内容ID
     * @param chapterNums 章节号列表
     * @param authorId 作者ID
     * @return 发布成功的数量
     */
    Integer batchPublishChapters(Long contentId, List<Integer> chapterNums, Long authorId);

    /**
     * 设置章节为草稿
     * 
     * @param chapterId 章节ID
     * @param authorId 作者ID
     * @return 是否设置成功
     */
    boolean setChapterToDraft(Long chapterId, Long authorId);

    // =================== 章节号管理 ===================

    /**
     * 获取下一个可用章节号
     * 
     * @param contentId 内容ID
     * @return 下一个章节号
     */
    Integer getNextChapterNum(Long contentId);

    /**
     * 检查章节号是否存在
     * 
     * @param contentId 内容ID
     * @param chapterNum 章节号
     * @return 是否存在
     */
    boolean isChapterNumExists(Long contentId, Integer chapterNum);

    /**
     * 重排章节号
     * 
     * @param contentId 内容ID
     * @param fromNum 原章节号
     * @param toNum 目标章节号
     * @param authorId 作者ID
     * @return 是否重排成功
     */
    boolean reorderChapter(Long contentId, Integer fromNum, Integer toNum, Long authorId);

    /**
     * 查找缺失的章节号
     * 
     * @param contentId 内容ID
     * @return 缺失的章节号列表
     */
    List<Integer> findMissingChapterNums(Long contentId);

    /**
     * 查找重复的章节号
     * 
     * @param contentId 内容ID
     * @return 重复的章节号列表
     */
    List<Integer> findDuplicateChapterNums(Long contentId);

    // =================== 统计功能 ===================

    /**
     * 统计内容的章节数量
     * 
     * @param contentId 内容ID
     * @param status 章节状态
     * @return 章节数量
     */
    Long countChaptersByContentId(Long contentId, String status);

    /**
     * 获取内容的总字数
     * 
     * @param contentId 内容ID
     * @param status 章节状态
     * @return 总字数
     */
    Long getTotalWordCount(Long contentId, String status);

    /**
     * 获取已发布章节统计
     * 
     * @param contentId 内容ID
     * @return 统计信息
     */
    Map<String, Object> getPublishedChapterStats(Long contentId);

    /**
     * 获取最新章节号
     * 
     * @param contentId 内容ID
     * @return 最新章节号
     */
    Integer getMaxChapterNum(Long contentId);

    // =================== 批量操作 ===================

    /**
     * 批量删除章节
     * 
     * @param contentId 内容ID
     * @param chapterNums 章节号列表
     * @param authorId 作者ID
     * @return 删除成功的数量
     */
    Integer batchDeleteChapters(Long contentId, List<Integer> chapterNums, Long authorId);

    /**
     * 删除内容的所有章节
     * 
     * @param contentId 内容ID
     * @param authorId 作者ID
     * @return 删除成功的数量
     */
    Integer deleteAllChaptersByContentId(Long contentId, Long authorId);

    /**
     * 批量更新章节状态
     * 
     * @param contentId 内容ID
     * @param chapterNums 章节号列表
     * @param status 新状态
     * @param authorId 作者ID
     * @return 更新成功的数量
     */
    Integer batchUpdateChapterStatus(Long contentId, List<Integer> chapterNums, 
                                   String status, Long authorId);

    // =================== 业务验证 ===================

    /**
     * 验证章节数据
     * 
     * @param chapter 章节对象
     * @return 是否有效
     */
    boolean validateChapter(ContentChapter chapter);

    /**
     * 检查章节发布权限
     * 
     * @param chapterId 章节ID
     * @param authorId 作者ID
     * @return 是否有权限
     */
    boolean canPublishChapter(Long chapterId, Long authorId);

    /**
     * 检查章节编辑权限
     * 
     * @param chapterId 章节ID
     * @param userId 用户ID
     * @return 是否有权限
     */
    boolean canEditChapter(Long chapterId, Long userId);

    /**
     * 检查章节删除权限
     * 
     * @param chapterId 章节ID
     * @param userId 用户ID
     * @return 是否有权限
     */
    boolean canDeleteChapter(Long chapterId, Long userId);

    // =================== 高级功能 ===================

    /**
     * 获取字数范围内的章节
     * 
     * @param contentId 内容ID
     * @param minWordCount 最小字数
     * @param maxWordCount 最大字数
     * @return 章节列表
     */
    List<ContentChapter> getChaptersByWordCountRange(Long contentId, Integer minWordCount, Integer maxWordCount);

    /**
     * 获取草稿章节列表
     * 
     * @param contentId 内容ID
     * @return 草稿章节列表
     */
    List<ContentChapter> getDraftChapters(Long contentId);

    /**
     * 获取已发布章节列表
     * 
     * @param contentId 内容ID
     * @return 已发布章节列表
     */
    List<ContentChapter> getPublishedChapters(Long contentId);

    /**
     * 自动计算章节字数
     * 
     * @param chapterId 章节ID
     * @return 计算后的字数
     */
    Integer calculateChapterWordCount(Long chapterId);

    /**
     * 批量计算字数
     * 
     * @param contentId 内容ID
     * @return 更新成功的章节数量
     */
    Integer batchCalculateWordCount(Long contentId);

    /**
     * 验证章节顺序完整性
     * 
     * @param contentId 内容ID
     * @return 验证结果Map（是否完整、缺失章节等）
     */
    Map<String, Object> validateChapterSequence(Long contentId);
}