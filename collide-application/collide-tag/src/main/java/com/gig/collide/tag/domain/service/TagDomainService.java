package com.gig.collide.tag.domain.service;

import com.gig.collide.api.tag.request.TagCreateRequest;
import com.gig.collide.api.tag.request.TagQueryRequest;
import com.gig.collide.api.tag.request.TagUpdateRequest;
import com.gig.collide.api.tag.request.UserInterestTagRequest;
import com.gig.collide.api.tag.response.data.TagInfo;
import com.gig.collide.base.response.PageResponse;

import java.util.List;

/**
 * 标签领域服务接口
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
public interface TagDomainService {

    /**
     * 创建标签
     */
    Long createTag(TagCreateRequest request);

    /**
     * 更新标签
     */
    void updateTag(TagUpdateRequest request);

    /**
     * 删除标签
     */
    void deleteTag(Long tagId);

    /**
     * 根据ID查询标签
     */
    TagInfo getTagById(Long tagId);

    /**
     * 分页查询标签
     */
    PageResponse<TagInfo> queryTags(TagQueryRequest request);

    /**
     * 根据类型获取标签
     */
    List<TagInfo> getTagsByType(String tagType);

    /**
     * 获取热门标签
     */
    List<TagInfo> getHotTags(String tagType, Integer limit);

    /**
     * 搜索标签
     */
    List<TagInfo> searchTags(String keyword, String tagType);

    /**
     * 获取用户兴趣标签
     */
    List<TagInfo> getUserInterestTags(Long userId);

    /**
     * 设置用户兴趣标签
     */
    void setUserInterestTags(UserInterestTagRequest request);

    /**
     * 添加用户兴趣标签
     */
    void addUserInterestTag(Long userId, Long tagId, Double interestScore);

    /**
     * 移除用户兴趣标签
     */
    void removeUserInterestTag(Long userId, Long tagId);

    /**
     * 推荐标签给用户
     */
    List<TagInfo> recommendTagsToUser(Long userId, Integer limit);
} 