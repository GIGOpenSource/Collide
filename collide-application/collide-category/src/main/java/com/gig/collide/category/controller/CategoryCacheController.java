package com.gig.collide.category.controller;

import com.gig.collide.category.domain.service.CategoryCacheService;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类缓存管理控制器
 * 注意：此控制器仅供管理员使用，普通用户无权限访问
 * 
 * @author Collide
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/categories/cache")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")  // 只有管理员角色可以访问
public class CategoryCacheController {

    private final CategoryCacheService categoryCacheService;

    /**
     * 清除分类详情缓存
     * 权限：ADMIN
     */
    @DeleteMapping("/detail/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> invalidateCategoryDetail(@PathVariable Long categoryId) {
        try {
            categoryCacheService.invalidateCategoryDetail(categoryId);
            log.info("管理员清除分类详情缓存，categoryId: {}", categoryId);
            return Result.success("清除分类详情缓存成功");
        } catch (Exception e) {
            log.error("清除分类详情缓存失败，categoryId: {}", categoryId, e);
            return Result.error("清除分类详情缓存失败");
        }
    }

    /**
     * 清除分类查询缓存
     * 权限：ADMIN
     */
    @DeleteMapping("/query")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> invalidateCategoryQuery(@RequestParam Long parentId, @RequestParam String status) {
        try {
            categoryCacheService.invalidateCategoryQuery(parentId, status);
            log.info("管理员清除分类查询缓存，parentId: {}, status: {}", parentId, status);
            return Result.success("清除分类查询缓存成功");
        } catch (Exception e) {
            log.error("清除分类查询缓存失败，parentId: {}, status: {}", parentId, status, e);
            return Result.error("清除分类查询缓存失败");
        }
    }

    /**
     * 清除分类搜索缓存
     * 权限：ADMIN
     */
    @DeleteMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> invalidateCategorySearch(@RequestParam String keyword) {
        try {
            categoryCacheService.invalidateCategorySearch(keyword);
            log.info("管理员清除分类搜索缓存，keyword: {}", keyword);
            return Result.success("清除分类搜索缓存成功");
        } catch (Exception e) {
            log.error("清除分类搜索缓存失败，keyword: {}", keyword, e);
            return Result.error("清除分类搜索缓存失败");
        }
    }

    /**
     * 清除分类树缓存
     * 权限：ADMIN
     */
    @DeleteMapping("/tree/{rootId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> invalidateCategoryTree(@PathVariable Long rootId) {
        try {
            categoryCacheService.invalidateCategoryTree(rootId);
            log.info("管理员清除分类树缓存，rootId: {}", rootId);
            return Result.success("清除分类树缓存成功");
        } catch (Exception e) {
            log.error("清除分类树缓存失败，rootId: {}", rootId, e);
            return Result.error("清除分类树缓存失败");
        }
    }

    /**
     * 清除分类路径缓存
     * 权限：ADMIN
     */
    @DeleteMapping("/path/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> invalidateCategoryPath(@PathVariable Long categoryId) {
        try {
            categoryCacheService.invalidateCategoryPath(categoryId);
            log.info("管理员清除分类路径缓存，categoryId: {}", categoryId);
            return Result.success("清除分类路径缓存成功");
        } catch (Exception e) {
            log.error("清除分类路径缓存失败，categoryId: {}", categoryId, e);
            return Result.error("清除分类路径缓存失败");
        }
    }

    /**
     * 清除热门分类缓存
     * 权限：ADMIN
     */
    @DeleteMapping("/popular")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> invalidatePopularCategories(@RequestParam Long parentId) {
        try {
            categoryCacheService.invalidatePopularCategories(parentId);
            log.info("管理员清除热门分类缓存，parentId: {}", parentId);
            return Result.success("清除热门分类缓存成功");
        } catch (Exception e) {
            log.error("清除热门分类缓存失败，parentId: {}", parentId, e);
            return Result.error("清除热门分类缓存失败");
        }
    }

    /**
     * 清除分类建议缓存
     * 权限：ADMIN
     */
    @DeleteMapping("/suggestions")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> invalidateCategorySuggestions(@RequestParam String keyword) {
        try {
            categoryCacheService.invalidateCategorySuggestions(keyword);
            log.info("管理员清除分类建议缓存，keyword: {}", keyword);
            return Result.success("清除分类建议缓存成功");
        } catch (Exception e) {
            log.error("清除分类建议缓存失败，keyword: {}", keyword, e);
            return Result.error("清除分类建议缓存失败");
        }
    }

    /**
     * 清除分类数量缓存
     * 权限：ADMIN
     */
    @DeleteMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> invalidateCategoryCount(@RequestParam Long parentId, @RequestParam String status) {
        try {
            categoryCacheService.invalidateCategoryCount(parentId, status);
            log.info("管理员清除分类数量缓存，parentId: {}, status: {}", parentId, status);
            return Result.success("清除分类数量缓存成功");
        } catch (Exception e) {
            log.error("清除分类数量缓存失败，parentId: {}, status: {}", parentId, status, e);
            return Result.error("清除分类数量缓存失败");
        }
    }

    /**
     * 清除所有分类缓存
     * 权限：ADMIN
     */
    @DeleteMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> invalidateAllCategoryCache() {
        try {
            categoryCacheService.invalidateAllCategoryCache();
            log.info("管理员清除所有分类缓存");
            return Result.success("清除所有分类缓存成功");
        } catch (Exception e) {
            log.error("清除所有分类缓存失败", e);
            return Result.error("清除所有分类缓存失败");
        }
    }

    /**
     * 预热分类缓存
     * 权限：ADMIN
     */
    @PostMapping("/warmup/detail")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> warmUpCategoryCache(@RequestBody List<Long> categoryIds) {
        try {
            categoryCacheService.warmUpCategoryCache(categoryIds);
            log.info("管理员预热分类缓存，分类数量: {}", categoryIds.size());
            return Result.success("开始预热分类缓存，分类数量: " + categoryIds.size());
        } catch (Exception e) {
            log.error("预热分类缓存失败", e);
            return Result.error("预热分类缓存失败");
        }
    }

    /**
     * 预热分类树缓存
     * 权限：ADMIN
     */
    @PostMapping("/warmup/tree")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> warmUpCategoryTreeCache(@RequestBody List<Long> rootIds) {
        try {
            categoryCacheService.warmUpCategoryTreeCache(rootIds);
            log.info("管理员预热分类树缓存，根分类数量: {}", rootIds.size());
            return Result.success("开始预热分类树缓存，根分类数量: " + rootIds.size());
        } catch (Exception e) {
            log.error("预热分类树缓存失败", e);
            return Result.error("预热分类树缓存失败");
        }
    }
} 