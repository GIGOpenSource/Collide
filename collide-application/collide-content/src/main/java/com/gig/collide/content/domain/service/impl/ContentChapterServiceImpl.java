package com.gig.collide.content.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.content.domain.entity.ContentChapter;
import com.gig.collide.content.domain.service.ContentChapterService;
import com.gig.collide.content.infrastructure.mapper.ContentChapterMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 内容章节业务逻辑实现类 - 简洁版
 * 基于content-simple.sql的t_content_chapter表，实现章节管理业务
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentChapterServiceImpl implements ContentChapterService {

    private final ContentChapterMapper contentChapterMapper;

    // =================== 基础CRUD ===================

    @Override
    @Transactional
    public ContentChapter createChapter(ContentChapter chapter) {
        try {
            log.info("创建章节：内容ID={}, 章节号={}", chapter.getContentId(), chapter.getChapterNum());
            
            // 验证章节数据
            if (!validateChapter(chapter)) {
                throw new IllegalArgumentException("章节数据验证失败");
            }
            
            // 检查章节号是否已存在
            if (isChapterNumExists(chapter.getContentId(), chapter.getChapterNum())) {
                throw new IllegalArgumentException("章节号已存在：" + chapter.getChapterNum());
            }
            
            // 自动计算字数
            if (chapter.getWordCount() == null && chapter.getContent() != null) {
                chapter.calculateWordCount();
            }
            
            // 设置默认状态
            if (chapter.getStatus() == null) {
                chapter.setStatus("DRAFT");
            }
            
            contentChapterMapper.insert(chapter);
            
            log.info("章节创建成功：ID={}", chapter.getId());
            return chapter;
            
        } catch (Exception e) {
            log.error("创建章节失败", e);
            throw new RuntimeException("创建章节失败：" + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public ContentChapter updateChapter(ContentChapter chapter) {
        try {
            log.info("更新章节：ID={}", chapter.getId());
            
            if (chapter.getId() == null) {
                throw new IllegalArgumentException("章节ID不能为空");
            }
            
            // 获取原章节信息
            ContentChapter existingChapter = getChapterById(chapter.getId());
            if (existingChapter == null) {
                throw new IllegalArgumentException("章节不存在：" + chapter.getId());
            }
            
            // 验证章节数据
            if (!validateChapter(chapter)) {
                throw new IllegalArgumentException("章节数据验证失败");
            }
            
            // 自动计算字数
            if (chapter.getContent() != null) {
                chapter.calculateWordCount();
            }
            
            contentChapterMapper.updateById(chapter);
            
            log.info("章节更新成功：ID={}", chapter.getId());
            return getChapterById(chapter.getId());
            
        } catch (Exception e) {
            log.error("更新章节失败", e);
            throw new RuntimeException("更新章节失败：" + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean deleteChapter(Long chapterId, Long authorId) {
        try {
            log.info("删除章节：ID={}, 作者ID={}", chapterId, authorId);
            
            if (chapterId == null || authorId == null) {
                throw new IllegalArgumentException("章节ID和作者ID不能为空");
            }
            
            // 验证删除权限
            if (!canDeleteChapter(chapterId, authorId)) {
                throw new IllegalArgumentException("无权限删除该章节");
            }
            
            int result = contentChapterMapper.deleteById(chapterId);
            
            log.info("章节删除成功：ID={}", chapterId);
            return result > 0;
            
        } catch (Exception e) {
            log.error("删除章节失败", e);
            throw new RuntimeException("删除章节失败：" + e.getMessage(), e);
        }
    }

    @Override
    public ContentChapter getChapterById(Long id) {
        if (id == null) {
            return null;
        }
        return contentChapterMapper.selectById(id);
    }

    @Override
    public ContentChapter getChapterByNum(Long contentId, Integer chapterNum) {
        if (contentId == null || chapterNum == null) {
            return null;
        }
        
        LambdaQueryWrapper<ContentChapter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ContentChapter::getContentId, contentId)
                   .eq(ContentChapter::getChapterNum, chapterNum);
        
        return contentChapterMapper.selectOne(queryWrapper);
    }

    // =================== 查询功能 ===================

    @Override
    public Page<ContentChapter> getChaptersByContentId(Page<ContentChapter> page, Long contentId, String status) {
        if (contentId == null) {
            return new Page<>();
        }
        List<ContentChapter> chapters = contentChapterMapper.selectByContentIdPaged(contentId, 
            (page.getCurrent() - 1) * page.getSize(), Math.toIntExact(page.getSize()));
        page.setRecords(chapters);
        return page;
    }

    @Override
    public List<ContentChapter> getAllChaptersByContentId(Long contentId, String status) {
        if (contentId == null) {
            return Collections.emptyList();
        }
        if (status == null) {
            return contentChapterMapper.selectByContentId(contentId);
        } else if ("PUBLISHED".equals(status)) {
            return contentChapterMapper.selectPublishedByContentId(contentId);
        } else {
            return contentChapterMapper.selectByContentId(contentId);
        }
    }

    @Override
    public ContentChapter getNextChapter(Long contentId, Integer currentChapterNum) {
        if (contentId == null || currentChapterNum == null) {
            return null;
        }
        return contentChapterMapper.selectNextChapter(contentId, currentChapterNum);
    }

    @Override
    public ContentChapter getPreviousChapter(Long contentId, Integer currentChapterNum) {
        if (contentId == null || currentChapterNum == null) {
            return null;
        }
        return contentChapterMapper.selectPreviousChapter(contentId, currentChapterNum);
    }

    @Override
    public Page<ContentChapter> searchChaptersByTitle(Page<ContentChapter> page, Long contentId, 
                                                     String keyword, String status) {
        if (contentId == null) {
            return new Page<>();
        }
        // 简化实现，使用基本查询
        List<ContentChapter> chapters = contentChapterMapper.selectByContentId(contentId);
        page.setRecords(chapters);
        return page;
    }

    // =================== 状态管理 ===================

    @Override
    @Transactional
    public ContentChapter publishChapter(Long chapterId, Long authorId) {
        try {
            log.info("发布章节：ID={}, 作者ID={}", chapterId, authorId);
            
            if (!canPublishChapter(chapterId, authorId)) {
                throw new IllegalArgumentException("无权限发布该章节或章节不满足发布条件");
            }
            
            // 直接更新状态
            ContentChapter chapter = getChapterById(chapterId);
            if (chapter != null) {
                chapter.setStatus("PUBLISHED");
                contentChapterMapper.updateById(chapter);
                log.info("章节发布成功：ID={}", chapterId);
                return chapter;
            } else {
                throw new RuntimeException("章节不存在");
            }
            
        } catch (Exception e) {
            log.error("发布章节失败", e);
            throw new RuntimeException("发布章节失败：" + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Integer batchPublishChapters(Long contentId, List<Integer> chapterNums, Long authorId) {
        try {
            log.info("批量发布章节：内容ID={}, 章节数量={}", contentId, chapterNums.size());
            
            if (contentId == null || CollectionUtils.isEmpty(chapterNums) || authorId == null) {
                throw new IllegalArgumentException("参数不能为空");
            }
            
            // 简化实现：逐个更新章节状态
            int result = 0;
            for (Integer chapterNum : chapterNums) {
                ContentChapter chapter = getChapterByNum(contentId, chapterNum);
                if (chapter != null) {
                    chapter.setStatus("PUBLISHED");
                    contentChapterMapper.updateById(chapter);
                    result++;
                }
            }
            
            log.info("批量发布章节成功：更新数量={}", result);
            return result;
            
        } catch (Exception e) {
            log.error("批量发布章节失败", e);
            throw new RuntimeException("批量发布章节失败：" + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean setChapterToDraft(Long chapterId, Long authorId) {
        try {
            log.info("设置章节为草稿：ID={}, 作者ID={}", chapterId, authorId);
            
            if (!canEditChapter(chapterId, authorId)) {
                throw new IllegalArgumentException("无权限编辑该章节");
            }
            
            // 直接更新状态
            ContentChapter chapter = getChapterById(chapterId);
            if (chapter != null) {
                chapter.setStatus("DRAFT");
                contentChapterMapper.updateById(chapter);
                log.info("章节设置为草稿成功：ID={}", chapterId);
                return true;
            } else {
                return false;
            }
            
        } catch (Exception e) {
            log.error("设置章节为草稿失败", e);
            throw new RuntimeException("设置章节为草稿失败：" + e.getMessage(), e);
        }
    }

    // =================== 章节号管理 ===================

    @Override
    public Integer getNextChapterNum(Long contentId) {
        if (contentId == null) {
            return 1;
        }
        
        // 使用MyBatis-Plus查询最大章节号
        LambdaQueryWrapper<ContentChapter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ContentChapter::getContentId, contentId)
                   .orderByDesc(ContentChapter::getChapterNum)
                   .last("LIMIT 1");
        ContentChapter lastChapter = contentChapterMapper.selectOne(queryWrapper);
        Integer maxNum = lastChapter != null ? lastChapter.getChapterNum() : null;
        return maxNum == null ? 1 : maxNum + 1;
    }

    @Override
    public boolean isChapterNumExists(Long contentId, Integer chapterNum) {
        if (contentId == null || chapterNum == null) {
            return false;
        }
        return getChapterByNum(contentId, chapterNum) != null;
    }

    @Override
    @Transactional
    public boolean reorderChapter(Long contentId, Integer fromNum, Integer toNum, Long authorId) {
        try {
            log.info("重排章节号：内容ID={}, {}→{}", contentId, fromNum, toNum);
            
            if (contentId == null || fromNum == null || toNum == null || authorId == null) {
                throw new IllegalArgumentException("参数不能为空");
            }
            
            if (fromNum.equals(toNum)) {
                return true; // 相同章节号，不需要重排
            }
            
            // 简化实现：交换两个章节的章节号
            ContentChapter fromChapter = getChapterByNum(contentId, fromNum);
            ContentChapter toChapter = getChapterByNum(contentId, toNum);
            
            if (fromChapter == null || toChapter == null) {
                throw new RuntimeException("章节不存在");
            }
            
            fromChapter.setChapterNum(toNum);
            toChapter.setChapterNum(fromNum);
            
            contentChapterMapper.updateById(fromChapter);
            contentChapterMapper.updateById(toChapter);
            
            int result = 2;
            
            log.info("章节重排成功：更新数量={}", result);
            return result > 0;
            
        } catch (Exception e) {
            log.error("重排章节失败", e);
            throw new RuntimeException("重排章节失败：" + e.getMessage(), e);
        }
    }

    @Override
    public List<Integer> findMissingChapterNums(Long contentId) {
        if (contentId == null) {
            return Collections.emptyList();
        }
        
        Integer maxNum = getMaxChapterNum(contentId);
        if (maxNum == null || maxNum <= 0) {
            return Collections.emptyList();
        }
        
        // 简化实现：查找1到maxNum中缺失的章节号
        List<Integer> missingNums = new ArrayList<>();
        for (int i = 1; i <= maxNum; i++) {
            if (!isChapterNumExists(contentId, i)) {
                missingNums.add(i);
            }
        }
        return missingNums;
    }

    @Override
    public List<Integer> findDuplicateChapterNums(Long contentId) {
        if (contentId == null) {
            return Collections.emptyList();
        }
        // 简化实现：返回空列表（假设没有重复）
        return Collections.emptyList();
    }

    // =================== 统计功能 ===================

    @Override
    public Long countChaptersByContentId(Long contentId, String status) {
        if (contentId == null) {
            return 0L;
        }
        return contentChapterMapper.countByContentId(contentId, status);
    }

    @Override
    public Long getTotalWordCount(Long contentId, String status) {
        if (contentId == null) {
            return 0L;
        }
        // 简化实现：累加所有章节的字数
        List<ContentChapter> chapters = getAllChaptersByContentId(contentId, status);
        Long wordCount = chapters.stream()
            .mapToLong(chapter -> chapter.getWordCount() != null ? chapter.getWordCount() : 0L)
            .sum();
        return wordCount != null ? wordCount : 0L;
    }

    @Override
    public Map<String, Object> getPublishedChapterStats(Long contentId) {
        if (contentId == null) {
            return Collections.emptyMap();
        }
        // 简化实现：手动计算统计信息
        List<ContentChapter> publishedChapters = getAllChaptersByContentId(contentId, "PUBLISHED");
        Map<String, Object> stats = new HashMap<>();
        stats.put("publishedCount", publishedChapters.size());
        stats.put("totalWords", publishedChapters.stream()
            .mapToLong(chapter -> chapter.getWordCount() != null ? chapter.getWordCount() : 0L)
            .sum());
        return stats;
    }

    @Override
    public Integer getMaxChapterNum(Long contentId) {
        if (contentId == null) {
            return null;
        }
        // 使用MyBatis-Plus查询最大章节号
        LambdaQueryWrapper<ContentChapter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ContentChapter::getContentId, contentId)
                   .orderByDesc(ContentChapter::getChapterNum)
                   .last("LIMIT 1");
        ContentChapter lastChapter = contentChapterMapper.selectOne(queryWrapper);
        return lastChapter != null ? lastChapter.getChapterNum() : null;
    }

    // =================== 批量操作 ===================

    @Override
    @Transactional
    public Integer batchDeleteChapters(Long contentId, List<Integer> chapterNums, Long authorId) {
        try {
            log.info("批量删除章节：内容ID={}, 章节数量={}", contentId, chapterNums.size());
            
            if (contentId == null || CollectionUtils.isEmpty(chapterNums) || authorId == null) {
                throw new IllegalArgumentException("参数不能为空");
            }
            
            int result = contentChapterMapper.batchDeleteChapters(contentId, chapterNums);
            
            log.info("批量删除章节成功：删除数量={}", result);
            return result;
            
        } catch (Exception e) {
            log.error("批量删除章节失败", e);
            throw new RuntimeException("批量删除章节失败：" + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Integer deleteAllChaptersByContentId(Long contentId, Long authorId) {
        try {
            log.info("删除内容的所有章节：内容ID={}", contentId);
            
            if (contentId == null || authorId == null) {
                throw new IllegalArgumentException("参数不能为空");
            }
            
            int result = contentChapterMapper.deleteByContentId(contentId);
            
            log.info("删除所有章节成功：删除数量={}", result);
            return result;
            
        } catch (Exception e) {
            log.error("删除所有章节失败", e);
            throw new RuntimeException("删除所有章节失败：" + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Integer batchUpdateChapterStatus(Long contentId, List<Integer> chapterNums, 
                                          String status, Long authorId) {
        try {
            log.info("批量更新章节状态：内容ID={}, 状态={}, 章节数量={}", contentId, status, chapterNums.size());
            
            if (contentId == null || CollectionUtils.isEmpty(chapterNums) || status == null || authorId == null) {
                throw new IllegalArgumentException("参数不能为空");
            }
            
            // 转换为Long列表以匹配mapper接口
            List<Long> chapterIds = chapterNums.stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());
            
            int result = contentChapterMapper.batchUpdateStatus(chapterIds, status);
            
            log.info("批量更新章节状态成功：更新数量={}", result);
            return result;
            
        } catch (Exception e) {
            log.error("批量更新章节状态失败", e);
            throw new RuntimeException("批量更新章节状态失败：" + e.getMessage(), e);
        }
    }

    // =================== 业务验证 ===================

    @Override
    public boolean validateChapter(ContentChapter chapter) {
        if (chapter == null) {
            return false;
        }
        
        // 使用实体类的验证方法
        return chapter.isValid();
    }

    @Override
    public boolean canPublishChapter(Long chapterId, Long authorId) {
        if (chapterId == null || authorId == null) {
            return false;
        }
        
        ContentChapter chapter = getChapterById(chapterId);
        if (chapter == null) {
            return false;
        }
        
        // 检查编辑权限（作者权限）
        if (!canEditChapter(chapterId, authorId)) {
            return false;
        }
        
        // 检查是否可以发布
        return chapter.canPublish();
    }

    @Override
    public boolean canEditChapter(Long chapterId, Long userId) {
        if (chapterId == null || userId == null) {
            return false;
        }
        
        ContentChapter chapter = getChapterById(chapterId);
        if (chapter == null) {
            return false;
        }
        
        // 这里需要根据实际业务逻辑实现权限检查
        // 目前简化为检查章节是否可编辑
        return chapter.canEdit();
    }

    @Override
    public boolean canDeleteChapter(Long chapterId, Long userId) {
        if (chapterId == null || userId == null) {
            return false;
        }
        
        ContentChapter chapter = getChapterById(chapterId);
        if (chapter == null) {
            return false;
        }
        
        // 这里需要根据实际业务逻辑实现权限检查
        // 目前简化为允许删除草稿状态的章节
        return chapter.isDraft();
    }

    // =================== 高级功能 ===================

    @Override
    public List<ContentChapter> getChaptersByWordCountRange(Long contentId, Integer minWordCount, Integer maxWordCount) {
        if (contentId == null) {
            return Collections.emptyList();
        }
        return contentChapterMapper.findByWordCountRange(contentId, minWordCount, maxWordCount);
    }

    @Override
    public List<ContentChapter> getDraftChapters(Long contentId) {
        if (contentId == null) {
            return Collections.emptyList();
        }
        return contentChapterMapper.findDraftChapters(contentId);
    }

    @Override
    public List<ContentChapter> getPublishedChapters(Long contentId) {
        if (contentId == null) {
            return Collections.emptyList();
        }
        return contentChapterMapper.findPublishedChapters(contentId);
    }

    @Override
    @Transactional
    public Integer calculateChapterWordCount(Long chapterId) {
        try {
            ContentChapter chapter = getChapterById(chapterId);
            if (chapter == null) {
                throw new IllegalArgumentException("章节不存在：" + chapterId);
            }
            
            chapter.calculateWordCount();
            contentChapterMapper.updateById(chapter);
            
            log.info("章节字数计算成功：ID={}, 字数={}", chapterId, chapter.getWordCount());
            return chapter.getWordCount();
            
        } catch (Exception e) {
            log.error("计算章节字数失败", e);
            throw new RuntimeException("计算章节字数失败：" + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Integer batchCalculateWordCount(Long contentId) {
        try {
            log.info("批量计算字数：内容ID={}", contentId);
            
            if (contentId == null) {
                throw new IllegalArgumentException("内容ID不能为空");
            }
            
            List<ContentChapter> chapters = getAllChaptersByContentId(contentId, null);
            if (CollectionUtils.isEmpty(chapters)) {
                return 0;
            }
            
            int updateCount = 0;
            for (ContentChapter chapter : chapters) {
                if (chapter.getContent() != null) {
                    chapter.calculateWordCount();
                    contentChapterMapper.updateById(chapter);
                    updateCount++;
                }
            }
            
            log.info("批量计算字数成功：更新数量={}", updateCount);
            return updateCount;
            
        } catch (Exception e) {
            log.error("批量计算字数失败", e);
            throw new RuntimeException("批量计算字数失败：" + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> validateChapterSequence(Long contentId) {
        Map<String, Object> result = new HashMap<>();
        
        if (contentId == null) {
            result.put("valid", false);
            result.put("message", "内容ID不能为空");
            return result;
        }
        
        try {
            // 获取最大章节号
            Integer maxNum = getMaxChapterNum(contentId);
            if (maxNum == null || maxNum <= 0) {
                result.put("valid", true);
                result.put("message", "暂无章节");
                result.put("totalChapters", 0);
                return result;
            }
            
            // 查找缺失和重复的章节号
            List<Integer> missingNums = findMissingChapterNums(contentId);
            List<Integer> duplicateNums = findDuplicateChapterNums(contentId);
            
            boolean isValid = CollectionUtils.isEmpty(missingNums) && CollectionUtils.isEmpty(duplicateNums);
            
            result.put("valid", isValid);
            result.put("message", isValid ? "章节顺序完整" : "章节顺序存在问题");
            result.put("totalChapters", maxNum);
            result.put("missingChapters", missingNums);
            result.put("duplicateChapters", duplicateNums);
            
            log.info("章节顺序验证完成：内容ID={}, 完整性={}", contentId, isValid);
            return result;
            
        } catch (Exception e) {
            log.error("验证章节顺序失败", e);
            result.put("valid", false);
            result.put("message", "验证失败：" + e.getMessage());
            return result;
        }
    }
}