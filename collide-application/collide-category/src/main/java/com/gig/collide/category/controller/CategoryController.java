package com.gig.collide.category.controller;

import com.gig.collide.api.category.CategoryFacadeService;
import com.gig.collide.api.category.request.CategoryCreateRequest;
import com.gig.collide.api.category.request.CategoryUpdateRequest;
import com.gig.collide.api.category.request.CategoryQueryRequest;
import com.gig.collide.api.category.response.CategoryResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 分类REST控制器 - 简洁版
 * 提供分类管理的HTTP接口
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryFacadeService categoryFacadeService;

    // =================== 分类管理 ===================

    /**
     * 创建分类
     */
    @PostMapping
    public Result<CategoryResponse> createCategory(@Valid @RequestBody CategoryCreateRequest request) {
        log.info("REST - 创建分类：{}", request);
        return categoryFacadeService.createCategory(request);
    }

    /**
     * 更新分类
     */
    @PutMapping("/{categoryId}")
    public Result<CategoryResponse> updateCategory(@PathVariable Long categoryId,
                                                 @Valid @RequestBody CategoryUpdateRequest request) {
        log.info("REST - 更新分类，ID：{}，请求：{}", categoryId, request);
        request.setId(categoryId);
        return categoryFacadeService.updateCategory(request);
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{categoryId}")
    public Result<Void> deleteCategory(@PathVariable Long categoryId,
                                     @RequestParam Long operatorId) {
        log.info("REST - 删除分类，ID：{}，操作人：{}", categoryId, operatorId);
        return categoryFacadeService.deleteCategory(categoryId, operatorId);
    }

    /**
     * 分页查询分类（POST方式，支持复杂查询条件）
     */
    @PostMapping("/query")
    public PageResponse<CategoryResponse> queryCategories(@RequestBody CategoryQueryRequest request) {
        log.info("REST - 查询分类：{}", request);
        Result<PageResponse<CategoryResponse>> result = categoryFacadeService.queryCategories(request);
        return result.getData();
    }

    /**
     * 分页查询分类（GET方式，支持基本查询参数）
     */
    @GetMapping("/query")
    public PageResponse<CategoryResponse> queryCategoriesGet(
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "active") String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "sort") String orderBy,
            @RequestParam(defaultValue = "ASC") String orderDirection) {
        
        log.info("REST - GET查询分类，父分类：{}，名称：{}，状态：{}", parentId, name, status);
        
        CategoryQueryRequest request = new CategoryQueryRequest();
        request.setParentId(parentId);
        request.setName(name);
        request.setStatus(status);
        request.setPageNum(pageNum);
        request.setPageSize(pageSize);
        request.setOrderBy(orderBy);
        request.setOrderDirection(orderDirection);
        
        Result<PageResponse<CategoryResponse>> result = categoryFacadeService.queryCategories(request);
        return result.getData();
    }

    /**
     * 获取分类详情
     */
    @GetMapping("/{categoryId}")
    public Result<CategoryResponse> getCategoryById(@PathVariable Long categoryId,
                                                  @RequestParam(defaultValue = "false") Boolean includeInactive) {
        log.info("REST - 获取分类详情，ID：{}", categoryId);
        return categoryFacadeService.getCategoryById(categoryId, includeInactive);
    }

    /**
     * 获取分类列表（默认接口，支持分页）
     */
    @GetMapping
    public PageResponse<CategoryResponse> getCategories(
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "active") String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "sort") String orderBy,
            @RequestParam(defaultValue = "ASC") String orderDirection) {
        
        log.info("REST - 获取分类列表，父分类：{}，名称：{}，状态：{}，页码：{}，大小：{}", 
                parentId, name, status, pageNum, pageSize);
        
        CategoryQueryRequest request = new CategoryQueryRequest();
        request.setParentId(parentId);
        request.setName(name);
        request.setStatus(status);
        request.setPageNum(pageNum);
        request.setPageSize(pageSize);
        request.setOrderBy(orderBy);
        request.setOrderDirection(orderDirection);
        
        Result<PageResponse<CategoryResponse>> result = categoryFacadeService.queryCategories(request);
        return result.getData();
    }

    // =================== 层级分类 ===================

    /**
     * 获取根分类列表
     */
    @GetMapping("/root")
    public PageResponse<CategoryResponse> getRootCategories(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "sort") String orderBy,
            @RequestParam(defaultValue = "ASC") String orderDirection) {
        
        log.info("REST - 获取根分类列表");
        Result<PageResponse<CategoryResponse>> result = categoryFacadeService.getRootCategories(pageNum, pageSize, orderBy, orderDirection);
        return result.getData();
    }

    /**
     * 获取子分类列表
     */
    @GetMapping("/{parentId}/children")
    public PageResponse<CategoryResponse> getChildCategories(
            @PathVariable Long parentId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "sort") String orderBy,
            @RequestParam(defaultValue = "ASC") String orderDirection) {
        
        log.info("REST - 获取子分类列表，父分类ID：{}", parentId);
        Result<PageResponse<CategoryResponse>> result = categoryFacadeService.getChildCategories(parentId, pageNum, pageSize, orderBy, orderDirection);
        return result.getData();
    }

    /**
     * 获取分类树
     */
    @GetMapping("/tree")
    public Result<List<CategoryResponse>> getCategoryTree(
            @RequestParam(required = false) Long rootId,
            @RequestParam(defaultValue = "5") Integer maxDepth,
            @RequestParam(defaultValue = "false") Boolean includeInactive) {
        
        log.info("REST - 获取分类树，根ID：{}", rootId);
        return categoryFacadeService.getCategoryTree(rootId, maxDepth, includeInactive);
    }

    /**
     * 获取分类路径
     */
    @GetMapping("/{categoryId}/path")
    public Result<List<CategoryResponse>> getCategoryPath(@PathVariable Long categoryId) {
        log.info("REST - 获取分类路径，ID：{}", categoryId);
        return categoryFacadeService.getCategoryPath(categoryId);
    }

    /**
     * 获取分类祖先
     */
    @GetMapping("/{categoryId}/ancestors")
    public Result<List<CategoryResponse>> getCategoryAncestors(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "false") Boolean includeInactive) {
        
        log.info("REST - 获取分类祖先，ID：{}", categoryId);
        return categoryFacadeService.getCategoryAncestors(categoryId, includeInactive);
    }

    /**
     * 获取分类后代
     */
    @GetMapping("/{categoryId}/descendants")
    public Result<List<CategoryResponse>> getCategoryDescendants(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "5") Integer maxDepth,
            @RequestParam(defaultValue = "false") Boolean includeInactive) {
        
        log.info("REST - 获取分类后代，ID：{}", categoryId);
        return categoryFacadeService.getCategoryDescendants(categoryId, maxDepth, includeInactive);
    }

    // =================== 状态管理 ===================

    /**
     * 更新分类状态
     */
    @PutMapping("/{categoryId}/status")
    public Result<Void> updateCategoryStatus(@PathVariable Long categoryId,
                                           @RequestParam String status,
                                           @RequestParam Long operatorId) {
        
        log.info("REST - 更新分类状态，ID：{}，状态：{}", categoryId, status);
        return categoryFacadeService.updateCategoryStatus(categoryId, status, operatorId);
    }

    /**
     * 激活分类
     */
    @PutMapping("/{categoryId}/activate")
    public Result<Void> activateCategory(@PathVariable Long categoryId,
                                       @RequestParam Long operatorId) {
        
        log.info("REST - 激活分类，ID：{}", categoryId);
        return categoryFacadeService.activateCategory(categoryId, operatorId);
    }

    /**
     * 停用分类
     */
    @PutMapping("/{categoryId}/deactivate")
    public Result<Void> deactivateCategory(@PathVariable Long categoryId,
                                         @RequestParam Long operatorId) {
        
        log.info("REST - 停用分类，ID：{}", categoryId);
        return categoryFacadeService.deactivateCategory(categoryId, operatorId);
    }

    /**
     * 批量更新分类状态
     */
    @PutMapping("/batch/status")
    public Result<Integer> batchUpdateCategoryStatus(@RequestBody List<Long> categoryIds,
                                                   @RequestParam String status,
                                                   @RequestParam Long operatorId) {
        
        log.info("REST - 批量更新分类状态，数量：{}，状态：{}", categoryIds.size(), status);
        return categoryFacadeService.batchUpdateCategoryStatus(categoryIds, status, operatorId);
    }

    // =================== 统计功能 ===================

    /**
     * 更新分类内容数量
     */
    @PutMapping("/{categoryId}/content-count")
    public Result<Long> updateContentCount(@PathVariable Long categoryId,
                                         @RequestParam Long increment) {
        
        log.info("REST - 更新内容数量，分类ID：{}，增量：{}", categoryId, increment);
        return categoryFacadeService.updateContentCount(categoryId, increment);
    }

    /**
     * 获取分类统计信息
     */
    @GetMapping("/{categoryId}/statistics")
    public Result<Map<String, Object>> getCategoryStatistics(@PathVariable Long categoryId) {
        log.info("REST - 获取分类统计，ID：{}", categoryId);
        return categoryFacadeService.getCategoryStatistics(categoryId);
    }

    /**
     * 统计分类数量
     */
    @GetMapping("/count")
    public Result<Long> countCategories(@RequestParam(required = false) Long parentId,
                                      @RequestParam(defaultValue = "active") String status) {
        
        log.info("REST - 统计分类数量，父分类：{}，状态：{}", parentId, status);
        return categoryFacadeService.countCategories(parentId, status);
    }

    /**
     * 获取热门分类
     */
    @GetMapping("/popular")
    public PageResponse<CategoryResponse> getPopularCategories(
            @RequestParam(required = false) Long parentId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        
        log.info("REST - 获取热门分类，父分类：{}", parentId);
        Result<PageResponse<CategoryResponse>> result = categoryFacadeService.getPopularCategories(parentId, pageNum, pageSize);
        return result.getData();
    }

    // =================== 搜索功能 ===================

    /**
     * 搜索分类
     */
    @GetMapping("/search")
    public PageResponse<CategoryResponse> searchCategories(
            @RequestParam String keyword,
            @RequestParam(required = false) Long parentId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        
        log.info("REST - 搜索分类，关键词：{}", keyword);
        Result<PageResponse<CategoryResponse>> result = categoryFacadeService.searchCategories(keyword, parentId, pageNum, pageSize);
        return result.getData();
    }

    /**
     * 获取分类建议
     */
    @GetMapping("/suggestions")
    public Result<List<CategoryResponse>> getCategorySuggestions(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        log.info("REST - 获取分类建议，关键词：{}", keyword);
        return categoryFacadeService.getCategorySuggestions(keyword, limit);
    }

    // =================== 其他接口（暂时返回未实现） ===================

    /**
     * 调整分类排序
     */
    @PutMapping("/{categoryId}/sort")
    public Result<Void> adjustCategorySort(@PathVariable Long categoryId,
                                         @RequestParam Integer newSort,
                                         @RequestParam Long operatorId) {
        return Result.error("NOT_IMPLEMENTED", "功能开发中");
    }

    /**
     * 移动分类
     */
    @PutMapping("/{categoryId}/move")
    public Result<Void> moveCategory(@PathVariable Long categoryId,
                                   @RequestParam Long newParentId,
                                   @RequestParam Long operatorId) {
        return Result.error("NOT_IMPLEMENTED", "功能开发中");
    }

    /**
     * 获取叶子分类
     */
    @GetMapping("/leaf")
    public Result<PageResponse<CategoryResponse>> getLeafCategories(
            @RequestParam(required = false) Long parentId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.error("NOT_IMPLEMENTED", "功能开发中");
    }
}