package com.gig.collide.social.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.social.domain.entity.SocialContent;
import com.gig.collide.social.domain.service.SocialContentService;
import com.gig.collide.social.infrastructure.mapper.SocialContentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 社交内容服务实现
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SocialContentServiceImpl implements SocialContentService {

    private final SocialContentMapper contentMapper;

    @Override
    @Transactional
    public Long createContent(SocialContent content) {
        // 设置默认值
        if (content.getStatus() == null) {
            content.setStatus(SocialContent.Status.NORMAL.getCode());
        }
        if (content.getPrivacy() == null) {
            content.setPrivacy(1); // 默认公开
        }
        // 初始化统计字段
        content.setLikeCount(0);
        content.setCommentCount(0);
        content.setShareCount(0);
        content.setFavoriteCount(0);
        content.setViewCount(0);
        content.setPurchaseCount(0);
        
        int inserted = contentMapper.insert(content);
        if (inserted > 0) {
            log.info("创建内容成功: contentId={}, userId={}", content.getId(), content.getUserId());
            return content.getId();
        }
        return null;
    }

    @Override
    @Transactional
    public boolean updateContent(SocialContent content) {
        if (content.getId() == null) {
            return false;
        }
        
        // 不允许修改统计字段和敏感字段
        content.setLikeCount(null);
        content.setCommentCount(null);
        content.setShareCount(null);
        content.setFavoriteCount(null);
        content.setViewCount(null);
        content.setPurchaseCount(null);
        content.setUserId(null);
        content.setCreateTime(null);
        
        int updated = contentMapper.updateById(content);
        if (updated > 0) {
            log.info("更新内容成功: contentId={}", content.getId());
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean deleteContent(Long contentId, Long userId) {
        // 软删除：只有内容作者可以删除
        SocialContent content = new SocialContent();
        content.setId(contentId);
        content.setStatus(SocialContent.Status.DELETED.getCode());
        
        LambdaQueryWrapper<SocialContent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SocialContent::getId, contentId)
               .eq(SocialContent::getUserId, userId)
               .ne(SocialContent::getStatus, SocialContent.Status.DELETED.getCode());
        
        int updated = contentMapper.update(content, wrapper);
        if (updated > 0) {
            log.info("删除内容成功: contentId={}, userId={}", contentId, userId);
            return true;
        }
        return false;
    }

    @Override
    public SocialContent getById(Long contentId) {
        return contentMapper.selectById(contentId);
    }

    @Override
    public IPage<SocialContent> getByUserId(Long userId, int pageNum, int pageSize) {
        Page<SocialContent> page = new Page<>(pageNum, pageSize);
        return contentMapper.selectByUserId(page, userId);
    }

    @Override
    public IPage<SocialContent> getByCategoryId(Long categoryId, int pageNum, int pageSize) {
        Page<SocialContent> page = new Page<>(pageNum, pageSize);
        return contentMapper.selectByCategoryId(page, categoryId);
    }

    @Override
    public IPage<SocialContent> getHotContent(int pageNum, int pageSize) {
        Page<SocialContent> page = new Page<>(pageNum, pageSize);
        return contentMapper.selectHotContent(page);
    }

    @Override
    public IPage<SocialContent> getLatestContent(int pageNum, int pageSize) {
        Page<SocialContent> page = new Page<>(pageNum, pageSize);
        return contentMapper.selectLatestContent(page);
    }

    @Override
    public IPage<SocialContent> searchContent(String keyword, int pageNum, int pageSize) {
        Page<SocialContent> page = new Page<>(pageNum, pageSize);
        return contentMapper.searchContent(page, keyword);
    }

    @Override
    public List<SocialContent> getBatchByIds(List<Long> contentIds) {
        if (contentIds == null || contentIds.isEmpty()) {
            return List.of();
        }
        return contentMapper.selectBatchIds(contentIds);
    }

    @Override
    public boolean checkContentAccess(Long contentId, Long userId) {
        SocialContent content = contentMapper.selectById(contentId);
        if (content == null || !Objects.equals(content.getStatus(), SocialContent.Status.NORMAL.getCode())) {
            return false;
        }
        
        // 检查隐私设置
        if (content.getPrivacy() == 3) { // 私密
            return Objects.equals(content.getUserId(), userId);
        } else if (content.getPrivacy() == 2) { // 仅关注者
            // 这里需要检查关注关系，简化处理先返回true
            return true;
        }
        
        return true; // 公开内容
    }

    @Override
    public int getUserContentCount(Long userId) {
        return contentMapper.countByUserId(userId);
    }

    @Override
    public int getCategoryContentCount(Long categoryId) {
        return contentMapper.countByCategoryId(categoryId);
    }
}