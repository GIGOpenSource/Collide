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

import java.util.*;

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
        
        // 简化实现：使用MyBatis-Plus的条件查询
        LambdaQueryWrapper<Content> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(title)) {
            queryWrapper.like(Content::getTitle, title);
        }
        if (StringUtils.hasText(contentType)) {
            queryWrapper.eq(Content::getContentType, contentType);
        }
        if (authorId != null) {
            queryWrapper.eq(Content::getAuthorId, authorId);
        }
        if (categoryId != null) {
            queryWrapper.eq(Content::getCategoryId, categoryId);
        }
        if (StringUtils.hasText(status)) {
            queryWrapper.eq(Content::getStatus, status);
        }
        if (StringUtils.hasText(reviewStatus)) {
            queryWrapper.eq(Content::getReviewStatus, reviewStatus);
        }
        
        // 排序
        if ("desc".equalsIgnoreCase(orderDirection)) {
            if ("createTime".equals(orderBy)) {
                queryWrapper.orderByDesc(Content::getCreateTime);
            } else if ("viewCount".equals(orderBy)) {
                queryWrapper.orderByDesc(Content::getViewCount);
            } else {
                queryWrapper.orderByDesc(Content::getId);
            }
        } else {
            if ("createTime".equals(orderBy)) {
                queryWrapper.orderByAsc(Content::getCreateTime);
            } else if ("viewCount".equals(orderBy)) {
                queryWrapper.orderByAsc(Content::getViewCount);
            } else {
                queryWrapper.orderByAsc(Content::getId);
            }
        }
        
        return contentMapper.selectPage(page, queryWrapper);
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
        LambdaQueryWrapper<Content> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Content::getAuthorId, authorId);
        if (StringUtils.hasText(contentType)) {
            queryWrapper.eq(Content::getContentType, contentType);
        }
        if (StringUtils.hasText(status)) {
            queryWrapper.eq(Content::getStatus, status);
        }
        queryWrapper.orderByDesc(Content::getCreateTime);
        return contentMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Page<Content> getContentsByCategory(Page<Content> page, Long categoryId, String contentType) {
        LambdaQueryWrapper<Content> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Content::getCategoryId, categoryId)
                   .eq(Content::getStatus, "PUBLISHED");
        if (StringUtils.hasText(contentType)) {
            queryWrapper.eq(Content::getContentType, contentType);
        }
        queryWrapper.orderByDesc(Content::getCreateTime);
        return contentMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Page<Content> searchContents(Page<Content> page, String keyword, String contentType) {
        LambdaQueryWrapper<Content> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Content::getStatus, "PUBLISHED");
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like(Content::getTitle, keyword)
                .or()
                .like(Content::getDescription, keyword));
        }
        if (StringUtils.hasText(contentType)) {
            queryWrapper.eq(Content::getContentType, contentType);
        }
        queryWrapper.orderByDesc(Content::getViewCount);
        return contentMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Page<Content> getPopularContents(Page<Content> page, String contentType, Integer timeRange) {
        LambdaQueryWrapper<Content> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Content::getStatus, "PUBLISHED");
        if (StringUtils.hasText(contentType)) {
            queryWrapper.eq(Content::getContentType, contentType);
        }
        // 按浏览量排序
        queryWrapper.orderByDesc(Content::getViewCount, Content::getLikeCount);
        return contentMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Page<Content> getLatestContents(Page<Content> page, String contentType) {
        LambdaQueryWrapper<Content> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Content::getStatus, "PUBLISHED");
        if (StringUtils.hasText(contentType)) {
            queryWrapper.eq(Content::getContentType, contentType);
        }
        queryWrapper.orderByDesc(Content::getPublishTime);
        return contentMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Page<Content> getContentsByScore(Page<Content> page, Double minScore, String contentType) {
        LambdaQueryWrapper<Content> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Content::getStatus, "PUBLISHED");
        if (minScore != null) {
            queryWrapper.ge(Content::getAverageScore, minScore);
        }
        if (StringUtils.hasText(contentType)) {
            queryWrapper.eq(Content::getContentType, contentType);
        }
        queryWrapper.orderByDesc(Content::getAverageScore);
        return contentMapper.selectPage(page, queryWrapper);
    }

    // =================== 统计功能 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long increaseViewCount(Long contentId, Integer increment) {
        log.debug("增加浏览量: ID={}, 增量={}", contentId, increment);
        
        // 使用MyBatis-Plus更新方式
        Content content = contentMapper.selectById(contentId);
        if (content != null) {
            content.setViewCount((content.getViewCount() != null ? content.getViewCount() : 0L) + increment);
            contentMapper.updateById(content);
            return content.getViewCount();
        }
        return 0L;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long increaseLikeCount(Long contentId, Integer increment) {
        log.debug("增加点赞数: ID={}, 增量={}", contentId, increment);
        
        Content content = contentMapper.selectById(contentId);
        if (content != null) {
            content.setLikeCount((content.getLikeCount() != null ? content.getLikeCount() : 0L) + increment);
            contentMapper.updateById(content);
            return content.getLikeCount();
        }
        return 0L;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long increaseCommentCount(Long contentId, Integer increment) {
        log.debug("增加评论数: ID={}, 增量={}", contentId, increment);
        
        Content content = contentMapper.selectById(contentId);
        if (content != null) {
            content.setCommentCount((content.getCommentCount() != null ? content.getCommentCount() : 0L) + increment);
            contentMapper.updateById(content);
            return content.getCommentCount();
        }
        return 0L;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long increaseFavoriteCount(Long contentId, Integer increment) {
        log.debug("增加收藏数: ID={}, 增量={}", contentId, increment);
        
        Content content = contentMapper.selectById(contentId);
        if (content != null) {
            content.setFavoriteCount((content.getFavoriteCount() != null ? content.getFavoriteCount() : 0L) + increment);
            contentMapper.updateById(content);
            return content.getFavoriteCount();
        }
        return 0L;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Double updateScore(Long contentId, Integer score) {
        log.debug("更新评分: ID={}, 评分={}", contentId, score);
        
        // 验证评分范围
        if (score < 1 || score > 10) {
            throw new IllegalArgumentException("评分必须在1-10之间");
        }
        
        Content content = contentMapper.selectById(contentId);
        if (content != null) {
            // 更新评分统计
            Long scoreTotal = content.getScoreTotal() != null ? content.getScoreTotal() : 0L;
            Long scoreCount = content.getScoreCount() != null ? content.getScoreCount() : 0L;
            
            scoreTotal += score;
            scoreCount += 1;
            
            content.setScoreTotal(scoreTotal);
            content.setScoreCount(scoreCount);
            
            contentMapper.updateById(content);
            return content.getAverageScore();
        }
        return 0.0;
    }

    @Override
    public Map<String, Object> getContentStatistics(Long contentId) {
        // 简化实现：基于实体数据构建统计信息
        Content content = contentMapper.selectById(contentId);
        if (content == null) {
            return Collections.emptyMap();
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("id", content.getId());
        stats.put("title", content.getTitle());
        stats.put("viewCount", content.getViewCount());
        stats.put("likeCount", content.getLikeCount());
        stats.put("commentCount", content.getCommentCount());
        stats.put("favoriteCount", content.getFavoriteCount());
        stats.put("scoreCount", content.getScoreCount());
        stats.put("averageScore", content.getAverageScore());
        stats.put("createTime", content.getCreateTime());
        stats.put("updateTime", content.getUpdateTime());
        return stats;
    }

    // =================== 数据同步 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateAuthorInfo(Long authorId, String nickname, String avatar) {
        log.info("同步作者信息: ID={}, 昵称={}", authorId, nickname);
        
        // 使用MyBatis-Plus更新相关内容的作者信息
        LambdaQueryWrapper<Content> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Content::getAuthorId, authorId);
        
        List<Content> contents = contentMapper.selectList(queryWrapper);
        if (contents.isEmpty()) {
            return 0;
        }
        
        int updateCount = 0;
        for (Content content : contents) {
            if (StringUtils.hasText(nickname)) {
                content.setAuthorNickname(nickname);
            }
            if (StringUtils.hasText(avatar)) {
                content.setAuthorAvatar(avatar);
            }
            contentMapper.updateById(content);
            updateCount++;
        }
        
        log.info("作者信息同步完成: 更新{}条记录", updateCount);
        return updateCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateCategoryInfo(Long categoryId, String categoryName) {
        log.info("同步分类信息: ID={}, 名称={}", categoryId, categoryName);
        
        // 使用MyBatis-Plus更新相关内容的分类信息
        LambdaQueryWrapper<Content> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Content::getCategoryId, categoryId);
        
        List<Content> contents = contentMapper.selectList(queryWrapper);
        if (contents.isEmpty()) {
            return 0;
        }
        
        int updateCount = 0;
        for (Content content : contents) {
            if (StringUtils.hasText(categoryName)) {
                content.setCategoryName(categoryName);
            }
            contentMapper.updateById(content);
            updateCount++;
        }
        
        log.info("分类信息同步完成: 更新{}条记录", updateCount);
        return updateCount;
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
        // 简化实现：推荐热门内容
        LambdaQueryWrapper<Content> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Content::getStatus, "PUBLISHED");
        
        if (StringUtils.hasText(contentType)) {
            queryWrapper.eq(Content::getContentType, contentType);
        }
        if (excludeAuthorId != null) {
            queryWrapper.ne(Content::getAuthorId, excludeAuthorId);
        }
        
        queryWrapper.orderByDesc(Content::getViewCount, Content::getLikeCount);
        return contentMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<Content> getSimilarContents(Long contentId, Integer limit) {
        Content content = contentMapper.selectById(contentId);
        if (content == null) {
            return Collections.emptyList();
        }
        
        // 简化实现：查找同分类同类型的其他内容
        LambdaQueryWrapper<Content> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Content::getCategoryId, content.getCategoryId())
                   .eq(Content::getContentType, content.getContentType())
                   .eq(Content::getStatus, "PUBLISHED")
                   .ne(Content::getId, contentId)
                   .orderByDesc(Content::getViewCount)
                   .last("LIMIT " + (limit != null ? limit : 10));
        
        return contentMapper.selectList(queryWrapper);
    }

    @Override
    public List<Content> getNeedsChapterManagement(Long authorId) {
        // 查找需要章节管理的内容类型（小说、漫画等）
        LambdaQueryWrapper<Content> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Content::getAuthorId, authorId)
                   .in(Content::getContentType, "NOVEL", "COMIC")
                   .eq(Content::getStatus, "PUBLISHED")
                   .orderByDesc(Content::getCreateTime);
        
        return contentMapper.selectList(queryWrapper);
    }

    @Override
    public Long countByAuthor(Long authorId, String status) {
        LambdaQueryWrapper<Content> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Content::getAuthorId, authorId);
        
        if (StringUtils.hasText(status)) {
            queryWrapper.eq(Content::getStatus, status);
        }
        
        return contentMapper.selectCount(queryWrapper);
    }

    @Override
    public Long countByCategory(Long categoryId, String status) {
        LambdaQueryWrapper<Content> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Content::getCategoryId, categoryId);
        
        if (StringUtils.hasText(status)) {
            queryWrapper.eq(Content::getStatus, status);
        }
        
        return contentMapper.selectCount(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> getContentTypeStats() {
        // 简化实现：基于现有数据统计各类型内容数量
        List<Map<String, Object>> stats = new ArrayList<>();
        
        String[] contentTypes = {"NOVEL", "COMIC", "VIDEO", "ARTICLE", "AUDIO"};
        for (String type : contentTypes) {
            LambdaQueryWrapper<Content> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Content::getContentType, type)
                       .eq(Content::getStatus, "PUBLISHED");
            
            Long count = contentMapper.selectCount(queryWrapper);
            
            Map<String, Object> stat = new HashMap<>();
            stat.put("content_type", type);
            stat.put("count", count);
            stats.add(stat);
        }
        
        return stats;
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