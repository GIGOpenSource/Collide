package com.gig.collide.category.facade;

import com.gig.collide.api.category.CategoryFacadeService;
import com.gig.collide.api.category.request.CategoryCreateRequest;
import com.gig.collide.api.category.request.CategoryUpdateRequest;
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
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分类门面服务实现 - 简洁版
 * 基于简洁版SQL设计和API
 * 
 * @author GIG Team
 * @version 2.0.0 (本地聚合服务)
 */
@Slf4j
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class CategoryFacadeServiceImpl implements CategoryFacadeService {

    private final CategoryService categoryService;

    @Override
    public Result<CategoryResponse> createCategory(CategoryCreateRequest request) {
        try {
            log.info("创建分类：{}", request);
            
            // 请求验证
            if (request == null) {
                return Result.error("INVALID_REQUEST", "请求参数不能为空");
            }
            
            // 转换为实体
            Category category = convertToEntity(request);
            
            // 创建分类
            Category created = categoryService.createCategory(category);
            
            // 转换为响应
            CategoryResponse response = convertToResponse(created);
            
            log.info("分类创建成功，ID：{}", created.getId());
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("创建分类失败：", e);
            return Result.error("CATEGORY_CREATE_ERROR", "创建分类失败：" + e.getMessage());
        }
    }

    @Override
    public Result<CategoryResponse> updateCategory(CategoryUpdateRequest request) {
        try {
            log.info("更新分类：{}", request);
            
            if (request == null || request.getId() == null) {
                return Result.error("INVALID_REQUEST", "分类ID不能为空");
            }
            
            // 转换为实体
            Category category = convertToEntity(request);
            
            // 更新分类
            Category updated = categoryService.updateCategory(category);
            if (updated == null) {
                return Result.error("CATEGORY_NOT_FOUND", "分类不存在");
            }
            
            // 转换为响应
            CategoryResponse response = convertToResponse(updated);
            
            log.info("分类更新成功，ID：{}", updated.getId());
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("更新分类失败：", e);
            return Result.error("CATEGORY_UPDATE_ERROR", "更新分类失败：" + e.getMessage());
        }
    }

    @Override
    public Result<Void> deleteCategory(Long categoryId, Long operatorId) {
        try {
            log.info("删除分类，ID：{}，操作人：{}", categoryId, operatorId);
            
            if (categoryId == null) {
                return Result.error("INVALID_REQUEST", "分类ID不能为空");
            }
            
            boolean deleted = categoryService.deleteCategory(categoryId, operatorId);
            if (!deleted) {
                return Result.error("CATEGORY_DELETE_ERROR", "删除分类失败");
            }
            
            log.info("分类删除成功，ID：{}", categoryId);
            return Result.success(null);
            
        } catch (Exception e) {
            log.error("删除分类失败：", e);
            return Result.error("CATEGORY_DELETE_ERROR", "删除分类失败：" + e.getMessage());
        }
    }

    @Override
    public Result<CategoryResponse> getCategoryById(Long categoryId, Boolean includeInactive) {
        try {
            if (categoryId == null) {
                return Result.error("INVALID_REQUEST", "分类ID不能为空");
            }
            
            Category category = categoryService.getCategoryById(categoryId, includeInactive);
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
    public Result<List<CategoryResponse>> getCategoryTree(Long rootId, Integer maxDepth, Boolean includeInactive) {
        try {
            List<Category> tree = categoryService.getCategoryTree(rootId, maxDepth, includeInactive);
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

    @Override
    public Result<List<CategoryResponse>> getCategoryAncestors(Long categoryId, Boolean includeInactive) {
        try {
            if (categoryId == null) {
                return Result.error("INVALID_REQUEST", "分类ID不能为空");
            }
            
            List<Category> ancestors = categoryService.getCategoryAncestors(categoryId, includeInactive);
            List<CategoryResponse> response = convertToResponseList(ancestors);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("获取分类祖先失败：", e);
            return Result.error("CATEGORY_ANCESTORS_ERROR", "获取分类祖先失败：" + e.getMessage());
        }
    }

    @Override
    public Result<List<CategoryResponse>> getCategoryDescendants(Long categoryId, Integer maxDepth, Boolean includeInactive) {
        try {
            if (categoryId == null) {
                return Result.error("INVALID_REQUEST", "分类ID不能为空");
            }
            
            List<Category> descendants = categoryService.getCategoryDescendants(categoryId, maxDepth, includeInactive);
            List<CategoryResponse> response = convertToResponseList(descendants);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("获取分类后代失败：", e);
            return Result.error("CATEGORY_DESCENDANTS_ERROR", "获取分类后代失败：" + e.getMessage());
        }
    }

    @Override
    public Result<Void> updateCategoryStatus(Long categoryId, String status, Long operatorId) {
        try {
            if (categoryId == null || status == null) {
                return Result.error("INVALID_REQUEST", "分类ID和状态不能为空");
            }
            
            boolean updated = categoryService.updateCategoryStatus(categoryId, status, operatorId);
            if (!updated) {
                return Result.error("CATEGORY_STATUS_UPDATE_ERROR", "更新分类状态失败");
            }
            
            return Result.success(null);
            
        } catch (Exception e) {
            log.error("更新分类状态失败：", e);
            return Result.error("CATEGORY_STATUS_UPDATE_ERROR", "更新分类状态失败：" + e.getMessage());
        }
    }

    @Override
    public Result<Integer> batchUpdateCategoryStatus(List<Long> categoryIds, String status, Long operatorId) {
        try {
            if (categoryIds == null || categoryIds.isEmpty()) {
                return Result.error("INVALID_REQUEST", "分类ID列表不能为空");
            }
            
            int count = categoryService.batchUpdateCategoryStatus(categoryIds, status, operatorId);
            return Result.success(count);
            
        } catch (Exception e) {
            log.error("批量更新分类状态失败：", e);
            return Result.error("CATEGORY_BATCH_UPDATE_ERROR", "批量更新分类状态失败：" + e.getMessage());
        }
    }

    @Override
    public Result<Void> activateCategory(Long categoryId, Long operatorId) {
        return updateCategoryStatus(categoryId, "active", operatorId);
    }

    @Override
    public Result<Void> deactivateCategory(Long categoryId, Long operatorId) {
        return updateCategoryStatus(categoryId, "inactive", operatorId);
    }

    @Override
    public Result<Long> updateContentCount(Long categoryId, Long increment) {
        try {
            if (categoryId == null || increment == null) {
                return Result.error("INVALID_REQUEST", "分类ID和增量值不能为空");
            }
            
            long newCount = categoryService.updateContentCount(categoryId, increment);
            return Result.success(newCount);
            
        } catch (Exception e) {
            log.error("更新内容数量失败：", e);
            return Result.error("CONTENT_COUNT_UPDATE_ERROR", "更新内容数量失败：" + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getCategoryStatistics(Long categoryId) {
        try {
            if (categoryId == null) {
                return Result.error("INVALID_REQUEST", "分类ID不能为空");
            }
            
            Map<String, Object> statistics = categoryService.getCategoryStatistics(categoryId);
            return Result.success(statistics);
            
        } catch (Exception e) {
            log.error("获取分类统计失败：", e);
            return Result.error("CATEGORY_STATISTICS_ERROR", "获取分类统计失败：" + e.getMessage());
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

    // 其他方法实现略...（由于篇幅限制）

    // =================== 辅助方法 ===================

    /**
     * 转换创建请求为实体
     */
    private Category convertToEntity(CategoryCreateRequest request) {
        Category category = new Category();
        BeanUtils.copyProperties(request, category);
        category.initDefaults();
        return category;
    }

    /**
     * 转换更新请求为实体
     */
    private Category convertToEntity(CategoryUpdateRequest request) {
        Category category = new Category();
        BeanUtils.copyProperties(request, category);
        return category;
    }

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
        response.setSuccess(true);  // 设置成功状态
        response.setDatas(convertToResponseList(page.getRecords()));
        response.setTotalPage((int) page.getPages());
        response.setCurrentPage((int) page.getCurrent());
        response.setPageSize((int) page.getSize());  // 修复：设置pageSize
        response.setTotal(page.getTotal());
        return response;
    }

    // 其他Facade方法实现...
    @Override
    public Result<Void> adjustCategorySort(Long categoryId, Integer newSort, Long operatorId) {
        return Result.error("NOT_IMPLEMENTED", "功能开发中");
    }

    @Override
    public Result<Integer> batchAdjustSort(Map<Long, Integer> sortMappings, Long operatorId) {
        return Result.error("NOT_IMPLEMENTED", "功能开发中");
    }

    @Override
    public Result<Void> moveCategory(Long categoryId, Long newParentId, Long operatorId) {
        return Result.error("NOT_IMPLEMENTED", "功能开发中");
    }

    @Override
    public Result<Integer> recalculateContentCount(Long categoryId) {
        return Result.error("NOT_IMPLEMENTED", "功能开发中");
    }

    @Override
    public Result<Integer> syncCategoryHierarchy() {
        return Result.error("NOT_IMPLEMENTED", "功能开发中");
    }

    @Override
    public Result<Boolean> existsCategoryName(String name, Long parentId, Long excludeId) {
        return Result.error("NOT_IMPLEMENTED", "功能开发中");
    }

    @Override
    public Result<PageResponse<CategoryResponse>> getLeafCategories(Long parentId, Integer currentPage, Integer pageSize) {
        return Result.error("NOT_IMPLEMENTED", "功能开发中");
    }

    @Override
    public Result<CategoryResponse> cloneCategory(Long sourceId, Long newParentId, String newName, Long operatorId) {
        return Result.error("NOT_IMPLEMENTED", "功能开发中");
    }

    @Override
    public Result<Void> mergeCategories(Long sourceId, Long targetId, Long operatorId) {
        return Result.error("NOT_IMPLEMENTED", "功能开发中");
    }
}