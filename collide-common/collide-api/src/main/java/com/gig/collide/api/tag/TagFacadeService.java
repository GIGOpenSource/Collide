package com.gig.collide.api.tag;

import com.gig.collide.api.tag.request.TagCreateRequest;
import com.gig.collide.api.tag.request.TagQueryRequest;
import com.gig.collide.api.tag.request.TagUpdateRequest;
import com.gig.collide.api.tag.response.TagResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import java.util.List;

/**
 * 标签管理门面服务接口 - 简洁版
 * 基于简洁版SQL设计（t_tag, t_user_interest_tag, t_content_tag）
 *
 * @author GIG Team
 * @version 2.0.0
 */
public interface TagFacadeService {

    /**
     * 创建标签
     */
    Result<Void> createTag(TagCreateRequest request);

    /**
     * 更新标签
     */
    Result<TagResponse> updateTag(TagUpdateRequest request);

    /**
     * 删除标签（逻辑删除）
     */
    Result<Void> deleteTag(Long tagId);

    /**
     * 根据ID查询标签
     */
    Result<TagResponse> getTagById(Long tagId);

    /**
     * 分页查询标签列表
     */
    Result<PageResponse<TagResponse>> queryTags(TagQueryRequest request);

    /**
     * 根据类型获取标签列表
     */
    Result<List<TagResponse>> getTagsByType(String tagType);

    /**
     * 搜索标签（按名称模糊搜索）
     */
    Result<List<TagResponse>> searchTags(String keyword, Integer limit);

    /**
     * 获取热门标签（按使用次数排序）
     */
    Result<List<TagResponse>> getHotTags(Integer limit);

    /**
     * 获取用户兴趣标签
     */
    Result<List<TagResponse>> getUserInterestTags(Long userId);

    /**
     * 添加用户兴趣标签
     */
    Result<Void> addUserInterestTag(Long userId, Long tagId, Double interestScore);

    /**
     * 移除用户兴趣标签
     */
    Result<Void> removeUserInterestTag(Long userId, Long tagId);

    /**
     * 更新用户兴趣分数
     */
    Result<Void> updateUserInterestScore(Long userId, Long tagId, Double interestScore);

    /**
     * 为内容添加标签
     */
    Result<Void> addContentTag(Long contentId, Long tagId);

    /**
     * 移除内容标签
     */
    Result<Void> removeContentTag(Long contentId, Long tagId);

    /**
     * 获取内容的标签列表
     */
    Result<List<TagResponse>> getContentTags(Long contentId);

    /**
     * 增加标签使用次数
     */
    Result<Void> increaseTagUsage(Long tagId);
} 