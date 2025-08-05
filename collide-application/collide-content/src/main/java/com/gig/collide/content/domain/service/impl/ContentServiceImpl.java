package com.gig.collide.content.domain.service.impl;

import com.gig.collide.content.domain.entity.Content;
import com.gig.collide.content.domain.service.ContentService;
import com.gig.collide.content.infrastructure.mapper.ContentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 内容服务实现
 * 极简版 - 12个核心方法，使用通用查询
 * 
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ContentServiceImpl implements ContentService {

    private final ContentMapper contentMapper;

    // =================== 核心CRUD功能（4个方法）===================

    @Override
    public Content createContent(Content content) {
        log.info("创建内容: title={}, authorId={}", content.getTitle(), content.getAuthorId());
        
        // 基础验证
        if (!StringUtils.hasText(content.getTitle()) || content.getAuthorId() == null) {
            throw new IllegalArgumentException("标题和作者ID不能为空");
        }
        
        // 设置默认值
        if (content.getCreateTime() == null) {
            content.setCreateTime(LocalDateTime.now());
        }
        if (content.getUpdateTime() == null) {
            content.setUpdateTime(LocalDateTime.now());
        }
        if (!StringUtils.hasText(content.getStatus())) {
            content.setStatus("DRAFT");
        }
        if (!StringUtils.hasText(content.getReviewStatus())) {
            content.setReviewStatus("PENDING");
        }
        
        // 初始化统计数据
        if (content.getViewCount() == null) {
            content.setViewCount(0L);
        }
        if (content.getLikeCount() == null) {
            content.setLikeCount(0L);
        }
        if (content.getCommentCount() == null) {
            content.setCommentCount(0L);
        }
        if (content.getFavoriteCount() == null) {
            content.setFavoriteCount(0L);
        }
        
        contentMapper.insert(content);
        log.info("内容创建成功: id={}", content.getId());
        return content;
    }

    @Override
    public Content updateContent(Content content) {
        log.info("更新内容: id={}", content.getId());
        
        if (content.getId() == null) {
            throw new IllegalArgumentException("内容ID不能为空");
        }
        
        content.setUpdateTime(LocalDateTime.now());
        contentMapper.updateById(content);
        
        log.info("内容更新成功: id={}", content.getId());
        return content;
    }

    @Override
    public Content getContentById(Long id, Boolean includeOffline) {
        log.debug("获取内容详情: id={}, includeOffline={}", id, includeOffline);
        
        if (id == null) {
            throw new IllegalArgumentException("内容ID不能为空");
        }
        
        Content content = contentMapper.selectById(id);
        
        // 如果不包含下线内容，检查状态
        if (content != null && Boolean.FALSE.equals(includeOffline)) {
            if (!"PUBLISHED".equals(content.getStatus())) {
                return null;
            }
        }
        
        return content;
    }

    @Override
    public boolean deleteContent(Long contentId, Long operatorId) {
        log.info("软删除内容: contentId={}, operatorId={}", contentId, operatorId);
        
        if (contentId == null) {
            throw new IllegalArgumentException("内容ID不能为空");
        }
        
        try {
            int result = contentMapper.softDeleteContent(contentId);
            boolean success = result > 0;
            if (success) {
                log.info("内容软删除成功: contentId={}", contentId);
            }
            return success;
        } catch (Exception e) {
            log.error("内容软删除失败: contentId={}", contentId, e);
            return false;
        }
    }

    // =================== 万能查询功能（3个方法）===================

    @Override
    public List<Content> getContentsByConditions(Long authorId, Long categoryId, String contentType,
                                                String status, String reviewStatus, Double minScore,
                                                Integer timeRange, String orderBy, String orderDirection,
                                                Integer currentPage, Integer pageSize) {
        log.debug("万能条件查询内容: authorId={}, categoryId={}, contentType={}, timeRange={}", 
                 authorId, categoryId, contentType, timeRange);
        
        return contentMapper.selectContentsByConditions(
            authorId, categoryId, contentType, status, reviewStatus, minScore,
            timeRange, orderBy, orderDirection, currentPage, pageSize
        );
    }

    @Override
    public List<Content> searchContents(String keyword, String contentType, Long categoryId,
                                      Integer currentPage, Integer pageSize) {
        log.debug("搜索内容: keyword={}, contentType={}", keyword, contentType);
        
        if (!StringUtils.hasText(keyword)) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }
        
        return contentMapper.searchContents(keyword, contentType, categoryId, currentPage, pageSize);
    }

    @Override
    public List<Content> getRecommendedContents(Long userId, List<Long> excludeContentIds, Integer limit) {
        log.debug("获取推荐内容: userId={}, excludeContentIds.size={}, limit={}", 
                 userId, excludeContentIds != null ? excludeContentIds.size() : 0, limit);
        
        return contentMapper.getRecommendedContents(userId, excludeContentIds, limit);
    }

    // =================== 状态管理功能（3个方法）===================

    @Override
    public boolean updateContentStatus(Long contentId, String status) {
        log.info("更新内容状态: contentId={}, status={}", contentId, status);
        
        if (contentId == null || !StringUtils.hasText(status)) {
            throw new IllegalArgumentException("内容ID和状态不能为空");
        }
        
        try {
            int result = contentMapper.updateContentStatus(contentId, status);
            boolean success = result > 0;
            if (success) {
                log.info("内容状态更新成功: contentId={}", contentId);
            }
            return success;
        } catch (Exception e) {
            log.error("内容状态更新失败: contentId={}", contentId, e);
            return false;
        }
    }

    @Override
    public boolean updateReviewStatus(Long contentId, String reviewStatus) {
        log.info("更新审核状态: contentId={}, reviewStatus={}", contentId, reviewStatus);
        
        if (contentId == null || !StringUtils.hasText(reviewStatus)) {
            throw new IllegalArgumentException("内容ID和审核状态不能为空");
        }
        
        try {
            int result = contentMapper.updateReviewStatus(contentId, reviewStatus);
            boolean success = result > 0;
            if (success) {
                log.info("审核状态更新成功: contentId={}", contentId);
            }
            return success;
        } catch (Exception e) {
            log.error("审核状态更新失败: contentId={}", contentId, e);
            return false;
        }
    }

    @Override
    public boolean batchUpdateStatus(List<Long> ids, String status) {
        log.info("批量更新内容状态: ids.size={}, status={}", 
                ids != null ? ids.size() : 0, status);
        
        if (ids == null || ids.isEmpty() || !StringUtils.hasText(status)) {
            throw new IllegalArgumentException("内容ID列表和状态不能为空");
        }
        
        try {
            int result = contentMapper.batchUpdateStatus(ids, status);
            boolean success = result > 0;
            if (success) {
                log.info("批量更新内容状态成功: 影响行数={}", result);
            }
            return success;
        } catch (Exception e) {
            log.error("批量更新内容状态失败", e);
            return false;
        }
    }

    // =================== 统计管理功能（2个方法）===================

    @Override
    public boolean updateContentStats(Long contentId, Long viewCount, Long likeCount, 
                                    Long commentCount, Long favoriteCount) {
        log.info("更新内容统计: contentId={}, viewCount={}, likeCount={}, commentCount={}, favoriteCount={}", 
                contentId, viewCount, likeCount, commentCount, favoriteCount);
        
        if (contentId == null) {
            throw new IllegalArgumentException("内容ID不能为空");
        }
        
        try {
            int result = contentMapper.updateContentStats(contentId, viewCount, likeCount, 
                                                        commentCount, favoriteCount);
            boolean success = result > 0;
            if (success) {
                log.info("内容统计更新成功: contentId={}", contentId);
            }
            return success;
        } catch (Exception e) {
            log.error("内容统计更新失败: contentId={}", contentId, e);
            return false;
        }
    }

    @Override
    public Long increaseViewCount(Long contentId, Integer increment) {
        log.debug("增加浏览量: contentId={}, increment={}", contentId, increment);
        
        if (contentId == null || increment == null || increment <= 0) {
            throw new IllegalArgumentException("内容ID和增量必须有效");
        }
        
        Content content = contentMapper.selectById(contentId);
        if (content == null) {
            throw new IllegalArgumentException("内容不存在");
        }
        
        Long newViewCount = content.getViewCount() + increment;
        updateContentStats(contentId, newViewCount, content.getLikeCount(), 
                         content.getCommentCount(), content.getFavoriteCount());
        
        return newViewCount;
    }
}