package com.gig.collide.business.domain.tag.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import java.util.stream.Collectors;

/**
 * 标签领域服务
 * 
 * @author collide
 * @date 2024/12/19
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TagDomainService {
    
    private final TagMapper tagMapper;
    private final UserInterestTagMapper userInterestTagMapper;
    
    /**
     * 创建标签
     */
    @Transactional(rollbackFor = Exception.class)
    public Tag createTag(String name, String description, String color, 
                        String tagType, Long categoryId) {
        
        // 检查标签名称是否已存在
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getName, name)
               .eq(Tag::getTagType, tagType)
               .eq(Tag::getStatus, "active");
        
        Tag existingTag = tagMapper.selectOne(wrapper);
        if (existingTag != null) {
            throw new BizException("标签名称已存在", BizErrorCode.DUPLICATED);
        }
        
        // 创建新标签
        Tag tag = new Tag();
        tag.setName(name);
        tag.setDescription(description);
        tag.setColor(StringUtils.hasText(color) ? color : "#1890ff");
        tag.setTagType(tagType);
        tag.setCategoryId(categoryId);
        tag.setUsageCount(0L);
        tag.setHeatScore(BigDecimal.ZERO);
        tag.setStatus("active");
        tag.setCreateTime(LocalDateTime.now());
        tag.setUpdateTime(LocalDateTime.now());
        
        tagMapper.insert(tag);
        log.info("创建标签成功，标签ID：{}，名称：{}", tag.getId(), tag.getName());
        
        return tag;
    }
    
    /**
     * 创建标签（支持更多参数）
     */
    @Transactional(rollbackFor = Exception.class)
    public Tag createTag(String name, String description, Long categoryId, 
                        String color, String iconUrl, Integer sort, Integer status) {
        
        // 设置默认值
        String tagType = "content"; // 默认为内容标签
        String statusStr = status != null && status == 1 ? "active" : "inactive";
        
        // 检查标签名称是否已存在
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getName, name)
               .eq(Tag::getTagType, tagType)
               .eq(Tag::getStatus, "active");
        
        Tag existingTag = tagMapper.selectOne(wrapper);
        if (existingTag != null) {
            throw new BizException("标签名称已存在", BizErrorCode.DUPLICATED);
        }
        
        // 创建新标签
        Tag tag = new Tag();
        tag.setName(name);
        tag.setDescription(description);
        tag.setColor(StringUtils.hasText(color) ? color : "#1890ff");
        tag.setIconUrl(iconUrl);
        tag.setTagType(tagType);
        tag.setCategoryId(categoryId);
        tag.setUsageCount(0L);
        tag.setHeatScore(BigDecimal.ZERO);
        tag.setStatus(statusStr);
        tag.setCreateTime(LocalDateTime.now());
        tag.setUpdateTime(LocalDateTime.now());
        
        tagMapper.insert(tag);
        log.info("创建标签成功，标签ID：{}，名称：{}", tag.getId(), tag.getName());
        
        return tag;
    }
    
    /**
     * 更新标签信息
     */
    @Transactional(rollbackFor = Exception.class)
    public Tag updateTag(Long tagId, String name, String description, 
                        String color, Long categoryId) {
        
        Tag tag = getTagById(tagId);
        
        // 如果修改了名称，检查是否重复
        if (StringUtils.hasText(name) && !name.equals(tag.getName())) {
            LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Tag::getName, name)
                   .eq(Tag::getTagType, tag.getTagType())
                   .eq(Tag::getStatus, "active")
                   .ne(Tag::getId, tagId);
            
            Tag existingTag = tagMapper.selectOne(wrapper);
            if (existingTag != null) {
                throw new BizException("标签名称已存在", BizErrorCode.DUPLICATED);
            }
            tag.setName(name);
        }
        
        if (StringUtils.hasText(description)) {
            tag.setDescription(description);
        }
        if (StringUtils.hasText(color)) {
            tag.setColor(color);
        }
        if (categoryId != null) {
            tag.setCategoryId(categoryId);
        }
        
        tag.setUpdateTime(LocalDateTime.now());
        tagMapper.updateById(tag);
        
        log.info("更新标签成功，标签ID：{}", tagId);
        return tag;
    }
    
    /**
     * 更新标签信息（支持更多参数）
     */
    @Transactional(rollbackFor = Exception.class)
    public Tag updateTag(Long tagId, String name, String description, Long categoryId,
                        String color, String iconUrl, Integer sort, String status) {
        
        Tag tag = getTagById(tagId);
        
        // 如果修改了名称，检查是否重复
        if (StringUtils.hasText(name) && !name.equals(tag.getName())) {
            LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Tag::getName, name)
                   .eq(Tag::getTagType, tag.getTagType())
                   .eq(Tag::getStatus, "active")
                   .ne(Tag::getId, tagId);
            
            Tag existingTag = tagMapper.selectOne(wrapper);
            if (existingTag != null) {
                throw new BizException("标签名称已存在", BizErrorCode.DUPLICATED);
            }
            tag.setName(name);
        }
        
        if (StringUtils.hasText(description)) {
            tag.setDescription(description);
        }
        if (StringUtils.hasText(color)) {
            tag.setColor(color);
        }
        if (StringUtils.hasText(iconUrl)) {
            tag.setIconUrl(iconUrl);
        }
        if (categoryId != null) {
            tag.setCategoryId(categoryId);
        }
        if (StringUtils.hasText(status)) {
            tag.setStatus(status);
        }
        
        tag.setUpdateTime(LocalDateTime.now());
        tagMapper.updateById(tag);
        
        log.info("更新标签成功，标签ID：{}", tagId);
        return tag;
    }
    
    /**
     * 根据ID获取标签
     */
    public Tag getTagById(Long tagId) {
        Tag tag = tagMapper.selectById(tagId);
        if (tag == null || "inactive".equals(tag.getStatus())) {
            throw new BizException("标签不存在", BizErrorCode.DUPLICATED);
        }
        return tag;
    }
    
    /**
     * 分页查询标签
     */
    public Page<Tag> queryTags(String name, Integer categoryId, String status,
                              Integer pageNo, Integer pageSize) {
        
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(name)) {
            wrapper.like(Tag::getName, name);
        }
        if (categoryId != null) {
            wrapper.eq(Tag::getCategoryId, categoryId.longValue());
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Tag::getStatus, status);
        } else {
            wrapper.eq(Tag::getStatus, "active");
        }
        
        wrapper.orderByDesc(Tag::getHeatScore)
               .orderByDesc(Tag::getUsageCount)
               .orderByDesc(Tag::getCreateTime);
        
        Page<Tag> page = new Page<>(pageNo != null ? pageNo : 1, pageSize != null ? pageSize : 10);
        return tagMapper.selectPage(page, wrapper);
    }
    
    /**
     * 获取热门标签
     */
    public List<Tag> getHotTags(String tagType, Integer limit) {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getStatus, "active");
        
        if (StringUtils.hasText(tagType)) {
            wrapper.eq(Tag::getTagType, tagType);
        }
        
        wrapper.orderByDesc(Tag::getHeatScore)
               .orderByDesc(Tag::getUsageCount)
               .last("LIMIT " + (limit != null ? limit : 20));
        
        return tagMapper.selectList(wrapper);
    }
    
    /**
     * 根据分类获取标签
     */
    public List<Tag> getTagsByCategory(Long categoryId) {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getCategoryId, categoryId)
               .eq(Tag::getStatus, "active")
               .orderByDesc(Tag::getHeatScore)
               .orderByDesc(Tag::getUsageCount);
        
        return tagMapper.selectList(wrapper);
    }
    
    /**
     * 批量获取标签
     */
    public List<Tag> getTagsByIds(List<Long> tagIds) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return new ArrayList<>();
        }
        
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Tag::getId, tagIds)
               .eq(Tag::getStatus, "active");
        
        return tagMapper.selectList(wrapper);
    }
    
    /**
     * 搜索标签
     */
    public List<Tag> searchTags(String keyword, String tagType, Integer limit) {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getStatus, "active");
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Tag::getName, keyword);
        }
        if (StringUtils.hasText(tagType)) {
            wrapper.eq(Tag::getTagType, tagType);
        }
        
        wrapper.orderByDesc(Tag::getHeatScore)
               .orderByDesc(Tag::getUsageCount)
               .last("LIMIT " + (limit != null ? limit : 10));
        
        return tagMapper.selectList(wrapper);
    }
    
    /**
     * 增加标签使用次数
     */
    @Transactional(rollbackFor = Exception.class)
    public void incrementUsageCount(Long tagId) {
        Tag tag = getTagById(tagId);
        tag.setUsageCount(tag.getUsageCount() + 1);
        tag.setUpdateTime(LocalDateTime.now());
        
        // 重新计算热度分数
        calculateHeatScore(tag);
        
        tagMapper.updateById(tag);
        log.info("标签使用次数增加，标签ID：{}，当前使用次数：{}", tagId, tag.getUsageCount());
    }
    
    /**
     * 批量增加标签使用次数
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchIncrementUsageCount(List<Long> tagIds) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return;
        }
        
        List<Tag> tags = getTagsByIds(tagIds);
        for (Tag tag : tags) {
            tag.setUsageCount(tag.getUsageCount() + 1);
            tag.setUpdateTime(LocalDateTime.now());
            calculateHeatScore(tag);
        }
        
        // 批量更新
        for (Tag tag : tags) {
            tagMapper.updateById(tag);
        }
        
        log.info("批量增加标签使用次数，标签数量：{}", tags.size());
    }
    
    /**
     * 计算标签热度分数
     * 热度 = 使用次数 * 0.7 + 兴趣用户数 * 0.3
     */
    private void calculateHeatScore(Tag tag) {
        // 获取关注此标签的用户数
        LambdaQueryWrapper<UserInterestTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInterestTag::getTagId, tag.getId())
               .eq(UserInterestTag::getStatus, "active");
        
        Long interestedUserCount = userInterestTagMapper.selectCount(wrapper);
        
        // 计算热度分数
        double heatScore = tag.getUsageCount() * 0.7 + interestedUserCount * 0.3;
        tag.setHeatScore(BigDecimal.valueOf(heatScore));
    }
    
    /**
     * 删除标签（软删除）
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteTag(Long tagId) {
        Tag tag = getTagById(tagId);
        tag.setStatus("inactive");
        tag.setUpdateTime(LocalDateTime.now());
        
        tagMapper.updateById(tag);
        log.info("删除标签成功，标签ID：{}", tagId);
    }
    
    /**
     * 获取推荐标签（基于用户兴趣）
     */
    public List<Tag> getRecommendedTags(Long userId, String tagType, Integer limit) {
        // 获取用户已关注的标签
        LambdaQueryWrapper<UserInterestTag> interestWrapper = new LambdaQueryWrapper<>();
        interestWrapper.eq(UserInterestTag::getUserId, userId)
                      .eq(UserInterestTag::getStatus, "active");
        
        List<UserInterestTag> userInterests = userInterestTagMapper.selectList(interestWrapper);
        List<Long> userTagIds = userInterests.stream()
            .map(UserInterestTag::getTagId)
            .collect(Collectors.toList());
        
        // 查询推荐标签（热门标签，排除用户已关注的）
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getStatus, "active");
        
        if (StringUtils.hasText(tagType)) {
            wrapper.eq(Tag::getTagType, tagType);
        }
        
        if (!CollectionUtils.isEmpty(userTagIds)) {
            wrapper.notIn(Tag::getId, userTagIds);
        }
        
        wrapper.orderByDesc(Tag::getHeatScore)
               .orderByDesc(Tag::getUsageCount)
               .last("LIMIT " + (limit != null ? limit : 10));
        
        return tagMapper.selectList(wrapper);
    }

    /**
     * 根据类型获取标签
     */
    public List<Tag> getTagsByType(String tagType) {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getStatus, "active");
        
        if (StringUtils.hasText(tagType)) {
            wrapper.eq(Tag::getTagType, tagType);
        }
        
        wrapper.orderByDesc(Tag::getHeatScore)
               .orderByDesc(Tag::getUsageCount);
        
        return tagMapper.selectList(wrapper);
    }
} 