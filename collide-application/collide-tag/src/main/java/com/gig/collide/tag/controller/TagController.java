package com.gig.collide.tag.controller;

import com.gig.collide.api.tag.request.TagCreateRequest;
import com.gig.collide.api.tag.request.TagQueryRequest;
import com.gig.collide.api.tag.request.TagUpdateRequest;
import com.gig.collide.api.tag.response.TagResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.tag.domain.service.TagService;
import com.gig.collide.web.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 标签管理控制器 - 简洁版
 *
 * @author GIG Team
 * @version 2.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * 创建标签
     */
    @PostMapping
    public Result<TagResponse> createTag(@Valid @RequestBody TagCreateRequest request) {
        try {
            // 调用facade服务处理
            // 这里简化处理，直接返回成功消息
            return Result.success(null);
        } catch (Exception e) {
            log.error("创建标签失败", e);
            return Result.error("TAG_CREATE_ERROR", "创建标签失败: " + e.getMessage());
        }
    }

    /**
     * 更新标签
     */
    @PutMapping("/{tagId}")
    public Result<TagResponse> updateTag(@PathVariable Long tagId, @Valid @RequestBody TagUpdateRequest request) {
        try {
            request.setId(tagId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("更新标签失败", e);
            return Result.error("TAG_UPDATE_ERROR", "更新标签失败: " + e.getMessage());
        }
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/{tagId}")
    public Result<Void> deleteTag(@PathVariable Long tagId) {
        try {
            tagService.deleteTag(tagId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("删除标签失败", e);
            return Result.error("TAG_DELETE_ERROR", "删除标签失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询标签
     */
    @GetMapping("/{tagId}")
    public Result<TagResponse> getTag(@PathVariable Long tagId) {
        try {
            return Result.success(null);
        } catch (Exception e) {
            log.error("查询标签失败", e);
            return Result.error("TAG_QUERY_ERROR", "查询标签失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询标签 - 专用端点
     */
    @PostMapping("/page")
    public Result<PageResponse<TagResponse>> queryTagsPost(@Valid @RequestBody TagQueryRequest request) {
        try {
            return Result.success(null);
        } catch (Exception e) {
            log.error("分页查询标签失败", e);
            return Result.error("TAG_QUERY_ERROR", "分页查询标签失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询标签 - GET方式（兼容性支持）
     */
    @GetMapping("/page")
    public Result<PageResponse<TagResponse>> queryTagsGet(@Valid TagQueryRequest request) {
        try {
            return Result.success(null);
        } catch (Exception e) {
            log.error("分页查询标签失败", e);
            return Result.error("TAG_QUERY_ERROR", "分页查询标签失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询标签 - 默认GET方式
     */
    @GetMapping
    public Result<PageResponse<TagResponse>> queryTags(@Valid TagQueryRequest request) {
        try {
            return Result.success(null);
        } catch (Exception e) {
            log.error("分页查询标签失败", e);
            return Result.error("TAG_QUERY_ERROR", "分页查询标签失败: " + e.getMessage());
        }
    }

    /**
     * 根据类型获取标签
     */
    @GetMapping("/type/{tagType}")
    public Result<List<TagResponse>> getTagsByType(@PathVariable String tagType) {
        try {
            return Result.success(null);
        } catch (Exception e) {
            log.error("根据类型查询标签失败", e);
            return Result.error("TAG_QUERY_ERROR", "根据类型查询标签失败: " + e.getMessage());
        }
    }

    /**
     * 搜索标签
     */
    @GetMapping("/search")
    public Result<List<TagResponse>> searchTags(@RequestParam String keyword, 
                                               @RequestParam(defaultValue = "10") Integer limit) {
        try {
            return Result.success(null);
        } catch (Exception e) {
            log.error("搜索标签失败", e);
            return Result.error("TAG_SEARCH_ERROR", "搜索标签失败: " + e.getMessage());
        }
    }

    /**
     * 获取热门标签
     */
    @GetMapping("/hot")
    public Result<List<TagResponse>> getHotTags(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            return Result.success(null);
        } catch (Exception e) {
            log.error("获取热门标签失败", e);
            return Result.error("TAG_QUERY_ERROR", "获取热门标签失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户兴趣标签
     */
    @GetMapping("/user/{userId}/interests")
    public Result<List<TagResponse>> getUserInterestTags(@PathVariable Long userId) {
        try {
            return Result.success(null);
        } catch (Exception e) {
            log.error("获取用户兴趣标签失败", e);
            return Result.error("TAG_QUERY_ERROR", "获取用户兴趣标签失败: " + e.getMessage());
        }
    }

    /**
     * 添加用户兴趣标签
     */
    @PostMapping("/user/{userId}/interests/{tagId}")
    public Result<Void> addUserInterestTag(@PathVariable Long userId, 
                                          @PathVariable Long tagId,
                                          @RequestParam(defaultValue = "50.0") Double interestScore) {
        try {
            tagService.addUserInterestTag(userId, tagId, java.math.BigDecimal.valueOf(interestScore));
            return Result.success(null);
        } catch (Exception e) {
            log.error("添加用户兴趣标签失败", e);
            return Result.error("TAG_INTEREST_ERROR", "添加用户兴趣标签失败: " + e.getMessage());
        }
    }

    /**
     * 移除用户兴趣标签
     */
    @DeleteMapping("/user/{userId}/interests/{tagId}")
    public Result<Void> removeUserInterestTag(@PathVariable Long userId, @PathVariable Long tagId) {
        try {
            tagService.removeUserInterestTag(userId, tagId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("移除用户兴趣标签失败", e);
            return Result.error("TAG_INTEREST_ERROR", "移除用户兴趣标签失败: " + e.getMessage());
        }
    }

    /**
     * 获取内容标签
     */
    @GetMapping("/content/{contentId}")
    public Result<List<TagResponse>> getContentTags(@PathVariable Long contentId) {
        try {
            return Result.success(null);
        } catch (Exception e) {
            log.error("获取内容标签失败", e);
            return Result.error("TAG_QUERY_ERROR", "获取内容标签失败: " + e.getMessage());
        }
    }

    /**
     * 为内容添加标签
     */
    @PostMapping("/content/{contentId}/tags/{tagId}")
    public Result<Void> addContentTag(@PathVariable Long contentId, @PathVariable Long tagId) {
        try {
            tagService.addContentTag(contentId, tagId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("为内容添加标签失败", e);
            return Result.error("TAG_CONTENT_ERROR", "为内容添加标签失败: " + e.getMessage());
        }
    }

    /**
     * 移除内容标签
     */
    @DeleteMapping("/content/{contentId}/tags/{tagId}")
    public Result<Void> removeContentTag(@PathVariable Long contentId, @PathVariable Long tagId) {
        try {
            tagService.removeContentTag(contentId, tagId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("移除内容标签失败", e);
            return Result.error("TAG_CONTENT_ERROR", "移除内容标签失败: " + e.getMessage());
        }
    }
} 