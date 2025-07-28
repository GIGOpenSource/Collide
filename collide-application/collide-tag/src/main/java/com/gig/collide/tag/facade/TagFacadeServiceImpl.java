package com.gig.collide.tag.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.tag.TagFacadeService;
import com.gig.collide.api.tag.request.TagCreateRequest;
import com.gig.collide.api.tag.request.TagQueryRequest;
import com.gig.collide.api.tag.request.TagUpdateRequest;
import com.gig.collide.api.tag.response.TagResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.tag.domain.entity.Tag;
import com.gig.collide.tag.domain.service.TagService;
import com.gig.collide.web.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签门面服务实现类 - 简洁版
 *
 * @author GIG Team
 * @version 2.0.0
 */
@Slf4j
@DubboService(version = "2.0.0")
public class TagFacadeServiceImpl implements TagFacadeService {

    @Autowired
    private TagService tagService;

    @Override
    public Result<TagResponse> createTag(TagCreateRequest request) {
        try {
            Tag tag = new Tag();
            BeanUtils.copyProperties(request, tag);
            
            Tag savedTag = tagService.createTag(tag);
            TagResponse response = convertToResponse(savedTag);
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("创建标签失败", e);
            return Result.error("TAG_CREATE_ERROR", "创建标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<TagResponse> updateTag(TagUpdateRequest request) {
        try {
            Tag tag = new Tag();
            BeanUtils.copyProperties(request, tag);
            
            Tag updatedTag = tagService.updateTag(tag);
            TagResponse response = convertToResponse(updatedTag);
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("更新标签失败", e);
            return Result.error("TAG_UPDATE_ERROR", "更新标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> deleteTag(Long tagId) {
        try {
            tagService.deleteTag(tagId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("删除标签失败", e);
            return Result.error("TAG_DELETE_ERROR", "删除标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<TagResponse> getTagById(Long tagId) {
        try {
            Tag tag = tagService.getTagById(tagId);
            if (tag == null) {
                return Result.error("TAG_NOT_FOUND", "标签不存在");
            }
            
            TagResponse response = convertToResponse(tag);
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询标签失败", e);
            return Result.error("TAG_QUERY_ERROR", "查询标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<TagResponse>> queryTags(TagQueryRequest request) {
        try {
            IPage<Tag> page = tagService.queryTags(
                request.getCurrentPage(),
                request.getPageSize(),
                request.getName(),
                request.getTagType(),
                request.getCategoryId(),
                request.getStatus()
            );
            
            List<TagResponse> responses = page.getRecords().stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            PageResponse<TagResponse> result = new PageResponse<>();
            result.setDatas(responses);
            result.setTotal((int) page.getTotal());
            result.setCurrentPage(request.getCurrentPage());
            result.setPageSize(request.getPageSize());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("分页查询标签失败", e);
            return Result.error("TAG_QUERY_ERROR", "分页查询标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<TagResponse>> getTagsByType(String tagType) {
        try {
            List<Tag> tags = tagService.getTagsByType(tagType);
            List<TagResponse> responses = tags.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("根据类型查询标签失败", e);
            return Result.error("TAG_QUERY_ERROR", "根据类型查询标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<TagResponse>> searchTags(String keyword, Integer limit) {
        try {
            List<Tag> tags = tagService.searchTags(keyword, limit);
            List<TagResponse> responses = tags.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("搜索标签失败", e);
            return Result.error("TAG_SEARCH_ERROR", "搜索标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<TagResponse>> getHotTags(Integer limit) {
        try {
            List<Tag> tags = tagService.getHotTags(limit);
            List<TagResponse> responses = tags.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("获取热门标签失败", e);
            return Result.error("TAG_QUERY_ERROR", "获取热门标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<TagResponse>> getUserInterestTags(Long userId) {
        try {
            List<Tag> tags = tagService.getUserInterestTags(userId);
            List<TagResponse> responses = tags.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("获取用户兴趣标签失败", e);
            return Result.error("TAG_QUERY_ERROR", "获取用户兴趣标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> addUserInterestTag(Long userId, Long tagId, Double interestScore) {
        try {
            BigDecimal score = interestScore != null ? BigDecimal.valueOf(interestScore) : null;
            tagService.addUserInterestTag(userId, tagId, score);
            return Result.success(null);
        } catch (Exception e) {
            log.error("添加用户兴趣标签失败", e);
            return Result.error("TAG_INTEREST_ERROR", "添加用户兴趣标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> removeUserInterestTag(Long userId, Long tagId) {
        try {
            tagService.removeUserInterestTag(userId, tagId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("移除用户兴趣标签失败", e);
            return Result.error("TAG_INTEREST_ERROR", "移除用户兴趣标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> updateUserInterestScore(Long userId, Long tagId, Double interestScore) {
        try {
            BigDecimal score = BigDecimal.valueOf(interestScore);
            tagService.updateUserInterestScore(userId, tagId, score);
            return Result.success(null);
        } catch (Exception e) {
            log.error("更新用户兴趣分数失败", e);
            return Result.error("TAG_INTEREST_ERROR", "更新用户兴趣分数失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> addContentTag(Long contentId, Long tagId) {
        try {
            tagService.addContentTag(contentId, tagId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("为内容添加标签失败", e);
            return Result.error("TAG_CONTENT_ERROR", "为内容添加标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> removeContentTag(Long contentId, Long tagId) {
        try {
            tagService.removeContentTag(contentId, tagId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("移除内容标签失败", e);
            return Result.error("TAG_CONTENT_ERROR", "移除内容标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<TagResponse>> getContentTags(Long contentId) {
        try {
            List<Tag> tags = tagService.getContentTags(contentId);
            List<TagResponse> responses = tags.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("获取内容标签失败", e);
            return Result.error("TAG_QUERY_ERROR", "获取内容标签失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> increaseTagUsage(Long tagId) {
        try {
            tagService.increaseTagUsage(tagId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("增加标签使用次数失败", e);
            return Result.error("TAG_UPDATE_ERROR", "增加标签使用次数失败: " + e.getMessage());
        }
    }

    /**
     * 转换为响应对象
     */
    private TagResponse convertToResponse(Tag tag) {
        TagResponse response = new TagResponse();
        BeanUtils.copyProperties(tag, response);
        return response;
    }
} 