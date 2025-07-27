package com.gig.collide.search.controller;

import com.gig.collide.api.category.CategoryFacadeService;
import com.gig.collide.api.category.request.CategoryCreateRequest;
import com.gig.collide.api.category.request.CategoryQueryRequest;
import com.gig.collide.api.category.request.CategoryUpdateRequest;
import com.gig.collide.api.category.response.CategoryOperatorResponse;
import com.gig.collide.api.category.response.CategoryQueryResponse;
import com.gig.collide.api.category.response.data.CategoryInfo;
import com.gig.collide.api.category.response.data.CategoryTree;
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
 * 分类管理控制器
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "分类管理", description = "内容分类管理相关接口")
public class CategoryController {

    private final CategoryFacadeService categoryFacadeService;

    @Operation(summary = "创建分类", description = "创建新的内容分类")
    @PostMapping
    public Result<Long> createCategory(@RequestBody CategoryCreateRequest request) {
        CategoryOperatorResponse response = categoryFacadeService.createCategory(request);
        if (response.getSuccess()) {
            return Result.success(response.getCategoryId());
        } else {
            return Result.error(response.getErrorCode(), response.getErrorMessage());
        }
    }

    @Operation(summary = "更新分类", description = "更新分类信息")
    @PutMapping("/{categoryId}")
    public Result<Void> updateCategory(
            @Parameter(description = "分类ID") @PathVariable Long categoryId,
            @RequestBody CategoryUpdateRequest request) {
        request.setCategoryId(categoryId);
        CategoryOperatorResponse response = categoryFacadeService.updateCategory(request);
        if (response.getSuccess()) {
            return Result.success(null);
        } else {
            return Result.error(response.getErrorCode(), response.getErrorMessage());
        }
    }

    @Operation(summary = "删除分类", description = "删除指定分类")
    @DeleteMapping("/{categoryId}")
    public Result<Void> deleteCategory(@Parameter(description = "分类ID") @PathVariable Long categoryId) {
        CategoryOperatorResponse response = categoryFacadeService.deleteCategory(categoryId);
        if (response.getSuccess()) {
            return Result.success(null);
        } else {
            return Result.error(response.getErrorCode(), response.getErrorMessage());
        }
    }

    @Operation(summary = "获取分类详情", description = "根据ID获取分类详细信息")
    @GetMapping("/{categoryId}")
    public Result<CategoryInfo> getCategoryById(@Parameter(description = "分类ID") @PathVariable Long categoryId) {
        CategoryQueryResponse<CategoryInfo> response = categoryFacadeService.getCategoryById(categoryId);
        if (response.getSuccess()) {
            return Result.success(response.getData());
        } else {
            return Result.error(response.getErrorCode(), response.getErrorMessage());
        }
    }

    @Operation(summary = "分页查询分类", description = "分页查询分类列表")
    @GetMapping
    public Result<PageResponse<CategoryInfo>> queryCategories(CategoryQueryRequest request) {
        CategoryQueryResponse<PageResponse<CategoryInfo>> response = categoryFacadeService.queryCategories(request);
        if (response.getSuccess()) {
            return Result.success(response.getData());
        } else {
            return Result.error(response.getErrorCode(), response.getErrorMessage());
        }
    }

    @Operation(summary = "获取分类树", description = "获取分类树结构")
    @GetMapping("/tree")
    public Result<List<CategoryTree>> getCategoryTree(
            @Parameter(description = "父分类ID，不传则获取完整树") @RequestParam(required = false) Long parentId) {
        CategoryQueryResponse<List<CategoryTree>> response = categoryFacadeService.getCategoryTree(parentId);
        if (response.getSuccess()) {
            return Result.success(response.getData());
        } else {
            return Result.error(response.getErrorCode(), response.getErrorMessage());
        }
    }

    @Operation(summary = "获取热门分类", description = "获取热门分类列表")
    @GetMapping("/hot")
    public Result<List<CategoryInfo>> getHotCategories(
            @Parameter(description = "返回数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        CategoryQueryResponse<List<CategoryInfo>> response = categoryFacadeService.getHotCategories(limit);
        if (response.getSuccess()) {
            return Result.success(response.getData());
        } else {
            return Result.error(response.getErrorCode(), response.getErrorMessage());
        }
    }

    @Operation(summary = "搜索分类", description = "根据关键词搜索分类")
    @GetMapping("/search")
    public Result<List<CategoryInfo>> searchCategories(
            @Parameter(description = "搜索关键词") @RequestParam String keyword) {
        CategoryQueryResponse<List<CategoryInfo>> response = categoryFacadeService.searchCategories(keyword);
        if (response.getSuccess()) {
            return Result.success(response.getData());
        } else {
            return Result.error(response.getErrorCode(), response.getErrorMessage());
        }
    }
} 