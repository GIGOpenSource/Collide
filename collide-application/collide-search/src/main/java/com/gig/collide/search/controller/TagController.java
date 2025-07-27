package com.gig.collide.search.controller;

import com.gig.collide.api.tag.TagFacadeService;
import com.gig.collide.api.tag.request.TagCreateRequest;
import com.gig.collide.api.tag.request.TagQueryRequest;
import com.gig.collide.api.tag.request.TagUpdateRequest;
import com.gig.collide.api.tag.request.UserInterestTagRequest;
import com.gig.collide.api.tag.response.TagOperatorResponse;
import com.gig.collide.api.tag.response.TagQueryResponse;
import com.gig.collide.api.tag.response.data.TagInfo;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签管理控制器
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "标签管理", description = "标签和用户兴趣管理相关接口")
public class TagController {

    private final TagFacadeService tagFacadeService;

    @Operation(summary = "创建标签", description = "创建新的标签")
    @PostMapping
    public Result<Long> createTag(@RequestBody TagCreateRequest request) {
        TagOperatorResponse response = tagFacadeService.createTag(request);
        if (response.getSuccess()) {
            return Result.success(response.getTagId());
        } else {
            return Result.error(response.getErrorCode(), response.getErrorMessage());
        }
    }

    @Operation(summary = "更新标签", description = "更新标签信息")
    @PutMapping("/{tagId}")
    public Result<Void> updateTag(
            @Parameter(description = "标签ID") @PathVariable Long tagId,
            @RequestBody TagUpdateRequest request) {
        request.setTagId(tagId);
        TagOperatorResponse response = tagFacadeService.updateTag(request);
        if (response.getSuccess()) {
            return Result.success(null);
        } else {
            return Result.error(response.getErrorCode(), response.getErrorMessage());
        }
    }

    @Operation(summary = "删除标签", description = "删除指定标签")
    @DeleteMapping("/{tagId}")
    public Result<Void> deleteTag(@Parameter(description = "标签ID") @PathVariable Long tagId) {
        TagOperatorResponse response = tagFacadeService.deleteTag(tagId);
        if (response.getSuccess()) {
            return Result.success(null);
        } else {
            return Result.error(response.getErrorCode(), response.getErrorMessage());
        }
    }

    @Operation(summary = "获取标签详情", description = "根据ID获取标签详细信息")
    @GetMapping("/{tagId}")
    public Result<TagInfo> getTagById(@Parameter(description = "标签ID") @PathVariable Long tagId) {
        TagQueryResponse<TagInfo> response = tagFacadeService.getTagById(tagId);
        if (response.getSuccess()) {
            return Result.success(response.getData());
        } else {
            return Result.error(response.getErrorCode(), response.getErrorMessage());
        }
    }

    @Operation(summary = "分页查询标签", description = "分页查询标签列表")
    @GetMapping
    public Result<PageResponse<TagInfo>> queryTags(TagQueryRequest request) {
        TagQueryResponse<PageResponse<TagInfo>> response = tagFacadeService.queryTags(request);
        if (response.getSuccess()) {
            return Result.success(response.getData());
        } else {
            return Result.error(response.getErrorCode(), response.getErrorMessage());
        }
    }

    @Operation(summary = "获取指定类型标签", description = "根据类型获取标签列表")
    @GetMapping("/type/{tagType}")
    public Result<List<TagInfo>> getTagsByType(@Parameter(description = "标签类型") @PathVariable String tagType) {
        TagQueryResponse<List<TagInfo>> response = tagFacadeService.getTagsByType(tagType);
        if (response.getSuccess()) {
            return Result.success(response.getData());
        } else {
            return Result.error(response.getErrorCode(), response.getErrorMessage());
        }
    }

    @Operation(summary = "获取热门标签", description = "获取热门标签列表")
    @GetMapping("/hot")
    public Result<List<TagInfo>> getHotTags(
            @Parameter(description = "标签类型") @RequestParam(required = false) String tagType,
            @Parameter(description = "返回数量限制") @RequestParam(defaultValue = "20") Integer limit) {
        TagQueryResponse<List<TagInfo>> response = tagFacadeService.getHotTags(tagType, limit);
        if (response.getSuccess()) {
            return Result.success(response.getData());
        } else {
            return Result.error(response.getErrorCode(), response.getErrorMessage());
        }
    }

    @Operation(summary = "搜索标签", description = "根据关键词搜索标签")
    @GetMapping("/search")
    public Result<List<TagInfo>> searchTags(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "标签类型") @RequestParam(required = false) String tagType) {
        TagQueryResponse<List<TagInfo>> response = tagFacadeService.searchTags(keyword, tagType);
        if (response.getSuccess()) {
            return Result.success(response.getData());
        } else {
            return Result.error(response.getErrorCode(), response.getErrorMessage());
        }
    }

    @Operation(summary = "获取用户兴趣标签", description = "获取指定用户的兴趣标签列表")
    @GetMapping("/user/{userId}/interests")
    public Result<List<TagInfo>> getUserInterestTags(@Parameter(description = "用户ID") @PathVariable Long userId) {
        TagQueryResponse<List<TagInfo>> response = tagFacadeService.getUserInterestTags(userId);
        if (response.getSuccess()) {
            return Result.success(response.getData());
        } else {
            return Result.error(response.getErrorCode(), response.getErrorMessage());
        }
    }

    @Operation(summary = "设置用户兴趣标签", description = "批量设置用户的兴趣标签")
    @PostMapping("/user/{userId}/interests")
    public Result<Void> setUserInterestTags(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @RequestBody UserInterestTagRequest request) {
        request.setUserId(userId);
        TagOperatorResponse response = tagFacadeService.setUserInterestTags(request);
        if (response.getSuccess()) {
            return Result.success(null);
        } else {
            return Result.error(response.getErrorCode(), response.getErrorMessage());
        }
    }

    @Operation(summary = "添加用户兴趣标签", description = "为用户添加单个兴趣标签")
    @PostMapping("/user/{userId}/interests/{tagId}")
    public Result<Void> addUserInterestTag(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "标签ID") @PathVariable Long tagId,
            @Parameter(description = "兴趣分数") @RequestParam(defaultValue = "1.0") Double interestScore) {
        TagOperatorResponse response = tagFacadeService.addUserInterestTag(userId, tagId, interestScore);
        if (response.getSuccess()) {
            return Result.success(null);
        } else {
            return Result.error(response.getErrorCode(), response.getErrorMessage());
        }
    }

    @Operation(summary = "移除用户兴趣标签", description = "移除用户的指定兴趣标签")
    @DeleteMapping("/user/{userId}/interests/{tagId}")
    public Result<Void> removeUserInterestTag(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "标签ID") @PathVariable Long tagId) {
        TagOperatorResponse response = tagFacadeService.removeUserInterestTag(userId, tagId);
        if (response.getSuccess()) {
            return Result.success(null);
        } else {
            return Result.error(response.getErrorCode(), response.getErrorMessage());
        }
    }

    @Operation(summary = "推荐标签给用户", description = "基于用户行为推荐标签")
    @GetMapping("/user/{userId}/recommendations")
    public Result<List<TagInfo>> recommendTagsToUser(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "推荐数量") @RequestParam(defaultValue = "10") Integer limit) {
        TagQueryResponse<List<TagInfo>> response = tagFacadeService.recommendTagsToUser(userId, limit);
        if (response.getSuccess()) {
            return Result.success(response.getData());
        } else {
            return Result.error(response.getErrorCode(), response.getErrorMessage());
        }
    }
} 