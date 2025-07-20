package com.gig.collide.api.tag.service;

import com.gig.collide.api.tag.request.*;
import com.gig.collide.api.tag.response.TagOperatorResponse;
import com.gig.collide.api.tag.response.TagQueryResponse;
import com.gig.collide.api.tag.response.data.TagInfo;
import com.gig.collide.api.tag.response.data.TagStatistics;
import com.gig.collide.api.tag.response.data.UserTagInfo;
import com.gig.collide.base.response.PageResponse;

import java.util.List;

/**
 * 标签门面服务接口
 * @author GIG
 */
public interface TagFacadeService {

    // ================ 标签管理 ================

    /**
     * 创建标签
     * @param tagCreateRequest
     * @return
     */
    TagOperatorResponse createTag(TagCreateRequest tagCreateRequest);

    /**
     * 更新标签
     * @param tagUpdateRequest
     * @return
     */
    TagOperatorResponse updateTag(TagUpdateRequest tagUpdateRequest);

    /**
     * 删除标签
     * @param tagId
     * @param operatorId
     * @return
     */
    TagOperatorResponse deleteTag(Long tagId, Long operatorId);

    /**
     * 批量删除标签
     * @param tagIds
     * @param operatorId
     * @return
     */
    TagOperatorResponse batchDeleteTags(List<Long> tagIds, Long operatorId);

    /**
     * 启用标签
     * @param tagId
     * @param operatorId
     * @return
     */
    TagOperatorResponse enableTag(Long tagId, Long operatorId);

    /**
     * 禁用标签
     * @param tagId
     * @param operatorId
     * @return
     */
    TagOperatorResponse disableTag(Long tagId, Long operatorId);

    // ================ 标签查询 ================

    /**
     * 根据ID查询标签
     * @param tagId
     * @return
     */
    TagQueryResponse<TagInfo> queryTagById(Long tagId);

    /**
     * 查询标签列表
     * @param tagQueryRequest
     * @return
     */
    TagQueryResponse<List<TagInfo>> queryTags(TagQueryRequest tagQueryRequest);

    /**
     * 分页查询标签
     * @param tagPageQueryRequest
     * @return
     */
    PageResponse<TagInfo> pageQueryTags(TagPageQueryRequest tagPageQueryRequest);

    /**
     * 查询标签树结构
     * @param parentTagId 父标签ID，null表示查询根标签
     * @param includeDisabled 是否包含禁用标签
     * @return
     */
    TagQueryResponse<List<TagInfo>> queryTagTree(Long parentTagId, Boolean includeDisabled);

    /**
     * 查询热门标签
     * @param limit 限制数量
     * @return
     */
    TagQueryResponse<List<TagInfo>> queryHotTags(Integer limit);

    /**
     * 搜索标签
     * @param keyword 关键词
     * @param limit 限制数量
     * @return
     */
    TagQueryResponse<List<TagInfo>> searchTags(String keyword, Integer limit);

    // ================ 用户打标签 ================

    /**
     * 用户打标签
     * @param userTagRequest
     * @return
     */
    TagOperatorResponse userTag(UserTagRequest userTagRequest);

    /**
     * 用户取消标签
     * @param userUntagRequest
     * @return
     */
    TagOperatorResponse userUntag(UserUntagRequest userUntagRequest);

    /**
     * 查询用户标签
     * @param userTagQueryRequest
     * @return
     */
    TagQueryResponse<List<UserTagInfo>> queryUserTags(UserTagQueryRequest userTagQueryRequest);

    /**
     * 分页查询用户标签
     * @param userId
     * @param currentPage
     * @param pageSize
     * @return
     */
    PageResponse<UserTagInfo> pageQueryUserTags(Long userId, int currentPage, int pageSize);

    /**
     * 查询用户对某个对象的标签
     * @param userId
     * @param relationObjectId
     * @return
     */
    TagQueryResponse<List<UserTagInfo>> queryUserTagsByObject(Long userId, String relationObjectId);

    /**
     * 检查用户是否已打标签
     * @param userId
     * @param tagId
     * @param relationObjectId
     * @return
     */
    TagQueryResponse<Boolean> checkUserTagged(Long userId, Long tagId, String relationObjectId);

    // ================ 标签统计 ================

    /**
     * 查询标签统计信息
     * @param tagId
     * @return
     */
    TagQueryResponse<TagStatistics> queryTagStatistics(Long tagId);

    /**
     * 查询标签使用排行
     * @param limit
     * @return
     */
    TagQueryResponse<List<TagStatistics>> queryTagUsageRanking(Integer limit);

    /**
     * 批量查询标签统计
     * @param tagIds
     * @return
     */
    TagQueryResponse<List<TagStatistics>> batchQueryTagStatistics(List<Long> tagIds);

    // ================ 推荐功能 ================

    /**
     * 根据用户标签推荐相关标签
     * @param userId
     * @param limit
     * @return
     */
    TagQueryResponse<List<TagInfo>> recommendTagsForUser(Long userId, Integer limit);

    /**
     * 根据内容推荐标签
     * @param content
     * @param limit
     * @return
     */
    TagQueryResponse<List<TagInfo>> recommendTagsForContent(String content, Integer limit);

    /**
     * 查询用户可能感兴趣的标签
     * @param userId
     * @param excludeTagIds 排除的标签ID
     * @param limit
     * @return
     */
    TagQueryResponse<List<TagInfo>> queryInterestedTags(Long userId, List<Long> excludeTagIds, Integer limit);
} 