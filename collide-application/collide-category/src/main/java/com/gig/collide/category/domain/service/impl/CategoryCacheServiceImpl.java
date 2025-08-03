package com.gig.collide.category.domain.service.impl;

import com.gig.collide.category.domain.service.CategoryCacheService;
import com.gig.collide.category.domain.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * 分类缓存管理服务实现
 * 
 * @author Collide
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryCacheServiceImpl implements CategoryCacheService {

    private final CategoryServiceImpl categoryService;
    private final Executor asyncExecutor;

    @Override
    public void invalidateCategoryDetail(Long categoryId) {
        try {
            categoryService.invalidateCategoryDetail(categoryId);
            log.info("成功清除分类详情缓存，categoryId: {}", categoryId);
        } catch (Exception e) {
            log.error("清除分类详情缓存失败，categoryId: {}", categoryId, e);
        }
    }

    @Override
    public void invalidateCategoryQuery(Long parentId, String status) {
        try {
            categoryService.invalidateCategoryQuery(parentId, status);
            log.info("成功清除分类查询缓存，parentId: {}, status: {}", parentId, status);
        } catch (Exception e) {
            log.error("清除分类查询缓存失败，parentId: {}, status: {}", parentId, status, e);
        }
    }

    @Override
    public void invalidateCategorySearch(String keyword) {
        try {
            categoryService.invalidateCategorySearch(keyword);
            log.info("成功清除分类搜索缓存，keyword: {}", keyword);
        } catch (Exception e) {
            log.error("清除分类搜索缓存失败，keyword: {}", keyword, e);
        }
    }

    @Override
    public void invalidateCategoryTree(Long rootId) {
        try {
            categoryService.invalidateCategoryTree(rootId);
            log.info("成功清除分类树缓存，rootId: {}", rootId);
        } catch (Exception e) {
            log.error("清除分类树缓存失败，rootId: {}", rootId, e);
        }
    }

    @Override
    public void invalidateCategoryPath(Long categoryId) {
        try {
            categoryService.invalidateCategoryPath(categoryId);
            log.info("成功清除分类路径缓存，categoryId: {}", categoryId);
        } catch (Exception e) {
            log.error("清除分类路径缓存失败，categoryId: {}", categoryId, e);
        }
    }

    @Override
    public void invalidatePopularCategories(Long parentId) {
        try {
            categoryService.invalidatePopularCategories(parentId);
            log.info("成功清除热门分类缓存，parentId: {}", parentId);
        } catch (Exception e) {
            log.error("清除热门分类缓存失败，parentId: {}", parentId, e);
        }
    }

    @Override
    public void invalidateCategorySuggestions(String keyword) {
        try {
            categoryService.invalidateCategorySuggestions(keyword);
            log.info("成功清除分类建议缓存，keyword: {}", keyword);
        } catch (Exception e) {
            log.error("清除分类建议缓存失败，keyword: {}", keyword, e);
        }
    }

    @Override
    public void invalidateCategoryCount(Long parentId, String status) {
        try {
            categoryService.invalidateCategoryCount(parentId, status);
            log.info("成功清除分类数量缓存，parentId: {}, status: {}", parentId, status);
        } catch (Exception e) {
            log.error("清除分类数量缓存失败，parentId: {}, status: {}", parentId, status, e);
        }
    }

    @Override
    public void invalidateAllCategoryCache() {
        try {
            categoryService.invalidateAllCategoryCache();
            log.info("成功清除所有分类缓存");
        } catch (Exception e) {
            log.error("清除所有分类缓存失败", e);
        }
    }

    @Override
    public void warmUpCategoryCache(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            log.warn("预热分类缓存失败：分类ID列表为空");
            return;
        }

        log.info("开始预热分类缓存，分类数量: {}", categoryIds.size());
        
        // 异步预热缓存
        CompletableFuture.runAsync(() -> {
            try {
                for (Long categoryId : categoryIds) {
                    try {
                        categoryService.getCategoryById(categoryId);
                        log.debug("预热分类详情缓存成功，categoryId: {}", categoryId);
                    } catch (Exception e) {
                        log.warn("预热分类详情缓存失败，categoryId: {}", categoryId, e);
                    }
                }
                log.info("分类缓存预热完成，成功预热 {} 个分类", categoryIds.size());
            } catch (Exception e) {
                log.error("分类缓存预热过程中发生异常", e);
            }
        }, asyncExecutor);
    }

    @Override
    public void warmUpCategoryTreeCache(List<Long> rootIds) {
        if (rootIds == null || rootIds.isEmpty()) {
            log.warn("预热分类树缓存失败：根分类ID列表为空");
            return;
        }

        log.info("开始预热分类树缓存，根分类数量: {}", rootIds.size());
        
        // 异步预热缓存
        CompletableFuture.runAsync(() -> {
            try {
                for (Long rootId : rootIds) {
                    try {
                        categoryService.getCategoryTree(rootId, 3); // 默认3层深度
                        log.debug("预热分类树缓存成功，rootId: {}", rootId);
                    } catch (Exception e) {
                        log.warn("预热分类树缓存失败，rootId: {}", rootId, e);
                    }
                }
                log.info("分类树缓存预热完成，成功预热 {} 个根分类", rootIds.size());
            } catch (Exception e) {
                log.error("分类树缓存预热过程中发生异常", e);
            }
        }, asyncExecutor);
    }
} 