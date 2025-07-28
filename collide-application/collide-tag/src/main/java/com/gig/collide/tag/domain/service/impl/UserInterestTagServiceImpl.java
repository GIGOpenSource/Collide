package com.gig.collide.tag.domain.service.impl;

import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheRefresh;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.gig.collide.api.tag.request.UserInterestTagRequest;
import com.gig.collide.base.exception.BizException;
import com.gig.collide.base.exception.CommonErrorCode;
import com.gig.collide.cache.constant.CacheConstant;
import com.gig.collide.tag.domain.entity.Tag;
import com.gig.collide.tag.domain.entity.UserInterestTag;
import com.gig.collide.tag.domain.service.TagService;
import com.gig.collide.tag.domain.service.UserInterestTagService;
import com.gig.collide.tag.infrastructure.mapper.UserInterestTagMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户兴趣标签业务服务实现类
 * 
 * @author GIG Team
 * @version 1.0.0
 * @since 2024-12-19
 */
@Slf4j
@Service
public class UserInterestTagServiceImpl implements UserInterestTagService {

    @Autowired
    private UserInterestTagMapper userInterestTagMapper;
    
    @Autowired
    private TagService tagService;

    private static final String DEFAULT_SOURCE = "manual";
    private static final String DEFAULT_STATUS = "active";
    private static final BigDecimal DEFAULT_INTEREST_SCORE = new BigDecimal("50.0");
    private static final BigDecimal MAX_INTEREST_SCORE = new BigDecimal("100.0");
    private static final BigDecimal MIN_INTEREST_SCORE = new BigDecimal("0.0");

    @Override
    @Cached(name = CacheConstant.TAG_USER_INTEREST_CACHE, 
            cacheType = CacheType.BOTH, 
            key = "#userId", 
            expire = CacheConstant.TAG_USER_INTEREST_CACHE_EXPIRE, 
            localExpire = CacheConstant.LOCAL_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheNullValue = true)
    @CacheRefresh(refresh = CacheConstant.TAG_USER_INTEREST_CACHE_EXPIRE, timeUnit = TimeUnit.MINUTES)
    public List<UserInterestTag> getUserInterestTags(Long userId) {
        if (userId == null) {
            return List.of();
        }
        log.debug("从数据库查询用户兴趣标签，用户ID：{}", userId);
        return userInterestTagMapper.selectByUserId(userId, DEFAULT_STATUS);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = CacheConstant.TAG_USER_INTEREST_CACHE, key = "#request.userId")
    @CacheInvalidate(name = CacheConstant.TAG_RECOMMEND_CACHE, key = "'recommend:' + #request.userId + '*'")
    public boolean setUserInterestTags(UserInterestTagRequest request) {
        log.info("设置用户兴趣标签开始，请求参数：{}", request);
        
        if (request == null || request.getUserId() == null) {
            throw new BizException("用户ID不能为空", CommonErrorCode.PARAM_INVALID);
        }
        
        try {
            // 清空用户现有的兴趣标签
            clearUserInterestTags(request.getUserId());

            // 添加新的兴趣标签
            if (!CollectionUtils.isEmpty(request.getInterestTags())) {
                for (UserInterestTagRequest.UserInterestTagItem item : request.getInterestTags()) {
                    // 验证标签是否存在
                    Tag tag = tagService.getTagById(item.getTagId());
                    if (tag == null) {
                        log.warn("标签不存在，跳过添加，标签ID：{}", item.getTagId());
                        continue;
                    }

                    UserInterestTag userInterestTag = new UserInterestTag();
                    userInterestTag.setUserId(request.getUserId());
                    userInterestTag.setTagId(item.getTagId());
                    userInterestTag.setInterestScore(item.getInterestScore() != null ? 
                        BigDecimal.valueOf(item.getInterestScore()) : DEFAULT_INTEREST_SCORE);
                    userInterestTag.setSource(item.getSource() != null ? item.getSource() : DEFAULT_SOURCE);
                    userInterestTag.setStatus(DEFAULT_STATUS);
                    userInterestTag.setCreateTime(LocalDateTime.now());
                    userInterestTag.setUpdateTime(LocalDateTime.now());
                    userInterestTag.setVersion(1);

                    userInterestTagMapper.insert(userInterestTag);
                }
            }

            log.info("设置用户兴趣标签成功，用户ID：{}", request.getUserId());
            return true;
        } catch (Exception e) {
            log.error("设置用户兴趣标签失败", e);
            throw new BizException("设置用户兴趣标签失败", CommonErrorCode.SYSTEM_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = CacheConstant.TAG_USER_INTEREST_CACHE, key = "#userId")
    @CacheInvalidate(name = CacheConstant.TAG_RECOMMEND_CACHE, key = "'recommend:' + #userId + '*'")
    public boolean addUserInterestTag(Long userId, Long tagId, Double interestScore) {
        if (userId == null || tagId == null) {
            throw new BizException("用户ID和标签ID不能为空", CommonErrorCode.PARAM_INVALID);
        }

        // 检查是否已存在
        UserInterestTag existing = userInterestTagMapper.selectByUserIdAndTagId(userId, tagId);
        if (existing != null) {
            throw new BizException("用户已关注该标签", CommonErrorCode.RESOURCE_ALREADY_EXISTS);
        }

        // 验证标签是否存在
        Tag tag = tagService.getTagById(tagId);
        if (tag == null) {
            throw new BizException("标签不存在", CommonErrorCode.RESOURCE_NOT_FOUND);
        }

        UserInterestTag userInterestTag = new UserInterestTag();
        userInterestTag.setUserId(userId);
        userInterestTag.setTagId(tagId);
        userInterestTag.setInterestScore(interestScore != null ? 
            BigDecimal.valueOf(interestScore) : DEFAULT_INTEREST_SCORE);
        userInterestTag.setSource(DEFAULT_SOURCE);
        userInterestTag.setStatus(DEFAULT_STATUS);
        userInterestTag.setCreateTime(LocalDateTime.now());
        userInterestTag.setUpdateTime(LocalDateTime.now());
        userInterestTag.setVersion(1);

        int result = userInterestTagMapper.insert(userInterestTag);
        return result == 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = CacheConstant.TAG_USER_INTEREST_CACHE, key = "#userId")
    @CacheInvalidate(name = CacheConstant.TAG_RECOMMEND_CACHE, key = "'recommend:' + #userId + '*'")
    public boolean removeUserInterestTag(Long userId, Long tagId) {
        if (userId == null || tagId == null) {
            throw new BizException("用户ID和标签ID不能为空", CommonErrorCode.PARAM_INVALID);
        }

        UserInterestTag existing = userInterestTagMapper.selectByUserIdAndTagId(userId, tagId);
        if (existing == null) {
            throw new BizException("用户未关注该标签", CommonErrorCode.RESOURCE_NOT_FOUND);
        }

        // 软删除：更新状态为inactive
        existing.setStatus("inactive");
        existing.setUpdateTime(LocalDateTime.now());

        int result = userInterestTagMapper.updateById(existing);
        return result == 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateInterestScore(Long userId, Long tagId, BigDecimal interestScore) {
        if (userId == null || tagId == null || interestScore == null) {
            return false;
        }

        UserInterestTag existing = userInterestTagMapper.selectByUserIdAndTagId(userId, tagId);
        if (existing == null) {
            return false;
        }

        // 限制兴趣分数范围
        BigDecimal finalScore = interestScore;
        if (interestScore.compareTo(MAX_INTEREST_SCORE) > 0) {
            finalScore = MAX_INTEREST_SCORE;
        } else if (interestScore.compareTo(MIN_INTEREST_SCORE) < 0) {
            finalScore = MIN_INTEREST_SCORE;
        }

        existing.setInterestScore(finalScore);
        existing.setUpdateTime(LocalDateTime.now());

        int result = userInterestTagMapper.updateById(existing);
        return result == 1;
    }

    @Override
    public UserInterestTag getUserInterestTag(Long userId, Long tagId) {
        if (userId == null || tagId == null) {
            return null;
        }
        return userInterestTagMapper.selectByUserIdAndTagId(userId, tagId);
    }

    @Override
    public List<UserInterestTag> getInterestedUsers(Long tagId) {
        if (tagId == null) {
            return List.of();
        }
        return userInterestTagMapper.selectByTagId(tagId, DEFAULT_STATUS);
    }

    @Override
    @Cached(name = CacheConstant.TAG_RECOMMEND_CACHE, 
            cacheType = CacheType.BOTH, 
            key = "'recommend:' + #userId + ':' + #limit", 
            expire = CacheConstant.TAG_RECOMMEND_CACHE_EXPIRE, 
            localExpire = CacheConstant.LOCAL_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheNullValue = true)
    public List<Long> recommendTagsToUser(Long userId, Integer limit) {
        if (userId == null) {
            return List.of();
        }

        // 简单推荐逻辑：基于用户已关注标签的类型推荐热门标签
        List<UserInterestTag> userInterestTags = getUserInterestTags(userId);
        if (CollectionUtils.isEmpty(userInterestTags)) {
            // 如果用户没有关注任何标签，推荐通用热门标签
            List<Tag> hotTags = tagService.getHotTags(null, limit);
            return hotTags.stream().map(Tag::getId).collect(Collectors.toList());
        }

        // 获取用户已关注的标签ID
        List<Long> followedTagIds = userInterestTags.stream()
                .map(UserInterestTag::getTagId)
                .collect(Collectors.toList());

        // 获取用户关注标签的类型统计
        Map<String, Long> typeCount = userInterestTags.stream()
                .map(uit -> tagService.getTagById(uit.getTagId()))
                .filter(tag -> tag != null)
                .collect(Collectors.groupingBy(
                        Tag::getTagType,
                        Collectors.counting()
                ));

        // 基于最常关注的标签类型推荐
        String mostInterestedType = typeCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (mostInterestedType != null) {
            List<Tag> recommendedTags = tagService.getHotTags(mostInterestedType, limit != null ? limit * 2 : 20);
            // 过滤掉已关注的标签
            return recommendedTags.stream()
                    .filter(tag -> !followedTagIds.contains(tag.getId()))
                    .limit(limit != null ? limit : 10)
                    .map(Tag::getId)
                    .collect(Collectors.toList());
        }

        List<Tag> hotTags = tagService.getHotTags(null, limit);
        return hotTags.stream().map(Tag::getId).collect(Collectors.toList());
    }

    @Override
    public Map<String, BigDecimal> calculateUserInterestByType(Long userId) {
        if (userId == null) {
            return Map.of();
        }

        List<UserInterestTag> userInterestTags = getUserInterestTags(userId);
        if (CollectionUtils.isEmpty(userInterestTags)) {
            return Map.of();
        }

        // 计算各类型的平均兴趣分数
        return userInterestTags.stream()
                .collect(Collectors.groupingBy(
                        uit -> {
                            Tag tag = tagService.getTagById(uit.getTagId());
                            return tag != null ? tag.getTagType() : "unknown";
                        },
                        Collectors.averagingDouble(uit -> uit.getInterestScore().doubleValue())
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> BigDecimal.valueOf(entry.getValue())
                ));
    }

    @Override
    public long countUserInterestTags(Long userId) {
        if (userId == null) {
            return 0;
        }
        return userInterestTagMapper.countByUserId(userId, DEFAULT_STATUS);
    }

    @Override
    public long countInterestedUsers(Long tagId) {
        if (tagId == null) {
            return 0;
        }
        return userInterestTagMapper.countByTagId(tagId, DEFAULT_STATUS);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean clearUserInterestTags(Long userId) {
        if (userId == null) {
            throw new BizException("用户ID不能为空", CommonErrorCode.PARAM_INVALID);
        }

        try {
            int result = userInterestTagMapper.deleteByUserId(userId);
            log.info("清空用户兴趣标签成功，用户ID：{}，清空数量：{}", userId, result);
            return true;
        } catch (Exception e) {
            log.error("清空用户兴趣标签失败，用户ID：{}", userId, e);
            throw new BizException("清空用户兴趣标签失败", CommonErrorCode.SYSTEM_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean clearTagInterestUsers(Long tagId) {
        if (tagId == null) {
            throw new BizException("标签ID不能为空", CommonErrorCode.PARAM_INVALID);
        }

        try {
            int result = userInterestTagMapper.deleteByTagId(tagId);
            log.info("清空标签关注用户成功，标签ID：{}，清空数量：{}", tagId, result);
            return true;
        } catch (Exception e) {
            log.error("清空标签关注用户失败，标签ID：{}", tagId, e);
            throw new BizException("清空标签关注用户失败", CommonErrorCode.SYSTEM_ERROR);
        }
    }
} 