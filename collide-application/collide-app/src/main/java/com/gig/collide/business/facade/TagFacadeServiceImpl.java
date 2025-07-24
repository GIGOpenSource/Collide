package com.gig.collide.business.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.tag.TagFacadeService;
import com.gig.collide.api.tag.request.TagCreateRequest;
import com.gig.collide.api.tag.request.TagQueryRequest;
import com.gig.collide.api.tag.request.TagUpdateRequest;
import com.gig.collide.api.tag.request.UserInterestTagRequest;
import com.gig.collide.api.tag.response.TagOperatorResponse;
import com.gig.collide.api.tag.response.TagQueryResponse;
import com.gig.collide.api.tag.response.data.TagInfo;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.business.domain.tag.entity.Tag;
import com.gig.collide.business.domain.tag.entity.UserInterestTag;
import com.gig.collide.business.domain.tag.service.TagDomainService;
import com.gig.collide.business.domain.tag.service.UserInterestTagService;
import com.gig.collide.base.exception.BizErrorCode;
import com.gig.collide.base.exception.CommonErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 标签门面服务实现
 *
 * @author collide
 * @date 2024/12/19
 */
@DubboService(version = "1.0.0", timeout = 3000)
@RequiredArgsConstructor
@Slf4j
public class TagFacadeServiceImpl implements TagFacadeService {
    
    private final TagDomainService tagDomainService;
    private final UserInterestTagService userInterestTagService;
    
    @Override
    public TagOperatorResponse createTag(TagCreateRequest request) {
        try {
            // 参数验证
            if (request == null || !request.isValid()) {
                return TagOperatorResponse.error(CommonErrorCode.PARAM_INVALID.getCode(), "创建标签参数不能为空");
            }
            
            // 创建标签
            Tag tag = tagDomainService.createTag(
                request.getName(),
                request.getDescription(),
                request.getCategoryId(),
                request.getColor(),
                request.getIconUrl(),
                request.getSort(),
                request.getStatus()
            );
            
            // 构建响应
            TagInfo tagInfo = buildTagInfo(tag);
            return TagOperatorResponse.success(tagInfo.getTagId());
            
        } catch (Exception e) {
            log.error("创建标签失败：{}", e.getMessage(), e);
            return TagOperatorResponse.error(CommonErrorCode.SYSTEM_ERROR.getCode(), "创建标签失败：" + e.getMessage());
        }
    }
    
    @Override
    public TagOperatorResponse updateTag(TagUpdateRequest request) {
        try {
            // 参数验证
            if (request == null || request.getTagId() == null) {
                return TagOperatorResponse.error(CommonErrorCode.PARAM_INVALID.getCode(), "更新标签参数不能为空");
            }
            
            // 更新标签
            Tag tag = tagDomainService.updateTag(
                request.getTagId(),
                request.getName(),
                request.getDescription(),
                request.getCategoryId(),
                request.getColor(),
                request.getIconUrl(),
                request.getSort(),
                request.getStatus()
            );
            
            // 构建响应
            TagInfo tagInfo = buildTagInfo(tag);
            return TagOperatorResponse.success(tagInfo.getTagId());
            
        } catch (Exception e) {
            log.error("更新标签失败：{}", e.getMessage(), e);
            return TagOperatorResponse.error(CommonErrorCode.SYSTEM_ERROR.getCode(), "更新标签失败：" + e.getMessage());
        }
    }
    
    @Override
    public TagOperatorResponse deleteTag(Long tagId) {
        try {
            // 参数验证
            if (tagId == null) {
                return TagOperatorResponse.error(CommonErrorCode.PARAM_INVALID.getCode(), "标签ID不能为空");
            }
            
            // 删除标签
            tagDomainService.deleteTag(tagId);
            return TagOperatorResponse.success(tagId);
            
        } catch (Exception e) {
            log.error("删除标签失败：{}", e.getMessage(), e);
            return TagOperatorResponse.error(CommonErrorCode.SYSTEM_ERROR.getCode(), "删除标签失败：" + e.getMessage());
        }
    }
    
    @Override
    public TagQueryResponse<TagInfo> getTagById(Long tagId) {
        try {
            // 参数验证
            if (tagId == null) {
                return TagQueryResponse.error(CommonErrorCode.PARAM_INVALID.getCode(), "标签ID不能为空");
            }
            
            // 查询标签
            Tag tag = tagDomainService.getTagById(tagId);
            if (tag == null) {
                return TagQueryResponse.error(CommonErrorCode.DATA_NOT_FOUND.getCode(), "标签不存在");
            }
            
            // 构建响应
            TagInfo tagInfo = buildTagInfo(tag);
            return TagQueryResponse.success(tagInfo);
            
        } catch (Exception e) {
            log.error("获取标签失败：{}", e.getMessage(), e);
            return TagQueryResponse.error(CommonErrorCode.SYSTEM_ERROR.getCode(), "获取标签失败：" + e.getMessage());
        }
    }
    
    @Override
    public TagQueryResponse<PageResponse<TagInfo>> queryTags(TagQueryRequest request) {
        try {
            // 参数验证
            if (request == null) {
                return TagQueryResponse.error(CommonErrorCode.PARAM_INVALID.getCode(), "查询参数不能为空");
            }
            
            // 分页查询标签
            Page<Tag> tagPage = tagDomainService.queryTags(
                request.getName(),
                request.getCategoryId() != null ? request.getCategoryId().intValue() : null,
                request.getStatus(),
                request.getPageNo(),
                request.getPageSize()
            );
            
            // 构建响应
            List<TagInfo> tagInfos = tagPage.getRecords().stream()
                .map(this::buildTagInfo)
                .collect(Collectors.toList());
            
            PageResponse<TagInfo> pageResponse = PageResponse.of(
                tagInfos, 
                (int) tagPage.getTotal(), 
                request.getPageSize() != null ? request.getPageSize() : 10,
                request.getPageNo() != null ? request.getPageNo() : 1
            );
            
            return TagQueryResponse.success(pageResponse);
            
        } catch (Exception e) {
            log.error("查询标签失败：{}", e.getMessage(), e);
            return TagQueryResponse.error(CommonErrorCode.SYSTEM_ERROR.getCode(), "查询标签失败：" + e.getMessage());
        }
    }
    
    @Override
    public TagQueryResponse<List<TagInfo>> getTagsByType(String tagType) {
        try {
            List<Tag> tags = tagDomainService.getTagsByType(tagType);
            List<TagInfo> tagInfos = tags.stream()
                .map(this::buildTagInfo)
                .collect(Collectors.toList());
            return TagQueryResponse.success(tagInfos);
        } catch (Exception e) {
            log.error("根据类型获取标签失败：{}", e.getMessage(), e);
            return TagQueryResponse.error(CommonErrorCode.SYSTEM_ERROR.getCode(), "根据类型获取标签失败：" + e.getMessage());
        }
    }

    @Override
    public TagQueryResponse<List<TagInfo>> getHotTags(String tagType, Integer limit) {
        try {
            List<Tag> tags = tagDomainService.getHotTags(tagType, limit);
            List<TagInfo> tagInfos = tags.stream()
                .map(this::buildTagInfo)
                .collect(Collectors.toList());
            return TagQueryResponse.success(tagInfos);
        } catch (Exception e) {
            log.error("获取热门标签失败：{}", e.getMessage(), e);
            return TagQueryResponse.error(CommonErrorCode.SYSTEM_ERROR.getCode(), "获取热门标签失败：" + e.getMessage());
        }
    }

    @Override
    public TagQueryResponse<List<TagInfo>> searchTags(String keyword, String tagType) {
        try {
            List<Tag> tags = tagDomainService.searchTags(keyword, tagType, 50);
            List<TagInfo> tagInfos = tags.stream()
                .map(this::buildTagInfo)
                .collect(Collectors.toList());
            return TagQueryResponse.success(tagInfos);
        } catch (Exception e) {
            log.error("搜索标签失败：{}", e.getMessage(), e);
            return TagQueryResponse.error(CommonErrorCode.SYSTEM_ERROR.getCode(), "搜索标签失败：" + e.getMessage());
        }
    }

    @Override
    public TagQueryResponse<List<TagInfo>> getUserInterestTags(Long userId) {
        try {
            // 参数验证
            if (userId == null) {
                return TagQueryResponse.error(CommonErrorCode.PARAM_INVALID.getCode(), "用户ID不能为空");
            }
            
            List<Tag> tags = userInterestTagService.getUserFollowedTags(userId, null);
            List<TagInfo> tagInfos = tags.stream()
                .map(this::buildTagInfo)
                .collect(Collectors.toList());
            return TagQueryResponse.success(tagInfos);
        } catch (Exception e) {
            log.error("获取用户兴趣标签失败：{}", e.getMessage(), e);
            return TagQueryResponse.error(CommonErrorCode.SYSTEM_ERROR.getCode(), "获取用户兴趣标签失败：" + e.getMessage());
        }
    }
    
    @Override
    public TagOperatorResponse setUserInterestTags(UserInterestTagRequest request) {
        try {
            // 参数验证
            if (request == null || CollectionUtils.isEmpty(request.getTagIds())) {
                return TagOperatorResponse.error(CommonErrorCode.PARAM_INVALID.getCode(), "批量关注标签参数不能为空");
            }
            
            userInterestTagService.setUserInterestTags(
                request.getUserId(), 
                request.getTagIds(), 
                request.getSource()
            );
            return TagOperatorResponse.success(request.getUserId());
        } catch (Exception e) {
            log.error("批量关注标签失败：{}", e.getMessage(), e);
            return TagOperatorResponse.error(CommonErrorCode.SYSTEM_ERROR.getCode(), "批量关注标签失败：" + e.getMessage());
        }
    }
    
    @Override
    public TagOperatorResponse addUserInterestTag(Long userId, Long tagId, Double interestScore) {
        try {
            // 参数验证
            if (userId == null || tagId == null) {
                return TagOperatorResponse.error(CommonErrorCode.PARAM_INVALID.getCode(), "用户ID和标签ID不能为空");
            }
            
            userInterestTagService.followTag(userId, tagId, "manual");
            return TagOperatorResponse.success(tagId);
        } catch (Exception e) {
            log.error("添加用户兴趣标签失败：{}", e.getMessage(), e);
            return TagOperatorResponse.error(CommonErrorCode.SYSTEM_ERROR.getCode(), "添加用户兴趣标签失败：" + e.getMessage());
        }
    }
    
    @Override
    public TagOperatorResponse removeUserInterestTag(Long userId, Long tagId) {
        try {
            // 参数验证
            if (userId == null || tagId == null) {
                return TagOperatorResponse.error(CommonErrorCode.PARAM_INVALID.getCode(), "用户ID和标签ID不能为空");
            }
            
            userInterestTagService.unfollowTag(userId, tagId);
            return TagOperatorResponse.success(tagId);
        } catch (Exception e) {
            log.error("移除用户兴趣标签失败：{}", e.getMessage(), e);
            return TagOperatorResponse.error(CommonErrorCode.SYSTEM_ERROR.getCode(), "移除用户兴趣标签失败：" + e.getMessage());
        }
    }

    @Override
    public TagQueryResponse<List<TagInfo>> recommendTagsToUser(Long userId, Integer limit) {
        try {
            // 参数验证
            if (userId == null) {
                return TagQueryResponse.error(CommonErrorCode.PARAM_INVALID.getCode(), "用户ID不能为空");
            }
            
            List<Tag> tags = userInterestTagService.recommendTagsToUser(userId, limit);
            List<TagInfo> tagInfos = tags.stream()
                .map(this::buildTagInfo)
                .collect(Collectors.toList());
            return TagQueryResponse.success(tagInfos);
        } catch (Exception e) {
            log.error("获取推荐标签失败：{}", e.getMessage(), e);
            return TagQueryResponse.error(CommonErrorCode.SYSTEM_ERROR.getCode(), "获取推荐标签失败：" + e.getMessage());
        }
    }
    
    /**
     * 转换为TagInfo
     */
    private TagInfo convertToTagInfo(Tag tag) {
        if (tag == null) {
            return null;
        }
        
        TagInfo tagInfo = new TagInfo();
        tagInfo.setTagId(tag.getId());
        tagInfo.setName(tag.getName());
        tagInfo.setDescription(tag.getDescription());
        tagInfo.setColor(tag.getColor());
        tagInfo.setIconUrl(tag.getIconUrl());
        tagInfo.setTagType(tag.getTagType());
        tagInfo.setCategoryId(tag.getCategoryId());
        tagInfo.setUsageCount(tag.getUsageCount());
        tagInfo.setHeatScore(tag.getHeatScore());
        tagInfo.setStatus(tag.getStatus());
        tagInfo.setCreateTime(tag.getCreateTime());
        
        return tagInfo;
    }
    
    /**
     * 构建TagInfo（兼容性方法）
     */
    private TagInfo buildTagInfo(Tag tag) {
        return convertToTagInfo(tag);
    }
    
    /**
     * 批量转换为TagInfo列表
     */
    private List<TagInfo> convertToTagInfoList(List<Tag> tags) {
        if (CollectionUtils.isEmpty(tags)) {
            return new ArrayList<>();
        }
        
        return tags.stream()
            .map(this::convertToTagInfo)
            .collect(Collectors.toList());
    }
} 