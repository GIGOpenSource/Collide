package com.gig.collide.social.domain.service.impl;

import com.gig.collide.social.domain.service.InteractionStatsService;
import com.gig.collide.social.infrastructure.mapper.SocialContentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        log.debug("内容[{}]点赞数+1, 影响行数: {}", contentId, updated);
    }

    @Override
    public void decrementLikeCount(Long contentId) {
        int updated = contentMapper.decrementLikeCount(contentId);
        log.debug("内容[{}]点赞数-1, 影响行数: {}", contentId, updated);
    }

    @Override
    public void incrementCommentCount(Long contentId) {
        int updated = contentMapper.incrementCommentCount(contentId);
        log.debug("内容[{}]评论数+1, 影响行数: {}", contentId, updated);
    }

    @Override
    public void decrementCommentCount(Long contentId) {
        int updated = contentMapper.decrementCommentCount(contentId);
        log.debug("内容[{}]评论数-1, 影响行数: {}", contentId, updated);
    }

    @Override
    public void incrementFavoriteCount(Long contentId) {
        int updated = contentMapper.incrementFavoriteCount(contentId);
        log.debug("内容[{}]收藏数+1, 影响行数: {}", contentId, updated);
    }

    @Override
    public void decrementFavoriteCount(Long contentId) {
        int updated = contentMapper.decrementFavoriteCount(contentId);
        log.debug("内容[{}]收藏数-1, 影响行数: {}", contentId, updated);
    }

    @Override
    public void incrementShareCount(Long contentId) {
        int updated = contentMapper.incrementShareCount(contentId);
        log.debug("内容[{}]分享数+1, 影响行数: {}", contentId, updated);
    }

    @Override
    public void incrementViewCount(Long contentId) {
        int updated = contentMapper.incrementViewCount(contentId);
        log.debug("内容[{}]浏览数+1, 影响行数: {}", contentId, updated);
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
}