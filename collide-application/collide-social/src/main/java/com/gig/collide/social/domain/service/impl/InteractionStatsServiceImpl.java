package com.gig.collide.social.domain.service.impl;

import com.gig.collide.social.domain.service.InteractionStatsService;
import com.gig.collide.social.infrastructure.mapper.SocialContentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 互动统计服务实现
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InteractionStatsServiceImpl implements InteractionStatsService {

    private final SocialContentMapper contentMapper;

    @Override
    public void incrementLikeCount(Long contentId) {
        int updated = contentMapper.incrementLikeCount(contentId);
        if (updated > 0) {
            log.info("内容[{}]点赞数+1, 影响行数: {}", contentId, updated);
        } else {
            log.warn("内容[{}]点赞数更新失败, 影响行数: {}", contentId, updated);
        }
    }

    @Override
    public void decrementLikeCount(Long contentId) {
        int updated = contentMapper.decrementLikeCount(contentId);
        if (updated > 0) {
            log.info("内容[{}]点赞数-1, 影响行数: {}", contentId, updated);
        } else {
            log.warn("内容[{}]点赞数减少失败, 影响行数: {}", contentId, updated);
        }
    }

    @Override
    public void incrementCommentCount(Long contentId) {
        int updated = contentMapper.incrementCommentCount(contentId);
        if (updated > 0) {
            log.info("内容[{}]评论数+1, 影响行数: {}", contentId, updated);
        } else {
            log.warn("内容[{}]评论数更新失败, 影响行数: {}", contentId, updated);
        }
    }

    @Override
    public void decrementCommentCount(Long contentId) {
        int updated = contentMapper.decrementCommentCount(contentId);
        if (updated > 0) {
            log.info("内容[{}]评论数-1, 影响行数: {}", contentId, updated);
        } else {
            log.warn("内容[{}]评论数减少失败, 影响行数: {}", contentId, updated);
        }
    }

    @Override
    public void incrementFavoriteCount(Long contentId) {
        int updated = contentMapper.incrementFavoriteCount(contentId);
        if (updated > 0) {
            log.info("内容[{}]收藏数+1, 影响行数: {}", contentId, updated);
        } else {
            log.warn("内容[{}]收藏数更新失败, 影响行数: {}", contentId, updated);
        }
    }

    @Override
    public void decrementFavoriteCount(Long contentId) {
        int updated = contentMapper.decrementFavoriteCount(contentId);
        if (updated > 0) {
            log.info("内容[{}]收藏数-1, 影响行数: {}", contentId, updated);
        } else {
            log.warn("内容[{}]收藏数减少失败, 影响行数: {}", contentId, updated);
        }
    }

    @Override
    public void incrementShareCount(Long contentId) {
        int updated = contentMapper.incrementShareCount(contentId);
        if (updated > 0) {
            log.info("内容[{}]分享数+1, 影响行数: {}", contentId, updated);
        } else {
            log.warn("内容[{}]分享数更新失败, 影响行数: {}", contentId, updated);
        }
    }

    @Override
    public void incrementViewCount(Long contentId) {
        int updated = contentMapper.incrementViewCount(contentId);
        if (updated > 0) {
            log.info("内容[{}]浏览数+1, 影响行数: {}", contentId, updated);
        } else {
            log.warn("内容[{}]浏览数更新失败, 影响行数: {}", contentId, updated);
        }
    }

    @Override
    public ContentStats getContentStats(Long contentId) {
        return contentMapper.getContentStats(contentId);
    }

    @Override
    public Map<Long, ContentStats> getContentStatsBatch(List<Long> contentIds) {
        if (contentIds == null || contentIds.isEmpty()) {
            return new HashMap<>();
        }
        
        List<ContentStats> statsList = contentMapper.getContentStatsBatch(contentIds);
        Map<Long, ContentStats> statsMap = new HashMap<>();
        
        // 注意：这里需要修改ContentStats添加contentId字段
        // 或者通过其他方式关联contentId和统计数据
        for (int i = 0; i < contentIds.size() && i < statsList.size(); i++) {
            statsMap.put(contentIds.get(i), statsList.get(i));
        }
        
        return statsMap;
    }

    @Override
    public void recalculateContentStats(Long contentId) {
        // 重新计算内容的统计数据
        // 这个方法可以用于修复数据不一致的情况
        ContentStats realStats = contentMapper.calculateRealContentStats(contentId);
        if (realStats != null) {
            contentMapper.updateContentStats(contentId, realStats);
            log.info("重新计算内容[{}]统计数据完成: {}", contentId, realStats);
        }
    }
    
    /**
     * 诊断统计数据一致性
     */
    public void diagnoseContentStats(Long contentId) {
        try {
            // 获取当前内容表中的统计数据
            ContentStats currentStats = contentMapper.getContentStats(contentId);
            log.info("内容[{}]当前统计数据: {}", contentId, currentStats);
            
            // 计算实际的统计数据
            ContentStats realStats = contentMapper.calculateRealContentStats(contentId);
            log.info("内容[{}]实际统计数据: {}", contentId, realStats);
            
            // 检查是否一致
            if (currentStats != null && realStats != null) {
                boolean isConsistent = Objects.equals(currentStats.getLikeCount(), realStats.getLikeCount()) &&
                                     Objects.equals(currentStats.getCommentCount(), realStats.getCommentCount()) &&
                                     Objects.equals(currentStats.getFavoriteCount(), realStats.getFavoriteCount()) &&
                                     Objects.equals(currentStats.getShareCount(), realStats.getShareCount());
                
                if (!isConsistent) {
                    log.warn("内容[{}]统计数据不一致！当前: {} vs 实际: {}", contentId, currentStats, realStats);
                    log.info("建议调用 recalculateContentStats({}) 来修复数据", contentId);
                } else {
                    log.info("内容[{}]统计数据一致", contentId);
                }
            }
        } catch (Exception e) {
            log.error("诊断内容[{}]统计数据失败", contentId, e);
        }
    }
}