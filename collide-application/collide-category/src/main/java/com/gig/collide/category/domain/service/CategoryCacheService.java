package com.gig.collide.category.domain.service;

/**
 * 分类缓存管理服务接口
 * 
 * @author Collide
 * @version 1.0.0
 * @since 2024-01-01
 */
public interface CategoryCacheService {
    
    /**
     * 清除分类详情缓存
     * 
     * @param categoryId 分类ID
     */
    void invalidateCategoryDetail(Long categoryId);
    
    /**
     * 清除分类查询缓存
     * 
     * @param parentId 父分类ID
     * @param status 状态
     */
    void invalidateCategoryQuery(Long parentId, String status);
    
    /**
     * 清除分类搜索缓存
     * 
     * @param keyword 搜索关键词
     */
    void invalidateCategorySearch(String keyword);
    
    /**
     * 清除分类树缓存
     * 
     * @param rootId 根分类ID
     */
    void invalidateCategoryTree(Long rootId);
    
    /**
     * 清除分类路径缓存
     * 
     * @param categoryId 分类ID
     */
    void invalidateCategoryPath(Long categoryId);
    
    /**
     * 清除热门分类缓存
     * 
     * @param parentId 父分类ID
     */
    void invalidatePopularCategories(Long parentId);
    
    /**
     * 清除分类建议缓存
     * 
     * @param keyword 搜索关键词
     */
    void invalidateCategorySuggestions(String keyword);
    
    /**
     * 清除分类数量缓存
     * 
     * @param parentId 父分类ID
     * @param status 状态
     */
    void invalidateCategoryCount(Long parentId, String status);
    
    /**
     * 清除所有分类相关缓存
     */
    void invalidateAllCategoryCache();
    
    /**
     * 预热分类缓存
     * 
     * @param categoryIds 分类ID列表
     */
    void warmUpCategoryCache(java.util.List<Long> categoryIds);
    
    /**
     * 预热分类树缓存
     * 
     * @param rootIds 根分类ID列表
     */
    void warmUpCategoryTreeCache(java.util.List<Long> rootIds);
} 