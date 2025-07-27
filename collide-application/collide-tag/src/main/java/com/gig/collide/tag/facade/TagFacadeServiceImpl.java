package com.gig.collide.tag.facade;

import com.gig.collide.api.tag.TagFacadeService;
import com.gig.collide.api.tag.request.TagCreateRequest;
import com.gig.collide.api.tag.request.TagQueryRequest;
import com.gig.collide.api.tag.request.TagUpdateRequest;
import com.gig.collide.api.tag.request.UserInterestTagRequest;
import com.gig.collide.api.tag.response.TagOperatorResponse;
import com.gig.collide.api.tag.response.TagQueryResponse;
import com.gig.collide.api.tag.response.data.TagInfo;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.tag.domain.service.TagDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 标签服务 Dubbo RPC 接口实现
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
@Slf4j
@Component
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class TagFacadeServiceImpl implements TagFacadeService {

    private final TagDomainService tagDomainService;

    @Override
    public TagOperatorResponse createTag(TagCreateRequest request) {
        try {
            Long tagId = tagDomainService.createTag(request);
            return TagOperatorResponse.success(tagId);
        } catch (Exception e) {
            log.error("创建标签失败", e);
            return TagOperatorResponse.error("TAG_CREATE_FAILED", "创建标签失败");
        }
    }

    @Override
    public TagOperatorResponse updateTag(TagUpdateRequest request) {
        try {
            tagDomainService.updateTag(request);
            return TagOperatorResponse.success();
        } catch (Exception e) {
            log.error("更新标签失败", e);
            return TagOperatorResponse.error("TAG_UPDATE_FAILED", "更新标签失败");
        }
    }

    @Override
    public TagOperatorResponse deleteTag(Long tagId) {
        try {
            tagDomainService.deleteTag(tagId);
            return TagOperatorResponse.success();
        } catch (Exception e) {
            log.error("删除标签失败", e);
            return TagOperatorResponse.error("TAG_DELETE_FAILED", "删除标签失败");
        }
    }

    @Override
    public TagQueryResponse<TagInfo> getTagById(Long tagId) {
        try {
            TagInfo tagInfo = tagDomainService.getTagById(tagId);
            return TagQueryResponse.success(tagInfo);
        } catch (Exception e) {
            log.error("查询标签详情失败", e);
            return TagQueryResponse.error("TAG_QUERY_FAILED", "查询标签详情失败");
        }
    }

    @Override
    public TagQueryResponse<PageResponse<TagInfo>> queryTags(TagQueryRequest request) {
        try {
            PageResponse<TagInfo> result = tagDomainService.queryTags(request);
            return TagQueryResponse.success(result);
        } catch (Exception e) {
            log.error("分页查询标签失败", e);
            return TagQueryResponse.error("TAG_PAGE_QUERY_FAILED", "分页查询标签失败");
        }
    }

    @Override
    public TagQueryResponse<List<TagInfo>> getTagsByType(String tagType) {
        try {
            List<TagInfo> tags = tagDomainService.getTagsByType(tagType);
            return TagQueryResponse.success(tags);
        } catch (Exception e) {
            log.error("根据类型查询标签失败", e);
            return TagQueryResponse.error("TAG_TYPE_QUERY_FAILED", "根据类型查询标签失败");
        }
    }

    @Override
    public TagQueryResponse<List<TagInfo>> getHotTags(String tagType, Integer limit) {
        try {
            List<TagInfo> hotTags = tagDomainService.getHotTags(tagType, limit);
            return TagQueryResponse.success(hotTags);
        } catch (Exception e) {
            log.error("获取热门标签失败", e);
            return TagQueryResponse.error("HOT_TAG_QUERY_FAILED", "获取热门标签失败");
        }
    }

    @Override
    public TagQueryResponse<List<TagInfo>> searchTags(String keyword, String tagType) {
        try {
            List<TagInfo> tags = tagDomainService.searchTags(keyword, tagType);
            return TagQueryResponse.success(tags);
        } catch (Exception e) {
            log.error("搜索标签失败", e);
            return TagQueryResponse.error("TAG_SEARCH_FAILED", "搜索标签失败");
        }
    }

    @Override
    public TagQueryResponse<List<TagInfo>> getUserInterestTags(Long userId) {
        try {
            List<TagInfo> userTags = tagDomainService.getUserInterestTags(userId);
            return TagQueryResponse.success(userTags);
        } catch (Exception e) {
            log.error("获取用户兴趣标签失败", e);
            return TagQueryResponse.error("USER_TAG_QUERY_FAILED", "获取用户兴趣标签失败");
        }
    }

    @Override
    public TagOperatorResponse setUserInterestTags(UserInterestTagRequest request) {
        try {
            tagDomainService.setUserInterestTags(request);
            return TagOperatorResponse.success();
        } catch (Exception e) {
            log.error("设置用户兴趣标签失败", e);
            return TagOperatorResponse.error("USER_TAG_SET_FAILED", "设置用户兴趣标签失败");
        }
    }

    @Override
    public TagOperatorResponse addUserInterestTag(Long userId, Long tagId, Double interestScore) {
        try {
            tagDomainService.addUserInterestTag(userId, tagId, interestScore);
            return TagOperatorResponse.success();
        } catch (Exception e) {
            log.error("添加用户兴趣标签失败", e);
            return TagOperatorResponse.error("USER_TAG_ADD_FAILED", "添加用户兴趣标签失败");
        }
    }

    @Override
    public TagOperatorResponse removeUserInterestTag(Long userId, Long tagId) {
        try {
            tagDomainService.removeUserInterestTag(userId, tagId);
            return TagOperatorResponse.success();
        } catch (Exception e) {
            log.error("移除用户兴趣标签失败", e);
            return TagOperatorResponse.error("USER_TAG_REMOVE_FAILED", "移除用户兴趣标签失败");
        }
    }

    @Override
    public TagQueryResponse<List<TagInfo>> recommendTagsToUser(Long userId, Integer limit) {
        try {
            List<TagInfo> recommendedTags = tagDomainService.recommendTagsToUser(userId, limit);
            return TagQueryResponse.success(recommendedTags);
        } catch (Exception e) {
            log.error("推荐标签给用户失败", e);
            return TagQueryResponse.error("TAG_RECOMMEND_FAILED", "推荐标签给用户失败");
        }
    }
} 