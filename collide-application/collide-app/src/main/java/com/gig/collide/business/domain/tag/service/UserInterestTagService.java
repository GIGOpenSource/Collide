package com.gig.collide.business.domain.tag.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.business.domain.tag.entity.Tag;
import com.gig.collide.business.domain.tag.entity.UserInterestTag;
import com.gig.collide.business.infrastructure.mapper.TagMapper;
import com.gig.collide.business.infrastructure.mapper.UserInterestTagMapper;
import com.gig.collide.base.exception.BizException;
import com.gig.collide.base.exception.BizErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户兴趣标签服务
 * 
 * @author collide
 * @date 2024/12/19
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserInterestTagService {
    
    private final UserInterestTagMapper userInterestTagMapper;
    private final TagMapper tagMapper;
    private final TagDomainService tagDomainService;
    
    /**
     * 用户关注标签
     */
    @Transactional(rollbackFor = Exception.class)
    public void followTag(Long userId, Long tagId, String source) {
        
        // 验证标签是否存在
        Tag tag = tagDomainService.getTagById(tagId);
        
        // 检查是否已经关注
        LambdaQueryWrapper<UserInterestTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInterestTag::getUserId, userId)
               .eq(UserInterestTag::getTagId, tagId);
        
        UserInterestTag existingInterest = userInterestTagMapper.selectOne(wrapper);
        
        if (existingInterest != null) {
            if ("active".equals(existingInterest.getStatus())) {
                throw new BizException("已关注该标签", BizErrorCode.DUPLICATED);
            }
            // 重新激活
            existingInterest.setStatus("active");
            existingInterest.setInterestScore(BigDecimal.valueOf(1.0));
            existingInterest.setSource(StringUtils.hasText(source) ? source : "manual");
            existingInterest.setUpdateTime(LocalDateTime.now());
            userInterestTagMapper.updateById(existingInterest);
        } else {
            // 创建新的关注记录
            UserInterestTag userInterestTag = new UserInterestTag();
            userInterestTag.setUserId(userId);
            userInterestTag.setTagId(tagId);
            userInterestTag.setInterestScore(BigDecimal.valueOf(1.0));
            userInterestTag.setSource(StringUtils.hasText(source) ? source : "manual");
            userInterestTag.setStatus("active");
            userInterestTag.setCreateTime(LocalDateTime.now());
            userInterestTag.setUpdateTime(LocalDateTime.now());
            
            userInterestTagMapper.insert(userInterestTag);
        }
        
        // 增加标签使用次数
        tagDomainService.incrementUsageCount(tagId);
        
        log.info("用户关注标签成功，用户ID：{}，标签ID：{}", userId, tagId);
    }
    
    /**
     * 用户取消关注标签
     */
    @Transactional(rollbackFor = Exception.class)
    public void unfollowTag(Long userId, Long tagId) {
        
        LambdaQueryWrapper<UserInterestTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInterestTag::getUserId, userId)
               .eq(UserInterestTag::getTagId, tagId)
               .eq(UserInterestTag::getStatus, "active");
        
        UserInterestTag userInterestTag = userInterestTagMapper.selectOne(wrapper);
        if (userInterestTag == null) {
            throw new BizException("未关注该标签", BizErrorCode.DUPLICATED);
        }
        
        userInterestTag.setStatus("inactive");
        userInterestTag.setUpdateTime(LocalDateTime.now());
        
        userInterestTagMapper.updateById(userInterestTag);
        
        log.info("用户取消关注标签成功，用户ID：{}，标签ID：{}", userId, tagId);
    }
    
    /**
     * 批量关注标签
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchFollowTags(Long userId, List<Long> tagIds, String source) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return;
        }
        
        for (Long tagId : tagIds) {
            try {
                followTag(userId, tagId, source);
            } catch (BizException e) {
                log.warn("批量关注标签时跳过已关注的标签，用户ID：{}，标签ID：{}", userId, tagId);
            }
        }
        
        log.info("批量关注标签完成，用户ID：{}，标签数量：{}", userId, tagIds.size());
    }
    
    /**
     * 获取用户关注的标签
     */
    public List<UserInterestTag> getUserInterestTags(Long userId, String tagType) {
        LambdaQueryWrapper<UserInterestTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInterestTag::getUserId, userId)
               .eq(UserInterestTag::getStatus, "active")
               .orderByDesc(UserInterestTag::getInterestScore)
               .orderByDesc(UserInterestTag::getUpdateTime);
        
        List<UserInterestTag> userInterestTags = userInterestTagMapper.selectList(wrapper);
        
        if (CollectionUtils.isEmpty(userInterestTags)) {
            return new ArrayList<>();
        }
        
        // 如果指定了标签类型，需要过滤
        if (StringUtils.hasText(tagType)) {
            List<Long> tagIds = userInterestTags.stream()
                .map(UserInterestTag::getTagId)
                .collect(Collectors.toList());
            
            LambdaQueryWrapper<Tag> tagWrapper = new LambdaQueryWrapper<>();
            tagWrapper.in(Tag::getId, tagIds)
                     .eq(Tag::getTagType, tagType)
                     .eq(Tag::getStatus, "active");
            
            List<Tag> filteredTags = tagMapper.selectList(tagWrapper);
            List<Long> filteredTagIds = filteredTags.stream()
                .map(Tag::getId)
                .collect(Collectors.toList());
            
            userInterestTags = userInterestTags.stream()
                .filter(uit -> filteredTagIds.contains(uit.getTagId()))
                .collect(Collectors.toList());
        }
        
        return userInterestTags;
    }
    
    /**
     * 获取用户关注的标签详情
     */
    public List<Tag> getUserFollowedTags(Long userId, String tagType) {
        List<UserInterestTag> userInterestTags = getUserInterestTags(userId, tagType);
        
        if (CollectionUtils.isEmpty(userInterestTags)) {
            return new ArrayList<>();
        }
        
        List<Long> tagIds = userInterestTags.stream()
            .map(UserInterestTag::getTagId)
            .collect(Collectors.toList());
        
        return tagDomainService.getTagsByIds(tagIds);
    }
    
    /**
     * 检查用户是否关注了标签
     */
    public boolean isUserFollowedTag(Long userId, Long tagId) {
        LambdaQueryWrapper<UserInterestTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInterestTag::getUserId, userId)
               .eq(UserInterestTag::getTagId, tagId)
               .eq(UserInterestTag::getStatus, "active");
        
        return userInterestTagMapper.selectCount(wrapper) > 0;
    }
    
    /**
     * 批量检查用户是否关注了标签
     */
    public Map<Long, Boolean> batchCheckUserFollowedTags(Long userId, List<Long> tagIds) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return Map.of();
        }
        
        LambdaQueryWrapper<UserInterestTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInterestTag::getUserId, userId)
               .in(UserInterestTag::getTagId, tagIds)
               .eq(UserInterestTag::getStatus, "active");
        
        List<UserInterestTag> userInterestTags = userInterestTagMapper.selectList(wrapper);
        List<Long> followedTagIds = userInterestTags.stream()
            .map(UserInterestTag::getTagId)
            .collect(Collectors.toList());
        
        return tagIds.stream()
            .collect(Collectors.toMap(
                tagId -> tagId,
                followedTagIds::contains
            ));
    }
    
    /**
     * 更新用户对标签的兴趣分数
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateInterestScore(Long userId, Long tagId, BigDecimal score) {
        LambdaQueryWrapper<UserInterestTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInterestTag::getUserId, userId)
               .eq(UserInterestTag::getTagId, tagId)
               .eq(UserInterestTag::getStatus, "active");
        
        UserInterestTag userInterestTag = userInterestTagMapper.selectOne(wrapper);
        if (userInterestTag == null) {
            throw new BizException("用户未关注该标签", BizErrorCode.DUPLICATED);
        }
        
        userInterestTag.setInterestScore(score);
        userInterestTag.setUpdateTime(LocalDateTime.now());
        
        userInterestTagMapper.updateById(userInterestTag);
        
        log.info("更新用户兴趣分数成功，用户ID：{}，标签ID：{}，分数：{}", userId, tagId, score);
    }
    
    /**
     * 基于用户行为分析兴趣标签
     */
    @Transactional(rollbackFor = Exception.class)
    public void analyzeUserInterest(Long userId, List<Long> contentTagIds) {
        if (CollectionUtils.isEmpty(contentTagIds)) {
            return;
        }
        
        // 获取用户当前关注的标签
        List<UserInterestTag> currentInterests = getUserInterestTags(userId, null);
        Map<Long, UserInterestTag> currentInterestMap = currentInterests.stream()
            .collect(Collectors.toMap(UserInterestTag::getTagId, uit -> uit));
        
        for (Long tagId : contentTagIds) {
            UserInterestTag existingInterest = currentInterestMap.get(tagId);
            
            if (existingInterest != null) {
                // 增加兴趣分数
                BigDecimal newScore = existingInterest.getInterestScore().add(BigDecimal.valueOf(0.1));
                if (newScore.compareTo(BigDecimal.valueOf(5.0)) > 0) {
                    newScore = BigDecimal.valueOf(5.0);
                }
                updateInterestScore(userId, tagId, newScore);
            } else {
                // 自动关注新标签（低分数）
                try {
                    followTag(userId, tagId, "behavior");
                    updateInterestScore(userId, tagId, BigDecimal.valueOf(0.5));
                } catch (Exception e) {
                    log.warn("基于行为分析自动关注标签失败，用户ID：{}，标签ID：{}", userId, tagId);
                }
            }
        }
        
        log.info("用户兴趣分析完成，用户ID：{}，分析标签数：{}", userId, contentTagIds.size());
    }
    
    /**
     * 获取标签的关注用户数
     */
    public Long getTagFollowerCount(Long tagId) {
        LambdaQueryWrapper<UserInterestTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInterestTag::getTagId, tagId)
               .eq(UserInterestTag::getStatus, "active");
        
        return userInterestTagMapper.selectCount(wrapper);
    }
    
    /**
     * 获取多个标签的关注用户数
     */
    public Map<Long, Long> batchGetTagFollowerCounts(List<Long> tagIds) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return Map.of();
        }
        
        return tagIds.stream()
            .collect(Collectors.toMap(
                tagId -> tagId,
                this::getTagFollowerCount
            ));
    }
    
    /**
     * 获取用户兴趣标签统计
     */
    public Map<String, Object> getUserInterestStatistics(Long userId) {
        LambdaQueryWrapper<UserInterestTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInterestTag::getUserId, userId)
               .eq(UserInterestTag::getStatus, "active");
        
        List<UserInterestTag> userInterestTags = userInterestTagMapper.selectList(wrapper);
        
        if (CollectionUtils.isEmpty(userInterestTags)) {
            return Map.of(
                "totalCount", 0,
                "averageScore", 0.0,
                "topInterestTags", new ArrayList<>()
            );
        }
        
        // 计算统计数据
        long totalCount = userInterestTags.size();
        double averageScore = userInterestTags.stream()
            .mapToDouble(uit -> uit.getInterestScore().doubleValue())
            .average()
            .orElse(0.0);
        
        // 获取前5个最感兴趣的标签
        List<Long> topTagIds = userInterestTags.stream()
            .sorted((a, b) -> b.getInterestScore().compareTo(a.getInterestScore()))
            .limit(5)
            .map(UserInterestTag::getTagId)
            .collect(Collectors.toList());
        
        List<Tag> topTags = tagDomainService.getTagsByIds(topTagIds);
        
        return Map.of(
            "totalCount", totalCount,
            "averageScore", Math.round(averageScore * 100.0) / 100.0,
            "topInterestTags", topTags
        );
    }
    
    /**
     * 设置用户兴趣标签（替换现有的）
     */
    @Transactional(rollbackFor = Exception.class)
    public void setUserInterestTags(Long userId, List<Long> tagIds, String source) {
        // 先取消所有现有的关注
        LambdaQueryWrapper<UserInterestTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInterestTag::getUserId, userId)
               .eq(UserInterestTag::getStatus, "active");
        
        List<UserInterestTag> existingInterests = userInterestTagMapper.selectList(wrapper);
        for (UserInterestTag interest : existingInterests) {
            interest.setStatus("inactive");
            interest.setUpdateTime(LocalDateTime.now());
            userInterestTagMapper.updateById(interest);
        }
        
        // 批量关注新标签
        if (!CollectionUtils.isEmpty(tagIds)) {
            batchFollowTags(userId, tagIds, source);
        }
        
        log.info("设置用户兴趣标签完成，用户ID：{}，新标签数：{}", userId, tagIds != null ? tagIds.size() : 0);
    }
    
    /**
     * 推荐标签给用户
     */
    public List<Tag> recommendTagsToUser(Long userId, Integer limit) {
        if (limit == null) {
            limit = 10;
        }
        
        // 获取用户已关注的标签
        List<UserInterestTag> userInterests = getUserInterestTags(userId, null);
        List<Long> followedTagIds = userInterests.stream()
            .map(UserInterestTag::getTagId)
            .collect(Collectors.toList());
        
        // 获取热门标签，排除已关注的
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getStatus, "active");
        
        if (!CollectionUtils.isEmpty(followedTagIds)) {
            wrapper.notIn(Tag::getId, followedTagIds);
        }
        
        wrapper.orderByDesc(Tag::getHeatScore)
               .orderByDesc(Tag::getUsageCount)
               .last("LIMIT " + limit);
        
        List<Tag> recommendedTags = tagMapper.selectList(wrapper);
        log.info("为用户推荐标签，用户ID：{}，推荐数量：{}", userId, recommendedTags.size());
        
        return recommendedTags;
    }
} 