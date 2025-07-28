package com.gig.collide.social.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.social.domain.entity.SocialDynamic;
import com.gig.collide.social.domain.service.SocialDynamicService;
import com.gig.collide.social.infrastructure.mapper.SocialDynamicMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 社交动态服务实现类 - 简洁版
 *
 * @author GIG Team
 * @version 2.0.0
 */
@Slf4j
@Service
public class SocialDynamicServiceImpl implements SocialDynamicService {

    @Autowired
    private SocialDynamicMapper socialDynamicMapper;

    @Override
    @Transactional
    public SocialDynamic createDynamic(SocialDynamic dynamic) {
        // 设置默认值
        if (dynamic.getLikeCount() == null) {
            dynamic.setLikeCount(0L);
        }
        if (dynamic.getCommentCount() == null) {
            dynamic.setCommentCount(0L);
        }
        if (dynamic.getShareCount() == null) {
            dynamic.setShareCount(0L);
        }
        if (!StringUtils.hasText(dynamic.getStatus())) {
            dynamic.setStatus("normal");
        }
        
        socialDynamicMapper.insert(dynamic);
        return dynamic;
    }

    @Override
    @Transactional
    public SocialDynamic updateDynamic(SocialDynamic dynamic) {
        SocialDynamic existingDynamic = socialDynamicMapper.selectById(dynamic.getId());
        if (existingDynamic == null) {
            throw new RuntimeException("动态不存在");
        }
        
        socialDynamicMapper.updateById(dynamic);
        return dynamic;
    }

    @Override
    @Transactional
    public void deleteDynamic(Long dynamicId) {
        SocialDynamic dynamic = socialDynamicMapper.selectById(dynamicId);
        if (dynamic != null) {
            dynamic.setStatus("deleted");
            socialDynamicMapper.updateById(dynamic);
        }
    }

    @Override
    public SocialDynamic getDynamicById(Long dynamicId) {
        return socialDynamicMapper.selectById(dynamicId);
    }

    @Override
    public IPage<SocialDynamic> queryDynamics(int pageNum, int pageSize, 
                                             Long userId, String dynamicType, String status, 
                                             String keyword, Long minLikeCount, 
                                             String sortBy, String sortDirection) {
        Page<SocialDynamic> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SocialDynamic> queryWrapper = new LambdaQueryWrapper<>();
        
        if (userId != null) {
            queryWrapper.eq(SocialDynamic::getUserId, userId);
        }
        if (StringUtils.hasText(dynamicType)) {
            queryWrapper.eq(SocialDynamic::getDynamicType, dynamicType);
        }
        if (StringUtils.hasText(status)) {
            queryWrapper.eq(SocialDynamic::getStatus, status);
        } else {
            queryWrapper.eq(SocialDynamic::getStatus, "normal");
        }
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(SocialDynamic::getContent, keyword);
        }
        if (minLikeCount != null) {
            queryWrapper.ge(SocialDynamic::getLikeCount, minLikeCount);
        }
        
        // 排序
        if ("like_count".equals(sortBy)) {
            if ("asc".equals(sortDirection)) {
                queryWrapper.orderByAsc(SocialDynamic::getLikeCount);
            } else {
                queryWrapper.orderByDesc(SocialDynamic::getLikeCount);
            }
        } else if ("comment_count".equals(sortBy)) {
            if ("asc".equals(sortDirection)) {
                queryWrapper.orderByAsc(SocialDynamic::getCommentCount);
            } else {
                queryWrapper.orderByDesc(SocialDynamic::getCommentCount);
            }
        } else if ("share_count".equals(sortBy)) {
            if ("asc".equals(sortDirection)) {
                queryWrapper.orderByAsc(SocialDynamic::getShareCount);
            } else {
                queryWrapper.orderByDesc(SocialDynamic::getShareCount);
            }
        } else {
            // 默认按创建时间排序
            if ("asc".equals(sortDirection)) {
                queryWrapper.orderByAsc(SocialDynamic::getCreateTime);
            } else {
                queryWrapper.orderByDesc(SocialDynamic::getCreateTime);
            }
        }
        
        return socialDynamicMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<SocialDynamic> getUserDynamics(Long userId, Integer limit) {
        return socialDynamicMapper.selectByUserId(userId, limit);
    }

    @Override
    public IPage<SocialDynamic> getFollowingDynamics(Long userId, int pageNum, int pageSize) {
        Page<SocialDynamic> page = new Page<>(pageNum, pageSize);
        int offset = (pageNum - 1) * pageSize;
        
        List<SocialDynamic> dynamics = socialDynamicMapper.selectFollowingDynamics(userId, offset, pageSize);
        page.setRecords(dynamics);
        
        return page;
    }

    @Override
    public List<SocialDynamic> getDynamicsByType(String dynamicType, Integer limit) {
        return socialDynamicMapper.selectByDynamicType(dynamicType, limit);
    }

    @Override
    public List<SocialDynamic> searchDynamics(String keyword, Integer limit) {
        return socialDynamicMapper.searchByContent(keyword, limit);
    }

    @Override
    public List<SocialDynamic> getHotDynamics(Integer limit) {
        return socialDynamicMapper.selectHotDynamics(limit);
    }

    @Override
    @Transactional
    public void likeDynamic(Long dynamicId, Long userId) {
        // 增加点赞数
        socialDynamicMapper.increaseLikeCount(dynamicId);
        
        // 这里可以添加点赞记录的逻辑（如果需要的话）
        // 由于简洁版设计，点赞记录可以存储在独立的like模块中
    }

    @Override
    @Transactional
    public void unlikeDynamic(Long dynamicId, Long userId) {
        // 减少点赞数
        socialDynamicMapper.decreaseLikeCount(dynamicId);
    }

    @Override
    @Transactional
    public void commentDynamic(Long dynamicId, Long userId, String content) {
        // 增加评论数
        socialDynamicMapper.increaseCommentCount(dynamicId);
        
        // 这里可以添加评论记录的逻辑（如果需要的话）
        // 由于简洁版设计，评论记录可以存储在独立的comment模块中
    }

    @Override
    @Transactional
    public SocialDynamic shareDynamic(Long dynamicId, Long userId, String shareContent) {
        // 增加分享数
        socialDynamicMapper.increaseShareCount(dynamicId);
        
        // 创建分享动态
        SocialDynamic originalDynamic = socialDynamicMapper.selectById(dynamicId);
        if (originalDynamic == null) {
            throw new RuntimeException("原动态不存在");
        }
        
        SocialDynamic shareDynamic = new SocialDynamic();
        shareDynamic.setContent(shareContent);
        shareDynamic.setDynamicType("share");
        shareDynamic.setUserId(userId);
        shareDynamic.setShareTargetType("dynamic");
        shareDynamic.setShareTargetId(dynamicId);
        shareDynamic.setShareTargetTitle(originalDynamic.getContent().substring(0, 
            Math.min(originalDynamic.getContent().length(), 100)));
        
        return createDynamic(shareDynamic);
    }

    @Override
    @Transactional
    public void increaseStatCount(Long dynamicId, String statType) {
        switch (statType) {
            case "like":
                socialDynamicMapper.increaseLikeCount(dynamicId);
                break;
            case "comment":
                socialDynamicMapper.increaseCommentCount(dynamicId);
                break;
            case "share":
                socialDynamicMapper.increaseShareCount(dynamicId);
                break;
            default:
                log.warn("未知的统计类型: {}", statType);
        }
    }
} 