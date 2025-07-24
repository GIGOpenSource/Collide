package com.gig.collide.api.category;

import com.gig.collide.api.category.request.CategoryCreateRequest;
import com.gig.collide.api.category.request.CategoryQueryRequest;
import com.gig.collide.api.category.request.CategoryUpdateRequest;
import com.gig.collide.api.category.response.CategoryOperatorResponse;
import com.gig.collide.api.category.response.CategoryQueryResponse;
import com.gig.collide.api.category.response.data.CategoryInfo;
import com.gig.collide.api.category.response.data.CategoryTree;
import com.gig.collide.base.response.PageResponse;

import java.util.List;

/**
 * 分类管理门面服务接口
 *
 * @author GIG Team
 * @version 1.0.0
 */
public interface CategoryFacadeService {

    /**
     * 创建分类
     */
    CategoryOperatorResponse createCategory(CategoryCreateRequest request);

    /**
     * 更新分类
     */
    CategoryOperatorResponse updateCategory(CategoryUpdateRequest request);

    /**
     * 删除分类
     */
    CategoryOperatorResponse deleteCategory(Long categoryId);

    /**
     * 查询分类详情
     */
    CategoryQueryResponse<CategoryInfo> getCategoryById(Long categoryId);

    /**
     * 分页查询分类列表
     */
    CategoryQueryResponse<PageResponse<CategoryInfo>> queryCategories(CategoryQueryRequest request);

    /**
     * 获取分类树
     */
    CategoryQueryResponse<List<CategoryTree>> getCategoryTree(Long parentId);

    /**
     * 获取热门分类
     */
    CategoryQueryResponse<List<CategoryInfo>> getHotCategories(Integer limit);

    /**
     * 搜索分类
     */
    CategoryQueryResponse<List<CategoryInfo>> searchCategories(String keyword);
} 