package com.gig.collide.tag.controller;

import com.gig.collide.api.tag.request.TagCreateRequest;
import com.gig.collide.api.tag.request.TagQueryRequest;
import com.gig.collide.api.tag.request.TagUpdateRequest;
import com.gig.collide.api.tag.request.UserInterestTagRequest;
import com.gig.collide.api.tag.response.TagOperatorResponse;
import com.gig.collide.api.tag.response.TagQueryResponse;
import com.gig.collide.api.tag.response.data.TagInfo;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.tag.facade.TagFacadeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 标签控制器
 * 提供标签管理的HTTP API接口
 * 
 * @author GIG Team
 * @version 1.0.0
 * @since 2024-12-19
 */
@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private TagFacadeServiceImpl tagFacadeService;

    /**
     * 创建标签
     * 
     * @param request 创建请求
     * @return 创建结果
     */
    @PostMapping
    public TagOperatorResponse createTag(@Valid @RequestBody TagCreateRequest request) {
        return tagFacadeService.createTag(request);
    }

    /**
     * 更新标签
     * 
     * @param request 更新请求
     * @return 更新结果
     */
    @PutMapping
    public TagOperatorResponse updateTag(@Valid @RequestBody TagUpdateRequest request) {
        return tagFacadeService.updateTag(request);
    }

    /**
     * 删除标签
     * 
     * @param tagId 标签ID
     * @return 删除结果
     */
    @DeleteMapping("/{tagId}")
    public TagOperatorResponse deleteTag(@PathVariable Long tagId) {
        return tagFacadeService.deleteTag(tagId);
    }

    /**
     * 根据ID查询标签详情
     * 
     * @param tagId 标签ID
     * @return 标签详情
     */
    @GetMapping("/{tagId}")
    public TagQueryResponse<TagInfo> getTagById(@PathVariable Long tagId) {
        return tagFacadeService.getTagById(tagId);
    }

    /**
     * 分页查询标签
     * 
     * @param request 查询请求
     * @return 分页结果
     */
    @PostMapping("/query")
    public TagQueryResponse<PageResponse<TagInfo>> queryTags(@Valid @RequestBody TagQueryRequest request) {
        return tagFacadeService.queryTags(request);
    }

    /**
     * 根据类型查询标签
     * 
     * @param tagType 标签类型
     * @return 标签列表
     */
    @GetMapping("/type/{tagType}")
    public TagQueryResponse<List<TagInfo>> getTagsByType(@PathVariable String tagType) {
        return tagFacadeService.getTagsByType(tagType);
    }

    /**
     * 查询热门标签
     * 
     * @param tagType 标签类型（可选）
     * @param limit 限制数量（可选）
     * @return 热门标签列表
     */
    @GetMapping("/hot")
    public TagQueryResponse<List<TagInfo>> getHotTags(
            @RequestParam(required = false) String tagType,
            @RequestParam(required = false) Integer limit) {
        return tagFacadeService.getHotTags(tagType, limit);
    }

    /**
     * 搜索标签
     * 
     * @param keyword 搜索关键词
     * @param tagType 标签类型（可选）
     * @return 搜索结果
     */
    @GetMapping("/search")
    public TagQueryResponse<List<TagInfo>> searchTags(
            @RequestParam String keyword,
            @RequestParam(required = false) String tagType) {
        return tagFacadeService.searchTags(keyword, tagType);
    }

    /**
     * 查询用户兴趣标签
     * 
     * @param userId 用户ID
     * @return 用户兴趣标签列表
     */
    @GetMapping("/user/{userId}/interests")
    public TagQueryResponse<List<TagInfo>> getUserInterestTags(@PathVariable Long userId) {
        return tagFacadeService.getUserInterestTags(userId);
    }

    /**
     * 设置用户兴趣标签
     * 
     * @param request 用户兴趣标签请求
     * @return 设置结果
     */
    @PostMapping("/user/interests")
    public TagOperatorResponse setUserInterestTags(@Valid @RequestBody UserInterestTagRequest request) {
        return tagFacadeService.setUserInterestTags(request);
    }

    /**
     * 添加用户兴趣标签
     * 
     * @param userId 用户ID
     * @param tagId 标签ID
     * @param interestScore 兴趣分数（可选）
     * @return 添加结果
     */
    @PostMapping("/user/{userId}/interests/{tagId}")
    public TagOperatorResponse addUserInterestTag(
            @PathVariable Long userId,
            @PathVariable Long tagId,
            @RequestParam(required = false) Double interestScore) {
        return tagFacadeService.addUserInterestTag(userId, tagId, interestScore);
    }

    /**
     * 移除用户兴趣标签
     * 
     * @param userId 用户ID
     * @param tagId 标签ID
     * @return 移除结果
     */
    @DeleteMapping("/user/{userId}/interests/{tagId}")
    public TagOperatorResponse removeUserInterestTag(@PathVariable Long userId, @PathVariable Long tagId) {
        return tagFacadeService.removeUserInterestTag(userId, tagId);
    }

    /**
     * 推荐标签给用户
     * 
     * @param userId 用户ID
     * @param limit 推荐数量限制（可选）
     * @return 推荐标签列表
     */
    @GetMapping("/user/{userId}/recommend")
    public TagQueryResponse<List<TagInfo>> recommendTagsToUser(
            @PathVariable Long userId,
            @RequestParam(required = false) Integer limit) {
        return tagFacadeService.recommendTagsToUser(userId, limit);
    }
} 