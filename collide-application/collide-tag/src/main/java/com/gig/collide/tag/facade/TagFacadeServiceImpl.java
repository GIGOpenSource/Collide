package com.gig.collide.tag.facade;

import com.gig.collide.api.tag.request.TagCreateRequest;
import com.gig.collide.api.tag.request.TagQueryRequest;
import com.gig.collide.api.tag.request.TagUpdateRequest;
import com.gig.collide.api.tag.request.UserInterestTagRequest;
import com.gig.collide.api.tag.response.TagOperatorResponse;
import com.gig.collide.api.tag.response.TagQueryResponse;
import com.gig.collide.api.tag.response.data.TagInfo;
import com.gig.collide.api.tag.TagFacadeService;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.tag.domain.entity.Tag;
import com.gig.collide.tag.domain.entity.UserInterestTag;
import com.gig.collide.tag.domain.entity.convertor.TagConvertor;
import com.gig.collide.tag.domain.service.TagService;
import com.gig.collide.tag.domain.service.UserInterestTagService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 标签门面服务实现
 * 
 * @author GIG Team
 * @version 1.0.0
 * @since 2024-12-19
 */
@Slf4j
@DubboService(version = "1.0.0")
public class TagFacadeServiceImpl implements TagFacadeService {

    @Autowired
    private TagService tagService;
    
    @Autowired
    private UserInterestTagService userInterestTagService;

    @Override
    public TagOperatorResponse createTag(TagCreateRequest request) {
        log.info("创建标签请求开始，参数：{}", request);
        
        try {
            Tag tag = tagService.createTag(request);
            
            log.info("创建标签请求成功，标签ID：{}", tag.getId());
                         return TagOperatorResponse.success(tag.getId());
        } catch (Exception e) {
            log.error("创建标签请求失败", e);
            return TagOperatorResponse.error("TAG_CREATE_ERROR", "创建标签失败：" + e.getMessage());
        }
    }

    @Override
    public TagOperatorResponse updateTag(TagUpdateRequest request) {
        log.info("更新标签请求开始，参数：{}", request);
        
                 try {
             if (request.getTagId() == null) {
                 return TagOperatorResponse.error("PARAM_ERROR", "标签ID不能为空");
             }
             
             Tag tag = tagService.updateTag(request.getTagId(), request);
             
             log.info("更新标签请求成功，标签ID：{}", tag.getId());
             return TagOperatorResponse.success(tag.getId());
        } catch (Exception e) {
            log.error("更新标签请求失败", e);
            return TagOperatorResponse.error("TAG_UPDATE_ERROR", "更新标签失败：" + e.getMessage());
        }
    }

    @Override
    public TagOperatorResponse deleteTag(Long tagId) {
        log.info("删除标签请求开始，标签ID：{}", tagId);
        
        try {
            if (tagId == null) {
                return TagOperatorResponse.error("PARAM_ERROR", "标签ID不能为空");
            }
            
            boolean success = tagService.deleteTag(tagId);
            
                         if (success) {
                 log.info("删除标签请求成功，标签ID：{}", tagId);
                 return TagOperatorResponse.success(tagId);
             } else {
                 return TagOperatorResponse.error("TAG_DELETE_ERROR", "删除标签失败");
             }
        } catch (Exception e) {
            log.error("删除标签请求失败", e);
            return TagOperatorResponse.error("TAG_DELETE_ERROR", "删除标签失败：" + e.getMessage());
        }
    }

    @Override
    public TagQueryResponse<TagInfo> getTagById(Long tagId) {
        log.info("查询标签详情请求开始，标签ID：{}", tagId);
        
        try {
            if (tagId == null) {
                return TagQueryResponse.error("PARAM_ERROR", "标签ID不能为空");
            }
            
            Tag tag = tagService.getTagById(tagId);
            if (tag == null) {
                return TagQueryResponse.error("TAG_NOT_FOUND", "标签不存在");
            }
            
            TagInfo tagInfo = TagConvertor.INSTANCE.entityToInfo(tag);
            
            log.info("查询标签详情请求成功，标签ID：{}", tagId);
            return TagQueryResponse.success(tagInfo);
        } catch (Exception e) {
            log.error("查询标签详情请求失败", e);
            return TagQueryResponse.error("TAG_QUERY_ERROR", "查询标签详情失败：" + e.getMessage());
        }
    }

    @Override
    public TagQueryResponse<PageResponse<TagInfo>> queryTags(TagQueryRequest request) {
        log.info("分页查询标签请求开始，参数：{}", request);
        
        try {
            PageResponse<Tag> pageResult = tagService.queryTags(request);
            
            // 转换为TagInfo
            List<TagInfo> tagInfoList = TagConvertor.INSTANCE.entityListToInfoList(pageResult.getRecords());
            
            PageResponse<TagInfo> result = PageResponse.of(tagInfoList, (int) pageResult.getTotal(), pageResult.getPageSize(), pageResult.getCurrentPage());
            
            log.info("分页查询标签请求成功，总数：{}", result.getTotal());
            return TagQueryResponse.success(result);
        } catch (Exception e) {
            log.error("分页查询标签请求失败", e);
            return TagQueryResponse.error("TAG_QUERY_ERROR", "分页查询标签失败：" + e.getMessage());
        }
    }

    @Override
    public TagQueryResponse<List<TagInfo>> getTagsByType(String tagType) {
        log.info("根据类型查询标签请求开始，标签类型：{}", tagType);
        
        try {
            if (tagType == null || tagType.trim().isEmpty()) {
                return TagQueryResponse.error("PARAM_ERROR", "标签类型不能为空");
            }
            
            List<Tag> tags = tagService.getTagsByType(tagType);
            List<TagInfo> tagInfoList = TagConvertor.INSTANCE.entityListToInfoList(tags);
            
            log.info("根据类型查询标签请求成功，标签类型：{}，数量：{}", tagType, tagInfoList.size());
            return TagQueryResponse.success(tagInfoList);
        } catch (Exception e) {
            log.error("根据类型查询标签请求失败", e);
            return TagQueryResponse.error("TAG_QUERY_ERROR", "根据类型查询标签失败：" + e.getMessage());
        }
    }

    @Override
    public TagQueryResponse<List<TagInfo>> getHotTags(String tagType, Integer limit) {
        log.info("查询热门标签请求开始，标签类型：{}，限制数量：{}", tagType, limit);
        
        try {
            List<Tag> tags = tagService.getHotTags(tagType, limit);
            List<TagInfo> tagInfoList = TagConvertor.INSTANCE.entityListToInfoList(tags);
            
            log.info("查询热门标签请求成功，标签类型：{}，数量：{}", tagType, tagInfoList.size());
            return TagQueryResponse.success(tagInfoList);
        } catch (Exception e) {
            log.error("查询热门标签请求失败", e);
            return TagQueryResponse.error("TAG_QUERY_ERROR", "查询热门标签失败：" + e.getMessage());
        }
    }

    @Override
    public TagQueryResponse<List<TagInfo>> searchTags(String keyword, String tagType) {
        log.info("搜索标签请求开始，关键词：{}，标签类型：{}", keyword, tagType);
        
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return TagQueryResponse.error("PARAM_ERROR", "搜索关键词不能为空");
            }
            
            List<Tag> tags = tagService.searchTags(keyword, tagType);
            List<TagInfo> tagInfoList = TagConvertor.INSTANCE.entityListToInfoList(tags);
            
            log.info("搜索标签请求成功，关键词：{}，数量：{}", keyword, tagInfoList.size());
            return TagQueryResponse.success(tagInfoList);
        } catch (Exception e) {
            log.error("搜索标签请求失败", e);
            return TagQueryResponse.error("TAG_QUERY_ERROR", "搜索标签失败：" + e.getMessage());
        }
    }

    @Override
    public TagQueryResponse<List<TagInfo>> getUserInterestTags(Long userId) {
        log.info("查询用户兴趣标签请求开始，用户ID：{}", userId);
        
        try {
            if (userId == null) {
                return TagQueryResponse.error("PARAM_ERROR", "用户ID不能为空");
            }
            
            List<UserInterestTag> userInterestTags = userInterestTagService.getUserInterestTags(userId);
            
            // 获取标签详情并设置兴趣分数
            List<TagInfo> tagInfoList = new ArrayList<>();
            for (UserInterestTag userInterestTag : userInterestTags) {
                Tag tag = tagService.getTagById(userInterestTag.getTagId());
                if (tag != null) {
                    TagInfo tagInfo = TagConvertor.INSTANCE.entityToInfo(tag);
                    tagInfo.setInterestScore(userInterestTag.getInterestScore());
                    tagInfo.setInterestSource(userInterestTag.getSource());
                    tagInfoList.add(tagInfo);
                }
            }
            
            log.info("查询用户兴趣标签请求成功，用户ID：{}，数量：{}", userId, tagInfoList.size());
            return TagQueryResponse.success(tagInfoList);
        } catch (Exception e) {
            log.error("查询用户兴趣标签请求失败", e);
            return TagQueryResponse.error("TAG_QUERY_ERROR", "查询用户兴趣标签失败：" + e.getMessage());
        }
    }

    @Override
    public TagOperatorResponse setUserInterestTags(UserInterestTagRequest request) {
        log.info("设置用户兴趣标签请求开始，参数：{}", request);
        
        try {
            boolean success = userInterestTagService.setUserInterestTags(request);
            
                         if (success) {
                 log.info("设置用户兴趣标签请求成功，用户ID：{}", request.getUserId());
                 return TagOperatorResponse.success(request.getUserId());
             } else {
                 return TagOperatorResponse.error("USER_INTEREST_SET_ERROR", "设置用户兴趣标签失败");
             }
        } catch (Exception e) {
            log.error("设置用户兴趣标签请求失败", e);
            return TagOperatorResponse.error("USER_INTEREST_SET_ERROR", "设置用户兴趣标签失败：" + e.getMessage());
        }
    }

    @Override
    public TagOperatorResponse addUserInterestTag(Long userId, Long tagId, Double interestScore) {
        log.info("添加用户兴趣标签请求开始，用户ID：{}，标签ID：{}，兴趣分数：{}", userId, tagId, interestScore);
        
        try {
            boolean success = userInterestTagService.addUserInterestTag(userId, tagId, interestScore);
            
                         if (success) {
                 log.info("添加用户兴趣标签请求成功，用户ID：{}，标签ID：{}", userId, tagId);
                 return TagOperatorResponse.success(tagId);
             } else {
                 return TagOperatorResponse.error("USER_INTEREST_ADD_ERROR", "添加用户兴趣标签失败");
             }
        } catch (Exception e) {
            log.error("添加用户兴趣标签请求失败", e);
            return TagOperatorResponse.error("USER_INTEREST_ADD_ERROR", "添加用户兴趣标签失败：" + e.getMessage());
        }
    }

    @Override
    public TagOperatorResponse removeUserInterestTag(Long userId, Long tagId) {
        log.info("移除用户兴趣标签请求开始，用户ID：{}，标签ID：{}", userId, tagId);
        
        try {
            boolean success = userInterestTagService.removeUserInterestTag(userId, tagId);
            
                         if (success) {
                 log.info("移除用户兴趣标签请求成功，用户ID：{}，标签ID：{}", userId, tagId);
                 return TagOperatorResponse.success(tagId);
             } else {
                 return TagOperatorResponse.error("USER_INTEREST_REMOVE_ERROR", "移除用户兴趣标签失败");
             }
        } catch (Exception e) {
            log.error("移除用户兴趣标签请求失败", e);
            return TagOperatorResponse.error("USER_INTEREST_REMOVE_ERROR", "移除用户兴趣标签失败：" + e.getMessage());
        }
    }

    @Override
    public TagQueryResponse<List<TagInfo>> recommendTagsToUser(Long userId, Integer limit) {
        log.info("推荐标签给用户请求开始，用户ID：{}，限制数量：{}", userId, limit);
        
        try {
            if (userId == null) {
                return TagQueryResponse.error("PARAM_ERROR", "用户ID不能为空");
            }
            
                         List<Long> recommendedTagIds = userInterestTagService.recommendTagsToUser(userId, limit);
             
             // 获取推荐标签的详情
             List<Tag> recommendedTags = tagService.getTagsByIds(recommendedTagIds);
             // 转换为TagInfo
             List<TagInfo> tagInfoList = TagConvertor.INSTANCE.entityListToInfoList(recommendedTags);
            
            log.info("推荐标签给用户请求成功，用户ID：{}，推荐数量：{}", userId, tagInfoList.size());
            return TagQueryResponse.success(tagInfoList);
        } catch (Exception e) {
            log.error("推荐标签给用户请求失败", e);
            return TagQueryResponse.error("TAG_RECOMMEND_ERROR", "推荐标签失败：" + e.getMessage());
        }
    }
} 