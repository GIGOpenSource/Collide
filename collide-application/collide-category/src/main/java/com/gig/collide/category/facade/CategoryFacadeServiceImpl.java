package com.gig.collide.category.facade;

import com.gig.collide.api.category.CategoryFacadeService;
import com.gig.collide.api.category.request.CategoryCreateRequest;
import com.gig.collide.api.category.request.CategoryQueryRequest;
import com.gig.collide.api.category.request.CategoryUpdateRequest;
import com.gig.collide.api.category.response.CategoryOperatorResponse;
import com.gig.collide.api.category.response.CategoryQueryResponse;
import com.gig.collide.api.category.response.data.CategoryInfo;
import com.gig.collide.api.category.response.data.CategoryTree;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.category.domain.service.CategoryDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 分类服务 Dubbo RPC 接口实现
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
@Slf4j
@Component
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class CategoryFacadeServiceImpl implements CategoryFacadeService {

    private final CategoryDomainService categoryDomainService;

    @Override
    public CategoryOperatorResponse createCategory(CategoryCreateRequest request) {
        try {
            Long categoryId = categoryDomainService.createCategory(request);
            return CategoryOperatorResponse.success(categoryId);
        } catch (Exception e) {
            log.error("创建分类失败", e);
            return CategoryOperatorResponse.error("CATEGORY_CREATE_FAILED", "创建分类失败");
        }
    }

    @Override
    public CategoryOperatorResponse updateCategory(CategoryUpdateRequest request) {
        try {
            categoryDomainService.updateCategory(request);
            return CategoryOperatorResponse.success();
        } catch (Exception e) {
            log.error("更新分类失败", e);
            return CategoryOperatorResponse.error("CATEGORY_UPDATE_FAILED", "更新分类失败");
        }
    }

    @Override
    public CategoryOperatorResponse deleteCategory(Long categoryId) {
        try {
            categoryDomainService.deleteCategory(categoryId);
            return CategoryOperatorResponse.success();
        } catch (Exception e) {
            log.error("删除分类失败", e);
            return CategoryOperatorResponse.error("CATEGORY_DELETE_FAILED", "删除分类失败");
        }
    }

    @Override
    public CategoryQueryResponse<CategoryInfo> getCategoryById(Long categoryId) {
        try {
            CategoryInfo categoryInfo = categoryDomainService.getCategoryById(categoryId);
            return CategoryQueryResponse.success(categoryInfo);
        } catch (Exception e) {
            log.error("查询分类详情失败", e);
            return CategoryQueryResponse.error("CATEGORY_QUERY_FAILED", "查询分类详情失败");
        }
    }

    @Override
    public CategoryQueryResponse<PageResponse<CategoryInfo>> queryCategories(CategoryQueryRequest request) {
        try {
            PageResponse<CategoryInfo> result = categoryDomainService.queryCategories(request);
            return CategoryQueryResponse.success(result);
        } catch (Exception e) {
            log.error("分页查询分类失败", e);
            return CategoryQueryResponse.error("CATEGORY_PAGE_QUERY_FAILED", "分页查询分类失败");
        }
    }

    @Override
    public CategoryQueryResponse<List<CategoryTree>> getCategoryTree(Long parentId) {
        try {
            List<CategoryTree> categoryTree = categoryDomainService.getCategoryTree(parentId);
            return CategoryQueryResponse.success(categoryTree);
        } catch (Exception e) {
            log.error("获取分类树失败", e);
            return CategoryQueryResponse.error("CATEGORY_TREE_QUERY_FAILED", "获取分类树失败");
        }
    }

    @Override
    public CategoryQueryResponse<List<CategoryInfo>> getHotCategories(Integer limit) {
        try {
            List<CategoryInfo> hotCategories = categoryDomainService.getHotCategories(limit);
            return CategoryQueryResponse.success(hotCategories);
        } catch (Exception e) {
            log.error("获取热门分类失败", e);
            return CategoryQueryResponse.error("HOT_CATEGORY_QUERY_FAILED", "获取热门分类失败");
        }
    }

    @Override
    public CategoryQueryResponse<List<CategoryInfo>> searchCategories(String keyword) {
        try {
            List<CategoryInfo> categories = categoryDomainService.searchCategories(keyword);
            return CategoryQueryResponse.success(categories);
        } catch (Exception e) {
            log.error("搜索分类失败", e);
            return CategoryQueryResponse.error("CATEGORY_SEARCH_FAILED", "搜索分类失败");
        }
    }
} 