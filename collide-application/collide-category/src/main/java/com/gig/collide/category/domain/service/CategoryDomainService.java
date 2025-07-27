package com.gig.collide.category.domain.service;

import com.gig.collide.api.category.request.CategoryCreateRequest;
import com.gig.collide.api.category.request.CategoryQueryRequest;
import com.gig.collide.api.category.request.CategoryUpdateRequest;
import com.gig.collide.api.category.response.data.CategoryInfo;
import com.gig.collide.api.category.response.data.CategoryTree;
import com.gig.collide.base.response.PageResponse;

import java.util.List;

/**
 * 分类领域服务接口
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
public interface CategoryDomainService {

    /**
     * 创建分类
     */
    Long createCategory(CategoryCreateRequest request);

    /**
     * 更新分类
     */
    void updateCategory(CategoryUpdateRequest request);

    /**
     * 删除分类
     */
    void deleteCategory(Long categoryId);

    /**
     * 根据ID查询分类
     */
    CategoryInfo getCategoryById(Long categoryId);

    /**
     * 分页查询分类
     */
    PageResponse<CategoryInfo> queryCategories(CategoryQueryRequest request);

    /**
     * 获取分类树
     */
    List<CategoryTree> getCategoryTree(Long parentId);

    /**
     * 获取热门分类
     */
    List<CategoryInfo> getHotCategories(Integer limit);

    /**
     * 搜索分类
     */
    List<CategoryInfo> searchCategories(String keyword);
} 