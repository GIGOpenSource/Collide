package com.gig.collide.category.domain.service.impl;

import com.alicp.jetcache.anno.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.category.domain.entity.Category;
import com.gig.collide.category.domain.service.CategoryService;
import com.gig.collide.category.infrastructure.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 分类业务服务实现 - C端简化版
 * 专注于客户端使用的简单查询功能，移除复杂的管理功能
 * 
 * @author Collide
 * @version 2.0.0 (C端简化版)
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    // =================== 基础查询 ===================

    @Override
    @Cached(name = "category:detail:", key = "#categoryId", expire = 30, timeUnit = TimeUnit.MINUTES)
    @CacheRefresh(refresh = 5, stopRefreshAfterLastAccess = 30, timeUnit = TimeUnit.MINUTES)
    public Category getCategoryById(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getId, categoryId);
        wrapper.eq(Category::getStatus, "active"); // 只返回激活状态的分类
        
        return categoryMapper.selectOne(wrapper);
    }

    @Override
    @Cached(name = "category:query:", key = "#parentId + ':' + #status + ':' + #currentPage + ':' + #pageSize + ':' + #orderBy + ':' + #orderDirection", 
            expire = 15, timeUnit = TimeUnit.MINUTES)
    @CacheRefresh(refresh = 3, stopRefreshAfterLastAccess = 20, timeUnit = TimeUnit.MINUTES)
    public IPage<Category> queryCategories(Long parentId, String status, Integer currentPage, Integer pageSize,
                                         String orderBy, String orderDirection) {
        Page<Category> page = new Page<>(currentPage, pageSize);
        
        // 默认只查询激活状态的分类
        if (status == null) {
            status = "active";
        }
        
        return categoryMapper.selectCategoriesPage(page, parentId, status, orderBy, orderDirection);
    }

    @Override
    @Cached(name = "category:search:", key = "#keyword + ':' + #parentId + ':' + #currentPage + ':' + #pageSize + ':' + #orderBy + ':' + #orderDirection", 
            expire = 10, timeUnit = TimeUnit.MINUTES)
    @CacheRefresh(refresh = 2, stopRefreshAfterLastAccess = 15, timeUnit = TimeUnit.MINUTES)
    public IPage<Category> searchCategories(String keyword, Long parentId, Integer currentPage, Integer pageSize,
                                          String orderBy, String orderDirection) {
        Page<Category> page = new Page<>(currentPage, pageSize);
        String status = "active"; // 搜索只返回激活状态的分类
        
        return categoryMapper.searchCategories(page, keyword, parentId, status, orderBy, orderDirection);
    }

    // =================== 层级查询 ===================

    @Override
    @Cached(name = "category:root:", key = "#currentPage + ':' + #pageSize + ':' + #orderBy + ':' + #orderDirection", 
            expire = 20, timeUnit = TimeUnit.MINUTES)
    @CacheRefresh(refresh = 5, stopRefreshAfterLastAccess = 25, timeUnit = TimeUnit.MINUTES)
    public IPage<Category> getRootCategories(Integer currentPage, Integer pageSize, String orderBy, String orderDirection) {
        Page<Category> page = new Page<>(currentPage, pageSize);
        String status = "active";
        
        return categoryMapper.selectRootCategories(page, status, orderBy, orderDirection);
    }

    @Override
    @Cached(name = "category:children:", key = "#parentId + ':' + #currentPage + ':' + #pageSize + ':' + #orderBy + ':' + #orderDirection", 
            expire = 15, timeUnit = TimeUnit.MINUTES)
    @CacheRefresh(refresh = 3, stopRefreshAfterLastAccess = 20, timeUnit = TimeUnit.MINUTES)
    public IPage<Category> getChildCategories(Long parentId, Integer currentPage, Integer pageSize,
                                            String orderBy, String orderDirection) {
        Page<Category> page = new Page<>(currentPage, pageSize);
        String status = "active";
        
        return categoryMapper.selectCategoriesPage(page, parentId, status, orderBy, orderDirection);
    }

    @Override
    @Cached(name = "category:tree:", key = "#rootId + ':' + #maxDepth", expire = 30, timeUnit = TimeUnit.MINUTES)
    @CacheRefresh(refresh = 10, stopRefreshAfterLastAccess = 40, timeUnit = TimeUnit.MINUTES)
    public List<Category> getCategoryTree(Long rootId, Integer maxDepth) {
        String status = "active"; // 只获取激活状态的分类
        
        // 获取所有相关分类
        List<Category> allCategories = categoryMapper.selectCategoryTree(rootId, maxDepth, status, "sort", "ASC");
        
        // 构建树形结构
        return buildCategoryTree(allCategories, maxDepth);
    }

    @Override
    @Cached(name = "category:path:", key = "#categoryId", expire = 20, timeUnit = TimeUnit.MINUTES)
    @CacheRefresh(refresh = 5, stopRefreshAfterLastAccess = 25, timeUnit = TimeUnit.MINUTES)
    public List<Category> getCategoryPath(Long categoryId) {
        if (categoryId == null) {
            return new ArrayList<>();
        }
        
        // 获取分类路径（简化实现，实际应该递归查询父分类）
        Category category = getCategoryById(categoryId);
        if (category == null) {
            return new ArrayList<>();
        }
        
        List<Category> path = new ArrayList<>();
        path.add(category);
        
        // 递归获取父分类路径
        Long parentId = category.getParentId();
        while (parentId != null && parentId != 0) {
            Category parent = getCategoryById(parentId);
            if (parent != null) {
                path.add(0, parent); // 在开头添加父分类
                parentId = parent.getParentId();
            } else {
                break;
            }
        }
        
        return path;
    }

    // =================== 统计功能 ===================

    @Override
    @Cached(name = "category:popular:", key = "#parentId + ':' + #currentPage + ':' + #pageSize", 
            expire = 10, timeUnit = TimeUnit.MINUTES)
    @CacheRefresh(refresh = 2, stopRefreshAfterLastAccess = 15, timeUnit = TimeUnit.MINUTES)
    public IPage<Category> getPopularCategories(Long parentId, Integer currentPage, Integer pageSize) {
        Page<Category> page = new Page<>(currentPage, pageSize);
        String status = "active";
        
        return categoryMapper.selectPopularCategories(page, parentId, status);
    }

    @Override
    @Cached(name = "category:suggestions:", key = "#keyword + ':' + #limit", expire = 5, timeUnit = TimeUnit.MINUTES)
    @CacheRefresh(refresh = 1, stopRefreshAfterLastAccess = 10, timeUnit = TimeUnit.MINUTES)
    public List<Category> getCategorySuggestions(String keyword, Integer limit) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String status = "active";
        return categoryMapper.selectCategorySuggestions(keyword.trim(), limit, status);
    }

    @Override
    @Cached(name = "category:count:", key = "#parentId + ':' + #status", expire = 15, timeUnit = TimeUnit.MINUTES)
    @CacheRefresh(refresh = 3, stopRefreshAfterLastAccess = 20, timeUnit = TimeUnit.MINUTES)
    public long countCategories(Long parentId, String status) {
        // 默认只统计激活状态的分类
        if (status == null) {
            status = "active";
        }
        
        return categoryMapper.countCategories(parentId, status);
    }

    // =================== 缓存更新方法 ===================

    /**
     * 清除分类详情缓存
     */
    @CacheInvalidate(name = "category:detail:", key = "#categoryId")
    public void invalidateCategoryDetail(Long categoryId) {
        log.info("清除分类详情缓存，categoryId: {}", categoryId);
    }

    /**
     * 清除分类查询缓存
     */
    @CacheInvalidate(name = "category:query:", key = "#parentId + ':' + #status + ':*'")
    public void invalidateCategoryQuery(Long parentId, String status) {
        log.info("清除分类查询缓存，parentId: {}, status: {}", parentId, status);
    }

    /**
     * 清除分类搜索缓存
     */
    @CacheInvalidate(name = "category:search:", key = "#keyword + ':*'")
    public void invalidateCategorySearch(String keyword) {
        log.info("清除分类搜索缓存，keyword: {}", keyword);
    }

    /**
     * 清除分类树缓存
     */
    @CacheInvalidate(name = "category:tree:", key = "#rootId + ':*'")
    public void invalidateCategoryTree(Long rootId) {
        log.info("清除分类树缓存，rootId: {}", rootId);
    }

    /**
     * 清除分类路径缓存
     */
    @CacheInvalidate(name = "category:path:", key = "#categoryId")
    public void invalidateCategoryPath(Long categoryId) {
        log.info("清除分类路径缓存，categoryId: {}", categoryId);
    }

    /**
     * 清除热门分类缓存
     */
    @CacheInvalidate(name = "category:popular:", key = "#parentId + ':*'")
    public void invalidatePopularCategories(Long parentId) {
        log.info("清除热门分类缓存，parentId: {}", parentId);
    }

    /**
     * 清除分类建议缓存
     */
    @CacheInvalidate(name = "category:suggestions:", key = "#keyword + ':*'")
    public void invalidateCategorySuggestions(String keyword) {
        log.info("清除分类建议缓存，keyword: {}", keyword);
    }

    /**
     * 清除分类数量缓存
     */
    @CacheInvalidate(name = "category:count:", key = "#parentId + ':' + #status")
    public void invalidateCategoryCount(Long parentId, String status) {
        log.info("清除分类数量缓存，parentId: {}, status: {}", parentId, status);
    }

    /**
     * 清除所有分类相关缓存
     */
    @CacheInvalidate(name = "category:*", key = "*")
    public void invalidateAllCategoryCache() {
        log.info("清除所有分类缓存");
    }

    // =================== 私有方法 ===================

    /**
     * 构建分类树形结构
     */
    private List<Category> buildCategoryTree(List<Category> categories, Integer maxDepth) {
        if (categories == null || categories.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 按parent_id分组
        Map<Long, List<Category>> categoryMap = categories.stream()
                .collect(Collectors.groupingBy(Category::getParentId));
        
        // 获取根分类（parent_id = 0）
        List<Category> rootCategories = categoryMap.getOrDefault(0L, new ArrayList<>());
        
        // 递归构建树形结构
        for (Category root : rootCategories) {
            buildCategoryChildren(root, categoryMap, maxDepth, 1);
        }
        
        return rootCategories;
    }

    /**
     * 递归构建分类子节点
     */
    private void buildCategoryChildren(Category parent, Map<Long, List<Category>> categoryMap, 
                                     Integer maxDepth, int currentDepth) {
        if (maxDepth != null && currentDepth >= maxDepth) {
            return;
        }
        
        List<Category> children = categoryMap.get(parent.getId());
        if (children != null && !children.isEmpty()) {
            parent.initChildren();
            for (Category child : children) {
                child.setLevel(currentDepth);
                child.setParentName(parent.getName());
                parent.addChild(child);
                buildCategoryChildren(child, categoryMap, maxDepth, currentDepth + 1);
            }
        }
    }
}