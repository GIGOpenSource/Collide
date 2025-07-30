package com.gig.collide.tag.controller;

import com.gig.collide.api.tag.TagFacadeService;
import com.gig.collide.api.tag.request.TagCreateRequest;
import com.gig.collide.api.tag.request.TagQueryRequest;
import com.gig.collide.api.tag.request.TagUpdateRequest;
import com.gig.collide.api.tag.response.TagResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 标签管理控制器 - 缓存增强版
 * 基于JetCache双级缓存，对齐collide-content设计风格
 *
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagFacadeService tagFacadeService;

    /**
     * 创建标签
     */
    @PostMapping
    public Result<Void> createTag(@Valid @RequestBody TagCreateRequest request) {
        log.info("REST - 创建标签：{}", request);
        return tagFacadeService.createTag(request);
    }

    /**
     * 更新标签
     */
    @PutMapping("/{tagId}")
    public Result<TagResponse> updateTag(@PathVariable Long tagId, @Valid @RequestBody TagUpdateRequest request) {
        log.info("REST - 更新标签，ID：{}，请求：{}", tagId, request);
        request.setId(tagId);
        return tagFacadeService.updateTag(request);
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/{tagId}")
    public Result<Void> deleteTag(@PathVariable Long tagId) {
        log.info("REST - 删除标签，ID：{}", tagId);
        return tagFacadeService.deleteTag(tagId);
    }

    /**
     * 根据ID查询标签
     */
    @GetMapping("/{tagId}")
    public Result<TagResponse> getTag(@PathVariable Long tagId) {
        log.info("REST - 查询标签，ID：{}", tagId);
        return tagFacadeService.getTagById(tagId);
    }

    /**
     * 分页查询标签 - 专用端点
     */
    @PostMapping("/page")
    public PageResponse<TagResponse> queryTagsPost(@Valid @RequestBody TagQueryRequest request) {
        log.info("REST - POST分页查询标签：{}", request);
        Result<PageResponse<TagResponse>> result = tagFacadeService.queryTags(request);
        return result.getData();
    }

    /**
     * 分页查询标签 - GET方式（兼容性支持）
     */
    @GetMapping("/page")
    public PageResponse<TagResponse> queryTagsGet(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String tagType,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "active") String status,
            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "create_time") String orderBy,
            @RequestParam(defaultValue = "DESC") String orderDirection) {
        
        log.info("REST - GET分页查询标签，名称：{}，类型：{}，分类：{}，状态：{}，页码：{}，大小：{}", 
                name, tagType, categoryId, status, currentPage, pageSize);
        
        TagQueryRequest request = new TagQueryRequest();
        request.setName(name);
        request.setTagType(tagType);
        request.setCategoryId(categoryId);
        request.setStatus(status);
        request.setCurrentPage(currentPage);
        request.setPageSize(pageSize);
        request.setOrderBy(orderBy);
        request.setOrderDirection(orderDirection);
        
        Result<PageResponse<TagResponse>> result = tagFacadeService.queryTags(request);
        return result.getData();
    }

    /**
     * 分页查询标签 - 默认GET方式
     */
    @GetMapping
    public PageResponse<TagResponse> queryTags(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String tagType,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "active") String status,
            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "create_time") String orderBy,
            @RequestParam(defaultValue = "DESC") String orderDirection) {
        
        log.info("REST - 默认分页查询标签，名称：{}，类型：{}，分类：{}，状态：{}，页码：{}，大小：{}", 
                name, tagType, categoryId, status, currentPage, pageSize);
        
        TagQueryRequest request = new TagQueryRequest();
        request.setName(name);
        request.setTagType(tagType);
        request.setCategoryId(categoryId);
        request.setStatus(status);
        request.setCurrentPage(currentPage);
        request.setPageSize(pageSize);
        request.setOrderBy(orderBy);
        request.setOrderDirection(orderDirection);
        
        Result<PageResponse<TagResponse>> result = tagFacadeService.queryTags(request);
        return result.getData();
    }

    /**
     * 根据类型获取标签
     */
    @GetMapping("/type/{tagType}")
    public Result<List<TagResponse>> getTagsByType(@PathVariable String tagType) {
        log.info("REST - 根据类型查询标签，类型：{}", tagType);
        return tagFacadeService.getTagsByType(tagType);
    }

    /**
     * 搜索标签
     */
    @GetMapping("/search")
    public Result<List<TagResponse>> searchTags(@RequestParam String keyword, 
                                               @RequestParam(defaultValue = "10") Integer limit) {
        log.info("REST - 搜索标签，关键词：{}，限制：{}", keyword, limit);
        return tagFacadeService.searchTags(keyword, limit);
    }

    /**
     * 获取热门标签
     */
    @GetMapping("/hot")
    public Result<List<TagResponse>> getHotTags(@RequestParam(defaultValue = "10") Integer limit) {
        log.info("REST - 获取热门标签，限制：{}", limit);
        return tagFacadeService.getHotTags(limit);
    }

    /**
     * 获取用户兴趣标签
     */
    @GetMapping("/user/{userId}/interests")
    public Result<List<TagResponse>> getUserInterestTags(@PathVariable Long userId) {
        log.info("REST - 获取用户兴趣标签，用户ID：{}", userId);
        return tagFacadeService.getUserInterestTags(userId);
    }

    /**
     * 添加用户兴趣标签
     */
    @PostMapping("/user/{userId}/interests/{tagId}")
    public Result<Void> addUserInterestTag(@PathVariable Long userId, 
                                          @PathVariable Long tagId,
                                          @RequestParam(defaultValue = "50.0") Double interestScore) {
        log.info("REST - 添加用户兴趣标签，用户ID：{}，标签ID：{}，兴趣分数：{}", userId, tagId, interestScore);
        return tagFacadeService.addUserInterestTag(userId, tagId, interestScore);
    }

    /**
     * 移除用户兴趣标签
     */
    @DeleteMapping("/user/{userId}/interests/{tagId}")
    public Result<Void> removeUserInterestTag(@PathVariable Long userId, @PathVariable Long tagId) {
        log.info("REST - 移除用户兴趣标签，用户ID：{}，标签ID：{}", userId, tagId);
        return tagFacadeService.removeUserInterestTag(userId, tagId);
    }

    /**
     * 获取内容标签
     */
    @GetMapping("/content/{contentId}")
    public Result<List<TagResponse>> getContentTags(@PathVariable Long contentId) {
        log.info("REST - 获取内容标签，内容ID：{}", contentId);
        return tagFacadeService.getContentTags(contentId);
    }

    /**
     * 为内容添加标签
     */
    @PostMapping("/content/{contentId}/tags/{tagId}")
    public Result<Void> addContentTag(@PathVariable Long contentId, @PathVariable Long tagId) {
        log.info("REST - 为内容添加标签，内容ID：{}，标签ID：{}", contentId, tagId);
        return tagFacadeService.addContentTag(contentId, tagId);
    }

    /**
     * 移除内容标签
     */
    @DeleteMapping("/content/{contentId}/tags/{tagId}")
    public Result<Void> removeContentTag(@PathVariable Long contentId, @PathVariable Long tagId) {
        log.info("REST - 移除内容标签，内容ID：{}，标签ID：{}", contentId, tagId);
        return tagFacadeService.removeContentTag(contentId, tagId);
    }
} 