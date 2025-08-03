package com.gig.collide.tag.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.tag.domain.entity.Tag;
import com.gig.collide.tag.domain.entity.UserInterestTag;
import com.gig.collide.tag.domain.entity.ContentTag;
import com.gig.collide.tag.domain.service.TagService;
import com.gig.collide.tag.infrastructure.mapper.TagMapper;
import com.gig.collide.tag.infrastructure.mapper.UserInterestTagMapper;
import com.gig.collide.tag.infrastructure.mapper.ContentTagMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签服务实现类 - 简洁版
 *
 * @author GIG Team
 * @version 2.0.0
 */
@Slf4j
@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;
    
    @Autowired
    private UserInterestTagMapper userInterestTagMapper;
    
    @Autowired
    private ContentTagMapper contentTagMapper;

    @Override
    @Transactional
    public Tag createTag(Tag tag) {
        // 检查标签名称是否重复
        int count = tagMapper.countByNameAndType(tag.getName(), tag.getTagType());
        if (count > 0) {
            throw new RuntimeException("同类型下标签名称已存在");
        }
        
        // 设置默认值
        if (tag.getUsageCount() == null) {
            tag.setUsageCount(0L);
        }
        if (!StringUtils.hasText(tag.getStatus())) {
            tag.setStatus("active");
        }
        
        tagMapper.insert(tag);
        return tag;
    }

    @Override
    @Transactional
    public Tag updateTag(Tag tag) {
        Tag existingTag = tagMapper.selectById(tag.getId());
        if (existingTag == null) {
            throw new RuntimeException("标签不存在");
        }
        
        // 如果修改了名称或类型，检查是否重复
        if (!existingTag.getName().equals(tag.getName()) || 
            !existingTag.getTagType().equals(tag.getTagType())) {
            int count = tagMapper.countByNameAndType(tag.getName(), tag.getTagType());
            if (count > 0) {
                throw new RuntimeException("同类型下标签名称已存在");
            }
        }
        
        tagMapper.updateById(tag);
        return tag;
    }

    @Override
    @Transactional
    public void deleteTag(Long tagId) {
        Tag tag = tagMapper.selectById(tagId);
        if (tag != null) {
            tag.setStatus("inactive");
            tagMapper.updateById(tag);
        }
    }

    @Override
    public Tag getTagById(Long tagId) {
        return tagMapper.selectById(tagId);
    }

    @Override
    public IPage<Tag> queryTags(int pageNum, int pageSize, String name, String tagType, Long categoryId, String status) {
        Page<Tag> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(name)) {
            queryWrapper.like(Tag::getName, name);
        }
        if (StringUtils.hasText(tagType)) {
            queryWrapper.eq(Tag::getTagType, tagType);
        }
        if (categoryId != null) {
            queryWrapper.eq(Tag::getCategoryId, categoryId);
        }
        if (StringUtils.hasText(status)) {
            queryWrapper.eq(Tag::getStatus, status);
        }
        
        queryWrapper.orderByDesc(Tag::getUsageCount).orderByDesc(Tag::getCreateTime);
        
        return tagMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<Tag> getTagsByType(String tagType) {
        return tagMapper.selectByTagType(tagType);
    }

    @Override
    public List<Tag> searchTags(String keyword, Integer limit) {
        return tagMapper.searchByName(keyword, limit);
    }

    @Override
    public List<Tag> getHotTags(Integer limit) {
        return tagMapper.selectHotTags(limit);
    }

    @Override
    @Transactional
    public void increaseTagUsage(Long tagId) {
        tagMapper.increaseUsageCount(tagId);
    }

    @Override
    public List<Tag> getUserInterestTags(Long userId) {
        List<UserInterestTag> userInterestTags = userInterestTagMapper.selectByUserId(userId);
        List<Long> tagIds = userInterestTags.stream()
                .map(UserInterestTag::getTagId)
                .collect(Collectors.toList());
        
        if (tagIds.isEmpty()) {
            return List.of();
        }
        
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Tag::getId, tagIds).eq(Tag::getStatus, "active");
        return tagMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public void addUserInterestTag(Long userId, Long tagId, BigDecimal interestScore) {
        // 检查是否已存在
        int count = userInterestTagMapper.countByUserIdAndTagId(userId, tagId);
        if (count > 0) {
            throw new RuntimeException("用户已关注此标签");
        }
        
        UserInterestTag userInterestTag = new UserInterestTag();
        userInterestTag.setUserId(userId);
        userInterestTag.setTagId(tagId);
        userInterestTag.setInterestScore(interestScore != null ? interestScore : BigDecimal.valueOf(50.0));
        userInterestTag.setStatus("active");
        
        userInterestTagMapper.insert(userInterestTag);
    }

    @Override
    @Transactional
    public void removeUserInterestTag(Long userId, Long tagId) {
        LambdaQueryWrapper<UserInterestTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInterestTag::getUserId, userId)
                   .eq(UserInterestTag::getTagId, tagId);
        userInterestTagMapper.delete(queryWrapper);
    }

    @Override
    @Transactional
    public void updateUserInterestScore(Long userId, Long tagId, BigDecimal interestScore) {
        userInterestTagMapper.updateInterestScore(userId, tagId, interestScore);
    }

    @Override
    @Transactional
    public void addContentTag(Long contentId, Long tagId) {
        // 检查是否已存在
        int count = contentTagMapper.countByContentIdAndTagId(contentId, tagId);
        if (count > 0) {
            return; // 已存在，直接返回
        }
        
        ContentTag contentTag = new ContentTag();
        contentTag.setContentId(contentId);
        contentTag.setTagId(tagId);
        
        contentTagMapper.insert(contentTag);
        
        // 增加标签使用次数
        increaseTagUsage(tagId);
    }

    @Override
    @Transactional
    public void removeContentTag(Long contentId, Long tagId) {
        LambdaQueryWrapper<ContentTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ContentTag::getContentId, contentId)
                   .eq(ContentTag::getTagId, tagId);
        contentTagMapper.delete(queryWrapper);
    }

    @Override
    public List<Tag> getContentTags(Long contentId) {
        List<ContentTag> contentTags = contentTagMapper.selectByContentId(contentId);
        List<Long> tagIds = contentTags.stream()
                .map(ContentTag::getTagId)
                .collect(Collectors.toList());
        
        if (tagIds.isEmpty()) {
            return List.of();
        }
        
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Tag::getId, tagIds).eq(Tag::getStatus, "active");
        return tagMapper.selectList(queryWrapper);
    }
} 