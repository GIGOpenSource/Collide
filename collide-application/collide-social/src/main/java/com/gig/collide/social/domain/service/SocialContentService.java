package com.gig.collide.social.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.social.domain.entity.SocialContent;

import java.util.List;

/**
 * 社交内容服务接口 - 简洁版CRUD
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
public interface SocialContentService {

    /**
     * 创建内容
     */
    Long createContent(SocialContent content);

    /**
     * 更新内容
     */
    boolean updateContent(SocialContent content);

    /**
     * 删除内容（软删除）
     */
    boolean deleteContent(Long contentId, Long userId);

    /**
     * 根据ID获取内容
     */
    SocialContent getById(Long contentId);

    /**
     * 根据用户ID分页获取内容
     */
    IPage<SocialContent> getByUserId(Long userId, int pageNum, int pageSize);

    /**
     * 根据分类ID分页获取内容
     */
    IPage<SocialContent> getByCategoryId(Long categoryId, int pageNum, int pageSize);

    /**
     * 获取热门内容
     */
    IPage<SocialContent> getHotContent(int pageNum, int pageSize);

    /**
     * 获取最新内容
     */
    IPage<SocialContent> getLatestContent(int pageNum, int pageSize);

    /**
     * 搜索内容
     */
    IPage<SocialContent> searchContent(String keyword, int pageNum, int pageSize);

    /**
     * 批量获取内容
     */
    List<SocialContent> getBatchByIds(List<Long> contentIds);

    /**
     * 检查用户是否有权限访问内容
     */
    boolean checkContentAccess(Long contentId, Long userId);

    /**
     * 获取用户的内容数量
     */
    int getUserContentCount(Long userId);

    /**
     * 获取分类下的内容数量
     */
    int getCategoryContentCount(Long categoryId);
}