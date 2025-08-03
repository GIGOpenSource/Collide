package com.gig.collide.category.facade;

import com.gig.collide.api.category.CategoryFacadeService;
import com.gig.collide.api.category.request.CategoryQueryRequest;
import com.gig.collide.api.category.response.CategoryResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.category.domain.entity.Category;
import com.gig.collide.category.domain.service.CategoryService;
import com.gig.collide.web.vo.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类门面服务实现 - C端简化版
 * 专注于客户端使用的简单查询功能，移除复杂的管理功能
 * 
 * @author Collide
 * @version 2.0.0 (C端简化版)
 * @since 2024-01-01
 */
@Slf4j
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class CategoryFacadeServiceImpl implements CategoryFacadeService {

    private final CategoryService categoryService;

    // =================== 基础查询 ===================

    @Override
    public Result<CategoryResponse> getCategoryById(Long categoryId) {
        try {
            if (categoryId == null) {
                return Result.error("INVALID_REQUEST", "分类ID不能为空");
            }
            
            Category category = categoryService.getCategoryById(categoryId);
            if (category == null) {
                return Result.error("CATEGORY_NOT_FOUND", "分类不存在");
            }
            
            CategoryResponse response = convertToResponse(category);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("获取分类失败：", e);
            return Result.error("CATEGORY_GET_ERROR", "获取分类失败：" + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<CategoryResponse>> queryCategories(CategoryQueryRequest request) {
        try {
            log.info("查询分类：{}", request);
            
            if (request == null) {
                request = new CategoryQueryRequest();
            }
            
            IPage<Category> page = categoryService.queryCategories(
                request.getActualParentId(),
                request.getActualStatus(),
                request.getCurrentPage(),
                request.getPageSize(),
                request.getOrderBy(),
                request.getOrderDirection()
            );
            
            PageResponse<CategoryResponse> response = convertToPageResponse(page);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("查询分类失败：", e);
            return Result.error("CATEGORY_QUERY_ERROR", "查询分类失败：" + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<CategoryResponse>> searchCategories(String keyword, Long parentId,
                                                                 Integer currentPage, Integer pageSize) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return Result.error("INVALID_REQUEST", "搜索关键词不能为空");
            }
            
            IPage<Category> page = categoryService.searchCategories(keyword, parentId, currentPage, pageSize, "sort", "ASC");
            PageResponse<CategoryResponse> response = convertToPageResponse(page);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("搜索分类失败：", e);
            return Result.error("CATEGORY_SEARCH_ERROR", "搜索分类失败：" + e.getMessage());
        }
    }

    // =================== 层级查询 ===================

    @Override
    public Result<PageResponse<CategoryResponse>> getRootCategories(Integer currentPage, Integer pageSize,
                                                                  String orderBy, String orderDirection) {
        try {
            IPage<Category> page = categoryService.getRootCategories(currentPage, pageSize, orderBy, orderDirection);
            PageResponse<CategoryResponse> response = convertToPageResponse(page);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("获取根分类失败：", e);
            return Result.error("CATEGORY_QUERY_ERROR", "获取根分类失败：" + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<CategoryResponse>> getChildCategories(Long parentId, Integer currentPage, Integer pageSize,
                                                                   String orderBy, String orderDirection) {
        try {
            if (parentId == null) {
                return Result.error("INVALID_REQUEST", "父分类ID不能为空");
            }
            
            IPage<Category> page = categoryService.getChildCategories(parentId, currentPage, pageSize, orderBy, orderDirection);
            PageResponse<CategoryResponse> response = convertToPageResponse(page);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("获取子分类失败：", e);
            return Result.error("CATEGORY_QUERY_ERROR", "获取子分类失败：" + e.getMessage());
        }
    }

    @Override
    public Result<List<CategoryResponse>> getCategoryTree(Long rootId, Integer maxDepth) {
        try {
            List<Category> tree = categoryService.getCategoryTree(rootId, maxDepth);
            // 分类树需要包含子分类结构
            List<CategoryResponse> response = convertToResponseList(tree, true);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("获取分类树失败：", e);
            return Result.error("CATEGORY_TREE_ERROR", "获取分类树失败：" + e.getMessage());
        }
    }

    @Override
    public Result<List<CategoryResponse>> getCategoryPath(Long categoryId) {
        try {
            if (categoryId == null) {
                return Result.error("INVALID_REQUEST", "分类ID不能为空");
            }
            
            List<Category> path = categoryService.getCategoryPath(categoryId);
            List<CategoryResponse> response = convertToResponseList(path);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("获取分类路径失败：", e);
            return Result.error("CATEGORY_PATH_ERROR", "获取分类路径失败：" + e.getMessage());
        }
    }

    // =================== 统计功能 ===================

    @Override
    public Result<PageResponse<CategoryResponse>> getPopularCategories(Long parentId, Integer currentPage, Integer pageSize) {
        try {
            IPage<Category> page = categoryService.getPopularCategories(parentId, currentPage, pageSize);
            PageResponse<CategoryResponse> response = convertToPageResponse(page);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("获取热门分类失败：", e);
            return Result.error("CATEGORY_POPULAR_ERROR", "获取热门分类失败：" + e.getMessage());
        }
    }

    @Override
    public Result<List<CategoryResponse>> getCategorySuggestions(String keyword, Integer limit) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return Result.error("INVALID_REQUEST", "搜索关键词不能为空");
            }
            
            List<Category> suggestions = categoryService.getCategorySuggestions(keyword, limit);
            List<CategoryResponse> response = convertToResponseList(suggestions);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("获取分类建议失败：", e);
            return Result.error("CATEGORY_SUGGESTIONS_ERROR", "获取分类建议失败：" + e.getMessage());
        }
    }

    @Override
    public Result<Long> countCategories(Long parentId, String status) {
        try {
            long count = categoryService.countCategories(parentId, status);
            return Result.success(count);
            
        } catch (Exception e) {
            log.error("统计分类数量失败：", e);
            return Result.error("CATEGORY_COUNT_ERROR", "统计分类数量失败：" + e.getMessage());
        }
    }

    // =================== 辅助方法 ===================

    /**
     * 转换实体为响应
     */
    private CategoryResponse convertToResponse(Category category) {
        return convertToResponse(category, false);
    }
    
    /**
     * 转换实体为响应（支持是否包含子分类）
     */
    private CategoryResponse convertToResponse(Category category, boolean includeChildren) {
        if (category == null) {
            return null;
        }
        
        CategoryResponse response = new CategoryResponse();
        BeanUtils.copyProperties(category, response);
        
        // 只有明确要求时才转换子分类，避免无限递归
        if (includeChildren && category.getChildren() != null) {
            response.setChildren(convertToResponseList(category.getChildren(), true));
        }
        
        return response;
    }

    /**
     * 转换实体列表为响应列表
     */
    private List<CategoryResponse> convertToResponseList(List<Category> categories) {
        return convertToResponseList(categories, false);
    }
    
    /**
     * 转换实体列表为响应列表（支持是否包含子分类）
     */
    private List<CategoryResponse> convertToResponseList(List<Category> categories, boolean includeChildren) {
        if (categories == null) {
            return null;
        }
        
        return categories.stream()
                .map(category -> convertToResponse(category, includeChildren))
                .collect(Collectors.toList());
    }

    /**
     * 转换分页结果
     */
    private PageResponse<CategoryResponse> convertToPageResponse(IPage<Category> page) {
        PageResponse<CategoryResponse> response = new PageResponse<>();
        response.setSuccess(true);
        response.setDatas(convertToResponseList(page.getRecords()));
        response.setTotalPage((int) page.getPages());
        response.setCurrentPage((int) page.getCurrent());
        response.setPageSize((int) page.getSize());
        response.setTotal(page.getTotal());
        return response;
    }
}