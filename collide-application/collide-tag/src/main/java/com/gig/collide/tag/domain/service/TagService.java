package com.gig.collide.tag.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.tag.domain.entity.Tag;
import com.gig.collide.tag.domain.entity.UserInterestTag;
import com.gig.collide.tag.domain.entity.ContentTag;

import java.math.BigDecimal;
import java.util.List;

/**
 * 标签服务接口 - 简洁版
 *
 * @author GIG Team
 * @version 2.0.0
 */
public interface TagService {

    /**
     * 创建标签
     */
    Tag createTag(Tag tag);

    /**
     * 更新标签
     */
    Tag updateTag(Tag tag);

    /**
     * 删除标签（逻辑删除）
     */
    void deleteTag(Long tagId);

    /**
     * 根据ID查询标签
     */
    Tag getTagById(Long tagId);

    /**
     * 分页查询标签
     */
    IPage<Tag> queryTags(int pageNum, int pageSize, String name, String tagType, Long categoryId, String status);

    /**
     * 根据类型获取标签列表
     */
    List<Tag> getTagsByType(String tagType);

    /**
     * 搜索标签
     */
    List<Tag> searchTags(String keyword, Integer limit);

    /**
     * 获取热门标签
     */
    List<Tag> getHotTags(Integer limit);

    /**
     * 增加标签使用次数
     */
    void increaseTagUsage(Long tagId);

    /**
     * 获取用户兴趣标签
     */
    List<Tag> getUserInterestTags(Long userId);

    /**
     * 添加用户兴趣标签
     */
    void addUserInterestTag(Long userId, Long tagId, BigDecimal interestScore);

    /**
     * 移除用户兴趣标签
     */
    void removeUserInterestTag(Long userId, Long tagId);

    /**
     * 更新用户兴趣分数
     */
    void updateUserInterestScore(Long userId, Long tagId, BigDecimal interestScore);

    /**
     * 为内容添加标签
     */
    void addContentTag(Long contentId, Long tagId);

    /**
     * 移除内容标签
     */
    void removeContentTag(Long contentId, Long tagId);

    /**
     * 获取内容的标签列表
     */
    List<Tag> getContentTags(Long contentId);
} 