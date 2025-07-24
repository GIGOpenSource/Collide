package com.gig.collide.api.tag;

import com.gig.collide.api.tag.request.TagCreateRequest;
import com.gig.collide.api.tag.request.TagQueryRequest;
import com.gig.collide.api.tag.request.TagUpdateRequest;
import com.gig.collide.api.tag.request.UserInterestTagRequest;
import com.gig.collide.api.tag.response.TagOperatorResponse;
import com.gig.collide.api.tag.response.TagQueryResponse;
import com.gig.collide.api.tag.response.data.TagInfo;
import com.gig.collide.base.response.PageResponse;

import java.util.List;

/**
 * 标签管理门面服务接口
 *
 * @author GIG Team
 * @version 1.0.0
 */
public interface TagFacadeService {

    /**
     * 创建标签
     */
    TagOperatorResponse createTag(TagCreateRequest request);

    /**
     * 更新标签
     */
    TagOperatorResponse updateTag(TagUpdateRequest request);

    /**
     * 删除标签
     */
    TagOperatorResponse deleteTag(Long tagId);

    /**
     * 查询标签详情
     */
    TagQueryResponse<TagInfo> getTagById(Long tagId);

    /**
     * 分页查询标签列表
     */
    TagQueryResponse<PageResponse<TagInfo>> queryTags(TagQueryRequest request);

    /**
     * 根据类型获取标签列表
     */
    TagQueryResponse<List<TagInfo>> getTagsByType(String tagType);

    /**
     * 获取热门标签
     */
    TagQueryResponse<List<TagInfo>> getHotTags(String tagType, Integer limit);

    /**
     * 搜索标签
     */
    TagQueryResponse<List<TagInfo>> searchTags(String keyword, String tagType);

    /**
     * 获取用户兴趣标签
     */
    TagQueryResponse<List<TagInfo>> getUserInterestTags(Long userId);

    /**
     * 设置用户兴趣标签
     */
    TagOperatorResponse setUserInterestTags(UserInterestTagRequest request);

    /**
     * 添加用户兴趣标签
     */
    TagOperatorResponse addUserInterestTag(Long userId, Long tagId, Double interestScore);

    /**
     * 移除用户兴趣标签
     */
    TagOperatorResponse removeUserInterestTag(Long userId, Long tagId);

    /**
     * 推荐标签给用户
     */
    TagQueryResponse<List<TagInfo>> recommendTagsToUser(Long userId, Integer limit);
} 