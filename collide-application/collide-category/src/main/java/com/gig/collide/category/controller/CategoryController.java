package com.gig.collide.category.controller;

import com.gig.collide.api.category.CategoryFacadeService;
import com.gig.collide.api.category.request.CategoryQueryRequest;
import com.gig.collide.api.category.response.CategoryResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类REST控制器 - 规范版
 * 参考Content模块设计，直接返回Facade层的Result包装
 * 专注于客户端使用的查询功能
 * 
 * @author Collide
 * @version 5.0.0 (与Content模块一致版)
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryFacadeService categoryFacadeService;

    // =================== 基础查询 ===================

    /**
     * 获取分类详情
     */
    @GetMapping("/{categoryId}")
    public Result<CategoryResponse> getCategoryById(@PathVariable Long categoryId) {
        log.info("REST - 获取分类详情，ID：{}", categoryId);
        return categoryFacadeService.getCategoryById(categoryId);
    }

    /**
     * 分页查询分类（POST方式，支持复杂查询条件）
     */
    @PostMapping("/query")
    public Result<PageResponse<CategoryResponse>> queryCategories(@RequestBody CategoryQueryRequest request) {
        log.info("REST - 查询分类：{}", request);
        return categoryFacadeService.queryCategories(request);
    }

    /**
     * 分页查询分类（GET方式，支持基本查询参数）
     */
    @GetMapping("/query")
    public Result<PageResponse<CategoryResponse>> queryCategoriesGet(
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "active") String status,
            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "sort") String orderBy,
            @RequestParam(defaultValue = "ASC") String orderDirection) {
        
        log.info("REST - GET查询分类，父分类：{}，名称：{}，状态：{}", parentId, name, status);
        
        CategoryQueryRequest request = new CategoryQueryRequest();
        request.setParentId(parentId);
        request.setName(name);
        request.setStatus(status);
        request.setCurrentPage(currentPage);
        request.setPageSize(pageSize);
        request.setOrderBy(orderBy);
        request.setOrderDirection(orderDirection);
        
        return categoryFacadeService.queryCategories(request);
    }

    /**
     * 获取分类列表（默认接口，支持分页）
     */
    @GetMapping
    public Result<PageResponse<CategoryResponse>> getCategories(
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "active") String status,
            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "sort") String orderBy,
            @RequestParam(defaultValue = "ASC") String orderDirection) {
        
        log.info("REST - 获取分类列表，父分类：{}，名称：{}，状态：{}，页码：{}，大小：{}", 
                parentId, name, status, currentPage, pageSize);
        
        CategoryQueryRequest request = new CategoryQueryRequest();
        request.setParentId(parentId);
        request.setName(name);
        request.setStatus(status);
        request.setCurrentPage(currentPage);
        request.setPageSize(pageSize);
        request.setOrderBy(orderBy);
        request.setOrderDirection(orderDirection);
        
        return categoryFacadeService.queryCategories(request);
    }

    /**
     * 搜索分类
     */
    @GetMapping("/search")
    public Result<PageResponse<CategoryResponse>> searchCategories(
            @RequestParam String keyword,
            @RequestParam(required = false) Long parentId,
            @RequestParam(defaultValue = "active") String status,
            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "sort") String orderBy,
            @RequestParam(defaultValue = "ASC") String orderDirection) {
        
        log.info("REST - 搜索分类，关键词：{}，父分类：{}，状态：{}", keyword, parentId, status);
        return categoryFacadeService.searchCategories(keyword, parentId, status, currentPage, pageSize, orderBy, orderDirection);
    }

    // =================== 层级查询 ===================

    /**
     * 获取根分类列表
     */
    @GetMapping("/root")
    public Result<PageResponse<CategoryResponse>> getRootCategories(
            @RequestParam(defaultValue = "active") String status,
            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "sort") String orderBy,
            @RequestParam(defaultValue = "ASC") String orderDirection) {
        
        log.info("REST - 获取根分类列表，状态：{}，页码：{}，大小：{}", status, currentPage, pageSize);
        return categoryFacadeService.getRootCategories(status, currentPage, pageSize, orderBy, orderDirection);
    }

    /**
     * 获取子分类列表
     */
    @GetMapping("/{parentId}/children")
    public Result<PageResponse<CategoryResponse>> getChildCategories(
            @PathVariable Long parentId,
            @RequestParam(defaultValue = "active") String status,
            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "sort") String orderBy,
            @RequestParam(defaultValue = "ASC") String orderDirection) {
        
        log.info("REST - 获取子分类列表，父分类ID：{}，状态：{}，页码：{}，大小：{}", parentId, status, currentPage, pageSize);
        return categoryFacadeService.getChildCategories(parentId, status, currentPage, pageSize, orderBy, orderDirection);
    }

    /**
     * 获取分类树
     */
    @GetMapping("/tree")
    public Result<List<CategoryResponse>> getCategoryTree(
            @RequestParam(required = false) Long rootId,
            @RequestParam(defaultValue = "5") Integer maxDepth,
            @RequestParam(defaultValue = "active") String status,
            @RequestParam(defaultValue = "sort") String orderBy,
            @RequestParam(defaultValue = "ASC") String orderDirection) {
        
        log.info("REST - 获取分类树，根ID：{}，最大深度：{}，状态：{}", rootId, maxDepth, status);
        return categoryFacadeService.getCategoryTree(rootId, maxDepth, status, orderBy, orderDirection);
    }

    /**
     * 获取分类路径
     */
    @GetMapping("/{categoryId}/path")
    public Result<List<CategoryResponse>> getCategoryPath(@PathVariable Long categoryId) {
        log.info("REST - 获取分类路径，ID：{}", categoryId);
        return categoryFacadeService.getCategoryPath(categoryId);
    }

    // =================== 统计功能 ===================

    /**
     * 获取热门分类
     */
    @GetMapping("/popular")
    public Result<PageResponse<CategoryResponse>> getPopularCategories(
            @RequestParam(required = false) Long parentId,
            @RequestParam(defaultValue = "active") String status,
            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        
        log.info("REST - 获取热门分类，父分类：{}，状态：{}，页码：{}，大小：{}", parentId, status, currentPage, pageSize);
        return categoryFacadeService.getPopularCategories(parentId, status, currentPage, pageSize);
    }

    /**
     * 获取分类建议
     */
    @GetMapping("/suggestions")
    public Result<List<CategoryResponse>> getCategorySuggestions(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "active") String status) {
        
        log.info("REST - 获取分类建议，关键词：{}，限制数量：{}，状态：{}", keyword, limit, status);
        return categoryFacadeService.getCategorySuggestions(keyword, limit, status);
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
}