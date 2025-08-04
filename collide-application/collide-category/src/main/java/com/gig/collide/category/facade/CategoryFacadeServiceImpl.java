package com.gig.collide.category.facade;

import com.gig.collide.api.category.CategoryFacadeService;
import com.gig.collide.api.category.request.CategoryCreateRequest;
import com.gig.collide.api.category.request.CategoryQueryRequest;
import com.gig.collide.api.category.request.CategoryUpdateRequest;
import com.gig.collide.api.category.response.CategoryResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.category.domain.entity.Category;
import com.gig.collide.category.domain.service.CategoryService;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分类门面服务实现 - 规范版
 * 参考Content模块设计，所有方法返回Result包装
 * 支持完整的分类功能：查询、层级、统计、管理
 * 
 * @author Collide
 * @version 5.0.0 (与Content模块一致版)
 * @since 2024-01-01
 */
@Slf4j
@DubboService(version = "5.0.0", timeout = 5000)
@RequiredArgsConstructor
public class CategoryFacadeServiceImpl implements CategoryFacadeService {

    private final CategoryService categoryService;

    // =================== 基础查询 ===================

    @Override
    public Result<CategoryResponse> getCategoryById(Long categoryId) {
        try {
            log.debug("获取分类详情: categoryId={}", categoryId);
            
            Category category = categoryService.getCategoryById(categoryId);
            
            if (category == null) {
                return Result.error("CATEGORY_NOT_FOUND", "分类不存在");
            }
            
            CategoryResponse response = convertToResponse(category);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("获取分类详情失败: categoryId={}", categoryId, e);
            return Result.error("CATEGORY_GET_ERROR", "获取分类详情失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<CategoryResponse>> queryCategories(CategoryQueryRequest request) {
        try {
            log.debug("查询分类列表: request={}", request);
            
            if (request == null) {
                request = new CategoryQueryRequest();
            }
            
            // 调用Service获取分页数据
            PageResponse<Category> pageResponse = categoryService.queryCategories(
                request.getActualParentId(),
                request.getActualStatus(),
                request.getCurrentPage(),
                request.getPageSize(),
                request.getOrderBy(),
                request.getOrderDirection()
            );
            
            // 转换为响应对象
            PageResponse<CategoryResponse> responsePageResponse = convertToResponsePageResponse(pageResponse);
            
            return Result.success(responsePageResponse);
            
        } catch (Exception e) {
            log.error("查询分类列表失败: request={}", request, e);
            return Result.error("CATEGORY_QUERY_ERROR", "查询分类列表失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<CategoryResponse>> searchCategories(String keyword, Long parentId, String status,
                                                                  Integer currentPage, Integer pageSize,
                                                                  String orderBy, String orderDirection) {
        try {
            log.debug("搜索分类: keyword={}, parentId={}, status={}, page={}/{}", 
                     keyword, parentId, status, currentPage, pageSize);
            
            // 调用Service获取分页数据
            PageResponse<Category> pageResponse = categoryService.searchCategories(keyword, parentId, status, 
                                                                                  currentPage, pageSize, orderBy, orderDirection);
            
            // 转换为响应对象
            PageResponse<CategoryResponse> responsePageResponse = convertToResponsePageResponse(pageResponse);
            
            return Result.success(responsePageResponse);
            
        } catch (Exception e) {
            log.error("搜索分类失败: keyword={}, parentId={}", keyword, parentId, e);
            return Result.error("CATEGORY_SEARCH_ERROR", "搜索分类失败: " + e.getMessage());
        }
    }

    // =================== 层级查询 ===================

    @Override
    public Result<PageResponse<CategoryResponse>> getRootCategories(String status, Integer currentPage, Integer pageSize,
                                                                   String orderBy, String orderDirection) {
        try {
            log.debug("获取根分类列表: status={}, page={}/{}", status, currentPage, pageSize);
            
            // 调用Service获取分页数据
            PageResponse<Category> pageResponse = categoryService.getRootCategories(status, currentPage, pageSize, 
                                                                                   orderBy, orderDirection);
            
            // 转换为响应对象
            PageResponse<CategoryResponse> responsePageResponse = convertToResponsePageResponse(pageResponse);
            
            return Result.success(responsePageResponse);
            
        } catch (Exception e) {
            log.error("获取根分类列表失败: status={}", status, e);
            return Result.error("ROOT_CATEGORIES_ERROR", "获取根分类列表失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<CategoryResponse>> getChildCategories(Long parentId, String status,
                                                                    Integer currentPage, Integer pageSize,
                                                                    String orderBy, String orderDirection) {
        try {
            log.debug("获取子分类列表: parentId={}, status={}, page={}/{}", 
                     parentId, status, currentPage, pageSize);
            
            // 调用Service获取分页数据
            PageResponse<Category> pageResponse = categoryService.getChildCategories(parentId, status, currentPage, pageSize, 
                                                                                    orderBy, orderDirection);
            
            // 转换为响应对象
            PageResponse<CategoryResponse> responsePageResponse = convertToResponsePageResponse(pageResponse);
            
            return Result.success(responsePageResponse);
            
        } catch (Exception e) {
            log.error("获取子分类列表失败: parentId={}", parentId, e);
            return Result.error("CHILD_CATEGORIES_ERROR", "获取子分类列表失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<CategoryResponse>> getCategoryTree(Long rootId, Integer maxDepth, String status,
                                                         String orderBy, String orderDirection) {
        try {
            log.debug("获取分类树: rootId={}, maxDepth={}, status={}", rootId, maxDepth, status);
            
            List<Category> tree = categoryService.getCategoryTree(rootId, maxDepth, status, 
                                                                 orderBy, orderDirection);
            
            List<CategoryResponse> responseList = convertToResponseList(tree, true);
            
            return Result.success(responseList);
            
        } catch (Exception e) {
            log.error("获取分类树失败: rootId={}, maxDepth={}", rootId, maxDepth, e);
            return Result.error("CATEGORY_TREE_ERROR", "获取分类树失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<CategoryResponse>> getCategoryPath(Long categoryId) {
        try {
            log.debug("获取分类路径: categoryId={}", categoryId);
            
            List<Category> path = categoryService.getCategoryPath(categoryId);
            
            List<CategoryResponse> responseList = convertToResponseList(path);
            
            return Result.success(responseList);
            
        } catch (Exception e) {
            log.error("获取分类路径失败: categoryId={}", categoryId, e);
            return Result.error("CATEGORY_PATH_ERROR", "获取分类路径失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<CategoryResponse>> getCategoryAncestors(Long categoryId, Boolean includeInactive) {
        try {
            log.debug("获取分类祖先: categoryId={}, includeInactive={}", categoryId, includeInactive);
            
            List<Category> ancestors = categoryService.getCategoryAncestors(categoryId, includeInactive);
            
            List<CategoryResponse> responseList = convertToResponseList(ancestors);
            
            return Result.success(responseList);
            
        } catch (Exception e) {
            log.error("获取分类祖先失败: categoryId={}", categoryId, e);
            return Result.error("CATEGORY_ANCESTORS_ERROR", "获取分类祖先失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<CategoryResponse>> getCategoryDescendants(Long categoryId, Integer maxDepth, Boolean includeInactive) {
        try {
            log.debug("获取分类后代: categoryId={}, maxDepth={}, includeInactive={}", 
                     categoryId, maxDepth, includeInactive);
            
            List<Category> descendants = categoryService.getCategoryDescendants(categoryId, maxDepth, includeInactive);
            
            List<CategoryResponse> responseList = convertToResponseList(descendants);
            
            return Result.success(responseList);
            
        } catch (Exception e) {
            log.error("获取分类后代失败: categoryId={}", categoryId, e);
            return Result.error("CATEGORY_DESCENDANTS_ERROR", "获取分类后代失败: " + e.getMessage());
        }
    }

    // =================== 高级查询 ===================

    @Override
    public Result<PageResponse<CategoryResponse>> getPopularCategories(Long parentId, String status,
                                                                      Integer currentPage, Integer pageSize) {
        try {
            log.debug("获取热门分类: parentId={}, status={}, page={}/{}", 
                     parentId, status, currentPage, pageSize);
            
            // 调用Service获取分页数据
            PageResponse<Category> pageResponse = categoryService.getPopularCategories(parentId, status, currentPage, pageSize);
            
            // 转换为响应对象
            PageResponse<CategoryResponse> responsePageResponse = convertToResponsePageResponse(pageResponse);
            
            return Result.success(responsePageResponse);
            
        } catch (Exception e) {
            log.error("获取热门分类失败: parentId={}", parentId, e);
            return Result.error("POPULAR_CATEGORIES_ERROR", "获取热门分类失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<CategoryResponse>> getLeafCategories(Long parentId, String status,
                                                                   Integer currentPage, Integer pageSize) {
        try {
            log.debug("获取叶子分类: parentId={}, status={}, page={}/{}", 
                     parentId, status, currentPage, pageSize);
            
            // 调用Service获取分页数据
            PageResponse<Category> pageResponse = categoryService.getLeafCategories(parentId, status, currentPage, pageSize);
            
            // 转换为响应对象
            PageResponse<CategoryResponse> responsePageResponse = convertToResponsePageResponse(pageResponse);
            
            return Result.success(responsePageResponse);
            
        } catch (Exception e) {
            log.error("获取叶子分类失败: parentId={}", parentId, e);
            return Result.error("LEAF_CATEGORIES_ERROR", "获取叶子分类失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<CategoryResponse>> getCategorySuggestions(String keyword, Integer limit, String status) {
        try {
            log.debug("获取分类建议: keyword={}, limit={}, status={}", keyword, limit, status);
            
            List<Category> suggestions = categoryService.getCategorySuggestions(keyword, limit, status);
            
            List<CategoryResponse> responseList = convertToResponseList(suggestions);
            
            return Result.success(responseList);
            
        } catch (Exception e) {
            log.error("获取分类建议失败: keyword={}", keyword, e);
            return Result.error("CATEGORY_SUGGESTIONS_ERROR", "获取分类建议失败: " + e.getMessage());
        }
    }

    // =================== 统计功能 ===================

    @Override
    public Result<Long> countCategories(Long parentId, String status) {
        try {
            log.debug("统计分类数量: parentId={}, status={}", parentId, status);
            
            Long count = categoryService.countCategories(parentId, status);
            
            return Result.success(count);
            
        } catch (Exception e) {
            log.error("统计分类数量失败: parentId={}, status={}", parentId, status, e);
            return Result.error("COUNT_CATEGORIES_ERROR", "统计分类数量失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Long> countChildCategories(Long parentId, String status) {
        try {
            log.debug("统计子分类数量: parentId={}, status={}", parentId, status);
            
            Long count = categoryService.countChildCategories(parentId, status);
            
            return Result.success(count);
            
        } catch (Exception e) {
            log.error("统计子分类数量失败: parentId={}", parentId, e);
            return Result.error("COUNT_CHILD_CATEGORIES_ERROR", "统计子分类数量失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getCategoryStatistics(Long categoryId) {
        try {
            log.debug("获取分类统计信息: categoryId={}", categoryId);
            
            Map<String, Object> statistics = categoryService.getCategoryStatistics(categoryId);
            
            return Result.success(statistics);
            
        } catch (Exception e) {
            log.error("获取分类统计信息失败: categoryId={}", categoryId, e);
            return Result.error("CATEGORY_STATISTICS_ERROR", "获取分类统计信息失败: " + e.getMessage());
        }
    }

    // =================== 验证功能 ===================

    @Override
    public Result<Boolean> existsCategoryName(String name, Long parentId, Long excludeId) {
        try {
            log.debug("检查分类名称是否存在: name={}, parentId={}, excludeId={}", name, parentId, excludeId);
            
            boolean exists = categoryService.existsCategoryName(name, parentId, excludeId);
            
            return Result.success(exists);
            
        } catch (Exception e) {
            log.error("检查分类名称是否存在失败: name={}, parentId={}", name, parentId, e);
            return Result.error("CHECK_CATEGORY_NAME_ERROR", "检查分类名称是否存在失败: " + e.getMessage());
        }
    }

    // =================== 管理功能 ===================

    @Override
    public Result<CategoryResponse> createCategory(CategoryCreateRequest request) {
        try {
            log.debug("创建分类: request={}", request);
            
            // 转换请求对象为实体
            Category category = convertCreateRequestToEntity(request);
            Category created = categoryService.createCategory(category);
            
            CategoryResponse response = convertToResponse(created);
            
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("创建分类失败: request={}", request, e);
            return Result.error("CREATE_CATEGORY_ERROR", "创建分类失败: " + e.getMessage());
        }
    }

    @Override
    public Result<CategoryResponse> updateCategory(Long categoryId, CategoryUpdateRequest request) {
        try {
            log.debug("更新分类: categoryId={}, request={}", categoryId, request);
            
            // 转换请求对象为实体
            Category category = convertUpdateRequestToEntity(request);
            category.setId(categoryId);
            Category updated = categoryService.updateCategory(category);
            
            CategoryResponse response = convertToResponse(updated);
            
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("更新分类失败: categoryId={}", categoryId, e);
            return Result.error("UPDATE_CATEGORY_ERROR", "更新分类失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> deleteCategory(Long categoryId) {
        try {
            log.debug("删除分类: categoryId={}", categoryId);
            
            boolean deleted = categoryService.deleteCategory(categoryId);
            
            return Result.success(deleted);
            
        } catch (Exception e) {
            log.error("删除分类失败: categoryId={}", categoryId, e);
            return Result.error("DELETE_CATEGORY_ERROR", "删除分类失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> batchUpdateStatus(List<Long> categoryIds, String status) {
        try {
            log.debug("批量更新分类状态: categoryIds={}, status={}", categoryIds, status);
            
            int updated = categoryService.batchUpdateStatus(categoryIds, status);
            
            return Result.success(updated);
            
        } catch (Exception e) {
            log.error("批量更新分类状态失败: categoryIds={}, status={}", categoryIds, status, e);
            return Result.error("BATCH_UPDATE_STATUS_ERROR", "批量更新分类状态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> updateContentCount(Long categoryId, Long increment) {
        try {
            log.debug("更新分类内容数量: categoryId={}, increment={}", categoryId, increment);
            
            boolean updated = categoryService.updateContentCount(categoryId, increment);
            
            return Result.success(updated);
            
        } catch (Exception e) {
            log.error("更新分类内容数量失败: categoryId={}, increment={}", categoryId, increment, e);
            return Result.error("UPDATE_CONTENT_COUNT_ERROR", "更新分类内容数量失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> recalculateContentCount(Long categoryId) {
        try {
            log.debug("重新计算分类内容数量: categoryId={}", categoryId);
            
            int updated = categoryService.recalculateContentCount(categoryId);
            
            return Result.success(updated);
            
        } catch (Exception e) {
            log.error("重新计算分类内容数量失败: categoryId={}", categoryId, e);
            return Result.error("RECALCULATE_CONTENT_COUNT_ERROR", "重新计算分类内容数量失败: " + e.getMessage());
        }
    }

    // =================== 私有辅助方法 ===================

    /**
     * 转换创建请求对象为实体
     */
    private Category convertCreateRequestToEntity(CategoryCreateRequest request) {
        if (request == null) {
            return null;
        }
        
        Category category = new Category();
        BeanUtils.copyProperties(request, category);
        
        // 确保字段清理
        if (request.getName() != null) {
            category.setName(request.getName().trim());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription().trim());
        }
        if (request.getIconUrl() != null) {
            category.setIconUrl(request.getIconUrl().trim());
        }
        
        return category;
    }

    /**
     * 转换更新请求对象为实体
     */
    private Category convertUpdateRequestToEntity(CategoryUpdateRequest request) {
        if (request == null) {
            return null;
        }
        
        Category category = new Category();
        BeanUtils.copyProperties(request, category);
        
        // 确保字段清理
        if (request.getName() != null) {
            category.setName(request.getName().trim());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription().trim());
        }
        if (request.getIconUrl() != null) {
            category.setIconUrl(request.getIconUrl().trim());
        }
        
        return category;
    }

    /**
     * 转换实体为响应对象
     */
    private CategoryResponse convertToResponse(Category category) {
        return convertToResponse(category, false);
    }
    
    /**
     * 转换实体为响应对象（支持是否包含子分类）
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
     * 转换分页响应
     */
    private PageResponse<CategoryResponse> convertToResponsePageResponse(PageResponse<Category> pageResponse) {
        if (pageResponse == null) {
            return null;
        }
        
        PageResponse<CategoryResponse> responsePageResponse = new PageResponse<>();
        
        // 转换数据列表
        if (!CollectionUtils.isEmpty(pageResponse.getDatas())) {
            List<CategoryResponse> responseList = convertToResponseList(pageResponse.getDatas());
            responsePageResponse.setDatas(responseList);
        } else {
            responsePageResponse.setDatas(Collections.emptyList());
        }
        
        // 复制分页信息
        responsePageResponse.setTotal(pageResponse.getTotal());
        responsePageResponse.setCurrentPage(pageResponse.getCurrentPage());
        responsePageResponse.setPageSize(pageResponse.getPageSize());
        responsePageResponse.setTotalPage(pageResponse.getTotalPage());
        responsePageResponse.setSuccess(pageResponse.isSuccess());
        
        return responsePageResponse;
    }
}