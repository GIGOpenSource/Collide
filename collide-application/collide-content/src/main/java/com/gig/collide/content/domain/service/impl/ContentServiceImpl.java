package com.gig.collide.content.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import java.util.Map;

/**
 * 内容业务逻辑实现类 - 简洁版
 * 基于content-simple.sql的设计，实现核心内容管理业务
 * 包含评分功能、事务管理、权限验证
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private final ContentMapper contentMapper;

    // =================== 基础CRUD ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Content createContent(Content content) {
        log.info("创建内容: {}", content.getTitle());
        
        // 数据验证
        if (!validateContent(content)) {
            throw new IllegalArgumentException("内容数据验证失败");
        }
        
        // 状态初始化
        if (!StringUtils.hasText(content.getStatus())) {
            content.setStatus("DRAFT");
        }
        if (!StringUtils.hasText(content.getReviewStatus())) {
            content.setReviewStatus("PENDING");
        }
        
        // 统计字段初始化
        initializeStatistics(content);
        
        // 保存内容
        contentMapper.insert(content);
        
        log.info("内容创建成功: ID={}, 标题={}", content.getId(), content.getTitle());
        return content;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Content updateContent(Content content) {
        log.info("更新内容: ID={}", content.getId());
        
        // 获取原始内容
        Content existingContent = contentMapper.selectById(content.getId());
        if (existingContent == null) {
            throw new IllegalArgumentException("内容不存在: " + content.getId());
        }
        
        // 权限验证（简化实现，实际应结合用户权限）
        if (!canEdit(content.getId(), content.getAuthorId())) {
            throw new IllegalArgumentException("无权限编辑此内容");
        }
        
        // 保护关键字段
        content.setAuthorId(existingContent.getAuthorId());
        content.setCreateTime(existingContent.getCreateTime());
        
        // 更新内容
        contentMapper.updateById(content);
        
        log.info("内容更新成功: ID={}", content.getId());
        return content;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteContent(Long contentId, Long operatorId) {
        log.info("删除内容: ID={}, 操作人={}", contentId, operatorId);
        
        Content content = contentMapper.selectById(contentId);
        if (content == null) {
            return false;
        }
        
        // 删除权限验证
        if (!canDelete(contentId, operatorId)) {
            throw new IllegalArgumentException("无权限删除此内容");
        }
        
        // 逻辑删除（设为OFFLINE状态）
        content.setStatus("OFFLINE");
        contentMapper.updateById(content);
        
        log.info("内容删除成功: ID={}", contentId);
        return true;
    }

    @Override
    public Content getContentById(Long id, Boolean includeOffline) {
        if (includeOffline == null) {
            includeOffline = false;
        }
        
        Content content = contentMapper.selectById(id);
        if (content == null) {
            return null;
        }
        
        // 检查是否包含下线内容
        if (!includeOffline && "OFFLINE".equals(content.getStatus())) {
            return null;
        }
        
        return content;
    }

    @Override
    public Page<Content> queryContents(Page<Content> page, String title, String contentType,
                                     Long authorId, Long categoryId, String status, String reviewStatus,
                                     String orderBy, String orderDirection) {
        
        return contentMapper.findWithConditions(page, title, contentType, authorId, categoryId,
                                               status, reviewStatus, null, null, null, null, null,
                                               orderBy, orderDirection);
    }

    // =================== 状态管理 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Content publishContent(Long contentId, Long authorId) {
        log.info("发布内容: ID={}, 作者={}", contentId, authorId);
        
        Content content = contentMapper.selectById(contentId);
        if (content == null) {
            throw new IllegalArgumentException("内容不存在");
        }
        
        // 发布权限验证
        if (!canPublish(contentId, authorId)) {
            throw new IllegalArgumentException("无权限发布此内容");
        }
        
        // 发布条件验证
        if (!"APPROVED".equals(content.getReviewStatus())) {
            throw new IllegalArgumentException("内容未通过审核，无法发布");
        }
        
        // 发布内容
        content.publish();
        contentMapper.updateById(content);
        
        log.info("内容发布成功: ID={}", contentId);
        return content;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean offlineContent(Long contentId, Long operatorId) {
        log.info("下线内容: ID={}, 操作人={}", contentId, operatorId);
        
        Content content = contentMapper.selectById(contentId);
        if (content == null) {
            return false;
        }
        
        // 权限验证（管理员或作者可下线）
        if (!content.getAuthorId().equals(operatorId)) {
            // TODO: 添加管理员权限检查
            log.warn("用户{}试图下线不属于自己的内容{}", operatorId, contentId);
        }
        
        // 下线内容
        content.offline();
        contentMapper.updateById(content);
        
        log.info("内容下线成功: ID={}", contentId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Content reviewContent(Long contentId, String reviewStatus, Long reviewerId, String reviewComment) {
        log.info("审核内容: ID={}, 状态={}, 审核人={}", contentId, reviewStatus, reviewerId);
        
        Content content = contentMapper.selectById(contentId);
        if (content == null) {
            throw new IllegalArgumentException("内容不存在");
        }
        
        // 更新审核状态
        if ("APPROVED".equals(reviewStatus)) {
            content.approveReview();
        } else if ("REJECTED".equals(reviewStatus)) {
            content.rejectReview();
        }
        
        contentMapper.updateById(content);
        
        log.info("内容审核完成: ID={}, 结果={}", contentId, reviewStatus);
        return content;
    }

    // =================== 查询功能 ===================

    @Override
    public Page<Content> getContentsByAuthor(Page<Content> page, Long authorId, String contentType, String status) {
        return contentMapper.findByAuthor(page, authorId, contentType, status);
    }

    @Override
    public Page<Content> getContentsByCategory(Page<Content> page, Long categoryId, String contentType) {
        return contentMapper.findByCategory(page, categoryId, contentType, "PUBLISHED");
    }

    @Override
    public Page<Content> searchContents(Page<Content> page, String keyword, String contentType) {
        return contentMapper.searchContents(page, keyword, contentType, "PUBLISHED");
    }

    @Override
    public Page<Content> getPopularContents(Page<Content> page, String contentType, Integer timeRange) {
        return contentMapper.findPopularContents(page, contentType, timeRange, null, null);
    }

    @Override
    public Page<Content> getLatestContents(Page<Content> page, String contentType) {
        return contentMapper.findLatestContents(page, contentType, "PUBLISHED");
    }

    @Override
    public Page<Content> getContentsByScore(Page<Content> page, Double minScore, String contentType) {
        return contentMapper.findByScore(page, minScore, contentType);
    }

    // =================== 统计功能 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long increaseViewCount(Long contentId, Integer increment) {
        log.debug("增加浏览量: ID={}, 增量={}", contentId, increment);
        
        contentMapper.increaseViewCount(contentId, increment);
        
        Content content = contentMapper.selectById(contentId);
        return content != null ? content.getViewCount() : 0L;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long increaseLikeCount(Long contentId, Integer increment) {
        log.debug("增加点赞数: ID={}, 增量={}", contentId, increment);
        
        contentMapper.increaseLikeCount(contentId, increment);
        
        Content content = contentMapper.selectById(contentId);
        return content != null ? content.getLikeCount() : 0L;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long increaseCommentCount(Long contentId, Integer increment) {
        log.debug("增加评论数: ID={}, 增量={}", contentId, increment);
        
        contentMapper.increaseCommentCount(contentId, increment);
        
        Content content = contentMapper.selectById(contentId);
        return content != null ? content.getCommentCount() : 0L;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long increaseFavoriteCount(Long contentId, Integer increment) {
        log.debug("增加收藏数: ID={}, 增量={}", contentId, increment);
        
        contentMapper.increaseFavoriteCount(contentId, increment);
        
        Content content = contentMapper.selectById(contentId);
        return content != null ? content.getFavoriteCount() : 0L;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Double updateScore(Long contentId, Integer score) {
        log.debug("更新评分: ID={}, 评分={}", contentId, score);
        
        // 验证评分范围
        if (score < 1 || score > 10) {
            throw new IllegalArgumentException("评分必须在1-10之间");
        }
        
        contentMapper.addScore(contentId, score);
        
        Content content = contentMapper.selectById(contentId);
        return content != null ? content.getAverageScore() : 0.0;
    }

    @Override
    public Map<String, Object> getContentStatistics(Long contentId) {
        return contentMapper.getContentStatistics(contentId);
    }

    // =================== 数据同步 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateAuthorInfo(Long authorId, String nickname, String avatar) {
        log.info("同步作者信息: ID={}, 昵称={}", authorId, nickname);
        return contentMapper.updateAuthorInfo(authorId, nickname, avatar);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateCategoryInfo(Long categoryId, String categoryName) {
        log.info("同步分类信息: ID={}, 名称={}", categoryId, categoryName);
        return contentMapper.updateCategoryInfo(categoryId, categoryName);
    }

    // =================== 业务验证 ===================

    @Override
    public boolean validateContent(Content content) {
        if (content == null) {
            return false;
        }
        
        // 标题验证
        if (!StringUtils.hasText(content.getTitle()) || content.getTitle().length() > 200) {
            log.warn("内容标题无效: {}", content.getTitle());
            return false;
        }
        
        // 内容类型验证
        if (!isValidContentType(content.getContentType())) {
            log.warn("内容类型无效: {}", content.getContentType());
            return false;
        }
        
        // 作者ID验证
        if (content.getAuthorId() == null || content.getAuthorId() <= 0) {
            log.warn("作者ID无效: {}", content.getAuthorId());
            return false;
        }
        
        return true;
    }

    @Override
    public boolean canPublish(Long contentId, Long authorId) {
        Content content = contentMapper.selectById(contentId);
        return content != null 
               && content.getAuthorId().equals(authorId)
               && content.canPublish();
    }

    @Override
    public boolean canEdit(Long contentId, Long userId) {
        Content content = contentMapper.selectById(contentId);
        return content != null 
               && content.getAuthorId().equals(userId)
               && content.canEdit();
    }

    @Override
    public boolean canDelete(Long contentId, Long userId) {
        Content content = contentMapper.selectById(contentId);
        return content != null 
               && content.getAuthorId().equals(userId)
               && content.canDelete();
    }

    // =================== 高级功能 ===================

    @Override
    public Page<Content> getRecommendedContents(Page<Content> page, String contentType, Long excludeAuthorId) {
        return contentMapper.findRecommendedContents(page, contentType, excludeAuthorId);
    }

    @Override
    public List<Content> getSimilarContents(Long contentId, Integer limit) {
        Content content = contentMapper.selectById(contentId);
        if (content == null) {
            return List.of();
        }
        
        return contentMapper.findSimilarContents(content.getCategoryId(), 
                                                content.getContentType(), 
                                                contentId, limit);
    }

    @Override
    public List<Content> getNeedsChapterManagement(Long authorId) {
        return contentMapper.findNeedsChapterManagement(authorId);
    }

    @Override
    public Long countByAuthor(Long authorId, String status) {
        return contentMapper.countByAuthor(authorId, status);
    }

    @Override
    public Long countByCategory(Long categoryId, String status) {
        return contentMapper.countByCategory(categoryId, status);
    }

    @Override
    public List<Map<String, Object>> getContentTypeStats() {
        return contentMapper.getContentTypeStats();
    }

    // =================== 私有辅助方法 ===================

    /**
     * 初始化统计字段
     */
    private void initializeStatistics(Content content) {
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
        if (content.getScoreCount() == null) {
            content.setScoreCount(0L);
        }
        if (content.getScoreTotal() == null) {
            content.setScoreTotal(0L);
        }
    }

    /**
     * 验证内容类型
     */
    private boolean isValidContentType(String contentType) {
        if (!StringUtils.hasText(contentType)) {
            return false;
        }
        
        return "NOVEL".equals(contentType) 
               || "COMIC".equals(contentType)
               || "VIDEO".equals(contentType)
               || "ARTICLE".equals(contentType)
               || "AUDIO".equals(contentType);
    }
}